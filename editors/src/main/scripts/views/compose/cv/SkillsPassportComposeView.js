define(
        [
            'jquery',
            'underscore', 'Utils',
            'views/compose/ComposeView',
            'views/prefs/PrintingPreferencesView',
            'hbs!templates/compose/cv/skillspassport',
            'views/compose/cv/PersonalInfoComposeView',
            'views/compose/cv/HeadlineComposeView',
            'views/compose/cv/WorkExperienceListComposeView',
            'views/compose/cv/EducationListComposeView',
            'views/compose/cv/SkillsComposeView',
            'views/compose/cv/AdditionalInfoComposeView',
            'views/compose/cv/AnnexesComposeView',
            'ModalFormInteractions',
            'i18n!localization/nls/GuiLabel',
            'i18n!localization/nls/EditorHelp',
            'europass/TabletInteractionsView',
            'typeahead',
            'views/interaction/TypeaheadView',
            'europass/http/FileManager'
        ],
        function ($, _, Utils,
                ComposeView,
                PrintingPreferencesView,
                HtmlTemplate,
                PersonalInfoComposeView,
                HeadlineComposeView,
                WorkExperienceListComposeView,
                EducationListComposeView,
                SkillsComposeView,
                AdditionalInfoComposeView,
                AnnexesComposeView,
                ModalFormInteractions,
                GuiLabel,
                EditorHelp,
                TabletInteractionsView, typeahead, TypeaheadView,
                FileManager
                ) {

            var SkillsPassportComposeView = ComposeView.extend({

                tagName: "section"

                , className: "navigation-area wysiwyg Compose"

                , id: "Compose:SkillsPassport"

                , htmlTemplate: HtmlTemplate

                , prefsView: null

                , swipeDistance: 10

                , events: _.extend({

                    "click 		.opens-modal-form": "openModalForm", //Modal-form opening behavior
                    "click 		.foreign-language-skills.empty": "openModalForm", //Modal-form opening behavior
                    "click		:button.delete:not(.photo):not(.attachment)": "deleteSection", //Button-Delete behavior

                    //Section sorting  behavior, sortable placeholder, auto sort and sort move buttons (up, down and top)
                    "click		:button.autoSort": "sortSection",
                    "click		:button[class^=sort-move]:not(.inactive)": "sortSection",
                    "click		span.sortable_placeholder": "sortSection",

                    "click :button.work-education-switch": "switchWorkEducationOrder", //Button switch work-education order
                    "click :button.enablePageBreak": "togglePageBreak"
                }, ComposeView.prototype.events)

                , onInit: function (options) {
                    this.sections = [];

                    this.navigation = options.navigation;

                    this.prefsView = new PrintingPreferencesView({
                        model: this.model
                    });
                    //Re-render when emptying Photo + Signature + Attachments
                    this.model.bind("model:binaries:reset", this.onReRendering, this);
                    //Re-render when emptying ESP
                    this.model.bind("model:content:reset", this.onReRendering, this);
                    //Re-render when uploading an ESP
                    this.model.bind("model:uploaded:esp", this.onReRendering, this);
                    //Re-render when uploading from cloud drives
                    this.model.bind("model:uploaded:cloud", this.onReRendering, this);
                    // Re-render when loading cloud document
                    this.model.bind("model:loaded:cloud:document", this.onReRendering, this);

                    $("#main-content-area").trigger("europass:dragdrop:init");

                    ComposeView.prototype.onInit.apply(this, [options]);

                    if (this.isTablet) {
                        TabletInteractionsView.startListeners()
                    }
                }
                , onClose: function () {
                    ComposeView.prototype.onClose.apply(this);

                    if (_.isObject(this.prefsView) && _.isFunction(this.prefsView.close)) {
                        this.prefsView.close();
                    }
                    delete this.prefsView;

                    $(this.sections).each(function (idx, section) {
                        if (_.isObject(section) && _.isFunction(section.close))
                            section.close();
                    });
                    this.sections = [];

                    this.model.unbind("model:binaries:reset", this.onReRendering);
                    this.model.unbind("model:content:reset", this.onReRendering);
                    this.model.unbind("model:uploaded:esp", this.onReRendering);
                    this.model.unbind("model:uploaded:cloud", this.onReRendering, this);
                    this.model.unbind("model:loaded:cloud:document", this.onReRendering, this);

                    $("#main-content-area").trigger("europass:dragdrop:close");

                    delete this.options;
                }
                , deleteSection: function (event) {

                    // TODO CLEAN UP COMMENT On mobile not using 2 step tooltip anymore !!
// 				if ((this.isTablet && _.isFunction(this.tablets.handleButton)) && this.tablets.handleButton(event) === false) {
// 					return false;
// 				} else {
// 					ModalFormInteractions.confirmDeleteSection(event);
// 				}
                    ModalFormInteractions.confirmDeleteSection(event);
                }
                , sortSection: function (event) {
//				alert("handleButton: "+_.isFunction(this.tablets.handleButton));
                    if ((this.isTablet && _.isFunction(this.tablets.handleButton)) && this.tablets.handleButton(event) === false) {
                        return false;
                    }
                }
                , openModalForm: function (event) {

                    var tar = $(event.currentTarget);
                    var el = $(event.target);

                    /* Since Other Languages section differs from the default .opens-modal-form implementation, a specific handler is 
                     * needed in order for it to open the modal by clicking on the whole section's area instead of just the nested 
                     * div which actually has the ability to open the modal  */
                    var isForeignLanguage = tar.is(".foreign-language-skills.empty, div.Skills");

                    /*var modalOpeningElement = null;
                     if (isForeignLanguage){
                     modalOpeningElement = ( el.hasClass("opens-modal-form-lang-empty")? el : 	//if event's target is the modal opener, use it
                     tar.hasClass("opens-modal-form-lang-empty")? tar : //else, use the current target
                     tar.find(".opens-modal-form-lang-empty"));		//else, find it inside the current target
                     event.currentTarget = modalOpeningElement;   never do this, causes many tears 
                     }*/

                    if (this.isTablet) {
                        var isAttachment = el.is("button.open-linked-attachments");
                        var isSortablePlaceholder = el.is("span.sortable_placeholder");
                        var isSortButton = el.is(":button[class^=sort-move]");

                        if (isSortButton || isSortablePlaceholder) {  //there is already an event handler for these sort buttons 
//						return false;
                            return;
                        }
                        // // TODO CLEAN UP COMMENT On mobile not using 2 step tooltip anymore !!
                        // if (isAttachment && this.tablets.handleAttachmentModalPreview(event) === false) {
                        // handle attachment preview did not return false, open the modal form
                        // 	return false;
                        // }
                        if (isAttachment) {
                            ModalFormInteractions.openForm(event);
                        } else if (isForeignLanguage && this.tablets.handleForeignLanguageSection(event) === false) {
                            return false;
                        } else { //it is a modal opening element, handle modal form
                            this.tablets.handleModalForm(event, ModalFormInteractions);
                        }
                    } else {
                        ModalFormInteractions.openForm(event, Utils.EditorForms.ecvFormsLoaded);
                        Utils.EditorForms.ecvFormsLoaded = true;
                    }
                }
                , switchWorkEducationOrder: function (event) {
//				alert("handleTipSpot: "+_.isFunction(this.tablets.handleTipSpot));

                    // TODO CLEAN UP COMMENT On mobile not using 2 step tooltip anymore !!
// 				if ((this.isTablet && _.isFunction(this.tablets.handleTipSpot)) && this.tablets.handleTipSpot(event, "currentTarget") === false) {
// 					return false;
// 				} else {
// 					this.doSwitchWordEducation(event);
// 				}

                    this.doSwitchWordEducation(event);
                }
                , doSwitchWordEducation: function (event) {
                    //start the waiting indicator...
                    this.$el.trigger("europass:waiting:indicator:show");

                    if (this.prefsView === undefined || this.prefsView === null) {
                        return false;
                    }

                    var switched = this.prefsView.switchWorkEducationOrder();

                    //console.log("New order of sections:" + this.prefsView.model.attributes.SkillsPassport.PrintingPreferences.ECV[0].order);
                    if (switched === true) {
                        this.model.trigger("prefs:order:changed", this.section);
                    }
                }
                , togglePageBreak: function (event) {

                    if (this.prefsView === undefined || this.prefsView === null) {
                        return false;
                    }

                    var elem = $(event.currentTarget);
                    elem.toggleClass("break-page");

                    if (elem.hasClass("break-page")) {
                        elem.find("span.data-title").text(EditorHelp["Remove.Page.Break"]);
                    } else {
                        elem.find("span.data-title").text(EditorHelp["Insert.Page.Break"]);
                    }

                    elem.hasClass("sectionBreak")
                            ? $(elem).closest("dl").prev("hr").toggleClass("break-page")
                            : $(elem).closest("dd").prev("hr").toggleClass("break-page");

                    var section = elem.data("section");

                    var toggled = this.prefsView.toggleSectionPageBreak(section);
                    if (toggled === true) {
                        this.model.trigger("prefs:pageBreaks:changed");
                    }
                }
                //@Override
                , render: function (callback, args) {
                    ComposeView.prototype.render.apply(this, [callback, args]);

                    if ($.isArray(this.sections) && this.sections.length === 0) {
                        this.onRender();
                    }
                }
                /**
                 * IMPORTANT!!!
                 * Unfortunately re-delegation of views is necessary if not initiated on each re-render of the main SkillsPassport view...
                 * See http://stackoverflow.com/questions/9271507/how-to-render-and-append-sub-views-in-backbone-js
                 */
                , onRender: function () {

                    this.sections = [];

                    //Personal Information 
                    this.personalInfoComposeView = new PersonalInfoComposeView({
                        el: '#Compose\\:LearnerInfo\\.Identification',
                        model: this.model,
                        section: "SkillsPassport.LearnerInfo.Identification",
                        initialRendering: true,
                        parentView: this
                    });
                    this.sections.push(this.personalInfoComposeView);

                    //Occupation
                    this.headlineComposeView = new HeadlineComposeView({
                        el: '#Compose\\:LearnerInfo\\.Headline',
                        model: this.model,
                        section: "SkillsPassport.LearnerInfo.Headline",
                        initialRendering: true,
                        parentView: this
                    });
                    this.sections.push(this.headlineComposeView);

                    //Work Experience
                    this.workExperienceListComposeView = new WorkExperienceListComposeView({
                        el: '#Compose\\:LearnerInfo\\.WorkExperience',
                        model: this.model,
                        section: "SkillsPassport.LearnerInfo.WorkExperience",
                        initialRendering: true,
                        parentView: this
                    });
                    this.sections.push(this.workExperienceListComposeView);

                    //Education
                    this.educationListComposeView = new EducationListComposeView({
                        el: '#Compose\\:LearnerInfo\\.Education',
                        model: this.model,
                        section: "SkillsPassport.LearnerInfo.Education",
                        initialRendering: true,
                        parentView: this
                    });
                    this.sections.push(this.educationListComposeView);

                    //Personal Skills
                    this.skillsComposeView = new SkillsComposeView({
                        el: '#Compose\\:LearnerInfo\\.Skills',
                        model: this.model,
                        section: "SkillsPassport.LearnerInfo.Skills",
                        initialRendering: true,
                        parentView: this
                    });
                    this.sections.push(this.skillsComposeView);

                    //Additional Info
                    this.additionalInfoComposeView = new AdditionalInfoComposeView({
                        el: '#Compose\\:LearnerInfo\\.Achievement',
                        model: this.model,
                        section: "SkillsPassport.LearnerInfo.Achievement",
                        initialRendering: true,
                        parentView: this
                    });
                    this.sections.push(this.additionalInfoComposeView);

                    //Annexes
                    this.annexesComposeView = new AnnexesComposeView({
                        el: '#Compose\\:LearnerInfo\\.ReferenceTo',
                        model: this.model,
                        section: "SkillsPassport.LearnerInfo.ReferenceTo",
                        initialRendering: true,
                        parentView: this
                    });
                    this.sections.push(this.annexesComposeView);

                    //This will open the upload model if requested so by the URL
                    if (_.isFunction(this.openModals)) {
                        this.openModals(this.options);
                    }
                }
                /**
                 * @Override
                 */
                , reRenderAttachments: function (origin) {
                    //If the annexes section will not be deleted now - there are still attachments
                    //then the indication target should be the annexes section, instead of the entire document
                    var attachments = this.model.get("SkillsPassport.LearnerInfo.ReferenceTo");
                    if (!_.isEmpty(attachments) && _.isArray(attachments)) {
                        this.setRenderIndicationTarget("#Compose\\:LearnerInfo\\.ReferenceTo");
                    }
//				console.log("call on rerendering from rerender attachment ");
                    this.onReRendering(origin);
                }
                /**
                 * @Override
                 * Instead of calling re-render we need to cal onReRendering
                 */
                , reRenderPreferenceOrder: function (relSection) {
                    if (relSection === this.section) {
                        this.onReRendering("click-origin-controls");
                    }
                }
                , onReRendering: function (origin) {
//				console.log("SkillsPassportComposeView on re-rendering...");
                    $(this.sections).each(function (idx, section) {
                        if (_.isObject(section) && _.isFunction(section.close))
                            section.close();
                    });
                    this.sections = [];
                    this.reRender(this.section, origin);
                }

                , enableFunctionalities: function (model) {
                    this.displayNewEuropassPortalNotification(GuiLabel["new.europass.portal.info"]);
                    this.displayDocumentNotification(GuiLabel["ecv.download.document.hint"]);
                    this.displayMissingDocumentNotification(GuiLabel["uploaded.documents.not.available.text.info"]);
                }
            });

            return SkillsPassportComposeView;
        }
);