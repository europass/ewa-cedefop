define(
        ['jquery',
            'europass/structures/KeyValueMap',
            'i18n!localization/nls/GenderOption',
            'europass/maps/GenderMap'],
        function ($, KeyValueMap, GenderLabels, Self) {
            if (Self === undefined || Self === null) {
                var Self = new KeyValueMap();

                $.each(GenderLabels, function (i) {
                    var label = i.split(".");
                    Self.put(GenderLabels[i], label[label.length - 1]);
                });
            }
            return Self;
        }
);