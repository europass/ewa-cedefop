define(
	['jquery','handlebars'], 
	function ( $, Handlebars ) {
		function with_list_item ( list, idx, options) {
			
			var item = {};
			item.list_index = 0;
			if ( $.isArray(list)){
				//The index must be the length of the list
				item.list_index = list.length;
				if ( idx>=0  && list[idx] !== undefined && list[idx] !== null ){
					item = list[idx];
					item.list_index = idx;
				}
			}
			return options.fn( item );
		}
		Handlebars.registerHelper( 'with_list_item', with_list_item );
		return with_list_item;
	}
);