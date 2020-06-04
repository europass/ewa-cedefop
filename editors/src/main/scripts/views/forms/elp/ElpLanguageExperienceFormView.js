define(
        [
            'jquery',
            'views/forms/FormView',
            'views/interaction/TypeaheadView',
            'hbs!templates/forms/elp/elpLanguageExperience',
            'europass/structures/PreferencesSchema',
            'europass/maps/LinguisticExperienceAreaMap',
            'views/interaction/CurrentPositionView'
        ],
        function ($,
                FormView, TypeaheadView,
                HtmlTemplate, PreferencesSchema, LinguisticExperienceAreaMap, CurrentPositionView) {

            var ElpLanguageExperienceFormView = FormView.extend({

                htmlTemplate: HtmlTemplate

                , enableFunctionalities: function () {
                    //call parent enable functionalities
                    FormView.prototype.enableFunctionalities.call(this);

                    var frm = this.frm;

                    var that = this;
                    //1. ENABLE AUTOCOMPLETE
                    frm.find("div.composite.select[name\*=\"" + ".Area" + "\"]").each(function (idx, el) {
                        var cntAutocomplete = new TypeaheadView({
                            el: $(el),
                            minLength: 0,
                            topN: 10,
                            name: "experience-type",
                            map: LinguisticExperienceAreaMap
                        });
                        that.addToViewsIndex(cntAutocomplete);
                    });

                    //Bind the Current Position View 
                    frm.find("fieldset.Dates[name$=\"Period\"]").each(function (idx, el) {
                        var CurrentPosView = new CurrentPositionView({
                            el: $(el)
                        });
                        that.addToViewsIndex(CurrentPosView);
                    });


                    //ENABLE THE AUTOCOMPLETE AND MULTIFIELD FUNCTIONALITIES

                    /**
                     * Return the title of this section
                     */
                    var header = this.$el.find("header > legend > .subsection-title");
                    var title = PreferencesSchema.getSectionLabel(this.section, this.model.attributes.SkillsPassport, this.model.get(this.section), null, true);
                    header.html(title);

                    //call parent FINALLY enable functionalities
                    FormView.prototype.finallyEnableFunctionalities.call(this);
                }

            });

            return ElpLanguageExperienceFormView;
        }
);