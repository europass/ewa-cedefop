define(
	['backbone' ],
	function( Backbone ){
		
		Backbone.Model.prototype.close = function(){
			//Call on close to each instance
			_.isFunction(this.onClose) && this.onClose();
		};
	}
);