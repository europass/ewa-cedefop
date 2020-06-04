define(
	[
	 'handlebars',
	 'HelperUtils'
	 ],
	function ( Handlebars, HelperUtils ) {
		function loopForMapWithShortKeyLabel ( context, mapName, mapShortKey, options ) {
			
			var map = HelperUtils.require_map( mapName );//required
			var mapShortKey = HelperUtils.require_map( ( mapShortKey !== undefined && mapShortKey !== null )? mapShortKey : "LanguageShortLevelMap" );//required
			var buffer="";
			var fn= options.fn;
			var hasEmptyOption = options.hash.emptyOption;
			var ifEmptySetSelected= options.hash.ifEmptySetSelected; //1
			var tmp = {};
			var shortLabel = ( options.hash.shortLabel !== undefined && options.hash.shortLabel!== null) ? options.hash.shortLabel : false;
			
			/* The option below ('showCode') controls whether the displayed text of the option element
			 * needs to also display the key of the map.
			 */
			var showCode = options.hash.showCode;
			var spanCode = "<span class=\"Code\">";
			var spanLabel = "<span class=\"Label\">";			
			var spanEnd = "</span>";
			var spanShortKey = "<span class=\"short-key-label\">";
			var keyLabelSeparator = (shortLabel? "" : " - ");
			
			
			var defaultvalue = ( options === undefined || options === null ) 
								? null : ( options.hash === undefined || options.hash === null )
										? null : ( options.hash.defaultvalue === undefined || options.hash.defaultvalue === null )
											? null : options.hash.defaultvalue;//this can be used as a placeholder for example -- enter a value --, but it will not be displayed in soft gray (different css)
			
			var selValue = "";
			
			if (map!==undefined && map!== null && mapShortKey!==undefined && mapShortKey!= null){
				
				var values = map.toArray();
				var valuesShortKeys = mapShortKey.toArray();
				
				if (values!= null && values.length >0){
					//We need to display an empty option
					
					if ( hasEmptyOption === true && values[0]["key"] !== ""){
						if (defaultvalue!== undefined && defaultvalue !== null && defaultvalue !== ""){
							//add default value as an option to the select
							var options2 = {"hash" : {"key" : defaultvalue}};
							var defaultText = HelperUtils.get_text(NaN, options2);
							values.splice(0,0,{"key" : "", "value" : defaultText.toString()});//adds defaultText for empty option, to array of values 
						} else {
							values.splice(0,0,{"key" : "", "value":""});//adds empty option to array of values
						}
					}
					if ( !context || Handlebars.Utils.isEmpty(context) ){
						context = {};//selValue will remain ""
						if ( ifEmptySetSelected !== undefined && ifEmptySetSelected !== null && ifEmptySetSelected !== ""){
							//then set the selected value to the one specified
							//select from the values list, the indexed value
							selValue = values[ifEmptySetSelected-1].key;
						}
					}else{
						if (context.Code !== undefined && context.Code !== null)
							selValue = context.Code;//in case the context contains Code and Value objects
						else
							selValue = context;//in case the context is the code, i.e. "Listening" : "A1"
					}
					
					for (var i=0; i <= values.length-1; i++){
						var valueC = values[i].key;
						
						var valueValue = values[i].value;
						var valueShort = "";
						var valueFull = "";
//						var valueShortLabel = "";
						
						if (hasEmptyOption === true){
							if (i>0){
								valueShort = spanShortKey + valuesShortKeys[i-1].value + spanEnd + keyLabelSeparator;
//								valueShortLabel = valuesShortKeys[i-1].value;
							}
						}else{
							valueShort = spanShortKey + valuesShortKeys[i].value + spanEnd + keyLabelSeparator;
//							valueShortLabel = valuesShortKeys[i].value;
						}
						
						valueFull = valueShort + valueValue; 
						
						var isSelected = (selValue === values[i].key);
						
						tmp = { "Code" : valueC, "Label":  valueFull, "Selected": isSelected};

						showCode == false ? spanCode = spanCode.replace(">", " style=\"display:none\">") : spanCode;
						
						tmp.Label = 
							( (valueC !== null && valueC !== "") ? ( spanCode + valueC + spanEnd ) : "") + 
							( (valueValue !== null && valueValue !== "") ? ( spanLabel + valueFull + spanEnd ) : valueValue );
						
						buffer += fn(tmp);
					}
				}
			}
			return buffer;
		}
		Handlebars.registerHelper( 'loopForMapWithShortKeyLabel', loopForMapWithShortKeyLabel );
		return loopForMapWithShortKeyLabel;
	}
);