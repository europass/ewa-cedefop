define(['handlebars', 
        'i18n!localization/nls/OrganisationAddressFormat', 
        'HelperUtils'], 

function ( Handlebars, OrganisationAddress, HelperUtils ) {
  function format_organisation_address ( context, options ) {
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
		  addressString = HelperUtils.addressFormat(context, format);	
	  }			
	   
		
	  buffer += options.fn(new Handlebars.SafeString(addressString));
		
	  return new Handlebars.SafeString(buffer);
  }
  Handlebars.registerHelper( 'format_organisation_address', format_organisation_address );
  return format_organisation_address;
});