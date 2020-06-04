define(
        [
            'jquery',
            'jqueryui',
            'underscore',
            'backbone',
            'Utils',
//		EWA-1811
//		'i18n!localization/nls/EditorHelp',
//		'europass/TabletInteractionsView',
            'views/interaction/MultiFieldView'
        ],
        function ($, jqueryui, _, Backbone, Utils,
//			EWA-1811
//			EditorHelp, 
//			TabletInteractionsView, 
                MultiFieldView) {
            var SingleInputView = MultiFieldView.extend({

                events: _.extend({
                    "blur input.formfield.copy": "checkEmpty"
                }, MultiFieldView.prototype.events)

                , enableFunctionality: function (parent) {

                    MultiFieldView.prototype.enableFunctionality.call(parent);
                    //We will bind the functionality to an element which is supposed to include the source and all copied elements.
                    //Note that when loading the form with an already populated Model, we might already have multiple elements.
                    //In this case the first should be considered the source, and the rest the copies.
                    var children = parent.children();
                    var childrenNo = children.length;
                    var hasChildren = (childrenNo && childrenNo > 0);

                    //Prepare the sourceClone
                    var firstChild = parent.children(":first");
                    this.sourceClone = this.prepareCloneSource(firstChild);

                    var isFormPopulated = true;

                    //Add remove buttons to the already existing elements as these are rendered by the page
                    if (hasChildren) {
                        var that = this;
                        children.each(function () {
                            var composite = $(this);

                            var isEmpty = that.isCompositeEmpty(composite);
                            if (!isEmpty) {
                                //Add the Remove button
                                that.appendRemoveButton(composite);
                            }

                            isFormPopulated = isFormPopulated && !isEmpty;

                        });
                    }

                    if (isFormPopulated) {
                        this.appendField();
                    } else {
                        this.appendAddButton(firstChild);
                    }

                }

                , checkEmpty: function (event) {
                    var input = $(event.target);

                    if (input.parent().siblings().is("button[name='compound_multifield_btn_remove']"))
                        if (input.val() === "")
                            this.handleRemove(event);
                }
                /**
                 * Runs when the beforeadded event is fired in order to allow the component to proceed with the addition.
                 * @param event
                 * @param effect whether to use an effect to show the added html
                 */
                , doAdd: function (event) {
                    var el = $(event.target);
                    var composite = el.parents(".composite[data-index]");
                    var inputField = composite.find("input.formfield.copy");

                    var isEmpty = this.isFieldEmpty(inputField);

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
                    var index = $(composite).attr("data-index");
                    index++;
                    $(composite).siblings("[data-index=" + index + "]").promise().done(function () {
                        $(this).find("input.formfield.copy").focus();
                    });

                }
                //========== CLONING FUNCTIONS ====================
                /**
                 * Prepare/Sanitize a source based on the HTML template (first-child), which is the 
                 * @param source. Unbind the events, Reset the values of the form fields, and Delete the Remove button.
                 * @return the sourceClone html element
                 * @return whether the sourceClone :input have already values.
                 */
                , prepareCloneSource: function (source) {
                    var sourceClone = source.clone();

                    var clone = $(sourceClone);

                    //Then, the clone needs to be value free.
                    clone.find(":input:not(button)").each(function () {
                        var input = $(this);
                        input.val("");
                    });

                    //Finally, the clone should not have a remove button
                    clone.find(":button[name=\"compound_multifield_btn_remove\"]").remove();

                    clone.find("span.placeholder").removeAttr("style");

                    clone.find(":input:not(button)[data-init-index]").attr("tabIndex", "0");
                    clone.find(":input:not(button)[data-init-index]").removeAttr("data-init-index");

                    return sourceClone;
                }
                /**
                 * The actual process of removing the composite from view.
                 * Performed here as it has to take place AFTER all components have finished working on the before delete event.
                 * @param event
                 */
                , onAfterBeforeRemoved: function (event) {

                    var composite = $(event.target);
                    //...and remove the composite element
//				var that = this;
                    composite.slideUp("slow", "linear", function () {
                        var el = $(this);

                        // -1. Parent updates

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

                        // -3- Remove
                        el.remove();

                        // -4- Focus on the next
                        nextEl.find(":input:not(button)").first().focus();
                        nextEl.find(":input:not(button)").attr("tabIndex", "0");
                    });
                }
                /**
                 * Append a field to the parent element (Parent is the actual element where this functionality is bound to.
                 * @param effect may be null or undefined, if we do not need to apply effects.
                 * @return nothing
                 */
                , appendField: function (effect) {
                    var parent = this.$el;

                    var lastChild = parent.children(":last");

                    var item = MultiFieldView.prototype.prepareClone(parseInt(lastChild.attr("data-index"), 10) + 1, this.sourceClone);

                    if (effect !== undefined && effect === true) {
                        item.hide();
                    }

                    this.appendAddButton(item);

                    parent.append(item);

                    if (effect !== undefined && effect === true) {
                        item.slideDown("slow");
                    }
                }

                , isFieldEmpty: function (el) {
                    var defaultValue = el.siblings('span.placeholder').text();//elem.attr("data-defaultvalue");
                    if (defaultValue === undefined) {
                        return null;
                    }
                    var value = el.val();
                    return (value === "");
                }
                , isCompositeEmpty: function (compound) {

                    var input = compound.find(":input:not(button)").first();
                    var defaultValue = input.siblings('span.placeholder').text();
                    if (defaultValue === undefined) {
                        return null;
                    }
                    var fieldEmpty = input.val() === "";
                    return (fieldEmpty === null || fieldEmpty);
                }

            });
            return SingleInputView;
        }
);