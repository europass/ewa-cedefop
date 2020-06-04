define(
        [
            'module',
            'jquery',
            'underscore',
            'Utils',
            'HttpUtils',
            'europass/http/UploadUtils',
            'europass/http/FileManager',
            'europass/GlobalDocumentInstance',
            'routers/SkillsPassportRouterInstance',
            'i18n!localization/nls/Notification',
            'HelperManageModelUtils'
        ]
        , function (
                module,
                $,
                _,
                Utils,
                HttpUtils,
                UploadUtils,
                FileManager,
                GlobalDocument,
                SkillsPassportRouter,
                Notification,
                HelperManageModelUtils) {

            /**
             * Constructor
             * @param relatedController: the BackboneView which includes this Controller, e.g. LinkedInUploadView
             * @param messageContainer: jQuery element
             */
            var UploadController = function (config) {
                this.relatedController = config.relatedController;
                this.messageContainer = config.messageContainer || $("body");
                this.modelUpdateEvent = config.modelUpdateEvent;
                this.modelUpdateMsgKey = config.modelUpdateMsgKey;
                this.uploadedEsp = {};
            };
            /**
             * Called when this controller is no longer useful for clean up purposes
             */
            UploadController.prototype.cleanup = function () {
                this.uploadedEsp = {};
            };
            /**
             * Callback as soon as the API has returned successfully
             * @param response
             */
            UploadController.prototype.uploadedCallback = function (response, filename, stepNextParentView) {
                if (_.isEmpty($(response)) || _.isUndefined($(response).get(3))) {
                    this.triggerEmptyModel();
                    return;
                }

                var json = UploadUtils.uploadSuccess({result: response}, $(this.messageContainerSelector), "esp", FileManager.handleFailureResponse);

                this.onUploadSuccess(json, stepNextParentView);

                // prepend filename message wrapper
                if (!_.isUndefined(filename) && !_.isEmpty(filename)) {
                    var msg = "<div class=\"file-names\"><span class=\"file-name\">" + filename + "</span></div>";
                    this.messageContainer.trigger("europass:message:show", ["success", msg + Notification["success.code.upload.default"], false]);
                } else
                    this.messageContainer.trigger("europass:message:show", ["success", Notification["success.code.upload.default"], false]);

            };
            /**
             * On successful uploading
             */
            UploadController.prototype.onUploadSuccess = function (json, stepNextParentView) {
                if (Utils.isEmptyObject(json)) {
                    this.triggerEmptyModel();
                    return false;
                }
                var uploaded = json.Uploaded;
                if (Utils.isEmptyObject(uploaded)) {
                    this.triggerEmptyModel();
                    return false;
                }
                var esp = uploaded.SkillsPassport;
                if (Utils.isEmptyObject(esp)) {
                    this.triggerEmptyModel();
                    return false;
                }
                // Warning feedback
                var feedback = uploaded.Feedback;
                if ($.isArray(feedback) && feedback.length > 0) {

                    var feedbackMsg = HttpUtils.readableFeedback(feedback);

                    this.messageContainer
                            .trigger("europass:message:show",
                                    ["warning", feedbackMsg]);
                }
                // Store somewhere the Model
                this.uploadedEsp = {SkillsPassport: esp};

                var uploadedJSON = {Uploaded: this.uploadedEsp};
                GlobalDocument.setDocTypeImportJSON(uploadedJSON);
                if (!_.isUndefined(stepNextParentView)) {
                    // Moving to next step (confirm overwrite for CL - step3.hbs - EPAS-486)
                    this.uploaded();
                }

            };

            /**
             * The user has confirmed the upload of the imported model
             * dragDropConfirm : if import happens from dragDrop view / otherwise normal wizard.
             */
            UploadController.prototype.uploaded = function (dragDropConfirm) {
                //start the waiting indicator...
//		this.messageContainer.trigger("europass:waiting:indicator:show");			
                var jsonFromModel = JSON.parse(this.relatedController.model.conversion().toTransferable());
                var jsonFromImport = this.uploadedEsp;

                this.uploadedEsp = HelperManageModelUtils.decideJsonToImport(jsonFromModel, jsonFromImport, dragDropConfirm);

                //Reload only if there is a locale!
                var locale = null;
                try {
                    locale = this.uploadedEsp.SkillsPassport.Locale;
                } catch (err) {
                }


                var decideRouteF = this.relatedController.decideAfterView;
                if (!_.isFunction(decideRouteF)) {
                    decideRouteF = function () {
                        return SkillsPassportRouter.decideView(this.relatedController.model, GlobalDocument.get());
                    };
                }
                if ((locale === undefined || locale === null || locale === "")
                        || (module.config().locale === locale)) {
                    //Replace the existing model with the one uploaded
                    //true: do translation!
                    this.relatedController.model.conversion().fromTransferable(this.uploadedEsp, true, true);

                    //Navigate -update the URL without creating an entry in the browser's history,
                    //set the replace option to true
                    //Router will not actually perform a navigation if the current route is the same as the one noted by this url
                    var navigateTo = decideRouteF.call(this);
//			console.log("Navigate To: "+ navigateTo );
                    SkillsPassportRouter.navigate(navigateTo, {trigger: true, replace: true});

                    this.relatedController.model.trigger(this.modelUpdateEvent, this.modelUpdateMsgKey);
                    //if (Utils.readCookieByName("cloud-signed-in") && Utils.readCookieByName("cloud-signed-in") !== "") {
                    $("body").trigger("europass:cloud:manage:load:uploadExistingDocument", [JSON.stringify(this.uploadedEsp)]);
                    //}

                } else {
                    //Replace the existing model with the one uploaded
                    //false: do not do translation, it will be done during reload!
                    this.relatedController.model.conversion().fromTransferable(this.uploadedEsp, false, true);
                    this.relatedController.updateConfigLocale(locale, decideRouteF.call(this));
                }
            };
            /**
             * Trigger a suitable message when the model is found empty
             */
            UploadController.prototype.triggerEmptyModel = function () {
                this.messageContainer
                        .trigger("europass:message:show",
                                ["error", Notification["error.code.xml.empty"]]);
            };

            UploadController.prototype.onUploadFailure = function (status, responseText) {
                //Parsing the raw HTML responseText in order to find the Error JSON object
                var info = HttpUtils.readHtmlErrorResponse(responseText);

                if ("error" === info.status) {
                    var defaultMsg = (Notification["skillspassport.import.cloud.error"] || "Notification[\"skillspassport.import.cloud.error\"]");
                    //If there is an error status, then we try to fetch a message based on the Error JSON object code
                    // Trace is also append
                    var message = HttpUtils.downloadErrorMessage(info.msg, defaultMsg);
                    //Finally, display the error message or a default text.
                    this.messageContainer
                            .trigger("europass:message:show",
                                    ["error", (message || "There was a problem uploading the Europass editor from the remote file")]);

                }
            };

            return UploadController;
        }
);