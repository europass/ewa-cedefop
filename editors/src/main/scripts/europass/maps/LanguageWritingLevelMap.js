define(
        ['jquery',
            'europass/structures/KeyValueMap',
            'i18n!localization/nls/LanguageWritingLevel',
            'europass/maps/LanguageWritingLevelMap'],
        function ($, KeyValueMap, LanguageWritingLevel, Self) {
            if (Self === undefined || Self === null) {
                var Self = new KeyValueMap();

                $.each(LanguageWritingLevel, function (i) {
                    Self.put(LanguageWritingLevel[i], i);
                });
            }
            return Self;
        }
);