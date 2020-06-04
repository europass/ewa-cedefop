define(
        [
            'jquery',
            'underscore',
            'Utils',
            'views/forms/FormView',
            'views/interaction/TypeaheadView',
            'hbs!templates/forms/elp/elpProficiencyLevel',
            'europass/maps/LanguageMap',
            'europass/structures/PreferencesSchema',
            'europass/structures/MapWrapper'//,'HelperUtils'
        ],
        function ($, _, Utils, FormView, TypeaheadView, HtmlTemplate, LanguageMap, PreferencesSchema, MapWrapper) {

            var ElpProficiencyLevelFormView = FormView.extend({

                htmlTemplate: HtmlTemplate

                , events: _.extend({
                    "change.fs": "onChange"
                }, FormView.prototype.events)

                , enableFunctionalities: function () {
                    //call parent enable functionalities
                    FormView.prototype.enableFunctionalities.call(this);

                    //Events of FormView plus those here..
                    //ENABLE THE AUTOCOMPLETE AND MULTIFIELD FUNCTIONALITIES
                    /**
                     * Return the title of this section
                     */
                    var header = this.$el.find("header > legend > .subsection-title");
                    var sections = this.section.split(" ");
                    var section = sections[0];
                    var title = PreferencesSchema.getSectionLabel(section, this.model.attributes.SkillsPassport, this.model.get(section), null, true);
                    header.html(title);
                    var frm = this.frm;

                    var that = this;
                    /**
                     *  Bind Foreign Language with AutoComplete 
                     */
                    var foreignLanguage = this.model.get("SkillsPassport.LearnerInfo.Skills.Linguistic.ForeignLanguage");
                    var motherTongue = this.model.get("SkillsPassport.LearnerInfo.Skills.Linguistic.MotherTongue");
                    var languageArray = [];
                    if (foreignLanguage !== undefined) {
                        languageArray = languageArray.concat(foreignLanguage);
                    }
                    if (motherTongue !== undefined) {
                        languageArray = languageArray.concat(motherTongue);
                    }
                    var mapWrapperFL = new MapWrapper(LanguageMap, languageArray);
                    frm.find("div.composite.select[name\*=\"" + ".ForeignLanguage" + "\"]").each(function (idx, el) {
                        var languageAutocomplete = new TypeaheadView({
                            el: $(el),
                            minLength: 0,
                            topN: 10,
                            name: "proficiency-level",
                            exclusive: true,
                            map: mapWrapperFL,
                            global: $(el).parent().is(".global") ? true : false,
                            globalExcludedValues: Utils.modelArrayToValues(languageArray)
                        });
                        that.addToViewsIndex(languageAutocomplete);
                    });

                    /*			//call parent FINALLY enable functionalities
                     options = {
                     select2Config : {
                     applyTo:[
                     {
                     namePattern: "ProficiencyLevel",
                     config: {
                     formatResult: this.select2FormatResult,
                     formatSelection : this.formatAssesmentLevelSelect
                     }
                     }
                     ]
                     }
                     };*/
                    FormView.prototype.finallyEnableFunctionalities.call(this);
                }

                , onChange: function (event) {
                    this.formatSelected($(event.target).siblings(".trigger"));
                }
            });

            return ElpProficiencyLevelFormView;
        }
);