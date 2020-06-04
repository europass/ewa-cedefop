/**
 * 
 */
define(['jquery', 'handlebars', 'Utils'], function ( $, Handlebars, Utils ) {
	function isEmptyObject ( context, options ) {
		if ( Utils.isEmptyObject(context) ){
			return options.fn( context );
		} 
		return options.inverse(this);
	}
	Handlebars.registerHelper( 'isEmptyObject', isEmptyObject );
	return isEmptyObject;
});