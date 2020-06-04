define(
        [
            'jquery',
            'jqueryui',
            'underscore', //	'Utils',
            'Interactions',
            'views/forms/FormView',
            'views/interaction/TypeaheadView',
            'views/interaction/SelectFieldView',
            'hbs!templates/forms/cv/headline',
            'europass/maps/HeadlineTypeMap',
            //'i18n!localization/nls/HeadlineType',
            'i18n!localization/nls/GuiLabel'//,'views/interaction/RichTextEditorView'
        ],
        function ($, jqueryui, _, Interactions, FormView, TypeaheadView, SelectFieldView, HtmlTemplate,
                HeadlineTypeMap, GuiLabel) {

            var HeadlineFormView = FormView.extend({

                events: _.extend({
                    "europass:custom_select:type": "changed"
//				"mousedown .tt-suggestion" : "suggestionFocused",
//				"mouseup .tt-suggestion" : "suggestionSelected"
                }, FormView.prototype.events),

                hybridSelector: "div.occupation-related",

                simpleSelector: "div.non-occupation-related",

                personalStatementSelector: "div.personal-statement",

                htmlTemplate: HtmlTemplate

                , changed: function (event) {
                    var frm = this.frm;

                    var typeInput = $(event.target).parent().find('select');

                    if (!typeInput.is("select"))
                        return;

                    var selectedType = typeInput.val();
                    var hybridDiv = frm.find(this.hybridSelector);
                    var simpleDiv = frm.find(this.simpleSelector);
                    var letterDiv = frm.find(this.personalStatementSelector);

                    switch (selectedType) {
                        case "studies_applied_for":
                        {
                            this.switchDivDisplay([hybridDiv, letterDiv], simpleDiv);
                            break;
                        }
                        case "personal_statement" :
                        {
                            this.switchDivDisplay([hybridDiv, simpleDiv], letterDiv);
                            break;
                        }
                        default:
                        {
                            this.switchDivDisplay([simpleDiv, letterDiv], hybridDiv);
                            break;
                        }
                    }

                    var selectedTypeLabel = "";
                    if (selectedType !== "") {
                        selectedTypeLabel = typeInput.find("option[value='" + selectedType + "']").text();
                    }

                    var inputLabel = this.$el.find("input[name\*=\"LearnerInfo\.Headline\.Type\"]").filter(":hidden");
                    if (inputLabel !== undefined) {
                        inputLabel.val(selectedTypeLabel);
                    }
                    frm.trigger("europass.remove.helpTip");//remove help tooltip when change position and its related input fields
                }

                , switchDivDisplay: function (hideDivArray, showDiv) {
                    //always one div should be visible
                    if (showDiv.is(":visible") === false) {
                        //hide others
                        $.each(hideDivArray, function (idx, hideDiv) {
                            if (hideDiv.is(":visible") === true) {
                                hideDiv.toggleClass("inactive");
                                hideDiv.slideUp("slow", function () {
                                    var el = hideDiv.find(":input:not(button)");
                                    if (el.is("textarea.rich-editor")) {
                                        el.trigger("europass:rte:clear");
                                    } else {
                                        $.each(el, function (idx, subel) {
                                            $(subel).val("");
                                            $(subel).trigger("europass:clear:typeahead");
                                        });
                                    }
                                });
                            }
                        });
                        showDiv.toggleClass("inactive");
                        showDiv.slideDown("slow");
                    }
                }

                , enableFunctionalities: function () {
                    //call parent enable functionalities
                    FormView.prototype.enableFunctionalities.call(this);
                    var frm = this.frm;
                    var that = this;
                    //1. ENABLE AUTOCOMPLETE
                    /* Bind headline description with AutoComplete and PositionMap according to gender*/
                    var map = this.model.translation().chooseOccupationMap();
//				frm.find("div.composite.select[name\*=\"" + ".Description"+"\"]").each ( function( idx, el){
//					var headlineAutocomplete = $(el).find("input.typeahead").typeahead(null,{
//								name: "choices",
//								 displayKey: 'value',
//								 displayValue: 'value',
//								 source: TypeaheadView.prototype.startsWithAgainstMap(map.values)
////								 ,templates: {
////									 suggestion: handlebars.compile('<a href="#" class="suggestion-link" style="width:100%;padding:0">{{value}}</a>')
////								 } 
//							});
////					that.addToViewsIndex( headlineAutocomplete );
//				});
                    frm.find("div.composite.select[name\*=\"" + ".Description" + "\"]").each(function (idx, el) {
                        var headlineAutocomplete = new TypeaheadView({
                            el: $(el),
                            minLength: 0,
                            topN: 10,
                            name: "headline",
                            map: map
                        });
                        that.addToViewsIndex(headlineAutocomplete);
                    });

                    /* 2 : Bind Headline Types with HeadlineTypesMap */
//				frm.find("div.composite.select[name\*=\"" + ".Headline.Type"+"\"]").each ( function( idx, el){
                    frm.find("div.composite.select .custom_select").each(function (idx, el) {
                        var headlineTypeView = new SelectFieldView({
                            el: $(el),
                            map: HeadlineTypeMap,
                            // pgia: isEmpty and allowClear are set to decide if the .clear or the .arrow span must be visible
                            isEmpty: $(el).parent().find('select').val === "",
                            allowClear: true
                        });
                        that.addToViewsIndex(headlineTypeView);

                    });

                    FormView.prototype.finallyEnableFunctionalities.call(this);

                }, //end enableFunctionalities

                /**
                 * Before rendering the form, set the value of the hidden input field of headline.type.label with the first option's label of the headline.type select
                 * the headline.select options are initialized with the loop-for-map helper witch sets the first option to selected when there is no context
                 * The headline.type.label initialization occurs only when there is no learnerInfo context 
                 */
                render: function (index, subsection) {
                    FormView.prototype.render.call(this);
                    var context = this.model.get("SkillsPassport.LearnerInfo");

                    if ($.isEmptyObject(context) || context.Headline === undefined || $.isEmptyObject(context.Headline) ||
                            (context.Headline.Type !== undefined && context.Headline.Type.Code === undefined)) {

                        var inputLabel = this.$el.find("input[name\*=\"LearnerInfo\.Headline\.Type\"]").filter(":hidden");
                        if (inputLabel !== undefined) {
                            inputLabel.val("");
                        }
                    }
                    //--------- Delete the val of non-related Headline.Description fields depending on the Headline.Type.Code
                    var typeInput = this.$el.find(":input[name$=\"Headline\.Type\.Code\"]");
                    var selectedType = typeInput.val();
                    //console.log("selectedType: "+selectedType);
                    var hybridDiv = this.$el.find(this.hybridSelector);
                    var simpleDiv = this.$el.find(this.simpleSelector);
                    var letterDiv = this.$el.find(this.personalStatementSelector);

                    switch (selectedType) {
                        case "studies_applied_for" :
                        {
                            hybridDiv.find(":input:not(:button)").val('');
                            letterDiv.find('textarea.rich-editor').html('');
                            letterDiv.find(":input:not(:button)").val('');
                            break;
                        }
                        case "personal_statement" :
                        {
                            hybridDiv.find(":input:not(:button)").val('');
                            simpleDiv.find(":input:not(:button)").val('');
                            break;
                        }
                        default :
                        {
                            simpleDiv.find(":input:not(:button)").val('');
                            letterDiv.find(":input:not(:button)").val('');
                            letterDiv.find('textarea.rich-editor').html('');
                            break;
                        }
                    }
                }
            });
            return HeadlineFormView;
        }
);
