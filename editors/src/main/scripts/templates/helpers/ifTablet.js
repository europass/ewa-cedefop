/**
 * 
 */
define(['jquery', 'handlebars'], function ( $, Handlebars ) {
	function ifTablet (options) {
		
		var isTablet = (/(iPhone|iPod|iPad)/.test(navigator.userAgent) || (/Android/.test(navigator.userAgent)) || (/BlackBerry/.test(navigator.userAgent)));
		
		if ( isTablet ){
			return options.fn(this);
		} 
		return options.inverse(this);
	}
	Handlebars.registerHelper( 'ifTablet', ifTablet );
	return ifTablet;
});