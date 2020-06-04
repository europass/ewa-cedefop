define(
        ['backbone'],
        function (Backbone) {
            /**
             * BEWARE OF ZOMBIES!!!!!!
             * TODO: Move to prototype Backbone.View
             * Proper clean up of Views
             * http://lostechies.com/derickbailey/2011/09/15/zombies-run-managing-page-transitions-in-backbone-apps/
             */
            Backbone.View.prototype.close = function () {
                //The call to `this.remove()` delegates to jQuery behind the scenes, by calling `$(this.el).remove()`. 
                //The effect of this is 2-fold. We get the HTML that is currently populated inside of `this.el` removed from the DOM 
                //(and therefore, removed from the visual portion of the application), and we also get all of the DOM element events cleaned up for us. 
                //This means that all of the events we have in the `events: { }` declaration of our view are cleaned up automatically!
                this.remove();
                //Secondly, the call to `this.unbind()` will unbind any events that our view triggers directly 
                //that is, anytime we may have called `this.trigger()` from within our view, in order to have our view raise an event.
                this.unbind();
                //The last thing we need to do, then, is unbind any model and collection events that our view is bound to.
                //To do this, though, we cannot use a generic close method on our base view.
                if (this.onClose) {
                    this.onClose();
                }
            };
        }
);