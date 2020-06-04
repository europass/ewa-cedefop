/**
 * 
 */
define(['handlebars'], function ( Handlebars ) {
	function not_equals (obj1, obj2, options) {
		if(!obj1 || Handlebars.Utils.isEmpty(obj1)) {
			return options.inverse(obj1);
		} 
		if ( obj1 !== obj2 ){
			return options.fn( obj1 );
		}
	}
	Handlebars.registerHelper( 'not_equals', not_equals );
	return not_equals;
});