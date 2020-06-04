define(
        ['jquery',
            'europass/structures/KeyValueMap',
            'i18n!localization/nls/TelephoneType',
            'europass/maps/TelephoneTypeMap'],
        function ($, KeyValueMap, TelephoneType, Self) {
            if (Self === undefined || Self === null) {
                var Self = new KeyValueMap();
                $.each(TelephoneType, function (i) {
                    Self.put(TelephoneType[i], i);
                });
            }
            return Self;
        }
);