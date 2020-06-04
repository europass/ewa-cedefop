/**
 * checks whether the auto sorting button has the descending class
 * used to set the right tooltips on the button
 * TODO could be refactored ... 
 * 
 */
define(['handlebars'], function ( Handlebars ) {
	function set_autosort_tip ( context, options ) {
		
		
		if ( options !== undefined && options.hash !== undefined && $.isPlainObject(options.hash) ){
			
			if (options.hash.object != undefined && options.hash.object != null && options.hash.object != ""){
				var elClass = options.hash.object;
				
				var el = $.find('button.' + elClass);
				
				if (el.length>0){
					
					if ($(el[0]).hasClass('ascending')){
						return options.fn(this);
					}else{
						return options.inverse(this);
					}
					
				}else{
					return options.fn(this);
				}
				
			}
		}
		
		return options.inverse(this);
		
	}
	Handlebars.registerHelper( 'set_autosort_tip', set_autosort_tip );
	return set_autosort_tip;
});