define(
        ['jquery'
                    , 'europass/structures/KeyValueMap'
                    , 'i18n!localization/nls/Nationality'
                    , 'europass/maps/NationalitiesMap'],
        function ($, KeyValueMap, NationalityLabels, Self) {
            if (Self === undefined || Self === null) {
                var Self = new KeyValueMap();
                $.each(NationalityLabels, function (i) {
                    Self.put(NationalityLabels[i], i);
                });
                Self.sortByOrder('asc');
            }
            return Self;
        }
);