define(
        [
            'jquery',
            'HttpUtils',
            'europass/http/SessionManagerInstance'
        ],
        function ($, HttpUtils, SessionManager) {

            var UploadUtils = {};

            UploadUtils.uploadSuccess = function (data, messageContainer, filetype, handleFailureFunc) {

                var result = data.result;

                if (result === undefined || result === null || result === "") {
                    HttpUtils.statusResponse(505, messageContainer);
                    return false;
                }
                /* WORKAROUND SO THAT BOTH APPROACHES - WITH IFRAME TRANSPORT OR NOT - CONTINUE TO WORK */
                var html = null;
                if (result.prevObject !== undefined && result.prevObject.prop("tagName") === "IFRAME") {
                    html = $(result).contents();
                } else if (typeof (result) === "string") {
                    //jQuery cannot parse html text that starts will <html>
                    html = $("<div></div>").append(result);
                }
                if (html === null) {
                    HttpUtils.statusResponse(505, messageContainer);
                    return false;
                }
                var status = html.find("meta[name=\"status\"]").attr("content");

                var statusNotNullOrUndef = status !== undefined && status !== null;

                var status4xx = statusNotNullOrUndef && typeof status === "string" && status.charAt(0) === "4";

                // check if the status is "error" or has a 4xx code
                if (statusNotNullOrUndef && status === "error" || status4xx) {
                    return handleFailureFunc(messageContainer, html, data.files[0], filetype);
                }

                var jsonStr = html.find("script[type=\"application/json\"]").html();

                if (jsonStr === undefined || jsonStr === null || jsonStr === "") {
                    HttpUtils.statusResponse(505, messageContainer);
                    return false;
                }

                var json = $.parseJSON(jsonStr);

                if (!$.isPlainObject(json) || $.isEmptyObject(json)) {
                    HttpUtils.statusResponse(505, messageContainer);
                    return false;
                }

                return json;
            };

            UploadUtils.uploadFail = function (data, messageContainer, filetype, uploadAbortFunc, handleFailureFunc) {
                var xhr = data.jqXHR;

                var status = (xhr === undefined) ? "505" : xhr.status;
                //console.log("HTTP Status: " + status );
                /* In case of upload and 500, 
                 * further investigate the error codes looking for invalid mime and big size.
                 */
                var httpCode = parseInt(status);

                //console.log("status: " + status );
                if (httpCode === 0 || httpCode === 505) {
                    /* Aborted! */
                    return uploadAbortFunc(messageContainer, data.files[0], filetype);
                } else if (httpCode >= 400) {
                    /* CONSUME RESPONSE OF TEXT/HTML TYPE */
                    var html = $("<div></div>").append(xhr.responseText);

                    return handleFailureFunc(messageContainer, html, data.files[0], filetype);
                }

                //Handle generic rest status codes if any
                HttpUtils.statusResponse(httpCode, messageContainer);
            };

            return UploadUtils;
        }
);
