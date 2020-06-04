define([ 'jquery', 'backbone', 'jasmine-html', 'models/SkillsPassport', 'models/TranslationManager'], 

function($, Backbone, jasmine, SkillsPassport, TranslationManager) {

	
	
	describe("SkillsPassport :: given SkillsPassport.LearnerInfo.Demographics.Nationality[0].Code = 'EL'\n" +
			"and SkillsPassport.LearnerInfo.Demographics.Nationality[0].Label = 'Ελληνική'", function() {
		
		var sp = new SkillsPassport();
		
		var nationalityCode = { "SkillsPassport.LearnerInfo.Demographics.Nationality[0].Code" : "EL" };

		sp.set( nationalityCode, {silent: true} );
		
		var nationalityLabel = { "SkillsPassport.LearnerInfo.Demographics.Nationality[0].Label" : "Ελληνική" };

		sp.set( nationalityLabel, {silent: true} );
		
		sp.translation().lookups();
		
		var label = sp.get("SkillsPassport.LearnerInfo.Demographics.Nationality[0].Label");
		
		it("SkillsPassport.LearnerInfo.Demographics.Nationality[0].Label translates as '"+label+"'", function() {
			expect(label).toEqual('Greek');
		});
	});
});

