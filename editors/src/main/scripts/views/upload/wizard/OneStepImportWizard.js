define(["jquery",
    "backbone",
    'views/upload/wizard/ImportInfo',
    'views/upload/wizard/LocationSelectionStep',
    'views/upload/wizard/DocTypeSelectionStep',
    'views/upload/wizard/DoImportStep',
    'views/upload/LocalFileUploadView',
    'views/upload/DropboxUploadView',
    'views/upload/GoogleDriveUploadView',
    'views/upload/OneDriveUploadView',
    'Utils',
    "hbs!templates/upload/wizard/one_step_import_wizard",
    'europass/http/WindowConfigInstance',
    'i18n!localization/nls/Notification'
], function ($, Backbone, ImportInfo, LocationSelectionStep, DocTypeSelectionStep, DoImportStep,
        LocalFileUploadView, DropboxUploadView, GoogleDriveUploadView, OneDriveUploadView,
        Utils, HtmlTemplate, WindowConfig, Notification) {
    var OneStepExportWizard = Backbone.View.extend({
        wizard: null,
        htmlTemplate: HtmlTemplate,
        docs: null,
        emailView: null,
        dropboxView: null,
        googledriveView: null,
        onedriveView: null,
        events: {
            "click .close": "onClose",
            "change :checkbox.enabled": "changeDoc",
            "change #import-wizard-step-1 :radio": "changeLocation",
            "click #import-button": 'importDocument',
            "click #import-connect-button": 'connectCloud'
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
            ctx.permissionToKeepNotImportedCv = WindowConfig.permissionToKeepNotImportedCv;
            this.openWizard();
            var mainHtml = this.htmlTemplate(ctx);
            this.main.html(mainHtml);
            this.info = new ImportInfo();
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
            var step1 = new LocationSelectionStep({
                el: $("#import-wizard-step-1"),
                main: this.main,
                aside: this.aside,
                model: this.info,
                previous: step2
            });

            var step2 = new DocTypeSelectionStep({
                el: $("#import-wizard-step-2"),
                main: this.main,
                aside: this.aside,
                model: this.info,
                previous: null
            });

            var step3 = new DoImportStep({
                el: $("#import-wizard-step-3"),
                main: this.main,
                aside: this.aside,
                model: this.info,
                previous: step3
            });

            step1.next = step2;
            step2.next = step3;
            this.steps = [step1, step2, step3];

            step1.render();

            this.localFileUploadView = new LocalFileUploadView({
                el: this.$el.prevObject,
                model: this.model,
                info: this.info,
                messageContainer: $('#app-notifications'),
                parentView: this.steps[2],
                form: this
            }).render();

            if (this.steps[2].getContext().dataExists) {
                $('#data-exists').show();
            } else {
                $('#data-exists').hide();
            }

            $('#import-local-file').show();
            step2.render();
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
            this.steps[1].saveModel();
            this.checkSelectedDocuments();
        },

        changeLocation: function (e) {
            var location = $(this.steps[0].saveModel())[0].value;
            var localFileContainer = $('#import-local-file');
            var connectButton = $("#import-connect-button");
            if (location === 'local') {
                localFileContainer.show();
                connectButton.hide();
            } else if (location === 'dropbox' || location === 'googledrive' || location === 'onedrive') {
                localFileContainer.hide();
                connectButton.show();
                $.each(this.info.europassLocations(), function (id, item) {
                    connectButton.removeClass(item.name)
                })
                connectButton.addClass(location)
            }
            this.checkSelectedDocuments();
        },

        checkSelectedDocuments: function (e) {
            var location = $("#import-wizard-step-1 :radio:checked").val();
            if (location === 'local') {
                if ($('#import-wizard-step-2 input:checked').length === 0) {
                    $('#import-local-file span.button.blue.upload').addClass("disabled");
                    $('#import-local-file input:file').attr('disabled', true);
                } else {
                    $('#import-local-file span.button.blue.upload').removeClass("disabled");
                    $('#import-local-file input:file').attr('disabled', false);
                }
            } else {
                var connectButton = $('#import-connect-button');
                if ($('#import-wizard-step-2 input:checked').length === 0) {
                    connectButton.addClass("disabled");
                    connectButton.attr('disabled', true);
                } else {
                    connectButton.removeClass("disabled");
                    connectButton.attr('disabled', false);
                }
            }
        },

        importDocument: function () {
            var hasDocument = this.steps[0].canProceed();
            var hasLocation = this.steps[1].canProceed();

            if (hasDocument && hasLocation) {
                var localFileUploadView = new LocalFileUploadView({
                    el: this.$el.prevObject,
                    model: this.model,
                    info: this.info,
                    messageContainer: $('#app-notifications'),
                    parentView: this.steps[2],
                    form: this
                });
                localFileUploadView.render();
            }
        },

        connectCloud: function () {
            var location = $(this.steps[0].saveModel())[0].value;
            if (location === 'dropbox') {
                if (!this.dropboxView) {
                    this.dropboxView = new DropboxUploadView({
                        el: this.$el,
                        model: this.model,
                        info: this.info,
                        messageContainer: $('#app-notifications'),
                        parentView: this
                    });
                }
                this.dropboxView.render();
                this.dropboxView.doConnect();
            } else if (location === 'googledrive') {
                if (!this.googledriveView) {
                    this.googledriveView = new GoogleDriveUploadView({
                        el: this.$el,
                        model: this.model,
                        info: this.info,
                        messageContainer: $('#app-notifications'),
                        parentView: this
                    });
                }
                this.googledriveView.render();
                this.googledriveView.doConnect();

            } else if (location === 'onedrive') {
                if (!this.onedriveView) {
                    this.onedriveView = new OneDriveUploadView({
                        el: this.$el,
                        model: this.model,
                        info: this.info,
                        messageContainer: $('#app-notifications'),
                        parentView: this
                    });
                }
                this.onedriveView.render();
                this.onedriveView.doConnect();
            }

            this.onClose();
        },

        checkPopUp: function (view) {
            var self = this;
            var allowed = Utils.tryOpeningPopUp();
            var timeOutMilis = 500;
            setTimeout(function () {
                setTimeout(function () {	//seems like FF needs another setTimeout in order to make tryOpeningPopUp() work
                    if (allowed === false) {
                        $("body").trigger("europass:waiting:indicator:hide");
                        self.trigger("europass:message:show",
                                ["error", (Notification["skillspassport.wizard.cloud.popup.blocked"]), false]);
                        self.onClose();
                    } else {
                        $(view).trigger("europass:popup:enabled");
                    }
                }, timeOutMilis);
            }, timeOutMilis);
        }
    });

    return OneStepExportWizard;
});
