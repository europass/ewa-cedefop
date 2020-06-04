define(
        [
            'jquery',
            'underscore',
            'backbone',
            'europass/http/WindowConfigInstance',
            'europass/GlobalDocumentInstance',
            'Utils',
            'i18n!localization/nls/GuiLabel'
        ],
        function (
                $, _, Backbone,
                WindowConfig,
                GlobalDocument,
                Utils,
                GuiLabel
                ) {
            var ExportInfo = Backbone.Model.extend({
                model: null,

                DEFAULT_LOCATION: "local",
                //Available Location Options
                LOCATION: {
                    LOCAL: "local",
                    EMAIL: "email",
                    PARTNERS: "partners",
                    DROPBOX: "dropbox",
                    GOOGLEDRIVE: "googledrive",
                    ONEDRIVE: "onedrive"
                            //,EURES		: "eures" commented out as requested in EWA-1695: Temporarily Hide the Export To Eures Button
                },
                DEFAULT_CLOUD_STORAGE: "dropbox",
                CLOUD_STORAGE: {
                    DROPBOX: "dropbox",
                    GOOGLEDRIVE: "googledrive",
                    ONEDRIVE: "onedrive"
                },
                DEFAULT_FILE_FORMAT: "pdf",
                //Available FileFormat Options
                FILE_FORMAT: {
                    PDF: "pdf",
                    WORD: "word",
                    ODT: "odt",
                    XML: "xml"
                },
                UPLOADABLE_FILE_FORMAT: {
                    pdf: true,
                    word: false,
                    odt: false,
                    xml: true
                },
                //Available Document Types
                DOCUMENT_TYPE: {
                    ECV: "ECV",
                    ECL: "ECL",
                    ELP: "ELP",
                    ESP: "ESP",
                    ECV_ESP: "ECV_ESP"
                },

                USER_OPTIONS: {
                    NOT_ACTIVE: []
                },
                //Incompatibilities amongst documents
                //Currently no incompatibilities may exist
                DOCUMENT_INCOMPATIBILITIES: {

//			"ECL" : [ "ELP", "ESP" ],
//			"ESP" : [ "ELP", "ECL" ],
//			"ELP" : [ "ESP", "ECL" ]
                },
                //Compatibilities amongst documents
                DOCUMENT_COMPATIBILITIES: {
                    "ELP": ["ECL", "ECV", "ESP"],
                    "ESP": ["ECL", "ECV", "ELP"],
                    "ECL": ["ECV", "ELP", "ESP"],
                    "ECV": ["ECL", "ELP", "ESP"]

                },
                //Available Document Combinations
                //Using the desired order of document generation
                DOCUMENT_COMBINATIONS: [
                    ["ECV"],
                    ["ESP"],
                    ["ELP"],
                    ["ECL"],

                    ["ECL", "ECV"],
                    ["ELP", "ECL"],
                    ["ESP", "ECL"],
                    ["ESP", "ELP"],
                    ["ECV", "ELP"],
                    ["ECV", "ESP"],

                    ["ECL", "ECV", "ELP"],
                    ["ECL", "ECV", "ESP"],
                    ["ECL", "ELP", "ESP"],
                    ["ECV", "ELP", "ESP"],

                    ["ECL", "ECV", "ELP", "ESP"]
                ],
                //Default attributes present in every new instance
                defaults: {
                    "FileFormat": "pdf",
                    "Location": "local",
                    "CloudStorage": "dropbox"
                },
                // Clean up work
                onClose: function () {
                    delete this.model;
                },
                //Initialize
                initialize: function () {

                },
                /**
                 * Sets the related SkillsPassport instance
                 */
                setSkillsPassport: function (model) {
                    //IMPORTANT!!! 
                    //Assignment over clone means that any changes done HERE in this.model are reflected in the live model.
                    //So, if you need to perform transient changes, use an additional variable as CLONE instead
                    this.model = model;
                },
                getSkillsPassport: function () {
                    return this.model;
                },
                /**
                 * Return an array of the text names of the available locations
                 * @returns {Array}
                 */
                getAvailableLocations: function () {
                    var locs = "";
                    for (var key in this.LOCATION) {
                        locs += this.LOCATION[key] + " ";
                    }
                    return locs;
                },

                /**
                 * Return an array of the text names of the available locations
                 * @returns {Array}
                 */
                getAvailableCloudStorage: function () {
                    var opts = "";
                    for (var key in this.CLOUD_STORAGE) {
                        opts += this.CLOUD_STORAGE[key] + " ";
                    }
                    return opts;
                },
                /**
                 * Set Location  only when not empty
                 * @param fileFormat
                 */
                setLocation: function (location) {
                    if (_.isEmpty(location))
                        return;
//			console.log("Set Location to '"+location+"'");
                    this.set("Location", location);
                },
                /**
                 * Get Location
                 * @param location
                 */
                getLocation: function (location) {
                    return this.get("Location");
                },

                /**
                 * Set Cloud Option  only when not empty
                 * @param cloudOption
                 */
                setCloudStorage: function (cloudStorage) {
                    if (_.isEmpty(cloudStorage))
                        return;
//			console.log("Set File Format to '"+fileFormat+"'");
                    this.set("CloudStorage", cloudStorage);
                },
                /**
                 * Get Cloud Option
                 * @param fileFormat
                 */
                getCloudStorage: function (cloudStorage) {
                    return this.get("CloudStorage");
                },
                /**
                 * Set File Format  only when not empty
                 * @param fileFormat
                 */
                setFileFormat: function (fileFormat) {
                    if (_.isEmpty(fileFormat))
                        return;
//			console.log("Set File Format to '"+fileFormat+"'");
                    this.set("FileFormat", fileFormat);
                },
                /**
                 * Get File Format
                 * @param fileFormat
                 */
                getFileFormat: function (location) {
                    return this.get("FileFormat");
                },
                /**
                 * Set Document Type only when not empty
                 * @param docType
                 */
                setDocumentType: function (docType) {
                    if (_.isEmpty(docType))
                        return;
//			console.log("Set Document Type to '"+docType+"'");
                    this.set("DocumentType", docType);
                    this.getSkillsPassport().set("SkillsPassport.DocumentInfo.DocumentType", docType);
//			console.log( "ESP DocumentType: " + JSON.stringify(this.getSkillsPassport().get( "SkillsPassport.DocumentInfo.DocumentType")) );
                },
                /**
                 * Set Document only when not empty array
                 * @param docType
                 */
                setBundle: function (bundle) {
                    if (_.isEmpty(bundle))
//			console.log("Set Bundle to '"+bundle+"'");
                        this.set("Bundle", bundle);
                    this.getSkillsPassport().set("SkillsPassport.DocumentInfo.Document", bundle);
//			console.log( "ESP Bundle: " + JSON.stringify(this.getSkillsPassport().get( "SkillsPassport.DocumentInfo.Document")) );
                },
                /**
                 * Checks whether the document type is set
                 * @returns boolean
                 */
                hasDocumentType: function () {
                    return this._hasInfo("DocumentType");
                },
                /**
                 * Checks whether the file format is set
                 * @returns boolean
                 */
                hasFileFormat: function () {
                    return this._hasInfo("FileFormat");
                },
                /**
                 * Checks whether the file format is set
                 * @returns boolean
                 */
                hasLocation: function () {
                    return this._hasInfo("Location");
                },
                /**
                 * Checks whether a cloud storage is set
                 * @returns boolean
                 */
                hasCloudStorage: function () {
                    return this._hasInfo("CloudStorage");
                },
                /**
                 * Checks whether the attribute denoted by the path parameter is set
                 * @param path string
                 * @returns boolean
                 */
                _hasInfo: function (path) {
                    var info = this.get(path);
                    return !_.isEmpty(info);
                },
                /**
                 * Return a list of europass documents with info as to whether they are empty or current
                 * @returns {Array}
                 * of 
                 * { name, title, empty, current }
                 *
                 */
                europassDocuments: function () {
                    var allDocs = GlobalDocument.europassDocuments();

                    var docs = [];

//			var incompatible = [];

                    var espHeading = false;
                    var noOfNoContent = 0;
                    var noContent = false;

                    var nonEmptyCV = false;

                    var currentDoc = "";

                    var globalDocument = GlobalDocument.getDocument();
                    for (var i = 0; i < allDocs.length; i++) {
                        var doc = allDocs[i];
                        var item = {name: doc};

                        var title = GuiLabel[ "export.wizard.doc.option." + doc ] || doc;
                        item.title = title;

                        var isEmpty = this.model.info().isEmpty(doc);

                        item.empty = isEmpty;

                        if (item.name === this.DOCUMENT_TYPE.ECV && !item.empty)
                            nonEmptyCV = true;

                        //Only one can be current
                        var isCurrent = (doc === globalDocument);
                        if (this.DOCUMENT_TYPE.ECV_ESP === globalDocument
                                && (this.DOCUMENT_TYPE.ECV === doc || this.DOCUMENT_TYPE.ESP === doc)) {
                            isCurrent = true;
                        }

                        if (isCurrent) {
                            item.current = isCurrent;
//					incompatible = this.DOCUMENT_INCOMPATIBILITIES[ doc ];
                            currentDoc = doc;
                        }

                        //ELP/ESP use heading
                        var isELP = this.DOCUMENT_TYPE.ELP === doc;
                        var isESP = this.DOCUMENT_TYPE.ESP === doc;
                        if (isELP)
                            item.isESP = true;
                        if (isESP)
                            item.isESP = true;

                        if (espHeading === false && (isELP || isESP)) {
                            item.espHeading = GuiLabel[ "export.wizard.doc.option.ESP.title" ] || "European Skills Passport";
                            espHeading = true;
                        }

                        docs.push(item);
                    }
                    if (currentDoc === this.DOCUMENT_TYPE.ECL && nonEmptyCV) {
//				var index = incompatible.indexOf("ESP");
//				delete incompatible[index];
                    }
                    for (var i = 0; i < docs.length; i++) {
                        var doc = docs[i];
//				if ( _.indexOf( incompatible, doc.name ) > -1 )
//					doc.incompatible = true;
                        if (doc.empty) {
                            noOfNoContent += 1;
                        }
                    }
                    var context = {
                        docs: docs
                    };

                    if (noOfNoContent === docs.length) {
                        noContent = true;
                        context.noContent = noContent;
                    }

                    return context;
                },
                /**
                 * Find the remaining compatible documents by combining an array of already selected documents.
                 * @param arr
                 * @returns an object with two arrays of document names
                 */
                findCompatible: function (arr) {
//			console.log("Checked: " + JSON.stringify(arr) );
                    if (_.isEmpty(arr)) {
                        return {
                            compatible: GlobalDocument.europassDocuments()
//					incompatible: []
                        };
                    }
                    var compatible = _.union([], arr);
                    for (var i = 0; i < arr.length; i++) {
                        var doc = arr[i];
//				console.log("Union of " + JSON.stringify(compatible) );
//				console.log("with: " + JSON.stringify(this.DOCUMENT_COMPATIBILITIES[ doc ]) );
                        compatible = _.union(compatible, this.DOCUMENT_COMPATIBILITIES[ doc ]);
//				console.log("Results in compatible: " + JSON.stringify(compatible) );
                    }
//			console.log("Compatible: " + JSON.stringify(compatible) );
//			var incompatible = _.difference( GlobalDocument.europassDocuments(), compatible );
//			console.log("Incompatible: " + JSON.stringify(incompatible) );
                    return {
                        compatible: compatible
//				incompatible: incompatible
                    };
                },
                /**
                 * Return information about file formats
                 */
                europassFileFormats: function () {
                    var allFormats = this.FILE_FORMAT;

                    var formats = [];
                    for (var key in allFormats) {
                        var item = {};

                        var name = allFormats[ key ];
                        item.name = name;

                        var title = GuiLabel[ "export.wizard.file.option." + name ] || name;
                        item.title = title;

                        if (this.DEFAULT_FILE_FORMAT === name)
                            item.checked = true;

                        if (this.UPLOADABLE_FILE_FORMAT[ name ] === false)
                            item.nonuploadable = true;

                        formats.push(item);
                    }
                    return formats;
                },
                /**
                 * Return information about store locations
                 */
                europassLocations: function () {
                    var allLocations = this.LOCATION;

                    var isIOS = ("IOS" === WindowConfig.operatingSystem);

                    var isPartner = Utils.isPartnerAvailable();

                    var locations = [];
                    for (var key in allLocations) {
                        var item = {};

                        var name = allLocations[ key ];
                        item.name = name;
                        if (key === "PARTNERS" && WindowConfig.remoteUploadCallbackUrl) {
                            var partnerUrl = new URL(WindowConfig.remoteUploadCallbackUrl);
                            var title = WindowConfig.remoteUploadPartnerName || GuiLabel[ "export.wizard.location.option." + name ] || name;
                            item.title = title;
                        } else {
                            var title = GuiLabel[ "export.wizard.location.option." + name ] || name;
                            item.title = title;
                        }
                        if (isIOS && (WindowConfig.browserName !== 'Safari' && WindowConfig.browserName !== 'CriOS/')) {
                            if (this.LOCATION.LOCAL === name)
                                item.hidden = true;
                            if (this.LOCATION.EMAIL === name)
                                item.checked = true;
                        } else {
                            if (this.DEFAULT_LOCATION === name)
                                item.checked = true;
                        }

                        if (this.LOCATION.PARTNERS === name) {
                            if (!isPartner)
                                continue;
                        }

                        locations.push(item);
                    }
                    return locations;
                },

                /**
                 * Return information about cloud locations
                 */
                europassCloudOptions: function () {
                    var allCloudOptions = this.CLOUD_STORAGE;
                    var isIOS = ("IOS" === WindowConfig.operatingSystem);
                    var browser = WindowConfig.browserName;
                    var isSafari = (!_.isEmpty(browser) && ("Safari" === browser));

                    var cloudOptions = [];
                    for (var key in allCloudOptions) {
                        var item = {};

                        var name = allCloudOptions [ key ];
                        item.name = name;

                        var title = GuiLabel[ "export.wizard.location.option." + name ] || name;
                        item.title = title;

                        if (this.DEFAULT_CLOUD_STORAGE === name)
                            item.checked = true;

                        cloudOptions.push(item);
                    }
                    return cloudOptions;

                }
            });

            return ExportInfo;
        }
);
