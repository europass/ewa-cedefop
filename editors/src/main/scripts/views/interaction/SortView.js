define(
        [
            'require',
            'jquery',
            'underscore',
            'backbone',
            'Utils',
            'xdate',
            'europass/http/WindowConfigInstance'//'europass/GlobalDocumentInstance', 'i18n!localization/nls/GuiLabel','europass/TabletInteractionsView'
        ],
        function (require, $, _, Backbone, Utils, XDate, WindowConfig) {

            var SortView = Backbone.View.extend({

                events: {
                    "europass:sort:list:moveUp": "moveUp",
                    "europass:sort:list:moveDown": "moveDown",
                    "europass:sort:list:moveTop": "moveTop",
                    "europass:autosort:date": "autoSortByDate"
                }

                , onClose: function () {
                    //perform any clean-up here
                }
                , initialize: function (options) {
                    this.model = options.model;
                }

                /**
                 * checks whether the sortable element is valid, check parent, and attributes
                 */
                , validSortElement: function (el, isLP) {

                    if (!Utils.isUndefined(el)) {

                        if (!Utils.isUndefined(el.parentNode)) {

                            if (!Utils.isUndefined(el.parentNode.parentNode)) {

                                if (isLP) {
                                    if (!Utils.isUndefined(el.parentNode.parentNode.parentNode.getAttribute('data-rel-section'))) {
                                        return true;
                                    }
                                } else {
                                    if (!Utils.isUndefined(el.parentNode.parentNode.getAttribute('data-rel-section'))) {
                                        return true;
                                    }
                                }
                            }
                        }
                    }
                    return false;
                }

                /**
                 * move element upwards
                 */
                , moveUp: function (event, ui, isLP) {
                    //var list = $(event.target);
                    if (isLP === undefined || isLP === null || isLP === "")
                        isLP = false;

                    if (!this.validSortElement(ui, isLP)) {
                        return;
                    }

                    var parentUi = $(ui).closest(".list-item");

                    if (Utils.isUndefined(parentUi))
                        return;

                    var startPos = null;
                    startPos = parentUi.attr('data-rel-index');
                    //if (isLP) startPos = parentUi.getAttribute('data-rel-item-index');

                    if (Utils.isUndefined(startPos))
                        return;

                    if (parseInt(startPos) === 0)
                        return;//first item can not be moved upwards

                    var endPos = parseInt(startPos) - 1;

                    //Get the previous list item
                    var prvEl = $(parentUi).prev();

                    //Call a function that calculates the new positions of the moving elements
                    var downPos = this.getFinalPos(parentUi, prvEl, "down");

                    var upPos = this.getFinalPos(parentUi, prvEl, "up");

                    //Add the proper classes for the transition effect and apply the new positions accordingly
                    var classA1 = this.getSortCls("A1");
                    var classA2 = this.getSortCls("A2");
                    var classB1 = this.getSortCls("B1");
                    var classB2 = this.getSortCls("B2");

                    //Do the animation
                    this.animateSort(parentUi, classA1, classA2, upPos);
                    this.animateSort(prvEl, classB1, classB2, downPos);

                    var relSection = "";
                    relSection = parentUi.parent().attr('data-rel-section');//infoEl.attr("data-rel-section");
                    //if (isLP) relSection = parentUi.parentNode.parentNode.getAttribute('data-rel-section');//infoEl.attr("data-rel-section");

                    //-------- update model:content -----------
                    var items = this.model.get(relSection);

                    if ($.isArray(items)) {
                        Utils.arrayMove(items, startPos, endPos);
                    }
                    //Remove from the element any needed aspect for the animation and trigger the custom model event for the change of a list
                    var that = this;
                    setTimeout(function () {
                        that.removeAnimCls(parentUi, classA1, classA2);
                        that.removeAnimCls(prvEl, classB1, classB2);
                        //Finally trigger a custom model event so that the listening views react to it
                        that.model.trigger("list:sort:change", relSection, startPos, endPos);
                        if (isLP) {
                            that.doElpTransition(endPos);
                        }

                    }, 1000);
                }

                /**
                 * move element downwards
                 */
                , moveDown: function (event, ui, isLP) {
                    //var list = $(event.target);
                    if (isLP === undefined || isLP === null || isLP === "")
                        isLP = false;

                    if (!this.validSortElement(ui, isLP)) {
                        return;
                    }

                    var parentUi = $(ui).closest(".list-item");

                    if (Utils.isUndefined(parentUi))
                        return;

                    var startPos = null;
                    startPos = parentUi.attr('data-rel-index');
                    //if (isLP) startPos = parentUi.getAttribute('data-rel-item-index');

                    if (Utils.isUndefined(startPos))
                        return;

                    var endPos = parseInt(startPos) + 1;

                    //Get the previous list item
                    var nxtEl = $(parentUi).next();

                    //Call a function that calculates the new positions of the moving elements
                    var downPos = this.getFinalPos(nxtEl, parentUi, "down");
                    var upPos = this.getFinalPos(nxtEl, parentUi, "up");

                    //Add the proper classes for the transition effect and apply the new positions accordingly
                    var classA1 = this.getSortCls("A1");
                    var classA2 = this.getSortCls("A2");
                    var classB1 = this.getSortCls("B1");
                    var classB2 = this.getSortCls("B2");

                    //Do the animation
                    this.animateSort(parentUi, classA1, classA2, downPos);
                    this.animateSort(nxtEl, classB1, classB2, upPos);

                    var relSection = "";
                    relSection = parentUi.parent().attr('data-rel-section');//infoEl.attr("data-rel-section");
                    //if (isLP) relSection = parentUi.parentNode.parentNode.getAttribute('data-rel-section');//infoEl.attr("data-rel-section");


                    //-------- update model:content -----------
                    var items = this.model.get(relSection);

                    if ($.isArray(items)) {

                        var itemsCount = items.length;

                        if (parseInt(itemsCount) === parseInt(endPos)) {
                            return;//the last item can not be moved down.
                        }

                        Utils.arrayMove(items, startPos, endPos);
                    }

                    //Remove from the element any needed aspect for the animation and trigger the custom model event for the change of a list
                    var that = this;
                    setTimeout(function () {
                        that.removeAnimCls(parentUi, classA1, classA2);
                        that.removeAnimCls(nxtEl, classB1, classB2);
                        //Finally trigger a custom model event so that the listening views react to it
                        that.model.trigger("list:sort:change", relSection, startPos, endPos);
                        if (isLP) {
                            that.doElpTransition(endPos);
                        }
                    }, 1000);
                }

                /**
                 * move element upwards
                 */
                , moveTop: function (event, ui, isLP) {
                    //var list = $(event.target);
                    if (isLP === undefined || isLP === null || isLP === "")
                        isLP = false;

                    if (!this.validSortElement(ui, isLP)) {
                        return;
                    }

                    var parentUi = $(ui).closest(".list-item");

                    if (Utils.isUndefined(parentUi))
                        return;

                    var startPos = null;
                    startPos = parentUi.attr('data-rel-index');
                    //if (isLP) startPos = parentUi.getAttribute('data-rel-item-index');

                    if (Utils.isUndefined(startPos))
                        return;

                    if (parseInt(startPos) === 0)
                        return;//first item can not be moved upwards

                    var relSection = "";
                    relSection = parentUi.parent().attr('data-rel-section');//infoEl.attr("data-rel-section");
                    //if (isLP) relSection = parentUi.parentNode.parentNode.getAttribute('data-rel-section');//infoEl.attr("data-rel-section");

                    //-------- update model:content -----------
                    var items = this.model.get(relSection);

                    if ($.isArray(items)) {
                        Utils.arrayMove(items, startPos, 0);
                    }
                    //Finally trigger a custom model event so that the listening views react to it
                    this.model.trigger("list:sort:change", relSection, startPos, 0);

                    if (isLP) {
                        this.doElpTransition(0);
                    }
                }

                , findRelatedIndex: function (item) {
                    var tmpPos = item.getAttribute("data-rel-item-index");
                    var pos = tmpPos === undefined || tmpPos === null ?
                            parseInt(item.getAttribute("data-rel-index")) :
                            parseInt(tmpPos);
                    return pos;
                }
                /** Function which calculates the moving elements' animation positions
                 * @param elUp: element that moves upwards
                 * @param elDown: element that moves downwards
                 * @param direction: a string that defines the direction of the moving element, in order to return the proper position
                 ***/
                , getFinalPos: function (elUp, elDown, direction) {

                    var upTop = $(elUp).position().top;
//				console.log ("moving upwards element position top: " + upTop);		
                    var downTop = $(elDown).position().top;
//				console.log ("moving downwards element position top: "  + downTop);
                    var upHeight = $(elUp).outerHeight();
//				console.log ("moving upwards element height: " + upHeight);
                    var downHeight = $(elDown).outerHeight();
//				console.log ("moving downwards element height: " + downHeight);
                    var posDown = upTop - downTop - (downHeight - upHeight);
//				console.log ("moving downwards element final position: " + posDown);
                    var posUp = downTop - upTop;
//				console.log ("moving upwards element final position: " + posUp);

                    //Return the proper position according to the element's moving direction
                    if (direction === "up") {
                        return posUp;
                    }

                    if (direction === "down") {
                        return posDown;
                    }

                }
                /*** Function which returns the proper classes that should be added in the animated element, in order to  accomplish the css transitions
                 * @param type: has four(4) possible values 
                 * A1: the start of the first animation (primarily moving element starts animating)
                 * A2: the end of the first animation ((primarily moving element ends animating)
                 * B1: the start of the second animation (secondarily moving element starts animating)
                 * B2: the end of the second animation (secondarily moving element stops animating) ***/
                , getSortCls: function (type) {
                    var prefixA = "frst-animation-";
                    var prefixB = "scnd-animation-";
                    var postfixSt = null;
                    var postfixEn = null;
                    postfixSt = "start";
                    postfixEn = "end";
                    //}
                    //Return the proper class according to the name
                    switch (type) {
                        case "A1":
                        {
                            return prefixA + postfixSt;
                            break;
                        }
                        case "A2":
                        {
                            return prefixA + postfixEn;
                            break;
                        }
                        case "B1":
                        {
                            return prefixB + postfixSt;
                            break;
                        }
                        case "B2":
                        {
                            return prefixB + postfixEn;
                            break;
                        }
                    }
                }
                /***Function which does the sorting animation
                 * @param elem: moving element (can be primary or secondary)
                 * @param clazzA: animation starting's class
                 * @param clazzB: animation ending's class
                 * @param pos: element's final position
                 ***/
                , animateSort: function (elem, clazzA, clazzB, pos) {
                    $(elem).addClass(clazzA).animate({top: pos}, 500, 'linear').promise().done(function () {
                        $(elem).addClass(clazzB);
                    }, 500);
                }

                /***Function to remove the animation's classes
                 * After the end of the animation, the transition classes are no longer needed
                 * @param elem: moving element (can be ui element or the sibling with whom they exchange positionings)
                 * @param clazzA: animation starting's class
                 * @param clazzB: animation ending's class
                 * Additionally, the top position of the element must be reset, so no jquery style will affect the element's behavior after the sorting procedure
                 ***/
                , removeAnimCls: function (elem, clazzA, clazzB) {
                    $(elem).removeClass(clazzA + " " + clazzB).css({"top": ""});
                }

                , doElpTransition: function (endPos) {
                    //get the new element
                    var clazz = "highlight";
                    var newIdStr = "ForeignLanguage[" + endPos + "]";
                    var newEl = $.find("li.list-item[id$='" + newIdStr + "']");
                    var child = $(newEl).find("dl.foreign-language-details");
                    if (child.length === 0) {
                        return;
                    }
                    child.addClass(clazz);

                    var mainScrl = $('#main-content-area').scrollTop();
                    var newTop = $(newEl).offset().top;
                    var newPos = mainScrl + newTop - 80;

                    setTimeout(function () {
                        setTimeout(function () {
                            var pos = newPos;
                            //Added more top margin in scroll position for tablets, due to inconsistencies
                            var isTablet = (/(iPhone|iPod|iPad)/.test(navigator.userAgent) || (/Android/.test(navigator.userAgent)) || (/BlackBerry/.test(navigator.userAgent)));
                            if (isTablet) {
                                pos = pos - 80;
                            }
                            $('#main-content-area').animate({scrollTop: pos}, 200, 'easeInOutQuart');
                        }, 100, newPos);
                    }, 100, newPos);
                    setTimeout(function () {
                        child.removeClass(clazz);
                    }, 3000);
                }

                , bubbleSortBydate: function (data, descending) {

                    // First loop for nulls and make two arrays ( not null period , null period data)
                    var dataPeriods = [];
                    var dataNullPeriods = [];
                    var j = 0;
                    var k = 0;

                    for (var i = 0; i < _.size(data); i++) {
                        if (_.isUndefined(data[i].from) && _.isUndefined(data[i].to))
                            dataNullPeriods[j++] = data[i];
                        else
                            dataPeriods[k++] = data[i];
                    }
                    // Then sort the not null period array by date conventions as described in EWA-1489

                    var swapped;

                    do {

                        swapped = false;
                        for (var i = 0; i < _.size(dataPeriods) - 1; i++) {

                            var dateFrom = dataPeriods[i].from;
                            var dateTo = dataPeriods[i].to;

                            if (descending) {
                                if (dateTo < dataPeriods[i + 1].to) {
                                    var temp = dataPeriods[i];
                                    dataPeriods[i] = dataPeriods[i + 1];
                                    dataPeriods[i + 1] = temp;
                                    swapped = true;
                                } else if (dateTo.diffMilliseconds(dataPeriods[i + 1].to) === 0) {
                                    if (dateFrom < dataPeriods[i + 1].from) {
                                        var temp = dataPeriods[i];
                                        dataPeriods[i] = dataPeriods[i + 1];
                                        dataPeriods[i + 1] = temp;
                                        swapped = true;
                                    }
                                }
                            }
                        }
                    } while (swapped);

                    // Finally, concat the null period data (in the end) with the period data array and return			    
                    return dataPeriods.concat(dataNullPeriods);
                }

                , autoSortByDate: function (event, data, section, el) {

                    for (var i = 0; i < _.size(data); i++) {

                        var periodFrom = data[i].from;
                        var periodTo = data[i].to;

                        var isCurrent = data[i].item !== undefined ?
                                (data[i].item.Period !== undefined ? data[i].item.Period.Current : false)
                                : false;

                        // Construct From date
                        var fromDate = new XDate();
                        if (!_.isUndefined(periodFrom)) {

                            if (!_.isUndefined(periodFrom.Year)) {

                                //XDate month is zero-indexed ( 0 = Jan, 1 = Feb etc)
                                fromDate = new XDate(periodFrom.Year, 0, 1);

                                if (!_.isUndefined(periodFrom.Month)) {

                                    //XDate month is zero-indexed ( 0 = Jan, 1 = Feb etc)
                                    var month = periodFrom.Month - 1;
                                    fromDate.setMonth(month);

                                    if (!_.isUndefined(periodFrom.Day)) {
                                        fromDate.setDate(periodFrom.Day);
                                    }
                                }
                            }
                        }

                        // Construct To date
                        var toDate = new XDate();
                        if (!_.isUndefined(periodTo)) {
                            if (!_.isUndefined(periodTo.Year)) {

                                //XDate month is zero-indexed ( 0 = Jan, 1 = Feb etc )
                                toDate = new XDate(periodTo.Year, 0, 1);

                                if (!_.isUndefined(periodTo.Month)) {

                                    //XDate month is zero-indexed ( 0 = Jan, 1 = Feb etc )
                                    var month = periodTo.Month - 1;
                                    toDate.setMonth(month);

                                    if (!_.isUndefined(periodTo.Day)) {
                                        toDate.setDate(periodTo.Day);
                                    } else {
                                        toDate.setDate(XDate.getDaysInMonth(toDate.getFullYear(), toDate.getMonth()));
                                    }
                                } else {
                                    toDate = new XDate(periodTo.Year, 11, 31);
                                }
                            }
                        } else if (!_.isUndefined(periodFrom)) {	// if there is from, and to undefined, construct the whole period

                            // If it is current get current date else set along with from
                            if (isCurrent)
                                toDate = new XDate();
                            else {

                                if (!_.isUndefined(periodFrom.Year)) {

                                    toDate = new XDate(periodFrom.Year, 0, 1);	//XDate month is zero-indexed ( 0 =Jan, 1 = Feb etc)

                                    if (!_.isUndefined(periodFrom.Month)) {

                                        //XDate month is zero-indexed ( 0 =Jan, 1 = Feb etc)
                                        var month = periodFrom.Month - 1;

                                        toDate.setMonth(month);

                                        if (!_.isUndefined(periodFrom.Day)) {
                                            toDate.setDate(periodFrom.Day);
                                        } else {
                                            toDate.setDate(XDate.getDaysInMonth(toDate.getFullYear(), toDate.getMonth()));
                                        }
                                    } else {
                                        toDate = new XDate(periodFrom.Year, 11, 31);
                                    }
                                }
                            }
                        }
                        data[i].from = fromDate;
                        data[i].to = toDate;
                    }

                    // true if it is in descending order
//				var autoSortBtn = $(this.el).find("button.autoSort.byDate");
//				var descending = autoSortBtn.hasClass("descending");
//				var ascending = autoSortBtn.hasClass("ascending");
//				
//				if(!ascending && !descending){
                    var descending = true;
                    var result = this.bubbleSortBydate(data, descending);
                    for (var i = 0; i < _.size(result); i++) {
                        this.model.set(section + "[" + i + "]", result[i].item, {silent: true});
                    }
                    this.model.trigger("model:content:changed", section, true);
                }
            });
            return SortView;
        }
);