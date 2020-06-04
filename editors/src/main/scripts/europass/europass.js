define([
    'require'
            , 'module'
            , 'jquery'
            , 'jqueryui'
            , 'underscore'
            , 'backbone'
            , 'views/MainPageView'
            , 'models/SkillsPassportInstance'
            , 'views/BackboneLocalStorageView'
            , 'views/BackboneErrorReportingView'
            , 'europass/http/SessionManagerInstance'
            , 'europass/http/WindowConfigInstance'
            , 'models/NavigationRoutesInstance'
            , 'routers/SkillsPassportRouterInstance'

],
        function (require, module, $, jqueryuiEffects, _, Backbone,
                MainPageView, SkillsPassportInstance, BackboneLocalStorageView, BackboneErrorReportingView,
                SessionManagerInstance, WindowConfig,
                NavigationRoutesInstance, SkillsPassportRouterInstance) {

            var initialize = function () {
                //This renders the body of the page
                var mainPage = new MainPageView({
                    el: 'body',
                    model: SkillsPassportInstance,
                    navigationModel: NavigationRoutesInstance
                });

                var isPushStateSupported = "pushState" in window.history;
                var locale = module.config().locale;
                var historyRoot = WindowConfig.getDefaultEwaEditorContext() + "/" + ((locale === undefined || locale === null || locale === "") ? "en/" : (locale + "/"));
                Backbone.history.start(
                        {
                            pushState: isPushStateSupported, //boolean that indicates if push-state is supported by the browser
                            root: historyRoot
                        });

                return mainPage;

            };

            return {
                initialize: initialize
            };
        }
);
