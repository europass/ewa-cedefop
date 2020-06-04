define(
        [
            'jquery',
            'underscore',
            'Utils',
            'views/compose/ComposeView',
            'views/compose/elp/OverviewComposeView',
            'views/compose/elp/LanguageListComposeView',
            'hbs!templates/compose/elp/maincompose',
            'routers/SkillsPassportRouterInstance',
            'ModalFormInteractions',
            'i18n!localization/nls/GuiLabel',
            'europass/TabletInteractionsView'
        ],
        function ($, _, Utils, MasterComposeView
                , OverviewComposeView
                , LanguageListComposeView
                , HtmlTemplate
                , AppRouter
                , ModalFormInteractions
                , GuiLabel
                , TabletInteractionsView) {

            var ComposeView = MasterComposeView.extend({

                htmlTemplate: HtmlTemplate

                , tagName: "section"

                , elpSortableTblColor: "elp-sortable-tbl-hover"

                , className: "navigation-area wysiwyg Compose"

                , events: _.extend({

                    "click 		.opens-modal-form:not(>.exp-placeholder)": "openModalForm", //Sections-Add

                    "click 		:button.delete:not(.photo):not(.attachment)": "deleteSection", //Button-Delete

                    "click		:button.autoSort": "sortSection", //Desktop - section sorting  behavior -  auto sort
                    "click      :button[class^=sort-move]:not(.inactive)": "sortSection", //Desktop - section sorting  behavior - sort move buttons (up, down and top)
                    "click      span.sortable_placeholder": "sortSection", //Desktop - section sorting  behavior - sortable placeholder

                    "click		.subsection-container.compose-list.list-item": "focusInSection", //Section sorting  behavior, sortable placeholder, auto sort and sort move buttons (up, down and top)

                    "touchstart .ui-sortable > tr.compose-list.list-item": "focusInSection",

                    "mouseover :button[class^=sort-move]": "elpTableOverEffect",
                    "mouseout  :button[class^=sort-move]": "elpTableOverEffect",

                    "mouseover button.elp.delete": "elpTableOverEffect",
                    "mouseout  button.elp.delete": "elpTableOverEffect",

                    "europass:cefrgrid:toggle": "elpToggleCEFRgrid"

                }, MasterComposeView.prototype.events)

                , onInit: function (options) {

                    this.navigation = options.navigation;

                    this.sections = [];

                    //Re-render when emptying ESP
                    this.model.bind("model:content:reset", this.onReRendering, this);
                    //Re-render when uploading an ESP
                    this.model.bind("model:uploaded:esp", this.onReRendering, this);
                    //Re-render when uploading from cloud
                    this.model.bind("model:uploaded:cloud", this.onReRendering, this);
                    // Cloud services document loading
                    this.model.bind("model:loaded:cloud:document", this.onReRendering, this);

                    MasterComposeView.prototype.onInit.apply(this, [options]);

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

                    this.model.unbind("model:content:reset", this.onReRendering);
                    this.model.unbind("model:uploaded:esp", this.onReRendering);
                    this.model.unbind("model:uploaded:cloud", this.onReRendering, this);
                    this.model.unbind("model:loaded:cloud:document", this.onReRendering, this);
                }
                , openModalForm: function (event) {
                    if (isTablet) {
                        var el = $(event.currentTarget).closest('[data-rel-section]');
                        if (!($(el).hasClass('firstTap'))) {
                            $('.firstTap').removeClass('firstTap');
                            $(el).addClass('firstTap');
                            this.tablets.addTapHover($(el));
                            return false;
                        }
                        if ($(el).closest('button.delete').length !== 0) {
                            return false;
                        }
                        if ($(el).closest('span.sortable_placeholder').length !== 0) {
                            return false;
                        }
                        $('.firstTap').removeClass('firstTap');
                        //$(".tap-hover").removeClass("tap-hover");
                        event.currentTarget = el;
                        var isSortablePlaceholder = el.is("span.sortable_placeholder");

                        //console.log("opentbl in();");
                        if (isSortablePlaceholder)
                            return false;
                        //console.log("opentbl opening();");
                        this.tablets.handleModalForm(event, ModalFormInteractions);
                        this.focusInSection(event); // add tap hover to the parent item in order for additional buttons to show
                    } else {
                        ModalFormInteractions.openForm(event, Utils.EditorForms.elpFormsLoaded);
                        Utils.EditorForms.elpFormsLoaded = true;
                    }
                }
                , sortSection: function (event) {
//				console.log("sortSection();");
                    if ((isTablet && _.isFunction(this.tablets.handleButton)) && this.tablets.handleButton(event) === false) {
                        return false;
                    }
                }
                , focusInSection: function (event) {
//				console.log("focusInSection();");
                    if (isTablet) {
                        var el = $(event.currentTarget);
                        $(".tap-hover").removeClass("tap-hover");
                        var elClosest = el.closest("ol.compose-list > li.list-item");
                        if (elClosest.length > 0) {
                            elClosest.addClass("tap-hover");
                        }
                        elClosest = el.closest(".compose-list.list-item");
                        if (elClosest.length > 0) {
                            elClosest.addClass("tap-hover");
                        }
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
                }
                , CHILD_SECTIONS_REGEXP: new RegExp(/([\S]*)ForeignLanguage\[\d+\]\.(ProficiencyLevel|((Certificate|Experience)\[\d+\]))$/)
                        /**
                         * We override the function that runs as a response to various events (e.g. model:content:changed)
                         * so that we make sure that the child views are properly cleaned-up before they are re-rendered.
                         */
                        //@Override
                , reRender: function (relSection, origin) {
//console.log(" Father ELP :: reRender when relSection is '"+relSection+"'");
                    //e.g. SkillsPassport.LearnerInfo.Identification SkillsPassport.LearnerInfo.Skills.Linguistic
                    //e.g. SkillsPassport.LearnerInfo.Skills.Linguistic.ForeignLanguage
                    if (relSection.indexOf(this.section) === 0 && this.CHILD_SECTIONS_REGEXP.test(relSection) === false) {
                        this.cleanupChildSections();
//console.log("Father ELP :: DO render after clean up ");
                        this.render(this.reRenderIndicator, [this.doTransition(origin)]);
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

                    //overview elp
                    this.overviewComposeView = new OverviewComposeView({
                        el: '#Compose\\:ElpOverview',
                        model: this.model,
                        section: "SkillsPassport.LearnerInfo.Identification.PersonName SkillsPassport.LearnerInfo.Skills.Linguistic.MotherTongue SkillsPassport.LearnerInfo.Skills.Linguistic.ForeignLanguage",
                        initialRendering: true
                    });
                    this.sections.push(this.overviewComposeView);

                    //list of foreign languages
                    this.languageListComposeView = new LanguageListComposeView({
                        el: '#Compose\\:ElpForeignLanguagesList',
                        model: this.model,
                        section: "SkillsPassport.LearnerInfo.Skills.Linguistic.ForeignLanguage",
                        initialRendering: true
                    });
                    this.sections.push(this.languageListComposeView);

                    //This will open the uploda model if requested so by the URL
                    if (_.isFunction(this.openModals)) {
                        this.openModals(this.options);
                    }
                }

                , onReRendering: function (origin) {
                    this.cleanupChildSections();
                    this.reRender(this.section, origin);
                }
                , MAIN_VIEW_REGEXP: new RegExp(/([\S]*)ForeignLanguage$/)

                , downloadElp: function (event) {
                    var link = $(event.target);
                    var href = link.attr("data-href");

                    if (href === undefined) {
                        return  false;
                    }

                    var passThrough = (href.indexOf('sign_out') >= 0);

                    if (!passThrough && !event.altKey && !event.ctrlKey && !event.metaKey && !event.shiftKey) {
                        event.preventDefault();
                    }

                    //Remove leading slashes and hash bangs (backward compatablility)
                    var url = href.replace(/^\//, '').replace('\#\!\/', '');

                    if (url.indexOf("/") !== 0) {
                        url = "/" + url;
                    }

                    AppRouter.navigate(url, {
                        trigger: true,
                        replace: !("pushState" in window.history) //To update the URL without creating an entry in the browser's history, set the replace option to true. 
                    });

                    return false;
                }

                , deleteSection: function (event) {

                    // TODO CLEAN UP COMMENT On mobile not using 2 step tooltip anymore !!
                    // if ((isTablet && _.isFunction(this.tablets.handleButton)) && this.tablets.handleButton(event) === false) {
                    // 	return false;
                    // } else {
                    // 	ModalFormInteractions.confirmDeleteSection(event);
                    // }
                    ModalFormInteractions.confirmDeleteSection(event);
                }

                , enableFunctionalities: function (model) {
                    this.displayNewEuropassPortalNotification(GuiLabel["new.europass.portal.info"]);
                    this.displayDocumentNotification(GuiLabel["elp.download.document.hint"]);
                }

                , elpTableOverEffect: function (event) {
                    var el = $(event.target);
                    var evtype = event.type;

                    if (el === undefined || el == null || el == "")
                        return;

                    var tbl = el.closest('dl.foreign-language-details');
                    if (tbl.length > 0) {
                        if (evtype == "mouseover")
                            tbl.addClass(this.elpSortableTblColor);
                        else if (evtype == "mouseout")
                            tbl.removeClass(this.elpSortableTblColor);
                    }
                }

                , elpToggleCEFRgrid: function (event, include) {

                    var switched = this.overviewComposeView.prefsView.toggleCEFLanguageLevelsGrid(include);
                    if (switched === true) {
                        this.model.trigger("prefs:cefrgrid:changed", this.section);
                        this.reRender(this.section, "click-origin-controls");
                    }
                }

            });
            return ComposeView;
        });