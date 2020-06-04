define(
	[
	 'jquery'
	,'handlebars'
	,'HelperUtils'], 
	function ( $, Handlebars, HelperUtils ) {
		function area_with_description ( context, options ) {
			var areaWithDescr = (context != undefined && context != null) ? context : "";
			if ( options != undefined && options != null){
				areaWithDescr = HelperUtils.areaWithDescription(areaWithDescr, options);
			}
			return areaWithDescr;
		}
		Handlebars.registerHelper( 'area_with_description', area_with_description );
		return area_with_description;
	}
);