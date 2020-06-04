define(
        [
            'jquery',
            'underscore',
            'views/forms/FormView',
            'views/interaction/TypeaheadView', //'views/interaction/CompoundMultiFieldView',
            'views/interaction/SelectFieldView',
            'hbs!templates/forms/cl/holderDetails',
            'europass/maps/CountryMap',
            'europass/maps/InstantMessagingTypeMap',
            'europass/maps/TelephoneTypeMap'
        ],
        function ($, _, FormView, TypeaheadView, SelectFieldView, HtmlTemplate, CountryMap, InstantMessagingTypeMap, TelephoneTypeMap) {//, CompoundMultiFieldView

            var HolderDetailsFormView = FormView.extend({
                htmlTemplate: HtmlTemplate

                , events: _.extend({
                    "change :input:not(button):not(:radio):not(:checkbox).Telephone[name\$=\".Use.Code\"]": "getPhoneSelection",
                    "keyup input[type=email]": "isEmailValid"
                }, FormView.prototype.events)

                , maxItems: 2

                , enableFunctionalities: function () {
                    //call parent enable functionalities
                    FormView.prototype.enableFunctionalities.call(this);

                    //ENABLE THE AUTOCOMPLETE AND MULTIFIELD FUNCTIONALITIES
                    var frm = this.frm;

                    var that = this;
                    //1. ENABLE AUTOCOMPLETE
                    /* 1.a : Bind Country with AutoComplete and CountryMap */
                    frm.find("div.composite.select[name\*=\".Country\"]").each(function (idx, el) {
                        var cntAutocomplete = new TypeaheadView({
                            el: $(el),
                            minLength: 0,
                            topN: 10,
                            name: "holder-details",
                            map: CountryMap
                        });
                        that.addToViewsIndex(cntAutocomplete);
                    });
                    /* 1.c : Bind InstantMessaging Types with AutoComplete and InstantMessagingTypesMap */
                    frm.find("div.composite.select[name\*=\".InstantMessaging\"]").each(function (idx, el) {
                        var imAutocomplete = new TypeaheadView({
                            el: $(el),
                            minLength: 0,
                            topN: 10,
                            name: "im",
                            map: InstantMessagingTypeMap
                        });
                        that.addToViewsIndex(imAutocomplete);
                    });
                    /* 1.d : Bind Telephone Types with TelephoneTypesMap */
                    frm.find("div.composite.select[name\*=\"" + ".Telephone" + "\"]").each(function (idx, el) {
                        var telView = new SelectFieldView({
                            el: $(el),
                            map: TelephoneTypeMap
                        });
                        that.addToViewsIndex(telView);
                    });

                    //call parent FINALLY enable functionalities
                    FormView.prototype.finallyEnableFunctionalities.call(this);
                }

                /**
                 * @Override
                 */
                , getRelatedSections: function (section) {
                    return ["Demographics", "Photo", "Signature"];
                }

                , capitaliseFirstLetter: function (string) {
                    return string.charAt(0).toUpperCase() + string.slice(1);
                }

                , getPhoneSelection: function (event) {
                    var _selection = $(event.target);
                    var type = this.capitaliseFirstLetter(_selection.val());
                    var name = _selection.attr("name");
                    var parent_name = name.slice(0, name.lastIndexOf("."));
                    parent_name = "[name=\"" + parent_name + "\"]";
                    var parent = _selection.closest(parent_name);
                    if (parent !== undefined && parent !== null && !_.isEmpty(parent)) {
                        parent.removeClass("Work Mobile Home");
                        parent.addClass(type);
                    }
                }

                /*
                 * Check on type for valid email
                 */
                , isEmailValid: function (event) {
                    this.$el.find('.notification.error').fadeOut('slow', function () {
                        $(this).remove();
                    });
                }
            });

            return HolderDetailsFormView;
        }
);