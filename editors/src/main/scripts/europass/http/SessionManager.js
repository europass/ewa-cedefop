define(
        [
            'jquery'
//	,'i18n!localization/nls/Notification'
        ]
        , function ($/*, Notification*/) {

            var SessionManager = function () {
                this.currentSession = null;
            };
            SessionManager.prototype.get = function () {
                return this.currentSession;
            };
            /**
             * The currentSession is set to null, which means that there is no session.
             * The currentSession is already set to a value other than the input session.
             * This means that the previous session is expired. In that case,
             * it will display a message about session expiration, when the modal is not open.
             * @param session: the new session
             */
            SessionManager.prototype.set = function (session) {
                if (session !== undefined && session !== null && session !== "") {

                    if (this.currentSession !== null && this.currentSession === session) {
                        return; //same session
                    }
                    //set the currentSession
                    this.currentSession = session;
                }
            };

            SessionManager.prototype.urlappend = function () {
                return (this.currentSession === null) ? "" : ";jsessionid=" + this.currentSession;
            };

            SessionManager.prototype.clear = function () {
                this.currentSession = null;
            };

            return SessionManager;
        }
);