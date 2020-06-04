/**
 * If Greater Than or Equal To
 * if_gteq this compare=that
 */
define(['handlebars'], function ( Handlebars ) {
	function if_gteq ( context, options ) {
		if (context >= options.hash.compare)
			return options.fn(this);
		return options.inverse(this);
	}
	Handlebars.registerHelper( 'if_gteq', if_gteq );
	return if_gteq;
});