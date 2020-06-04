define(
    'HelperManageModelUtils',

    [
        'require',
        'jquery',
        'underscore',
        'Utils',
        'europass/GlobalDocumentInstance'
    ],

    function( require, $, _, Utils, GlobalDocument){

        var HelperManageModelUtils = {};

        HelperManageModelUtils.customiseTemplateHtml = function (elem, model) {
            // Find all input
            var allInputDocuments = ["ECL", "REST"];

            for (var i = 0; i < allInputDocuments.length; i++) {
                elem.find(":checkbox[name=\"" + allInputDocuments[i] + "\"]").prop("checked", true);
            }

            var modelInfo = model.info();
            if (Utils.checkModelInfoTypesNonEmpty(modelInfo).filetypes.length === 0) {
                $(".import-overwrite-descr").closest('.feedback-area').hide();
            }
            else {
                $(".import-overwrite-descr").closest('.feedback-area').show();
            }

        };

        HelperManageModelUtils.isCheckedOverwriteCL =  function (dragDropConfirm) {
            if (dragDropConfirm) {
                return $("#Import_Option_Doc_Confirm_ECL").is(":checked");

            }
            return $("#Import_Option_Doc_ECL").is(":checked");
        };
        HelperManageModelUtils.isCheckedOverwriteNonCL =  function (dragDropConfirm) {
            if (dragDropConfirm) {
                return $("#Import_Option_Doc_Confirm_REST").is(":checked");
            }
            return $("#Import_Option_Doc_REST").is(":checked");
        };

        HelperManageModelUtils.decideJsonToImport =  function (jsonFromModel, jsonToImport, dragDropConfirm) {

            var json = {};
            if (this.isCheckedOverwriteCL(dragDropConfirm) && this.isCheckedOverwriteNonCL(dragDropConfirm)) {
                json = jsonToImport;
            }
            else if (!this.isCheckedOverwriteCL(dragDropConfirm) && !this.isCheckedOverwriteNonCL(dragDropConfirm)) {
                json = jsonFromModel;
            }
            else if (this.isCheckedOverwriteCL(dragDropConfirm) && !this.isCheckedOverwriteNonCL(dragDropConfirm)) {
                json = this.overwriteSections(jsonFromModel, jsonToImport, true);
            }
            else {
                json = this.overwriteSections(jsonFromModel, jsonToImport, false);
            }
            return this.syncPersonNameOrder(json);
        };

        HelperManageModelUtils.overwriteSections = function (jsonCurrentModel, jsonToImport, clOverwriteOnly) {

            var jsonModel, jsonImport;
            if (clOverwriteOnly) {
                jsonModel = jsonCurrentModel;
                jsonImport = jsonToImport;
            }
            else {
                jsonModel = jsonToImport;
                jsonImport = jsonCurrentModel;
            }

            this.overwriteCL(jsonModel, jsonImport);
            this.overwriteInterDocumentRefs(jsonModel);
            this.overwriteSignature(jsonModel, jsonImport);
            this.overwritePrintingPrefs(jsonModel, jsonImport);

            return jsonModel;
        };

        HelperManageModelUtils.overwriteCL = function (jsonModel, jsonImport) {
            // Overwrite/ keep CL section
            jsonModel.SkillsPassport.CoverLetter = {};
            if (!_.isUndefined(jsonImport.SkillsPassport.CoverLetter)) {

                jsonModel.SkillsPassport.CoverLetter = jsonImport.SkillsPassport.CoverLetter;
            }
        };

        HelperManageModelUtils.overwriteInterDocumentRefs = function (jsonModel) {
            // Reference to old CV/ ESP/ LP should be cleared.
            if ( !_.isUndefined(jsonModel.SkillsPassport.CoverLetter.Documentation) &&
                !_.isUndefined(jsonModel.SkillsPassport.CoverLetter.Documentation.InterDocument)) {

                jsonModel.SkillsPassport.CoverLetter.Documentation.InterDocument = null;
            }
        };

        HelperManageModelUtils.overwritePrintingPrefs = function (jsonModel, jsonImport) {
            // PrintingPrefences overwrite
            if (!_.isUndefined(jsonModel.SkillsPassport.PrintingPreferences) && !_.isNull(jsonModel.SkillsPassport.PrintingPreferences) &&
                !_.isUndefined(jsonImport.SkillsPassport.PrintingPreferences) && !_.isNull(jsonImport.SkillsPassport.PrintingPreferences) ) {
                jsonModel.SkillsPassport.PrintingPreferences.ECL = jsonImport.SkillsPassport.PrintingPreferences.ECL;
            }
        };

        HelperManageModelUtils.overwriteSignature = function (jsonModel, jsonImport) {
            // Overwrite Signature
            if (!_.isUndefined(jsonImport.SkillsPassport.LearnerInfo) &&
                !_.isUndefined(jsonImport.SkillsPassport.LearnerInfo.Identification) &&
                !_.isNull(jsonImport.SkillsPassport.LearnerInfo.Identification) &&
                !_.isUndefined(jsonImport.SkillsPassport.LearnerInfo.Identification.Signature &&
                    !_.isNull(jsonImport.SkillsPassport.LearnerInfo.Identification.Signature))) {

                if (!_.isUndefined(jsonModel.SkillsPassport.LearnerInfo) &&
                    _.isUndefined(jsonModel.SkillsPassport.LearnerInfo.Identification)) {
                    jsonModel.SkillsPassport.LearnerInfo.Identification = {};
                }
                jsonModel.SkillsPassport.LearnerInfo.Identification.Signature = jsonImport.SkillsPassport.LearnerInfo.Identification.Signature;

            }
            else {
                if (!_.isUndefined(jsonModel.SkillsPassport.LearnerInfo) &&
                    !_.isUndefined(jsonModel.SkillsPassport.LearnerInfo.Identification) &&
                    !_.isNull(jsonModel.SkillsPassport.LearnerInfo.Identification)) {
                    jsonModel.SkillsPassport.LearnerInfo.Identification.Signature = null;
                }
            }
        };

        HelperManageModelUtils.syncPersonNameOrder = function resetOptions(json) {

            if (!_.isUndefined(json.SkillsPassport) &&
                !_.isUndefined(json.SkillsPassport.PrintingPreferences) &&  !_.isNull(json.SkillsPassport.PrintingPreferences)) {

                if (!_.isUndefined(json.SkillsPassport.PrintingPreferences.ECV)) {
                    // Get default printing preference order for firstname / lastname
                    for (var i=0; i<json.SkillsPassport.PrintingPreferences.ECV.length; i++) {
                        var printPrefEl = json.SkillsPassport.PrintingPreferences.ECV[i];
                        if (!_.isUndefined(printPrefEl.name) &&
                            printPrefEl.name === "LearnerInfo.Identification.PersonName") {
                            var cvOrderPrefName = printPrefEl.order;
                            var resetObjectCV = printPrefEl;
                            break;
                        }
                    }
                }
                if (!_.isUndefined(json.SkillsPassport.PrintingPreferences.ELP)) {
                    for (var i=0; i<json.SkillsPassport.PrintingPreferences.ELP.length; i++) {
                        var printPrefEl = json.SkillsPassport.PrintingPreferences.ELP[i];
                        if (!_.isUndefined(printPrefEl.name) && printPrefEl.name === "LearnerInfo.Identification.PersonName") {
                            var cvOrderPrefName = printPrefEl.order;
                            if (typeof cvOrderPrefName !== "undefined") {
                                delete json.SkillsPassport.PrintingPreferences.ELP[i];
                                json.SkillsPassport.PrintingPreferences.ELP[i] = resetObjectCV;
                            }
                            break;
                        }
                    }
                }
                if (!_.isUndefined(json.SkillsPassport.PrintingPreferences.ECL)) {
                    for (var i=0; i<json.SkillsPassport.PrintingPreferences.ECL.length; i++) {
                        var printPrefEl = json.SkillsPassport.PrintingPreferences.ECL[i];
                        if (!_.isUndefined(printPrefEl.name) && printPrefEl.name === "LearnerInfo.Identification.PersonName") {
                            var cvOrderPrefName = printPrefEl.order;
                            if (typeof cvOrderPrefName !== "undefined") {
                                delete json.SkillsPassport.PrintingPreferences.ECL[i];
                                json.SkillsPassport.PrintingPreferences.ECL[i] = resetObjectCV;
                            }
                            break;
                        }
                    }
                }
            }

            return json;
        };

        HelperManageModelUtils.resetOptions = function resetOptions(model, resetCL, resetCV, resetLP, resetESP) {

            if (resetCL && resetCV && resetLP && resetESP){
                GlobalDocument.setDocTypeDownloadSelections([]);
                model.resetAll(false);
                return;
            }

            if (resetCL) {
                model.resetCLOnly(false, false);
            }
            if (resetCV) {
                model.resetCV(false, false);
            }
            if (resetLP) {
                model.resetLPOnly(false, false);
            }
            if (resetESP) {
                model.resetESPOnly(false, false);
            }
        };

        HelperManageModelUtils.resetOptionCL = function () {
            return $("#Erase_Option_Doc_Confirm_ECL").is(":checked");
        };
        HelperManageModelUtils.resetOptionCV = function () {
            return $("#Erase_Option_Doc_Confirm_ECV").is(":checked");
        };
        HelperManageModelUtils.resetOptionLP = function () {
            return $("#Erase_Option_Doc_Confirm_ELP").is(":checked");
        };
        HelperManageModelUtils.resetOptionESP = function () {
            return $("#Erase_Option_Doc_Confirm_ESP").is(":checked");
        };

        HelperManageModelUtils.preselectResetOptionCheckboxes = function () {
            var allInputDocuments = GlobalDocument.europassDocuments();

            for (var i = 0; i < allInputDocuments.length; i++) {
                $("#\\#resetConfirmationDialog").find(":checkbox[name=\"" + allInputDocuments[i] + "\"]").prop("checked", true);
            }
            $("#\\#resetConfirmationDialog").find(":checkbox[id=\"select-all-erase-input\"]").prop("checked", true);
        };

        return HelperManageModelUtils;
    }
);
