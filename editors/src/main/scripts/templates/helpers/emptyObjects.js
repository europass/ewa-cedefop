/**
 * Will check whether all of the given sections are empty
 * @param index: to guide the loop
 * @param options.hash a key-value: key is the index and value is the section from the model 
 */
define(
	['handlebars', 'underscore'], 
	function ( Handlebars, _ ) {
		function emptyObjects ( index, options ) {
			
			var end = Number(index);
			
			var empty = true;
			
			for (var  i=0;i<end;i++ ) {
			
				var section = options.hash[i];
				
				if ( _.isNumber(section) ){
					empty = false;
					break;
				}	
				
				var isEmpty = _.isEmpty( section );
				if ( !isEmpty ){
					empty = false;
					break;
				}	
			}
			
			return ( empty ) ? options.fn( this ) : options.inverse( this ) ;

		}
		Handlebars.registerHelper( 'emptyObjects', emptyObjects );
		return emptyObjects;
	}
);