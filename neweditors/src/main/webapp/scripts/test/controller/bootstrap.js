define(
[
'backboneModelClose',
'backboneViewClose',
'jasmine',
'jasmine-html',
'test/spec/mustache/TextsTest',
'test/spec/controller/PageLayoutTest',
'test/spec/submodel/SetGetTest',
'test/spec/submodel/EventsTest'
],

function( modelClose, viewClose, jasmine, jasmineHtml, TextsTest, PageLayoutTest, SetGetTest, EventsTest ){
	
	var start = function(){
		
		var htmlReporter = new jasmine.HtmlReporter();
	    jasmine.getEnv().addReporter(htmlReporter);
	    jasmine.getEnv().specFilter = function(spec){
	        return htmlReporter.specFilter(spec);
	    };
	    jasmine.getEnv().execute();
		
	};
	

	return {
		start : start
	};
}

);