define(
        ['jquery', 'xdate']
        , function ($, XDate) {

            var WindowConfig = function (config) {
                this.photosize = config.photosize;
                this.photomimes = config.photomimes;
                this.photoext = config.photoext;
                this.phototypes = config.phototypes;
                this.signaturesize = config.signaturesize;
                this.signaturemimes = config.signaturemimes;
                this.signatureext = config.signatureext;
                this.signaturetypes = config.signaturetypes;
                this.fileCumulativeSize = config.fileCumulativeSize;
                this.filesize = config.filesize;
                this.filemimes = config.filemimes;
                this.fileext = config.fileext;
                this.filetypes = config.filetypes;
                this.version = config.version;
                this.serverDateTime = config.serverDateTime;
                this.startDateTime = new XDate(true);
                this.defaultEwaEditorContext = config.defaultEwaEditorContext;
                this.operatingSystem = config.operatingSystem;
                this.browserName = config.browserName;
                this.browserDescription = config.browserDescription;
                this.cloudExportStorageFolder = config.cloudExportStorageFolder;
                this.cloudStorageFolder = config.cloudStorageFolder;
                this.cloudShareFolder = config.cloudShareFolder;
                this.gdrive = config.gdrive;
                this.onedrive = config.onedrive;
                this.onedriveAppkey = config.onedriveAppkey;
                this.onedriveCallbackUrl = config.onedriveCallbackUrl;
                this.onedriveFilePickerAppId = config.onedriveFilePickerAppId;
                this.onedriveFilePickerCallbackUrl = config.onedriveFilePickerCallbackUrl;
                this.googledriveAppId = config.googledriveAppId;
                this.googledriveClientId = config.googledriveClientId;
                this.googledriveDevKey = config.googledriveDevKey;
                this.googledriveClientEmail = config.googledriveClientEmail;
                this.dropboxAppKey = config.dropboxAppKey;
                this.dropboxCallbackUrl = config.dropboxCallbackUrl;
                this.remoteUploadPartnerKey = config.remoteUploadPartnerKey;
                this.remoteUploadCallbackUrl = config.remoteUploadCallbackUrl;
                this.remoteUploadPartnerName = config.remoteUploadPartnerName;
                this.remoteUploadPartnerLocale = config.remoteUploadPartnerLocale;
                this.sharedRemoteModel = config.sharedRemoteModel;
                this.sharedRemoteSharedEmail = config.sharedRemoteSharedEmail;
                this.sharedRemoteRecipientEmail = config.sharedRemoteRecipientEmail;
                this.showShareButton = (config.showShareButton === 'true');
                this.showShareXingButton = (config.showShareXingButton === 'true');
                this.showShareMonsterButton = (config.showShareMonsterButton === 'true');
                this.showShareCvLibraryButton = (config.showShareCvLibraryButton === 'true');
                this.showShareAnpalButton = (config.showShareAnpalButton === 'true');
                this.showShareIndeedButton = (config.showShareIndeedButton === 'true');
                this.showShareEuresButton = (config.showShareEuresButton === 'true');
                this.showShareForReview = (config.showShareForReview === 'true');
                this.showNewEuropassPortalNotification = (config.showNewEuropassPortalNotification === 'true');
                this.survey = config.survey;
                this.showCloudLogin = (config.showCloudLogin === 'true');
                this.cookieId = config.cookieId;
                this.cloudCookieId = config.cloudCookieId;
                this.cloudAccessToken = config.cloudAccessToken;
                this.connectedCloudCookie = config.connectedCloudCookie;
                this.showLinkedIn = (config.showLinkedin === 'true');
                this.projectVersion = config.projectVersion;
                this.environment = config.environment;
                this.matomoUrl = config.matomoUrl;
                this.permissionToKeepCv = (config.permissionToKeepCv === 'true');
                this.permissionToKeepNotImportedCv = (config.permissionToKeepNotImportedCv === 'true');
            };

            WindowConfig.prototype.allowedMaxSize = function (type) {
                switch (type) {
                    case "photo":
                    {
                        return this.getPhotoSize();
                    }
                    case "signature":
                    {
                        return this.getSignatureSize();
                    }
                    case "cumulative":
                    {
                        return this.getFileCumulativeSize();
                    }
                    default:
                    {
                        return this.getFileSize();
                    }
                }
            };
            WindowConfig.prototype.allowedFileExtension = function (type) {
                switch (type) {
                    case "esp":
                    {
                        return this.getEspExtensions();
                    }
                    case "photo":
                    {
                        return this.getPhotoExtensions();
                    }
                    case "signature":
                    {
                        return this.getSignatureExtensions();
                    }
                    default:
                    {
                        return this.getFileExtensions();
                    }
                }
            };
            WindowConfig.prototype.allowedFileType = function (type) {
                switch (type) {
                    case "esp":
                    {
                        return this.getEspTypes();
                    }
                    case "photo":
                    {
                        return this.getPhotoTypes();
                    }
                    case "signature":
                    {
                        return this.getSignatureTypes();
                    }
                    default:
                    {
                        return this.getFileTypes();
                    }
                }
            };
            WindowConfig.prototype.isAllowedFileExtension = function (type, ext) {
                var extensions = "";
                switch (type) {
                    case "esp":
                    {
                        extensions = this.getEspExtensions();
                        break;
                    }
                    case "photo":
                    {
                        extensions = this.getPhotoExtensions();
                        break;
                    }
                    case "signature":
                    {
                        extensions = this.getSignatureExtensions();
                        break;
                    }
                    default:
                    {
                        extensions = this.getFileExtensions();
                        break;
                    }
                }
                if (extensions === "") {
                    return true;
                }
                var extArray = extensions.split(",");
                return ($.inArray(ext.toLowerCase(), extArray) !== -1);
            };
            WindowConfig.prototype.isAllowedMimeType = function (type, thisMime) {
                var mimes = "";
                switch (type) {
                    case "esp":
                    {
                        return this.getEspMimes();
                    }
                    case "photo":
                    {
                        mimes = this.getPhotoMimes();
                        break;
                    }
                    case "signature":
                    {
                        mimes = this.getSignatureMimes();
                        break;
                    }
                    default:
                    {
                        mimes = this.getFileMimes();
                        break;
                    }
                }
                return (mimes.indexOf(thisMime) >= 0);
            };
            WindowConfig.prototype.allowedFileMime = function (type) {
                switch (type) {
                    case "photo":
                    {
                        return this.getPhotoMimes();
                    }
                    case "signature":
                    {
                        return this.getSignatureMimes();
                    }
                    default:
                    {
                        return this.getFileMimes();
                    }
                }
            };
            WindowConfig.prototype.getEspExtensions = function () {
                return ".xml,.pdf";
            };
            WindowConfig.prototype.getEspTypes = function () {
                return "XML,PDF";
            };
            WindowConfig.prototype.getEspMimes = function () {
                return "application/xml,text/xml,application/pdf,application/x-pdf";
            };
            WindowConfig.prototype.getPhotoSize = function () {
                return (this.photosize === null) ? "" : this.photosize;
            };
            WindowConfig.prototype.getPhotoTypes = function () {
                return (this.phototypes === null) ? "" : this.phototypes;
            };
            WindowConfig.prototype.getPhotoMimes = function () {
                return (this.photomimes === null) ? "" : this.photomimes;
            };
            WindowConfig.prototype.getPhotoExtensions = function () {
                return (this.photoext === null) ? "" : this.photoext;
            };
            WindowConfig.prototype.getSignatureSize = function () {
                return (this.signaturesize === null) ? "" : this.signaturesize;
            };
            WindowConfig.prototype.getSignatureTypes = function () {
                return (this.signaturetypes === null) ? "" : this.signaturetypes;
            };
            WindowConfig.prototype.getSignatureMimes = function () {
                return (this.signaturemimes === null) ? "" : this.signaturemimes;
            };
            WindowConfig.prototype.getSignatureExtensions = function () {
                return (this.signatureext === null) ? "" : this.signatureext;
            };
            WindowConfig.prototype.getFileCumulativeSize = function () {
                return (this.fileCumulativeSize === null) ? "" : this.fileCumulativeSize;
            };
            WindowConfig.prototype.getFileSize = function () {
                return (this.filesize === null) ? "" : this.filesize;
            };
            WindowConfig.prototype.getFileTypes = function () {
                return (this.filetypes === null) ? "" : this.filetypes;
            };
            WindowConfig.prototype.getFileMimes = function () {
                return (this.filemimes === null) ? "" : this.filemimes;
            };
            WindowConfig.prototype.getFileExtensions = function () {
                return (this.fileext === null) ? "" : this.fileext;
            };
            WindowConfig.prototype.getVersion = function () {
                return (this.version === null) ? "" : this.version;
            };
            WindowConfig.prototype.getServerDateTime = function () {
                return (this.serverDateTime === null) ? "" : this.serverDateTime;
            };
            /**
             * Get the period of stay in the page since first loading (in minutes)
             * 
             */
            WindowConfig.prototype.getElapsedMinutes = function () {
                return this.startDateTime.diffMinutes(new XDate(true));
            };
            WindowConfig.prototype.getServerLastUpdateDate = function () {
                var serverDate = new XDate(this.getServerDateTime(), true);
                var elapsedMinutes = this.getElapsedMinutes();
                return serverDate.addMinutes(elapsedMinutes);
            };
            /**
             * Return the context of the web application
             * Important! used during navigation
             * @returns {String}
             */
            WindowConfig.prototype.getDefaultEwaEditorContext = function () {

                var returnContext = "/editors";

                if (this.defaultEwaEditorContext !== null) {
                    var confContext = "";
                    if (this.defaultEwaEditorContext.substring(0, 1) != "/") {
                        confContext += "/";
                    }
                    confContext += this.defaultEwaEditorContext;

                    returnContext = confContext;
                }
                return returnContext;
            };

            WindowConfig.prototype.getOperatingSystem = function () {
                return this.operatingSystem;
            };

            WindowConfig.prototype.getBrowserName = function () {
                return this.browserName;
            };

            return WindowConfig;
        }
);