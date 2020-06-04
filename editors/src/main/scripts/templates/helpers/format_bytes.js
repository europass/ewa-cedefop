define(
	['handlebars','HttpUtils' ],
	function ( Handlebars, HttpUtils) {
		function format_bytes ( context, options ) {
			
			if( !context || Handlebars.Utils.isEmpty(context) ) {
				return "";
			}
			return HttpUtils.formatBytes( context );
		}
		Handlebars.registerHelper( 'format_bytes', format_bytes );
		return format_bytes;
	}
);