define(
        ['jquery'
                    , 'europass/structures/KeyValueMap'
                    , 'europass/resources/FilteredTitle'
                    , 'europass/maps/TitleMap'],
        function ($, KeyValueMap, TitleLabels, Self) {
            if (Self === undefined || Self === null) {
                var Self = new KeyValueMap();

                $.each(TitleLabels, function (i) {
                    Self.put(TitleLabels[i], i);
                });
            }
            return Self;
        }
);