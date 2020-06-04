define(
[
'jquery', 
'backbone',
'backboneSubModel',
'model/europass/Linguistic'
],
function( $, Backbone, SubModel, Linguistic ){
	
	var model = Backbone.SubModel.extend({
	  
		name: "Skills",
		
		fields: {
			
			Linguistic : { type: Backbone.SubModel.MODEL_KEY, model: Linguistic }
			
		}
		
	});
	
	return model;
}
);