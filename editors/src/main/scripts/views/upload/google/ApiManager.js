define(
        [
            'jquery',
            'require',
            'underscore',
            'Utils',
            'europass/http/WindowConfigInstance',
            'i18n!localization/nls/Notification'
        ], function ($, require, _, Utils, WindowConfig, Notification) {

    function ApiManager(config) {
        this.config = config;
//		this.loadGapi();
        this.authToken = null;
        this.driveApiLoaded = false;
        this.isDisconnected = true;
        this.timer = null;
    }

    ApiManager.prototype.init = function (immediate) {
        var _that = this;

        function driveAPICallback() {
            _that.driveApiLoaded = true;
        }
        ;

        if (!this.driveApiLoaded) {
            gapi.client.load('drive', 'v2', driveAPICallback);
        }

        function handleClientLoad() {
            gapi.client.setApiKey(_that.config.apiKey);
            window.setTimeout(checkAuth, 100);
        }
        ;

        function checkAuth() {

            if (_that.isDisconnected || typeof _that.isDisconnected === 'undefined') {
                immediate = false;
            } else {
                immediate = true;
            }

            gauthorize(immediate);
        }
        ;

        function updateSigninStatus(isSignedIn) {
            // When signin status changes, this function is called.
            // If the signin status is changed to signedIn, we make an API call.
            if (isSignedIn) {
                var authResult = gapi.auth2.getAuthInstance().currentUser.get().getAuthResponse();
                //console.log('authresult when signin status changes');
                //console.log(authResult);
                _that.setAccessToken(authResult);
            } else {
                $("body").trigger("europass:cloud:manage:disconnect");
            }
        }
        ;

        function gauthorize(immediate) {

            var authConfigOptions = {};
            if (immediate) {
                authConfigOptions = {prompt: 'none'}
            }
            gapi.client.init({
                apiKey: _that.config.apiKey,
                client_id: _that.config.clientId,
                scope: _that.config.scopes,
                options: authConfigOptions
            }).then(function () {
                if (!immediate) {
                    gapi.auth2.getAuthInstance().isSignedIn.listen(updateSigninStatus);
                    var signInObj = gapi.auth2.getAuthInstance().signIn();
                    signInObj.then(function (data) { })
                            .catch(function (err) {
                                _that.isDisconnected = true;
                                $("body").trigger("europass:waiting:indicator:hide");
                            });
                }
                updateSigninStatus(gapi.auth2.getAuthInstance().isSignedIn.get());
            }, function () {
                // On error init !!
                gapi.auth2.getAuthInstance().isSignedIn.listen(updateSigninStatus);
                updateSigninStatus(gapi.auth2.getAuthInstance().isSignedIn.get());
            });
        }

        handleClientLoad();
    };

    /**
     * isDisconnected = true means that a connection attempts happens after disconnection.
     * This means we don't want to create a token in loadGapi in order to have immediate=false
     * in checkAuth when authorization request is made.
     * @param {type} isDisconnected
     * @returns {undefined}
     */
    ApiManager.prototype.loadGapi = function (isDisconnected) {
        var _that = this;
        this.isDisconnected = isDisconnected;
        var setToken = function () {
            if (typeof gapi.auth !== 'undefined') {
                var existingTokenInfo = gapi.auth.getToken();
                if (existingTokenInfo !== null && typeof existingTokenInfo !== 'undefined') {
                    var token = existingTokenInfo.access_token;
                    if (!_.isEmpty(token)) {
                        if (!isDisconnected) {
                            _that.authToken = token;
                        }
                    }
                }
            }
        };

        // Don't load gapi if it's already present
        if (typeof gapi !== 'undefined' && typeof gapi.client !== 'undefined') {
            setToken();
            return this.init(this.authToken === null);
        }
        if (Utils.isHostReachable()) {
            require(['https://apis.google.com/js/client.js'], //?onload=define' ],
                    function () {
                        // Poll until gapi is ready
                        function checkGAPI() {
                            if (gapi && gapi.client) {
                                setToken();
                                _that.init(this.authToken === null);
                            } else {
                                setTimeout(checkGAPI, 100);
                            }
                        }

                        checkGAPI();
                    });
        } else { //network error
            if (isDisconnected) {
                $("body").trigger("europass:cloud-sign-in:drawer:hide");
            } else {
                $("body").trigger("europass:cloud:manage:disconnect");
            }
            $("body").trigger("europass:waiting:indicator:hide");
            Utils.triggerErrorWhenCloudLoginAction("error", "Error loading Google API", "main");
        }
    };

    ApiManager.prototype.setAccessToken = function (authResult) {
        var _that = this;
        if (_that.config.source === "cloud") {
            var isRefreshToken = false;
            if (authResult && !authResult.error) {
                // get a new token right exactly after 1h(3600s)
                _that.timer = setTimeout(function () {
                    var relAuthRespObj = gapi.auth2.getAuthInstance().currentUser.get().reloadAuthResponse();
                    relAuthRespObj.then(function (authResult) {
                        _that.setAccessToken(authResult);
                        //console.log('authresult when set access token from apimanager');
                        //console.log(authResult);
                    });
                }, ((authResult.expires_in - 200) * 1000));

                Utils.createOrSetCookieByName(WindowConfig.cloudCookieId, WindowConfig.gdrive);
                _that.isDisconnected = false;
                if (_that.authToken !== null) { //case of refresh token
                    isRefreshToken = true;
                }
                _that.authToken = authResult.access_token;
                _that.config.callback.apply(_that.config.scope, [authResult, isRefreshToken]);
            } else { //erroneous authorization - disconnect
                $("body").trigger("europass:cloud:manage:disconnect");
                if (!immediate) {
                    $("body").trigger("europass:cloud-sign-in:drawer:hide");
                }
            }
        } else {
            _that.authToken = authResult.access_token;
            _that.config.callback.apply(_that.config.scope, [authResult]);
        }
    };

    ApiManager.prototype.clearTokenTimeout = function () {
        this.authToken = null;
        clearTimeout(this.timer);
        this.isDisconnected = true;
    };

    return ApiManager;
});
