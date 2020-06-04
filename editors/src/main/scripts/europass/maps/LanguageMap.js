define(
        ['jquery',
            'europass/structures/KeyValueMap',
            'i18n!localization/nls/Language',
            'europass/maps/LanguageMap'],
        function ($, KeyValueMap, Language, Self) {
            if (Self === undefined || Self === null) {
                var Self = new KeyValueMap();

                $.each(Language, function (i) {
                    Self.put(Language[i], i);
                });

                Self.sortByOrder('asc');
            }
            return Self;
        }
);