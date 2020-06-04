/**
 * 
 */
define(
	[
	 'jquery',
	 'handlebars',
	 'Utils',
	 'HelperUtils',
	 'europass/http/WindowConfigInstance',
//	 'templates/helpers/get_text'
	], 
	function ( $, Handlebars, Utils, HelperUtils, Config/*, get_text*/ ) {
		
		
		function get_smart_text ( context ) {
//			var textObj = get_text.apply( this, arguments );
			var textObj =  HelperUtils.get_text.apply( this, arguments );
			
			if ( textObj === undefined || textObj === null || textObj === "" ){
				return context;
			}
			
			var text = textObj.toString();
			
			var options = arguments[ arguments.length-1 ];
			if ( options !== undefined && options.hash !== undefined && $.isPlainObject(options.hash) ){
				var repl = options.hash.replacements;
				if ( repl === undefined || repl === null || repl === "" ){
					return new Handlebars.SafeString(text);
				}
				
				var replaces = JSON.parse( repl );
				
				if (  $.isArray(replaces) === false ){
					return new Handlebars.SafeString(text);
				}
				
				$(replaces).each( function(i, replace){
					var key = replace.key;
					var value = replace.value;
					
					var gotValue = Config[ value ];
					text = Utils.replaceKey( text, key, gotValue );			
				});
			}
			
			return new Handlebars.SafeString(text);
		};
	Handlebars.registerHelper( 'get_smart_text', get_smart_text );
	return get_smart_text;
});