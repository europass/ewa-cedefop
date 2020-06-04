define(
        ['jquery',
            'europass/structures/KeyValueMap',
            'i18n!localization/nls/IctLevel',
            'europass/maps/IctLevelMap'],
        function ($, KeyValueMap, IctLevel, Self) {
            if (Self === undefined || Self === null) {
                var Self = new KeyValueMap();

                $.each(IctLevel, function (i) {
                    Self.put(IctLevel[i], i);
                });
            }
            return Self;
        }
);