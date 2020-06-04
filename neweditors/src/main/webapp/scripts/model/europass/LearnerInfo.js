define(
[
'jquery', 
'backbone',
'backboneSubModel',
'model/europass/Identification',
'model/europass/Skills'
],
function( $, Backbone, SubModel, Identification, Skills ){
	
	var model = Backbone.SubModel.extend({
	  
		name: "LearnerInfo",
		
		fields: {
			
			Identification : { type: Backbone.SubModel.MODEL_KEY, model: Identification },
			Skills : { type: Backbone.SubModel.MODEL_KEY, model: Skills }
			
		}
		
	});
	
	return model;
}
);