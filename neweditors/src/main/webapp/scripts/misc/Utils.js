define(
[
'jquery'
], 	
function( $ ){
	var Utils = {};
	
	Utils.escapeForJQuery = function( str ){
		if ( str === undefined || str === null ){ return str;}
		return str.replace(/(:|\.|\/|\[|\])/g,'\\$1');
	};
	Utils.escapeForRegExp = function(str) {
		if ( str === undefined || str === null ){ return str;}
		return str.replace(/[-\[\]\/\{\}\(\)\*\+\?\.\\\^\$\|]/g, "\\$&");
	};
	
	/**
	 * Replaces all occurrences of key within a str with a value
	 * @param str
	 * @param key
	 * @param value
	 * @param defaultValue
	 */
	Utils.replaceKey = function( str, key, value, defaultValue ){
		if ( str === undefined || str === null || str === ""){ return "empty-text";}
		
		if ( key === undefined || key === null || key === "" ){
			return str;
		}
		var undefinedValue = (value === undefined || value === null || value === "" );
		var undefinedDefaultValue = (defaultValue === undefined || defaultValue === null || defaultValue === "" );
		if ( undefinedValue === true && undefinedDefaultValue === false ){
			value = defaultValue;
		} else if ( undefinedValue === true && undefinedDefaultValue === true ){
			return str;
		}
		var escapedKey = Utils.escapeForRegExp( key );
		
		str = str.replace( new RegExp(escapedKey, "g"), value );
		
		return str;
	};

	Utils.iterateObj = function( obj, callback, scope, level, jsonpath ){
		
		if ( level === undefined || level === null ){ level = 0 ; }
		
		if ( jsonpath === undefined || jsonpath === null ){ jsonpath = "" ; }
		
		if ( obj === undefined || obj === null 
				|| (obj !== null && typeof( obj ) === "object" && $.isEmptyObject(obj)) ){
			return false;
		}
		for ( var prop in obj ){
			if ( !obj.hasOwnProperty(prop) ){ continue; }
			
			var value = obj[ prop ];
			
			if ( value === undefined || value === null ){ continue; }
			if ( typeof value === "function" ){ continue; }
			if ( typeof value !== "object" ){ continue; }
			
			callback.apply( scope || this, [ value, prop, level, jsonpath ] );
		}
	};
	Utils.objAttr = function( obj, jsonpath ){
		if ( obj === undefined || obj === null 
				|| (obj !== null && typeof( obj ) === "object" && $.isEmptyObject(obj)) ){ return false; }
		var value = obj;
		if ( jsonpath !== undefined && jsonpath !== null && jsonpath !== ""
				&& typeof jsonpath === "string" ){
			var parts = jsonpath.split(".");
			for (var i=0; parts!== undefined && i<parts.length; i++){
				var part = parts[i];
				var tmp = null;
				var matchArray = Utils.matchArray( part );
				if ( matchArray !== null &&  matchArray[1] != null &&  matchArray[3] != null && value[matchArray[1]] != null ){
					var partPath = matchArray[1];
					var partIndex = matchArray[3];
					tmp = value[ partPath ][ partIndex ];
				}else {
					tmp = value[ part ];
				}
				if ( tmp === undefined || tmp === null ){ return false; }
				value = tmp;
			}
		}
		return value;
	};
	Utils.jId = function( str ){
		if ( str === undefined ){ return ""; }
		return '#' + Utils.escapeForJQuery(str);
	};
	/**
	 * Convert a Path returned from JSONPATH to valid jsonPath
	 */
	Utils.convertJsonPath = function( jpath ){
		//e.g. "$['SkillsPassport']['LearnerInfo']['WorkExperience'][0]['Employer']['ContactInfo']['Address']['Contact']['Country']['Code']"
		//convert to
		//"$['SkillsPassport']['LearnerInfo']['WorkExperience'][0]['Employer']['ContactInfo']['Address']['Contact']['Country']['Code']"
		//replace inner indexed
		var path = jpath.replace( /'\](\[\d+\])\['/g, "$1.");
		//replace inner
		path = path.replace(/'\]\['/g, ".");
		//replace first 
		path = path.replace(/\$\['/g, "");
		//replace last
		path = path.replace(/'\]/g, "");
		return path;
	};
	
	Utils.compareArray = function( arr1, arr2 ){
		return (  $(arr1).not(arr2).length == 0 && $(arr2).not(arr1).length == 0  );
	};
	
	var EMAIL_REGEXP = /^([a-zA-Z0-9_\.\-\+])+\@(([a-zA-Z0-9\-])+\.)+([a-zA-Z0-9]{2,4})+$/;
	
	Utils.isValidEmail = function( str ){
		return EMAIL_REGEXP.test( str );
	};
	/**
	 * Find the difference arr - filter.
	 * Elements of arr that are not part of filter.
	 * @param arr the original array
	 * @param filter the array based on which to filter
	 */
	Utils.setDifference = function ( arr , filter ){
		var result = [];
		
		$.grep( arr, function( n ,i ){
			if ( $.inArray( n, filter ) < 0){
				result.push( n );
			}
		} );
		
		return result;
	};
	/**
	 * Used to find the different class names of the input element
	 * and return them
	 * @param input
	 * @returns
	 */
	Utils.chooseClassNames = function(input){
		var clazzes = input.attr("class").split(" ");
		//get the of the two arrays
		var chosenNames = Utils.setDifference( $(clazzes), Utils.functionalClassNames );
		if ( chosenNames.length > 0 ) {
			return chosenNames.join(" ");
		} else {
			return "europass-select2-results";
		}
	};
	
	/** HASH CODE JAVA-like ***/
	Utils.hashCode = function( str ){
	    var hash = 0;
	    if (str.length == 0) return hash;
	    for ( var i = 0; i < str.length; i++) {
	        var charAt = str.charCodeAt(i);
	        hash = ((hash<<5)-hash)+charAt;
	        hash = hash & hash; // Convert to 32bit integer
	    }
	    var hashStr = "" + hash;
	    if ( hashStr.indexOf("-") === 0 ){ hashStr = hashStr.substr(1);}
	    return hashStr;
	};
	Utils.randomInK = function(){
		var r = Math.random(); //between 0 and 1
		return parseInt(r*1000);
	};

	return Utils;
});
