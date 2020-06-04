var stats = {
    dataType: null,
    dataFmt: null,

    //this object will hold all the parameters that have values assigned on them, 	
    params: {},
    extra: {
        order: {},
        operation: {}
    }, // parameters' extra values, eg orderby #param asc
    maxAllowedParams: 8,

    sections: {
        parameters: {name: "paramName", val: "paramVal"},
        special: {name: "specialParamName", val: "specialParamVal"},
        shortcuts: {name: "shortcutName", val: "shortcutVal"}
    },
    canSend: true, //a coordination flag to limit consequent API calls, while another is being processed

    init: function () {

        //this.render();
        //Initialize each section's select options with the respective parameters and descriptions
        for (var section in stats.sections) {

            //find the select element to hook on, for each section
            var $selectEl = $("select[data-attr='" + stats.sections[section].name + "']");

            //iterate over each section's members
            for (var param in properties[section]) {

                if (!isNullOrUndef(param) && !isNullOrUndef(properties[section][param].descr)) {
                    $selectEl.append(
                            $("<option>", {value: param, "text": properties[section][param].descr //"<option value="+ param +">"+properties[section][param].descr+"</option>"
                            }));
                }
            }
        }
        this.update();
    },

    show: function ($messageContainer, message) {

        if ($.isEmptyObject($messageContainer) || $.isEmptyObject(message))
            return false;

        if ($messageContainer instanceof jQuery && $messageContainer.length > 0) {

            if (!$.isEmptyObject(message)) {
                $messageContainer.html(properties[message]);
            }
        } //else show on a defaul container?

    },
    /* updates the stats object parameters by parsing the DOM for set parameters
     * */
    update: function () {

        var submitBtn = "#ss-send-form-button";

        stats.dataFmt = $(".ss-formwidget-div").find("[data-attr='dataFmt']").val();
        stats.dataType = $(".ss-formwidget-div").find("[data-attr='dataType']").val();

        //no preview for JSON
        if (stats.dataFmt === "json")
            $(submitBtn).hide();
        else
            $(submitBtn).show();

        //reset previous parameters in stats model
        stats.params = {};

        for (var section in stats.sections) {

            stats.params[section] = {}; //reset values
            stats.extra.order[section] = {};
            stats.extra.operation[section] = {};

            var $params = $("[data-section='" + section + "'] [data-type]");

            if (isNullOrUndef($params) || !$params instanceof jQuery || !$params.length > 0)
                continue;

            for (var idx = 0; idx < $params.length; idx++) {

                var selectElem = $params.eq(idx).find("[data-attr='" + stats.sections[section].name + "']");

                var paramName = selectElem.val();  //

                if (!isNullOrUndef(paramName) &&
                        !isNullOrUndef(properties[section][paramName])) {

                    var paramVal = $params.eq(idx).find("[data-attr='" + stats.sections[section].val + "']").val();

                    if (!isNullOrUndef(paramVal) && paramVal.length > 0)
                        stats.params[section][paramName] = paramVal;  //TODO check
                    else
                        stats.params[section][paramName] = "";

                    var operationVal = $params.eq(idx).find("[data-attr='operation']").val();

                    if (!isNullOrUndef(operationVal))
                        stats.extra.operation[section][paramName] = operationVal;

                    var extra = properties[section][paramName]["extra"];

                    if (!isNullOrUndef(extra)) {     //if the current parameter has an extra property
                        var $extraVal = $params.eq(idx).find("[data-attr='extraVal']");

                        if (!isNullOrUndef($extraVal) && $extraVal.length > 0)
                            if (!isNullOrUndef($extraVal.val()) && $extraVal.val().length > 0) {
                                stats.extra.order[section] = stats.extra[section] || {};
                                stats.extra.order[section][paramName] = $extraVal.val();
                            }

                    }
                }
            }
        }
    },

    /* form validation function
     * Iterates on each element with a data-type= parameter on the form and validates its value according to parameters.json
     * returns true if valid, a set of validation error messages to be displayed otherwise*/
    validate: function () {

        var empty = true;

        //list of invalid params messages
        var messages = [];

        for (var section in stats.sections) {

            if ($.isEmptyObject(stats.params[section])) {
                continue;
            }
            empty = false;

            for (key in stats.params[section]) {

                var paramVal = null;
                if (!isNullOrUndef(stats.params[section][key]))
                    paramVal = stats.params[section][key];

                var valid = false;
                //get me each key  
                if (!isNullOrUndef(properties[section][key]) || !isNullOrUndef(properties.parameters[key])) {
                    switch (properties[section][key]["type"]) {
                        case "number" :
                        {
                        }
                        case "input" :
                        {
                        }
                        case "date":
                        {
                            var regexes = properties[section][key]["validation"]["regex"];
                            //check for range too

                            if ($.isEmptyObject(regexes))
                                continue; //TODO ndim stats : considered valid if no regex?

                            //Split if any and/or parameter operators exist
                            var values = paramVal.split(/\||\,/);

                            for (var value in values) { //iterate over each value and check its validity

                                if ($.isEmptyObject(value)) {
                                    valid = false;
                                    break;
                                }

                                // loop over each available regex to find a match
                                for (var idx = 0; idx < regexes.length; idx++) {
                                    var regex = new RegExp(regexes[idx]);
                                    if (regex.test(paramVal)) {
                                        valid = true;
                                    } else if (paramVal.split("-").length == 2 && !$.isEmptyObject(paramVal.split("-")[0]) //might be a range
                                            && !$.isEmptyObject(paramVal.split("-")[1])) {
                                        if (regex.test(paramVal.split("-")[0]) && regex.test(paramVal.split("-")[1])) {
                                            valid = true;
                                        }
                                    }
                                }
                                if (valid === false)
                                    break;
                            }

                            break;
                        }
                        default:
                        {	//TODO implement more validations ( enumlist )
                            valid = true;
                        }
                    }
                }
                if (valid === false) {
                    var msg = (properties[section][key]["validation"] && properties[section][key]["validation"]["message"]) || properties.error.message.validation.replace("[[param]]", key);
                    var hint = properties[section][key]["hint"] || "";
                    messages.push(msg + hint || "Validation error for " + key + " parameter.");
                }
            }
        }

        if (empty)
            return [properties.error.message.empty];
        else if (messages.length > 0)
            return messages;
        else {
            $(".ss-data-validation-error").html("");	//clear messages area
            return true;
        }
    },

    /** returns the GET url by reading parameters' values from stats object
     */
    getURL: function () {

        var url = "https://europassdev1.instore.gr/stats/";

        var dataFmt = "to/" + stats.dataFmt + "/";
        var dataType = stats.dataType + ";";

        url += dataFmt + dataType;
        for (var section in stats.sections) {
            for (key in stats.params[section]) {
                url += key;

                var values = stats.params[section][key];

                if ($.isEmptyObject(values)) {
                    url += ";";
                    continue;
                }

                url += "=";

                var op = stats.extra.operation[section][key]; //operation between parameter values

                if (typeof values === "object")		//it's an array of elements
                    for (var idx = 0; idx < values.length; idx++) {
                        if (op === "OR")
                            url += values[idx] + "|";
                        else if (op === "AND")
                            url += values[idx] + ",";

                        url = (idx === values.length - 1) ? url.substring(0, url.length - 1) : url; //remove the tailing operator when reached last element
                    }
                else if (typeof values === "string")
                    url += values;
                else
                    continue;  // can't handle this value

                var extra = properties[section][key]["extra"];

                if (!isNullOrUndef(extra) && !isNullOrUndef(stats.extra.order[section][key]))
                    url += "." + stats.extra.order[section][key];

                url += ";";
            }
        }
        return url;
    },

    /*performs the AJAX GET on the stats API and 
     * @parameter the resultsDisplayEl
     * @returns the response data
     * @returns false if an error has occured ( in csv format
     * on case of error, it displays it appropriately on the 
     */
    getStatsFromAPI: function (resultsDisplayEl) {
        //build the URL depending on input values
        var url = stats.getURL();

        //format the data ?
        var result = null;

        var that = this; // for the callback later to see which element it was called from

        $.get(url, function (data) {

            if (data === undefined || data === null) {

                $(resultsDisplayEl).html("No data available for this request");
                return false;
            }

            if (typeof data === "string" && data.indexOf("error_code" == 0) && data.split('\n').length == 2) { //if is error CSV

                var messages = data.split('\n')[1].split(",");
                // get the rest of the array items (can be more than 2 because the split(",") might pick up on commas from invalid parameter values
                var msg = messages.length >= 4 ? messages[2] : properties.error.message.general;

                if (messages.slice(3, messages.length).indexOf("NULL") < 0)
                    msg += ". Cause: " + messages.slice(3, messages.length); //only display the message and the cause

                $(resultsDisplayEl).html(msg); //TODO error results el
                return false;
            }

            //if called from "download" button
            if ($(that).hasClass("download")) {

                downloadData.apply(that, [data]);

                $(resultsDisplayEl).html("Downloaded successfully");
            } else {  //else is preview

                if (stats.dataFmt === "json" && data.Dataset !== undefined) {
                    result = JSON.stringify(data, null, 2);
                } else if (stats.dataFmt === "csv") {
                    result = csvToTable(data);
                }
                $(resultsDisplayEl).html(result);
            }
        })
                // On HTTP error response status
                .fail(function (data) {
                    if (!isNullOrUndef(data)) {

                        if (!isNullOrUndef(data.responseJSON) && !isNullOrUndef(data.responseJSON.error) && !isNullOrUndef(data.responseJSON.error.message)) {

                            var message = data.responseJSON.error.message;

                            if (!isNullOrUndef(data.responseJSON.error.cause) && "NULL" !== data.responseJSON.error.cause) {
                                message += " because " + data.responseJSON.error.cause;
                            }
                            //TODO $( resultsDisplayEl ) >> error results el
                            $(resultsDisplayEl).html(message);
                        } else if (!isNullOrUndef(data.responseText)) {
                            $(resultsDisplayEl).html(data.responseText);
                        }
                    } else {
                        $(resultsDisplayEl).html(properties.error.message.general);
                    }

                })
                .always(function () {
                    stats.canSend = true;
                });
        return  result;
    }
};

$().ready(function () {

    var submitBtn = "#ss-send-form-button";
    //var downloadBtn = ".download";

    stats.init();

    ///////////////// EVENT HANDLING /////////////////
    //PARAMETER CHANGE
    $("select[data-attr]").on("change", function (e) {

        var el = $(e.target);
        var paramName;

        if (!isNullOrUndef(el)) {

            var $valContainer = el.closest("[data-type]").find(".valContainer");

            var $paramVal = $valContainer.find(".paramValue");

            $valContainer.find(".operation").html(""); //reset the operation and extra selectors' container divs

            var $extraVal = $valContainer.find(".extra");
            $extraVal.html("");

            if (!isNullOrUndef(el.val()) && el.val() !== "") {

                paramName = el.val();

                var params = el.closest("[data-section]").attr("data-section");

                //find the element that is the parameter value container

                if (!isNullOrUndef(properties[params]) && !isNullOrUndef(properties[params][paramName])) {

                    var paramType = properties[params][paramName]["type"];

                    if (typeof paramType === "string") {

                        $valContainer.siblings(".reset").show();

                        // Show different input fields depending on parameter type
                        switch (paramType) {

                            case "enum":
                            {

                                var selectEl = $("<select>", {class: paramName, "data-attr": stats.sections[params].val});  //"<select class='" + paramName + "' data-attr='"+ stats.sections[params].val +"'></select>";

                                if (params === "parameters")
                                    selectEl.attr("multiple", "");

                                $paramVal.html(selectEl);

                                var $selectEl = $paramVal.find("." + paramName);

                                var values = properties[params][paramName]["values"];
                                var labels = properties[params][paramName]["labels"];

                                for (idx in values) {

                                    var escapedValue = values[idx];
                                    if (paramName == "language" || paramName == "mlanguage" || paramName == "olanguage")
                                        escapedValue = escapedValue.replace(/;/g, "<GRC-QMARK>");

                                    $selectEl.append($('<option>', {
                                        value: escapedValue,
                                        text: (labels === undefined ? values[idx] : labels[idx])
                                    }));
                                }
                                if (params === "parameters") {

                                    var $operation = $valContainer.find(".operation"); //wrapper for the operation selector

                                    var $opSel = $("<select>", {"data-attr": "operation"});

                                    $opSel.append($("<option>", {value: "OR", text: "Union"})).append($("<option>", {value: "AND", text: "Intersection"}));
                                    $operation.html($opSel);	//Add AND/ OR selectors
                                }

                                var extra = properties[params][paramName]["extra"];  // append extra options

                                if (!$.isEmptyObject(extra)) {

                                    var $extraSel = $("<select>", {"data-attr": "extraVal"}).appendTo($extraVal); //add data- attr for update to pick up

                                    for (var idx = 0; idx < extra.length; idx++) {
                                        $extraSel.append($('<option>', {
                                            value: extra[idx],
                                            text: extra[idx] //TODO show user friendly text
                                        }));
                                    }
                                }
                                break;
                            }
                            case "number":
                            {
                            }
                            case "date":
                            {
                                var inputEl = $("<input>", {class: paramName, "data-attr": stats.sections[params].val}); //"<input class='" + paramName + "' data-attr='paramVal'></input>";   
                                $paramVal.html(inputEl);
                                break;
                            }
                            default :
                            {
                            }
                        }
                    }
                }
            } else {	//reset was clicked, reset all the fields.
                $paramVal.html("");
                $valContainer.siblings(".reset").hide();
            }
            stats.update();
        }
    });

    $(".addParameter").on("click", function (e) {

        var $el = $(e.target);

        //find current section
        var section = $el.closest("[data-section]").attr("data-section");

        //find parent element
        var $paramEl = $el.closest("[data-type]");

        var paramName = $paramEl.find("[data-attr]").val();

        // New element construction
        var $newParam = $paramEl.clone(true, true); //copy this element, event handlers included

        //reset the parameter values html and update with remaining parameters
        $newParam.find(".valContainer").html("").append($("<div>", {class: "paramValue"})).append($("<div>", {class: "extra"})).append($("<div>", {class: "operation"}));

        $newParam.find(".reset").hide(); //hide the param reset button

        var $paramSelect = $newParam.find("select");

        $paramSelect.html($("<option>", {text: "Select an option", "disabled": "", "selected": ""}).attr("disabled", "disabled").attr("selected", "enabled"));

        for (var param in properties[section]) {
            //if this isn't the parameter being added now and it hasnt been already added, show it as an option
            if (param !== paramName && $.isEmptyObject(stats.params[section][param]))
                $paramSelect.append(
                        $("<option>", {value: param, "text": properties[section][param].descr //"<option value="+ param +">"+properties[section][param].descr+"</option>"
                        }));
        }

        $paramEl.find(".addParameter").hide();
        $paramEl.find(".removeParameter").show();

        $paramEl.after($newParam);

        stats.update();  //update the model
    });

    $(".removeParameter").on("click", function (e) {
        var $el = $(e.target);

        if (!isNullOrUndef($el) && $el.length > 0)
            $el.closest("[data-type]").remove();

        stats.update();
    });

    $(".reset").on("click", function (e) {
        var $el = $(e.target);

        if (isNullOrUndef($el) || $el.length == 0)
            return false;

        var $sel = $el.closest("[data-type]").find("[data-attr]");

        $sel.prop('selectedIndex', 0).trigger("change"); //trigger change on select, let the select change handler do the rest

        stats.update();
    });

    //FORM SUBMISSION EVENT
    $(submitBtn + ",.download").on("click", function () {

        var resultsDisplayEl = ".confirmation-message-container";
        var feedbackContainer = ".ss-data-validation-error";

        stats.update();

        var validated = stats.validate();

        if (validated !== true) {

            //get messages stats.show(  )
            if (!$.isEmptyObject(validated))
                $(feedbackContainer).show().html(validated);
            return false;
        }

        $(feedbackContainer).hide();

        if (stats.canSend === true) {
            stats.canSend = false;

            //if validation ok
            $("#results-container").show();

            $(resultsDisplayEl).html("Loading..");

            stats.update();

            stats.getStatsFromAPI.apply(this, [resultsDisplayEl]);
        }
    });
});



//#################### PROPERTIES / UTILITIES #############################

var properties = {
    "parameters": {
        "document-type": {
            "type": "enum",
            "values": ["CV", "ESP", "LP", "CL", "CV_ESP"],
            "valRegEx": "",
            "descr": "Document Type",
            "hint": ""
        },
        /*"document-format":{  //TODO uncomment when more data types beyond generated are available
         "type":"enum",
         "values":["template","instructions","examples"],
         "valRegEx":"",
         "descr":"Document Format"
         },
         "examples-format":{  
         "type":"enum",
         "values":["EM","ECS","EDS"],
         "valRegEx":"",
         "descr":"Example Document Formats"
         },*/
        "country": {
            "type": "enum",
//			"values":["bg","hu","es","mk","cs","mt","da","nl","de","no","et","pl","el","pt","en","ro","fr","sk","hr","sl","is","fi","it","sv","lv","tr","lt"],
            "values": ["Andorra", "United Arab Emirates", "Afghanistan", "Antigua and Barbuda", "Anguilla", "Albania", "Armenia", "Netherlands Antilles", "Angola", "Antarctica", "Argentina", "American Samoa", "Austria", "Australia", "Aruba", "Aland Islands", "Azerbaijan", "Bosnia and Herzegovina", "Barbados", "Bangladesh", "Belgium", "Burkina Faso", "Bulgaria", "Bahrain", "Burundi", "Benin", "Bermuda", "Brunei Darussalam", "Bolivia", "Brazil", "Bahamas", "Bhutan", "Bouvet Island", "Botswana", "Belarus", "Belize", "Canada", "Cocos (Keeling) Islands", "Congo, Democratic Republic of the", "Central African Republic", "Congo, Republic of the", "Switzerland", "Cote d/Ivoire", "Cook Islands", "Chile", "Cameroon", "China", "Colombia", "Costa Rica", "Cuba", "Cape Verde", "Christmas Island", "Cyprus", "Czech Republic", "Germany", "Djibouti", "Denmark", "Dominica", "Dominican Republic", "Algeria", "Ecuador", "Estonia", "Egypt", "Western Sahara", "Eritrea", "Spain", "Ethiopia", "Finland", "Fiji", "Falklands Islands", "Federated States of Micronesia", "Faroe Islands", "France", "Gabon", "United Kingdom", "Grenada", "Georgia", "French Guiana", "Ghana", "Gibraltar", "Greenland", "Gambia", "Guinea", "Guadeloupe", "Equatorial Guinea", "Greece", "South Georgia and the South Sandwich Islands", "Guatemala", "Guam", "Guinea-Bissau", "Guyana", "Hong Kong", "Heard Island and McDonald Islands", "Honduras", "Croatia", "Haiti", "Hungary", "Indonesia", "Ireland", "Israel", "India", "British Indian Ocean Territory", "Iraq", "Iran", "Iceland", "Italy", "Jamaica", "Jordan", "Japan", "Kenya", "Kyrgyzstan", "Cambodia", "Kiribati", "Comoros", "Saint Kitts and Nevis", "Korea, Democratic People/s Republic", "Korea, Republic of", "Kuwait", "Cayman Islands", "Kazakstan", "Lao People/s Democratic Republic", "Lebanon", "Saint Lucia", "Liechtenstein", "Sri Lanka", "Liberia", "Lesotho", "Lithuania", "Luxembourg", "Latvia", "Libyan Arab Jamahiriya", "Morocco", "Monaco", "Moldova, Republic of", "Montenegro", "Madagascar", "Marshall Islands", "Macedonia, the former Yugoslav Republic of", "Mali", "Myanmar", "Mongolia", "Macao", "Northern Mariana Islands", "Martinique", "Mauritania", "Montserrat", "Malta", "Mauritius", "Maldives", "Malawi", "Mexico", "Malaysia", "Mozambique", "Namibia", "New Caledonia", "Niger", "Norfolk Island", "Nigeria", "Nicaragua", "Netherlands", "Norway", "Nepal", "Nauru", "Niue", "New Zealand", "Oman", "Panama", "Peru", "French Polynesia", "Papua New Guinea", "Philippines", "Pakistan", "Poland", "Saint Pierre and Miquelon", "Pitcairn", "Puerto Rico", "Palestinian Territory, Occupied", "Portugal", "Palau", "Paraguay", "Qatar", "Reunion", "Romania", "Serbia", "Russian Federation", "Rwanda", "Saudi Arabia", "Solomon Islands", "Seychelles", "Sudan", "Sweden", "Singapore", "Saint Helena", "Slovenia", "Svalbard and Jan Mayen", "Slovakia", "Sierra Leone", "San Marino", "Senegal", "Somalia", "Suriname", "Sao Tome and Principe", "El Salvador", "Syrian Arab Republic", "Swaziland", "Turks and Caicos Islands", "Chad", "French Southern Territories", "Togo", "Thailand", "Tajikistan", "Tokelau", "Timor-Leste", "Turkmenistan", "Tunisia", "Tonga", "Turkey", "Trinidad and Tobago", "Tuvalu", "Taiwan, Province of China", "Tanzania, United republic of", "Ukraina", "Uganda", "United states minor outlaying islands", "United States", "Uruguay", "Uzbekistan", "Holy See (Vatican City State)", "Saint Vincent and the Granadines", "Venezuela", "Virgin Islands, British", "Virgin Islands, U.S.", "Viet Nam", "Vanuatu", "Wallis and Futuna", "Samoa", "Yemen", "Mauritius", "South Africa", "Zambia", "Zimbabwe"],
            "valRegEx": "",
            "descr": "Country"
        },
        "language": {
            "type": "enum",
            "values": ["bg_BG", "hu_HU", "es_ES", "mk_MK", "cs_CZ", "mt_MT", "da_DK", "nl_NL", "de_DE", "no_NO", "et_EE", "pl_PL", "el_GR", "pt_PT", "en_GB", "ro_RO", "fr_FR", "sk_SK", "hr_HR", "sl_SI", "is_IS", "fi_FI", "it_IT", "sv_SE", "lv_LV", "tr_TR", "lt_LT"],
            "labels": ["Bulgarian", "Hungarian", "Spanish", "mk_MK", "Czech", "Maltese", "Danish", "Dutch", "German", "Norwegian", "Estonian", "Polish", "Greek", "Portuguese", "English", "Romanian", "French", "Slovak", "Hungarian", "Slovenian", "Icelandic", "Finnish", "Italian", "Swedish", "Latvian", "Turkish", "Lithuanian"],
            "valRegEx": "",
            "descr": "Document Language"
        },
        "mlanguage": {
            "type": "enum",
//			"values":["bg_BG","hu_HU","es_ES","mk_MK","cs_CS","mt_MT","da_DA","nl_NL","de_DE","no_NO","et_ET","pl_PL","el_GR","pt_PT","en_GB","ro_RO","fr_FR","sk_SK","hr_HR","sl_SL","is_IS","fi_FI","it_IT","sv_SV","lv_LV","tr_TR","lt_LT"],
            "values": ["Afar", "Abkhazian", "Achinese", "Acoli; acholi", "Adangme", "Adyghe; Adygei", "Afrihili (constructed language)", "Afrikaans", "Ainu", "Akan", "Akkadian (dead language)", "Albanian", "Aleut", "Southern Altai", "Amharic", "English, Old (ca.450-1100)", "Angika", "Apache language", "Arabic", "Aramaic", "Aragonese", "Armenian", "Araucanian", "Arapaho", "Arawak", "Assamese", "Asturian; Bable", "Avaric", "Avestan", "Awadhi", "Aymara", "Azerbaijani", "Banda", "Bashkir", "Baluchi", "Bambara", "Balinese", "Basque", "Basa", "Beja; Bedawi", "Belarusian", "Bemba", "Bengali", "Bhojpuri", "Bihari", "Bikol", "Bini", "Bislama", "Siksika", "Bosnian", "Braj", "Breton", "Batak (Indonesia)", "Buriat", "Buginese", "Bulgarian", "Burmese", "Blin; Bilin", "Caddo", "Carib", "Catalan; Valencian", "Cebuano", "Chamorro", "Chibcha", "Chechen", "Chagatai", "Chinese", "Chuukese", "Mari", "Chinook jargon", "Choctaw", "Chipewyan", "Cherokee", "Church Slavic; Old Slavonic; Church Slavonic; Old Bulgarian; Old Church Slavonic", "Chuvash", "Cheyenne", "Coptic", "Cornish", "Corsican", "Cree", "Crimean Tatar; Crimean Turkish", "Kashubian", "Czech", "Dakota", "Danish", "Dargwa", "Dayak", "Delaware", "Dogrib", "Dinka", "Divehi; (Maldivian)", "Dogri", "Lower Sorbian", "Duala", "Dutch, Middle (ca.1050-1350)", "Dutch", "Dyula", "Dzongkha", "Efik", "Egyptian (Ancient)", "Ekajuk", "Elamite", "English", "English, Middle (1100-1500)", "Esperanto", "Estonian", "Ewe", "Ewondo", "Fang", "Faroese", "Fanti", "Fijian", "Filipino; Pilipino", "Finnish", "Fon", "French", "French, Middle (ca.1400-1600)", "French, Old (842-ca.1400)", "Northern Frisian", "Eastern Frisian", "Western Frisian", "Fulah", "Friulian", "Ga", "Gayo", "Gbaya", "Georgian", "German", "Geez", "Gilbertese", "Gaelic; Scottish Gaelic", "Irish", "Galician", "Manx", "German, Middle High (ca.1050-1500)", "German, Old High (ca.750-1050)", "Gondi", "Gorontalo", "Gothic", "Grebo", "Ancient Greek", "Greek", "Guarani", "Alemanic; Swiss German", "Gujarati", "Gwich/in", "Haida", "Haitian; Haitian Creole", "Hausa", "Hawaiian", "Hebrew", "Herero", "Hiligaynon", "Himachali", "Hindi", "Hittite", "Hmong", "Hiri Motu", "Upper Sorbian", "Hungarian", "Hupa", "Iban", "Igbo", "Icelandic", "Ido", "Sichuan Yi (Nosu)", "Ijo", "Inuktitut", "Interlingue", "Iloko", "Interlingua (International Auxiliary Language Association)", "Indonesian", "Ingush", "Inupiaq", "Italian", "Javanese", "Lojban", "Japanese", "Judeo-Persian", "Judeo-Arabic", "Kara-Kalpak", "Kabyle", "Kachin", "Kalaallisut; Greenlandic", "Kamba", "Kannada", "Karen", "Kashmiri", "Kanuri", "Kazakh", "Kabardian", "Khasi", "Khmer", "Khotanese", "Kikuyu; Gikuyu", "Kinyarwanda", "Kirghiz", "Kimbundu", "Konkani", "Komi", "Kongo", "Korean", "Kosraean", "Kpelle", "Karachay-Balkar", "Karelian", "Kru", "Kurukh", "Kuanyama; Kwanyama", "Kumyk", "Kurdish", "Kutenai", "Ladino", "Lahnda", "Lamba", "Lao", "Latin", "Latvian", "Lezghian", "Limburgan; Limburger; Limburgish", "Lingala", "Lithuanian", "Mongo", "Lozi", "Luxembourgish; Letzeburgesch", "Luba-Lulua", "Luba-Katanga", "Ganda", "Luiseno", "Lunda", "Luo (Kenya and Tanzania)", "lushai", "Macedonian", "Madurese", "Magahi", "Marshallese", "Maithili", "Makasar", "Malayalam", "Mandingo", "Maori", "Marathi", "Masai", "Malay", "Moksha", "Mandar", "Mende", "Irish, Middle (900-1200)", "Mi'kmaq; Micmac", "Minangkabau", "Malagasy", "Maltese", "Manchu", "Manipuri", "Mohawk", "Moldavian", "Mongolian", "Mossi", "Creek", "Mirandese", "Marwari", "Erzya", "Nahuatl", "Neapolitan", "Nauru", "Navajo; Navaho", "Ndebele, South; South Ndebele", "Ndebele, North; North Ndebele", "Ndonga", "Low German; Low Saxon; German, Low; Saxon, Low", "Nepali", "Newari; Nepal Bhasa", "Nias", "Niuean", "Norwegian Nynorsk; Nynorsk, Norwegian", "Norwegian Bokmal; Bokmal, Norwegian*", "Nogai", "Norse, Old", "Norwegian", "Northern Sotho, Pedi; Sepedi", "Classical Newari; Old Newari; Classical Nepal Bhasa", "Chichewa; Chewa; Nyanja", "Nyamwezi", "Nyankole", "Nyoro", "Nzima", "Occitan (post 1500); Provencal", "Ojibwa", "Oriya", "Oromo", "Osage", "Ossetian; Ossetic", "Otomian languages", "Papuan (Other)", "Pangasinan", "Pahlavi", "Pampanga", "Panjabi; Punjabi", "Papiamento", "Palauan", "Persian, Old (ca.600-400 B.C.)", "Persian", "Philippine (Other)", "Phoenician", "Pali", "Polish", "Pohnpeian", "Portuguese", "Provencal, Old (to 1500)", "Pushto", "Quechua", "Rajasthani", "Rapanui", "Rarotongan", "Raeto-Romance", "Romany", "Romanian", "Rundi", "Aromanian; Arumanian; Macedo-Romanian", "Russian", "Sandawe", "Sango", "Yakut", "Samaritan Aramaic", "Sanskrit", "Sasak", "Santali", "Serbian", "Sicilian", "Scots", "Croatian", "Selkup", "Irish, Old (to 900)", "Sign Languages", "Shan", "Sidamo", "Sinhala; Sinhalese", "Slovak", "Slovenian", "Southern Sami", "Northern Sami", "Lule Sami", "Inari Sami", "Samoan", "Skolt Sami", "Shona", "Sindhi", "Soninke", "Sogdian", "Somali", "Songhai", "Sotho, Southern", "Spanish; Castilian", "Sardinian", "Sranan Togo", "Serer", "Swati", "Sukuma", "Sundanese", "Susu", "Sumerian", "Swahili", "Swedish", "Syriac", "Tahitian", "Tamil", "Tatar", "Telugu", "Timne", "Tereno", "Tetum", "Tajik", "Tagalog", "Thai", "Tibetan", "Tigre", "Tigrinya", "Tiv", "Tokelau", "Klingon; tlhIngan-Hol", "Tlingit", "Tamashek", "Tonga (Nyasa)", "Tonga (Tonga Islands)", "Tok Pisin", "Tsimshian", "Tswana", "Tsonga", "Turkmen", "Tumbuka", "Turkish", "Tuvalu", "Twi", "Tuvinian", "Udmurt", "Ugaritic", "Uighur; Uyghur", "Ukrainian", "Umbundu", "Urdu", "Uzbek", "Vai", "Venda", "Vietnamese", "Volapuk", "Votic", "Walamo", "Waray", "Washo", "Welsh", "Walloon", "Wolof", "Kalmyk", "Xhosa", "Yao", "Yapese", "Yiddish", "Yoruba", "Zapotec", "Zenaga", "Zhuang; Chuang", "Zande", "Zulu", "Zuni"],
            "valRegEx": "",
            "descr": "Mother Language"
        },
        "olanguage": {
            "type": "enum",
//			"values":["bg_BG","hu_HU","es_ES","mk_MK","cs_CS","mt_MT","da_DA","nl_NL","de_DE","no_NO","et_ET","pl_PL","el_GR","pt_PT","en_GB","ro_RO","fr_FR","sk_SK","hr_HR","sl_SL","is_IS","fi_FI","it_IT","sv_SV","lv_LV","tr_TR","lt_LT"],
            "values": ["Afar", "Abkhazian", "Achinese", "Acoli; acholi", "Adangme", "Adyghe; Adygei", "Afrihili (constructed language)", "Afrikaans", "Ainu", "Akan", "Akkadian (dead language)", "Albanian", "Aleut", "Southern Altai", "Amharic", "English, Old (ca.450-1100)", "Angika", "Apache language", "Arabic", "Aramaic", "Aragonese", "Armenian", "Araucanian", "Arapaho", "Arawak", "Assamese", "Asturian; Bable", "Avaric", "Avestan", "Awadhi", "Aymara", "Azerbaijani", "Banda", "Bashkir", "Baluchi", "Bambara", "Balinese", "Basque", "Basa", "Beja; Bedawi", "Belarusian", "Bemba", "Bengali", "Bhojpuri", "Bihari", "Bikol", "Bini", "Bislama", "Siksika", "Bosnian", "Braj", "Breton", "Batak (Indonesia)", "Buriat", "Buginese", "Bulgarian", "Burmese", "Blin; Bilin", "Caddo", "Carib", "Catalan; Valencian", "Cebuano", "Chamorro", "Chibcha", "Chechen", "Chagatai", "Chinese", "Chuukese", "Mari", "Chinook jargon", "Choctaw", "Chipewyan", "Cherokee", "Church Slavic; Old Slavonic; Church Slavonic; Old Bulgarian; Old Church Slavonic", "Chuvash", "Cheyenne", "Coptic", "Cornish", "Corsican", "Cree", "Crimean Tatar; Crimean Turkish", "Kashubian", "Czech", "Dakota", "Danish", "Dargwa", "Dayak", "Delaware", "Dogrib", "Dinka", "Divehi; (Maldivian)", "Dogri", "Lower Sorbian", "Duala", "Dutch, Middle (ca.1050-1350)", "Dutch", "Dyula", "Dzongkha", "Efik", "Egyptian (Ancient)", "Ekajuk", "Elamite", "English", "English, Middle (1100-1500)", "Esperanto", "Estonian", "Ewe", "Ewondo", "Fang", "Faroese", "Fanti", "Fijian", "Filipino; Pilipino", "Finnish", "Fon", "French", "French, Middle (ca.1400-1600)", "French, Old (842-ca.1400)", "Northern Frisian", "Eastern Frisian", "Western Frisian", "Fulah", "Friulian", "Ga", "Gayo", "Gbaya", "Georgian", "German", "Geez", "Gilbertese", "Gaelic; Scottish Gaelic", "Irish", "Galician", "Manx", "German, Middle High (ca.1050-1500)", "German, Old High (ca.750-1050)", "Gondi", "Gorontalo", "Gothic", "Grebo", "Ancient Greek", "Greek", "Guarani", "Alemanic; Swiss German", "Gujarati", "Gwich/in", "Haida", "Haitian; Haitian Creole", "Hausa", "Hawaiian", "Hebrew", "Herero", "Hiligaynon", "Himachali", "Hindi", "Hittite", "Hmong", "Hiri Motu", "Upper Sorbian", "Hungarian", "Hupa", "Iban", "Igbo", "Icelandic", "Ido", "Sichuan Yi (Nosu)", "Ijo", "Inuktitut", "Interlingue", "Iloko", "Interlingua (International Auxiliary Language Association)", "Indonesian", "Ingush", "Inupiaq", "Italian", "Javanese", "Lojban", "Japanese", "Judeo-Persian", "Judeo-Arabic", "Kara-Kalpak", "Kabyle", "Kachin", "Kalaallisut; Greenlandic", "Kamba", "Kannada", "Karen", "Kashmiri", "Kanuri", "Kazakh", "Kabardian", "Khasi", "Khmer", "Khotanese", "Kikuyu; Gikuyu", "Kinyarwanda", "Kirghiz", "Kimbundu", "Konkani", "Komi", "Kongo", "Korean", "Kosraean", "Kpelle", "Karachay-Balkar", "Karelian", "Kru", "Kurukh", "Kuanyama; Kwanyama", "Kumyk", "Kurdish", "Kutenai", "Ladino", "Lahnda", "Lamba", "Lao", "Latin", "Latvian", "Lezghian", "Limburgan; Limburger; Limburgish", "Lingala", "Lithuanian", "Mongo", "Lozi", "Luxembourgish; Letzeburgesch", "Luba-Lulua", "Luba-Katanga", "Ganda", "Luiseno", "Lunda", "Luo (Kenya and Tanzania)", "lushai", "Macedonian", "Madurese", "Magahi", "Marshallese", "Maithili", "Makasar", "Malayalam", "Mandingo", "Maori", "Marathi", "Masai", "Malay", "Moksha", "Mandar", "Mende", "Irish, Middle (900-1200)", "Mi'kmaq; Micmac", "Minangkabau", "Malagasy", "Maltese", "Manchu", "Manipuri", "Mohawk", "Moldavian", "Mongolian", "Mossi", "Creek", "Mirandese", "Marwari", "Erzya", "Nahuatl", "Neapolitan", "Nauru", "Navajo; Navaho", "Ndebele, South; South Ndebele", "Ndebele, North; North Ndebele", "Ndonga", "Low German; Low Saxon; German, Low; Saxon, Low", "Nepali", "Newari; Nepal Bhasa", "Nias", "Niuean", "Norwegian Nynorsk; Nynorsk, Norwegian", "Norwegian Bokmal; Bokmal, Norwegian*", "Nogai", "Norse, Old", "Norwegian", "Northern Sotho, Pedi; Sepedi", "Classical Newari; Old Newari; Classical Nepal Bhasa", "Chichewa; Chewa; Nyanja", "Nyamwezi", "Nyankole", "Nyoro", "Nzima", "Occitan (post 1500); Provencal", "Ojibwa", "Oriya", "Oromo", "Osage", "Ossetian; Ossetic", "Otomian languages", "Papuan (Other)", "Pangasinan", "Pahlavi", "Pampanga", "Panjabi; Punjabi", "Papiamento", "Palauan", "Persian, Old (ca.600-400 B.C.)", "Persian", "Philippine (Other)", "Phoenician", "Pali", "Polish", "Pohnpeian", "Portuguese", "Provencal, Old (to 1500)", "Pushto", "Quechua", "Rajasthani", "Rapanui", "Rarotongan", "Raeto-Romance", "Romany", "Romanian", "Rundi", "Aromanian; Arumanian; Macedo-Romanian", "Russian", "Sandawe", "Sango", "Yakut", "Samaritan Aramaic", "Sanskrit", "Sasak", "Santali", "Serbian", "Sicilian", "Scots", "Croatian", "Selkup", "Irish, Old (to 900)", "Sign Languages", "Shan", "Sidamo", "Sinhala; Sinhalese", "Slovak", "Slovenian", "Southern Sami", "Northern Sami", "Lule Sami", "Inari Sami", "Samoan", "Skolt Sami", "Shona", "Sindhi", "Soninke", "Sogdian", "Somali", "Songhai", "Sotho, Southern", "Spanish; Castilian", "Sardinian", "Sranan Togo", "Serer", "Swati", "Sukuma", "Sundanese", "Susu", "Sumerian", "Swahili", "Swedish", "Syriac", "Tahitian", "Tamil", "Tatar", "Telugu", "Timne", "Tereno", "Tetum", "Tajik", "Tagalog", "Thai", "Tibetan", "Tigre", "Tigrinya", "Tiv", "Tokelau", "Klingon; tlhIngan-Hol", "Tlingit", "Tamashek", "Tonga (Nyasa)", "Tonga (Tonga Islands)", "Tok Pisin", "Tsimshian", "Tswana", "Tsonga", "Turkmen", "Tumbuka", "Turkish", "Tuvalu", "Twi", "Tuvinian", "Udmurt", "Ugaritic", "Uighur; Uyghur", "Ukrainian", "Umbundu", "Urdu", "Uzbek", "Vai", "Venda", "Vietnamese", "Volapuk", "Votic", "Walamo", "Waray", "Washo", "Welsh", "Walloon", "Wolof", "Kalmyk", "Xhosa", "Yao", "Yapese", "Yiddish", "Yoruba", "Zapotec", "Zenaga", "Zhuang; Chuang", "Zande", "Zulu", "Zuni"],
            "valRegEx": "",
            "descr": "Other Language"
        },
        "nationality": {
            "type": "enum",
            "values": ["Afghan", "Albanian", "Algerian", "American", "Andorran", "Angolan", "Antiguan", "Argentinian", "Armenian", "Australian", "Austrian", "Azerbaijani", "Bahamian", "Bahraini", "Bangladeshi", "Barbadian", "Belgian", "Belizean", "Beninese", "Bhutanese", "Bolivian", "Bosnian-Herzegovinian", "Botswanan", "Brazilian", "British", "Bruneian ", "Bulgarian", "Burkinabe", "Burmese (Myanmar)", "Burundian", "Byelorussian", "Cambodian", "Cameroonian", "Canadian", "Cape Verdean", "Central African", "Chadian", "Chilean", "Chinese", "Colombian", "Comorian", "Congolese (Democratic Republic of the Congo)", "Congolese (Republic of the Congo)", "Costa Rican", "Croatian", "Cuban", "Cypriot", "Czech", "Danish", "Djiboutian", "Dominican (Dominica)", "Dominican (Dominican Republic)", "Dutch", "Ecuadorian", "Egyptian", "Emirati (United Arab Emirates)", "Equatoguinean (Equatorial Guinea)", "Eritrean", "Estonian", "Ethiopian", "Fijian", "Finnish", "French", "Gabonese", "Gambian", "Georgian", "German", "Ghanaian", "Greek", "Greenlandic ", "Grenadian", "Guatemalan", "Guinean (Guinea)", "Guinean (Republic of Guinea-Bissau)", "Guyanese (Guyana)", "Haitian", "Honduran", "Hungarian", "I-kiribati", "Icelandic", "Indian", "Indonesian", "Iranian", "Iraqi", "Irish", "Israeli", "Italian", "Ivorian", "Jamaican", "Japanese", "Jordanian", "kazakhstani", "Kenyan", "Kittsian", "Kosovar", "Kuwaiti", "Kyrgyzstani", "Laotian", "Latvian", "Lebanese", "Liberian", "Libyan", "Liechtensteiner", "Lithuanian", "Luxembourgish", "Malagasy", "Malawian", "Malaysian", "Maldivian", "Malian", "Maltese", "Marshallese", "Mauritanian", "Mauritian", "Mexican", "Micronesian", "Moldovan", "Monegasque", "Mongolian", "Montenegrin", "Moroccan", "Mosotho", "Mozambican", "Namibian", "Nauruan", "Nepalese", "New Zealander", "Ni-Vanuatu", "Nicaraguan", "Nigerian (Nigeria)", "Nigerien (Niger)", "Niuean", "North Korean (Democratic People's Republic of Korea)", "Norwegian", "of the former Yugoslav Republic of Macedonia", "of Trinidad and Tobago", "Omani", "Pakistani", "Palauan ", "Palestinian (Autonomous Palestinian Territories)", "Panamanian", "Papua New Guinean", "Paraguayan", "Peruvian", "Philippine", "Polish", "Portuguese", "Qatari", "Romanian", "Russian", "Rwandan", "Saint Lucian", "Saint Vincentian", "Salvadoran", "Sammarinese", "Samoan", "Sao Tomean", "Saudi Arabian", "Senegalese", "Serbian", "Seychellois ", "Sierra Leonean", "Singaporean", "Slovak", "Slovenian", "Solomon Islander", "Somali", "South African", "South Korean (Republic of Korea)", "South Sudanese", "Spanish", "Sri Lankan", "Sudanese", "Surinamese", "Swazi", "Swedish", "Swiss", "Syrian", "Tajik", "Tanzanian", "Thai", "Timorese", "Togolese", "Tongan", "Tunisian", "Turkish", "Turkmen", "Tuvaluan", "Ugandan ", "Ukrainian", "Uruguayan", "Uzbek", "Vatican", "Venezuelan", "Vietnamese", "Yemeni", "Zambian", "Zimbabwean"],
            "valRegEx": "",
            "descr": "Nationality"
        },
        "onationality": {
            "type": "enum",
            "values": ["Afghan", "Albanian", "Algerian", "American", "Andorran", "Angolan", "Antiguan", "Argentinian", "Armenian", "Australian", "Austrian", "Azerbaijani", "Bahamian", "Bahraini", "Bangladeshi", "Barbadian", "Belgian", "Belizean", "Beninese", "Bhutanese", "Bolivian", "Bosnian-Herzegovinian", "Botswanan", "Brazilian", "British", "Bruneian ", "Bulgarian", "Burkinabe", "Burmese (Myanmar)", "Burundian", "Byelorussian", "Cambodian", "Cameroonian", "Canadian", "Cape Verdean", "Central African", "Chadian", "Chilean", "Chinese", "Colombian", "Comorian", "Congolese (Democratic Republic of the Congo)", "Congolese (Republic of the Congo)", "Costa Rican", "Croatian", "Cuban", "Cypriot", "Czech", "Danish", "Djiboutian", "Dominican (Dominica)", "Dominican (Dominican Republic)", "Dutch", "Ecuadorian", "Egyptian", "Emirati (United Arab Emirates)", "Equatoguinean (Equatorial Guinea)", "Eritrean", "Estonian", "Ethiopian", "Fijian", "Finnish", "French", "Gabonese", "Gambian", "Georgian", "German", "Ghanaian", "Greek", "Greenlandic ", "Grenadian", "Guatemalan", "Guinean (Guinea)", "Guinean (Republic of Guinea-Bissau)", "Guyanese (Guyana)", "Haitian", "Honduran", "Hungarian", "I-kiribati", "Icelandic", "Indian", "Indonesian", "Iranian", "Iraqi", "Irish", "Israeli", "Italian", "Ivorian", "Jamaican", "Japanese", "Jordanian", "kazakhstani", "Kenyan", "Kittsian", "Kosovar", "Kuwaiti", "Kyrgyzstani", "Laotian", "Latvian", "Lebanese", "Liberian", "Libyan", "Liechtensteiner", "Lithuanian", "Luxembourgish", "Malagasy", "Malawian", "Malaysian", "Maldivian", "Malian", "Maltese", "Marshallese", "Mauritanian", "Mauritian", "Mexican", "Micronesian", "Moldovan", "Monegasque", "Mongolian", "Montenegrin", "Moroccan", "Mosotho", "Mozambican", "Namibian", "Nauruan", "Nepalese", "New Zealander", "Ni-Vanuatu", "Nicaraguan", "Nigerian (Nigeria)", "Nigerien (Niger)", "Niuean", "North Korean (Democratic People's Republic of Korea)", "Norwegian", "of the former Yugoslav Republic of Macedonia", "of Trinidad and Tobago", "Omani", "Pakistani", "Palauan ", "Palestinian (Autonomous Palestinian Territories)", "Panamanian", "Papua New Guinean", "Paraguayan", "Peruvian", "Philippine", "Polish", "Portuguese", "Qatari", "Romanian", "Russian", "Rwandan", "Saint Lucian", "Saint Vincentian", "Salvadoran", "Sammarinese", "Samoan", "Sao Tomean", "Saudi Arabian", "Senegalese", "Serbian", "Seychellois ", "Sierra Leonean", "Singaporean", "Slovak", "Slovenian", "Solomon Islander", "Somali", "South African", "South Korean (Republic of Korea)", "South Sudanese", "Spanish", "Sri Lankan", "Sudanese", "Surinamese", "Swazi", "Swedish", "Swiss", "Syrian", "Tajik", "Tanzanian", "Thai", "Timorese", "Togolese", "Tongan", "Tunisian", "Turkish", "Turkmen", "Tuvaluan", "Ugandan ", "Ukrainian", "Uruguayan", "Uzbek", "Vatican", "Venezuelan", "Vietnamese", "Yemeni", "Zambian", "Zimbabwean"],
            "valRegEx": "",
            "descr": "Other Nationalities"
        },

        "date": {
            "type": "date",
            "values": "range,list",
            "validation": {
                "regex": ["^(2(\\d){3})$", "^(2(\\d){3}\\.((0[1-9])|(1[0-2])))", "^(2(\\d){3}\\.((0[1-9])|(1[0-2]))\\.((0[1-9])|((1|2)[0-9])|30|31))$"],
                "message": "The date parameter needs to be properly formatted."
            },
            "descr": "Date",
            "hint": "e.g. 2013.02-2013.05 or 2013,2014 or 2013.02.02-2014.02.14 or 2013,2014"
        },
        "gender": {
            "type": "enum",
            "values": ["male", "female", "other"],
            "valRegEx": "",
            "descr": "Gender"
        },
        "age": {
            "type": "number",
            "values": "range,list",
            "validation": {
                "regex": ["^1[0-9][0-9]$|^[0-9][0-9]$|^[0-9]$"],
                "message": "The age parameter needs to be properly formatted."
            },
            "descr": "Age"
        },
        "work-experience": {
            "type": "number",
            "values": "range",
            "validation": {
                "regex": ["^1[0-9][0-9]$|^[0-9][0-9]$|^[0-9]$"],
                "message": "The work experience parameter needs to be properly formatted."
            },
            "descr": "Work Experience"
        }
    }
    ,
    "operators": {
        "|": "Intersection",
        ",": "Union"
    },
    "special": {
        "groupby": {
            "type": "enum",
            "values": ["format", "country", "language", "mlanguage", "olanguage", "nationality", "onationality", "date", "gender", "age", "work-experience"],
            "valRegEx": "",
            "descr": "Group by"
        },
        "orderby": {
            "type": "enum",
            "values": ["format", "country", "language", "mlanguage", "olanguage", "nationality", "onationality", "date", "gender", "age", "work-experience"],
            "valRegEx": "",
            "descr": "Order by",
            "extra": ["ASC", "DESC"]
        },
        "top": {
            "type": "number",
            "validation": {
                "regex": ["(\\d){3}$"],
                "message": "The 'return top queries' parameter needs to be properly formatted."
            },
            "descr": "Topmost results"
        },
        "bottom": {
            "type": "number",
            "validation": {
                "regex": ["(\\d){3}$"],
                "message": "The 'return bottom queries' parameter needs to be properly formatted."
            },
            "descr": "Bottom results"
        },
    },
    "shortcuts": {
        /*	"percentage":{  //TODO uncomment these when availabele
         "type":"number",
         "valRegEx":"",
         "descr":"Percentage"
         },*/
        "cl-residence-comparison": {
            "type": "number",
            "valRegEx": "",
            "descr": "CL Residence Comparison"
        },
        /*	"magazine-downloads":{
         "type":"number",
         "valRegEx":"",
         "descr":"Magazine Downloads"
         }*/
    },
    "error": {
        "message": {
            "general": "A general connection error has occured, please try again later or contact the Europass team.",
            "empty": "There are parameters missing you need to specify.",
            "validation": "The [[param]] parameter needs to be properly formatted."
        }
    }
};
/*Utility function for downloading the data to the user's client,
 * it should be clicked from an anchor element in order for the download to happen
 * TODO fix json download carriage return insertion after space in explorer
 */
function downloadData(data) {

    var $anchorEL = $(".downloadAnchor");

    var filename = 'stats_export';

    // Data payload
    var finalData = null;
    var suffix;

    if (stats.dataFmt === "csv") {
        suffix = ".csv";
        finalData = 'data:application/csv;charset=utf-8,' + encodeURIComponent(data);
    } else if (stats.dataFmt === "json") {
        suffix = ".json";
        var jsonStr = JSON.stringify(data);
        finalData = 'data:application/json;charset=utf-8,' + encodeURIComponent(jsonStr);
    } else {
        return false;
    }

    if (isIE()) {
        downloadForIE(data, filename, suffix);
    } else {
        //Sets the <a> attributes and triggers the click event on it 
        $anchorEL
                .attr({
                    'download': filename + suffix,
                    'href': finalData,
                    'target': '_blank'
                });
        $anchorEL.get(0).click();
        ;
    }
}
;

function isIE() {
    var myNav = navigator.userAgent.toLowerCase();
    return  myNav.indexOf('msie') != -1 || myNav.indexOf('.net4.0') != -1 || myNav.match(/Trident.*rv\:11\./) ? /* parseInt(myNav.split('msie')[1]) < 10*/true : false;
}

function csvToTable(csv) {

    var table = $("<table>");

    if (isNullOrUndef(csv) || typeof csv !== "string")
        return false;

    var sep = csv.split('\n');

    sep[0] = prettyPrintHeaders(sep[0]); //show headers in a more readable way

    for (var idx = 0; idx < sep.length; idx++) {

        if ($.isEmptyObject(sep[idx]))
            continue;

        var row = $("<tr>");
        var vals = sep[idx].split(",");

        for (var ind = 0; ind < vals.length; ind++) {
            var col = $("<td>", {text: vals[ind]});
            row.append(col);
        }
        table.append(row);
    }
    return table;
}

function downloadForIE(data, filename, extension) {

    var frame = $("#downloadFrame")[0];

    /*	if ( extension === ".csv" ){
     }
     else*/ if (extension === ".json") {
        data = JSON.stringify(data);//.replace(/Records.*Number/g, "Records_Number");;
        extension += ".csv";	 //setting to txt because of some extension filters IE applies.  http://stackoverflow.com/questions/2515791/execcommandsaveas-null-file-csv-is-not-working-in-ie8
    }

    var iframe = frame.contentWindow || frame.contentDocument;

    iframe.document.open("", "replace");
    try {
        iframe.document.charset = "utf-8";
    } catch (e) {
    }
    iframe.document.write(data);
    iframe.document.close();
    iframe.focus();

    frame.contentDocument.execCommand ? frame.contentDocument.execCommand('SaveAs', null, filename + extension) :
            frame.contentWindow.execCommand('SaveAs', null, filename);
}

//Utility function to show headers in more readable manner.
function prettyPrintHeaders(headersStr) {
    var textArray = headersStr.split(",");
    if (!$.isEmptyObject(textArray) && textArray.length > 0 && textArray.toString().split("_").length > 0)
        return textArray.map(
                function (str) {
                    return str.split("_").
                            map(function (string) {
                                return string.charAt(0).toUpperCase() + string.slice(1); //return replaces _ with space and first letters uppercase
                            }).join(' ');
                }).join(',');
}
function isNullOrUndef(param) {
    return typeof param === "undefined" || param === null;
}