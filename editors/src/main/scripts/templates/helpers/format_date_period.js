define(
	['handlebars',
	 'Utils',
	 'HelperUtils',
	 'i18n!localization/nls/DocumentLabel',
	 'i18n!localization/nls/DefaultDateFormat'
	],
	function ( Handlebars, Utils, HelperUtils, DocumentLabel, DefaultDateFormat ) {
		function format_date_period ( context, format ) {
			var options = arguments[ arguments.length-1 ];
			var document = options.hash["DocumentType"] !== undefined ? options.hash["DocumentType"] : "ECV";
			
			var buffer = options.fn( HelperUtils.formatPeriod( context, format, false, document ) );
			
			return new Handlebars.SafeString(buffer);
		}
		Handlebars.registerHelper( 'format_date_period', format_date_period );
		return format_date_period;
});