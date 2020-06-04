define(
	[
	 'jquery'
     ,'handlebars' 
     ,'i18n!localization/nls/OrganisationAddressFormat' 
     ,'HelperUtils'
     ], 

function ( $, Handlebars, OrganisationAddress, HelperUtils ) {
  function format_education_organisation_address ( context, options ) {
	  if ( options === undefined || ( options!==undefined && options.fn === undefined ) ){
		return "";
	  }
	  var buffer = "";
	  var addressString="";
	  var format = OrganisationAddress.format;	
	  
	  if ( format === undefined || format === null || format === "" ){
		  format = "s, z m (c)";
	  }
	  if( !context || Handlebars.Utils.isEmpty(context) ) {
			return options.fn(this);
	  }else{
		  // in order to ignore address and postal code
		  // we remove them from a context clone
		  // thus keeping them in the original model
		  var sanitized_context = $.extend({}, context);
		  delete sanitized_context.AddressLine;
		  delete sanitized_context.PostalCode;
		  addressString = HelperUtils.addressFormat(sanitized_context, format);	
	  }			
	   
		
	  buffer += options.fn(new Handlebars.SafeString(addressString));
		
	  return new Handlebars.SafeString(buffer);
  }
  Handlebars.registerHelper( 'format_education_organisation_address', format_education_organisation_address );
  return format_education_organisation_address;
});