define(
        ['europass/http/WindowConfig', 'europass/http/WindowConfigInstance']
        , function (WindowConfig, Self) {
            if (Self === undefined || Self === null) {
                var Self = new WindowConfig(window.config);
            }
            return Self;
        }
);