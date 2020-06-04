define(
        [
            'jquery',
            'views/forms/FormView',
            'views/forms/attachment/LinkedAttachmentFormView',
            'views/interaction/TypeaheadView',
            'views/interaction/CurrentPositionView',
            'views/interaction/OrganisationUseView',
            'hbs!templates/forms/cv/workexperience',
            'europass/maps/CountryMap',
            'europass/maps/BusinessSectorMap',
            'europass/maps/PositionFMap',
            'europass/maps/PositionMMap',
            'europass/maps/PositionNAMap',
            'europass/http/WindowConfigInstance',
            'ModalFormInteractions'
        ],
        function ($,
                FormView, LinkedAttachmentFormView,
                TypeaheadView,
                CurrentPositionView, OrganisationUseView,
                HtmlTemplate,
                CountryMap, BusinessSectorMap, PositionFMap, PositionMMap, PositionNAMap,
                WindowConfig, ModalFormInteractions
                ) {

            var WorkExperienceFormView = function (options) {
                LinkedAttachmentFormView.apply(this, [options]);
            };

            WorkExperienceFormView.prototype = {
                htmlTemplate: HtmlTemplate

                , enableFunctionalities: function () {
                    //call parent enable functionalities
                    LinkedAttachmentFormView.prototype.enableFunctionalities.call(this);

                    var frm = this.frm;

                    var that = this;
                    //1. ENABLE AUTOCOMPLETE
                    /* Bind Country with AutoComplete and CountryMap */
//				frm.find("div.composite.select2autocomplete[name\*=\"" + ".Country"+"\"]").each ( function( idx, el){
//					var cntAutocomplete = new Select2AutocompleteView({
//						el : $(el),
//						minLength: 1,
//						topN: 10,
//						map: CountryMap
//					});
//					that.addToViewsIndex( cntAutocomplete );
//				});
                    frm.find("div.composite.select[name\*=\"" + ".Country" + "\"]").each(function (idx, el) {
                        var cntAutocomplete = new TypeaheadView({
                            el: $(el),
                            minLength: 0,
                            name: "occupation-country",
                            topN: 10,
                            map: CountryMap
                        });
                        that.addToViewsIndex(cntAutocomplete);
                    });

                    /* Bind Business Sector with AutoComplete and BusinessSectorMap */
//				frm.find("div.composite.select2autocomplete[name\*=\"" + ".Sector"+"\"]").each ( function( idx, el){
//					var sectorAutocomplete = new Select2AutocompleteView({
//						el : $(el),
//						minLength: 1,
//						topN: 10,
//						map: BusinessSectorMap
//					});
//					that.addToViewsIndex( sectorAutocomplete );
//				});		
                    frm.find("div.composite.select[name\*=\"" + ".Sector" + "\"]").each(function (idx, el) {
                        var sectorAutocomplete = new TypeaheadView({
                            el: $(el),
                            minLength: 0,
                            topN: 10,
                            name: "occupation-sector",
                            map: BusinessSectorMap
                        });
                        that.addToViewsIndex(sectorAutocomplete);
                    });

                    /* Change the display of fieldset.more.details in firefox due to width adjustment problems */
                    var sectorFieldset = frm.find('fieldset.more.details');
                    var isFirefox = (WindowConfig.browserName === "MODERN");
                    if (sectorFieldset !== undefined && $(sectorFieldset).css('display') === 'block') {
                        if (isFirefox) {
                            $(sectorFieldset).css('display', 'table-column');
                        }
                    }

                    /* Bind Occupation-Position with AutoComplete and PositionMap according to gender*/
//				var map = this.model.translation().chooseOccupationMap();
//				frm.find("div.composite.select2autocomplete[name\*=\"" + ".Position"+"\"]").each ( function( idx, el){
//					var positionAutocomplete = new Select2AutocompleteView({
//						el : $(el),
//						minLength: 1,
//						topN: 10,
//						map: map
//					});
//					that.addToViewsIndex( positionAutocomplete );
//				});
                    var map = this.model.translation().chooseOccupationMap();
                    frm.find("div.composite.select[name\*=\"" + ".Position" + "\"]").each(function (idx, el) {
                        var positionAutocomplete = new TypeaheadView({
                            el: $(el),
                            minLength: 0,
                            topN: 10,
                            name: "occupation",
                            map: map
                        });
                        that.addToViewsIndex(positionAutocomplete);
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

                    //Bind the Employer Website Hidden Use Code View 
                    frm.find("fieldset[name$=\"Website\"]").each(function (idx, el) {
                        var organisationUseView = new OrganisationUseView({
                            el: $(el)
                        });
                        that.addToViewsIndex(organisationUseView);
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

            WorkExperienceFormView.prototype = $.extend(
                    //true, 
                            {},
                            LinkedAttachmentFormView.prototype,
                            WorkExperienceFormView.prototype
                            );

                    return WorkExperienceFormView;
                }
        );