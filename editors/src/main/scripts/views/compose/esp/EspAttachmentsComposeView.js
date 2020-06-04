define(
        [
            'jquery',
            'views/compose/cv/AnnexesComposeView',
            'hbs!templates/compose/esp/attachments'
        ],
        function ($, AnnexesComposeView, HtmlTemplate) {

            var EspAttachmentsComposeView = function (options) {
                AnnexesComposeView.apply(this, [options]);
            };

            EspAttachmentsComposeView.prototype = {
                htmlTemplate: HtmlTemplate,
                sortableAxis: "both",
                sortableConfig: {
                    placeholder: "esp-item-re-ordering"
                }
            };

            EspAttachmentsComposeView.prototype = $.extend(
                    //true, 
                            {},
                            AnnexesComposeView.prototype,
                            EspAttachmentsComposeView.prototype
                            );
                    return EspAttachmentsComposeView;
                }
        );