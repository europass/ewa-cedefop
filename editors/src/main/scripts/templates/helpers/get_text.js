/**
 * 
 */
define(
	[
	 'jquery',
	 'handlebars',
	 'Utils',
	 'HelperUtils'
	], 
	function ( $, Handlebars, Utils, HelperUtils ) {
//		var SUBSECTION_TXT = "{{subsection}}";
//		var regexp = new RegExp( /{{subsection}}/ );
		
		function get_text ( context) {
			return HelperUtils.get_text.apply( this, arguments );
//			var initKey = context;
//			var group = null;
//			var options = arguments[ arguments.length-1 ];
//			
//			if ( context === undefined || context === null ){
//				return "no-text";
//			}
//			if ( context === "" ){
//				return "";
//			}
//			if ( options !== undefined && options.hash !== undefined && $.isPlainObject(options.hash) ){
//				group = options.hash.group;
//			}
//			//if there is a subsection use it (e.g. Generic Skill Titles).  Subsections are used in the additional skills section.	
//			var indexedPos = initKey.indexOf(SUBSECTION_TXT);
//			if ( indexedPos > 0 ){
//				var i = 1;
//				var subsection = arguments[i];
//				while ( indexedPos > 0 ){
//					initKey = initKey.replace( regexp, subsection );
//					indexedPos = initKey.indexOf(SUBSECTION_TXT);
//					i++;
//				}
//			}
//			//end subsection handling
//			var groupDefined = ( group !== undefined && group !== null && group!=="");
//			var labelBundle = HelperUtils.requireLabelBundle( groupDefined ? group : null );
//			if ( labelBundle === null ){
//				return "no-labels-bundle-named-"+group;
//			}
//				
//			var key = Utils === undefined ? initKey : Utils.removeIndexTxt(initKey);
//			
//			var label = labelBundle [key];
//			
//			//In case it is not found in thhe "group" search in the list of fallbackGroups
//			if ( label === undefined || label === null ) {
//				//fallback group?
//				var fallbackGroup = options.hash.fallbackGroup;
//				if ( fallbackGroup !== undefined && fallbackGroup !== null && fallbackGroup!==""){
//					var fGroups = fallbackGroup.split(" ");
//					for ( var i=0; i<fGroups.length; i++ ){
//						var fGroupBundle = HelperUtils.requireLabelBundle( fGroups[i] );
//						if ( fGroupBundle === null ){
//							continue;
//						}
//						label = fGroupBundle [key];
//						if ( label !== undefined && label !== null ) {
//							break;
//						}
//					}
//				}
//			}
//			if ( label === undefined || label === null ) {
//				label = (groupDefined ? group : "GuiLabel") +"[" + key + "]";
//			}
//			return new Handlebars.SafeString(label);
		};
	Handlebars.registerHelper( 'get_text', get_text );
	return get_text;
});