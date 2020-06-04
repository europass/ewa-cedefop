define(
[
 'handlebars',
 'underscore',
 'europass/GlobalDocumentInstance',
 'i18n!localization/nls/EditorHelp',
 'i18n!localization/nls/GuiLabel'],
 
function ( Handlebars, _, GlobalDocument, EditorHelp, GuiLabel ) {
	
	function loop_europass_documents ( context, extra, options ) {
		
		var documents = GlobalDocument.europassDocuments();
		
		if ( _.isArray( documents ) ){
			var buffer = "";
			
			var fn = options.fn;
			
			var without = undefined;
			if ( _.isString(extra) ){
				without = extra.split(" ");
			}
			
			if(!_.isUndefined(without)){

				var defaultWithout = options.hash.without;
				if(_.isString(defaultWithout) && defaultWithout.length > 0)
					without[without.length] = defaultWithout;
				
				for ( var j=0; j<without.length; j++ ){
					documents = _.without( documents, without[j] );
				}
			}
			
			for ( var i = 0; i < documents.length; i++ ){
				var doc = documents[i];
				
				if ( doc === null )
					continue;
				
				var tmp = { 
					"loopId" : i, 
					"loopValue" : doc
				};
				//Help
				if ( !_.isEmpty(options.hash) ){
					var optionsHelpKeyPrefix = options.hash.helpKeyPrefix;
					if ( _.isString(optionsHelpKeyPrefix) ) {
						var helpKeyPrefix = optionsHelpKeyPrefix;
						tmp.help = EditorHelp[helpKeyPrefix+doc] ;
					}
				}
				
				tmp.loopLabel = GuiLabel["CoverLetter.Enclose.Document."+doc];
				
				if ( _.isArray( context ) && context.length > 0 ){
					var match = _.find( context, function(liveRef){ 
						if ( _.isObject(liveRef) && !_.isEmpty( liveRef ) ){
							return doc === liveRef.ref;
						}
						return false;
					});
					if ( !_.isUndefined( match ) ){
						tmp.checked = "checked";
					}
				}
				
				buffer = buffer + fn( tmp );
			}
			return new Handlebars.SafeString(buffer);
			
		} else {
			return "";
		}
	}
	Handlebars.registerHelper( 'loop_europass_documents', loop_europass_documents );
	return loop_europass_documents;
}
);