/**
 * Checks if the following fields are empty: 
 * 1. Municipality
 * 2. AddressLine
 * 3. PostalCode
 * AND if the Country field is NOT empty.
 * Returns "true" only if the Country field is not empty in the Contact Section, while the rest of the address fields are empty (refers to Work/Education)
 */
define(['jquery', 'handlebars', 'Utils'], function ( $, Handlebars, Utils ) {
	function isOnlyCountry ( context, options ) {
		
		if ( !context || Handlebars.Utils.isEmpty(context) || Utils.isEmptyObject(context) ){
			return options.fn( context );
		}else{
			var emptyMunicipality = !context.Municipality  || Handlebars.Utils.isEmpty(context.Municipality ) || Utils.isEmptyObject(context.Municipality );
			var emptyAddressLine = !context.AddressLine || Handlebars.Utils.isEmpty(context.AddressLine) || Utils.isEmptyObject(context.AddressLine);
			var emptyPostalCode = !context.PostalCode || Handlebars.Utils.isEmpty(context.PostalCode) || Utils.isEmptyObject(context.PostalCode);
			var hasCountry = context.Country|| !Handlebars.Utils.isEmpty(context.Country) || !Utils.isEmptyObject(context.Country);
         
			if ( emptyMunicipality && emptyAddressLine && emptyPostalCode && hasCountry){
				return options.fn( this );
			}else{
				return options.inverse(this);
			}
		}		
	}
	Handlebars.registerHelper( 'isOnlyCountry', isOnlyCountry );
	return isOnlyCountry;
});