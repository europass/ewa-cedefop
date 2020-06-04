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
            'hbs!templates/download/jobPortals', //'europass/http/Resource',
            'europass/http/ServicesUri',
            'europass/http/WindowConfigInstance',
            'models/ShareCloudInfoModel',
            'views/download/DownloadController'
        ],
        function ($, _, Backbone, Utils, cookie, HttpUtils,
                Notification, GuiLabel, Template, ServicesUri,
                WindowConfig, ShareCloudInfoModel, DownloadController) {

            var JobPortalsDownloadView = Backbone.View.extend({

                alreadyRendered: false,

                events: {

                },

                initialize: function (options) {

                    //this.parentView = options.parentView;
                    //this.finishBtn = options.parentView.finishBtn;

                    this.messageContainer = options.messageContainer;

                    this.downloadController = new DownloadController({
                        relatedController: this,
                        messageContainer: options.messageContainer,
                        info: options.info
                    });

                    this.contextRoot = WindowConfig.getDefaultEwaEditorContext();

                },

                onClose: function () {
                    $('iframe.hidden-download').unbind("load.ewa.download.iframe", $.proxy(this.iframeOnload, this));
                    this.alreadyRendered = false;

                    this.finishBtn.hide();

                    this.downloadController.cleanup();
                    delete this.downloadController;
                },

                render: function () {
                    /* if ( this.alreadyRendered === false ){ */

                    var html = Template(/*{ postedto: WindowConfig.remoteUploadCallbackUrl} */);
                    this.$el.html(html);

                    //Adds the hidden iframe
                    html = "<iframe class=\"hidden-download\" name=\"downloadiframe\" style=\"display: none;\"></iframe>";
                    $(html).appendTo(this.$el);

                    /* 	this.alreadyRendered = true;
                     } */
                    //this.parentView.cleanupFeedback();

                    //this.parentView.nextBtn.hide();

                    this.finishBtn.html(GuiLabel["export.wizard.eures"]);
                    this.finishBtn.show();
                }

                /**
                 * When Download is clicked
                 */
                , doFinish: function () {
                    //this.parentView.cleanupFeedback();

                    //start the waiting indicator...
                    this.$el.trigger("europass:waiting:indicator:show", true);

                    this.postToEures();
                }
                /**
                 * Do the actual Eures POST
                 */
                , postToEures: function () {

                    var text = "DOWNLOAD-" + new Date().getTime(); //Download token
                    var activeDownloadToken = Utils.hashCode(text) + "-" + Utils.randomInK();

                    var _that = this;

                    $.ajax({
                        url: ServicesUri.document_conversion_to.post_to_eures,
                        type: "POST",
                        data: {
                            json: this.model.conversion().toTransferable(),
                            downloadToken: activeDownloadToken
                        },
                        success: function (result) {

                            var parsed = null;

                            try {
                                parsed = JSON.parse(result);
                            } catch (e) {
                                throw new Error("Result is not parsable.");
                            }

                            _that.triggerResponseSuccess();
                            if (parsed && parsed.token && parsed.url) {	 //trigger redirect , TODO tidy up + checks
                                //setTimeout( function(){},timeOutMillis );
                                var euresRedirect = parsed.url + parsed.token;
                                var euresTab = window.open(euresRedirect, '_blank');

                                //if popup was blocked, show a message
                                if (euresTab === undefined || (euresTab === null && Utils.tryOpeningPopUp() === false)/*$.isEmptyObject( euresTab )*/) {

                                    var msg = Notification["eures.post.redirection"] || "<a href='[[redirectURL]]'>Click here</a> to proceed to Eures.";
                                    msg = msg.replace("[[redirectURL]]", euresRedirect);

                                    _that.messageContainer.trigger("europass:message:show", ["error", msg]);
                                }
                            }
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
                        },
                        complete: function () {
                            $("body").trigger("europass:waiting:indicator:hide");
                        }
                    });

                    this.$el.find("iframe.hidden-download").remove();
                    html = "<iframe class=\"hidden-download\" name=\"downloadiframe\" style=\"display: none;\"></iframe>";
                    $(html).appendTo(this.$el);

                    this.iframeBound = false;
                }
                , triggerResponseSuccess: function () {

                    this.$el.trigger("europass:wizard:export:complete");
                    var msg = Notification["eures.post.success"]/*.replace("<posturl>", WindowConfig.remoteUploadCallbackUrl)*/;
                    this.messageContainer.trigger("europass:message:show", ["success", msg]);
                    //this.parentView.setModalFeedbackClass( "success" );
                }

                , triggerResponseError: function (error, errCode) {

                    var errorKey = "error.code.status500";

                    if (error === 401 || error === 403 || error === 404 || error === 500) {
                        errorKey = "error.code.status" + error;
                    } else if (error >= 400 && error < 500 && error !== 401) {
                        errorKey = "error.code.status400";
                    }

                    var msg = Notification["eures.post.error"]/*.replace("<posturl>", WindowConfig.remoteUploadCallbackUrl)*/;

                    this.messageContainer.trigger("europass:message:show", ["error", msg + Notification[errorKey] + " " + errCode]);
                    //this.parentView.setModalFeedbackClass( "error" );
                }
            });

            return JobPortalsDownloadView;
        }
);