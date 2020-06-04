({
    // Assume your scripts are in a subdirectory under this path.
    appDir: 'webapp',

    // By default, all modules are located relative to this path.
    baseUrl: 'scripts',

    // Location of the runtime config be read for the build.
    mainConfigFile: 'webapp/scripts/main.js',

    //The directory path to save the output.
    dir: 'app-build',

    // If you do not want uglifyjs optimization.
    optimize: 'uglify2',//'none',

    // Inlines any text! dependencies, to avoid separate requests.
    inlineText: true,

    // Modules to stub out in the optimized file.
    stubModules: ['text', 'html'],

    // Files combined into a build layer will be removed from the output folder.
    removeCombined: true,

    // This option will turn off the auto-preservation.
    preserveLicenseComments: false,

    //List the modules that will be optimized.
    modules: [
        {
            name: "main" // main config file
        }
    ]
});
