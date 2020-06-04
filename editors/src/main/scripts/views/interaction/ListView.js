/**
 * This view is bound to numbered list elements,
 * and handles the interaction related to list items, e.g. re-indexing
 * 
 * 
 * Events related to sorting:
 * sortcreate  : This event is triggered when sortable is created.
 * sortstart : This event is triggered when sorting starts.
 * sort : This event is triggered during sorting.
 * sortstop : This event is triggered when sorting has stopped.
 * sortbeforestop : This event is triggered when sorting stops, but when the placeholder/helper is still available.
 * sortchange : This event is triggered during sorting, but only when the DOM position has changed.
 * sortupdate : This event is triggered when the user stopped sorting and the DOM position has changed.
 * sortout : This event is triggered when a sortable item is moved away from a connected list.
 * sortremove : This event is triggered when a sortable item has been dragged out from the list and into another.
 */
define(
        [
            'require',
            'jquery',
            'jqueryui',
            'jqueryui',
            'jqueryui',
            'underscore',
            'backbone',
            'Utils',
            'europass/GlobalDocumentInstance',
            'i18n!localization/nls/GuiLabel'
        ],
        function (require, $, jqueryui, jqueryui, jqueryui, _, Backbone, Utils, GlobalDocument, GuiLabel) {

            var ListView = Backbone.View.extend({
                swap: "XXX"
                , events: {
                    "sortstart ": "sortStarted"
                    , "sortstop ": "sortStopped"
                }
                , onClose: function () {
                    try {
                        if ($.data(this.$el, "sortable")) {
                            this.$el.sortable("destroy");
                        }

                    } catch (err) {
                    }

                    delete this.list;
                    delete this.startPos;
                    delete this.startListArray;
                    delete this.endListArray;

                }
                , initialize: function (options) {
                    try {
                        this.currentDocument = GlobalDocument.getPrefDocument();

                        var fixHelper = function (e, ui) {
                            ui.children().each(function () {
                                $(this).width($(this).width());
                            });
                            return ui;
                        };

                        var config = {
                            helper: function (event, draggableItem) {
                                var el = $(draggableItem);
                                el.css({
                                    "max-height": "60px",
                                    "overflow": "hidden"
                                });
                                return el;
                            },
                            handle: ".sortable_placeholder",
                            revert: true,
                            cursor: "move",
                            //Defines the opacity of the helper while sorting. From 0.01 to 1.
                            opacity: 0.5,
                            containment: "parent", // Set constraint of dragging to the document's edge
                            //Time in milliseconds to define when the sorting should start. 
                            //Adding a delay helps preventing unwanted drags when clicking on an element.
                            delay: 200,
                            //Specify which items are eligible to sort by passing a jQuery selector into the items option. 
                            //Items excluded from this option are not sortable, nor are they valid targets for sortable items. 
                            items: ".compose-list.list-item",
                            //A class name that gets applied to the otherwise white space.
                            placeholder: "item-re-ordering",
                            //Specifies which mode to use for testing whether the item being moved
                            //is hovering over another item. Possible values: intersect (default, The item overlaps the other item by at least 50%.) 
                            //and pointer (The mouse pointer overlaps the other item.)
                            tolerance: "pointer",
                            scroll: true,
                            //Defines how near the mouse must be to an edge to start scrolling (Default: 20).
                            scrollSensitivity: 2,
                            //The speed at which the window should scroll once the mouse pointer gets within the ScrollSensitivity distance.
                            scrollSpeed: 20,
                            refreshPositions: false,
                            dropOnEmpty: false
                        };
                        if (_.isString(options.sortableAxis) && options.sortableAxis !== "both") {
                            config.axis = options.sortableAxis;
                        }
                        if (_.isObject(options.sortableConfig) && !_.isEmpty(options.sortableConfig)) {
                            $.extend(config, options.sortableConfig);
                        }

                        if (_.isString(options.type) && options.type === "tbody") {
                            var helperTmp = {
                                helper: fixHelper
                            };
                            $.extend(config, helperTmp);
                        }
                        var msg = GuiLabel["Sortable.Placeholder.Text"];
                        this.dropText = msg === undefined ? "Drop here" : msg;

                        if (_.isFunction(this.$el.sortable)) {
                            this.$el.sortable(config);

                            this.$el.disableSelection();
                        }

                    } catch (err) {
                    }

                },

                cssOverflowConfigSet: {
                    "overflow": "hidden"
                },
                cssOverflowConfigUnSet: {
                    "overflow": "visible"
                },
                cssTmpConfigSet: {
                    "height": "60px",
                    "max-height": "60px",
                    "opacity": "1",
                    "margin-top": "20px"
                },
                cssTmpConfigUnSet: {
                    "height": "",
                    "max-height": "",
                    "margin-top": "0px"
                },
                overlayCls: "draggable-item-overlay",
                overlaySelector: ".draggable-item-overlay",
                easingSpeed: 500,

                /**
                 * Add the effect with the gradient overlay when dragging list items
                 * @param it
                 */
                addEffect: function (it) {
                    try {
                        var that = this;
                        it.animate(this.cssTmpConfigSet, this.easingSpeed, function () {
                            var el = $(this);
                            el.css(that.cssOverflowConfigSet);
                            el.prepend("<div class=\"" + that.overlayCls + "\"/>");
                        });
                    } catch (err) {
                    }

                },
                /**
                 * Remove the previously added effect with the gradient overlay when dragging list items
                 * @param it
                 */
                removeEffect: function (it) {
                    try {

                        it.css(this.cssTmpConfigUnSet);
                        it.css(this.cssOverflowConfigUnSet);
                        it.find(this.overlaySelector).remove();

                    } catch (err) {
                    }
                }
                /**
                 * Function to handle sorting start event.
                 * @param event
                 * @param ui
                 */
                , sortStarted: function (event, ui) {
                    try {

                        var item = ui.item;
                        item.addClass("disable-editing");
                        /** "Add field" option is not necessary to be appeared while sorting **/
                        item.siblings(".compose-list.empty").hide();
                        this.uiStartPos = item.index();

                        this.startPos = this.findRelatedIndex(item);

                        ui.placeholder.append("<span>" + this.dropText + "</span>").show(this.easingSpeed);

                        var tableEffect = item.is("tr");
                        var sortEffect = !tableEffect && item.parents(".esp").length === 0;
                        if (sortEffect) {
                            var items = item.siblings(".compose-list.list-item");
                            items.push(item);
                            for (var i = 0; i < items.length; i++) {
                                this.addEffect($(items[i]));
                                $(items[i]).addClass("is-being-dragged");
                            }
                            item.addClass("current-drag");
                        }
                        if (tableEffect) {
                            var tds = parseInt(item.attr("data-nested-tds"));
                            if (isNaN(tds)) {
                                tds = item.children("td").length;
                            }
                            ui.placeholder.html("<td class=\"empty-item-reorder\"><td colspan=\"" + tds + "\" class=\"table-item-re-ordering\"><span>" + this.dropText + "</span></td></td>");
                            ui.placeholder.css("height", item.css("height"));

                            //fix for the "jumping table" issue after sorting table rows
                            var parentDl = item.closest("dl");
                            if (parentDl.length > 0) {
                                parentDl.height(parentDl.height());
                            }
                            //various stylistic arrangements in table rows while sorting
                            if (item.is("[id^='experience']")) {
                                item.css({"margin-left": "4%"});
                            } else if (item.is("[id^='certificate']")) {
                                item.css({"margin-left": "-1.1%"});
                            }
                            //remove the effect of "dropping" while sorting table rows
                            this.$el.sortable("option", "revert", false);
                            /** Use "RefreshPositions" only on tables, as in LP- Languages section it is too heavy and hinders the proper functionality of sortables **/
                            this.$el.sortable("refreshPositions");
                        }
                    } catch (err) {
                    }

                }
                , findRelatedIndex: function (item) {
                    var tmpPos = item.attr("data-rel-item-index");
                    var pos;
                    pos = tmpPos === undefined || tmpPos === null ?
                            parseInt(item.attr("data-rel-index")) :
                            parseInt(tmpPos);
                    return pos;
                }
                /**
                 * Function to handle sorting stop event.
                 * @param event
                 * @param ui
                 */
                , sortStopped: function (event, ui) {
                    try {
                        var list = $(event.target);
                        var item = ui.item;
                        item.removeClass("disable-editing");
                        //The "Add Field" option should re-appear after the end of sorting.
                        item.siblings(".compose-list.empty").show();
                        var sortEffect = !item.is("tr") && item.parents(".esp").length === 0;
                        if (sortEffect) {
                            var items = item.siblings(".compose-list.list-item");
                            items.push(item);
                            for (var i = 0; i < items.length; i++) {
                                this.removeEffect($(items[i]));
                                $(items[i]).removeClass("is-being-dragged");
                            }
                        }

                        var movedItem = ui.item;
                        //remove the options that were applied while sorting
                        this.removeEffect(movedItem);
                        movedItem.removeClass("current-drag");

                        //Setting the value of a style property to an empty string â€” e.g. $('#mydiv').css('color', '') â€” 
                        //removes that property from an element if it has already been directly applied, 
                        //whether in the HTML style attribute, through jQuery's .css() method, 
                        //or through direct DOM manipulation of the style property.
                        var uiStartPos = this.uiStartPos;
                        var targetPos = movedItem.index();
                        var startPos = this.startPos;
                        var endPos = startPos;


                        //Attention: Because jQuery's implementation of :nth- selectors is strictly derived from the CSS 
                        //specification, the value of n is "1-indexed", meaning that the counting starts at 1.
                        var targetIndex = targetPos + 1;
                        if (uiStartPos > targetPos) {
                            //moved upwards
                            targetIndex = targetIndex + 1;
                            //element after
                        } else if (uiStartPos < targetPos) {
                            //moved downwards
                            targetIndex = targetIndex - 1;
                            //element before
                        } else {
                            return;
                        }
//					console.log("uiStartPos: '"+uiStartPos+"'\ntargetPos: '"+
//							targetPos+"'\nstart: '"+startPos+"'\nend:'"+endPos+
//							"'\ntargetIndex: '"+targetIndex+"'");

                        var guidingEl = this.$el.find(".compose-list.list-item:nth-child(" + (targetIndex) + ")");
                        if (guidingEl.length > 0) {
                            endPos = this.findRelatedIndex(guidingEl);

                        }

//					console.log("start: '"+startPos+"'\n end:'"+endPos+"' ");
                        if (startPos === endPos) {
                            return;
                        }


                        //---------- update model:prefs SILENTLY ------------
                        var infoEl = list;
                        if (infoEl.is("tbody")) {
                            infoEl = list.closest("table");
                        }
                        var relSection = infoEl.attr("data-rel-section");
                        //Decide for which documents should the prefs be updated
                        var relPrefDocument = infoEl.attr("data-bind-document");
                        var prefDocument = [];
                        if (relPrefDocument === undefined || relPrefDocument === null || relPrefDocument === "") {
                            prefDocument = [this.currentDocument];
                        } else {
                            prefDocument = relPrefDocument.split(" ");
                        }
                        for (var i = 0; i < prefDocument.length; i++) {
                            var prefSection = Utils.removeSkillsPassportPrefix(relSection) + ".array";
                            this.model.preferences.reorderArrayPerDocument(prefDocument[i], null, prefSection, startPos, endPos);
                        }

                        //-------- update model:content -----------
                        var items = this.model.get(relSection);
                        if ($.isArray(items)) {
                            Utils.arrayMove(items, startPos, endPos);
                        }

                        //Finally trigger a custom model event so that the listening views react to it
                        this.model.trigger("list:sort:change", relSection, startPos, endPos);


                    } catch (err) {
                    }

                }

            });

            return ListView;
        }
);