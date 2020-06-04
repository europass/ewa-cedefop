define(
[
 'jquery',
 'backboneModelClose',
 'backboneViewClose'
],

function( $ ){
	
	var start = function(){
		
		$("body").append("<h1>Under construction</h1>");
		
	};
	

	return {
		start : start
	};
}

);