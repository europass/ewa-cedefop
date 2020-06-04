/**
 * Checks if LearnerInfo.Idenitification is empty. 
 * Checks if the LearnerInfo.Indentification.Photo is null, in case the photo is deleted.
 * Used in the personalInfo compose template to decide whether to show the icon Click To Add
 */
define(['jquery', 'handlebars', 'Utils'], function ( $, Handlebars, Utils ) {
	function isIdentificationWithPhotoEmpty ( context, options ) {
		
		if ( !context || Handlebars.Utils.isEmpty(context) || Utils.isEmptyObject(context) ){
			return options.fn( context );
		}else{
			var emptyPersonName = !context.PersonName || Handlebars.Utils.isEmpty(context.PersonName) || Utils.isEmptyObject(context.PersonName);
			var emptyContactInfo = !context.ContactInfo || Handlebars.Utils.isEmpty(context.ContactInfo) || Utils.isEmptyObject(context.ContactInfo)
			var emptyDemographics = !context.Demographics || Handlebars.Utils.isEmpty(context.Demographics) || Utils.isEmptyObject(context.Demographics)
			var emptyPhoto = context.Photo === undefined || context.Photo === null || Utils.isEmptyObject(context.Photo);
			
			if ( emptyPhoto && emptyPersonName && emptyContactInfo && emptyDemographics){
				return options.fn( context );
			}else{
				return options.inverse(this);
			}
		}		
	}
	Handlebars.registerHelper( 'isIdentificationWithPhotoEmpty', isIdentificationWithPhotoEmpty );
	return isIdentificationWithPhotoEmpty;
});