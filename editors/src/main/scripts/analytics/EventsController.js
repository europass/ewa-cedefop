define(["jquery", "underscore", "backbone"], function ($, _, Backbone) {
    var Events = Backbone.Model.extend({
        /*
         * Tracking Events for Imports
         */
        //LocalFileUploadView
        //LinkedInUploadView
        //DropboxUploadView
        //GoogleDriveUploadView
        //OneDriveUploadView
        importFrom: function (option) {
            if (!_paq || !_paq.push) {
                return;
            }
            if (option === "Computer Button") {
                _paq.push(["trackEvent", "Import CV", "Computer", "Import Button"]);
            } else if (option === "Computer DragNDrop") {
                _paq.push(["trackEvent", "Import CV", "Computer", "Drag&Drop"]);
            } else {
                _paq.push(["trackEvent", "Import CV", option, option]);
            }
        },
        /*
         * Tracking Events for Preview
         */
        //ExportWizardViewView
        preview: function () {
            if (!_paq || !_paq.push) {
                return;
            }
            _paq.push(["trackEvent", "Preview", "Preview", "Preview"]);
        },

        /*
         * Tracking Events for Downloads
         */
        exportTo: function (api) {
            if (!_paq || !_paq.push) {
                return;
            }
            var getUrl = window.location.protocol + "//" + window.location.host;

            if (api === getUrl + "/api/document/to/pdf") {
                _paq.push(["trackEvent", "Export To", "Computer", "PDF"]);
            } else if (api === getUrl + "/api/document/to/word") {
                _paq.push(["trackEvent", "Export To", "Computer", "Word"]);
            } else if (api === getUrl + "/api/document/to/opendoc") {
                _paq.push(["trackEvent", "Export To", "Computer", "OpenDoc"]);
            } else if (api === getUrl + "/api/document/to/xml") {
                _paq.push(["trackEvent", "Export To", "Computer", "XML"]);
            }
            //Email
            if (api === getUrl + "/api/document/email/pdf") {
                _paq.push(["trackEvent", "Export To", "Email", "PDF"]);
            } else if (api === getUrl + "/api/document/email/word") {
                _paq.push(["trackEvent", "Export To", "Email", "Word"]);
            } else if (api === getUrl + "/api/document/email/opendoc") {
                _paq.push(["trackEvent", "Export To", "Email", "OpenDoc"]);
            } else if (api === getUrl + "/api/document/email/xml") {
                _paq.push(["trackEvent", "Export To", "Email", "XML"]);
            }
            //Dropbox
            if (api === getUrl + "/api/document/cloud/dropbox/pdf") {
                _paq.push([
                    "trackEvent",
                    "Export To",
                    "Cloud Storage",
                    "Dropbox PDF"
                ]);
            } else if (api === getUrl + "/api/document/cloud/dropbox/word") {
                _paq.push([
                    "trackEvent",
                    "Export To",
                    "Cloud Storage",
                    "Dropbox Word"
                ]);
            } else if (api === getUrl + "/api/document/cloud/dropbox/opendoc") {
                _paq.push([
                    "trackEvent",
                    "Export To",
                    "Cloud Storage",
                    "Dropbox OpenDoc"
                ]);
            } else if (api === getUrl + "/api/document/cloud/dropbox/xml") {
                _paq.push([
                    "trackEvent",
                    "Export To",
                    "Cloud Storage",
                    "Dropbox XML"
                ]);
            }
            //GoogleDrive
            if (api === getUrl + "/api/document/cloud/googledrive/pdf") {
                _paq.push([
                    "trackEvent",
                    "Export To",
                    "Cloud Storage",
                    "GoogleDrive PDF"
                ]);
            } else if (api === getUrl + "/api/document/cloud/googledrive/word") {
                _paq.push([
                    "trackEvent",
                    "Export To",
                    "Cloud Storage",
                    "GoogleDrive Word"
                ]);
            } else if (api === getUrl + "/api/document/cloud/googledrive/opendoc") {
                _paq.push([
                    "trackEvent",
                    "Export To",
                    "Cloud Storage",
                    "GoogleDrive OpenDoc"
                ]);
            } else if (api === getUrl + "/api/document/cloud/googledrive/xml") {
                _paq.push([
                    "trackEvent",
                    "Export To",
                    "Cloud Storage",
                    "GoogleDrive XML"
                ]);
            }
            //OneDrive
            if (api === getUrl + "/api/document/cloud/onedrive/pdf") {
                _paq.push([
                    "trackEvent",
                    "Export To",
                    "Cloud Storage",
                    "OneDrive PDF"
                ]);
            } else if (api === getUrl + "/api/document/cloud/onedrive/word") {
                _paq.push([
                    "trackEvent",
                    "Export To",
                    "Cloud Storage",
                    "OneDrive Word"
                ]);
            } else if (api === getUrl + "/api/document/cloud/onedrive/opendoc") {
                _paq.push([
                    "trackEvent",
                    "Export To",
                    "Cloud Storage",
                    "OneDrive OpenDoc"
                ]);
            } else if (api === getUrl + "/api/document/cloud/onedrive/xml") {
                _paq.push([
                    "trackEvent",
                    "Export To",
                    "Cloud Storage",
                    "OneDrive XML"
                ]);
            }
        },

        /*
         * Tracking Events for Share
         */

        //main/JobPortalsView
        postTo: function (option) {
            if (!_paq || !_paq.push) {
                return;
            }
            if (option === "Dropbox") {
                _paq.push(["trackEvent", "Share", "Review", option]);
            } else if (option === "GoogleDrive") {
                _paq.push(["trackEvent", "Share", "Review", option]);
            } else if (option === "OneDrive") {
                _paq.push(["trackEvent", "Share", "Review", option]);
            } else {
                _paq.push(["trackEvent", "Share", option, option]);
            }
        },

        /*
         * Tracking Events for Application Settings
         */
        //FeedBackView
        deleteCV: function (section) {
            if (!_paq || !_paq.push) {
                return;
            }
            _paq.push(["trackEvent", "Erase", section, section]);
        },
        //ApplicationSettingsView
        applicationOptionLogo: function () {
            if (!_paq || !_paq.push) {
                return;
            }
            _paq.push(["trackEvent", "Option", "Disabled Logo", "Disabled Logo"]);
        },
        //ApplicationSettingsView
        applicationOptionStorage: function () {
            if (!_paq || !_paq.push) {
                return;
            }
            _paq.push(["trackEvent", "Option", "Disabled Local Storage", "Disabled Local Storage"]);
        },
        closeMessage: function () {
            if (!_paq || !_paq.push) {
                return;
            }
            _paq.push(["trackEvent", "Message Is Closed", "Message Is Closed", "Message Is Closed"]);
        },
        openPrepareYourInterviewModal: function () {
            if (!_paq || !_paq.push) {
                return;
            }
            _paq.push(["trackEvent", "Prepare your Interview", "Modal Opened", "Prepare your Interview"]);
        },

        /*
         * Tracking Events for Cloud LogIn
         */
        enterCloudLogin: function () {
            if (!_paq || !_paq.push) {
                return;
            }
            _paq.push([
                "trackEvent",
                "Cloud",
                "Sign in",
                "Entered CloudLogin Area"
            ]);
        },
        googleDriveCloud: function () {
            if (!_paq || !_paq.push) {
                return;
            }
            _paq.push([
                "trackEvent",
                "Cloud",
                "Sign in",
                "GoogleDrive Cloud Login"
            ]);
        },
        oneDriveCloud: function () {
            if (!_paq || !_paq.push) {
                return;
            }
            _paq.push(["trackEvent", "Cloud", "Sign in", "OneDrive Cloud Login"]);
        },
        dropboxCloud: function () {
            if (!_paq || !_paq.push) {
                return;
            }
            _paq.push(["trackEvent", "Cloud", "Sign in", "Dropbox Cloud Login"]);
        },

        // Cloud Actions event tracking
        cloud_open_drawer_button: function () {
            if (!_paq || !_paq.push) {
                return;
            }
            _paq.push(["trackEvent", "Cloud", "Actions", "Drawer Document Button"]);
        },

        cloud_create_new_file: function () {
            if (!_paq || !_paq.push) {
                return;
            }
            _paq.push(["trackEvent", "Cloud", "Actions", "Create new file"]);
        },

        cloud_document_ok_button_same_document: function () {
            if (!_paq || !_paq.push) {
                return;
            }
            _paq.push([
                "trackEvent",
                "Cloud",
                "Actions",
                "Same Document OK Button"
            ]);
        },

        cloud_document_ok_button_other_document: function () {
            if (!_paq || !_paq.push) {
                return;
            }
            _paq.push([
                "trackEvent",
                "Cloud",
                "Actions",
                "Other Document OK Button"
            ]);
        },

        cloud_duplicate_button: function () {
            if (!_paq || !_paq.push) {
                return;
            }
            _paq.push(["trackEvent", "Cloud", "Actions", "Duplicate Button"]);
        },

        cloud_delete_button: function () {
            if (!_paq || !_paq.push) {
                return;
            }
            _paq.push(["trackEvent", "Cloud", "Actions", "Delete Button"]);
        },

        cloud_rename_button: function () {
            if (!_paq || !_paq.push) {
                return;
            }
            _paq.push(["trackEvent", "Cloud", "Actions", "Rename Button"]);
        },

        cloud_share_button: function () {
            if (!_paq || !_paq.push) {
                return;
            }
            _paq.push(["trackEvent", "Cloud", "Actions", "Share Button"]);
        },

        cloud_upload_keep_existing_document: function () {
            if (!_paq || !_paq.push) {
                return;
            }
            _paq.push(["trackEvent", "Cloud", "Actions", "Keep Existing Document"]);
        },

        cloud_upload_discard_existing_document: function () {
            if (!_paq || !_paq.push) {
                return;
            }
            _paq.push([
                "trackEvent",
                "Cloud",
                "Actions",
                "Discard Existing Document"
            ]);
        },

        cloud_signout_button: function () {
            if (!_paq || !_paq.push) {
                return;
            }
            _paq.push(["trackEvent", "Cloud", "Actions", "Sign Out Button"]);
        }
    });
    return Events;
});
