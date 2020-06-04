define(
        [
            'jquery',
            'underscore',
            'backbone',
//		'handlebars',
            'typeahead',
            'europass/structures/MapWrapper'//'europass/TabletInteractionsView'
        ],
        function ($, _, Backbone, TypeAhead, MapWrapper) {//, TabletInteractionsView
            var TypeaheadView = Backbone.View.extend({
                //el is the parent element			
                events: {
                    "typeahead:opened": "hidePlaceholder",
//				"typeahead:blurred" : "showPlaceholder",
                    "blur :input.typeahead": "showPlaceholder",
//				"typeahead:closed"  : "checkPersonalDataTreatment",
                    "typeahead:select": "checkPersonalDataTreatment",
                    "click span.clear": "clearInput",
                    "europass:clear:typeahead": "clearInput",
                    "click .twitter-typeahead": "personalInfoOption",
                    "europass:personal_data_treatment_select:typeahead": "personalDataTreatmentSelect"
                            //"click :not(.esco-alt-selected) > a.alt-esco-label-link": "selectEscoAlternativeOccupation",
                            //"click .esco-alt-selected > a.alt-esco-label-link": "unselectEscoAlternativeOccupation"
                },
                escoEnabled: true,

                personalInfoOption: function (event) {
                    var dataset = $(event.target).parent().find('.tt-dataset');
                    dataset.children().each(function (idx, elem) {
                        if ($(elem).text() === 'Trattamento dei dati personali' || $(elem).text() === 'Klauzula o przetwarzaniu danych osobowych') {
                            var data = $(elem);
                            data.css('color', '#e25500');
                            dataset.prepend(data);
                        }
                    });
                },

                personalDataTreatmentSelect: function (event) {
                    var _that = this;
                    var targetEl = event ? event.target : window.event.srcElement;
                    var dataset = $(targetEl).parent().find('.tt-dataset');
                    dataset.children().each(function (idx, elem) {
                        if ($(elem).text() === 'Trattamento dei dati personali' || $(elem).text() === 'Klauzula o przetwarzaniu danych osobowych') {
                            $(elem).click();
                            _that.showPlaceholder();
                        }
                    });
                },
                personalInfoPreselect: function (isDataPersonalEnabled) {
                    if (isDataPersonalEnabled) {
                        var _that = this;
                        setTimeout(function () {
                            $(_that.typeahead).focus();
                            setTimeout(function () {
                                $(_that.typeahead).trigger("europass:personal_data_treatment_select:typeahead");
                            }, 300);
                        }, 600);
                    }
                },

                initialModalValues: null

                , onClose: function () {
                    this.typeahead.typeahead('destroy');
                    delete this.map;
                }
                , initialize: function (options) {

                    this.el = $(options.el);
                    var input = $(this.el.find("input.typeahead"));
                    this.placeholder = input.attr("placeholder");

                    var visuals = {
                        hint: true,
                        highlight: true,
                        minLength: options.minLength
                    };

                    if (this.configured(options)) {
                        this.typeahead = input.typeahead(visuals, {
                            name: !_.isUndefined(options.name) ? options.name : "choices",
                            displayKey: 'value',
                            limit: this.mapValues.length,
                            source: this.startsWithAgainstMap(this.mapValues)
                        });

                        if (!this.el.hasClass('occupation')) {
                            this.updateInputCodes();
                        }

                        // Construct the list when nothing matches the inserted value
                        this.noMatches = [];
                        var that = this;
                        $.each(this.mapValues, function (i, str) {
                            that.noMatches.push({value: str});
                        });

                        // Used for the clear span handling and referencing
                        this.parent = $(input.parent(".twitter-typeahead"));

                        // Append clear span
                        var clearSpan = "<span class=\"typeahead clear " + (this.typeahead.val() === "" ? "hidden" : "") + "\">";
                        this.parent.append(clearSpan);

                        // Used for refering the element that holds the code of the map value
                        this.inputCodeEl = $(this.parent.closest(".composite")).find("[name$='.Code']");
                    }
                    input.attr('spellcheck', 'true');
                    this.personalInfoPreselect(options['isPersonalDataTreatment'], input);
                }

                , configured: function (options) {

                    this.map = options.map;

                    this.isMultipliable = !_.isUndefined(options.multipliable) ? options.multipliable : false;
                    this.isExclusive = !_.isUndefined(options.exclusive) ? options.exclusive : false;
                    this.global = !_.isUndefined(options.global) ? options.global : false;

                    var mapIsMapWrapper = (this.map instanceof MapWrapper);

                    if ((this.global && (!this.isExclusive || !mapIsMapWrapper || _.isUndefined(options.globalExcludedValues))) ||
                            (this.isExclusive && !mapIsMapWrapper))
                        return false;

                    if (this.isExclusive) {

                        this.fullMap = this.map.originalMap;
                        this.mapValues = this.map.map.values;

                        if (!_.isUndefined(options.globalExcludedValues))
                            this.globalExcludedValues = options.globalExcludedValues;

                    } else {

                        this.fullMap = this.map;
                        this.mapValues = this.map.values;
                    }
                    return true;
                }

                , updateInputCodes: function () {

                    var inputs;
                    if (this.isMultipliable) {
                        inputs = $(this.el.parent(".multipliable").find("input.typeahead.tt-input"));
                    } else {
                        inputs = $(this.el.find("input.typeahead.tt-input"));
                    }

                    var that = this;
                    inputs.each(function (idx, el) {

                        var current =
                                ($(el).val() === "" ?
                                        ($(el).siblings("pre").html() === "" ?
                                                $(el).attr("value") :
                                                $(el).siblings("pre").html()
                                                ) :
                                        $(el).val()
                                        );

                        //var escoArea = $(el).parentsUntil(".composite").siblings(".esco-description-area");
                        //$(escoArea).html("");

                        var inputCode = $(el).parentsUntil(".composite").siblings("input[name$='Code']");

                        var key = that.fullMap.reverseGet(current);

                        if (key === null) {
                            $(inputCode).val("");
                            //$(escoArea).html("");
                            return;
                        }
                        if (that.map.get(key) === current) {
                            $(inputCode).val(key);
                            //if (that.escoEnabled && $(escoArea).length > 0) {
                            //    var escoUrl = "https://ec.europa.eu/esco/api/resource/occupation?uri=http://data.europa.eu/esco/occupation/" + key + "&language=" + ewaLocale;
                            //    that.escoGetDescription(escoUrl, escoArea, current);
                            //}
                        }

                    });
                }

//			, escoGetDescription: function (url, escoAreaEl, currentLabel) {
//
//				$.ajax({
//                    url : url,
//                    type : "get",
//                    success: function( data ) {
//                        $(escoAreaEl).html("<p>" + data.description.en.literal + "</p>");
//                        var escoSiteUri = "https://ec.europa.eu/esco/portal/occupation?uri=" + data.uri + "&conceptLanguage=" + ewaLocale + "&full=true";
//						var alternativeLabelsObj = data.alternativeLabel;
//                        var alternativeLabelsByLocale = alternativeLabelsObj[ewaLocale];
//
//                        if (typeof alternativeLabelsByLocale !== "undefined") {
//                            if (alternativeLabelsByLocale.length > 0) {
//                                $(escoAreaEl).append("<div id='escoAreaAltLabels' class='esco-alternative-wrapper' data-original-value='" + currentLabel + "'></div>");
//                                $("#escoAreaAltLabels").append("<span class='esco-alternative-label'>Alternative labels:</span>");
//                                for (var i=0; i < alternativeLabelsByLocale.length; i++) {
//                                    var altLabel = alternativeLabelsByLocale[i].charAt(0).toUpperCase() + alternativeLabelsByLocale[i].slice(1);
//                                    $("#escoAreaAltLabels").append("<span class='alt-esco-label-i button'><a href='#' class='alt-esco-label-link'>"+ altLabel + "</a></span>");
//                                }
//                            }
//						}
//
//                        $(escoAreaEl).append("<a class='esco-more button' href='" + escoSiteUri + "' target='_blank'>Learn more about Esco</a>");
//                    },
//                    error: function( data ) { },
//                    always: function() { }
//                });
//            }
//
//            , selectEscoAlternativeOccupation: function (event) {
//
//                $('span.alt-esco-label-i').removeClass('esco-alt-selected');
//
//			    var elem = event.currentTarget;
//                $(elem).closest('span.alt-esco-label-i').toggleClass('esco-alt-selected');
//
//                this.typeahead.typeahead('val', $(elem).text());
//            }
//
//            ,unselectEscoAlternativeOccupation: function (event) {
//
//                var elem = event.currentTarget;
//                $(elem).closest('span.alt-esco-label-i').removeClass('esco-alt-selected');
//
//                this.typeahead.typeahead('val', $(elem).closest('#escoAreaAltLabels').data('originalValue'));
//            }

                , updateByCurrentValues: function () {

                    var currentValues = this.currentValues();

                    // simple, if is not global get current values and populate against full map
                    if (this.isExclusive && !this.global)
                        return $(this.fullMap.values).not(currentValues).get();

                    if (this.isExclusive && this.global) {

                        if (!this.isMultipliable) {

                            // Will apply only on the first choose of teh list to strip out the current value so it is available later
                            this.globalExcludedValues = $(this.globalExcludedValues).not(currentValues).get();

                            // exclude all but currentValue ( = currentValues )
                            return $(this.fullMap.values).not(this.globalExcludedValues).not(currentValues).get();

                        }

                        if (this.globalExcludedValues.length === 0) {
                            // exclude all but currentValue ( = currentValues )
                            return $(this.fullMap.values).not(this.globalExcludedValues).not(currentValues).get();
                        }

                        // Exclude global values
                        var fullGlobalExcluded = $(this.fullMap.values).not(this.globalExcludedValues).get();

                        //Exclude map values
                        var modalCurrentValues = $(fullGlobalExcluded).not(this.mapValues).get();

                        // flag used to check if the multipliable has changed
                        var mulitpliableChanged = !($(modalCurrentValues).not(currentValues).length === 0 && $(currentValues).not(modalCurrentValues).length === 0);

                        if (mulitpliableChanged) {

                            return $(this.fullMap.values).not(this.globalExcludedValues).not(currentValues).get();
                        }
                    }

                    return this.mapValues;
                }

                , updateMapValues: function (event, action, mapObject) {}

                /**
                 * Utility function that collects and stores the input values to an array
                 */
                , currentValues: function () {

                    var valuesArray = new Array();

                    var globalParent = $(this.global ? this.el.closest(".modalform") : this.el.parent(".multipliable"));

                    globalParent.find("input.typeahead.tt-input").each(function (idx, el) {

                        var input = $(el);

                        var current =
                                (input.val() === "" ?
                                        (input.siblings("pre").html() === "" ?
                                                input.attr("value") :
                                                input.siblings("pre").html()
                                                ) :
                                        input.val()
                                        );

                        if (current !== undefined && current !== "" && !_.contains(valuesArray, current)) {
                            valuesArray.push(current);
                        }
                    });
                    return valuesArray;
                }

                , startsWithAgainstMap: function (strs) {
                    var that = this;
                    return function findMatches(q, cb) {
                        var matches;

                        // an array that will be populated with substring matches
                        matches = [];

                        // if exclusion is enabled, exclude the current input values
                        var values = strs;
                        if (that.isExclusive) {
                            values = that.updateByCurrentValues();
                        }

                        // iterate through the pool of strings and for any string that
                        // contains the substring `q`, add it to the `matches` array
                        $.each(values, function (i, str) {

                            // Check against current values, upper cases and lower cases to cover all potential matches 
                            var strUpperCase = str.toUpperCase();
                            var strLowerCase = str.toLowerCase();

                            var qUpperCase = q.toUpperCase();
                            var qLowerCase = q.toLowerCase();

                            var wordStartQ = ' ' + q;
                            var wordStartQUpperCase = (' ' + q).toUpperCase();
                            var wordStartQLowerCase = (' ' + q).toLowerCase();

                            var wordSlashQ = '/' + q;
                            var wordSlashQUpperCase = ('/' + q).toUpperCase();
                            var wordSlashQLowerCase = ('/' + q).toLowerCase();

                            var strNormalIndexOfq = str.indexOf(q);
                            var strUpperIndexOfq = strUpperCase.indexOf(qUpperCase);
                            var strLowerIndexOfq = strLowerCase.indexOf(qLowerCase);

                            // Case 1: taxonomy value starts with str

                            if (strNormalIndexOfq === 0 || strUpperIndexOfq === 0 || strLowerIndexOfq === 0) {
                                matches.push({value: str});
                            } else {	// Case 2: taxonomy value inside str as a word or a word with preceding / (case Eire/Ireland)

                                var strNormalIndexOfWordQ = str.indexOf(wordStartQ) > str.indexOf(wordSlashQ) ? str.indexOf(wordStartQ) : str.indexOf(wordSlashQ);

                                var strUpperIndexOfWordQ =
                                        (strUpperCase.indexOf(wordStartQUpperCase) > strUpperCase.indexOf(wordSlashQUpperCase) ?
                                                strUpperCase.indexOf(wordStartQUpperCase) :
                                                strUpperCase.indexOf(wordSlashQUpperCase));

                                var strLowerIndexOfWordQ =
                                        (strLowerCase.indexOf(wordStartQLowerCase) > strLowerCase.indexOf(wordSlashQLowerCase) ?
                                                strLowerCase.indexOf(wordStartQLowerCase) :
                                                strLowerCase.indexOf(wordSlashQLowerCase));

                                var realIndex = (strNormalIndexOfWordQ > 0 ? strNormalIndexOfWordQ :
                                        (strUpperIndexOfWordQ > 0 ? strUpperIndexOfWordQ :
                                                (strLowerIndexOfWordQ > 0 ? strLowerIndexOfWordQ : -1)
                                                )
                                        );
                                if (realIndex > 0) {
                                    matches.push({value: str});
                                }
                            }
                        });

                        // if nothing found show the full list
                        if (matches.length === 0) {
                            matches = this.noMatches;
                        }
                        cb(matches);
                    };
                }

                , showPlaceholder: function (event) {

                    //console.log('input id: ' + this.typeahead.attr('id'));
                    //console.log('input value: ' + this.typeahead.attr('value'));
                    if (this.isEmptyTypeahead()) {
                        this.typeahead.attr("placeholder", this.placeholder);
                        this.typeahead.parentsUntil(".composite").siblings("input[type='hidden']").val("");

                        if (window.config.browserName === "MSIE 9.0" && this.typeahead.val() === "") {

                            var placeHolder = this.typeahead.attr("placeholder");

                            this.typeahead.val(placeHolder);
                            this.typeahead.addClass("empty");
                        }

                        // hide clear span
                        this.parent.find("span.typeahead.clear").addClass("hidden");
                    } else {
                        this.focusPlaceholderAndUpdateCodes();
                    }
                    // EPAS-268
                    if (this.inputCodeEl.hasClass("OpeningSalutation")) {
                        //console.log('hidden input value: ' + this.inputCodeEl.val());						
                        this.el.trigger("europass:salutation:check", this.inputCodeEl);
                    }
                }

                , hidePlaceholder: function () {
//				if(window.config.browserName == "MSIE 9.0" && this.typeahead.is(".empty")){
//					this.typeahead.removeClass("empty");
//				}
                    this.typeahead.attr("placeholder", "");
                    this.parent.find("span.typeahead.clear").removeClass("hidden");
                }

                , clearInput: function (event) {
                    if (!this.isEmptyTypeahead()) {
                        var clear = true;
                        this.emptyTypeaheadValue();
                        this.updateInputCodes();
                        this.checkPersonalDataTreatment(event, clear);
                        this.typeahead.trigger("blur");
                    }
                }

                , focusPlaceholderAndUpdateCodes: function () {
                    this.hidePlaceholder();
                    if (!this.el.hasClass('occupation') && !this.el.hasClass('occupation-related')) {
                        this.updateInputCodes();
                    }
                }

                //EWA-1664 / pgia: patch for Dati Personali in Additional Info
                , checkPersonalDataTreatment: function (event, clear) {

                    this.isTablet = (/(iPhone|iPod|iPad)/.test(navigator.userAgent) || (/Android/.test(navigator.userAgent)) || (/BlackBerry/.test(navigator.userAgent)));
                    if (this.isTablet) {
                        this.focusPlaceholderAndUpdateCodes();
                    }

                    var el = this.el;
                    if (!_.isUndefined(el) && !_.isNull(el) && !_.isEmpty(el)) {
                        if (el.hasClass('Achievement')) {
                            this.updateInputCodes();
                            this.typeahead.trigger("europass.personal.data.treatment", clear);
                        }
                        if (el.hasClass('occupation') || el.hasClass('occupation-related')) {
                            this.updateInputCodes();
                        }
                    }

                    /*
                     var el = this.el;
                     if ( !Utils.isUndefined(this.el) ){
                     
                     if (el.hasClass('Achievement')){
                     var parentEl = el.closest('fieldset');
                     if (!Utils.isUndefined(parentEl)){
                     parentEl.trigger("europass.personal.data.treatment");
                     }
                     }
                     }*/
                    //this.typeahead.trigger("europass.personal.data.treatment");
                }

                , styleAboveInput: function () {
                    if (!this.el.hasClass("above-input"))
                        return;

                    var dropdown = $(this.typeahead.siblings(".tt-dropdown-menu"));

                    var dropDownHeightVal = Number(dropdown.css("height").substr(0, dropdown.css("height").length - 2));
                    var dropDownMaxHeightVal = Number(dropdown.css("max-height").substr(0, dropdown.css("max-height").length - 2));

                    var dropDownActualHeightVal = dropDownMaxHeightVal > dropDownHeightVal ? dropDownHeightVal : dropDownMaxHeightVal;

                    dropdown.css("top", "-" + (dropDownActualHeightVal - 2) + "px");
                }

                , isEmptyTypeahead: function () {
                    if (this.typeahead.typeahead("val") === "") {
//					if(this.parent.find("pre").html() === "")
//						return true;
//					else
//						return false;
                        return true;
                    }
                    return false;
                }

                , emptyTypeaheadValue: function () {
                    this.typeahead.typeahead("val", "");
                    this.parent.find("pre").html("");
//				this.inputCodeEl.attr("value","");
                }
            });
            return TypeaheadView;
        }
);