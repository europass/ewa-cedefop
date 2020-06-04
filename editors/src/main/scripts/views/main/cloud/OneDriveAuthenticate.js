/*
 *
 *  Authenticator for One Drive
 *
 * */

define(
        [
            'module',
            'jquery',
            'Utils',
            'europass/http/WindowConfigInstance'
        ],
        function (module, $, Utils, WindowConfig) {

            var OneDriveAuthenticate = function (config) {};
            var tokenExpirationTime = (60 * 60 * 1000) + 1;
            var onedriveCallbackUrl = WindowConfig.onedriveCallbackUrl;
            var clientID = WindowConfig.onedriveAppkey;
            var silentAuthMode = "silentAuth";
            var firstTimeConsentAuthMode = "consentAuth";
            var authenticationMode = firstTimeConsentAuthMode;
            var consentRequiredMessage = 'login_required';

            // TODO Add in properties file.
            var onedriveCallbackUrlForGenericAuth = "/editors/onedrive_oauth_receiver.html";

            OneDriveAuthenticate.prototype.genericAuthenticate = function () {
                var authURL = "https://login.live.com/oauth20_authorize.srf";
                var redirectURI = window.location.protocol + "//" + window.location.hostname + onedriveCallbackUrlForGenericAuth;
                var url = authURL + "?client_id=" + clientID + "&scope=onedrive.readwrite&response_type=token&redirect_uri=" + redirectURI;

                var popup = window.open(url, "oauth", this.getPopupWindowFeatures().join(","));
                if (popup !== null && popup !== undefined && popup) {
                    popup.focus();
                    return popup;
                } else {
                    $("body").trigger("europass:waiting:indicator:cloud:hide", true);
                    return popup;
                }
            };

            OneDriveAuthenticate.prototype.doConnect = function () {
                var _that = this;
                setTimeout(function () {
                    _that.connectAction("silentAuth");
                    setInterval(function () {
                        if (Utils.readCookieByName(WindowConfig.cloudCookieId) === "onedrive") {
                            // There is a silent re-authentication every 1 hour to get a fresh token.
                            authenticationMode = silentAuthMode;
                            _that.connectAction(authenticationMode);
                        }
                    }, tokenExpirationTime);
                }, 0);
            };

            OneDriveAuthenticate.prototype.connectAction = function (authType) {
                var _that = this;
                $("body").trigger("europass:waiting:indicator:cloud:show", true);
                var authURL = "https://login.live.com/oauth20_authorize.srf";
                var redirectURI = window.location.protocol + "//" + window.location.hostname + onedriveCallbackUrl;
                var url = authURL + "?client_id=" + clientID + "&scope=onedrive.readwrite&response_type=token&redirect_uri=" + redirectURI;

                try {
                    _that.createAuthenticationCallerByType(url, authType);
                } catch (error)
                {
                    $("body").trigger("europass:waiting:indicator:cloud:hide", true);
                }
            };

            OneDriveAuthenticate.prototype.createAuthenticationCallerByType = function (url, authType) {

                var _that = this;
                switch (authType) {
                    case firstTimeConsentAuthMode:
                        // First time connect .. This is a new popup window..
                        var popup = window.open(url, "oauth", _that.getPopupWindowFeatures().join(","));
                        if (popup === null || popup === undefined) {
                            $("body").trigger("europass:waiting:indicator:cloud:hide", true);
                            return;
                        }
                        if (!popup) {
                        }
                        popup.focus();
                        var timer = setInterval(checkAuthWindowClosed, 500);
                        function checkAuthWindowClosed() {
                            if (popup.closed && Utils.readCookieByName(WindowConfig.cloudAccessToken) !== consentRequiredMessage) {
                                clearInterval(timer);
                                _that.postAuthenticateAction();
                            }
                        }
                        $("body").trigger("europass:waiting:indicator:cloud:hide", true);
                        break;
                    case silentAuthMode:
                        // When token expired, need to have a silent authentication with Iframe
                        var myUrl = url + '&prompt=none';
                        $('#oneDriveIframe').remove();
                        $('<iframe />');
                        $('<iframe />', {
                            name: 'oneDriveIframe',
                            id: 'oneDriveIframe',
                            src: myUrl
                        }).appendTo('body');
                        $('#oneDriveIframe').on('load', function () {
                            _that.postAuthenticateAction();
                        });
                        break;
                    default:
                        break;
                }
            };

            OneDriveAuthenticate.prototype.postAuthenticateAction = function () {
                var accessToken = Utils.readCookieByName(WindowConfig.cloudAccessToken);
                if (accessToken === consentRequiredMessage) {
                    this.connectAction(firstTimeConsentAuthMode);
                } else if (accessToken !== '' && accessToken !== null && accessToken !== false && typeof accessToken !== 'undefined') {
                    $("body").trigger("europass:cloud:onedrive:folder:ready", accessToken);
                    $("body").trigger("europass:cloud:manage:folders", ["onedrive", null, accessToken]);
                    Utils.createOrSetCookieByName(WindowConfig.cloudCookieId, "onedrive");
                }
            };


            OneDriveAuthenticate.prototype.getPopupWindowFeatures = function () {
                var width = 525, height = 525, screenX = window.screenX, screenY = window.screenY,
                        outerWidth = window.outerWidth, outerHeight = window.outerHeight;
                var left = screenX + Math.max(outerWidth - width, 0) / 2,
                        top = screenY + Math.max(outerHeight - height, 0) / 2;
                var features = ["width=" + width, "height=" + height, "top=" + top, "left=" + left,
                    "status=no", "resizable=yes", "toolbar=no", "menubar=no", "scrollbars=yes"];

                return features;
            };

            OneDriveAuthenticate.prototype.getAuthenticationMode = function () {
                return authenticationMode;
            };

            return OneDriveAuthenticate;
        }
);