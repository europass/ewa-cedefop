define(
        [
            'jquery',
            'jqueryui',
            'underscore',
            'backbone',
            'Utils',
            'i18n!localization/nls/EditorHelp'
//	,
//	'europass/TabletInteractionsView'
        ],
        function ($, jqueryui, _, Backbone, Utils, EditorHelp
//			, TabletInteractionsView
                ) {
            var CompoundMultiFieldView = Backbone.View.extend({
                events: {
                    //ALL FIELDS
                    "blur :input:not(:button).copy": "onBlurred",

                    //MULTIPLIEABLE FIELDS - on change
                    "europass:autocomplete:blurred :input:not(:button).copy": "onBlurred",
                    "europass:autocomplete:selected :input:not(:button).copy": "onBlurred",
                    //ALL BUTTONS
                    "click :button[name=\"compound_multifield_add_remove\"]": "handleAdd",
                    "click :button[name=\"compound_multifield_btn_remove\"]": "handleRemove",

                    "europass:multipliable:beforeadded": "onAfterBeforeAdded",
                    "europass:multipliable:beforeremoved": "onAfterBeforeRemoved",

                    "europass:multipliable:removed": "onAfterRemoved",

                    "europass:multipliable:added:complete": "onCompletedAction",
                    "europass:multipliable:removed:complete": "onCompletedAction"
                }
                , onClose: function () {
                    delete this.sourceClone;
                    delete this.enableRemove;
                    delete this.removeButton;
                    delete this.addButton;
                    delete this.maxItemsAllowedToAdd;
                    delete this.maxItems;
                }
                , initialize: function (options) {
                    this.options = options;

                    var removeLabel = EditorHelp["Multifield.Remove.Tooltip"];
                    if (removeLabel === undefined || removeLabel === null || removeLabel === "")
                        removeLabel = "Remove field";
                    var addLabel = EditorHelp["Multifield.Add.Tooltip"];
                    if (addLabel === undefined || addLabel === null || addLabel === "")
                        addLabel = "Add field";

                    this.enableRemove = this.options.enableRemove || true;
                    this.removeButton = this.options.removeButton || '<button type="button" name="compound_multifield_btn_remove" class="multifield remove-item tip spot" data-tip.position="top-right" >Remove<span class="data-title" style="display:none;">' + removeLabel + '</span></button>';
                    this.addButton = this.options.addButton || '<button type="button" name="compound_multifield_add_remove" class="multifield add-item tip spot" data-tip.position="top-right" >Add<span class="data-title" style="display:none;">' + addLabel + '</span></button>';
                    this.maxItemsAllowedToAdd = this.options.maxItemsAllowedToAdd || null;
                    this.maxItems = this.$el.attr("data-max-items") || undefined;

                    this.isTablet = (/(iPhone|iPod|iPad)/.test(navigator.userAgent) || (/Android/.test(navigator.userAgent)) || (/BlackBerry/.test(navigator.userAgent)));
                    if (this.isTablet) {

                        /**
                         * pgia: EWA-1815
                         * Load TabletInteractionsView via require on the tablets variable ONLY if isTablet == true
                         */

                        var _that = this;
                        require(['europass/TabletInteractionsView'], function (TabletInteractionsView) {
                            _that.tablets = TabletInteractionsView;
                        }
                        );
                    }

                    this.enableFunctionality(this.$el);
                }
                , enableFunctionality: function (parent) {
                    //We will bind the functionality to an element which is supposed to include the source and all copied elements.
                    //Note that when loading the form with an already populated Model, we might already have multiple elements.
                    //In this case the first should be considered the source, and the rest the copies.
                    var children = parent.children();
                    var childrenNo = children.length;
                    var hasChildren = (childrenNo && childrenNo > 0);

                    //The following attributes are set only to the parent element
                    parent.attr("cardinality", (hasChildren ? childrenNo : 0));
                    parent.attr("ismax", ((this.maxItemsAllowedToAdd && hasChildren && (childrenNo >= this.maxItemsAllowedToAdd)) ? true : false));
                    parent.attr("added", (hasChildren ? (childrenNo - 1) : 0));

                    var isPrepopulated = {};

                    //Prepare the sourceClone
                    var firstChild = parent.children(":first");
                    this.sourceClone = this.prepareCloneSource(firstChild);

                    //Add remove buttons to the already existing elements as these are rendered by the page
                    if (hasChildren) {
                        var that = this;
                        children.each(function () {
                            var composite = $(this);

                            //Trigger a prepare event
                            composite.trigger("europass:multipliable:prepare");
                            composite.find(":input:not(button)").each(function (idx, el) {
                                var input = $(el);
                                that.disableSelectAndInput(composite, input);

                                input.trigger("europass:multipliable:prepare:input");
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

                    // Disable and Hide elements according to limit, if exists
                    if (this.maxItems !== undefined) {
                        this.adjustAccordingToLimit();
                    }
                },
                getCodeLabel: function (el) {
                    var code = el.find(":input:not(:button)[name$=\"Code\"]").first().attr("data-prev-code") !== undefined ? el.find(":input:not(:button)[name$=\"Code\"]").first().attr("data-prev-code") : el.find(":input:not(:button)[name$=\"Code\"]").first().val();
                    var label = el.find(":input:not(:button)[name$=\"Label\"]").first().attr("data-prev-label") !== undefined ? el.find(":input:not(:button)[name$=\"Label\"]").first().attr("data-prev-label") : el.find(":input:not(:button)[name$=\"Label\"]").first().val();
                    return {
                        id: code,
                        text: label
                    };
                },
                //========== EVENTS ===============================
                onBlurred: function (event) {
                    var el = $(event.target);
                    //console.log("blurred " + el.attr("name") );
                    var composite = el.parents(".composite[data-index]");
                    var codeLabelInfo = this.getCodeLabel(composite);

                    // The siblings variable holds the number of the current composite's siblings (tells us if there are any composites besides teh current)
                    // the index variable holds the data-index value (if it is 0 it is the first composite first)

                    var siblings = composite.siblings().length;
                    var index = parseInt(composite.attr("data-index"));

                    //in case this.maxItems is not defined, then its value is zero 
                    var maxItems = 0;
                    if (!_.isUndefined(maxItems))
                        maxItems = parseInt(this.maxItems);

                    /**
                     * Perform checks:
                     * The specific field will NOT be removed if: 
                     * 
                     * - it is the first composite AND: 
                     * 		
                     * 		- max items restriction is defined and equals to 1 composite allowed OR
                     * 		- it is the only composite (there are no siblings)
                     */
                    if (index === 0) {
                        if (siblings === 1 && maxItems === 1)
                            return;
                        else if (siblings === 0 && maxItems > 1)
                            return;
                    }
                    if (this.isFieldEmpty(el)) {
                        if (composite.next().length > 0) {
                            this.removeField(composite, codeLabelInfo);
                        }
                    }
                    //If the specific field has other siblings before and those are empty, they need to be removed.
                    var that = this;
                    composite.prevAll().each(function (i, c) {
                        //If the included field is empty
                        var prevComposite = $(c);
                        if (prevComposite.find(":input:not(:button).copy").filter(Utils.filterEmptyVal).length > 0) {
                            //console.log("remove field " + prevComposite.attr("data-index"));
                            that.removeField(prevComposite, codeLabelInfo);
                        }
                    });
                }
                /**
                 * Runs when the remove button is clicked in order to remove the corresponding html element
                 * @param event whose target is the button
                 */
                , handleRemove: function (event) {

                    // TODO CLEAN UP COMMENT On mobile not using 2 step tooltip anymore !!
                    // if ((this.isTablet && _.isFunction(this.tablets.handleTipSpot)) && this.tablets.handleTipSpot(event, "currentTarget") === false) {
                    // 	return false;
                    // } else {
                    // 	this.doRemove(event);
                    // }
                    this.doRemove(event);
                }
                , doRemove: function (event) {
                    var el = $(event.target);
                    var composite = el.parents(".composite[data-index]");
                    var codeLabelInfo = this.getCodeLabel(composite);
                    this.removeField(composite, codeLabelInfo);
                }
                /**
                 * Runs when the beforeadded event is fired in order to allow the component to proceed with the addition.
                 * @param event
                 * @param effect whether to use an effect to show the added html
                 */
                , handleAdd: function (event) {

                    // TODO CLEAN UP COMMENT On mobile not using 2 step tooltip anymore !!
                    // if ((this.isTablet && _.isFunction(this.tablets.handleTipSpot)) && this.tablets.handleTipSpot(event, "currentTarget") === false) {
                    // 	return false;
                    // } else {
                    // 	this.doAdd(event);
                    // }
                    this.doAdd(event);
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

                    // Focus on the next select field
//				console.log($(event.target));
                    var index = composite.attr("data-index");
                    index++;
//				console.log(index);

                }
                //========== CLONING FUNCTIONS ====================
                /**
                 * Prepare/Sanitize a source based on the HTML template (first-child), which is the 
                 * @param source. Unbind the events, Reset the values of the form fields, and Delete the Remove button.
                 * @return the sourceClone html element
                 * @return whether the sourceClone :input have already values.
                 */
                , prepareCloneSource: function (source) {
                    var sourceClone = source.clone(true);

                    var clone = $(sourceClone);

                    //Then, the clone needs to be value free.
                    clone.find(":input:not(button)").each(function () {
                        var input = $(this);
                        //Set value to the default value and add the class name
                        if (input.is("select")) {
                            input.children("[selected]").each(function () {
                                $(this).removeAttr("selected");
                            });
                            input.children(":first").removeAttr("value");
                            input.children(":first").attr("selected", true);
                        } else {
                            input.val("");
                        }
                    });

                    //Finally, the clone should not have a remove button
                    clone.find(":button[name=\"compound_multifield_btn_remove\"]").remove();

                    clone.find(":input:not(button)[data-init-index]").removeAttr("data-init-index");
                    clone.find(":input:not(button)[data-init-index]").attr("tabIndex", "0");

                    return sourceClone;
                }
                /**
                 * Prepare a clone out of the 
                 * @param source clone 
                 * and update all its attributes that contain index information, 
                 * according to 
                 * @param nextIndex.
                 * @return theClone HTML element with the correct index
                 */
                , prepareClone: function (nextIndex, source) {

                    var sanitizedClone = this.prepareCloneSource(source);

                    var replacement = "[" + nextIndex + "]";

                    var theClone = sanitizedClone.clone(true);

                    Utils.reIndexElementAndChildren($(theClone), replacement);

                    return theClone;
                }
                //========== DOM UPDATE FUNCTIONS ================
                , appendRemoveButton: function (el, effect) {
                    var btn = $(this.removeButton);//el.find( ":button[name=\"compound_multifield_btn_remove\"]" );

                    if (effect !== undefined) {
                        btn.hide();
                    }
                    el.append(btn);

                    var btnEl = el.find("[name=\"compound_multifield_btn_remove\"]");

                    if (effect !== undefined) {
                        btnEl.fadeIn("slow");
                        btnEl.css("display", "inline-block");
                    }
                }
                , appendAddButton: function (el, effect) {
                    var btn = $(this.addButton);

                    if (effect !== undefined) {
                        btn.hide();
                    }
                    el.append(btn);

                    var btnEl = el.find("[name=\"compound_multifield_add_remove\"]");

                    if (effect !== undefined) {
                        btnEl.fadeIn("slow");
                    }
                }
                , removeAddButton: function (el) {
                    var btnEl = el.find("[name=\"compound_multifield_add_remove\"]");
                    btnEl.remove();
                }
                , removeRemoveButton: function (el) {
                    var btnEl = el.find("[name=\"compound_multifield_btn_remove\"]");
                    btnEl.remove();
                }
                /**
                 * Removes the entire composite from the view.
                 * Note that this ony trigger the before remove event to give the opportunity to others to perform necessary actions.
                 * @param composite : the html element to remove
                 * @param codeLabelInfo, the code label if applicable
                 */
                , removeField: function (composite, codeLabelInfo) {
//console.log("remove field info:\n" + JSON.stringify( removedInfo ) );

                    //fire the before remove callback fpr the pref and for the item
                    composite.trigger("europass:multipliable:removepref");

                    composite.trigger("europass:multipliable:beforeremoved", [codeLabelInfo]);
                    //and continue with the implementation onAfterBeforeRemove

                }

                , updateCardinality: function () {
                    var parent = this.$el;
                    var cardinality = parseInt(parent.attr("cardinality"), 10);
                    var added = parseInt(parent.attr("added"), 10);

                    if (cardinality > 0)
                        cardinality--;
                    if (added > 0)
                        added--;
                    parent.attr("cardinality", cardinality);
                    parent.attr("added", added);
                }
                /**
                 * The actual process of removing the composite from view.
                 * Performed here as it has to take place AFTER all components have finished working on the before delete event.
                 * @param event
                 */
                , onAfterBeforeRemoved: function (event) {

                    var composite = $(event.target);
//	console.log("Remove: " + composite.attr("name"));
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
                            //console.log( "reindex " + nextEl.attr("name") + " to " + idxAttr );
                            Utils.reIndexElement(nextEl, "[" + idx + "]", (idx + 1));
                            //also update all its children
                            nextEl.find("*").each(function (j, rne) {
                                //console.log( "reindex " + $(this).attr("name") + " to " + idxAttr );
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

                        that.$el.trigger("europass:multipliable:removed:complete");

                    });
                }
                /**
                 * Runs just before element is removed and AFTER reindexing of the rest elements is complete
                 */
                , onAfterRemoved: function (event) {
                    var that = this;
                    var el = $(event.target);
//console.log("On el remove - after re-index is complete ");
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
                    var cardinality = parseInt(parent.attr("cardinality"), 10);
                    var added = parseInt(parent.attr("added"), 10);
                    if (this.maxItemsAllowedToAdd === null || cardinality < this.maxItemsAllowedToAdd) {
                        var that = this;

                        cardinality++;
                        added++;
                        parent.attr("cardinality", cardinality);
                        parent.attr("added", added);

                        //Get the item to be added with the correct index corresponding to the updated added
                        //Source Element is the Parent's first child. This is the one to be cloned.
                        var item = this.prepareClone(added, this.sourceClone);

                        if (effect !== undefined && effect === true) {
                            item.hide();
                        }

                        this.appendAddButton(item);

                        parent.append(item);

                        if (effect !== undefined && effect === true) {
                            item.slideDown("slow");
                        }

                        item.trigger("europass:multipliable:added");


                        var codes = [];

                        item.find(":input:not(button)").each(function (idx, el) {
                            var input = $(el);
                            input.trigger("europass:multipliable:add:input", [codes]);
                            //console.log("'europass:multipliable:add:input' for "+ input.attr("name"));
                            that.disableSelectAndInput(parent, input);

                        });

                        that.$el.trigger("europass:multipliable:added:complete");
                    }
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
                , isCompositeEmpty: function (compound) {

                    var that = this;
                    var isEmpty = true;

                    compound.find(":input:not(button)").each(function () {
                        var fieldEmpty = that.isFieldEmpty($(this));
                        isEmpty = isEmpty && (fieldEmpty === null || fieldEmpty);
                    });
                    return isEmpty;
                }

                , onCompletedAction: function (event) {
                    this.adjustAccordingToLimit();
                }

                /**
                 * Will find all children and hide those that exceed the limit.
                 * If the limit is 1, then the remove button should not be displayed at all.
                 */
                , adjustAccordingToLimit: function (callback, scope) {

                    if (this.maxItems === undefined)
                        return;

                    var limit = parseInt(this.maxItems, 10);

                    var children = this.$el.find(".composite:not([class*='select'])");
                    var childrenLength = children.length;

                    for (var i = 1; i <= childrenLength; i++) {
                        var composite = $(children[i - 1]);
                        var button = composite.find(":button");

                        if (i <= limit) {

                            if (limit === 1 && childrenLength <= 2 && i === 1) {
                                //composite.find("input.formfield.copy").toggleClass("NoButton");
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
                                if (_.isFunction(callback) && _.isObject(scope)) {
                                    composite.slideDown("slow", function () {
                                        callback.apply(scope, []);
                                    });
                                } else {
                                    composite.slideDown("slow");
                                }

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
                                if (_.isFunction(callback) && _.isObject(scope)) {
                                    composite.slideDown("slow", function () {
                                        callback.apply(scope, []);
                                    });
                                } else {
                                    composite.slideDown("slow");
                                }

                            }
                        }
                        // If i exceeds limit and is the last child, it's the empty compound, so hide it
                        // else if i exceeds limit and is not the last child, hide the button and disable inputs
                        if (i > limit) {

                            if (this.isCompositeEmpty(composite)) {
                                composite.fadeOut("slow");
                            } else {
                                button.fadeOut("slow");
                            }
                        }
                    }
                }
                /**
                 * Decides how to disable the form field according to its type:
                 * simple input, select or hybrid
                 */
                , FIELD_SIMPLE: "input"
                , FIELD_SELECT: "select"
                , FIELD_HYBRID: "hybrid"

                , disableSelectAndInput: function (parent, elem) {
                    if (this.maxItems !== null && parent !== undefined) {
//console.log("Decide disabled status for el " + elem.attr("name") );
                        var fieldType = null;
                        if (elem.is("select.formfield")) {
                            fieldType = this.FIELD_SELECT;
                        } else if (elem.is(":input.formfield:not(button):not([type='hidden'])")) {
                            fieldType = this.FIELD_SIMPLE;
                        }
//console.log("Field Type is " + fieldType );
                        var index = parseInt(parent.attr("data-index"));
                        var limit = parseInt(this.maxItems, 10);
//console.log("Index: "+index + " Limit: " +limit );
                        var exceedsLimit = index >= limit;
//console.log("Beyond limit ? " + exceedsLimit);
                        switch (fieldType) {
                            case this.FIELD_SIMPLE:
                            {
                                elem.attr("readonly", exceedsLimit);
                                if (exceedsLimit && !elem.hasClass("readonlyFormfield")) {
//	console.log("Case input:: addClass 'readonlyFormfield'");
                                    elem.addClass("readonlyFormfield");
                                } else if (!exceedsLimit && elem.hasClass("readonlyFormfield")) {
//	console.log("Case input:: removeClass 'readonlyFormfield'");
                                    elem.removeClass("readonlyFormfield");
                                }
                                break;
                            }
                            case this.FIELD_SELECT:
                            {
//	console.log("Case simple select:: make disabled ? "+exceedsLimit);
                                elem.attr("disabled", exceedsLimit);
                                break;
                            }
                            default:
                            {
                                break;
                            }
                        }
                    }
                }

                /**
                 * Append an add/remove button next to the field
                 */
                , toggleRemoveButton: function (event) {
                    var el = $(event.target);
                    var composite = el.parents(".composite[data-index]");

                    if (this.maxItems === undefined)
                        return;

                    var limit = parseInt(this.maxItems, 10);
                    if (limit === 1)
                        return;

                    var index = parseInt(composite.attr("data-index"));

                    if (index === 0 && composite.siblings().length === 0) {
                        // Append add button
                        var addBtn = composite.find("[name=\"compound_multifield_add_remove\"]");
                        if (addBtn.length === 0)
                            this.appendAddButton(composite);
                        else {
                            if (composite.find("[name=\"compound_multifield_add_remove\"]:visible").length === 0)
                                addBtn.show();
                        }
                    } else if (index === limit - 1) {
                        // check the case where the event has been caught but the last character 
                        // has not yet been removed 
                        //Add remove button
                        var removeBtn = composite.find("[name=\"compound_multifield_btn_remove\"]");
                        if (removeBtn.length === 0)
                            this.appendRemoveButton(composite);
                        else {
                            if (composite.find("[name=\"compound_multifield_btn_remove\"]:visible").length === 0)
                                removeBtn.show();
                        }
                    }
                }
            });
            return CompoundMultiFieldView;
        }
);