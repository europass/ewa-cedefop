define(
        [
            'jquery',
            'Utils',
//		'HelperUtils', 'Interactions', 'views/interaction/Select2AutocompleteView', 'views/interaction/CompoundMultiFieldView',
            'underscore',
            'views/forms/FormView',
            'views/forms/attachment/LinkedAttachmentFormView',
            'views/interaction/TypeaheadView',
            'hbs!templates/forms/cv/foreignLanguage',
            'europass/maps/LanguageMap',
//		'europass/maps/LanguageListeningLevelMap', 'europass/maps/LanguageReadingLevelMap', 'europass/maps/LanguageSpokenInteractionLevelMap', 'europass/maps/LanguageSpokenProductionLevelMap', 'europass/maps/LanguageWritingLevelMap',
            'europass/structures/MapWrapper',
            'ModalFormInteractions'
        ],
        function ($, Utils, _, FormView, LinkedAttachmentFormView, TypeaheadView, HtmlTemplate, LanguageMap, MapWrapper, ModalFormInteractions) {
//				LanguageListeningLevelMap, LanguageReadingLevelMap, LanguageSpokenInteractionLevelMap, LanguageSpokenProductionLevelMap, LanguageWritingLevelMap

            var ForeignLanguageFormView = function (options) {
                LinkedAttachmentFormView.apply(this, [options]);
            };

            ForeignLanguageFormView.prototype = {

                htmlTemplate: HtmlTemplate

                        //Events of FormView plus those here..
                , events: _.extend({
                    "change.fs": "onChange"
                }, LinkedAttachmentFormView.prototype.events)

                , CLEAR_LABEL: "–"

                , adjustContext: function (context, index, subsection) {
                    var certificateTitleList = Utils.objAttr(context, "SkillsPassport.LearnerInfo.Skills.Linguistic.ForeignLanguage[" + index + "].Certificate");
                    var certificateTitle = this.CLEAR_LABEL;
                    var jsonLabel = {Title: certificateTitle};
                    for (var i = 0; i < certificateTitleList.length; i++) {

                        if (certificateTitleList[i] !== null && certificateTitleList[i].Title !== null && !$.isEmptyObject(certificateTitleList[i].Title)) {
                            if (certificateTitleList[i].Title === "" || certificateTitleList[i].Title === null || $.isEmptyObject(certificateTitleList[i].Title)) {
                                certificateTitleList[i].Title = certificateTitle;
                            }
                        } else {
                            $.extend(true, certificateTitleList[i], jsonLabel);
                        }
                    }
                    if (certificateTitleList.length !== undefined) {
                        context.SkillsPassport.LearnerInfo.Skills.Linguistic.ForeignLanguage[index].Certificate = certificateTitleList;
                    }

                    return context;
                }
                /**
                 * @Override
                 */
                , getRelatedSections: function (section) {
                    return ["Experience"];
                }
                , formToModel: function (frm, section) {

                    var newModel = FormView.prototype.formToModel.apply(this, [frm]);

                    //Handle Certificates
                    var certFields = frm.find(":input.formfield[name*=\"Certificate\"]").filter(Utils.filterNonEmptyVal);
                    for (var i = 0; i < certFields.length; i++) {
                        var certField = $(certFields[i]);

                        var name = certField.attr("name");
                        var titleIndex = name.indexOf("Title");
                        var certPath = name.substr(0, titleIndex - 1);

                        var liveIndex = certField.attr("data-init-index");

                        if (_.isUndefined(liveIndex)) {
                            continue;
                        }

                        /*
                         * Copy live certificate
                         */
                        var liveCertificate = this.model.get(section + ".Certificate[" + liveIndex + "]");
                        var tmpCertificate = {};
                        $.extend(tmpCertificate, liveCertificate);
                        /*
                         * Use Title from temp Model
                         */
                        var m = newModel.model;
                        tmpCertificate.Title = m.get(certPath + ".Title");
                        m.set(certPath, tmpCertificate);
                    }
                    return newModel;
                }
                , enableFunctionalities: function () {
                    //call parent enable functionalities
                    LinkedAttachmentFormView.prototype.enableFunctionalities.call(this);

                    var frm = this.$el;

                    var that = this;
                    //1. ENABLE AUTOCOMPLETE
                    var foreignLanguage = this.model.get("SkillsPassport.LearnerInfo.Skills.Linguistic.ForeignLanguage");
                    var motherTongue = this.model.get("SkillsPassport.LearnerInfo.Skills.Linguistic.MotherTongue");
                    var languageArray = [];
                    if (foreignLanguage != undefined) {
                        languageArray = languageArray.concat(foreignLanguage);
                    }
                    if (motherTongue != undefined) {
                        languageArray = languageArray.concat(motherTongue);
                    }
                    var mapWrapper = new MapWrapper(LanguageMap, languageArray);
                    frm.find("div.composite.select[name\*=\"" + ".ForeignLanguage" + "\"]").each(function (idx, el) {
                        var languageAutocomplete = new TypeaheadView({
                            el: $(el),
                            minLength: 0,
                            topN: 10,
                            map: mapWrapper,
                            multipliable: false,
                            exclusive: true,
                            global: true,
                            globalExcludedValues: Utils.modelArrayToValues(languageArray)
                        });
                        that.addToViewsIndex(languageAutocomplete);
                    });

                    /*				//call parent FINALLY enable functionalities
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

                , onChange: function (event) {
                    // the event takes place on the select
                    this.formatSelected($(event.target).siblings(".trigger"));
                }
            };

            ForeignLanguageFormView.prototype = $.extend(
                    //true, 
                            {},
                            LinkedAttachmentFormView.prototype,
                            ForeignLanguageFormView.prototype
                            );

                    return ForeignLanguageFormView;
                }
        );