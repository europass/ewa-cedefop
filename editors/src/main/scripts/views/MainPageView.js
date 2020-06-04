define(
        [
            'module',
            'jquery',
            'jqueryui',
            'underscore',
            'backbone',
            'routers/SkillsPassportRouterInstance',
            'models/NavigationRoutesInstance',
            'Utils',
            'routers/ApplicationViewControllerInstance',
            'hbs!templates/main/page',

            'views/GlobalController',

            'views/messaging/WaitingIndicatorView',
            'views/messaging/CloudWaitingIndicatorView',
            'views/messaging/FeedbackView',
            'views/messaging/CloseSessionMessagingView',
            'views/messaging/SessionKeepAliveView',

            'views/attachment/AttachmentManagerInstance',

            'views/main/CurrentDocumentView',
            'views/main/AsideNavigationView',
            'views/main/DocumentControlsView',
            'views/interaction/ClickableAreaView',
            'views/interaction/SortView',
            //	'views/interaction/AutoSortView',

            'views/upload/ImportWizardController',
            'views/download/ExportWizardController',

            'views/help/TooltipView',
            'views/main/LocaleView',
            'views/main/cloud/SectionAreaSignInView',
            'views/main/cloud/SignInView',
            'views/main/cloud/GoogleDriveView',
            'views/main/cloud/OneDriveView',
            'views/main/cloud/ManageDocumentsView',
            'views/main/ApplicationSettingsView',
            'views/main/BasicCvPrinciplesView',
            'views/main/PrepareYourInterviewView',
            'views/main/JobPortalsView',
            'views/main/ModelLocalStoreView',
            'views/main/TabLinkView',
            'views/interaction/NewFeatureTooltipView',
            'views/interaction/ServiceProvidersTooltipView',
            'views/main/SendFeedbackView',

            'views/main/share/GoogleShareView',
            'views/main/share/DropboxShareView',
            'views/main/share/OneDriveShareView',

            'views/main/ShareReviewView',

            'views/main/share/ShareView',
            'views/main/share/ManageSharesView',

            'views/download/PartnersMainView',

            'europass/http/WindowConfigInstance',
            'views/WizardStep',
            'analytics/EventsController'
        ],
        function (module, $, jqueryui, _, Backbone, AppRouter, NavigationRoutes, Utils,
                ApplicationViewControllerInstance,
                MainTemplate,
                GlobalController,
                WaitingIndicatorView,
                CloudWaitingIndicatorView,
                FeedbackView,
                CloseSessionMessagingView,
                SessionKeepAliveView,
                AttachmentManager,
                CurrentDocumentView,
                AsideNavigationView,
                DocumentControlsView,
                ClickableAreaView,
                SortView,
//			 AutoSortView,

                ImportWizardController,
                ExportWizardController,
                TooltipView,
                LocaleView,
                CloudSectionAreaSignInView,
                CloudSignInView,
                GoogleDriveView,
                CloudOneDriveView,
                ManageDocumentsView,
                ApplicationSettingsView,
                BasicCvPrinciplesView,
                PrepareYourInterviewView,
                JobPortalsView,
                ModelLocalStoreView,
                TabLinkView,
                NewFeatureTooltipView,
                ServiceProvidersTooltipView,
                SendFeedbackView,
                GoogleShareView,
                DropboxShareView,
                OneDriveShareView,
                ShareReviewView,
                ShareView,
                ManageSharesView,
                PartnersMainView,
                WindowConfig,
                WizardStep,
                Events
                ) {

            var MainPageView = Backbone.View.extend({
                event: new Events
                , events: {
                    "click #document-storing-warning button": "gaEventSend",
                    "click #close-modal": "closeNewEuropassModal"
                }
                , areas: {
                    // ---- COMPOSE VIEWS
                    "compose.ecv": {
                        "clazz": "views/compose/cv/SkillsPassportComposeView",
                        "bundle": "ecvCompose"
                        , "config": {
                            "section": "SkillsPassport.LearnerInfo",
                            "initialRendering": false,
                            "navigation": "compose.ecv"
                        }
                    }
                    , "compose.esp": {
                        "clazz": "views/compose/esp/EspComposeView",
                        "bundle": "espCompose"
                        , "config": {
                            "section": "SkillsPassport.LearnerInfo",
                            "navigation": "compose.esp"
                        }
                    }
                    , "compose.elp": {
                        "clazz": "views/compose/elp/ComposeView",
                        "bundle": "elpCompose"
                        , "config": {
                            "section": "SkillsPassport.LearnerInfo",
                            "navigation": "compose.elp"
                        }
                    }
                    , "compose.ecl": {
                        "clazz": "views/compose/cl/ComposeView",
                        "bundle": "eclCompose"
                        , "config": {
                            "section": "SkillsPassport.CoverLetter",
                            "navigation": "compose.ecl"
                        }
                    }
                    // --- DOWNLOAD VIEWS
                    , "download.ecv": {
                        "clazz": "views/compose/cv/SkillsPassportComposeView",
                        "bundle": "ecvCompose",
                        "config": {
                            "navigation": "download.ecv",
                            "events": [
                                {"el": "#export-wizard-init-btn", "event": "click"}
                            ]
                        }
                    }
                    , "download.elp": {
                        "clazz": "views/compose/elp/ComposeView",
                        "bundle": "elpCompose",
                        "config": {
                            "navigation": "download.elp",
                            "events": [
                                {"el": "#export-wizard-init-btn", "event": "click"}
                            ]
                        }
                    }
                    , "download.esp": {
                        "clazz": "views/compose/esp/EspComposeView",
                        "bundle": "espCompose",
                        "config": {
                            "navigation": "download.esp",
                            "events": [
                                {"el": "#export-wizard-init-btn", "event": "click"}
                            ]
                        }
                    }
                    , "download.ecl": {
                        "clazz": "views/compose/cl/ComposeView",
                        "bundle": "eclCompose",
                        "config": {
                            "navigation": "download.ecl",
                            "events": [
                                {"el": "#export-wizard-init-btn", "event": "click"}
                            ]
                        }
                    }
                    , "download": {
                        "clazz": "views/compose/cv/SkillsPassportComposeView",
                        "bundle": "ecvCompose",
                        "config": {
                            "navigation": "download",
                            "events": [
                                {"el": "#export-wizard-init-btn", "event": "click"}
                            ]
                        }
                    }
                    //---- UPLOAD VIEWS
                    //render the cv compose view AND have the upload modal opened
                    , "upload": {
                        "clazz": "views/compose/cv/SkillsPassportComposeView",
                        "bundle": "ecvCompose",
                        "config": {
                            "navigation": "upload",
                            "events": [
                                {"el": "#import-wizard-init-btn", "event": "click"}
                            ]
                        }
                    }
                    //render the lp compose view AND have the upload modal opened
                    , "upload.elp": {
                        "clazz": "views/compose/elp/ComposeView",
                        "bundle": "elpCompose",
                        "config": {
                            "navigation": "upload",
                            "events": [
                                {"el": "#import-wizard-init-btn", "event": "click"}
                            ]
                        }
                    }
                    //render the esp compose view AND have the upload modal opened
                    , "upload.esp": {
                        "clazz": "views/compose/esp/EspComposeView",
                        "bundle": "espCompose",
                        "config": {
                            "navigation": "upload",
                            "events": [
                                {"el": "#import-wizard-init-btn", "event": "click"}
                            ]
                        }
                    }
                    , "upload.ecl": {
                        "clazz": "views/compose/cl/ComposeView",
                        "bundle": "eclCompose",
                        "config": {
                            "navigation": "upload",
                            "events": [
                                {"el": "#import-wizard-init-btn", "event": "click"}
                            ]
                        }
                    }
                    // ---- UPLOAD FROM LINKEDIN 
                    , "linkedin.callback": {
                        "clazz": "views/compose/cv/SkillsPassportComposeView",
                        "bundle": "ecvCompose",
                        "config": {
                            "navigation": "upload",
                            "events": [
                                {"el": "#import-wizard-init-btn", "event": "click", "args": [1, "linkedin"]}
                            ]
                        }
                    }
                    , "linkedin.ecv_esp.upload": {
                        "clazz": "views/compose/cv/SkillsPassportComposeView",
                        "bundle": "ecvCompose",
                        "config": {
                            "navigation": "upload",
                            "events": [
                                {"el": "#import-wizard-init-btn", "event": "click", "args": [1, "linkedin"]}
                            ]
                        }
                    }
                    , "linkedin-ok.ecv_esp.upload": {
                        "clazz": "views/compose/cv/SkillsPassportComposeView",
                        "bundle": "ecvCompose",
                        "config": {
                            "navigation": "upload",
                            "events": [
                                {"el": "#import-wizard-init-btn", "event": "click", "args": [1, "linkedin"]}
                            ]
                        }
                    }
                    , "linkedin-error.ecv_esp.upload": {
                        "clazz": "views/compose/cv/SkillsPassportComposeView",
                        "bundle": "ecvCompose",
                        "config": {
                            "navigation": "upload",
                            "events": [
                                {"el": "#import-wizard-init-btn", "event": "click", "args": [1, "linkedin"]}
                            ]
                        }
                    }
                    // ----- REMOTE UPLOAD VIEWS
                    , "remote-upload": {
                        "clazz": "views/compose/cv/SkillsPassportComposeView",
                        "bundle": "ecvCompose"
                        , "config": {
                            "section": "SkillsPassport.LearnerInfo",
                            "navigation": "compose.ecv",
                            "clazzes": [
                                {
                                    "clazz": "ModelRemoteUploadView",
                                    "requirePath": "views/main/ModelRemoteUploadView",
                                    "showFirstTimeOnly": true
                                }
                            ]
                        }
                    }
                    , "remote-upload.elp": {
                        "clazz": "views/compose/elp/ComposeView",
                        "bundle": "elpCompose"
                        , "config": {
                            "section": "SkillsPassport.LearnerInfo",
                            "navigation": "compose.elp",
                            "clazzes": [
                                {
                                    "clazz": "ModelRemoteUploadView",
                                    "requirePath": "views/main/ModelRemoteUploadView",
                                    "showFirstTimeOnly": true
                                }
                            ]
                        }
                    }
                    , "remote-upload.esp": {
                        "clazz": "views/compose/esp/EspComposeView",
                        "bundle": "espCompose"
                        , "config": {
                            "section": "SkillsPassport.LearnerInfo",
                            "navigation": "compose.esp",
                            "clazzes": [
                                {
                                    "clazz": "ModelRemoteUploadView",
                                    "requirePath": "views/main/ModelRemoteUploadView",
                                    "showFirstTimeOnly": true
                                }
                            ]
                        }
                    }
                    , "remote-upload.ecl": {
                        "clazz": "views/compose/cl/ComposeView",
                        "bundle": "eclCompose"
                        , "config": {
                            "section": "SkillsPassport.LearnerInfo",
                            "navigation": "compose.ecl",
                            "clazzes": [
                                {
                                    "clazz": "ModelRemoteUploadView",
                                    "requirePath": "views/main/ModelRemoteUploadView",
                                    "showFirstTimeOnly": true
                                }
                            ]
                        }
                    }
                    // ----- PUBLISH VIEWS
                    , "publish": {
                        "clazz": "views/compose/cv/SkillsPassportComposeView",
                        "bundle": "ecvCompose",
                        "config": {
                            "navigation": "publish",
                            "events": [
                                {"el": "#share-document-btn", "event": "click"}
                            ]
                        }
                    }
                    , "publish-cv-library": {
                        "clazz": "views/compose/cv/SkillsPassportComposeView",
                        "bundle": "ecvCompose",
                        "config": {
                            "navigation": "publish.cv",
                            "events": [
                                {"el": "#share-document-btn", "event": "click"},
                                {"el": "#cvlibrary", "event": "click"}
                            ]
                        }
                    }
                    , "publish-xing": {
                        "clazz": "views/compose/cv/SkillsPassportComposeView",
                        "bundle": "ecvCompose",
                        "config": {
                            "navigation": "publish",
                            "events": [
                                {"el": "#share-document-btn", "event": "click"},
                                {"el": "#xing", "event": "click"}
                            ]
                        }
                    }
                    , "publish-monster": {
                        "clazz": "views/compose/cv/SkillsPassportComposeView",
                        "bundle": "ecvCompose",
                        "config": {
                            "navigation": "publish",
                            "events": [
                                {"el": "#share-document-btn", "event": "click"},
                                {"el": "#monster", "event": "click"}
                            ]
                        }
                    }
                    , "publish-eures": {
                        "clazz": "views/compose/cv/SkillsPassportComposeView",
                        "bundle": "ecvCompose",
                        "config": {
                            "navigation": "publish",
                            "events": [
                                {"el": "#share-document-btn", "event": "click"},
                                {"el": "#eures", "event": "click"}
                            ]
                        }
                    }
                    , "publish-anpal": {
                        "clazz": "views/compose/cv/SkillsPassportComposeView",
                        "bundle": "ecvCompose",
                        "config": {
                            "navigation": "publish",
                            "events": [
                                {"el": "#share-document-btn", "event": "click"},
                                {"el": "#anpal", "event": "click"}
                            ]
                        }
                    }
                    , "publish-indeed": {
                        "clazz": "views/compose/cv/SkillsPassportComposeView",
                        "bundle": "ecvCompose",
                        "config": {
                            "navigation": "publish",
                            "events": [
                                {"el": "#share-document-btn", "event": "click"},
                                {"el": "#indeed", "event": "click"}
                            ]
                        }
                    }
                },
                onClose: function () {
                    this.navigationModel.unbind("model:navigation:changed", $.proxy(this.navigateView, this));
                    this.closeViews();

                }
                , closeViews: function () {
                    this.globalController.close();

                    this.waitingIndicatorView.close();
                    this.feedbackView.close();
                    this.importWizard.close();

                    this.currentDocumentView.close();
                    this.asideview.close();
                    this.documentControlsView.close();
                    this.tooltipView.close();
                    this.closeSessionMessagingView.close();
                    this.sessionKeepAliveView.close();
                    this.clickableAreaView.close();
                    this.sortView.close();
                    this.modelLocalStoreView.close();
                    this.tabLinkView.close();
                }
                , initialize: function (options) {

                    this.template = MainTemplate;
                    this.navigationModel = options.navigationModel;

                    //Bind Events
                    this.navigationModel.bind("model:navigation:changed", $.proxy(this.navigateView, this));

                    //Delay the navigation...
                    this.navigationModel.delayNavigation(true);
                    //Render...
                    this.render(null);

                }

                , render: function (updated) {

                    var context = (updated === null ? this.model.attributes : updated);
                    var html = this.template(context);

                    var that = this;
                    this.$el.fadeOut("slow", function () {

                        $(this).html(html).fadeIn("slow");

                        //MUST COME FIRST
                        that.modelLocalStoreView = new ModelLocalStoreView({
                            el: $("#local-storage-options"),
                            model: that.model
                        });


                        that.globalController = new GlobalController({
                            el: $("body"),
                            model: that.model
                        });

                        that.currentDocumentView = new CurrentDocumentView({
                            el: $("#CurrentDocumentTitle"),
                            model: that.model,
                            navigationModel: that.navigationModel
                        });

                        that.documentControlsView = new DocumentControlsView({
                            el: $("#DocumentControls"),
                            model: that.model,
                            navigationModel: that.navigationModel
                        });

                        that.LocaleView = new LocaleView({
                            el: $("body"),
                            model: that.model
                        });

                        that.CloudSectionAreaSignInView = new CloudSectionAreaSignInView({
                            el: $("body"),
                            model: that.model
                        });

                        that.SendFeedbackView = new SendFeedbackView({
                            el: $("body"),
                            model: that.model
                        });

                        that.feedbackView = new FeedbackView({
                            model: that.model
                        });
                        that.applicationSettingsView = new ApplicationSettingsView({
                            el: $("body"),
                            model: that.model
                        });

                        that.basicCvPrinciplesView = new BasicCvPrinciplesView({
                            el: $("body"),
                            model: that.model
                        });
                        that.prepareYourInterviewView = new PrepareYourInterviewView({
                            el: $("body"),
                            model: that.model
                        });
                        that.jobPortalsView = new JobPortalsView({
                            el: $("body"),
                            model: that.model
                        });
                        that.waitingIndicatorView = new WaitingIndicatorView({
                            model: that.model
                        });

                        that.cloudwaitingIndicatorView = new CloudWaitingIndicatorView({
                            model: that.model
                        });

                        that.attachmentManagerInstance = AttachmentManager;

                        that.importWizard = new ImportWizardController({
                            el: $("#ImportWizard"),
                            model: that.model
                        });

                        that.exportWizard = new ExportWizardController({
                            el: $("#ExportWizard"),
                            model: that.model
                        });

                        that.cloudSignInView = new CloudSignInView({
                            el: $("body"),
                            model: that.model
                        });

                        that.GoogleDriveView = new GoogleDriveView({
                            el: $("body"),
                            model: that.model
                        });

                        that.CloudOneDriveView = new CloudOneDriveView({
                            el: $("body"),
                            model: that.model
                        });
                        that.ManageDocumentsView = new ManageDocumentsView({
                            el: $("body"),
                            model: that.model
                        });

                        that.googleShareView = new GoogleShareView({
                            el: $("body"),
                            model: that.model
                        });

                        that.dropboxShareView = new DropboxShareView({
                            el: $("body"),
                            model: that.model
                        });

                        that.oneDriveShareView = new OneDriveShareView({
                            el: $("body"),
                            model: that.model
                        });

                        // Share for Review right aside view
                        that.ShareReviewView = new ShareReviewView({
                            el: $("body"),
                            model: that.model
                        });

                        that.ShareView = new ShareView({
                            el: $("body"),
                            model: that.model
                        });

                        that.ManageSharesView = new ManageSharesView({
                            el: $("body"),
                            model: that.model
                        });

                        that.PartnersMainView = new PartnersMainView({
                            el: $("body"),
                            model: that.model
                        });

                        that.asideview = new AsideNavigationView({
                            el: $("#AvailableDocuments"),
                            model: that.model,
                            navigationModel: that.navigationModel
                        });

                        that.tooltipView = new TooltipView({
                            el: $("body")
                        });

                        that.closeSessionMessagingView = new CloseSessionMessagingView();

                        that.sessionKeepAliveView = new SessionKeepAliveView({
                            el: $("body"),
                            model: that.model
                        });

                        that.clickableAreaView = new ClickableAreaView({
                            el: $("body")
                        });

                        that.sortView = new SortView({
                            el: $("body"),
                            model: that.model
                        });

//					that.autoSortView = new AutoSortView({
//						el: $("body"),
//						model : that.model
//					});

                        that.tabLinkView = new TabLinkView({});


                        that.NewFeatureTooltipView = new NewFeatureTooltipView({
                            el: $("#cloud-login-tooltip"),
                            model: that.model
                        });

                        // that.ServiceProvidersTooltipView = new ServiceProvidersTooltipView({
                        //     el: $("#publish-tooltip"),
                        //     model: that.model
                        // });

                        try {
                            //console.log("about to delete cv from local storage");
                            window.localStorage.removeItem("europass.ewa.skillspassport.v3");
                        } catch (err) {
                        }

                        that.navigationModel.delayNavigation(false);

                    });

                    /**
                     *  This is a fix for a bug in Tablet- Android-Chrome that renders the modal screen with wrong dimensions in case of orientation change
                     */

                    var isAndroid = (WindowConfig.operatingSystem === "ANDROID");
                    var isChrome = (WindowConfig.browserName === "Chrome/10.");

                    window.addEventListener("orientationchange", function () {
                        if (isAndroid && isChrome) {

                            $("body").hide();

                            setTimeout(function () {
                                $("body").show();
                            }, 100);

                        }

                    }, false);

                    //create user-cookie 
                    if (!Utils.readCookie()) {
                        Utils.createCookie();
                    }
                    
                }
                /**
                 * Response to change in the Navigation Model in order to load the correct View
                 */
                , navigateView: function (view, routesModel) {
//console.log("navigateView VIEW:MainPage");		
                    if (view === undefined || view === null || view === "") {
                        view = "compose.ecv";
                    }

                    //Show the Correct Main View
                    var area = this.areas[view];
                    if (area !== undefined && area !== null) {
                        var clazz = area.clazz;
                        var bundle = area.bundle;
                        var config = area.config;

                        if (!_.isEmpty(routesModel) && _.isObject(routesModel)) {
                            var espModelInfo = routesModel.espModelInfo;
                            if (!_.isEmpty(espModelInfo) && _.isObject(espModelInfo)) {
                                this.model.espModelInfo = espModelInfo;
                            }
                        }
                        config.model = this.model;
                        ApplicationViewControllerInstance.showView(clazz, bundle, config);
                    }

                    if (AppRouter !== undefined) {
                        var url = NavigationRoutes.findActiveRoute().replace(".", "/");
                        if (Utils.readCookieByName('cloud-signed-in')) {
                            url += '/cloud';
                        }
                        AppRouter.navigate(url, {
                            trigger: false,
                            replace: !("pushState" in window.history)
                        });
                    }
                    if (window.config.matomoUrl) {
                        var currentUrl = location.href;
                        _paq.push(['setCustomUrl', currentUrl]);
                        _paq.push(['deleteCustomVariables', 'page']);
                        _paq.push(['setGenerationTimeMs', 0]);
                        _paq.push(['trackPageView']);
                    }

                }
                , gaEventSend: function () {
                    this.event.closeMessage();
                }
                
                , closeNewEuropassModal: function() {
                     $("#new-europass-modal").remove(); 
                }

            });

            return MainPageView;
        }
);
