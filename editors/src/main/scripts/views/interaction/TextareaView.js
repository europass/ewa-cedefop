define(
        [
            'jquery',
            'backbone',
            'UtilsForRTE'
        ],
        function ($, Backbone, UtilsForRTE) {

            var TextareaView = Backbone.View.extend({
                //el is the Textarea

                initialize: function (options) {
                    this.textify();
                    this.$el.removeClass("rich-editor");
                    this.$el.closest("fieldset").addClass("no-rte");
                },
                textify: function () {
                    //Step 1: Preserve line-breaks and tabs and convert them into \n and \t
                    UtilsForRTE.textifyTextarea(this.$el);
                    //Step 2: Strip HTML
                    this.stripHTML();
                },
                htmlify: function () {
                    //Step 1: XML escape text
                    this.escapeXML();
                    //Step 2: 
                    UtilsForRTE.htmlifyTextarea(this.$el);
                },
                stripHTML: function () {
                    this.$el.val(UtilsForRTE.stripHtml(this.$el.val()));
                },
                escapeXML: function () {
                    this.$el.val(UtilsForRTE.escapeXml(this.$el.val()));
                },
                validateView: function () {
                    this.htmlify();
                    return true;
                }
            });

            return TextareaView;
        }
);