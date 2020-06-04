define(
        [
            'jquery',
//		EWA-1811
            'underscore',
            'views/forms/FormView',
            'hbs!templates/forms/elp/elpCEFRGrid'
        ],
        function ($,
//		EWA-1811
                _,
                FormView, HtmlTemplate) {

            var CEFRGridFormView = function (options) {
                FormView.apply(this, [options]);
            };

            CEFRGridFormView.prototype = {
                htmlTemplate: HtmlTemplate

                , include: true

                , events: _.extend({
                    "click .toggle-grid": "toggleCEFRShow"
                }, FormView.prototype.events)

//			,render: function( index, subsection ){
//				
//				FormView.prototype.submitted.apply( this, [index, subsection] );				
//			}

                        /**
                         * @Override
                         */
                , submitted: function (event) {
                    this.$el.trigger("europass:waiting:indicator:show");

                    $("body").find("section.Compose").trigger("europass:cefrgrid:toggle", [this.include]);

//				var prefs = this.model.getPreferences("ELP");
//				if ( prefs.LearnerInfo.CEFLanguageLevelsGrid.show ){
//					
//				}

                    FormView.prototype.submitted.apply(this, [event]);
                }

                /**
                 * @Override
                 */
                , cancelled: function (event) {
                    FormView.prototype.cancelled.apply(this, [event]);
                },

                /*
                 * Triggers the change of the preference related to the inclusion of the CEFR LEvels grid in the generated LP document
                 */

                toggleCEFRShow: function (event) {

                    var elem = $(event.currentTarget);

                    this.include = elem.hasClass("include") ? true : false;

                    elem.toggleClass("blue-pressed");
                    elem.siblings(".toggle-grid").toggleClass("blue-pressed");

//				$("body").find(".elp.compose .elp-overview").trigger("europass:cefrgrid:show", [ input, include ]);
                }

            };

            CEFRGridFormView.prototype = $.extend(
                    //true, 
                            {},
                            FormView.prototype,
                            CEFRGridFormView.prototype
                            );

                    return CEFRGridFormView;
                }
        );