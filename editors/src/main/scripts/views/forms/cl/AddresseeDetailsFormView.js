define(
        [
            'jquery',
            'views/forms/FormView',
            'views/interaction/TypeaheadView',
            'europass/maps/TitleMap',
            'europass/maps/CountryMap'
        ],
        function ($, FormView, TypeaheadView, TitleMap, CountryMap) {

            var AddresseeDetailsFormView = FormView.extend({
                htmlTemplate: "forms/cl/addresseeDetails"

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
                            name: "country",
                            map: CountryMap
                        });
                        that.addToViewsIndex(cntAutocomplete);
                    });

                    //1. ENABLE AUTOCOMPLETE
                    /* 1.a : Bind title with AutoComplete and TitleMap */
                    frm.find("div.composite.select[name\*=\".Title\"]").each(function (idx, el) {
                        var cntAutocomplete = new TypeaheadView({
                            el: $(el),
                            minLength: 0,
                            topN: 10,
                            name: "title",
                            map: TitleMap
                        });
                        that.addToViewsIndex(cntAutocomplete);
                    });

                    //call parent FINALLY enable functionalities
                    FormView.prototype.finallyEnableFunctionalities.call(this);
                }
            });
            return AddresseeDetailsFormView;
        }
);