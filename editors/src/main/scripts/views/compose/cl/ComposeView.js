define(
        [
            'jquery',
            'underscore',
            'Utils',
            'views/compose/ComposeView',
            'views/compose/cl/HolderDetailsComposeView',
            'views/compose/cl/AddresseeDetailsComposeView',
            'views/compose/cl/SubjectLineLocalisationComposeView',
            'views/compose/cl/LocalisationComposeView',
            'views/compose/cl/OpeningSalutationComposeView',
            'views/compose/cl/MainbodyComposeView',
            'views/compose/cl/ClosingSalutationComposeView',
            'views/compose/cl/EnclosedComposeView',
            'ModalFormInteractions',
            'i18n!localization/nls/GuiLabel',
//		'templates/helpers/emptyObjects'
            'views/prefs/PrintingPreferencesView',
            'europass/TabletInteractionsView'
//		'Utils'
        ],
        function ($
                , _
                , Utils
                , MasterComposeView
                , HolderDetailsComposeView
                , AddresseeDetailsComposeView
                , SubjectLineLocalisationComposeView
                , LocalisationComposeView
                , OpeningSalutationComposeView
                , MainbodyComposeView
                , ClosingSalutationComposeView
                , EnclosedComposeView
                , ModalFormInteractions
                , GuiLabel
//			, checkEmptyObjects
                , PrintingPreferencesView
                , TabletInteractionsView
//			,Utils
                ) {

            var ComposeView = MasterComposeView.extend({

                htmlTemplate: "compose/cl/maincompose"

                , tagName: "section"

                , className: "navigation-area wysiwyg Compose"

                , events: _.extend({
                    "click :button.delete:not(.signature)": "deleteSection", //Button-Delete
                    "click .opens-modal-form": "openModalForm",
                    "europass:ecl:global:justify": "toggleGlobalJustify",
                    "europass:ecl:signature:toggle:name": "toggleCLSignaturePersonName"
                }, MasterComposeView.prototype.events)

                , onInit: function (options) {
                    this.sections = [];

                    this.navigation = options.navigation;

                    this.prefsView = new PrintingPreferencesView({
                        model: this.model
                    });

                    MasterComposeView.prototype.onInit.apply(this, [options]);

                    //Re-render when emptying Photo + Signature + Attachments
                    this.model.bind("model:binaries:reset", this.onReRendering, this);
                    //Re-render when emptying CL
                    this.model.bind("model:content:reset", this.onReRendering, this);
                    //Re-render when uploading an ESP
                    this.model.bind("model:uploaded:esp", this.onReRendering, this);
                    //Re-render when content has changed
                    this.model.bind("model:content:changed", this.onReRendering, this);

                    //Re-render when uploading from cloud drives
                    this.model.bind("model:uploaded:cloud", this.onReRendering, this);
                    // Re-render when loading cloud service document
                    this.model.bind("model:loaded:cloud:document", this.onReRendering, this);

                    if (this.isTablet) {
                        TabletInteractionsView.startListeners()
                    }
                }
                , onClose: function () {
                    MasterComposeView.prototype.onClose.apply(this);

                    $(this.sections).each(function (idx, section) {
                        if (_.isObject(section) && _.isFunction(section.close))
                            section.close();
                    });
                    this.sections = [];

                    this.model.unbind("model:binaries:reset", this.onReRendering);
                    this.model.unbind("model:content:reset", this.onReRendering);
                    this.model.unbind("model:uploaded:esp", this.onReRendering);
                    this.model.unbind("model:uploaded:cloud", this.onReRendering, this);
                    this.model.unbind("model:content:changed", this.onReRendering, this);
                    this.model.unbind("model:loaded:cloud:document", this.onReRendering, this);
                }
                , deleteSection: function (event) {

                    // TODO CLEAN UP COMMENT On mobile not using 2 step tooltip anymore !!
                    // if ((this.isTablet && _.isFunction(this.tablets.handleButton)) && this.tablets.handleButton(event) === false) {
                    // 	return false;
                    // } else {
                    // 	ModalFormInteractions.confirmDeleteSection(event);
                    // }

                    ModalFormInteractions.confirmDeleteSection(event);
                }
                /*,openModalForm: function( event ){
                 ModalFormInteractions.openForm( event );
                 }*/
                , openModalForm: function (event) {
                    if (this.isTablet) {
                        this.tablets.handleModalForm(event, ModalFormInteractions);
                    } else {
                        ModalFormInteractions.openForm(event, Utils.EditorForms.eclFormsLoaded);
                        Utils.EditorForms.eclFormsLoaded = true;
                    }
                }
                /**
                 * Properly close all child sections
                 */
                , cleanupChildSections: function () {
                    for (var i = 0; i < this.sections.length; i++) {
                        var section = this.sections[ i ];
                        if (_.isObject(section) && _.isFunction(section.close))
                            section.close();
                    }
                    this.sections = [];
                }
                /**
                 * First render the skeleton template so that the el of all child views are in place.
                 * Then call onRender, which will instatiate the child views.
                 */
                //@Override
                , render: function (args) {
                    MasterComposeView.prototype.render.apply(this, [args]);

                    if ($.isArray(this.sections) && this.sections.length === 0) {
                        this.onRender();
                    }

                    //This will open the uploda model if requested so by the URL
                    if (_.isFunction(this.openModals)) {
                        this.openModals(this.options);
                    }
                }

                /**
                 * IMPORTANT!!!
                 * Unfortunately re-delegation of views is necessary if not initiated on each re-render of the main SkillsPassport view...
                 * See http://stackoverflow.com/questions/9271507/how-to-render-and-append-sub-views-in-backbone-js
                 * 
                 * Will instantiate all child views and populate the array of child views (this.sections)
                 * 
                 */
                , onRender: function () {

                    this.sections = [];

                    var holderEl = this.$el.find("#Compose\\:CL\\:HolderDetails");
                    if (holderEl.length > 0) {
                        this.holderDetailsComposeView = new HolderDetailsComposeView({
                            el: holderEl,
                            model: this.model,
                            section: "SkillsPassport.LearnerInfo.Identification",
                            initialRendering: true
                        });
                        this.sections.push(this.holderDetailsComposeView);
                    }

                    var addresseeEl = this.$el.find("#Compose\\:CL\\:AddresseeDetails");
                    if (addresseeEl.length > 0) {
                        this.addresseeDetailsComposeView = new AddresseeDetailsComposeView({
                            el: addresseeEl,
                            model: this.model,
                            section: "SkillsPassport.CoverLetter.Addressee",
                            initialRendering: true
                        });
                        this.sections.push(this.addresseeDetailsComposeView);
                    }

                    var localisationAndsubjectEl = this.$el.find("#Compose\\:CL\\:SubjectLineLocalisation");
                    if (localisationAndsubjectEl.length > 0) {
                        this.subjectLineLocalisationComposeView = new SubjectLineLocalisationComposeView({
                            el: localisationAndsubjectEl,
                            model: this.model,
                            section: "SkillsPassport.CoverLetter.Letter.SubjectLine SkillsPassport.CoverLetter.Letter.Localisation",
                            initialRendering: true
                        });
                        this.sections.push(this.subjectLineLocalisationComposeView);
                    }

                    var localisationEl = this.$el.find("#Compose\\:CL\\:Localisation");
                    if (localisationEl.length > 0) {
                        this.localisationComposeView = new LocalisationComposeView({
                            el: localisationEl,
                            model: this.model,
                            section: "SkillsPassport.CoverLetter.Letter.Localisation",
                            initialRendering: true
                        });
                        this.sections.push(this.localisationComposeView);
                    }

                    var openingSalutationEl = this.$el.find("#Compose\\:CL\\:OpeningSalutation");
                    if (openingSalutationEl.length > 0) {
                        this.openingSalutationComposeView = new OpeningSalutationComposeView({
                            el: openingSalutationEl,
                            model: this.model,
                            section: "SkillsPassport.CoverLetter.Letter.OpeningSalutation",
                            initialRendering: true
                        });
                        this.sections.push(this.openingSalutationComposeView);
                    }

                    var mainBodyEl = this.$el.find("#Compose\\:CL\\:MainBody");
                    if (mainBodyEl.length > 0) {
                        this.mainbodyComposeView = new MainbodyComposeView({
                            el: mainBodyEl,
                            model: this.model,
                            section: "SkillsPassport.CoverLetter.Letter.Body",
                            initialRendering: true
                        });
                        this.sections.push(this.mainbodyComposeView);
                    }

                    var closingSalutationEl = this.$el.find("#Compose\\:CL\\:ClosingSalutation");
                    if (closingSalutationEl.length > 0) {
                        this.closingSalutationComposeView = new ClosingSalutationComposeView({
                            el: closingSalutationEl,
                            model: this.model,
                            section: "SkillsPassport.CoverLetter.Letter.ClosingSalutation",
                            initialRendering: true
                        });
                        this.sections.push(this.closingSalutationComposeView);
                    }

                    var enclosedEl = this.$el.find("#Compose\\:CL\\:Enclosed");
                    if (enclosedEl.length > 0) {
                        this.enclosedComposeView = new EnclosedComposeView({
                            el: enclosedEl,
                            model: this.model,
                            section: "SkillsPassport.CoverLetter.Documentation",
                            initialRendering: true
                        });
                        this.sections.push(this.enclosedComposeView);
                    }

                }

                , onReRendering: function (origin) {

                    this.cleanupChildSections();

                    this.reRender(this.section, origin);
                }

                , enableFunctionalities: function (model) {
                    this.displayNewEuropassPortalNotification(GuiLabel["new.europass.portal.info"]);
                    this.displayDocumentNotification(GuiLabel["ecl.download.document.hint"] || "GuiLabel[ecl.download.document.hint]");
                    this.displayMissingDocumentNotification(GuiLabel["uploaded.documents.not.available.text.info"]);
                }

                , toggleGlobalJustify: function (event, align) {

                    if (this.prefsView !== undefined && this.prefsView !== null) {
                        //TODO: revise fucntionallity to use the proepr one for handling the justify button tip spot
//					if ( (isTablet && _.isFunction(this.tablets.handleTipSpot)) && this.tablets.handleTipSpot(event,"target") === false ) {
//						return false;
//					} 
                        this.justifyCLSections(align);
                    }
                    return false;
                }

                , justifyCLSections: function (align) {
                    //start the waiting indicator...
                    var justified = this.prefsView.toggleCLJustification(align);
                    if (justified === true) {
//					this.onReRendering("click-origin-controls");
                        this.model.trigger("prefs:order:changed", this.section);
                        this.model.trigger("content:changed", this.section, "click-origin-controls");
                    }
                }

                , toggleCLSignaturePersonName: function (event, enabled) {


                    var enableName = this.prefsView.toggleCLClosingSalutationNameEnabled(enabled);

                    if (enableName === true) {
                        this.model.trigger("prefs:order:changed", this.section);
                        this.model.trigger("content:changed", this.section, "click-origin-controls");
                    }
                }

            });
            return ComposeView;
        });
