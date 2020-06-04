define([
    'jquery',
    'underscore',
    'views/forms/FormView',
    'views/interaction/CurrentPositionView',
    'hbs!templates/forms/cl/localisationAndsubject'],
        function ($, _, FormView, CurrentPositionView, HtmlTemplate) {

            var SubjectLineLocalisationFormView = FormView.extend({

                htmlTemplate: HtmlTemplate

                , enableFunctionalities: function (model) {

                    FormView.prototype.enableFunctionalities.apply(this, [model]);

                    var frm = this.frm;

                    var that = this;

                    //Bind the Current Position View 
                    frm.find("fieldset.Dates[name$=\"Date\"]").each(function (idx, el) {
                        var CurrentPosView = new CurrentPositionView({
                            el: $(el)
                        });
                        that.addToViewsIndex(CurrentPosView);
                    });

                    this.prependSubject(frm);

                    //call parent FINALLY enable functionalities
                    FormView.prototype.finallyEnableFunctionalities.call(this);

                }
                /** EWA 959 find subject line and then prepend it to the subject input field */
                , prependSubject: function (frm) {
                    var subjectText = frm.find("fieldset.SubjectLine label");
                    var subjectLine = frm.find("fieldset.SubjectLine input");
                    var placeholder = frm.find("span.help.placeholder");

                    if (_.isEmpty(subjectLine.val()))
                        subjectLine.val(subjectText.html() + ":");
                    placeholder.hide();
                }

            });
            return SubjectLineLocalisationFormView;
        }
);