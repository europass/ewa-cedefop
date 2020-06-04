/**
 * Will check whether all of the given sections are empty
 * @param index: to guide the loop
 * @param options.hash a key-value: key is the index and value is the section from the model 
 */
define(
	['handlebars', 'underscore', 'i18n!localization/nls/DocumentLabel'], 
	function ( Handlebars, _, DocumentLabel ) {
		function emptyCLEnclosed ( enclosed, options ) {
			
			// Undefined or Null
			var notDefined = enclosed === undefined || enclosed === null ;
			if ( notDefined )
				return options.fn( this );
			
			// {}
			var empty = _.isEmpty( enclosed );
			if ( empty )
				return options.fn( this );
			
			var emptyAttachments = _.isEmpty( enclosed.InterDocument ) && _.isEmpty( enclosed.ExtraDocument );
			
			var emptyHeading = _.isEmpty( enclosed.Heading );
			var emptyHeadingLabel =  ( !emptyHeading && _.isEmpty( enclosed.Heading.Label ) );
			var emptyHeader = emptyHeading || emptyHeadingLabel;
				
			if ( emptyAttachments && emptyHeader )
				return options.fn( this );
			
			if ( !emptyHeader ){
				var defaultHeader = DocumentLabel["CoverLetter.Enclosed"];
				var hasDefaultHeader = enclosed.Heading.Label === defaultHeader ;
				if ( hasDefaultHeader && emptyAttachments )
					return options.fn( this );
			}
			return options.inverse( this );
		}
		Handlebars.registerHelper( 'emptyCLEnclosed', emptyCLEnclosed );
		return emptyCLEnclosed;
	}
);