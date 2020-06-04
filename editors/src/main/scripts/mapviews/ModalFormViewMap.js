define(
        [
            'jquery',
            'underscore',
            'europass/structures/KeyObjMap',
            'europass/GlobalHelpOptionInstance',
            'mapviews/ModalFormViewMap',
            'models/SkillsPassportInstance',
            'views/forms/DefaultsFormView',
            'views/prefs/FormPrintingPreferencesView',
            'views/help/HelpView',
            'views/validation/ValidationFormView',
            'Utils'
        ],
        function (
                $,
                _,
                KeyObjMap,
                GlobalHelpOption,
                MODAL_FORM_VIEW_MAP,
                SkillsPassportInstance,
                DefaultsFormView,
                FormPrintingPreferencesView,
                HelpView,
                ValidationFormView,
                Utils) {

            if (MODAL_FORM_VIEW_MAP === undefined || MODAL_FORM_VIEW_MAP === null) {

                var MODAL_FORM_VIEW_MAP = new KeyObjMap();

                /**
                 * Instantiate the View that controls the behavior of placeholder texts in form fields
                 */
                MODAL_FORM_VIEW_MAP.instantiateDefaultsView = function (formEl, model, root, section) {
                    var defaultsFormView = new DefaultsFormView({
                        el: formEl.parent()
                    });
                    return defaultsFormView;
                };
                /**
                 * Instantiate the view that controls the storing of Printing Preferences
                 */
                MODAL_FORM_VIEW_MAP.instantiatePrefsView = function (formEl, model, section, index) {
                    var rootPrefs = "SkillsPassport.PrintingPreferences";
                    var sectionPrefs = "SkillsPassport.PrintingPreferences.ECV";

                    var printingPreferences = new FormPrintingPreferencesView({
                        el: formEl.parent(),
                        model: model,
                        root: rootPrefs,
                        section: sectionPrefs,
                        contentSection: section,
                        itemIndex: index
                    });
                    return printingPreferences;
                };
                /**
                 * Instantiate the view that controls the display of the context-sensitive help
                 */
                MODAL_FORM_VIEW_MAP.instantiateHelpView = function (formEl, model, root, section, helpSection) {
                    var helpView = new HelpView({
                        el: formEl.parent(),
                        root: root,
                        section: section,
                        helpSection: helpSection
                    });
                    return helpView;
                };

                /**
                 * Instantiate the view that validates the dates
                 */
                MODAL_FORM_VIEW_MAP.instantiateValidationView = function (formEl, model, section, validationField) {
                    //call newInstance with a callback, scope and args...
                    var validationView = new ValidationFormView({
                        el: formEl.parent(),
                        model: model,
                        section: section,
                        validation: validationField
                    });
                    return validationView;
                };
                /**
                 * Instantiate the view that controls the storing of the form field values
                 */
                MODAL_FORM_VIEW_MAP.instantiateFormView = function (formEl, model, root, section, doc, clazz, callback, scope, args) {
                    var formViewName = clazz;
                    if (clazz === undefined || clazz === null || clazz === "") {
                        formViewName = "FormView";
                    }

                    var docDir = "";
                    var bundlePath = "assembly/ecvForms"; //default

                    if (_.isString(doc) && doc !== "") {
                        docDir = doc.toLowerCase();
                        var bundle = "";
                        switch (docDir) {
                            case "attachment":
                            {
                                bundle = "esp";
                                break;
                            }
                            default:
                            {
                                bundle = (docDir.indexOf("e") === 0 ? docDir : "e" + docDir);
                                break;
                            }
                        }
                        bundlePath = "assembly/" + bundle + "Forms";
                    }
                    if (docDir !== "")
                        docDir = docDir + "/";

                    require(
                            [bundlePath],
                            function () {

                                var reqName = "views/forms/" + docDir + formViewName;

                                //call newInstance with a callback, scope and args...
                                Utils.newFormInstance({
                                    _className: formViewName,
                                    _requireName: reqName,
                                    el: formEl.parent(),
                                    model: model,
                                    root: root,
                                    section: section
                                },
                                        MODAL_FORM_VIEW_MAP.instantiateFormViewCallback,
                                        MODAL_FORM_VIEW_MAP,
                                        [callback, scope, args]);
                            }
                    );
                };


                /**
                 * Callback that runs after the formView is loaded and instantiated.
                 * 
                 * Here callback is MODAL_FORM_VIEW_MAP.instantiateViewsCallback
                 */
                MODAL_FORM_VIEW_MAP.instantiateFormViewCallback = function (callback, scope, args, formView) {
                    if ($.isArray(args)) {
                        args.push(formView);
                    } else {
                        args = [formView];
                    }
                    callback.apply(scope, args);
                };

                /**
                 * Instantiate the necessary views bound to every Modal Form that adds data to the model
                 */
                MODAL_FORM_VIEW_MAP.instantiateViews = function (formId, info, callback, scope, args) {
                    var model = SkillsPassportInstance;
                    var root = "SkillsPassport";
                    var formEl = $(Utils.jId(formId));

                    var section = info.section;

                    var views = {
                        validation: MODAL_FORM_VIEW_MAP.instantiateValidationView(formEl, model, section, info.validationField),
                        defaults: MODAL_FORM_VIEW_MAP.instantiateDefaultsView(formEl, model, root, section),
                        prefs: MODAL_FORM_VIEW_MAP.instantiatePrefsView(formEl, model, section, info.index),
                        help: MODAL_FORM_VIEW_MAP.instantiateHelpView(formEl, model, root, section, info.helpSection)
                    };

                    //instantiate form view with callback and let the callback add it to the views map...
                    MODAL_FORM_VIEW_MAP.instantiateFormView(
                            formEl, model, root, section, info.doc, info.clazz,
                            MODAL_FORM_VIEW_MAP.instantiateViewsCallback,
                            MODAL_FORM_VIEW_MAP,
                            [formId, views, callback, scope, args]);
                };
                /**
                 * Callback of views instantiation, runs after the formView is loaded.
                 * Puts the form to the supplied views map
                 * and calls the supplied callback at the defined scope.
                 * 
                 * Here callback is MODAL_FORM_VIEW_MAP.renderFormCallback
                 */
                MODAL_FORM_VIEW_MAP.instantiateViewsCallback = function (formId, views, callback, scope, args, form) {

                    //1.
                    views.form = form;

                    //2.
                    MODAL_FORM_VIEW_MAP.put(formId, views);

                    if ($.isArray(args)) {
                        args.push(views);
                    } else {
                        args = [views];
                    }
                    //3.
                    callback.apply(scope, args);
                };
                /**
                 * Render the necessary views bound to every Modal Form that adds data to the model
                 * If not already instantiate them and put them in an array.
                 */
                MODAL_FORM_VIEW_MAP.renderForm = function (info, model, callback, scope, args) {
                    var formId = info.form;

                    //Check if the various views are created.
                    var views = MODAL_FORM_VIEW_MAP.get(formId);
                    if (views === null) {
                        //instantiate with a callback!
                        MODAL_FORM_VIEW_MAP.instantiateViews(
                                formId,
                                info,
                                MODAL_FORM_VIEW_MAP.renderFormCallback,
                                MODAL_FORM_VIEW_MAP,
                                [info, model, callback, scope, args]);
                    } else {
                        MODAL_FORM_VIEW_MAP.renderFormCallback(info, model, callback, scope, args, views);
                    }
                };
                /**
                 * Callback after the form rendering is done
                 */
                MODAL_FORM_VIEW_MAP.renderFormCallback = function (info, model, callback, scope, args, views) {

                    var form = views.form;
                    //THE ORDER IS IMPORTANT - The Defaults View modified the HTML of the Form, so the Form must be rendered first.
                    //Initial rendering of the Form View
                    //0 - Printing Preferences
                    views.prefs.render();
                    //1 - Form
                    form.setOrigin(info.origin === undefined ? "click-origin-compose" : info.origin);
                    form.setPersonalDataTreatment(info.achievementPersonalDataTreatment === undefined ? false : info.achievementPersonalDataTreatment);
                    form.render(info.index, info.subsection);
                    //2 - Default Values
                    views.defaults.render();
                    //3 - Enable Functionalities (autocomplete / multifields)
                    //The Autocomplete should go first, as it modifies the HTML as well (adding the expansion button).
                    //The MultiField should go then, as it pre-computes the source element to be used for multiplying, so the source has to be ready.
                    form.enableFunctionalities();

                    //4 - Render the Help View 
                    views.help.render();

                    if (callback !== undefined && $.isFunction(callback)) {
                        callback.apply(scope, args);
                    }
                };

                //======= SIMPLE FORM - WITH ONLY FORM AND HELP VIEWS, E.G. UPLOAD CV FORM - NO PRINTNG PREFERENCES, NO DEFAULT VALUES ============
                /**
                 * Simple Form comprised by the Modal Form and the Help
                 */
                MODAL_FORM_VIEW_MAP.instantiateSimpleView = function (formId, info, callback, scope, args) {
                    var model = SkillsPassportInstance;
                    var root = "SkillsPassport";
                    var formEl = $(Utils.jId(formId));

                    var section = info.section;

                    var views = {
                        help: MODAL_FORM_VIEW_MAP.instantiateHelpView(formEl, model, root, section)
                    };

                    MODAL_FORM_VIEW_MAP.instantiateFormView(
                            formEl, model, root, section, info.doc, info.clazz,
                            MODAL_FORM_VIEW_MAP.instantiateSimpleViewCallback,
                            MODAL_FORM_VIEW_MAP,
                            [formId, views, callback, scope, args]);

                };
                /**
                 * Callback for simple View instantiation
                 * 
                 * Here callback is MODAL_FORM_VIEW_MAP.renderSimpleFormCallback
                 */
                MODAL_FORM_VIEW_MAP.instantiateSimpleViewCallback = function (formId, views, callback, scope, args, form) {
                    //1.
                    views.form = form;

                    //2.
                    MODAL_FORM_VIEW_MAP.put(formId, views);

                    if ($.isArray(args)) {
                        args.push(views);
                    } else {
                        args = [views];
                    }
                    //3.
                    callback.apply(scope, args);
                };
                /**
                 * Render the necessary views bound to every Modal Form that adds data to the model
                 * If not already instantiate them and put them in an array.
                 */
                MODAL_FORM_VIEW_MAP.renderSimpleForm = function (info, model, callback, scope, args) {
                    var formId = info.form;

                    //Check if the various views are created.
                    var views = MODAL_FORM_VIEW_MAP.get(formId);
                    if (views === null) {
                        //instantiate with a callback!
                        MODAL_FORM_VIEW_MAP.instantiateSimpleView(
                                formId,
                                info,
                                MODAL_FORM_VIEW_MAP.renderSimpleFormCallback,
                                MODAL_FORM_VIEW_MAP,
                                [info, model, callback, scope, args]);
                    } else {
                        MODAL_FORM_VIEW_MAP.renderSimpleFormCallback(info, model, callback, scope, args, views);
                    }
                };
                /**
                 * Callback for form simple re-rendering
                 */
                MODAL_FORM_VIEW_MAP.renderSimpleFormCallback = function (info, model, callback, scope, args, views) {

                    var form = views.form;
                    //THE ORDER IS IMPORTANT - The Defaults View modified the HTML of the Form, so the Form must be rendered first.
                    //1 - Form
                    form.render(info.index, info.subsection);
                    //2 - Enable Functionalities (autocomplete / multifields)
                    form.enableFunctionalities();
                    //3 - Render the Help View if Help is ON
                    if (GlobalHelpOption.isOn())
                        views.help.render(info.helpSection);

                    if (callback !== undefined && $.isFunction(callback)) {
                        callback.apply(scope, args);
                    }
                };
            }
            return MODAL_FORM_VIEW_MAP;
        }
);