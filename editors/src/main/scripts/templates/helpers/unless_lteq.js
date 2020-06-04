/**
 * Unless Less Than or Equal To
 * unless_lteq this compare=that
 */
define(['handlebars'], function ( Handlebars ) {
	function unless_gteq ( context, options ) {
		if (context <= options.hash.compare)
			return options.unless(this);
		return options.fn(this);
	}
	Handlebars.registerHelper( 'unless_lteq', unless_lteq );
	return unless_lteq;
});