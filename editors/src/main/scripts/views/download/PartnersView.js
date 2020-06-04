define(
        [
            'jquery',
            'underscore',
            'backbone',
            'Utils',
            'cookie',
            'HttpUtils',
            'i18n!localization/nls/Notification',
            'i18n!localization/nls/GuiLabel',
            'hbs!templates/download/partners',
//		'europass/http/Resource',
            'europass/http/ServicesUri',
            'europass/http/WindowConfigInstance',
            'views/download/DownloadController'
        ],
        function ($, _, Backbone, Utils, cookie, HttpUtils, Notification, GuiLabel, Template, //Resource,
                ServicesUri, WindowConfig, DownloadController) {

            var PartnersView = Backbone.View.extend({

//			events: {
//				"wizard:process:completed" : "onCompleted"
//				"europass:wizard:export:complete" : "onCompleted",
//			},

                alreadyRendered: false,

                postedToPartners: false,

                onClose: function () {
                    $('iframe.hidden-download').unbind("load.ewa.download.iframe", $.proxy(this.iframeOnload, this));
                    this.alreadyRendered = false;
                    this.postedToPartners = false;

                    this.finishBtn.hide();
//				this.parentView.cleanup();

                    this.downloadController.cleanup();
                    delete this.downloadController;
                },

                initialize: function (options) {

                    this.parentView = options.parentView;

                    this.finishBtn = options.parentView.finishBtn;

                    this.messageContainer = options.messageContainer;

                    this.downloadController = new DownloadController({
                        relatedController: this,
                        messageContainer: options.messageContainer,
                        info: options.info
                    });

                    this.contextRoot = WindowConfig.getDefaultEwaEditorContext();

                    //live works on event bubbling mechanism where as iframe load event is not user action triggered. 
                    //So we cannot use live to handle the iframe load event.
                    this.iframeBound = false;
                }

                , render: function () {
                    if (this.alreadyRendered === false) {

                        var html = Template({postedto: WindowConfig.remoteUploadCallbackUrl});
                        this.$el.html(html);

                        //Adds the hidden iframe
                        html = "<iframe class=\"hidden-download\" name=\"downloadiframe\" style=\"display: none;\"></iframe>";
                        $(html).appendTo(this.$el);

                        this.alreadyRendered = true;
                        this.postedToPartners = false;
                    }
                    this.parentView.cleanupFeedback();

                    this.parentView.nextBtn.hide();

                    this.finishBtn.html(GuiLabel["export.wizard.partners"]);
                    this.finishBtn.show();
                }
                /**
                 * Function that runs when the remote method of downloading returns
                 */
                , iframeOnload: function (event) {

                    var iframe = $(event.target);

                    var isSuccessfulDownload = ($.trim(iframe.contents().find("head").html()) === "");
                    if (!isSuccessfulDownload) {
                        this.handleFailedDownload(iframe);
                    }

                    if (!this.postedToPartners) {

                        var _that = this;
                        var url = WindowConfig.remoteUploadCallbackUrl;
//					var iframeContents = $(iframe[0].contentWindow.document).contents().html();
                        var iframeContents = $("<div>").append($(iframe[0].contentWindow.document).contents()).html();

                        $.ajax({
                            url: ServicesUri.document_conversion_to.proxy_xml,
                            type: "POST",
                            data: {xml: iframeContents, callbackurl: url},
                            success: function (result) {
                                _that.triggerResponseSuccess();
                            },
                            error: function (xhr, ajaxOptions, thrownError) {

                                //Extract the JSON response from the response text
                                var parsed = {};

                                try {
                                    if (!_.isUndefined(xhr.responseText)) {
                                        var json = xhr.responseText.split('<script type="application/json">')[1].split("</script>")[0];
                                        parsed = JSON.parse(json);
                                    }

                                } catch (e) {
                                }

                                var errCode = "";

                                if (!_.isUndefined(parsed.Error)) {
                                    if (!_.isUndefined(parsed.Error.trace))
                                        errCode = parsed.Error.trace;
                                }

                                _that.triggerResponseError(xhr.status, errCode);
                            }
                        });

                        this.$el.find("iframe.hidden-download").remove();
                        html = "<iframe class=\"hidden-download\" name=\"downloadiframe\" style=\"display: none;\"></iframe>";
                        $(html).appendTo(this.$el);

                        this.postedToPartners = true;
                        this.iframeBound = false;
                    }

//console.log("stop waiting indicator with success status '"+success+"'");
                    //stop the waiting indicator...
//				this.disableWaitingIndicator( success );
                }
                /**
                 * The download failed
                 */
                , handleFailedDownload: function (iframe) {
                    var response = HttpUtils.readHtmlResponse(iframe);
                    var msg = HttpUtils.downloadErrorMessage(response.msg);

                    this.parentView.setModalFeedbackClass("error");
                    this.parentView.completeBtn.hide();
                    this.finishBtn.hide();
                    this.messageContainer.trigger("europass:message:show", ["error", msg]);

                }
                /**
                 * When Download is clicked
                 */
                , doFinish: function () {
                    this.parentView.cleanupFeedback();

                    //start the waiting indicator...
//				this.enableWaitingIndicator();

                    this.$el.trigger("europass:waiting:indicator:show");

                    var iframe = $("iframe.hidden-download");
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
                    var url = this.downloadController.decideUrl();

                    var data = {
                        json: this.model.conversion().toTransferable()
                    };
                    //Download token
                    var text = "DOWNLOAD-" + new Date().getTime();
                    this.activeDownloadToken = Utils.hashCode(text) + "-" + Utils.randomInK();
                    data["downloadToken"] = this.activeDownloadToken;

                    data["remoteUploadCallbackUrl"] = WindowConfig.remoteUploadCallbackUrl;

                    HttpUtils.download(url, data, null, "downloadiframe");
                    this.postedToPartners = false;
                }

                , triggerResponseSuccess: function () {
                    this.$el.trigger("europass:wizard:export:complete");
                    var msg = Notification["partners.post.success"].replace("<posturl>", WindowConfig.remoteUploadCallbackUrl);
                    this.messageContainer.trigger("europass:message:show", ["success", msg]);
                    this.parentView.setModalFeedbackClass("success");
                }

                , triggerResponseError: function (error, errCode) {

                    var errorKey = "error.code.status500";

                    if (error === 403 || error === 404 || error === 500) {
                        errorKey = "error.code.status" + error;
                    } else if (error >= 400 && error < 500) {
                        errorKey = "error.code.status400";
                    }

                    var msg = Notification["partners.post.error"].replace("<posturl>", WindowConfig.remoteUploadCallbackUrl);

                    this.messageContainer.trigger("europass:message:show", ["error", msg + Notification[errorKey] + " " + errCode]);
                    this.parentView.setModalFeedbackClass("error");
                }
            });

            return PartnersView;
        }
);