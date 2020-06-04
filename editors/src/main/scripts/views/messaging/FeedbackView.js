define(
        [
            'jquery',
            'jqueryui',
            'underscore',
            'backbone',
            'scrollTo',
            'Utils',
            'hbs!templates/messaging/message',
            'hbs!templates/dialog/resetconfirmation',
            'hbs!templates/dialog/deleteconfirmation',
            'hbs!templates/dialog/saveconfirmation',
            'i18n!localization/nls/Notification',
            'i18n!localization/nls/AccessibilityLabel',
//		EWA-1811
//	'	i18n!localization/nls/GuiLabel',
            'ModalFormInteractions',
//		'europass/TabletInteractionsView',
            'europass/http/WindowConfigInstance',
            'HelperManageModelUtils',
            'europass/GlobalDocumentInstance'
        ],
        function ($,
                jqueryui,
                _,
                Backbone,
                scrollTo,
                Utils,
                MessageTpl,
                ResetConfirmationTpl,
                DeleteConfirmationTpl,
                SaveConfirmationTpl,
                Notification,
                AccessibilityLabel,
//			 EWA-1811
//			 GuiLabel,
                ModalFormInteractions,
//			 TabletInteractionsView,
                WindowConfig,
                //Events,
                HelperManageModelUtils,
                GlobalDocument) {

            var FeedbackView = Backbone.View.extend({
                el: "body"

                , globalContainerSelector: "#app-notifications"
                , feedbackAreaElSelector: ".feedback-area"
                , contactUsSectionId: "#sendfeedback-notification-area"
                , shareForReviewSectionClass: ".share-cloud-notification-area"
                , shareFormSectionId: "#share-warning-msg"
                , accessibilityHiddenLinkId: "#accessibility-title-hidden-link"
                , messageElSelector: ":not(:button).notification"
                , nonErrorMessageElSelector: ":not(:button).notification:not(.error)"
                , tabletContainerSelector: "#header-logo-moto-small"
                , tabletContainerSelectorSibling: "#CurrentDocumentTitle"
                , fileUploadError: false
                , events: {
                    "europass:section:updated": "highlight",
                    "europass:message:fileUploadError": "fileUploadedError",
                    "europass:message:show": "show",
                    "europass:message:clear": "clear",
                    "click :button.notification.message.close": "hide",

                    "DOMSubtreeModified #app-notifications": "scrollToErrorMessage",

                    "europass:delete:requested": "confirmDeletion",
                    "europass:save:requested": "confirmSave",
                    "europass:reset:requested": "confirmReset",

                    "click menu.confirm-dialog.delete-model > button.confirm-submit.delete-model": "doDeletion",
                    "click menu.confirm-dialog.delete-model > button.confirm-cancel.delete-model": "cancelConfirmation",
                    "click menu.confirm-dialog.save-model > button.confirm-submit.save-model": "doSave",
                    "click menu.confirm-dialog.save-model > button.confirm-cancel.save-model": "cancelSave",
                    "click .ui-dialog button.ui-dialog-titlebar-close": "cancelConfirmation",

                    "click #Erase_Option_Doc_Confirm_ECV": "doSelectCVToErase",
                    "click #Erase_Option_Doc_Confirm_ELP": "doSelectLPToErase",
                    "click #Erase_Option_Doc_Confirm_ESP": "doSelectESPToErase",
                    "click #Erase_Option_Doc_Confirm_ECL": "doSelectECLToErase",
                    "click #select-all-erase-input": "selectAllChooseErase",

                    "click #notifications-area > section.notification-section": "expandNotificationsAreas",
                    //"click #cloud-share-done-reviewing": "selectShareReviewAction",
                    "click #confirmSharedDocumentReviewModal .confirm-dialog .confirm-save.share-review-document-btn": "confirmSaveReviewedDocument",
                    "click #confirmSharedDocumentReviewModal .confirm-dialog .confirm-save-finalize.share-review-document-btn": "confirmSaveAndFinalizeReviewedDocument"
                }

                , initialize: function (options) {

                    this.model = options.model;

                    this.messageTpl = MessageTpl;
                    this.resetConfirmationTpl = ResetConfirmationTpl;
                    this.deleteConfirmationTpl = DeleteConfirmationTpl;
                    this.saveConfirmationTpl = SaveConfirmationTpl;
                    this.relatedViewSection = null;
                    this.section = null;
                    $(this.accessibilityHiddenLinkId).attr("aria-label", AccessibilityLabel["Accessibility.Hidden.Link.For.Accessible.Editor.Title"]);
                    $(this.accessibilityHiddenLinkId).attr("href", "/accessible-editors/cv/?lang=" + ewaLocale + "&ref=" + document.location.origin);

                    //Event when the Model LearnerInfo has changed
                    this.model.bind("local:storage:model:populated", this.modelContentRestored, this);
                    this.model.bind("model:content:changed", this.modelContentChanged, this);
                    this.model.bind("model:content:reset", this.modelContentReset, this);
                    this.model.bind("model:uploaded:esp", this.modelContentUploaded, this);
                    this.model.bind("model:uploaded:cloud", this.modelContentUploaded, this);
                    this.model.bind("model:linked:attachment:changed", this.modelLinkedAttachmentChanged, this);
                    this.model.bind("share:link:copy:clipboard", this.linkCopyClipBoard, this);
                    this.model.bind("share:link:copy:clipboard:manual", this.linkCopyClipBoardManual, this);
                }
                , onClose: function () {
                    this.model.unbind("local:storage:model:populated", this.modelContentRestored);
                    this.model.unbind("model:content:changed", this.modelContentChanged);
                    this.model.unbind("model:content:reset", this.modelContentReset);
                    this.model.unbind("model:uploaded:esp", this.modelContentUploaded);
                    this.model.unbind("model:uploaded:cloud", this.modelContentUploaded);
                    this.model.unbind("model:linked:attachment:changed", this.modelLinkedAttachmentChanged);
                    this.model.unbind("share:link:copy:clipboard", this.linkCopyClipBoard);
                    this.model.unbind("share:link:copy:clipboard:manual", this.linkCopyClipBoardManual);
                }
                , fileUploadedError: function (event, state) {
                    this.fileUploadError = state;
                }
                /**
                 * Appends a message html element to the element that triggered the event.
                 * @param event, the dom element which may have fired the europass:message:show event.
                 * 		  if the event and the event.target exist, then the message will be shown
                 * 		  inside this element.
                 * 		  Otherwise, if the event or the event target does not exist, the message
                 * 		  will be displayed in the globally defined position, which is the body > section.notification.
                 *  
                 * @param state, string to be added as class name to control the styling (error, warning, neutral)
                 * @param message, string (may be html) to display inside the message.
                 * @param blink, true when informative messages that need to be cleared
                 * @param forceKeepLocalStorage: this is used from ModelLocalStoreView.localStorageNotSupported so the message is not removed
                 */
                , show: function (event, state, message, blink, forceKeepLocalStorage, messageKey) {

                    var container = null;
                    var closable = "no";
                    var template = this.messageTpl;
                    var that = this;

                    forceKeepLocalStorage = forceKeepLocalStorage || false;	//if (forceKeepLocalStorage == undefined || forceKeepLocalStorage == null || forceKeepLocalStorage == '')

                    message = message || "";

                    messageKey = messageKey || "";

                    container = event ? $(event.target) : null;

                    /*if (event!= undefined && event != null){container = ;	}*/

                    //In case a client has triggered the event on "body"
                    if (container === undefined || container === null || container.length === 0 || container.is("body")) {
                        //will display the message to the top of the page
                        container = $(this.globalContainerSelector);
                    }

                    if (message === Notification["success.copy.clipboard"] || message === Notification["success.copy.clipboard.manual"]) {
                        container = this.$el.find("#notification-copy");
                    }
                    var containerNotifications = container.find("section.notification");

                    if (state !== "error" && containerNotifications.is(".error") && !containerNotifications.is("#preview-document-error")) {
                        //if you have errors which have not been cleared, clear them
                        container.find("section.notification.error").remove();
                        //return;//vpol changed: if success, then remove the error
                    }

                    if (forceKeepLocalStorage)
                        container.addClass('forceKeepLocalStorage');

                    if (container.is($(this.globalContainerSelector))) {
                        if (!container.hasClass('forceKeepLocalStorage'))
                            this.emptyFeedback(container, false, true, true);
                    }

                    if ((state === "error" || blink === false) &&
                            !container.is(this.contactUsSectionId) && !container.is(this.shareFormSectionId) && !container.is(this.shareForReviewSectionClass))
                        closable = "yes";

                    var context = this.prepareContext(state, message, null, null, closable, messageKey);

                    var isMainMsg = this.checkMsg(message);   //Check if the success message belongs to a modal (use for tablet mode only)...

                    var html = template(context);

                    var el = $(html);

                    /*	if ( blink === false && !container.is(this.contactUsSectionId) ){
                     closable = "yes";
                     }
                     el.hide();*/

                    //if message exists, dont display again
                    var messageExists = function () {
                        var result = false;
                        container.find(".message-area").each(function (idx, el) {
                            if (messageKey === $(el).attr("data-rel-key")) {
                                result = true;
                                return false;
                            }
                        });
                        return result;
                    };



                    if (messageKey !== "" && messageExists() === true)
                        return; //return if the message already exists in the notification area

                    container.find(".message-area").each(function (idx, subel) {
                        if ($(subel).attr("data-rel-key") === 'no-internet' && messageKey === 'internet') {
                            $(subel).parent().slideUp(100);
                        }
                    });
                    el.slideUp(100);

                    el.appendTo(container).slideDown(200);

                    container.removeClass("dismissed");

                    if (state === "success" && isMainMsg && this.checkVariable(this.tabletContainerSelector) && this.checkVariable(this.tabletContainerSelectorSibling)) {
                        $(this.tabletContainerSelectorSibling).fadeOut(200, function () {
                            $(this).css({"display": "none"});
                        });

                        el.appendTo(this.tabletContainerSelector).slideDown(200).promise().done(function () {
                            setTimeout(function () {
                                that.emptyFeedback(container, false, true, false);
                            }, 4000);
                        });
                        container.removeClass('forceKeepLocalStorage');//after the local-storage message, the save button will appear and when it will hide it will remove the forceKeepLocalStorage class also.
                    }
                    if (state === "warning global-warning" && isMainMsg && this.checkVariable(this.tabletContainerSelector) && this.checkVariable(this.tabletContainerSelectorSibling)) {
                        el.appendTo(this.tabletContainerSelector).slideDown(200);
                    }
                    if (blink === true) {
                        container.trigger("europass:message:clear", true);
                    }
                },

                emptyFeedback: function (container, filterErrors, noeffect, notToTablet) {

                    var selector = this.messageElSelector;
                    if (filterErrors === true && !container.is(this.contactUsSectionId)) {
                        selector = this.nonErrorMessageElSelector;
                    }
                    if (noeffect === true) {
                        container.find(selector).remove();
                    } else {
                        //vpol altered because notification errors where not cleared when uploading files
                        var notifications = container.find(selector);
                        if (notifications.length > 0) {
                            container.find(selector).slideUp(200, function () {
                                $(this).remove();
                            });
                        } else {
                            notifications = container.find(this.messageElSelector);
                            if (notifications.length > 0) {
                                //remove if found messages with the initial selector (regardless of .error class)
                                container.find(this.messageElSelector).slideUp(200, function () {
                                    $(this).remove();
                                });
                            }
                        }
                    }

                    if (notToTablet === false && this.checkVariable(this.tabletContainerSelector) && this.checkVariable(this.tabletContainerSelectorSibling)) {
                        var msg = $(this.tabletContainerSelector).find("section.success");
                        if (msg !== undefined && msg !== null) {
                            msg.fadeOut(200, function () {
                                $(this).remove();
                            });
                            $(this.tabletContainerSelectorSibling).slideDown(200, function () {
                                $(this).css({"display": ""});
                            });
                        }
                    }
                },
                /**
                 * Clears the area of all messages
                 */
                clear: function (event, blink) {
                    var container = null;

                    if (event !== undefined && event !== null) {
                        container = $(event.target);
                    }

                    if (container === undefined || container === null || container.length === 0) {
                        container = $(this.globalContainerSelector);
                    }

                    if (blink === true) {//this is needed because the clear message is also called from FileManager to clear messages container
                        var that = this;
                        setTimeout(function () {
                            that.emptyFeedback(container, true);
                        }, 4000);
                    } else { //NO WE WANT MESSAGE TO STAY - why?
                        this.emptyFeedback(container, true);
                    }
                }

                /**
                 * Clears the message that wraps this button 
                 */
                , hide: function (event) {
                    var button = $(event.target);

                    var container = button.closest(this.feedbackAreaElSelector);

                    if (container === undefined || container === null || container.length === 0) {
                        container = $(this.globalContainerSelector);
                    }
                    container.find(button.closest(this.messageElSelector)).slideUp(200, function () {
                        container.addClass("dismissed");
                        $(this).remove();
                    });
                },
                /**
                 * Displays a confirmation Modal Dialog.
                 * The dialog is rendered using a Handlebars template (/template/dialog/confirmation)
                 * which is imported as dependency.
                 * 
                 * The template is executed with a suitable context that includes the html id of the 'el'
                 * element of the Backbone View (e.g. id="Compose:LearnerInfo.WorkExperience"), 
                 * in which the delete button -that triggered the event- is included.
                 *  (e.g. introduce a data-rel-view attribute to the button.
                 *        get it as id, get it as dom element, 
                 *        and pass the element as param to the event triggering )
                 * 
                 * The dom element of the compose view will be availabe in this function. 
                 * So, when OK is clicked on the modal dialog, an event 'europass:delete:confirmed' is triggered
                 * on the available Dom element of the compose view.
                 * -The ComposeView will need to listen-to and respond to the 'europass:delete:confirmed' event.
                 * 
                 * @param the event object
                 * @param the DOM element of the compose view that includes the delete button 
                 *        (the 'confirmed' event will need to be triggered to this one)
                 * @param the name of the section as it comes from the button and which needs to be propagated to the 'confirmed' event. 
                 */
                //this goes on compose?
                confirmDeletion: function (event, relatedView, section) {

                    var context = this.prepareContext("confirm", "", section, $(Utils.jId(relatedView)).attr("data-onDelete-ignoreTip"));

                    this.renderDeleteConfirm(context, relatedView, section);

                },
                
                /**
                 * Displays a Model Save Confirmation Dialog
                 * @param event
                 * @param relatedView the DOM element of the compose view that includes the save button 
                 *        (the 'confirmed' event will need to be triggered to this one)
                 */
                confirmSave: function (event, relatedView, section) {
                    var context = this.prepareContext("confirm", "", section, $(Utils.jId(relatedView)).attr("data-onDelete-ignoreTip"));
                    this.renderSaveConfirm(context, relatedView, section);
                },

                /**
                 * Displays a Model Reset Confirmation Dialog
                 * @param event
                 * @param relatedView the DOM element of the compose view that includes the delete button 
                 *        (the 'confirmed' event will need to be triggered to this one)
                 */
                confirmReset: function (event, relatedView) {
                    var context = this.prepareContext("confirm", "", "", $(Utils.jId(relatedView)).attr("data-onDelete-ignoreTip"));

                    this.renderResetConfirm(context, relatedView);
                },

                prepareContext: function (state, message, section, ignoreTip, closable, messageKey) {
                    var json = {};
                    json["state"] = state;
                    json["message"] = message;
                    json["section"] = section;
                    json["ignoreTip"] = ignoreTip;
                    json["closable"] = closable;
                    json["messageKey"] = messageKey;
                    return json;
                },

                /**
                 * Runs when the model is populated by the local storage
                 */
                modelContentRestored: function () {
                    var message = Notification["store.data.locally.restored"];
                    if (message === null) {
                        message = "Your CV has been restored with data previously saved in the browser.";
                    }
                    this.show(null, "warning", message, true);
                },
                /**
                 * Runs when the model is populated from data via the remote upload
                 */
                remoteContentUploaded: function () {
                    var message = Notification["remote.upload.received"];
                    if (message === null) {
                        message = "Your document(s) has been populated with data received from a remote upload.";
                    }
                    this.show(null, "warning", message, true);
                },
                /**
                 * When the model is reset
                 */
                modelContentReset: function () {
                    var message = Notification["success.model.reset"];
                    if (message === null) {
                        message = "Your document(s) has been successfully reset";
                    }
                    this.show(null, "success", message, true);
                },

                /**
                 * When modal forms are used to update the model
                 * @param section
                 */
                modelContentChanged: function (section, orderchanged) {
                    var message = Notification["success.save"];//for any modal
                    if (section !== undefined && section !== null && section !== "") {

                        if (orderchanged !== undefined && orderchanged !== null && orderchanged === true) {
                            message = Notification["success.order.date.changed"];

                        } else {
                            if (section.indexOf(".Identification.Photo") > 0) {
                                message = Notification["success.photo.save"]; //for the photo modal
                            } else if (section.indexOf(".ReferenceTo") > 0) {
                                message = Notification["success.attachment.save"]; //for the linked attachment modal to sections
                            }
                        }
                    }
                    if (!this.fileUploadError && GlobalDocument.getDocument() === 'ESP') {
                        this.show(null, "success", message, true);
                    }
                },

                linkCopyClipBoard: function () {
                    var msg = Notification["success.copy.clipboard"];
                    this.show(null, "success", msg, true);
                },
                linkCopyClipBoardManual: function () {
                    var msg = Notification["success.copy.clipboard.manual"];
                    this.show(null, "success", msg, true);
                },
                modelLinkedAttachmentChanged: function () {
                    if (GlobalDocument.getDocument() === 'ESP') {
                        this.show(null, "success", Notification["success.attachment.save"], true); //for esp modal
                    }
                },
                modelContentUploaded: function (message) {

                    if (message && _.isString(message))
                        this.show(null, "success", Notification[message], true); //for the upload from linkedin
                    else
                        this.show(null, "success", Notification["success.upload.cv.saved"], true); //for the upload cv and esp modal
                },
                renderResetConfirm: function (context, relatedView, section) {
                    var _that = this;
                    require(
                            ['i18n!localization/nls/GuiLabel'],
                            function (GuiLabel) {

                                var docs = [{name: "ECL", title: GuiLabel["export.wizard.doc.option.ECL"], empty: false, intend: 'disabled'},
                                    {name: "ECV", title: GuiLabel["export.wizard.doc.option.ECV"], empty: false, intend: 'disabled'},
                                    {name: "ELP", title: GuiLabel["export.wizard.doc.option.ELP"], empty: false, intend: 'enabled'},
                                    {name: "ESP", title: GuiLabel["export.wizard.doc.option.ESP"], empty: false, intend: 'enabled'}];
                                context["docs"] = docs;
                                var dialogHtml = _that.resetConfirmationTpl(context);

                                _that.renderConfirm("#resetConfirmationDialog", dialogHtml, relatedView, section);

                                HelperManageModelUtils.preselectResetOptionCheckboxes();
                            }
                    );
                },

                renderSaveConfirm: function (context, relatedView, section) {
                    var dialogHtml = this.saveConfirmationTpl(context);
                    this.renderConfirm("#saveConfirmationDialog", dialogHtml, relatedView, section);
                },

                renderDeleteConfirm: function (context, relatedView, section) {
                    var dialogHtml = this.deleteConfirmationTpl(context);

                    this.renderConfirm("#deleteConfirmationDialog", dialogHtml, relatedView, section);
                },

                renderConfirm: function (modalId, dialogHtml, relatedView, section) {

                    var dialogEl = $(modalId);
                    if (dialogEl === undefined || dialogEl === null || dialogEl.length === 0) {
                        dialogEl = $(document.createElement("div"));
                        dialogEl.attr("id", modalId);
                        dialogEl.attr("class", "modal-window confirmation");
                        $(dialogEl).html(dialogHtml);
                    }
                    this.relatedViewSection = $(Utils.jId(relatedView));
                    this.section = section;

                    dialogEl.dialog({
                        autoOpen: false,
                        modal: true,
                        resizable: false,
                        draggable: false,
                        position: ["center", "center"],
                        width: 500,
                        // height: 200 remove because is not respected and now it takes by default height:auto
                        // minHeight: 200
                    });

                    dialogEl.dialog("open");
                },
                
                doSave: function (event) {
                    this.closeModal($(event.target));
                    this.relatedViewSection.siblings(".side").find("button.submit:not(.disabled)").trigger("click");
                },

                doDeletion: function (event) {

                    var eraseCL = HelperManageModelUtils.resetOptionCL();
                    var eraseCV = HelperManageModelUtils.resetOptionCV();
                    var eraseLP = HelperManageModelUtils.resetOptionLP();
                    var eraseESP = HelperManageModelUtils.resetOptionESP();

                    this.closeModal($(event.target));

                    this.relatedViewSection.trigger("europass:delete:confirmed", [this.section, eraseCL, eraseCV, eraseLP, eraseESP]);
                    //delete from Local Storage the 'LoadedFile' if exists - only if whole model is deleted
                    try {
                        if (_.isUndefined(this.section) && window.localStorage.getItem('LoadedFile')) {
                            //update loaded file from cloud with an empty cv
                            $("body").trigger("europass:cloud:manage:erase:update");
                        }
                    } catch (err) {
                    }
                    this.cleanupOldResetModal();
                },

                cancelSave: function (event) {
                    this.closeModal($(event.target));
                    this.relatedViewSection.trigger("europass:cancel:save");
                },

                cancelConfirmation: function (event) {
                    this.closeModal($(event.target));
                    this.cleanupOldResetModal();
                },

                cleanupOldResetModal: function () {
                    $("#\\#resetConfirmationDialog").closest('.ui-dialog').remove();
                },

                closeModal: function (el) {
                    var dialogEl = el.closest("div.modal-window.confirmation");
                    if (dialogEl !== undefined || dialogEl != null || dialogEl.length != 0) {
                        $(dialogEl).dialog("close");
                    }
                },

                adjustEraseWhenSelectCheckboxes: function (event) {

                    var selectedAllLength = 0;
                    var unselectedAllLength = 0;
                    $(".reset-confirmation-doc-select input").each(function () {
                        if ($(this).is(":checked"))
                            selectedAllLength++;
                        else
                            unselectedAllLength++;
                    });

                    if (selectedAllLength > 0) {
                        $(event.currentTarget).closest('#\\#resetConfirmationDialog').find('button.confirm-submit.delete-model').removeClass('disabled');
                        if (selectedAllLength === 4) {
                            $(event.currentTarget).closest('#\\#resetConfirmationDialog').find('#select-all-erase-input').prop('checked', true);
                        }
                    }
                    if (unselectedAllLength > 0) {
                        $(event.currentTarget).closest('#\\#resetConfirmationDialog').find('#select-all-erase-input').prop('checked', false);
                        if (unselectedAllLength === 4) {
                            $(event.currentTarget).closest('#\\#resetConfirmationDialog').find('button.confirm-submit.delete-model').addClass('disabled');
                        }
                    }
                },

                selectAllChooseErase: function (event) {
                    if ($(event.currentTarget).is(":checked")) {
                        $(".reset-confirmation-doc-select input").prop('checked', true);
                        $(event.currentTarget).closest('#\\#resetConfirmationDialog').find('button.confirm-submit.delete-model').removeClass('disabled');
                    } else {
                        $(".reset-confirmation-doc-select input").prop('checked', false);
                        $(event.currentTarget).closest('#\\#resetConfirmationDialog').find('button.confirm-submit.delete-model').addClass('disabled');
                    }
                },
                doSelectECLToErase: function (event) {
                    this.adjustEraseWhenSelectCheckboxes(event);
                },
                doSelectCVToErase: function (event) {
                    if ($(event.currentTarget).is(":checked")) {
                        $(event.currentTarget).closest('#\\#resetConfirmationDialog').find('#Erase_Option_Doc_Confirm_ELP, #Erase_Option_Doc_Confirm_ESP').prop('checked', true);
                    }
                    this.adjustEraseWhenSelectCheckboxes(event);
                },
                doSelectLPToErase: function (event) {
                    if (!$(event.currentTarget).is(":checked")) {
                        $(event.currentTarget).closest('#\\#resetConfirmationDialog').find('#Erase_Option_Doc_Confirm_ECV').prop('checked', false);
                    }
                    this.adjustEraseWhenSelectCheckboxes(event);
                },
                doSelectESPToErase: function (event) {
                    if (!$(event.currentTarget).is(":checked")) {
                        $(event.currentTarget).closest('#\\#resetConfirmationDialog').find('#Erase_Option_Doc_Confirm_ECV').prop('checked', false);
                    }
                    this.adjustEraseWhenSelectCheckboxes(event);
                },

                // After EPAS-1044 changes we need to have some timeout set in order to properly show highlight animations
                highlight: function (event, el, doTransition, targetListItem, attrID) {
                    if (el.length === 1 && /*el.is("dl")*/ el.attr("id") !== "Compose:Esp.Attachment") {
                        if (targetListItem !== null && targetListItem !== undefined) {
                            var listIt = el.find(".list-item:not(.section.manage):nth-child(" + (targetListItem + 1) + ")");
                            if (listIt.length > 0) {
                                var _that = this;
                                setTimeout(function () {
                                    _that._highlight(listIt, doTransition, true, attrID);
                                }, 300);
                            }
                        } else {
                            var _that = this;
                            setTimeout(function () {
                                _that._highlight(el, doTransition, true, attrID);
                            }, 300);
                        }
                    }
                },

                _highlight: function (el, doTransition, doSortingTransition, attrID) {

                    var isTablet = (/(iPhone|iPod|iPad)/.test(navigator.userAgent) || (/Android/.test(navigator.userAgent)) || (/BlackBerry/.test(navigator.userAgent)));
                    //ANIMATE
                    var clazz = "highlight";
                    var elem = $(el);
                    var isECV_WorkExperienceOrEducation =
                            el.selector === '#Compose\\:LearnerInfo\\.WorkExperience'
                            || el.selector === '#Compose\\:LearnerInfo\\.Education';

                    // Highlight functionality for ECV (without work experience/education)
                    if (GlobalDocument.getDocument() === 'ECV' && !isECV_WorkExperienceOrEducation) {
                        elem.addClass(clazz);
                    } else {
                        attrID = attrID.replace("SkillsPassport.", "ListItem:");

                        $(Utils.jId(attrID)).addClass(clazz);
                        // for elp highlighting
                        $(Utils.jId(attrID)).closest('.highlight-container').addClass(clazz);
                    }

                    setTimeout(function () {
                        if (!elem.is("section.Compose") && _.isBoolean(doTransition) && doTransition) {
                            setTimeout(function () {
//							console.log("Scroll to...");
                                var el = $(Utils.jId(elem.attr("id")));
//							console.log( Utils.jId(elem.attr("id")) );
                                if ((elem.attr("id") === "Compose:LearnerInfo.Skills.Driving" || elem.attr("id") === "Compose:LearnerInfo.Skills.Other") && doTransition) {
//								console.log("Scroll to...");
                                    var mainScrl = $('#main-content-area').scrollTop();
                                    var elemPos = elem.offset().top;
                                    var pos = mainScrl + elemPos - 80;
                                    //Added more top margin in scroll position for tablets, due to inconsistencies
                                    if (isTablet) {
                                        pos = pos - 80;
                                    }
                                    //Scroll the main content area to the item that has just changed position
                                    //Added  a margin to top (80 in this case) for better positioning of the element
                                    if (elemPos === 0) {
                                        $('#main-content-area').scrollTo(el, {duration: 1000, easing: 'linear', axis: 'y'});
                                    } else {
                                        $('#main-content-area').animate({scrollTop: pos}, 1000, 'linear');
                                    }
                                } else {
                                    $('#main-content-area').scrollTo(el, {duration: 1000, easing: 'linear', axis: 'y'});
                                }
                            }, 100);
                        } else if (elem.is(".list-item") && doSortingTransition) {
//							|| elem.attr("id") == "Compose:LearnerInfo.ReferenceTo")  {
                            setTimeout(function () {
//							console.log("Scroll to...");
                                var mainScrl = $('#main-content-area').scrollTop();
                                var elemPos = elem.offset().top;
                                var pos = mainScrl + elemPos - 80;
                                //Added more top margin in scroll position for tablets, due to inconsistencies
                                if (isTablet) {
                                    pos = pos - 80;
                                }
                                //Scroll the main content area to the item that has just changed position
                                //Added  a margin to top (80 in this case) for better positioning of the element
                                $('#main-content-area').animate({scrollTop: pos}, 200, 'easeInOutQuart');
                            }, 100);
                        }
                    }, 100);
                    setTimeout(function () {
                        $(Utils.jId(attrID)).removeClass(clazz);
                        $(Utils.jId(attrID)).closest('.highlight-container').removeClass(clazz);
                        elem.removeClass(clazz);

                    }, 3000);
                }
                /*scrollToErrorMessage: utility function to help accessibility to error messages
                 * DOMSubtreeModified was used instead of known events in order to capture error messages
                 * from the backed (.ie SessionAwareWithHTMLBodyWrapper )
                 * */
                , scrollToErrorMessage: function (event) {
                    ($("#app-notifications").find('.error').length > 0 ?
                            $('#main-content-area').scrollTo('#app-notifications', {duration: 1000, easing: 'linear', axis: 'y'}) : "");
                },

                /** Check if the message concerns a modal - feedback notification
                 * This variable will be useful for the appendage of the success message on tablets
                 *If it is true, the message won't be appended. It is a modal message and we don't want it applied on the top blue area (tablet only).
                 *@param msg: We check if it matches with specific values
                 **/
                checkMsg: function (msg) {
                    /* var msgFileName = msg.match(/<div class="file-names">/g);
                     
                     var msgFeedback = msg.match(Notification["feedback.success.info"]);
                     
                     var msgDownload = msg.match(Notification["Download.Document.Ready"]);
                     
                     var mailSendSuccess = msg.match(Notification["success.email.cv.sent"]);
                     
                     var msgExportWizard = msg.match(GuiLabel["export.wizard.step...."]); */

                    var isDataSaved = msg.match(Notification["success.save"]);

                    var isDataSorted = msg.match(Notification["success.order.date.changed"]);

                    var isUploadedAttachment = msg.match(Notification["success.attachment.save"]);

                    //if (this.checkVariable(msgFileName) || this.checkVariable( msgFeedback) || this.checkVariable(msgDownload) || this.checkVariable(mailSendSuccess) ) {
                    if (isDataSaved || isDataSorted || isUploadedAttachment) {
                        return true;
                    } else {
                        return false;
                    }
                },

                /** 
                 * Function to check shortly if a variable is NOT undefined / null / variable.length = 0... 
                 * param: el: the element that should be checked
                 **/
                checkVariable: function (el) {
                    if (el !== undefined && el !== null && el.length > 0) {
                        return true;
                    } else {
                        return false;
                    }
                },

                /**
                 * Function used to add class for expanding notifications area section - mobile/tablets only
                 * @param event: the trigger clicked event
                 **/
                expandNotificationsAreas: function (event) {
                    var isTablet = (/(iPhone|iPod|iPad)/.test(navigator.userAgent) || (/Android/.test(navigator.userAgent)) || (/BlackBerry/.test(navigator.userAgent)));
                    if (isTablet) {
                        var el = $(event.currentTarget);
                        $(el).addClass("expanded");
                    }
                }
            });

            return FeedbackView;
        }
);
