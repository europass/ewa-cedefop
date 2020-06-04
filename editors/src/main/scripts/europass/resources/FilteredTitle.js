define(
        ['jquery'
                    , 'i18n!localization/nls/Title'
                    , 'europass/resources/FilteredTitle'],
        function ($, Title, Self) {
            if (Self === undefined || Self === null) {
                var Self = {};
                $.each(Title, function (i) {
                    var label = Title[i];
                    if (label !== "")
                        Self[i] = Title[i];
                });
            }
            return Self;
        }
);