define(
        ['jquery'
                    , 'europass/structures/KeyValueMap'
                    , 'i18n!localization/nls/Country'
                    , 'europass/maps/CountryMap'],
        function ($, KeyValueMap, CountryLabels, Self) {
            if (Self === undefined || Self === null) {
                var Self = new KeyValueMap();

                $.each(CountryLabels, function (i) {
                    Self.put(CountryLabels[i], i);
                });
            }
            Self.sortByOrder('asc');
            return Self;
        }
);