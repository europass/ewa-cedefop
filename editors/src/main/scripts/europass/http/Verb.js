define(
        ['europass/http/Verb']
        , function (Self) {
            if (Self === undefined || Self === null) {
                var Self = {
                    _post: 'POST',
                    _get: 'GET',
                    _put: 'PUT',
                    _delete: 'DELETE',
                    _options: 'OPTIONS',
                    _header: 'HEADER'
                };
            }
            return Self;
        }
);