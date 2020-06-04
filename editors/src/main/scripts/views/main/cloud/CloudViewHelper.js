/*
 *
 *  Authenticator for One Drive
 *
 * */

define(
        [
            'module',
            'jquery',
            'underscore'
        ],
        function (module, $, _) {

            var CloudViewHelper = function (config) {
                this.overlay = config.overlay;
                this.area = config.area;
                this.sectionEl = config.sectionEl;
                if (!_.isUndefined(config.errorMsgElem)) {
                    this.errorMsgElem = config.errorMsgElem;
                }
            };

            /**
             * Show the modal drawer with my files of connected cloud.
             * Include a drawer effect, sliding from left to right.
             * Also prepares and shows the respective cloud provider's files list
             */
            CloudViewHelper.prototype.showCloudDrawer = function () {
                // TODO Can refactor - maybe can use existing drawers show functionality check also SignInView
                var _that = this;
                var children = (this.area !== undefined ? this.area.children() : undefined);
                this.overlay.addClass("visible", function () {
                    $(this).animate({"background-color": "rgba(0,0,0, 0.7)"}, 400);
                    if (children !== undefined) {
                        children.addBack().show('slide', {direction: "left", easing: "easeInSine"}, 400, function () {
                            //Making the vertical overflow auto for overflow-y scrolling if needed, while the modal is open
                            _that.area.find(".ui-settings-area").css("overflow-y", "auto");
                            _that.sectionEl.trigger("europass:drawer:opened");
                        });
                    }
                });
            };

            /**
             * Hide the my files list modal, include a drawer effect, sliding from right to left
             */
            CloudViewHelper.prototype.hideCloudDrawer = function () {
                var _that = this;
                this.triggerResponseSuccess(true);
                // TODO Can refactor - maybe can use existing drawers hide functionality
                var children = (this.area !== undefined ? this.area.children() : undefined);
                if (children !== undefined) {
                    children.hide('slide', {direction: "left", easing: "easeInSine"}, 400);
                }
                this.area.hide('slide', {direction: "left", easing: "easeInSine"}, 400);
                this.area.find(".ui-settings-area").css("overflow-y", "hidden");
                _that.overlay.animate({"background-color": "rgba(0,0,0,0)"}, 400, function () {
                    $(_that.overlay).removeClass("visible");
                });
            };


            /**
             * Function to show the appropriate error messages to the right location
             * errors during sign in process or while trying to fetch files are shown on
             * main page - errors during rename, clone etc. should be displayed on my files
             * drawer.
             * @param {type} errCode
             * @param {type} errMessage
             * @param {type} messageLocation
             * @returns void
             */
            CloudViewHelper.prototype.triggerResponseError = function (errCode, errMessage, messageLocation) {

                var errorKey = "error.code.status500";

                if (errCode === 401 || errCode === 403 || errCode === 404 || errCode === 500) {
                    errorKey = "error.code.status" + errCode;
                } else if (errCode >= 400 && errCode < 500 && errCode !== 401) {
                    errorKey = "error.code.status400";
                }

                // e.g. var msg = Notification["eures.post.error"] + (errMessage !== undefined ? " (" + errMessage + ")" : "");
                //this.messageContainer.trigger("europass:message:show", ["error", msg + Notification[errorKey]+ " " + errCode]);
                var genericMessage = "Error during cloud action";
                var msg = genericMessage + " (" + errMessage + ")";

                if (messageLocation === "main") {
                    if (this.area.is(':visible')) {
                        this.hideCloudDrawer();
                    }
                    $("body").trigger("europass:message:show", ["error", msg]);
                } else if (messageLocation === "drawer") {

                    this.errorMsgElem.find("p").empty();
                    this.errorMsgElem.find("p").append(msg);
                    this.triggerResponseSuccess(false);
                }

                $("body").trigger("europass:waiting:indicator:cloud:hide");
            };


            /**
             * Given the success argument, it shows/hides the errorMsg container
             * @param {type} success
             * @returns {undefined}
             */
            CloudViewHelper.prototype.triggerResponseSuccess = function (success) {
                if (!_.isUndefined(this.errorMsgElem)) {
                    if (success === true) {
                        this.errorMsgElem.hide();
                    } else {
                        this.errorMsgElem.show();
                    }
                }
            };

            return CloudViewHelper;
        }
);