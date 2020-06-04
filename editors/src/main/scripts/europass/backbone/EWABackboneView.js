define(
        ['module', 'backbone', 'text', 'underscore', 'Utils'],
        function (module, Backbone, text, _, Utils) {
            Backbone.EWAView = Backbone.View.extend({

                initialize: function (options) {

                    //set template if not already set by a child instance
                    if (_.isUndefined(this.template) || !_.isFunction(this.template)) {
//					console.log("EWA View: evaluate htmlTemplate: " + this.htmlTemplate );
                        //Case String...
                        if (_.isString(this.htmlTemplate)) {
                            var locale = module.config().locale || "en";
//						console.log("A. require template for "+locale);
//						var testRequireName = "text!templates/" + locale + "/" + this.htmlTemplate + ".hbs";
                            var requireName = "hbs!templates/" + locale + "/" + this.htmlTemplate;
                            var fallbackRequireName = "hbs!templates/en/" + this.htmlTemplate;

                            Utils.requireResource(
                                    {
                                        _requireName: requireName,
                                        _fallbackRequireName: fallbackRequireName
//							,_testRequireName: testRequireName
                                    },
                                    this.initialized,
                                    this,
                                    [options]);
                        }
                        //Case Function
                        else if (_.isFunction(this.htmlTemplate)) {
//						console.log("B. hbs template");
                            this.initialized(options, this.htmlTemplate);
                        }
                        //Case Nothing
                        else {
//						console.log("C. no template");
                            this.initialized("<div>Failed to load template</div>");
                        }
                    } else {
//					console.log("D. this.template");
                        this.initialized(options, this.template);
                    }

                }
                , initialized: function (options, htmlTemplate) {
//				console.log("initialized callback");
                    this.template = htmlTemplate;
//				this.initialized = true;
                    this.onInit(options);
                }
                /**
                 * If options have a callback, it will be called.
                 */
                , onInit: function (options) {
//				console.log("EWABackboneView:onInit for '" + this.section +"'" );
                    if (!_.isUndefined(options.callback) && _.isFunction(options.callback)) {
//					console.log("EWABackboneView:onInit apply callback" );
                        var args = options.args || [];
                        args.push(this);
                        options.callback.apply(options.scope, args);
                    }
                }
            });

            return {};

        }
);