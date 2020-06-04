define(
        [
            'jquery',
//		EWA-1811
//		'underscore',
            'backbone',
            'europass/http/ServicesUri',
            'europass/http/Resource',
            'europass/http/SessionManagerInstance'
//		EWA-1811
//		,'i18n!localization/nls/Notification'
        ],
        function ($,
//		EWA-1811
//		_,
                Backbone,
                ServicesUri,
                Resource,
                Session
//		EWA-1811
//		,Notification
                ) {

            var SessionKeepAliveView = Backbone.View.extend({

                events: {
                    "europass.session.keepAlive": "keepAlive",
                    "europass.session.stopKeepAlive": "stopTimer"
                }

                , initialize: function (options) {
                    this.timer_ON = false;
                    this.timer = null;

                    this.pingURL = ServicesUri.scopeValidity;
                    this.model = options.model;

                    this.interval = 60000; //a minute

                    //If the model already has Photo or Attachments as an outcome of switching language set the trigger!
                    var modelInfo = this.model.info();
                    if (modelInfo !== null
                            && (modelInfo.hasPhoto() || modelInfo.hasSignature() || modelInfo.hasAttachments())) {
                        this.keepAlive();
                    }

                    //Bind on model:content:reset
                    this.model.bind("model:content:reset", this.onEmptyEsp, this);

                }
                /**
                 * Clean up on close
                 */
                , onClose: function () {
                    this.stopTimer();
                    delete this.timer;
                    delete this.timer_ON;
                    delete this.interval;

                    this.model.unbind("model:content:reset", this.onEmptyEsp);
                }
                /**
                 * When the CV/ESP is reset we need to stop the timer
                 */
                , onEmptyEsp: function () {
                    this.stopTimer();
                }
                /**
                 * stops the timer of keeping session alive
                 */
                , stopTimer: function () {
                    //The setInterval() method will continue calling the function until clearInterval() is called, 
                    //or the window is closed.
                    if (this.timer_ON === true) {
                        if (this.timer !== null) {
                            clearInterval(this.timer);
                            //reset the variables
                            this.timer = null;
                            this.timer_ON = false;
                        }
                    }

                }
                /**
                 * starts the timer of keeping session alive
                 */
                , keepAlive: function () {

                    var that = this;
                    var fn = function () {
                        var uri = that.pingURL + Session.urlappend();
                        //that.callServer(uri);					
                    };
                    if (this.timer_ON === false) {
                        //set the variables
                        this.timer_ON = true;
                        fn();
                        this.timer = setInterval(fn, this.interval);
                    }

                }
                /**
                 * Perform a call to the server
                 */
                , callServer: function (url) {

                    var ajaxGet = $.ajax({

                        type: "GET",

                        url: url,

                        context: this,

                        dataType: "html",

                        success: function (data, textStatus, jqXHR) {
                            //Consume the HTML Response
                            var respObj = this.parseResponse(data);

                            //Invalid Scope
                            var json = respObj.json;
                            if (json !== undefined && json !== null && json.scopeValid === false) {

                                Session.set(respObj.session);

                                this.stopTimer();
                            }
                        },

//					error: function(){/*DO NOTHING when server request/response*/},
                        cache: false, //if set to false it will force the pages that you request to not be cached by the browser
                        // don't want the execution to wait until the response has been received from the server
                        // By default, all requests are sent asynchronous, synchronous requests may temporarily lock the browser
                        async: true //set to true to be sure
                    });
                    return ajaxGet.responseText;
                },

                parseResponse: function (data) {

                    var html = $("<div id=\"tmpAjaxResp\"></div>").append(data);

                    var status = html.find("meta[name=\"status\"]").attr("content");

                    var sessionid = html.find("meta[name=\"jsessionid\"]").attr("content");

                    var jsonStr = html.find("script[type=\"application/json\"]").html();

                    var json = $.parseJSON(jsonStr);

                    $("#tmpAjaxResp").remove();

                    return {
                        status: status,
                        session: sessionid,
                        json: json
                    };
                }


            });

            return SessionKeepAliveView;
        }
);