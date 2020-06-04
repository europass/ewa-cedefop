define(
        ['jquery', 'underscore'],
        function ($, _) {
            var mapWrapper = function (map, excludedItems) {
                this.originalMap = map;
                this.map = {};
                $.extend(true, this.map, map);

                if ($.isArray(excludedItems) && excludedItems.length > 0) {
                    for (var x in excludedItems) {

                        var item = excludedItems[x];
                        if (item === undefined || item === null || !_.isObject(item)) {
                            continue;
                        }
                        var key = null;
                        if (!_.isEmpty(item.Description) && _.isObject(item.Description)) {
                            key = item.Description.Code;
                        }
                        if (key === undefined || key === null) {
                            key = item.Code;
                        }
                        if (key !== undefined && key !== null
                                && _.isFunction(this.map.containsKey)
                                && this.map.containsKey(key)) {
                            this.remove(key);
                        }
                    }
                }
            };
            /**
             * Implement all methods of mapWrapper
             * @param key
             * @returns {Boolean}
             */
            mapWrapper.prototype.put = function (v, k) {
                if (!_.isFunction(this.map.containsKey)) {
                    return false;
                }
                if (this.map.containsKey(k)) {
                    return false;
                }
                this.map.keys.push(k);
                this.map.values.push(v);
                var keyJson = {
                    key: k,
                    value: v
                };
                this.map.arrayObj.push(keyJson);
                return true;
            };

            mapWrapper.prototype.get = function (k) {
                var idx = $.inArray(k, this.originalMap.keys);
                if (idx !== -1) {
                    return this.originalMap.values[idx];
                }
                return null;
            };

            mapWrapper.prototype.getKey = function (v) {
                var idx = $.inArray(v, this.originalMap.values);
                if (idx !== -1) {
                    return this.originalMap.keys[idx];
                }
                return null;
            };

            mapWrapper.prototype.update = function (k, newValue) {
                var idx = $.inArray(k, this.map.keys);
                if (idx !== -1) {
                    this.map.values[idx] = newValue;
                    return true;
                }
                return false;
            };

            mapWrapper.prototype.containsKey = function (k) {
                var contains = false;
                var idx = $.inArray(k, this.map.keys);
                if (idx !== -1) {
                    contains = true;
                }
                return contains;
            };

            mapWrapper.prototype.reverseGet = function (v) {
                var idx = $.inArray(v, this.map.keys);
                if (idx !== -1)
                    return this.map.keys[idx];
            };

            mapWrapper.prototype.reverseGetKey = function (v) {
                var idx = $.inArray(v, this.map.values);
                if (idx !== -1)
                    return this.map.keys[idx];
            };

            /**
             * applies custom function (callbackFnk) to a value (v)
             * applies same custom function to the values of the map
             * if v=map.value then returns the corresponding map key else returns null
             */
            mapWrapper.prototype.reverseGetByFunction = function (callbackFnk, v) {
                var result = null;
                var isFunc = $.isFunction(callbackFnk);
                if (isFunc) {
                    var str = callbackFnk.call(v);//convert in value
                    //convert values in the array
                    $.map(this.originalMap.arrayObj, function (e, i) {
                        var mapVal = callbackFnk.call(e.value);
                        if (mapVal !== undefined && str !== undefined &&
                                mapVal !== null && str !== null &&
                                mapVal === str) {
                            result = e.key;
                            return false; //exits loop (Not The Function)
                        }
                    });
                } else {
                    return null;
                }
                return result;//Please Don't Remove
            };

            mapWrapper.prototype.toArray = function () {
                return this.map.arrayObj;
            };

            //remove function
            mapWrapper.prototype.remove = function (key) {
                if (key === null) {
                    return false;
                }
                if (!_.isFunction(this.map.containsKey)) {
                    return false;
                }
                if (!this.map.containsKey(key)) {
                    return false;
                }
                var idx = $.inArray(key, this.map.keys);
                if (idx !== -1) {
                    this.map.keys.splice(idx, 1);
                    this.map.values.splice(idx, 1);
                }
                for (var i = 0; i < this.map.arrayObj.length; i++) {
                    var arrObj = this.map.arrayObj[i];
                    if (arrObj === undefined || arrObj === null) {
                        continue;
                    }
                    if (arrObj.key === key) {
                        this.map.arrayObj.splice(i, 1);
                    }
                }
                /*		for(var i=0;i<this.map.select2Map.length;i++){
                 var selectObj = this.map.select2Map[i];
                 if ( selectObj === undefined || selectObj === null ){
                 continue;
                 }
                 if( selectObj.id == key){
                 this.map.select2Map.splice(i,1);
                 }
                 }*/
                return true;
            };

            mapWrapper.prototype.restore = function (idJson) {
                this.map.put(idJson.text, idJson.id);
            };

            /**
             * Implements the select2Map method of KeyValueMap
             * @returns
             */
//	mapWrapper.prototype.toSelect2Map = function() {
//		return this.map.toSelect2Map();	
//	};

            mapWrapper.prototype.getMap = function () {
                return this.map;
            };

            return mapWrapper;
        });