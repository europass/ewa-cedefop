define(
        ['jquery',
            'europass/structures/KeyValueMap',
            'i18n!localization/nls/IctContentCreationLevel',
            'europass/maps/IctContentCreationLevelMap'],
        function ($, KeyValueMap, IctContentCreationLevel, Self) {
            if (Self === undefined || Self === null) {
                var Self = new KeyValueMap();

                $.each(IctContentCreationLevel, function (i) {
                    Self.put(IctContentCreationLevel[i], i);
                });
            }
            return Self;
        }
);