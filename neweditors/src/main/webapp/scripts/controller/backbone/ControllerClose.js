define(
[
 'backbone',
 'underscore'
],
function( Backbone, _ ){
	/**
	 * BEWARE OF ZOMBIES!!!!!!
	 * Proper clean up of Views
	 * http://lostechies.com/derickbailey/2011/09/15/zombies-run-managing-page-transitions-in-backbone-apps/
	 */
	Backbone.View.prototype.close = function(){
		
		// dispose any sub-views
        _.each(this.views || [], function(view) {
            view.close();
        });
        
		//Un-bind any model and collection events that our view is bound to.
		//To do this, though, we cannot use a generic close method on our base view.
		_.isFunction(this.onClose) && this.onClose();
		
		this.off();
		
		this.stopListening();
		
		// finally, call Backbone's default remove method to
        // remove the view from the DOM
        Backbone.View.prototype.remove.call(this);
		//OR...this.remove();
		
	};
}
);