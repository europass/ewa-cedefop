define(
        [
            'jquery',
            'backbone',
            'Utils',
            'underscore',
            'views/forms/FormView',
//		'views/interaction/Select2AutocompleteView',
//		'views/interaction/CompoundMultiFieldView',
            'views/interaction/GenderLabelView',
            'views/interaction/SelectFieldView',
            'views/interaction/TypeaheadView',
            'hbs!templates/forms/cv/personalinfo',
            'europass/maps/CountryMap',
            'europass/maps/InstantMessagingTypeMap',
            'europass/maps/NationalitiesMap',
            'europass/maps/TelephoneTypeMap',
            'europass/structures/MapWrapper'
        ],
        function ($, Backbone, Utils, _, FormView,
//		Select2AutocompleteView, CompoundMultiFieldView, 
                GenderLabelView, SelectFieldView, TypeaheadView,
                HtmlTemplate,
                CountryMap, InstantMessagingTypeMap, NationalitiesMap, TelephoneTypeMap, MapWrapper) {

            var PersonalInfoFormView = FormView.extend({
                htmlTemplate: HtmlTemplate

                , genderDivFocusClass: "genderDivFocus"

                , events: _.extend({
                    "change :input:not(button):not(:radio):not(:checkbox).Telephone[name\$=\".Use.Code\"]": "getPhoneSelection"
                    , "focus :input.formfield.css-checkbox[id\^=\"Gender\"]": "genderFocusOn"
                    , "blur :input.formfield.css-checkbox[id\^=\"Gender\"]": "genderFocusOf"
                    , "keyup input[type=email]": "isEmailValid"

                }, FormView.prototype.events)

                , enableFunctionalities: function () {
                    //call parent enable functionalities
                    FormView.prototype.enableFunctionalities.call(this);

                    //ENABLE THE AUTOCOMPLETE AND MULTIFIELD FUNCTIONALITIES
                    var frm = this.frm;

                    var that = this;
                    //1. ENABLE AUTOCOMPLETE
                    /* 1.a : Bind Country with AutoComplete and CountryMap */

                    frm.find("div.composite[name\*=\".Country\"]").each(function (idx, el) {
                        var cntAutocomplete = new TypeaheadView({
                            el: $(el),
                            minLength: 0,
                            topN: 10,
                            name: "countries",
                            map: CountryMap,
                            multipliable: false
                        });
                        that.addToViewsIndex(cntAutocomplete);
                    });
                    /* 1.b : Bind Nationality with AutoComplete and NationalityMap */
                    /*				var mapWrapper = new MapWrapper(NationalitiesMap, this.model.get("SkillsPassport.LearnerInfo.Identification.Demographics.Nationality") );
                     frm.find("div.composite.select2autocomplete[name\*=\".Nationality\"]").each ( function( idx, el){
                     var natAutocomplete = new Select2AutocompleteView({
                     el : $(el),
                     minLength: 1,
                     topN: 10,
                     map: mapWrapper
                     //selectedCodes: that.model.get("SkillsPassport.LearnerInfo.Identification.Demographics.Nationality")
                     });
                     that.addToViewsIndex( natAutocomplete );
                     });
                     */
                    var NationalitiesMapWrapper = new MapWrapper(NationalitiesMap, this.model.get("SkillsPassport.LearnerInfo.Identification.Demographics.Nationality"));
                    frm.find("div.composite.select[name\*=\".Nationality\"]").each(function (idx, el) {
                        var natAutocomplete = new TypeaheadView({
                            el: $(el),
                            minLength: 0,
                            topN: 10,
                            map: NationalitiesMapWrapper,
                            multipliable: true,
                            exclusive: true,
                            global: false
                        });
                        that.addToViewsIndex(natAutocomplete);
                    });
                    /* 1.c : Bind InstantMessaging Types with AutoComplete and InstantMessagingTypesMap */
                    /*				frm.find("div.composite.select2autocomplete[name\*=\".InstantMessaging\"]").each ( function( idx, el){
                     var imAutocomplete = new Select2AutocompleteView({
                     el : $(el),
                     minLength: 1,
                     topN: 10, 
                     map: InstantMessagingTypeMap
                     });
                     that.addToViewsIndex( imAutocomplete );
                     });
                     */
                    frm.find("div.composite.select[name\*=\".InstantMessaging\"][name$=\".Use\"]").each(function (idx, el) {
                        var cntAutocomplete = new TypeaheadView({
                            el: $(el),
                            minLength: 0,
                            topN: 10,
                            map: InstantMessagingTypeMap,
                            multipliable: true,
                            exclusive: false,
                            global: false
                        });
                        that.addToViewsIndex(cntAutocomplete);
                    });

                    /* 1.d : Bind Telephone Types with TelephoneTypesMap */
                    frm.find("div.composite.select[name\*=\"" + ".Telephone" + "\"]").each(function (idx, el) {
                        var telView = new SelectFieldView({
                            el: $(el),
                            map: TelephoneTypeMap
                        });
                        that.addToViewsIndex(telView);
                    });


                    //Bind the Gender with the GENDER_MAP ??
                    var genderWrapperId = "div" + Utils.jId("Control:LearnerInfo.Identification.Demographics.Gender");
                    var genderWrapper = $(frm.find(genderWrapperId));
                    var genderLabelView = new GenderLabelView({
                        el: genderWrapper
                    });
                    that.addToViewsIndex(genderLabelView);

                    //call parent FINALLY enable functionalities
                    FormView.prototype.finallyEnableFunctionalities.call(this);
                }
                /**
                 * @Override
                 */
                , getRelatedSections: function (section) {
                    return ["Photo", "Signature"];
                }

                , getPhoneSelection: function (event) {
                    var _selection = $(event.target);
                    var type = Utils.capitaliseFirstLetter(_selection.val());
                    var name = _selection.attr("name");
                    var parent_name = name.slice(0, name.lastIndexOf("."));
                    parent_name = "[name=\"" + parent_name + "\"]";
                    var parent = _selection.closest(parent_name);
                    if (!Utils.isEmptyObject(parent)) {
                        parent.removeClass("Work Mobile Home");
                        parent.addClass(type);
                    }

                }

                /**
                 * On gender radio button focus event, add the dotted border of the radio group
                 */
                , genderFocusOn: function (event) {
                    var genderInput = $(event.target);

                    if (genderInput === undefined || genderInput === null || genderInput === "")
                        return;

                    var genderDiv = genderInput.closest('div');
                    if (genderDiv.length > 0) {
                        genderDiv.addClass(this.genderDivFocusClass);
                    }
                }

                /**
                 * On gender radio button blur event, remove the dotted border of the radio group
                 */
                , genderFocusOf: function (event) {
                    var genderInput = $(event.target);

                    if (genderInput === undefined || genderInput === null || genderInput === "")
                        return;

                    var genderDiv = genderInput.closest('div');
                    if (genderDiv.length > 0) {
                        genderDiv.removeClass(this.genderDivFocusClass);
                    }
                }
                /*
                 * Check on type for valid email
                 */
                , isEmailValid: function (event) {

                    this.$el.find('.notification.error').fadeOut('slow', function () {
                        $(this).remove();
                    });

//				Utils.isValidEmail( event.target.value );
//				if (Utils.isValidEmail( event.target.value ) === false & event.target.value !== '') {
//					var emailFormFieldSet = this.$el.find('.formfield.Email').parent();
//					var errorMsg = emailFormFieldSet.children('.validation-error');
//					if (errorMsg.length ===0 ){
//						emailFormFieldSet.css( "background-color", "#f9c7c6" ).append(
//							"<div class='validation-error'>Please enter a valid Email Adress</div>"
//						);
//					}
//				}
//				else{
//					this.$el.find('.formfield.Email').css( "background-color", "" );
//					this.$el.find('.validation-error').remove();
//				}
                }
            });

            return PersonalInfoFormView;
        }
);
