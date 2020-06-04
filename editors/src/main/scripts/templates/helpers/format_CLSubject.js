/** EWA 959: [CL] Add "Subject:" label in all languages
 * format_CLSubject 
 * @param  SkillsPassport.CoverLetter.Letter.SubjectLine
 * @returns "Subject:" in bold if it locates it as the first word in the subjectLine
 */
define(
	['handlebars', 'underscore', 'i18n!localization/nls/GuiLabel', ],
	function ( Handlebars, _, GuiLabel) {
		function format_CLSubject ( context, options ) {
			
			/** if there is no context return false
			 */
			if(!context)
				return options.inverse(this);
			
			var subjectText = context;
			var result = Handlebars.Utils.escapeExpression(subjectText);
			
			var subject = GuiLabel["CoverLetter.Letter.SubjectLine"];	//get the localizable Subject label
				
			if ( _.isEmpty(subject) )
				return options.fn( this );
			
			if ( subjectText.trim().indexOf(subject+":") == 0) 
				result = '<b>' + subject + ':</b>' + subjectText.substring(subject.length+1);
			
			return new Handlebars.SafeString(result);
		}
		Handlebars.registerHelper( 'format_CLSubject', format_CLSubject );
		return format_CLSubject;
	}
);