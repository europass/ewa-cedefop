/**
 * Unless Less Than
 * unless_lt this compare=that
 */
define(['handlebars'], function ( Handlebars ) {
	function unless_lt ( context, options ) {
		if (context < options.hash.compare)
			return options.unless(this);
		return options.fn(this);
	}
	Handlebars.registerHelper( 'unless_lt', unless_lt );
	return unless_lt;
});
