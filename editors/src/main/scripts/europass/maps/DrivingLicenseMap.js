define(
        ['jquery',
            'europass/structures/KeyValueMap',
            'i18n!localization/nls/DrivingLicense',
            'europass/maps/DrivingLicenseMap'],
        function ($, KeyValueMap, DrivingLicense, Self) {
            if (Self === undefined || Self === null) {
                var Self = new KeyValueMap();

                $.each(DrivingLicense, function (i) {
                    Self.put(DrivingLicense[i], i);
                });
            }
            return Self;
        }
);