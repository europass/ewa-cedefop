define(
        [
            'jquery',
            'views/forms/FormView',
            'views/forms/attachment/LinkedAttachmentFormView',
            'hbs!templates/forms/cv/genericSkills',
            'ModalFormInteractions'
        ],
        function ($, FormView, LinkedAttachmentFormView, HtmlTemplate, ModalFormInteractions) {

            var GenericSkillsFormView = function (options) {
                LinkedAttachmentFormView.apply(this, [options]);
            };

            GenericSkillsFormView.prototype = {
                htmlTemplate: HtmlTemplate

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
            };

            GenericSkillsFormView.prototype = $.extend(
                    //true, 
                            {},
                            LinkedAttachmentFormView.prototype,
                            GenericSkillsFormView.prototype
                            );

                    return GenericSkillsFormView;
                }
        );