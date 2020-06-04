define(
        ['backbone', 'europass/http/ServicesUri', 'jquery'/*, 'i18n!localization/nls/Notification'*/],
        function (Backbone, ServicesUri, $/*, Notification*/) {

            Backbone.View.prototype.reportError = function (errorStr) {

                var loggingUrl = ServicesUri.logging;

                var postdata = {
                    loggingInfo: {
                        errorMessage: errorStr,
                        userAgent: window.navigator.userAgent,
                        errCode: ''
                    }
                };

                $.ajax({
                    url: loggingUrl,
                    type: "POST",
                    data: JSON.stringify(postdata),
                    contentType: "application/json; charset=utf-8",
                    dataType: "json",
                    success: function (data, textStatus, jqXHR) {
                        displayErrorMessage(data, true);
                    },
                    error: function (jqXHR, textStatus, errorThrown) {
                        displayErrorMessage(null, false);
                    }
                });
            };

            displayErrorMessage = function (data, hasErrCode) {


                require(
                        ['i18n!localization/nls/Notification'],
                        function (Notification) {

                            var text = Notification["Global.JavaScript.Error"];
                            if (text === undefined || text === null || text === "") {
                                //text = "A general JavaScript error has occured that prevents the proper function of the application. Please contact the Europass administrators the soonest possible, reporting your problem;
                                text = "<p>A general error has occurred (JavaScript). Please try again later. If the problem persists, please contact the <a href=\"http://europass.cedefop.europa.eu/en/contact\">Europass Team</a> </p>";
                            }

                            if (hasErrCode) {
                                if (data !== null && data !== undefined) {
                                    if (data.loggingInfo !== null && data.loggingInfo !== undefined) {
                                        if (data.loggingInfo.completeErrorCode !== null && data.loggingInfo.completeErrorCode !== undefined) {
                                            text += "<em class=\"trace-code\">" + data.loggingInfo.completeErrorCode + "</em>";
                                            text += "<span class=\"hidden\">" + data.loggingInfo.specificErrorMessage + "</span>";
                                        }
                                    }
                                }
                            }
                            $("body").trigger("europass:message:show", ["error", text]);
                        }
                );


            };
        }
);