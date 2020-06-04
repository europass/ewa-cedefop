define(
        ['jquery', 'i18n!localization/nls/DateFormat'],
        function ($, DateFormat) {

            var MonthField = function (length) {
                this.length = (length === undefined || length === null || parseInt(length) === NaN) ? 0 : length;
                this.localeSettings = {};
                this.getLocaleSettings();
            };

            /**
             * Formats a Month Field.
             * 
             * If the month is undefined, null or empty, then return false;
             * 
             * If the passed month length is less than this object's length, then add 0 to the start (see String.padStart)
             * 
             * Finally append to strBuilder and return true
             * 
             * @param the string that holds the output
             * @param date object from which to get the month, if any
             * @param hasPrevious, boolean that indicates if there is a previous date object (relevant for Text Field)
             * 
             * @returns boolean that indicates if a month has been added or not
             */
            MonthField.prototype.format = function (strBuilder, date, hasPrevious) {
                var day = date.Day;
                var month = date.Month;
                var hasMonth = (month !== undefined && month !== null && month <= 12);
                if (!hasMonth)
                    return false;

                var monthAsString = month.toString();
                var monthLength = monthAsString.length;

                if (this.length > monthLength) {

                    if (this.length > 2) { // month is string
                        monthAsString = this.replaceWithName(day, monthAsString, month);
                    } else {
                        for (var i = monthLength; i < this.length; i++) {
                            monthAsString = '0' + monthAsString;
                        }
                    }

                }

                strBuilder.push(monthAsString);

                return true;
            };

            MonthField.prototype.replaceWithName = function (day, monthAsString, month) {
                if (this.length == 3) { // short/text
                    monthAsString = this.localeSettings.monthNamesShort[month - 1];
                } else { // long/text
                    if (day == null || day == undefined) {
                        monthAsString = this.localeSettings.monthNamesPrecedingDay[month - 1];
                    } else {
                        monthAsString = this.localeSettings.monthNames[month - 1];
                    }
                }
                return monthAsString;
            };

            MonthField.prototype.getLocaleSettings = function (monthAsString) {
                this.localeSettings = {
                    "monthNames": DateFormat.nameWithDay,
                    "monthNamesShort": DateFormat.nameShort,
                    "monthNamesPrecedingDay": DateFormat.nameNoDay,
                    "dayNames": [],
                    "dayNamesShort": []
                };
            };

            return MonthField;
        }
);