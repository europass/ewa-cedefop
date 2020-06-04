define(
        [
            'jquery',
            'underscore',
            'views/WizardStep',
            'europass/GlobalDocumentInstance',
            'hbs!templates/download/wizard/step1',
        ],
        function ($, _, WizardStep, GlobalDocument, Template) {
            //Set a global variable for the user's selections
            //var userSelect = GlobalDocument.getDocTypeDownloadSelections();

            var DocTypeSelectionStep = WizardStep.extend({

                //This step must be re-rendered to capture any changes in the model
                forceRerender: true,

                localKey2: "temporary.europass.docType.download.selection",

                htmlTemplate: Template,

                events: _.extend({

                    "change :checkbox.enabled": "enabledChecked"

                }, WizardStep.prototype.events),

                /**
                 * @Override
                 */
                render: function () {

                    WizardStep.prototype.render.call(this);

                    //Find all enabled checkboxes
                    var boxes = this.findAllInput();

                    var selections = this.getSavedSelections();


                    //If user's selections are null...
                    if ($.isEmptyObject(selections)) {

                        //..then check all available options
                        this.$el.find(":checkbox.enabled").first().prop("checked", true);

                    } else {

                        //For all options
                        for (var i = 0; i < boxes.length; i++) {

                            //Each checkbox which is contained in the saved user options, then make it checked
                            if ($.inArray(boxes[i], selections) !== -1) {

                                this.$el.find(":checkbox[name=\"" + boxes[i] + "\"]").prop("checked", true);

                            } else {

                                //Otherwise, make it unchecked (it remains available - enabled)
                                this.$el.find(":checkbox[name=\"" + boxes[i] + "\"]").prop("checked", false);
                            }
                        }

                    }

                    this.updateButtons();

                },
                /**
                 * @Override
                 * Prepare the context with which to render the step main template
                 * {
                 *   docs: [ ... ],
                 *   noContent: true | false 
                 * }
                 */
                prepareContext: function () {
                    var context = this.model.europassDocuments();

                    return context;
                },
                /**
                 * @Override
                 * Hide the previous
                 * Hide the save
                 * Decide on the status of next according to whether there exists info
                 */
                updateButtons: function () {
                    this.prevBtn.hide();

                    this.finishBtn.hide();

                    this._updateNextButton();
                },
                _updateNextButton: function () {
                    var activeChecked = this.$el.find(":checkbox:checked:not(.disabled):not(.incompatible)");
                    if (activeChecked.length === 0) {
                        this.nextBtn.hide();
                    } else {
                        this.nextBtn.show();
                    }

                    return activeChecked;
                },

                /**
                 * A checkbox is checked or unchecked.
                 * We need to apply the rules and update the status of the NEXT button
                 */
                enabledChecked: function (event) {

                    this._updateNextButton();

                },
                /**
                 * @Override
                 * Go back to the previous step
                 */
                doPrevious: function () {
                    return;
                },
                /**
                 * Reset the user's selections on close
                 */
//		doCancel: function(){
//			userSelect = [];
//		},

                /**
                 * @Override
                 */
                saveModel: function () {
                    var ecvChecked = this.findEnabledInput("ECV").length > 0;
                    var espChecked = this.findEnabledInput("ESP").length > 0;
                    var elpChecked = this.findEnabledInput("ELP").length > 0;
                    var eclChecked = this.findEnabledInput("ECL").length > 0;

                    //Set the model
                    //Document Type
                    var docType = this.findDocumentType(ecvChecked, espChecked, elpChecked, eclChecked);
                    this.model.setDocumentType(docType);

                    //Bundle?
                    var bundle = this.findDocumentBundle(ecvChecked, espChecked, elpChecked, eclChecked);
                    this.model.setBundle(bundle);

                    //"Save" the user's selections while proceeding to the next step
                    this.saveDocTypeSelection();

                },
                /**
                 * "Save" the user's selections while proceeding to the next step
                 */
                saveDocTypeSelection: function () {
                    GlobalDocument.setDocTypeDownloadSelections(this.findCheckedInput());
                    var localEl = $.find("#local-storage-options");
                    $(localEl[0]).trigger("download:docType:selection:local:save");
                },

                /**
                 * @Override
                 */
                canProceed: function () {
                    return this.model.hasDocumentType();

                },
                /**
                 * Conclude on the selected Document Type.
                 * - If CV and ESP are checked, then type is ECV_ESP
                 * - If CV is checked and not ESP (even when other docs are selected), then type is ECV
                 * - If ESP is checked and not ECV, then type is ECV
                 * - If ELP is checked and not ECV or ELP, then type is ELP
                 * - If ECL is checked and not ECV or ELP, then type is ECL
                 * @returns {String}
                 */
                findDocumentType: function (ecvChecked, espChecked, elpChecked, eclChecked) {

                    if (ecvChecked && espChecked)
                        return "ECV_ESP";

                    if (ecvChecked)
                        return "ECV";

                    if (espChecked)
                        return "ESP";

                    if (elpChecked)
                        return "ELP";

                    if (eclChecked)
                        return "ECL";
                },
                /**
                 * Given the combinations supported currently (January 2015) by Europass,
                 * all document combinations should be available
                 * this functions returns the proper bundle
                 */
                findDocumentBundle: function (ecvChecked, espChecked, elpChecked, eclChecked) {

                    var bundleObject = [];

                    if (ecvChecked) {
                        if (eclChecked)
                            bundleObject.push("ECL");
                        if (elpChecked)
                            bundleObject.push("ELP");
                        /*if (espChecked)
                         bundleObject.push("ESP");in this case the document type would be ECV_ESP */
                        return bundleObject;
                    }

                    if (espChecked) {
                        if (eclChecked)
                            bundleObject.push("ECL");
                        if (elpChecked)
                            bundleObject.push("ELP");
                        return bundleObject;
                    }

                    if (elpChecked) {
                        if (eclChecked)
                            bundleObject.push("ECL");
                        return bundleObject;
                    }
                    return null;
                },
                /**
                 * 
                 * @param name
                 * @returns jquery el
                 */
                findEnabledInput: function (name) {
                    return this.$el.find(":checkbox[name=\"" + name + "\"]:checked.enabled");
                },
                findInput: function (name) {
                    return this.$el.find(":checkbox[name=\"" + name + "\"]");
                },

                /**
                 * Function to find all the "checked by the user" document options
                 */
                findCheckedInput: function () {
                    var checkedNames = [];

                    this.$el.find(":checkbox:checked.enabled").each(function () {
                        checkedNames.push($(this).attr("name"));

                    });
                    return checkedNames;

                },

                /**
                 * Function to find all the available document options
                 */
                findAllInput: function () {
                    var allBoxes = [];

                    this.$el.find(":checkbox.enabled").each(function () {
                        allBoxes.push($(this).attr("name"));
                    });
                    return allBoxes;

                },

                getSavedSelections: function () {
                    try {
                        if (!$.isEmptyObject(GlobalDocument.getDocTypeDownloadSelections()))
                            return GlobalDocument.getDocTypeDownloadSelections();
                        else {
                            var localStorageSelections = window.localStorage.getItem(this.localKey2);
                            if (localStorageSelections !== undefined && localStorageSelections !== null && localStorageSelections !== "") {
                                var localStorageSelectionsArray = localStorageSelections.split(",");
                                if (!$.isEmptyObject(localStorageSelectionsArray))
                                    return localStorageSelectionsArray;
                            } else
                                return [];
                        }
                        return [];
                    } catch (err) {
                        return [];
                    }
                }

            });

            return DocTypeSelectionStep;

        }
);