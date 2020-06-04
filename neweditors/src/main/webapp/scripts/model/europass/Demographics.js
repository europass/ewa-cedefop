define(
[
'jquery', 
'backbone',
'backboneSubModel',
'model/europass/Nationality'
],
function( $, Backbone, SubModel, Nationality ){
	
	var model = Backbone.SubModel.extend({
	  
		name: "Demographics",
		
		fields:{
			Nationality: { type: Backbone.SubModel.COLLECTION_KEY, model: Nationality }
		}
		
	});
	
	return model;
}
);