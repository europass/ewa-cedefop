define(
        [
            'jquery',
            'backbone',
            'backbonenested',
            'module'
        ],
        function ($, Backbone, BackboneNested, module) {
            var ShareCloudInfoModel = Backbone.NestedModel.extend({
                defaults: {
                    ShareInfo: {
                        Locale: "",
                        Link: "",
                        Sender: "",
                        FullName: "",
                        Email: "",
                        Cc: "",
                        Subject: "",
                        Message: ""
                    }
                }

                , initialize: function () { }

                /*			,populateEnvironmentInfo: function(){
                 var EnvironmentInfo = {
                 Javascript : "Yes",
                 Cookies: navigator.cookieEnabled,
                 Language: module.config().locale || "en",
                 Screen_Depth: window.screen.width+"x"+window.screen.height,
                 Color_Depth: window.screen.colorDepth,
                 Browser_Screen : window.innerWidth+"x"+window.innerHeight,
                 Java_Enabled: navigator.javaEnabled()
                 };
                 
                 this.set("ContactInfo.EnvironmentInfo", EnvironmentInfo ,{ silent : true });
                 }*/

                , clearModel: function () {
                    this.clear().set(this.defaults);
                }
            });
            return ShareCloudInfoModel;
        }
);