define(['jquery','underscore','handlebars'], 
	function ( $, _, Handlebars ) {
		function obj_concatenate ( ) {
			
			var argumentsLength = arguments.length;
			
			var options = arguments[ argumentsLength-1 ];
			
			var separator = options.hash.separator || " ";
			var end = options.hash.end || "";
			var matchStr = options.hash.match || "…";
			
			var text = "";
			var skipNext = true;
			
			var length = argumentsLength-1;
			for (var i=0; i<length; i++ ) {
				
				var item = arguments[i];
				
				if ( item == null ){ 
					skipNext = skipNext && true;//if the item is null then skip the comma
					continue; 
				}
				// decide when to add the comma
				if (i > 0 && i < length && skipNext===false && text.indexOf(matchStr, 0) == -1 && text.indexOf("...",0) == -1){
					text += separator;
				}else if(i > 0 && i < length && skipNext===false ){
					text = text.replace(matchStr, item);
					text = text.replace("...", item);
					continue;
				}
				text += item;
				skipNext = false;
			}
			if (text === ""){
				return options.inverse(this);
			}
			text += end;
			
			return options.fn( text );
		}
		Handlebars.registerHelper( 'obj_concatenate', obj_concatenate );
		return obj_concatenate;
	}
);