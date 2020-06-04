define(
        [
            'jquery',
            'jqueryui',
            'underscore',
            'backbone',
            'Utils',
            'views/interaction/MultiFieldView'],
//	'i18n!localization/nls/EditorHelp','europass/TabletInteractionsView',

        function ($, jqueryui, _, Backbone, Utils, MultiFieldView) {
//			EditorHelp, TabletInteractionsView, 

            var FixedValueListInputView = MultiFieldView.extend({

                events: _.extend({
                    "blur input.formfield.copy": "checkEmpty",
                    "click ul.custom_select_single:not(.custom_select_multi)": "handleMenu",
                    "click ul.custom_select_single:not(.custom_select_multi) ul li": "setValue"
                }, MultiFieldView.prototype.events)

                , handleMenu: function (event) {
                    if ($(event.target).is('li.init') || $(event.target).is('.arrow')) {
                        $(event.target).parent("ul.custom_select.custom_select_single").toggleClass('active');
                    }
                }
                , setValue: function (event) {
                    $("ul.custom_select.custom_select_single").removeClass('active');
                    $(event.target).closest("ul.custom_select.custom_select_single").children('.init').addClass('selected').html($(event.target).html());
                    var selectElem = $(event.target).closest("ul.custom_select.custom_select_single").parent('div').find('select');
                    selectElem.val($(event.target).attr('data-value'));

                }
                , enableFunctionality: function (parent) {

                    MultiFieldView.prototype.enableFunctionality.call(parent);
                    var children = parent.children();
                    var childrenNo = children.length;
                    var hasChildren = (childrenNo && childrenNo > 0);

                    //The following attributes are set only to the parent element
                    parent.attr("ismax", ((this.maxItemsAllowedToAdd && hasChildren && (childrenNo >= this.maxItemsAllowedToAdd)) ? true : false));

                    parent.attr("added", (hasChildren ? (childrenNo - 1) : 0));

                    var isPrepopulated = {};

                    //Prepare the sourceClone
                    var firstChild = parent.children(":first");
                    this.sourceClone = this.prepareClone(childrenNo, this.prepareCloneSource(firstChild));

                    //Add remove buttons to the already existing elements as these are rendered by the page
                    if (hasChildren) {
                        var that = this;
                        children.each(function () {
                            var composite = $(this);

                            composite.find(":input:not(button)").each(function (idx, el) {
                                var input = $(el);
                                that.disableSelectAndInput(composite, input);
                            });

                            var isEmpty = that.isCompositeEmpty(composite);
                            isPrepopulated[ composite ] = !isEmpty;
                            if (!isEmpty) {
                                //Add the Remove button
                                that.appendRemoveButton(composite);
                            }
                        });
                    }

                    //If the form loads with data, then we need to add an extra field at the bottom
                    if (isPrepopulated[firstChild]) {
                        this.appendField();
                    } else {
                        this.appendAddButton(firstChild);
                    }
                    this.populateLimitedMulipliables();

                    $("ul.custom_select.custom_select_single ul").children('li').each(function (idx, elem) {
                        if ($(elem).attr('selected')) {
                            $(elem).closest("ul.custom_select").children('.init').addClass('selected').html($(elem).html());
                        }
                    });
                }

                , prepareCloneSource: function (source) {
                    //Get the item to be added with the correct index corresponding to the updated added
                    //Source Element is the Parent's first child. This is the one to be cloned.
                    var cloned = source.clone();
                    cloned.find('select.hidden_select').val('');
                    cloned.find('ul.custom_select.custom_select_single .init').removeClass('selected');
                    cloned.find('ul.custom_select.custom_select_single ul li[selected="selected"]').removeAttr('selected');
                    cloned.find(":input:not('ul')").attr("value", "");
                    cloned.find("span.placeholder").removeAttr("style");
                    $("<div />").after(cloned);

                    return cloned;
                }

                , checkEmpty: function (event) {
                    var input = $(event.target);

                    if (input.parent().siblings().is("button[name='compound_multifield_btn_remove']"))
                        if (input.val() === "")
                            this.handleRemove(event);
                }

                //========== EVENTS ===============================

                /**
                 * Performs the actual addition of a remove button to the last copy of the multiple element
                 * and the addition of a new element.
                 * @param the composite
                 * @param effect whether to use an effect to show the added html
                 */
                , onAfterBeforeAdded: function (event, effect) {

                    var composite = $(event.target);
                    this.appendRemoveButton(composite, true);
                    this.removeAddButton(composite);
                    this.appendField(effect);

                    var index = composite.attr("data-index");
                    index++;
                    var nextSelect = composite.siblings("[data-index=" + index + "]").find("ul.custom_select");
                    nextSelect.focus();

                }

                , updateCardinality: function () {
                    var parent = this.$el;
                    var added = parseInt(parent.attr("added"), 10);

                    if (added > 0)
                        added--;
                    parent.attr("added", added);
                }

                , doAdd: function (event) {
                    var el = $(event.target);
                    var composite = el.parents(".composite[data-index]");
                    var isEmpty = this.isFieldEmpty(composite.find("input.formfield.copy"));

                    if (composite.is(':last-child') && !isEmpty) {
                        composite.trigger("europass:multipliable:beforeadded", [true]);
                    }
                }

                /**
                 * The actual process of removing the composite from view.
                 * Performed here as it has to take place AFTER all components have finished working on the before delete event.
                 * @param event
                 */
                , onAfterBeforeRemoved: function (event) {

                    var composite = $(event.target);
                    //...and remove the composite element
                    var that = this;
                    composite.slideUp("slow", "linear", function () {
                        var el = $(this);

                        // -1. Parent updates
                        that.updateCardinality();

                        // -2- Shift up the next siblings
                        var idxAttr = el.attr("data-index");
                        var idx = parseInt(idxAttr, 10);
                        var nextEl = el.next();
                        el.nextUntil().each(function (i, ne) {
                            nextEl = $(ne);
                            Utils.reIndexElement(nextEl, "[" + idx + "]", (idx + 1));
                            //also update all its children
                            nextEl.find("*").each(function (j, rne) {
                                Utils.reIndexElement($(rne), "[" + idx + "]", (idx + 1));
                            });
                            idx++;
                        });

                        el.trigger("europass:multipliable:removed");

                        // -3- Remove
                        el.remove();

                        // -4- Focus on the next
                        nextEl.find(":input:not(button)").first().focus();
                        nextEl.find(":input:not(button)").attr("tabIndex", "0");

                        that.populateLimitedMulipliables();
                    });
                }
                /**
                 * Runs just before element is removed and AFTER reindexing of the rest elements is complete
                 */
                , onAfterRemoved: function (event) {
                    var that = this;
                    var el = $(event.target);
                    el.nextUntil().each(function (i, ne) {
                        var nextEl = $(ne);
                        nextEl.find(":input.formfield:not(button)").each(function (idx, el) {
                            that.disableSelectAndInput(nextEl, $(el));
                        });
                    });
                }
                /**
                 * Append a field to the parent element (Parent is the actual element where this functionality is bound to.
                 * @param effect may be null or undefined, if we do not need to apply effects.
                 * @return nothing
                 */
                , appendField: function (effect) {
                    var parent = this.$el;
                    var added = parseInt(parent.attr("added"), 10);

                    added++;
                    parent.attr("added", added);

                    //Get the item to be added with the correct index corresponding to the updated added
                    //Source Element is the Parent's first child. This is the one to be cloned.

                    //Prepare the sourceClone

                    var item = this.prepareClone(added, this.sourceClone);
                    if (effect !== undefined && effect === true) {
                        item.hide();
                    }

                    this.appendAddButton(item);

                    parent.append(item);

                    if (effect !== undefined && effect === true) {
                        item.slideDown("slow");
                    }

                    this.populateLimitedMulipliables();
                }

                //========== UTILITY FUNCTIONS ====================
                , updateElementAttributes: function (el, replacement, regexIdx, attrName) {

                    if (regexIdx === null)
                        regexIdx = 0;
                    var regexp = new RegExp("(\\[" + regexIdx + "\\])(?=[^\\[" + regexIdx + "\\]]*$)");

                    $.each(this.ATTRIBUTES_WITH_INDEX, function (idx, attrName) {
                        var attr = el.attr(attrName);
                        if (attr) {
                            var newAttr;
                            if (attrName === "data-index") {
                                var nextIdx = replacement.substring(1, replacement.length - 1);
                                newAttr = nextIdx;
                            } else {
                                newAttr = attr.replace(regexp, replacement);
                            }
                            el.attr(attrName, newAttr);
                        }

                    });
                }

                , isFieldEmpty: function (elem) {
                    var defaultValue = elem.siblings('span.placeholder').text();//elem.attr("data-defaultvalue");
                    if (defaultValue === undefined) {
                        return null;
                    }
                    var value = elem.val();
                    return (value === "");
                }
                , isCompositeEmpty: function (compound, excludeSelect) {

                    var that = this;
                    var isEmpty = true;

                    compound.find(":input:not(button)").each(function () {
                        var fieldEmpty = that.isFieldEmpty($(this));
                        isEmpty = isEmpty && (fieldEmpty === null || fieldEmpty);
                    });
                    return isEmpty;
                }

                , populateLimitedMulipliables: function (event) {

                    if (this.maxItems === undefined)
                        return;

                    var limit = parseInt(this.maxItems, 10);

                    var children = this.$el.find(".composite:not([class*='select'])");
                    var childrenLength = children.length;
                    var current_fields = 0;
                    for (var i = 1; i <= childrenLength; i++) {
                        var composite = $(children[i - 1]);
                        var button = composite.find(":button");

                        if (i <= limit) {

                            if (limit === 1 && childrenLength <= 2 && i === 1) {
                                composite.toggleClass("NoButton");
                            }
                            if (!this.isCompositeEmpty(composite) && childrenLength >= limit) {

                                if (limit > 1 || (limit === 1 && childrenLength > limit + 1)) {
                                    if (!button.hasClass("add-item")) {
                                        button.fadeIn("slow").css("display", "inline-block");
                                    } else {
                                        button.fadeOut("slow");
                                    }
                                } else if (limit === 1 && childrenLength > limit) {
                                    button.fadeOut("slow");
                                }

                            } else if (this.isCompositeEmpty(composite) && childrenLength === limit) {
                                if (limit > 1) {
                                    if (button.hasClass("add-item"))
                                        this.removeAddButton(composite);

                                    this.appendRemoveButton(composite);
                                } else {
                                    button.fadeOut("slow");
                                }
                                composite.slideDown("slow");

                            } else {
                                if (childrenLength === 1) {
                                    if (button.hasClass("remove-item")) {
                                        this.removeRemoveButton(composite);

                                        var addBtn = composite.find("[name=\"compound_multifield_add_remove\"]");
                                        if (addBtn.length === 0)
                                            this.appendAddButton(composite);
                                        else
                                            button.fadeIn("slow").css("display", "inline-block");
                                    }
                                } else {
                                    button.fadeIn("slow");
                                }
                                composite.slideDown("slow");
                            }
                        }
                        // If i exceeds limit and is the last child, it's the empty compound, so hide it
                        // else if i exceeds limit and is not the last child, hide the button and disable inputs
                        current_fields += 1;
                        if (i >= limit) {
                            if (this.isCompositeEmpty(composite) && i > limit) {
                                composite.fadeOut("slow");
                                current_fields -= 1;
                            } else if (this.isCompositeEmpty(composite)) {
                                button.fadeOut("slow");
                                //composite.fadeOut("slow");
                            } else if (i > limit) {
                                composite.fadeOut("slow");
                                current_fields -= 1;
                            }

                            if (current_fields <= limit) {
                                composite.slideDown('slow');
                                current_fields += 1;
                            }
                        }
                    }
                }

                , disableSelectAndInput: function (parent, elem) {
                    if (this.maxItems !== null && parent !== undefined) {

                        var index = parseInt(parent.attr("data-index"));
                        var limit = parseInt(this.maxItems, 10);
                        var exceedsLimit = index >= limit;

                        if (elem.is("ul.custom_select.custom_select_single.formfield")) {
                            if (exceedsLimit)
                                elem.trigger("disable");
                            else
                                elem.trigger("enable");

                            elem.trigger("update.fs");

                        } else if (elem.is(":input.formfield:not(button):not([type='hidden'])")) {

                            elem.attr("readonly", exceedsLimit);
                            if (exceedsLimit && !elem.hasClass("readonlyFormfield")) {
                                elem.addClass("readonlyFormfield");
                            } else if (!exceedsLimit && elem.hasClass("readonlyFormfield")) {
                                elem.removeClass("readonlyFormfield");
                            }
                        }
                    }
                }
            });
            return FixedValueListInputView;
        }
);