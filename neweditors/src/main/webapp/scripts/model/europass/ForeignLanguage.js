define(
[
'jquery', 
'backbone',
'backboneSubModel',
'model/europass/CodeLabel',
'model/europass/Certificate'
],
function( $, Backbone, SubModel, CodeLabel, Certificate ){
	
	var model = Backbone.SubModel.extend({
	  
		name: "ForeignLanguage",
		
		fields: {
			Description : { type: Backbone.SubModel.MODEL_KEY, model: CodeLabel },
			Certificate : { type: Backbone.SubModel.COLLECTION_KEY, model: Certificate }
		}
		
	});
	
	return model;
}
);