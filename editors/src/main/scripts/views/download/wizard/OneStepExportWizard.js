define(["jquery",
    "backbone",
    'views/download/wizard/ExportInfo',
    'views/download/wizard/FileFormatSelectionStep',
    'views/download/wizard/LocationSelectionStep',
    'views/download/wizard/DocTypeSelectionStep',
    'views/download/wizard/DoExportStep',
    'views/download/RequestEmailView',
    'views/download/DownloadView',
    'views/download/DropboxStoreView',
    'views/download/GoogleDriveStoreView',
    'views/download/OneDriveStoreView',
    'Utils',
    "hbs!templates/download/wizard/one_step_export_wizard",
    'europass/http/WindowConfigInstance',
    'i18n!localization/nls/Notification'
], function (
        $,
        Backbone,
        ExportInfo,
        FileFormatSelectionStep,
        LocationSelectionStep,
        DocTypeSelectionStep,
        DoExportStep,
        RequestEmailView,
        DownloadView,
        DropboxStoreView,
        GoogleDriveStoreView,
        OneDriveStoreView,
        Utils,
        HtmlTemplate,
        WindowConfig,
        Notification
        ) {
    var OneStepExportWizard = Backbone.View.extend({
        wizard: null,
        htmlTemplate: HtmlTemplate,
        docs: null,
        emailView: null,
        cloudProviderView: null,
        events: {
            "click .close": "onClose",
            "change :checkbox.enabled": "changeDoc",
            "click .disabled": "warningEmptyDoc",
            "click #export-wizard-step-2 :radio": "changeFormat",
            "change #export-wizard-step-3 :radio": "changeLocation",
            "click #display_more_formats": "displayAllFileTypes",
            "click #download-button": 'downloadDocument',
            "click #connect-button": 'connectCloud',
            "click #email-button": 'sendEmail'
        },
        onClose: function () {
            var _that = this;
            var overlay = this.$el;
            var modal = (this.$el !== undefined ? this.$el.find(".modal").first() : undefined);
            var children = (modal !== undefined ? modal.children() : undefined);
            var overlayTopMid = $("body").find(".transition-overlay");
            if (children !== undefined) {
                children.hide('slide', {direction: "right", easing: "easeInSine"}, 300);
            }
            if (modal !== undefined) {
                modal.hide('slide', {direction: "right", easing: "easeInSine"}, 400);
                modal.find("form.drawer").css("overflow-y", "hidden");
                _that.onCloseArea();

                overlay.animate({"background-color": "rgba(0,0,0, 0)"}, 400, function () {
                    $(this).hide();
                    if (overlayTopMid !== undefined && overlayTopMid !== null && overlayTopMid.length !== 0) {
                        $(overlayTopMid).remove();
                    }
                });
            }
            $('body').removeClass('modal_overlay_open');
            // COMPLETELY UNBIND THE VIEW
            this.undelegateEvents();
            this.unbind();
        },
        onCloseArea: function () {
            if (_.isObject(this.info)) {
                this.info.clear();
                var feedbackArea = this.main.find(".feedback-area");
                if (!feedbackArea.is(':empty'))
                    feedbackArea.empty();
            }
            this.currentStep = 0;
        },

        initialize: function (options) {
            this.main = this.$el.find(".main");
            //Aside
            this.aside = this.$el.find(".side");
            //Models			
            this.model = options.model;
        },

        render: function () {
            var ctx = {};
            ctx.permissionToKeepCv = WindowConfig.permissionToKeepCv;
            this.openWizard();
            var mainHtml = this.htmlTemplate(ctx);
            this.main.html(mainHtml);
            this.info = new ExportInfo();
            this.info.setSkillsPassport(this.model);
            this.setupProcess();
        },
        /**
         * Use the drawer effect to open the wizard
         */
        openWizard: function () {
            var modal = (this.$el !== undefined ? this.$el.children(".modal") : undefined);
            var children = (modal !== undefined ? modal.children() : undefined);
            modal.addClass('export');
            this.$el.show(function () {
                $(this).animate({"background-color": "rgba(0,0,0, 0.7)"}, 400);
                modal.find("form.drawer").css("overflow-y", "scroll");
                if (children !== undefined && modal !== undefined) {
                    children.addBack().show('slide', {direction: "right", easing: "easeInSine"}, 400, function () {
                    });
                }
            });
        },
        setupProcess: function () {

            //Process
            var step1 = new DocTypeSelectionStep({
                el: $("#export-wizard-step-1"),
                main: this.main,
                aside: this.aside,
                model: this.info,
                previous: null
            });

            var step2 = new FileFormatSelectionStep({
                el: $("#export-wizard-step-2"),
                main: this.main,
                aside: this.aside,
                model: this.info,
                previous: step1
            });

            var step3 = new LocationSelectionStep({
                el: $("#export-wizard-step-3"),
                main: this.main,
                aside: this.aside,
                model: this.info,
                previous: step2
            });

            var step4 = new DoExportStep({
                el: $("#export-wizard-step-4"),
                main: this.main,
                aside: this.aside,
                model: this.info,
                previous: step3
            });
            //step4.render()
            step1.next = step2;
            step2.next = step3;
            step3.next = step4;

            this.steps = [step1, step2, step3, step4];
            step1.render();
            step1.saveModel();
            step2.render();
            step3.render();
            if (!step1.getSavedSelections().length) {
                var labels = this.$el.prevObject.find('label');
                $.each(labels, function (id, label) {
                    $(label).addClass('disabled');
                });
                this.disableAllButtons(true);
                this.warningEmptyDoc();
            }
        },
        disableAllButtons: function (action) {
            var buttons = this.$el.prevObject.find('button');
            $.each(buttons, function (id, button) {
                if (action) {
                    $(button).addClass('disabled');
                    $(button).attr('disabled', true);
                } else {
                    $(button).removeClass('disabled');
                    $(button).attr('disabled', false);
                }
            });
        },

        displayAllFileTypes: function (e) {
            $('#display_more_formats').hide();
            $('#show_other_file_formats_container').slideDown(200);
        },
        changeDoc: function (e) {
            $(this.steps[0].el).find('.note').slideUp(200);
            this.steps[0].saveModel();
            if (!this.steps[0].getSavedSelections().length) {
                this.disableAllButtons(true);
            } else {
                this.disableAllButtons(false);
            }
        },
        warningEmptyDoc: function () {
            $(this.steps[0].el).find('.note').slideDown(200);
        },
        changeFormat: function (e) {
            var formats = this.steps[1].prepareContext().formats;
            var selected = this.steps[1].findCheckedInput()[0];
            var warning = false;
            $.each(formats, function (id, element) {
                if (element.nonuploadable && selected === element.name) {
                    warning = true;
                }
            })
            if (warning === true) {
                $('#non-uploadable-warning').slideDown(200);
            } else {
                $('#non-uploadable-warning').slideUp(200);
            }
            this.steps[1].saveModel();
        },
        changeLocation: function (e) {
            var location = this.steps[2]._updateButtons()[0].value;
            var downloadButton = $("#download-button");
            var connectButton = $("#connect-button");
            var emailButton = $("#email-button");
            var emailContainer = $('#email_area');
            var emailWarning = $('#email-warning');
            var permissionFieldset = $('fieldset.step-permision');
            if (location === 'local') {
                permissionFieldset.show();
                downloadButton.show();
                connectButton.hide();
                emailButton.hide();
                emailContainer.slideUp(200);
                emailWarning.slideUp(200);
            } else if (location === 'dropbox' || location === 'googledrive' || location === 'onedrive') {
                permissionFieldset.hide();
                downloadButton.hide();
                connectButton.show();
                $.each(this.info.europassLocations(), function (id, item) {
                    connectButton.removeClass(item.name)
                })
                connectButton.addClass(location)
                emailButton.hide();
                emailContainer.slideUp(200);
                emailWarning.slideUp(200);
            } else if (location === 'email') {
                permissionFieldset.hide();
                downloadButton.hide();
                connectButton.hide();
                emailButton.slideDown(200);
                emailContainer.slideDown(200);
                this.emailView = new RequestEmailView({
                    el: emailContainer,
                    model: this.model,
                    info: this.info,
                    messageContainer: $('#app-notifications'),
                    parentView: this
                });
                this.emailView.render();
                emailWarning.fadeIn();
            }
            this.steps[2].saveModel();
        },

        downloadDocument: function () {
            var hasDocument = this.steps[0].canProceed();
            var hasFileFormat = this.steps[1].canProceed();
            var hasLocation = this.steps[2].canProceed();
            if (hasDocument && hasFileFormat && hasLocation) {
                var downloadView = new DownloadView({
                    el: this.$el,
                    model: this.model,
                    info: this.info,
                    messageContainer: $('#app-notifications'),
                    parentView: this.$el.prevObject
                });
                downloadView.render();
                downloadView.doFinish();
                this.onClose();
            }
        },

        connectCloud: function () {
            var location = this.steps[2]._updateButtons()[0].value;
            if (location === 'dropbox') {
                this.cloudProviderView = new DropboxStoreView({
                    el: this.$el,
                    model: this.model,
                    info: this.info,
                    messageContainer: $('#app-notifications'),
                    parentView: this
                });
                this.cloudProviderView.render();
                this.cloudProviderView.doConnect();
            } else if (location === 'googledrive') {
                this.cloudProviderView = new GoogleDriveStoreView({
                    el: this.$el,
                    model: this.model,
                    info: this.info,
                    messageContainer: $('#app-notifications'),
                    parentView: this
                });
                this.cloudProviderView.render();
                this.cloudProviderView.doConnect();

            } else if (location === 'onedrive') {
                this.cloudProviderView = new OneDriveStoreView({
                    el: this.$el,
                    model: this.model,
                    info: this.info,
                    messageContainer: $('#app-notifications'),
                    parentView: this
                });
                this.cloudProviderView.render();
                this.cloudProviderView.doConnect();
            }

            this.onClose();
        },

        sendEmail: function () {
            if (this.emailView !== null) {
                this.emailView.doConnect();
            }
        },
        checkPopUp: function (view) {
            var self = this;
            var timeOutMilis = 500;

            setTimeout(function () {
                setTimeout(function () {	//seems like FF needs another setTimeout in order to make tryOpeningPopUp() work
                    var allowed = Utils.tryOpeningPopUp();

                    if (allowed === false) {
                        $("body").trigger("europass:waiting:indicator:hide");
                        self.trigger("europass:message:show",
                                ["error", (Notification["skillspassport.wizard.cloud.popup.blocked"]), false]);
                    } else {
                        $(view).trigger("europass:popup:enabled");
                    }
                }, timeOutMilis);
            }, timeOutMilis);
        }
    });

    return OneStepExportWizard;
});
