define(['jquery','handlebars'], function ( $, Handlebars ) {
	function each_with_default ( context, options ) {
		var buffer = "";
		var fn = options.fn;
		
		if( !context || Handlebars.Utils.isEmpty(context) ) {
			if ( options.hash.item ){
				var defaultItem = JSON.parse( options.hash.item );
				if ( defaultItem === undefined || defaultItem=== null || $.isEmptyObject(defaultItem) ){
					defaultItem = {};
				}
				defaultItem.index = 0;
				buffer += fn(defaultItem);
			}
		} 
		else {
			for (var i = 0, j = context.length; i < j; i++) {
				var item = context[i];
				
				if ( item === undefined || item=== null || $.isEmptyObject(item) ){
					item = {};
				}

				// stick an index property onto the item, starting with 0
				item.index = i;

				// show the inside of the block
				buffer += fn(item);
			}
		}
		// return the finished buffer
		return buffer;
	}
	Handlebars.registerHelper( 'each_with_default', each_with_default );
	return each_with_default;
});