define(
        ['jquery', 'underscore', 'i18n!localization/nls/DateFormat'],
        function ($, _, DateFormat) {

            var DayField = function (length) {
                this.length = (length === undefined || length === null || parseInt(length) === NaN) ? 0 : length;
                this.localeSettings = {};
                this.getLocaleSettings();
            };

            /**
             * Formats a Day Field.
             * 
             * If the day is undefined, null or empty, then return false;
             * 
             * If the passed day's length is less than this object's length, then add 0 to the start (see String.padStart)
             * 
             * Finally append to strBuilder and return true
             * 
             * @param the string that holds the output
             * @param date object from which to get the date, if any
             * @param hasPrevious, boolean that indicates if there is a previous date object (relevant for Text Field)
             * 
             * @returns boolean that indicates if a day has been added or not
             */
            DayField.prototype.format = function (strBuilder, date, hasPrevious) {
                var day = date.Day;
                var hasDay = (day !== undefined && day !== null && day >= 1 && day <= 31);

                if (!hasDay) {
                    return false;
                }

                var dayAsString = day.toString();
                var dayLength = dayAsString.length;

                if (this.length > dayLength) {
                    switch (this.length) {
                        case 2 :
                        {
                            //add 0
                            for (var i = dayLength; i < this.length; i++) {
                                dayAsString = '0' + dayAsString;
                            }
                            break;
                        }
                        case 3:
                        {
                            var ds = this.localeSettings.daySuffix;
                            if (ds.length > 0) {
                                var suffix = ds[ day - 1 ];
                                dayAsString = dayAsString + (_.isString(suffix) ? suffix : "");
                            }
                            break;
                        }
                    }

                }

                strBuilder.push(dayAsString);

                return true;
            };

            DayField.prototype.getLocaleSettings = function () {
                this.localeSettings = {
                    "daySuffix": (_.isArray(DateFormat.daySuffix) ? DateFormat.daySuffix : [])
                };
            };
            return DayField;
        }
);