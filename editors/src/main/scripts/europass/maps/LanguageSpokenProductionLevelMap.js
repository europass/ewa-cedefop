define(
        ['jquery',
            'europass/structures/KeyValueMap',
            'i18n!localization/nls/LanguageSpokenProductionLevel',
            'europass/maps/LanguageSpokenProductionLevelMap'],
        function ($, KeyValueMap, LanguageSpokenProductionLevel, Self) {
            if (Self === undefined || Self === null) {
                var Self = new KeyValueMap();

                $.each(LanguageSpokenProductionLevel, function (i) {
                    Self.put(LanguageSpokenProductionLevel[i], i);
                });
            }
            return Self;
        }
);