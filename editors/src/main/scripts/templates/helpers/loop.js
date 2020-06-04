define(
	['handlebars','xdate', 'underscore', 'europass/http/WindowConfigInstance'], 
	function ( Handlebars, XDate, _, Config ) {
		
		function loop ( context, options ) {
			var buffer="";
			var INCREMENT_YEAR = 1;
			var fn= options.fn;	
			
			var end = Number(options.hash.end);
			var start = Number(options.hash.start);
			var step = Number(options.hash.step);
			var looptype = options.hash.looptype;
			
			var hasStart = ( _.isNumber(start) && start>=0);
			var hasStep = ( _.isNumber(step) && step>0);
			var hasLooptype = (looptype!==undefined && looptype!==null && looptype !== "");
			
			if ( hasLooptype && "year" === looptype ){
				/**
				 * the “end” options for year may be:
				 *	1. a specific number: use that number as end year
				 *	2. +{digit}: use current year and add the number of years specified by the digit
				 *	3. -{digit}: use current year and substract the number of years specified by the digit
				 */
				var serverNowYear = new XDate(Config.getServerDateTime(), true).getFullYear() ;
				end = options.hash.end;	
				//case undefined...
				if ( end === undefined || end === null || end === "" ){
					end = serverNowYear;
				} 
				else {
					var firstChar = end.substring(0,1);
					switch ( firstChar ){
						case "+":{
							var add = end.length > 1 ? Number(end.substr(1)) : 0;
							end = serverNowYear + ( _.isNumber(add) ? add : 0 );
							break;
						}
						case "-":{
							var minus = end.length > 1 ? Number(end.substr(1)) : 0;
							end = serverNowYear - ( _.isNumber(minus) ? minus : 0 );
							break;
						}
						default:{
							var num = Number( end );
							if ( _.isNumber(num) )
								end = num;
							break;
						}
					}
				}
				
				
				//Start
				if (!hasStart) start=1930;
			}
			
			var hasEnd = ( _.isNumber(end) && end>0);
			
			if (hasLooptype && looptype=="month"){
				if (!hasStart) start=1;
				if (!hasEnd) end=12;
			}
			
			if (hasLooptype && looptype=="day"){
				if (!hasStart) start=1;
				if (!hasEnd) end=31;
			}
			
			var tmp={};
			//Note: The Index should be added as a string, because we then use equals, which works on objects of the same type, and Date parts are already stored as numbers.
			if (hasLooptype && looptype=="year"){//if year the reverse loop
				for (var i=end; i>=start; i--){	
					tmp = {"Index" : ""+i+"", "Label" : i};
					if (hasStep && step>1) i=i-step+1;
					buffer += fn(tmp);
				}
			}else{
				for (var i=start; i<=end; i++){	
					tmp = {"Index" : ""+i+"", "Label" : i};
					//IS THIS NECESSARY SINCE START IS AT 1 ? (looptype=="month")? context["Index"] = ""+(i-1)+"" : context["Index"] = ""+i+"" ;
					if (hasStep && step>1) i=i+step-1;
					buffer += fn(tmp);
				}
			}
			return buffer;
		}
		Handlebars.registerHelper( 'loop', loop );
		return loop;
	}
);