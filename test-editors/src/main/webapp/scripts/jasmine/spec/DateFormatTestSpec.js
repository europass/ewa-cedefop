define([ 'jquery',
         'underscore',
         'backbone', 
         'jasmine-html',
         'i18n!localization/nls/DateFormat',
         'europass/dates/EuropassDateFormat',
         'europass/dates/DayField'],

function($, _,
		Backbone, 
		jasmine,
		DateFormat,
		EuropassDateFormat,
		DayField) {
	
	describe("Test the Dateformat with pattern \"some text ddd MMMM wherever yyyy\" for the 31 day of March...", function() {
		
		it("0: When Date has the format: text/long/texts", function() {
			
			var localisedFmt = "some text dd MMMM wherever yyyy";
			var context = {
					"Day":31,"Month":3,"Year":2014
			};
			
			var europassDateFormat = new EuropassDateFormat(localisedFmt);
			result = europassDateFormat.format(context);
			
			expect(result).toEqual("some text 31 March wherever 2014");
		});
		
	});
	
	describe("Test the Dateformat with pattern ddd MMMM yyyy for the 31st day of March...", function() {
		
		it("0: When Date has the format: text/long/suffix", function() {
			
			var localisedFmt = "ddd MMMM yyyy";
			var context = {
					"Day":31,"Month":3,"Year":2014
			};
			
			var europassDateFormat = new EuropassDateFormat(localisedFmt);
			result = europassDateFormat.format(context);
			
			expect(result).toEqual("31st March 2014");
		});
		
	});
	
	describe("Test the Dateformat for all days of March and in all patterns...", function() {
		
		for(var i=1; i<=31; i++){
			doTheThing(i);
		}
		
		function doTheThing(index){
			 for(var pattern in DateFormat.patterns){
				 	doTheOtherThing( DateFormat.patterns[pattern],index);
			 	}
		}
		
		function doTheOtherThing(pattern, index){
			
				it("For day "+i+" with pattern "+pattern, function() {
				  var day = index.toString();
				 
				  var dayArray = [];
				  
				  var context = {};
				  context.Day=index;
				  context.Month=3;
				  context.Year=2014;
				  
				  var europassDateFormat = new EuropassDateFormat(pattern);
				  result = europassDateFormat.format(context);
				  
				  var hasPrevious = true;
					
			 	  if(pattern == "ddd MMMM yyyy"){
					  
					  switch(day.length){
					  	case 1: 
					  		day = addDaySuffix(day, day);
					  	    break;
					  	case 2:
					  		day = addDaySuffix(day.charAt(1),day);
					  	    break;
					  	default:
					  	    break;
					  }
					  
					  expect(result).toEqual(day+" March 2014");
					  
				  }else{
					  var symbolPos = pattern.indexOf(" ") != -1 ? pattern.indexOf(" ") : 
						  				(pattern.indexOf("/")!=-1 ? pattern.indexOf("/") : pattern.indexOf(".") );
					  var daySymbol = pattern.substring(0,symbolPos);
					  
					  var dayfield = new DayField(daySymbol.length);
					  dayfield.format( dayArray, context, hasPrevious );
					  
					  var resultDay = null;
					  
					  if(result.indexOf(" ") != -1)
					  	resultDay = result.substring(0, result.indexOf(" "));
					  else if (result.indexOf("/") != -1)
					  	resultDay = result.substring(0, result.indexOf("/"));
					  else if (result.indexOf(".") != -1)
						resultDay = result.substring(0, result.indexOf("."));
					  
					  expect(resultDay).toEqual(dayArray[0]);
				  }
			 	  
				});

		}
		
		function addDaySuffix(endsWith, day){
	  		switch(endsWith){
		  		case "1":
		  			if(day=="1" || day == "21" || day=="31"){
			  			day = day+"st";
			  			return day;
		  			}
			  		else{
				    	day = day+"th";
				        return day;
			  		} 			
		  		case "2":
		  			if( day=="2" || day == "22"){
			  			day = day+"nd";
			  			return day;
		  			}else{
			  			day = day+"th";
			  			return day;
		  			}
		  		case "3":
		  			if(day == "3" || day=="23"){
			  			day = day+"rd";
			  			return day;
		  			}else{
			  			day = day+"th";
			  			return day;
		  			}
			    default:
			    	day = day+"th";
			        return day;
			}
		}
		
	});
	
	
});