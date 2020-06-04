/**
 * Apply a template with a default context item, when the context is empty
 */
define(['handlebars'], function ( Handlebars ) {
	function with_default ( context, options ) {
		var buffer = "";
		var fn = options.fn;
		
		if( !context || Handlebars.Utils.isEmpty(context) ) {
			if ( options.hash.item ){
				var defaultItem = JSON.parse( options.hash.item );
				buffer += fn(defaultItem);
			}
		} 
		else {
			buffer += fn(context);
		}
		// return the finished buffer
		return buffer;
	}
	Handlebars.registerHelper( 'with_default', with_default );
	return with_default;
});