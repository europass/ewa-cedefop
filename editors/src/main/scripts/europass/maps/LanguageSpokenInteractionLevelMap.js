define(
        ['jquery',
            'europass/structures/KeyValueMap',
            'i18n!localization/nls/LanguageSpokenInteractionLevel',
            'europass/maps/LanguageSpokenInteractionLevelMap'],
        function ($, KeyValueMap, LanguageSpokenInteractionLevel, Self) {
            if (Self === undefined || Self === null) {
                var Self = new KeyValueMap();

                $.each(LanguageSpokenInteractionLevel, function (i) {
                    Self.put(LanguageSpokenInteractionLevel[i], i);
                });
            }
            return Self;
        }
);