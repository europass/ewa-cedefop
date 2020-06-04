/* This module is responsible for -initially- handling the behavior of 
 * TabletInteractionsView Object: 
 * Handles tablet touch events  , but due to its location can be further extended to take on 
 * broader tablet behavior control responsibilities.
 * */
define(
        [
            'jquery',
            'underscore',
            'backbone',
            'ModalFormInteractions',
            'Utils'
        ],
        function ($, _, Backbone, ess, Utils) {

            var TabletInteractionsView = {

            };
            //global variable to check if client is a portable device
            this.isTablet = (/(iPhone|iPod|iPad)/.test(navigator.userAgent) || (/Android/.test(navigator.userAgent)) || (/BlackBerry/.test(navigator.userAgent)));

            TabletInteractionsView.initialise = function () {
                /** Add viewport meta tag in <head> to disable user zooming for all tablets.
                 * */
                $("head").append('<meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1, user-scalable=no">');
            };
            /*touchListener module, for tablets (only) binds itself on .touchspot elements for touchstart and click events
             * 
             * */
            TabletInteractionsView.touchListener = function (el, target) {
                /* attaching tablet hover effect event handler on tip spots  ,.non-empty-indicator  */
                $(el).find(target).on('click', function (event) {
                    TabletInteractionsView.handleTipSpot(event);
                });
            };
            /* checkIfTooltipShown :  (looks for first child or sibling of element and lastly for parent)
             * @param: the element to make the check on
             * */
            TabletInteractionsView.checkIfTooltipShown = function (el) {
                //if element is not a tip spot, find closest ancestor that is
                el = (!$(el).is(".tip.spot") ? el.closest('.tip.spot') : el);

                var childIsTooltip = (el.children(0).length > 0 && el.children(0).is(".tooltip,.in"));
                //if there are siblings
                var siblingIsTooltip = ((el.siblings(0).length > 0 ?
                        // in case there are more than one tip spots as siblings, get only the next element , else check all siblings 
                                (el.siblings(0).is(".tip.spot") ? el.next().is(".tooltip,.in") : el.siblings(0).is(".tooltip,.in")) : ""));		// if no siblings exist, check ancestors	
                var result = childIsTooltip || siblingIsTooltip;

                return result;
            };
            /*addTapHover removes all occurences of the tap-hover class in the document and adds it to a given element
             * @param $el */
            TabletInteractionsView.addTapHover = function (el) {
                $(".tap-hover").removeClass("tap-hover");
                $(el).addClass("tap-hover");
            };
            /*handleTipSpot: tablet controller which shows tooltips on .touchspot 
             * elements before activating them
             * @param: event (touchstart, click, change) 
             * @param: element type (target or current target)
             * @returns false for click,change events when tooltip isn't shown yet
             * */
            TabletInteractionsView.handleTipSpot = function (event, type) {
                //default target
                var el = $(event.currentTarget);

                if (type != undefined) {
                    switch (type) {
                        case "target":
                            el = $(event.target);
                            break;
                        case "currentTarget":
                            el = $(event.currentTarget);
                    }
                }
                var isTooltipshown = TabletInteractionsView.checkIfTooltipShown(el);

                var addElements = [el];
                var removeElements = [$(".tap-hover")];

                if (isTooltipshown) {
                    $(".tap-hover").removeClass("tap-hover");
                    $(removeElements).each(function () {
                        this.addClass("tap-hover");
                    });
                    el.trigger("europass:tooltip:remove");
                } else {
                    el.trigger("europass:tooltip:show");
                    $(addElements).each(function () {
                        this.addClass("tap-hover");
                    });
                    return false;
                }
            };
            /*	Refactored method, ported from each compose view to Tablet interactions 
             *  @parameter fn: the ModalForminteractions object on which to call openForm on
             *  @param opensImmediately: if true, opens the modal without checking anything
             * */
            TabletInteractionsView.handleModalForm = function (event, fn, opensImmediately) {
                if (!touchmoved) {
                    var el = $(event.target);

                    var open = true;
                    var canOpenModal = el.hasClass('opens-modal-form');
                    var openImmediately = el.is('button.edit.opens-modal-form') | opensImmediately;
                    if (!canOpenModal) {
                        el = $(event.currentTarget);//the topmost target in the DOM tree
                    }

                    if (!openImmediately && isTablet /*&& !isAttachment*/) {
                        open = TabletInteractionsView.tapped(event);
                    }
                    el.trigger("europass:tooltip:removeAll");
                    if (open) {
                        if (openImmediately) {					//in the case we call to open immediately from foreign languages, remove the section hover effect
                            $("button.tap-hover.menu,tap-hover,.foreign-language-skills").removeClass("tap-hover");
                            el.trigger("europass:waiting:indicator:show");
                            if (_.isFunction(fn.openForm))
                                fn.openForm(event);
                        } else {
                            //el.trigger("europass:waiting:indicator:show");
                            if (_.isFunction(fn.openForm))
                                fn.openForm(event);
                        }
                    }
                }
            };
            var touchmoved = false;
            TabletInteractionsView.startListeners = function () {
                $('body').on('touchmove', function (e) {
                    touchmoved = true;
                });
                $('body').on('touchend', function (e) {
                    touchmoved = false;
                });
                $('body').on('touchstart', function (e) {
                    touchmoved = false;
                });
            }
            /* Refactored method, ported from each compose view to Tablet interactions 
             * @returns true to open the model (if second tap)
             * @returns false to not open model (if single tap)
             * @returns false for swipe
             * if the element already has the tap-hover class, then perform the mouse click event.
             * if the element doesn't have the tap-hover class, then assign the hover class, and remove it from other elements.
             *  */
            TabletInteractionsView.tapped = function (event) {

                var el = $(event.target),
                        touchable = 'ontouchstart' in document.documentElement,
                        open = false,
                        currentDocument = Utils.getDocumentFromWindow(),
                        foreignSkills = []; 			//the foreign skills section has a different markup than usual and needs special attention

                //event.which normalizes button presses (mousedown and mouseupevents), reporting 1 for left button, 2 for middle, and 3 for right.;
                if (touchable && event.which === 1) {
                    if (!el.hasClass('opens-modal-form')) {
                        el = $(event.currentTarget);
                    } else {
                        foreignSkills = el.closest(".foreign-language-skills.empty");
                        if (foreignSkills.length) {
                            // Add tap hover to the parent element of the empty foreign language skills div. (in order for the styling to be correct)
                            el = foreignSkills;
                        }
                    }
                    if (el.length > 0) {

                        if (currentDocument === "CV") {
                            //check for nested
                            var nested = ($(event.target).hasClass("nested-modal") || $(event.target).closest(".nested-modal").length > 0) && (!$(event.currentTarget).hasClass("nested-modal"));
                            if (nested) {
                                return false;
                            }
                        }
                        if ((el.hasClass("opens-modal-form") && !el.hasClass("edit")) || foreignSkills.length) {
                            if (el.is(".tap-hover")) {
                                $(".tap-hover").removeClass("tap-hover");
                                open = true;
                            } else {
                                $(".tap-hover").removeClass("tap-hover");
                                //remove tap-hover class from other elements
                                switch (currentDocument) {

                                    case "LP":
                                        $([el.closest("td.empty"), el]).each(function () {
                                            this.addClass("tap-hover");
                                        });
                                        break;
                                    case "ESP":
                                    case "CV":
                                    case "CL":
                                        el.addClass("tap-hover");
                                }
                            }
                        } else {
                            open = true;
                        }
                        return open;
                    }
                }
            };
            /*handleButton performs the hover effect operations when a button is touched
             * @parameter fn: the callback function;
             * 	*/
            TabletInteractionsView.handleButton = function (event) {
                var el = $(event.currentTarget);
                var isAttachment = el.is("button.open-linked-attachments");
                var isDelete = el.is("button.delete");
                if (isAttachment)
                    el = $(event.target);

                if (TabletInteractionsView.checkIfTooltipShown(el)) {
                    if (isAttachment) {
                        if (event.target !== event.currentTarget) {
                            el.trigger("europass:tooltip:remove");
                            $(".tap-hover").removeClass("tap-hover");
                        }
                    } else if (isDelete) {
                        el.trigger("europass:tooltip:remove");
                        $(".tap-hover").removeClass("tap-hover");
                        $([el, el.closest("menu")]).each(function () {
                            this.addClass("tap-hover");
                        });
                    }
                } else {
                    $("button.tap-hover,menu.tap-hover").removeClass("tap-hover");
                    $([el, el.closest("menu")]).each(function () {
                        this.addClass("tap-hover");
                    });
                    el.trigger("europass:tooltip:show");
                    return false;
                }
            };
            TabletInteractionsView.handleSignatureDeletion = function (event) {

                var tar = $(event.target);
                if (TabletInteractionsView.checkIfTooltipShown(tar)) {
                    tar.trigger("europass:tooltip:removeAll");
                    $(tar).removeClass("tap-hover");
                } else {
                    tar.trigger("europass:tooltip:show");
                    $(tar).addClass("tap-hover");
                    return false;
                }
            };
            TabletInteractionsView.handleAttachment = function (event, target) {

                var el = $(event.target);

                if (TabletInteractionsView.checkIfTooltipShown(el)) {
                    $(el).removeClass("tap-hover");
                    el.trigger("europass:tooltip:remove");
                } else {
                    $("button.tap-hover,menu.tap-hover,a.tap-hover").removeClass("tap-hover");
                    $([el, el.closest("menu")]).each(function () {
                        this.addClass("tap-hover");
                    });
                    el.trigger("europass:tooltip:show");
                    return false;
                }
            };
            TabletInteractionsView.handleAttachmentModalPreview = function (event) {
                var el = $(event.target);

                if (TabletInteractionsView.checkIfTooltipShown(el)) {
                    // the second call actually opens the modal
                    if (event.target !== event.currentTarget) {
                        el.trigger("europass:tooltip:remove");
                        $(".tap-hover").removeClass("tap-hover");
                    }
                } else {
                    $("button.tap-hover,menu.tap-hover").removeClass("tap-hover");
                    $([el, el.closest("menu")]).each(function () {
                        this.addClass("tap-hover");
                    });
                    el.trigger("europass:tooltip:show");
                    return false;
                }
            };
            /* handleDrivingLicence is responsible for simulating hover effects for the driving licence form view elements 
             * after removing any tap-hover or selected classes on elements, it handles 3 states:
             * first click on an element: shows the tooltip and adds .tap-hover
             * second: removes tap hover and the tooltip, adds .selected on the element and checks the checkbox
             * third: unselects the checkbox
             * 
             * There is provision for webkit browsers firing events twice and return or return false is used.
             * */
            TabletInteractionsView.handleDrivingLicence = function (event) {

                var el = $(event.target),
                        area = $(event.currentTarget), //li
                        input = $(area).find("input");

                /*hotfix for click being triggered twice in mozilla-chrome for label elements
                 * description here: http://bugs.jquery.com/ticket/10673  
                 */
                if (el.is(".css-label.checkbox-label")) {
                    return;
                }

                /* clear left over selected li elements that have not been checked*/
                $('li.tip.selected>input:checked').not(
                        'li.tip.selected>input:checked').parent().removeClass('selected');

                $(".tap-hover").not(area).removeClass("tap-hover");

                // handle 1st click on a list element
                if (!area.is(".selected") && !area.is(".tap-hover")) {
                    area.addClass("tap-hover").removeClass("selected").trigger(
                            "europass:tooltip:show");
                    return false;
                } else if (area.is(".tap-hover")) {
                    area.removeClass("tap-hover").addClass("selected").trigger(
                            "europass:tooltip:remove");
                    input.prop("checked", true);
                } else if (area.is(".selected") || area.is(" input:checked")) {
                    area.removeClass("selected").removeClass("tap-hover");
                    input.prop("checked", false);
                }
                // hotfix for clicks on divs being triggered twice
                if (el.is("div.icon"))
                    return false;
            };
            /*handleForeignLanguageSection enables opening the other languages modal form 
             * from the whole other skills section instead just from the nested opens modal form div inside it*/
            TabletInteractionsView.handleForeignLanguageSection = function (event) {

                //var openImmediately = true,
                //opensModal = el.hasClass("opens-modal-form"),el = $(event.currentTarget),
                foreign_language_section = $(".foreign-language-skills");
                foreign_language_opens_modal = foreign_language_section.find(".Skills");

                if (foreign_language_section.hasClass("tap-hover")) {
                    /*if (opensModal){
                     //event.target = el[0];
                     }else{ // is the Section, so pass the div which can open the modal form 
                     //event.target = $(event.currentTarget).find(".opens-modal-form")[0];
                     }*/
                    //$(".tap-hover").removeClass("tap-hover");
                    //TabletInteractionsView.handleModalForm (event, ModalFormInteractions, openImmediately, currentTarget );
                    return;
                } else {
                    if (foreign_language_opens_modal.hasClass("empty")) {
                        TabletInteractionsView.addTapHover(foreign_language_section);
                    } else {
                        TabletInteractionsView.addTapHover(foreign_language_opens_modal);
                    }
                    return false;
                }
            };

            return TabletInteractionsView;
        }
        );
