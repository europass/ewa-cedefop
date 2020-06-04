/**
 * Share for Review view
 *
 *
 */
define(
        [
            'jquery',
            'underscore',
            'backbone',
            'Utils',
            'HttpUtils',
            'i18n!localization/nls/Notification',
            'europass/http/WindowConfigInstance',
            'hbs!templates/main/shareReview',
            'europass/http/ServicesUri',
            'europass/http/MediaType',
            'europass/http/Resource',
            'models/ShareCloudInfoModel',
            'HelperManageModelUtils'
        ],
        function ($, _, Backbone, Utils, HttpUtils, Notification, WindowConfig, HtmlTemplate,
                ServicesUri, MediaType, Resource, ShareCloudInfoModel, HelperManageModelUtils) {

            var ShareReviewView = Backbone.View.extend({

                htmlTemplate: HtmlTemplate
                , sectionEl: $("#cloud-share-done-reviewing")
                , scrollableSelector: "#ShareReviewForm .shareReview-info .ui-settings-area"
                , optionsSelector: "#ShareReviewForm .shareReview-info"
                , overlaySelector: "#ShareReviewForm"
                , shareReviewedNotificationSelector: ".share-reviewed-notification-area"
                , errorMsgSelector: ".share-reviewed-notification-area"
                , shareUploadDocumentUrl: '/editors/upload-share-review'

                , events: {
                    "click #cloud-share-done-reviewing": "showShareReviewForm",
                    "click #ShareReviewForm button#btn-share-post-back": "postBackConfirm",
                    "click #ShareReviewForm button.close": "hideShareReviewForm",
                    "europass:share-review:drawer:hide": "hideShareReviewForm"
                }
                , initialize: function () {
                    if (typeof WindowConfig.sharedRemoteModel !== "undefined" &&
                            WindowConfig.sharedRemoteModel !== null &&
                            WindowConfig.sharedRemoteModel !== "") {

                        this.render();
                        this.listenUpdateModel();
                    }
                }
                , render: function () {

                    var context = {};

                    //Html of the Modal - html is appended and it is hidden
                    var html = HtmlTemplate(context);
                    this.$el.append(html);

                    this.optionsArea = this.$el.find(this.optionsSelector);
                    this.scrollableArea = this.$el.find(this.scrollableSelector);
                    this.overlay = this.$el.find(this.overlaySelector);
                    this.errorMsg = this.$el.find(this.errorMsgSelector);

                    $('#share-reviewed-recipient-email').val(Utils.readCookieByName("shareSenderEmail"));
                }

                , listenUpdateModel: function () {
                    this.listenTo(this.model, "prefs:order:changed", this.onModelChangeUploadCV);
                    this.listenTo(this.model, "prefs:data:format:changed", this.onModelChangeUploadCV);
                    this.listenTo(this.model, "model:content:changed", this.onModelChangeUploadCV);
                    this.listenTo(this.model, "list:sort:change", this.onModelChangeUploadCV);
                    this.listenTo(this.model, "linked:attachment:changed", this.onModelChangeUploadCV);
                    this.listenTo(this.model, "europassLogo:changed", this.onModelChangeUploadCV);
                }

                , onModelChangeUploadCV: function () {

                    if (typeof WindowConfig.sharedRemoteModel === "undefined" ||
                            WindowConfig.sharedRemoteModel === null &&
                            WindowConfig.sharedRemoteModel === '') {

                        return;
                    }

                    var _that = this;
                    $("body").trigger("europass:waiting:indicator:show", true);
                    var json = this.model.conversion().toTransferable();
                    $.ajax({
                        type: "POST",
                        url: _that.shareUploadDocumentUrl,
                        data: {
                            'json': json,
                            'documentTypes': Utils.checkModelInfoTypesNonEmpty(this.model.info()).filetypes.toString().replace(/ /g, ''),
                            'keepReviewSession': true,
                            'lastUpdateTime': JSON.parse(json).SkillsPassport.DocumentInfo.LastUpdateDate
                        },
                        success: function (data) {
                            if (data !== "success") {
                                $("body").trigger("europass:message:show", ["error", "Something went wrong. Please retry to open the link from your email."]);
                            }
                        },
                        error: function (data) {
                            $("body").trigger("europass:message:show", ["error", "Something went wrong. Please retry to open the link from your email."]);
                        },
                        complete: function () {
                            $("body").trigger("europass:waiting:indicator:hide", true);
                        }
                    });

                }

                , showShareReviewForm: function (event) {
                    var _that = this;
                    var _overlay = this.overlay;
                    var _area = this.optionsArea;

                    var children = (_area !== undefined ? _area.children() : undefined);
                    _overlay.toggleClass("visible", function () {
                        $(this).animate({"background-color": "rgba(0,0,0, 0.7)"}, 400);
                        if (children !== undefined) {
                            children.addBack().show('slide', {direction: "right", easing: "easeInSine"}, 400, function () {
                                _area.find(".ui-settings-area").css("overflow-y", "auto");
                                _that.sectionEl.trigger("europass:drawer:opened");
                            });
                        }
                    });
                }
                , hideShareReviewForm: function (event) {
                    var _overlay = this.overlay;
                    var _area = this.optionsArea;
                    var children = (_area !== undefined ? _area.children() : undefined);
                    if (children !== undefined) {
                        children.hide('slide', {direction: "left", easing: "easeInSine"}, 400);
                    }
                    _area.hide('slide', {direction: "right", easing: "easeInSine"}, 400);
                    _area.find(".ui-settings-area").css("overflow-y", "hidden");
                    _overlay.animate({"background-color": "rgba(0,0,0,0)"}, 400, function () {
                        $(this).toggleClass("visible");
                    });
                }

                , postBackConfirm: function (event) {

                    var target = $(event.currentTarget);
                    var parentElem = $(target).closest("#ShareReviewForm");

                    this.$el.find(this.shareReviewedNotificationSelector).trigger("europass:message:clear");

                    var sender = Utils.readCookieByName("shareRecipientEmail");
                    var recipient = Utils.readCookieByName("shareSenderEmail");
                    var message = $("textarea#share-reviewed-message-textarea").val()
                            .replace(new RegExp('<script(.|\n)*</script>', 'g'), '')
                            .replace(/(?:\r\n|\r|\n)/g, '<br />');
                    var validEmail = false;
                    var emptyEmail = _.isEmpty(sender) || _.isEmpty(recipient);
                    if (!emptyEmail) {
                        validEmail = Utils.isValidEmail(sender) && Utils.isValidEmail(recipient);
                    }
                    if (emptyEmail) {
                        $(parentElem).find('.share-reviewed-notification-area').trigger("europass:message:show", ["error", "Empty email-address", false])
                    } else if (!validEmail) {
                        $(parentElem).find('.share-reviewed-notification-area').trigger("europass:message:show", ["error", "Not valid email-address", false]);
                    }

                    if (validEmail && !emptyEmail) {
                        this.uploadToCloudServiceAction(event, sender, recipient, message);
                    }
                    event.stopPropagation();
                },

                uploadToCloudServiceAction: function (event, sender, recipient, message) {
                    var _that = this;
                    var json = this.model.conversion().toTransferable();
                    $("body").trigger("europass:waiting:indicator:show", true);
                    $.ajax({
                        type: "POST",
                        url: _that.shareUploadDocumentUrl,
                        data: {
                            'json': json,
                            'documentTypes': Utils.checkModelInfoTypesNonEmpty(this.model.info()).filetypes.toString().replace(/ /g, ''),
                            'keepReviewSession': false,
                            'lastUpdateTime': JSON.parse(json).SkillsPassport.DocumentInfo.LastUpdateDate
                        },
                        success: function (data) {
                            if (data === "success") {
                                _that.sendReviewEmail(event, sender, recipient, message);

                                $("#ImportWizard,#share-document-btn,#export-wizard-init-btn,#export-wizard-init-btn,#top-ui-cloud-sign-in-section").show();
                                $("#cloud-share-done-reviewing").hide();
                                delete WindowConfig.sharedRemoteModel;
                                HelperManageModelUtils.resetOptions(_that.model, true, true, true, true);

                                _that.hideShareReviewForm();
                                _that.cleanCookiesSet();
                            } else {
                                $("body").trigger("europass:message:show", ["error", Notification["error.code.share.post.back.reviewed"]]);
                            }
                        },
                        error: function (data) {
                            $("body").trigger("europass:message:show", ["error", Notification["error.code.share.post.back.reviewed"]]);
                        },
                        complete: function () {
                            $("body").trigger("europass:waiting:indicator:hide", true);
                        }
                    });
                },

                sendReviewEmail: function (event, senderEmail, recipientEmail, messageEmail) {

                    var infoModel = new ShareCloudInfoModel();
                    var editorsUrl = window.location.origin + WindowConfig.getDefaultEwaEditorContext();

                    // Using session to get cloud !!!
                    // TODO replace when using more than one cloud accounts !!
                    var cloudAppendUrl = 'googledrive';

                    var shareReviewUrl = "/shareReviewPostback/" + ewaLocale + "/" + cloudAppendUrl + "?language=" + ewaLocale +
                            "&senderEmail=" + senderEmail + "&recipientEmail=" + recipientEmail;

                    infoModel.set("ShareInfo.Sender", senderEmail);
                    infoModel.set("ShareInfo.Email", recipientEmail);
                    infoModel.set("ShareInfo.Locale", this.model.get("SkillsPassport.Locale"));
                    infoModel.set("ShareInfo.Link", editorsUrl + shareReviewUrl);
                    infoModel.set("ShareInfo.Message", messageEmail);

                    var data = infoModel.get("ShareInfo");
                    var httpResource = new Resource(ServicesUri.shareReviewPostback);
                    httpResource.contentType(MediaType.json);

                    var _that = this;
                    httpResource._post({
                        data: data
                    }, {
                        success: {
                            scope: _that,
                            callback: function (response) {
                                var elem = $("<div id='share-send-email-response-status'></div>").append(response);
                                var responseStr = elem.find("script[type=\"application/json\"]").html();

                                if (responseStr === undefined || responseStr === null || responseStr === "") {
                                    $("body").trigger("europass:message:show", ["error", Notification["error.code.share.post.back.reviewed.email"]]);
                                    return;
                                }
                                var respStatus = $.parseJSON(responseStr);
                                if (respStatus.EmailStatus !== "SENT") {
                                    $("body").trigger("europass:message:show", ["error", Notification["error.code.share.post.back.reviewed.email"]]);
                                    return;
                                }
                            }
                        },
                        complete: {
                            scope: _that,
                            callback: function () {
                                $("body").trigger("europass:waiting:indicator:hide", true);
                            }
                        }
                    });
                },

                cleanCookiesSet: function () {
                    Utils.deleteCookieByName("remoteUploadPartnerLocale");
                    Utils.deleteCookieByName("sharedDocumentId");
                    Utils.deleteCookieByName("sharedPermissionId");
                    Utils.deleteCookieByName("shareRecipientEmail");
                    Utils.deleteCookieByName("shareSenderEmail");
                }

            });

            return ShareReviewView;
        }
);