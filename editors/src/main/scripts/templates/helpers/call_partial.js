/**
 * Call a partial and add the hash map to the context
 */
define(
	['handlebars'], 
	function ( Handlebars ) {
		function call_partial ( context, options ) {
			if( !context || Handlebars.Utils.isEmpty(context) ) {
				return options.inverse( context );
			} 
			if ( options.hash !== undefined ){
				for ( var option in options.hash ){
					context[option] = options.hash[option];
				}
				return options.fn( context );
			}
		}
		Handlebars.registerHelper( 'call_partial', call_partial );
		return call_partial;
	}
);