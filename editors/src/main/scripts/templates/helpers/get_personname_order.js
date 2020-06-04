define(
	['handlebars',
	 'i18n!localization/nls/GuiLabel'],
	function ( Handlebars , GuiLabel) {
		function get_personname_order ( context, pref ) {
			var buffer = "";
			var options = arguments[ arguments.length-1 ];
			
			if( !context || Handlebars.Utils.isEmpty(context) ) {
				return options.inverse(this);
			}
			var order =
				( pref === undefined || pref === null )
				? "Title FirstName Surname"
				: ( pref.order === undefined || pref.order === null || pref.order === "" )
					? "Title FirstName Surname"
					: pref.order;
			
			var rx = /^Title(?:.*)/;
			var isTitleFirst = rx.test(order);
			rx = /(?:.*)FirstName Surname(?:.*)/;
			var isSurnameAfterFirstName = rx.test(order);
			var firstname = context.FirstName;
			var surname = context.Surname;
			var title = ( context.Title !== undefined && context.Title !== null ) ? context.Title.Label : null;
			
			if(isTitleFirst && isSurnameAfterFirstName){
				buffer += title !== null && title !== undefined ? "<span>"+title+"</span>" : "<span class=\"default-value\">"+GuiLabel["CoverLetter.Addressee.PersonName.Title"]+"</span>";
				buffer += firstname !== null && firstname !== undefined ? "<span class=\"FirstName\"> "+firstname+" </span>" : "<span class=\"FirstName default-value\"> "+GuiLabel["CoverLetter.Addressee.PersonName.FirstName"]+" </span>";
				buffer += surname !== null && surname !== undefined ? "<span class=\"Surname\"> "+surname+" </span>" : "<span class=\"Surname default-value\"> "+GuiLabel["CoverLetter.Addressee.PersonName.Surname"]+" </span>";
			}else if(!isTitleFirst && isSurnameAfterFirstName){
				buffer += firstname !== null && firstname !== undefined ? "<span class=\"FirstName\">"+firstname+"</span>" : "<span class=\"FirstName default-value\">"+GuiLabel["CoverLetter.Addressee.PersonName.FirstName"]+"</span>";
				buffer += surname !== null && surname !== undefined? "<span class=\"Surname\"> "+surname+" </span>" : "<span class=\"Surname default-value\"> "+GuiLabel["CoverLetter.Addressee.PersonName.Surname"]+" </span>";
				buffer += title !== null && title !== undefined ? "<span> "+title+" </span>" : "<span class=\"default-value\"> "+GuiLabel["CoverLetter.Addressee.PersonName.Title"]+" </span>";
			}else if(isTitleFirst && !isSurnameAfterFirstName){
				buffer += title !== null && title !== undefined ? "<span>"+title+"</span>" : "<span class=\"default-value\">"+GuiLabel["CoverLetter.Addressee.PersonName.Title"]+"</span>";
				buffer += surname !== null && surname !== undefined ? "<span class=\"Surname\"> "+surname+" </span>" : "<span class=\"Surname default-value\"> "+GuiLabel["CoverLetter.Addressee.PersonName.Surname"]+" </span>";
				buffer += firstname !== null && firstname !== undefined ? "<span class=\"FirstName\"> "+firstname+" </span>" : "<span class=\"FirstName default-value\"> "+GuiLabel["CoverLetter.Addressee.PersonName.FirstName"]+" </span>";
			}else{
				buffer += surname !== null && surname !== undefined ? "<span class=\"Surname\"> "+surname+" </span>" : "<span class=\"Surname default-value\">"+GuiLabel["CoverLetter.Addressee.PersonName.Surname"]+"</span>";
				buffer += firstname !== null && firstname !== undefined ? "<span class=\"FirstName\"> "+firstname+" </span>" : "<span class=\"FirstName default-value\"> "+GuiLabel["CoverLetter.Addressee.PersonName.FirstName"]+" </span>";
				buffer += title !== null && title !== undefined ? "<span> "+title+" </span>" : "<span class=\"default-value\"> "+GuiLabel["CoverLetter.Addressee.PersonName.Title"]+" </span>";
			}
			
			return new Handlebars.SafeString(buffer);

		}
		Handlebars.registerHelper( 'get_personname_order', get_personname_order );
		return get_personname_order;
	
});