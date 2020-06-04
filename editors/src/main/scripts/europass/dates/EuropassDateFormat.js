define(
        [
            'jquery',
            'europass/dates/DayField',
            'europass/dates/MonthField',
            'europass/dates/YearField',
            'europass/dates/TextField'
        ],
        function ($, DayField, MonthField, YearField, TextField) {

            var EuropassDateFormat = function (pattern) {
                this.pattern = pattern;
                this.fields = [];
                this.compile(); // populates fields[]
            };

            EuropassDateFormat.prototype.format = function (date) {

                var textArray = [];

                //assumes that the pattern cannot start with a non-symbol
                var hasPrevious = true;
                for (var i = 0; i < this.fields.length; i++) {
                    var field = this.fields[ i ];

                    hasPrevious = field.format(textArray, date, hasPrevious);
                }

                var text = textArray.join("");
                return text;
            };

            EuropassDateFormat.prototype.DAY_SYMBOL = "d";
            EuropassDateFormat.prototype.MONTH_SYMBOL = "M";
            EuropassDateFormat.prototype.YEAR_SYMBOL = "y";
            EuropassDateFormat.prototype.SQUOTE = "'";
            /**
             * @returns Array of Fields
             */
            EuropassDateFormat.prototype.compile = function () {
                this.patternL = this.pattern.length;
                this.curPos = 0;

                while (this.curPos < this.patternL) {

                    var c = this.pattern.charAt(this.curPos);

                    switch (c) {
                        case this.DAY_SYMBOL:
                        {
                            var dayField = new DayField(this.consumeField(this.DAY_SYMBOL));
                            this.fields.push(dayField);
                            break;
                        }
                        case this.MONTH_SYMBOL:
                        {
                            var monthField = new MonthField(this.consumeField(this.MONTH_SYMBOL));
                            this.fields.push(monthField);
                            break;
                        }
                        case this.YEAR_SYMBOL:
                        {
                            var yearField = new YearField(this.consumeField(this.YEAR_SYMBOL));
                            this.fields.push(yearField);
                            break;
                        }
                        case this.SQUOTE:
                        {
                            var textField = new TextField(this.consumeQuotedText());
                            this.fields.push(textField);
                            break;
                        }
                        default:
                        {
                            var textField = new TextField(this.consumeText(c));
                            this.fields.push(textField);
                            break;
                        }
                    }

                }
                return this.fields;
            };

            EuropassDateFormat.prototype.consumeField = function (symbol) {
                var length = 0;

                while (this.curPos < this.patternL) {

                    var c = this.pattern.charAt(this.curPos);

                    if (c.search(symbol) === -1) {
                        return length;
                    } else {
                        this.curPos = this.curPos + 1;
                        length = length + 1;
                    }
                }
                return length;
            };

            EuropassDateFormat.prototype.consumeQuotedText = function () {
                this.curPos = this.curPos + 1; //skip the quote

                if (this.pattern.charAt(this.curPos) == this.SQUOTE) {
                    return "'";
                }

                var text = "";

                while (this.curPos < this.patternL) {
                    var c = this.pattern.charAt(this.curPos);

                    switch (c) {
                        case this.SQUOTE:
                        {
                            //Check next char
                            this.curPos = this.curPos + 1;
                            if (this.pattern.charAt(this.curPos) == this.SQUOTE) {
                                text = text + c;
                                this.curPos = this.curPos + 1;
                            } else {
                                return text;
                            }
                            break;
                        }
                        default:
                        {
                            text = text + c;
                            this.curPos = this.curPos + 1;
                        }
                    }
                }
                return text;
            };

            EuropassDateFormat.prototype.consumeText = function (c) {
                this.curPos = this.curPos + 1;
                return c;
            };

            return EuropassDateFormat;
        }
);