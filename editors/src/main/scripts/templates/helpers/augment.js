/**
 * Augments the current context with the extra argument according to the name 
 */
define(['handlebars', 'underscore'], function ( Handlebars, _ ) {
	function augment ( context, options ) {
		if( !context || Handlebars.Utils.isEmpty(context) ) {
			options.fn( this );
		} 
		else {
			for ( var name in options.hash ){
				if ( name === undefined || name === null || name === "" ){
					continue;
				}
				var extra = options.hash[ name ];
				context[ name ] = extra;
			}
			return options.fn( context );
		}
	}
	Handlebars.registerHelper( 'augment', augment );
	return augment;
});