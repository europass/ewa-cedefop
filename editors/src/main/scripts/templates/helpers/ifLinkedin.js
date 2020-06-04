/**
 * 
 */
define(['jquery', 'handlebars'], function ( $, Handlebars ) {
	function ifLinkedin (context, options) {
		
		var linkedinRegex = /^((https?|ftp|smtp):\/\/)?(www.)?linkedin.com(\w+:{0,1}\w*@)?(\S+)(:([0-9])+)?(\/|\/([\w#!:.?+=&%@!\-\/]))?/;
		
		if ( linkedinRegex.test(context) ){
			return options.fn(this);
		} 
		return options.inverse(this);
	}
	Handlebars.registerHelper( 'ifLinkedin', ifLinkedin );
	return ifLinkedin;
});