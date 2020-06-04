define(
        ['jquery',
            'europass/structures/KeyValueMap',
            'i18n!localization/nls/LanguageListeningLevel',
            'europass/maps/LanguageListeningLevelMap'
        ],
        function ($, KeyValueMap, LanguageListeningLevel, Self) {
            if (Self === undefined || Self === null) {
                var Self = new KeyValueMap();

                $.each(LanguageListeningLevel, function (i) {
                    Self.put(LanguageListeningLevel[i], i);
                });
            }
            return Self;
        }
);