define(
        [
            'jquery',
            'Utils',
//		'Interactions','views/interaction/CompoundMultiFieldView',
            'views/forms/FormView',
            'views/interaction/TypeaheadView',
            'hbs!templates/forms/cv/motherTongue',
            'europass/maps/MotherLanguageMap',
            'europass/structures/MapWrapper'
        ],
        function ($, Utils, FormView, TypeaheadView, HtmlTemplate, LanguageMap, MapWrapper) {

            var MotherTongueFormView = FormView.extend({

                htmlTemplate: HtmlTemplate

                , enableFunctionalities: function () {
                    //call parent enable functionalities

                    FormView.prototype.enableFunctionalities.call(this);

                    var frm = this.frm;

                    var that = this;
                    //1. ENABLE AUTOCOMPLETE
                    /* Bind Mother Language with AutoComplete */
                    var motherTongue = this.model.get("SkillsPassport.LearnerInfo.Skills.Linguistic.MotherTongue");
                    var foreignLanguage = this.model.get("SkillsPassport.LearnerInfo.Skills.Linguistic.ForeignLanguage");
                    var languageArray = [];
                    if (foreignLanguage !== undefined) {
                        languageArray = languageArray.concat(foreignLanguage);
                    }
                    if (motherTongue !== undefined) {
                        languageArray = languageArray.concat(motherTongue);
                    }
                    var mapWrapper = new MapWrapper(LanguageMap, languageArray);
                    /*				frm.find("div.composite.select2autocomplete[name\*=\"" + ".MotherTongue"+"\"]").each ( function( idx, el){
                     var MotherTongueAutocomplete = new Select2AutocompleteView({
                     el : $(el),
                     minLength: 1,
                     topN: 10,
                     map: mapWrapper
                     });
                     that.addToViewsIndex( MotherTongueAutocomplete );
                     });*/
                    frm.find("div.composite[name*=\".MotherTongue\"]").each(function (idx, el) {
                        var MotherTongueAutocomplete = new TypeaheadView({
                            el: $(el),
                            minLength: 0,
                            topN: 10,
                            map: mapWrapper,
                            multipliable: true,
                            exclusive: true,
                            global: true,
                            globalExcludedValues: Utils.modelArrayToValues(foreignLanguage)
                        });
                        that.addToViewsIndex(MotherTongueAutocomplete);
                    });

                    this.exclusionMap = mapWrapper;

                    //call parent FINALLY enable functionalities
                    FormView.prototype.finallyEnableFunctionalities.call(this);

                }//end enableFunctionalities

            });
            return MotherTongueFormView;
        }
);