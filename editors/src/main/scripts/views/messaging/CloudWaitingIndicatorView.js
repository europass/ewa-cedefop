/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


define(
        ['jquery', 'backbone', 'hbs!templates/messaging/cloud_waitingindicator'],
        function ($, Backbone, WaitIndicatorTpl) {

            var CloudWaitingIndicatorView = Backbone.View.extend({
                el: "body"

                , waitSelector: "#waiting-indicator-cloud"

                , events: {
                    "europass:waiting:indicator:cloud:show": "show",
                    "europass:waiting:indicator:cloud:hide": "hideAndInterrrupt",
                    "click #abort_cloud_connection": "signOut"
                }

                , initialize: function (options) {

                    this.model = options.model;

                    this.waitIndicatorTpl = WaitIndicatorTpl;

                    //Event when the Model LearnerInfo has changed
                    this.model.bind("model:content:changed", this.hide, this);
                    this.model.bind("model:linked:attachment:changed", this.hide, this);
                    this.model.bind("model:content:reset", this.hide, this);
                    this.model.bind("model:uploaded:esp", this.hide, this);
                    this.model.bind("model:prefs:order:changed", this.hide, this);
                    this.model.bind("model:list:sort:change", this.hide, this);
                    this.model.bind("model:uploaded:cloud", this.hide, this);
                    this.model.bind("model:loaded:cloud:document", this.hide, this);
                }
                , onClose: function () {
                    this.model.unbind("model:content:changed", this.hide);
                    this.model.unbind("model:linked:attachment:changed", this.hide);
                    this.model.unbind("model:content:reset", this.hide);
                    this.model.unbind("model:uploaded:esp", this.hide);
                    this.model.unbind("model:prefs:order:changed", this.hide);
                    this.model.unbind("model:list:sort:change", this.hide);
                    this.model.unbind("model:uploaded:cloud", this.hide);
                    this.model.unbind("model:loaded:cloud:document", this.hide);
                }

                , browserSupportsCSSProperty: function (propertyName) {
                    var elm = document.createElement('div');
                    propertyName = propertyName.toLowerCase();
                    if (elm.style[propertyName] !== undefined && elm.style[propertyName] !== "")
                        return true;
                    var propertyNameCapital = propertyName.charAt(0).toUpperCase() + propertyName.substr(1), domPrefixes = 'Webkit Moz ms O'.split(' ');
                    for (var i = 0; i < domPrefixes.length; i++) {
                        if (elm.style[domPrefixes[i] + propertyNameCapital] !== undefined)
                            return true;
                    }
                    return false;
                }

                /**
                 * Append the waiting indicator below the body
                 */
                , show: function (event, withOwnTimer) {
                    //console.log("waiting show!");
                    var body = this.$el;
                    var indicator = body.find(this.waitSelector);
                    if (indicator.length === 0) {
                        var template = this.waitIndicatorTpl;
                        var html = template();
                        body.prepend(html);

                        //start a timer and end this timer after 4 seconds
                        if (withOwnTimer !== true) {
                            var that = this;
                            var counter = 1;
                            this.timer = window.setInterval(function () {
                                counter++;
//							console.log("counter:"+counter);

                                if (counter === 400) {
                                    that.hideAndInterrrupt();
                                }
                            }, 10);
                        }
                    }
                }
                /**
                 * Remove the waiting indicator, if there is one
                 */
                , hideAndInterrrupt: function () {
//				console.log("hide and interrrupt...");
                    window.clearInterval(this.timer);
                    this.hide();
                }
                /**
                 * Remove the waiting indicator, if there is one
                 */
                , hide: function () {
                    //console.log("waiting remove	!");
                    var body = this.$el;
                    var indicator = body.find(this.waitSelector);
                    if (indicator.length > 0) {
                        indicator.remove();
                    }
                },
                signOut: function () {
                    $("body").trigger("europass:cloud:manage:disconnect");
                }
            });

            /* vpol commented the text from the Europass Logo
             var divLogo = document.getElementById("page-loading-waiting-indicator-graphic-text");
             if (divLogo != null && divLogo!="" && divLogo!= undefined){
             var logo = GuiLabel[ 'EWA.Moto' ] ;
             if (logo == undefined || logo == null || logo == "") 
             logo = "ONLINE EDITOR";
             divLogo.innerHTML = logo;
             }*/
            return CloudWaitingIndicatorView;
        }
);