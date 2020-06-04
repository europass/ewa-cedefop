define(
	['jquery','handlebars'], 
	function ( $, Handlebars ) {
		function with_generic_skill ( context, subsection, options ) {
			if ( options === undefined || ( options!==undefined && options.fn === undefined ) ){
				return "";
			}
			if( Handlebars.Utils.isEmpty(context) ) {
				return options.fn(this);
			}
			if (subsection === undefined || subsection===null || subsection===""){
				return options.fn(this);
			}
			
			var subcontent = context[subsection] ;
			
			if (subcontent === undefined || subcontent===null || subcontent===""){
				return options.fn(this);
			}
			
			return options.fn( subcontent );
		}
		Handlebars.registerHelper( 'with_generic_skill', with_generic_skill );
		return with_generic_skill;
	}
);