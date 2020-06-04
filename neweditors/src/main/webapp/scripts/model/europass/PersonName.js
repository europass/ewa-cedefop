define(
[
'jquery', 
'backbone',
'backboneSubModel'
],
function( $, Backbone, SubModel ){
	
	var model = Backbone.SubModel.extend({
	  
		name: "PersonName"/*,
		
		initialize: function( options ){
			this.on( "change:FirstName", function( model, value, options ){
				console.log("==Capture 'change::FirstName'=="
						+"\nModel is '"+model.name+"'"
						+"\nValue is '"+value+"'");
			} );
		}*/
		
	});
	
	return model;
}
);