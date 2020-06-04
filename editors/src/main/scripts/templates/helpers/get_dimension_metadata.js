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
		
		function get_dimension_metadata ( context , options ) {
			
			var photoWidth = 95;
			var photoHeight = 110;
			
			if( !context || Handlebars.Utils.isEmpty(context) ) {
				return options.inverse( this );
			} 
			
			if(options.hash.type != null && options.hash.type != undefined && options.hash.type == "signature"){
				photoWidth = 300;
				photoHeight = 100;
			}
				
			var key = "dimension";
			
			var dimension = Utils.getMetadata( context, key );
			
			if ( dimension == null ){
				
				return options.inverse( this );
			}
			var parts = dimension.split("x");
			var width = parseInt( parts[0] );
			var height = parseInt( parts[1] );
			
			width = isNaN(width) ? photoWidth : width ;
			height = isNaN(height) ? photoHeight : height;
			
			/**
			 * pgia: EWA-1696
			 * 
			 * In order to upload the photo/signature from the drag&drop functionality we have to adjust the heigh or width accordingly to avoid image stretching
			 */
			
			var json = {};
			
			if(photoWidth / photoHeight > width / height)
				json.width = width > photoWidth ? photoWidth : width ;
			else
				json.height = height > photoHeight ? photoHeight : height;
			
			return options.fn( json );
			
		}
		Handlebars.registerHelper( 'get_dimension_metadata', get_dimension_metadata );
		return get_dimension_metadata;
});