define(
        [
            'jquery',
            'underscore',
            'backbone',
            'Utils',
            'i18n!localization/nls/EditorHelp',
            'europass/GlobalHelpOptionInstance',
            'hbs!templates/help/modal',
            'hbs!templates/help/innermodal',
//     'europass/TabletInteractionsView',
            'europass/http/WindowConfigInstance'
        ],
        function ($, _, Backbone, Utils, EditorHelp, GlobalHelpOption, TooltipTemplate, InnerTemplate, WindowConfig) {//TabletInteractionsView,

            var HelpView = Backbone.View.extend({

                tablets: {},

                TIP_POSITION: "top",

                HEADER_TIP_POSITION: "top",

                INPUT_KEY: "data-help-key",

                GROUP_KEY: "data-help-group",

                events: {
                    //When a multipliable field is added
                    //Note we do not remove the help when a multipliable is deleted to save some computing effort
                    "europass:multipliable:added ": "enableFieldHelp",
                    //Event for the Global Help switch
                    "click :input.help-switch-control": "manageHelpOption",
                    //Focus on .help.formfield must show the help
                    //Lose focus on .help.formfield must hide the help

                    //General input fields
                    "focus :input.formfield.help": "show",
                    "blur :input.formfield.help": "hide",
                    //Textareas converted to Rich Text Areas
                    "europass:rich-text-editor:focused textarea.formfield.help": "show",
                    "europass:rich-text-editor:blurred textarea.formfield.help": "hide",
                    //Radios
                    "mouseenter .formfield-label.css-label.overHelp": "showRadio",
                    "mouseleave .formfield-label.css-label.overHelp": "hideRadio",

                    //General input fields
                    "focus :input.formfield.noHelp": "showHeader",
                    //Textareas converted to Rich Text Areas
                    "europass:rich-text-editor:focused .noHelp": "showHeader",
                    //Radios
                    "mouseenter :radio.formfield.noHelp": "showHeader",
                    //Focus on area other than input
                    "click ": "focusElseWhere",
                    //Tooltip close button disables global help
                    "click button.fixed-tooltip-close": "hideTooltip",
                    //Toggle expansion of fixed tooltip when its content is too long
                    "click button.btn-toggle-txt": "toggleTextExpansion",
                    //Stop propagation in fixed tooltip
                    "click .btn-toggle-wrapper": "stopProp",
                    //Link of help at eqf section
                    "mousedown .eqf-help-link": "eqfHelpClicked",
                    //On close modal
                    "europass:modal:closed ": "onClose",
                    "click :button.cancel": "onClose"

//				"europass:multipliable:removed .multipliable" : "fieldRemoved",
                },
                /**
                 * el          : div.modal
                 * model       : instance of SkillsPassport
                 * root        : "SkillsPassport"
                 * section     : JSON path,
                 * helpSection : JSON path
                 */
                initialize: function (options) {

                    this.frm = this.$el.find("form");
                    this.asideHelp = this.$el.find(".side").find(".help-container");
                    this.section = options.section;
                    this.helpSection = options.helpSection;

                    this.helpSwitch = this.$el.find(":input.help-switch-control");

                    this.sectionCtx = this.prepareSectionContext();

                    this.isHelpBuilt = false;
                    this.isiPad = navigator.userAgent.match(/(iPod|iPhone|iPad)/) !== null;
                    this.isTablet = (/(iPhone|iPod|iPad)/.test(navigator.userAgent) || (/Android/.test(navigator.userAgent)) || (/BlackBerry/.test(navigator.userAgent)));
                    if (this.isTablet) {

                        /**
                         * pgia: EWA-1815
                         * Load TabletInteractionsView via require on the tablets variable ONLY if isTablet == true
                         */
                        var _that = this;
                        require(
                                ['europass/TabletInteractionsView'],
                                function (TabletInteractionsView) {
                                    _that.tablets = TabletInteractionsView;
                                }
                        );
                    }
                },

                onClose: function () {
                    $(window).off("resize", $.proxy(this.onResize));
                    $(window).off("orientationchange", $.proxy(this.onResize));
                },

                /**
                 * Create a clone of the header help before it becomes rendered
                 * Hide this clone and return its height (which is dynamic, according to the content of each help section).
                 * The value of height is needed for deciding if the help section will include the toggle expansion button.
                 * @param element: .fixed-form-tooltip
                 * @returns height: height of the fixed-tooltip-inner, child of .fixed-form-tooltip
                 */
                getHeaderHelpHeight: function (element) {
                    var tempId = 'tmp-' + Math.floor(Math.random() * 99999);//generating unique id just in case
                    var body = $.find("body");
                    $(element).clone()
                            .css('position', 'absolute')
                            .css('height', 'auto')
                            //inject right into parent element so all the css applies
                            .appendTo(body)
                            .css('left', '-10000em')
                            .addClass(tempId).show();
                    var wrapper = $('.' + tempId) !== undefined ? $('.' + tempId).find('.fixed-tooltip-inner') : undefined;
                    helpHeight = $(wrapper).height();
                    $('.' + tempId).remove();
                    return helpHeight;
                },

                onResize: function () {
                    var currHelp = this.$el.find(".fixed-form-tooltip:visible");
                    if (currHelp !== undefined && currHelp.length > 0) {
                        this.arrangeTltp(currHelp);
                    }
                },
                /**
                 * Render the view.
                 * Only when not already calculated, build the tooltips for the section and inputs
                 * @param forceBuild: boolean to force build of help, even if already built
                 */
                render: function (forceBuild) {
                    this.isOn = GlobalHelpOption.isOn();
                    //Get the current state of global help
                    var status = GlobalHelpOption.get();
                    var checkbox = this.$el.find(".help-switch-control");
                    if (forceBuild !== undefined && forceBuild !== null && _.isBoolean(forceBuild)) {
                        this.forceBuild = forceBuild;
                    }
                    //Set the button class name properly
                    $(checkbox).addClass(status);
                    $(checkbox).prop("checked", "on" === status);
                    $(checkbox).closest('menu').find(".help-check").addClass(status);

                    var headers = this.findHeader();
                    if (headers.length > 0) {
                        this.hideVisible();
                        headers.remove().not().first();
                    }

                    //Build properly the help sections if global help is on, otherwise remove any already existing help.
                    if (this.isOn) {
                        this.buildHelp(this.forceBuild);
                    } else {
                        this.removeHelp();
                    }

                    $(window).on("resize", $.proxy(this.onResize, this));

                    $(window).on("orientationchange", $.proxy(this.onResize, this));

                    if (this.isiPad) {
                        this.fixIosRubberBand();
                    }
                },

                /**
                 * Will add a new HTML structure next to each :input:not(button).formfield.help
                 * @param forceBuild: boolean to force build of help, even if already built
                 */
                buildHelp: function (forceBuild) {
                    if (!(forceBuild === true) && this.isHelpBuilt) {
                        return;
                    }

                    if (forceBuild === true) {
                        this.sectionCtx = this.prepareSectionContext();
                    }
                    // ---- HEADER ---- 
                    this.buildHeaderHelp();

                    // ---- INPUTS ----
                    var inputs = this.frm.find(":input:not(button).formfield.help,:input:not(button).formfield.overHelp");
                    for (var i = 0; i < inputs.length; i++) {
                        var input = $(inputs[i]);
                        this.buildInputHelp(input);
                    }
                },
                /**
                 * Prepare help section context
                 */
                prepareSectionContext: function () {
                    var key = _.isEmpty(this.helpSection) ? this.section : this.helpSection;
                    var helpKey = Utils.removeIndexTxt(key);

                    var msg = EditorHelp[ helpKey ];

                    var context = {};
                    if (!_.isEmpty(msg)) {
                        context.section_message = msg;
                        context.id = helpKey;
                    }
                    return context;
                },
                /**
                 * Render the help for the section.
                 * Place in two places:
                 *  1. In the side area inside the help-container, at the top
                 *  2. In a fixed position, always on top.
                 */
                buildHeaderHelp: function () {
                    //Hide any previous header help
                    this.findAllHeaders().hide();
                    var helpExists = this.findHeader().length > 0;
                    if (helpExists) {
                        this.showHeader();
                    }

                    if (!_.isEmpty(this.sectionCtx)) {
                        var drawer = this.frm.closest(".drawer.main");
                        if (drawer.length > 0) {
                            var sectionCtx = {
                                position: this.HEADER_TIP_POSITION,
                                message: this.sectionCtx.section_message,
                                id: "header-help_" + this.helpSection
                            };
                            //SIDE
                            var sideSection = $(TooltipTemplate(sectionCtx));
                            this.asideHelp.prepend(sideSection);

                            //MAIN
                            sectionCtx.tooltip = true;
                            var tooltipSection = $(TooltipTemplate(sectionCtx));
                            tooltipSection.prependTo(drawer);

                            this.arrangeTltp(tooltipSection);
                        }
                    }
                },
                /**
                 * Calculates the data-rel text
                 * @param input
                 * @returns {String}
                 */
                buildInputId: function (input) {
                    var helpKey = input.attr(this.INPUT_KEY);
                    if (_.isEmpty(helpKey)) {
                        helpKey = input.attr(this.GROUP_KEY);
                    }
                    return input.attr("name") + "_" + helpKey;
                },
                /**
                 * Prepare the message for field and group (if any) help
                 * And place in two places:
                 * 	1. In the side area inside the help-container
                 *  2. Always on top of the formfield
                 */
                buildInputHelp: function (input) {
                    var id = this.buildInputId(input);
                    var existedHelp = this.asideHelp.find(".form-help[data-rel=\"" + id + "\"]");
                    //var isExportStep2 = this.asideHelp.find(".fixed-form-tooltip[data-rel^=\"Export_Option_File_export.wizard.help.step2.file.option\"]");

                    if (existedHelp.length > 0) {
                        existedHelp.remove().not().first();
                    }
                    var context = this.prepareInputContext(input);
                    if (_.isEmpty(context))
                        return;

                    var msg = InnerTemplate(context);

                    var ctx = {
                        position: input.attr("data-help-position") || this.TIP_POSITION,
                        message: msg,
                        id: id
                    };
                    //SIDE
                    var sideTip = $(TooltipTemplate(ctx));
                    sideTip.hide();
                    this.asideHelp.append(sideTip);

                    //MAIN
                    ctx.tooltip = true;
                    var drawer = this.frm.closest(".drawer.main");
                    var tooltipTip = $(TooltipTemplate(ctx));
                    tooltipTip.hide();
                    //Call a function to define the parent in which the help will be prepended
                    var parent = this.defineTltp(drawer, this.helpSection, tooltipTip);
                    tooltipTip.prependTo(parent);
                },
                /**
                 * When a multipliable field is added, the corresponding tooltip needs to appear
                 */
                enableFieldHelp: function (event) {
                    var composite = $(event.target);
                    var input = composite.find(":input.copy");
                    if (input.length > 0)
                        this.buildInputHelp(input);
                },
                /**
                 * Prepare the context to render the tooltip per input
                 */
                prepareInputContext: function (input) {
                    var context = {};
                    var showGroup = false;
                    var showField = false;
                    //Group
                    var groupAttr = input.attr(this.GROUP_KEY);
                    if (!_.isEmpty(groupAttr)) {
                        var tmp = groupAttr.split(" ");
                        groupAttr = tmp[0];//get the first group, but this might change
                        groupAttr = Utils.removeIndexTxt(groupAttr);
                        var msgGroup = EditorHelp[ groupAttr ];
                        showGroup = !_.isEmpty(msgGroup);
                        if (showGroup) {
                            context.group_message = msgGroup;
                        }
                    }
                    //Input
                    var attr = input.attr(this.INPUT_KEY);
                    if (!_.isEmpty(attr)) {
                        attr = Utils.removeIndexTxt(attr);
                        var msg = EditorHelp[ attr ];
                        showField = !_.isEmpty(msg);
                        if (showField) {
                            context.message = msg;
                        }
                    }
                    //Id
                    if (showGroup || showField) {
                        context.id = this.buildInputId(input);
                    }
                    return context;
                },
                /**
                 * Handle tablet hover effects before calling manageHelp
                 */
                manageHelpOption: function (event) {

                    // TODO CLEAN UP COMMENT On mobile not using 2 step tooltip anymore !!
                    // if ((this.isTablet && _.isFunction(this.tablets.handleTipSpot)) && this.tablets.handleTipSpot(event, "target") === false) {
                    // 	return false;
                    // }
                    this.manageHelp(event);
                },
                /**
                 * Respond to the on/off help switch
                 */
                manageHelp: function (event) {

                    var checkbox = $(event.target);
                    //get the current status of the global help
                    var prevStatus = GlobalHelpOption.get();

                    //change the status of the Global Help
                    var status = GlobalHelpOption.switcher();

                    //Set the button class name properly
                    checkbox.prop("checked", "on" === status);
                    checkbox.removeClass(prevStatus);
                    checkbox.addClass(status);
                    $(checkbox).closest('menu').find(".help-check").removeClass(prevStatus).addClass(status);

                    //Build properly the help sections if global help is on, otherwise remove any already existing help.
                    if (status === "on") {
                        this.buildHelp(this.forceBuild);

                    } else if (status === "off") {
                        this.removeHelp();
                    }
                },
                /**
                 * Remove all form tooltips and remove the top margin of the legend,
                 * so that no unnecessary space will remain.
                 */
                removeHelp: function () {
                    this.$el.find(".form-help").remove();
                    this.movedLegend(false);
                },

                /**
                 * On focus on an element
                 */
                show: function (event) {
//				console.log("show");
                    this.hideVisible();
                    this.hideHeader();

                    var id = this.buildInputId($(event.target));
                    var tip = this.findRelated($(event.target));

                    var isImportStep1 = "Import_Option_Loc_import.wizard.help.step1.location.option";
                    var isExportStep3 = "Export_Option_Loc_export.wizard.help.step3.location.option";
                    var isExportStep1 = this.$el.find("input[id^=\"Export_Option_Doc_\"]").length > 0;


                    //Exclude export/import wizard specific steps from showing the field help, due to tooltips duplication (tablet)
                    if (id === isImportStep1 || id === isExportStep3 || isExportStep1) {
                        this.showHeader();
                    } else {
                        tip.show();
                    }

                    //Elements that determine the existence of the expandable-collapsible tooltip box
                    //It depends on the size of the help text
                    var hlpText = tip.find(".fixed-tooltip-inner");
                    var helpHeight = hlpText.height();

                    //Add the proper class to toggle expansion button if it does not have already any
                    if (helpHeight > 110) {
                        if (!hlpText.hasClass("tltp-hidden") && !hlpText.hasClass("tltp-visible")) {
                            this.renderTxtBtn(tip);
                        } else if (hlpText.hasClass("tltp-hidden")) {
                            this.toggleTxtBtn("expand", tip);
                        } else if (hlpText.hasClass("tltp-visible")) {
                            this.toggleTxtBtn("collapse", tip);
                        }
                    }


                    //Fix for iPad for not scrolling the modal when a field is focused
                    if (GlobalHelpOption.isOn() && this.isiPad) {
                        this.fixIosScroll();
                    }
                },

                /**
                 * Show the help for the header
                 */
                showHeader: function () {
//				console.log("show header");
                    this.hideVisible();
                    this.findAllHeaders().hide();
                    this.findHeader().show();
                },
                /**
                 * On lose focus off an element
                 * Do not hide a help text with a link (so it can be clicked) and the tablet fixed help tooltips
                 */
                hide: function (event) {
                    var elem = $(event.target);
                    var helpForm = this.findRelated(elem);
                    helpForm.not(".fixed-form-tooltip, .has-link").hide();
                },
                /**
                 * Hide the help for the header
                 */
                hideHeader: function () {
                    this.findHeader().hide();
                },

                hideVisible: function () {
                    this.$el.find(".form-help:visible").hide();
                },

                /**
                 * Finds the tooltip which is related to this view based on the data-help-key of the input and the data-rel attribute of the tooltip
                 * @param el
                 * @returns
                 */
                findRelated: function (el) {
                    var name = this.buildInputId(el);
//				console.log("Find by "+name);
                    return this.$el.find(".form-help[data-rel=\"" + name + "\"]");
                },
                findHeader: function () {
                    return this.$el.find(".form-help[data-rel=\"header-help_" + this.helpSection + "\"]");
                },
                findAllHeaders: function () {
                    return this.$el.find("[data-rel^=\"header-help_\"]");
                },

                /**
                 * Show help for radios which are replaced by css-label elements
                 */
                showRadio: function (event) {
                    var input = this.findRadio(event);
                    //In case the according formfield event does not trigger and the input is undefined
                    if (input.length <= 0 || input === undefined) {
                        this.showHeader();
                    } else {
//						console.log("show radio");
                        this.hideHeader();
                        var tip = this.findRelated(input);
                        this.arrangeTltp(tip);
                        tip.show();
                    }
                },

                /**
                 * Hide help for radios which are replaced by css-label elements
                 */
                hideRadio: function (event) {
//				console.log("hide radio");
                    var input = this.findRadio(event).hide();
                    this.findRelated(input).hide();
                    //Show Header
                    this.showHeader();
                },
                findRadio: function (event) {
                    var label = $(event.target);
                    return label.siblings("#" + label.attr("for"));
                },
                /**
                 * When focus is anywhere else
                 */
                focusElseWhere: function (event) {
                    var obj = $(event.target);
                    var isFormField = obj.is("textarea, :input:text, :input:password, :input:not(:button).formfield, .help.placeholder");
                    var isRTE = obj.closest(".cke_wysiwyg_div").length > 0;
                    //var isCSSLabelOverHelp = obj.closest(".formfield-label.css-label.overHelp").length > 0;
                    var isCSSLabel = obj.closest(".formfield-label.css-label").length > 0;
                    var isEqfLink = obj.is(".eqf-help-link");
                    var isCloseTltpBtn = obj.is("button.fixed-tooltip-close");
                    var isBtnCancel = obj.is("button.cancel");
                    var isGlobalSwitch = obj.is(":input.help-switch-control");

                    var isSuggestionMenu = obj.is(".tt-dropdown-menu");
                    var isSuggestion = obj.is(".tt-suggestion");

//				if(this.isTablet){
//					throw "-> "+obj.parents(".twitter-typeahead").html();
//				}
                    if (isSuggestionMenu || isSuggestion) {
                        if (this.isTablet || this.isiPad)
                            obj.find(".tt-dropdown-menu").trigger("click.tt", [obj]);
                        return;
                    }
                    var isVague = (obj.is("p") && obj.selector === "");
//				if(isVague)
//					alert("vague");
//				obj.parents(".modal").find(".fixed-tooltip-wrapper").html("<div>"+obj.attr("class")+"</div>");
//				console.log(obj);

                    //Show header help when clicking anywhere else, except for some specific elements
                    if (!isFormField && !isRTE && !isCSSLabel && !isCloseTltpBtn && !isBtnCancel && !isGlobalSwitch && !isVague) {
                        this.showHeader();
                    }

                    //If target is not the eqf link, find this element and remove the class
                    if (!isEqfLink) {
                        var modal = obj.closest(".modal");
                        var clickedHelp = modal.find(".has-link");
                        if (clickedHelp.length > 0) {
                            clickedHelp.removeClass("has-link");
                            clickedHelp.hide();
                        }
                    }
                },

                /**
                 * Toggle the expansion of tablet help tooltips which are containing long texts
                 */
                toggleTextExpansion: function (event) {
                    var btn = $(event.currentTarget);
                    var tltp = btn.closest(".fixed-form-tooltip");
                    var tltpInner = btn.closest(".fixed-tooltip-inner");
                    var tltpWrap = btn.closest(".fixed-tooltip-wrapper");
                    var btnWrap = btn.closest(".btn-toggle-wrapper");

                    tltpInner.toggleClass("tltp-hidden");
                    tltpInner.toggleClass("tltp-visible");

                    btn.toggleClass("less");
                    btn.toggleClass("more");

                    /**Adjust the height of the help section according to the text's length
                     * Apply the proper style and content in the toggle - expansion button
                     * Set the proper height of the toggle button's wrapper
                     * Added an animation to the toggle / expansion operation
                     */
                    //Collapsible
                    if (btn.hasClass("less") && tltpInner.hasClass("tltp-visible")) {
                        var currHeight = $(tltpWrap).height();
                        $(tltpWrap).css('height', 'auto');
                        var autoHeight = $(tltpWrap).height();
                        tltpWrap.height(currHeight).animate({height: autoHeight}, 500);
                        btnWrap.animate({"top": autoHeight - 40}, 500);

                        this.toggleTxtBtn("collapse", tltp);

                        //Expandable
                    } else {
                        tltpWrap.animate({"height": "140px"}, 500);
                        btnWrap.animate({"top": "100px"}, 500);

                        this.toggleTxtBtn("expand", tltp);
                    }
                },

                /**
                 * Remove any event from the tablet tooltip section, for the proper appearance of each field's help
                 * This has been applied for not showing the header when the toggle text button is tapped
                 */
                stopProp: function (event) {
                    event.stopPropagation();
                    return false;
                },

                /**
                 * Hide tooltips and set off the global switch when a tooltip is closed
                 */
                hideTooltip: function (event) {
                    var closeButton = $(event.target);
                    var modal = closeButton.closest(".modal");
                    var checkbox = modal.find(".help-switch :checkbox");
                    if (GlobalHelpOption.isOn) {
                        var prevStatus = GlobalHelpOption.get();
                        if (prevStatus === "on") {
                            //set to "off" the status of the Global Help
                            var status = GlobalHelpOption.set("off");

                            //set the button class name properly
                            checkbox.prop("checked", "on" === status);
                            checkbox.removeClass(prevStatus);
                            checkbox.addClass(status);
                            $(checkbox).closest('menu').find(".help-check").removeClass(prevStatus).addClass(status);
                        }
                    }
                    //remove any appearing help
                    if (!GlobalHelpOption.isOn()) {
                        this.removeHelp();
                    }
                },

                /**Function to set the appearance of longer than the fixed height tooltips
                 * and make it expandable - collapsible
                 * @param tltp: The section's tooltip **/
                arrangeTltp: function (tltp) {

                    if (tltp.hasClass("fixed-form-tooltip")) {
                        /*If tooltip help (tablet), get the height of the header help via getHeaderHelpHeight()
                         * a clone is used because the exact height must be gotten before the modal will be rendered
                         * otherwise the height would be zero*/
                        var helpHeight = this.getHeaderHelpHeight(tltp);
                        var hlpText = tltp.find(".fixed-tooltip-inner");

                        /*If the text height is higher than the fixed height of help section, a toggle expansion button appears.
                         Adjust the style of the button according to its state */
                        if (helpHeight > 110) {
                            if (hlpText.hasClass("tltp-hidden")) {
                                this.toggleTxtBtn("expand", tltp);
                            } else if (hlpText.hasClass("tltp-visible")) {
                                this.toggleTxtBtn("collapse", tltp);
                            } else if (!hlpText.hasClass("tltp-visible") && !hlpText.hasClass("tltp-hidden")) {
                                this.renderTxtBtn(tltp);
                            }

                        }
                        //Aply a top margin at the legend so it won't overlap with the appearing tooltip
                        this.movedLegend(true);
                    }
                },

                /**Apply-remove a top margin at the legend, so that the help section won't overlap with the form's top elements 
                 * and equally the legend will return to its place if the help is removed.
                 * @param bool : Boolean to move downwards the legend (add top margin) if help exists (true) or restore legend's position (false);
                 **/
                movedLegend: function (bool) {
                    var legend = this.frm.find("legend:first");
                    var clazz = "top-help";
                    if (bool === true) {

                        /* If the form is export wizard (legend has class preview) then move the form's fieldset
                         This is a solution for a bug in which the top margin would not be applied to the legend itself */
                        if (legend.hasClass("preview")) {
                            var fieldset = legend.parent("fieldset.process");
                            fieldset.addClass(clazz);
                        } else {
                            legend.addClass(clazz);
                        }
                    } else if (bool === false) {
                        if (legend.hasClass("preview")) {
                            var fieldset = legend.parent("fieldset.process");
                            fieldset.removeClass(clazz);
                        } else {
                            legend.removeClass(clazz);
                        }
                    }
                },

                /**Render the button of expandable - collapsible tooltips and hide the overflow of longer texts.
                 * This options is appearing when a tooltip's text is longer than the fixed height of all help sections (tablet) 
                 * @param tl: this section's tooltip
                 **/
                renderTxtBtn: function (tl) {
                    var hlpText = tl.find(".fixed-tooltip-inner");
                    var showTxtBtn = tl.find(".btn-toggle-wrapper");
                    var showBtnSpanTxt = showTxtBtn.find("span.tltp-txt");
                    var showBtnSpanImg = showTxtBtn.find("span.tltp-img");
                    var moreTxt = EditorHelp["modal.help.tooltip.text.more"];
                    var btnToggle = showTxtBtn.find(".btn-toggle-txt");


                    //Hide the ovwerflow of long text
                    hlpText.addClass("tltp-hidden");
                    //Make the toggle button appear
                    showTxtBtn.css({"visibility": "visible"});
                    //Always show the button centered
                    btnToggle.css({"margin-left": "-5%"});
                    //Icon
                    showBtnSpanImg.css("background-position", "0px -275px");
                    //Text
                    //Temporarily these options are used until the CMS key will not be undefined
                    if (moreTxt === undefined) {
                        showBtnSpanTxt.text("More");
                    } else {
                        showBtnSpanTxt.text(moreTxt);
                    }
                },

                /**Determine the state of the toggle expansion button (text and icon)
                 * @param optn: string that sets if the tooltip is expandable or collapsible
                 * @param tl: this section's tooltip **/
                toggleTxtBtn: function (optn, tl) {
                    var showTxtBtn = tl.find(".btn-toggle-wrapper");
                    var showBtnSpanTxt = showTxtBtn.find("span.tltp-txt");
                    var showBtnSpanImg = showTxtBtn.find("span.tltp-img");
                    var btnToggle = showTxtBtn.find(".btn-toggle-txt");
                    var btnWidth = btnToggle.width();

                    //Expandable
                    if (optn === "expand") {
                        var moreTxt = EditorHelp["modal.help.tooltip.text.more"];
                        //Position the button in center
                        btnToggle.css({"margin-left": -(btnWidth / 2)});
                        //Icon (arrow-down)
                        showBtnSpanImg.css("background-position", "0px -275px");
                        //Text (More)
                        //Temporarily these options are used until the CMS key will not be undefined
                        if (moreTxt === undefined) {
                            showBtnSpanTxt.text("More");
                        } else {
                            showBtnSpanTxt.text(moreTxt);
                        }
                        //Collapsible	  
                    } else if (optn == "collapse") {
                        var lessTxt = EditorHelp["modal.help.tooltip.text.less"];
                        //Position the button in center
                        btnToggle.css({"margin-left": -(btnWidth / 2)});
                        //Icon (arrow-up)
                        showBtnSpanImg.css("background-position", "0px -258px");
                        //Text (Less)
                        //Temporarily these options are used until the CMS key will not be undefined
                        if (lessTxt === undefined) {
                            showBtnSpanTxt.text("Less");
                        } else {
                            showBtnSpanTxt.text(lessTxt);
                        }
                    }
                },

                /**
                 * Add a class to eqf link help text, so that the containing link can be clicked
                 */
                eqfHelpClicked: function (event) {
                    var form = $(event.currentTarget).closest(".form-help");
                    if (form.length > 0) {
                        form.addClass("has-link");
                    }
                },

                /**
                 * Workaround for prepending the tooltips accordingly to every wizard step
                 * Due to the fact that all wizard steps have the same id, the tooltips were appearing duplicated
                 * That was happening because in every step, ALL wizard's tooltips were built, as every step is considered the same form (this.frm). 
                 * 
                 **/
                //TODO Maybe transfer this to Utils???
                defineTltp: function (main, hlpSection, tltpTip) {

                    if ((main.attr("id") === "ExportWizardForm") || (main.attr("id") === "ImportWizardForm")) {
                        //Get current step's number by helpSection (last character of header help)
                        var currStep = hlpSection.substr(-1);

                        //Get the specific fieldset of this step
                        var fld = main.children("fieldset[data-active-step=\"" + currStep + "\" ]");
                        //A string that help us to locate the index which indicates the step in which belongs the current tooltip
                        var str = "help.step";
                        //A helper which returns the index of the character that indicates the current step
                        var helper = tltpTip.attr("data-rel").indexOf(str) + str.length;
                        //Finally get the step value
                        var tltpStep = tltpTip.attr("data-rel").slice(helper, helper + 1);
                        //                  console.log (currStep + "   " + tltpStep + "   tooltipTip: " + tooltipTip.attr("data-rel"));
                        //If the tooltip's step param is equal to helpSection's step param...
                        if (currStep === tltpStep) {
                            //Get the parent drawer of the specific fieldset
                            var specMain = fld.parent(".drawer.main");
                            return specMain;
                        }
                    } else {
                        return main;
                    }

                },

                /**
                 * Causes the browser to reflow all elements on the page.
                 * Fix for the iOS bug where fixed positioned elements are
                 * unavailable after programmatically calling window.scrollTo()
                 */
                reflowFixedPositions: function () {
                    document.documentElement.style.paddingRight = '1px';
                    setTimeout(function () {
                        document.documentElement.style.paddingRight = '';
                    }, 0);
                },

                /**Disable the form's "bouncing" effect when the user focuses a field in IOS
                 * This is needed only when the global help is on, because this bounce ruins the fixed tooltips position.
                 * For more info about this workaround, visit:
                 * stackoverflow.com/questions/10238084/ios-safari-how-to-disable-overscroll-but-allow-scrollable-divs-to-scroll-norma
                 */

                fixIosScroll: function () {
                    setTimeout(function () {
                        $('html, body').animate({scrollTop: 0, scrollLeft: 0}, 0);
                    }, 0);

                    var scrl = this.frm;
                    //Uses document because document will be topmost level in bubbling
                    $(document).on('touchmove', function (event) {
                        event.preventDefault();
                    }, false);

                    //Uses body because jQuery on events are called off of the element they are
                    //added to, so bubbling would not work if we used document instead.
                    $('body').on('touchstart', scrl, function (e) {

                        if (e.currentTarget.scrollTop === 0) {
                            e.currentTarget.scrollTop = 1;
                        } else if (e.currentTarget.scrollHeight === e.currentTarget.scrollTop + e.currentTarget.offsetHeight) {
                            e.currentTarget.scrollTop -= 1;
                        }
                    });
                    //Stops preventDefault from being called on document if it sees a scrollable div
                    $('body').on('touchmove', scrl, function (e) {
                        e.stopPropagation();
                    });
                },

                /**When touchmoving the formfields, the window was making an effect of "absolute element being dragged"
                 * and the form was not being scrolled properly (the so-called "rubber effect" on IOS).
                 * This function disables any dragging or scrolling effect when the touchmove event takes place in formfields.
                 */
                fixIosRubberBand: function () {

                    var frmFld = this.frm.find("input.formfield");
                    var txtAre = this.frm.find("textarea.formfield");

                    if (frmFld !== undefined && frmFld.length > 0) {
                        frmFld.on("touchmove", function (e) {
                            e.preventDefault();//Stops the default behavior
                        }, false);
                    }
                    if (txtAre !== undefined && txtAre.length > 0) {
                        txtAre.on("touchmove", function (e) {
                            e.preventDefault();//Stops the default behavior
                        }, false);
                    }
                }
            });

            return HelpView;
        }
);