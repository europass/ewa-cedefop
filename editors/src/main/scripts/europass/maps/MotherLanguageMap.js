define(
        ['jquery',
            'europass/structures/KeyValueMap',
            'i18n!localization/nls/MotherLanguage',
            'europass/maps/MotherLanguageMap'],
        function ($, KeyValueMap, MotherLanguage, Self) {
            if (Self === undefined || Self === null) {
                var Self = new KeyValueMap();

                $.each(MotherLanguage, function (i) {
                    Self.put(MotherLanguage[i], i);
                });
                Self.sortByOrder('asc');
            }
            return Self;
        }
);