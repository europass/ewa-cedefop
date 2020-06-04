/**
 * This view is bound to the fieldset including as descendant DOM elements
 * the from and to fields, as well as the currently holding position checkbox.
 * The View is configured to receive:
 * 1. the el element <fieldset class=" ends with Period">
 *  
 */
define(['jquery', 'backbone'],
        function ($, Backbone) {
            var CurrentPositionView = Backbone.View.extend({

                events: {
                    "change :checkbox[name$=\"Period.Current\"]": "updateCurrentPos"
                }
                , onClose: function () {
                    //perform any clean-up here
                }
                , initialize: function () {}

                , updateCurrentPos: function (event) {
                    var obj = $(event.target);

                    var overlay = this.$el.find("div.overlay");
                    var dtparts = this.$el.find(":input:not(button).formfield:not(.PrintingPreferences)[name*=\"Period.To\"]");

                    if (obj.is(':checked')) {
                        //Toggle span.period-present
                        $(dtparts).each(function (index, el) {
                            var element = $(el);
                            element.closest(".custom_select_wrapper").removeAttr("tabindex");
                            element.trigger("europass:formselect:changed");
                        });
                        overlay.show();

                    } else {
                        $(dtparts).each(function (index, el) {
                            $(el).closest(".custom_select_wrapper").attr("tabindex", "0");
                        });
                        overlay.hide();
                    }
                }
            });

            return CurrentPositionView;
        }
);