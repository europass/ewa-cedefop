define(
        [
            'jquery',
            'underscore',
            'Utils',
            'views/interaction/ListView',
            'views/compose/ComposeView',
            'views/compose/elp/ExperienceListComposeView',
            'views/compose/elp/CertificateListComposeView',
            'hbs!templates/compose/elp/languageList'
        ],
        function ($, _, Utils, ListView, ComposeView, ExperienceListComposeView, CertificateListComposeView, HtmlTemplate) {

            var LanguageListComposeView = ComposeView.extend({

                htmlTemplate: HtmlTemplate

                , events: _.extend({
                    "click :button:not(.inactive).sort-move-up": "sortMoveUp",
                    "click :button:not(.inactive).sort-move-down": "sortMoveDown",
                    "click :button.sort-move-top": "sortMoveTop"
                }, ComposeView.prototype.events)

                , onInit: function (options) {
                    this.subsections = [];

                    this.model.bind("model:prefs:data:format:changed", this.dateFormatChanged, this);

                    ComposeView.prototype.onInit.apply(this, [options]);
                }
                , onClose: function () {
                    ComposeView.prototype.onClose.apply(this);

                    $(this.subsections).each(function (idx, subsection) {
                        if (_.isObject(subsection) && _.isFunction(subsection.close))
                            subsection.close();
                    });

                    this.subsections = [];

                    this.model.unbind("model:prefs:data:format:changed", this.dateFormatChanged);
                }
                , openModalForm: function (event) {
                    ModalFormInteractions.openForm(event);
                }

                /**
                 * Properly close all child sections
                 */
                , cleanupChildSections: function () {
                    for (var i = 0; i < this.subsections.length; i++) {
                        var subsection = this.subsections[ i ];
                        if (_.isObject(subsection) && _.isFunction(subsection.close))
                            subsection.close();
                    }
                    this.subsections = [];
                }
                /**
                 * @Override
                 */
                , render: function (args) {
                    ComposeView.prototype.render.apply(this, [args]);

                    if ($.isArray(this.subsections) && this.subsections.length === 0) {
                        this.onRender();
                    }
                }
                , ITEM_REGEXP: new RegExp(/([\S]*)ForeignLanguage\[\d+\](\.ProficiencyLevel)?$/)

                        /**
                         * @Override
                         */
                , reRender: function (relSection, origin) {
//console.log("reRender ELP FOREIGN LIST View '"+this.section+"' when relSection is '"+relSection+"'");
                    //e.g. SkillsPassport.LearnerInfo.Skills.Linguistic.ForeignLanguage
                    if (relSection === this.section || this.ITEM_REGEXP.test(relSection) === true) {

                        this.cleanupChildSections();
//console.log("ELP FOREIGN LIST View :: do render after clean up ");					
                        this.render(this.reRenderIndicator, [this.doTransition(origin)]);
                    }
                }
                /**
                 * Returns a list with the indexes of active/visible languages
                 */
                , activeLanguages: function () {
                    var languages = this.model.get(this.section);
                    if ($.isArray(languages) === false) {
                        return 0;
                    }
                    var activeItems = [];
                    for (var i = 0; i < languages.length; i++) {
                        if (!Utils.isEmptyObject(languages[i])) {
                            activeItems.push(i);
                        }
                    }
                    return activeItems;

                }
                /**
                 * Get the length of the foreign language list
                 * and create that many views of CertificateList and ExperienceList.
                 * 
                 * Add each one in the array of subsections, so that the view
                 * has a reference on how many sub-views it contains.
                 * This way proper cleanup and re-initialisation will be achieved.
                 */
                , onRender: function () {

                    this.subsections = [];

                    var activeLangs = this.activeLanguages();


                    for (var i = 0; i < activeLangs.length; i++) {
                        var idx = activeLangs[i];
                        var listOfCertificates = new CertificateListComposeView({
                            el: "#Compose\\:LearnerInfo\\.Skills\\.Linguistic\\.ForeignLanguage\\[" + idx + "\\]\\.Certificate",
                            model: this.model,
                            itemIndex: idx,
                            section: "SkillsPassport.LearnerInfo.Skills.Linguistic.ForeignLanguage[" + idx + "].Certificate",
                            initialRendering: true
                        });
                        this.subsections.push(listOfCertificates);

                        var listOfExperiences = new ExperienceListComposeView({
                            el: "#Compose\\:LearnerInfo\\.Skills\\.Linguistic\\.ForeignLanguage\\[" + idx + "\\]\\.Experience",
                            model: this.model,
                            itemIndex: idx,
                            section: "SkillsPassport.LearnerInfo.Skills.Linguistic.ForeignLanguage[" + idx + "].Experience",
                            initialRendering: true
                        });
                        this.subsections.push(listOfExperiences);

                    }
                }
                , enableFunctionalities: function (model) {

                    ComposeView.prototype.enableFunctionalities.apply(this, [model]);

                    var that = this;
                    //List View for all section > compose-list
                    this.$el.find(".sortable.compose-list").each(function (idx, el) {
                        var list = $(el);
                        //Sortable when the list contains more than 1 item.
                        if (list.find("> li.list-item").length > 1) {
                            var listView = new ListView({
                                el: list,
                                model: model
                            });
                            that.addToViewsIndex(listView);
                        }
                    });
                }

                , sortMoveUp: function (event) {
                    this.$el.trigger("europass:sort:list:moveUp", [event.target, true]);
                }

                , sortMoveDown: function (event) {
                    this.$el.trigger("europass:sort:list:moveDown", [event.target, true]);
                }
                , sortMoveTop: function (event) {
                    this.$el.trigger("europass:sort:list:moveTop", [event.target, true]);
                }
            });
            return LanguageListComposeView;
        }
);