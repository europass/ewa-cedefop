define(
        [
            'jquery',
            'jqueryui',
            'underscore',
            'backbone',
            'Utils',
            'scrollTo',
            'xdate',
            'europass/structures/ValidationError',
            'i18n!localization/nls/ValidationErrorMessages',
            'i18n!localization/nls/DocumentLabel',
            'i18n!localization/nls/GuiLabel',
            'hbs!templates/forms/validationMessages',
            'europass/http/WindowConfigInstance'
        ],
        function ($, jqueryui, _, Backbone, Utils, scrollTo, XDate, ValidationError,
                ValidationErrorMessages, DocumentLabel, GuiLabel,
                validationMessagesTpl,
                WindowConfigInstance) {

            var ValidationFormView = Backbone.View.extend({

                validationResults: undefined,
                serverDate: null,

                events: {
                    "click button.submit:not(.disabled)": "requireValidation",
                    "europass:form:invalid": "displayErrorMessages",
                    "europass:modal:closed": "modalClosed"
                },

                initialize: function (options) {
                    this.options = options;
                    this.removeStyles(this.$el);
                    this.serverToday = WindowConfigInstance.serverDateTime;

                    this.errorMessages = {
                        ok: "validation.error.ok", // 0
                        noInput: "validation.error.noInput", // 1
                        invalidDate: "validation.error.invalidDate", // 100
                        dateAfterToday: "validation.error.dateAfterToday", // 101 
                        impossibleDate: "validation.error.impossibleDate", //102
                        negativePeriod: "validation.error.negativePeriod", // 201
                        nonexistFrom: "validation.error.nonexistFrom", // 202
                        nonexistTo: "validation.error.nonexistTo", // 203
                        invalidEmail: "validation.error.invalidEmail",
                        invalidWebsite: "validation.error.invalidWebsite"
                    };
                },

                /**
                 * Validates the given date (valid dates are also dates like 2012, November 2012 etc)
                 *	@args : day, month, year
                 *	@returns: ValidationError obj
                 */
                dateValidate: function (day, month, year) {
                    var validationResult = new ValidationError();

                    // allow all fields of date to be empty, but return error on incomplete fields
                    // XOR(day,month,year)?
                    if (day !== undefined && month !== undefined && year !== undefined) {
                        if (
                                (day !== '' && month === '' && year !== '') ||
                                (day !== '' && month === '' && year === '') ||
                                (day !== '' && month !== '' && year === '') ||
                                (day === '' && month !== '' && year === '')
                                ) {
                            validationResult.setErrorCode(100);
                            validationResult.setErrorMsgKey(this.errorMessages.invalidDate);
                            return validationResult;
                        } else if (day === '' && month === '' && year === '') {
                            validationResult.setErrorCode(1);
                            validationResult.setErrorMsgKey(this.errorMessages.noInput);
                            return validationResult;
                        } else {
                            validationResult.setErrorCode(0);
                            validationResult.setErrorMsgKey(this.errorMessages.ok);
                        }

                    } else {
                        validationResult.setErrorCode(100);
                        validationResult.setErrorMsgKey(this.errorMessages.invalidDate);
                        return validationResult;
                    }

                    if (month >= 1 && month <= 7) {

                        if (month === 2) {		// February
                            if (day >= 29) {
                                if ((year % 4) === 0 && day === 29) {		// Leap Years have 29/2
                                    validationResult.setErrorCode(0);
                                    validationResult.setErrorMsgKey(this.errorMessages.ok);
                                } else {
                                    validationResult.setErrorCode(102);
                                    validationResult.setErrorMsgKey(this.errorMessages.impossibleDate);
                                    return validationResult;
                                }
                            } else {
                                validationResult.setErrorCode(0);
                                validationResult.setErrorMsgKey(this.errorMessages.ok);
                            }
                        }
                        switch (month % 2) {

                            case 0:
                                if (day === 31) {
                                    validationResult.setErrorCode(102);
                                    validationResult.setErrorMsgKey(this.errorMessages.impossibleDate);
                                    return validationResult;
                                } else {
                                    validationResult.setErrorCode(0);
                                    validationResult.setErrorMsgKey(this.errorMessages.ok);
                                }
                                break;
                            case 1:			// January, March, May, July 
                                validationResult.setErrorCode(0);
                                validationResult.setErrorMsgKey(this.errorMessages.ok);
                                break;
                            default:
                                validationResult.setErrorCode(100);
                                validationResult.setErrorMsgKey(this.errorMessages.invalidDate);
                                return validationResult;
                        }
                    } else if (month >= 8) {
                        switch (month % 2) {

                            case 1:				// August, October, December
                                if (day === 31) {
                                    validationResult.setErrorCode(102);
                                    validationResult.setErrorMsgKey(this.errorMessages.impossibleDate);
                                    return validationResult;
                                }
                                validationResult.setErrorCode(0);
                                validationResult.setErrorMsgKey(this.errorMessages.ok);
                                break;
                            case 0:
                                validationResult.setErrorCode(0);
                                validationResult.setErrorMsgKey(this.errorMessages.ok);
                                break;
                            default:
                                validationResult.setErrorCode(100);
                                validationResult.setErrorMsgKey(this.errorMessages.invalidDate);
                                return validationResult;
                        }
                    }

                    return validationResult;

                },

                /**
                 * Validates the given date if it is later than today
                 *	@args : day, month, year
                 *	@returns: ValidationError obj
                 */
                currentDateValidate: function (day, month, year) {

                    var validationResult = new ValidationError();

                    // validate date against current date
                    if (
                            (day !== '' && month !== '' && year !== '') ||
                            (day === '' && month !== '' && year !== '') ||
                            (day === '' && month === '' && year !== '')
                            ) {

                        var today = new XDate(this.serverToday, true);

                        if (day === '')
                            day = today.getDate();
                        if (month === '')
                            month = today.getMonth() + 1;

                        var validatedXDate = new XDate(year, month - 1, day, true);
                        if (validatedXDate.diffDays(today) < 0) {
                            validationResult.setErrorCode(101);
                            validationResult.setErrorMsgKey(this.errorMessages.dateAfterToday);
                            return validationResult;
                        } else {
                            validationResult.setErrorCode(0);
                            validationResult.setErrorMsgKey(this.errorMessages.ok);
                        }
                    } else {
                        validationResult.setErrorCode(0);
                        validationResult.setErrorMsgKey(this.errorMessages.ok);
                    }

                    return validationResult;

                },
                /**
                 *@param dateArray of day, month, year  
                 * Will return false if there exists at least one defined and non-empty string item
                 */
                isDateUndefined: function (dateArray) {

                    if (!_.isArray(dateArray))
                        return true;
                    for (var i = 0; i < dateArray.length; i++) {
                        var d = dateArray[i];
                        if (!_.isUndefined(d) && (d !== "" || $.trim(d) !== ""))
                            return false;
                    }
                    return true;

                },

                /**
                 *	@args : fromDate(day,month,year), toDate(day,month,year), current:true|false, fatherFieldsetName
                 *	@returns: ValidationErrors Array(validationError object)
                 */

                periodValidate: function (fromDate, toDate, current, fatherFieldsetName) {

                    // Array to hold the validation errors that may occur 
                    // Every validation error is pushed as validationError object
                    var validationErrors = new Array();

                    // Get the From and To attributes, as well as the Current attribute

                    // fromXDate and toXDate are:
                    // - undefined when no validation is applied
                    // - 'empty' when no input is given (errorCode = 1)
                    // - XDate objects when the date is valid (errorCode = 0)
                    //initialize in an OK state
                    var fromXDate = this.isDateUndefined(fromDate) ? 'empty' : new XDate(fromDate[2], Math.abs(fromDate[1] - 1), fromDate[0], true);
                    var toXDate = this.isDateUndefined(toDate) ? 'empty' : new XDate(toDate[2], Math.abs(toDate[1] - 1), toDate[0], true);

                    // 1. Check if the Form date is defined and holds day, month, year attributes
                    // check that the From date is valid, according to the logic of "date validation"
                    // Get the From date attributes and store to fromXDate object			
                    if (typeof fromDate !== 'undefined' && fromDate instanceof Array && fromDate.length == 3) {

                        var validateFromDate = this.dateValidate(fromDate[0], fromDate[1], fromDate[2]);

                        if (!_.isUndefined(validateFromDate) && !_.isNull(validateFromDate)) {
                            switch (validateFromDate.errorCode) {

                                case 0:
                                    validationErrors.push(new ValidationError(fatherFieldsetName + ".From", 0, this.errorMessages.ok));
                                    break;
                                case 1:
                                    fromXDate = 'empty';
                                    validationErrors.push(new ValidationError(fatherFieldsetName + ".From", 1, this.errorMessages.noInput));
                                    break;
                                case 100:
                                    validationErrors.push(new ValidationError(fatherFieldsetName + ".From", 100, this.errorMessages.invalidDate));
                                    return validationErrors;
                                    break;
                                case 102:
                                    validationErrors.push(new ValidationError(fatherFieldsetName + ".From", 102, this.errorMessages.impossibleDate));
                                    break;
                                default:
                                    validationErrors.push(validateFromDate);
                                    break;
                            }
                        }
                    }

                    // 2. Check if the To date is defined and holds day, month, year attributes
                    // check that the To date is valid, according to the logic of "date validation"
                    // Get the To date attributes and store to fromXDate object				
                    if (typeof toDate !== 'undefined' && toDate instanceof Array && toDate.length === 3 && !current) {
                        var validateToDate = this.dateValidate(toDate[0], toDate[1], toDate[2]);

                        if (!_.isUndefined(validateToDate) && !_.isNull(validateToDate)) {
                            switch (validateToDate.errorCode) {
                                case 0:
                                    validationErrors.push(new ValidationError(fatherFieldsetName + ".To", 0, this.errorMessages.ok));
                                    break;
                                case 1:
                                    toXDate = 'empty';
                                    validationErrors.push(new ValidationError(fatherFieldsetName + ".To", 1, this.errorMessages.noInput));
                                    break;
                                case 100:
                                    validationErrors.push(new ValidationError(fatherFieldsetName + ".To", 100, this.errorMessages.invalidDate));
                                    return validationErrors;
                                    break;
                                case 102:
                                    validationErrors.push(new ValidationError(fatherFieldsetName + ".To", 102, this.errorMessages.impossibleDate));
                                    break;
                                default:
                                    validationErrors.push(validateToDate);
                                    break;
                            }
                        }

                    }

                    // (in case both from and to dates are empty and 
                    //  current is false or undefined, return OK with error code 1 - no change)					
                    if (
                            (fromXDate === 'empty' && toXDate === 'empty') &&
                            ((current !== undefined && current === false) || (current === undefined))
                            ) {

                        return new Array(new ValidationError(fatherFieldsetName, 1, this.errorMessages.noInput));
                    }

                    /**
                     * This case is omitted as there is a requirement to allow only to date without from or current date to exist
                     */
                    // 3. Check if the From is undefined or empty,
                    // while the To is defined and non-empty OR the Current is defined and set to true.
                    //if (
                    //	(fromXDate === undefined || fromXDate === 'empty') &&
                    //	((toXDate instanceof XDate) || (current !== undefined && current === true))
                    //	) {
                    //	validationErrors.push(new ValidationError(fatherFieldsetName + ".From", 202, this.errorMessages.nonexistFrom));
                    // }

                    /**
                     * This case is omitted as there is a requirement to allow only from date without to or current date to exist
                     */
                    // 4. Check if the To is undefined or empty AND the Current is defined and set to false,
                    // while the From is defined and non-empty .
                    /*					if(
                     (fromXDate instanceof XDate && toXDate == 'empty' &&
                     ((current != undefined && current === false) || (current == undefined))) 
                     ){
                     validationErrors.pop();   // remove current 0 - OK
                     validationErrors.push(new ValidationError(fatherFieldsetName+".To",203,this.errorMessages.nonexistTo));							
                     }
                     */
                    // 5. In case the Current is set to true, check that From is a valid date before the Current Date, 
                    // and From date is an earlier date than today 
                    if (fromXDate instanceof XDate) {

                        if ((current !== undefined && current !== true) && fromXDate.diffDays(new XDate(this.serverToday), true) < 0) {

                            validationErrors.pop();   // remove current 101 error
                            validationErrors.push(new ValidationError(fatherFieldsetName + ".From", 201, this.errorMessages.negativePeriod));
                        }
                    }

                    // 6. Check that the To date comes after than the From date						
                    if (fromXDate instanceof XDate && toXDate instanceof XDate && !current)
                        if (fromXDate.diffDays(toXDate) < 0) {
                            validationErrors.pop();   // remove current 101 error
                            validationErrors.push(new ValidationError(fatherFieldsetName + ".From", 201, this.errorMessages.negativePeriod));
                        }

                    return validationErrors;


                },
                websiteValidate: function (site) {

                    var webUrl = /^(?:http(s)?:\/\/)?[\w.-]+(?:\.[\w\.-]+)+[\w\-\._~:/?#[\]@!\$&'\(\)\*\+,;=.]+$/
                    var linkedinRegex = /^((https?|ftp|smtp):\/\/)?(www.)?linkedin.com(\w+:{0,1}\w*@)?(\S+)(:([0-9])+)?(\/|\/([\w#!:.?+=&%@!\-\/]))?/;
                    var websiteWarning = $('.modal .ContactInfo .website-warning').length;
                    if (!websiteWarning) {
                        if (linkedinRegex.test(site)) {
                            return true
                        } else {
                            return webUrl.test(site);
                        }
                    } else {
                        this.$('.modal .ContactInfo').removeClass("website-warning");
                        return true;
                    }

                },

                /*
                 * requireValidation gets the event from Save Button, validates and 
                 * triggers 'europass:form:valid' or 'europass:form:invalid' events
                 */

                requireValidation: function (event) {

                    var btn = $(event.target);

                    if (!_.isFunction(ValidationError)) {
                        throw "no-constructor-for-validation-error";
                    }

                    var that = this;

                    that.removeStyles(this.$el);
                    that.validationResults = new Array();

                    // If no date and/or period validation exists for the form, trigger europass:form:valid event and continues
                    // to the form submittion

                    if (this.options.validation) {
                        var validatesArray = this.options.validation.split(" ");
                    } else {
                        var validatesArray = [];
                    }


                    // get validation requirements from this.options.validation and perform validation 
                    for (var i = 0; i < validatesArray.length; i++) {

                        var validationRule = validatesArray[i];

                        if (!_.isString(validationRule))
                            continue;

                        var includeCurrent = false;
                        switch (validationRule) {
                            case "currentDate":
                            {
                                includeCurrent = true;
                            }
                            case "date":
                            {

                                var dateFieldsets = that.$el.find("fieldset[class='Birthdate'],fieldset[class='Date']");
                                $.each(dateFieldsets, function () {

                                    var f = $(this);

                                    var day = f.find("select[name$='Day']").val();
                                    var month = f.find("select[name$='Month']").val();
                                    var year = f.find("select[name$='Year']").val();

                                    var results = new Array();

                                    var currentResult = undefined;
                                    if (includeCurrent === true) {
                                        currentResult = that.currentDateValidate(day, month, year);
                                    } else {
                                        currentResult = that.dateValidate(day, month, year);
                                    }
                                    if (currentResult !== undefined && currentResult !== null) {

                                        // Get fieldset element's name that its class ends in "Date"
                                        currentResult.elementName = f.closest("fieldset.Date").attr("name");

                                        //TODO: alternative text
                                        if (_.isUndefined(currentResult.elementName))
                                            currentResult.elementName = "N/A";

                                        results.push(currentResult);
                                    }

                                    if (_.isArray(results) === false) {
                                        return;
                                    }
                                    for (var j = 0; j < results.length; j++) {
                                        var res = results[j];
                                        if (_.isUndefined(res) || _.isEmpty(res))
                                            continue;
                                        if (res.errorCode !== 0 && res.errorCode !== 1)
                                            that.validationResults.push(res);
                                    }
                                });

                                break;
                            }
                            case "period":
                            {

                                //Period validation does not make sense, if the dates are already invalid.

                                var periodFieldsets = that.$el.find("fieldset[name$='Period']");
                                $.each(periodFieldsets, function () {

                                    var f = $(this);

                                    var fromDay = f.find("select[name$='From.Day']").val();
                                    var fromMonth = f.find("select[name$='From.Month']").val();
                                    var fromYear = f.find("select[name$='From.Year']").val();

                                    var current = f.find("input:checkbox[name$='Current']").prop("checked");

                                    var toDay = f.find("select[name$='To.Day']").val();
                                    var toMonth = f.find("select[name$='To.Month']").val();
                                    var toYear = f.find("select[name$='To.Year']").val();

                                    var results =
                                            that.periodValidate(
                                                    new Array(fromDay, fromMonth, fromYear),
                                                    new Array(toDay, toMonth, toYear),
                                                    current,
                                                    f.attr("name")
                                                    );
                                    if (_.isArray(results) === false) {
                                        return;
                                    }
                                    // iterate validationResults and append validationError objects 
                                    // with code other than 0,1 to validationErrors 
                                    for (var j = 0; j < results.length; j++) {
                                        var res = results[j];
                                        if (_.isUndefined(res) || _.isEmpty(res))
                                            continue;
                                        if (res.errorCode !== 0 && res.errorCode !== 1)
                                            that.validationResults.push(res);
                                    }

                                });

                                break;
                            }
                            case "email":
                            {
                                var emailFieldsets = that.$el.find("fieldset[name$='Email']");
                                $.each(emailFieldsets, function () {
                                    var currentResult = undefined;
                                    var f = $(this);
                                    var email = f.find("input[type$='email']").val();
                                    if (Utils.isValidEmail(email) === false && email !== '') {
                                        var errMsg = that.errorMessages.invalidEmail;
                                        currentResult = new ValidationError(f.attr("name"), 300, errMsg);
                                        that.validationResults.push(currentResult);
                                    }
                                    ;
                                });
                                break;
                            }
                            case "website":
                            {
                                $("input[type^=url]").each(function (index, element) {
                                    var website = element.value;
                                    if (element.value !== '') {
                                        var f = $(this);
                                        if (!that.websiteValidate(website)) {
                                            var errMsg = that.errorMessages.invalidWebsite;
                                            currentResult = new ValidationError(f.attr("name"), 400, errMsg);
                                            that.validationResults.push(currentResult);
                                        }
                                    }
                                });
                                break;
                            }

                            default:
                                continue;
                        }
                    }

                    var isArray = _.isArray(that.validationResults);
                    if (isArray === false) {
                        throw "validation-results-no-array";
                    } else {
                        if (that.validationResults.length === 0) {
                            btn.trigger("europass:form:valid");
                        } else {
                            this.$el.trigger("europass:form:invalid");
                        }
                    }

                },
                
                modalClosed: function (event) {
                    $(event.target).trigger("europass:check:date:format:changes");
                },

                /*
                 * displayErrorMessages uses the hbs validationMessages template to
                 * draw the error messages to the space above the Save/Cancel buttons 
                 */

                displayErrorMessages: function () {

                    var that = this;

                    if (_.isArray(that.validationResults) === false) {
                        return;
                    }

                    var resultSize = that.validationResults.length;
                    if (resultSize === 0) {
                        return;
                    }
                    var context = {};
                    //construct context for validationMessages Template by iterating through the  
                    context.errors = [];
                    context.style = 'margin-top:10px';
                    context.status = 'error';
                    for (var i = 0; i < resultSize; i++) {

                        var el = that.validationResults[i];
                        if (el === undefined || el === null) {
                            continue;
                        }

                        var elemName = el.elementName;

                        var validatedDate = "";

                        //When a specific date is invalid only
                        if (_.isString(elemName) && (el.errorCode === 100 || el.errorCode === 101 || el.errorCode === 102)) {

                            // find array index like [number] and remove it from string
                            var regex = /\[([0-9]+)\]/;

                            var elemNameLabel = elemName.replace(regex, "");
                            elemNameLabel = elemNameLabel.replace("Period", "Dates");

                            elemNameLabel = elemNameLabel.replace(regex, "");

                            validatedDate = DocumentLabel[ elemNameLabel ];
                            if (validatedDate === undefined || validatedDate === null) {
                                validatedDate = GuiLabel[ elemNameLabel ];
                            }

                            validatedDate = "'" + validatedDate + "' ";

//							this.$el.find("fieldset[name='"+elemName+"']").find(".formfield.Date").not('.global-format').addClass("validation-error");
                            this.$el.find("fieldset[name='" + elemName + "']").find("div.formfield.Date").addClass("validation-error");
                        } else {
//						   this.$el.find("fieldset[name='"+elemName+"'],fieldset[class$='Period']").find(".formfield.Date").not('.global-format').addClass("validation-error");
                            this.$el.find("fieldset[name='" + elemName + "'],fieldset[class$='Period']").find(".formfield.Date").addClass("validation-error");
                        }
                        var finalMsg = ValidationErrorMessages[el.errorMsgKey];

                        if (el.errorCode === 102) {
                            var falseSelectedDate = this.$el.find("fieldset[name='" + elemName + "']").find("li.selected").attr("data-raw-value");
                            if (falseSelectedDate !== undefined && falseSelectedDate !== null && falseSelectedDate !== '') {
                                finalMsg = finalMsg.replace("[[DD]]", falseSelectedDate);
                            }
                        }

                        if (el.errorCode === 300) {
                            var json = {};
                            json.message = validatedDate + finalMsg;
                            json.type = 'email';
                            context.errors.push(json);
                            //specific for email context
                            break
                        } else if (el.errorCode === 400) {
                            var json = {};
                            json.type = 'website';
                            json.message = validatedDate + finalMsg;
                            context.errors.push(json);
                            context.status = 'warning';
                            break
                        }
                        var json = {};
                        json.message = validatedDate + finalMsg;
                        context.errors.push(json);

                    }

                    var html = validationMessagesTpl(context);
                    var el = $(html);
                    el.hide();

                    if (context.errors[0].type === 'email') {
                        this.$el.find("fieldset[name$='Email']").prepend(el);
                        el.slideDown(200);
                        //scroll to the top of the page where error message resides
                        $(".overlay:visible > .modal > .main")
                                .scrollTo(".Email", {duration: 1000, easing: 'linear', axis: 'y'});
                    } else if (context.errors[0].type === 'website') {
                        this.$el.find("fieldset[name$='Website']").prepend(el);
                        el.slideDown(200);
                        this.$el.find(".Website").addClass('website-warning');
                        //scroll to the top of the page where error message resides
                        $(".overlay:visible > .modal > .main")
                                .scrollTo(".Website", {duration: 1000, easing: 'linear', axis: 'y'});
                    } else {
                        el.insertAfter(this.$el.find("fieldset:first > header")).slideDown(200);
                        //scroll to the top of the page where error message resides
                        $(".overlay:visible > .modal > .main")
                                .scrollTo('fieldset:first', {duration: 1000, easing: 'linear', axis: 'y'});
                    }
                },

                removeStyles: function (parent) {

                    // reset select elements style sheets
                    parent.find(".formfield.Date").removeClass("validation-error");

                    // remove any error validation messages
                    parent.find('div.feedback-area').remove();


                }
            });

            return ValidationFormView;
        }
);