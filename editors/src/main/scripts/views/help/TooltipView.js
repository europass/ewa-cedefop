define(
        [
            'jquery',
            'underscore',
            'backbone',
            'Utils',
            'hbs!templates/help/tooltip',
            'hbs!templates/help/modal',
            'hbs!templates/help/entry',
            'i18n!localization/nls/EditorHelp',
            'europass/GlobalHelpOptionInstance'//	 ,'europass/TabletInteractionsView'
        ],
        function ($, _, Backbone, Utils, TooltipTemplate, modalHtmlTemplate, entryHelpTemplate, LabelsHelp, GlobalHelpOption) {//TabletInteractionsView

            var TooltipView = Backbone.View.extend({

                isRendered: false

                , section: null

                , events: {
                    "mouseenter .tip.spot": "showTip",
                    "mouseleave .tip.spot": "removeTip",
                    "mouseenter .tip.mouse": "showSectionTip",
                    "mouseleave .tip.mouse": "removeAll",

                    "europass:tooltip:show": "showTip",
                    "europass:tooltip:remove": "removeTip",
                    "europass:tooltip:removeAll": "removeAll"

                }
                , initialize: function () {
                    this.context = {};
                    this.position = null;
                    this.message = null;
                    this.tipHtml = null;
                    this.tooltipTemplate = TooltipTemplate;
                }
                , onClose: function () {}
                /**
                 * el: the element, that would be the 'a href', a 'button', or dl section
                 * position: default is top
                 * owner: called for mousemove tip or for a spot tip [null, 'mouse', 'spot']
                 */
                , setContext: function (el, position, owner) {
                    var messageEl = el.find('span.data-title').first();
                    if (messageEl.length === 0)
                        messageEl = el.siblings('span.data-title').first();

                    if (messageEl.length === 0)
                        return {};

                    var message = messageEl.html();

                    //Temporary fix for tooltip of ESP attachments
                    var replaceKey = el.attr("data-tip-replacement-key");
                    var replaceTxt = el.attr("data-tip-replacement-text");
                    if (el.hasClass("active-tip-text")
                            && replaceKey !== undefined && replaceKey !== null
                            && replaceTxt !== undefined && replaceTxt !== null) {
                        message = Utils.replaceKey(message, replaceKey, replaceTxt);
                    }

                    //safety, if not the correct el
                    if ((message === undefined || message === null || message === "") && (owner === 'mouse')) {
                        message = LabelsHelp['Modify.default'];
                    }

                    var context = {};
                    context.message = message;
                    context.position = position;

                    var tipHtml = this.tooltipTemplate(context);
                    return $(tipHtml);
                }

                , showTip: function (event) {
                    /*if ( isTablet && event.type === "mouseenter" ){
                     TabletInteractionsView.clearSelectedUncheckedItems ( event );
                     return false;
                     }*/
                    var el = $(event.target);

                    if (!el.is(".tip.spot"))
                        el = el.closest(".tip.spot");

                    //if the tip from mousemove is active, then make inactive
                    this.removeOverlappingTip(el);

                    var position = el.attr("data-tip.position") || "top";

                    var tipEl = this.setContext(el, position, 'spot');

                    if (_.isFunction(tipEl.remove))
                        if (_.isFunction(tipEl.remove.css))
                            tipEl.remove().css({top: 0, left: 0, display: 'block'});

                    var jEl = $(el);

                    //ATTENTION: If the jEl is absolutely positioned, place the tooltip NEXT-TO it
                    if (jEl.css("position") === "absolute") {
                        el.after(tipEl);
                    } else {
                        el.prepend(tipEl); //add it inside the button which is to be relatively positioned - if not already absolutely positioned!
                    }

                    //TODO Investigate why this position prevents the "Erase" tooltip from appearing on IE
                    if (jEl.css("position") !== "absolute") {
                        jEl.css({position: "relative"});
                    }

                    this.setPosition(el, position, true);
                }
                /**
                 * Find the newly added tooltip
                 */
                , findTooltip: function (el) {
                    var tooltip = el.find('.tooltip');
                    if (tooltip.length === 0) {
                        tooltip = el.next('.tooltip');
                    }
                    return tooltip;
                }
                /**
                 * Sets the tooltips' position
                 * 
                 * The tooltips' coordinates depends upon its nature, 
                 * whether is a switch button, delete button, add-link button 
                 * or a spot-questionmark, 
                 * or a ClickToAdd button on a li or on a tr, 
                 * or is a dl section.
                 * 
                 * el: is the event target (a spot-questionmark, a switch-delete-link button, a dl-section, a clickToAdd button)
                 * position: is default top
                 * isSpot: is a boolean parameter for whether is a spot-questionmark or not
                 * 
                 * The method setPosition is not used by the mouseenter-follow-mouse tooltip (showMouseTip)
                 * 
                 * the top position for clickToAdd and edit sections: displays tooltip in the top-right-corner of the el
                 * the top position for spot and switch buttons: displays tooltip in the top-center of the el
                 */
                , setPosition: function (el, position, isSpot) {
                    //el is the target of the tooltip, e.g. the a href with the questionmark
                    var tooltip = this.findTooltip(el);
                    if (tooltip === undefined || tooltip.length === 0 || tooltip === null) {
                        return;
                    }
                    if (isSpot === true) {
                        this.setSpotPosition(el, tooltip, position);
                    } else {
                        this.setSectionPosition(el, tooltip, position);
                    }
                }
                /**
                 * For Spot Tooltips
                 */
                , setSpotPosition: function (el, tooltip, position) {
                    var tipWidth = tooltip[0].offsetWidth;
                    var tipHeight = tooltip[0].offsetHeight;

                    var jEl = $(el);
                    var jElPosition = jEl.position();
                    var pos = {
                        top: jElPosition.top,
                        left: jElPosition.left,
                        width: el[0].offsetWidth,
                        height: el[0].offsetHeight
                    };
                    if (jEl.css("position") !== "absolute") {
                        pos.top = 0;
                        pos.left = 0;
                    }
                    if (jEl.css("position") === "absolute") {
                        //Added safety net on parseInt for NaN 
                        var marginTop = parseInt(jEl.css("margin-top").replace("px", "")) || 0;//!isNaN(marginTop)
                        var marginLeft = parseInt(jEl.css("margin-left").replace("px", "")) || 0;//!isNaN(marginTop)

                        pos.top = pos.top + marginTop;
                        pos.left = pos.left + marginLeft;
                    }
                    var tp = {};

                    var extraSpace = jEl.hasClass("tip-adj") ? 0 : 30;//if exists moves tooltip further up
                    var closerTop = jEl.hasClass("tip-closerTop") ? 15 : 0;//if exists moves tooltip closer
                    var extraLeft = jEl.hasClass("tip-addLeft") ? 20 : 0;
                    var extraRight = jEl.hasClass("tip-addRight") ? 20 : 0;

                    var classes = jEl.attr("class");
                    var removeLeft = 0;
                    var addtop = 0;

                    /*Use it as tip-removeLeft-10pixels for remove left disposition */
                    if (classes.indexOf("tip-removeLeft-") > 0) {
                        var start = classes.lastIndexOf("tip-removeLeft-") + 15;
                        var end = classes.lastIndexOf("pixels");
                        removeLeft = classes.substring(start, end);

                    }
                    /*Use it as tip-addTop-10pixels for adding top pixels*/
                    if (classes.indexOf("tip-addTop-") > 0) {
                        var start2 = classes.lastIndexOf("tip-addTop-") + 11;
                        var end2 = classes.lastIndexOf("pixels");
                        addtop = classes.substring(start2, end2);

                    }

                    //var addRight = $(jEl[]);
                    //$("div[class^='btn-']")

                    switch (position) {
                        case 'top':
                            tp.top = pos.top - tipHeight - extraSpace + closerTop - Number((isNaN(addtop) ? 0 : addtop));
                            tp.left = pos.left + (pos.width / 2) - (tipWidth / 2);
                            break;
                        case 'bottom'://not tested
                            tp = {top: pos.top + pos.height + extraSpace, left: pos.left + pos.width / 2 - tipWidth / 2};
                            break;
                        case 'left': //not tested
                            tp = {top: pos.top + pos.height / 2 - tipHeight / 2, left: pos.left - tipWidth};
                            break;
                        case 'right': //not tested
                            tp = {top: pos.top + pos.height / 2 - tipHeight / 2, left: pos.left + pos.width};
                            break;
                        case 'top-left':
                            tp.top = pos.top - tipHeight - extraSpace + closerTop - Number((isNaN(addtop) ? 0 : addtop));
                            tp.left = pos.left - extraLeft + Number((isNaN(removeLeft) ? 0 : removeLeft));
                            break;
                        case 'top-right':
                            tp.top = pos.top - tipHeight - extraSpace + closerTop - Number((isNaN(addtop) ? 0 : addtop));
                            tp.left = pos.left - tipWidth + pos.width + extraRight;
                            break;
                    }
                    tooltip.css(tp).addClass(position);
                }
                /**
                 * For Section Tooltips
                 */
                , setSectionPosition: function (el, tooltip, position) {
                    var tipWidth = tooltip[0].offsetWidth;
                    var tipHeight = tooltip[0].offsetHeight;

                    var jEl = $(el);
                    var elWidth = el[0].offsetWidth;
                    var jElPosition = jEl.position();
                    var pos = {
                        top: jElPosition.top,
                        left: jElPosition.left + elWidth,
                        width: elWidth,
                        height: el[0].offsetHeight
                    };
                    if (jEl.css("position") !== "absolute") {
                        pos.top = 0;
                        pos.left = elWidth;
                    }
                    if (jEl.css("position") === "absolute") {
                        pos.top = pos.top + parseInt(jEl.css("margin-top").replace("px", ""));
                        pos.left = pos.left + parseInt(jEl.css("margin-left").replace("px", ""));
                    }
                    var tp = {};
                    var extraSpace = jEl.hasClass("tip-adj") ? 0 : 30;
                    switch (position) {
                        case 'top':
                            tp.top = pos.top - tipHeight - extraSpace;
                            tp.left = pos.left - tipWidth;
                            break;
                        case 'bottom'://not tested
                            tp = {top: pos.top + pos.height + extraSpace, left: pos.left + pos.width / 2 - tipWidth / 2};
                            break;
                        case 'left': //not tested
                            tp = {top: pos.top + pos.height / 2 - tipHeight / 2, left: pos.left - tipWidth};
                            break;
                        case 'right': //not tested
                            tp = {top: pos.top + pos.height / 2 - tipHeight / 2, left: pos.left + pos.width};
                            break;
                    }
                    tooltip.css(tp).addClass(position);
                }

                /**
                 * Hides the (section) tooltip meaning clickToAdd and edit sections tooltips 
                 * when the mouse enters spot-questionmarks and switch-delete-link buttons
                 * so to display the buttons tooltip
                 */
                , removeOverlappingTip: function (el) {
                    //there could be more than one
                    $("body").find(".tooltip").each(function (idx, el) {
                        //ATTENTION: Remove instead of hide and let it be redrawn
                        //$(el).hide();
                        $(el).remove();
                    });
                }

                /**
                 * When the mouse leaves the spot or button object, then the section tooltip should be shown again 
                 * the section tooltips are temporarily hidden (not removed) by temporaryHideMouseTip
                 */
                //@Deprecated
                /*,unHideMouseTip: function(el){
                 
                 //there could be more than one
                 if (this.$el.closest("body").find(".tooltip")!= undefined && this.$el.closest("body").find(".tooltip").length>0){
                 this.$el.closest("body").find(".tooltip").each(function(idx, tooltip){
                 $(tooltip).show();
                 }) ;
                 }
                 
                 }*/

                /**
                 * Removes all tooltips, except those that are created for modal help.
                 * When the modal help is called, the mouseleave events may still run, which removes tooltips.
                 * But we don't want the modal help tooltips to be removed.
                 * The modal's help tooltips have the 'help' class
                 */
                , removeAll: function (event) {
                    $("body").find(".tooltip").each(function (idx, tooltip) {
                        //check if the tooltip belongs to the modal help.  
                        $(tooltip).remove();
                    });
                }
                /**
                 * Removes tooltips from objects like buttons
                 */
                , removeTip: function (event) {

                    //ATTENTION - do not show it, rather let it be drawn again.
                    /*if (event.handleObj.selector === ".tip.spot" ){//&& event.handleObject.type=mouseover
                     this.unHideMouseTip(event);
                     }*/
                    //console.log("remove tooltip");
                    var el = $(event.target);
                    //console.log("remove tooltip el: "+ el.attr("class")+", "+el.attr("id") );
                    if (el.hasClass("tooltip")) {
                        //console.log("remove tooltip - has");
                        el.remove();
                    } else if (el.siblings(".tooltip").length > 0) {
                        el.siblings(".tooltip").remove();
                    } else if (el.parents().siblings(".tooltip").length > 0) {
                        el.parents().siblings(".tooltip").remove();
                    } else {
                        //console.log("remove tooltip - find");
                        el.find(".tooltip").remove();
                    }
                }

                /**
                 * Displays the section tips
                 */
                , showSectionTip: function (event) {
                    var el = $(event.currentTarget);

                    //First remove any tooltip inside the target area!
                    this.removeTip(event);

                    var tipEl = this.setContext(el, 'top', 'mouse');
                    tipEl
                            .remove()
                            .css({top: 0, left: 0, display: 'block'});
                    /* If the el is a tr, then we cannot simply prepend the tip html inside the tr,
                     * 	rather find the first td and prepend it there.
                     * However the el remains the tr
                     */
                    if (el.is("tr")) {
                        $(el.find("td")[0]).prepend(tipEl);
                    } else {
                        el.prepend(tipEl);
                    }
                    this.setPosition(el, 'top', false);
                }

                /**
                 * @Deprecated for now...
                 * Shows the section tips, which trails the mouse movements
                 */
                , showMouseTip: function (event) {

                    //var el = $(event.target);
                    var el = $(event.currentTarget);
                    this.removeTip(event);
                    var tipEl = this.setContext(el, 'top', 'mouse');
                    tipEl.remove().css({top: 0, left: 0, display: 'block'});
                    /* If the el is a tr, then we cannot simply prepend the tip html inside the tr,
                     * rather find the first td and prepend it there.
                     * However the el remains the tr
                     */
                    if (el.is("tr")) {
                        $(el.find("td")[0]).prepend(tipEl);
                    } else {
                        el.prepend(tipEl);
                    }

                    var tooltip = el.find(".tooltip");
                    var tipWidth = tooltip[0].offsetWidth;//Returns the width of an element, including borders and padding if any, but not margins
                    var tipHeight = tooltip[0].offsetHeight;// 	Returns the height of an element, including borders and padding if any, but not margins

                    tooltip.removeClass('top bottom left right');

                    $(el).mousemove(function (e) {
                        var x = e.pageX - $(el).offset().left;
                        var y = e.pageY - $(el).offset().top;
                        var pos = {top: y,
                            left: x,
                            width: el[0].offsetWidth,
                            height: el[0].offsetHeight
                        };
                        var tp = {top: pos.top - tipHeight - 2, left: pos.left - tipWidth / 2};

                        //ugly workaround...
                        if (el.is("tr")) {
                            tp.top = pos.top - 2;
                        }
                        tooltip.css(tp).addClass("top");
                    });
                }
            });
            return TooltipView;
        }
);