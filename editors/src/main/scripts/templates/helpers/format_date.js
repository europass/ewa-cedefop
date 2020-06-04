define(
	['handlebars',
	 'HelperUtils',
	 'i18n!localization/nls/GuiLabel',	 
	],
	function ( Handlebars, HelperUtils, GuiLabel ) {
		function format_date ( context, pref, useDefaultDate ) {
			var buffer = "";
			var options = arguments[ arguments.length-1 ];
			var document = options.hash["DocumentType"] !== undefined ? options.hash["DocumentType"] : "ECV";
			
			if( !context || Handlebars.Utils.isEmpty(context) ) {
				
				if(document === "ELP")
					return GuiLabel["LearnerInfo.Skills.Linguistic.ForeignLanguage.Certificate.IssueDate.Select.Prompt"];
				
				return options.inverse(this);
				
			}
			var format =
				( pref === undefined || pref === null )
				? null
				: ( pref.format === undefined || pref.format === null || pref.format === "" )
					? null
					: pref.format;
			
			var outDate = HelperUtils.formatDate( context, format, useDefaultDate, document);
			if (outDate !== undefined && outDate !== null && outDate !== "" && outDate.length >0 ){
				buffer += options.fn(outDate);	
				return new Handlebars.SafeString(buffer);
			}						
			return options.inverse(this);
		}
		Handlebars.registerHelper( 'format_date', format_date );
		return format_date;
	
});
