var ewaLocale = null;
try {
	ewaLocale = window.localStorage.getItem("europass.ewa.locale");
} catch ( err ){}
if ( ewaLocale === undefined || ewaLocale === null || ewaLocale === "" || ewaLocale === "null" ){
	ewaLocale = window.sessionLocale;
}
if ( ewaLocale === undefined || ewaLocale === null || ewaLocale === "" || ewaLocale === "null" ){
	ewaLocale = "en";
}
try {
	window.localStorage.removeItem("europass.ewa.locale");
} catch ( err ){}


window.baseResourcesUrl = "/neweditors";

window.globalConfig = {
	baseUrl: window.baseResourcesUrl+"/scripts",
	locale: ewaLocale,
	//The number of seconds to wait before giving up on loading a script. 
	//Setting it to 0 disables the timeout. The default is 7 seconds.
	waitSeconds: 20,
	urlArgs: 'version=12345',
	
	stache :{
		path: 'templates/',
		extension : '.html'
	},

	
	paths: {
		'jquery': 'lib/jquery/jquery-2.0.3.min',
		'underscore': 'lib/underscore/underscore-min',
		'backbone': 'lib/backbone/backbone-1.0.0',
		'backboneSubModel' : 'model/backbone/SubModel',
		'backboneModelClose' : 'model/backbone/ModelClose',
		'backboneViewClose' : 'controller/backbone/ControllerClose',
		'i18n': 'lib/require/i18n',
		'text': 'lib/require/text',
		'domReady': 'lib/require/domReady',
		'mustache': 'lib/mustache/mustache',
		'html': 'lib/mustache/mustache-require-plugin',
		'jasmine' : 'test/lib/jasmine',
		'jasmine-html' : 'test/lib/jasmine-html'
	},
	shim: {
		'jquery' :{exports: '$'},
		'underscore' :{exports: '_'},
		'backbone': {deps: [ 'underscore' , 'jquery' ], exports: 'Backbone'},
		'backboneSubModel' : { deps : ['backbone'], exports: 'Backbone.SubModel' },
		'backboneModelClose' : { deps : ['backbone'], exports: 'Backbone.SubModel' },
		'backboneViewClose' : { deps : ['backbone'], exports: 'Backbone.View' },
		'mustache' :{exports: 'Mustache'},
		'html' :{ deps: ['mustache'], exports: 'html'},
		'jasmine' : {exports: 'jasmine'},
		'jasmine-html' : {deps: [ 'jasmine' ],exports: 'jasmine'}
	}
};
window.reloadScripts = {};


