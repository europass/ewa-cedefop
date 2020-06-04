/**
 * If employer extra fields
 * return boolean this or inverse 
 */
define(['handlebars'], function ( Handlebars ) {
	function employer_extra_fields ( field1, field2, field3, field4 ,options) {
		
	  if(field1||field2||field3||field4){
		  return options.fn(this);
	  }
      else {
        return options.inverse(this);;
      }

	}
	Handlebars.registerHelper( 'employer_extra_fields', employer_extra_fields );
});