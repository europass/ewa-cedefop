define(
        ['jquery',
            'europass/structures/KeyValueMap',
            'i18n!localization/nls/EducationalLevel',
            'europass/maps/EducationalLevelMap'],
        function ($, KeyValueMap, EducationalLevel, Self) {
            if (Self === undefined || Self === null) {
                var Self = new KeyValueMap();

                $.each(EducationalLevel, function (i) {
                    Self.put(EducationalLevel[i], i);
                });
            }
            return Self;
        }
);