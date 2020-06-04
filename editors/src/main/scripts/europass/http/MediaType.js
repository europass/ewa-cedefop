define(
        ['europass/http/MediaType']
        , function (Self) {
            if (Self === undefined || Self === null) {
                var Self = {
                    json: 'application/json',
                    xml: 'application/xml',
                    form: 'application/x-www-form-urlencoded',
                    multipart: 'multipart/form-data'
                };
            }
            return Self;
        }
);