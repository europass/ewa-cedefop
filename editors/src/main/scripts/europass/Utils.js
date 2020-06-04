define('Utils',
        [
            'require',
            'jquery',
            'jqueryui',
            'jqueryui',
            'underscore',
            'i18n!localization/nls/EditorHelp',
            'europass/maps/LocaleMap',
            'europass/http/WindowConfigInstance',
            'cookie'
        ],
        function (require, $, jqueryui, jqueryui, _, EditorHelp, LocaleMap, WindowConfig, Cookies) {
            var Utils = {
                COOKIE_PATH: '/editors/',
                COOKIE_EXPIRATION_TIME: 10 * 365	//10 years
            };

            Utils.HTML_CHAR_MAP = {
                '<': '&lt;',
                '>': '&gt;',
                '&': '&amp;',
                '"': '&quot;',
                "'": '&#39;',
                "%": '&#37;'
            };

            Utils.SPECIAL_CHARS_ALIGNMENT_MAP = {
                "locale": {
                    "el": {
                        'Ό': 'Ο',
                        'Ή': 'Η',
                        'Ά': 'Α',
                        'Έ': 'Ε',
                        "ΟΎ": 'ΟΥ',
                        "ΑΊ": 'ΑΙ',
                        "ΕΊ": 'ΕΙ',
                        "ΟΊ": 'ΟΙ',
                        "Ύ": 'Υ',
                        "Ώ": 'Ω'
                    }
                }
            };

            /**
             * called from GoogleShareView
             */
            Utils.htmlUnescape = function (s) {
                if (s === undefined || s === null || s === "") {
                    return s;
                }
//			var indertedHTMLcharMap = Utils.invertObj(Utils.HTML_CHAR_MAP[ch]);
                return s.replace(/(&gt;)/g, ">").replace(/(&lt;)/g, "<");
            };

            /*		Utils.invertObj = function (obj) {
             var new_obj = {};
             for (var prop in obj) {
             if(obj.hasOwnProperty(prop)) {
             new_obj[obj[prop]] = prop;
             }
             }
             return new_obj;
             };
             */
            /**
             * remove tags <>
             * not used
             */
            /*Utils.htmlEscape = function (s) {
             if ( s === undefined || s === null || s === "" ){ return s;}
             
             return s.replace(/[<>]/g, function (ch) {
             return Utils.HTML_CHAR_MAP[ch];
             });
             };*/

            /**
             * many references
             * pgia: Fix for EWA-1441, escape '+' character before decoding
             */
            Utils.encodePlusCharPercent = function (s) {
                var str = s.replace(/\+/g, '<plus>');
                return str.replace(/%/g, '<percentage>');
            };
            /**
             * used by ModelRemoteUpload
             */
            Utils.deleteWinProperty = function (prop) {
                window[prop] = undefined;
                try {
                    delete window[prop];
                } catch (e) {
                    //do not throw and stop process;
                }
            };

            Utils.ATTRIBUTES_WITH_INDEX = ["name", "data-bind-name", "data-help-key", "data-index", "data-rel-section", "data-rel", "rel"];

            /**
             * used by HttpUtils, DocumentDateFormat.js, PreferencesSchema, 
             * ModalFormInteractions, conversionManager, PrintingPreferencesModel, 
             * TranslationManager, SkillsPassportRouter
             * 
             * could be substitute by $.isEmptyObject 
             */
            Utils.isEmptyObject = function (obj) {
                return (obj === undefined
                        || obj === null
                        || _.isEmpty(obj));
            };

            /**
             * used in ModalFormInteractions, TranslationManager, FormView, PersonalDataTreatment, SortView
             */
            Utils.isUndefined = function (value) {
                return (value === undefined || value === null || value === "");
            };

            Utils.NonBreakingSpaceRegExp = new RegExp("&nbsp;", "g");

            /**
             * used in FormView 
             */
            Utils.escapeHtml = function (str) {
                if (str === undefined || str === null || str === "") {
                    return str;
                }
                var html = str;
                //EWA-852 : Chrome uses &nbsp; as normal space.
                html = html.replace(Utils.NonBreakingSpaceRegExp, " ");
                html = html.replace(/(\n)*/gi, "");
                //html = $.trim(html);
                html = html.replace(/^(<br( )?[\/\\]?>)*$/gi, "");
                html = html.replace(/<br( )?>/gi, "<br/>");
                return html;
            };

            /**
             * not used
             */
            /*Utils.ahrefBlank = function( str ){
             var html = "<div id=\"temp\">" + str + "</div>";
             var el = $( html );
             el.find("a[href!=\"\"]").attr("target", "_blank");
             var newHtml = el.html();
             newHtml = newHtml.replace( /<br( )?>/gi, "<br/>" );//.html puts <br>
             return newHtml;
             };*/

            /**
             * used in PrintingPreferencesView
             */
            Utils.escapeForJQuery = function (str) {
                if (str === undefined || str === null) {
                    return str;
                }
                return str.replace(/(:|\.|\/|\[|\])/g, '\\$1');
            };
            /**
             * internal usage
             */
            Utils.escapeForRegExp = function (str) {
                if (str === undefined || str === null) {
                    return str;
                }
                return str.replace(/[-\[\]\/\{\}\(\)\*\+\?\.\\\^\$\|]/g, "\\$&");
            };
            /**
             * used in PrintingPreferencesModel
             */
            Utils.removePrefix = function (prefix, str) {
                if (str === undefined || str === null) {
                    return str;
                }
                prefix = (prefix.substr(-1, 1) === ".") ? prefix : (prefix + ".");
                var prefixIdx = str.indexOf(prefix);
                if (prefixIdx === 0) {
                    str = str.substr(prefix.length);
                }
                return str;
            };

            /**
             * used in PreferencesSchema, SkillsPassport, FormView, ListView, FormPrintingPreferencesView
             */
            Utils.removeSkillsPassportPrefix = function (str) {
                if (str === undefined || str === null) {
                    return str;
                }
                var prefix = "SkillsPassport.";
                var prefixIdx = str.indexOf(prefix);
                if (prefixIdx === 0) {
                    str = str.substr(prefix.length);
                }
                return str;
            };
            /**
             * used in PreferencesSchema, PrintingPreferencesModel, FormPrintingPreferencesView, PrintingPreferencesView 
             */
            Utils.addPrefix = function (prefix, str) {
                if (str === undefined || str === null) {
                    return str;
                }

                prefix = (prefix.substr(-1, 1) === ".") ? prefix : (prefix + ".");
                var prefixIdx = str.indexOf(prefix);
                if (prefixIdx < 0) {
                    str = prefix + str;
                }
                return str;
            };
            /**
             * used in SkillsPassport.js, PreferencesSchema
             */
            Utils.addSkillsPassportPrefix = function (str) {
                if (str === undefined || str === null) {
                    return str;
                }
                var prefix = "SkillsPassport.";
                var prefixIdx = str.indexOf(prefix);
                if (prefixIdx < 0) {
                    str = prefix + str;
                }
                return str;
            };

            /**
             * Replaces all occurrences of key within a str with a value
             * @param str
             * @param key
             * @param value
             * @param defaultValue
             * 
             * used in FileManager, HttpUtils, get_smart_text, TooltipView
             */
            Utils.replaceKey = function (str, key, value, defaultValue) {
                if (str === undefined || str === null || str === "") {
                    return "empty-text";
                }

                if (key === undefined || key === null || key === "") {
                    return str;
                }
                var undefinedValue = (value === undefined || value === null || value === "");
                var undefinedDefaultValue = (defaultValue === undefined || defaultValue === null || defaultValue === "");
                if (undefinedValue === true && undefinedDefaultValue === false) {
                    value = defaultValue;
                } else if (undefinedValue === true && undefinedDefaultValue === true) {
                    return str;
                }
                var escapedKey = Utils.escapeForRegExp(key);

                str = str.replace(new RegExp(escapedKey, "g"), value);

                return str;
            };
            /**
             * used in FormView.js
             */
            Utils.matchArray = function (str) {
                if (str === undefined || str === null || typeof str !== "string" || str === "") {
                    return false;
                }
                var match = str.match(/([\S]*)(\[(\d+)\])$/);
                return match;
            };
            /**
             * used in PrintingPreferencesView, 
             * could be replaced by $.inArray ?
             */
            Utils.inArray = function (str) {
                if (str === undefined || str === null || typeof str !== "string" || str === "") {
                    return false;
                }
                return (Utils.matchArray(str) != null);
            };
            /**
             * dublicate as PreferencesSchema.prototype.isIndexedTxt
             */
            /*Utils.isIndexedTxt = function( str ) {
             if ( str === undefined || str === null || typeof str !== "string" || str === ""){ return str;}
             return ( str.match(/\[\d+\]/g) !== null );
             };*/
            /**
             * used in PrintingPreferencesModel
             */
            Utils.ArraySuffix = ".array";
            Utils.ArraySuffixLength = Utils.ArraySuffix.length;
            Utils.appendArrayTxt = function (str) {
                if (str === undefined || str === null || str === "") {
                    return str;
                }
                if (str.indexOf(Utils.ArraySuffix, str.length - Utils.ArraySuffixLength) !== -1) {
                    return str;
                }
                return str + Utils.ArraySuffix;
            };

            /**
             * dublicate PreferencesSchema.prototype.toArrayTxt
             * used in PrintingPreferencesModel, SkillsPassport, FormPrintingPreferencesView
             */
            Utils.toArrayTxt = function (str) {
                if (str === undefined || str === null || str === "") {
                    return str;
                }
                return str.replace(/\[(\d+)\]/g, ".array[$1]");
            };
            /**
             * not used
             */
            /*Utils.toNonArrayTxt = function( str ) {
             if ( str === undefined || str === null || str === ""){ return str;}
             return str.replace(/\.array\[(\d+)\]/g , "[$1]" );
             };*/
            /**
             * used in FormView
             */
            Utils.toZeroArrayTxt = function (str) {
                if (str === undefined || str === null || str === "") {
                    return str;
                }
                return str.replace(/\[\d+\]/g, ".array[0]");
            };
            /**
             * not used
             */
            /*Utils.toIndexText = function( str ) {
             if ( str === undefined || str === null || str === ""){ return str;}
             return str.replace(/\[(\d+)\]/g , ".array._$1" );
             };*/
            /**
             * dublicate ? PreferencesSchema.prototype.toZeroIndexTxt
             * PrintingPreferencesModel, FormPrintingPreferencesView
             */
            Utils.toZeroIndexTxt = function (str) {
                if (str === undefined || str === null || str === "") {
                    return str;
                }
                return str.replace(/\[\d+\]/g, "[0]");
            };
            /**
             * used in PreferencesSchema, get_text helper ? HelperUtils, FormView, HelpView
             */
            Utils.removeIndexTxt = function (str) {
                if (str === undefined || str === null || str === "") {
                    return str;
                }
                return str.replace(/\[\d+\]/g, "");
            };
            /**
             * used in PrintingPreferencesModel
             */
            Utils.iterateObj = function (obj, callback, scope, level, jsonpath) {

                if (level === undefined || level === null) {
                    level = 0;
                }

                if (jsonpath === undefined || jsonpath === null) {
                    jsonpath = "";
                }

                if (obj === undefined || obj === null
                        || (obj !== null && typeof (obj) === "object" && $.isEmptyObject(obj))) {
                    return false;
                }
                for (var prop in obj) {
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

//				console.log("UTILS - prop: " + prop+
//						"\nUTILS - level: " + level+
//						"\nUTILS - jsonpath: " + jsonpath );
                    callback.apply(scope || this, [value, prop, level, jsonpath]);
                }
            };
            /**
             * used in PreferencesSchema, ComputerSkillsFormView, ForeignLanguageFormView, ElpOverviewFormView, FormView
             */
            Utils.objAttr = function (obj, jsonpath) {
                if (obj === undefined || obj === null
                        || (obj !== null && typeof (obj) === "object" && $.isEmptyObject(obj))) {
                    return false;
                }
                var value = obj;
                if (jsonpath !== undefined && jsonpath !== null && jsonpath !== ""
                        && typeof jsonpath === "string") {
                    var parts = jsonpath.split(".");
                    for (var i = 0; parts !== undefined && i < parts.length; i++) {
                        var part = parts[i];
                        var tmp = null;
                        var matchArray = Utils.matchArray(part);
                        if (matchArray !== null && matchArray[1] != null && matchArray[3] != null && value[matchArray[1]] != null) {
                            var partPath = matchArray[1];
                            var partIndex = matchArray[3];
                            tmp = value[ partPath ][ partIndex ];
                        } else {
                            tmp = value[ part ];
                        }
                        if (tmp === undefined || tmp === null) {
                            return false;
                        }
                        value = tmp;
                    }
                }
                return value;
            };
            /**
             * several usages in compose views
             */
            Utils.getListSection = function (str) {
                if (str === undefined || str === null || typeof str !== "string") {
                    return null;
                }
                var list = str;
                var matched = list.match(/\[\d+\]$/);
                var isIndexed = (matched !== null);
                if (isIndexed === true) {
                    list = list.substr(0, list.lastIndexOf("["));
                    return list;
                }
                return null;
            };
            /**
             * several usages
             */
            Utils.prepareModelAttr = function (key, value) {
                if (typeof value == "string") {
                    value = $.trim(value);
                    if (value.length == 0)
                        return null;
                }
                var json = {};
                json[key] = value;
                return json;
            };
            /**
             * not used
             */
            /*Utils.replaceLastIndex = function(str, oldIndex, newIndex){
             if ( str === undefined || str === null || str === ""){ return str;}
             var regexp = new RegExp("(\\["+oldIndex+"\\])(?=[^\\["+oldIndex+"\\]]*$)");
             var newStr = str.replace(regexp, "["+newIndex+"]"  );
             return newStr;
             };*/
            /**
             * not used
             */
            /*Utils.replaceAnyLastIndex = function(str, newIndex){
             if ( str === undefined || str === null || str === ""){ return str;}
             var regexp = new RegExp("(\\[\\d+\\])(?=[^\\[\\d+\\]]*$)");
             var newStr = str.replace(regexp, "["+newIndex+"]"  );
             return newStr;
             };*/
            /**
             * not used
             */
            /*Utils.getLastIndex = function(str){
             if ( str === undefined || str === null || typeof str !== "string" || str === "" ){ return null;}
             var regexp = new RegExp("\\[(\\d+)\\](?=[^\\[\\d+\\]]*$)");
             var match = str.match(regexp );
             return (match == null || (match != null && match.length < 2 ) )? null : match[1];
             };*/
            /**
             * used in PrinitngPreferencesModel, ListView, SortView, 
             */
            Utils.arrayMove = function (arr, old_index, new_index) {
                while (old_index < 0) {
                    old_index += arr.length;
                }
                while (new_index < 0) {
                    new_index += arr.length;
                }
                if (new_index >= arr.length) {
                    var k = new_index - arr.length;
                    while ((k--) + 1) {
                        arr.push(undefined);
                    }
                }
                arr.splice(new_index, 0, arr.splice(old_index, 1)[0]);
                return arr;
            };

            /**
             * several usages 
             */
            Utils.jId = function (str) {
                if (str === undefined) {
                    return "";
                }
                return '#' + Utils.escapeForJQuery(str);
            };

            /**
             * not used
             */
            /*Utils.getIndexedText = function( text, index ){
             var INDEX_TXT = "{{index}}";
             if ( index !== undefined && index!==null && index!== "" ){
             text = text.replace(INDEX_TXT, index );
             }
             return text;
             };*/
            /**
             * FACTORY METHOD
             * Loads and instantiates from the module specified by the 'requireName'.
             * There is no guarantee that the code will execute synchronously, 
             * because the module might not be loaded and cached.
             * For that reason instead of returning the instance, the instance is returned by running the supplied callback.
             * 
             * @param config
             * @param callback
             * @param scope
             * @param args, MUST BE ARRAY, otherwise ignored.
             * 
             * ?not used?
             */
            Utils.newInstance = function (config, callback, scope, args) {
                var requireName = config["_requireName"];

                var myObj = null;
                require([requireName], function (newModule) {
                    if ($.isFunction(newModule)) {
                        myObj = new newModule(config);
                        if ($.isArray(args)) {
                            args.push(myObj);
                        } else {
                            args = [myObj];
                        }
                        if (callback !== undefined && callback !== null && $.isFunction(callback)) {
                            callback.apply(scope, args);
                        }
                    }

                });
            };
            /**
             * Similar to Utils.newInstance, only that it will pass the callback to the EWA Backbone View
             * @param config
             * @param callback
             * @param scope
             * @param args
             * 
             * usages in ModalFormViewMap, composeView
             */
            Utils.newFormInstance = function (config, callback, scope, args) {
                var requireName = config["_requireName"];

                require([requireName], function (formViewModule) {
                    if ($.isFunction(formViewModule)) {

                        config.callback = callback;
                        config.scope = scope;
                        config.args = args;

                        new formViewModule(config);
                    }

                });
            };
            /**
             * Loads a resource using a dynamic require.
             * 
             * Note that since require is performed asynchronously,
             * a suitable callback must be provided in order for the processing to continue.
             * 
             * @param config
             * The config needs to supply the "_requireName".
             * Optionally, it may also include a _testRequireName and a _fallbackRequireName.
             * 
             * @param callback
             * @param scope
             * @param args
             * 
             * usages in EWABackboneView, DocumentControlsView
             */
            Utils.requireResource = function (config, callback, scope, args) {
                var requireName = config["_requireName"];
                var fallbackRequireName = config["_fallbackRequireName"];
                var testRequireName = config["_testRequireName"];

                var successFunction = function (resource) {
                    if ($.isFunction(resource)) {
                        if ($.isArray(args)) {
                            args.push(resource);
                        } else {
                            args = [resource];
                        }
                        if (callback !== undefined && callback !== null && $.isFunction(callback)) {
                            callback.apply(scope, args);
                        }
                    }
                };
                var errorFunction = function (error) {
//				console.log("Dynamic Require error, try fallback if exists");
                    if (fallbackRequireName !== undefined) {
                        require(
                                [fallbackRequireName],
                                successFunction
                                );
                    }
                };
                if (testRequireName === undefined) {
//				console.log("Dynamic Require no matter what");
                    require(
                            [requireName],
                            //SUCCESS
                            successFunction,
                            //ERROR
                            errorFunction
                            );
                } else {
                    //Check if exists...
                    require(
                            [testRequireName],
                            //SUCCESS
                                    function (testResource) {
//						console.log("Dynamic Require success");
                                        require(
                                                [requireName],
                                                successFunction
                                                );
                                    },
                                    //ERROR
                                    errorFunction
                                    );
                        }
            };
            /**
             * several usages
             */
            Utils.reIndexElement = function (el, replacement, regexIdx, attrName, extraAttrs) {
                if (regexIdx == null)
                    regexIdx = 0;

                var regexp = new RegExp("(\\[" + regexIdx + "\\])(?=[^\\[" + regexIdx + "\\]]*$)");

                var indexedAttrs = Utils.ATTRIBUTES_WITH_INDEX;
                if (extraAttrs !== undefined && $.isArray(extraAttrs)) {
                    indexedAttrs = $.merge(Utils.ATTRIBUTES_WITH_INDEX, extraAttrs);
                }

                $.each(indexedAttrs, function (idx, attrName) {
                    var attr = el.attr(attrName);
                    if (attr) {
                        var newAttr;
                        if (attrName === "data-index") {
                            var nextIdx = replacement.substring(1, replacement.length - 1);
                            newAttr = nextIdx;
                        } else {
                            newAttr = attr.replace(regexp, replacement);
                        }
                        el.attr(attrName, newAttr);
                    }

                });
            };
            /**
             * used in CompoundMultiFieldView, MultiFieldView, SingleFixedValueListView
             */
            Utils.reIndexElementAndChildren = function (el, replacement, regexIdx, attrName, extraAttrs) {
                Utils.reIndexElement(el, replacement, regexIdx, attrName, extraAttrs);
                //also update all its children
                el.find("*").each(function () {
                    Utils.reIndexElement($(this), replacement, regexIdx, attrName, extraAttrs);
                });
            };

            /**
             * Convert a Path returned from JSONPATH to valid jsonPath
             * used by TranslationManager
             */
            Utils.convertJsonPath = function (jpath) {
                //e.g. "$['SkillsPassport']['LearnerInfo']['WorkExperience'][0]['Employer']['ContactInfo']['Address']['Contact']['Country']['Code']"
                //convert to
                //"$['SkillsPassport']['LearnerInfo']['WorkExperience'][0]['Employer']['ContactInfo']['Address']['Contact']['Country']['Code']"
                //replace inner indexed
                var path = jpath.replace(/'\](\[\d+\])\['/g, "$1.");
                //replace inner
                path = path.replace(/'\]\['/g, ".");
                //replace first 
                path = path.replace(/\$\['/g, "");
                //replace last
                path = path.replace(/'\]/g, "");
                return path;
            };
            /**
             * used in LinkedAttachmentFormView
             */
            Utils.compareArray = function (arr1, arr2) {
                return ($(arr1).not(arr2).length == 0 && $(arr2).not(arr1).length == 0);
            };

            var EMAIL_REGEXP = /^[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*@(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?$/i;
            /**
             * used in RequestEmailView, SendFeedbackView
             */
            Utils.isValidEmail = function (str) {
                return EMAIL_REGEXP.test($.trim(str));
            };
            /**
             * Find the difference arr - filter.
             * Elements of arr that are not part of filter.
             * @param arr the original array
             * @param filter the array based on which to filter
             * 
             * used in Utils.chooseClassNames
             */
            Utils.setDifference = function (arr, filter) {
                var result = [];

                $.grep(arr, function (n, i) {
                    if ($.inArray(n, filter) < 0) {
                        result.push(n);
                    }
                });

                return result;
            };
            /**
             * used in Utils.chooseClassNames 
             */
            Utils.functionalClassNames = ["formfield", "pref", "help", "select2"];

            /**
             * Returns value in attribute "data-rel-section"
             * on the button/event.target object
             * several usages
             */
            Utils.getRelatedSection = function (event) {
                var btn = $(event.target);

                if (this.isUndefined(btn)) {
                    return null;
                }

                var rel = btn.attr("data-rel-section");

                if (this.isUndefined(rel)) {
                    return null;
                }

                return rel;
            };

            /**
             * Used to find the different class names of the input element
             * and return them
             * @param input
             * @returns
             * 
             * used in Select2AutocompleteView,  Select2MenuView
             */
            /*Utils.chooseClassNames = function(input){
             var clazzes = input.attr("class").split(" ");
             //get the of the two arrays
             var chosenNames = Utils.setDifference( $(clazzes), Utils.functionalClassNames );
             if ( chosenNames.length > 0 ) {
             return chosenNames.join(" ");
             } else {
             return "europass-select2-results";
             }
             };*/

            /** 
             * HASH CODE JAVA-like 
             * several uses
             */
            Utils.hashCode = function (str) {
                var hash = 0;
                if (str.length == 0)
                    return hash;
                for (var i = 0; i < str.length; i++) {
                    var charAt = str.charCodeAt(i);
                    hash = ((hash << 5) - hash) + charAt;
                    hash = hash & hash; // Convert to 32bit integer
                }
                var hashStr = "" + hash;
                if (hashStr.indexOf("-") === 0) {
                    hashStr = hashStr.substr(1);
                }
                return hashStr;
            };

            /**
             * several uses
             */
            Utils.randomInK = function () {
                var r = Math.random(); //between 0 and 1
                return parseInt(r * 1000);
            };

            /**
             * METADATA MANAGEMENT 
             * used in ImageFormView
             */
            Utils.getModelMetadata = function (model, path, metaKey) {
                if (model === undefined || model === null || path === undefined) {
                    return null;
                }
                //specific model, so get the object from there
                object = model.get(path);
                return Utils.getMetadata(object, metaKey);
            };
            /**
             * METADATA MANAGEMENT 
             * used in ImageFormView, and get_dimension_metadata get_obj_metadata helpers
             */
            Utils.getMetadata = function (object, metaKey) {

                if (object === undefined || object === null) {
                    return null;
                }

                var metadata = object.Metadata;

                if (metadata !== undefined && $.isArray(metadata) && metadata.length > 0) {

                    var metaValue = null;

                    $(metadata).each(function (idx, meta) {
                        if (meta.Key === metaKey) {
                            metaValue = meta.Value;
                            return false;
                        }
                    });
                    if (metaValue === null) {
                        return null;
                    }

                    if (metaValue.indexOf("{") === 0) { //is object
                        metaValue = JSON.parse(metaValue);
                    }
                    return metaValue;
                }
                return null;
            };
            /**
             * Return the index in the array of Metadata
             * @param object
             * @param metaKey
             * @returns
             * 
             * used in Utils.setMetadata
             */
            Utils.getMetadataIndex = function (object, metaKey) {
                if (object === undefined || object === null) {
                    return false;
                }

                var metadata = object.Metadata;

                if (metadata !== undefined && $.isArray(metadata) && metadata.length > 0) {

                    var index = -1;

                    $(metadata).each(function (idx, meta) {
                        if (meta.Key === metaKey) {
                            index = idx;
                            return false;
                        }
                    });
                    return index;
                }
                return -1;
            };
            /**
             * used in AttachmentManagerView, ImageFormView
             */
            Utils.removeMetadata = function (model, path, metaKey) {
                if (model === undefined || model === null || metaKey == null) {
                    return false;
                }

                var obj = model.get(path);
                if (obj === undefined || obj === null) {
                    return false;
                }

                var metadata = obj.Metadata;
                if (metadata === undefined) {
                    return false;
                }

                if ($.isArray(metadata) && metadata.length > 0) {
                    var index = -1;

                    $(metadata).each(function (idx, meta) {
                        if (meta.Key === metaKey) {
                            index = idx;
                            return false;
                        }
                    });
                    if (index == -1) {
                        return false;
                    }

                    //remove 1 at idx
                    metadata = metadata.splice(index, 1);
                }
                model.set(path, obj);
                //console.log( "REMOVE setting at '"+path+"': " + JSON.stringify(obj.Metadata) );
            };
            /**
             * Adds a metadata to the obj of the model at path
             * @param model, a Backbone Model
             * @param path, a Json path
             * @param metaKey
             * @param metaValue
             * 
             * @return false if failed
             * 
             * used in AttachmentManagerView, ImageFormView, 
             */
            Utils.setMetadata = function (model, path, metaKey, metaValue) {
                if (metaKey == null || metaValue == null) {
                    return false;
                }

                if (model === undefined || model === null) {
                    return false;
                }

                if (path === undefined || path === null || path === "") {
                    return false;
                }

                var obj = model.get(path);
                if (obj === undefined || obj === null) {
                    return false;
                }

                var value = metaValue;
                if ($.isPlainObject(value)) {
                    value = JSON.stringify(metaValue);
                }
                var metaJson = {
                    Key: metaKey,
                    Value: value
                };

                var metadata = obj.Metadata;
                //no metadata create new array and set the metaJson
                if (metadata === undefined) {
                    obj.Metadata = [];
                    metadata = obj.Metadata;
                    metadata.push(metaJson);
                } else {
                    //check if the metaJson already exists!
                    var existingIdx = Utils.getMetadataIndex(obj, metaKey);
                    if (existingIdx === -1) {
                        //does not exist... push
                        metadata.push(metaJson);
                    } else {
                        //replace
                        metadata[ existingIdx ] = metaJson;
                    }
                }
                model.set(path, obj);
                //console.log( "setting at '"+path+"': " + JSON.stringify(obj.Metadata) );
            };

            /**
             * Prepares a modal dialog
             * @param id
             * @param section
             * @returns
             * 
             * used in ComposeDragDropView, ModelRemoteUploadView
             */
            Utils.prepareModal = function (id, html, clazz, dialogClazz, removeClose) {

                var modalEl = $(Utils.jId(id));
                if (modalEl === undefined || modalEl === null || modalEl.length == 0) {
                    //The element does not exist, thus needs to be created
                    modalEl = $(document.createElement("form"));
                    //Add the html
                    if (html != null) {
                        modalEl.html(html);
                    }
                    modalEl.attr("id", id);
                    modalEl.attr("class", !clazz ? "modal-window" : clazz);

                    modalEl.dialog({
                        modal: true,
                        resizable: false,
                        draggable: false,
                        autoOpen: false,
                        closeText: "Click to close",
                        width: 675,
                        position: ["center", "center"],
                        dialogClass: !dialogClazz ? "modal-dialog" : dialogClazz,
                        show: {effect: "fade", duration: "normal"},
                        hide: {effect: "puff", duration: "normal", percent: 95}
                    });

                    //Close button tooltip
                    var closeEl = modalEl.parent().find(".ui-dialog-titlebar-close");
                    var closeTxt = EditorHelp["Modal.Help.Close"];
                    if (closeTxt == null) {
                        closeTxt = "Close dialog";
                    }

                    closeEl.removeClass("no-close");
                    if (removeClose) {
                        closeEl.addClass("no-close");
                    }
                    closeEl.addClass("tip spot");
                    closeEl.append('<span class=\"data-title\" style=\"display:none\">' + closeTxt + '</span>');
                    closeEl.click(function (event, ui) {
                        modalEl.trigger("europass:modal:dialog:closed");
                    });
                }
                return modalEl;
            };

            /**
             * Parses an HTML string and looks for a status meta tag and a application/json script tag
             * 
             * @param htmlStr
             * @returns an object with the status and the json response
             * 
             * used in ModelRemoteUpload
             */
            Utils.parseHtmlResponse = function (htmlStr) {
                if (htmlStr === undefined || htmlStr === null || htmlStr === "") {
                    return false;
                }

                var html = $("<div></div>").append(htmlStr);

                var response = {};
                var status = html.find("meta[name=\"status\"]").attr("content");

                var jsonStr = html.find("script[type=\"application/json\"]").html();
                if (jsonStr !== null) {
                    var json = $.parseJSON(jsonStr);
                    if (Utils.isEmptyObject(json) === false) {
                        response = json;
                    }
                }
                return {
                    status: status,
                    json: response
                };
            };

            /**
             * 
             * @param text: the given text
             * @param length: the limit after which the text needs to be cropped
             * @param crop: the first part before the 2 points
             * @returns
             * 
             * used in PreferencesSchema and crop_text helper
             */
            Utils.cropText = function (text, length, crop) {
                if (length === undefined || length === null) {
                    length = 68;//18
                }
                if (crop === undefined || crop === null) {
                    crop = 33; //8
                }

                if (text === undefined || text === null || text === "undefined" || text === "null") {
                    return "";
                }
                if (text.length > length)
                {
                    var tmp = text.substr(0, crop) + "..." + text.substr(text.length - crop, text.length - 1);
                    text = tmp;
                }
                return text;
            };

            /**
             * custom implementation of super/subscript
             * @param op (sup/sub)
             * 
             * not used
             */
            /*Utils.chooseScript = function( op ){
             
             var sub = document.createElement(op);
             var range = {};
             if (document.selection && document.selection.createRange) {
             range = document.selection.createRange();
             }else if (window.getSelection) {
             range = window.getSelection().getRangeAt(0);
             }else
             return;
             var content = range.extractContents();
             var tmp = new Array();
             var i = 0;
             var wrapAll = 0;
             while(content.childNodes.length>0){
             if(content.childNodes[0].nodeName == "SUP" || content.childNodes[0].nodeName == "SUB"){
             for(var j=0;j<content.childNodes[0].childNodes.length;j++){
             var temp = content.childNodes[0].childNodes[j];
             if(!Utils.isEmptyObject(temp)){
             if(wrapAll){
             sub.appendChild(temp);
             tmp.push('sub');
             }
             else
             tmp.push(temp);
             }
             }
             content.removeChild(content.childNodes[0]);
             }else if(content.childNodes[0].length > 0){
             wrapAll=1;
             sub.appendChild(content.childNodes[0]);
             tmp.push('sub');
             }else{
             content.removeChild(content.childNodes[0]);
             }
             }
             if(tmp.length>0){
             for(i=tmp.length-1;i>=0;i--){
             if(typeof tmp[i] === 'string')
             range.insertNode(sub);
             else
             range.insertNode(tmp[i]);
             }
             }
             };*/
            /**
             * several usages
             */
            Utils.filterEmptyVal = function () {
                var val = $(this).val();
                return val === undefined || val === null || val === "";
            };
            /**
             * used in ComputerSkillsformView, foreignLanguageFormView
             */
            Utils.filterNonEmptyVal = function () {
                var val = $(this).val();
                return val !== undefined && val !== null && val !== "";
            };

            /**
             * used in share views
             */
            Utils.getProtocolHost = function (urlStr) {
                var result = urlStr.match(/(http[s]?:\/\/)(www[0-9]?\.)?(.[^/:]+)/g);
                if (_.isArray(result))
                    if (result.length > 0)
                        return result[0];
                return "";
            };
            /**
             * used in cv/PersonalInfoFormView 
             */
            Utils.capitaliseFirstLetter = function (string) {
                return string.charAt(0).toUpperCase() + string.slice(1);
            };

            /** removes Empty (null or undefined) Keys In a given Object
             * @param object to iterate on
             * @param list: array of values for deletion (optional)
             * @returns processed object
             * 
             * used in ModelInfoManager
             */
            Utils.removeEmptyKeysInObject = function (obj, list) {
                if (list != undefined) {
                    for (var idx in list) {
                        if (obj[list[idx]] == null || obj[list[idx]] == undefined) {
                            delete obj[list[idx]];
                        }
                    }
                } else {
                    for (var idx in obj) {
                        if (obj[idx] == null || obj[idx] == undefined) {
                            delete obj[idx];
                        }
                    }
                }
                return obj;
            };

            Utils.getLocaleFromWindow = function () {
                return window.location.pathname.split("/")[2]; //TODO: get locale  in a cleaner way.
            };
            /**
             * used in TabletInteractions
             */
            Utils.getDocumentFromWindow = function () {
                return location.pathname.split("/")[3].toUpperCase(); //TODO: get Current Document  in a cleaner way.
            };
            /**
             * used in AutoComplereListInputView, SingleAutoCompleteListView
             */
            Utils.getDataExcludedSections = function (sections) {

                if (_.isUndefined(sections) || _.isNull(sections) || sections === "")
                    return [];

                var sectionsArray = sections.split(" ");

                for (var i = 0; i < sectionsArray.length; i++) {
                    sectionsArray[i] = Utils.addSkillsPassportPrefix(sectionsArray[i]);
                }

                return sectionsArray;
            };
            /**
             * several usages
             */
            Utils.modelArrayToValues = function (modelArray) {

                if (_.isUndefined(modelArray) || _.isNull(modelArray) || !$.isArray(modelArray))
                    return [];

                var values = [];
                for (var i = 0; i < modelArray.length; i++) {
                    if (!_.isUndefined(modelArray[i]) && !_.isNull(modelArray[i])
                            && !_.isUndefined(modelArray[i].Description) && !_.isNull(modelArray[i].Description)) {
                        values.push(modelArray[i].Description.Label);
                    }
                }

                return values;
            };

            /**
             * EWA 1636, returns whether the OS is Mac OS x , checking navigator.platform
             * https://stackoverflow.com/questions/10527983/best-way-to-detect-mac-os-x-or-windows-computers-with-javascript-or-jquery
             * 
             * used in WizardDoActionStep
             * 
             */
            Utils.isMacOS = function () {
                var isMac = navigator.platform.toUpperCase().indexOf('MAC') >= 0;
                return isMac;
            };
            /**
             * used in rightClickMenu and ImageFormView
             */
            Utils.isWindowsSafari = function () {
                var ua = navigator.userAgent.toLowerCase();
                return ua.indexOf("safari/") !== -1 && // It says it's Safari
                        ua.indexOf("windows") !== -1 && // It says it's on Windows
                        ua.indexOf("chrom") === -1;
            };

            /**
             * EWA-1641 Programmatically detect pop-up blockers
             * should be called programmaticaly(e.g. settimeout) and not from a user initiated call stack
             * 
             * used in JobPortalsView (download and main), WizardDoActionStep
             */
            Utils.tryOpeningPopUp = function (urlToOpen) {

                var allowed = true;
                var popup_window = window.open();
                try {
                    popup_window.close();
                } catch (e) {
                    //console.log("try popup false");
                    allowed = false;
                }

                return allowed;
            };

            // EWA-1840: Detect cookies blocked
            Utils.checkCookie = function () {
                var cookiesEnabled = ("cookie" in document && (document.cookie.length > 0 || (document.cookie = "test").indexOf.call(document.cookie, "test") > -1));
                return cookiesEnabled;
            };
            Utils.readCookie = function () {
                return Utils.readCookieByName(WindowConfig.cookieId);
            };
            Utils.createCookie = function () {
                var cookieValue = Utils.generateUUID();
                Cookies.set(WindowConfig.cookieId, cookieValue, {expires: Utils.COOKIE_EXPIRATION_TIME, path: Utils.COOKIE_PATH});
            };
            Utils.deleteCookie = function () {
                return Utils.deleteCookieByName(WindowConfig.cookieId);
            };

            // More generic handler methods for cookies
            Utils.readCookieByName = function (cookieID) {
                if (Cookies.get(cookieID)) {
                    return Cookies.get(cookieID);
                } else {
                    return false;
                }
            };
            Utils.createOrSetCookieByName = function (cookieName, cookieValue, expirationTime) {
                if (!expirationTime) {
                    expirationTime = Utils.COOKIE_EXPIRATION_TIME;
                }
                Cookies.set(cookieName, cookieValue, {expires: expirationTime, path: Utils.COOKIE_PATH});
            };
            Utils.deleteCookieByName = function (cookieID) {
                Cookies.remove(cookieID, {path: Utils.COOKIE_PATH});
            };

            Utils.generateUUID = function () {
                var d = new Date().getTime();
                if (window.performance && typeof window.performance.now === "function") {
                    d += performance.now(); //use high-precision timer if available
                }
                var uuid = 'xxxxxxxx-xxxx-4xxx-yxxx-xxxxxxxxxxxx'.replace(/[xy]/g, function (c) {
                    var r = (d + Math.random() * 16) % 16 | 0;
                    d = Math.floor(d / 16);
                    return (c === 'x' ? r : (r & 0x3 | 0x8)).toString(16);
                });
                return uuid;
            };

            Utils.getObjectType = function (object) {

                var objectConstructor = {}.constructor;
                if (!_.isUndefined(object) && !_.isNull(object)) {
                    if (object.constructor === objectConstructor) {
                        return "JSON";
                    } else {
                        return null;
                    }
                } else {
                    return null;
                }
            };

            /**
             * Function to set custom properties needed for Google Drive files
             * @param {type} key
             * @param {type} value
             * @param {type} visibility
             *
             */
            Utils.setGDriveProperty = function (key, value, visibility) {
                var json = {
                    "key": key,
                    "value": value,
                    "visibility": visibility
                };
                return json;
            };

            /**
             * Error function that passes status, responseText and messageLocation
             * to main Cloud error function (triggerResponseError)
             * @param {type} status
             * @param {type} responseText
             * @param {type} messageLocation
             * @returns void
             */
            Utils.triggerErrorWhenCloudLoginAction = function (status, responseText, messageLocation) {
                //Extract the JSON response from the response text
                var parsed = {};
                var errMessage = "";
                try {
                    if (!_.isUndefined(responseText)) {
                        try {
                            parsed = JSON.parse(responseText);
                        } catch (error) { //responseText is not JSON
                            errMessage = responseText;
                        }
                    }

                } catch (e) {
                    $("body").trigger("europass:waiting:indicator:cloud:hide", true);
                }

                if (!_.isUndefined(parsed.error)) {
                    if (!_.isUndefined(parsed.error.message))
                        errMessage = parsed.error.message;
                }

                $("body").trigger("europass:waiting:indicator:cloud:show", true);
                $("body").trigger("europass:cloud:manage:response:error", [status, errMessage, messageLocation]);
            };

            Utils.getCurrentTimestamp = function () {

                var now = new Date();
                var date = new XDate(now).toString('yyyyMMdd');
                var time = new XDate(now).toString('HHmmss');

                var timestamp = {
                    "date": date,
                    "time": time
                };

                return timestamp;
            };

            Utils.getFilenameTitle = function (fileName) {
                return fileName.substring(0, fileName.lastIndexOf("."));
            };

            Utils.isSkillsPassportObjectEmpty = function (data) {
                if ((_.isNull(data.SkillsPassport.Attachment) || _.isUndefined(data.SkillsPassport.Attachment))
                        && $.isEmptyObject(data.SkillsPassport.LearnerInfo) && $.isEmptyObject(data.SkillsPassport.CoverLetter)) {
                    return true;
                } else {
                    return false;
                }
            };

            Utils.getEmptySkillsPassportObject = function (locale, xsd, generator, comment, logoExists) {
                var ISO_DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.fffzzz";
                var currentDate = new XDate(WindowConfig.getServerLastUpdateDate(), true).toString(ISO_DATE_FORMAT);
                var obj = {
                    "SkillsPassport": {
                        "Locale": locale,
                        "PrintingPreferences": null,
                        "DocumentInfo": {
                            "DocumentType": "ECV",
                            "CreationDate": currentDate,
                            "LastUpdateDate": currentDate,
                            "XSDVersion": xsd,
                            "Generator": generator,
                            "Comment": comment,
                            "EuropassLogo": logoExists
                        },
                        "LearnerInfo": {},
                        "CoverLetter": {},
                        "Attachment": null
                    }
                };
                return JSON.stringify(obj);
            };

            Utils.checkModelInfoTypesNonEmpty = function (modelInfo) {
                var context = {};
                context.filetypes = [];

                if (modelInfo !== null) {

                    if (!modelInfo.isCVEmpty()) {
                        context.hasECV = true;
                        context.filetypes.push("CV");
                    }
                    if (!modelInfo.isESPEmpty()) {
                        context.hasESP = true;
                        context.filetypes.push("ESP");
                    }
                    if (!modelInfo.isLPEmpty()) {
                        context.hasELP = true;
                        context.filetypes.push("LP");
                    }
                    if (!modelInfo.isCLEmpty()) {
                        context.hasECL = true;
                        context.filetypes.push("CL");
                    }
                }
                return context;
            };

            Utils.htmlEscaping = function (str)
            {
                return String(str).replace(/&/g, '&amp;').replace(/</g, '&lt;').replace(/>/g, '&gt;').replace(/"/g, '&quot;');
            };

            Utils.isHostReachable = function () {
                // Handle IE and more capable browsers
                var xhr = new (window.ActiveXObject || XMLHttpRequest)("Microsoft.XMLHTTP");
                var status;

                // Open new request as a HEAD to the europass cedefop domain
                xhr.open("HEAD", "https://europass.cedefop.europa.eu", false);

                // Issue request and handle response
                try {
                    xhr.send();
                    return (xhr.status >= 200 && (xhr.status < 300 || xhr.status === 304));
                } catch (error) {
                    return false;
                }
            };

            Utils.EditorForms = {
                ecvFormsLoaded: false,
                espFormsLoaded: false,
                elpFormsLoaded: false,
                eclFormsLoaded: false
            };

            Utils.isPartnerAvailable = function () {
                return (!_.isEmpty(WindowConfig.remoteUploadPartnerKey) && !_.isEmpty(WindowConfig.remoteUploadCallbackUrl));
            };

            Utils.updateCVTitleFromLocale = function (title, locale) {

                var titleArr = title.split("-");
                var titleNew;
                var locales = LocaleMap.objs;
                var localeCodes = [];

                try {
                    for (var i = 0; i < locales.length; i++) {
                        localeCodes.push(locales[i].Code.toUpperCase());
                    }
                    if (titleArr.length === 5) {
                        if (localeCodes.indexOf(titleArr[3]) > -1) {
                            titleArr[3] = locale.toUpperCase();
                        }
                    } else if (titleArr.length === 6) {
                        // validation for locale...
                        if (localeCodes.indexOf(titleArr[3] + "-" + titleArr[4]) > -1) {
                            titleArr[3] = locale.toUpperCase();
                            titleArr.splice(4, 1);
                        }
                    }
                } catch (e) {
                    // when error happens ignore and use original title.
                } finally {
                    titleNew = titleArr;
                }

                return titleNew.toString().split(",").join("-");
            };

            /*
             * This method will get json object (root) and based on propertyName will return
             *  all paths of property with that propertyName name
             * */
            Utils.getJSONExtractedPathsByPropertyKey = function (root, propertyName) {
                var paths = [];
                var nodes = [{
                        obj: root,
                        path: []
                    }];

                while (nodes.length > 0) {
                    var n = nodes.pop();
                    for (var prop in n.obj) {
                        if (typeof n.obj[prop] === 'object') {
                            var path = n.path.concat(prop);
                            if (prop === propertyName) {
                                paths.push(path);
                            }
                            nodes.unshift({
                                obj: n.obj[prop],
                                path: path
                            });
                        }
                    }
                }

                return paths;
            };

            return Utils;
        }
        );

