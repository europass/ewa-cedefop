define(
        [
            'jquery',
            'jqueryui',
            'underscore',
            'backbone',
            'Utils',
            'i18n!localization/nls/EditorHelp',
//		'europass/TabletInteractionsView',
            'views/interaction/MultiFieldView',
            'views/interaction/TypeaheadView',
            'views/forms/FormView'
        ],
        function ($, jqueryui, _, Backbone, Utils, EditorHelp,
//		TabletInteractionsView, 
                MultiFieldView, TypeaheadView, FormView) {
            var AutoCompleteListInputView = MultiFieldView.extend({

                events: _.extend({
                    "blur input.formfield.copy": "checkEmpty"
                }, MultiFieldView.prototype.events)

                , enableFunctionality: function (parent) {
                    //We will bind the functionality to an element which is supposed to include the source and all copied elements.
                    //Note that when loading the form with an already populated Model, we might already have multiple elements.
                    //In this case the first should be considered the source, and the rest the copies.

                    MultiFieldView.prototype.enableFunctionality(parent);

                    var children = parent.children();
                    var childrenNo = children.length;
                    var hasChildren = (childrenNo && childrenNo > 0);

                    var isFormPopulated = true;

                    //Prepare the sourceClone
                    var firstChild = parent.children(":first");
                    this.sourceClone = this.prepareClone(childrenNo, this.prepareCloneSource(firstChild));

                    //Add remove buttons to the already existing elements as these are rendered by the page
                    if (hasChildren) {
                        var that = this;
                        children.each(function () {
                            var composite = $(this);

                            composite.find(":input.formfield:not(button)").each(function (idx, el) {
                                var input = $(el);
                                that.disableSelectAndInput(composite, input);
                            });

                            var isEmpty = that.isCompositeEmpty(composite);
                            if (!isEmpty) {
                                //Add the Remove button
                                that.appendRemoveButton(composite);
                            }

                            isFormPopulated = isFormPopulated && !isEmpty;
                        });
                    }

                    //If the form loads with data, then we need to add an extra field at the bottom
                    if (isFormPopulated) {
                        this.appendField();
                    } else {
                        this.appendAddButton(firstChild);
                    }

                    parent.trigger("europass.enable.typeahead");
                    // Disable and Hide elements according to limit, if exists
                    this.populateLimitedMulipliables();
                }

                , prepareCloneSource: function (source) {

                    //Get the item to be added with the correct index corresponding to the updated added
                    //Source Element is the Parent's first child. This is the one to be cloned.
                    var cloned = source.clone();

                    // remove class hasValue
                    cloned.find("div.placeholding").removeClass("hasValue");
                    // show placeholder
                    cloned.find("span.placeholder").attr("style", "display: block;");

                    var inputEl = cloned.find("input.tt-input");
                    var inputCode = cloned.find("input[name$='Code']");

                    inputCode.attr("value", "");

                    inputEl.removeClass("tt-input");
                    inputEl.removeAttr("style");
                    inputEl.attr("value", "");
                    var inputElHTML = $("<div />").append(inputEl).html();

                    var autoCompleteListContainer = cloned.find("div.placeholding[name$='Label']");

                    autoCompleteListContainer.find("span.twitter-typeahead").remove();
                    autoCompleteListContainer.html(inputElHTML);

                    cloned.append(inputCode);

                    // remove input field's value
                    cloned.find("input.formfield.copy").attr("value", "");

                    return cloned;
                }
                , checkEmpty: function (event) {
                    var input = $(event.target);

                    if (input.closest(".composite").find("button[name='compound_multifield_btn_remove']") &&
                            input.closest(".composite").find("button[name='compound_multifield_btn_remove']").length > 0)
                        if (input.val() === "") {
                            this.handleRemove(event);
                        }
                }

                , doAdd: function (event) {
                    var el = $(event.target);
                    var composite = el.parents(".composite[data-index]");
                    var inputField = composite.find("input.formfield.copy");

                    var isEmpty = this.isFieldEmpty(inputField);

                    // in case of typeahead field
                    if (inputField.is(".tt-hint, .tt-input"))
                        isEmpty = this.isTypeaheadFieldEmpty(inputField);

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

                    var code = composite.find("input[name$='Code']").val();
                    var value = composite.find("input[name$='Label']").val();

                    composite.trigger("europass:multipliable:update:map", ["exclude", {"key": code, "value": value}]);

                    this.appendField(effect);

                    // Focus on the next select field
                    var index = composite.attr("data-index");
                    index++;
                    composite.siblings("[data-index=" + index + "]").promise().done(
                            function (el) {
                                this.find("input.tt-input").focus();
                            });

                }

                //========== DOM UPDATE FUNCTIONS ================

                , updateCardinality: function () {
                    var parent = this.$el;
                    var added = parseInt(parent.attr("added"), 10);

                    if (added > 0)
                        added--;
                    parent.attr("added", added);
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

                        var code = composite.find("input[name$='Code']").val();
                        var value = composite.find("input[name$='Label']").val();

                        composite.trigger("europass:multipliable:update:map", ["include", {"key": code, "value": value}]);

                        // -3- Remove
                        el.remove();

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
                        nextEl = $(ne);
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

                    var lastChild = parent.children(":last");
                    var item = this.prepareClone(parseInt(lastChild.attr("data-index"), 10) + 1, this.sourceClone.clone());

                    this.newInput = item.find("input.typeahead");
                    this.newInput.typeahead('destroy');

                    if (effect !== undefined && effect === true) {
                        item.hide();
                    }

                    this.appendAddButton(item);

                    parent.append(item);

                    if (effect !== undefined && effect === true) {
                        item.slideDown("slow");
                    }

                    var dataMapPrefix = parent.attr("data-list-map");
                    if (dataMapPrefix !== undefined) {

                        var composite = $(item).find("div.composite.select");

                        var requirePath = 'europass/maps/' + dataMapPrefix + 'Map';

                        var excludedValuesArray = null;
                        var excludedSections = Utils.getDataExcludedSections(parent.attr("data-excluded-sections"));
                        if (excludedSections !== null)
                            excludedValuesArray = this.retrieveExcludedValues(excludedSections);

                        var isGlobal = parent.is(".global");

                        require([requirePath, 'europass/structures/MapWrapper'], function (ObjectMap, MapWrapper) {

                            new TypeaheadView({
                                el: $(composite),
                                minLength: 0,
                                map: (excludedValuesArray !== null ? new MapWrapper(ObjectMap, excludedValuesArray) : ObjectMap),
                                multipliable: true,
                                exclusive: (excludedValuesArray !== null ? true : false),
                                global: isGlobal
                            });
                        });

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

                , isTypeaheadFieldEmpty: function (input) {

                    var elem = input.is(".tt-hint") ? input.siblings(".tt-input") : input;

                    if (elem.length === 0 || input.siblings("pre").length === 0)
                        return null;

                    var isEmpty = (elem.siblings("pre").html() === "" && elem.attr("placeholder") !== "");

                    return isEmpty;
                }

                , isFieldEmpty: function (elem) {
                    var defaultValue = elem.siblings('span.placeholder').text();//elem.attr("data-defaultvalue");
                    if (defaultValue === undefined) {
                        return null;
                    }
                    var value = elem.val();
                    return (value === "" || elem.is(".empty")); // The .empty check is for IE 9
                }
                , isCompositeEmpty: function (compound, excludeTypeahead) {

                    var that = this;
                    var isEmpty = true;

                    compound.find(":input:not(button)").each(function () {

                        // in case excludeSelect is defined and true and the input element has at leaste a parent 
                        // of classes .composite and .select the input element concerns the typeahead element,
                        // so we avoid the check

                        if ((!_.isUndefined(excludeTypeahead) && excludeTypeahead === true) && $(this).parents(".composite.select").length > 0)
                            ;
                        else {
                            var fieldEmpty = that.isFieldEmpty($(this));
                            isEmpty = isEmpty && (fieldEmpty === null || fieldEmpty);
                        }
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
                , populateLimitedMulipliables: function (event) {

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
                , FIELD_TTINPUT: "input-typeahead"

                , disableSelectAndInput: function (parent, elem) {
                    if (this.maxItems !== null && parent !== undefined) {
                        var fieldType = null;
                        if (elem.is("input.tt-input")) {
                            fieldType = this.FIELD_TTINPUT;
                        } else if (elem.is(":input.formfield:not(button):not([type='hidden'])")) {
                            fieldType = this.FIELD_SIMPLE;
                        }
                        var index = parseInt(parent.attr("data-index"));
                        var limit = parseInt(this.maxItems, 10);
                        var exceedsLimit = index >= limit;
                        switch (fieldType) {
                            case this.FIELD_SIMPLE:
                            {
                                elem.attr("readonly", exceedsLimit);
                                if (exceedsLimit && !elem.hasClass("readonlyFormfield")) {
                                    elem.addClass("readonlyFormfield");
                                } else if (!exceedsLimit && elem.hasClass("readonlyFormfield")) {
                                    elem.removeClass("readonlyFormfield");
                                }
                                break;
                            }
                            case this.FIELD_TTINPUT:
                            {
                                elem.attr("disabled", exceedsLimit);
                                var clearButton = parent.find("span.typeahead");
                                if (exceedsLimit && clearButton.hasClass("clear")) {
                                    clearButton.removeClass("clear");
                                } else if (!exceedsLimit && !clearButton.hasClass("clear")) {
                                    clearButton.addClass("clear");
                                }
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

                , retrieveExcludedValues: function (sectionsArray) {

                    if (sectionsArray === undefined)
                        return null;

                    var excludedValuesArray = [];

                    for (var i = 0; i < sectionsArray.length; i++) {

                        var modelValues = this.model.get(sectionsArray[i]);

                        if (modelValues !== undefined) {

                            excludedValuesArray = excludedValuesArray.concat(modelValues);
                        }
                    }

                    return excludedValuesArray;
                }
            });
            return AutoCompleteListInputView;
        }
);