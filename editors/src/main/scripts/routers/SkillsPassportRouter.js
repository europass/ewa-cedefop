define(
        [
            'jquery',
            'backbone',
            'Utils'
        ],
        function ($, Backbone, Utils) {

            var SkillsPassportRouter = Backbone.Router.extend({

                routes: {
                    "cv/compose(/:cloud)": "composeView"
                    , "esp/compose(/:cloud)": "espComposeView"
                    , "lp/compose(/:cloud)": "lpComposeView"
                    , "cl/compose(/:cloud)": "clComposeView"

                    , "cv-esp/upload(/:cloud)": "uploadView"
                    , "cv/upload(/:cloud)": "cvUploadView"
                    , "esp/upload(/:cloud)": "espUploadView"
                    , "lp/upload(/:cloud)": "lpUploadView"
                    , "cl/upload(/:cloud)": "clUploadView"
                    , "signature/upload(/:cloud)": "signatureUploadView"

                    , "cv-esp/remote-upload*path": "remoteUploadView"
                    , "cv/remote-upload*path": "remoteUploadView"
                    , "esp/remote-upload": "espRemoteUploadView"
                    , "lp/remote-upload": "lpRemoteUploadView"
                    , "cl/remote-upload": "clRemoteUploadView"


                    , "cv/download(/:cloud)": "downloadView"
                    , "lp/download(/:cloud)": "lpDownloadView"
                    , "esp/download(/:cloud)": "espDownloadView"
                    , "cv-esp/download(/:cloud)": "cvespdownloadView"
                    , "cl/download(/:cloud)": "cldownloadView"


                    , "cv/google-upload": "cvGoogleUploadView"
                    , "cv-esp/google-upload": "googleUploadView"
                    , "esp/google-upload": "espGoogleUploadView"
                    , "lp/google-upload": "lpGoogleUploadView"

                    , "social/linkedin": "linkedInView"
                    , "social/linkedin/callback": "linkedInViewCallback"
                    , "social/linkedin/ok/:jsessionid": "linkedInViewOk"
                    , "social/linkedin/code/:httpCode/error/:errorKey/trace/:traceCode": "linkedInViewError"

                    , "cv/publish": "cvPublishView"
                    , "cv/publish-cv-library": "cvPublishCvLibraryView"
                    , "cv/publish-xing": "cvPublishXingView"
                    , "cv/publish-monster": "cvPublishMonsterView"
                    , "cv/publish-eures": "cvPublishEuresView"
                    , "cv/publish-anpal": "cvPublishAnpalView"
                    , "cv/publish-indeed": "cvPublishIndeedView"

                    , "*path": "defaultView"

                }

                , defaultViewUrl: "cv/compose"
                , cvComposeViewUrl: "cv/compose"
                , espComposeViewUrl: "esp/compose"
                , lpComposeViewUrl: "lp/compose"
                , clComposeViewUrl: "cl/compose"

                , initialize: function (options) {
                    this.model = options.model;
                }
                , defaultView: function () {
                    this.composeView();
                }
                , composeView: function () {
                    this.model.setActiveRoute("cv.compose");
                }
                , espComposeView: function () {
                    this.model.setActiveRoute("esp.compose");
                }
                , lpComposeView: function () {
                    this.model.setActiveRoute("lp.compose");
                }
                , clComposeView: function () {
                    this.model.setActiveRoute("cl.compose");
                }

                , uploadView: function () {
                    this.model.setActiveRoute("cv-esp.upload");
                }
                , cvUploadView: function () {
                    this.model.setActiveRoute("cv.upload");
                }
                , lpUploadView: function () {
                    this.model.setActiveRoute("lp.upload");
                }
                , espUploadView: function () {
                    this.model.setActiveRoute("esp.upload");
                }
                , clUploadView: function () {
                    this.model.setActiveRoute("cl.upload");
                }
                , signatureUploadView: function () {
                    this.model.setActiveRoute("signature.upload");
                }

                , googleUploadView: function () {
                    this.model.setActiveRoute("cv-esp.google-upload");
                }
                , cvGoogleUploadView: function () {
                    this.model.setActiveRoute("cv.google-upload");
                }
                , lpGoogleUploadView: function () {
                    this.model.setActiveRoute("lp.google-upload");
                }
                , espGoogleUploadView: function () {
                    this.model.setActiveRoute("esp.google-upload");
                }

                , remoteUploadView: function () {
                    this.model.setActiveRoute("cv-esp.remote-upload");
                }
                , cvRemoteUploadView: function () {
                    this.model.setActiveRoute("cv.remote-upload");
                }
                , lpRemoteUploadView: function () {
                    this.model.setActiveRoute("lp.remote-upload");
                }
                , espRemoteUploadView: function () {
                    this.model.setActiveRoute("esp.remote-upload");
                }
                , clRemoteUploadView: function () {
                    this.model.setActiveRoute("cl.remote-upload");
                }

                , downloadView: function () {
                    this.model.setActiveRoute("cv.download");
                }
                , lpDownloadView: function () {
                    this.model.setActiveRoute("lp.download");
                }
                , espDownloadView: function () {
                    this.model.setActiveRoute("esp.download");
                }
                , cvespdownloadView: function () {
                    this.model.setActiveRoute("cv-esp.download");
                }
                , cldownloadView: function () {
                    this.model.setActiveRoute("cl.download");
                }
                , linkedInView: function () {
                    this.model.setActiveRoute("social.linkedin");
                }
                , linkedInViewCallback: function () {
                    this.model.setActiveRoute("social.linkedin-callback");
                }
                , linkedInViewOk: function (jsessionid) {
                    this.model.espModelInfo = {};
                    this.model.espModelInfo.linkedInJsessionid = jsessionid;
                    this.model.setActiveRoute("social.linkedin-ok");
                }
                , linkedInViewError: function (httpCode, errorKey, trace) {
                    this.model.espModelInfo = {};
                    this.model.espModelInfo.linkedInErrorInfo = {
                        httpCode: httpCode,
                        errorKey: errorKey,
                        trace: trace
                    };
                    this.model.setActiveRoute("social.linkedin-error");
                }
                , cvPublishView: function () {
                    this.model.setActiveRoute("cv.publish");
                }
                , cvPublishCvLibraryView: function () {
                    this.model.setActiveRoute("cv.publish-cv-library");
                }
                , cvPublishXingView: function () {
                    this.model.setActiveRoute("cv.publish-xing");
                }
                , cvPublishMonsterView: function () {
                    this.model.setActiveRoute("cv.publish-monster");
                }
                , cvPublishEuresView: function () {
                    this.model.setActiveRoute("cv.publish-eures");
                }
                , cvPublishAnpalView: function () {
                    this.model.setActiveRoute("cv.publish-anpal");
                }
                , cvPublishIndeedView: function () {
                    this.model.setActiveRoute("cv.publish-indeed");
                }

                /**
                 * @param model : the esp model
                 * @param pageInfo : an object {
                 document: {cv | lp | cv-esp | esp},
                 page: { compose | download | upload }
                 }
                 */
                , decideView: function (model, pageInfo) {
                    //if we have pageInfo available consult this,
                    if (Utils.isEmptyObject(pageInfo) === false && (pageInfo.page === "upload" || pageInfo.page === "remote-upload")) {
                        switch (pageInfo.document) {
                            case "ESP" :
                                return this.espComposeViewUrl;
                            case "ELP" :
                                return this.lpComposeViewUrl;
                            case "ECL"  :
                                return this.clComposeViewUrl;
                        }
                    }
                    //otherwise, consult the XML document type
                    if (model === null) {
                        return this.defaultViewUrl;
                    }
                    var view;
                    var document = model.info().readDocumentType();
                    switch (document) {
                        case "ESP" :
                        {
                            view = this.espComposeViewUrl;
                            break;
                        }
                        case "ELP" :
                        {
                            view = this.lpComposeViewUrl;
                            break;
                        }
                        case "ECL" :
                        {
                            view = this.clComposeViewUrl;
                            break;
                        }
                        default :
                        {
                            //default view is ECV
                            view = this.defaultViewUrl;
                            break;
                        }
                    }
                    return view;
                }
            });
            return SkillsPassportRouter;
        }
);