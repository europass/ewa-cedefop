/**
 * 
 */
define(['jquery', 'handlebars'], function ( $, Handlebars ) {
	function headlineTypeCode ( context, options ) {
		//context is the LearnerInfo
		if ( context === undefined || context === null || $.isEmptyObject(context) === true ){
			return options.inverse(this);
		}
		var headline = context.Headline;
		
		if ( headline === undefined || headline === null || $.isEmptyObject(headline) === true ){
			return options.inverse(this);
		}
		var type = headline.Type;
		
		if ( type === undefined || type === null || $.isEmptyObject(type) === true ){
			return options.inverse(this);
		}
		
		var code = type.Code;
		
		if ( code === undefined || code === null || code === "" ){
			return options.inverse(this);
		}
		
		return options.fn( this );

	}
	Handlebars.registerHelper( 'headlineTypeCode', headlineTypeCode );
	return headlineTypeCode;
});