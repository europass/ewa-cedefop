define(['jquery', 'handlebars'], function ( $, Handlebars ) {
	function simple_each_with_index ( context, options ) {
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
			var item = {};
			item["value"] = context[i];
			// stick an index property onto the item, starting with 0
			item[index_name] = i;
			// show the inside of the block
			buffer += fn(item);
		}
		
		// return the finished buffer
		return buffer;
	}
	Handlebars.registerHelper( 'simple_each_with_index', simple_each_with_index );
	return simple_each_with_index;
});
