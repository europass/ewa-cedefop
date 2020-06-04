/**
 * Provides the Greyed-out and Default Values Mechanism on the Form.
 * 
 */
define(
        [
            'jquery',
            'underscore',
            'backbone',
            'Utils',
            'europass/http/WindowConfigInstance',
            'ckeditor'
        ],
        function ($, _, Backbone, Utils, WindowConfig, CKEDITOR) {
            var DefaultsFormView = Backbone.View.extend({

                defaultvalue_attr: "data-defaultvalue",

                events: {
                    "focus :input.with-placeholder": "focused",
                    "blur :input.with-placeholder": "blurred",

                    "europass:rich-text-editor:focused": "rtefocused",
                    "europass:rich-text-editor:blurred": "rteblurred",

                    "click .placeholder.help": "placeHolderClicked",
                    "touchstart .redactor_with-placeholder.redactor_help": "placeHolderClicked",
                    "europass:multipliable:added div.multipliable > div": "multiFieldAdded",
                    "europass:formselect:changed :input:not(button)": "blurred",

                }
                , initialize: function () {
                }
                , render: function () {
                    this.initDefaults();
                }
                , initDefaults: function () {

                    var frm = this.$el.hasClass("isForm") ? this.$el : this.$el.find("form");

                    var that = this;
                    frm.find(":input:not(button).with-placeholder").each(function (i, field) {
                        var el = $(field);
                        var inner = el;

                        var value = el.val();

                        //If a composite autocomplete element, e.g. Country Label/Code we need to wrap the hidden Code as well
                        if (el.parents(".composite.autocomplete").length > 0) {
                            var p = el.parents(".composite.autocomplete");
                            inner = p.find("[rel=\"" + el.attr("rel") + "\"]");
                        }
                        //If a composite select element, e.g. Telephone Label/Code we need to wrap the hidden Code as well
                        if (el.parents(".composite.select").length > 0) {
                            var p = el.parents(".composite.select");
                            inner = p.find("[rel=\"" + el.attr("rel") + "\"]");
                        }
                        //Wrapper Element
                        var wrapper = "<div class=\"help placeholding " + (that.valueIsEmpty(value) ? "" : "hasValue") + "\" name=\"" + field.name + "\"/>";
                        inner.next('span').addBack().wrapAll(wrapper);

                        //Default Value Element

                        if (that.valueIsEmpty(value)) {
                            defaultValue = el.siblings('span').show();
                        } else {
                            defaultValue = el.siblings('span').hide();
                        }
                    });
                }

                , EMPTY_REGEXP: new RegExp(/^((<br[\/\\]?>)*|(<p>(<br[\/\\]?>)?(<\/p>)?))$/)//vpol wasRegExp(/^((<br[\/\\]?>)*|(<p>(<\/p>)?))$/)

                , valueIsEmpty: function (value) {
                    return (value === "" || $.trim(value) === "" || this.EMPTY_REGEXP.test(value));
                }
                , multiFieldAdded: function (event) {
                    var that = this;
                    var multipliable = $(event.target);
                    //If the input values are empty, then the span must be shown
                    var emptyFields = multipliable.find(":input:not(button):not(:radio):not(:checkbox)").filter(Utils.filterEmptyVal);
                    var unchekcedRadios = multipliable.find(":radio:not(checked)");
                    var fields = emptyFields.add(unchekcedRadios);
                    fields.each(function (idx, el) {
                        var input = $(el);
                        //We need to set its value to "", otherwise the unbind group will find that there is a group member with non empty value and will not unbind the group preference
                        if (that.valueIsEmpty(input.val())) {
                            that.addDefault(input);
                        }
                    });
                }
                , getElementInfo: function (changed) {
                    var el = $(changed);
                    var key = el.attr('name');
                    var value = $('[name="' + key + '"]').val();
                    return {'el': el, 'key': key, 'value': value};
                }
                /**
                 * Find parent division placeholding
                 */
                , getPlaceHolding: function (el) {
                    return el.parents(".placeholding");
                }
                /**
                 * Find placeholder span
                 */
                , getPlaceHolder: function (field) {
                    var placeholder = field.siblings("span.placeholder");
                    if (placeholder.length === 0) {
                        placeholder = this.getPlaceHolding(field).find("span.placeholder");
                    }
                    return placeholder;
                }
                , getInput: function (placeholder) {
                    return placeholder.siblings(":input:not(button).with-placeholder");
                }
                /**
                 * remove the default text
                 */
                , removeDefault: function (el) {
                    this.getPlaceHolder(el).hide();
                    this.getPlaceHolding(el).addClass("hasValue");
                }
                /**
                 * add the default text
                 */
                , addDefault: function (el) {
                    this.getPlaceHolder(el).show();
                    this.getPlaceHolding(el).removeClass("hasValue");
                }
                /**
                 * Event when starting to type inside the field.
                 * Unbind immediatelly the event and remove the placeholder text
                 */
                , onStartTyping: function (event) {
                    var el = $(event.target);

                    //var test = $._data( el[0], "events" );
                    el.unbind("keypress.ewa.form.defaults");//.unbind("paste");

                    this.removeDefault(el);
                }

                /* vpol: this is a nasty fix for android devices
                 * the event for keypress/keydown/keyup in android tablets is triggered by the the input event
                 * https://developer.mozilla.org/en-US/docs/Web/Events/input
                 * http://stackoverflow.com/questions/21675458/use-keyboard-and-jquery-document-keypress-on-touch-devices?rq=1
                 */
                , onStartTypingAndroid: function (event) {
                    var el = $(event.target);
                    //el.unbind("keypress.ewa.form.defaults");
                    //var test = $._data( el[0], "events" );
                    //EWA1817-C1 Typeahead was not working, so it was excluded from the unbind
                    el.unbind("input .with-placeholder:not[.tt-input]");

                    // commented out 2016-Nov-07 to fix tons of regex exceptions on android. see eworx EPAS-110
                    //el.unbind("input .redactor_with-placeholder.redactor_help");
                    this.removeDefault(el);
                }
                /**
                 * Event when a value is selected
                 */
                , onSelected: function (event) {
                    var el = $(event.target);
                    el.unbind("europass:autocomplete:selected");

                    this.removeDefault(el);
                }
                /**
                 * When the placeholder span gets clicked
                 */
                , placeHolderClicked: function (event) {
                    //console.log("placehClicked");
                    var placeholder = $(event.target);
                    var input = this.getInput(placeholder);

                    input.focus();

                    this.getPlaceHolding(placeholder).trigger("europass:form:defaults:clicked");
                }
                /**
                 * When an input field gets focused
                 */
                , focused: function (event) {
                    var el = $(event.target);
                    var value = el.val();
//				console.log("focused");
                    if (this.valueIsEmpty(value)) {

                        if (WindowConfig.operatingSystem === "ANDROID" || el.is("select") || el.is("textarea")) {
                            this.removeDefault(el);
                        }

                        if (el.is("textarea")) {
                            this.$el.find("body").focus();
                        }
                        //Bind events
                        if (!el.is("select")) {
                            //el.bind ("keypress.ewa.form.defaults", $.proxy( this.onStartTyping, this ) );
                            //el.bind ("europass:autocomplete:selected", $.proxy( this.onSelected, this ) );

                            // Let s not handle specially Android for now!
                            // if ((navigator.appVersion).indexOf('Android') > 0) {
                            // 	el.bind("input :input.with-placeholder", $.proxy(this.onStartTypingAndroid, this));
                            // }

                            el.bind("keypress.ewa.form.defaults", $.proxy(this.onStartTyping, this))
                                    .bind("paste", $.proxy(this.onStartTyping, this));

                            el.bind("europass:autocomplete:selected", $.proxy(this.onSelected, this));

                        }
                    }
                }

                /**
                 * On blur if the value is empty then restore the default text and rebind the events
                 */
                , blurred: function (event) {
                    //console.log("blurred");
                    var el = $(event.target);
                    var value = el.val();

                    if (this.valueIsEmpty(value)) {
                        this.addDefault(el);
                        //Rebind events
                        if (!el.is("select") || el.is("textarea")) {
                            el.bind("keypress.ewa.form.defaults", $.proxy(this.onStartTyping, this));
                            el.bind("europass:autocomplete:selected", $.proxy(this.onSelected, this));
                        }
                    } else {
                        this.removeDefault(el);
                    }
                }
            });
            return DefaultsFormView;
        }
);