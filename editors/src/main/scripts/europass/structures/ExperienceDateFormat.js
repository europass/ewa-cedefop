/**
 * Maintains a list of preferences per document
 */
define(
        ['jquery',
            'i18n!localization/nls/DefaultDateFormat'],
        function ($, DefaultDateFormat) {

            var ExperienceDateFormat = function (document) {
                this.init(document);
            };
            ExperienceDateFormat.prototype.init = function (document) {
                this.document = document;
                this.defaultFormat = DefaultDateFormat[document];
                this.prefs = [];
                this.currentFormat = DefaultDateFormat[document];
            };

            ExperienceDateFormat.prototype.get = function () {
                return this.currentFormat;
            };

            ExperienceDateFormat.prototype.set = function (prefName, format) {
                if (format !== undefined && format !== null && format !== "") {
                    this.currentFormat = format;
                    if (!this.inList(prefName)) {
                        this.prefs.push(prefName);
                    }
                }
            };

            ExperienceDateFormat.prototype.addToList = function (prefName) {
//			console.log("About to add to list: " + prefName );
                if (!this.inList(prefName)) {
                    this.prefs.push(prefName);
                }
            };

            ExperienceDateFormat.prototype.list = function () {
                return this.prefs;
            };

            ExperienceDateFormat.prototype.inList = function (name) {
                return !($.inArray(name, this.prefs) === -1);
            };

            ExperienceDateFormat.prototype.clear = function (document) {
                this.init(document === undefined ? this.document : document);
            };

            return ExperienceDateFormat;
        }
);