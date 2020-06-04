define(
        [
            'module',
            'jquery',
            'underscore',
            'jsonpath',
            'Utils',
            'europass/ExperienceDateFormatInstance',
            'i18n!localization/nls/DefaultDateFormat',
            'i18n!localization/nls/DateFormat',
            'i18n!localization/nls/DateFormatPattern',
            // Lookups here to be used for translation purposes
            'i18n!localization/nls/AchievementType',
            'i18n!localization/nls/PersonalDataTreatment',
            'i18n!localization/nls/BusinessSector',
            'i18n!localization/nls/Country',
            'i18n!localization/nls/EducationalField',
            'i18n!localization/nls/EducationalLevel',
            'i18n!localization/nls/GenderOption',
            'i18n!localization/nls/HeadlineType',
            'i18n!localization/nls/InstantMessagingType',
            'i18n!localization/nls/Language',
            'i18n!localization/nls/MotherLanguage',
            'i18n!localization/nls/Nationality',
            'i18n!localization/nls/OccupationalField',
            'i18n!localization/nls/TelephoneType',
            'i18n!localization/nls/LinguisticExperienceType',
            'europass/resources/FilteredTitle',
            'i18n!localization/nls/EnclosedLabel',
            'europass/resources/FilteredOpeningSalutation',
            'europass/resources/FilteredClosingSalutation',
            'europass/maps/PositionFMap',
            'europass/maps/PositionMMap',
            'europass/maps/PositionNAMap'
        ],
        function (module, $, _, _JSONPATH, Utils, GlobalDateFormat,
                DefaultDateFormat, DateFormat, DateFormatPattern,
                AchievementType, PersonalDataTreatment, BusinessSector, Country, EducationalField,
                EducationalLevel, GenderOption, HeadlineType,
                InstantMessagingType, Language, MotherLanguage, Nationality,
                OccupationalField, TelephoneType, LinguisticExperienceType, Title, EnclosedLabel, OpeningSalutation, ClosingSalutation,
                PositionFMap, PositionMMap, PositionNAMap) {

            var TAXONOMIES = {
                AchievementType: AchievementType,
                PersonalDataTreatment: PersonalDataTreatment,
                BusinessSector: BusinessSector,
                Country: Country,
                EducationalField: EducationalField,
                EducationalLevel: EducationalLevel,
                GenderOption: GenderOption,
                HeadlineType: HeadlineType,
                InstantMessagingType: InstantMessagingType,
                Language: Language,
                MotherLanguage: MotherLanguage,
                Nationality: Nationality,
                OccupationalField: OccupationalField,
                TelephoneType: TelephoneType,
                LinguisticExperienceType: LinguisticExperienceType,
                Title: Title,
                EnclosedLabel: EnclosedLabel,
                OpeningSalutation: OpeningSalutation,
                ClosingSalutation: ClosingSalutation
            };
            /**
             * The LookupItems provide information on which fields
             * are controlled by Lookups. This is useful when
             * translating between languages.
             */
            var LookupItems = {
                Country: ["$..Country.Code"],
                InstantMessagingType: ["$..InstantMessaging[*].Use.Code"],
                TelephoneType: ["$..Telephone[*].Use.Code"],
                GenderOption: ["$..Gender.Code"],
                Nationality: ["$..Nationality[*].Code"],
                HeadlineType: ["$..Headline.Type.Code"],
                OccupationalField: [
                    "$..Headline.Description.Code",
                    "$..WorkExperience[*].Position.Code"],
                BusinessSector: ["$..WorkExperience[*].Employer.Sector.Code"],
                EducationalField: ["$..Education[*].Field.Code"],
                EducationalLevel: ["$..Education[*].Level.Code"],
                Language: ["$..ForeignLanguage[*].Description.Code"],
                MotherLanguage: ["$..MotherTongue[*].Description.Code"],
                AchievementType: ["$..Achievement[*].Title.Code"],
                //PersonalDataTreatment : [ "$..Achievement[*].Description" ],
                LinguisticExperienceType: ["$..Experience[*].Area.Code"],
                Title: ["$..Title.Code"],
                OpeningSalutation: ["$..OpeningSalutation.Salutation.Code"],
                ClosingSalutation: ["$..ClosingSalutation.Code"],
                EnclosedLabel: ["$..Documentation.Heading.Code"]

            };

            var TranslationManager = function (model) {
                this.model = model;
            };

            /**
             * 
             * @param avoidFullTranslation: boolean as to whether full translation needs to be done
             * @param preservePrefs: preserve user defined prefs
             * 1. lookups
             * 2. the printing preferences for order and date formats
             */
            TranslationManager.prototype.perform = function (avoidFullTranslation, preservePrefs) {
//			console.log("Avoid Translation? " +  avoidFullTranslation );
                //bootstrap
                this.bootstrap();

                if (avoidFullTranslation === true) {
                    return;
                }
                //0. Set the model locale
//			console.log("Locale before translation: " + this.model.get("SkillsPassport.Locale"));
                this.model.set("SkillsPassport.Locale", (module.config().locale || "en"));
//			console.log("Locale after translation: " + this.model.get("SkillsPassport.Locale"));

                //1. translate the code-labels !!!
                this.lookups();
                //2. reset the preferences for ECL - EXCEPT for PersonName Order AND Justification AND Localisation Date Format
                var eclPersonNameOrder = this.model.preferences.get("ECL.LearnerInfo.Identification.PersonName");
                var eclJustification = this.model.preferences.get("ECL.CoverLetter.Justification");
                var eclClosingSalutationEnabledName = this.model.preferences.get("ECL.CoverLetter.SignatureName");
                var eclLocalisationDate = this.model.preferences.get("ECL.CoverLetter.Letter.Localisation.Date");
                this.model.preferences.resetToDefaults("ECL");
                if (!_.isEmpty(eclPersonNameOrder))
                    this.model.preferences.set("ECL.LearnerInfo.Identification.PersonName", eclPersonNameOrder);
                if (!_.isEmpty(eclJustification))
                    this.model.preferences.set("ECL.CoverLetter.Justification", eclJustification);
                if (!_.isEmpty(eclClosingSalutationEnabledName))
                    this.model.preferences.set("ECL.CoverLetter.SignatureName", eclClosingSalutationEnabledName);
                if (!_.isEmpty(eclLocalisationDate))
                    this.model.preferences.set("ECL.CoverLetter.Letter.Localisation.Date", eclLocalisationDate);
                //ekar: Order is important. Prefs are reset to defaults for CL, but then any peculiarities for date formats are handled.
                //3. translate the format prefs  !!!
                this.others(preservePrefs);
            };
            /**
             * Translates all fields that are translatable and are
             * based on lookups
             */
            TranslationManager.prototype.lookups = function () {
                // console.log( "translateLookups...");
                var model = this.model;
                var content = model.attributes;

                for (var lookup in LookupItems) {
                    // require the map...
                    var resource = TAXONOMIES[lookup];

                    if (resource !== undefined) {
                        var fields = LookupItems[lookup];

                        for (var i = 0; i < fields.length; i++) {
                            var field = fields[i];
                            var matches = _JSONPATH(content, field, {resultType: "PATH"});
                            if (matches === false) {
                                continue;
                            }
                            for (var j = 0; j < matches.length; j++) {
                                var m = matches[j];
                                if (m === null) {
                                    continue;
                                }
                                var match = Utils.convertJsonPath(m);
                                var code = model.get(match);

                                if (code === undefined || code === null || code === "") {
                                    continue;
                                }
                                var transLabel = resource[code];
                                //-------------------------------------------------------------------------------------//
                                //the code is changing the Achievement Description of Dati Personali when the language is changed
                                //but if the user has Italian with custom Dati Personally and switches in English and back to Italian the custom text will be replaced by the default one. So the user might loose data without his/her knowledge.
                                /*if (lookup === "AchievementType" && code == 'signature_equivalent') {
                                 //find personal data treatment description and translate
                                 var fallback_taxonomy_des = "/ewa/cv/compose/additional-information/achievement/authorisation/default-description";
                                 
                                 //get the Description of the Dati Personali
                                 matchSpecialDescription = m.replace("['Title']['Code']", "['Description']");
                                 var achievementDescription = _JSONPATH(content, matchSpecialDescription, {resultType : "PATH"});
                                 
                                 if ( Utils.isUndefined(achievementDescription) ){
                                 continue;
                                 }
                                 var vmatch = Utils.convertJsonPath( achievementDescription[0] );
                                 var vcode = model.get(vmatch);
                                 if (vcode === undefined || vcode === null || vcode === "") {
                                 continue;
                                 }
                                 
                                 var vtransLabel = PersonalDataTreatment['signature_equivalent'];
                                 if (!Utils.isUndefined(vtransLabel) && vtransLabel != fallback_taxonomy_des)//Utils.stripHtml 
                                 model.set( vmatch, vtransLabel );
                                 }*/
                                //-------------------------------------------------------------------------------------//

                                if (lookup === "GenderOption") {
                                    var codeKey = "LearnerInfo.Identification.Demographics.Gender.";
                                    transLabel = resource[codeKey + code];
                                }

                                if ((lookup === "Country" || lookup === "Nationality") && (code === "GR" || code === "GB")) {
                                    model.set(match, (code === "GR" ? "EL" : "UK"), {silent: true});
                                }

                                if (transLabel === undefined || transLabel === null) {
                                    continue;
                                }

                                if (lookup === "OccupationalField") {
                                    var gender = model.get("SkillsPassport.LearnerInfo.Identification.Demographics.Gender.Code");
                                    switch (gender) {
                                        case "F":
                                        {
                                            transLabel = transLabel.F;
                                            break;
                                        }
                                        default:
                                        {
                                            transLabel = transLabel.M;
                                            break;
                                        }
                                    }
                                }

                                var labelPath = match.substring(0, (match.length - ".Code".length)) + ".Label";
                                model.set(labelPath, transLabel);
                            }
                        }
                    }
                }
            };
            /**
             * Populate the list of pref-sections that hold a date format that must be shared accross various sections
             */
            var DateFormatPrefs = {
                Education: "$..Education.array[*].Period",
                WorkExperience: "$..WorkExperience.array[*].Period",
                LinguisticCertificate: "$..ForeignLanguage.array[*].Certificate.array[*].Date",
                LinguisticExperience: "$..ForeignLanguage.array[*].Experience.array[*].Period"
            };

            TranslationManager.DateFormatPrefs_Regex =
                    new RegExp("(^SkillsPassport\\.LearnerInfo\\.Skills\\.Linguistic\\.ForeignLanguage\\[[0-9]*\\]\\.(Certificate|Experience)\\[[0-9]*\\]$)|" +
                            "(SkillsPassport\\.LearnerInfo\\.(WorkExperience|Education)\\[[0-9]*\\])|" +
                            "(CoverLetter\\.Letter\\.Localisation\\.Date)");

            TranslationManager.prototype.bootstrap = function () {
                var content = this.model.preferences.attributes;

                var formatSet = [];

                for (var item in DateFormatPrefs) {
                    if (item === undefined || item === null || item === "") {
                        continue;
                    }

                    var datePref = DateFormatPrefs[item];
                    if (datePref === undefined || datePref === null || datePref === "") {
                        continue;
                    }

                    var matches = _JSONPATH(content, datePref, {resultType: "PATH"});
                    if (matches === false) {
                        continue;
                    }
                    for (var j = 0; j < matches.length; j++) {
                        var m = matches[j];
                        if (m === undefined || m === null) {
                            continue;
                        }
                        var match = Utils.convertJsonPath(m);
                        var prefDocument = match.substring(0, match.indexOf("."));
                        var prefObj = this.model.preferences.get(match);

                        if (Utils.isEmptyObject(prefObj) === true) {
                            continue;
                        }
                        var prefFormat = prefObj.format;
                        if (formatSet[ prefDocument ] === undefined && typeof prefFormat === "string" && prefFormat !== "") {
                            if (!DateFormatPattern[prefDocument][prefFormat] || typeof prefFormat !== "string" || prefFormat === "") {
                                prefFormat = DefaultDateFormat[prefDocument];
                            }
                            //console.log("Set Global Format from '"+match+"' to  '"+prefFormat+"'");
                            GlobalDateFormat.applyFunction({f: "set", args: [match, prefFormat]}, prefDocument);
                            formatSet[ prefDocument ] = prefFormat;
                        }
//console.log("Add to list the '"+match+"'");
                        GlobalDateFormat.applyFunction({f: "addToList", args: [match]}, prefDocument);

                        if (prefFormat !== formatSet[ prefDocument ]) {
                            prefObj[ "format" ] = formatSet[ prefDocument ];
//console.log("Adjust the format of '"+match+"' to:\n"+JSON.stringify(prefObj) );
                        }
                    }
                }
            };
            /****************************************************************************************/
            var DATE_FORMAT_REGEX = /(\bBirthdate|\bPeriod|\bDate)/g;

            /**
             * Make any adjustments necessary to the model.
             * Will affect the Printing Preferences of All documents
             * @param preservePrefs: preserve user defined prefs
             */
            TranslationManager.prototype.others = function (preservePrefs) {
                if (this.model.preferences !== null) {
                    // every time a model is initialized, person name order is reverted to default
                    this.model.preferences.applyDefaultPersonNameOrder(null, null, !preservePrefs);

                    this.model.preferences.iterate(this.updateLocalisableDateFormat, this, DATE_FORMAT_REGEX);
                }

            };
            /**
             * Updates the DateFormat after a language switch
             * @param pref
             * @returns {Boolean}
             */
            TranslationManager.prototype.updateLocalisableDateFormat = function (pref, prefName) {
                if (prefName === undefined || prefName === null || prefName === "") {
                    return false;
                }
                var prefDocument = prefName.substring(0, prefName.indexOf("."));
                //console.log('translate date format');
                if (pref === undefined || pref === null) {
                    return false;
                }
                var format = pref.format;
                if (format === undefined || format === null || format === "") {
                    return false;
                }

                var provided = DateFormatPattern[prefDocument][format];

                if (provided === false) {
                    pref.format = DefaultDateFormat[prefDocument];
                }
            };

            TranslationManager.prototype.chooseOccupationMap = function (gender) {
                if (gender === undefined || gender === null && this.model !== undefined) {
                    gender = this.model.get("SkillsPassport.LearnerInfo.Identification.Demographics.Gender.Code");
                }
                var map = null;
                switch (gender) {
                    case "F":
                        map = PositionFMap;
                        break;
                    case "M":
                        map = PositionMMap;
                        break;
                    default:
                        map = PositionNAMap;
                }
                return map;
            };

            return TranslationManager;
        }
);