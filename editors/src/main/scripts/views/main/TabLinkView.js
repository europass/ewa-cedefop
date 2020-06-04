/**
 * Allow tab changing via links in the editor
 * This could be useful
 *   in the teasers used in the Save tab. For example the "Do you want to combine your ESP with a Curriculum Vitae?" teaser could become "Do you want to combine your ESP with a Curriculum Vitae?" directly pointing to the CV tab
 *   in notification messages in the bottom of the page about data not saved and prompt to download
 *   
 * Listens to all the "click" events of the "a" elements with "href" attribute that starts-with "ewa:/"   
 */
define(
        [
            'jquery',
            'backbone',
            'routers/SkillsPassportRouterInstance'
//		EWA-1811
//		,'i18n!localization/nls/GuiLabel'
        ],
        function ($, Backbone, AppRouter /* ,EWA-1811 GuiLabel*/) {

            var TabLinkView = Backbone.View.extend({
                el: "body"

                , events: {
                    "click a[href^=\"ewa\:\/\"]": "cmsLink"
                }
                , cmsLink: function (event) {
                    var link = event.target;
                    if (!this.empty(link)) {

                        if ($(link).is("#export-wizard-init-a")) {

                            $("body").find("#export-wizard-init-btn").click();

                            return false;
                        }

                        var href = link.href;
                        if (this.empty(href)) {
                            return  false;
                        } else {
                            //Remove leading slashes and hash bangs (backward compatablility)
                            var url = href.replace('ewa:', '').replace(/^\//, '').replace('\#\!\/', '');
                            if (url.indexOf("/") !== 0) {
                                url = "/" + url;
                            }
                            AppRouter.navigate(url, {
                                trigger: true,
                                replace: !("pushState" in window.history) //To update the URL without creating an entry in the browser's history, set the replace option to true. 
                            });
                            return false;
                        }
                    } else {
                        return false;
                    }
                }

                , empty: function (value) {
                    return (value === undefined || value === null || value === '');
                }

            });
            return TabLinkView;
        }
);