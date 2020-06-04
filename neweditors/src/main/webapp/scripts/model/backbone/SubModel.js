define(
[
 'backbone',
 'underscore',
 'jquery'
],
function( Backbone, _, $ ){
	
	Backbone.SubModel = Backbone.Model.extend(
	//instance properties
	{
		name : "SubModel",
		
		parentModel: null,
		
		
		//==============================================================================
		subSet : function ( fullPath, value ){
	    	var self = this;
	    	
	    	var paths = Backbone.SubModel.pathArray( fullPath );
	    	
	    	var current = self;
	    	for ( var i = 0; i <paths.length ; i++ ){
	    		var path = paths[i];
	    		
	    		//Index
	    		if ( _.isNumber( path ) && ( current instanceof Backbone.Collection ) ){
	    			
	    			var item = current.at( parseInt(path) );
					if ( _.isUndefined( item ) ){
						//the model does not exist and must be initialized before setting
						var ModelClazz = current.model;
						if ( !_.isFunction(ModelClazz) ){
							continue;
						}
						item = new ModelClazz();
						item.on({
							"change" : self.subModelChanged,
							"destroy": self.subModelDestroyed 
						}, item);
						item.collectionItem = true;
						item.collection = current;
						item.parentModel = current.parentModel;
						
						current.add( item );
					}
					current = item;
	    		}
	    		//String
	    		else {
	    			//Does the string correspond to a nested model or collection?
	    			var spec = current.getSpec ( path );
	        		
	        		if ( !_.isNull(spec) ){
	        			//nested model
	        			var nested = current[ path ];
	        			
	        			if ( _.isUndefined(nested) ){
	        				current.forward( path, spec );
	        			}
	        			current = current[ path ];
	        		}
	        		else {
	        			//set the attribute !
	        			current.set( path , value );
	        		}
	    		}
	    		
	    		
	    	}
	    },
	    
	    subUnset : function ( fullPath ){
	    	var self = this;
	    	
	    	var paths = Backbone.SubModel.pathArray( fullPath );
	    	
	    	var current = self;
	    	
	    	for ( var i = 0; i < paths.length ; i++ ){
	    		var path = paths[i];
	    		
	    		//Index
	    		if ( _.isNumber( path ) && ( current instanceof Backbone.Collection ) ){
	    			var item = current.at( parseInt(path) );
					if ( _.isUndefined( item ) ){
						return self;
					}
					current = item;
	    		}
	    		//String
	    		else {
	    			//Does the string correspond to a nested model or collection?
	    			var spec = current.getSpec ( path );
	        		
	        		if ( !_.isNull(spec) ){
	        			//nested model
	        			var nested = current[ path ];
	        			
	        			if ( _.isUndefined(nested) ){
	        				return self;
	        			}
	        			current = current[ path ];
	        		}
	        		else {
	        			//set the attribute !
	        			current.unset( path );
	        			return self;
	        		}
	    		}
	    	}
	    	//Check if current is an Item within a Collection, so to remove it
	    	var isCollectionItem = current.collectionItem;
	    	var collection = current.collection;
	    	if ( !_.isUndefined(isCollectionItem) 
	    			&& _.isBoolean(isCollectionItem) 
	    				&& isCollectionItem === true 
	    					&& !_.isUndefined(collection)){
	    		if ( current instanceof Backbone.Model )
	    			current.close();
	    		current.collection.remove( current );
	    	}
	    	//If it is a simple object, delete it
	    	else {
	    		if ( current instanceof Backbone.Model )
	    			current.close();
	    		delete current;
	    	}
	    },
	    
	    subGet : function ( fullPath ){
	    	var self = this;
	    	
	    	var paths = Backbone.SubModel.pathArray( fullPath );
	    	
	    	var current = self;
	    	for ( var i = 0; i <paths.length ; i++ ){
	    		var path = paths[i];
	    		
	    		//Index
	    		if ( _.isNumber( path ) && ( current instanceof Backbone.Collection ) ){
	    			current = current.at( parseInt(path) );
		   		}
	    		else {
	    			if ( _.isUndefined(current) ){
	        			return undefined;
	        		}
	    			
	    			var nested = current[ path ];
	        			
	        		if ( !_.isUndefined(nested) ){
	        			current = nested;
	        		}
	        		else {
	        			current = current.get( path );
	        		}
	    		}
	    		
	    	}
	    	return current;
	    },
	    
	    //----------------------------------------------------------------------
	    forward : function( attribute, spec ){
	    	var self = this;
	    	
	    	if ( !_.isObject( spec ) )
				return self;
			
			var ModelClazz = spec.model ;
			if ( !_.isFunction(ModelClazz) )
				return self;
			
			var type = ( _.isString(spec.type) ) ? spec.type : Backbone.SubModel.MODEL_KEY; 
			
			var subModel = self[attribute];
			
			if ( _.isUndefined(subModel ) || _.isNull( subModel ) ){
				
				switch ( type ){
					case Backbone.SubModel.COLLECTION_KEY:{
						
						subModel = new Backbone.Collection([], {
							model: ModelClazz
						});
						subModel.parentModel = self;
						subModel.name = attribute;
						
						//Whenever something changes to the sub-collection, fire a suitable event on the parent
						subModel.on({
							"add"   : self.subCollectionAdded,
							"remove": self.subCollectionRemoved,
							"reset" : self.subCollectionReset,
							"sort"  : self.subCollectionSorted
						}, subModel);
						break;
					}
					default :{
						subModel = new ModelClazz(); 
						subModel.parentModel = self;
						subModel.name = attribute;
						
						//Whenever something changes to the sub-model, fire a suitable event on the parent
						subModel.on({
							"change" : self.subModelChanged,
							"destroy": self.subModelDestroyed 
						}, subModel);
						
						break;
					}
				}
				self[attribute] = subModel;
			}
			
			return self;		
		},
		//----------------------------------------------------------------------
		//-----S U B  O B J E C T --- E V E N T -----B I N D I N G S -----------
		//----------------------------------------------------------------------
		captureAll: function( eventName, args ){
			var current = null;
			
			if ( _.isArray(args) ){
				current = args.length > 2 ? args[1] : args[0];
			} else if ( _.isObject(args) ){
				current = args;
			}
//			console.log("==ALL=="
//						+"\nCurrent Model: '"+this.name+"'"
//						+"'\nEvent  Name:'"+eventName
//						+"'\nEvent Model: '"+(_.isNull(current) ? "[no-model]" :current.name+"'") );
		},
		//----------------------------------------------------------------------
	    subModelChanged : function( model, options ){
	    	var self = this;
	    	var parent = self.parentModel;
//	    	console.log(
//	    			"==CHANGE=="
//	    			+"\nCapture 'change' on model: '"+self.name+"' ['"+model.name+"']"
//					+"\nTrigger on '"+ (_.isNull( parent ) ? "[null-parent]" : parent.name)+"'"
//					+" change:"+self.name
//	    		);
			if ( !_.isNull( parent ) ){
//				parent.trigger("change:"+self.name, [self,options], parent );
				parent.trigger("change", [self,options], parent );
			}
	    },
	    //----------------------------------------------------------------------
	    subModelDestroyed : function( model, options ){
			if ( !_.isNull( model.parentModel ) ){
				model.parentModel.trigger("destroy:"+model.name, [model,options] );
			}
	    },
	    //----------------------------------------------------------------------
	    //----------------------------------------------------------------------
	    subCollectionAdded : function( collection, options ){
			if ( !_.isNull( collection.parentModel ) ){
				collection.parentModel.trigger("add:"+collection.name, [collection, options]);
			}
	    },
	   //----------------------------------------------------------------------
	    subCollectionRemoved : function( collection, options ){
			if ( !_.isNull( collection.parentModel ) ){
				collection.parentModel.trigger("remove:"+collection.name, [collection, options]);
			}
	    },
	    //----------------------------------------------------------------------
	    subCollectionReset : function( collection, options ){
			if ( !_.isNull( collection.parentModel ) ){
				collection.parentModel.trigger("reset:"+collection.name, [collection, options]);
			}
	    },
	    //----------------------------------------------------------------------
	    subCollectionSorted : function( collection, options ){
			if ( !_.isNull( collection.parentModel ) ){
				collection.parentModel.trigger("sort:"+collection.name, [collection, options]);
			}
	    },
	    //----------------------------------------------------------------------
		//----------------------------------------------------------------------
	    getSpec :  function( field ){
	    	var self = this;
	    	
	    	if ( !_.isObject(self.fields) )
	    		return null;
	    		
	    	var spec = self.fields[field];
	    		
	    	if ( _.isUndefined( spec) )
	    		return null;
	    	
	    	return spec;

	    }
	    
	},
	//class properties
	{
		COLLECTION_KEY : "collection",
		MODEL_KEY : "model",
		ATTRIBUTE_KEY : "attribute",
		
		//----------------------------------------------------------------------
	    pathArray :  function( fullPath ){
			var path;

		    if (_.isString(fullPath)){
		    	path = (fullPath === "") ? [''] : fullPath.match(/[^\.\[\]]+/g);
		        path = _.map(path, function( val, idx, list ){
		        	if ( val.match(/^\d+$/) ){
		              return parseInt(val, 10);
		            } else {
		              return val;
		            }
		        });
		    } 
		    else {
		        path = fullPath;
		    }
		
		    return path;
	    }
	   
	});
	
	return {};
	
}
);