/**
 * Unless Greater Than or Equal To
 * unless_gteq this compare=that
 */
define(['handlebars'], function ( Handlebars ) {
	function unless_gteq ( context, options ) {
		if (context >= options.hash.compare)
			return options.unless(this);
		return options.fn(this);
	}
	Handlebars.registerHelper( 'unless_gteq', unless_gteq );
	return unless_gteq;
});