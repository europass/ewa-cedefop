define(
        [
            'jquery',
            'europass/structures/KeyValueMap',
            'i18n!localization/nls/CertificateLevel',
            'europass/maps/CertificateLevelMap'
        ],
        function ($, KeyValueMap, CertificateLevel, Self) {
            if (Self === undefined || Self === null) {
                var Self = new KeyValueMap();

                $.each(CertificateLevel, function (i) {
                    Self.put(CertificateLevel[i], i);
                });
            }
            return Self;
        }
);