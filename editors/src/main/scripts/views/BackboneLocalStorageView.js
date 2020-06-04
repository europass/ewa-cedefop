define(
        ['backbone', 'i18n!localization/nls/Notification'],
        function (Backbone, Notification) {

            Backbone.View.prototype.updateConfigLocale = function (locale, viewUrl) {

                var Model = this.model;

                if (Model === undefined) {
                    return false;
                }
                try {
                    window.localStorage.setItem("europass.ewa.locale", locale);
                    //Do not set locale now, it will be set, along with model translation (when the respective resources become available) on reload
                    window.localStorage.setItem("europass.ewa.skillspassport.v3", Model.conversion().toStorable());
                } catch (err) {
                    var msg = Notification["no.local.storage.reload.alert"];
                    if (msg === undefined || msg === null || msg === "") {
                        msg = "Your browser settings do not allow the temporary storage of data. If you continue you may lose all your data.";
                    }
                    var r = window.confirm(msg);
                    if (!(r === true)) {
                        //cancel and return
                        return true;
                    }
                }

                //Reload and change the url according to the new locale
                var url = window.location.pathname;
                var context = url.substring(0, url.indexOf("/", 1));

                var view = (viewUrl === undefined || viewUrl === null || viewUrl === "")
                        //? url.substring(context.length + 3)
                        // After change of url locale path pattern (CAN SUPPORT e.g sr-latn) we need to change how to take url view !!!)
                        ? url.substring(url.indexOf('/', url.indexOf('/', 1) + 1))
                        : viewUrl;

                view = (view.indexOf("/") === 0) ? view : "/" + view;

                var newUrl = context + "/" + locale + view;
                window.location = newUrl;
            };
        }
);