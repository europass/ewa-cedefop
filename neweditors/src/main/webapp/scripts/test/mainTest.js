

require.config( window.globalConfig )(
	[	
	 	'backboneSubModel',
	 	'test/controller/bootstrap'
	], 
	function( Backbone, bootstrap ){
		
		bootstrap.start();
		
	}
);

