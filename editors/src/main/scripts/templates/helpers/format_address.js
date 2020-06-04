define(['jquery', 'handlebars', 'underscore', 'europass/maps/AddressFormatMap', 'HelperUtils'], 
function ( $, Handlebars, _, AddressFormatMap, HelperUtils ){

	function format_address ( context, options ) {
		var address = $.extend( {}, context );
		var buffer = "";
		var fmtObj=null;
		var addressString ="";
		var format = null;
		
		if( !context || Handlebars.Utils.isEmpty(context) ) {
			return options.fn(this);
		} else {
			//Check if we have Country info in the Address
			if (address.Country !== undefined && address.Country !== null && address.Country!== ""){
				if (address.Country.Code!== undefined && address.Country.Code!== null && address.Country.Code!== ""){
					fmtObj = AddressFormatMap.get(address.Country.Code);
					if (fmtObj !== undefined && fmtObj !== null) {
						format = fmtObj.format;
					}
				}
			}
			if ( format === null ){
				var defaultFormatObj = AddressFormatMap.get("default");//get default format
				format = defaultFormatObj.format;
			}
			if ( format === undefined || format === null || format === "" ){
				format = "s, z m (c)";
			}
			
			var mzPartial = false;
			//Check of partial=m-z exists
			if ( !_.isEmpty(options.hash) ){
				var partial = options.hash.partial;
				if ( _.isString(partial) ){
					switch (partial){
						case "m-z":{
							mzPartial = true;
							delete address.Country;
							delete address.AddressLine;
							delete address.AddressLine2;
							
							address.transientCountry = context.Country;
							
							break;
						}
					}
				}
			}
			
			addressString = HelperUtils.addressFormat(address, format);
			
			var lastCharComma = ( addressString.lastIndexOf(", ") === addressString.length - 2) || (addressString.lastIndexOf(",") === addressString.length - 1);
			
			if ( mzPartial || lastCharComma)
				// remove all commas
				addressString = addressString.replace(/,/g, "");
			
		}
		buffer += options.fn(new Handlebars.SafeString(addressString));
		return new Handlebars.SafeString(buffer);
	}
	Handlebars.registerHelper( 'format_address', format_address );
	return format_address;
});