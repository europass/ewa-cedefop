define(
	['jquery','handlebars'], 
	function ( $, Handlebars ) {
		function with_attachment ( attachments, idref, options) {
			if ( options === undefined || ( options!==undefined && options.fn === undefined ) ){
				return "";
			}
			if ( idref === undefined || idref === null || idref === "" ){
				return options.inverse(this);
			}
			var item = {};
			
			if ( $.isArray(attachments)){
				
				for ( var i = 0; i<attachments.length; i++ ){
					var attachment = attachments[i];
					if ( attachment === undefined || attachment === null ){ return false; }
					var id = attachment.Id;
					if ( id!== undefined && id!== null && id===idref ){
						item = attachment;
						item.att_index = i;
					}
				};
			}
			return options.fn( item );
		}
		Handlebars.registerHelper( 'with_attachment', with_attachment );
		return with_attachment;
	}
);