
/**
 * If Greater Than
 * if_gt this compare=that
 */
define(['handlebars'], function ( Handlebars ) {
	function if_gt ( context, options ) {
		var c = "";
		if (context!=undefined)
			c = context.toString();
		if (context > options.hash.compare)
			return options.fn(this);
		return options.inverse(this);
	}
	Handlebars.registerHelper( 'if_gt', if_gt );
	return if_gt;
});