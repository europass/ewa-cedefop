/**
 * This view is bound to the simple select fields (i.e. Telephone and Email).
 * The View is configured to receive:
 * 1. the el element (div.composite[name\*=\"" + ".Telephone"+"\"]")
 * 2. the map containing the translation of the codes
 * 
 * This is initiated during PersonalInfoFormView.enableFunctionalities();
 *  
 */
define(
        [
            'jquery',
            'underscore',
            'backbone'
        ],
        function ($, _, Backbone) {

            var SelectFieldView = Backbone.View.extend({
                customSelect: null
                , selectElement: null
                , events: {
                    "change :input:not(button)[name$=\"Code\"]": "updateLabel",
                    "change.fs": "toggleControlSpans",
                    "click :not(clear)": "handleMenu",
                    "click ul li": "setValue",
                    "click .clear": "clearValue",
                    "valueSelected": "setValue"
                }
                , onClose: function () {
                    delete this.map;
//				this.fancySelect.trigger('disable.fs');
                }
                , initialize: function (options) {
                    this.customSelect = this.$el;
                    this.selectElem = this.customSelect.parent().find('select');
                    var selectedOption = this.selectElem.find('option:selected');
                    if (this.selectElem.val() !== '') {
                        this.customSelect.children('.init').addClass('selected')
                                .attr('data-value', this.selectElem.val())
                                .html(selectedOption.text());
                    }

                    // Convert HTML Elements into DOM
                    var that = this;
                    if (this.customSelect.is('ul')) {
                        this.customSelect.each(function (idx, el) {
                            $(el).find('li:not(.init)').each(function (idy, subEl) {
                                var list = $(subEl).parent();
                                var string = $(subEl).text();
                                $(subEl).text('');
                                var html = $.parseHTML('<li>' + string + '</li>');
                                html = $(html);
                                var code = $(subEl).attr('data-value');
                                $(subEl).html('');
                                html.attr('data-value', code);
                                $(subEl).remove();
                                list.append(html);
                            });
                        });
                    }

                    this.map = options.map;
                    if (!options.allowClear || this.customSelect.hasClass('no_clear')) {
                        this.allowClear = false;
                    } else {
                        this.allowClear = true;
                    }

                    if (this.allowClear) {
                        var clearSpan = $("<span class=\"fancySelect clear hidden\"></span>");
                        this.customSelect.append(clearSpan);
                        // When loading the field for the first time
                        this.toggleControlSpans();
                    }

                    // Close Action for ALL Custom Select Elements
                    $(document).mouseup(function (e) {
                        var container = $("ul.custom_select.active");
                        // if the target of the click isn't the container nor a descendant of the container
                        if (!container.is(e.target) && container.has(e.target).length === 0) {
                            container.removeClass('active');
                            container.find('.arrow').removeClass('flip');
                        }
                    });
                    
                    
                    var $selection;
                    var chars = ''; 
                    var lastKeyTime = Date.now();

                    $(".custom_select_wrapper").blur(function() {
                        var container = $("ul.custom_select");
                        if (container) {
                            container.removeClass('active');
                            container.removeClass('focused');
                            container.find('.arrow').removeClass('flip');
                        }
                    })
                    
                    $(".custom_select_wrapper").focus(function() {
                        $(this).find("ul.custom_select").addClass("focused");
                        chars = '';
                        $selection = undefined;
                    });
                    
                    $(".custom_select_wrapper").keydown(function(e) {
                        // check for arrow down keycode
                        if (e.which === 40) {
                            $("ul.custom_select.focused").addClass("active");
                            return false;
                        }
                    });
                    
                    $(".custom_select_wrapper").keypress(function(e) {
                        var $container = $("ul.custom_select.focused");
                        $container.find('ul li').css('color', '');
                        var key = '';
                        const currentTime = Date.now();
                        // accept only number keycodes
                        if ((e.which >= 48 && e.which <= 57) || (e.which >= 96 && e.which <= 105)) {
                            key = String.fromCharCode(e.which);
                        }
                        
                        // 1 second interval between key presses
                        if (currentTime - lastKeyTime <= 1000) {
                            chars += key;
                        } else {
                            // EPAS-2170
                            if (key === "0") {
                                chars = ''; 
                            } else {
                                chars = key;
                            }
                        }
                        
                        if (chars !== '') { 
                            var regex = new RegExp('^' + chars, 'i');
                            $selection = $container.find('ul li').filter(function() {
                                return regex.test($(this).data("value"));
                            }).first();
                            $selection.css('color', '#c55200');
                        } 
                        
                        if (e.which === 13 && $selection) {
                            // check for "ENTER" keycode
                            chars = '';
                            //timer = undefined;
                            $selection.trigger("valueSelected");
                        }
                        lastKeyTime = currentTime;
                    });
                }
                , handleMenu: function (event) {
                    if ($(event.target).is('li.init') || $(event.target).is('.arrow')) {
                        this.customSelect.toggleClass('active');
                        this.customSelect.find('.arrow').toggleClass('flip');
                    }
                }
                , setValue: function (event) {
                    var source = $(event.target);
                    if ($(event.target).is('span')) {
                        source = $(event.target).closest('li');
                    }
                    var clearSpan = $("<span class=\"fancySelect clear\"></span>");
                    this.customSelect.removeClass('active');
                    if (this.allowClear) {
                        this.customSelect.append(clearSpan);
                        this.customSelect.find('.arrow').addClass('hidden');
                    }
                    this.customSelect.find('.arrow').removeClass('flip');
                    this.customSelect.children('.init').addClass('selected').html(source.html());
                    this.selectElem.val(source.attr('data-value'));

                    this.customSelect.trigger('europass:custom_select:type');
                    if (this.selectElem.is('.PrintingPreferences.Date') && !this.selectElem.is('.Certificate.PrintingPreferences.Date')) {
                        this.selectElem.trigger('changed:dateFormatSelect');
                    } else if (this.selectElem.is('.PrintingPreferences.Period') || this.selectElem.is('.Certificate.PrintingPreferences.Date')) {
                        this.selectElem.trigger('changed:datePeriodSelect');
                    }
                }
                , clearValue: function (event) {
                    this.selectElem.val('');

                    var firstSelectOption = this.selectElem.find('option:selected');
                    this.customSelect.find('li.init').html(firstSelectOption.text());
                    this.customSelect.find('li.init').removeClass('selected');

                    var parent = $(event.target).closest(".composite");
                    var label = parent.find(":input:not(button)[name$=\"Label\"]");
                    label.val('');

                    this.customSelect.find('.arrow').removeClass('hidden');
                    this.customSelect.find('.clear').addClass('hidden');
                }
                , toggleControlSpans: function () {
                    if (!this.allowClear)
                        return;

                    var arrowSpan = this.$el.find('.arrow');
                    var clearSpan = this.$el.find('.clear');
                    var isEmptyChoice = (this.selectElem.val() === "");
                    if (isEmptyChoice) {
                        if (arrowSpan.hasClass("hidden"))
                            arrowSpan.removeClass("hidden");
                        if (!clearSpan.hasClass("hidden"))
                            clearSpan.addClass("hidden");
                    } else {
                        if (!arrowSpan.hasClass("hidden"))
                            arrowSpan.addClass("hidden");
                        if (clearSpan.hasClass("hidden"))
                            clearSpan.removeClass("hidden");
                    }

                }
                , updateLabel: function (event) {

                    if (_.isUndefined(this.map) || _.isEmpty(this.map))
                        return;

                    var obj = $(event.target);
                    var code = obj.val();
                    //First find the parent with class composite
                    //Then get the sibling with label
                    var parent = obj.closest(".composite");
                    //This is necessary because the code might be wrapped in more that one divs, as does the DefaultsFormView
                    var label = parent.find(":input:not(button)[name$=\"Label\"]");

                    //from obj find the hidden label field and set its value according to the map.
                    var value = this.map.get(code);
                    if (value !== undefined && value !== null && value !== "") {
                        label.val(value);
                    } else {
                        label.val("");
                    }
                }
            });

            return SelectFieldView;
        }
);