/**
 * Unless Equals
 * unless_eq this compare=that
 */
define(['handlebars'], function ( Handlebars ) {
	function unless_eq ( context, options ) {
		var c = "";
		if (context!=undefined)
			c = context.toString();
		if (c == options.hash.compare)
			return options.inverse(this);
		return options.fn(this);
	}
	Handlebars.registerHelper( 'unless_eq', unless_eq );
	return unless_eq;
});