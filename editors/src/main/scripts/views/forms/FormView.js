define(
        [
            'jquery',
            'jqueryui',
            'underscore',
            'europass/backbone/EWABackboneView',
            'europass/structures/PreferencesSchema',
            'hbs!templates/main/toolbar/htmloptions',
            'hbs!templates/forms/formbuttons',
//	    'views/interaction/CompoundMultiFieldView',
            'views/interaction/FixedValueListInputView',
            'views/interaction/AutoCompleteListInputView',
            'views/interaction/SingleAutoCompleteListView',
            'views/interaction/SingleInputView',
            'views/interaction/RichTextEditorView2',
            'views/interaction/TextareaView',
            'i18n!localization/nls/Notification',
            'Utils',
            'i18n!localization/nls/DocumentLabel',
            'europass/maps/LanguageShortLevelMap',
            'europass/maps/CertificateLevelMap',
            'hbs!templates/forms/selectSelectionFormat',
            'europass/http/WindowConfigInstance',
            'views/interaction/SelectFieldView',
            'ModalFormInteractions'
        ],
        function ($, jqueryui, _, EWABackboneView, PreferencesSchema,
                HtmlOptionsTpl, FormButtonsTpl,
//		CompoundMultiFieldView, 
                FixedValueListInputView,
                AutoCompleteListInputView,
                SingleAutoCompleteListView,
                SingleInputView,
                RichTextEditorView2,
                TextareaView,
                Notification, Utils,
                DocumentLabel, LanguageShortLevelMap, CertificateLevelMap, SelectionFormatTpl, WindowConfig, SelectFieldView, ModalFormInteractions) {

            var FormView = Backbone.EWAView.extend({
                isRendered: false,
                section: null,
                root: null,

                errorOnOpeningTxt: "<p>A problem has occured while trying to open this modal window. Please close it and try to re-open it. If the problem persist, please contact the contact the <a href=\"http://europass.cedefop.europa.eu/en/contact\" target=\"_blank\">Europass team</a> reporting the problem.</p>",

                events: {
                    "europass:form:prefs_changed :button.submit:not(.disabled)": "submitted",
                    "europass:cancel:save": "cancelled",
                    "click :button.cancel": "cancelled",
                    "europass:modal:check:changes": "modalClosed",
                    "focusin input.formfield": "scrollField"
                },
                onClose: function () {
                    this.emptyViewsIndex();
                    if ($.isFunction(this.onModalClose)) {
                        this.onModalClose();
                    }
                }
                , onInit: function (options) {

                    this.root = options.root;
                    this.section = options.section;

                    this.emptyJSON = {};
                    this.emptyJSON[this.section] = {};

                    this.emptyArray = {};
                    this.emptyArray[this.section] = [];

                    this.emptyStringJSON = {};
                    this.emptyStringJSON[this.section] = "";

                    this.errorOnOpeningTxt = Notification["error.modalform.opening"];

                    this.frm = this.$el.find("form");

                    this.exclusionMap = options.exclusionMap;

                    Backbone.EWAView.prototype.onInit.apply(this, [options]);
                },
                adjustContext: function (context, index, subsection) {
                    return context;
                },
                setOrigin: function (origin) {
                    this.origin = origin;
                },
                setPersonalDataTreatment: function (isPersonalDataTreatment) {
                    this.isPersonalDataTreatment = isPersonalDataTreatment;
                },
                render: function (index, subsection) {
                    $("body").addClass('modal_overlay_open');
                    var context = {};
                    $.extend(true, context, this.model.attributes);

                    context = this.adjustContext(context, index, subsection);

                    if (index !== undefined && index !== null && index !== "") {
                        context.Idx = index;
                        context.subsection = this.section.substr(this.section.lastIndexOf(".") + 1);
                        var matchArray = Utils.matchArray(context.subsection);
                        if (matchArray != null) {
                            context.SubIdx = matchArray[3];
                        }
                    }
                    if (subsection !== undefined && subsection !== null && subsection !== "") {
                        context.subsection = subsection;
                    }
                    context.Preferences = this.model.getPreferences();

                    if (_.isFunction(this.template) === false) {
                        this.frm.html("<fieldset>" + this.errorOnOpeningTxt + "</fieldset>");
                        return this;
                    }
                    var html = this.template(context);
                    this.frm.html(html);

                    // The first time it is rendered, this boolean will
                    // be set to true.
                    if (this.isRendered === false && this.$el.find(".side").find("menu").length < 1) {
                        this.isRendered = true;
                        this.appendMenuButtons();
                    }

                    this.prepareViewsIndex();
                    this.prepareValidationsIndex();

                    return this;
                }
                /**
                 * Adds the menu buttons
                 */
                , appendMenuButtons: function () {
                    //Form Buttons Template
                    var c = this.section.substr(this.section.lastIndexOf(".") + 1);
                    var className = c.substr(0, c.indexOf("["));
                    if (className === '') {
                        className = c;
                    }
                    var buttons = FormButtonsTpl({
                        className: className,
                        formName: this.frm.attr("id")
                    });
                    this.$el.find(".side").append(buttons);
                }
                /*
                 * Array to keep an index of all the views attached to
                 * form elements. It is best if those views are
                 * destroyed when the modal closes, given that whenever
                 * the modal opens render runs again and thus those
                 * views are re-created.
                 */
                ,
                prepareViewsIndex: function () {
                    this.enabledViews = [];
                },
                emptyViewsIndex: function () {
                    $(this.enabledViews).each(function (idx, v) {
                        if (_.isObject(v) && _.isFunction(v.close))
                            v.close();
                    });
                    this.enabledViews = [];
                },
                addToViewsIndex: function (view) {
                    if (this.enabledViews === undefined) {
                        this.prepareViewsIndex();
                    }
                    this.enabledViews.push(view);
                },
                /*
                 * Array to keep an index of all the views attached to
                 * form elements that ALSO introduce validation.
                 */
                prepareValidationsIndex: function () {
                    this.validations = [];
                },
                emptyValidationsIndex: function () {
                    this.validations = [];
                },
                addToValidationsIndex: function (view) {
                    if (this.validations === undefined) {
                        this.prepareValidationsIndex();
                    }
                    this.validations.push(view);
                },
                /**
                 * Call validate on all validation views
                 * And then call validate on the current form
                 */
                applyValidation: function () {
                    var isValid = true;
                    $(this.validations).each(function (idx, v) {
                        isValid = isValid && v.validateView();
                    });
                    var validateFormFunction = this.validateForm;
                    if (validateFormFunction !== undefined && typeof (validateFormFunction) === 'function') {
                        isValid = isValid && this.validateFormFunction();
                    }
                    return isValid;
                },
                /**
                 * Runs on render and allows to add some functionalities to form fields.
                 */
                enableFunctionalities: function (options) {
                    var frm = this.frm;
                    var that = this;

                    var settings = null;

                    if (options !== undefined && options !== null && options !== "") {
                        settings = options[ options.length - 1 ];
                    }

                    // ENABLE FANCY SELECT FOR FIXED VALUES
                    frm.find("ul[name*=\"Date\"], ul[name*=\"Birthdate\"], ul[name*=\"Period\"], ul[name$=\"format\"]").each(function (idx, el) {
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
                        var fixedValueViews = new SelectFieldView({
                            el: $(el),
                            map: {},
                            allowClear: ($(el).is("ul[name$=\"format\"]") ? undefined : true)
                        });
                        that.addToViewsIndex(fixedValueViews);
                    });

                    // ENABLE FANCY SELECT FOR FIXED VALUES WITH CUSTOM HTML
                    frm.find("ul.cefr-level[name*=\"Level\"]", "ul.cefr-level[name~=\"ProficiencyLevel\"]").each(function (idx, el) {
                        var fixedValueViews = new SelectFieldView({
                            el: $(el),
                            map: {},
                            allowClear: true
                        });
                        that.addToViewsIndex(fixedValueViews);
                    });

                    // ENABLE FANCY SELECT FOR FIXED VALUES WITH CUSTOM HTML
                    frm.find("ul.ict-level[name*=\"Level\"]", "ul.ict-level[name~=\"ProficiencyLevel\"]").each(function (idx, el) {
                        var fixedValueViews = new SelectFieldView({
                            el: $(el),
                            map: {},
                            allowClear: true
                        });
                        that.addToViewsIndex(fixedValueViews);
                    });

                    // Update Labels for the proper appearance of pre selected values
                    frm.find(".level.select").each(function (idx, el) {
                        var elem = $(el).find(".trigger");
                        if ($(elem).find(".Code").length > 0 && $(el).find(".Label").length > 0)
                            that.formatSelected(elem);
                    });

                    // Rich Text Editor
                    frm.find('textarea.rich-editor').each(function (idx, el) {
                        var _el = $(el);
                        _el.removeAttr("placeholder");
                        if (_el.attr("rows") !== undefined)
                            _el.height(_el.attr("rows") * 14); //14px of line-height
                        else
                            _el.height(250);

                        var config = {el: _el};
                        config.rteConfig = {
                            applyTo: [{config: {
                                        minHeight: _el.height()
                                    }}]
                        };

                        if (!_.isEmpty(settings)
                                && _.isObject(settings)
                                && !_.isEmpty(settings.rteConfig)
                                && _.isObject(settings.rteConfig)
                                && _.isArray(settings.rteConfig.applyTo)) {
                            config.rteConfig.applyTo =
                                    _.union(config.rteConfig.applyTo, settings.rteConfig.applyTo);
                        }

                        var rteView2 = null;
                        try {
                            rteView2 = new RichTextEditorView2(config);
                        } catch (err) {
                            //console.log( err );
                            //No Rich Text Editor
                            rteView2 = new TextareaView({el: _el});
                        }

                        that.addToViewsIndex(rteView2);
                        that.addToValidationsIndex(rteView2);

                    });

                    /**Hide the bottom fixed area when the virtual keyboard appears in very small android tablets - landscape mode.
                     * Due to the fact that the virtual keyboard (in this specific situation) hides the majority of the screen allocation,
                     * the fixed bottom area was sticking on top of the keyboard hiding more space, thus the field was barely visible.
                     * That fix is applied for enhancing the functionality of editing fields in this specific environment (EWA-1315).
                     **/
                    var isAndroid = (WindowConfig.operatingSystem === "ANDROID");
                    var height = $(window).height();
                    var width = $(window).width();
                    var areaBot = this.$el.find(".side");
                    window.addEventListener("resize", function () {
                        if (areaBot.length > 0) {
                            if (isAndroid && width > height && height < 681) {
                                if ($(window).height() < height / 2) {
                                    areaBot.hide();
                                } else {
                                    areaBot.show();
                                }
                            }
                        }
                    }, false);
                },

                /**
                 * Event that triggers when a field is focused in very small android tablets - landscape mode.
                 * The modal scrolls till the focused or selected element(field /rich text editor / select2) reaches the top of the page
                 * That fix is applied for preventing the virtual keyboard from hiding the field that is being edited.
                 * */
                scrollField: function (event) {
                    var isAndroid = (WindowConfig.operatingSystem == "ANDROID");
                    var height = $(window).height();
                    var width = $(window).width();
                    if (isAndroid && width > height && height < 681) {
                        var textField = $(event.currentTarget);
                        var modal = textField.closest(".drawer.main.modalform");
                        var fieldId = $(textField).attr('id');
                        var modalScrl = modal.scrollTop();
                        var fieldPos = textField.offset().top;
                        var pos = modalScrl + fieldPos - 45; //Applied a margin so that the element's title would be visible
                        if (modal.length > 0) {
                            //event.preventDefault();
                            $('input[id="' + fieldId + '"]').focus($(modal).animate({scrollTop: pos}, "fast"));
                        }
                    }
                },

                /**
                 * Function to run after all child FormViews have added
                 * their functionalities. Especially useful when we need
                 * to make sure that some functionalites are added
                 * towards the end.
                 * 
                 * ReferenceTo is handle by a different view than the
                 * rest of the info. Therefore we need to make sure that
                 * the values are preserved upon saving.
                 * 
                 */
                finallyEnableFunctionalities: function (options) {
                    var that = this;
                    var frm = this.frm;
                    var settings = null;

                    if (options !== undefined && options !== null && options !== "") {
                        settings = options[ options.length - 1 ];
                    }

                    frm.find(".multipliable.compound.fixed-value-list-input").each(function (idx, el) {
                        var multifieldview = new FixedValueListInputView({el: $(el)});
                        that.addToViewsIndex(multifieldview);
                    });
                    frm.find(".multipliable.compound.auto-complete-list-input").each(function (idx, el) {
                        var multifieldview = new AutoCompleteListInputView({el: $(el)});
                        that.addToViewsIndex(multifieldview);
                    });
                    frm.find(".multipliable.compound.single:not(.auto-complete-list, fixed-value-list)").each(function (idx, el) {
                        var multifieldview = new SingleInputView({el: $(el)});
                        that.addToViewsIndex(multifieldview);
                    });
                    frm.find(".multipliable.compound.single.auto-complete-list").each(function (idx, el) {
                        var multifieldview = new SingleAutoCompleteListView({el: $(el), model: that.model});
                        that.addToViewsIndex(multifieldview);
                    });
                },
                getElementInfo: function (changed) {
                    var el = $(changed);
                    var key = el.attr('name');
                    var value = $('[name="' + key + '"]').val();
                    return {
                        'el': el,
                        'key': key,
                        'value': value
                    };
                },
                /**
                 * Default way to fetch the values from the current form.
                 * 
                 * Children FormView instances may override this function.
                 * 
                 * @param frm
                 * @param section
                 * 
                 * @returns 
                 * {
                 *   model: Backbone Model with the new attributes
                 *   changes: [] array of changed values
                 * }
                 */
                formToModel: function (frm, section) {
                    return this.model.formToModel(frm, section);
                },
                /**
                 * @param newModel the temporary model on which to work
                 * @param section
                 * @param otherSections array of other sections that need to be added
                 * 
                 * E.g.
                 * section = ForeignLanguage
                 * otherSections = [ Certificate, Experience ];
                 * 
                 * E.g.
                 * section = LP: Identification	
                 * otherSections = [ ContactInfo, Demographics, Photo ];
                 * 
                 */
                enrichFormToModel: function (frm, newModel, section) {

                    var livePath = section;
                    var sectionPath = Utils.removeSkillsPassportPrefix(section);

                    var tempSection = newModel.get(sectionPath);

                    //This is to serve cases such as the Other Skills which receive Linked attachments
                    if (_.isEmpty(tempSection))
                        tempSection = {};

                    //Proceed to enrich if only this is an object...
                    if (_.isObject(tempSection)) {

                        var schema = PreferencesSchema.resolve(Utils.toZeroArrayTxt(sectionPath));
                        var isArray = (_.isObject(schema) && schema.type === "array" && _.isArray(tempSection));

                        var otherSections = this.getRelatedSections(section);

                        if (isArray) {
                            //iterate each array item
                            for (var i = 0; i < tempSection.length; i++) {

                                var newPath = sectionPath + "[" + i + "]";
                                /*
                                 * Get the index from the live content, as the current index might not be the same due to add/remove
                                 */
                                var field =
                                        frm.find(":input:not(button):not(.PrintingPreferences)[name^=\"" + newPath + "\"][data-init-index=\"" + i + "\"]").first();
                                var liveIndex = field.attr("data-init-index");

                                var liveItemPath = livePath + "[" + liveIndex + "]";
                                this.doEnrich(liveItemPath, newPath, newModel, otherSections);
                            }
                        } else {
                            //do for this section
                            this.doEnrich(livePath, sectionPath, newModel, otherSections);
                        }
                    }
                },
                doEnrich: function (livePath, newPath, newModel, otherSections) {
                    //ReferenceTo
                    this.model.appendDocumentation(newPath, newModel);

                    //Other Sections
                    if (_.isArray(otherSections) && otherSections.length > 0) {
                        var liveContent = this.model.get(livePath);

                        for (var i = 0; i < otherSections.length; i++) {
                            var other = otherSections[i];

                            var liveRelSection = Utils.objAttr(liveContent, other);

                            if (liveRelSection === false) {
                                continue;
                            }
                            var appendSection = newPath + "." + other;

                            var tmp = {};
                            tmp[appendSection] = liveRelSection;
                            newModel.set(tmp);
                        }
                    }
                },

                /**
                 * Return an array of *relative* sections that need to be included in the prepared model
                 * @param section
                 * @returns
                 */
                getRelatedSections: function (section) {
                    if (_.isObject(this.otherSections) && !_.isEmpty(this.otherSections)) {
                        var others = this.otherSections[ section ];
                        if (!_.isUndefined(others)) {
                            return others;
                        }
                    }
                    return [];
                },
                /**
                 * Runs right after the setting of printing preferences and the executions of validations
                 * in order to store the data entered through the form fields.
                 * 
                 * @param event
                 * @param globalDateFormatUpdated: argument, 
                 *        which is set by the FormPrintingPreferencesView when the date format is changed, 
                 *        so that a suitable event will be triggered here, 
                 *        which will lead to the views displaying date information to be re-rendered.
                 */
                submitted: function (event, globalDateFormatUpdated) {
                    /*
                     * Quick fix for preventing double click events on Save, 
                     * causing the form not to save the users inputs
                     */
                    $(".button.save.green.submit").attr("disabled", true);
                    setTimeout(function () {
                        $(".button.save.green.submit").removeAttr('disabled');
                    }, 500);
                    var triggerEvents = false;
                    var cumulativeChanges = [];

                    try {
                        var frm = this.frm;
                        /*
                         * Validation Related Logic
                         */
                        var isValid = this.applyValidation();
                        if (isValid === false) {
                            return false;
                        }

                        /*
                         * Loop sections
                         */
                        var sections = this.section.split(" ");
                        for (var i = 0; i < sections.length; i++) {
                            var section = sections[i];
                            /*
                             * Children of a FormView may override this method.
                             */
                            var toModel = this.formToModel(frm, section);
                            var newModel = (toModel.model === null) ? null : toModel.model;
                            if (newModel === null) {
                                //throw new "new-model-is-null";
                                throw new Error("new-model-is-null");
                            }
                            this.enrichFormToModel(frm, newModel, section);

                            var newModelAttrs = newModel.attributes;

                            /*
                             * Decide what happens when the form fields have returned an empty object.
                             * Should we delete everything?
                             * -Yes, but only if the live model is non-empty
                             */
                            var isEmptyNew = _.isEmpty(newModelAttrs);
                            var isEmptyLive = false;
                            if (isEmptyNew) {
                                var liveSection = this.model.get(section);
                                //ekar : April 2014 - better use underscorejs's isEmpty which returns true when object is empty, or when array is empty or when string is the empty string
                                isEmptyLive = _.isEmpty(liveSection);
//								( _.isObject( liveSection ) && _.isEmpty( liveSection ) ) 
//								|| ( _.isArray( liveSection ) && liveSection.length === 0 )
//								|| ( _.isString( liveSection ) && $.trim( liveSection ) === "" );
                            }
                            /*
                             * Current is empty, and live is Empty, so skip...
                             */
                            if (isEmptyNew && isEmptyLive) {
                                triggerEvents = triggerEvents || false;
                                this.model.trigger("content:store:skipped", section);
                                continue;
                            } else {
                                triggerEvents = true;
                                /*
                                 * Important!
                                 * 
                                 * We need to first unset the live model, as Backbone will merge content instead of replacing it in case of arrays.
                                 * 
                                 */
                                if (this.model.has(section) === true) {
                                    this.model.set(section, "", {silent: true});
                                }
                                /*
                                 * Set the model
                                 */
                                this.model.set(section, newModel.get(Utils.removeSkillsPassportPrefix(section)), {silent: true});

                                cumulativeChanges = _.union(cumulativeChanges, toModel.changes);
                            }
                        }
                    } catch (err) {
                        throw err;
                    } finally {
                        this.$el.trigger("europass:waiting:indicator:hide");

                        if (triggerEvents === true) {
                            //TRIGGER EVENT FOR GLOBAL DATE FORMAT CHANGE
                            if (globalDateFormatUpdated) {
//							console.log("FormView: content:changed for prefs" + this.section  );
                                this.model.trigger("prefs:data:format:changed", this.section);
                            }
                            /*
                             * Trigger a suitable event for all the changes
                             */
                            if (_.isArray(cumulativeChanges)) {
                                for (var i = 0; i < cumulativeChanges.length; i++) {
                                    var change = cumulativeChanges[i];
                                    for (var key in change) {
//									console.log("FormView: content:changed for single key" + this.section + " with key " + key );
                                        this.model.trigger("content:changed:" + key, change[key]);
                                    }
                                }
                            }
                            // TRIGGER FOR MODEL AS WHOLE
//						console.log("FormView: content:changed for " + this.section + " with origin " + this.origin );
                            this.model.trigger("content:changed", this.section, this.origin);
                        }
                        this.closeModal();
                    }

                },
                /**
                 * For each non-empty form field the value will be taken and used to prepare a json object that will later be set to the current section
                 * @param fieldsAsJson
                 * @param currentSection
                 */
                doStore: function (fieldsAsJson, currentSection) {

                },
                cancelled: function (ev) {
                    this.closeModal();
                },
                modalClosed: function (ev, dateFormatUpdated) {
                    if (dateFormatUpdated) {
                        return ModalFormInteractions.confirmSaveSection(ev, this.frm.attr("id"));
                    }

                    var frm = this.frm;
                    var hasChanged = false;
                    var isValid = this.applyValidation();
                    
                    if (isValid === false) {
                        return false;
                    }

                    var sections = this.section.split(" ");
                    for (var i = 0; i < sections.length; i++) {
                        var section = sections[i];
                        var toModel = this.formToModel(frm, section);
                        var newModel = (toModel.model === null) ? null : toModel.model;
                        if (newModel === null) {
                            //throw new "new-model-is-null";
                            throw new Error("new-model-is-null");
                        }
                        this.enrichFormToModel(frm, newModel, section);
                        
                        if (!_.isEqual(this.model.get(section), newModel.get(Utils.removeSkillsPassportPrefix(section)))) {
                            hasChanged = true;
                            break;
                        }
                    }

                    if (hasChanged) {
                        ModalFormInteractions.confirmSaveSection(ev, this.frm.attr("id"));
                    } else {
                        this.closeModal();
                    }
                },
                attrChanged: function (newModel) {
                    this.render(newModel);
                },
                /**
                 * Close the Modal
                 */
                closeModal: function () {
                    $('body').removeClass('modal_overlay_open');
                    this.emptyViewsIndex();
                    this.emptyValidationsIndex();

                    var modal = this.$el;

                    if ($.isFunction(this.onModalClose)) {
                        this.onModalClose();
                    }

                    var side = (modal !== undefined ? modal.find(".side") : undefined);
                    var main = (modal !== undefined ? modal.find(".main") : undefined);
                    var overlay = (modal !== undefined ? modal.parent(".overlay.visible") : undefined);

                    //Find if the overlay of the tablet-opened right menu exists
                    //This need to be found in order to be removed, in case the user slides the right side-bar and also opens a modal
                    //That happens when the user slides a hovered section
                    var overlayTopMid = $("body").find(".transition-overlay");

                    if (side !== undefined) {
                        side.hide('slide', {direction: "right", easing: "easeInSine"}, 300);
                    }
                    if (main !== undefined) {
                        main.hide('slide', {direction: "right", easing: "easeInSine"}, 300);
                    }
                    modal.hide('slide', {direction: "right", easing: "easeInSine"}, 400);
                    modal.find("form.drawer").css("overflow-y", "hidden");
                    if (overlay !== undefined) {
                        overlay.animate({"background-color": "rgba(0,0,0, 0)"}, 400, function () {
                            $(this).hide();
                            if (overlayTopMid !== undefined && overlayTopMid !== null && overlayTopMid.length !== 0) {
                                $(overlayTopMid).remove();
                            }
                        });
                    }
                },

                /**
                 * Make the buttons available again.
                 * Revert disabled
                 * @param enabled
                 */
                updateMenuAvailability: function (enabled) {
                    var button = this.$el.find("menu.manage > button.submit");
                    if (enabled === true) {
                        button.attr("disabled", false);
                        button.removeClass("disabled");
                    } else if (enabled === false && !button.hasClass("disabled")) {
                        button.attr("disabled", "disabled");
                        button.addClass("disabled");
                    }
                },

                /**
                 * Formats the selected result for the language self assesment select lists.
                 * 
                 * @param state
                 * @param container
                 * @returns {String}
                 */
                formatAssesmentLevelSelect: function (state, container) {
                    var stateId = state.id;
                    //get placeholder from the select element name and taxonomy, but if not found, then get the elements default placeholder	
                    var placeholder = this.element.prop("name");
                    var placeholder2 = Utils.removeIndexTxt(this.element.attr("data-format-selection-key"));
                    if (!Utils.isUndefined(placeholder2)) {
                        placeholder = placeholder2.toString();
                    }
                    if (!Utils.isUndefined(placeholder)) {
                        placeholder = Utils.removeIndexTxt(placeholder.toString());
                        placeholder = DocumentLabel[placeholder];
                    } else {
                        placeholder = $.trim(Utils.escapeHtml(this.placeholder)) || '';
                    }
                    var context = {};

                    if (!_.isEmpty(stateId)) {
                        context = {
                            'Code': $.trim(stateId),
                            'CodeLabel': (LanguageShortLevelMap === null ? stateId : $.trim(LanguageShortLevelMap.get(stateId))),
                            'Level': placeholder
                        };
                    }
                    var selection = SelectionFormatTpl(context);
                    return selection;
                },

                /**
                 * Formats the selected result for the certificate level select lists.
                 * 
                 * @param state
                 * @param container
                 * @returns {String}
                 */
                formatCertificateLevelSelect: function (state, container) {
                    var stateId = state.id;
                    var context = {};

                    if (!_.isEmpty(stateId)) {
                        context = {
                            'Code': $.trim(stateId),
                            'CodeLabel': (CertificateLevelMap == null ? stateId : $.trim(CertificateLevelMap.get(stateId)))
                        };
                    }
                    var selection = SelectionFormatTpl(context);
                    return selection;
                }

                , formatSelected: function (elem) {

                    var formattedElemName = elem.closest(".level.select").attr("name");

                    var code = elem.find(".Code").html();

                    if (code !== undefined) {
//					var label = elem.find(".short-key-label").html();
//					var level = elem.siblings("select").attr("data-name");
                        var context = {
                            'Code': code,
//						'CodeLabel' : label,
//						'Level' : level
                        };

                        if (formattedElemName === "Certificate") {
                            context.CodeLabel = elem.find(".Label").html();
                        }
                        if (formattedElemName === "Listening" ||
                                formattedElemName === "Reading" ||
                                formattedElemName === "SpokenInteraction" ||
                                formattedElemName === "SpokenProduction" ||
                                formattedElemName === "Writing"
                                ) {
                            context.CodeLabel = elem.find(".short-key-label").html();
                            context.Level = elem.siblings("select").attr("data-name");
                            //ICT Skills
                        } else if (
                                formattedElemName === "Information" ||
                                formattedElemName === "Communication" ||
                                formattedElemName === "ContentCreation" ||
                                formattedElemName === "Safety" ||
                                formattedElemName === "ProblemSolving"
                                ) {
                            context.CodeLabel = elem.find(".short-key-label").html();
                            //context.Level = elem.siblings("select").attr("data-name");
                        }
                        var selection = SelectionFormatTpl(context);
                        elem.html(selection);
                    }
                }

                /**
                 * Utility function that collects and stores the input values to an array
                 */
                , currentValues: function (elem) {

                    var valuesArray = new Array();

//				var globalParent = $( this.global ? this.el.closest(".modalform") : this.el.parent(".multipliable") );

                    var globalParent = $(elem).hasClass("global") ? $(elem).closest(".modalform") : $(elem);

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
            });
            return FormView;
        }
);
