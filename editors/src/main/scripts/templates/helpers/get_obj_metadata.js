/**
 * 
 */
define(
	[
	 'jquery',
	 'handlebars',
	 'Utils'
	], 
	function ( $, Handlebars, Utils ) {
		
		function get_obj_metadata ( context , options ) {
			if ( options === undefined || ( options!==undefined && options.fn === undefined ) ){
				return "";
			}
			if( !context || Handlebars.Utils.isEmpty(context) ) {
				return false;
			} 
			if ( options.hash !== undefined ){
				
				var key = options.hash.key;
				if ( key===undefined || key === null || key === "" ){ return false; }
				
				var metaInfo = Utils.getMetadata( context, key );
				
				context[key] = metaInfo;
				
				return options.fn( context );
			}
		}
		Handlebars.registerHelper( 'get_obj_metadata', get_obj_metadata );
		return get_obj_metadata;
});