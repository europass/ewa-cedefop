define(
[
'jquery',
'jasmine',
'backbone',
'controller/main/LayoutController'
],
function( $, jasmine, Backbone, LayoutController, weirdTextsTpl ){
	
	describe("== Page Layout Controller :: Tests ==",function(){
		
		var elName = "div";
		
		var layout = new LayoutController({ tagName : elName, id : "page-layout"});
		
		var html = layout.render().$el.html();
		
		describe("The related view includes: ",function(){
			
			it("- a header, ", function() {
				expect( html ).toMatch("<header");
			});
			it("- a notifications section, ", function() {
				expect( html ).toMatch("<section id=\"page-notifications\".*");
			});
			it("- a documents <nav>igation, ", function() {
				expect( html ).toMatch("<nav id=\"europass-documents\">");
			});
			it("- a article for the current view, ", function() {
				expect( html ).toMatch("<article");
			});
			it("- an <aside> controls area,", function() {
				expect( html ).toMatch("<aside id=\"controls\">");
			});
			it("- and a footer. ", function() {
				expect( html ).toMatch("<footer");
			});
		});
		describe("The view gets rendered ", function(){
			it("to the document "+elName+" element,", function() {
			    expect( layout.$el.prop( "tagName" ).toLowerCase() ).toBe(elName);
			});
			var pageLoadingId = "#page-loading-waiting-indicator";
			it("replacing the initially included section of the '"+pageLoadingId+"'.", function() {
				expect( layout.$el.find( pageLoadingId).length ).toBe(0);
			});
		});
		describe("The view properly translates the labels ", function(){
			var yourOpinionTxt = "Your opinion";
			it("so that one can see in the upper controls, the '"+yourOpinionTxt+"' text in English", function() {
				expect( html ).toMatch(yourOpinionTxt);
			});
			var copyrightTxt = "European Union,";
			it("and also in the footer, the '"+copyrightTxt+"' text in English.", function() {
				expect( html ).toMatch(copyrightTxt);
			});
		});
	});
});