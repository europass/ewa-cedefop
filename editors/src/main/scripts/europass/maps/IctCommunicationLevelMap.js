define(
        ['jquery',
            'europass/structures/KeyValueMap',
            'i18n!localization/nls/IctCommunicationLevel',
            'europass/maps/IctCommunicationLevelMap'],
        function ($, KeyValueMap, IctCommunicationLevel, Self) {
            if (Self === undefined || Self === null) {
                var Self = new KeyValueMap();

                $.each(IctCommunicationLevel, function (i) {
                    Self.put(IctCommunicationLevel[i], i);
                });
            }
            return Self;
        }
);