define(
        [
            'jquery',
            'jqueryui',
            'underscore',
            'views/forms/FormView',
            'hbs!templates/forms/formbuttons',
            'hbs!templates/attachment/attachmentedit',
            'hbs!templates/attachment/attachmentactions',
            'hbs!templates/attachment/listsections',
            'hbs!templates/attachment/attachmentrename',
            'views/attachment/AttachmentManagerInstance',
            'europass/structures/PreferencesSchema',
            'i18n!localization/nls/Notification', //'europass/TabletInteractionsView'
            'ModalFormInteractions'
        ],
        function ($, jqueryui, _, FormView,
                MenuButtonsTpl, HtmlTemplate, ActionsTpl, ListSectionsTpl, RenameTpl,
                AttachmentManager, PreferencesSchema, Notification, ModalFormInteractions) {//, TabletInteractionsView 

            var AttachmentEditFormView = FormView.extend({

                type: "Edit",

                withinAnnexes: true,

                htmlTemplate: HtmlTemplate,

                actionsTemplate: ActionsTpl,

                events: _.extend({
                    "europass:attachment:temp:model:changed ": "reRender",
                    "europass:photo:annex:changed ": "renderReset",

                    "click .option.linked.toggle-expansion": "requestLinkedSections",
                    "click .option.rename.toggle-expansion": "requestRenameAttachment",
                    "click .attachment-linked :button.reject": "rejectAction",

                    "click .attachment-rename :button.confirm": "renameAttachment",
                    "click .attachment-rename :button.reject": "rejectAction",
                    "blur .attachment-rename input": "rejectRename",
                    "keypress .attachment-rename :input": "keypressed",

                    "click .menu-prompt ": "toggleOptionsDisplay",
                    "click .attachment-info": "setTabletHoverEffect"
                }, FormView.prototype.events),

                dropZoneSelector: ".Upload.dropzone",

                messageContainerSelector: ".upload.feedback-area",

                updateableContainerSelector: "div.existing-file-details",

                renameContainerSelector: ".attachment-rename",

                sectionsContainerSelector: ".attachment-linked",

                linkedCls: "linked",

                unlinkedCls: "unlinked",

                applyLinking: function () {

                    var sections = this.$el.find(this.sectionsContainerSelector);
                    for (var i = 0; i < sections.length; i++) {
                        var section = $(sections[i]);
                        var attachmentId = (section.length > 0) ? section.attr("data-rel-id") : null;
                        if (attachmentId !== null) {
                            var referencesTo = this.model.documentation().findReferencesTo(attachmentId, true);
                            if (referencesTo.length > 0) {
                                var ahref = this.frm.find("a.attachment[data-rel-attachment=\"" + attachmentId + "\"]");
                                if (ahref.length > 0) {
                                    var li = ahref.closest('li.attachment-info');
                                    if (li.length > 0)
                                        li.addClass(this.linkedCls);
                                }
                            }
                        }
                    }
                    var that = this;
                    this.$el.find("li.attachment-info:not(." + this.linkedCls + ")").each(function (idx, el) {
                        var li = $(el);
                        li.addClass(that.unlinkedCls);
                    });
                },
                /**
                 * When the Backbone View gets destroyed
                 */
                onClose: function () {
                    AttachmentManager.throwaway();

                    FormView.prototype.onClose.call(this);

                    this.model.unbind("model:binaries:reset", this.clearTemp);
                },
                /**
                 * When session expires and photo/attachment need to be reset to the temp model
                 */
                clearTemp: function () {
                    if (this.$el.is(":visible") === false) {
                        return false;
                    }
                    //Clear temporary model
                    AttachmentManager.resetPhotoAttachment();

                    this.$el.trigger("europass:photo:annex:changed");
                },
                renderReset: function (event) {
                    this.reRender(event, this.section);
                },
                /**
                 * @Override
                 */
                onInit: function (options) {
                    //When the Photo/Attachment get reset (when session expires)
                    this.model.bind("model:binaries:reset", this.clearTemp, this);

                    FormView.prototype.onInit.apply(this, [options]);
                },
                /**
                 * Capture the keypress event in the input (of rename)
                 * and if enter 
                 */
                keypressed: function (event) {
                    var code = (event.keyCode ? event.keyCode : event.which);
                    if (code === 13) {
                        event.preventDefault();
                    }
                },
                /**
                 * Re-render the specific updatable part based on Attachment Manager's Temporary Model
                 */
                reRender: function (event, attachmentInfo) {
                    var that = this;
                    //Get the relevant model to re-render against it the template
                    var model = this.relevantModelSection(attachmentInfo);
                    //ATTENTION! this is to be used as a prefix for ids to distinguish the add annex from the edit annex
                    model["subsection"] = that.type;
                    var html = that.actionsTemplate(model);

                    var el = that.$el.find(that.updateableContainerSelector);
                    if (el.length > 0) {
                        var that2 = this;
                        el.fadeOut(500, function () {
                            el.html(html);
                            el.fadeIn(500);
                            that2.applyLinking();
                        });
                    }
                    that.$el.trigger("europass:attachment:form:rerender");
                },
                /**
                 * Capture the relevant part of the model to re-render
                 * When editing a specific Attachment the relevant part is this specific Attachment.
                 */
                relevantModelSection: function (attachmentInfo) {
                    var model = AttachmentManager.capture();
                    var attachment = attachmentInfo.match(/SkillsPassport\.Attachment\[\d+\]/);
                    if (attachment === null) {
                        return {};
                    }
                    return model.get(attachment[0]);
                },
                /**
                 * Unlink
                 */
                unLink: function (event) {
                    var btn = $(event.target);
                    var reference = btn.attr("data-rel");
                    var messageContainer = this.$el.find(this.messageContainerSelector);

                    var unlinked = AttachmentManager.unlink(reference);
                    if (unlinked) {
                        var li = btn.closest("li");
                        li.slideUp('slow', function () {
                            $(this).remove();
                        });

                        if (messageContainer !== undefined && messageContainer !== null && messageContainer.length > 0) {
                            require(
                                    ['i18n!localization/nls/Notification'],
                                    function (Notification) {
                                        var msg = "<em>" + Notification["success.attachment.unlink"] + "</em>";
                                        messageContainer.trigger("europass:message:show", ["success", msg, true]);
                                    }
                            );
                        }
                    } else {

                        if (messageContainer !== undefined && messageContainer !== null && messageContainer.length > 0) {
                            require(
                                    ['i18n!localization/nls/Notification'],
                                    function (Notification) {
                                        var errorMsg = "<em>" + Notification["error.attachment.link.general"] + "</em>";
                                        messageContainer.trigger("europass:message:show", ["error", errorMsg, false]);
                                    }
                            );
                        }
                    }
                },
                /**
                 * @Override
                 */
                enableFunctionalities: function () {

                    // Prepare the attachment manager in order to obtain a clone of the live model
                    AttachmentManager.prepare();

                    //Output the list of linked sections
                    this.prepareLinksContext();

                    this.applyLinking();

                    //Finally call parent
                    FormView.prototype.enableFunctionalities.call(this);
                },
                prepareLinksContext: function (sections) {

                    if (sections === undefined) {
                        sections = this.$el.find(this.sectionsContainerSelector);
                    }
                    for (var i = 0; i < sections.length; i++) {
                        var section = $(sections[i]);
                        var attachmentId = (section.length > 0) ? section.attr("data-rel-id") : null;

                        if (attachmentId !== null) {
                            var referencesTo = this.model.documentation().findReferencesTo(attachmentId, true);

                            var linkedSections = [];

                            if (referencesTo.length > 0) {

                                var that = this;
                                $(referencesTo).each(function (idx, referenceTo) {
                                    //We need to get the linked-section path from the current referenceTo
                                    //So we get the path up to where the ".ReferenceTo" begins
                                    var linkedSection = referenceTo.substring(0, referenceTo.indexOf(".ReferenceTo"));
                                    //the linkedSection path as key / the entire model / the ReferenceTo object corresponding to the linkedSection
                                    var value = PreferencesSchema.getSectionLabel(linkedSection, that.model.attributes.SkillsPassport, that.model.get(linkedSection));

                                    var json = {
                                        "Name": value,
                                        "Path": referenceTo
                                    };
                                    if (this.withinAnnexes === false) {
                                        json["controls"] = true;
                                    }

                                    linkedSections.push(json);

                                });
                            }
                            var context = {};
                            context["Section"] = linkedSections;
                            context["AttachmentId"] = attachmentId;
                            context["subsection"] = this.type;

                            var html = ListSectionsTpl(context);

                            section.html(html);
                        }
                    }
                },
                /**
                 * Request to view the names of the sections linked to this attachment
                 */
                requestLinkedSections: function (event) {
                    //if not empty...
                    var section = AttachmentManager.getRelatedSubForm(event);
                    if (section.html() === "") {
                        //Output the list of linked sections
                        this.prepareLinksContext(section);
                    }
                    this.hideOptions(event);
                },
                /**
                 * Request to show the template of renaming an attachment
                 */
                requestRenameAttachment: function (event) {
                    //if not empty...
                    var section = AttachmentManager.getRelatedSubForm(event);
                    if (section.html() === "") {
                        this.prepareRenameContext(section);
                    }
                    var input = section.find("input.new-name");
                    if (input.length >= 0) {
                        input.trigger("focus");
                    }

                    this.hideOptions(event);
                },
                /**
                 * Reject the previously selected action
                 */
                rejectAction: function (event) {
                    var section = AttachmentManager.getRelatedSubForm(event);

                    this.toggleInAction($(event.target));

                    section.fadeToggle("slow", function () {
                        var section = $(this);
                        var input = section.find("input:not(button)");
                        if (input.length >= 0) {
                            input.val(input.attr("data-prev-value") || "");
                        }
                        section.hide();
                    });
                },
                /**
                 * Confirm to rename an attachment.
                 * Attention! The button contains a <span> for the icon, so the event.target might be the span
                 */
                renameAttachment: function (event) {
                    var btn = $(event.currentTarget);
                    var section = AttachmentManager.getButtonRelatedSubForm(btn);
                    var attachment = AttachmentManager.getButtonRelatedSection(btn);
                    var name = section.find("input:not(button)").val();

                    if (attachment !== null && $.trim(name) !== "") {
                        this.toggleInAction(btn);
                        AttachmentManager.rename(attachment, name);
                        //Fire event to re-render the form with the Temporary Model of the Attachment Manager!
                        this.$el.trigger("europass:attachment:temp:model:changed", attachment);
                    } else {
                        this.rejectAction(event);
                    }
                },
                /**
                 * Prepare the template to rename the current attachment
                 */
                prepareRenameContext: function (section) {

                    var id = (section.length > 0) ? section.attr("data-rel-id") : null;

                    var model = AttachmentManager.capture();
                    var attachInfo = model.documentation().attachmentById(id);

                    var attachment = {};
                    var index = null;
                    if (id !== null && attachInfo !== null) {
                        //find Attachment based on this Id
                        attachment = attachInfo.attachment;
                        index = attachInfo.index;
                    }
                    var context = {};
                    context["Attachment"] = attachment;
                    context["att_index"] = index;
                    context["subsection"] = this.type;
                    var html = RenameTpl(context);
                    section.html(html);
                },
                /**
                 * On blur of the input field, reject the rename
                 */
                rejectRename: function (event) {
                    var input = $(event.target);

                    //simulate a cancel button click
                    var menu = input.siblings("menu");
                    if (menu.length === 0)
                        return;
                    var cancelBtn = menu.find("button.reject");
                    if (cancelBtn.length === 0)
                        return;
                    cancelBtn.trigger("click");
                },
                /**
                 * @Override
                 */
                submitted: function (event) {
                    //console.log(" === ATTACHMENT EDIT VIEW - SUBMIT ===");
                    //start the waiting indicator...
                    this.$el.trigger("europass:waiting:indicator:show");
                    AttachmentManager.saveAll();
                    this.closeModal();
                    //Trigger global re-render
                    this.model.trigger("linked:attachment:changed");
                },
                
                modalClosed: function (event) {
                    var hasChanged = false;
                    if (AttachmentManager.internalModel !== null && AttachmentManager.internalModel !== undefined && 
                            !_.isEqual(AttachmentManager.model.get(AttachmentManager.ATTACHMENTS), AttachmentManager.internalModel.get(AttachmentManager.ATTACHMENTS))) {
                        hasChanged = true;
                    }

                    if (hasChanged) {
                        ModalFormInteractions.confirmSaveSection(event, this.frm.attr("id"));
                    } else {
                        this.closeModal();
                    }
                },
                
                /**
                 * @Override
                 */
                appendMenuButtons: function () {
                    var formName = this.$el.find("form").attr("id");
                    var c = formName.substr(formName.lastIndexOf(".") + 1);
                    var className = c.substr(0, c.indexOf("["));
                    if (className === '') {
                        className = c;
                    }
                    var buttons = MenuButtonsTpl({
                        className: className,
                        formName: formName
                    });// Buttons Template
                    this.$el.find(".side").append(buttons);
                },
                /**
                 * Add/remove the "in-action" class which will keep the menu prompt visible
                 */
                toggleInAction: function (btn) {

                    var parent = btn.closest(".attachment-action");
                    if (parent.length === 0)
                        return;

                    var options = parent.siblings(".menu-options");
                    options.hide();

                    var prompt = options.siblings(".menu-prompt");
                    if (prompt.length === 0)
                        return;

                    prompt.removeClass("in-action");
                },
                /**
                 * The icon to see attachment actions is clicked
                 */
                toggleOptionsDisplay: function (event) {
//				console.log("toggleOptionsDisplay");
                    var prompt = $(event.currentTarget);

                    var options = prompt.siblings(".menu-options");
                    if (options.length === 0)
                        return;

                    //find open attachment-menu-options and in-action prompts and toggle, so that only one menu item can be open
                    var otherActivePrompt = prompt.parents('li.attachment-info').siblings('li.attachment-info').find('div.menu-prompt.in-action');
                    if (otherActivePrompt.length > 0) {
                        var otherActiveOption = otherActivePrompt.siblings('div.menu-options:visible');
                        if (otherActiveOption.length > 0) {
                            otherActiveOption.hide();
                            otherActivePrompt.toggleClass("in-action");
                        }
                    }

                    options.toggle();

                    prompt.toggleClass("in-action");
                },
                /**
                 * When requesting to delete/rename/link-unlink
                 */
                hideOptions: function (event) {
//				console.log("toggleOptionsDisplay");
                    var option = $(event.target);

                    var options = option.closest(".menu-options");
                    if (options.length === 0)
                        return;

                    options.hide();
                },
                /** setTabletHoverEffect calls TabletInteractionsView handler for hover event simulation */
                setTabletHoverEffect: function (event) {
                    var el = $(event.currentTarget);
                    var isTablet = (/(iPhone|iPod|iPad)/.test(navigator.userAgent) || (/Android/.test(navigator.userAgent)) || (/BlackBerry/.test(navigator.userAgent)));
                    if (isTablet) {
                        /**
                         * pgia: EWA-1815
                         * Load TabletInteractionsView via require on the tablets variable ONLY if isTablet == true
                         */
                        require(['europass/TabletInteractionsView'],
                                function (TabletInteractionsView) {
                                    TabletInteractionsView.addTapHover(el);
                                }
                        );
                    }
                }
            });
            return AttachmentEditFormView;
        }
);