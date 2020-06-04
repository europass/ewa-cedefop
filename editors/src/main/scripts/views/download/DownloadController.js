define(
        [
//		'module',
            'jquery',
            'underscore',

            'HttpUtils',
            'Utils',
            'europass/http/ServicesUri',
            'europass/http/SessionManagerInstance',

            'i18n!localization/nls/Notification'
        ],
        function ($, _, HttpUtils, Utils, ServicesUri, Session, Notification) {
            /**
             * Constructor
             * @param relatedController: the BackboneView which includes this Controller, e.g. LinkedInUploadView
             * @param messageContainer: jQuery element
             */
            var DownloadController = function (config) {
                this.relatedController = config.relatedController;
                this.messageContainer = config.messageContainer || $("body");
                this.info = config.info;
            };
            /**
             * Called when this controller is no longer useful for clean up purposes
             */
            DownloadController.prototype.cleanup = function () {
            };
            /**
             * Decide on the URL based on the button clicked
             */
            DownloadController.prototype.decideUrl = function () {

                var location = this.info.getLocation();
                var convertTo = location;

                if (["partners", "eures"].indexOf(location) === -1) {
                    var filetype = this.info.getFileFormat();
                    convertTo += "_" + filetype;
                }
                var downloadurl = ServicesUri.document_conversion_to[convertTo];
                return downloadurl;
            };
            /**
             * Prepare data to create the folder
             */
            DownloadController.prototype.prepareFolder = function (folder) {
                return {"title": folder, "parents": [{"id": "root"}], "mimeType": "application/vnd.google-apps.folder"};
            };
            /**
             * Prepare data to be sent via the POST
             */
            DownloadController.prototype.prepareData = function (accessToken, folder) {
                var data = {
                    json: Utils.encodePlusCharPercent(this.relatedController.model.conversion().toTransferable()),
                    folder: folder,
                    token: accessToken
                };
                return data;
            };
            /**
             * Failed to perform HTTP POST
             * 
             * @param status
             * @param responseText
             * @param appendToText: text to append to the message
             */
            DownloadController.prototype.onUploadFailure = function (status, responseText, appendToText) {
//		console.log(responseText);
                //Parsing the raw HTML responseText in order to find the Error JSON object
                var info = HttpUtils.readHtmlErrorResponse(responseText);
                if ("error" === info.status) {
                    var defaultMsg = (Notification["skillspassport.export.cloud.error"] || "Notification[\"skillspassport.export.cloud.error\"]");
                    //If there is an error status, then we try to fetch a message based on the Error JSON object code
                    // Trace is also append
                    var message = HttpUtils.downloadErrorMessage(info.msg, defaultMsg);
                    if (!_.isEmpty(appendToText)) {
                        message += appendToText;
                    }
                    //Finally, display the error message or a default text.
                    this.messageContainer.html("");
                    this.messageContainer.trigger("europass:message:show",
                            ["error", (message || "There was a problem storing the Europass document to cloud provider")]);

                }
                $("body").trigger("europass:waiting:indicator:hide");
            };

            /**
             * pgia: Fix for EWA-1441, escape '+' character before decoding
             * Replace all occurences of original with replacement on str
             * 
             * @param str
             * @param original
             * @param replacement
             */
            DownloadController.prototype.replaceAll = function (str, original, replacement) {
                var re = new RegExp(original, 'g');
                return str.replace(re, replacement);
            };

            /**
             * @param msgKey: string
             * @param status: string
             * @param blink: boolean
             * @param appendToMsg : string
             */
            DownloadController.prototype.triggerMessage = function (msgKey, status, blink, appendToMsg) {
                var defaultMsg = "Notification[\"" + msgKey + "\"]";
                var message = Notification[ msgKey ] || defaultMsg;

                if (!_.isEmpty(appendToMsg))
                    message += appendToMsg;

                this.messageContainer
                        .trigger("europass:message:show",
                                [status, (message || defaultMsg), blink]);
            };

            DownloadController.prototype.triggerMessageWithPathFile = function (msgKey, pathfile, status, blink, appendToMsg) {

                var defaultMsg = "Notification[\"" + msgKey + "\"]";
                var message = Notification[ msgKey ] || defaultMsg;

                message = message.replace("<pathfile>", pathfile);

                if (!_.isEmpty(appendToMsg))
                    message += appendToMsg;

                this.messageContainer
                        .trigger("europass:message:show",
                                [status, message, blink]);
            };

            DownloadController.prototype.triggerMessageWithPathFileEmail = function (msgKey, pathfile, emailTo, status, blink, appendToMsg) {

                var defaultMsg = "Notification[\"" + msgKey + "\"]";
                var message = Notification[ msgKey ] || defaultMsg;

                message = message.replace("<pathfile>", pathfile);
                message = message.replace("<emailto>", emailTo);

                if (!_.isEmpty(appendToMsg))
                    message += appendToMsg;

                this.messageContainer.trigger("europass:message:show", [status, message, blink]);
            };

            return DownloadController;
        }
);
