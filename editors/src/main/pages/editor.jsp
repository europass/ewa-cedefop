<!DOCTYPE html>
<html lang="${sessionScope['locale']}">
    <head>
        <meta charset="utf-8" />
        <meta http-equiv="X-UA-Compatible" content="IE=Edge">
        <meta name="google-site-verification" content="5tzz00r0Bd_OwPPqrTPmJblRC9SR122ysW8aihW_uzY" />
        <meta name="google" content="notranslate">
        <title>Europass online editor</title>
        <meta name="description" content="Give a comprehensive picture of your skills and qualifications">

        <!-- Facebook-->
        <meta property="og:title" content="Europass online editor">
        <meta property="og:description" content="Give a comprehensive picture of your skills and qualifications">
        <meta property="og:image" content="https://europass.cedefop.europa.eu/editors/static/ewa/images/screenshots/main_page.png">
        <meta property="og:url" content= "https://europass.cedefop.europa.eu/editors">

        <link href="https://europass.cedefop.europa.eu/editors/en" rel="alternate" hreflang="x-default" />
        <link href="https://europass.cedefop.europa.eu/editors/en" rel="alternate" hreflang="en" />
        <link href="https://europass.cedefop.europa.eu/editors/pt" rel="alternate" hreflang="pt" />
        <link href="https://europass.cedefop.europa.eu/editors/ro" rel="alternate" hreflang="ro" />
        <link href="https://europass.cedefop.europa.eu/editors/pl" rel="alternate" hreflang="pl" />
        <link href="https://europass.cedefop.europa.eu/editors/nl" rel="alternate" hreflang="nl" />
        <link href="https://europass.cedefop.europa.eu/editors/mk" rel="alternate" hreflang="mk" />
        <link href="https://europass.cedefop.europa.eu/editors/sk" rel="alternate" hreflang="sk" />
        <link href="https://europass.cedefop.europa.eu/editors/nb" rel="alternate" hreflang="nb" />
        <link href="https://europass.cedefop.europa.eu/editors/sl" rel="alternate" hreflang="sl" />
        <link href="https://europass.cedefop.europa.eu/editors/tr" rel="alternate" hreflang="tr" />
        <link href="https://europass.cedefop.europa.eu/editors/fi" rel="alternate" hreflang="fi" />
        <link href="https://europass.cedefop.europa.eu/editors/sv" rel="alternate" hreflang="sv" />
        <link href="https://europass.cedefop.europa.eu/editors/hu" rel="alternate" hreflang="hu" />
        <link href="https://europass.cedefop.europa.eu/editors/mt" rel="alternate" hreflang="mt" />
        <link href="https://europass.cedefop.europa.eu/editors/da" rel="alternate" hreflang="da" />
        <link href="https://europass.cedefop.europa.eu/editors/lt" rel="alternate" hreflang="lt" />
        <link href="https://europass.cedefop.europa.eu/editors/cs" rel="alternate" hreflang="cs" />
        <link href="https://europass.cedefop.europa.eu/editors/es" rel="alternate" hreflang="es" />
        <link href="https://europass.cedefop.europa.eu/editors/bg" rel="alternate" hreflang="bg" />
        <link href="https://europass.cedefop.europa.eu/editors/et" rel="alternate" hreflang="et" />
        <link href="https://europass.cedefop.europa.eu/editors/de" rel="alternate" hreflang="de" />
        <link href="https://europass.cedefop.europa.eu/editors/lv" rel="alternate" hreflang="lv" />
        <link href="https://europass.cedefop.europa.eu/editors/it" rel="alternate" hreflang="it" />
        <link href="https://europass.cedefop.europa.eu/editors/el" rel="alternate" hreflang="el" />
        <link href="https://europass.cedefop.europa.eu/editors/hr" rel="alternate" hreflang="hr" />
        <link href="https://europass.cedefop.europa.eu/editors/fr" rel="alternate" hreflang="fr" />
        <link href="https://europass.cedefop.europa.eu/editors/is" rel="alternate" hreflang="is" />

        <script type="text/javascript">
            var CKEDITOR_BASEPATH = '/editors/static/ewa/libraries/ckeditor/';
        </script>
        <!--<script type="text/javascript" src="${applicationScope['context.resourcesBase']}/static/ewa/libraries/touch/touche-20140704-TRUNK.js?version=${project.version}"></script> -->

        <link rel="shortcut icon" href="${applicationScope['context.resourcesBase']}/static/ewa/images/favicon.ico" />
        <link rel="icon" href="${applicationScope['context.resourcesBase']}/static/ewa/images/favicon.gif" type="image/gif" />
        <link rel="stylesheet" href="${applicationScope['context.resourcesBase']}/static/ewa/styles/css-reset.css?version=${project.version}" type="text/css" />
        <link rel="stylesheet" href="${applicationScope['context.resourcesBase']}/static/ewa/styles/all-styles.css?version=${project.version}" type="text/css" />
        <link rel="stylesheet" href="https://fonts.googleapis.com/css?family=Open+Sans:400,700italic,700,300,400italic,600,800&subset=latin,greek,cyrillic" type="text/css">

        <!--[if IE]>
        <link rel="stylesheet" 
                type="text/css" 
                href="${applicationScope['context.resourcesBase']}/static/ewa/styles/ewa-ie-background.css?version=${project.version}" />
        <![endif]-->

        <!--[if lte IE 9]>
        <script src="${applicationScope['context.resourcesBase']}/static/ewa/util/html5.js?version=${project.version}"></script>
        <script src="${applicationScope['context.resourcesBase']}/static/ewa/scripts/unsupportedBrowser.jsp?version=${project.version}"></script>
        <![endif]-->


        <!--[if IE]>
        <%String supportsHtml5 = (String) session.getAttribute( "SUPPORTS_HTML5" );
        if ( supportsHtml5 != null && "false".equals( supportsHtml5 ) ){
                session.setAttribute("CLIENT_REQUESTED_RELOAD", "true"); 
        }%>
        <script type="text/javascript">
         /* When we are redirected to the page from a different location 
          * (ie: using HTTP header "Location"), 
          * then modifying the hash will result in a reload.
          */
          window.location.hash = window.location.hash; 
          /* In normal circumstances, this should be a no-op, 
           * but if the user is using IE and has arrived via a redirect, 
           * the page will reload before they even notice it has loaded
           */
        </script>
        <![endif]-->
        <script type="text/javascript">
            window.onload = function () {
                createDynamicBounceDiv();
            };

            window.sessionLocale = "${sessionScope['locale']}";
            window.remoteModel = "${sessionScope['remoteModel']}";
            window.remoteFeedback = "${sessionScope['remoteFeedback']}";
            window.sharedRemoteModel = "${sessionScope['sharedRemoteModel']}";

            window.config = {
                photosize: "${applicationScope['context.photo.permitted.size']}"
                , photomimes: "${applicationScope['context.photo.permitted.mimes']}"
                , phototypes: "${applicationScope['context.photo.permitted.types']}"
                , photoext: "${applicationScope['context.photo.permitted.extension']}"
                , signaturesize: "${applicationScope['context.signature.permitted.size']}"
                , signaturemimes: "${applicationScope['context.signature.permitted.mimes']}"
                , signaturetypes: "${applicationScope['context.signature.permitted.types']}"
                , signatureext: "${applicationScope['context.signature.permitted.extension']}"
                , fileCumulativeSize: "${applicationScope['context.file.permitted.cumulative.size']}"
                , filesize: "${applicationScope['context.file.permitted.size']}"
                , filemimes: "${applicationScope['context.file.permitted.mimes']}"
                , filetypes: "${applicationScope['context.file.permitted.types']}"
                , fileext: "${applicationScope['context.file.permitted.extension']}"
                , version: "${applicationScope['context.current.version']}"
                , api: "${applicationScope['context.apiBaseUri']}"
                , serverDateTime: "${applicationScope['server.datetime']}"
                , defaultEwaEditorContext: "${applicationScope['context.ewa.editors.default']}"
                , operatingSystem: "${sessionScope['operatingSystem']}"
                , browserName: "${sessionScope['browserName']}"
                , browserDescription: "${sessionScope['browserDescription']}"
                , cloudExportStorageFolder: "Europass"	//TODO: decide how it will be loaded
                , cloudStorageFolder: "Europass/files"
                , cloudShareFolder: "Europass/shares"
                , gdrive: "gdrive"
                , onedrive: "onedrive"
                , onedriveAppkey: "${applicationScope['context.ewa.editors.onedrive.appkey']}"
                , onedriveCallbackUrl: "${applicationScope['context.ewa.editors.cloudlogin.callback.onedrive']}"
                , onedriveFilePickerAppId: "${applicationScope['context.ewa.editors.onedrive.filepicker.appId']}"
                , onedriveFilePickerCallbackUrl: "${applicationScope['context.ewa.editors.onedrive.filepicker.callbackurl']}"
                , googledriveAppId: "${applicationScope['context.ewa.editors.googledrive.appid']}"
                , googledriveClientId: "${applicationScope['context.ewa.editors.googledrive.clientid']}"
                , googledriveDevKey: "${applicationScope['context.ewa.editors.googledrive.devkey']}"
                , googledriveClientEmail: "${applicationScope['context.ewa.editors.googledrive.clientemail']}"
                , dropboxAppKey: "${applicationScope['context.ewa.editors.dropbox.appkey']}"
                , dropboxCallbackUrl: "${applicationScope['context.ewa.editors.dropbox.callbackurl']}"
                , showShareButton: "${applicationScope['context.ewa.editors.share.show']}"
                , showShareXingButton: "${applicationScope['context.ewa.editors.xing.share.show']}"
                , showShareEuresButton: "${applicationScope['context.ewa.editors.eures.share.show']}"
                , showShareMonsterButton: "${applicationScope['context.ewa.editors.monster.share.show']}"
                , showShareCvLibraryButton: "${applicationScope['context.ewa.editors.cvLibrary.share.show']}"
                , showShareAnpalButton: "${applicationScope['context.ewa.editors.anpal.share.show']}"
                , showShareIndeedButton: "${applicationScope['context.ewa.editors.indeed.share.show']}"
                , showShareForReview: "${applicationScope['context.ewa.editors.share.for.review.show']}"
                , showNewEuropassPortalNotification: "${applicationScope['context.ewa.editors.new.europass.portal.notification.show']}"
                , remoteUploadPartnerKey: "${sessionScope['remoteUploadPartnerKey']}"
                , remoteUploadCallbackUrl: "${sessionScope['remoteUploadCallbackUrl']}"
                , remoteUploadPartnerName: "${sessionScope['remoteUploadPartnerName']}"
                , remoteUploadPartnerLocale: "${sessionScope['remoteUploadPartnerLocale']}"
                , sharedRemoteModel: "${sessionScope['sharedRemoteModel']}"
                , sharedRemoteSharedEmail: "${sessionScope['shareSenderEmail']}"
                , sharedRemoteRecipientEmail: "${sessionScope['shareRecipientEmail']}"
                , survey: "${applicationScope['context.ewa.editors.survey']}"
                , showCloudLogin: "${applicationScope['context.ewa.editors.cloud.login.enabled']}"
                , cookieId: "${applicationScope['context.ewa.editors.user.cookie.id']}"
                , cloudCookieId: "${applicationScope['context.ewa.editors.user.cloudcookie.id']}"
                , cloudAccessToken: "${applicationScope['context.ewa.editors.user.cloudcookie.access.token']}"
                , showLinkedin: "${applicationScope['ewa.editors.social.linkedin.enabled']}"
                , projectVersion: "${project.version}"
                , environment: "${applicationScope['context.project.current.environment']}"
                , matomoUrl: "${applicationScope['context.ewa.editors.matomo.url']}"
                , permissionToKeepCv: "${applicationScope['context.ewa.editors.permission.keep.cv']}"
                , permissionToKeepNotImportedCv: "${applicationScope['context.ewa.editors.permission.keep.not.imported.cv']}"
            };

            window.resourcesBase = "${applicationScope['context.resourcesBase']}";

            /**
             * Dynamic creation of content according to browser animation support. For no animation support an animated gif is used, otherwise use animation css. 
             */
            function createDynamicBounceDiv() {
                var d1 = document.createElement("div");
                d1.className = 'bounce1';
                var d2 = document.createElement("div");
                d2.className = 'bounce2';
                var d3 = document.createElement("div");
                d3.className = 'bounce3';
                var parentDiv = document.getElementById("page-loading-waiting-indicator-graphic");
                parentDiv.appendChild(d1);
                parentDiv.appendChild(d2);
                parentDiv.appendChild(d3);
            }

        </script>
    </head>
    <body>
        <section id="page-loading-waiting-indicator">
            <div class="opacity">&nbsp;<span class="indicator-logo">&nbsp;</span></div>
            <div id="page-loading-waiting-indicator-graphic" class="loading spinner"></div>
        </section>

        <section id="page-loading-error" style="display:none">
            <div id="error_msg" class="error_msg">${sessionScope['initial.loading.error.msg']}</div>
        </section>

        <!-- main.hbs goes here -->
        <script type="text/javascript"
                data-main="${applicationScope['context.resourcesBase']}/static/ewa/scripts/main"
                src="${applicationScope['context.resourcesBase']}/static/ewa/libraries/require/require-2.3.3-min.js">
        </script>

        <script>
            // define waitSeconds to override the default, until main.js loads
            // to add caching versioning for main.js :
            // https://stackoverflow.com/questions/19495378/requirejs-data-main-with-query-string-not-working/19495379#19495379
            require.config({waitSeconds: 90, urlArgs: 'version=' + window.config.version});
        </script>

        <script id="dropboxjs" data-app-key="${applicationScope['context.ewa.editors.dropbox.appkey']}"></script>

        <!-- Matomo -->
        <script type="text/javascript">
            if (window.config.matomoUrl) {
                var _paq = _paq || [];
                /* tracker methods like "setCustomDimension" should be called before "trackPageView" */
                _paq.push(['enableLinkTracking']);
                (function () {
                    var u = window.config.matomoUrl;
                    _paq.push(['setTrackerUrl', u + 'piwik.php']);
                    _paq.push(['setSiteId', '2']);
                    var d = document, g = d.createElement('script'), s = d.getElementsByTagName('script')[0];
                    g.type = 'text/javascript';
                    g.async = true;
                    g.defer = true;
                    g.src = u + 'piwik.js';
                    s.parentNode.insertBefore(g, s);
                })();
            }
        </script>

        <script id="do-not-track" src="${applicationScope['context.resourcesBase']}/static/ewa/libraries/do-not-track.js"></script>

    </body>
</html>
