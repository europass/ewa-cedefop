define(
        [
            'jquery',
            'underscore',
            'views/forms/FormView',
            'views/interaction/TypeaheadView',
            'views/forms/cl/SignaturePhotoFormView',
            'europass/resources/ExtraFilteringOpeningClosingSalutationFrench',
            'ModalFormInteractions'
        ],
        function ($, _, FormView, TypeaheadView, SignaturePhotoFormView, FrenchOpeningClosingSalutationMapping, ModalFormInteractions) {

            var ClosingSalutationFormView = function (options) {
                SignaturePhotoFormView.apply(this, [options]);
            };

            ClosingSalutationFormView.prototype = {

                htmlTemplate: "forms/cl/closingSalutation"
                        /* TODO this is blocking signature preview from showing, needs fix 
                         ,events : _.extend({
                         "change :input#editableClosingSalutation" : "oneSalutationChanged"
                         }, FormView.prototype.events)**/

                , enableFunctionalities: function () {
                    //call parent enable functionalities
                    FormView.prototype.enableFunctionalities.call(this);

                    var frm = this.frm;
                    var closingSalutationMapFiltered;

                    var that = this;
                    //1. ENABLE AUTOCOMPLETE

                    frm.find("div.composite[name\*=\".ClosingSalutation\"]").each(function (idx, el) {
                        closingSalutationMapFiltered = FrenchOpeningClosingSalutationMapping.filteringClosingSalutationMap(that.model);
                        var cntAutocomplete = new TypeaheadView({
                            el: $(el),
                            minLength: 0,
                            topN: 10,
                            map: closingSalutationMapFiltered,
                            multipliable: false
                        });
                        that.addToViewsIndex(cntAutocomplete);
                    });
                    this.salutationFieldsAdjust(closingSalutationMapFiltered);

                    SignaturePhotoFormView.prototype.enableFunctionalities.call(this);

                    //call parent FINALLY enable functionalities
                    FormView.prototype.finallyEnableFunctionalities.call(this);

                }
                , salutationFieldsAdjust: function (closingSalutationMapFiltered) {

                    var saluteArray = closingSalutationMapFiltered.toArray();
                    var entries = saluteArray.length;

                    var editableSalutation = this.frm.find("div#editableClosingSalutation");
                    var selectableSalutation = this.frm.find("div.composite.select[name\*=\".ClosingSalutation\"]");

                    if (entries < 1) {

                        if (!_.isEmpty(editableSalutation)) {
                            editableSalutation.show();
                            var editableField = editableSalutation.find("input");
                            if (_.isEmpty(editableField.val()) && !_.isEmpty(saluteArray[0]) && !_.isEmpty(saluteArray[0].value)) {
                                editableField.val(saluteArray[0].value);
                            }
                        }
                        if (!_.isEmpty(selectableSalutation)) {
                            selectableSalutation.hide();
                        }
                    } else {
                        editableSalutation.remove();
                        if (!_.isEmpty(editableSalutation)) {
                            editableSalutation.hide();
                        }
                    }
                }

                , submitted: function (event, globalDateFormatUpdated) {
                    SignaturePhotoFormView.prototype.saveImage.call(this);

                    FormView.prototype.submitted.apply(this, [event, globalDateFormatUpdated]);
                }
                
                , modalClosed: function (event, globalDateFormatUpdated) {
                    if (SignaturePhotoFormView.prototype.formDataChanged.call(this)) {
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

            ClosingSalutationFormView.prototype = $.extend(
                    //true,
                            {},
                            SignaturePhotoFormView.prototype,
                            ClosingSalutationFormView.prototype
                            );

                    return ClosingSalutationFormView;
                }
        );