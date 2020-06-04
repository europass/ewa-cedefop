/**
 * Listens to the events from the navigation model
 * and updates the aside HTML elements accordingly,
 * e.g. by setting the active class name.
 */
define(
        [
            'jquery',
            'underscore',
            'backbone'//'europass/TabletInteractionsView'
        ],
        function ($, _, Backbone) {//,TabletInteractionsView

            var ClickableAreaView = Backbone.View.extend({

                tablets: {},

                areaSelector: ".clickable-area",

                inputSelector: ".clickable-area-input",

                events: {
                    "click .clickable-area": "toggleInput"
                            //"click input[id^='Driving']" : "toggleInput"
                }
                , initialize: function (options) {

                    this.isTablet = (/(iPhone|iPod|iPad)/.test(navigator.userAgent) || (/Android/.test(navigator.userAgent)) || (/BlackBerry/.test(navigator.userAgent)));
                    if (this.isTablet) {

                        /**
                         * pgia: EWA-1815
                         * Load TabletInteractionsView via require on the tablets variable ONLY if isTablet == true
                         */
                        var _that = this;
                        require(['europass/TabletInteractionsView'], function (TabletInteractionsView) {
                            _that.tablets = TabletInteractionsView;
                        }
                        );
                    }
                }

                , toggleInput: function (event) {

                    var el = $(event.target);
                    //Proceed only if the clicked item is not the checkbox/radio itself
                    var sameTarget = el.is(this.inputSelector);

                    //Do NOT continue if the clicked area contains an already clicked radio
                    var area = $(event.currentTarget);
                    var input = area.find(this.inputSelector);
                    var alreadyCheckedRadio = input.is(":radio") && input.is(":checked");

                    if (sameTarget || (el.is('.onoffswitch') && !el.is('.include-europass-logo-switch') && !el.is('.store-data-locally-switch'))) {
                        if ((this.isTablet && _.isFunction(this.tablets.handleTipSpot)) && this.tablets.handleTipSpot(event, "target") === false) {
                            return false;
                        }
                    }
                    if (!sameTarget && !alreadyCheckedRadio) {
                        var area = el;
                        if (!el.is(this.areaSelector)) {
                            area = el.closest(this.areaSelector);
                        }
                        var input = area.children(this.inputSelector);
                        var alreadyChecked = input.is(":checked");
                        input.prop("checked", !alreadyChecked);
                        input.change();
                    }
                }
            });

            return ClickableAreaView;
        }
);