define(
        ['views/compose/ComposeView'],
        function (ComposeView) {
            var LocalisationComposeView = ComposeView.extend({

                htmlTemplate: "compose/cl/localisation"

                , onInit: function (options) {

                    this.model.bind("model:content:changed", this.renderView, this);

                    ComposeView.prototype.onInit.apply(this, [options]);
                }

                , onClose: function () {
                    this.model.unbind("model:content:changed", this.renderView);
                    ComposeView.prototype.onClose.apply(this);
                }

                , renderView: function (relSection) {
                    var render = false;
                    var sections = relSection.split(" ");
                    for (var i = 0; i < sections.length; i++) {
                        if (this.section === sections[i]) {
                            render = true;
                        }
                    }
                    if (render)
                        this.render();
                }
            });
            return LocalisationComposeView;
        });