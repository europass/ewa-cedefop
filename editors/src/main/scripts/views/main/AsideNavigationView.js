/**
 * Listens to the events from the navigation model
 * and updates the aside HTML elements accordingly,
 * e.g. by setting the active class name.
 */
define(
        [
            'jquery',
//		EWA-1811
//		'underscore',
            'backbone',
            'routers/SkillsPassportRouterInstance',
            'hbs!templates/main/aside/documents',
            'Utils',
//	'europass/TabletInteractionsView'
        ],
        function ($, /* EWA-1811 _,*/ Backbone, AppRouter, HtmlTemplate, Utils
//			,TabletInteractionsView
                ) {

            var AsideNavigationView = Backbone.View.extend({

                events: {
                    "click .document a:not([data-bypass])": "doNavigate"

                }
                , initialize: function (options) {
                    this.navigationModel = options.navigationModel;

                    this.template = HtmlTemplate;

                    this.navigationModel.bind("model:navigation:changed", $.proxy(this.navigate, this));

                    this.model.bind("model:linked:attachment:changed", this.reRender, this);
                    this.model.bind("model:binaries:reset", this.reRender, this);
                    this.model.bind("model:content:changed", this.reRender, this);
                    this.model.bind("model:content:reset", this.reRender, this);
                    this.model.bind("model:uploaded:esp", this.reRender, this);
                    this.model.bind("model:uploaded:social", this.reRender, this);
                    this.model.bind("model:uploaded:cloud", this.reRender, this);
                    this.model.bind("local:storage:model:populated", this.reRender, this);
                    this.model.bind("model:loaded:cloud:document", this.reRender, this);

                    this.render(null);

                }
                , onClose: function () {
                    this.navigationModel.unbind("model:navigation:changed", $.proxy(this.navigate, this));

                    this.model.unbind("model:linked:attachment:changed", this.reRender);

                    this.model.unbind("model:binaries:reset", this.reRender);
                    this.model.unbind("model:content:reset", this.reRender);
                    this.model.unbind("model:content:changed", this.reRender);
                    this.model.unbind("model:uploaded:esp", this.reRender);
                    this.model.unbind("model:uploaded:social", this.reRender);
                    this.model.unbind("model:uploaded:cloud", this.reRender);
                    this.model.unbind("local:storage:model:populated", this.reRender);
                    this.model.unbind("model:loaded:cloud:document", this.reRender);
                }
                , doNavigate: function (event) {
                    var link = $(event.target);

                    var href = link.attr("data-href");

                    if (href === undefined || href === null) {
                        return  false;
                    }

                    var passThrough = (href.indexOf('sign_out') >= 0);

                    if (!passThrough && !event.altKey && !event.ctrlKey && !event.metaKey && !event.shiftKey) {
                        event.preventDefault();
                    }

                    var url = href.replace(/^\//, '').replace('\#\!\/', '');

                    if (url.indexOf("/") !== 0) {
                        url = "/" + url;
                    }

                    if (AppRouter !== undefined) {
                        AppRouter.navigate(url, {
                            trigger: true,
                            replace: !("pushState" in window.history) //To update the URL without creating an entry in the browser's history, set the replace option to true. 
                        });
                    }

                    return false;
                }
                , navigate: function (view) {
                    //try{console.log("aside navigation changed to " + view );} catch (err){}

                    if (view === undefined || view === null || view === "") {
                        view = "compose";
                    }
                    //Find active section - if there is any
                    var aside = this.$el;
                    var active = aside.find("li.active a");
                    var activeName = (active !== undefined && active !== null) ? active.attr("name") : null;

                    var docType = this.navigationModel.analyze(view).document;
                    this.setExtraFieldsClass(docType);

                    //Proceed only if the chosen view is NOT already active
                    if (activeName !== undefined && activeName !== null && activeName !== view) {
                        //remove all active class names
                        aside.find("li").each(function (idx, li) {
                            $(li).removeClass("active");
                            $(li).parents("li").removeClass("child-active");
                        });
                    }
                    //Finally set the class name

                    if (view.indexOf("download") !== -1) {
                        var li = aside.find("a[name=\"download\"]").closest("li");
                        li.addClass("active");
                    } else {
                        var li = aside.find("a[name=\"" + view + "\"]").closest("li");
                        li.addClass("active");
                        li.parents("li").addClass("child-active");
                    }

                }
                , render: function ( ) {
                    var modelInfo = this.model.info();
                    var context = Utils.checkModelInfoTypesNonEmpty(modelInfo);

                    var html = this.template(context);

                    this.$el.html(html);

                    var isTablet = (/(iPhone|iPod|iPad)/.test(navigator.userAgent) || (/Android/.test(navigator.userAgent)) || (/BlackBerry/.test(navigator.userAgent)));
                    if (isTablet) {

                        /**
                         * pgia: EWA-1815
                         * Load TabletInteractionsView via require on the tablets variable ONLY if isTablet == true
                         */

                        var _that = this;
                        require(
                                ['europass/TabletInteractionsView'],
                                function (TabletInteractionsView) {
                                    /* Register a listener to .waiting.indicator for tablet hover effect emulation, calling touchListener with $el and target */
                                    (modelInfo !== null ? TabletInteractionsView.touchListener(_that.$el, ".non-empty-indicator.tip.spot") : "");
                                }
                        );


                    }

                }
                , reRender: function () {
                    //console.log("AsideNavigationView on re-render...");
                    this.render();
                    //Attention! On re-rendering, we need to re-apply the active route.
                    this.navigate(this.navigationModel.findActiveView());
                }

                /* This is to add extra class whenever we have extra info on right aside bar every time we navigate (e.g ECV, ELP)
                 e.g Alignment for CL, Extra fields for ECv etc.
                 */
                , setExtraFieldsClass: function (docType) {
                    var asideRight = $("#aside-right-side-page");
                    $(asideRight).removeClass('extra-fields-for-tablet');

                    if (docType !== 'ESP' && docType !== 'ELP') {
                        $(asideRight).addClass('extra-fields-for-tablet');
                    }
                    ;
                }

            });

            return AsideNavigationView;
        }
);