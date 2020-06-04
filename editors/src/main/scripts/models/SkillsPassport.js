define(
        [
            'require',
            'module',
            'jquery',
            'underscore',
            'xdate',
            'backbone',
            'backbonenested',
            'models/PrintingPreferencesModel',
            'Utils',
            'europass/http/SessionManagerInstance',
            'europass/ExperienceDateFormatInstance',
            'europass/GlobalDocumentInstance',
            'europass/http/WindowConfigInstance',
            'models/ModelInfoManager',
            'models/TranslationManager',
            'models/ConversionManager',
            'models/DocumentationManager'
        ],
        function (require, module, $, _, XDate,
                Backbone, BackboneNested, PrintingPreferencesModel,
                Utils, SessionManager, GlobalDateFormat, GlobalDocument, Config,
                ModelInfoManager,
                TranslationManager,
                ConversionManager,
                DocumentationManager) {

            var SkillsPassport = Backbone.NestedModel.extend({

                SECTION_ECV_PREFS: "SkillsPassport.PrintingPreferences",

                ANNEXES_SECTION: "SkillsPassport.LearnerInfo.ReferenceTo",

                REFERENCETO: ".ReferenceTo",

                ATTACHMENT_SECTION: "SkillsPassport.Attachment",

                ATTACHMENT_SECTION_MIN: "Attachment",

                SIGNATURE: "SkillsPassport.LearnerInfo.Identification.Signature",

                PHOTO: "SkillsPassport.LearnerInfo.Identification.Photo",

                DOCUMENT_TYPE: "SkillsPassport.DocumentInfo.DocumentType",

                ISO_DATE_FORMAT: "yyyy-MM-dd'T'HH:mm:ss.fffzzz",

                /**
                 * The Defaults initializes the model to what it should
                 * be when empty. The items that are missing refer to
                 * list items, such as work experiences or education.
                 * When adding such an item, the default prefs are taken
                 * from the defaultItemPrefs.
                 */
                defaults: {
                    SkillsPassport: {
                        Locale: module.config().locale || "en",
                        PrintingPreferences: {},
                        DocumentInfo: {
                            DocumentType: "ECV",
                            CreationDate: null,
                            LastUpdateDate: null,
                            XSDVersion: module.config().xsdversion,
                            Generator: module.config().generator,
                            Comment: module.config().comment,
                            EuropassLogo: module.config().europassLogo
                        },
                        LearnerInfo: {},
                        CoverLetter: {}
                    }
                },
                /*****************************************************************************************/
                isUIandModelLocaleSame: function () {
                    var locale = this.get("SkillsPassport.Locale");

                    if (locale === undefined || locale === null || locale === "") {
                        return false;
                    }
                    var uiLocale = module.config().locale;
                    if (uiLocale === undefined || uiLocale === null || uiLocale === "") {
                        return false;
                    }
                    return uiLocale === locale;
                },
                /**
                 * Orchestrate the necessary steps for preparing the model, when a previous model is found (on local storage, session, etc)
                 * 
                 * @param considerUILocale: boolean to indicate that performing translation should consider active UI locale vs Model Locale
                 */
                populateModel: function (prevModel, considerUILocale) {

                    if (prevModel !== undefined && prevModel !== null) {
                        var conversionInfo = this.conversion();
                        if (conversionInfo !== null) {
                            conversionInfo.fromStorable(prevModel);

                            var translationInfo = this.translation();
                            if (translationInfo !== null) {
//						console.log("Consider locale ? " + considerUILocale );
//						console.log("Same locale ? " + this.isUIandModelLocaleSame() );
                                var avoidFullTranslation = (considerUILocale === true && this.isUIandModelLocaleSame() === true);
                                translationInfo.perform(avoidFullTranslation, false);
                            }

                            //Set the last update date to now!
                            var currentDate = new XDate(Config.getServerDateTime(), true).toString(this.ISO_DATE_FORMAT);
                            this.set("SkillsPassport.DocumentInfo.LastUpdateDate", currentDate, {silent: true});

                        }

                    }
                },
                /*****************************************************************************************/
                /**
                 * a reference to the PrintingPreferencesModel
                 */
                preferences: null,
                defaultPreferences: null,
                /**
                 * Get the active preferences for the given document
                 * @param docType
                 * @returns a copy of the active preferences attributes
                 */
                getPreferences: function (docType) {

                    var prefs = {};
                    if (this.preferences !== null) {
                        if (docType === undefined || docType === null || docType === "") {
                            prefs = $.extend(true, {}, this.preferences.attributes);
                        } else {
                            prefs = $.extend(true, {}, this.preferences.get(docType));
                        }
                    }
                    return prefs;
                },
                /**
                 * Get the default preferences for the given document
                 * @param docType
                 * @returns a copy of the default preferences attributes
                 */
                getDefaultPreferences: function (docType) {
                    if (docType === undefined || docType === null || docType === "") {
                        docType = GlobalDocument.getPrefDocument();
                    }
                    var prefs = {};
                    if (this.preferences !== null) {
                        prefs = $.extend(true, {}, this.preferences._originalAttributes[ docType ]);
                    }
                    return  prefs;
                },
                /*****************************************************************************************/
                initialize: function () {

                    // Set the dates
                    if (Config !== undefined) {
                        var currentDate = new XDate(Config.getServerDateTime(), true).toString(this.ISO_DATE_FORMAT);
                        this.defaults.SkillsPassport.DocumentInfo.CreationDate = currentDate;
                        this.defaults.SkillsPassport.DocumentInfo.LastUpdateDate = currentDate;
                    }

                    //Initiate a Printing Preferences Model
                    this.preferences = new PrintingPreferencesModel();
                    this.defaultPreferences = this.preferences.getDefaultsModel();

                    //Keep a reference to original attributes
                    this._originalAttributes = $.extend(true, {}, this.defaults);
                    this._originalModel = new Backbone.NestedModel(this._originalAttributes);

                    //-- EVENTS
                    this.bind("remote:upload:model:populated", this.modelUploaded, this);
                    this.bind("uploaded:esp", this.modelUploaded, this);
                    this.bind("prefs:order:changed", this.modelPrefsOrderChanged, this);
                    this.bind("prefs:pageBreaks:changed", this.modelPrefsPageBreaksChanged);
                    this.bind("prefs:data:format:changed", this.modelPrefsDateFormatChanged, this);
                    this.bind("content:changed", this.modelContentChanged, this);
                    this.bind("list:sort:change", this.modelListSortChanged, this);
                    this.bind("linked:attachment:changed", this.modelAttachmentChanged, this);

                    this.infoManager = null;
                    this.translationManager = null;
                    this.conversionManager = null;
                    this.documentationManager = null;
                },
                onClose: function () {
                    this.unbindEvents();
                },
                unbindEvents: function () {
                    this.unbind("remote:upload:model:populated", this.modelUploaded);
                    this.unbind("uploaded:esp", this.modelUploaded);
                    this.unbind("prefs:order:changed", this.modelPrefsOrderChanged);
                    this.unbind("prefs:pageBreaks:changed", this.modelPrefsPageBreaksChanged);
                    this.unbind("prefs:data:format:changed", this.modelPrefsDateFormatChanged);
                    this.unbind("content:changed", this.modelContentChanged);
                    this.unbind("list:sort:change", this.modelListSortChanged);
                    this.unbind("linked:attachment:changed", this.modelAttachmentChanged);
                },
                /*****************************************************************************************/
                setLastUpdateDate: function () {
                    var currentDate = new XDate(Config.getServerDateTime(), true).toString(this.ISO_DATE_FORMAT);
                    this.set("SkillsPassport.DocumentInfo.LastUpdateDate", currentDate, {silent: true});
                },
                /*****************************************************************************************/
                getEuropassLogo: function () {
                    var json = JSON.parse(this.conversion().toTransferable());
                    if (json != null &&
                            json.SkillsPassport != null && json.SkillsPassport.DocumentInfo != null &&
                            json.SkillsPassport.DocumentInfo.EuropassLogo != null) {
                        return json.SkillsPassport.DocumentInfo.EuropassLogo;
                    }
                    return this.get("SkillsPassport.DocumentInfo.EuropassLogo");
                },
                /*****************************************************************************************/
                setEuropassLogo: function (show) {
                    this.set("SkillsPassport.DocumentInfo.EuropassLogo", show, {silent: true});

                    var json = JSON.parse(this.conversion().toTransferable());
                    if (json != null &&
                            json.SkillsPassport != null && json.SkillsPassport.DocumentInfo != null &&
                            json.SkillsPassport.DocumentInfo.EuropassLogo != null) {
                        json.SkillsPassport.DocumentInfo.EuropassLogo = show;
                        this.set("SkillsPassport.DocumentInfo.EuropassLogo", show, {silent: true});

                        localStorage.setItem("temporary.europass.ewa.skillspassport.v3", JSON.stringify(json));
                        // We can use ConversionManager.prototype.fromTransferable but need to adjust it to include EuropassLogo
                    }
                    this.trigger("europassLogo:changed");
                },
                /*****************************************************************************************/
                clearContext: function () {
                    this.conversion().cleanUp(this.attributes, false);
                },
                /*****************************************************************************************/
                modelUploaded: function () {
                    this.trigger("model:uploaded:esp", this.attributes);
                },
                modelPrefsDateFormatChanged: function (relSection) {
                    this.trigger("model:prefs:data:format:changed", relSection);
                },
                modelPrefsOrderChanged: function (relSection) {
                    this.clearContext();
                    this.trigger("model:prefs:order:changed", relSection);
                },
                modelPrefsPageBreaksChanged: function () {
                    this.clearContext();
                    this.trigger("model:prefs:pageBreaks:changed");
                },
                modelContentChanged: function (relSection, origin) {
                    this.clearContext();
                    this.trigger("model:content:changed", relSection, origin);
                },
                modelListSortChanged: function (relSection, startPos, endPos) {
                    this.clearContext();
                    this.trigger("model:list:sort:change", relSection, startPos, endPos);
                },
                modelAttachmentChanged: function (origin) {
                    this.clearContext();
                    this.trigger("model:linked:attachment:changed", origin);
                },
                /*****************************************************************************************/
                info: function () {
                    if (this.infoManager === null) {
                        this.infoManager = new ModelInfoManager(this);
                    }
                    return this.infoManager;
                },
                translation: function () {
                    if (this.translationManager === null) {
                        this.translationManager = new TranslationManager(this);
                    }
                    return this.translationManager;
                },
                conversion: function () {
                    if (this.conversionManager === null) {
                        this.conversionManager = new ConversionManager(this);
                    }
                    return this.conversionManager;
                },
                documentation: function () {
                    if (this.documentationManager === null) {
                        this.documentationManager = new DocumentationManager(this);
                    }
                    return this.documentationManager;
                },
                /*****************************************************************************************/
                // Specific field for reRendering the Skills Sections
                setActiveSkillsSection: function (section) {
                    this.activeSkillsSection = section;
                },
                getActiveSkillsSection: function () {
                    return this.activeSkillsSection;
                },
                resetActiveSkillsSection: function () {
                    this.activeSkillsSection = undefined;
                },
                /*****************************************************************************************/
                resetAll: function (silently) {
                    //reset Attachments on the server-side (start early)
                    var documentationInfo = this.documentation();
                    if (documentationInfo !== null) {
                        documentationInfo.restRemoveAllAttachments();
                    }
                    //reset Session ID
                    if (_.isFunction(SessionManager.clear)) {
                        SessionManager.clear();
                    }
                    //reset the List of experience dates
                    GlobalDateFormat.applyFunction({f: "clear"}, "ECV");
                    GlobalDateFormat.applyFunction({f: "clear"}, "ELP");
                    GlobalDateFormat.applyFunction({f: "clear"}, "ECL");

                    var section = "SkillsPassport.LearnerInfo";
                    //reset silently
                    this.reset(section, true);

                    var cover = "SkillsPassport.CoverLetter";
                    //reset silently
                    this.reset(cover, true);

                    //reset the Attachment section
                    var unsetJson = JSON.parse("{\"SkillsPassport.Attachment\": null }");
                    this.set(unsetJson, {silent: true});
                    var currentDate = Config.getServerLastUpdateDate().toString(this.ISO_DATE_FORMAT);
                    this.set("SkillsPassport.PrintingPreferences", null, {silently: true});
                    this.set("SkillsPassport.DocumentInfo.LastUpdateDate", currentDate, {silently: true});
                    this.set("SkillsPassport.DocumentInfo.CreationDate", currentDate, {silently: true});
                    this.set("SkillsPassport.DocumentInfo.EuropassLogo", true, {silently: true});

                    //trigger now event
                    if (silently === undefined || silently === null || silently === false) {
                        this.trigger("model:content:reset");
                    }
                },

                resetCLOnly: function (silently, allReset) {

                    var coverLetterSignature = "SkillsPassport.LearnerInfo.Identification.Signature";
                    var coverLetter = "SkillsPassport.CoverLetter";
                    var preferences = "Preferences.ECL";
                    var printingPreferences = "SkillsPassport.PrintingPreferences.ECL";

                    GlobalDateFormat.applyFunction({f: "clear"}, "ECL");

                    this.reset(coverLetterSignature, true);
                    this.reset(coverLetter, true);

                    this.reset(preferences, true);
                    this.reset(printingPreferences, true);

                    this.set("SkillsPassport.DocumentInfo.LastUpdateDate", Config.getServerLastUpdateDate().toString(this.ISO_DATE_FORMAT), {silently: true});
                    if (!allReset && silently === undefined || silently === null || silently === false) {
                        this.trigger("model:content:reset", false, true, true, true);
                    }
                },

                resetESPOnly: function (silently, allReset) {

                    var _that = this;

                    //reset Attachments on the server-side (start early)
                    var documentationInfo = this.documentation();
                    if (documentationInfo !== null) {
                        documentationInfo.restRemoveAllAttachments();
                    }
                    var europeanSkillsPassport = "SkillsPassport.Attachment";
                    this.reset(europeanSkillsPassport, true);

                    var json = JSON.parse(this.conversion().toTransferable());
                    var pathsWithReference = Utils.getJSONExtractedPathsByPropertyKey(json, "ReferenceTo");

                    $.each(pathsWithReference, function (index) {
                        var subArrayWithRef = pathsWithReference[index];
                        $.each(subArrayWithRef, function (i) {
                            if (!isNaN(subArrayWithRef[i])) {
                                subArrayWithRef[i] = "[" + subArrayWithRef[i] + "]";
                            }
                        });
                        var joinedArray = subArrayWithRef.join('.');
                        _that.reset(joinedArray, true);
                    });

                    this.set("SkillsPassport.DocumentInfo.LastUpdateDate", Config.getServerLastUpdateDate().toString(_that.ISO_DATE_FORMAT), {silently: true});
                    if (!allReset && silently === undefined || silently === null || silently === false) {
                        this.trigger("model:content:reset", true, true, true, false);
                    }
                },

                resetLPOnly: function (silently, allReset) {

                    GlobalDateFormat.applyFunction({f: "clear"}, "ELP");
                    var preferences = "Preferences.ELP";
                    var printingPreferences = "SkillsPassport.PrintingPreferences.ELP";
                    this.reset(preferences, true);
                    this.reset(printingPreferences, true);

                    var _that = this;
                    var json = JSON.parse(this.conversion().toTransferable());

                    if (json.SkillsPassport.LearnerInfo === undefined ||
                            json.SkillsPassport.LearnerInfo.Skills === undefined ||
                            json.SkillsPassport.LearnerInfo.Skills.Linguistic === undefined ||
                            json.SkillsPassport.LearnerInfo.Skills.Linguistic.ForeignLanguage === undefined) {
                        return;
                    }

                    var resetObject = {};
                    var resetObjectLingExperience = {};
                    json.SkillsPassport.LearnerInfo.Skills.Linguistic.ForeignLanguage.map(function (item, langIndex) {
                        if (item.Experience) {
                            item.Experience.map(function (keys, i) {
                                var sectionToReset = "SkillsPassport.LearnerInfo.Skills.Linguistic.ForeignLanguage[" + langIndex + "].Experience";
                                resetObjectLingExperience[sectionToReset] = sectionToReset;
                            });
                        }

                        if (item.Certificate) {
                            item.Certificate.map(function (keys, i) {
                                Object.keys(keys).forEach(function (title) {
                                    if (title !== 'Title') {
                                        var generSectionsToReset = "SkillsPassport.LearnerInfo.Skills.Linguistic.ForeignLanguage[" + langIndex + "].Certificate[" + i + "]";
                                        resetObject[generSectionsToReset + ".AwardingBody"] = generSectionsToReset + ".AwardingBody";
                                        resetObject[generSectionsToReset + ".Date"] = generSectionsToReset + ".Date";
                                        resetObject[generSectionsToReset + ".Level"] = generSectionsToReset + ".Level";
                                    }
                                });
                            });
                        }
                    });

                    var elpCleanup = '';
                    for (var key in resetObject) {
                        elpCleanup = elpCleanup + ' ' + key;
                    }
                    _that.reset(elpCleanup.trim(), true);

                    var elpCleanupExp = '';
                    for (var key in resetObjectLingExperience) {
                        elpCleanupExp = elpCleanupExp + ' ' + key;
                    }
                    _that.reset(elpCleanupExp.trim(), true, true);


                    if (!allReset && silently === undefined || silently === null || silently === false) {
                        this.trigger("model:content:reset", true, true, false, true);
                    }
                    this.set("SkillsPassport.DocumentInfo.LastUpdateDate", Config.getServerLastUpdateDate().toString(_that.ISO_DATE_FORMAT), {silently: true});
                },

                resetCV: function (silently, allReset) {

                    var coverLetterSignature = "SkillsPassport.LearnerInfo.Identification.Signature";
                    var coverLetterSignatureObj = undefined;

                    if (localStorage.getItem('temporary.europass.ewa.skillspassport.v3') !== null) {
                        var jsonObj = JSON.parse(localStorage.getItem('temporary.europass.ewa.skillspassport.v3'));

                        if (jsonObj.SkillsPassport !== undefined && jsonObj.SkillsPassport.LearnerInfo !== undefined
                                && jsonObj.SkillsPassport.LearnerInfo.Identification !== undefined) {
                            coverLetterSignatureObj = jsonObj.SkillsPassport.LearnerInfo.Identification.Signature;
                        }
                    }

                    //reset the List of experience dates
                    GlobalDateFormat.applyFunction({f: "clear"}, "ECV");

                    var section = "SkillsPassport.LearnerInfo";
                    //reset silently
                    this.reset(section, true);

                    //reset the Attachment section
                    var unsetJson = JSON.parse("{\"SkillsPassport.Attachment\": null }");
                    this.set(unsetJson, {silent: true});
                    var currentDate = Config.getServerLastUpdateDate().toString(this.ISO_DATE_FORMAT);
                    this.set("SkillsPassport.DocumentInfo.LastUpdateDate", currentDate, {silently: true});
                    this.set("SkillsPassport.DocumentInfo.CreationDate", currentDate, {silently: true});
                    this.set("SkillsPassport.DocumentInfo.EuropassLogo", true, {silently: true});

                    if (!this.info().isCLEmpty()) {
                        //reset also enclosed documents related to CV/LP etc.
                        this.set("SkillsPassport.CoverLetter.Documentation.InterDocument", null, {silently: true});

                        if (coverLetterSignatureObj !== undefined) {
                            this.set(coverLetterSignature, coverLetterSignatureObj, true);
                        }
                    }

                    //trigger now event
                    if (!allReset && silently === undefined || silently === null || silently === false) {
                        this.trigger("model:content:reset", false, true);
                    }
                },

                reset: function (section, silently, elpExperienceSection) {

                    if (section === undefined || section === null || section === "") {
                        return false;
                    }

                    //console.log( "MODEL RESET: " + section );
                    var sectionArray = section.split(" ");

                    for (var idx in  sectionArray) {
                        // DECIDE Whether the section is part of an array REG-EXP ENDS WITH Index
                        var match = sectionArray[idx].match(/([\S]*)(\[(\d+)\])$/);
                        var inArray = (match !== null);

                        var prefSection = Utils.removeSkillsPassportPrefix(sectionArray[idx]);

                        if (elpExperienceSection) {
                            var sectionExperienceToRemove = this.get(sectionArray[idx]);
                            if (sectionExperienceToRemove && typeof sectionExperienceToRemove != 'undefined' && sectionExperienceToRemove != null) {
                                sectionExperienceToRemove.splice(0);
                            }
                        } else if (inArray) {
                            prefSection = Utils.toArrayTxt(prefSection);
                            // PREFERENCES
                            this.preferences.removeArrayItem("", prefSection);
                            // CONTENT: Simply remove from array, the rest will be-re-ordered
                            this.remove(sectionArray[idx]);
                        } else {
                            // DEFAULT Matching Printing Preferences - add them silently
                            if (prefSection === "LearnerInfo" || prefSection === "CoverLetter") {
                                this.preferences.resetToDefaults();
                            } else {
                                this.preferences.resetPreference("", prefSection);
                            }
                            // CONTENT
                            var unsetJson = JSON.parse("{\"" + sectionArray[idx] + "\": null }");
                            this.set(unsetJson);

                            var resetContent = this._originalModel.get(sectionArray[idx]) || null;
                            var resetJson = Utils.prepareModelAttr(sectionArray[idx], resetContent);
                            this.set(resetJson);
                        }
                        //console.log("PREF after reset: \n" + JSON.stringify(this.preferences.attributes));
                        if (silently === undefined || silently === null || silently === false) {
                            this.trigger("content:changed", sectionArray[idx]);
                        }
                    }
                },
                FORM_FIELDS_SELECTOR:
                        //ekar Apr 2014: value selector is not safe, as it uses the value attribute, while the user has updated the value property.
//			":input:not(button):not(:radio):not(:checkbox):not(.PrintingPreferences):not([value=\"\"]),"
                        ":input:not(button):not(:radio):not(:checkbox):not(.PrintingPreferences)",
                FORM_FIELDS_REST_SELECTOR:
                        ":radio:checked:not(.PrintingPreferences),"
                        + ":checkbox:checked",

                getFormFields: function (frm, fieldPrefix) {
                    var txtFields = frm.find(this.FORM_FIELDS_SELECTOR).filter(function () {
                        var val = $(this).val();
                        return val !== undefined && val !== null && val !== "";
                    });
                    var restFields = frm.find(this.FORM_FIELDS_REST_SELECTOR);

                    var fields = txtFields.add(restFields);
//			console.log( fields );	
                    if (_.isString(fieldPrefix) && fieldPrefix !== "") {
                        fields = fields.filter("[name^=\"" + fieldPrefix + "\"]");
                    }

                    return fields;
                },

                CLEAR_LABEL: "–",

                /**
                 * Read non-empty form fields and prepare a model with the values that are to be set.
                 * 
                 * @param form
                 * @returns JSON with the model and an array of the changed json paths
                 */
                formToModel: function (frm, section) {

                    var m = new Backbone.NestedModel({});
                    var changes = [];

                    var fieldPrefix = Utils.removeSkillsPassportPrefix(section);

                    var fields = this.getFormFields(frm, fieldPrefix);
                    for (var i = 0; i < fields.length; i++) {
                        var input = $(fields[i]);
                        var name = input.attr("name");
                        var value = input.val();

                        //When the form fields is set to a ignorable text, then skip it
                        if (value === this.CLEAR_LABEL) {
                            continue;
                        }

                        /**
                         * pgia: eliminate telephone type without assigned values
                         * 
                         * ( current field is select.Telephone ) AND ( current field value NOT empty ) ?
                         *     current field + 1 is undefined ?
                         *         continue;
                         *     current field + 1 is input.Telephone ?
                         *         current field + 1 value empty ?
                         *             continue;
                         */

                        if (input.is("select.Telephone") && value !== "") {
                            if (fields[i + 1] === undefined || fields[i + 1] === null) {
                                continue;
                            } else {
                                var nextField = $(fields[i + 1]);
                                if (nextField.is("input.Telephone")) {
                                    if (nextField.val() === "")
                                        continue;
                                } else {
                                    continue;
                                }
                            }
                        }

                        value = this.handleBinaryRelatedValue(input, value);
                        value = this.handleDateRelatedValue(input, value);

                        var attr = Utils.prepareModelAttr(name, value);
                        if (attr !== null) {
                            changes.push(attr);
                            m.set(attr);
                        }
                    }
                    /*
                     * Append other sections
                     */
                    return {
                        "model": m,
                        "changes": changes
                    };
                },

                /**
                 * EWA-1637 / EWA-1665
                 * Will return the previous attributes object of the requested section, in order to compare
                 * with current attributes value and determine if we will rerender the related section
                 * Used for GenericSkills, as there is no available current section, and we handle this
                 * explicitly
                 */

                getModelPreviousSection: function (section) {

                    var previousAttributes = this.previousAttributes();
                    var sectionParts = section.split(".");

                    var prevAttrSectionValue = null;
                    for (var i = 0; i < sectionParts.length; i++) {
                        if (i === 0)
                            prevAttrSectionValue = previousAttributes[sectionParts[i]];
                        else if (prevAttrSectionValue !== undefined)
                            prevAttrSectionValue = prevAttrSectionValue[sectionParts[i]];
                        else
                            break;
                    }

                    if (prevAttrSectionValue !== null && prevAttrSectionValue !== undefined) {
                        return prevAttrSectionValue;
                    }

                    return null;
                },

                /**
                 * Will append the "ReferenceTo" section of the live model to the given model
                 * @param section (without the SkillsPassport prefix!)
                 * @param model
                 */
                appendDocumentation: function (section, otherModel) {
                    var referenceToSection = section + this.REFERENCETO;
                    var referenceTo = this.get(Utils.addSkillsPassportPrefix(referenceToSection));
                    if (!_.isUndefined(referenceTo) && !_.isNull(referenceTo)) {
                        var tmp = {};
                        tmp[referenceToSection] = referenceTo;
                        otherModel.set(tmp);
                    }
                },
                /**
                 * Handle a value that must be stored as true or false
                 */
                handleBinaryRelatedValue: function (input, value) {
                    if (input.is(":checkbox") && input.hasClass("isBinary")) {
                        return true;
                    }
                    return value;
                },
                /**
                 * Parses the value of the given input field as int,
                 * when the input field name ends with Day, Month or
                 * Year
                 */
                handleDateRelatedValue: function (input, value) {
                    if (input.is("[name$=Day]")
                            || input.is("[name$=Month]")
                            || input.is("[name$=Year]")) {
                        return parseInt(value);
                    }
                    return value;
                }

            });
            return SkillsPassport;
        });