define(['jquery','underscore','handlebars'], 
	function ( $, _, Handlebars ) {
		function commalist ( list, prefs, options ) {
			
			if ( !_.isArray( list ) ){
				return list;
			}
			// Decide what would be the label key
			var labelKey = "Label";
			var hasHash = !_.isEmpty(options.hash);
			if ( hasHash ){
				var key = options.hash.labelKey;
				if ( _.isString(key) ){
					labelKey = key;
				}
			}
			var hasPrefs = _.isArray( prefs ) && prefs.length > 0 ;
			
			var length = list.length;
			
			var commaList = "";
			var skipNext = true;
			
			for (var i=0; i<length; i++ ) {
				
				var item = list[i];
				
				if ( item === undefined || item === null ){ 
					skipNext = skipNext && true;//if the item is null then skip the comma
					continue; 
				}
				
				var label = item[labelKey];
				if ( label === undefined ){
					var description = item.Description;
					if ( description !== undefined && description !== null )
						label = description.Label;
					if ( label === undefined )
						label = description;
				}
				
				if ( label === undefined || label === null || label === "" ){ 
					skipNext = skipNext && true;//if the item is null then skip the comma
					continue; 
				}
				// decide when to add the comma
				if (i > 0 && i < length && skipNext===false ) {
					commaList += ", ";
				}
				commaList += label;
				skipNext = false;
								
			}
			if (commaList === ""){
				return options.inverse(this);
			}
			return options.fn( commaList );
		}
		Handlebars.registerHelper( 'commalist', commalist );
		return commalist;
	}
);
