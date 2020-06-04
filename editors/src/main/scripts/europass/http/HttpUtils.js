define(
        'HttpUtils',
        [
            'jquery',
            'underscore',
            'Utils',
            'europass/http/Resource',
            'europass/http/ServicesUri',
            'europass/http/SessionManagerInstance',
            'i18n!localization/nls/Notification',
            'i18n!localization/nls/GuiLabel'
        ],
        function ($, _, Utils, Resource, ServicesUri, Session, Notification, GuiLabel) {
            var HttpUtils = {};

            HttpUtils.download = function (url, data, method, target) {
                //url and data options required
                if (url && data) {
                    //data can be string of parameters or array/object
                    data = typeof data == 'string' ? data : $.param(data);
                    //split params into form inputs
                    var inputs = '';
                    $.each(data.split('&'), function () {
                        var pair = this.split('=');
                        inputs += '<input type="hidden" name="' + pair[0] + '" value="' + pair[1] + '" />';
                    });
                    //send request
                    //VPOL: was enctype=\"application/x-www-form-urlencoded\" changed to enctype=\"multipart/form-data\" because of https://jira.cedefop.europa.eu/browse/EWA-1704 Impossible to upload CV/photo/attachment using IE9
                    //was giving error on IE9 A message body reader for Java class com.sun.jersey.multipart.FormDataMultiPart, and Java type class com.sun.jersey.multipart.FormDataMultiPart, and MIME media type application/octet-stream was not found.
                    var downloadRequestForm = $('<form target=\"' + target + '\" enctype=\"application/x-www-form-urlencoded\" onsubmit="' + HttpUtils.IOSPreviewFormOnSubmit + '" action="' + url + '" accept-charset="utf-8" method="' + (method || 'post') + '">' + inputs + '</form>');
                    downloadRequestForm.appendTo($(".modal"));
                    downloadRequestForm.submit();
                    downloadRequestForm.remove();
                }
            };

            HttpUtils.formatBytes = function (bytes) {
                if (bytes === undefined || bytes === null || bytes === "" || isNaN(bytes)) {
                    return "";
                } else if (bytes === 0) {
                    return "0 bytes";
                } else {
                    var s = ['bytes', 'kb', 'MB', 'GB', 'TB', 'PB'];
                    var e = Math.floor(Math.log(bytes) / Math.log(1024));
                    return (bytes / Math.pow(1024, Math.floor(e))).toFixed(2) + " " + s[e];
                }
            };
            HttpUtils.unFormatBytes = function (bytesStr) {
                if (bytesStr === undefined || bytesStr === null || bytesStr === "") {
                    return -1;
                } else {
                    var inBytes = bytesStr.match(/bytes/);
                    var inKiloBytes = bytesStr.match(/kb|KB/);
                    var inMGTPBytes = bytesStr.match(/([MGTP])B/);

                    if (inBytes !== null) {
                        bytesStr = bytesStr.replace(/bytes/g, "");
                        return parseInt(bytesStr);
                    } else if (inKiloBytes !== null) {
                        bytesStr = bytesStr.replace(/kb|KB/g, "");
                        var toInt = parseInt(bytesStr);
                        if (isNaN(toInt)) {
                            return -1;
                        }
                        return (toInt * 1024);
                    } else if (inMGTPBytes !== null) {
                        var metric = inMGTPBytes[1];
                        var power = 0;
                        switch (metric) {
                            case "M":
                            {
                                power = 2;
                                break;
                            }
                            case "G":
                            {
                                power = 3;
                                break;
                            }
                            case "T":
                            {
                                power = 4;
                                break;
                            }
                            case "P":
                            {
                                power = 5;
                                break;
                            }
                            default:
                            {
                                power = 2;
                                break;
                            }
                        }
                        bytesStr = bytesStr.replace(/[MGTP]B/g, "");
                        var toInt = parseInt(bytesStr);
                        if (isNaN(toInt)) {
                            return -1;
                        }
                        return (toInt * Math.pow(1024, power));
                    }
                    return -1;
                }
            };
            /**
             * Returns an object containing the status int and possibly an html element containing an error text
             * {
             *   status  : int HTTP code,
             *   state   : String [ "error" ],
             *   message : String Html
             * }
             */
            HttpUtils.statusResponse = function (status, messageContainer) {
                var msg = "";
                var state = "error";
                if (status === undefined || status === null || status === 0) {
                    msg += (Notification["error.code.general"]);
                }
                if (messageContainer === undefined || messageContainer === null) {
                    messageContainer = $("body > section.notifications");
                }
                var httpCode = parseInt(status);
                if (httpCode === 403) {
                    msg += ("<em class=\"http-" + httpCode + "\">" + Notification["error.code.status403"] + "</em>");
                } else if (httpCode >= 400 && httpCode < 500) {
                    msg += ("<em class=\"http-" + httpCode + "\">" + Notification["error.code.status400"] + "</em>");
                } else if (httpCode >= 500) {
                    msg += ("<em class=\"http-" + httpCode + "\">" + Notification["error.code.status500"] + "</em>");
                }
                messageContainer.trigger("europass:message:show", [state, msg]);
            };

            HttpUtils.downloadErrorMessage = function (response, defaultText) {
                var defaultMsg = defaultText || Notification["error.code.status500"]; //Download.Document.Failure
                var errorMessage = defaultMsg;
                if (response === undefined || response === null) {
                    return "<p>" + defaultMsg + "</p>";
                }
                var error = response.Error;
                if (error === undefined || error === null) {

                    if (response !== undefined && response !== null) {
                        if (typeof response == 'string') {
                            return "<p>" + response + "</p>";
                        }
                    }

                    return "<p>" + defaultMsg + "</p>";
                }
                var errorCode = error.code;
                if (errorCode === undefined || errorCode === null || "" === errorCode) {
                    return "<p>" + defaultMsg + "</p>"; //general unspecified 
                }
                var msg = Notification[errorCode];
                var messageInfo = HttpUtils.msgFromErrCodeFailedUpload(errorCode);

                if (messageInfo === undefined || messageInfo === null) {
                    return msg || defaultMsg;
                }

                var knownIssue = false;
                if (messageInfo.known !== undefined && messageInfo.known !== null)
                    knownIssue = messageInfo.known;
                errorMessage = messageInfo.message || msg;
                var traceCode = (error.trace && !knownIssue) ? "<em class=\"trace-code\">" + error.trace + "</em>" : "";
                return "<p>" + errorMessage + "</p>" + traceCode;
            };
            /**
             * Reads the html response as this one is written in the body of the iframe
             * @param iframe
             */
            HttpUtils.STATUS_META_PATTERN = new RegExp(/<meta name="status" content="(.*)"\/>/);

            HttpUtils.JSON_SCRIPT_PATTERN = new RegExp(/<script type="application\/json">(.*)<\/script>/);

            HttpUtils.readHtmlResponse = function (iframe) {
                var defaultError = {"status": "error"};

                var contents = iframe.contents();
                //Find meta tag with name = status
                var metaStatusEl = contents.find("meta[name=\"status\"]");
                var status = null;
                var jsonStr = null;

                status = metaStatusEl.attr("content");
                var jsonEl = contents.find("script[type=\"application/json\"]");
                if (jsonEl.length == 0) {
                    //check for head title
                    var headTitle = contents.find("h1");
                    if (headTitle.length > 0) {
                        if (headTitle.text().indexOf("HTTP Status") != -1) {
                            defaultError = {"status": "error",
                                "msg": headTitle.text()
                            };
                        }
                    }
                    return defaultError;
                }

                jsonStr = jsonEl.html();

                if (status == null || jsonStr == null) {
                    return defaultError;
                }

                //Now consume the feedback...
                var json = {};
                try {
                    json = JSON.parse(jsonStr);
                } catch (err) {
                    return defaultError;
                }

                switch (status) {
                    case "OK":
                    {
                        var feedback = json.Feedback;
                        var filteredFeedback = [];
                        if ($.isArray(feedback) === true && feedback.length > 0) {
                            for (var i = 0; i < feedback.length; i++) {
                                var f = feedback[i];
                                if (f.code === "ok") {
                                    continue;
                                }
                                filteredFeedback.push(f);
                            }
                        }
                        return {
                            "status": "success",
                            "msg": {Feedback: filteredFeedback}
                        };
                    }
                    default:
                    {
                        return {
                            "status": "error",
                            "msg": json
                        };
                    }
                }
            };
            HttpUtils.readHtmlErrorResponse = function (html) {
                var defaultError = {
                    "status": "error"
                };

                var contents = $("<div>" + html + "</div>");
                //Find meta tag with name = status
                var metaStatusEl = contents.find("meta[name=\"status\"]");
//			var metaStatusEl = $(html)[2];
                var status = null;

                status = $(metaStatusEl).attr("content");
                var jsonStr = contents.find("script[type=\"application/json\"]").html();
//			var jsonStr = $($(html)[4]).html();

                if (_.isNull(jsonStr) || _.isUndefined(jsonStr)) {
                    return defaultError;
                }
                if (jsonStr.length == 0) {
                    return defaultError;
                }
                if (status == null || jsonStr == null) {
                    return defaultError;
                }

                //Now consume the feedback...
                var json = {};
                try {
                    json = JSON.parse(jsonStr);
                } catch (err) {
                    return defaultError;
                }


                switch (status) {
                    case "OK":
                    {
                        break;
                    }
                    default:
                    {
                        return {
                            "status": "error",
                            "msg": json
                        };
                    }
                }
            };

            HttpUtils.readableFeedback = function (feedbackList) {
                var feedbackMsg = "<ul>";

                for (var i = 0; i < feedbackList.length; i++) {
                    var feedback = feedbackList[i];
                    var msg = null;
                    if (msg === undefined || msg === null) {
                        msg = "File with name \"[[section]]\" could not be included.";
                    }
                    var section = feedback.section;
                    if (!Utils.isEmptyObject(section)) {
                        switch (section.key) {
                            case "[[section]]":
                            {
                                msg = "";
                                msg = Utils.replaceKey(Notification[ feedback.code ], section.key, section.value);
                                break;
                            }
                            case "[[photo]]":
                            {
                                msg = Utils.replaceKey(Notification[ feedback.code ], section.key, GuiLabel["LearnerInfo.Identification.Photo"], "'photo'");
                                break;
                            }
                            case "[[signature]]":
                            {
                                msg = Utils.replaceKey(Notification[ feedback.code ], section.key, GuiLabel["LearnerInfo.Identification.Signature"], "'signature'");
                                break;
                            }
                            default:
                            {
                                msg = Utils.replaceKey(msg, section.key, section.value, "'not-specified'");
                                break;
                            }
                        }
                    }
                    var trace = feedback.trace;
                    if (trace) {
                        msg += "<em class=\"trace-code\">" + trace + "</em>";
                    }
                    feedbackMsg += "<li class=\"" + feedback.level + "\">" + msg + "</li>";
                }
                feedbackMsg += "</ul>";
                return feedbackMsg;
            };

            HttpUtils.msgFromErrCodeFailedUpload = function (errorCode) {
                var msg = "";
                var knownIssue = false;

                switch (errorCode) {
                    case "model.to.json" :
                    {
                        //XML validation against the schema failed
                        msg += (Notification["error.code.xml.model.to.json"]);
                        break;
                    }
                    case "pdf.xml.attachment" :
                    {
                        //No XML attachment in PDF
                        msg += (Notification["error.code.pdf.xml.attachment"]);
                        break;
                    }
                    case "pdf.xml.attachment.invalid.meta" :
                    {
                        //Invalid XML attachment metadata 
                        msg += (Notification["error.code.pdf.xml.attachment.invalid.meta"]);
                        break;
                    }
                    case "pdf.xml.attachment.invalid.quartz" :
                    {
                        //Invalid XML attachment metadata in PDF, edited with quartz editor
                        msg += (Notification["error.code.pdf.xml.attachment.invalid.quartz"]);
                        break;
                    }
                    case "pdf.xml.attachment.invalid.macOS" :
                    {
                        //Invalid XML attachment metadata in PDF, since OS is macOS maybe it was edited with quartz editor
                        msg += (Notification["error.code.pdf.xml.attachment.invalid.macOS"]);
                        break;
                    }
                    case "pdf.xml.attachment.invalid.word" :
                    {
                        //Invalid XML attachment metadata in PDF, since OS is macOS maybe it was edited with quartz editor
                        msg += (Notification["error.code.pdf.xml.attachment.invalid.word"]);
                        break;
                    }
                    case "pdf.password.protected" :
                    {
                        //No XML attachment in PDF
                        msg += (Notification["error.code.pdf.password.protected"]);
                        break;
                    }
                    case "xml.read" :
                    {
                        //XML validation against the schema failed
                        msg += (Notification["error.code.xml.read"]);
                        break;
                    }
                    case "xml.to.model" :
                    {
                        //XML validation against the schema failed
                        msg += (Notification["error.code.xml.read.to.model"]);
                        break;
                    }
                    case "xml.empty" :
                    {
                        //XML validation against the schema failed
                        msg += (Notification["error.code.xml.empty"]);
                        break;
                    }
                    case "xml.undefined.version" :
                    {
                        //XML validation against the schema failed
                        msg += (Notification["error.code.xml.undefined.version"]);
                        knownIssue = true;
                        break;
                    }
                    case "xml.invalid" :
                    {
                        //XML validation against the schema failed
                        msg += (Notification["error.code.xml.validation"]);
                        knownIssue = true;
                        break;
                    }
                    case "xml.invalid.word" :
                    {
                        //XML validation against the schema failed
                        msg += (Notification["error.code.xml.validation.word"]);
                        knownIssue = true;
                        break;
                    }
                    case "xml.fail.transform" :
                    {
                        //Xml transformation failed.
                        msg += (Notification["error.code.xml.transformation"]);
                        break;
                    }
                    case "request.number.quota.exceeded" :
                    {
                        //Requests number exceeded quota allowed
                        msg += (Notification["request.number.quota.exceeded"]);
                        break;
                    }
                    case "file.too.big":
                    {
                        // Case Handled by FileManager
                        break;
                    }
                    case "file.exceeded.cumm.size.limit":
                    {
                        // Case Handled by FileManager
                        break;
                    }
                    case "file.forbidden":
                    {
                        //Forbidden
                        msg += (Notification["error.code.file.forbidden"]);
                        break;
                    }
                    case "content.type.not.defined":
                    {
                        msg += (Notification["file.content.type.undefined"]);
                        break;
                    }
                    case "file.undefined.mime":
                    {
                        // Case Handled by FileManager
                        break;
                    }
                    case "content.type.not.allowed":
                    {
                        msg += (Notification["file.content.type.invalid"]);
                        break;
                    }
                    case "file.invalid.mime":
                    {
                        // Case Handled by FileManager
                        break;
                    }
                    case "file.not.found":
                    {
                        //Not Found
                        msg += (Notification["error.code.file.notfound"]);
                        break;
                    }
                    case "file.url.empty":
                    {
                        msg += (Notification["error.code.pdf.xml.attachment"]);
                        break;
                    }
                    case "file.url.not.allowed":
                    {
                        msg += (Notification["file.url.not.allowed"]);
                        break;
                    }
                    case "file.url.not.available":
                    {
                        msg += (Notification["file.url.not.available"]);
                        break;
                    }
                    case "file.fail.parse":
                    {
                        //Parsing
                        msg += (Notification["error.code.file.parsing"]);
                        break;
                    }
                    case "file.invalid.xml":
                    {
                        //Invalid xml
                        msg += (Notification["error.code.file.invalid"]);
                        break;
                    }
                    case "pdf.thumb":
                    case "photo.thumb":
                    {
                        //Thumbail creation failure
                        msg += (Notification["photo.thumb"]);
                        break;
                    }
                    case "file.other.error":
                    {
                        msg += (Notification["error.code.file.parsing"]);
                        break;
                    }
                    case "download.other.error":
                    {
                        msg += (Notification["download.other.error"]);
                        break;
                    }
                    case "email.invalid.recipient":
                    {
                        msg += Notification["error.code.email.invalid.recipient"];
                        break;
                    }
                    case "email.not.sent":
                    {
                        msg += Notification["error.code.email.notsent"];
                        break;
                    }
                    default:
                    {
                        var message = Notification[ errorCode ];
                        if (message === undefined || message === null || message === "") {
                            message = Notification["error.code.file.parsing"];
                        }
                        msg += (message);
                        break;
                    }
                }
                return {message: msg, known: knownIssue};
            };

            HttpUtils.checkApiAvailability = function () {
                var httpResource = new Resource(ServicesUri.document_conversion_to.available);
                var isAvailable = false;
                httpResource._get({
                    async: false,
                    success: {
                        scope: this,
                        callback: function (response) {
                            if (_.isNull(response) || _.isUndefined(response))
                                isAvailable = false;
                            else
                                isAvailable = true;
                        }
                    },
                    error: {
                        scope: this,
                        callback: function (status, responseText) {
                            isAvailable = false;
                        }
                    },
                });

                return isAvailable;
            };

            return HttpUtils;
        }
);
