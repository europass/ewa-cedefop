define(
        ['jquery']
        , function ($) {
            var GlobalDocument = function () {
                this.document = "ECV";
                this.prefDocument = "ECV";
                this.urlDocument = "cv";
                this.page = "compose";
                this.docTypeDownloadSelections = [];
                this.docTypeImportJSON = {};
            };

            GlobalDocument.prototype.europassDocuments = function () {
                return ["ECL", "ECV", "ELP", "ESP"];
            };

            GlobalDocument.prototype.get = function () {
                return {
                    document: this.document,
                    prefDocument: this.prefDocument,
                    page: this.page
                };
            };

            GlobalDocument.prototype.set = function (pageInfo) {
                this.document = pageInfo.document;
                this.prefDocument = (pageInfo.document === "ESP" ? "ECV" : pageInfo.document);
                this.urlDocument = this.setUrlDocument();
                this.page = pageInfo.page;
//			console.log( "SET GLOBAL DOCUMENT TO:" + JSON.stringify( pageInfo ) );
            };

            GlobalDocument.prototype.getDocument = function () {
                return this.document;
            };

            GlobalDocument.prototype.getUniqueDocument = function () {
                if ("ECV_ESP" === this.document)
                    return "ECV";
                return this.document;
            };

            GlobalDocument.prototype.getUrlDocument = function () {
                return this.urlDocument;
            };

            GlobalDocument.prototype.getPrefDocument = function () {
                return this.prefDocument;
            };

            GlobalDocument.prototype.getPage = function () {
                return this.page;
            };

            GlobalDocument.prototype.getDocTypeDownloadSelections = function () {
                if ($.isArray(this.docTypeDownloadSelections))
                    return this.docTypeDownloadSelections;
                else
                    return $.makeArray(this.docTypeDownloadSelections);
            };

            GlobalDocument.prototype.setDocTypeDownloadSelections = function (selections) {
                if ($.isArray(selections))
                    this.docTypeDownloadSelections = selections;
                else
                    this.docTypeDownloadSelections = $.makeArray(selections);
            };

            GlobalDocument.prototype.getDocTypeImportJSON = function () {
                return this.docTypeImportJSON;
            };

            GlobalDocument.prototype.setDocTypeImportJSON = function (json) {
                this.docTypeImportJSON = json;
            };

            GlobalDocument.prototype.setUrlDocument = function () {
                var url = this.document;
                switch (url) {
                    case "ECV_ESP":
                    {
                        url = "cv-esp";
                        break;
                    }
                    case "ESP":
                    {
                        url = url.toLowerCase();
                        break;
                    }
                    case "ECL":
                    {
                    }
                    case "ELP":
                    {
                    }
                    case "ECV":
                    {
                        url = url.slice(1).toLowerCase();
                        break;
                    }
                    default:
                    {
                        url = url.toLowerCase();
                        break;
                    }
                }
                return url;
            };
            return GlobalDocument;
        }
);