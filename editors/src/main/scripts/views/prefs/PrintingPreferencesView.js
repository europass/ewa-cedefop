/**
 * Core View for Printing Preferences.
 * Includes basic functions reused by the Views extending this core view.
 * 
 */
define(
        [
            'jquery',
            'underscore',
            'backbone',
            'Utils',
            'europass/GlobalDocumentInstance',
            'i18n!localization/nls/PersonNameOrder'
        ],
        function ($, _, Backbone, Utils, GlobalDocument, PersonNamesOrder) {
            var PrintingPreferencesView = Backbone.View.extend({

                bindGroup_attr: "data-bind-group"
                , bindName_attr: "data-bind-name"
                , bindDocument_attr: "data-bind-document"
                , name_attr: "name"
                , LEARNERINFO_KEY: "LearnerInfo"
                , CL_JUSTIFICATION: "CoverLetter.Justification"
                , CL_CLOSINGSALUTATIONNAMEENABLED: "CoverLetter.SignatureName"
                , SECTION_ORDER: "Identification Headline WorkExperience Education Skills Achievement ReferenceTo"
                , PAGE_BREAKS: " "
                , PERSONNAME_KEY: "LearnerInfo.Identification.PersonName"
                , CEFRLEVELS_KEY: "LearnerInfo.CEFLanguageLevelsGrid"
                , STATUSES: {
                    ALL: "A",
                    SUGGESTED: "S",
                    FILLED: "F",
                    CUSTOM: "C"
                }
                , initialize: function () {
                    this.prefModel = this.model.preferences;
                    this.prefsDocument = GlobalDocument.getPrefDocument();
                }
                , updatePref: function (el) {

                    if (this.prefModel === null) {
                        return false;
                    }

                    //This may be an array
                    var bindGroup = el.attr(this.bindGroup_attr);
                    var bindName = this.getInputPrefName(el);
                    var bindDocument = this.getInputPrefDocument(el);

                    var that = this;
                    if (this.groupBindingOnly(bindGroup, bindName)) {
                        var bindGroups = (bindGroup === undefined || bindGroup === null || bindGroup === "") ? [] : $(bindGroup.split(" "));
                        bindGroups.each(function (idx, group) {
                            //bind to group if NOT already bound
                            if (that.findPreference(group) === null) {
                                that.addDocumentsPreference(bindDocument, group, true, null, null);
                            } else {
                                that.updatePreference(group, "show", true);
                            }
                        });
                    } else if (this.groupAndIndividualBinding(bindGroup, bindName)) {
                        var bindGroups = (bindGroup === undefined || bindGroup === null || bindGroup === "") ? [] : $(bindGroup.split(" "));
                        bindGroups.each(function (idx, group) {
                            //bind to group if NOT already bound
                            if (that.findPreference(group) === null) {
                                that.addDocumentsPreference(bindDocument, group, true, null, null);
                            } else {

                                that.updateDocumentsPreference(bindDocument, group, true, null, null);
                            }
                        });
                        //as well as to individual using data-bind-name
                        if (this.findPreference(bindName) === null) {
                            this.addDocumentsPreference(bindDocument, bindName, true, null, null);
                        } else {
                            this.updateDocumentsPreference(bindDocument, bindName, true, null, null);
                        }
                    } else {
                        //Bind to individual only using name if NOT already bound
                        var name = null;
                        if ((bindName !== undefined && bindName !== "" && bindName !== "group")) {
                            name = bindName;
                            if (this.findPreference(name) === null) {
                                this.addDocumentsPreference(bindDocument, name, true, null, null);
                            } else {
                                this.updatePreference(name, "show", true);
                            }
                        }
                    }
                }
                , unbindPref: function (el) {
                    var bindGroup = el.attr(this.bindGroup_attr);
                    var bindName = el.attr(this.bindName_attr);
                    var bindDocument = this.getInputPrefDocument(el);

                    if (this.groupBindingOnly(bindGroup, bindName)) {
                        //unbind group if already bound and if all related inputs have NO value
                        this.unbindGroup(bindDocument, bindGroup);
                    } else if (this.groupAndIndividualBinding(bindGroup, bindName)) {
                        //unbind group if already bound
                        this.unbindGroup(bindDocument, bindGroup);
                        //as well as to individual using data-bind-name
                        this.unbindEntry(bindDocument, bindName);
                    } else {
                        //UnBind individual only using name if already bound
                        var name = null;
                        if ((bindName !== undefined && bindName !== "" && bindName !== "group")) {
                            name = bindName;
                            this.unbindEntry(bindDocument, name);
                        }
                    }
                }
                , removePref: function (el) {
                    var bindGroup = el.attr(this.bindGroup_attr);
                    var bindName = el.attr(this.bindName_attr);
                    var bindDocument = this.getInputPrefDocument(el);

                    //console.log("remove pref for bindname: " + bindName +" / bindGroup: " + bindGroup );
                    //console.log("remove: " + bindName);
                    if (this.groupBindingOnly(bindGroup, bindName)) {
                        //unbind group if already bound and if all related inputs have NO value
                        this.unbindGroup(bindDocument, bindGroup);
                    } else if (this.groupAndIndividualBinding(bindGroup, bindName)) {
                        //unbind group if already bound
                        this.unbindGroup(bindDocument, bindGroup);
                        //as well as remove individual using data-bind-name
                        this.removeDocumentsPreference(bindDocument, bindName);
                    } else {
                        //remove individual only using name if already bound
                        var name = null;
                        if ((bindName !== undefined && bindName !== "" && bindName !== "group")) {
                            name = bindName;
                            this.removeDocumentsPreference(bindDocument, name);
                        }
                    }
                }
                /**
                 * Sets the printing preference governing the order of names according to the configuration from the CMS per language.
                 * 
                 * @param doesNotExist, boolean - when true indicates that there is no preference with key = this.PERSONNAME_KEY
                 * @param noOrder, boolean - when true indicates that there is a preference with key = this.PERSONNAME_KEY, but has no 'order' attribute
                 */
                , managePersonNameOrder: function (document, doesNotExist, noOrder) {
                    if (this.prefModel !== null) {

                        this.prefModel.applyDefaultPersonNameOrder(document, doesNotExist, noOrder);
                    }
                }
                /**
                 * Used when switching the order of PersonNames
                 */
                , switchPersonNames: function (documentType) {

                    var existingPref = this.findPreference(this.PERSONNAME_KEY);

                    //Proceed only when the preference does not exist,
                    var doesNotExist = (existingPref === undefined || existingPref === null);
                    //or when the preference exists but there is no 'order' value
                    var noOrder = (doesNotExist === false
                            && (existingPref.order === undefined || existingPref.order === null || existingPref.order === ""));

                    //If the Preference does not exist, or
                    //If the Preference exists but has no 'order' attribute, then set to the default and return false
                    //false means that there is no need to call re-render (see PersonalInfoComposeView.managePersonNameOrder )
                    if (doesNotExist || noOrder) {
                        this.managePersonNameOrder(documentType, doesNotExist, noOrder);
                        return false;
                    }
                    //Otherwise proceed with switching...
                    var order = existingPref.order;
                    var reverseOrder = ((order.split(" ")).reverse()).toString();
                    var newOrder = reverseOrder.replace(",", " ");

                    this.updatePreference(this.PERSONNAME_KEY, "order", newOrder);//( name, attribute, value, silently ){
                    return true;
                }
                /**
                 * Sets the order of sections when none is set.
                 * 
                 * @param doesNotExist, boolean - when true indicates that there is no preference with key = this.PERSONNAME_KEY
                 * @param noOrder, boolean - when true indicates that there is a preference with key = this.PERSONNAME_KEY, but has no 'order' attribute
                 */
                , manageSectionOrder: function (doesNotExist, noOrder) {
                    var name = this.LEARNERINFO_KEY;
                    var order = this.SECTION_ORDER;

                    if (doesNotExist) { //add if preference does not exist
                        this.addPreference(name, true, null, order, null, null);
                    } else if (!doesNotExist && noOrder) { //update if preference exists, but there is no order
                        this.updatePreference(name, "order", order);
                    }
                }
                , manageSectionPageBreaks: function (doesNotExist, noPageBreaks) {
                    var name = this.LEARNERINFO_KEY;
                    var pageBreaks = this.PAGE_BREAKS;

                    if (doesNotExist) {
                        this.addPreference(name, true, null, null, null, null, pageBreaks);
                    } else if (!doesNotExist && noPageBreaks) { //update if preference exists, but there is no order
                        this.updatePreference(name, "pageBreaks", pageBreaks);
                    }
                }
                /**
                 * Switches the order of Work and Education experience order
                 */
                , switchWorkEducationOrder: function () {

                    var existingPref = this.findPreference(this.LEARNERINFO_KEY);
                    var doesNotExist = (existingPref === undefined || existingPref === null);
                    var noOrder = (doesNotExist === false
                            &&
                            (existingPref.order === undefined || existingPref.order === null || existingPref.order === ""));

                    //If the Preference does not exist, or
                    //If the Preference exists but has no 'order' attribute, then set to the default order
                    //return false, because there is no reason to switch a non-existing value
                    if (doesNotExist || noOrder) {
                        this.manageSectionOrder(doesNotExist, noOrder);
                        return false;
                    }
                    //else switch
                    //switch 2-3
                    var originalOrder = (existingPref.order).split(" ");//to array
                    var order = (existingPref.order).split(" ");//to array
                    /* slice does not alter the original array, but returns a new "one level deep" copy 
                     * that contains copies of the elements sliced from the original array. 
                     * Elements of the original array are copied into the new array */
                    var sections = order.slice(2, 4);//get work and education and reverse
                    sections.reverse();
                    originalOrder.splice(2, 2, sections[0], sections[1]);
                    var newOrder = originalOrder.toString();

                    this.updatePreference(this.LEARNERINFO_KEY, "order", newOrder.replace(/,/g, " "));//( name, attribute, value, silently ){

                    return true;
                }
                /**
                 * Inserts page break before a section
                 */
                , toggleSectionPageBreak: function (section) {

                    var existingPref = this.findPreference(this.LEARNERINFO_KEY);
                    var doesNotExist = existingPref === undefined || existingPref === null;
                    var noPageBreaks = doesNotExist === false &&
                            (existingPref.pageBreaks === undefined || existingPref.pageBreaks === null);

                    if (doesNotExist || noPageBreaks) {
                        this.manageSectionPageBreaks(doesNotExist, noPageBreaks);
                    }

                    var originalValue = existingPref.pageBreaks;

                    if (originalValue !== undefined) {
                        var pageBreakSections = $.trim(originalValue).split(" ");
                        var newValue;
                        if ($.inArray(section, pageBreakSections) !== -1) {
                            pageBreakSections = _.without(pageBreakSections, section);
                            newValue = pageBreakSections.join(" ");
                        } else {
                            newValue = originalValue + " " + section;
                        }

                        this.updatePreference(this.LEARNERINFO_KEY, "pageBreaks", newValue);

                        return true;
                    } else {
                        return false;
                    }

                }
                /**
                 * Toggles the visibility of the CEFR Levels grid in the exported document 
                 */
                , toggleCEFLanguageLevelsGrid: function (show) {

                    var existingPref = this.findPreference(this.CEFRLEVELS_KEY);

                    var doesNotExist = (existingPref === undefined || existingPref === null);

                    if (doesNotExist || (existingPref.show === show))
                        return false;

                    this.updatePreference(this.CEFRLEVELS_KEY, "show", show);

                    return true;
                }

                /**
                 * Toggles the Global CL Justification
                 */
                , toggleCLJustification: function (justify) {

                    var existingPref = this.findPreference(this.CL_JUSTIFICATION);

                    var doesNotExist = (existingPref === undefined || existingPref === null);
                    var noJustify = (doesNotExist === false
                            &&
                            (existingPref.justify === undefined || existingPref.justify === null || existingPref.justify === ""));

                    if (doesNotExist || noJustify || (existingPref.justify === justify))
                        return false;

                    this.updatePreference(this.CL_JUSTIFICATION, "justify", justify);

                    return true;
                }

                /**
                 * Toggles CL Closing Salutation appearance of name
                 */
                , toggleCLClosingSalutationNameEnabled: function (enableName) {

                    var existingPref = this.findPreference(this.CL_CLOSINGSALUTATIONNAMEENABLED);

                    var doesNotExist = (existingPref === undefined || existingPref === null);
                    var noClosingSalutationNameEnabled = (doesNotExist === false
                            &&
                            (existingPref.enableName === undefined || existingPref.enableName === null || existingPref.enableName === ""));

                    if (doesNotExist || noClosingSalutationNameEnabled || (existingPref.enableName === enableName))
                        return false;

                    this.updatePreference(this.CL_CLOSINGSALUTATIONNAMEENABLED, "enableName", enableName);

                    return true;
                }



//========== UTILS =====================
                , unbindGroup: function (documents, bindGroup) {
                    var that = this;
                    var bindGroups = (bindGroup === undefined || bindGroup === null || bindGroup === "") ? [] : $(bindGroup.split(" "));
                    //Perform only for the first group - the one that is more relevant
                    var group = bindGroups[0];
                    var matchedGroup = that.findPreference(group);
                    if (matchedGroup !== null) {
                        var safeToDelete = true;
                        var escGroup = Utils.escapeForJQuery(group);
                        $(":input:not(button)[" + that.bindGroup_attr + "*=\"" + escGroup + "\"]").each(function (idx, el) {
                            var inGroup = $(el);
                            var v = inGroup.val();
                            var value = (typeof (v) === "string" ? $.trim(v) : v);
                            if (value.length === 0) {
                                safeToDelete = false;
                                return false;
                            }
                        });
                        if (safeToDelete)
                            that.unbindExistingEntry(documents, group);
                    }
                }
                , unbindEntry: function (documents, bindName, silently) {
                    var matchedPref = this.findPreference(bindName);
                    if (matchedPref !== null) {
                        this.unbindExistingEntry(documents, bindName, silently);
                    }
                }
                , unbindExistingEntry: function (documents, name, silently) {
                    //If in array, will have to remove it...
                    if (Utils.inArray(name)) {
                        this.removeDocumentsPreference(documents, name);
                    } else {
                        this.updatePreference(name, "show", false, silently);
                    }
                }

                , updatePreferenceAttribute: function (pref, attribute, value) {
                    //proceed if the attribute is not already set to this value
                    var attrValue = pref[attribute];
                    if ((attrValue === undefined) && (value === null || value === "")) {
                        //do nothing
                        return;
                    } else if ((attrValue !== undefined) && (value === null || value === "")) {
                        //delete the value
                        delete pref[ attribute ];
                    } else if (attrValue === value) {
                        //do nothing
                        return;
                    } else {
                        //update
                        pref[ attribute ] = value;
                    }
                }
                , updatePreference: function (prefName, attr, value, silently) {
                    if (this.prefModel !== null) {

                        if ($.isArray(prefName) === true) {
                            prefName = prefName[0];
                        } //fix

                        var name = Utils.addPrefix(this.prefsDocument, prefName);

                        var matchPref = this.findPreference(name);
                        if (matchPref === null) {
                            this.addPreference(name, true, null, null, null, null);
                            matchPref = this.findPreference(name);
                        }

                        if (matchPref === undefined || matchPref === null) {
                            return false;
                        }

                        this.updatePreferenceAttribute(matchPref, attr, value);
                    }
                }

                /*******************************************************************/
                /****  REMOVE PREFERENCE ****/
                /*******************************************************************/
                /**
                 * Remove the preference from the given @param documents, provided as a space separated list
                 */
                , removeDocumentsPreference: function (documents, prefName) {
                    var docs;
                    if (documents === undefined || documents === null || documents === "") {
                        docs = [this.prefsDocument];
                    } else {
                        docs = documents.split(" ");
                    }
                    for (var i = 0; i < docs.length; i++) {
                        this.removeDocumentPreference(docs[i], prefName);
                    }
                }
                /**
                 * Remove the preference from the given @param document
                 */
                , removeDocumentPreference: function (document, prefName) {
                    var name = Utils.addPrefix(document, prefName);

                    this.removeByName(name);
                }
                /**
                 * Remove the preference in the current document
                 */
                , removePreference: function (prefName) {
                    var name = Utils.addPrefix(this.prefsDocument, prefName);

                    this.removeByName(name);
                }
                , removeByName: function (name) {
                    var matchPref = this.prefModel === null ? null : this.prefModel.get(name);
                    if (matchPref !== null) {
                        this.prefModel.remove(name, {silent: true});
                    }
                }
                /*******************************************************************/
                /*******************************************************************/
                /**
                 * Returns the object that matched the given Pref Name
                 */
                , findPreference: function (prefName) {
                    if (this.prefModel === null) {
                        return null;
                    }

                    var name = Utils.addPrefix(this.prefsDocument, prefName);
                    return this.prefModel.get(name);
                }
                /*******************************************************************/
                /****  ADD PREFERENCE ****/
                /*******************************************************************/

                /**
                 * Add the preference for all related @param documents, provided as space-separated list
                 */
                , addDocumentsPreference: function (documents, prefName, show, format, order) {
                    var docs;
                    if (documents === undefined || documents === null || documents === "") {
                        docs = [this.prefsDocument];
                    } else {
                        docs = documents.split(" ");
                    }
                    for (var i = 0; i < docs.length; i++) {
                        this.addDocumentPreference(docs[i], prefName, show, format, order);
                    }
                }
                /**
                 * Add the preference for the given @param document 
                 */
                , addDocumentPreference: function (document, prefName, show, format, order, justify, enableName, pageBreaks) {
                    if (this.prefModel === null) {
                        return false;
                    }
                    if ($.isArray(prefName) === true) {
                        prefName = prefName[0];
                    } //fix

                    var name = Utils.addPrefix(document, prefName);

                    this.prefModel.addPreference(name, show, order, format, justify, enableName, pageBreaks);
                }

                /**
                 * Update the preference for all related @param documents, provided as space-separated list
                 */
                , updateDocumentsPreference: function (documents, prefName, show, format, order, justify, enableName, pageBreaks) {
                    var docs;
                    if (documents === undefined || documents === null || documents === "") {
                        docs = [this.prefsDocument];
                    } else {
                        docs = documents.split(" ");
                    }
                    for (var i = 0; i < docs.length; i++) {
                        this.updateDocumentPreference(docs[i], prefName, show, format, order, justify, enableName, pageBreaks);
                    }
                }
                /**
                 * Update the preference for the given @param document 
                 */
                , updateDocumentPreference: function (document, prefName, show, format, order, justify, enableName, pageBreaks) {
                    if (this.prefModel === null) {
                        return false;
                    }
                    if ($.isArray(prefName) === true) {
                        prefName = prefName[0];
                    } //fix

                    var name = Utils.addPrefix(document, prefName);

                    this.prefModel.updatePreference(name, show, order, format, justify, enableName, pageBreaks);
                }
                /**
                 * Add content to the prefs for the current document
                 */
                , addPreference: function (prefName, show, format, order, justify, enableName, pageBreaks) {
                    if (this.prefModel === null) {
                        return false;
                    }
                    if ($.isArray(prefName) === true) {
                        prefName = prefName[0];
                    } //fix

                    var name = Utils.addPrefix(this.prefsDocument, prefName);

                    this.prefModel.addPreference(name, show, order, format, justify, enableName, pageBreaks);
                }
                /*******************************************************************/
                /*******************************************************************/
                , groupBindingOnly: function (bindGroup, bindName) {
                    //If ( data-bind-group AND NOT(data-bind-name) )
                    var hasBindName = (bindName !== undefined && bindName !== "" && bindName !== "group");
                    return ((bindGroup !== undefined && bindGroup !== "") && !hasBindName);
                }
                , groupAndIndividualBinding: function (bindGroup, bindName) {
                    //If ( data-bind-group AND data-bind-name )
                    return ((bindGroup !== undefined && bindGroup !== "") && (bindName !== undefined && bindName !== "" && bindName !== "group"));
                }
                /**
                 * Reset to default prefs
                 */
                , resetToDefaults: function (silently) {
                    this.prefModel.resetToDefaults(silently);
                }
                /**
                 * Return a suitable prefName for the element
                 */
                , getInputPrefName: function (input) {
                    var prefName = input.attr(this.bindName_attr);
                    if (prefName === "group") {
                        prefName = input.attr(this.bindGroup_attr);
                    }
                    if (prefName === undefined || prefName === null || prefName === "") {
                        prefName = input.attr(this.name_attr);
                    }
                    return prefName;
                }
                /**
                 * Return a suitable related document for the element
                 */
                , getInputPrefDocument: function (input) {
                    var prefDocument = input.attr(this.bindDocument_attr);
                    if (prefDocument === undefined || prefDocument === null || prefDocument === "") {
                        prefDocument = this.prefsDocument;
                    }
                    return prefDocument;
                }
            });
            return PrintingPreferencesView;
        }
);