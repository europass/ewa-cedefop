CKEDITOR.plugins.add( 'epas', {
    icons: 'assistant_menu',
	hidpi: true,
    init: function( editor ) {
	editor.ui.addButton( 'Recommendations', {
            label: 'Open Recommendations',
            command: '',
            toolbar: 'insert'
        });
    }
});
