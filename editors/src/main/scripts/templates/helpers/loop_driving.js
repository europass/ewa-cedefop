define(
	[
	 'handlebars',
	 'europass/maps/DrivingLicenseMap',
	 'i18n!localization/nls/EditorHelp'],
	 
	 /**
	  * The helper returns an object with the DrivingLicenseMap objects to be used in checkbox items.
	  * It also checks the context, to return checked value if it is already contained. 
	  */
	function ( Handlebars, map, help) {
		
		var CELLS_PER_ROW = 4;
		
		var dlHelp =	[
						 
			     			{
			     				"value": "AM",
			     				"html": new Handlebars.SafeString("<li class=\"capacity\">&lt; 50 cm&sup3; </li><li class=\"speed\">&lt; 45 km&#47;h</li><li class=\"kw\">&lt; 4kW</li><li class=\"age\">16&#43;</li>")},
			     			
			     			{
			     				"value": "A1",
			     				"html": new Handlebars.SafeString("<li class=\"capacity\">&lt; 125 cm&sup3; </li><li class=\"kw\">&lt; 11 kW &#45; 0.1 kW&#47;kg </li><li class=\"age\">16&#43;</li>")},
			     			  
			     			{
			     				"value": "A2",
			     				"html": new Handlebars.SafeString("<li class=\"kw\">&lt; 35 kW &#45; 0.2 kW&#47;kg </li><li class=\"age\">18&#43;</li>")},						           
			     		           
			     			{
			     				"value": "A",
			     				"html": new Handlebars.SafeString("<li class=\"kw\">&gt; 35 kW &#45; 0.2 kW&#47;kg </li><li class=\"age\">20&#47;24&#43;</li>")},
			     			
			     			{   "value": "B1",
			     			    "html": new Handlebars.SafeString("<li class=\"kw\">&lt; 15 kW</li><li class=\"mass\">&lt;400&#47;500 kg</li><li class=\"age\">16&#43;</li>")}, 
			
			     			
			     			{
					     	    "value": "B",
					     		"html": new Handlebars.SafeString("<li class=\"load\">&lt; 3500 kg</li><li class=\"persons\">max. 8+1</li><li class=\"limit\">&lt; 750 kg</li><li class=\"age\">18&#43;</li>")},	
			     			    
			     			    
			     			{
				     			"value": "BE",
				     			"html": new Handlebars.SafeString("<li class=\"load\">&lt; 3500 kg</li><li class=\"persons\">max. 8+1</li><li class=\"limit\">&gt; 750 kg</li><li class=\"age\">18&#43;</li>")}, 
			     			    
			     			    
				     		{
				     			"value": "C1",
				     			"html": new Handlebars.SafeString("<li class=\"load\">&lt; 7500 kg</li><li class=\"persons\">max. 8+1</li><li class=\"limit\">&lt; 750 kg</li><li class=\"age\">18&#43;</li>")}, 
				     			
			     			
			     			{
			     				"value": "C1E",
			     				"html": new Handlebars.SafeString("<li class=\"load\">&lt; 12000 kg</li><li class=\"persons\">max. 8+1</li><li class=\"limit\">&gt; 750 kg</li><li class=\"age\">18&#43;</li>")}, 
			     			
			     		    
			     				
			     		    {
			     				"value": "C",
			     				"html": new Handlebars.SafeString("<li class=\"load\">&gt; 3500 kg</li><li class=\"persons\">max. 8+1</li><li class=\"limit\">&lt; 750 kg</li><li class=\"age\">21&#43;</li>")}, 
			     			
			     		    {
			     				"value": "CE",
			     				"html": new Handlebars.SafeString("<li class=\"load\">&gt; 3500 kg</li><li class=\"persons\">max. 8+1</li><li class=\"limit\">&gt; 750 kg</li><li class=\"age\">21&#43;</li>")}, 
			     			
			     			{
			     				"value": "D1",
			     				"html": new Handlebars.SafeString("<li class=\"persons\" style=\"margin-left: -5px;\">max. 16+1</li><li class=\"length\">max. 8m</li><li class=\"limit\">&lt; 750 kg</li><li class=\"age\">21&#43;</li>")}, 
			     			
			     			{
			     				"value": "D1E",
			     				"html": new Handlebars.SafeString("<li class=\"persons\" style=\"margin-left: -5px;\">max. 16+1</li><li class=\"length\">max. 8m</li><li class=\"limit\">&gt; 750 kg</li><li class=\"age\">21&#43;</li>")}, 
			     			
			     			{
			     				"value": "D",
			     				"html": new Handlebars.SafeString("<li class=\"limit\">&lt; 750 kg</li><li class=\"age\">24&#43;</li>")}, 
			     			
			     			{
			     				"value": "DE",
			     				"html": new Handlebars.SafeString("<li class=\"limit\">&gt; 750 kg</li><li class=\"age\">24&#43;</li>")}
			     		    
			     		   ];

		function loop_driving ( context, options ) {
			
			var j = 1;
			var buffer="";
			var fn= options.fn;
			
			if (map!==undefined && map!== null){
				
				var values = map.toArray();
				
				if (values!= null && values.length >0){
					
					var modelValues = {};
					if ( context && !Handlebars.Utils.isEmpty(context) ){
						modelValues = context;
					}
					var size = values.length;
					
					for ( var i=0; i < size; i++ ){
						var currValue = values[i];
						
						if ( currValue == null ){ continue; }
						
						var currentKey = currValue.key;
				
	
						var tmp = { 
								"loopId" : i, 
								"loopValue" : currentKey, 
								"helpContent" :dlHelp[i]
						};
						
						
						
						//if the current context contains the current key
						var keyChecked = ( $.inArray(currentKey, modelValues) >= 0 );
						if ( keyChecked ){
							tmp.checked = "checked";
						}
						
						var currentChar = currentKey.charAt(0);
						var nextIdx = ( i + 1 ); 
						var emptyLi = (CELLS_PER_ROW - j);
						
						//Last one...
						if ( nextIdx === size ){ 
							buffer += fn(tmp);
							continue; 
						}
						
						var nextChar = values[nextIdx].key.charAt(0);
						var sameChar = ( currentChar === nextChar );
						var isLarge = currentKey.length > 2;
						
						if ( ( j < CELLS_PER_ROW ) && ( (isLarge && sameChar) || ( sameChar === false ) ) ){
							j = 1;
							tmp.emptyCell = true;
							/*tmp.emptyCell = [];
							for ( var t = 0; t < emptyLi ; t++ ){
								tmp.emptyCell.push("empty");
							}*/
						} else {
							j++;
						}
						
						buffer += fn(tmp);
					}
				}
			}
			return buffer;
		}
		Handlebars.registerHelper( 'loop_driving', loop_driving );
		return loop_driving;
	}
);