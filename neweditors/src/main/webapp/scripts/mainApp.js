
require.config( window.globalConfig )(
	[	
	 	'domReady',
	 	'backboneSubModel',
	 	'controller/bootstrap'
	], 
	function( domReady, Backbone, bootstrap ){
		domReady( function () {
			bootstrap.start();
		});
	}
);

