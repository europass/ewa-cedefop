/**
 * 
 */
define(['jquery', 'handlebars'], function ( $, Handlebars ) {
	function isEmptyArray ( array, options ) {
		if ( $.isArray( array ) && array.length == 0 ){
			return options.fn( array );
		} 
		return options.inverse(this);
	}
	Handlebars.registerHelper( 'isEmptyArray', isEmptyArray );
	return isEmptyArray;
});