/**
 * Returns an object containing the current date values
 */
define(['handlebars', 'xdate', 'europass/http/WindowConfigInstance' ], function ( Handlebars, XDate, WindowConfigInstance) {
	function get_current_date (options) {
		
		var d = new XDate(WindowConfigInstance.serverDateTime, true );
		
		var dateObj = { "Date" : { 
							"Day" : d.getDate(), 
							"Month" : d.getMonth()+1, 
							"Year" : d.getFullYear()
							}
						};
		
		return options.fn( dateObj );
	}
	Handlebars.registerHelper( 'get_current_date', get_current_date );
	return get_current_date;
});