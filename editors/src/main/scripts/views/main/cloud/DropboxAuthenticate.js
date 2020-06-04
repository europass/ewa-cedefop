/*
 *
 *  Authenticator for Dropbox
 *
 * */

define(
        [
            'Utils',
            'europass/http/WindowConfigInstance'
        ],
        function (Utils, WindowConfig) {

            var DropboxAuthenticate = function (config) {};

            var appKey = WindowConfig.dropboxAppKey;
            var callback = WindowConfig.dropboxCallbackUrl;

            DropboxAuthenticate.prototype.authenticate = function () {
                var authURL = "https://www.dropbox.com/oauth2/authorize";
                var receiverUrl = window.location.protocol + "//" + window.location.host + callback;
                var url = authURL + "?client_id=" + appKey + "&response_type=token&redirect_uri=" + receiverUrl;
                var popup = window.open(url, "oauth");
                if (popup !== null && popup !== undefined && popup) {
                    popup.focus();
                    return popup;
                } else {
                    $("body").trigger("europass:waiting:indicator:cloud:hide", true);
                    return popup;
                }
            };

            return DropboxAuthenticate;
        }
);