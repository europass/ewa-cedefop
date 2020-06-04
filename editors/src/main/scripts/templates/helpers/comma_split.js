define(['jquery','underscore','handlebars'], 
	function ( $, _, Handlebars ) {
		function comma_split ( listStr, options ) {
			if ( !_.isString( listStr ) ){
				return listStr;
			}
			var list = listStr.split(", ");
			if ( !_.isArray( list ) ){
				return listStr;
			}
			var fn = options.fn;
			var buffer = "";
			var skipped = false;
			for ( var i = 0; i<list.length; i++ ){
				var item = list[i];
				if ( item === null ){
					skipped = true;
					continue;
				}
				buffer = buffer + ( i>0 && skipped==false ? ", " : "" ) + fn ( item );
			}
			return new Handlebars.SafeString(buffer);
		}
		Handlebars.registerHelper( 'comma_split', comma_split );
		return comma_split;
	}
);