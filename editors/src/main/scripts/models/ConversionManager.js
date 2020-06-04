define(
        [
            'module',
            'jquery',
            'underscore',
            'xdate',
            'Utils',
            'europass/http/SessionManagerInstance',
            'europass/http/WindowConfigInstance',
            'europass/GlobalDocumentInstance'
        ]
        , function (module, $, _, XDate, Utils, SessionManager, Config, GlobalDocument) {
            var ConversionManager = function (model) {
                this.model = model;
            };
            ConversionManager.prototype.cleanUp = function (currentModel, clearSession) {
                delete currentModel.Idx;
                delete currentModel.subsection;
                delete currentModel.Preferences;
                delete currentModel.type;
                if (clearSession === true) {
                    delete currentModel.SessionID;
                }
            };
            /**
             * Translate the model, as expected in EWA Editor, to a JSON string to be locally stored and later consumed by the EWA Editor.
             * Also set properly: i) the preferences, ii) the session-id
             * @returns
             */
            ConversionManager.prototype.toStorable = function () {
                var content = {};
                $.extend(true, content, this.model.attributes);
                //Store the preferences for all documents
                var prefs = {};
                $.extend(true, prefs, this.model.getPreferences());
                //--- SET THE PREFERENCES
                content.Preferences = prefs;

                //--- SET THE LAST UPDATE DATE
                content.SkillsPassport.DocumentInfo.LastUpdateDate = Config.getServerLastUpdateDate().toString(this.model.ISO_DATE_FORMAT);

                return JSON.stringify(content);
            };
            /**
             * Translate the model, as expected in EWA Editor to a JSON string as managed by the REST API.
             * Also set properly: i) the preferences, ii) the locale and iii) the session-id
             * @returns
             */
            ConversionManager.prototype.toTransferable = function (locale) {

                var content = {};
                $.extend(true, content, this.model.attributes);
                this.cleanUp(content, true);

                //Set the preferences for the current document
                var prefs = {};
//			$.extend( true, prefs, this.model.preferences.toSchema( GlobalDocument.getPrefDocument() ) );
                var documentType = this.model.attributes.SkillsPassport.DocumentInfo.DocumentType;
                var prefsForDocumentType = ((documentType == "ECV_ESP" || documentType == "ESP") ? "ECV" : documentType);
                $.extend(true, prefs, this.model.preferences.toSchema(prefsForDocumentType));

                // Check if there are any bundled documents to set their preferences too
                var bundles = content.SkillsPassport.DocumentInfo.Document;
                if (!_.isUndefined(bundles) && !_.isNull(bundles) && bundles.length > 0) {
                    for (var idx in bundles) {
                        $.extend(true, prefs, this.model.preferences.toSchema(bundles[idx]));
                    }
                }

                //--- SET THE PREFERENCES
                content.SkillsPassport.PrintingPreferences = prefs;
                delete content.Preferences;

                //--- SET LOCALE
                if (locale !== undefined && locale !== null && locale !== "") {
                    content.SkillsPassport.Locale = locale;
                }

                //--- SET THE LAST UPDATE DATE
                content.SkillsPassport.DocumentInfo.LastUpdateDate = Config.getServerLastUpdateDate().toString(this.model.ISO_DATE_FORMAT);

                delete content.type;
                delete content.SessionID;

                return JSON.stringify(content);
            };
            /**
             * Translate a JSON string as managed by the REST API to a model as expected in EWA Editor.
             * Handle the session and preferences.
             * @param jsonString
             * @param doTranslation or not
             * @param preserve user prefs, such as Person name order
             */
            ConversionManager.prototype.fromTransferable = function (json, doTranslation, preservePrefs) {
                //--- READ AND SET THE MODEL
                // Modify the last update date with the current date
                var hasSkillsPassport = json.SkillsPassport !== undefined && json.SkillsPassport !== null;
                if (hasSkillsPassport === false) {
                    json.SkillsPassport = {};
                }
                var hasDocInfo = json.SkillsPassport.DocumentInfo !== undefined && json.SkillsPassport.DocumentInfo !== null;
                if (hasDocInfo === false) {
                    json.SkillsPassport.DocumentInfo = {};
                }

                json.SkillsPassport.DocumentInfo.LastUpdateDate = new XDate(Config.getServerDateTime(), true).toString(this.model.ISO_DATE_FORMAT);

                //Set the XSD Version if not already set
                if (json.SkillsPassport.DocumentInfo.XSDVersion === undefined || json.SkillsPassport.DocumentInfo.XSDVersion === null) {
                    json.SkillsPassport.DocumentInfo.XSDVersion = module.config().xsdversion || "V3.4";
                }
                //Set the Document Type if not already set
                if (json.SkillsPassport.DocumentInfo.DocumentType === undefined || json.SkillsPassport.DocumentInfo.DocumentType === null) {
                    json.SkillsPassport.DocumentInfo.DocumentType = module.config().documentType || "ECV";
                }
                //Set locale if not already set
                if (json.SkillsPassport.Locale === undefined || json.SkillsPassport.Locale === null) {
//				console.log("from transferable set locale because not defined");
                    json.SkillsPassport.Locale = module.config().locale || "en";
                }

                //first re-set
                this.model.unset("SkillsPassport");
                this.model.unset("PrintingPreferences");
                this.model.unset("Preferences");

                this.model.set(json);

                //--- READ AND SET THE SESSION
                var sessionID = json.SkillsPassport.SessionID;
                if (sessionID !== undefined && sessionID !== null && sessionID !== "") {
                    SessionManager.set(sessionID);
                }
                //--- READ AND SET THE PREFERENCES for all documents
                var prefs = json.SkillsPassport.PrintingPreferences;
                this.model.preferences.fromSchema(prefs);

                //translate?
                if (doTranslation === true) {
                    this.model.translation().perform(false, preservePrefs);
                }
            };
            /**
             * Read a JSON string stored as expected in EWA Editor to a model as expected by the EWA Editor.
             * Handle the session and preferences.
             * @param jsonString
             * @return Boolean or nothing
             */
            ConversionManager.prototype.fromStorable = function (jsonString) {
                //first re-set
                this.model.unset("SkillsPassport");
                this.model.unset("PrintingPreferences");
                this.model.unset("Preferences");

                //--- READ THE MODEL
                var json = JSON.parse(jsonString);

                //SET THE LOCALE TO THE UI LOCALE if not already set
                if (json.SkillsPassport.Locale === undefined || json.SkillsPassport.Locale === null) {
//				console.log("from transferable set locale because not defined");
                    json.SkillsPassport.Locale = module.config().locale || "en";
                }

                //--- SET THE MODEL
                this.model.set(json);

                //--- READ AND SET THE SESSION
                var sessionID = json.SkillsPassport.SessionID;
                if (sessionID !== undefined && sessionID !== null && sessionID !== "") {
                    SessionManager.set(sessionID);
                }
                //--- READ AND SET THE PREFERENCES for all documents
                var prefs = json.Preferences;
                if (Utils.isEmptyObject(prefs) === false) {
                    this.model.preferences.resetToAttrs(prefs);
                } else {
                    //try the existing prefs inside the model
                    var modelPrefs = json.SkillsPassport.PrintingPreferences;
                    this.model.preferences.fromSchema(modelPrefs);
                }
            };

            return ConversionManager;
        }
);