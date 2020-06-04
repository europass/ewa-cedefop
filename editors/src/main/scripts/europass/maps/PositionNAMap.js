define(
        [
            'jquery'
                    , 'europass/structures/KeyValueMap'
                    , 'i18n!localization/nls/OccupationalField'
                    , 'europass/maps/PositionNAMap'],
        function ($, KeyValueMap, OccupationalField, Self) {
            if (Self === undefined || Self === null) {
                var Self = new KeyValueMap();

                $.each(OccupationalField, function (i) {
                    var obj = OccupationalField[i];
                    var valueM = (obj === null || "" === obj) ? "" : obj.M;
                    var valueF = (obj === null || "" === obj) ? "" : obj.F;
                    var value = valueM;
                    if ($.trim(valueF.toLowerCase()) !== $.trim(valueM.toLowerCase())) {
                        value = valueM.concat("/").concat(valueF);
                    }
                    Self.put(value, i);
                });
                Self.sortByOrder('asc');
            }
            return Self;
        }
);
