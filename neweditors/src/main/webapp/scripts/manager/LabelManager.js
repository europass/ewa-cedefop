define(
[
],
function(){
	//Constructor
	var LabelManager = function( bundleName, bundle ){
		this[bundleName] = bundle;
		this.labels = this[bundleName];
	};
	//Properties
	
	
	//Methods
	LabelManager.prototype.todo = function(){
		
	};
	
	return LabelManager;
});