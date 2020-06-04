define(
        ['jquery',
            'europass/structures/KeyValueMap',
            'i18n!localization/nls/LanguageReadingLevel',
            'europass/maps/LanguageReadingLevelMap'],
        function ($, KeyValueMap, LanguageReadingLevel, Self) {
            if (Self === undefined || Self === null) {
                var Self = new KeyValueMap();

                $.each(LanguageReadingLevel, function (i) {
                    Self.put(LanguageReadingLevel[i], i);
                });
            }
            return Self;
        }
);