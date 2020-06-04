define(
[
'jquery', 
'backbone',
'backboneSubModel',
'model/europass/ForeignLanguage'
],
function( $, Backbone, SubModel, ForeignLanguage ){
	
	var model = Backbone.SubModel.extend({
	  
		name: "Linguistic",
		
		fields: {
			
			ForeignLanguage : { type: Backbone.SubModel.COLLECTION_KEY, model: ForeignLanguage }
			
		}
		
	});
	
	return model;
}
);