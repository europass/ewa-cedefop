/**
 * 
 */
define(['jquery', 'handlebars', 'Utils'], function ( $, Handlebars, Utils ) {
	function isSectionWithAttachmentsEmpty ( context, options ) {
		var isEmptySection = Utils.isEmptyObject( context )?  true :
			(Object.keys(context).length == 1 && ("ReferenceTo" in context) && Utils.isEmptyObject(context["ReferenceTo"])? true 
					: false);
		if ( isEmptySection ){
			return options.fn( context );
		} 
		return options.inverse(this);
	}
	Handlebars.registerHelper( 'isSectionWithAttachmentsEmpty', isSectionWithAttachmentsEmpty );
	return isSectionWithAttachmentsEmpty;
});