define(
	[
	 'handlebars',
	 'HelperUtils'
	 ],
	function ( Handlebars, HelperUtils/*, getText*/ ) {
		function loopForMap ( context, mapName, options ) {
			
			var map = HelperUtils.require_map( mapName );
			var buffer="";
			var fn= options.fn;
			var hasEmptyOption = options.hash.emptyOption;
			var ifEmptySetSelected= options.hash.ifEmptySetSelected; //1
			var extraSpaceBetween = options.hash.extraSpaceBetween;
			var tmp = {};
			
			/* The option below ('showCode') controls whether the displayed text of the option element
			 * needs to also display the key of the map.
			 */
			var showCode = options.hash.showCode;
			var spanCode = "<span class=\"Code\">";
			var spanLabel = "<span class=\"Label\">";
			var spanEnd = "</span>";
			
			var defaultvalue = ( options === undefined || options === null ) 
								? null : ( options.hash === undefined || options.hash === null )
										? null : ( options.hash.defaultvalue === undefined || options.hash.defaultvalue === null )
											? null : options.hash.defaultvalue;//this can be used as a placeholder for example -- enter a value --, but it will not be displayed in soft gray (different css)
			
			var selValue = "";
			
			if (map!==undefined && map!== null){
				
				var values = map.toArray();
				
				if (values!= null && values.length >0){
					//We need to display an empty option
					
					if ( hasEmptyOption === true && values[0]["key"] !== ""){
						if (defaultvalue!== undefined && defaultvalue !== null && defaultvalue !== ""){
							//add default value as an option to the select
							var options2 = {"hash" : {"key" : defaultvalue}};
							var defaultText = HelperUtils.get_text(NaN, options2);
							values.splice(0,0,{"key" : "", "value" : defaultText.toString()});//adds empty option to array of values for select
						} else {
							values.splice(0,0,{"key" : "", "value":""});//adds empty option to array of values for select
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
						var valueV = values[i].value;
						var isSelected = (selValue === values[i].key);
						
						tmp = {	"Code" : valueC, "Label":  valueV, "Selected": isSelected};
						
						if ( showCode == true ){

                            var extraSpace = "";
                            if (extraSpaceBetween !== undefined && extraSpaceBetween !== null && extraSpaceBetween !== false) {
                                extraSpace = " ";
                            }

							tmp.Label = 
							( (valueC !== null && valueC !== "") ? ( spanCode + valueC + spanEnd + extraSpace) : "") +
							( (valueV !== null && valueV !== "") ? ( spanLabel + valueV + spanEnd ) : valueV );
						}
						buffer += fn(tmp);
					}
					/*if ( ifEmptySetSelected !== undefined && ifEmptySetSelected !== null && ifEmptySetSelected !== ""){
						//trigger change event ?
					}*/
				}
			}
			return buffer;
		}
		Handlebars.registerHelper( 'loopForMap', loopForMap );
		return loopForMap;
	}
);