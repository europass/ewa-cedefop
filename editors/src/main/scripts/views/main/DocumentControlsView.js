define(
        [
            'jquery',
            'backbone',
            'underscore',
            'Utils',
            'ModalFormInteractions',
            'europass/GlobalDocumentInstance',
            'i18n!localization/nls/CLDefaultPrintingPreferences'
        ],
        /**
         * Displays a different set of controls depending on the current document viewed.
         * Listens to the controls of each one.
         * CV:
         * 		buttons for adding photo, driving license, other skills, additional info, annex
         * LP:  
         *      button to include/exclude CEFR grid
         * CL: 
         * 		button to apply global HTML formatting to the document.
         */
                function ($, Backbone, _,
                        Utils, ModalFormInteractions, GlobalDocument, CLDefaultPrintingPreferences) {
                    var DocumentControlsView = Backbone.View.extend({

                        templatePath: "hbs!templates/main/controls/[doc]controls",

                        events: {
                            "click .opens-modal-form": "openModalForm"
                        },

                        onClose: function () {
                            delete this.currentDocument;
                            this.navigationModel.unbind("model:navigation:changed", $.proxy(this.navigateView, this));

                            this.model.unbind("model:linked:attachment:changed", this.reRender);
                            this.model.unbind("model:binaries:reset", this.reRender);
                            this.model.unbind("model:content:reset", this.reRender);
                            this.model.unbind("model:uploaded:esp", this.reRender);
                            this.model.unbind("model:uploaded:social", this.reRender);
                            this.model.unbind("model:uploaded:cloud", this.reRender);
                            this.model.unbind("model:content:changed", this.reRender);
                            this.model.unbind("prefs:cefrgrid:changed", this.reRender);

                        },

                        initialize: function (options) {
                            this.navigationModel = options.navigationModel;

                            this.navigationModel.bind("model:navigation:changed", $.proxy(this.navigateView, this));
                            //When attachments are added
                            this.model.bind("model:linked:attachment:changed", this.reRender, this);
                            //When emptying Photo + Signature + Attachments
                            this.model.bind("model:binaries:reset", this.reRender, this);
                            //When emptying model
                            this.model.bind("model:content:reset", this.reRender, this);
                            //When uploading an ESP
                            this.model.bind("model:uploaded:esp", this.navigateView, this);
                            //When uploading an ESP from Social Services
                            this.model.bind("model:uploaded:social", this.navigateView, this);
                            this.model.bind("model:uploaded:cloud", this.navigateView, this);
                            //When saving from forms
                            this.model.bind("model:content:changed", this.reRender, this);
                            this.model.bind("prefs:cefrgrid:changed", this.reRender, this);
                        },
                        /**
                         * Re-render the view 
                         */
                        reRender: function () {
                            if (_.isEmpty(this.currentDocument))
                                this.setCurrentDocument();
//				console.log("load and re-render hbs for " + this.currentDocument );
                            this.chooseTemplate();
                        },
                        /**
                         * Opens the respective modal form according to the data-* attributes of the event.target
                         * @param event
                         */
                        openModalForm: function (event) {
                            if (event.target.className === "opens-modal-form attachments") {
                                ModalFormInteractions.openForm(event, Utils.EditorForms.espFormsLoaded);
                                Utils.EditorForms.espFormsLoaded = true;
                            } else {
                                ModalFormInteractions.openForm(event, Utils.EditorForms.ecvFormsLoaded);
                                Utils.EditorForms.ecvFormsLoaded = true;
                            }
                        }
                        /**
                         * Respond to the event transmitted when the view changes.
                         * @param view
                         */
                        , navigateView: function (view) {
                            this.setCurrentDocument();

//				console.log("load and render hbs for " + this.currentDocument );
                            this.chooseTemplate();
                        }
                        , setCurrentDocument: function () {
                            var currentDocument = "ECV";

                            //Render the correct template
                            var currentDocumentInfo = GlobalDocument.get();

                            if (!_.isEmpty(currentDocumentInfo)) {
                                currentDocument = currentDocumentInfo.document;
                            }
                            //Avoid reloading this controller, 
                            //when the view changes, but the same document remains. 
                            //E.g. /cv/compose, /cv/download
                            if (currentDocument === this.currentDocument) {
                                return;
                            }
                            if ("ECV_ESP" === currentDocument)
                                currentDocument = "ECV";
                            //Set the current document in a static variable
                            this.currentDocument = currentDocument;
                        }
                        /**
                         * Will execute the template according to a context and display it
                         */
                        , renderTemplate: function (chosenTemplate) {
                            var context = this.chooseContext();

                            var html = chosenTemplate(context);

                            this.$el.html(html);
                        }
                        /**
                         * Will decide on the path of the template to load based on the current document.
                         * After the template is successfully loaded this.renderTemplate will run.
                         * Otherwise an error will occur.
                         */
                        , chooseTemplate: function () {

                            var tplPath = this.templatePath.replace("[doc]", this.currentDocument.toLowerCase());

                            Utils.requireResource(
                                    {
                                        _requireName: tplPath
                                    },
                                    this.renderTemplate,
                                    this,
                                    []);
                        },
                        /**
                         * Prepare the context against which the template will be executed
                         */
                        chooseContext: function () {
                            var modelInfo = this.model.info();
                            var ctx = {};
                            var controls = false;

                            switch (this.currentDocument) {
                                case "ECV":
                                {

                                    var photo = this.model.get("SkillsPassport.LearnerInfo.Identification.Photo");
                                    if (_.isEmpty(photo)) {
                                        ctx.photo = true;
                                        controls = true;
                                    }
                                    if (modelInfo.isSectionEmpty("Skills.Other")) {
                                        ctx.otherSkills = true;
                                        controls = true;
                                    }
                                    if (modelInfo.isSectionEmpty("Skills.Driving")) {
                                        ctx.driving = true;
                                        controls = true;
                                    }
                                    if (modelInfo.isSectionEmpty("Achievement")) {
                                        ctx.achievements = true;
                                        controls = true;
                                        if (ewaLocale === 'it' || ewaLocale === 'pl') {
                                            ctx.personalDataTreatmentAchievements = true;
                                        }
                                    }
                                    var attachments = this.model.get("SkillsPassport.LearnerInfo.ReferenceTo");
                                    if (_.isEmpty(attachments)) {
                                        ctx.attachments = true;
                                        controls = true;
                                    }
                                    if (controls === true)
                                        ctx.controls = true;

                                    return ctx;
                                }
                                case "ELP":
                                {

                                    var prefs = this.model.getPreferences("ELP");
                                    if (prefs.LearnerInfo.CEFLanguageLevelsGrid.show) {
                                        ctx.show = true;
                                    }

                                    return ctx;
                                }
                                case "ECL":
                                {

                                    controls = true;

                                    var prefs = this.model.getPreferences("ECL");
                                    if (prefs.CoverLetter) {
                                        if (prefs.CoverLetter.Justification) {
                                            if (prefs.CoverLetter.Justification.justify) {
                                                ctx.show = true;
                                            }
                                        }
                                        if (prefs.CoverLetter.SignatureName) {
                                            ctx.showEnabledName = false;
                                            if (prefs.CoverLetter.SignatureName.enableName) {
                                                if (prefs.CoverLetter.SignatureName.enableName === true) {
                                                    ctx.showEnabledName = true;
                                                }
                                            }
                                        } else {
                                            var defaultEnableSignatureName = CLDefaultPrintingPreferences["CoverLetter.SignatureName"]["enableName"];
                                            this.model.preferences.addPreference("ECL.CoverLetter.SignatureName",
                                                    true, null, null, null, defaultEnableSignatureName);

                                            ctx.showEnabledName = defaultEnableSignatureName;
                                        }
                                    }

                                    return ctx;
                                }

                                default:
                                    return ctx;
                            }
                        }
                    });
                    return DocumentControlsView;
                }
        );