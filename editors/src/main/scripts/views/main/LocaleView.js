/**
 * This view is bound to the parent division wrapping the three radio buttons controlling the Gender information.
 * The View is configured to receive:
 * 1. the el element (div.choice.#Control:LearnerInfo.Identification.Demographics.Gender)
 * 2. the map containing the translation of the gender codes
 * 
 * This is initiated during PersonalInfoFormView.enableFunctionalities();
 *  
 */
define(
        [
            'module',
            'jquery',
            'jqueryui',
//		EWA-1811
//		'underscore',
            'backbone',
            'europass/maps/LocaleMap',
            'hbs!templates/main/locale',
            'hbs!templates/main/currentLocale',
//		EWA-1811?
            'europass/http/SessionManagerInstance'
        ],
        function (module, $, jqueryui,
//		EWA-1811
//			_,
                Backbone, LocaleMap, HtmlTemplate, LocaleTmpl, SessionManager) {

            var LocaleView = Backbone.View.extend({

                htmlTemplate: HtmlTemplate

                , sectionEl: $("#top-ui-languages")

                , scrollableSelector: "#LanguagesGrid .available-languages .ui-languages-area"
                , optionsSelector: "#LanguagesGrid .available-languages"
                , overlaySelector: "#LanguagesGrid"

                , events: {
                    "click #LanguagesGrid li.option a.locale": "toggleLocale",
                    "click #top-ui-languages a.current-language": "showOptions",
                    "click #LanguagesGrid button.close": "hideOptions",
                    "click #LanguagesGrid button.cancel": "hideOptions"
                }
                , initialize: function () {
                    this.render();
                }
                , render: function () {
                    var currentLocale = module.config().locale;

                    var matchedLocale = LocaleMap.get(currentLocale);
                    if (matchedLocale == null) {
                        //set default
                        matchedLocale = LocaleMap.get("en");
                    }
                    matchedLocale["Active"] = true;
                    this.setOthersFalse(LocaleMap, matchedLocale);

                    var context = this.prepareContext(matchedLocale);

                    var localeHtml = LocaleTmpl(context);
                    this.$el.find("#top-ui-languages").html(localeHtml);

                    var html = HtmlTemplate(context);
                    this.$el.append(html);

                    this.optionsArea = this.$el.find(this.optionsSelector);
                    this.scrollableArea = this.$el.find(this.scrollableSelector);
                    this.overlay = this.$el.find(this.overlaySelector);

                }
                /**
                 * Prepare the context for executing the template
                 */
                , prepareContext: function (matchedLocale) {
                    var locales = LocaleMap.objs;
                    var length = locales.length;

                    var splitLimit = Math.ceil(length / 2);

                    var reordered = [];
                    for (var i = 0; i < splitLimit; i++) {
                        var left = locales[ i ];
                        left.position = "left";
                        reordered.push(left);

                        var right = locales[ i + splitLimit ];
                        if (right === undefined) {
                            right = {
                                empty: true
                            };
                        }
                        right.position = "right";
                        reordered.push(right);
                    }
                    var context = {
                        current: matchedLocale,
                        locales: reordered
                    };
                    return context;
                }
                /**
                 * When the an <a> is clicked indicating a change of locale
                 */
                , toggleLocale: function (event) {
                    var el = $(event.currentTarget);
                    var locale = el.attr("data-locale-code");
                    this.updateConfigLocale(locale);
                }
                /**
                 * Show the list of available languages
                 * Include a drawer effect, sliding from left to right
                 */
                , showOptions: function (event) {
//				console.log("show");
                    var _that = this;
                    var _overlay = this.overlay;
                    var _area = this.optionsArea;
                    var children = (_area !== undefined ? _area.children() : undefined);
                    _overlay.toggleClass("visible", function () {
                        $(this).animate({"background-color": "rgba(0,0,0, 0.7)"}, 400);
                        _area.find(".drawer").css("overflow-y", "auto");
                        if (children !== undefined) {
                            children.addBack().show('slide', {direction: "left", easing: "easeInSine"}, 400, function () {
                                _that.sectionEl.trigger("europass:drawer:opened");
                            });
                        }

                    });
                }
                /**
                 * Hide the list of available languages
                 * Include a drawer effect, sliding from right to left
                 */
                , hideOptions: function (event) {
//				console.log("hide");
//				var _that = this;
                    var _overlay = this.overlay;
                    var _area = this.optionsArea;
                    var children = (_area !== undefined ? _area.children() : undefined);
                    if (children !== undefined) {
                        children.hide('slide', {direction: "left", easing: "easeInSine"}, 400);
                    }
//				console.log("set visible");
                    _area.hide('slide', {direction: "left", easing: "easeInSine"}, 400);
                    _area.find(".drawer").css("overflow-y", "hidden");
                    _overlay.animate({"background-color": "rgba(0,0,0,0)"}, 400, function () {
                        $(this).toggleClass("visible");
                    });
                }
                /**
                 * Update the rest of available locales to not indicate that they are active
                 */
                , setOthersFalse: function (LocaleMap, matchedLocale) {
                    $.each(LocaleMap.objs, function (idx, locale) {
                        if (locale.Code !== matchedLocale.Code) {
                            locale["Active"] = false;
                        }
                    });
                }

            });

            return LocaleView;
        }
);