define(
        [
            'jquery', //'Utils',
            'views/forms/FormView',
            'views/forms/attachment/LinkedAttachmentFormView',
//		'views/interaction/Select2AutocompleteView',
            'views/interaction/TypeaheadView',
            'hbs!templates/forms/cv/additionalInfo',
            'europass/maps/AchievementTitlesMap',
            'views/interaction/PersonalDataTreatmentView',
            'ModalFormInteractions'
        ],
        function ($, FormView, LinkedAttachmentFormView, TypeaheadView, HtmlTemplate, AchievementTitlesMap, PersonalDataTreatmentView, ModalFormInteractions) {//Utils, Select2AutocompleteView, 

            var AchievementFormView = function (options) {
                LinkedAttachmentFormView.apply(this, [options]);
            };

            AchievementFormView.prototype = {
                htmlTemplate: HtmlTemplate

                , enableFunctionalities: function () {
                    //call parent enable functionalities
                    LinkedAttachmentFormView.prototype.enableFunctionalities.call(this);

                    var frm = this.frm;
                    var that = this;
                    //1. ENABLE AUTOCOMPLETE
                    /* Bind AchievementTitlesMap */
//				frm.find("div.composite.select2autocomplete[name\*=\"" + ".Title"+"\"]").each ( function( idx, el){
//					var cntAutocomplete = new Select2AutocompleteView({
//						el : $(el),
//						minLength: 1,
//						topN: 10,
//						map: AchievementTitlesMap
//					});
//					that.addToViewsIndex( cntAutocomplete );
//				});

                    frm.find("div.composite[name\*=\".Title\"]").each(function (idx, el) {
                        var cntAutocomplete = new TypeaheadView({
                            el: $(el),
                            minLength: 0,
                            topN: 10,
                            name: "countries",
                            isPersonalDataTreatment: that.isPersonalDataTreatment,
                            map: AchievementTitlesMap,
                            multipliable: false
                        });
                        that.addToViewsIndex(cntAutocomplete);
                    });
                    var personalDataTreatmentView = new PersonalDataTreatmentView({
                        el: frm.find("fieldset").first()
                    });
                    this.addToViewsIndex(personalDataTreatmentView);

                    //call parent FINALLY enable functionalities
                    LinkedAttachmentFormView.prototype.finallyEnableFunctionalities.call(this);
                }//end enableFunctionalities
                /**
                 * @Override
                 */
                , submitted: function (event, globalDateFormatUpdated) {
                    this.$el.trigger("europass:waiting:indicator:show");

                    LinkedAttachmentFormView.prototype.doSubmit.call(this);

                    FormView.prototype.submitted.apply(this, [event, globalDateFormatUpdated]);
                }
                
                , modalClosed: function (event, globalDateFormatUpdated) {
                    if (LinkedAttachmentFormView.prototype.doModalClosed.call(this)) {
                        ModalFormInteractions.confirmSaveSection(event, this.frm.attr("id"));
                    } else {
                        FormView.prototype.modalClosed.apply(this, [event, globalDateFormatUpdated]);
                    }
                }
                
                /**
                 * @Override
                 */
                , cancelled: function (event) {
                    FormView.prototype.cancelled.apply(this, [event]);
                }

            };

            AchievementFormView.prototype = $.extend(
                    //true, 
                            {},
                            LinkedAttachmentFormView.prototype,
                            AchievementFormView.prototype
                            );

                    return AchievementFormView;
                }
        );