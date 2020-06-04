define(
        ['jquery'],
        function ($) {
            var KeyObjMap = function () {
                this.keys = new Array();
                this.objs = new Array();
            };
            //Public
            KeyObjMap.prototype.put = function (k, obj) {
                //console.log("PUT KEY : " + k );
                this.keys.push(k);
                this.objs.push(obj);
            };
            KeyObjMap.prototype.get = function (k) {
                //console.log("GET KEY : " + k );
                //var idx = this.keys.indexOf(k);
                var idx = $.inArray(k, this.keys);//fix for i.e 8
                if (idx !== -1)
                    return this.objs[idx];
                return null;
            };
            return KeyObjMap;
        }
);
