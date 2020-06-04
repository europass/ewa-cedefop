define(
        [
            'jquery',
            'views/forms/FormView',
            'views/forms/attachment/LinkedAttachmentFormView',
            'views/interaction/TypeaheadView',
            'views/interaction/CurrentPositionView',
            'hbs!templates/forms/cv/education',
            'europass/maps/CountryMap',
            'europass/maps/EducationalLevelMap',
            'europass/maps/EducationalFieldMap',
            'ModalFormInteractions'
        ],
        function ($, FormView, LinkedAttachmentFormView,
                TypeaheadView,
                CurrentPositionView,
                HtmlTemplate,
                CountryMap, EducationalLevelMap, EducationalFieldMap, ModalFormInteractions) {

            var EducationFormView = function (options) {
                LinkedAttachmentFormView.apply(this, [options]);
            };

            EducationFormView.prototype = {
                htmlTemplate: HtmlTemplate

                , enableFunctionalities: function () {
                    //call parent enable functionalities
                    LinkedAttachmentFormView.prototype.enableFunctionalities.call(this);

                    var frm = this.$el;

                    var that = this;
                    //1. ENABLE AUTOCOMPLETE
                    /* Bind Country with AutoComplete and CountryMap */
                    frm.find("div.composite.select[name\*=\"" + ".Country" + "\"]").each(function (idx, el) {
                        var cntAutocomplete = new TypeaheadView({
                            el: $(el),
                            minLength: 0,
                            topN: 10,
                            name: "education-country",
                            map: CountryMap
                        });
                        that.addToViewsIndex(cntAutocomplete);
                    });

                    /* Bind Educational LEvel with AutoComplete and EducationalLevelMap */
                    frm.find("div.composite.select[name\*=\"" + ".Level" + "\"]").each(function (idx, el) {
                        var educLevelAutocomplete = new TypeaheadView({
                            el: $(el),
                            minLength: 0,
                            topN: 10,
                            name: "education-level",
                            map: EducationalLevelMap
                        });
                        that.addToViewsIndex(educLevelAutocomplete);
                    });


                    /* Bind Educational Field with AutoComplete and EducationalFieldMap */
                    frm.find("div.composite.select[name\*=\"" + ".Field" + "\"]").each(function (idx, el) {
                        var educFieldAutocomplete = new TypeaheadView({
                            el: $(el),
                            minLength: 0,
                            topN: 10,
                            name: "education-field",
                            map: EducationalFieldMap
                        });
                        that.addToViewsIndex(educFieldAutocomplete);
                    });

                    //Bind the Current Position View 
                    frm.find("fieldset.Dates[name$=\"Period\"]").each(function (idx, el) {
                        var days = frm.find("ul.Day");
                        var months = frm.find("ul.Month");
                        var years = frm.find("ul.Year");

                        months.each(function (id, month) {
                            $(month).on('click', function (e) {
                                if ($(this).find('li.selected').length) {
                                    $(this).find('li.selected')[0].innerHTML;

                                    var selectedMonth = $(this).find('li.selected')[0].innerHTML;
                                    var selectedYear = $(years[id]).find('li.selected')[0] ? $(years[id]).find('li.selected')[0].innerHTML : (new Date()).getFullYear();
                                    var selectedDay = $(days[id]).find('li.selected')[0] ? $(days[id]).find('li.selected')[0].innerHTML : 0;

                                    var availableDays = new Date(selectedYear, selectedMonth, 0).getDate();
                                    var displayedDays = $(days[id]).find('li:not(.init)');
                                    var daysList = displayedDays.parent();

                                    if (parseInt(selectedDay) > availableDays) {
                                        var clearButton = daysList.parent().find('span.clear:not(.hidden)')[0];
                                        $(clearButton).trigger('click');
                                    }
                                    displayedDays.remove();

                                    for (var i = 1; i <= availableDays; i++) {
                                        daysList.append("<li data-value=\"" + (i) + "\">" + (i) + "</li>");
                                    }

                                }
                            });
                        });

                        years.each(function (id, year) {
                            $(year).on('click', function (e) {
                                if ($(this).find('li.selected').length) {
                                    $(this).find('li.selected')[0].innerHTML;
                                    var selectedYear = $(this).find('li.selected')[0].innerHTML;
                                    var selectedMonth = $(months[id]).find('li.selected')[0] ? $(months[id]).find('li.selected')[0].innerHTML : 1;
                                    var selectedDay = $(days[id]).find('li.selected')[0] ? $(days[id]).find('li.selected')[0].innerHTML : 0;

                                    var availableDays = new Date(selectedYear, selectedMonth, 0).getDate();
                                    var displayedDays = $(days[id]).find('li:not(.init)');
                                    var daysList = displayedDays.parent();

                                    if (parseInt(selectedDay) > availableDays) {
                                        var clearButton = daysList.parent().find('span.clear:not(.hidden)')[0];
                                        $(clearButton).trigger('click');
                                    }
                                    displayedDays.remove();

                                    for (var i = 1; i <= availableDays; i++) {
                                        daysList.append("<li data-value=\"" + (i) + "\">" + (i) + "</li>");
                                    }

                                }
                            });
                        });
                        var CurrentPosView = new CurrentPositionView({
                            el: $(el)
                        });
                        that.addToViewsIndex(CurrentPosView);
                    });
                    //call parent FINALLY enable functionalities
                    LinkedAttachmentFormView.prototype.finallyEnableFunctionalities.call(this);

                }//end enableFunctionalities

                /**
                 * @Override
                 */
                , submitted: function (event, globalDateFormatUpdated) {
                    this.$el.trigger("europass:waiting:indicator:show");

                    LinkedAttachmentFormView.prototype.doSubmit.call(this);

                    FormView.prototype.submitted.apply(this, [event, globalDateFormatUpdated]);
                }
                
                , modalClosed: function (event, globalDateFormatUpdated) {
                    if (LinkedAttachmentFormView.prototype.doModalClosed.call(this)) {
                        ModalFormInteractions.confirmSaveSection(event, this.frm.attr("id"));
                    } else {
                        FormView.prototype.modalClosed.apply(this, [event, globalDateFormatUpdated]);
                    }
                }

                /**
                 * @Override
                 */
                , cancelled: function (event) {
                    FormView.prototype.cancelled.apply(this, [event]);
                }
            };

            EducationFormView.prototype = $.extend(
                    //true, 
                            {},
                            LinkedAttachmentFormView.prototype,
                            EducationFormView.prototype
                            );

                    return EducationFormView;
                }
        );