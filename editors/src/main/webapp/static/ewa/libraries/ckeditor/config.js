/**
 * @license Copyright (c) 2003-2017, CKSource - Frederico Knabben. All rights reserved.
 * For licensing, see LICENSE.md or http://ckeditor.com/license
 */

CKEDITOR.editorConfig = function( config ) {
	// Define changes to default configuration here.
	// For complete reference see:
	// http://docs.ckeditor.com/#!/api/CKEDITOR.config

	// The default plugins included in the basic setup define some buttons that
	// are not needed in a basic editor. They are removed here.

	// Dialog windows are also simplified.
	config.removeDialogTabs = 'link:advanced';
};
