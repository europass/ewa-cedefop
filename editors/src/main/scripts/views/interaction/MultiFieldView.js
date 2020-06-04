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
            var MultiFieldView = Backbone.View.extend({

                tablets: {},

                events: {
                    //ALL FIELDS
//				"blur :input:not(:button).copy" : "onBlurred",

                    //ALL BUTTONS
                    "click :button[name=\"compound_multifield_add_remove\"]": "handleAdd",
                    "click :button[name=\"compound_multifield_btn_remove\"]": "handleRemove",

                    "europass:multipliable:beforeadded": "onAfterBeforeAdded",
                    "europass:multipliable:beforeremoved": "onAfterBeforeRemoved",

                    "europass:multipliable:removed": "onAfterRemoved"
                }
                , onClose: function () {
                    delete this.sourceClone;
                    delete this.enableRemove;
                    delete this.removeButton;
                    delete this.addButton;
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

                    // Used for multifield composites number restrictions (like CL Holder Details)
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
                //To be Overriden
                , enableFunctionality: function (parent) {},

                //========== EVENTS ===============================
                onBlurred: function (event) {
                    var el = $(event.target);

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
                    // return false;
                    // } else {
                    // this.doRemove(event);
                    // }
                    this.doRemove(event);

                }
                , doRemove: function (event) {
                    var el = $(event.target);
                    var composite = el.parents(".composite[data-index]");
                    this.removeField(composite);
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
                // To Be Overriden
                , doAdd: function (event) { }
                /**
                 * Performs the actual addition of a remove button to the last copy of the multiple element
                 * and the addition of a new element.
                 * @param the composite
                 * @param effect whether to use an effect to show the added html
                 */
                // To be Overriden
                , onAfterBeforeAdded: function (event, effect) { }
                //========== CLONING FUNCTIONS ====================
                /**
                 * Prepare/Sanitize a source based on the HTML template (first-child), which is the 
                 * @param source. Unbind the events, Reset the values of the form fields, and Delete the Remove button.
                 * @return the sourceClone html element
                 * @return whether the sourceClone :input have already values.
                 */
                //To Be Overriden
                , prepareCloneSource: function (source) {
                    var sourceClone = source.clone();

                    return $(sourceClone);
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

//				source is made with this.prepareCloneSource();
                    var sanitizedClone = $(source);
                    var replacement = "[" + nextIndex + "]";
                    var theClone = sanitizedClone.clone();

                    Utils.reIndexElementAndChildren($(theClone), replacement, parseInt($(source).attr("data-index"), 10));

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
                    this.configLabelRemoveButton(el);

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
                    this.replaceBtnLabelWithLanguageSpecific(el);

                    if (effect !== undefined) {
                        btnEl.fadeIn("slow");
                    }
                }

                // Logic to replace btn label with Specific for btns when adding new Language
                , replaceBtnLabelWithLanguageSpecific: function (el) {
                    if (typeof el === 'undefined') {
                        el = this.$el;
                    }
                    var btnEl = $(el).find("[name=\"compound_multifield_add_remove\"]");
                    if (this.checkFieldInputIsLanguage(btnEl)) {
                        btnEl.find('.data-title').text(EditorHelp["Multifield.Add.Language.Field.Tooltip"]);
                    }
                }
                , configLabelRemoveButton: function (el) {
                    var btnEl = $(el).find("[name=\"compound_multifield_btn_remove\"]");
                    if (this.checkFieldInputIsLanguage(btnEl)) {
                        btnEl.find('.data-title').text(EditorHelp["Multifield.Remove.Language.Field.Tooltip"]);
                    }
                }
                , checkFieldInputIsLanguage: function (btn) {
                    if (btn.closest('fieldset.MotherTongue').length > 0
                            || btn.closest('fieldset.LearnerInfo\\.Skills\\.Linguistic\\.MotherTongue').length > 0
                            || btn.closest('fieldset.ForeignLanguage.ELP.Overview').length > 0) {
                        return true;
                    }
                    return false;
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

                    //fire the before remove callback fpr the pref and for the item
                    composite.trigger("europass:multipliable:removepref");
                    composite.trigger("europass:multipliable:beforeremoved", [codeLabelInfo]);
                    //and continue with the implementation onAfterBeforeRemove
                }

                /**
                 * The actual process of removing the composite from view.
                 * Performed here as it has to take place AFTER all components have finished working on the before delete event.
                 * @param event
                 */
                //To Be Overriden
                , onAfterBeforeRemoved: function (event) { }
                /**
                 * Runs just before element is removed and AFTER reindexing of the rest elements is complete
                 */
                // To be Overriden
                , onAfterRemoved: function (event) { }
                /**
                 * Append a field to the parent element (Parent is the actual element where this functionality is bound to.
                 * @param effect may be null or undefined, if we do not need to apply effects.
                 * @return nothing
                 */
                // To Be Overriden
                , appendField: function (effect) { }
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

                // To Be Overriden
                , isFieldEmpty: function (el) {
                    return false;
                }

                /**
                 * Will find all children and hide those that exceed the limit.
                 * If the limit is 1, then the remove button should not be displayed at all.
                 */
                // to be overriden
                , populateLimitedMulipliables: function (callback, scope) { }
                /**
                 * Decides how to disable the form field according to its type:
                 * simple input, select or hybrid
                 */
                /*			,FIELD_SIMPLE : "input"
                 ,FIELD_SELECT : "select"
                 ,FIELD_HYBRID : "hybrid"
                 
                 ,disableSelectAndInput : function( parent, elem ){
                 if ( this.maxItems != null && parent !== undefined ){
                 
                 var index = parseInt(parent.attr("data-index"));
                 var limit = parseInt(this.maxItems, 10);
                 var exceedsLimit = index >= limit;
                 
                 if ( elem.is("select.fancySelect.formfield") ){
                 if ( exceedsLimit )
                 elem.trigger("disable");
                 else
                 elem.trigger("enable");
                 
                 elem.trigger("update.fs");
                 
                 }else if ( elem.is( ":input.formfield:not(button):not([type='hidden'])" )) {
                 
                 elem.attr("readonly",exceedsLimit);
                 if ( exceedsLimit && !elem.hasClass("readonlyFormfield")){
                 elem.addClass("readonlyFormfield");
                 }
                 else if ( !exceedsLimit && elem.hasClass("readonlyFormfield")){
                 elem.removeClass("readonlyFormfield");	
                 }	
                 }
                 }
                 }*/
            });
            return MultiFieldView;
        }
);