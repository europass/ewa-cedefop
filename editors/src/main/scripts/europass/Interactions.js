define('Interactions',
        ['jquery'],
        function ($) {
            var Interactions = {};

            /**
             * Activate the outline view - no overlay. 
             */
            Interactions.hideOverlay = function (el, clazz) {

                if (el.parent().hasClass("inactive-overlay")) {
                    el.next(".overlay-shade").detach();
                    el.unwrap("<div class=\"inactive-overlay " + clazz + "\">");
                }

            };
            /**
             * De-activate view - show overlay, and collapse it too
             */
            Interactions.showOverlay = function (el, clazz) {

                if (!el.parent().hasClass("inactive-overlay")) {
                    el.wrap("<div class=\"inactive-overlay " + clazz + "\">");
                    el.parent(".inactive-overlay." + clazz).append("<div class=\"overlay-shade\">");
                }

            };
            return Interactions;
        }
);
