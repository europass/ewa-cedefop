define([ 'jquery',
         'jasmine',
         'backbone',
         'backboneSubModel',
         'model/europass/SkillsPassport'], 

function( $, jasmine, Backbone, SubModel, SkillsPassport ) {
	
	describe("SubModel :: Event Tests ", function(){
		
		describe("-- Set PersonName and monitor the 'subModelChanged' event -- ", function(){	
			
			var firstNameKey = "LearnerInfo.Identification.PersonName.FirstName";
			var firstNameValue = "Babis";
				
			beforeEach(function(){
				spyOn( Backbone.SubModel.prototype, 'subModelChanged').andCallThrough();
				var esp = new SkillsPassport();
				esp.set( firstNameKey, firstNameValue );
			});
			
			it("'subModelChanged' was called", function() {
				expect(Backbone.SubModel.prototype.subModelChanged).toHaveBeenCalled();
			});

			it("three (3) times while bubbling up the change event", function() {
				expect(Backbone.SubModel.prototype.subModelChanged.calls.length).toEqual(3);
			});

			it("First, 'subModelChanged' was called on 'PersonName' after setting the FirstName", function() {
				expect(Backbone.SubModel.prototype.subModelChanged.calls[0].object.name).toEqual("PersonName");
			});
			it(" then 'subModelChanged' was called on 'Identification'", function() {
				expect(Backbone.SubModel.prototype.subModelChanged.calls[1].object.name).toEqual("Identification");
			});
			it(" then 'subModelChanged' was called on 'LearnerInfo'", function() {
				expect(Backbone.SubModel.prototype.subModelChanged.calls[2].object.name).toEqual("LearnerInfo");
			});
			
		});
		
		describe("-- Set Certificate Titles in Foreign Language and monitor 'subModelChanged' and 'subCollectionAdded' events -- ", function(){	
			
			var key1 = "LearnerInfo.Skills.Linguistic.ForeignLanguage[0].Certificate[0].Title";
			var key2 = "LearnerInfo.Skills.Linguistic.ForeignLanguage[0].Certificate[1].Title";
			var title1 = "Cambridge";
			var title2 = "Michigan";
				
			beforeEach(function(){
				spyOn( Backbone.SubModel.prototype, 'subModelChanged').andCallThrough();
				spyOn( Backbone.SubModel.prototype, 'subCollectionAdded').andCallThrough();
				var esp = new SkillsPassport();
				esp.set( key1, title1 );
				esp.set( key2, title2 );
			});
			
			it("'subModelChanged' was called", function() {
				expect(Backbone.SubModel.prototype.subModelChanged).toHaveBeenCalled();
			});

			it(""+(2*5)+" times, 5 times while bubbling up the event for each set of the Certificate Title", function() {
				expect(Backbone.SubModel.prototype.subModelChanged.calls.length).toEqual( 2*5 );
			});
			
			it("'subCollectionAdded' was also called", function() {
				expect(Backbone.SubModel.prototype.subCollectionAdded).toHaveBeenCalled();
			});

			it("In total "+(1+2*1)+" times, 1 for Foreign Language, and 1 for each of the 2 Certificates", function() {
				expect(Backbone.SubModel.prototype.subCollectionAdded.calls.length).toEqual( 3 );
			});

			it("When setting the Certificate Title...\nFirst, 'subModelChanged' was called on 'Certificate' after setting the Title", function() {
				expect(Backbone.SubModel.prototype.subModelChanged.calls[0].object.name).toEqual("Certificate");
			});
			it(" then 'subModelChanged' was called on 'ForeignLanguage'", function() {
				expect(Backbone.SubModel.prototype.subModelChanged.calls[1].object.name).toEqual("ForeignLanguage");
			});
			it(" then 'subModelChanged' was called on 'Linguistic'", function() {
				expect(Backbone.SubModel.prototype.subModelChanged.calls[2].object.name).toEqual("Linguistic");
			});
			it(" then 'subModelChanged' was called on 'Skills'", function() {
				expect(Backbone.SubModel.prototype.subModelChanged.calls[3].object.name).toEqual("Skills");
			});
			it(" then 'subModelChanged' was called on 'LearnerInfo'", function() {
				expect(Backbone.SubModel.prototype.subModelChanged.calls[4].object.name).toEqual("LearnerInfo");
			});
			
		});
		
		describe("-- Set Certificate Titles in Foreign Language, call sort() and monitor 'subCollectionSorted' event -- ", function(){	
			
			var key1 = "LearnerInfo.Skills.Linguistic.ForeignLanguage[0].Certificate[0].Title";
			var key2 = "LearnerInfo.Skills.Linguistic.ForeignLanguage[0].Certificate[1].Title";
			var title1 = "Cambridge";
			var title2 = "Michigan";
				
			beforeEach(function(){
				spyOn( Backbone.SubModel.prototype, 'subCollectionSorted').andCallThrough();
				var esp = new SkillsPassport();
				esp.set( key1, title1 );
				esp.set( key2, title2 );
				var certificates = esp.get("LearnerInfo.Skills.Linguistic.ForeignLanguage[0].Certificate");
				certificates.comparator = function( model ) {
					  return model.get( "Title" );
				};
				certificates.sort();
			});
			
			it("'subCollectionSorted' was called", function() {
				expect(Backbone.SubModel.prototype.subCollectionSorted).toHaveBeenCalled();
			});

			it("once during sorting", function() {
				expect(Backbone.SubModel.prototype.subCollectionSorted.calls.length).toEqual( 1 );
			});
			
		});
		
		describe("-- Set Certificate Titles in Foreign Language, then unset() the second Certificate, and monitor 'subCollectionRemoved' event -- ", function(){	
			
			var key1 = "LearnerInfo.Skills.Linguistic.ForeignLanguage[0].Certificate[0].Title";
			var key2 = "LearnerInfo.Skills.Linguistic.ForeignLanguage[0].Certificate[1].Title";
			var title1 = "Cambridge";
			var title2 = "Michigan";
				
			var esp = undefined;
			beforeEach(function(){
				spyOn( Backbone.SubModel.prototype, 'subCollectionRemoved').andCallThrough();
				esp = new SkillsPassport();
				esp.set( key1, title1 );
				esp.set( key2, title2 );
				esp.unset("LearnerInfo.Skills.Linguistic.ForeignLanguage[0].Certificate[1]");
	
			});
			
			it("'subCollectionRemoved' was called", function() {
				expect(Backbone.SubModel.prototype.subCollectionRemoved).toHaveBeenCalled();
			});

			it("once for unseting the second Certificate", function() {
				expect(Backbone.SubModel.prototype.subCollectionRemoved.calls.length).toEqual( 1 );
			});
			
			it("Certificate Collection now has only 1 item, titled '"+title1+"'", function() {
				var certificates = esp.get("LearnerInfo.Skills.Linguistic.ForeignLanguage[0].Certificate");
				expect( certificates.length ).toBe( 1 );
				expect( certificates.at(0).get("Title") ).toBe( title1 );
			});
			
			it("The Foreign Language Collection remains unaffected and contains 1 item", function() {
				var languages = esp.get("LearnerInfo.Skills.Linguistic.ForeignLanguage");
				expect( languages.length ).toBe( 1 );
			});
			
		});
		
		describe("-- Set Certificate Titles in Foreign Language, then unset() the entire Foreign Language, and monitor 'subCollectionRemoved' event -- ", function(){	
			
			var key1 = "LearnerInfo.Skills.Linguistic.ForeignLanguage[0].Certificate[0].Title";
			var key2 = "LearnerInfo.Skills.Linguistic.ForeignLanguage[0].Certificate[1].Title";
			var title1 = "Cambridge";
			var title2 = "Michigan";
				
			var esp = undefined;
			beforeEach(function(){
				spyOn( Backbone.SubModel.prototype, 'subCollectionRemoved').andCallThrough();
				esp = new SkillsPassport();
				esp.set( key1, title1 );
				esp.set( key2, title2 );
				esp.unset("LearnerInfo.Skills.Linguistic.ForeignLanguage[0]");
	
			});
			
			it("'subCollectionRemoved' was called", function() {
				expect(Backbone.SubModel.prototype.subCollectionRemoved).toHaveBeenCalled();
			});

			it("once for removing the first and only Foreign Language", function() {
				expect(Backbone.SubModel.prototype.subCollectionRemoved.calls.length).toEqual( 1 );
			});
			
			it("Certificate Collection now is undefined", function() {
				var certificates = esp.get("LearnerInfo.Skills.Linguistic.ForeignLanguage[0].Certificate");
				expect( certificates ).toBeUndefined();
			});
			
			it("And the Foreign Language collection is empty", function() {
				var languages = esp.get("LearnerInfo.Skills.Linguistic.ForeignLanguage");
				expect( languages.length ).toBe( 0 );
			});
			
		});
	});
	
});