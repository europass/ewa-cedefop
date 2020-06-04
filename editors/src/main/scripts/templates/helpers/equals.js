/**
 * 
 */
define(['handlebars'], function ( Handlebars ) {
	function equals (obj1, obj2, options) {
		if(!obj1 || Handlebars.Utils.isEmpty(obj1)) {
			return options.inverse(obj1);
		} 
		if ( obj1 == obj2 ){
			return options.fn( obj1 );
		}
	}
	Handlebars.registerHelper( 'equals', equals );
	return equals;
});