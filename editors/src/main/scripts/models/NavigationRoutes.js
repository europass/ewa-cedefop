define(
        [
            'jquery',
            'backbone',
            //	'i18n!localization/nls/Notification',
            //	'views/messaging/FeedbackView',
            'backbonenested'
        ],
        function ($, Backbone/*, Notification, FeedbackView*/) {

            var NavigationRoutes = Backbone.NestedModel.extend({

                defaults: {
                    "config": {
                        "delayNavigation": false
                    }
                    , "cv": {
                        "compose": false
                        , "upload": false
                        , "remote-upload": false
                    },
                    "esp": {
                        "compose": false
                        , "upload": false
                        , "remote-upload": false
                    },
                    "cv-esp": {
                        "upload": false
                        , "remote-upload": false
                        , "download": false
                    },
                    "cvesp": {
                        "upload": false
                        , "remote-upload": false
                        , "download": false
                    },
                    "lp": {
                        "compose": false
                        , "upload": false
                        , "remote-upload": false
                        , "download": false
                    },
                    "cl": {
                        "compose": false
                        , "upload": false
                        , "remote-upload": false
                        , "download": false
                    },
                    "social": {
                        "linkedin": false,
                        "linkedin-callback": false,
                        "linkedin-ok": false,
                        "linkedin-error": false
                    }

                }
                , viewsPerRoute: {
                    "cv-esp.upload": "upload",
                    "cv.upload": "upload",
                    "esp.upload": "upload.esp",
                    "lp.upload": "upload.elp",
                    "cl.upload": "upload.ecl",

                    "cv-esp.remote-upload": "remote-upload",
                    "cv.remote-upload": "remote-upload",
                    "esp.remote-upload": "remote-upload.esp",
                    "lp.remote-upload": "remote-upload.elp",
                    "cl.remote-upload": "remote-upload.ecl",

                    "cv-esp.google-upload": "google-upload",
                    "cv.google-upload": "google-upload",
                    "esp.google-upload": "google-upload.esp",
                    "lp.google-upload": "google-upload.elp",

                    "cv.compose": "compose.ecv",
                    "esp.compose": "compose.esp",
                    "lp.compose": "compose.elp",
                    "cl.compose": "compose.ecl",

                    "cv-esp.download": "download",
                    "cv.download": "download.ecv",
                    "esp.download": "download.esp",
                    "lp.download": "download.elp",
                    "cl.download": "download.ecl",

                    "social.linkedin": "linkedin.ecv_esp.upload",
                    "social.linkedin-callback": "linkedin.callback",
                    "social.linkedin-ok": "linkedin-ok.ecv_esp.upload",
                    "social.linkedin-error": "linkedin-error.ecv_esp.upload",

                    "cv.publish": "publish",
                    "cv.publish-cv-library": "publish-cv-library",
                    "cv.publish-xing": "publish-xing",
                    "cv.publish-monster": "publish-monster",
                    "cv.publish-eures": "publish-eures",
                    "cv.publish-anpal": "publish-anpal",
                    "cv.publish-indeed": "publish-indeed",

                }
                , initialize: function () {

                    this.bind("change:cv-esp.remote-upload", this.navigateRemoteUpload);
                    this.bind("change:cv.remote-upload", this.navigateRemoteUploadCV);
                    this.bind("change:esp.remote-upload", this.navigateRemoteUploadESP);
                    this.bind("change:lp.remote-upload", this.navigateRemoteUploadLP);
                    this.bind("change:cl.remote-upload", this.navigateRemoteUploadCL);


                    this.bind("change:cv-esp.google-upload", this.navigateGoogleUpload);
                    this.bind("change:cv.google-upload", this.navigateGoogleUploadCV);
                    this.bind("change:esp.google-upload", this.navigateGoogleUploadESP);
                    this.bind("change:lp.google-upload", this.navigateGoogleUploadLP);

                    this.bind("change:cv-esp.upload", this.navigateUpload);
                    this.bind("change:cv.upload", this.navigateUploadCV);
                    this.bind("change:esp.upload", this.navigateUploadESP);
                    this.bind("change:lp.upload", this.navigateUploadLP);
                    this.bind("change:cl.upload", this.navigateUploadCL);


                    this.bind("change:cv.compose", this.navigateCompose);
                    this.bind("change:esp.compose", this.navigateESP);
                    this.bind("change:lp.compose", this.navigateLP);
                    this.bind("change:cl.compose", this.navigateCL);

                    this.bind("change:cv.download", this.navigateDownload);
                    this.bind("change:cv-esp.download", this.navigateDownloadCVESP);
                    this.bind("change:lp.download", this.navigateDownloadLP);
                    this.bind("change:esp.download", this.navigateDownloadESP);
                    this.bind("change:cl.download", this.navigateDownloadCL);

                    this.bind("change:social.linkedin", this.navigateUploadLinkedIn);
                    this.bind("change:social.linkedin-ok", this.navigateUploadLinkedInOk);
                    this.bind("change:social.linkedin-error", this.navigateUploadLinkedInError);

                    this.bind("change:cv.publish", this.navigatePublishCV);
                    this.bind("change:cv.publish-cv-library", this.navigatePublishCVToCvLibrary);
                    this.bind("change:cv.publish-xing", this.navigatePublishCVToXing);
                    this.bind("change:cv.publish-monster", this.navigatePublishCVToMonster);
                    this.bind("change:cv.publish-eures", this.navigatePublishCVToEures);
                    this.bind("change:cv.publish-anpal", this.navigatePublishCVToAnpal);
                    this.bind("change:cv.publish-indeed", this.navigatePublishCVToIndeed);

                }
                , unbindEvents: function () {
                    this.unbind("change:cv-esp.remote-upload", this.navigateRemoteUpload);
                    this.unbind("change:cv.remote-upload", this.navigateRemoteUploadCV);
                    this.unbind("change:esp.remote-upload", this.navigateRemoteUploadESP);
                    this.unbind("change:lp.remote-upload", this.navigateRemoteUploadLP);
                    this.unbind("change:cl.remote-upload", this.navigateRemoteUploadCL);


                    this.unbind("change:cv-esp.google-upload", this.navigateGoogleUpload);
                    this.unbind("change:cv.google-upload", this.navigateGoogleUploadCV);
                    this.unbind("change:esp.google-upload", this.navigateGoogleUploadESP);
                    this.unbind("change:lp.google-upload", this.navigateGoogleUploadLP);

                    this.unbind("change:cv-esp.upload", this.navigateUpload);
                    this.unbind("change:cv.upload", this.navigateUploadCV);
                    this.unbind("change:esp.upload", this.navigateUploadESP);
                    this.unbind("change:lp.upload", this.navigateUploadLP);
                    this.unbind("change:cl.upload", this.navigateUploadCL);

                    this.unbind("change:social.linkedin", this.navigateUploadLinkedIn);
                    this.unbind("change:social.linkedin-ok", this.navigateUploadLinkedInOk);
                    this.unbind("change:social.linkedin-error", this.navigateUploadLinkedInError);

                    this.unbind("change:cv.compose", this.navigateCompose);
                    this.unbind("change:esp.compose", this.navigateESP);
                    this.unbind("change:elp.compose", this.navigateLP);
                    this.unbind("change:cl.compose", this.navigateCL);


                    this.unbind("change:cv.download", this.navigateDownload);
                    this.unbind("change:cv-esp.download", this.navigateDownloadCVESP);
                    this.unbind("change:lp.download", this.navigateDownloadLP);
                    this.unbind("change:esp.download", this.navigateDownloadESP);
                    this.unbind("change:cl.download", this.navigateDownloadCL);

                    this.unbind("change:cv.publish", this.navigatePublishCV);
                    this.unbind("change:cv.publish-cv-library", this.navigatePublishCVToCvLibrary);
                    this.unbind("change:cv.publish-xing", this.navigatePublishCVToXing);
                    this.unbind("change:cv.publish-monster", this.navigatePublishCVToMonster);
                    this.unbind("change:cv.publish-eures", this.navigatePublishCVToEures);
                    this.unbind("change:cv.publish-anpal", this.navigatePublishCVToAnpal);
                    this.unbind("change:cv.publish-indeed", this.navigatePublishCVToIndeed);

                }
                /**
                 * Set the delayNavigation status
                 */
                , delayNavigation: function (status) {
                    this.set("config.delayNavigation", status, {silent: true});
                }
                /**
                 * Utility function to be used before setting to this
                 */
                , changeRoute: function (name, state) {
                    var active = {};
                    active[ name ] = state;
                    return active;
                }
                /**
                 * Sets all routes as inactive
                 */
                , deactivateRoutes: function () {
                    this.recursivelyDeactivateRoutes(this.attributes, "");
                }
                /**
                 * Sets all routes as inactive
                 */
                , recursivelyDeactivateRoutes: function (obj, prevRoute) {
                    for (var route in obj) {

                        if (route === "config") {
                            continue;
                        }

                        var nextObj = obj[route];

                        if ($.isPlainObject(nextObj)) {
                            this.recursivelyDeactivateRoutes(nextObj,
                                    prevRoute + (prevRoute === "" ? "" : ".") + route);
                        } else {
                            this.set(
                                    this.changeRoute(
                                            prevRoute + (prevRoute === "" ? "" : ".") + route, false),
                                    {silent: true});
                        }
                    }
                }
                /**
                 * Searches the attributes for the active route.
                 */
                , findActiveRoute: function () {
                    return this.recursivelyFindRoute(this.attributes, "", null);
                }
                /**
                 * Recursively searches attributes for the active route
                 */
                , recursivelyFindRoute: function (obj, prevRoute, foundRoute) {
                    for (var route in obj) {
                        if (foundRoute !== null) {
                            return foundRoute;
                        }
                        if (route === "config") {
                            continue;
                        }

                        var nextObj = obj[route];

                        if ($.isPlainObject(nextObj)) {
                            foundRoute = this.recursivelyFindRoute(nextObj, (prevRoute + (prevRoute === "" ? "" : ".") + route), foundRoute);
                        } else {
                            if (nextObj === true) {
                                foundRoute = (prevRoute + (prevRoute === "" ? "" : ".") + route);
                            }
                        }
                    }
                    return foundRoute;
                }
                /**
                 * Sets the route that matches the given name as active (true) and
                 * all the rest as inactive (false)
                 * @param the name of the route to activate
                 */
                , setActiveRoute: function (name) {
                    this.deactivateRoutes();
                    this.set(this.changeRoute(name, true));
                }

                /************** UPLOAD **************************/
                , navigateUpload: function (model, state) {
                    if (state === true)
                        this.navigateView("upload");
                }
                , navigateUploadCV: function (model, state) {
                    if (state === true)
                        this.navigateView("upload");
                }
                , navigateUploadESP: function (model, state) {
                    if (state === true)
                        this.navigateView("upload.esp");
                }
                , navigateUploadLP: function (model, state) {
                    if (state === true)
                        this.navigateView("upload.elp");
                }
                , navigateUploadCL: function (model, state) {
                    if (state === true)
                        this.navigateView("upload.ecl");
                }
                /************** GOOGLE UPLOAD ********************/

                , navigateGoogleUpload: function (model, state) {
                    if (state === true)
                        this.navigateView("google-upload");
                }
                , navigateGoogleUploadCV: function (model, state) {
                    if (state === true)
                        this.navigateView("google-upload");
                }
                , navigateGoogleUploadESP: function (model, state) {
                    if (state === true)
                        this.navigateView("google-upload.esp");
                }
                , navigateGoogleUploadLP: function (model, state) {
                    if (state === true)
                        this.navigateView("google-upload.elp");
                }

                /************** REMOTE UPLOAD ********************/

                , navigateRemoteUpload: function (model, state) {
                    if (state === true)
                        this.navigateView("remote-upload");
                }
                , navigateRemoteUploadCV: function (model, state) {
                    if (state === true)
                        this.navigateView("remote-upload");
                }
                , navigateRemoteUploadESP: function (model, state) {
                    if (state === true)
                        this.navigateView("remote-upload.esp");
                }
                , navigateRemoteUploadLP: function (model, state) {
                    if (state === true)
                        this.navigateView("remote-upload.elp");
                }
                , navigateRemoteUploadCL: function (model, state) {
                    if (state === true)
                        this.navigateView("remote-upload.ecl");
                }
                , navigateUploadLinkedIn: function (model, state) {
                    $("body").find("#linkedin-import-area").trigger("europass:social:upload:linkedin");
                }

                , navigateUploadLinkedInOk: function (model, state) {
                    if (state === true)
                        this.navigateView("linkedin-ok.ecv_esp.upload", model);
                }

                , navigateUploadLinkedInError: function (model, state) {
                    if (state === true)
                        this.navigateView("linkedin-error.ecv_esp.upload", model);
                }

                /************** COMPOSE **************************/
                , navigateCompose: function (model, state) {
                    if (state === true)
                        this.navigateView("compose.ecv");
                }
                , navigateESP: function (model, state) {
                    if (state === true)
                        this.navigateView("compose.esp");
                }
                , navigateLP: function (model, state) {
                    if (state === true)
                        this.navigateView("compose.elp");
                }
                , navigateCL: function (model, state) {
                    if (state === true)
                        this.navigateView("compose.ecl");
                }

                /************** DOWNLOAD **************************/
                , navigateDownload: function (model, state) {
                    if (state === true)
                        this.navigateView("download.ecv");
                }
                , navigateDownloadLP: function (model, state) {
                    if (state === true)
                        this.navigateView("download.elp");
                }
                , navigateDownloadESP: function (model, state) {
                    if (state === true)
                        this.navigateView("download.esp");
                }
                , navigateDownloadCVESP: function (model, state) {
                    if (state === true)
                        this.navigateView("download");
                }
                , navigateDownloadCL: function (model, state) {
                    if (state === true)
                        this.navigateView("download.ecl");
                }
                /************** PUBLISH **************************/
                , navigatePublishCV: function (model, state) {
                    if (state === true)
                        this.navigateView("publish");
                }
                , navigatePublishCVToCvLibrary: function (model, state) {
                    if (state === true)
                        this.navigateView("publish-cv-library");
                }
                , navigatePublishCVToXing: function (model, state) {
                    if (state === true)
                        this.navigateView("publish-xing");
                }
                , navigatePublishCVToMonster: function (model, state) {
                    if (state === true)
                        this.navigateView("publish-monster");
                }
                , navigatePublishCVToEures: function (model, state) {
                    if (state === true)
                        this.navigateView("publish-eures");
                }
                , navigatePublishCVToAnpal: function (model, state) {
                    if (state === true)
                        this.navigateView("publish-anpal");
                }
                , navigatePublishCVToIndeed: function (model, state) {
                    if (state === true)
                        this.navigateView("publish-indeed");
                }
                /**
                 * Delay the actual navigation if the delayNavigation attribute is set to true
                 */
                , navigateView: function (view, routesModel) {
                    if (this.get("config.delayNavigation") === true) {

                        var that = this;

                        var delayNavigation = setInterval(function () {

                            if (that.get("config.delayNavigation") === false) {
                                clearInterval(delayNavigation);

                                that.trigger("model:navigation:changed", view, routesModel);
                            }
                        }, 500);
                    } else {
                        this.trigger("model:navigation:changed", view, routesModel);
                    }

                }
                /**
                 * Returns the view based on the route
                 */
                , findActiveView: function () {
                    var route = this.findActiveRoute();

                    if (route === undefined || route === null || route === "") {
                        return "cv.compose";
                    }

                    return this.viewsPerRoute[ route ];
                }
                /**
                 * Retrieves document and view information based on the view
                 */
                , analyze: function (view) {
                    var parts = view.split(".");

                    switch (parts.length) {
                        case 0:
                        {
                            return {
                                page: "compose",
                                document: "ecv"
                            };
                        }
                        case 1:
                        {
                            return {
                                page: "compose",
                                document: this.analyzeDocumentType(parts[1])
                            };
                        }
                        default:
                        {
                            return {
                                page: parts[0],
                                document: this.analyzeDocumentType(parts[1]),
                            };
                        }
                    }
                }
                , analyzeDocumentType: function (document) {
                    if (document === undefined || document === null || document === "") {
                        return "ECV_ESP";
                    }

                    var firstLetter = document.substring(0, 1);

                    var doc = document;
                    if (firstLetter.toUpperCase() !== "E") {
                        doc = "E" + doc;
                    }

                    return doc.toUpperCase();
                }
            });
            return NavigationRoutes;
        }
);