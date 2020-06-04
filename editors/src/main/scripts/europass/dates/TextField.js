define(
        ['jquery'],
        function ($) {

            var TextField = function (text) {
                this.text = text;
            };

            /**
             * Formats a Text Field.
             * 
             * @param the array that holds the output, not string due to javascript variable scopes
             * @param date object from which to get the date, if any
             * @param hasPrevious, boolean that indicates if there is a previous date object (relevant for Text Field)
             * 
             * @returns boolean that indicates if a year has been added or not
             */
            TextField.prototype.format = function (strBuilder, date, hasPrevious) {

                if (hasPrevious === false) {
                    return false;
                }

                strBuilder.push(this.text);
                return true;
            };

            return TextField;
        }
);