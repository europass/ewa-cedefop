define(
        ['europass/http/SessionManager', 'europass/http/SessionManagerInstance']
        , function (SessionManager, Self) {
            if (Self === undefined || Self === null) {
                var Self = new SessionManager();
            }
            return Self;
        }
);