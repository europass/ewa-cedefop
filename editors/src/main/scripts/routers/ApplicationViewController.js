/**
 * Utility structure that controls which View to display,
 * taking into consideration to properly close existing views,
 * thus unbinding all events and avoid memory leaks and multiple event initializations.
 */
define(
        [
            'require',
            'jquery',
            'jqueryui',
            'underscore'
        ],
        function (require, $, jqueryui, _) {
            var ApplicationViewController = function () {
                this.currentView = null;
            };
            /**
             * If a call to this function is the last one to go, then ok.
             * If other things need to happen after the class is required, a callback function needs to be passed.
             * 
             * This is because dynamically calling require starts and asynchronous batch of execution.
             * @param clazz
             * @param bundle
             * @param config
             */
            ApplicationViewController.prototype.showView = function (clazz, bundle, config) {
                var bundlePath = "assembly/" + bundle;
//			console.log("about to require: " + bundlePath );			
                var that = this;
                require(
                        [bundlePath],
                        function () {
                            require([clazz], function (ViewModule) {

                                var mainArea = $("#MainArea\\:SkillsPassport");

                                mainArea.fadeOut("fast", function () { // When available, fade out the container
                                    //Destroy - Close the previous view!!!
                                    if (_.isObject(that.currentView) && _.isFunction(that.currentView.close)) {
                                        that.currentView.close();
                                    }
                                    //Now we can instantiate the requested view...
                                    //initialize and fetch template
                                    config.args = [config.navigation];
                                    config.scope = this;
                                    config.callback = function (navigation, viewInstance) {
                                        that.currentView = viewInstance;

                                        var currentEl = viewInstance.$el;
                                        $(this).html(currentEl).fadeIn("fast");

                                        viewInstance.render(navigation);

                                        currentEl.trigger("europass:view:rendered", [navigation]);
                                        currentEl.trigger("europass:drawer:opened");
                                    };
                                    new ViewModule(config);
                                });
                            });
                        }
                );
            };
            return ApplicationViewController;
        }
);