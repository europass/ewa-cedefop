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
            'jquery',
            'jqueryui',
            'underscore',
            'backbone',
            'hbs!templates/main/appsettings',
//          'views/interaction/ClickableAreaView',
            'views/main/ModelLocalStoreView',
            'europass/GlobalLocalStoreOptionInstance',
            'analytics/EventsController',
        ],
        function ($, jqueryui, _, Backbone, HtmlTemplate,
//		ClickableAreaView, 
                ModelLocalStoreView, GlobalLocalStoreOption, Events) {

            var ApplicationSettingsView = Backbone.View.extend({

                htmlTemplate: HtmlTemplate

                , sectionEl: $("#application-settings")

                , scrollableSelector: "#AppSettingsForm .appsettings-info .ui-settings-area"
                , optionsSelector: "#AppSettingsForm .appsettings-info"
                , overlaySelector: "#AppSettingsForm"
                , storageSelector: "#store-data-locally-control"
                , logoSelector: "#include-europass-logo-control"
                , event: new Events
                , events: {
                    "click #application-settings a.change-settings": "showSettingsForm",
                    "click #AppSettingsForm button.close": "hideSettingsForm",
//				"click #AppSettingsForm button.cancel"        : "hideSettingsForm",
                    "click #AppSettingsForm button.green": "hideSettingsForm",
//				"change :input.include-europass-logo-control" : "toggleLogoAppearance",
                    "click :input.include-europass-logo-control": "toggleLogoAppearance",
                    "change :input.store-data-locally-control": "changeStorageSelection",
                    "click :input.store-data-locally-control": "changeStorageSelection"
                }
                , initialize: function () {
                    this.render();
                    this.arrangeEuropassLogo();
                    this.arrangeLocalStorage();
                }
                , onClose: function () {
//				this.clickableAreaView.close();
                    this.modelLocalStoreView.close();
                }
                , render: function () {
                    var that = this;
                    var context = {};
//				that.clickableAreaView = new ClickableAreaView({
//					el : that.inputSelector
//				});
                    that.modelLocalStoreView = new ModelLocalStoreView({
                        //el: $(that.storageSelector),
                        model: that.model
                    });

                    //Html of the Settings Modal - html is appended and it is hidden
                    var html = HtmlTemplate(context);
                    this.$el.append(html);

                    this.optionsArea = this.$el.find(this.optionsSelector);
                    this.scrollableArea = this.$el.find(this.scrollableSelector);
                    this.overlay = this.$el.find(this.overlaySelector);
                    this.storageSelector = this.$el.find(this.storageSelector);
                    this.logoSelector = this.$el.find(this.logoSelector);
                }

                /**
                 * Show the modal where the local storage and the europass logo inclusion can be switched
                 * Include a drawer effect, sliding from left to right
                 */
                , showSettingsForm: function (event) {
//				console.log("show");
//               this.arrangeLocalStorage();
                    this.arrangeEuropassLogo();
                    this.arrangeLocalStorage();

                    var _that = this;
                    var _overlay = this.overlay;
                    var _area = this.optionsArea;
                    var children = (_area !== undefined ? _area.children() : undefined);
                    _overlay.toggleClass("visible", function () {
                        $(this).animate({"background-color": "rgba(0,0,0, 0.7)"}, 400);
                        if (children !== undefined) {
                            children.addBack().show('slide', {direction: "left", easing: "easeInSine"}, 400, function () {
                                //Making the vertical overflow auto for overflow-y scrolling if needed, while the modal is open
                                _area.find(".ui-settings-area").css("overflow-y", "auto");
                                _that.sectionEl.trigger("europass:drawer:opened");
                            });
                        }
                    });
                }
                /**
                 * Toggle the visibility of the Europass Logo
                 * on the produced document
                 */
                , toggleLogoAppearance: function (event) {
                    var elem = $(event.target);
                    if (!elem.is(":checked")) {
                        this.model.setEuropassLogo(false);
                    } else {
                        this.model.setEuropassLogo(true);
                    }
                }
                /**
                 * Hide the settings modal
                 * Include a drawer effect, sliding from right to left
                 */
                , hideSettingsForm: function (event) {
//				console.log("hide");
                    //if($("#include-europass-logo-control").attr("checked") != "checked" || $("#include-europass-logo-control").attr("checked") != true){
                    //	this.event.applicationOptionStorage();
                    //}
                    if (!($("#include-europass-logo-control").prop("checked"))) {
                        this.event.applicationOptionLogo();
                    }
                    if (!($("#store-data-locally-control").prop("checked"))) {
                        this.event.applicationOptionStorage();
                    }
                    var _overlay = this.overlay;
                    var _area = this.optionsArea;
                    var children = (_area !== undefined ? _area.children() : undefined);
                    if (children !== undefined) {
                        children.hide('slide', {direction: "left", easing: "easeInSine"}, 400);
                    }
//				console.log("set visible");
                    _area.hide('slide', {direction: "left", easing: "easeInSine"}, 400);
                    _area.find(".ui-settings-area").css("overflow-y", "hidden");
                    _overlay.animate({"background-color": "rgba(0,0,0,0)"}, 400, function () {
                        $(this).toggleClass("visible");
                    });
                },

                changeStorageSelection: function (event) {
//				var that = this;
                    var input = $(event.currentTarget);
                    this.modelLocalStoreView.toggleStoring(input);
                },
                arrangeLocalStorage: function () {
                    var isStorable = GlobalLocalStoreOption.isStorable();
                    var storageBox = (this.$el !== undefined ? this.$el.find("#store-data-locally-control") : undefined);
                    if (storageBox !== undefined) {
                        $(storageBox).prop("checked", isStorable);
                    }
                },
                arrangeEuropassLogo: function () {
                    var showEuropassLogo = this.model.getEuropassLogo();
                    var europassLogo = (this.$el !== undefined ? this.$el.find("#include-europass-logo-control") : undefined);
                    if (europassLogo !== undefined)
                        $(europassLogo).prop("checked", showEuropassLogo);
                }
            });

            return ApplicationSettingsView;
        }
);