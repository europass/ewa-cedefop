define(
        [
            'jquery',
            'underscore',
            'backbone',
            'Utils',
            'views/compose/ComposeView',
            'views/compose/esp/EspAttachmentsComposeView',
            'hbs!templates/compose/esp/maincompose',
            'ModalFormInteractions',
            'i18n!localization/nls/GuiLabel', 'europass/TabletInteractionsView'
        ],
        function ($, _, Backbone, Utils, ComposeView, EspAttachmentsComposeView, HtmlTemplate, ModalFormInteractions, GuiLabel, TabletInteractionsView) {

            var EspComposeView = ComposeView.extend({

                htmlTemplate: HtmlTemplate

                , tagName: "section"

                , tablets: {}

                , className: "navigation-area wysiwyg Compose"

                , events: _.extend({
                    "click 		.opens-modal-form": "openModalForm", //Sections-Add

                    "click :button.delete:not(.photo):not(.attachment)": "deleteSection", //Button-Delete

                    "click :button:not(.inactive).sort-move-up": "sortMoveUp",
                    "click :button:not(.inactive).sort-move-down": "sortMoveDown"
                }, ComposeView.prototype.events)

                , onInit: function (options) {

                    this.navigation = options.navigation;

                    this.sections = [];

                    this.model.bind("model:prefs:data:format:changed", this.dateFormatChanged, this);

                    //Re-render when emptying Photo + Attachments
                    this.model.bind("model:binaries:reset", this.onReRendering, this);
                    //Re-render when emptying ESP
                    this.model.bind("model:content:reset", this.onReRendering, this);
                    //Re-render when uploading an ESP
                    this.model.bind("model:uploaded:esp", this.onReRendering, this);
                    //Re-render when uploading from cloud
                    this.model.bind("model:uploaded:cloud", this.onReRendering, this);
                    //Re-render when loading from cloud services (e.g OneDrive/ Google Drive etc)
                    this.model.bind("model:loaded:cloud:document", this.onReRendering, this);

                    //TODO make this content area this.$el?
                    $("#main-content-area").trigger("europass:dragdrop:init");

                    ComposeView.prototype.onInit.apply(this, [options]);
                    if (this.isTablet) {
                        TabletInteractionsView.startListeners()
                    }
                }

                , onClose: function () {
                    ComposeView.prototype.onClose.apply(this);

                    $(this.sections).each(function (idx, section) {
                        if (_.isObject(section) && _.isFunction(section.close))
                            section.close();
                    });
                    this.sections = [];

                    this.model.unbind("model:binaries:reset", this.onReRendering);
                    this.model.unbind("model:content:reset", this.onReRendering);
                    this.model.unbind("model:uploaded:esp", this.onReRendering);
                    this.model.unbind("model:uploaded:cloud", this.onReRendering, this);
                    this.model.unbind("model:loaded:cloud:document", this.onReRendering, this);

                    $("#main-content-area").trigger("europass:dragdrop:close");
                }

                //@Override
                , render: function (args) {
                    ComposeView.prototype.render.apply(this, [args]);

                    if ($.isArray(this.sections) && this.sections.length === 0) {
                        this.onRender();
                    }
                }

                , onRender: function () {
                    this.sections = [];
                    //Attachments 
                    this.espAttachmentsComposeView = new EspAttachmentsComposeView({
                        el: '#Compose\\:Esp\\.Attachment',
                        model: this.model,
                        section: "SkillsPassport.LearnerInfo.ReferenceTo",
                        initialRendering: true
                    });
                    this.sections.push(this.espAttachmentsComposeView);
                    this.applyWhenMissingDocuments(this.espAttachmentsComposeView);

                    //This will open the uploda model if requested so by the URL
                    if (_.isFunction(this.openModals)) {
                        this.openModals(this.options);
                    }
                }
                , deleteSection: function (event) {
                    // TODO CLEAN UP COMMENT On mobile not using 2 step tooltip anymore !!
                    // if ((this.isTablet && _.isFunction(this.tablets.handleButton)) && this.tablets.handleButton(event) === false) {
                    // return false;
                    // } else {
                    // ModalFormInteractions.confirmDeleteSection(event);
                    // }
                    ModalFormInteractions.confirmDeleteSection(event);
                }
                , openModalForm: function (event) {

                    var isSortablePlaceholder = $(event.target).is("span.sortable_placeholder");

                    if (this.isTablet) {
                        if (isSortablePlaceholder) {
                            return false;
                        }
                        this.tablets.handleModalForm(event, ModalFormInteractions);
                    } else {
                        ModalFormInteractions.openForm(event, Utils.EditorForms.espFormsLoaded);
                        Utils.EditorForms.espFormsLoaded = true;
                    }
                }
                , onReRendering: function (origin) {
                    $(this.sections).each(function (idx, section) {
                        if (_.isObject(section) && _.isFunction(section.close))
                            section.close();
                    });
                    this.sections = [];
                    this.reRender(this.section, origin);
                }
                /**
                 * @Override
                 */
                , reRenderAttachments: function () {
                    this.onReRendering();
                }

                , enableFunctionalities: function (model) {
                    this.displayNewEuropassPortalNotification(GuiLabel["new.europass.portal.info"]);
                    this.displayDocumentNotification(GuiLabel["ecv.download.document.hint"]);
                    this.displayMissingDocumentNotification(GuiLabel["uploaded.documents.not.available.text.info"]);
                }
                , sortMoveUp: function (event) {
                    this.$el.trigger("europass:sort:list:moveUp", [event.target]);
                }
                , sortMoveDown: function (event) {
                    this.$el.trigger("europass:sort:list:moveDown", [event.target]);
                }

                , applyWhenMissingDocuments: function (_that) {
                    var _that = this;
                    this.espAttachmentsComposeView.$el.find('.attachment-esp-photo').on('error', function (evt) {
                        _that.displayDefaultViewWhenMissingDocuments(this);
                    });

                    var elementsForDefaultView = this.espAttachmentsComposeView.$el.find('.pdf-default-attachment-icon');
                    for (var i = 0; i < elementsForDefaultView.length; i++) {

                        var elemForView = elementsForDefaultView[i];
                        var sourceUrl = $(elemForView).data('temp-uri');
                        var elem = $(elemForView).find('.attachment-esp-photo');

                        $.ajax({
                            url: sourceUrl,
                            type: "get",
                            async: false,
                            error: function () {
                                _that.displayDefaultViewWhenMissingDocuments(elem[0]);
                            }
                        });
                    }
                }
            });
            return EspComposeView;
        });