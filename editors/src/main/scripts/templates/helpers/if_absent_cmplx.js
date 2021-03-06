
define(['handlebars'], function (Handlebars) {
	function if_absent_cmplx( context, part1, part2, options ) {
		var c = "";
		if (context != undefined)
			c = context.toString();
		
		var str = part1 + (part2 != undefined ? part2 : '');
		
		var valuesArray = $.trim(c).split(" ");
		if ($.inArray(str, valuesArray) === -1) {
			return options.fn(this);
		}
		return options.inverse(this);
	}
	Handlebars.registerHelper( 'if_absent_cmplx', if_absent_cmplx );
	return if_absent_cmplx;
});
