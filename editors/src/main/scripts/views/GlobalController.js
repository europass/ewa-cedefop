define(
        [
            'jquery',
            'jqueryui',
            'underscore',
            'backbone',

            'Utils',
            'ModalFormInteractions',
            'europass/GlobalDocumentInstance',
            'i18n!localization/nls/Notification',
            'europass/TabletInteractionsView',
            'analytics/EventsController',
            'HelperManageModelUtils'
        ],
        function (
                $, jqueryui, _, Backbone,
                Utils,
                ModalFormInteractions,
                GlobalDocument,
                Notification,
                TabletInteractionsView,
                Events,
                HelperManageModelUtils
                ) {

            var GlobalController = Backbone.View.extend({
                event: new Events,
                events: {
                    "click :button.toggle-expansion": "toggleExpansionButton",
                    "click .option.toggle-expansion": "toggleExpansionButton",
                    "click :button.simple-close": "closeArea",
                    "change :radio.parent-selectable": "updateSelectionFieldParent",
                    "change :checkbox.parent-selectable": "updateSelectionFieldParent",
                    "keydown ": "onKeyDown",

                    //Tablet specific - show left and right sidebars by using buttons
                    "click #show-left-sidebar-btn": "toggleSidebar",
                    "click #show-right-sidebar-btn": "toggleSidebar",
                    "click #show-close-sidebar-btn": "closeRightSidebar",
                    "europass:drawer:opened ": "restoreSidebars",

                    //Erase All Content
                    "click #SkillsPassport_delete_btn": "_resetModel",
                    "europass:delete:confirmed": "doReset",

                    //When contact us is clicked
                    "click .message-area a[href$=\"/contact-us\"] ": "openContactUs",
                    "click .message-area a[href$=\"/contact\"] ": "openContactUs",

                    // When CEFR Levels grid show is clicked
                    "click .elp-controls .clickable-area": "triggerCEFRShow",

                    // when toggle justify is clicked
                    "click .ecl-controls .cl-justification": "toggleGlobalJustify",
                    "click .ecl-controls button span": "toggleGlobalJustify",

                    // when toggle enable name in cl signature
                    "click .ecl-controls .enable-person-name-switch": "toggleEnableNameCLSignature",

                    "europass:dragdrop:init #main-content-area": "initDragDrop",
                    "europass:dragdrop:close #main-content-area": "closeDragDrop"
                },
                onClose: function () {
                },

                //Global variables for touch positions and distances
                startx: 0,
                starty: 0,
                distv: 0,
                disth: 0,

                initialize: function (options) {
                    //global variable to check if client is a portable device TODO move to editor.jsp
                    var isTablet = (/(iPhone|iPod|iPad)/.test(navigator.userAgent) || (/Android/.test(navigator.userAgent)) || (/BlackBerry/.test(navigator.userAgent)));
                    //Global Error Handling
                    window.onerror = $.proxy(this.handleGlobalError, this);
                    if (isTablet) {
                        TabletInteractionsView.initialise();
                    }

                    //Moved from Filemanager.js, disables drag drop before WYSIWYG drag drop controller loads 
                    $(document).bind('drop dragover', function (e) {
                        //TODO investigate case where user drops the file too fast and the default action is not prevented
                        e.preventDefault();
                    });

                    this.handleConnectivityStatus();
                },

                /**
                 * When a ahref is clicked with ends with /contact-us, then open the contact form
                 */
                openContactUs: function (event) {
//			console.log("open contact us");
                    event.preventDefault();
//			event.stopPropagation();

                    var a = $(event.target);
                    href = a.attr("href");
                    if (href.indexOf("europass.cedefop.europa.eu") > 0 || href.indexOf("/editors") === 0) {

                        var html = a.closest(".message-area").html();
                        a.trigger("europass:open:contact:form", [{message: "<div class=\"message-area\">" + html + "</div>"}]);
                    }

                },

                closeRightSidebar: function () {
                    $("#show-right-sidebar-btn").click();
                },

                /**
                 * TABLET: Show hidden sidebar
                 */
                toggleSidebar: function (event) {

                    var btn = $(event.target).closest(".show-sidebar-btn");
                    var direction = btn.attr("data-effect-direction");
                    var canvas = this.$el.find(".off-canvas-wrap");
                    var topEl = this.$el.find("#header-logo-moto-small");
                    var midEl = this.$el.find("#main-content-area");
                    var overlayTop = (topEl !== undefined ? topEl.find(".transition-overlay") : undefined);
                    var overlayMid = (midEl !== undefined ? midEl.find(".transition-overlay") : undefined);
                    var topHeight = topEl.height();
                    var midHeight = midEl.prop("scrollHeight");

                    canvas.toggleClass('noactive-' + direction);
                    canvas.toggleClass('move-' + direction);

                    if (direction == "left" && canvas.hasClass("noactive-right") && canvas.hasClass("move-left")) {
                        $("<div class=\"transition-overlay \"></div>").css({height: topHeight}).prependTo(topEl).animate({"background-color": "rgba(0,0,0, 0.7)"}, 500);
                        $("<div class=\"transition-overlay \"></div>").css({height: midHeight}).prependTo(midEl).animate({"background-color": "rgba(0,0,0, 0.7)"}, 500);
                    } else if (direction === "left" && canvas.hasClass("noactive-right") && canvas.hasClass("noactive-left")) {
                        $(overlayTop).animate({"background-color": "rgba(0,0,0, 0)"}, 500).promise().done(function () {
                            $(this).remove();
                        });
                        $(overlayMid).animate({"background-color": "rgba(0,0,0, 0)"}, 500).promise().done(function () {
                            $(this).remove();
                        });
                    }
                },
                /**
                 * When a drawer opens than make sure that the left and right sidebars are closed
                 */
                restoreSidebars: function (event) {
//			console.log("restore sidebars");
                    var el = $(event.target);

                    //El is body
                    var isBody = el.is("body");

                    //El inside left aside
                    var insideLeft = el.closest("aside.left").length > 0;
                    //El inside right aside
                    var insideRight = el.closest("aside.right").length > 0;

                    if (isBody || !insideLeft) {
                        this.$el.find(".off-canvas-wrap")
                                .addClass('noactive-right')
                                .removeClass('move-right');
                    }
                    if (isBody || !insideRight) {
                        this.$el.find(".off-canvas-wrap")
                                .addClass('noactive-left')
                                .removeClass('move-left');
                    }

                },
                /**
                 * The user requested to reset the model.
                 * First show an confirmation modal
                 */
                _resetModel: function (event) {

                    if (this.isEditorEmpty() === true) {
                        $("body").trigger("europass:message:show", ["warning", Notification["warning.model.reset.already.empty"], true]);
                        return false;
                    }
                    ModalFormInteractions.confirmReset(event);
                },
                /** checks if the editor is empty
                 * @ returns boolean value 
                 * */
                // isEditorEmpty: function () {
                // 	var isEmpty = false;
                // 	var currentDocument = GlobalDocument.getDocument();
                // 	var isEmptyLearnerInfo = Utils.isEmptyObject(this.model.get("SkillsPassport.LearnerInfo"));
                // 	var isEmptyCL = Utils.isEmptyObject(this.model.get("SkillsPassport.CoverLetter"));
                //
                // 	if (currentDocument === "ECL") {
                // 		var cover = this.model.get("SkillsPassport.CoverLetter");
                // 		if (_.isUndefined(cover) || _.isNull(cover)) {
                // 			isEmpty = isEmptyLearnerInfo;
                // 		} else {
                // 			isEmpty = Utils.isEmptyObject(cover) && isEmptyLearnerInfo;
                // 		}
                // 	} else {
                // 		isEmpty = isEmptyLearnerInfo && isEmptyCL;
                // 	}
                // 	return isEmpty;
                // },

                /** checks if the editor is empty
                 * @ returns boolean value
                 * */
                isEditorEmpty: function () {

                    var modelInfo = this.model.info();

                    return modelInfo.isCVEmpty() && modelInfo.isLPEmpty()
                            && modelInfo.isCLEmpty() && modelInfo.isESPEmpty();
                },

                doReset: function (event, section, resetCL, resetCV, resetLP, resetESP) {
                    this.$el.trigger("europass:waiting:indicator:show");

                    if (resetCL) {
                        this.event.deleteCV("Erase CL");
                    }
                    if (resetCV) {
                        this.event.deleteCV("Erase CV");
                    }
                    if (resetLP) {
                        this.event.deleteCV("Erase LP");
                    }
                    if (resetESP) {
                        this.event.deleteCV("Erase ESP");
                    }

                    HelperManageModelUtils.resetOptions(this.model, resetCL, resetCV, resetLP, resetESP);

                    event.stopPropagation();
                    if (Utils.readCookie()) {//cookie exists
                        Utils.deleteCookie();
                        Utils.createCookie();
                    }

                },
                /**
                 * On click of a button toggles the display of the area with id 
                 * indicated by the data-rel-subform attribute
                 */
                toggleExpansionButton: function (event) {
                    var btn = $(event.target);
                    var rel = btn.attr("data-rel-subform");
                    var effect = btn.attr("data-expansion-effect");

                    var section = $(Utils.jId(rel));

                    switch (effect) {
                        case "fade":
                        {
                            section.fadeToggle("slow");
                            break;
                        }
                        default:
                        {
                            section.slideToggle("slow");
                            break;
                        }
                    }

                    btn.toggleClass("less");
                    btn.toggleClass("more");

                    if (btn.hasClass("more")) {
                        btn.trigger("europass:toggle:closed");
                    } else if (btn.hasClass("less")) {
                        btn.trigger("europass:toggle:opened");
                    }
                },
                /**
                 * On change of a radio or a checkbox with the class name "parent-selectable"
                 * The parent element receives the class name "selected"
                 * and the parent of the rest input fields of the family lose the class name "selected"
                 */
                updateSelectionFieldParent: function (event) {

                    var thisField = $(event.target);
                    var fieldType = thisField.is(":radio") ? "radio" : (thisField.is(":checkbox") ? "checkbox" : null);

                    var isDrivingLicence = isTablet && thisField.is(".driving-licence");
                    if (isDrivingLicence) {
                        return false;
                    }

                    if (fieldType !== null && thisField.is(":checked")) {
                        //Add class to the radio's immediate parent'
                        thisField.parent().addClass("selected");

                        if (fieldType === "radio") {
                            //remove the 'selected' class name from the rest
                            var family = thisField.attr("name");

                            thisField.closest(".selections")
                                    .find(":" + fieldType + "[name=\"" + family + "\"]").each(function (idx, field) {

                                if (thisField[0] !== field) {
                                    $(field).parent().removeClass("selected");
                                }
                            });
                        }
                    }
                    if (fieldType === "checkbox" && !thisField.is(":checked")) {
                        //Remove class to the radio's immediate parent'
                        thisField.parent().removeClass("selected");
                    }
                },
                /**
                 * When a modal dialog is open and backspace is hit, close the modal.
                 * Attention, when the event comes from a form field, we need to let the event run.
                 * EWA-437
                 * 
                 * Attention this depends on the use of Dialog from jQuery-UI
                 * @param event
                 */
                onKeyDown: function (event) {
                    var code = (event.keyCode ? event.keyCode : event.which);
                    if (code === 8 || code === 27) { //backspace or escape
                        var obj = $(event.target);

                        var drawer = this.$el.find(".drawer:visible");
                        //when an input field is text, password or textarea or has the formfield class name
                        //AND a dialog is visible, then, close the dialog by triggering a 'click' event on its close button
                        //vpol: added also :input:url for url field in redactor10-link url
                        if (obj.is("textarea, :input:text, :input:password, :input:not(:button).formfield, .cke_wysiwyg_div") === false && drawer.length > 0) {
                            drawer.siblings(".close").trigger("click");
                            event.preventDefault();
                        }
                    }
                },
                /**
                 * Hide an area indicated by the data-rel-closable-section attribute of the event target.
                 * Use the slide up effect
                 */
                closeArea: function (event) {
                    var btn = $(event.target);
                    var rel = btn.attr("data-rel-closable-section");
                    var section = $(Utils.jId(rel));

                    section.slideUp("slow");
                },
                /**
                 * Check for Browser Connection Status
                 */
                handleConnectivityStatus: function () {
                    window.addEventListener('offline', function (e) {
                        $("body").trigger("europass:message:show", ["warning global-warning", "No internet connection", false, true, "no-internet"]);
                    });
                    window.addEventListener('online', function (e) {
                        $("body").trigger("europass:message:show", ["success", "Connected to internet", true, false, 'internet']);
                    });
                },

                /**
                 * The window.onerror event is raised when either 
                 * 1.) there is an uncaught exception or 
                 * 2.) a compile time error occurs.
                 * Browsers supporting window.onerror
                 * Chrome 13+ / Firefox 6.0+ / Internet Explorer 5.5+ / Opera 11.60+ / Safari 5.1+
                 */
                handleGlobalError: function (msg, url, line, column) {
                    if (url === undefined || url === null || "" === url) {
                        return true; //ignore by suppressing
                    }
                    //Ignore messages that are not served by us, meaning the url does not contain 'static/ewa/scripts | localization | util'
                    //if the url is not towards one of our scripts
                    //var isLibraries = url.toLowerCase().indexOf( '/static/ewa/libraries'.toLowerCase() ) >= 0;

                    // var isEuropass = url.toLowerCase().indexOf('/static/ewa/'.toLowerCase()) >= 0;
                    // //console.log( " handle global error... ");
                    // //1. report this via Ajax POST!
                    // var errorStr = 'Error: ' + msg + ' Script: ' + url + ' Line: ' + line
                    // 	+ ' Column: ' + column;
                    //
                    // if (isEuropass) {
                    // 	this.reportError(errorStr);
                    // } else {
                    // 	console.log(console.log("Message: " + errorStr));
                    // }


                    var isLibraries = url.toLowerCase().indexOf('/static/ewa/libraries'.toLowerCase()) >= 0;
                    if (isLibraries) {
                        return true;
                    }
                    //Ignore messages that are served by us, but are merely libraries
                    if (url.toLowerCase().indexOf('/static/ewa/scripts/libs/'.toLowerCase()) >= 0) {
                        return true;
                    }
                    //console.log( " handle global error... ");
                    //1. report this via Ajax POST!
                    var errorStr = "Error: " + msg + "\nurl: " + url + "\nline: " + line + ' column: ' + column;
                    //console.log( "Message: " + errorStr );
                    this.reportError(errorStr);




                    var suppressErrorAlert = true;
                    // If you return true, then error alerts (like in older versions of 
                    // Internet Explorer) will be suppressed.
                    return suppressErrorAlert;
                },

                /*simulates hover effect on tablet before resetting the model.. needs refactoring?
                 // TODO CLEAN UP COMMENT On mobile not using 2 step tooltip anymore !!
                 * */
                resetModel: function (event) {

                    if (isTablet) {
                        if (TabletInteractionsView.handleTipSpot(event, "currentTarget") === false) {
                            return false;
                        }
                    }
                    this._resetModel(event);
                },

                /*
                 * Triggers the change of the preference related to the inclusion of the CEFR LEvels grid in the generated LP document
                 */

                triggerCEFRShow: function (event, include) {

                    $("body").trigger("europass:cefrgrid:show");
                },

                toggleGlobalJustify: function (event) {

                    var elem = $(event.target);

                    if (elem.is("span"))
                        elem = elem.parent("button");

                    var align = false;
                    if (elem.is(".justify")) {
                        align = true;
                    }

//			var buttonActive = this.$el.find("button:not(.disabled)");
//			var buttonDisabled = this.$el.find("button.disabled");
//			
//			//TODO css
//			if(buttonDisabled.is(".justify") && !isJustified){
//				$(buttonActive).toggleClass("disabled");
//				ctx.show = false;
//			}
//			else if (buttonDisabled.is(".unjustify") && isJustified){
//				$(buttonActive).toggleClass("disabled");
//				ctx.show = true;
//			}			

//			this.$el.find("#Compose\\:CL\\:MainBody > .value.rich-content").trigger("europass:ecl:mainbody:justify", [ elem.attr("data-rel-section"), align ]);
                    this.$el.find(".cl.compose").trigger("europass:ecl:global:justify", [align]);
                }

                , toggleEnableNameCLSignature: function (event) {

                    var _that = this;
                    var elem = $(event.currentTarget);
                    $(elem).toggleClass("enable-name");
                    var enableName = false;
                    if (elem.is(".enable-name")) {
                        enableName = true;
                    }
                    _that.$el.find(".cl.compose").trigger("europass:ecl:signature:toggle:name", [enableName]);
                }

                //WYSIYG Drag and drop view instance
                , dragDrop: null

                , initDragDrop: function (e) {

                    //No drag and drop functionality in mobile
                    if (isTablet)
                        return false;

                    var $el = $(e.currentTarget);

                    var self = this;
                    require(["views/interaction/ComposeDragDropView"], function (ComposeDragDropView) {
                        self.dragDrop = new ComposeDragDropView({
                            parentEl: $el,
                            parentView: this,
                            model: self.model,
                            SkillsPassport: self.model.get("SkillsPassport"),
                            isEditorEmpty: self.isEditorEmpty,
                            currentDocument: GlobalDocument.getDocument()
                        });

                        self.dragDrop.enableDragDrop($el);
                    });
                }

                , closeDragDrop: function (e) {

                    if (isTablet)
                        return false;

                    var $el = $(e.currentTarget);
                    //var dragDrop = {};
                    if (!$.isEmptyObject(this.dragDrop) && $.isFunction(this.dragDrop.disableDragDrop))
                        this.dragDrop.disableDragDrop($el);

                    this.dragDrop = null; //release instance	
                }
            });

            return GlobalController;
        }
);
