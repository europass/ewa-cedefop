/**
 * data-bind-name: The Name used for the binding is considered to be the name of
 * the input field. However if there is a "data-bind-name" attribute, then this
 * value is used instead. data-bind-group: This attribute specifies that the
 * specific input field belongs to a preference group. This means that there are
 * other input fields with the same data-bind-group, and if at least one is set,
 * then the entire group must be shown. In the same spirit, if this group is
 * selected to not be shown, all fields belonging to it should not be shown
 * also.
 * 
 * If ( data-bind-group AND NOT(data-bind-name) ) //e.g. Address, Birthdate
 * parts Then bind to group If ( data-bind-group AND data-bind-name ) //e.g.
 * Telephone[i] Then bind to group as well as to individual using data-bind-name
 * Else //only pref class Bind to individual using name
 * 
 * Input Fields: Text, Hidden, TextArea, Radio, Select, AutocompletDropDown,
 * Checkbox Hidden fields do not fire preference-binding related events.
 * Checkboxes not handled yet. Preference-binding related events are handled on
 * Field Blur.
 */
define(
        [
            'jquery', 'underscore', 'backbone', 'Utils',
            'europass/GlobalDocumentInstance',
            'models/PrintingPreferencesModel',
            'views/prefs/PrintingPreferencesView',
//		EWA-1811
//		'europass/maps/AddressFormatMap',
            'i18n!localization/nls/DateFormat',
//		EWA-1811
//		'i18n!localization/nls/OrganisationAddressFormat',
            'i18n!localization/nls/DefaultDateFormat',
            'europass/ExperienceDateFormatInstance',

            'models/TranslationManager'
        ],
        function ($, _, Backbone, Utils, GlobalDocument,
                PrintingPreferencesModel, PrintingPreferencesView,
//				EWA-1811
//				AddressFormatMap,
                DateFormat,
//				EWA-1811
//				OrganisationAddress,
                DefaultDateFormat, ExperienceDate, TranslationManager) {
            var PrintingPreferencesFormView = PrintingPreferencesView
                    .extend({
                        // el will be the form
                        prefix: "ECV",
                        root: null,
                        section: null,
                        events: {

                            "europass:form:valid": "submitted",
                            "europass:check:date:format:changes": "checkDateFormatChanges",
                            "click :button.cancel": "cancelled",

                            // ADDRESS FORMAT based on postal COUNTRY CODE for
                            // input with name that ENDS IN
                            // Address.Contact.Country.Code and update the Model
                            // "europass:autocomplete:set:code
                            // fieldset.address:not(.organisation-address)
                            // :input:not(:button)[name$=\"Address.Contact.Country.Code\"]"
                            // : "updatePostalAddressFormat",
                            // ADDRESS FORMAT for any other Address field
                            // "change
                            // fieldset.address:not(.organisation-address)
                            // :input:not(button).Address" :
                            // "managePostalAddressFormat",

                            // ADDRESS FORMAT based on organisational COUNTRY
                            // CODE for input with name that ENDS IN
                            // Address.Contact.Country.Code and update the Model
                            // "europass:autocomplete:set:code
                            // fieldset.organisation-address
                            // :input:not(:button)[name$=\"Address.Contact.Country.Code\"]"
                            // : "manageOrganisationAddressFormat",
                            // ADDRESS FORMAT for any other ORGANISATION Address
                            // field
                            // "change fieldset.organisation-address
                            // :input:not(button).Address" :
                            // "manageOrganisationAddressFormat",

                            // DATE FORMAT simple - WITHOUT updating the GLOBAL
                            // DATES (CV.Birthdate and CL.LocalisationDate)
                            // a: Date parts (yyyy, mm, dd)
                            "change :input:not(:button).pref.Date.format": "manageFormat",
                            "change.fs :input:not(:button).pref.Date.format": "manageFormat",
                            // b: Date format control
                            "change :input:not(:button).PrintingPreferences.Date.format": "updateFormat",
                            "changed:dateFormatSelect": "updateFormat",
                            // GLOBAL DATE FORMAT
                            // a: Date parts (yyyy, mm, dd, ongoing)
                            "change :input:not(:button).pref.Period.global-format": "managePeriodDateFormat",
                            "change.fs :input:not(:button).pref.Period.global-format": "managePeriodDateFormat",

                            // GLOBAL DATE FORMAT For Certificate
                            "change :input:not(:button).pref.Certificate.Date.global-format": "manageDateFormatAsPeriod",
                            "change.fs :input:not(:button).pref.Certificate.Date.global-format": "manageDateFormatAsPeriod",
                            // b: Date format control for Experience.Period +
                            // Certificate.Date
                            "change :input:not(:button).PrintingPreferences.global-format": "manageGlobalFormat",
                            "change.fs :input:not(:button).PrintingPreferences.global-format": "manageGlobalFormat",
                            "changed:datePeriodSelect": "manageGlobalFormat",
                            // PERSON NAMES ORDER
                            "change :input:not(:button)[name^=\"LearnerInfo.Identification.PersonName\"]": "setPersonNameOrder",
                            "change.fs :input:not(:button)[name^=\"LearnerInfo.Identification.PersonName\"]": "setPersonNameOrder"

                        },
                        initialize: function (options) {
                            this.root = options.root;
                            this.section = options.section;
                            this.contentSection = options.contentSection;
                            this.itemIndex = options.itemIndex;

                            this.emptyJSON = {};
                            this.emptyJSON[this.section] = {};

                            this.prefsDocument = GlobalDocument
                                    .getPrefDocument();
                        },
                        render: function () {
                            // clone does not
                            // work...this.model.preferences.clone();
                            this.prefModel = new PrintingPreferencesModel($
                                    .extend(true, {}, this.model
                                            .getPreferences()));
                            this.liveModel = this.model.preferences;
                        },
                        isEmpty: function (v) {
                            var value = _.isString(v) ? $.trim(v) : null;
                            if (value === null)
                                return true;
                            else
                                return value.length === 0;
                        },
                        manageForeignLanguage: function (input) {
                            // console.log("manageForeignLanguage...");
                            // LP add another language or edit an existing one
                            if (input
                                    .closest("fieldset.ForeignLanguage.ELP.OneLanguage").length > 0) {
                                this.addForeignLangDefaults(input, "ECV");
                            }
                            // CV add another language or edit an existing one
                            else if (input
                                    .closest("fieldset.ForeignLanguage.ECV.OneLanguage").length > 0) {
                                this.addForeignLangDefaults(input, "ELP");
                            }
                        },
                        addForeignLangDefaults: function (input, documentName,
                                outlineStatus) {
                            // console.log("manageElpForeignLanguage...");
                            // but also add default preferences for the items of
                            // the language
                            var sectionName = input.attr("rel");
                            this.addDefaultPrefsForSection(sectionName,
                                    documentName, outlineStatus);
                        }
                        /**
                         * Update an already existing preference (or add one and
                         * then update it) with the specific format attribute
                         */
                        ,
                        updateFormat: function (event) {
                            // console.log("updateFormat");
                            this.datePreferenceUpdated = true;
                            var input = $(event.target);
                            this.updatePreferenceForInput(input, "format");
                        },
                        manageFormat: function (event) {
                            // console.log("manageFormat");
                            var input = $(event.target);
                            var bindGroups = input.attr(this.bindGroup_attr);
                            if (bindGroups !== undefined && bindGroups !== null
                                    && bindGroups !== "") {
                                var bindGroupsArray = $(bindGroups.split(" "));
                                var bindGroup = bindGroupsArray[0]; // get the
                                // first

                                var existingPref = this
                                        .findPreference(bindGroup);
                                if ((_.isEmpty(existingPref))
                                        || (!_.isEmpty(existingPref) && _
                                                .isEmpty(existingPref.format))) {
                                    this
                                            .updatePreference(
                                                    name,
                                                    "format",
                                                    DefaultDateFormat[this.prefsDocument]);
                                }
                            }
                        }
                        /**
                         * Update the format of the Global object, as well as
                         * update the format of the current and all relevant
                         * Preferences, when the DATE FORMAT Select changes. Use
                         * europass/ExperienceDateFormatInstance
                         */
                        ,
                        manageGlobalFormat: function (event) {
                            var that = this;
                            var input = $(event.target);
                            // select field - no need to use trim
                            var format = input.val();
                            var name = this.getInputPrefName(input);

                            var otherPrefs = ExperienceDate.applyFunction({
                                f: "list"
                            }, this.prefsDocument);
                            if (_.isArray(otherPrefs)) {
                                for (var i = 0; otherPrefs !== null
                                        && i < otherPrefs.length; i++) {
                                    var other = otherPrefs[i];
                                    if (other !== name) {
                                        that.updatePreference(other, "format",
                                                format);
                                    }
                                }
                                ;
                            }

                            // Finally set the new one
                            ExperienceDate.applyFunction({
                                f: "set",
                                args: [
                                    Utils.addPrefix(this.prefsDocument,
                                            name), format]
                            }, this.prefsDocument);
                            this.updatePreference(name, "format", format);

                            this.globalDateUpdated = true;

                            this.manageForeignLanguage(input);

                        },
                        manageDateFormatAsPeriod: function (event) {
                            // console.log("manageDateFormatAsPeriod");
                            var input = $(event.target);
                            var bindName = input.attr(this.bindName_attr);
                            if (bindName !== undefined && bindName !== null
                                    && bindName !== "") {
                                this._manageGlobalDateFormat(bindName);
                            }
                            this.manageForeignLanguage(input);
                        }
                        /**
                         * Update the pref.format for the experience period if
                         * not already existing or set to the default of the
                         * ExperienceDate, when ANY OF THE DATE PARTS SELECTS
                         * changes
                         */
                        ,
                        managePeriodDateFormat: function (event) {
                            // console.log("managePeriodDateFormat");
                            var input = $(event.target);
                            if (input.is("checkbox") && !input.is(":checked")) {
                                return;
                            }
                            var bindGroups = input.attr(this.bindGroup_attr);
                            if (bindGroups !== undefined && bindGroups !== null
                                    && bindGroups !== "") {
                                var bindGroupsArray = $(bindGroups.split(" "));
                                var bindGroup = bindGroupsArray[0]; // get the
                                // first

                                this._manageGlobalDateFormat(bindGroup);
                            }
                            this.manageForeignLanguage(input);
                        },
                        _manageGlobalDateFormat: function (name) {
                            // console.log("_manageGlobalDateFormat");
                            // add to list of prefs with date format
                            ExperienceDate.applyFunction({
                                f: "addToList",
                                args: [Utils.addPrefix(this.prefsDocument,
                                            name)]
                            }, this.prefsDocument);

                            var existingPref = this.findPreference(name);
                            if (Utils.isEmptyObject(existingPref)) {
                                return false;
                            }
                            var existingFormat = existingPref.format;
                            var globalDate = ExperienceDate.applyFunction({
                                f: "get"
                            }, this.prefsDocument);
                            if (existingFormat == null
                                    || (existingFormat !== null && existingFormat !== globalDate)) {
                                this.updatePreference(name, "format",
                                        globalDate);
                            }
                        }
                        /**
                         * Update an already existing preference (or add one and
                         * then update it) with the format attribute being the
                         * specific address format according to the country code
                         */
                        /*
                         * ,updatePostalAddressFormat: function( event , format ){ //
                         * console.log("updatePostalAddressFormat"); var input =
                         * $(event.target); var value = input.val(); var
                         * bindGroup = input.attr( this.bindGroup_attr ); var
                         * defaultFormat = AddressFormatMap.get( "default" );
                         * var format = _.isEmpty(defaultFormat) ? null :
                         * defaultFormat.format; if ( value != ""){ var config =
                         * AddressFormatMap.get( value ); if ( config !==
                         * undefined && config !== null &&
                         * !$.isEmptyObject(config) ){ format = config.format; } }
                         * this.updatePreference( bindGroup, "format", format ); }
                         */
                        /**
                         * Manage address format after interacting with any of
                         * the Address fields
                         */
                        /*
                         * ,managePostalAddressFormat: function( event ){ //
                         * console.log("managePostalAddressFormat"); var
                         * defaultFormat = AddressFormatMap.get( "default" );
                         * var format = _.isEmpty(defaultFormat) ? null :
                         * defaultFormat.format; this._manageAddressFormat(
                         * event, format ); }
                         */
                        /**
                         * Add a preference (if it does not already exist) with
                         * the format attribute taken from the respective global
                         * object, when interacting with any of the organisation
                         * address fields
                         */
                        /*
                         * ,manageOrganisationAddressFormat: function( event ){ //
                         * console.log("manageOrganisationAddressFormat");
                         * this._manageAddressFormat( event,
                         * OrganisationAddress.format ); }
                         * ,_manageAddressFormat: function( event, defaultFormat ){ //
                         * console.log("_manageAddressFormat"); var input =
                         * $(event.target); var bindGroup = input.attr(
                         * this.bindGroup_attr );
                         * 
                         * var existingPref = this.findPreference( bindGroup );
                         * if ( ( _.isEmpty( existingPref ) ) || ( !_.isEmpty(
                         * existingPref ) && _.isEmpty( existingPref.format ) ) ){
                         * this.updatePreference( bindGroup, "format",
                         * defaultFormat ); } }
                         */

                        /**
                         * Update an already existing preference (or add one and
                         * then update it) w ith the before attribute being
                         * accordingly set according to the firstNameBefore
                         * value per language
                         */
                        ,
                        setPersonNameOrder: function (event) {
                            // console.log("setPersonNameOrder");
                            var input = $(event.target);
                            var v = input.val();
                            var value = _.isString(v) ? $.trim(v) : null;
                            if (value !== null && value.length > 0) {
                                var name = this.PERSONNAME_KEY;

                                var existingPref = this.findPreference(name);

                                // Proceed only when the preference does not
                                // exist,
                                var doesNotExist = (existingPref === undefined || existingPref === null);
                                // or when the preference exists but there is no
                                // 'order' value
                                var noOrder = (doesNotExist === false && (existingPref.order === undefined
                                        || existingPref.order === null || existingPref.order === ""));

                                if (doesNotExist || noOrder) {
                                    this.managePersonNameOrder(
                                            this.prefsDocument, doesNotExist,
                                            noOrder);
                                }

                            }
                        }

                        /**
                         * Recursive function used before submitting, used when
                         * dealing with List items. The particularity of this
                         * case is that the new item does not contain already
                         * all possible default prefs. Therefore before
                         * submitting we iterate the default prefs for the
                         * index-0, and add those that are missing for the
                         * specific indexed item.
                         * 
                         * @param defaultObj
                         *            the default prefs object to iterate
                         * @param obj
                         *            the live prefs object
                         * @param showValue,
                         *            boolean: when true indicates that the
                         *            preference should be set to show true,
                         *            when false indicates that the preference
                         *            should be set to show false, when left
                         *            null or undefined indicates that the
                         *            preferences should be set according to the
                         *            default value.
                         * @param update
                         */
                        ,
                        addDefaultPreferences: function (defaultObj, obj,
                                showValue, update) {
                            if (Utils.isEmptyObject(defaultObj) === true) {
                                return;
                            }
                            // console.log("start for defaultObj:
                            // "+JSON.stringify( defaultObj ) );
                            // console.log("start for Obj: "+JSON.stringify( obj
                            // ) );
                            for (var key in defaultObj) {
                                // // console.log("add default preference for
                                // key: "+key);
                                if (key === "array") {
                                    // // console.log("Case array");
                                    var isArrayObj = _.isArray(obj.array);
                                    if (isArrayObj === false) {
                                        obj.array = [];
                                    }
                                    var arrItems = obj.array.length === 0 ? 1
                                            : obj.array.length;
                                    var value = defaultObj.array[0];

                                    for (var i = 0; i < arrItems; i++) {
                                        // // console.log("array item "+i);
                                        var liveValue = obj.array[i];
                                        // // console.log("array defaultObj:
                                        // "+JSON.stringify( value ) );
                                        // // console.log("array Obj:
                                        // "+JSON.stringify( liveValue ) );
                                        if (typeof value === "object") {

                                            obj.array = this.addDefaultPref(
                                                    obj.array, i, value,
                                                    liveValue, showValue,
                                                    update);

                                            obj.array[i] = this
                                                    .addDefaultPreferences(
                                                            value,
                                                            obj.array[i],
                                                            showValue, update);
                                        }
                                    }
                                } else {

                                    var value = defaultObj[key];
                                    // console.log("value: "+JSON.stringify(
                                    // value ) );
                                    // console.log("before Obj:
                                    // "+JSON.stringify( obj ) );
                                    if (obj === undefined || obj === null) {
                                        obj = {};
                                    }

                                    var liveValue = obj[key];

                                    if (typeof value === "object") {
                                        if (value.format !== undefined
                                                && TranslationManager.DateFormatPrefs_Regex
                                                .test(this.contentSection)
                                                && !_
                                                .isEmpty(DateFormat.patterns[value.format])) {
                                            value.format = ExperienceDate
                                                    .applyFunction({
                                                        f: "get"
                                                    }, this.prefsDocument);
                                            // Add to list
                                            var periodPrefName = Utils
                                                    .addPrefix(
                                                            this.prefsDocument,
                                                            Utils
                                                            .removeSkillsPassportPrefix(this.contentSection));
                                            // console.log("Add to list of
                                            // global prefs: " + periodPrefName
                                            // );
                                            ExperienceDate.applyFunction({
                                                f: "addToList",
                                                args: [periodPrefName]
                                            }, this.prefsDocument);
                                        }
                                        obj = this.addDefaultPref(obj, key,
                                                value, liveValue, showValue,
                                                update);

                                        obj[key] = this.addDefaultPreferences(
                                                value, obj[key], showValue,
                                                update);
                                    }
                                }

                            }
                            // console.log("updated Obj: "+JSON.stringify( obj )
                            // );
                            return obj;
                        },
                        addDefaultPref: function (obj, key, value, liveValue,
                                showValue, update) {
                            // console.log("Default for key: '"+key+"' to be");
                            if (value !== null && typeof value === "object") {
                                var nonExisting = (Utils
                                        .isEmptyObject(liveValue) === true);
                                if (update === true || nonExisting === true) {

                                    if (_.isUndefined(obj[key])
                                            || _.isNull(obj[key])) {
                                        obj[key] = {};
                                    }
                                    if (!_.isEmpty(showValue))
                                        obj[key].show = showValue;
                                    else if (!_.isEmpty(value.show))
                                        obj[key].show = value.show;

                                    if (!_.isEmpty(value.format))
                                        obj[key].format = value.format;
                                    if (!_.isEmpty(value.order))
                                        obj[key].order = value.order;
                                }
                            }
                            return obj;
                        }

                        /**
                         * Some items when stored, need to update other
                         * preferences besides those handled by the form field.
                         * The exact way to update those depends on the current
                         * Outline Status. Also if the related section is a list
                         * item, special handling is necesasary.
                         * 
                         * This takes place when submitting the form and also in
                         * the LP Overview, when adding foreign languages,
                         * through the mutlipliable field.
                         * 
                         * @param prefsSection
                         * @param documentName:
                         *            This is the preferred document name. If
                         *            left undefined the "this.prefsDocument"
                         *            will be used
                         * @param update:
                         *            Update the printing preference based on
                         *            the defaults, even when already set
                         * @return void; modifies the current prefModel before
                         *         storing to the live model
                         */
                        ,
                        addDefaultPrefsForSection: function (prefsSection,
                                documentName, update) {
                            if (prefsSection === undefined
                                    || prefsSection === null
                                    || prefsSection === "") {
                                return false;
                            }

                            var doc = (documentName === undefined) ? this.prefsDocument
                                    : documentName;

                            var relatedSection = Utils.addPrefix(doc, Utils
                                    .removeSkillsPassportPrefix(prefsSection));
                            var isListItem = prefsSection.match(/(\[\d+\])+$/g); // ends
                            // with
                            // [\d+]
                            var prefPath = relatedSection;

                            if (isListItem !== null) {
                                prefPath = Utils.toArrayTxt(relatedSection);
                            }

                            if (this.prefModel === null) {
                                return;
                            }

                            // If a preference included in the defaults, set its
                            // show accordingly
                            // Iterate the preferences so far
                            var currentPrefItem = this.prefModel.get(prefPath);
                            var defaultPrefItem = this.prefModel
                                    .getDefaultsModel().get(
                                    Utils.toZeroIndexTxt(prefPath));
                            // true stands for update if exists
                            currentPrefItem = this.addDefaultPreferences(
                                    defaultPrefItem, currentPrefItem, null,
                                    update === true ? true : false);
                            this.prefModel.set(prefPath, currentPrefItem);

                            // console.log("=== MANAGE DEFAULTs:::PREF MODEL ===
                            // \n"+JSON.stringify(this.prefModel.attributes[this.prefsDocument])
                            // );

                        }
                        /**
                         * Respond to the modal form submit event. First get the
                         * status from the Outline. Then continue will the usual
                         * setting of the Printing Preferences
                         */
                        ,
                        submitted: function (event) {
                            // to start the waiting indicator....
                            this.$el.trigger("europass:waiting:indicator:show");
                            // console.log("=== SUBMIT:::PREF MODEL ===
                            // \n"+JSON.stringify(this.prefModel.attributes.ELP)
                            // );
                            // handle default preferences
                            // Do not update if already set
                            this.addDefaultPrefsForSection(this.contentSection,
                                    this.prefsDocument, false);
                            // console.log("=== SUBMIT:::DEFAULT-PREFS:::PREF
                            // MODEL ===
                            // \n"+JSON.stringify(this.prefModel.attributes.ELP)
                            // );
                            if (this.section !== undefined
                                    && this.section !== null
                                    && this.prefModel !== undefined
                                    && this.prefModel !== null) {

                                this.liveModel.set(this.prefModel.attributes, {
                                    silent: true
                                });

                                // Reset PrefModel
                                this.resetPrefModel();
                            }
                            // Global Date Format changed?
                            var globalDateFormatUpdated = false;
                            if (this.globalDateUpdated === true) {
                                this.globalDateUpdated = false;
                                globalDateFormatUpdated = true;
                            }
                            // TRIGGER An event to be caught be the next in the
                            // submit bubble;
                            var btn = $(event.target);
                            btn.trigger("europass:form:prefs_changed",
                                    [globalDateFormatUpdated]);
                        }

                        , checkDateFormatChanges: function() {
                            var dateFormatUpdated = (_.isUndefined(this.globalDateUpdated)? false: this.globalDateUpdated) || 
                                    (_.isUndefined(this.datePreferenceUpdated)? false: this.datePreferenceUpdated);
                            
                            $(event.target).trigger("europass:modal:check:changes", [dateFormatUpdated]);
                            this.datePreferenceUpdated = false;
                        }
                        
                        /**
                         * Reset the internal model.
                         */
                        ,
                        cancelled: function () {
                            this.resetPrefModel();
                        }
                        /**
                         * Reset the internal model.
                         */
                        ,
                        resetPrefModel: function () {
                            if (this.prefModel !== undefined
                                    && this.prefModel !== null) {
                                // Deprecated this.prefModel.unbindEvents();
                                this.prefModel.clear();
                                delete this.prefModel;
                                this.prefModel = null;
                            }
                        }
                        /**
                         * Update the prefenece according to the input's value
                         */
                        ,
                        updatePreferenceForInput: function (input, attribute) {
                            var value = input.val();
                            console.log('value', value);
                            var name = this.getInputPrefName(input);
                            this.updatePreference(name, attribute, value);
                        }

                    });
            return PrintingPreferencesFormView;
        });
