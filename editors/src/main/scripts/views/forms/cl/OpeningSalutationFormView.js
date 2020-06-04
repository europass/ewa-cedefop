define(
        [
            'jquery',
            'underscore',
            'views/forms/FormView',
            'views/interaction/TypeaheadView',
            'hbs!templates/forms/cl/openingSalutation',
            'europass/resources/ExtraFilteringOpeningClosingSalutationFrench'
        ],
        function ($, _, FormView, TypeaheadView, HtmlTemplate, FrenchOpeningClosingSalutationMapping) {

            var OpeningSalutationFormView = FormView.extend({
                htmlTemplate: HtmlTemplate

                , events: _.extend({
                    "change :input#limitedOpenSalutationField": "oneSalutationChanged",
                    "europass:salutation:check": "handleSalutationCodeChange"
                }, FormView.prototype.events)

                , enableFunctionalities: function () {
                    //call parent enable functionalities
                    FormView.prototype.enableFunctionalities.call(this);

                    var frm = this.frm;
                    var openingSalutationMapFiltered;

                    var that = this;
                    //1. ENABLE AUTOCOMPLETE

                    frm.find("div.composite[name\*=\".Salutation\"]").each(function (idx, el) {
                        openingSalutationMapFiltered = FrenchOpeningClosingSalutationMapping.filteringOpeningSalutationMap(that.model);
                        var cntAutocomplete = new TypeaheadView({
                            el: $(el),
                            minLength: 0,
                            topN: 10,
                            map: openingSalutationMapFiltered,
                            multipliable: false
                        });
                        that.addToViewsIndex(cntAutocomplete);
                    });
                    this.salutationFieldsAdjust(openingSalutationMapFiltered);

                    //check if 'surname' field should be readonly or not and adjust it properly
                    var codeEl = frm.find("[name$='.Code']")[0];
                    this.adjustSurname(codeEl);
                    //call parent FINALLY enable functionalities
                    FormView.prototype.finallyEnableFunctionalities.call(this);
                }
                , salutationFieldsAdjust: function (openingSalutationMapFiltered) {

                    var saluteArray = openingSalutationMapFiltered.toArray();
                    var entries = saluteArray.length;

                    var editableSalutation = this.frm.find("div#limitedOpenSalutationDiv");

                    if (entries <= 0) { // no entries or 1 entry ???

                        if (!_.isEmpty(editableSalutation)) {
                            editableSalutation.show();
                            var editableField = editableSalutation.find("input");
                            if (_.isEmpty(editableField.val()) && !_.isEmpty(saluteArray[0]) && !_.isEmpty(saluteArray[0].value)) {
                                editableField.val(saluteArray[0].value);
                            }
                        }

                    } else {
                        editableSalutation.remove();
                        if (!_.isEmpty(editableSalutation)) {
                            editableSalutation.hide();
                        }
                    }
                }

                , handleSalutationCodeChange: function (event, codeEl) {
                    this.adjustSurname(codeEl);
                },
                disableSurname: function (event) {
                    //console.log('inside disableSurname');
                    var surname = $(event.target);
                    surname.addClass("inputDisabled");
                }
                , adjustSurname: function (codeEl) {
                    var code = codeEl.value;
                    //console.log('code: ' + code);
                    var surnameActive = true;
                    if (code !== undefined && code !== null && code !== "" && (code.indexOf("impersonal") > -1)) {
                        surnameActive = false;
                    }

                    var surname = this.frm.find(":input[name$=\"Surname\"]");

                    //If surname is to be active, and it is not already readonly, skip the steps below
                    if (surnameActive && !surname.hasClass("readonly")) {
                        surname.unbind("focus", this.disableSurname);
                        surname.removeClass("inputDisabled");
                        //				console.log("return");				
                        return;
                    }
//			console.log("decide");
                    if (surnameActive) {
                        surname.unbind("focus", this.disableSurname);
                        surname.removeClass("inputDisabled");
                        surname.removeClass("readonly");
                        surname.removeAttr("readonly");
                        var prev = surname.attr("data-prev-value");
                        if ("" === surname.val() && _.isString(prev))
                            surname.val(prev);
                        surname.blur();
                    } else {
                        surname.bind("focus", this.disableSurname);
                        surname.addClass("readonly");
                        surname.prop("readonly", true);
                        if ("" !== surname.val()) {
                            surname.attr("data-prev-value", surname.val());
                            surname.val("");
                        }
                        surname.blur();
                    }
                }
            });

            return OpeningSalutationFormView;
        }
);