/**
 * 
 */
define(
        [
            'jquery',
            'backbone',
            'europass/GlobalDocumentInstance',
            'i18n!localization/nls/GuiLabel'
        ],
        function ($, Backbone, GlobalDocument, GuiLabel) {

            var CurrentDocumentView = Backbone.View.extend({

                onClose: function () {
                    this.navigationModel.unbind("model:navigation:changed", $.proxy(this.navigateView, this));
                },

                initialize: function (options) {
                    this.navigationModel = options.navigationModel;

                    this.navigationModel.bind("model:navigation:changed", $.proxy(this.navigateView, this));

                    this.render();
                },
                navigateView: function (view) {
                    var currentPageInfo = this.navigationModel.analyze(view);
                    GlobalDocument.set(currentPageInfo);
                    this.render();
                },
                render: function () {
                    var doc = GlobalDocument.get().document;
                    if ("ECV" === doc)
                        doc = "CV";

                    var key = "Navigation.Header.SkillsPassport." + doc;

                    var title = GuiLabel[ key ] || "GuiLabel[ " + key + " ]";

                    this.$el.html(title);
                    document.title = GuiLabel["EWA.Moto"] + ": " + title;

                    var element = $('meta[name="description"]');
                    element.remove();

                    var meta = document.createElement('meta');
                    meta.name = 'description';

                    if (doc === 'CV') {
                        meta.content = GuiLabel["EWA.Moto.CV.Description"];
                    } else if (doc === 'ESP') {
                        meta.content = GuiLabel["EWA.Moto.ESP.Description"];
                    } else if (doc === 'ELP') {
                        meta.content = GuiLabel["EWA.Moto.ELP.Description"];
                    } else if (doc === 'ECL') {
                        meta.content = GuiLabel["EWA.Moto.ECL.Description"];
                    }

                    document.head.appendChild(meta);

                }
            });

            return CurrentDocumentView;
        }
);