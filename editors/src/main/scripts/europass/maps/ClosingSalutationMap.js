define(
        ['jquery'
                    , 'europass/structures/KeyValueMap'
                    , 'europass/resources/FilteredClosingSalutation'
                    , 'europass/maps/ClosingSalutationMap'],
        function ($, KeyValueMap, ClosingSalutationLabels, Self) {
            if (Self === undefined || Self === null) {
                var Self = new KeyValueMap();

                $.each(ClosingSalutationLabels, function (i) {
                    var label = ClosingSalutationLabels[i];
                    Self.put(label, i);
                });
            }
            return Self;
        }
);