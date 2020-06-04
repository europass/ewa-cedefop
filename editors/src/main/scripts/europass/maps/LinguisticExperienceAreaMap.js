define(
        ['jquery',
            'europass/structures/KeyValueMap',
            'i18n!localization/nls/LinguisticExperienceType',
            'europass/maps/LinguisticExperienceAreaMap'],
        function ($, KeyValueMap, LinguisticExperienceArea, Self) {
            if (Self === undefined || Self === null) {
                var Self = new KeyValueMap();

                $.each(LinguisticExperienceArea, function (i) {
                    Self.put(LinguisticExperienceArea[i], i);
                });
            }
            return Self;
        }
);