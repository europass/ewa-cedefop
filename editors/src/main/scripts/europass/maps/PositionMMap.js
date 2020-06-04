define(
        ['jquery',
            'europass/structures/KeyValueMap',
            'i18n!localization/nls/OccupationalField',
            'europass/maps/PositionMMap'],
        function ($, KeyValueMap, OccupationalField, Self) {
            if (Self === undefined || Self === null) {
                var Self = new KeyValueMap();

                $.each(OccupationalField, function (i) {
                    var obj = OccupationalField[i];
                    var value = (obj === null || "" === obj) ? "" : obj.M;
                    Self.put(value, i);
                });
                Self.sortByOrder('asc');
            }
            return Self;
        }
);