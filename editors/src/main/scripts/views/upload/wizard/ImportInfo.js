define(
        [
            'jquery',
            'underscore',
            'backbone',
            'europass/http/WindowConfigInstance',
            'i18n!localization/nls/GuiLabel'

        ],
        function (
                $, _, Backbone,
                WindowConfig,
                GuiLabel
                ) {
            var ImportInfo = Backbone.Model.extend({
                model: null,
                DEFAULT_LOCATION: "local",
                //Available Location Options
                LOCATION: {
                    LOCAL: "local",
                    LINKEDIN: "linkedin",
                    DROPBOX: "dropbox",
                    GOOGLEDRIVE: "googledrive",
                    ONEDRIVE: "onedrive"

                },
                DEFAULT_CLOUD_STORAGE: "dropbox",
                CLOUD_STORAGE: {
                    DROPBOX: "dropbox",
                    GOOGLEDRIVE: "googledrive",
                    ONEDRIVE: "onedrive"
                },
                //Default attributes present in every new instance
                defaults: {
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
                 * Return a space separated string of the text names of the available locations
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
                 * Checks whether the file format is set
                 * @returns boolean
                 */
                hasLocation: function () {
                    return this._hasInfo("Location");
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
                 * Return information about store locations
                 */
                europassLocations: function () {
                    var allLocations = this.LOCATION;
                    if (!WindowConfig.showLinkedIn) {
                        allLocations = {
                            LOCAL: "local",
                            DROPBOX: "dropbox",
                            GOOGLEDRIVE: "googledrive",
                            ONEDRIVE: "onedrive"
                        };
                    }
                    var isIOS = ("IOS" === WindowConfig.operatingSystem);
                    var locations = [];
                    for (var key in allLocations) {
                        var item = {};

                        var name = allLocations[ key ];
                        item.name = name;

                        var title = GuiLabel[ "import.wizard.location.option." + name ] || name;
                        item.title = title;

                        if (isIOS && (WindowConfig.browserName !== 'Safari' && WindowConfig.browserName !== 'CriOS/')) {
                            if (this.LOCATION.LOCAL === name)
                                item.hidden = true;
                            if (this.LOCATION.DROPBOX === name)
                                item.checked = true;
                        } else {
                            if (this.DEFAULT_LOCATION === name)
                                item.checked = true;
                        }

                        locations.push(item);
                    }
                    return locations;
                },
                /**
                 * Return information about cloud storage options
                 */
                europassCloudOptions: function () {
                    var allCloudOptions = this.CLOUD_STORAGE;

                    var isIOS = ("IOS" === WindowConfig.operatingSystem);
                    var browser = WindowConfig.browserName;
                    var isIE = (!_.isEmpty(browser) && (browser.indexOf("MSIE") > -1));
                    var isSafari = (!_.isEmpty(browser) && ("Safari" === browser));
                    var isChrome = (!_.isEmpty(browser) && ("Safari" === browser));

                    var cloudOptions = [];
                    for (var key in allCloudOptions) {
                        var item = {};

                        var name = allCloudOptions[ key ];
                        item.name = name;

                        var title = GuiLabel[ "import.wizard.location.option." + name ] || name;
                        item.title = title;

                        if (isIE) {
                            if (this.CLOUD_STORAGE.DROPBOX === name)
                                item.hidden = true;
                        }

                        if (this.DEFAULT_CLOUD_STORAGE === name)
                            item.checked = true;

                        cloudOptions.push(item);
                    }
                    return cloudOptions;

                }
            });

            return ImportInfo;
        }
);