define(
[
'jquery',
'underscore',
'jasmine',
'mustache',
'text!test/templates/various-texts.html',
'manager/Bundles',
'i18n!nls/GuiLabel',
'i18n!nls/DocumentLabel'
],
function( $, _, jasmine, Mustache, weirdTextsTpl, Bundles, GuiLabel, DocumentLabel ){
	
//	describe("== Mustache compile template with Bundles :: Tests ==",function(){
//		
//		var thisTemplate = Mustache.compile( weirdTextsTpl );
//		
//		var enrichedDocumentLabel = _.extend( {}, DocumentLabel );
//		enrichedDocumentLabel["smart"] = "Hello, my name is [[name.first]] [[name.surname]]";
//		
//		var bundle = Bundles.load( [{"GuiLabel": GuiLabel }, {"DocumentLabel": enrichedDocumentLabel }] );
//		
//		var html = thisTemplate( bundle );
//		
//		describe("The html includes the properly translated labls of ", function(){
//			var cvTxt = "Curriculum Vitae";
//			it("'"+cvTxt+"' text in English", function() {
//				expect( html ).toMatch(cvTxt);
//			});
//			var copyrightTxt = "European Union,";
//			it("and '"+copyrightTxt+"' text in English.", function() {
//				expect( html ).toMatch(copyrightTxt);
//			});
//		});
//		describe("The html also includes the properly managed \"smart\" text", function(){
//			var txt = enrichedDocumentLabel["smart"].replace("[[name.first]]", "John").replace("[[name.surname]]", "Doe");
//			it("that replaces keys with dynamic text.", function() {
//				expect( html ).toMatch(txt);
//			});
//		});
//	});
});