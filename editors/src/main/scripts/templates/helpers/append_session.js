define(
	[
	 'jquery'
	,'handlebars'
	,'europass/http/SessionManagerInstance'], 
	function ( $, Handlebars, Session ) {
		function append_session ( tempuri, options) {
			var buffer = "";
			
			if ( tempuri === undefined || tempuri === null || tempuri === "" ){
				return buffer;
			}
			var thumb =(options == true)?'/thumb': '';
			buffer += ( tempuri + thumb + Session.urlappend() );
			return new Handlebars.SafeString(buffer);
		}
		Handlebars.registerHelper( 'append_session', append_session );
		return append_session;
	}
);