define(
        ['jquery',
            'europass/structures/KeyValueMap',
            'i18n!localization/nls/EducationalField',
            'europass/maps/EducationalFieldMap'],
        function ($, KeyValueMap, EducationalField, Self) {
            if (Self === undefined || Self === null) {
                var Self = new KeyValueMap();

                $.each(EducationalField, function (i) {
                    Self.put(EducationalField[i], i);
                });
                Self.sortByOrder('asc');
            }
            return Self;
        }
);