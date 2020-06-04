define(
[
 'manager/Bundles',
 'jquery',
 'underscore'
],
function( Self, $, _ ){
	if ( Self === undefined || Self === null ){
		//Constructor
		var Bundles = function(){};
		
		
		//Methods
		Bundles.prototype.load = function( bundleConfigArray ){
			if ( _.isUndefined( bundleConfigArray ) || _.isNull( bundleConfigArray )){
				return;
			}
			var configs = undefined;
			if ( !_.isArray( bundleConfigArray ) ){
				configs = []; 
				configs.push ( bundleConfigArray );
			} else {
				configs = bundleConfigArray;
			}
			for ( var i = 0; i < configs.length ; i++ ){
				var config = this.parse(configs[i]);
				if ( _.isEqual(config, {} ) )
					continue;
				var bundleName = config.bundleName;
				this.loadOne( bundleName, config.bundle );
			}
			return this;
		};
		
		Bundles.prototype.loadOne = function( bundleName, bundle ){
			if ( _.isUndefined(this[ bundleName ]) ){
				this[ bundleName ] = bundle;
			}
			return this[ bundleName ];
		};
		
		Bundles.prototype.parse = function( config ){
			if ( !_.isObject( config ) ){
				return {};
			}
			for ( var bundleName in config ) break;
			return {
				bundleName : bundleName,
				bundle : config[bundleName]
			};
		};
		
		Bundles.prototype.dynamic = function () {
			
//			var t = "Hello, my name is [[name.first]] [[name.surname]] -[[name.surname]]-";
//
//			var l = {};
//			l["name"] = "SOS";
//			l["name.first"] = "John";
//			l["name.surname"] = "Doe";
//
//			var find = "/[(.*)/]"
//			var re = new RegExp(/\[\[([^\]]*)\]\]/g);
//
//			var m = t.match(re);
//			console.log( m );
//
//			var r = t.replace(re, function( group ){
//			    var g = group.replace("[[","").replace("]]","");
//			    return l[g];
//			});
//			console.log( r );
			
		};
		//Instance
		var Self = new Bundles();
	}
	
	return Self;
});