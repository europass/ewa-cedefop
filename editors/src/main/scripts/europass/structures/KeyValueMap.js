define(
        ['jquery', 'europass/structures/ResourcesTransliterationMap', 'underscore']
        , function ($, ResourcesTransliterationMap, _) {
            var KeyValueMap = function () {
                this.keys = new Array();
                this.values = new Array();
                this.arrayObj = new Array();
                this.transliterationMap = new ResourcesTransliterationMap();
//			this.select2Map = new Array();
            };
            //Public
            KeyValueMap.prototype.put = function (v, k) {
                this.keys.push(k);
                this.values.push(v);
                var keyJson = {
                    key: k,
                    value: v
                };
                this.arrayObj.push(keyJson);
//			this.select2Map.push( idJson );
            };
            KeyValueMap.prototype.get = function (k) {
                var idx = $.inArray(k, this.keys);
                if (idx !== -1) {
                    return this.values[idx];
                }
                return null;
            };
            KeyValueMap.prototype.update = function (k, newValue) {
                var idx = $.inArray(k, this.keys);
                if (idx !== -1) {
                    this.values[idx] = newValue;
                    return true;
                }
                return false;
            };

            KeyValueMap.prototype.containsKey = function (k) {
                var contains = false;
                var idx = $.inArray(k, this.keys);
                if (idx !== -1) {
                    contains = true;
                }
                return contains;
            };

            KeyValueMap.prototype.reverseGet = function (v) {
                var idx = $.inArray(v, this.values);
                if (idx !== -1) {
                    return this.keys[idx];
                }
                return null;
            };
            /**
             * applies custom function (callbackFnk) to a value (v)
             * applies same custom function to the values of the this.map
             * if v=map.value then returns the corresponding map key else returns null
             */
            KeyValueMap.prototype.reverseGetByFunction = function (callbackFnk, v) {
                var result = null;
                var isFunc = $.isFunction(callbackFnk);
                if (isFunc) {
                    var str = callbackFnk.call(v);//convert in value
                    //convert values in the array
                    $.map(this.arrayObj, function (e, i) {
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
            KeyValueMap.prototype.toArray = function () {
                return this.arrayObj;
            };
            KeyValueMap.prototype.sortByOrder = function (order) {
                var orderBy = (_.isUndefined(order) || _.isNull(order)) ? "asc" : order;
                this.transliterationMap.sortMap(this, orderBy);
            };
            return KeyValueMap;
        }
);