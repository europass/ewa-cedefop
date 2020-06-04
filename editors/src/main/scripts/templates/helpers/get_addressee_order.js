define(
	['handlebars',
	 'i18n!localization/nls/CLExtraPreferences'],
	function ( Handlebars , CLExtraPreferences) {
		/* This custom block helper function checks the given order between PersonName and Organisation 
		 * for CoverLetter.Addressee in the CLExtraPreferences bundle and 
		 * @returns boolean, true if order is the default (PersonName Organisation) and false otherwise
		 * */
		function get_addressee_order ( context , options ) {
			
			var DEFAULT_ORDER = "PersonName Organisation",
			 	order = CLExtraPreferences["CoverLetter.Addressee.order"]; //get the order from the CLExtraPreferences bundle
			
			//if return true: PersonName Organization, else return false: Organization Personname
			if( order.indexOf(DEFAULT_ORDER)>=0) {
				return options.fn(this);
			}else{
				return options.inverse(this);
			}
		}
		Handlebars.registerHelper( 'get_addressee_order', get_addressee_order );
		return get_addressee_order;
});