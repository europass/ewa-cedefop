define(
        ['jquery', 'europass/structures/KeyObjMap', 'i18n!localization/nls/ContentLocale', 'europass/maps/LocaleMap'],
        function ($, KeyObjMap, Language, Self) {
            if (Self === undefined || Self === null) {
                var Self = new KeyObjMap();

                $.each(Language, function (i) {
                    Self.put(i, {
                        Code: i,
                        Label: Language[i]
                    });
                });
            }
            return Self;
        }
);