define(
        [
            'jquery'
                    , 'europass/structures/CountryAddressMap'
                    , 'i18n!localization/nls/AddressFormat'
                    , 'i18n!localization/nls/PostalCode'
                    , 'europass/maps/AddressFormatMap'
        ],
        function ($, CountryAddressMap, AddressFormats, PostalCountryCodes, Self) {
            if (Self === undefined || Self === null) {
                var Self = new CountryAddressMap();

                $.each(AddressFormats, function (i) {
                    var key = i;
                    Self.put(key, AddressFormats[i], PostalCountryCodes[key]);
                });
            }
            return Self;
        }
);