
define(
        [
            'jquery',
            'underscore',
            'jqueryui',
            'jqueryui',
            'backbone',
            'Utils',
            'europass/GlobalDocumentInstance',
            'routers/SkillsPassportRouterInstance',
            'hbs!templates/dialog/remoteuploadconfirmation',
//          'i18n!localization/nls/Notification',//'i18n!localization/nls/GuiLabel',
            'europass/http/SessionManagerInstance'
        ],
        function ($, _, jqueryui, jqueryui, Backbone, Utils, GlobalDocument, AppRouter, Template, /*Notification,*/ SessionManager) {

            var ModelRemoteUploadView = Backbone.View.extend({

                el: "body",

                onClose: function () {},

                initialize: function (options) {
                    this.template = Template;
                    var optionsDriveName = options._driveName;
                    this.driveName =
                            (optionsDriveName === undefined || optionsDriveName === null || optionsDriveName === "")
                            ? "" : options._driveName + ".";

                    if (!_.isUndefined(options.callback) && _.isFunction(options.callback)) {
                        var args = options.args || [];
                        args.push(this);
                        options.callback.apply(options.scope, args);
                    }
                },

                modalId: "SkillsPassport.Remote.Upload.Modal",

                events: {
                    "click :button.remote-upload.confirm-submit": "populateRemote",
                    "click :button.remote-upload.confirm-cancel": "rejectRemote",
                    "europass:modal:dialog:closed ": "rejectRemote"
                },

                render: function () {

                    var remoteFeedback = window.remoteFeedback;

                    if (remoteFeedback === null || (remoteFeedback !== null && remoteFeedback === "SUCCESS")) {
                        //open up the modal dialog

//					var header = GuiLabel[this.driveName+"remote.upload.confirmation.header"];

                        var _that = this;
                        require(
                                ['i18n!localization/nls/Notification'],
                                function (Notification) {

                                    var context = {};
                                    context.message = Notification[_that.driveName + "remote.upload.confirmation.message"];

                                    var html = _that.template(context);
                                    var confirmModal = Utils.prepareModal(_that.modalId, html);

                                    $(confirmModal).dialog("open");

                                }
                        );
                    } else {
                        // Remote feedback - display warning
                        this.handleFeedback();

                    }
                },
                /**
                 * Use the remote uploaded model to populate the editor
                 * 
                 * @param event
                 */
                populateRemote: function (event) {

                    var remoteModelStr = window.remoteModel;

                    // Override when using Sharing For Review functionality !
                    var sharedRemoteModelStr = window.sharedRemoteModel;
                    if (sharedRemoteModelStr !== undefined && sharedRemoteModelStr !== null && sharedRemoteModelStr !== "") {
                        remoteModelStr = sharedRemoteModelStr;
                    }

                    if (remoteModelStr !== undefined && remoteModelStr !== null && remoteModelStr !== "") {
                        var populated = this.model.populateModel(remoteModelStr);
                        if (!(populated === false)) {
                            this.model.trigger("remote:upload:model:populated");
                            this.handleRemoteUploadShareReviewMode(sharedRemoteModelStr);
                        }
                    }

                    this.closeModal();
                },
                /**
                 * Function to call when the remote content is rejected by not confirming the upload
                 * 
                 * @param event
                 */
                rejectRemote: function (event) {
                    this.closeModal();
                },
                /**
                 * Closes the modal
                 */
                closeModal: function () {
                    //delete window.remoteModel;
                    Utils.deleteWinProperty(remoteModel);

                    var modal = $(Utils.jId(this.modalId));

                    modal.dialog("close");

                    AppRouter.navigate(AppRouter.decideView(this.model, GlobalDocument.get()), {trigger: true, replace: true});

                },
                /**
                 * Consume the HTML feedback response, display a suitable error and navigate to the main view
                 * 
                 */
                handleFeedback: function () {

                    var _that = this;
                    require(
                            ['i18n!localization/nls/Notification'],
                            function (Notification) {

                                var errorMsg = Notification[_that.driveName + "remote.upload.confirmation.error"];
                                if (errorMsg === null) {
                                    errorMsg = "There has been a problem while trying to remotely upload the given Europass XML.";
                                }

                                var response = Utils.parseHtmlResponse(window.remoteFeedback);
                                if (response !== null && response !== false
                                        && response.json !== null
                                        && response.json.Error !== null && response.json.Error.trace !== null) {
                                    errorMsg += "<em class=\"trace-code\">" + response.json.Error.trace + "</em>";
                                }

                                Utils.deleteWinProperty(remoteModel);
                                Utils.deleteWinProperty(remoteFeedback);

                                _that.$el.trigger("europass:message:show", ["error", errorMsg, false]);

                                AppRouter.navigate(AppRouter.decideView(null, GlobalDocument.get()), {trigger: true, replace: true});
                            }
                    );
                },

                // Review Mode when coming from SHARED url // DISABLE CLOUD SIGN IN AREA + ENABLE SHARE CONFIRMATION BUTTON !!
                handleRemoteUploadShareReviewMode: function (sharedRemoteModel) {

                    if (typeof sharedRemoteModel !== "undefined" &&
                            sharedRemoteModel !== null &&
                            sharedRemoteModel !== '') {

                        Utils.deleteCookieByName("cloud-signed-in");
                        $("#ImportWizard,#share-document-btn,#export-wizard-init-btn,#export-wizard-init-btn,#top-ui-cloud-sign-in-section").hide();
                        $("#cloud-share-done-reviewing").show();
                    }
                }

            });

            return ModelRemoteUploadView;
        }
);