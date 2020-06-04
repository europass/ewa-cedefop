define(
        [
            'jquery', //'Utils',
            'underscore',
            'views/interaction/ListView',
            'views/compose/ComposeView',
            'hbs!templates/compose/elp/certificateList'
        ],
        function ($, _, ListView, ComposeView, HtmlTemplate) {

            var CertificateListComposeView = ComposeView.extend({

                htmlTemplate: HtmlTemplate

                , events: _.extend({
                    "click :button:not(.inactive).sort-move-up": "sortMoveUp",
                    "click :button:not(.inactive).sort-move-down": "sortMoveDown"
                }, ComposeView.prototype.events)

                , onInit: function (options) {
                    this.model.bind("model:prefs:data:format:changed", this.dateFormatChanged, this);

                    ComposeView.prototype.onInit.apply(this, [options]);
                }
                , onClose: function () {
                    ComposeView.prototype.onClose.apply(this);

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
                , enableFunctionalities: function (model) {

                    ComposeView.prototype.enableFunctionalities.apply(this, [model]);

                    var that = this;
                    //List View for all section > compose-list
                    this.$el.find("table.list-actions > tbody").each(function (idx, el) {
                        var list = $(el);
                        //Sortable when the list contains more than 1 item.
                        if (list.find("> tr.list-item").length > 1) {
                            var listView = new ListView({
                                el: list,
                                model: model
                            });
                            that.addToViewsIndex(listView);
                        }
                    });
                }

                , sortMoveUp: function (event) {
                    this.$el.trigger("europass:sort:list:moveUp", [event.target, true]);
                }
                , sortMoveDown: function (event) {
                    this.$el.trigger("europass:sort:list:moveDown", [event.target, true]);
                }
            });
            return CertificateListComposeView;
        }
);