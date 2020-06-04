define(
[
'jquery', 
'backbone',
'backboneSubModel',
],
function( $, Backbone, SubModel ){
	
	var model = Backbone.SubModel.extend({
	  
		name: "Certificate"
		
	});
	
	return model;
}
);