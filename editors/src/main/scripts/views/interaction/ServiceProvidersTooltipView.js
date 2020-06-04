/*
 * Sets the appropriate cookie accordingly in order to show
 * the tooltip with the providers (XING/Monster/Eures) info
 */
define(
        [
            'jquery',
            'backbone',
            'underscore',
            'Utils',
            'hbs!templates/messaging/providersfeaturetooltip'
        ],
        function ($, Backbone, _, Utils, htmlTemplate) {
            var ProvidersTooltipView = Backbone.View.extend({
                EXPIRATION_DURATION: 7, // 1 week expiration
                TOOLTIP_COOKIE_NAME: "europass-editors-providers-cookie-tooltip",
                events: {
                    "click :button#publish-providers-tooltip-close": "storeCookie",
                    "click .providers-tooltip-apply-btn": "storeCookieAndPublish"
                }
                , initialize: function (options) {
                    var cookie = Utils.readCookieByName('europass-editors-providers-cookie-tooltip');
                    var cookieExists = (cookie !== undefined && cookie !== false && cookie !== null);
                    var providersTooltipCookieExists = (Utils.readCookieByName("europass-editors-providers-cookie-tooltip"));

                    if (!cookieExists && !providersTooltipCookieExists) {
                        this.template = htmlTemplate();
                        this.render();
                    }

                }
                , render: function () {
                    this.$el.show();
                    this.$el.html(this.template);
                }

                , storeCookie: function () {
                    Utils.createOrSetCookieByName("europass-editors-providers-cookie-tooltip", "europass-editors-providers-cookie-closed", this.EXPIRATION_DURATION);
                    this.$el.slideUp("slow", function () {
                        $(this).remove();
                    });
                }

                , storeCookieAndPublish: function () {
                    this.storeCookie();
                    $("#share-document-btn").click();
                }

            });

            return ProvidersTooltipView;

        }
);

