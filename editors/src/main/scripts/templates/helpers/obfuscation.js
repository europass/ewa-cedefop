
define(['jquery','underscore','handlebars','Utils', 'HelperUtils' ],
	function ( $, _, Handlebars, Utils, HelperUtils) {
		function obfuscation() {
			
			var options = arguments[ arguments.length-1 ];
			if ( options === undefined || options.hash === undefined){
				return "";
			}
			
			var buffer = "";
			var nlsGroup = options.hash.group;
			var nlsKey = options.hash.nlsKey;
			var elmClass = options.hash.elmClass;
			var titleKey = options.hash.titleKey;
			var titleGroup = options.hash.titleGroup;
			var mail = "";
			var mailparts;
			
			var optionsMailKey = {"hash" : {"group" : nlsGroup}};
			var mailto = HelperUtils.get_text(nlsKey, optionsMailKey);
			var optionsTitleKey = {"hash" : {"group" : titleGroup}};
			var title = HelperUtils.get_text(titleKey, optionsTitleKey);
			var encodeTitle = (title!==null && title!="" && title!= undefined ) ? (title.toString()).replace(/@/g, '&#064;') : "";
			
			if (mailto === undefined || mailto === null || mailto === ""){
				mail = ""; 
			}else{
				mail = mailto.toString();
			}
			
			mailparts = (mail!==null && mail!==undefined && mail!== "") ? mail.split('@') : ["", ""];
			
			var h = '<a title="' + encodeTitle +'" href="mailto:' + mailparts[0] + '&#064;' + mailparts[1] + '"';
			
			if (elmClass!==null && elmClass!==undefined && elmClass!== "") {
				h = h + 'class = "'+elmClass+'"';
			} 
			
			h += '>';
			
			h += (encodeTitle!= null && encodeTitle!= undefined && encodeTitle!="" ) ? encodeTitle : mailparts[0] + '&#064;' + mailparts[1];
			
			h += '</a>';
			
			//safeStr = Handlebars.Utils.escapeExpression(h);
			buffer += options.fn(h);
			return new Handlebars.SafeString(buffer);
		}
		Handlebars.registerHelper( 'obfuscation', obfuscation );
		return obfuscation;
	}
);