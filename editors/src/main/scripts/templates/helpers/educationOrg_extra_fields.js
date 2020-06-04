/**
 * If education organisation extra fields
 * return boolean this or inverse 
 */
define(['handlebars'], function ( Handlebars ) {
	function educationOrg_extra_fields ( field1, field2, field3, options) {
		
		if(field1||field2||field3){
			return options.fn(this);
		}else {
			return options.inverse(this);;
		}

	}
	Handlebars.registerHelper( 'educationOrg_extra_fields', educationOrg_extra_fields );
});