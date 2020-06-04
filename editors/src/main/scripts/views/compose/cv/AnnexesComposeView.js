define(
        [
            'jquery',
            'underscore',
            'Utils',
//		'jqueryui',
            'views/interaction/ListView',
            'views/compose/ComposeView',
            'hbs!templates/compose/cv/annexes'
//		,'i18n!localization/nls/Notification'//'europass/TabletInteractionsView'
        ],
        function ($, _, Utils, ListView, ComposeView, HtmlTemplate/*, Notification*/) {//, TabletInteractionsView, jqueryui, 

            var AnnexesComposeView = ComposeView.extend({

                htmlTemplate: HtmlTemplate

                , sortableAxis: "y"

                , events: _.extend({
                    "click 		:button.delete.attachment": "handleDeleteAttachment",
                    // "touchstart :button.delete.attachment": "handleDeleteAttachment",
                    "touchstart a.ReferenceTo.view": "handleAttachmentPreview",

                    "click		span.sortable_placeholder": "sortSection", //Section sorting  behavior, sortable placeholder, auto sort and sort move buttons (up, down and top)
                    //"touchstart span.sortable_placeholder": "sortSection",

                    "click :button:not(.inactive).sort-move-up": "sortMoveUp",
                    "click :button:not(.inactive).sort-move-down": "sortMoveDown",
                    "click :button.sort-move-top": "sortMoveTop"
                }, ComposeView.prototype.events)

                , enableFunctionalities: function (model) {

                    ComposeView.prototype.enableFunctionalities.apply(this, [model]);

                    var that = this;
                    //List View for all section > compose-list
                    this.$el.find(".sortable.compose-list").each(function (idx, el) {
                        var list = $(el);
                        //Sortable when the list contains more than 1 item.
                        if (list.find("> li.list-item").length > 1) {
                            var listView = new ListView({
                                el: list,
                                model: model,
                                sortableAxis: that.sortableAxis,
                                sortableConfig: that.sortableConfig
                            });
                            that.addToViewsIndex(listView);
                        }
                    });
                }
                , handleDeleteAttachment: function (event) {

                    // TODO CLEAN UP COMMENT On mobile not using 2 step tooltip anymore !!
                    // if ((this.isTablet && _.isFunction(this.tablets.handleAttachment)) && this.tablets.handleAttachment(event) === false) {
                    // 	return false;
                    // } else {
                    // 	this.doDeleteAttachment(event);
                    // }
                    this.doDeleteAttachment(event);
                }
                , doDeleteAttachment: function (event) {
                    var btn = $(event.target);
                    var section = btn.attr("data-rel-section");
                    var annex = btn.closest(".list-item");
                    var relatedView = btn.attr("data-rel-view");

                    btn.trigger("europass:delete:requested", [relatedView, section]);
                    //Fix for the scrolling effect of main area when deleting an attachment (EWA-1512)
                    //The new wysiwyg height is caclulated by the old wysiwyg height - list-item's height
                    var main = annex.closest(".main-area");
                    var mainPos = main.scrollTop();
                    var compose = annex.closest(".wysiwyg.Compose");
                    if (main !== undefined && main.length > 0 && compose !== undefined && compose.length > 0 && annex !== undefined && annex.length > 0) {
                        //Fix for the jumping-area-effect when deleting an attachment and the height is re-rendered
                        compose.height(compose.height() - 88).promise().done(function () {
                            //After preserving the compose area's height, the style of this specific height must be reset
                            //This is necessary for not getting a long blank area under the editor, after erasing a big document.
                            //The compose area's height is reset 0.5 second after the user clicks the final confirmation for deleting an attachment
                            $(".delete-item.important-confirmation .confirm-submit.delete-model.button").on("click", function (event) {
                                setTimeout(function () {
                                    //Reset the height of wysiwyg
                                    compose.css({"height": ""});
                                    //Maintain the main-area's scroll position
                                    main.scrollTop(mainPos + 88);
                                }, 500);
                            });
                        });
                    }
                }
                /**
                 * On confirmation of delete we need to run 
                 * proceedDeletion: function( event, section )
                 * as per the ComposeView
                 *  @Override
                 */
                , proceedDeletion: function (event, section) {
                    //start the waiting indicator...
                    this.$el.trigger("europass:waiting:indicator:show");

                    var messageContainer = this.$el.closest("body").find("section.notifications");

                    event.stopPropagation();

                    var remove = null;
                    var documentationInfo = _.isFunction(this.model.documentation) ? this.model.documentation() : null;
                    if (documentationInfo !== null) {
                        remove = documentationInfo.removeAttachment(section);
                    }

                    if (remove === false) {
                        require(
                                ['i18n!localization/nls/Notification'],
                                function (Notification) {
                                    messageContainer.trigger("europass:message:clear");
                                    var errorMsg = "<em>" + Notification["error.code.file.general"] + "</em>";
                                    messageContainer.trigger("europass:message:show", ["error", errorMsg]);
                                }
                        );
                    }
                }
                , sortSection: function (event) {
                    //console.log("sort();");
                    if ((this.isTablet && _.isFunction(this.tablets.handleButton)) && this.tablets.handleButton(event) === false) {
                        return false;
                    }
                }
                /**
                 * @Override
                 * Re-render not only when the relSection equals this section, 
                 * but also when the relSection equals "Attachment" 
                 */
                , reRender: function (relSection, origin) {
                    //First check if the related section updated is an indexed section
                    var listSection = Utils.getListSection(relSection);
                    if (listSection !== null) {
                        relSection = listSection;
                    }

                    if (relSection === this.section || relSection === this.model.ATTACHMENT_SECTION_MIN) {
                        if (relSection === 'SkillsPassport.LearnerInfo.ReferenceTo') {
                            if (arguments !== null && arguments !== undefined && arguments !== '') {
                                if (arguments.length > 0) {
                                    //get argument for sort target position
                                    var targetListItem = arguments[2];
                                }
                            }
                        }
                        this.render(this.reRenderIndicator, [this.doTransition(origin), targetListItem]);
                    }
                }
                /* handleAttachmentPreview, emulates hover effect on tablets before previewing image
                 * */
                , handleAttachmentPreview: function (event) {
                    if ((this.isTablet && _.isFunction(this.tablets.handleAttachment)) && this.tablets.handleAttachment(event) === false) {
                        return false;
                    }
                }
                , sortMoveUp: function (event) {
                    this.$el.trigger("europass:sort:list:moveUp", [event.target]);
                }
                , sortMoveDown: function (event) {
                    this.$el.trigger("europass:sort:list:moveDown", [event.target]);
                }
                , sortMoveTop: function (event) {
                    this.$el.trigger("europass:sort:list:moveTop", [event.target]);
                }
            });

            return AnnexesComposeView;
        }
);