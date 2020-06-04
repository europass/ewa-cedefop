define(
	[
	 'jquery'
	,'handlebars'
	,'Utils'], 
	function ( $, Handlebars, Utils ) {
		function crop_text ( context, options ) {
			var length = null;
			var crop =null;
			var escape = true; // not parametrized yet
			if ( options.hash !== undefined ){
				length = options.hash.length;
				crop = options.hash.crop;
			}
			var croppedText = Utils.cropText( context, length, crop);
			
			return escape?
				Handlebars.Utils.escapeExpression(croppedText):
				croppedText;
		}
		Handlebars.registerHelper( 'crop_text', crop_text );
		return crop_text;
	}
);