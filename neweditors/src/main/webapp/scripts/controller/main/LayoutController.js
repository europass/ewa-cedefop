/**
 * Will render to body the page layout
 */
define([
 'backbone',
 'underscore',
 'html!layout/page',
 'manager/Bundles',
 'i18n!nls/GuiLabel'
],
function( Backbone, _, pageLayout, Bundles, GuiLabel, DocumentLabel ){
	
	var LayoutController = Backbone.View.extend({
		
		template : pageLayout,
		
//		onClose: function(){
//		},
//		initialize: function ( options ) {
//		},
		render: function(){
			var labels = Bundles.load( {"GuiLabel": GuiLabel } );
			
			var layout = this.template( labels );
			
			this.$el.fadeOut("slow", function() { 
				$(this).html(layout).fadeIn("slow");
			});
			return this;
		}
	});
	
	return LayoutController;
});