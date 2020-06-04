var librariesLoc = window.resourcesBase + "/static/ewa/libraries";

var requireConfig = {
    baseUrl: window.resourcesBase + "/static/ewa/scripts",

    locale: window.ewaLocale,

    urlArgs: 'version=' + window.version,

    paths: {

        localization: window.resourcesBase + "/static/ewa/localization",

//		requireLib: librariesLoc + '/require/require',
        i18n: librariesLoc + '/require/plugins/i18n-2.0.4',
        domReady: librariesLoc + '/require/plugins/domReady-2.0.1'
    }
};
require.config(requireConfig)(
        [
            'domReady',
            'i18n!localization/nls/GuiLabel'
        ],
        function (domReady, GuiLabel) {
            domReady(function () {
                var defaultPageTitle = "Redirected";
                var defaultHeading = "Unsupported Browser";
                var defaultDescription = "<p>We are sorry, but your browser is <em>not supported</em>.</p><p>If you wish to fill-in your Europass CV online, please visit the <a href=\"http://europass.cedefop.europa.eu/instruments/cv/step0.do\" target=\"_blank\">previous europass cv editor</a></p>";

                var pageTitle = defaultPageTitle;
                var heading = defaultHeading;
                var description = defaultDescription;

                if (GuiLabel !== undefined) {
                    var translatedPageTitle = GuiLabel["EWA.UnsupportedBrowser.PageTitle"];
                    if (translatedPageTitle !== undefined && translatedPageTitle !== null && translatedPageTitle !== "") {
                        pageTitle = translatedPageTitle;
                    }
                    var translatedHeading = GuiLabel["EWA.UnsupportedBrowser.Headline"];
                    if (translatedHeading !== undefined && translatedHeading !== null && translatedHeading !== "") {
                        heading = translatedHeading;
                    }
                    var translatedDescription = GuiLabel["EWA.UnsupportedBrowser.Description"];
                    if (translatedDescription !== undefined && translatedDescription !== null && translatedDescription !== "") {
                        description = translatedDescription;
                    }
                }

                //Page Title
                document.title = pageTitle;
                //h1
                var h1 = document.getElementById("unsupported-browser-notice");
                if (h1 !== undefined && h1 !== null && h1 !== "") {
                    h1.innerHTML = heading;
                }
                //div.description
                var divDescription = document.getElementById("unsupported-browser-message");
                if (divDescription !== undefined && divDescription !== null && divDescription !== "") {
                    divDescription.innerHTML = description;
                }

            });
        }
);
