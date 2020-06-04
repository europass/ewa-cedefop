({
	mainConfigFile: '${basedir}/src/main/scripts/main.js',
	baseUrl: '${basedir}/src/main/scripts',
	//HOT: When removeCombined is set to true and NodeJS is used, optimization with Node will fail with missing resources
	removeCombined: false, //Set to false when using NodeJS,
	findNestedDependencies: true,

	optimize: "${js.minification}" === "false" ? "none" : "uglify",
	//preserveLicenseComments:false,
	//generateSourceMaps:true,	
	uglify: {
		//useSourceUrl :true,//urls for the source maps files

		//If you are using uglifyjs to minify the code, do not set the uglify option toplevel to true, 
		//or if using the command line do not pass -mt. 
		//That option mangles the global names that shim uses to find exports.
		toplevel: false,
//		ascii_only: true,
//		beautify: true,
		max_line_length: 1000,
		//How to pass uglifyjs defined symbols for AST symbol replacement,
		//see "defines" options for ast_mangle in the uglifys docs.
//		defines: {
//			DEBUG: ['name', 'false']
//		},
		//Custom value supported by r.js but done differently
		//in uglifyjs directly:
		//Skip the processor.ast_mangle() part of the uglify call (r.js 2.0.5+)
		no_mangle: false,
		no_mangle_functions: false,
		no_squeeze: false
	},
	/*
	 * HOT: ekar 11/07/2014: Preserving exclude in main will lead to missing libraries.
	 * However, the libraries are included multiple times, and thus loaded multiple times...
	 */
	modules: [
		{
			name: 'main',
			include: [
				'assembly/importWizard',
				'assembly/exportWizard',
				'assembly/handlebarsHelpers',
				'assembly/ecvCompose',
				'assembly/eclCompose',
				'assembly/elpCompose',
				'assembly/espCompose',
				'assembly/ecvControls',
				'assembly/share',
				'assembly/aside',
				'assembly/ecvForms',
				'assembly/eclForms',
				'assembly/elpForms',
				'assembly/espForms'
			]

		},
		/******** Handlebars Helpers ***************/
		{
			name: 'assembly/handlebarsHelpers'
			, exclude: [
				//jQuery Dependent
				'backbone', 'backbonenested', 'cookie', 'fileupload', 'iframetransport', 'handlebars', 'jcrop', 'jquery', 'jsonjs',
				'ckeditor', 'scrollTo', 'underscore', 'fastclick', 'hbs', 'i18nprecompile', 'i18n', 'typeahead',
				'assembly/importWizard',
				'assembly/exportWizard',
				'assembly/share',
				'assembly/aside',
				'main'
			]
		},
		/******** WYSIWYG Bundle Per Document ********/
		{
			name: 'assembly/ecvCompose'
			, include: ['jqueryui']
			, exclude: [
				//jQuery Dependent
				'backbone', 'backbonenested', 'cookie', 'fileupload', 'iframetransport', 'handlebars', 'jcrop', 'jquery', 'jsonjs',
				'ckeditor', 'scrollTo', 'underscore', 'fastclick', 'hbs', 'i18nprecompile', 'i18n', 'typeahead',
				'assembly/handlebarsHelpers',
				'assembly/importWizard',
				'assembly/exportWizard',
				'assembly/share',
				'assembly/aside',
				'main'
			]
		},
		{
			name: 'assembly/eclCompose'
			, include: ['jqueryui']
			, exclude: [
				//jQuery Dependent
				'backbone', 'backbonenested', 'cookie', 'fileupload', 'iframetransport', 'handlebars', 'jcrop', 'jquery', 'jsonjs',
				'ckeditor', 'scrollTo', 'underscore', 'fastclick', 'hbs', 'i18nprecompile', 'i18n', 'typeahead',
				'assembly/handlebarsHelpers',
				'assembly/importWizard',
				'assembly/exportWizard',
				'assembly/share',
				'assembly/aside',
				'main'
			]
		},
		{
			name: 'assembly/elpCompose'
			, include: ['jqueryui']
			, exclude: [
				//jQuery Dependent
				'backbone', 'backbonenested', 'cookie', 'fileupload', 'iframetransport', 'handlebars', 'jcrop', 'jquery', 'jsonjs',
				'ckeditor', 'scrollTo', 'underscore', 'fastclick', 'hbs', 'i18nprecompile', 'i18n', 'typeahead',
				'assembly/handlebarsHelpers',
				'assembly/importWizard',
				'assembly/exportWizard',
				'assembly/share',
				'assembly/aside',
				'main'
			]
		},
		{
			name: 'assembly/espCompose'
			, include: ['jqueryui']
			, exclude: [
				//jQuery Dependent
				'backbone', 'backbonenested', 'cookie', 'fileupload', 'iframetransport', 'handlebars', 'jcrop', 'jquery', 'jsonjs',
				'ckeditor', 'scrollTo', 'underscore', 'fastclick', 'hbs', 'i18nprecompile', 'i18n', 'typeahead',
				'assembly/handlebarsHelpers',
				'assembly/importWizard',
				'assembly/exportWizard',
				'assembly/share',
				'assembly/aside',
				'main'
			]
		},
		/******** Controls Per Document ********/
		{
			name: 'assembly/ecvControls'
			, exclude: [
				//jQuery Dependent
				'backbone', 'backbonenested', 'cookie', 'fileupload', 'iframetransport', 'handlebars', 'jcrop', 'jquery', 'jsonjs',
				'ckeditor', 'scrollTo', 'underscore', 'fastclick', 'hbs', 'i18nprecompile', 'i18n', 'typeahead', 'jqueryui',
				'assembly/handlebarsHelpers',
				'assembly/importWizard',
				'assembly/exportWizard',
				'assembly/share',
				'assembly/aside',
				'main'
			]
		},
		/******* IMPORT/EXPORT WIZARDS, SHARE, ASIDE **********/
		{
			name: 'assembly/importWizard'
			, exclude: [
				//jQuery Dependent
				'backbone', 'backbonenested', 'cookie', 'fileupload', 'iframetransport', 'handlebars', 'jcrop', 'jquery', 'jsonjs',
				'ckeditor', 'scrollTo', 'underscore', 'fastclick', 'hbs', 'i18nprecompile', 'i18n', 'typeahead', 'jqueryui',
				'assembly/share',
				'assembly/aside',
				'main'
			]
		},
		{
			name: 'assembly/exportWizard'
			, exclude: [
				//jQuery Dependent
				'backbone', 'backbonenested', 'cookie', 'fileupload', 'iframetransport', 'handlebars', 'jcrop', 'jquery', 'jsonjs',
				'ckeditor', 'scrollTo', 'underscore', 'fastclick', 'hbs', 'i18nprecompile', 'i18n', 'typeahead', 'jqueryui',
				'assembly/importWizard',
				'assembly/share',
				'assembly/aside',
				'main'
			]
		},
		{
			name: 'assembly/share'
			, exclude: [
				//jQuery Dependent
				'backbone', 'backbonenested', 'cookie', 'fileupload', 'iframetransport', 'handlebars', 'jcrop', 'jquery', 'jsonjs',
				'ckeditor', 'scrollTo', 'underscore', 'fastclick', 'hbs', 'i18nprecompile', 'i18n', 'typeahead', 'jqueryui',
				'assembly/importWizard',
				'assembly/exportWizard',
				'assembly/aside',
				'main'
			]
		},
		{
			name: 'assembly/aside'
			, exclude: [
				//jQuery Dependent
				'backbone', 'backbonenested', 'cookie', 'fileupload', 'iframetransport', 'handlebars', 'jcrop', 'jquery', 'jsonjs',
				'ckeditor', 'scrollTo', 'underscore', 'fastclick', 'hbs', 'i18nprecompile', 'i18n', 'typeahead', 'jqueryui',
				'assembly/importWizard',
				'assembly/exportWizard',
				'assembly/share',
				'main'
			]
		},
		/******* CRUD Bundle Per Document **********/
		{
			name: 'assembly/ecvForms'
//		,include:['assembly/modals/PersonalInfoFormView']
			, include: ['typeahead', 'cookie', 'jcrop', 'ckeditor']
			, exclude: [
				//jQuery Dependent
				'backbone', 'backbonenested', 'fileupload', 'iframetransport', 'handlebars', 'jquery', 'jsonjs',
				'scrollTo', 'underscore', 'fastclick', 'hbs', 'i18nprecompile', 'i18n', 'jqueryui',
				'assembly/handlebarsHelpers',
				'assembly/importWizard',
				'assembly/exportWizard',
				'assembly/share',
				'assembly/aside',
				'main'
			]
		},
		{
			name: 'assembly/eclForms'
			, include: ['typeahead', 'cookie', 'jcrop', 'ckeditor']
			, exclude: [
				//jQuery Dependent
				'backbone', 'backbonenested', 'fileupload', 'iframetransport', 'handlebars', 'jquery', 'jsonjs',
				'scrollTo', 'underscore', 'fastclick', 'hbs', 'i18nprecompile', 'i18n', 'jqueryui',
				'assembly/handlebarsHelpers',
				'assembly/importWizard',
				'assembly/exportWizard',
				'assembly/share',
				'assembly/aside',
				'main'
			]
		},
		{
			name: 'assembly/elpForms'
			, include: ['typeahead', 'cookie', 'jcrop', 'ckeditor']
			, exclude: [
				//jQuery Dependent
				'backbone', 'backbonenested', 'fileupload', 'iframetransport', 'handlebars', 'jquery', 'jsonjs',
				'scrollTo', 'underscore', 'fastclick', 'hbs', 'i18nprecompile', 'i18n', 'jqueryui',
				'assembly/handlebarsHelpers',
				'assembly/importWizard',
				'assembly/exportWizard',
				'assembly/share',
				'assembly/aside',
				'main'
			]
		},
		{
			name: 'assembly/espForms'
			, include: ['typeahead', 'cookie', 'jcrop', 'ckeditor']
			, exclude: [
				//jQuery Dependent
				'backbone', 'backbonenested', 'fileupload', 'iframetransport', 'handlebars', 'jquery', 'jsonjs',
				'scrollTo', 'underscore', 'fastclick', 'hbs', 'i18nprecompile', 'i18n', 'jqueryui',
				'assembly/handlebarsHelpers',
				'assembly/importWizard',
				'assembly/exportWizard',
				'assembly/share',
				'assembly/aside',
				'main'
			]
		}
	],

	dir: '${basedir}/src/main/webapp/static/ewa/scripts',

	paths: {
		localization: '../webapp/static/ewa/localization',
		libraries: '../webapp/static/ewa/libraries',
		backbone: '../webapp/static/ewa/libraries/backbone/backbone-1.3.3-min',
		backbonenested: '../webapp/static/ewa/libraries/backbone-nested/backbone-nested-2.0.4',
		cookie: '../webapp/static/ewa/libraries/cookie/Cookie',
		fileupload: '../webapp/static/ewa/libraries/fileupload/jquery.fileupload-9.5.2-min',
		"jquery.ui.widget": '../webapp/static/ewa/libraries/fileupload/jquery.ui.widget-1.10.4-amd-min',
		iframetransport: '../webapp/static/ewa/libraries/fileupload/jquery.iframe-transport-9.5.2-min',
		handlebars: '../webapp/static/ewa/libraries/require/plugins/hbs/handlebars-1.3.0-slexaxton-amd-min',
		jcrop: '../webapp/static/ewa/libraries/jcrop/jquery.Jcrop-0.9.12-min',
		jquery: '../webapp/static/ewa/libraries/jquery/jquery-3.2.1-min',
		underscore: '../webapp/static/ewa/libraries/underscore/underscore-1.8.3-min',
		jqueryui: '../webapp/static/ewa/libraries/jquery-ui/jquery-ui.min',

		jsonjs: '../webapp/static/ewa/libraries/jsonjs/json3-3.3.1-min',
		jsonpath: '../webapp/static/ewa/libraries/jsonpath/jsonpath-0.8.0',

		typeahead: '../webapp/static/ewa/libraries/typeahead/typeahead-0.11.1-custom',
		xdate: '../webapp/static/ewa/libraries/xdate/xdate-0.8.2',

		scrollTo: '../webapp/static/ewa/libraries/scrollbar/jquery.scrollTo',
		fastclick: '../webapp/static/ewa/libraries/touch/fastclick-1.0.6',

		domReady: '../webapp/static/ewa/libraries/require/plugins/domReady-2.0.1-min',
		hbs: '../webapp/static/ewa/libraries/require/plugins/hbs/hbs-0.8.1-min',
		i18nprecompile: '../webapp/static/ewa/libraries/require/plugins/hbs/i18nprecompile-0.8.1-min',
		i18n: '../webapp/static/ewa/libraries/require/plugins/i18n-2.0.4-min',
		text: '../webapp/static/ewa/libraries/require/plugins/text-2.0.10-min',

		Utils: 'europass/Utils',
		UtilsForRTE: 'europass/UtilsForRTE',
		HttpUtils: 'europass/http/HttpUtils',
		HelperUtils: 'templates/HelperUtils',
        HelperManageModelUtils: 'templates/HelperManageModelUtils',
		Interactions: 'europass/Interactions',
		ModalFormInteractions: 'europass/ModalFormInteractions',
		BackboneViewAugmented: 'europass/backbone/BackboneViewAugmented',
		BackboneNestedModelAugmented: 'europass/backbone/BackboneNestedModelAugmented',

		dropin: '../webapp/static/ewa/libraries/dropbox/dropins',
		dropboxSDK: '../webapp/static/ewa/libraries/dropbox/Dropbox-sdk.min',

		latinise: '../webapp/static/ewa/libraries/latinise/latinise',

		oneDriveAPI: '../webapp/static/ewa/libraries/onedrive/oneDriveAPI',		
		optout: '../webapp/static/ewa/libraries/do-not-track',
		ckeditor: '../webapp/static/ewa/libraries/ckeditor/ckeditor',
		domPurify: '../webapp/static/ewa/libraries/dompurify/purify.min'

//		,touche: '../webapp/static/ewa/libraries/touch/touche-20140704-TRUNK'		
	},
	shim: {
		'jquery': {exports: '$'},
		'jqueryui': {deps: ['jquery'], exports: '$.ui'},
		'iframetransport': {deps: ['jquery'], exports: 'iframetransport'},
		'fileupload': {deps: ['jquery', 'iframetransport'], exports: 'fileupload'},
		'jcrop': {deps: ['jquery'], exports: '$.Jcrop'},
		'fastclick': {deps: ['jquery'], exports: 'fastclick'},
		'underscore': {exports: '_'},
		'backbone': {deps: ['underscore', 'jquery'], exports: 'Backbone'},
		'BackboneViewAugmented': {deps: ['backbone']},
		'backbonenested': {deps: ['jquery', 'underscore', 'backbone'], exports: 'backbonenested'},
		'BackboneNestedModelAugmented': {deps: ['backbone', 'backbonenested']},
		'hbs': {deps: ['handlebars', 'underscore', 'i18nprecompile', 'jsonjs', 'i18n']},
		'typeahead': {deps: ['jquery', 'underscore'], exports: 'typeahead'},
		'xdate': {exports: 'XDate'},
		'dropin': {exports: 'Dropbox'},
		'dropboxSDK': {exports: 'Dropbox'},
		'latinise': {exports: 'latinise'},
		'oneDriveAPI': {exports: 'oneDriveAPI'},		
		'ckeditor': {exports: 'CKEDITOR'}
//		,'touche'   : {exports: 'Touche'}
	},
	fileExclusionRegExp: /^(libraries|localization)$/
})