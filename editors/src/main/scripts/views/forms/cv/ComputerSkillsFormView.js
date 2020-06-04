define(
        [
            'jquery',
            'Utils',
            'underscore',
            'views/forms/FormView',
            'views/forms/attachment/LinkedAttachmentFormView',
            'hbs!templates/forms/cv/computerSkills',
            'ModalFormInteractions'
//		'europass/maps/IctInformationProcessingLevelMap', 'europass/maps/IctCommunicationLevelMap', 'europass/maps/IctContentCreationLevelMap', 'europass/maps/IctSafetyLevelMap', 'europass/maps/IctProblemSolvingLevelMap'
//		'HelperUtils', 'Interactions', 'views/interaction/CompoundMultiFieldView', 'views/interaction/SelectFieldView','europass/structures/MapWrapper'
        ],
        function ($, Utils, _, FormView, LinkedAttachmentFormView, HtmlTemplate, ModalFormInteractions) {
//			IctInformationProcessingLevelMap, IctCommunicationLevelMap, IctContentCreationLevelMap,IctSafetyLevelMap, IctProblemSolvingLevelMap
//			HelperUtils, Interactions, CompoundMultiFieldView, SelectFieldView, MapWrapper

            var ComputerSkillsFormView = function (options) {
                LinkedAttachmentFormView.apply(this, [options]);
            };

            ComputerSkillsFormView.prototype = {

                htmlTemplate: HtmlTemplate

                        //Events of FormView plus those here..
                , events: _.extend({
                    "change.fs": "onChange",
                    "keydown #Form\\:LearnerInfo\\.Skills\\.Computer input[data-help-key=LearnerInfo\\.Skills\\.Computer\\.Certificate]": "stopPropagationEnterCertificate"
                }, LinkedAttachmentFormView.prototype.events)

                , CLEAR_LABEL: "–"

                , stopPropagationEnterCertificate: function (event) {
                    var code = (event.keyCode ? event.keyCode : event.which);
                    if (code === 13) { // ENTER key
                        event.stopPropagation();
                        return false;
                    }
                }

                , adjustContext: function (context, index, subsection) {
                    var certificateTitleList = Utils.objAttr(context, "SkillsPassport.LearnerInfo.Skills.Computer.Certificate");
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
                        context.SkillsPassport.LearnerInfo.Skills.Computer.Certificate = certificateTitleList;
                    }

                    return context;
                }
                /**
                 * @Override
                 */
//			,getRelatedSections: function( section ){
//				return ["Experience"];
//			}

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

            ComputerSkillsFormView.prototype = $.extend(
                    //true, 
                            {},
                            LinkedAttachmentFormView.prototype,
                            ComputerSkillsFormView.prototype
                            );

                    return ComputerSkillsFormView;
                }
        );