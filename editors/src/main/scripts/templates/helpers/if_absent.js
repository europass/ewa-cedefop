/**
 * 
 * Handlebars helper: Contains
 * check if a value is not contained in a string
 */
define(['handlebars'], function (Handlebars) {
	function if_absent( context, options ) {
		var c = "";
		if (context != undefined)
			c = context.toString();
		
		var valuesArray = $.trim(c).split(" ");
		if ($.inArray(options.hash.compare, valuesArray) === -1) {
			return options.fn(this);
		}
		return options.inverse(this);
	}
	Handlebars.registerHelper( 'if_absent', if_absent );
	return if_absent;
});
