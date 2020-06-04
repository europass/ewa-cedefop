define(
        [
            'jquery',
            'Utils',
            'views/forms/FormView',
            'views/interaction/TypeaheadView',
            'hbs!templates/forms/elp/elpOverview',
            'europass/maps/LanguageMap',
            'europass/maps/MotherLanguageMap',
            'europass/structures/MapWrapper',
            'views/interaction/KnownLanguagesView'//,'i18n!localization/nls/Language'
        ],
        function ($, Utils, FormView, TypeaheadView, HtmlTemplate,
                LanguageMap, MotherLanguageMap, MapWrapper, KnownLanguagesView) {//,Language

            var ElpOverviewFormView = FormView.extend({
                htmlTemplate: HtmlTemplate

                , CLEAR_LABEL: "–"

                , adjustContext: function (context, index, subsection) {
                    var foreignLanguageList = Utils.objAttr(context, "SkillsPassport.LearnerInfo.Skills.Linguistic.ForeignLanguage");
                    var foreignLangLabel = this.CLEAR_LABEL;
                    var jsonLabel = {Description: {Label: foreignLangLabel}};
                    for (var i = 0; i < foreignLanguageList.length; i++) {

                        if (foreignLanguageList[i] !== null && foreignLanguageList[i].Description !== null && !$.isEmptyObject(foreignLanguageList[i].Description)) {
                            if (foreignLanguageList[i].Description.Label === "" || foreignLanguageList[i].Description.Label === null || $.isEmptyObject(foreignLanguageList[i].Description.Label)) {
                                foreignLanguageList[i].Description.Label = foreignLangLabel;
                            }
                        } else {
                            $.extend(true, foreignLanguageList[i], jsonLabel);
                        }
                    }
                    if (foreignLanguageList.length !== undefined) {
                        context.SkillsPassport.LearnerInfo.Skills.Linguistic.ForeignLanguage = foreignLanguageList;
                    }

                    return context;
                },
                getRelatedSections: function (section) {
                    switch (section) {
                        case "SkillsPassport.LearnerInfo.Identification.PersonName":
                        {
                            return ["ContactInfo", "Demographics", "Photo", "Signature"];
                        }
                        case "SkillsPassport.LearnerInfo.Skills.Linguistic.MotherTongue":
                        {
                            return [];
                        }
                        case "SkillsPassport.LearnerInfo.Skills.Linguistic.ForeignLanguage":
                        {
                            return ["ProficiencyLevel", "Certificate", "Experience"];
                        }
                    }
                }
                , enableFunctionalities: function () {
                    //call parent enable functionalities
                    FormView.prototype.enableFunctionalities.call(this);

                    //ENABLE THE AUTOCOMPLETE AND MULTIFIELD FUNCTIONALITIES
                    var frm = this.frm;

                    var that = this;
                    //1. ENABLE AUTOCOMPLETE
                    //Prepare array of not-currently used language arrays
                    var motherTongue = this.model.get("SkillsPassport.LearnerInfo.Skills.Linguistic.MotherTongue");
                    var foreignLanguage = this.model.get("SkillsPassport.LearnerInfo.Skills.Linguistic.ForeignLanguage");
                    var languageArray = [];
                    if (foreignLanguage !== undefined) {
                        languageArray = languageArray.concat(foreignLanguage);
                    }
                    if (motherTongue !== undefined) {
                        languageArray = languageArray.concat(motherTongue);
                    }
                    /* Bind Mother Language with AutoComplete */
                    var mapWrapperMT = new MapWrapper(MotherLanguageMap, languageArray);
                    frm.find("div.composite.select[name\*=\"" + ".MotherTongue" + "\"]").each(function (idx, el) {
                        var MotherTongueAutocomplete = new TypeaheadView({
                            el: $(el),
                            minLength: 0,
                            topN: 10,
                            name: "mother-tongue",
                            map: mapWrapperMT,
                            multipliable: true,
                            exclusive: true,
                            global: $(el).parent(".multipliable.compound").is(".global") ? true : false,
                            globalExcludedValues: Utils.modelArrayToValues(languageArray)
                        });
                        that.addToViewsIndex(MotherTongueAutocomplete);
                    });
                    /* Bind Foreign Language with AutoComplete */
                    var mapWrapperFL = new MapWrapper(LanguageMap, languageArray);
                    frm.find("div.composite.select[name\*=\"" + ".ForeignLanguage" + "\"]").each(function (idx, el) {
                        var languageAutocomplete = new TypeaheadView({
                            el: $(el),
                            minLength: 0,
                            topN: 10,
                            name: "foreign-language",
                            map: mapWrapperFL,
                            multipliable: true,
                            exclusive: true,
                            global: $(el).parent(".multipliable.compound").is(".global") ? true : false,
                            globalExcludedValues: Utils.modelArrayToValues(languageArray)
                        });
                        that.addToViewsIndex(languageAutocomplete);
                    });


                    var knownLanguagesView = new KnownLanguagesView({
                        el: frm.find("fieldset").first()
                    });
                    that.addToViewsIndex(knownLanguagesView);

                    //call parent FINALLY enable functionalities
                    FormView.prototype.finallyEnableFunctionalities.call(this);
                }
            });

            return ElpOverviewFormView;
        }
);