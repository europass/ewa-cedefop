/**
 * If Less Than or Equal To
 * if_lteq this compare=that
 */
define(['handlebars'], function ( Handlebars ) {
	function if_lteq ( context, options ) {
		if (context <= options.hash.compare)
			return options.fn(this);
		return options.inverse(this);
	}
	Handlebars.registerHelper( 'if_lteq', if_lteq );
	return if_lteq;
});