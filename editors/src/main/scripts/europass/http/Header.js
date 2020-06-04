define(
        ['europass/http/Header']
        , function (Self) {
            if (Self === undefined || Self === null) {
                var Self = {
                    contentType: 'Content-Type',
                    accept: 'Accept',
                    contentLanguage: 'Content-Language',
                    acceptLanguage: 'Accept-Language',
                    contentLength: 'Content-Length',
                    location: 'Location',
                    sessionid: 'EWA-Session-Id'
                };
            }
            return Self;
        }
);