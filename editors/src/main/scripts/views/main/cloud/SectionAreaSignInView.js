define(
        [
            'jquery',
            'underscore',
            'backbone',
            'hbs!templates/main/cloudSignIn',
            'europass/http/WindowConfigInstance'
        ],
        function ($, _, Backbone, HtmlTemplate, WindowConfig) {

            var SectionAreaSignInView = Backbone.View.extend({

                htmlTemplate: HtmlTemplate

                , initialize: function () {
                    this.render();
                }
                , render: function () {
                    var context = {};
                    if (WindowConfig.showCloudLogin !== true) {
                        $('#top-ui-languages').addClass('cloud-login-not-available');
                    } else {
                        var html = HtmlTemplate(context);
                        this.$el.find("#top-ui-cloud-sign-in-section").html(html);
                    }
                }
            });

            return SectionAreaSignInView;
        }
);