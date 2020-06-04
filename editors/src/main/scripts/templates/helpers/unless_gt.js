/**
 * Unless Greater Than
 * unless_gt this compare=that
 */
define(['handlebars'], function ( Handlebars ) {
	function unless_gt ( context, options ) {
		if (context > options.hash.compare)
			return options.unless(this);
		return options.fn(this);
	}
	Handlebars.registerHelper( 'unless_gt', unless_gt );
	return unless_gt;
});