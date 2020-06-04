var ewaLocale = null;
try {
    ewaLocale = window.localStorage.getItem("europass.ewa.locale");
} catch (err) {
    showError();
}
if (ewaLocale === undefined || ewaLocale === null || ewaLocale === "" || ewaLocale === "null") {
    try {
        var tempSkillsPassportString = window.localStorage.getItem("temporary.europass.ewa.skillspassport.v3");

        var tempSkillsPassportJSON = JSON.parse(tempSkillsPassportString);
        if (tempSkillsPassportJSON
                && tempSkillsPassportJSON.SkillsPassport) {
            var modelLocale = tempSkillsPassportJSON.SkillsPassport.Locale;
        } else {
            var modelLocale = null;
        }
        //#EPAS-242  CL date should be updated automatically
        if (tempSkillsPassportJSON
                && tempSkillsPassportJSON.SkillsPassport
                && tempSkillsPassportJSON.SkillsPassport.CoverLetter
                && tempSkillsPassportJSON.SkillsPassport.CoverLetter.Letter
                && tempSkillsPassportJSON.SkillsPassport.CoverLetter.Letter.Localisation
                && tempSkillsPassportJSON.SkillsPassport.CoverLetter.Letter.Localisation.Date) {
            var d = new Date();
            tempSkillsPassportJSON.SkillsPassport.CoverLetter.Letter.Localisation.Date.Day = d.getUTCDate();
            tempSkillsPassportJSON.SkillsPassport.CoverLetter.Letter.Localisation.Date.Month = d.getMonth() + 1;
            tempSkillsPassportJSON.SkillsPassport.CoverLetter.Letter.Localisation.Date.Year = d.getUTCFullYear();
            window.localStorage.setItem('temporary.europass.ewa.skillspassport.v3', JSON.stringify(tempSkillsPassportJSON));
        }

        // When loading through Share override locale that is set on session!
        if (window.config.sharedRemoteModel !== undefined && window.config.sharedRemoteModel !== '' &&
                window.config.remoteUploadPartnerLocale !== '') {
            modelLocale = window.config.remoteUploadPartnerLocale;
        }

        if (window.config.remoteUploadPartnerKey !== '' && window.config.remoteUploadCallbackUrl !== '' &&
                window.config.remoteUploadPartnerLocale !== '' && window.config.remoteUploadPartnerLocale !== 'invalid-locale') {

            // This is to override locale for interop partners.
            modelLocale = window.config.remoteUploadPartnerLocale;
        }

        if (!(modelLocale === undefined || modelLocale === null || modelLocale === "" || modelLocale === "null")
                && (modelLocale !== window.sessionLocale)) {
            ewaLocale = modelLocale;

            //Reload and change the url according to the new locale
            var url = window.location.pathname;
            var context = url.substring(0, url.indexOf("/", 1));

            // var view = url.substring(context.length + 3)
            // After change of url locale path pattern (CAN SUPPORT e.g sr-latn) we need to change how to take url view !!!)
            var view = url.substring(url.indexOf('/', url.indexOf('/', 1) + 1));
            view = (view.indexOf("/") === 0) ? view : "/" + view;
            var newUrl = context + "/" + ewaLocale + view;

            window.location = newUrl;
        }
    } catch (err) {
        showError();
    }
}
if (ewaLocale === undefined || ewaLocale === null || ewaLocale === "" || ewaLocale === "null") {
    ewaLocale = window.sessionLocale;
}
if (ewaLocale === undefined || ewaLocale === null || ewaLocale === "" || ewaLocale === "null") {
    ewaLocale = "en";
}
try {
    window.localStorage.removeItem("europass.ewa.locale");
} catch (err) {
    showError();
}
window.baseResourcesUrl = window.resourcesBase + "/static/ewa";

var librariesLoc = window.baseResourcesUrl + "/libraries";

var globalConfig = {
    baseUrl: window.baseResourcesUrl + "/scripts",

    locale: ewaLocale,

    application: "ECV",

    //The number of seconds to wait before giving up on loading a script. 
    //Setting it to 0 disables the timeout. The default is 7 seconds.
    waitSeconds: 90,

    //If set to true, an error will be thrown if a script loads that does not call define() 
    //or have a shim exports string value that can be checked. 
    //See Catching load failures in IE for more information.
    //NOTE: If you do set enforceDefine: true, and you use data-main="" to load your main JS module, 
    //then that main JS module must call define() instead of require() to load the code it needs. 
    //The main JS module can still call require/requirejs to set config values, 
    //but for loading modules it should use define().
    //enforceDefine: true,
    //ekar 15/01/2013: sorry could not make it work with optimizer and handlebars
    //it is required that main.js starts with a define call which the causes the dependency resolving to break for handlebars

    urlArgs: 'version=' + window.config.version,

    config: {
        i18n: {
            locale: ewaLocale
        },
        'europass/europass': {
            locale: ewaLocale
        },
        'europass/backbone/EWABackboneView': {
            locale: ewaLocale
        },
        'models/SkillsPassport': {
            documentType: "ECV",
            locale: ewaLocale,
            xsdversion: "V3.4",
            generator: "EWA",
            comment: "Europass CV",
            europassLogo: true
        },
        'models/ConversionManager': {
            documentType: "ECV",
            locale: ewaLocale,
            xsdversion: "V3.4",
            generator: "EWA",
            comment: "Europass CV",
            europassLogo: true
        },
        'models/TranslationManager': {
            locale: ewaLocale
        },
        'views/MainPageView': {
            locale: ewaLocale
        },
        'views/main/LocaleView': {
            locale: ewaLocale
        },
        'views/forms/SkillsPassportUploadView': {
            locale: ewaLocale,
            xsdversion: "V3.4",
            documentType: "ECV"
        },
        'views/upload/LinkedInUploadView': {
            locale: ewaLocale
        },
        'views/main/AsideNavigationView': {
            locale: ewaLocale
        },
        'views/interaction/RichTextEditor2': {
            locale: ewaLocale
        },
        'views/upload/UploadController': {
            locale: ewaLocale
        },
        'views/upload/GoogleDriveUploadView': {
            locale: ewaLocale
        },
        'views/main/cloud/LoadDocumentController': {
            locale: ewaLocale
        },
        'views/main/cloud/GoogleDriveView': {
            locale: ewaLocale,
            xsdversion: "V3.4",
            generator: "EWA",
            comment: "Europass CV",
            europassLogo: true
        },
        'views/main/cloud/OneDriveView': {
            locale: ewaLocale,
            xsdversion: "V3.4",
            generator: "EWA",
            comment: "Europass CV",
            europassLogo: true
        }
    },

    paths: {
        localization: window.baseResourcesUrl + "/localization",

        libraries: librariesLoc,

        backbone: librariesLoc + '/backbone/backbone-1.3.3-min',
        backbonenested: librariesLoc + '/backbone-nested/backbone-nested-2.0.4',
        cookie: librariesLoc + '/cookie/Cookie',
        fileupload: librariesLoc + '/fileupload/jquery.fileupload-9.5.2-min',
        "jquery.ui.widget": librariesLoc + '/fileupload/jquery.ui.widget-1.10.4-amd-min',
        iframetransport: librariesLoc + '/fileupload/jquery.iframe-transport-9.5.2-min',
        handlebars: librariesLoc + '/require/plugins/hbs/handlebars-1.3.0-slexaxton-amd-min',
        jcrop: librariesLoc + '/jcrop/jquery.Jcrop-0.9.12-min',
        jquery: librariesLoc + '/jquery/jquery-3.2.1-min',

        jqueryui: librariesLoc + '/jquery-ui/jquery-ui.min',
        jsonjs: librariesLoc + '/jsonjs/json3-3.3.1-min',
        jsonpath: librariesLoc + '/jsonpath/jsonpath-0.8.0',

        scrollTo: librariesLoc + '/scrollbar/jquery.scrollTo',

        underscore: librariesLoc + '/underscore/underscore-1.8.3-min',
        xdate: librariesLoc + '/xdate/xdate-0.8.2',

        fastclick: librariesLoc + '/touch/fastclick-1.0.6',

        domReady: librariesLoc + '/require/plugins/domReady-2.0.1-min',
        hbs: librariesLoc + '/require/plugins/hbs/hbs-0.8.1-min',
        i18nprecompile: librariesLoc + '/require/plugins/hbs/i18nprecompile-0.8.1-min',
        i18n: librariesLoc + '/require/plugins/i18n-2.0.4-min',
        text: librariesLoc + '/require/plugins/text-2.0.10-min',

        Utils: 'europass/Utils',
        UtilsForRTE: 'europass/UtilsForRTE',
        HttpUtils: 'europass/http/HttpUtils',
        HelperUtils: 'templates/HelperUtils',
        HelperManageModelUtils: 'templates/HelperManageModelUtils',
        Interactions: 'europass/Interactions',
        ModalFormInteractions: 'europass/ModalFormInteractions',
        BackboneViewAugmented: 'europass/backbone/BackboneViewAugmented',
        BackboneNestedModelAugmented: 'europass/backbone/BackboneNestedModelAugmented',

        dropin: librariesLoc + '/dropbox/dropins',
        dropboxSDK: librariesLoc + '/dropbox/Dropbox-sdk.min',

//		typeahead: librariesLoc + '/typeahead/typeahead-0.10.5-custom',
        typeahead: librariesLoc + '/typeahead/typeahead-0.11.1-custom',

        latinise: librariesLoc + '/latinise/latinise',

        oneDriveAPI: librariesLoc + '/onedrive/oneDriveAPI',
        optout: librariesLoc + '/do-not-track',
        ckeditor: librariesLoc + '/ckeditor/ckeditor',
        domPurify: librariesLoc + '/dompurify/purify.min'
//		,touche: librariesLoc + '/touch/touche-20140704-TRUNK'
    },
//	Note:
//		only other shim configured modules, or modules that do not
//		have dependencies and that execute right away and do the define() call
//		later (like jquery and lodash) work with other shim configured
//		libraries
    shim: {
        'jquery': {exports: '$'},
        'jqueryui': {deps: ['jquery'], exports: '$.ui'},
        'iframetransport': {deps: ['jquery'], exports: 'iframetransport'},
        'fileupload': {deps: ['jquery', 'iframetransport'], exports: 'fileupload'},
        'jcrop': {deps: ['jquery'], exports: '$.Jcrop'},
        'fastclick': {deps: ['jquery'], exports: 'fastclick'},
        'underscore': {exports: '_'},
        'backbone': {deps: ['underscore', 'jquery'], exports: 'Backbone'},
        'BackboneViewAugmented': {deps: ['backbone']},
        'backbonenested': {deps: ['jquery', 'underscore', 'backbone'], exports: 'backbonenested'},
        'BackboneNestedModelAugmented': {deps: ['backbone', 'backbonenested']},
        'hbs': {deps: ['handlebars', 'underscore', 'i18nprecompile', 'jsonjs', 'i18n']},
        'xdate': {exports: 'XDate'},
        'dropin': {exports: 'Dropbox'},
        'dropboxSDK': {exports: 'Dropbox'},
        'typeahead': {deps: ['jquery', 'underscore'], exports: 'typeahead'},
        'latinise': {exports: 'latinise'},
        'oneDriveAPI': {exports: 'oneDriveAPI'},
        'ckeditor': {exports: 'CKEDITOR'}
//		,'touche'   : {exports: 'Touche'}
    }
};
window.reloadScripts = {};

/**
 * To detect errors that are not caught by local errbacks, override requirejs.onError():
 * 
 * 404, Not Found: nodefine
 * Network Timeout: timeout
 * Error in script: scripterror
 * 
 * Error object:
 *   requireType: A string value with a general classification, like "timeout", "nodefine", "scripterror".
 *   requireModules: an array of module names/URLs that timed out.
 *   
 *  javascript error types 
 *  EvalError: Raised when the eval() functions is used in an incorrect manner.
 *  RangeError: Raised when a numeric variable exceeds its allowed range.
 *  ReferenceError: Raised when an invalid reference is used.
 *  SyntaxError: Raised when a syntax error occurs while parsing JavaScript code.
 *  TypeError: Raised when the type of a variable is not as expected.
 *  URIError: Raised when the encodeURI() or decodeURI() functions are used in an incorrect manner.
 *  
 */
require.onError = function (err) {

    //In case of timeout: 
    //if (err.requireType === 'timeout') {
    var cause = (err === undefined && err === null) ? "unknown" : err.requireType;

    switch (cause) {
        case 'timeout':
        {
            try {
                var reload = window.localStorage.getItem("europass.ewa.reload");
                var reloadTimes = null;
                if (reload === undefined || reload === null) {
                    reloadTimes = 0;
                } else {
                    reloadTimes = parseInt(reload);
                }
                if (reloadTimes !== null && reloadTimes < 3) {
                    window.localStorage.setItem("europass.ewa.reload", (reloadTimes === null ? 0 : (reloadTimes + 1)));
                    //console.log("about to reload");
                    window.location.reload();
                } else if (reloadTimes !== null && reloadTimes >= 3) {
                    //We have already reloaded 3 times but nothing better happened.
                    //So remove the CV data if they exist as well
                    //console.log("three times reloaded");
                    window.localStorage.removeItem("europass.ewa.reload");
                    window.localStorage.removeItem("europass.ewa.skillspassport.v3");
                }

            } catch (err) {
                showError();
            }
            break;
        }
        case 'unknown':
        {
            break;
        }
        //in case of nodefine or scripterror
        default:
        {
            var errMessage = err.message;
            var errModules = err.requireModules;
            var modules = "";
            if (errModules !== undefined && errModules !== null) {
                for (var i = 0; i < errModules.length; i++) {
                    if (i > 0) {
                        modules = modules + "|";
                    }
                    modules = modules + errModules[i];

                    //consider re-requesting
                    //undefine and require
                    //attempt 3 times then stop
                    var failedId = errModules[i];
                    var prevAttempt = window.reloadScripts[ failedId ];
                    if (prevAttempt === undefined || prevAttempt === null) {
                        prevAttempt = 0;
                    }
                    var currAttempt = prevAttempt + 1;
                    window.reloadScripts[ failedId ] = currAttempt;
                    if (currAttempt > 5) {
                        window.reloadScripts[ failedId ] = undefined;

                        showError();

                        try {
                            delete window.reloadScripts[ failedId ];
                        } catch (e) {
                        }
                    } else { //try for 3 times
                        setTimeout(function () {
                            requirejs.undef(failedId);
                            require([failedId], function () {});
                            return true;
                        }, 2000);
                        return true;
                    }
                }
            }
            err.message = "[[" + cause + ":" + modules + "]]" + errMessage;
        }
    }

    throw err;

};
/************************************************************************/
/************************************************************************/
/************************************************************************/
//require.config( globalConfig )(
//	[	
//		'domReady', 
//		'fastclick',
//		'europass/europass',
//		'BackboneViewAugmented',
//		'BackboneNestedModelAugmented'
//	], 
//	function( domReady, fastclick, Europass ){
//		domReady( function () {
//			fastclick.attach(document.body);
//			Europass.initialize();
//		});
//	}
//);
//EWA- 978 
require.config(globalConfig);
require(
        ['BackboneViewAugmented', 'BackboneNestedModelAugmented', 'assembly/handlebarsHelpers'],
        function () {
            require(
                    ["app", "cookie"],
                    function (app, Cookies) {
                        app.init();
                        if ((Cookies.get('do-not-track') === "false" || !Cookies.get('do-not-track'))) {
                            var trackerEvent = new CustomEvent("load-do-not-track", {
                                detail: {
                                    locale: ewaLocale
                                }
                            });
                            document.body.dispatchEvent(trackerEvent);
                        }
                    });

        }
);

/************************************************************************/
/************************************************************************/
/************************************************************************/

function showError() {

    var errorEl = document.getElementById('page-loading-error');
    var graphicEl = document.getElementById('page-loading-waiting-indicator-graphic');
    if (errorEl !== undefined && errorEl !== null)
        errorEl.style.display = "block";
    if (graphicEl !== undefined && graphicEl !== null)
        graphicEl.style.display = "none";
}
