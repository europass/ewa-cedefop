define(
        ['jquery',
            'europass/structures/KeyValueMap',
            'i18n!localization/nls/IctInformationProcessingLevel',
            'europass/maps/IctInformationProcessingLevelMap'],
        function ($, KeyValueMap, IctInformationProcessingLevel, Self) {
            if (Self === undefined || Self === null) {
                var Self = new KeyValueMap();

                $.each(IctInformationProcessingLevel, function (i) {
                    Self.put(IctInformationProcessingLevel[i], i);
                });
            }
            return Self;
        }
);