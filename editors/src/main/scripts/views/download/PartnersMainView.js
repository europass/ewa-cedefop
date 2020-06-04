define(
        [
            'jquery',
            'underscore',
            'backbone',
            'europass/http/WindowConfigInstance',
            'Utils',
            'HttpUtils',
            'europass/http/ServicesUri',
            'hbs!templates/main/partnersResponse',
            'i18n!localization/nls/GuiLabel'
        ],
        function ($, _, Backbone, WindowConfig, Utils, HttpUtils, ServicesUri, HtmlTemplate, GuiLabel) {

            var PartnersMainView = Backbone.View.extend({

                sectionEl: $("#share-document-btn"),
                htmlTemplate: HtmlTemplate,
                alreadyRendered: false,
                postedToPartners: false,
                isPartner: false,
                contentSelector: "#PartnersResponseForm .basic-principles-content",
                overlaySelector: "#PartnersResponseForm",

                events: {
                    "europass:post:back:main:partners:finish": "doFinish",
                    "click #PartnersResponseForm button.close": "hideSettingsForm",
                    "click #PartnersResponseForm #partnersMainOkBtn": "hideSettingsForm"
                }
                , initialize: function () {
                    //live works on event bubbling mechanism where as iframe load event is not user action triggered.
                    //So we cannot use live to handle the iframe load event.
                    this.iframeBound = false;

                    if (Utils.isPartnerAvailable()) {
                        this.render();
                    }
                }

                , onClose: function () {
                    $('iframe.hidden-download-partners').unbind("load.ewa.download.iframe", $.proxy(this.iframeOnload, this));
                    this.alreadyRendered = false;
                    this.postedToPartners = false;
                }

                , render: function () {

                    var titleName = WindowConfig.remoteUploadPartnerName || GuiLabel[ "export.wizard.location.option.partners"];
                    $("#share-document-btn span.img span.txt").text(titleName);

                    var context = {};
                    var html = HtmlTemplate(context);
                    this.$el.append(html);

                    if (this.alreadyRendered === false) {

                        //Adds the hidden iframe
                        var htmlIframe = "<iframe class=\"hidden-download-partners\" name=\"download-iframe-partners\" style=\"display: none;\"></iframe>";
                        $(htmlIframe).appendTo(this.$el);

                        this.alreadyRendered = true;
                        this.postedToPartners = false;
                    }

                    this.contentArea = this.$el.find(this.contentSelector);
                    this.overlay = this.$el.find(this.overlaySelector);
                }

                /**
                 * When Post-Back is clicked
                 */
                , doFinish: function () {
                    this.$el.trigger("europass:waiting:indicator:show");

                    var iframe = $("iframe.hidden-download-partners");
                    if (this.iframeBound === false) {
                        iframe.bind("load.ewa.download.iframe", $.proxy(this.iframeOnload, this));
                        this.iframeBound = true;
                    }
                    this.callRemoteMethod();
                }

                /**
                 * Do the actual FORM POST
                 */
                , callRemoteMethod: function (url) {
                    var url = ServicesUri.document_conversion_to.partners;
                    var data = {
                        json: this.model.conversion().toTransferable()
                    };
                    //Download token
                    var text = "DOWNLOAD-" + new Date().getTime();
                    this.activeDownloadToken = Utils.hashCode(text) + "-" + Utils.randomInK();
                    data["downloadToken"] = this.activeDownloadToken;

                    data["remoteUploadCallbackUrl"] = WindowConfig.remoteUploadCallbackUrl;

                    HttpUtils.download(url, data, null, "download-iframe-partners");
                    this.postedToPartners = false;
                }

                /**
                 * Function that runs when the remote method of downloading returns
                 */
                , iframeOnload: function (event) {

                    var iframe = $(event.target);

                    var isSuccessfulDownload = ($.trim(iframe.contents().find("head").html()) === "");
                    if (!isSuccessfulDownload) {
                        this.showSendResponseForm();
                        this.setModalFeedbackClass("error");
                        return;
                    }

                    if (!this.postedToPartners) {

                        var _that = this;
                        var url = WindowConfig.remoteUploadCallbackUrl;
                        var iframeContents = $("<div>").append($(iframe[0].contentWindow.document).contents()).html();

                        $.ajax({
                            url: ServicesUri.document_conversion_to.proxy_xml,
                            type: "POST",
                            data: {xml: iframeContents, callbackurl: url},
                            success: function (result) {
                                _that.showSendResponseForm();
                                _that.setModalFeedbackClass("success");
                            },
                            error: function (xhr) {
                                _that.showSendResponseForm();
                                _that.setModalFeedbackClass("error");
                                //_that.triggerResponseError(xhr.status, errCode);
                            }
                        });

                        this.$el.find("iframe.hidden-download-partners").remove();
                        html = "<iframe class=\"hidden-download-partners\" name=\"download-iframe-partners\" style=\"display: none;\"></iframe>";
                        $(html).appendTo(this.$el);

                        this.postedToPartners = true;
                        this.iframeBound = false;
                    }
                }

                , showSendResponseForm: function (event) {
                    var _that = this;
                    var _overlay = this.overlay;
                    var _area = this.contentArea;
                    var children = (_area !== undefined ? _area.children() : undefined);

                    _overlay.toggleClass("visible", function () {
                        $(this).animate({"background-color": "rgba(0,0,0,0.7)"}, 400);
                        if (children !== undefined) {
                            children.addBack().show('slide', {direction: "right", easing: "easeInSine"}, 400, function () {
                                //Making the vertical overflow auto for overflow-y scrolling if needed, while the modal is open
                                _area.find(".ui-cv-principles-area").css("overflow-y", "auto");
                                _that.sectionEl.trigger("europass:drawer:opened");
                            });
                        }
                        $("body").trigger("europass:waiting:indicator:hide");
                    });
                }

                , hideSettingsForm: function () {
                    var _overlay = this.overlay;
                    var _area = this.contentArea;
                    var children = (_area !== undefined ? _area.children() : undefined);

                    if (children !== undefined) {
                        children.hide('slide', {direction: "right", easing: "easeInSine"}, 400);
                    }
                    _area.hide('slide', {direction: "right", easing: "easeInSine"}, 400);
                    _area.find(".ui-cv-principles-area").css("overflow-y", "hidden");
                    _overlay.animate({"background-color": "rgba(0,0,0,0)"}, 400, function () {
                        $(this).toggleClass("visible");
                    });
                }

                , handleFailedDownload: function (iframe) {
                    var response = HttpUtils.readHtmlResponse(iframe);
                    var msg = HttpUtils.downloadErrorMessage(response.msg);

                    this.setModalFeedbackClass("error");
                }

                , setModalFeedbackClass: function (status) {
                    var modal = $("#PartnersResponseForm").find(".modal");
                    switch (status) {
                        case "error":
                        {
                            modal.addClass("error-status").removeClass("success-status");
                            $("#PartnersResponseForm .partners-main-on-success").hide();
                            $("#PartnersResponseForm .partners-main-on-error").show();
                            break;
                        }
                        case "success":
                        {
                            modal.removeClass("error-status").addClass("success-status");
                            $("#PartnersResponseForm .partners-main-on-success").show();
                            $("#PartnersResponseForm .partners-main-on-error").hide();
                            break;
                        }
                        default:
                        {
                            modal.removeClass("error-status").removeClass("success-status");
                            break;
                        }
                    }
                }

            });

            return PartnersMainView;
        }
);