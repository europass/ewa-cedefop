define(
	'HelperUtils',
	
	[
	 'require',
	 'jquery', 
	 'underscore',
	 'handlebars',
	 'xdate',
	 'Utils', 
	 
	 'i18n!localization/nls/AddressFormat',
	 'europass/maps/AddressFormatMap', 
	 'i18n!localization/nls/DateFormat',
	 'i18n!localization/nls/DefaultDateFormat',
	 
	 //------ Maps -----
	 'europass/maps/HeadlineTypeMap',
	 'europass/maps/LanguageListeningLevelMap',
	 'europass/maps/LanguageReadingLevelMap',
	 'europass/maps/LanguageSpokenInteractionLevelMap',
	 'europass/maps/LanguageSpokenProductionLevelMap',
	 'europass/maps/LanguageWritingLevelMap',
	 'europass/maps/TelephoneTypeMap',
	 'europass/maps/CertificateLevelMap',
	 'europass/maps/LanguageShortLevelMap',
	 'europass/maps/IctInformationProcessingLevelMap',
	 'europass/maps/IctCommunicationLevelMap',
	 'europass/maps/IctContentCreationLevelMap',
	 'europass/maps/IctSafetyLevelMap',
	 'europass/maps/IctProblemSolvingLevelMap',
	 'europass/maps/IctLevelMap',
	
	 //------ Localisable Labels -----
	 'i18n!localization/nls/DateFormatOption',
	 'i18n!localization/nls/GuiLabel',
	 'i18n!localization/nls/GuiLabelExtra',
	 'i18n!localization/nls/Country',
	 'i18n!localization/nls/DocumentLabel',
	 'i18n!localization/nls/EditorHelp',
	 'i18n!localization/nls/EditorPlaceholder',
	 'i18n!localization/nls/Notification',
	 'i18n!localization/nls/HeadlineType',
	 'i18n!localization/nls/EditorCommands',
	 'i18n!localization/nls/AccessibilityLabel',
	 'i18n!localization/nls/DocumentCustomizations',
	 
	 'europass/dates/EuropassDateFormat',
	 'europass/ExperienceDateFormatInstance'
	 ], 
	
	function( require, $, _, Handlebars, XDate, Utils, AddressFormats, AddressFormatMap, DateFormat, DefaultDateFormat,
			HeadlineTypeMap, LanguageListeningLevelMap, LanguageReadingLevelMap, 
			LanguageSpokenInteractionLevelMap, LanguageSpokenProductionLevelMap, 
			LanguageWritingLevelMap, TelephoneTypeMap, CertificateLevelMap,LanguageShortLevelMap,
			IctInformationProcessingLevelMap,IctCommunicationLevelMap,IctContentCreationLevelMap,IctSafetyLevelMap,IctProblemSolvingLevelMap,IctLevelMap,
			DateFormatOption, GuiLabel, GuiLabelExtra, CountryLabel, DocumentLabel, EditorHelp, EditorPlaceholder, Notification, 
			HeadlineType, EditorCommands, AccessibilityLabel, DocumentCustomizations, EuropassDateFormat, DocumentDateFormat ){
		
		var LOCALISED_LABELS = {
			DateFormatOption: DateFormatOption,
			GuiLabel : GuiLabel,
			GuiLabelExtra : GuiLabelExtra,
			CountryLabel : CountryLabel,
			DocumentLabel : DocumentLabel,
			EditorHelp : EditorHelp,
			EditorPlaceholder : EditorPlaceholder,
			Notification : Notification,
			HeadlineType: HeadlineType,
			EditorCommands : EditorCommands,
			AccessibilityLabel : AccessibilityLabel,
			DocumentCustomizations : DocumentCustomizations
		} ;
		
		var LOCALISED_MAPS = {
			HeadlineTypeMap : HeadlineTypeMap,
			LanguageListeningLevelMap : LanguageListeningLevelMap,
			LanguageReadingLevelMap : LanguageReadingLevelMap,
			LanguageSpokenInteractionLevelMap : LanguageSpokenInteractionLevelMap,
			LanguageSpokenProductionLevelMap : LanguageSpokenProductionLevelMap,
			LanguageWritingLevelMap : LanguageWritingLevelMap,
			TelephoneTypeMap : TelephoneTypeMap,
			CertificateLevelMap : CertificateLevelMap,
			LanguageShortLevelMap : LanguageShortLevelMap,
			IctInformationProcessingLevelMap : IctInformationProcessingLevelMap,
			IctCommunicationLevelMap : IctCommunicationLevelMap,
			IctContentCreationLevelMap : IctContentCreationLevelMap,
			IctSafetyLevelMap : IctSafetyLevelMap,
			IctProblemSolvingLevelMap : IctProblemSolvingLevelMap,
			IctLevelMap : IctLevelMap
		};
		
		var HelperUtils = {};
		
		HelperUtils.require_map = function( mapName ){
			return LOCALISED_MAPS[ mapName ];
		};
		
		HelperUtils.requireLabelBundle = function( filename ){
			return LOCALISED_LABELS[ 
			       ( filename === undefined || filename === null || filename === "")
			       		? "GuiLabel" : filename ];

		};

		HelperUtils.addressFormatExists = function( format ){
			var formatOK = false;
			$.each(AddressFormats, function(i) {
				if ( format === AddressFormats[i]){
					formatOK = true;
					return false;
				}
			});
			return formatOK;
		};
		
		HelperUtils.addressFormat = function(address, format){
			var s=""; var m=""; var z=""; var c=""; var p="";
			var hasS=false; var hasM = false; var hasZ=false; var hasC = false; var hasP = false;
			var fmtObj = null;			
			var addressString = "";
			var fmtArray = new Array();		
			
			if ( address !== undefined && address !== null ){
				var addressLine = address.AddressLine;
				if ( addressLine !== undefined && addressLine !== null && addressLine!== '' ){
					s = addressLine;
					hasS = true;
				}	
				var municipality = address.Municipality;
				if ( municipality !== undefined && municipality !== null && municipality !== '' ){
					m = municipality;
					hasM = true;
				}	
				var postalCode = address.PostalCode;
				if ( postalCode !== undefined && postalCode !== null && postalCode!== ''){
					z = postalCode;
					hasZ = true;
					hasP=true;
				}
				//Check if we have Country info in the Address
				var countryActive = false;
				var countryCode = undefined;
				var country = address.Country;
				var hasCountry = country !== undefined && country !== null && _.isObject(country);
				if ( hasCountry ){
					var countryLabel = country.Label;
					if ( countryLabel !== undefined && countryLabel !== null && countryLabel !== '' ){
						c = countryLabel;		
						hasC=true;
					}
					
					//Check if we have Country.Code 
					countryCode = country.Code;
					var hasCountryCode = (countryCode !== undefined && countryCode !== null && countryCode !== "");
					if ( hasCountryCode ){
						countryActive = true;
					}//end if address.Country.Code
				}//end if address.Country
				/*
				 * 
				 * Transient Country is used when we want to supply some fixed Country info, other than user-added data.
				 * For example CoverLetter > Addressee > Address : we need to format only zip code and municipality
				 * 
				 */
				else {
					var transientCountry = address.transientCountry;
					if ( transientCountry !== undefined && transientCountry !== null && _.isObject(transientCountry) ){
						countryCode = transientCountry.Code;
						var hasTransientCountryCode = (countryCode !== undefined && countryCode !== null && countryCode !== "");
						if ( hasTransientCountryCode ){
							countryActive = true;
						}
					}
				}
				
				//Check if we have transient Country Info in the Address
				if ( countryActive && countryCode !== undefined ){
					p = countryCode;
					fmtObj = AddressFormatMap.get(p);
					
					
					//If the map defines a specific country postal code, reset the p value
					if( fmtObj === undefined || fmtObj === null || fmtObj === ""){
						//Default Format
						var defaultFormatObj = AddressFormatMap.get("default");//get default format
						p = defaultFormatObj.countryPostalCode;
						//format = defaultFormatObj.format;
					} else {
						if ( fmtObj.countryPostalCode !== undefined && fmtObj.countryPostalCode !== null && fmtObj.countryPostalCode !== "" ){
							p = fmtObj.countryPostalCode;
						}
					}
				}
				
			}//end if address
			
			fmtArray = HelperUtils.addressReplaceNewLines( format );
			
			if (!hasS) fmtArray = HelperUtils.addressClean(fmtArray,"s");
			if (!hasM) fmtArray = HelperUtils.addressClean(fmtArray,"m");
			if (!hasZ) fmtArray = HelperUtils.addressClean(fmtArray,"z");
			if (!hasC) fmtArray = HelperUtils.addressClean(fmtArray,"c");
			if (!hasP) fmtArray = HelperUtils.addressClean(fmtArray,"p");
			
			fmtArray = HelperUtils.addressCleanAfter( fmtArray );
			
			//fill placeholders with values
			for (var i = 0, len = fmtArray.length; i<len; i++) {
				var value = fmtArray[i];
				switch(value){
					case "s":
						(hasS)? fmtArray[i]=s: fmtArray[i]="";
						break;
					case "m":
						(hasM)? fmtArray[i]=m: fmtArray[i]="";  
						break;
					case "z":
						(hasZ)? fmtArray[i]=z: fmtArray[i]="";  
						break;
					case "p":
						(hasP)? fmtArray[i]=p: fmtArray[i]="";
						break;
					case "c":
						(hasC)? fmtArray[i]=c: fmtArray[i]="";  
						break;
					case "newline":
						fmtArray[i]="<BR>";
						break;
					default:	
						fmtArray.push(fmtArray[i]);  
				}
				addressString += Handlebars.Utils.escapeExpression(fmtArray[i]);
			}
			return addressString;
		};
		
		HelperUtils.addressClean = function(fmtArray, placeholder){
			var value=0;
			var pos = -1;
			var tempArray1 = new Array();
			var tempArray2 = new Array();
			
			pos = $.inArray( placeholder, fmtArray );//fix for i.e 8
			
			//remove from start
			if ( pos==0 ){
				value = fmtArray[1];
				if (value!="s" && value!="m" && value!="z" && value!="p" && value!="c" && value!="newline"){
					fmtArray.splice(0,2);//remove next
				}else 
					fmtArray.splice(0,1);	
			//remove from middle
			}else if ( pos > 0 && pos < fmtArray.length-1 ){
				value = fmtArray[pos-1];//slice previous
				if (value!="s" && value!="m" && value!="z" && value!="p" && value!="c" && value!="newline"){
					tempArray1 = fmtArray.slice(0,pos-1);
				}else
					tempArray1 = fmtArray.slice(0,pos);
				
				value = fmtArray[pos+1]; //slice next
				if (value!="s" && value!="m" && value!="z" && value!="p" && value!="c" && value!="newline" && value!=" "){
					tempArray2 = fmtArray.slice( pos+2,fmtArray.length );// remove next
				}else
					tempArray2 = fmtArray.slice(pos+1,fmtArray.length);// remove next
				fmtArray = tempArray1.concat(tempArray2);
			//remove from end
			}else if (pos==fmtArray.length-1){
				value = fmtArray[pos-1];
				if (value!="s" && value!="m" && value!="z" && value!="p" && value!="c"){//removes newline if the end
					fmtArray.splice(pos-1,2);//remove previous
				}else{
					fmtArray.splice(pos,1);//remove 
				}
			}
			
			// EWA-1609: fix for removing extra commas - the pattern applies to formats like ", (" so we need to remove the comma before the left parenthesis
			if(tempArray1[tempArray1.length-1] == "," && tempArray2[1] == "(" ){
				fmtArray.splice(tempArray1.length-1,1);
			}
			
			return fmtArray;
		};
		//after address placeholders (s,m,z,p,c) have been set, the remove unnecessary newlines
		HelperUtils.addressCleanAfter = function (fmtArray){
			var pos=-1;
			//remove unnecessary new line if any
			for (var i = 0, len = fmtArray.length; i<len; i++) {
				pos = $.inArray( "newline", fmtArray );//fix for i.e 8
				//
				if ( pos == -1 ) break; //no newline
				
				if ( pos == 0) {//newline at the beginning
					if (fmtArray[pos+1] == " " ){
						fmtArray.splice(pos,2); 
					}else
						fmtArray.splice(pos,1);
				}
				
				if (pos == fmtArray.length-1){ //newline at the end
					if (fmtArray[pos-1]!="s" && fmtArray[pos-1]!="m" && 
						fmtArray[pos-1]!="z" && fmtArray[pos-1]!="p" && 
						fmtArray[pos-1]!="c" ){
						fmtArray.splice(pos-1,2); 
					}else
						fmtArray.splice(pos,1);
				} 
			}
			return fmtArray;
		};
		
		HelperUtils.addressReplaceNewLines = function(format){
			var pos =0;
			var dummy = "\n";
			var newLinePos = new Array();
			var fmtArray = new Array();
			//var fmtArrayNew = new Array();
			
			if (format.indexOf('\\n')!=-1)//have to check for '\n' and for '\\n'
				format= format.replace("\\n",dummy);
					
			while (format.indexOf(dummy,pos)!=-1){
				pos = format.indexOf(dummy,pos);				
				newLinePos.push(pos);
				pos++;
			}
			
			fmtArray = format.split("");
			
			//find "\n" and convert to "newline"
			if (newLinePos.length!=0){
				for (var i=0, len=newLinePos.length; i<len; i++){				
				 fmtArray[newLinePos[i]]="newline"; 

				}
			}
			return fmtArray;
		};
		
		/**
		 * Check and replace single quoted character
		 */
		
		HelperUtils.replaceSingleQuote = function(context){
			if(context.match(/[']/g)!= null ){
				context.match(/[']/g).length % 2 != 0 ? context = context.replace("\'","\''''") : context;
			}
			return context;
		};
		
		
		/**
		 * Converts the entered date (day/month/year) to the format defined in print_pref,
		 * depending on the user input, converts to the corresponding format choices
		 * if invalid input then return null
		 */
		HelperUtils.formatDate = function ( context, format, useDefaultDate, document ) {
			if ( format === undefined || format === null || DateFormat.patterns[format] === undefined ){
				format = useDefaultDate === true ?  DefaultDateFormat[document] : DocumentDateFormat.applyFunction( { f: "get" } );
			}
			var localisedFmt = DateFormat.patterns[format] !== undefined? DateFormat.patterns[format] : DefaultDateFormat[document];
			
			var europassDateFormat = new EuropassDateFormat(localisedFmt);
			finalFormat = europassDateFormat.format(context);

			if (finalFormat==""){
				return "";
			}

			return finalFormat;
		};
		/**
		 * Format the Context representing a Period according to the Date format, 
		 * taking into consideration if there are values and if the period reaches today.
		 * @param context
		 * @returns {String}
		 */
		HelperUtils.formatPeriod = function( context, format, useDefaultDate, document ){
			var spanPart1 = "<span class=\"period\">";
			var spanPart1Default = "<span class=\"period default-value\">";
			var spanPart2 = "</span>";
			var separator = "&ndash;";
			
			var period = "";
			
			if( Utils.isEmptyObject(context) === true ) {
				period += spanPart1Default + DocumentLabel["Dates.From"]+ spanPart2;
				period += separator + spanPart1Default + DocumentLabel["Dates.To"] + spanPart2;
				
			} else {
				var contextFrom = context.From;
				var contextTo = context.To;
				var contextCurrent = context.Current;
				
				var isCurrent = (contextCurrent!== undefined && contextCurrent!== null && ( contextCurrent==true || contextCurrent== "true") );
				var hasFrom = (contextFrom!== undefined && contextFrom!== null );
				var hasTo = (contextTo!== undefined && contextTo!== null);
				//get from date
				if (hasFrom){
					period += spanPart1 + HelperUtils.formatDate( contextFrom, format, useDefaultDate, document ) + spanPart2;
				}
				//get to date	
				if (hasTo===true && isCurrent===false){
					period += spanPart1 + ( hasFrom ? separator : ""); 
					period += HelperUtils.formatDate( contextTo, format, useDefaultDate, document) + spanPart2;
				} else if ( (hasTo===true && isCurrent===true) || (hasTo===false && isCurrent===true ) ){
					period += spanPart1 + ( hasFrom ? separator : "");
					period += DocumentLabel["Dates.Current"];
					period += spanPart2;
				}
			}
			return period;
		};
		
		HelperUtils.areaWithDescription = function(description, area ){
			var areaWithDescr = description;
			
			if (area != undefined && area != null){
				area = Handlebars.Utils.escapeExpression(area);
				if (description.length > 0){	
					if (description.match(/^<p>/)){
						areaWithDescr = description.replace(/^<p>/, "<p><strong>" + area + ":</strong> ");
					}
					else if (description.match(/^<ul>/) || description.match(/^<ol>/)){
						areaWithDescr = "<p><strong>" + area + ":</strong></p>" + description;
					}
					else{
						areaWithDescr = "<p><strong>" + area + ":</strong>" + description + "</p>";
					}
				}
				else{
					areaWithDescr = "<p><strong>" + area + "</strong></p>";
				}
			}
			
			return areaWithDescr;
		};	
		HelperUtils.GetText_SUBSECTION_TXT = "{{subsection}}";
		HelperUtils.GetText_regexp = new RegExp( /{{subsection}}/ );
		
		HelperUtils.get_text = function( context ) {
			var initKey = context;
			var group = null;
			var options = arguments[ arguments.length-1 ];
			
			if ( context === undefined || context === null ){
				return "no-text";
			}
			if ( context === "" ){
				return "";
			}
			if ( options !== undefined && options.hash !== undefined && $.isPlainObject(options.hash) ){
				group = options.hash.group;
			}
			//if there is a subsection use it (e.g. Generic Skill Titles).  Subsections are used in the additional skills section.	
			var indexedPos = initKey.indexOf(HelperUtils.GetText_SUBSECTION_TXT);
			if ( indexedPos > 0 ){
				var i = 1;
				var subsection = arguments[i];
				while ( indexedPos > 0 ){
					initKey = initKey.replace( HelperUtils.GetText_regexp, subsection );
					indexedPos = initKey.indexOf(HelperUtils.GetText_SUBSECTION_TXT);
					i++;
				}
			}
			//end subsection handling
			var groupDefined = ( group !== undefined && group !== null && group!=="");
			var labelBundle = HelperUtils.requireLabelBundle( groupDefined ? group : null );
			if ( labelBundle === null ){
				return "no-labels-bundle-named-"+group;
			}
				
			var key = Utils === undefined ? initKey : Utils.removeIndexTxt(initKey);
			
			var label = labelBundle [key];
			
			//In case it is not found in thhe "group" search in the list of fallbackGroups
			if ( label === undefined || label === null ) {
				//fallback group?
				var fallbackGroup = options.hash.fallbackGroup;
				if ( fallbackGroup !== undefined && fallbackGroup !== null && fallbackGroup!==""){
					var fGroups = fallbackGroup.split(" ");
					for ( var i=0; i<fGroups.length; i++ ){
						var fGroupBundle = HelperUtils.requireLabelBundle( fGroups[i] );
						if ( fGroupBundle === null ){
							continue;
						}
						label = fGroupBundle [key];
						if ( label !== undefined && label !== null ) {
							break;
						}
					}
				}
			}
			if ( label === undefined || label === null ) {
				label = (groupDefined ? group : "GuiLabel") +"[" + key + "]";
			}
			return new Handlebars.SafeString(label);
		};

        return HelperUtils;
	}
);
