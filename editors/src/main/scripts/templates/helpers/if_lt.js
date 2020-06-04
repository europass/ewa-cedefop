
/**
 * If Less Than
 * if_lt this compare=that
 */
define(['handlebars'], function ( Handlebars ) {
	function if_lt ( context, options ) {
		if (context < options.hash.compare)
			return options.fn(this);
		return options.inverse(this);
	}
	Handlebars.registerHelper( 'if_lt', if_lt );
	return if_lt;
});