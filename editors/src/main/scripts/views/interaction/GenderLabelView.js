/**
 * This view is bound to the parent division wrapping the three radio buttons controlling the Gender information.
 * The View is configured to receive:
 * 1. the el element (div.choice.#Control:LearnerInfo.Identification.Demographics.Gender)
 * 2. the map containing the translation of the gender codes
 * 
 * This is initiated during PersonalInfoFormView.enableFunctionalities();
 *  
 */
define(
        [
            'jquery',
//		'underscore',
            'backbone',
            'europass/maps/GenderMap'
        ],
        function ($, /*_,*/ Backbone, GenderMap) {

            var GenderLabelView = Backbone.View.extend({

                events: {
                    // Register an event fired when the Gender radio button ( Female/Male) is selected to store the Gender's Label.
                    "change :radio:not(.PrintingPreferences)[name=\"LearnerInfo.Identification.Demographics.Gender.Code\"]": "updateGenderLabel",
                    // Register an event fired when the Gender radio button ( Do not indicate ) is selected to remove the Gender's Label.
                    "change :radio.PrintingPreferences[name=\"LearnerInfo.Identification.Demographics.Gender.Code\"]": "removeGenderLabel"
                },
                onClose: function () {
                    //perform any clean-up here
                },
                initialize: function () {},

                updateGenderLabel: function (event) {
                    var radio = $(event.target);
                    //from radio find the hidden input field with the label and set its value according to the map.
                    var label = $(radio.siblings("input[name$=\".Label\"]"));
                    var key = radio.val();
                    var value = GenderMap.get(key);
                    if (value !== "") {
                        label.val(value);
                    }
                    //Trigger event
                    radio.trigger("europass:gender:changed", key);
                },
                removeGenderLabel: function (event) {
                    var radio = $(event.target);
                    var key = radio.val();
                    var label = $(radio.siblings("input[name$=\".Label\"]"));
                    label.val("");

                    //Trigger event
                    radio.trigger("europass:gender:changed", key);
                }
            });

            return GenderLabelView;
        }
);