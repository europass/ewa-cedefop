define(
        ['jquery']
        , function ($) {

            var GlobalLocalStoreOption = function () {
                this.status = this.readStorable();
            };

            window.onerror = this.handleError;

            GlobalLocalStoreOption.prototype.storableLocalKey = "europass.ewa.store.model.locally";

            GlobalLocalStoreOption.prototype.readStorable = function () {
                var isStorable = true;

                try {
                    //if null, undefined
                    var isStorableLocal = window.localStorage.getItem(this.storableLocalKey);

                    isStorable = !(isStorableLocal === "false");

                } catch (err) {
                    //TODO VPOL: sorry could not call GlobalController.handleGlobalError, or BackboneErrorReportingView.reportError, or Backbone.EWAView.reportError or 
                    //Backbone.View.reportError('test error');
                    //EWABackboneView.reportError('test error');
                    //Backbone.EWAView.reportError('test error');
                    //GlobalController.handleGlobalError('local storage error');
                    //BackboneErrorReportingView.reportError('local storage error');
                    //throw "error reading local storage";
                }

                return isStorable;
            };

//		GlobalLocalStoreOption.prototype.readStorable = function() {
//			var testKey = 'test', storage = window.sessionStorage;
//			try {
//				storage.setItem(testKey, '1');
//				storage.removeItem(testKey);
//				return true;
//			} catch (error) {
//				return false;
//			}
//		};

            GlobalLocalStoreOption.prototype.isStorable = function () {
                return this.status;
            };

            GlobalLocalStoreOption.prototype.clear = function () {
                return window.localStorage.removeItem(this.storableLocalKey);
            };

            GlobalLocalStoreOption.prototype.set = function (status) {

                if (status !== undefined && status !== null && typeof status === "boolean") {

                    this.status = status;

                    try {
                        var isStorableLocal = "" + status + "";

                        window.localStorage.setItem(this.storableLocalKey, isStorableLocal);

                    } catch (err) {
                        //TODO VPOL: sorry could not call GlobalController.handleGlobalError, or BackboneErrorReportingView.reportError, or Backbone.EWAView.reportError or
                        //throw "error setting local storage";
                    }
                }
            };
            return GlobalLocalStoreOption;
        }
);