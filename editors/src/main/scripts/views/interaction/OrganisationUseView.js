define([
    'jquery',
//  EWA-1811
//	'underscore',
    'backbone'
],
        function ($,
//          EWA-1811
//			_,
                Backbone) {

            var OrganisationUseView = Backbone.View.extend({

                events: {
                    "change :input:not(button)[name$=\"Contact\"]": "updateLabel"
                }
                , onClose: function () {
                    //perform any necessary cleanup here
                }
                , initialize: function () {
                }
                , updateLabel: function (event) {
                    var website = $(event.target);
                    var parent = website.closest("fieldset");
                    var usecode = parent.find(":hidden[name$=\"Code\"]");

                    var value = website.val();
                    if (value !== undefined && value !== null && value !== "" && $.trim(value) !== "") {
                        usecode.val("business");
                    } else {
                        usecode.val("");
                    }
                }
            });

            return OrganisationUseView;
        }
);