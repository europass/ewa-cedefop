define(
        ['jquery'],
        function ($) {

            function ValidationError(elementName, errorCode, errorMsgKey) {
                this.elementName = elementName;
                this.errorCode = errorCode;
                this.errorMsgKey = errorMsgKey;
            }
            ;

            ValidationError.prototype = {
                constructor: ValidationError
            };

            ValidationError.prototype.setElementName = function (name) {
                this.elementName = name;
            };

            ValidationError.prototype.setErrorCode = function (errorCode) {
                this.errorCode = errorCode;
            };

            ValidationError.prototype.setErrorMsgKey = function (errorMsgKey) {
                this.errorMsgKey = errorMsgKey;
            };
            return ValidationError;
        });