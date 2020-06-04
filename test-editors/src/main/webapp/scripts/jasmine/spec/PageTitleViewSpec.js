define([ 
         'jquery', 
         'backbone', 
         'jasmine-html', 
         'views/interaction/PageTitleView', 
         'models/NavigationRoutes', 
         'i18n!localization/nls/GuiLabel'
         ], 

function($, Backbone, jasmine, PageTitleView, NavigationRoutes, GuiLabel ) {

	model =  new NavigationRoutes();
	view = new PageTitleView( {model : model} );
	
	$.each(model.viewsPerRoute, function(route, value) {

		//regular expression matches: (part1) or (part1.part2)
		
		var regexp = "^(upload|remote-upload|compose|download|google-upload|linkedin)(\.(cv|esp|ecv|elp|lp|cv-esp|ecl|upload|upload-ok|upload-error))?$";
		var expectedDocumentTitle = '';
		var currentViewTitle = '';
		var headerHTML = '';
		
		var elp = false;
		var ecl = false;
		
		//check view:
		// match[0]: full match (part1) or (part1.part2)
		// match[1]: part1 of (part1.part2) match
		// match[2]: part2 of (part1.part2) match - undefined on part1 of (part1) match
		var match = value.match( regexp );
		
		if ( match === null )
			match = [];
		
		// in case of elp we render span.forth
		switch ( match[2] ){
		
			case ".elp":{
				elp = true;
				ecl = false;
				expectedDocumentTitle = GuiLabel['Navigation.Header.SkillsPassport.ELP'];
				headerHTML = '<span class="forth">European Language Passport</span>';
				break;
			}
			case ".lp":{
				elp = true;
				ecl = false;
				expectedDocumentTitle = GuiLabel['Navigation.Header.SkillsPassport.ELP'];
				headerHTML = '<span class="forth">European Language Passport</span>';
				break;
			}
			case ".cl":{
				ecl = true;
				elp = false;
				expectedDocumentTitle = GuiLabel['Navigation.Header.SkillsPassport.CL'];
				headerHTML = '<span class="first">Curriculum Vitae</span><span class="second">Cover Letter</span>';
				break;
			}
			case ".ecl":{
				ecl = true;
				elp = false;
				expectedDocumentTitle = GuiLabel['Navigation.Header.SkillsPassport.CV']+" - "+GuiLabel['Navigation.Header.SkillsPassport.CL'];
				headerHTML = '<span class="first">Curriculum Vitae</span><span class="second">Cover Letter</span>';
				break;
			}
			case undefined:{
				expectedDocumentTitle = GuiLabel['Navigation.Header.SkillsPassport.CV']+" - "+GuiLabel['Navigation.Header.SkillsPassport.ESP'];
				headerHTML = '<span class="first">Curriculum Vitae</span><span class="second">European Skills Passport</span>';
				break;
			}
			default:{
				expectedDocumentTitle = GuiLabel['Navigation.Header.SkillsPassport.CV']+" - "+GuiLabel['Navigation.Header.SkillsPassport.ESP'];
				headerHTML = '<span class="first">Curriculum Vitae</span><span class="second">European Skills Passport</span>';
				break;
			}
		}

		currentViewTitle = GuiLabel[ view.viewsPerRoute[match[0]]+".Breadcrumb" ];
		headerHTML = headerHTML + '<span class="third">'+currentViewTitle+'</span>';
		
		expectedDocumentTitle = "Europass: " + expectedDocumentTitle + " | " + currentViewTitle;//GuiLabel[ view.viewsPerRoute[value] + ".Breadcrumb"];
		
		var result = view.prepareTitles(value);
		//alert('"'+result.header+'"');

		describe("PageTitleView :: When route(view) is '"+route+"("+value+")'", function() {
			
			it("Appends Document Title: '" + expectedDocumentTitle + "'", function() {
				expect(result.title).toEqual(expectedDocumentTitle);
			});
	
			it("Appends to the H1 header tag: '" + headerHTML +"'", function() {
			
				if(elp == true){
					expect(result.header).not.toContain('<span class="first">Curriculum Vitae</span>');
					expect(result.header).not.toContain('<span class="second">European Skills Passport</span>');
					expect(result.header).toContain('<span class="third">'+(currentViewTitle!=undefined?currentViewTitle:"")+'</span>');
//					expect(result.header).toContain('<span class="forth">European Language Passport</span>');
				}else if(ecl == true){
					expect(result.header).toContain('<span class="first">Curriculum Vitae</span>');
					expect(result.header).toContain('<span class="second">Cover Letter</span>');
					expect(result.header).toContain('<span class="third">'+(currentViewTitle!=undefined?currentViewTitle:"")+'</span>');
					expect(result.header).not.toContain('<span class="forth">European Language Passport</span>');
				}else{
					expect(result.header).toContain('<span class="first">Curriculum Vitae</span>');
					expect(result.header).toContain('<span class="second">European Skills Passport</span>');
					expect(result.header).toContain('<span class="third">'+(currentViewTitle!=undefined?currentViewTitle:"")+'</span>');
					expect(result.header).not.toContain('<span class="forth">European Language Passport</span>');
				}					
			});
		});
	});
});

