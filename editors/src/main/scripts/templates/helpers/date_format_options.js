/**
 * Searches the printing preferences array based on the name from the options hash.
 * Optionally it accepts a defaultFormat in the options hash.
 * Finally the format is set either to the format found in the prefs, or the default format if specified, or it remains null.
 * The Handlebars function is compiled with the Format as context.
 */
define(
	[
	 'handlebars',
	 'europass/ExperienceDateFormatInstance',
	 'i18n!localization/nls/DateFormatPattern',
	 'i18n!localization/nls/DateFormatOption',
	 'i18n!localization/nls/DateFormat',
	 'i18n!localization/nls/DefaultDateFormat'
	],
	function ( Handlebars, ExperienceDate, DateFormatPatterns, DateFormatOption, DateFormat, DefaultDateFormat ) {
		function date_format_options ( context, options ) {
			var documentPatterns = DateFormatPatterns[ this.DocumentType ];
			var pattern = null;
			var text = null;
			var tmp;
			var buffer="";
			var selected = false;
			
			var isGlobal = false;
			var defaultDate = DefaultDateFormat[ this.DocumentType ];
			if ( this.isGlobal === undefined || this.isGlobal === null || this.isGlobal === "true"  ){
				isGlobal = true;
				defaultDate = ExperienceDate.applyFunction( { f: "get" } , this.DocumentType );
			}
			var isFormatUndefined = ( this.Format === undefined || this.Format === null );
			
			for ( var p in documentPatterns ){

				var showable = documentPatterns[p];
				
				if( showable === true && p !== "" ){
					pattern = p.toString();
					
					if ( isFormatUndefined && pattern === defaultDate ){
						tmp = { "pattern" : defaultDate, "text":  DateFormatOption[defaultDate] , "selected": true };
						buffer += context.fn(tmp);
						continue;
					}
					if ( (pattern === this.Format) || 
							( isGlobal && pattern === defaultDate ) ) {
						selected = true; 
					}
					else selected = false; 
					
					text = DateFormatOption[pattern];
					
					if(text!=null){
						tmp = { "pattern" : pattern, "text":  text , "selected": selected };
						buffer += context.fn(tmp);
					}
				}
			}
			return buffer;

		}
		Handlebars.registerHelper( 'date_format_options', date_format_options );
		return date_format_options;
	}
);
