define(
        ['jquery'
                    , 'europass/structures/KeyValueMap'
                    , 'i18n!localization/nls/EnclosedLabel'
                    , 'europass/maps/EnclosedLabelMap'],
        function ($, KeyValueMap, Taxonomy, Self) {
            if (Self === undefined || Self === null) {
                var Self = new KeyValueMap();
                $.each(Taxonomy, function (i) {
                    Self.put(Taxonomy[i], i);
                });
            }
            return Self;
        }
);