define(
[
'jquery', 
'backbone',
'backboneSubModel',
'model/europass/PersonName',
'model/europass/Demographics'
],
function( $, Backbone, SubModel, PersonName, Demographics ){
	
	var model = Backbone.SubModel.extend({
	  
		name: "Identification",
		
		fields: {
			
			PersonName : { type: Backbone.SubModel.MODEL_KEY, model: PersonName },
			Demographics: { type: Backbone.SubModel.MODEL_KEY, model: Demographics }
			
		}
		
	});
	
	return model;
}
);