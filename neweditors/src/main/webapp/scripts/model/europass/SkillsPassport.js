define(
[
'jquery', 
'underscore',
'backbone',
'backboneSubModel',
'model/europass/LearnerInfo'
],
function( $, _, Backbone, SubModel, LearnerInfo ){
	
	var model = Backbone.SubModel.extend({
	  
		name: "SkillsPassport",
		
		fields: {
			LearnerInfo : { type: Backbone.SubModel.MODEL_KEY, model: LearnerInfo }
		},
		
		//==============================================================================
		//@Override
		set: function( attribute, options ){
			var self = this;
			if ( _.isObject(attribute) )
				Backbone.Model.prototype.set.apply( this, [attribute, options] );
			else if ( _.isString(attribute) )
				self.subSet( attribute, options );
		},
		//@Override
		unset: function( attribute, options ){
			var self = this;
			if ( _.isObject(attribute) )
				Backbone.Model.prototype.unset.apply( this, [attribute, options] );
			else if ( _.isString(attribute) )
				self.subUnset( attribute, options );	
		},
		//@Override
		get: function( attr ){
			var self = this;
			return self.subGet( attr );
		}
		
	});
	
	return model;
}
);