/**
 * This view is bound to .....AchievementFormView
 * Add Personal Data Treatment Description in available languages
 */
define(
        [
            'jquery', //'underscore',
            'backbone',
            'Utils',
            'i18n!localization/nls/PersonalDataTreatment',
            'i18n!localization/nls/AchievementType',
            'ckeditor'
        ],
        function ($, Backbone, Utils, PersonalDataTreatment, AchievementType, CKEDITOR) {

            var PersonalDataTreatmentView = Backbone.View.extend({
                TAXONOMY_KEY: "signature_equivalent",
                fallback_taxonomy_des: "/ewa/cv/compose/additional-information/achievement/authorisation/default-description",

                events: {
                    "europass.personal.data.treatment": "updateDescription"
                }

                /**
                 * Update the description in Additional information
                 * When the locale is Italian, then the user can select Dati Personali and a default text is inserted in the textarea
                 * The Dati Personali is diselected or altered or cleared, then the textarea is also cleared (if it has the default text of Data Personali which is the PersonalDataTreatment Bundle
                 * Locales that support the Personal Data Treatment : Italian(it) and Polonez(pl) 
                 * clear: parameter to declare that the textarea should be cleared. it is initiated from TypeaheadView.clearInput
                 * 
                 */
                , updateDescription: function (event, clear) {
                    var el = $(event.target);
                    var form = el.closest("form.modalform");

                    if (!el.is(".typeahead.tt-input[name$=\"Title.Label\"]"))
                        return;

                    //get the sibling input that holds the key of the selection
                    var keyEl = ($(el).parents()).find(":hidden[name$=\"Title.Code\"]").first();
                    if (keyEl !== null && keyEl !== undefined) {
                        //given the key value, if this equals signature_equivalent
                        var key = keyEl.val();
                        var description = PersonalDataTreatment[key];  //PersonalDataTreatment.get();
                        var taxonomy_des = PersonalDataTreatment[this.TAXONOMY_KEY];
                        if (!Utils.isUndefined(clear)) {
                            if (clear && key !== this.TAXONOMY_KEY) {
                                this.clearPersonalDataTreatment(event);
                                //return;
                            }
                        }
                        for (var instanceName in CKEDITOR.instances) {
                            var ckeditor = CKEDITOR.instances[instanceName];
                        }

                        if (key !== null && key !== undefined && key === this.TAXONOMY_KEY) {
                            //then get the description from the language bundle if that exists
                            // if the description is not null, and the rte is not null? then update the rte with the description
                            if (ckeditor) {
                                ckeditor.setData(description);
                            }
                            var placeholder = $(el).parents().find('.editor').find('span.placeholder');
                            if (placeholder.length > 0) {
                                placeholder.hide();
                            }
                        } else if (key !== this.TAXONOMY_KEY && (ckeditor.getData() === taxonomy_des || taxonomy_des === this.fallback_taxonomy_des)) {
                            ////if is not DataPersonali but the text is of DatiPersonali (fix for all locales)
                            //else if ( key !== this.TAXONOMY_KEY && $("div.redactor_rich-editor").text() == taxonomy_des ){
                            if (!Utils.isUndefined(clear) && AchievementType[this.TAXONOMY_KEY] !== undefined) {
                                if (ckeditor) {
                                    ckeditor.setData('');
                                }
                            }
                        }
                    }
                }

                , clearPersonalDataTreatment: function (event) {
                    var el = $(event.target);
                    for (var instanceName in CKEDITOR.instances) {
                        var ckeditor = CKEDITOR.instances[instanceName];
                    }
                    if (ckeditor) {
                        ckeditor.setData('');
                        var placeholder = $(el).parents().find('.editor').find('span.placeholder');
                        placeholder.show();
                    }

                }
            });

            return PersonalDataTreatmentView;
        }
);