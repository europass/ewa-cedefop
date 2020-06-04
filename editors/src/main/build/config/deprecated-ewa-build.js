({
	baseUrl: '${basedir}/src/main/scripts',
	mainConfigFile: '${basedir}/src/main/scripts/main.js',
	//paths has to either be manually duplicated in the build config, or use
	//shim has to either be manually duplicated in the build config, or use
	//mainConfigFile to point to the file that has that shim config
	
	locale: 'en',
	dir: '${basedir}/src/main/webapp/static/ewa/scripts',
	modules: [
		//{ name : 'unsupportedBrowsers/main' },
		{ name : 'main' }
	],
	
	keepBuildDir: true,
	optimize: '${js.minification}' === 'false' ? 'none' : 'uglify2',
	uglify: {
		//If you are using uglifyjs to minify the code, do not set the uglify option toplevel to true, 
		//or if using the command line do not pass -mt. 
		//That option mangles the global names that shim uses to find exports.
		toplevel: false,
		//ascii: true,
		//beautify: true,
		max_line_length: 1000,
//		defines: {
//			DEBUG: ['name', 'false']
//		},
//		reserved_names : 
		no_mangle: false,
		no_mangle_functions: false,
		no_squeeze: false//,
//		no-copyright: true
	},
	//Introduced in 2.1.2: If using "dir" for an output directory, normally the
	//optimize setting is used to optimize the build layers (the "modules"
	//section of the config) and any other JS file in the directory. However, if
	//the non-build layer JS files will not be loaded after a build, you can
	//skip the optimization of those files, to speed up builds. Set this value
	//to true if you want to skip optimizing those other non-build layer JS
	//files.
	skipDirOptimize: false,
	generateSourceMaps: false,//false,
	normalizeDirDefines: 'skip',
	optimizeCss: 'none',
	inlineText: true,
	useStrict: false,
	skipPragmas: false,
	skipModuleInsertion: false,
	optimizeAllPluginResources: true,
	findNestedDependencies: true,//false
	removeCombined: false,
	//Exclusion, means that imports through define will not work
	fileExclusionRegExp:  /^\./, 
	preserveLicenseComments: false,//true if generateSourceMaps is false
	logLevel: 0,
	cjsTranslate: true,
	useSourceUrl: true
})