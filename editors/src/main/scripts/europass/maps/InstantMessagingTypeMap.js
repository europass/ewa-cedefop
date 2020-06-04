define(
        ['jquery',
            'europass/structures/KeyValueMap',
            'i18n!localization/nls/InstantMessagingType',
            'europass/maps/InstantMessagingTypeMap'],
        function ($, KeyValueMap, InstantMessagingType, Self) {
            if (Self === undefined || Self === null) {
                var Self = new KeyValueMap();

                $.each(InstantMessagingType, function (i) {
                    Self.put(InstantMessagingType[i], i);
                });
            }
            return Self;
        }
);
