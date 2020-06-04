define(
	[
	 'jquery',
	 'handlebars',
	 //'europass/maps/LanguageShortLevelMap',
	 //'europass/maps/IctLevelMap'
	 'HelperUtils'
	], 
	function ( $, Handlebars, HelperUtils ) {
		function showLangShortDescr ( context, mapName, options) {
			
			var mapOk = mapName !== undefined && mapName !== null && mapName !== "";
			
			if ( !context || Handlebars.Utils.isEmpty(context) ){
				return "";
			}else{
				
				if (mapOk){
					var map = HelperUtils.require_map( mapName );
					if (map !== undefined && map !== null){
						var code = context;
						var label="";
						if (code !== undefined && code !== null && code !== ""){
							label = map.get(code);
							return new Handlebars.SafeString(label);
						}else{
							return "";
						};
						
					}else{
						return "";
					};
				}else{
					return "";
				};//end map name exists
				
			};//end context exists
			
		};
	Handlebars.registerHelper( 'showLangShortDescr', showLangShortDescr );
	return showLangShortDescr;
});