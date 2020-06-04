define(
        ['jquery',
            'europass/structures/KeyValueMap',
            'i18n!localization/nls/IctSafetyLevel',
            'europass/maps/IctSafetyLevelMap'],
        function ($, KeyValueMap, IctSafetyLevel, Self) {
            if (Self === undefined || Self === null) {
                var Self = new KeyValueMap();

                $.each(IctSafetyLevel, function (i) {
                    Self.put(IctSafetyLevel[i], i);
                });
            }
            return Self;
        }
);