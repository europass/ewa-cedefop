define(
        ['jquery',
            'europass/structures/KeyValueMap',
            'i18n!localization/nls/LanguageShortLevel',
            'europass/maps/LanguageShortLevelMap'],
        function ($, KeyValueMap, LanguageShortLevel, Self) {
            if (Self === undefined || Self === null) {
                var Self = new KeyValueMap();

                $.each(LanguageShortLevel, function (i) {
                    Self.put(LanguageShortLevel[i], i);
                });
            }
            return Self;
        }
);