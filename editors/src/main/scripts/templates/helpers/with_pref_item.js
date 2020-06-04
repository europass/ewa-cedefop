define(
	['jquery','handlebars'], 
	function ( $, Handlebars ) {
		function with_pref_item ( list, idx, options) {
			
			var item = {};
			item.list_index = 0;
			if ( $.isArray(list)){
				//The index must be the length of the list
				item.list_index = list.length;
				if ( idx>=0  && list[idx] !== undefined && list[idx] !== null){
					item = list[idx];
					item.list_index = idx;
				}
			}
			//Temporary!
			this[ "current_pref_item"  ] = item;
			return options.fn( this );
		}
		Handlebars.registerHelper( 'with_pref_item', with_pref_item );
		return with_pref_item;
	}
);