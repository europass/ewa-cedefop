define(
        ['jquery'
                    , 'europass/structures/KeyValueMap'
                    , 'europass/resources/FilteredOpeningSalutation'
                    , 'europass/maps/OpeningSalutationMap'],
        function ($, KeyValueMap, SalutationLabels, Self) {
            if (Self === undefined || Self === null) {
                var Self = new KeyValueMap();

                $.each(SalutationLabels, function (i) {
                    Self.put(SalutationLabels[i], i);
                });
            }
            return Self;
        }
);