define(
        ['jquery'],
        function ($) {

            var YearField = function (length) {
                this.length = (length === undefined || length === null || parseInt(length) === NaN) ? 0 : length;
            };

            /**
             * Formats a Year Field.
             * 
             * If the year is undefined, null or empty, then return false;
             * 
             * If the passed year's length is less than this object's length, then add 0 to the start (see String.padStart)
             * 
             * Finally append to strBuilder and return true
             * 
             * @param the string that holds the output
             * @param date object from which to get the dat, if any
             * @param hasPrevious, boolean that indicates if there is a previous date object (relevant for Text Field)
             * 
             * @returns boolean that indicates if a year has been added or not
             */
            YearField.prototype.format = function (strBuilder, date, hasPrevious) {
                var year = date.Year;
                var hasYear = (year !== undefined && year !== null && year >= 0);

                if (!hasYear) {
                    return false;
                }

                var yearAsString = year.toString();
                var yearLength = yearAsString.length;

                if (this.length < yearLength) {
                    //use the last two digits
                    var stringLength = yearAsString.length;
                    yearAsString = yearAsString.substring(this.length, stringLength);
                }

                strBuilder.push(yearAsString);

                return true;
            };
            return YearField;
        }
);