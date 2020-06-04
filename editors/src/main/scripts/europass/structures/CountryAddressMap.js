define(
        ['jquery'],
        function ($) {
            var CountryAddressMap = function () {
                this.keys = new Array();
                this.formats = new Array();
                this.countryPostalCodes = new Array();
            };
            //Public
            CountryAddressMap.prototype.put = function (k, format, countryPostalCode) {
                this.keys.push(k);
                this.formats.push(format);
                this.countryPostalCodes.push(((countryPostalCode === undefined || countryPostalCode === null) ? "" : countryPostalCode));
            };
            CountryAddressMap.prototype.get = function (k) {
                //var idx = this.keys.indexOf(k);
                var idx = $.inArray(k, this.keys);//fix for i.e 8
                if (idx !== -1) {
                    var obj = {};
                    obj.format = this.formats[idx];
                    obj.countryPostalCode = this.countryPostalCodes[idx];
                    return obj;
                }
            };
            return CountryAddressMap;
        }
);