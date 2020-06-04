define([ 'jquery',
         'jquery-ui',
         'underscore',
         'backbone', 
         'jasmine-html', 
         'models/SkillsPassport',
         'views/compose/cv/SkillsPassportComposeView',
         'ModalFormInteractions',
         'models/PrintingPreferencesModel',
         'templates/helpers/get_text',
         'views/forms/FormView',
         'BackboneNestedModelAugmented'], 

function($, _, jqueryUI,
		Backbone, 
		jasmine, 
		SkillsPassport,
		SkillsPassportComposeView, 
		ModalFormInteractions, 
		PrintingPreferencesModel, 
		get_text,
		FormView,
		BackboneNestedModelAugmented) {

	describe("SkillsPassportComposeView :: check upon date", function() {
		
		$('body').append('<section style="display:none" id="SkillsPassport.LearnerInfo.WorkExperience[0]">\
				<form id="Form:WorkExperience[0]" class="modalform modal-window ui-dialog-content ui-widget-content" data-rel-section="SkillsPassport.LearnerInfo.WorkExperience[0]">\
				<fieldset class="LearnerInfo.Identification.PersonName PersonName">\
				<fieldset class="LearnerInfo.Identification.PersonName.FirstName FirstName">\
					<label for="FirstName">{{get_text "LearnerInfo.Identification.PersonName.FirstName"}}</label>\
					<input type="text" \
							class="formfield help mandatory pref PersonName"\
							name="LearnerInfo.Identification.PersonName.FirstName"\
							data-bind-name="LearnerInfo.Identification.PersonName"\
							data-bind-document="ECV ELP"\
							data-help-key="LearnerInfo.Identification.PersonName.FirstName"\
							value="Person\'s First Name">\
					<span name="LearnerInfo.Identification.PersonName.FirstName" class="help placeholder" style="display:none;" >\
					  {{{get_text "LearnerInfo.Identification.PersonName.FirstName" group="EditorPlaceholder"}}}\
					</span>\
				</fieldset>\
				<fieldset class="LearnerInfo.Identification.PersonName.Surname Surname">\
					<label for="Surname">{{get_text "LearnerInfo.Identification.PersonName.Surname"}}</label>\
					<!--  data-defaultvalue="{{get_text "LearnerInfo.Identification.PersonName.Surname" group="EditorPlaceholder"}}" -->\
					<input type="text" \
							class="formfield help mandatory pref PersonName"\
							name="LearnerInfo.Identification.PersonName.Surname"\
							data-bind-name="LearnerInfo.Identification.PersonName"\
							data-bind-document="ECV ELP"\
							data-help-key="LearnerInfo.Identification.PersonName.Surname"\
							value="Person\'s Lst Name">\
					<span name="LearnerInfo.Identification.PersonName.Surname" class="help placeholder" style="display:none;">\
						{{get_text "LearnerInfo.Identification.PersonName.Surname" group="EditorPlaceholder"}}\
					</span>\
				</fieldset>\
			</fieldset>\
				</form>\
				</section>\
				');
		
		var PPmodel = new PrintingPreferencesModel();
		var SPmodel = new SkillsPassport({model : PPmodel});
		
		var view = new FormView({//htmlTemplate : template,
									root: 'body',
									el: '#Form\\:LearnerInfo\\.Identification',
									model: SPmodel,
									section: 'SkillsPassport.LearnerInfo.Identification'
								});
		view.initialize();
		ModalFormInteractions.getModalForm("testDialog",view.section);
		ModalFormInteractions.simpleOpenFormByInfo({ form : "testDialog"});
		
		view.submitted();
		
		/* sofia's code
			var fromMonthEl = this.$el.find(":input:not(:button).formfield:not[name$=\"Description."+copyFrom+"."+month+"\"]");
			var toMonthEl = this.$el.find(":input:not(:button).formfield:not[name$=\"Description."+copyTo+"."+month+"\"]");
			
			destEl.select2("val", valueEntered);
		 */
		
		it("button clicked", function() {
			
			//alert(view.model.SkillsPassport.ECV.LearnerInfo.Identification.PersonName.Firstname);
			//alert(view.model.SkillsPassport.ECV.LearnerInfo.Identification.PersonName.Lastname);
			
//			var spy = spyOn(SPcomposeView, 'openModalForm');
//			SPcomposeView.delegateEvents();
//		
//			SPcomposeView.initialize({ options: {}});
//			
//			expect(spy).toHaveBeenCalled();
			
		});
	});
});