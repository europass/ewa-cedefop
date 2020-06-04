define(
	['handlebars',
	 'HelperUtils',
	 'i18n!localization/nls/GuiLabel',
	 'underscore'],
	function ( Handlebars , HelperUtils, GuiLabel, _) {
		/* alignSpecialChars  helper transforms localised text to its correct form checking for special characters.
		 * It is related to EWA-1507
		 * @params nlsGroup = the group to get,
		   @params cmsKey   = the key for the text that needs alignment,
		 * @params fallbackKey the key for the text to use if there is no cms key  //TODO change fallback name , to match better semantics
		 * @returns cms text properly formatted
		 * */
		function alignSpecialChars ( options ) {
			
			var options = arguments[ arguments.length-1 ];
			
			if ( options === undefined || options.hash === undefined ){
				return "";
			}
			
			var SPECIAL_CHARS_ALIGNMENT_MAP = {
				"locale":{
					"el":{
						 'Ό': 'Ο',
						 'Ή': 'Η',
						 'Ά': 'Α',
						 'Έ': 'Ε',
						"ΟΎ": 'ΟΥ',
						"ΑΊ": 'ΑΙ',
						"ΕΊ": 'ΕΙ',
						"ΟΊ": 'ΟΙ',
						 "Ύ": 'Υ',
						 "Ώ": 'Ω'
					}
				}
			};
			
			var browserLocale = window.location.pathname.split("/")[2];
			
			var specialCharsMap = SPECIAL_CHARS_ALIGNMENT_MAP["locale"],
				specialChars = {},
				localeExistsInMap = false,
				locale  = browserLocale, 
				nlsGroup = options.hash.group,
				cmsKey  = options.hash.cmsKey,
				fallbackKey = options.hash.fallbackKey;
			
			var group  = {"hash" : {"group" : nlsGroup}},
				fallbackText = HelperUtils.get_text(fallbackKey,group).toString(),
				cmsText      = HelperUtils.get_text(cmsKey,group).toString();
			
			if (_.isEmpty(cmsText)||_.isEmpty(fallbackText)){
				return _.isEmpty(cmsText)? 
						(_.isEmpty(fallbackText)? "" : fallbackText): cmsText;
			}
			
			//check if there is a map available for the current locale
			for (var key in specialCharsMap){
				if(locale.indexOf(key.toLowerCase())>=0){
					specialChars = specialCharsMap[locale];
					localeExistsInMap = true;
					break;
				}
			}
			if (localeExistsInMap){ //if not, return guilabel LearnerInfo.Headline.Title
				for (var key in specialChars){
					cmsText = cmsText.replace(new RegExp(key, 'g'),specialChars[key]);
				}
			}
			return cmsText;
		}
		Handlebars.registerHelper( 'alignSpecialChars', alignSpecialChars );
		return alignSpecialChars;
});