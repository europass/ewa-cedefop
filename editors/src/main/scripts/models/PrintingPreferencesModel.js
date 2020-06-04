define(
        [
            'jquery',
            'underscore',
            'backbone',
            'backbonenested',
            'Utils',
            'i18n!localization/nls/PersonNameOrder',
            'i18n!localization/nls/DefaultDateFormat',
            'i18n!localization/nls/CLDefaultPrintingPreferences'
        ],
        function ($, _, Backbone, BackboneNested, Utils, PersonNameOrder, DefaultDateFormat, CLDefaultPrintingPreferences) {
            var PrintingPreferencesModel = Backbone.NestedModel.extend({

                PERSONNAME_KEY: "LearnerInfo.Identification.PersonName"
                , FIRSTNAME_BEFORE: "FirstName Surname"
                , SURNAME_BEFORE: "Surname FirstName"

                , defaults: {
                    ECV: {
                        LearnerInfo: {
                            order: "Identification Headline WorkExperience Education Skills Achievement ReferenceTo",
                            pageBreaks: " ",
                            Identification: {
                                PersonName: {
                                    show: true,
                                    order: PersonNameOrder.order === "true" ? "FirstName Surname" : "Surname FirstName"
                                },
                                /*ContactInfo: {
                                 Address : {
                                 format : AddressFormat["default"]
                                 }
                                 },*/
                                Demographics: {
                                    Birthdate: {
                                        format: DefaultDateFormat.ECV
                                    }
                                }
                            },
                            WorkExperience: {
                                array: [{
                                        Period: {
                                            format: DefaultDateFormat.ECV
                                        }/*,
                                         Employer: {
                                         ContactInfo:{
                                         Address :{
                                         format : OrganisationAddressFormat.format
                                         }
                                         }
                                         }*/
                                    }]
                            },
                            Education: {
                                array: [{
                                        Period: {
                                            format: DefaultDateFormat.ECV
                                        }/*,
                                         Organisation: {
                                         ContactInfo:{
                                         Address :{
                                         format : OrganisationAddressFormat.format
                                         }
                                         }
                                         }*/
                                    }]
                            }
                        }
                    },
                    ELP: {
                        LearnerInfo: {
                            order: "Identification Skills ReferenceTo",
                            Identification: {
                                PersonName: {
                                    order: PersonNameOrder.order === "true" ? "FirstName Surname" : "Surname FirstName"
                                }
                            },
                            Skills: {
                                Linguistic: {
                                    ForeignLanguage: {
                                        array: [{
                                                Certificate: {
                                                    array: [{
                                                            Date: {
                                                                format: DefaultDateFormat.ELP
                                                            }
                                                        }]
                                                },
                                                Experience: {
                                                    array: [{
                                                            Period: {
                                                                format: DefaultDateFormat.ELP
                                                            }
                                                        }]
                                                }
                                            }]
                                    }
                                }
                            },
                            CEFLanguageLevelsGrid: {
                                show: true
                            }
                        }
                    },
                    ECL: {
                        LearnerInfo: {
                            Identification: {
                                PersonName: {
                                    order: PersonNameOrder.order === "true" ? "FirstName Surname" : "Surname FirstName"
                                }
                            }/*,
                             ContactInfo: {
                             Address : {
                             format : AddressFormat["default"]
                             }
                             }*/
                        },
                        CoverLetter: {
                            Justification: {
                                justify: CLDefaultPrintingPreferences["CoverLetter.Justification"].justify
                            },
                            SignatureName: {
                                enableName: CLDefaultPrintingPreferences["CoverLetter.SignatureName"].enableName
                            },
                            order: CLDefaultPrintingPreferences["CoverLetter"].order,
                            Addressee: {
                                position: CLDefaultPrintingPreferences["CoverLetter.Addressee"].position,
                                PersonName: {
                                    show: true,
                                    order: CLDefaultPrintingPreferences["CoverLetter.Addressee.PersonName"].order
                                }/*,
                                 Organisation:{
                                 ContactInfo: {
                                 Address : {
                                 format : AddressFormat["default"]
                                 }
                                 }
                                 }*/
                            },
                            Letter: {
                                Localisation: {
                                    order: CLDefaultPrintingPreferences["CoverLetter.Letter.Localisation"].order,
                                    position: CLDefaultPrintingPreferences["CoverLetter.Letter.Localisation"].position,
                                    Date: {
                                        format: DefaultDateFormat.ECL
                                    }
                                },
                                ClosingSalutation: {
                                    show: CLDefaultPrintingPreferences["CoverLetter.Letter.ClosingSalutation"].show
                                }
                            }
                        }
                    }
                },
                initialize: function () {
                    this._originalAttributes = $.extend(true, {}, this.defaults);
                    this._originalModel = new Backbone.NestedModel(this._originalAttributes);
                },
                getDefaultsModel: function () {
                    return this._originalModel;
                },
                documents: [
                    "ECV",
                    "ELP",
                    "ECL"
                ],
                loopDocuments: function (callback, scope, args) {
                    for (var i = 0; i < this.documents.length; i++) {
                        var nextDoc = this.documents[i];
                        var nextPrefs = this.get(nextDoc);
                        var nextArgs = [nextDoc, nextPrefs];

                        if ($.isArray(args) === true) {
                            for (var j = 0; j < args.length; j++) {
                                nextArgs.push(args[j]);
                            }
                        }
                        callback.apply(scope, nextArgs);
                    }
                },
                /*******************************************************************/
                /**
                 * Loads a Model from the structured as produced by the schema.
                 * When no specific document is targeted, then all document preferences will try to get loaded.
                 * @public
                 */
                fromSchema: function (map, document) {
                    var parsed = this.readFromSchema(map, document);
                    if (parsed !== false) {
                        this.resetToAttrs(parsed);
                    }
                },
                /**
                 * Prepare the Preferences model attributes according to the map provided 
                 * (the map is valid according to Europass Schema for Preferences).
                 * 
                 * When no specific document is targeted, then all document preferences will try to get loaded.
                 * 
                 * @param map
                 * @param document
                 * @returns a JSON object for the printing preferences
                 * e.g. {
                 * 	ECV : { LearnerInfo ... },
                 *  ELP : { LearnerInfo ... }
                 *   CL : { LearnerInfo ... }
                 * }
                 * private
                 */
                readFromSchema: function (map, document) {
                    if (map === undefined || map === null) {
                        return false;
                    }

                    var model = new Backbone.NestedModel();
                    //No document type define - prepare all
                    if (document === undefined || document === null || document === "") {
                        this.loopDocuments(this.readFromSchemaPerDocument, this, [map, model]);
                    }
                    //Document given
                    else {
                        this.readFromSchemaPerDocument(document, null, map, model);
                    }
                    return model.attributes;
                },
                /**
                 * Used by readFromSchema to handle preferences per document type.
                 * @param document
                 * @param prefs
                 * @param model
                 * @returns void - modifies the model
                 * @private
                 */
                readFromSchemaPerDocument: function (document, prefs, map, model) {
                    if (Utils.isEmptyObject(map)) {
                        return false;
                    }

                    var thisMap = map[ document ];

                    if ($.isArray(thisMap) === false) {
                        return false;
                    }

                    for (var i = 0, len = thisMap.length; i < len; i++) {
                        var pref = thisMap[i];
                        this.processReadPref(document, model, pref);
                    }
                },
                /**
                 * Used by readFromSchema to handle individual preference
                 * @param document
                 * @param model
                 * @param pref
                 * @returns void - modifies the model
                 * @private
                 */
                processReadPref: function (document, model, pref) {
                    if (pref === undefined || pref === null) {
                        return false;
                    }
                    var name = Utils.addPrefix(document, pref.name);
                    var show = pref.show;
                    var order = pref.order;
                    var format = pref.format;
                    var justify = pref.justify;
                    var enableName = pref.enableName;
                    var pageBreaks = pref.pageBreaks;

                    if (name !== null) {
                        var toArrayName = Utils.toArrayTxt(name);
                        var existing = model.get(toArrayName);
                        if (existing === undefined || existing === null) {
                            model.set(toArrayName, {}, {silent: true});
                        }
                        if (show) {
                            model.set(toArrayName + ".show", show, {silent: true});
                        }
                        if (order) {
                            model.set(toArrayName + ".order", order, {silent: true});
                        }
                        if (format) {
                            model.set(toArrayName + ".format", format, {silent: true});
                        }
                        if (justify) {
                            model.set(toArrayName + ".justify", justify, {silent: true});
                        }
                        if (enableName === false || enableName === true) {
                            model.set(toArrayName + ".enableName", enableName, {silent: true});
                        }
                        if (pageBreaks) {
                            model.set(toArrayName + ".pageBreaks", pageBreaks, {silent: true});
                        }
                    }
                },
                /*******************************************************************/

                /**
                 * Converts the Model to the structure as expected by the schema.
                 * When no specific document is given, the all document preferences will be serialized
                 * @param document
                 * @public
                 */
                toSchema: function (document) {
                    var finalMap = {};

                    if (document === undefined || document === null || document === "") {
                        this.loopDocuments(this.toSchemaPerDocument, this);
                    } else {
                        finalMap[document] = this.toSchemaPerDocument(document)[document];
                    }
                    return finalMap;
                },
                /**
                 * Used by toSchema
                 * @param document
                 * @returns a json object holding the preferenes per document
                 * @private
                 */
                toSchemaPerDocument: function (document) {
                    if (document === undefined || document === null || document === "") {
                        return;
                    }

                    var prefsArray = [];

                    var callback = function (pref, key, level, jsonpath) {
                        var path = jsonpath;

                        if (key !== "array") {
                            var digit = key.match(/\d+/g);
                            if (digit === null) {
                                path = jsonpath + (jsonpath === "" ? "" : ".") + key;
                            } else {
                                path = jsonpath + "[" + digit + "]";
                            }
                        }
//				console.log("MODEL - key: " + key +"\nMODEL - level: " + level +"\nMODEL - path: " + path);
                        if (pref !== null) {
                            var prefItem = {
                                name: Utils.removePrefix(document, path)
                            };
                            var show = pref.show;
                            var order = pref.order;
                            var format = pref.format;
                            var justify = pref.justify;
                            var enableName = pref.enableName;
                            var pageBreaks = pref.pageBreaks;
                            if (show) {
                                prefItem.show = show;
                            }
                            if (order) {
                                prefItem.order = order;
                            }
                            if (format) {
                                prefItem.format = format;
                            }
                            if (justify) {
                                prefItem.justify = justify;
                            }
                            if (enableName === false || enableName === true) {
                                prefItem.enableName = enableName;
                            }
                            if (pageBreaks) {
                                prefItem.pageBreaks = pageBreaks;
                            }
//					console.log( "Add: " + prefItem.name );
                            prefsArray.push(prefItem);
                        }
                        Utils.iterateObj(pref, callback, this, (level + 1), path);
                    };
                    Utils.iterateObj(this.get(document), callback, this);
                    var map = {};
                    map[document] = prefsArray;
                    return map;
                },

                /*******************************************************************/
                emptyDocument: function (document) {
                    var j = {};
                    j[ document ] = "";
                    return j;
                },
                /*******************************************************************/
                /*******************************************************************/
                reorderArray: function (document, path, startPos, endPos) {
                    if (document === undefined || document === null || document === "") {
                        this.loopDocuments(this.reorderArrayPerDocument, this, [path, startPos, endPos]);
                    } else {
                        this.reorderArrayPerDocument(document, null, path, startPos, endPos);
                    }
                },
                reorderArrayPerDocument: function (document, prefs, path, startPos, endPos) {
                    var fullPath = Utils.addPrefix(document, path);

                    var prefs = this.get(fullPath);
                    if ($.isArray(prefs)) {
                        Utils.arrayMove(prefs, startPos, endPos);
                    }
                },
                /*******************************************************************/
                /*******************************************************************/
                removeArrayItem: function (document, path) {
                    if (document === undefined || document === null || document === "") {
                        this.loopDocuments(this.removeArrayItemPerDocument, this, [path]);
                    } else {
                        this.removeArrayItemPerDocument(document, null, path);
                    }
                },
                removeArrayItemPerDocument: function (document, prefs, path) {
                    var fullPath = Utils.addPrefix(document, path);
                    //Simply remove from array, the rest will be-re-ordered
                    if (this.get(fullPath)) {
                        this.remove(fullPath);
                    }
                },
                /*******************************************************************/
                /*******************************************************************/
                /**
                 * Reset the preference denoted by the given path to its default.
                 * 
                 * @param document
                 * @param path
                 */
                resetPreference: function (document, path) {
                    if (document === undefined || document === null || document === "") {
                        this.loopDocuments(this.resetPreferencePerDocument, this, [path]);
                    } else {
                        this.resetPreferencePerDocument(document, null, path);
                    }
                },
                /**
                 * Reset the preference denoted by the given path to its default.
                 * 
                 * @param document
                 * @param prefs (that correspod to this document)
                 * @param path
                 */
                resetPreferencePerDocument: function (document, prefs, path) {
                    var fullPath = Utils.addPrefix(document, path);

                    var currentPref = this.get(fullPath);
                    var defaultPref = this._originalModel.get(Utils.toZeroIndexTxt(fullPath));

                    if (defaultPref === undefined || defaultPref === null) {
                        return false;
                    }

                    if (defaultPref.show !== null) {
                        currentPref.show = defaultPref.show;
                    }
                    if (defaultPref.format !== null) {
                        currentPref.format = defaultPref.format;
                    }
                    if (defaultPref.order !== null) {
                        currentPref.order = defaultPref.order;
                    }

                    if (defaultPref.justify !== null) {
                        currentPref.justify = defaultPref.justify;
                    }
                    if (defaultPref.enableName !== null) {
                        currentPref.enableName = defaultPref.enableName;
                    }
                    if (defaultPref.pageBreaks !== null) {
                        currentPref.pageBreaks = defaultPref.pageBreaks;
                    }

                },
                /*******************************************************************/
                /*******************************************************************/
                /**
                 * Resets the model to the default values
                 * @param document
                 * 
                 * @public
                 */
                resetToDefaults: function (document) {
                    if (document === undefined || document === null || document === "") {
                        this.loopDocuments(this.resetToDefaultsPerDocument, this);
                    } else {
                        this.resetToDefaultsPerDocument(document);
                    }
                },
                resetToDefaultsPerDocument: function (document) {
                    var tmp = {};
                    tmp[ document ] = this._originalAttributes[document];

                    var json = {};
                    $.extend(true, json, tmp);

                    //empty first
                    this.set(this.emptyDocument(document));
                    this.set(json);
                },
                /*******************************************************************/
                /*******************************************************************/
                /**
                 * Resets the model to a different one
                 * 
                 * @param otherModel
                 * @param document
                 */
                resetToOther: function (otherModel, document) {

                    if (document === undefined || document === null || document === "") {
                        this.loopDocuments(this.resetToOtherPerDocument, this, [otherModel]);
                    } else {
                        this.resetToOtherPerDocument(document, null, otherModel);
                    }
                },
                resetToOtherPerDocument: function (document, prefs, otherModel) {
                    var otherAttrs = otherModel.get(document);
                    if (otherAttrs === undefined || otherAttrs === null) {
                        return false;
                    }
                    var tmp = {};
                    tmp[document] = otherAttrs;
                    var json = {};
                    $.extend(true, json, tmp);

                    //empty first
                    this.set(this.emptyDocument(document));
                    this.set(json);
                },
                /******************************************************************************************/
                applyDefaultPersonNameOrder: function (document, doesNotExist, noOrder) {
                    if (document === undefined || document === null || document === "") {
                        this.loopDocuments(this.applyDefaultPersonNameOrderPerDocument, this, [doesNotExist, noOrder]);
                    } else {
                        this.applyDefaultPersonNameOrderPerDocument(document, null, doesNotExist, noOrder);
                    }
                },
                applyDefaultPersonNameOrderPerDocument: function (document, prefs, doesNotExist, noOrder) {
//			console.log("document: "+document);
                    var name = Utils.addPrefix(document, this.PERSONNAME_KEY);
                    //the default order per language, if true then first name comes first
                    var hasOrder = PersonNameOrder.order;

                    var order = null;

                    switch (hasOrder) {
                        case "true":
                        {
                            order = this.FIRSTNAME_BEFORE;
                            break;
                        }
                        case "false" :
                        {
                            order = this.SURNAME_BEFORE;
                            break;
                        }
                        default:
                        {
                            order = this.FIRSTNAME_BEFORE;
                            break;
                        }
                    }
                    if (doesNotExist === undefined || doesNotExist === null) {
                        var existingPref = this.get(name);
                        doesNotExist = (existingPref === undefined || existingPref === null);
                    }
//			console.log("Pref exists?: "+!doesNotExist);
//			console.log("Force update?: " +noOrder);
                    //add if preference does not exist
                    if (doesNotExist) {
                        this.addPreference(name, true, order, null, null, null, null);
                    }
                    //update if preference exists, but there is no order
                    else if (!doesNotExist && noOrder) {
                        this.updatePreference(name, true, order, null, null, null, null);
                    }
                },
                /******************************************************************************************/
                /** I T E R A T I O N **/
                /******************************************************************************************/
                /**
                 * Iterate the object and apply the callback
                 * @param callback
                 * @param scope
                 * @param regexp
                 */
                iterate: function (callback, scope, regularExpression) {

                    if (document === undefined || document === null || document === "") {
                        this.loopDocuments(this.iteratePerDocument, this, [callback, scope, regularExpression]);
                    } else {
                        this.iteratePerDocument(document, null, callback, scope, regularExpression);
                    }
                },
                iteratePerDocument: function (document, prefs, callback, scope, regularExpression) {
                    var obj = this.attributes;

                    var that = this;
                    var recursiveCallback = function (value, valueName, regexp, applyCallback) {
                        if (applyCallback) {
                            callback.apply(scope, [value, valueName]);
                        }

                        that.iterateRecursive(value, valueName, regexp, recursiveCallback, scope);
                    };
                    this.iterateRecursive(obj, "", regularExpression, recursiveCallback, scope);
                },
                /**
                 * @param obj
                 * @param objName
                 * @param regexp
                 * @param callback
                 * @param scope
                 */
                iterateRecursive: function (obj, objName, regexp, callback, scope) {

                    for (var prop in obj) {
//				console.log('prop: ' + prop) ;
                        if (!obj.hasOwnProperty(prop)) {
                            continue;
                        }

                        var value = obj[ prop ];
                        if (value === undefined || value === null) {
                            continue;
                        }
                        if (typeof value === "function") {
                            continue;
                        }
                        if (typeof value !== "object") {
                            continue;
                        }

                        var valueName = this.buildPrefName(objName, prop);
//				console.log('iterate: ' + valueName) ;
                        if (regexp !== undefined && regexp !== null) {
                            if (prop.search(regexp) !== -1) {//matches!
                                callback.apply(scope || this, [value, valueName, regexp, true]); //call the function
                            } else {
                                //continue iteration
                                callback.apply(scope || this, [value, valueName, regexp, false]);
                            }
                        } else {
                            callback.apply(scope || this, [value, valueName, regexp, true]);
                        }
                    }
                },
                IS_ARRAY_REGEXP: new RegExp(".\.array$"),

                buildPrefName: function (prev, key) {
                    var name = "";
                    if (_.isString(prev)) {
                        if (prev === "") {
                            name = key;
                        } else {
                            if (this.IS_ARRAY_REGEXP.test(prev) === true && _.isNumber(parseInt(key))) {
                                name = prev + "[" + key + "]";
                            } else {
                                name = prev + "." + key;
                            }
                        }
                    }

                    return name;
                },
                /******************************************************************************************/
                /** The functions below considers the prefPath as given (includes document if necessary) **/
                /******************************************************************************************/
                /**
                 * Resets the model according to the supplied attributes
                 * @param attrs
                 */
                resetToAttrs: function (attrs) {
                    if (attrs === undefined || attrs === null) {
                        return false;
                    }
                    var newAttrs = {};
                    $.extend(true, newAttrs, attrs);
                    //empty first - TODO check this!!
                    this.unset();
                    this.set(newAttrs);

                },
                /**
                 * Update an already existing preference
                 * @param prefPath
                 * @param show
                 * @param order
                 * @param format
                 * @returns {Boolean}
                 */
                updatePreference: function (prefPath, show, order, format, justify, enableName, pageBreaks) {
                    //console.log( "Update preference for " + prefPath );
                    var pref = this.get(prefPath);
                    if (pref === undefined || pref === null) {
                        return false;
                    }
                    if (show !== null) {
                        pref.show = show;
                    }
                    if (order !== null) {
                        pref.order = order;
                    }
                    if (format !== null) {
                        pref.format = format;
                    }
                    if (justify !== null) {
                        pref.justify = justify;
                    }
                    if (enableName !== null) {
                        pref.enableName = enableName;
                    }
                    if (pageBreaks !== null) {
                        pref.pageBreaks = pageBreaks;
                    }
                },
                /**
                 * Adds a Printing Preference
                 * 
                 * @param name
                 * @param show
                 * @param order
                 * @param format
                 */
                addPreference: function (prefPath, show, order, format, justify, enableName, pageBreaks) {
                    //console.log( "Set preference for " + prefPath );
                    if (!prefPath) {
                        return false;
                    }

                    if (this.has(prefPath) === true) {
                        //already exists, so update
                        this.updatePreference(prefPath, show, order, format, justify, enableName, pageBreaks);
                    } else {
                        //does not exist, so set....

                        //PREF is ARRAY ITEM
                        var match = prefPath.match(/([\S]*)(\[(\d+)\])$/);
                        var inArray = (match !== null);
                        if (inArray) {
                            //e.g. ContactInfo.Telephone.array[1]
                            var array = match[1];

                            if (array === undefined || array === null || array === "") {
                                return false;
                            }

                            //Check if array already exists in pref-model and if not add it 
                            if (this.has(array) === false) {
                                var arrayJson = {};
                                arrayJson[array] = [];
                                this.set(arrayJson, {silently: true});
                            }

                            var pref = {};
                            if (show !== null) {
                                pref.show = show;
                            }
                            if (order !== null) {
                                pref.order = order;
                            }
                            if (format !== null) {
                                pref.format = format;
                            }
                            if (justify !== null) {
                                pref.justify = justify;
                            }
                            if (enableName !== null) {
                                pref.enableName = enableName;
                            }
                            if (pageBreaks !== null) {
                                pref.pageBreaks = pageBreaks;
                            }

                            this.set(prefPath, pref);

                        }
                        //PREF IS NOT ARRAY ITEM
                        else {

                            if (show !== null) {
                                this.set(prefPath, {}, {silently: true});
                            }
                            if (order !== null) {
                                this.set(Utils.prepareModelAttr(prefPath + ".show", show), {silently: true});
                            }
                            if (order !== null) {
                                this.set(Utils.prepareModelAttr(prefPath + ".order", order), {silently: true});
                            }
                            if (format !== null) {
                                this.set(Utils.prepareModelAttr(prefPath + ".format", format), {silently: true});
                            }
                            if (justify !== null) {
                                this.set(Utils.prepareModelAttr(prefPath + ".justify", justify), {silently: true});
                            }
                            if (enableName !== null) {
                                this.set(Utils.prepareModelAttr(prefPath + ".enableName", enableName), {silently: true});
                            }
                            if (pageBreaks !== null) {
                                this.set(Utils.prepareModelAttr(prefPath + ".pageBreaks", pageBreaks), {silently: true});
                            }
                        }

                    }
                },
                listName: function (path) {
                    if (path.indexOf(".array") < 0) {
                        return path;
                    }
                    return path.substr(0, (path.lastIndexOf(".array") + ".array".length));
                }

                , getActiveListItems: function (section) {
                    //e.g. LearnerInfo.Skills.Linguistic.ForeignLanguage becomes LearnerInfo.Skills.Linguistic.ForeignLanguage.array
                    var prefPath = Utils.appendArrayTxt(section);

                    var list = this.get(prefPath);
                    if ($.isArray(list) === false) {
                        return [];
                    }
                    var items = [];
                    for (var i = 0; i < list.length; i++) {
                        var item = list[i];
                        if (item.show === true) {
                            items.push(item);
                        }
                    }
                    return items;
                }
            });
            return PrintingPreferencesModel;
        });