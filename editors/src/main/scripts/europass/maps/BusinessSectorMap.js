define(
        [
            'jquery'
                    , 'europass/structures/KeyValueMap'
                    , 'i18n!localization/nls/BusinessSector'
                    , 'europass/maps/BusinessSectorMap'
        ],
        function ($, KeyValueMap, BusinessSector, Self) {
            if (Self === undefined || Self === null) {
                var Self = new KeyValueMap();

                $.each(BusinessSector, function (i) {
                    Self.put(BusinessSector[i], i);
                });

                Self.sortByOrder('asc');
            }
            return Self;
        }
);