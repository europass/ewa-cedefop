define(
        ['jquery',
            'europass/structures/KeyValueMap',
            'i18n!localization/nls/HeadlineType',
            'europass/maps/HeadlineTypeMap'],
        function ($, KeyValueMap, HeadlineType, Self) {
            if (Self === undefined || Self === null) {
                var Self = new KeyValueMap();

                $.each(HeadlineType, function (i) {
                    Self.put(HeadlineType[i], i);
                });
            }
            return Self;
        }
);