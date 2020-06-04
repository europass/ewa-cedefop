/**
 * If Equals
 * if_eq this compare=that
 */
define(['handlebars'], function ( Handlebars ) {
	function if_eq ( context, options ) {
		var c = "";
		if (context != undefined)
			c = context.toString();
		if (c == options.hash.compare){
			return options.fn(this);
		}
		return options.inverse(this);
	}
	Handlebars.registerHelper( 'if_eq', if_eq );
	return if_eq;
});