<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
    "http://www.w3.org/TR/html4/loose.dtd">
<html lang="en">
    <head>
        <meta charset="utf-8" />
        <title>Europass Editors Test</title>

        <script type="text/javascript">
            window.baseResourcesUrl = "${initParam['context.editors.resources']}";
            window.config = {
                api: "http://localhost:8080/editors/static/ewa/scripts",
                serverDateTime: '14/06/2013 10:00',
            };

            var JasmineSrc = "${pageContext.servletContext.contextPath}/${initParam['context.jasmine.src']}";
                var JasmineSpec = "${pageContext.servletContext.contextPath}/${initParam['context.jasmine.spec']}";
        </script>

        <script type="text/javascript" src="${initParam['context.editors.resources']}/libraries/require/require-2.1.11-min.js" ></script>
        <link rel="stylesheet" href="${initParam['context.jasmine.src']}/jasmine.css">
        <script type="text/javascript">
                    var librariesLoc = window.baseResourcesUrl + "/libraries";

                    require({

                        locale: "en",
                        baseUrl: window.baseResourcesUrl + "/scripts",
                        waitSeconds: 20,

                        paths: {

                            localization: window.baseResourcesUrl + "/localization",
                            libraries: librariesLoc,

                            backbone: librariesLoc + '/backbone/backbone-1.1.2-min',
                            backbonenested: librariesLoc + '/backbone-nested/backbone-nested-20140422-TRUNK-min',
                            cookie: librariesLoc + '/cookie/jquery.cookie-1.4.0-min',
                            fileupload: librariesLoc + '/fileupload/jquery.fileupload-9.5.2-min',
                            "jquery.ui.widget": librariesLoc + '/fileupload/jquery.ui.widget-1.10.4-amd-min',
                            iframetransport: librariesLoc + '/fileupload/jquery.iframe-transport-9.5.2-min',
                            handlebars: librariesLoc + '/require/plugins/hbs/handlebars-1.3.0-slexaxton-amd-min',
                            jcrop: librariesLoc + '/jcrop/jquery.Jcrop-0.9.12-min',
                            jquery: librariesLoc + '/jquery/jquery-1.11.0-min',
                            jqueryui: librariesLoc + '/jquery-ui/jquery-ui-1.10.4-min',
                            jsonjs: librariesLoc + '/jsonjs/json3-3.3.1-min',
                            jsonpath: librariesLoc + '/jsonpath/jsonpath-0.8.0',
                            redactor: librariesLoc + '/redactor/redactor-9.2.2-min',
                            rlanguages: librariesLoc + '/redactor/languages',
                            htmlsanitizer: librariesLoc + '/redactor/html-sanitizer',
                            Select2: librariesLoc + '/select2/select2-3.4.6-min',
                            underscore: librariesLoc + '/underscore/underscore-1.6.0-min',
                            xdate: librariesLoc + '/xdate/xdate-0.8-min',
                            domReady: librariesLoc + '/require/plugins/domReady-2.0.1',
                            hbar: librariesLoc + '/require/plugins/hbar-0.0.2',
                            hbs: librariesLoc + '/require/plugins/hbs/hbs-0.4.0',
                            i18nprecompile: librariesLoc + '/require/plugins/hbs/i18nprecompile-0.4.0',
                            i18n: librariesLoc + '/require/plugins/i18n-2.0.4',
                            text: librariesLoc + '/require/plugins/text-2.0.10',
                            Utils: 'europass/Utils',
                            HttpUtils: 'europass/http/HttpUtils',
                            HelperUtils: 'templates/HelperUtils',
                            Interactions: 'europass/Interactions',
                            ModalFormInteractions: 'europass/ModalFormInteractions',
                            BackboneViewAugmented: 'europass/backbone/BackboneViewAugmented',
                            BackboneNestedModelAugmented: 'europass/backbone/BackboneNestedModelAugmented',

                            //---jasmine
                            jasmine: JasmineSrc + '/jasmine',
                            'jasmine-html': JasmineSrc + '/jasmine-html',

                            // header template
                            spec: JasmineSpec,
                        },
                        shim: {
                            'jquery': {exports: '$'},
                            'jquery-ui': {deps: ['jquery'], exports: 'jqueryUI'},
                            'underscore': {exports: '_'},
                            'backbone': {deps: ['underscore', 'jquery'], exports: 'Backbone'},
                            'BackboneViewAugmented': {deps: ['backbone']},
                            'backbonenested': {deps: ['jquery', 'underscore', 'backbone'], exports: 'backbonenested'},
                            'BackboneNestedModelAugmented': {deps: ['backbone', 'backbonenested']},
                            'handlebars': {deps: ['underscore', 'jsonjs', 'i18n'], exports: 'handlebars'},
                            'xdate': {exports: 'XDate'},
                            'jasmine': {exports: 'jasmine'},
                            'jasmine-html': {deps: ['jasmine'], exports: 'jasmine'},
                        }
                    },
                            [

                                JasmineSpec + "/DateCheckTestSpec.js"
                                        , JasmineSpec + "/PageTitleViewSpec.js"
                                        , JasmineSpec + "/TranslationManagerTestSpec.js"
                                        , JasmineSpec + "/DateFormatTestSpec.js"
                            ],
                            function () {

                                htmlReporter = new jasmine.HtmlReporter();
                                jasmine.getEnv().addReporter(htmlReporter);
                                jasmine.getEnv().specFilter = function (spec) {
                                    return htmlReporter.specFilter(spec);
                                };
                                jasmine.getEnv().execute();
                            }
                    );

        </script>
    </head>

    <body>
    </body>

</html>