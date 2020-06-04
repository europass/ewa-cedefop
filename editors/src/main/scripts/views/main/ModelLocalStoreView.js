/**
 * Listens to the events from the navigation model
 * and updates the aside HTML elements accordingly,
 * e.g. by setting the active class name.
 */
define(
        [
            'jquery',
            'backbone',
            'Utils',
            'hbs!templates/main/localstorage',
            'europass/GlobalLocalStoreOptionInstance',
            'europass/GlobalDocumentInstance'
//	,'i18n!localization/nls/Notification'
        ],
        function ($, Backbone, Utils, Template, GlobalLocalStoreOption, GlobalDocument/*, Notification*/) {

            var ModelLocalStoreView = Backbone.View.extend({
                //15 minutes
                KEEP_PERIOD_MINUTES: 15,

                localKey: "temporary.europass.ewa.skillspassport.v3",
                localKey2: "temporary.europass.docType.download.selection",

                clazz: "store-data-locally",

                events: {
//				"change :input" : "toggleStoring",
//				"click :input" : "toggleStoring",
                    "download:docType:selection:local:save": "storeDocTypeDownloadSelection"
                },
                onClose: function () {

                    this.model.unbind("model:content:reset", this.clearStorage);

                    this.model.unbind("model:binaries:reset", this.storeIfStorable);
                    this.model.unbind("model:content:changed", this.storeIfStorable);
                    this.model.unbind("model:uploaded:esp", this.storeIfStorable);
                    this.model.unbind("model:uploaded:social", this.storeIfStorable);
                    this.model.unbind("model:uploaded:cloud", this.storeIfStorable);
                    this.model.unbind("model:linked:attachment:changed", this.storeIfStorable);
                    this.model.unbind("model:prefs:order:changed", this.storeIfStorable);
                    this.model.unbind("model:prefs:pageBreaks:changed", this.storeIfStorable);
                    this.model.unbind("model:list:sort:change", this.storeIfStorable);
                    this.model.unbind("change:SkillsPassport.LearnerInfo.Identification.Signature", this.storeIfStorable);
                    this.model.unbind("download:docType:selection:change", this.storeIfStorable);
                    this.model.unbind("model:loaded:cloud:document", this.storeIfStorable);
                },

                initialize: function (options) {

                    this.template = Template;

                    //check if there is previously stored data
                    this.populateWithAvailableData();

                    this.render();

                    this.model.bind("model:content:reset", this.clearStorage, this);

                    this.model.bind("model:binaries:reset", this.storeIfStorable, this);
                    this.model.bind("model:content:changed", this.storeIfStorable, this);
                    this.model.bind("model:uploaded:esp", this.storeIfStorable, this);
                    this.model.bind("model:uploaded:social", this.storeIfStorable, this);
                    this.model.bind("model:uploaded:cloud", this.storeIfStorable, this);
                    this.model.bind("model:linked:attachment:changed", this.storeIfStorable, this);
                    this.model.bind("model:prefs:order:changed", this.storeIfStorable, this);
                    this.model.bind("model:prefs:pageBreaks:changed", this.storeIfStorable, this);
                    this.model.bind("model:list:sort:change", this.storeIfStorable, this);
                    this.model.bind("change:SkillsPassport.LearnerInfo.Identification.Signature", this.storeIfStorable, this);
                    this.model.bind("model:loaded:cloud:document", this.storeIfStorable, this);
                },
                render: function (restored) {

                    var isStorable = GlobalLocalStoreOption.isStorable();

                    var context = {storable: isStorable};
                    this.$el.html(this.template(context));

                    //Store if storable (might come from upload) 
                    if (isStorable) {
                        this.store();
                    }
                },
                /**
                 * Populate the model with data found in the local storage.
                 * 
                 * Note that if the model is already non-empty, the stored data will not be used (we are reloading after language switch).
                 * 
                 * Also, the stored data need to be ignored if the last-update-date is greater than KEEP_PERIOD_MINUTES
                 * 
                 * @returns {Boolean}
                 */
                populateWithAvailableData: function () {
                    //do not populate the model, if it is already populated (case switch language)
                    if (Utils.isEmptyObject(this.model.get("SkillsPassport.LearnerInfo")) === false || Utils.isEmptyObject(this.model.get("SkillsPassport.CoverLetter")) === false) {
                        return false;
                    }

                    var prevModel = null;
                    var prevDocTypeDownloadSelection = null;

                    try {
                        prevModel = window.localStorage.getItem(this.localKey);

                        prevDocTypeDownloadSelection = window.localStorage.getItem(this.localKey2);

                        //console.log("get from local storage after resuming from local storage" +  JSON.stringify(prevModel) );
                    } catch (err) {
                        this.localStorageNotSupported();
                    }

                    if (prevModel !== undefined && prevModel !== null) {
                        //true: perform full translation depending on whether the model's locale is different than ui locale
                        //console.log("Populate from locale storage...");
                        var populated = this.model.populateModel(prevModel, true);

                        if (!(populated === false)) {
                            this.model.trigger("local:storage:model:populated");
                        }
                    }

                    if (prevDocTypeDownloadSelection !== null) {
                        GlobalDocument.setDocTypeDownloadSelections(prevDocTypeDownloadSelection);
                    }
                },
                /**
                 * Listens to click event of the input
                 * @param event
                 */
                toggleStoring: function (input) {
//				console.log("toggleStoring");
//				var input = $(event.target);

                    var keepStorable = input === true ? input : input.is(':checked');

                    GlobalLocalStoreOption.set(keepStorable);

                    if (keepStorable) {
                        this.store();
                    } else {
                        this.clearStorage(true, true, true, true);
                    }

                },
                /**
                 * Runs on model updates
                 */
                storeIfStorable: function () {
                    //console.log("Local store...");
                    if (GlobalLocalStoreOption.isStorable()) {
                        this.store();
                    }
                },
                /**
                 * Stores the model and triggers an event
                 */
                store: function () {
                    //do not store if empty model
                    if (Utils.isEmptyObject(this.model.get("SkillsPassport.LearnerInfo")) && Utils.isEmptyObject(this.model.get("SkillsPassport.CoverLetter"))) {
                        return false;
                    }

                    this.storeDocTypeDownloadSelection();

                    try {
                        var esp = this.model.conversion().toStorable();

                        window.localStorage.setItem(this.localKey, esp);

                        //console.log("model stored locally");

                        this.model.trigger("local:storage:model:stored");

                    } catch (err) {
                        this.localStorageNotSupported();
                    }
                },
                /**
                 * saves to a window local key the value of GlobalDocument.getDocTypeDownloadSelections() (as a string)
                 */
                storeDocTypeDownloadSelection: function () {
                    try {
                        if (GlobalLocalStoreOption.isStorable()) {
                            window.localStorage.setItem(this.localKey2, GlobalDocument.getDocTypeDownloadSelections());
                            GlobalDocument.setDocTypeDownloadSelections([]);
                        }
                    } catch (err) {
                        this.localStorageNotSupported();
                    }
                },
                clearDocTypeDownloadSelection: function () {
                    try {
                        window.localStorage.removeItem(this.localKey2);
                    } catch (err) {
                    }
                },
                /**
                 * Updates or clears the storage and triggers event
                 */
                clearStorage: function (eraseCL, eraseCV, eraseLP, eraseESP) {
                    try {
                        // When all checkboxes have been selected then only remove localstorage key
                        if (eraseCL && eraseCV && eraseLP && eraseESP) {
                            window.localStorage.removeItem(this.localKey);
                            this.clearDocTypeDownloadSelection();
                        } else {
                            var jsonModel = this.model.conversion().toTransferable();
                            window.localStorage.setItem(this.localKey, jsonModel);
                        }

                        //console.log("model cleared from local storage");

                        this.model.trigger("local:storage:model:cleared");

                    } catch (err) {
                    }
                },
                /**
                 * Display a warning that the local storage is not supported by this browser.
                 */
                localStorageNotSupported: function () {
                    //trigger the message only if it is NOT already displayed
                    var notificationArea = $("body").find('section#app-notifications.forceKeepLocalStorage');
                    if (notificationArea.length === 0) {
                        require(
                                ['i18n!localization/nls/Notification'],
                                function (Notification) {
                                    $("body").trigger("europass:message:show", ["warning", Notification["store.data.locally.not.supported"], true, true]);
                                }
                        );
                    }
                }

            });

            return ModelLocalStoreView;
        }
);