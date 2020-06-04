define(
        [
            'jquery',
            'underscore',
            'views/compose/ComposeView',
            'views/prefs/PrintingPreferencesView'
//		'templates/helpers/get_current_date'
        ],
        function ($, _, ComposeView, PrintingPreferencesView) {//, get_current_date 

            var SubjectLineLocalisationComposeView = ComposeView.extend({

                htmlTemplate: "compose/cl/localisationAndsubject"

                , prefsView: null

                , onInit: function (options) {
                    this.model.bind("model:prefs:data:format:changed", this.dateFormatChanged, this);

                    this.prefsView = new PrintingPreferencesView({
                        model: this.model
                    });

                    ComposeView.prototype.onInit.apply(this, [options]);
                }
                , onClose: function () {
                    ComposeView.prototype.onClose.apply(this);
                    if (_.isObject(this.prefsView) && _.isFunction(this.prefsView.close))
                        this.prefsView.close();
                    delete this.prefsView;
                    this.model.unbind("model:prefs:data:format:changed", this.dateFormatChanged);
                }

                /**
                 * Re-render only if the  date format change event was initiated by a view other than the current.
                 */
                , dateFormatChanged: function (relSection) {
                    if (relSection.indexOf(this.section) === 0) {
                        return false;
                    } else {
                        this.reRender(this.section);
                    }
                }

                , applyTooltip: function (model) {
                    var modelInfo = _.isFunction(this.model.info) ? this.model.info() : null;
                    var sections = this.section.split(" ");
                    var empty = 1;
                    for (var i = 0; i < sections.length; i++) {
                        empty -= modelInfo.isCLSectionEmpty(sections[i]);
                    }
                    empty < 0 ?
                            this.$el.addClass("empty") :
                            this.$el.removeClass("empty");
                }

            });

            return SubjectLineLocalisationComposeView;
        }
);