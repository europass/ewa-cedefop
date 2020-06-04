define(
        ['backbone', 'backbonenested'],
        function (Backbone, BackboneNested) {

            Backbone.NestedModel.prototype.close = function () {
                //Call on close to each instance
                if (this.onClose) {
                    this.onClose();
                }
            };
        }
);