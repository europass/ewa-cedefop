/**
 * Loop iteration with adding an index in the context.
 */
define(['jquery', 'handlebars'], function ( $, Handlebars ) {
	function each_with_index ( context, options ) {
		var buffer = "";
		var fn = options.fn;
		
		var index_name = "index";
		
		if ( options.hash.index_name ){
			index_name = options.hash.index_name;
		}
		if( !context || Handlebars.Utils.isEmpty(context) ) {
			return;
		}
		
		for (var i = 0, j = context.length; i < j; i++) {
			var item = context[i]; 
			
			if ( item === undefined || item=== null || $.isEmptyObject(item) ){
				item = {};
			}
			// stick an index property onto the item, starting with 0
			item[index_name] = i;

			// show the inside of the block
			buffer += fn(item);
		}
		
		// return the finished buffer
		return buffer;
	}
	Handlebars.registerHelper( 'each_with_index', each_with_index );
	return each_with_index;
});
