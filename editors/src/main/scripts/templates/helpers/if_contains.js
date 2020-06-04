/**
 * 
 * Handlebars helper: Contains
 * check if a value is contained in a string
 */
define(['handlebars'], function (Handlebars) {
function if_contains(context, options) {
		var c = "";
		if (context != undefined)
			c = context.toString();
		
		var valuesArray = $.trim(c).split(" ");
		if ($.inArray(options.hash.compare, valuesArray) !== -1) {
			return options.fn(this);
		}
		return options.inverse(this);
	}
	Handlebars.registerHelper('if_contains', if_contains);
	return if_contains;
});
