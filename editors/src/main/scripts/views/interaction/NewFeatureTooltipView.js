/* 
 * Sets the appropriate cookie accordingly in order to show
 * the tooltip with the new feature info conditionally 
 */
define(
        [
            'jquery',
            'backbone',
            'underscore',
            'Utils',
            'hbs!templates/messaging/newfeaturetooltip'
        ],
        function ($, Backbone, _, Utils, htmlTemplate) {
            var NewFeatureTooltipView = Backbone.View.extend({
                EXPIRATION_DURATION: 1,
                events: {
                    "click :button.cookie-tooltip.close": "storeCookie",
                    "click	#cloud-login-feature-tooltip .tooltip-sign-in": "storeCookie",
                    "europass:cloud:tooltip:store": "storeCookie"
                }
                , initialize: function (options) {
                    var cookie = Utils.readCookieByName("europass-editors-cloud-login-cookie-tooltip");
                    var cookieExists = (cookie !== undefined && cookie !== false && cookie !== null);
                    var cloudSignedInCookieExists = (Utils.readCookieByName("cloud-signed-in") && Utils.readCookieByName("cloud-signed-in") !== "");
                    var cloudTooltipCookieExists = (Utils.readCookieByName("europass-editors-cloudlogin-cookie-tooltip"));
                    if (!cookieExists && !cloudSignedInCookieExists && !cloudTooltipCookieExists) {
                        this.template = htmlTemplate();
                        this.render();
                    }

                }
                , render: function () {
                    this.$el.show();
                    this.$el.html(this.template);
                }

                , storeCookie: function () {
                    Utils.createOrSetCookieByName("europass-editors-cloudlogin-cookie-tooltip", "europass-editors-cloudlogin-tooltip-closed", this.EXPIRATION_DURATION);
                    this.$el.slideUp("slow", function () {
                        $(this).remove();
                    });
                }
            });

            return NewFeatureTooltipView;

        }
);

