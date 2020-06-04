define(
        [
            'jquery',
            'underscore',
            'jqueryui',
            'backbone',
            'Utils',
            'cookie',
            'HttpUtils',
            'i18n!localization/nls/Notification',
            'i18n!localization/nls/GuiLabel',
            'hbs!templates/main/jobPortals',
            'hbs!templates/share/manageShares',
            'europass/http/Resource',
            'europass/http/ServicesUri',
            'europass/http/WindowConfigInstance',
            'views/download/DownloadController',
            'views/WizardProcess',
            'views/WizardStep',
            'models/ShareCloudInfoModel',
            'europass/http/MediaType'
        ],
        function ($, _, jqueryui, Backbone, Utils, cookie,
                HttpUtils, Notification, GuiLabel, Template, ManageTmpl, Resource, ServicesUri, WindowConfig,
                DownloadController, WizardProcess, WizardStep, ShareCloudInfoModel, MediaType) {

            var ManageSharesView = Backbone.View.extend({

                shareInfoSelector: "#PostToJobPortalsForm .job-portals-info"

                , manageSharesDrawerSelector: "#manage-share-drawer"
                , notificationRevokeSelector: "#manage-share-drawer #notification-revoke"
                , sharesHeaderSelector: "#manage-share-drawer header.manage-hdr"

                , closeShareSelector: "#btn-cancel-share"

                , alreadyRendered: false

                , googleDriveFolder: ""
                , dropboxFolder: ""
                , oneDriveFolder: ""

                , cloudProvider: ""

//				,currentFileId: ""

                , events: {

                    "europass:share:manage:folders": "toggleFolders",
                    "europass:share:manage:folders:reset": "resetFolders",

                    "europass:share:manage:render": "render",

                    "click .toggle-share-btn:not(.blue-pressed)": "toggleShareForms",
                    "click #revoke-btn": "toggleRevokeConfirm",

                    "click .reject-revoke-action": "rejectAction",
                    "click .confirm-revoke-action": "confirmAction"
                },

                /**
                 * Initialize
                 * @param options
                 */
                initialize: function (options) {

                    this.shareInfo = $(this.$el.find(this.shareInfoSelector));
                    this.manageShareDrawer = $(this.$el.find(this.manageSharesDrawerSelector));
                    this.notificationRevoke = $(this.$el.find(this.notificationRevokeSelector));
                    this.sharesHeader = $(this.$el.find(this.sharesHeaderSelector));

                    this.closeShareFrmBtn = $(this.$el.find(this.closeShareSelector));

                    this.contextRoot = WindowConfig.getDefaultEwaEditorContext();
                }

                , render: function (event, items) {

                    var context = {"provider": this.cloudProvider, "links": items};

                    var html = ManageTmpl(context);

                    $.each(this.sharesHeader.siblings(".manage-share-tile"), function (idx) {
                        $(this).addBack().remove();
                    });

                    $(html).insertAfter(this.sharesHeader);

                    this.sharesHeader.find("#share-cv-btn").attr("name", "btn-share-" + this.cloudProvider).css("display", "inline");
                    this.sharesHeader.find("#manage-shares-btn").attr("name", "btn-share-" + this.cloudProvider).css("display", "inline");
                    this.sharesHeader.css("display", "block");
                    this.manageShareDrawer.css("display", "block");

                    this.$el.find("#share-for-review").hide();
                }

                , toggleFolders: function (event, type, folder) {

                    this.cloudProvider = type;

                    switch (type) {

                        case "googledrive":
                            this.googleDriveFolder = folder;
                            break;
                        case "dropbox":
                            this.dropboxFolder = folder;
                            break;
                        case "onedrive":
                            this.oneDriveFolder = folder;
                            break;
                        default:
                            break;
                    }
                }

                , resetFolders: function () {
                    this.googleDriveFolder = "";
                    this.dropboxFolder = "";
                    this.oneDriveFolder = "";
                    this.cloudProvider = "";
                }

                , storeCurrentFileID: function (event, provider, fileId) {
                    this.currentFileId = fileId;
                }

                , hideTemp: function (temp) {
                    if (temp === "first") {
                        this.$el.find(".ui-eures-area.default").hide();
                        this.$el.find(".mid-divider").hide();
                        this.$el.find(".ui-cloud-area").hide();
                    }
                    if (temp === "second") {
                        this.$el.find("#share-for-review").hide();
                    }


                }
                , arrangeTypes: function (tp) {
                    var lbl = tp.slice(6);
                    this.$el.find(".share-option").find("input.formfield").attr("id", tp);
                    this.$el.find(".share-link-label").html(GuiLabel["share.cloud.link.label." + lbl]);
                }

                , goToOptions: function (event) {
                    /* EWA-1748 function for share TODO enhance it */
                    if ($(event.target).attr("id") === "btn-return-to-eures") {
                        this.errorDrawer.children().addBack().hide();
                        this.$el.find("#share-for-review").children().addBack().css("display", "block");
                    } else {
                        this.hideTemp("second");
                        this.$el.find(".ui-eures-area.default").children().addBack().show();
                        this.$el.find(".mid-divider").children().addBack().show();
                        this.$el.find(".ui-cloud-area").children().addBack().show();
                    }
                },

                toggleShareForms: function (event) {

                    $("body").trigger("europass:waiting:indicator:show");

                    var elem = $(event.currentTarget);
                    var shareId = elem.attr("id");

                    this.sharesHeader.find("#share-cv-btn").removeClass("blue-pressed");
                    this.sharesHeader.find("#manage-shares-btn").addClass("blue-pressed");

                    if (shareId === "share-cv-btn") {
                        this.manageShareDrawer.css("display", "none");
                        this.shareInfo.find("#btn-share-" + this.cloudProvider).trigger("click");
                        this.closeShareFrmBtn.addClass("non-visible");
                    } else if (shareId === "manage-shares-btn") {

                        var type = elem.attr("name").split("btn-share-")[1];

                        switch (type) {
                            case "googledrive":
                                $("body").trigger("europass:share:google:list", [this.googleDriveFolder]);
                                break;
                            case "dropbox":
                                $("body").trigger("europass:share:dropbox:list", [this.dropboxFolder]);
                                break;
                            case "onedrive":
                                $("body").trigger("europass:share:onedrive:list", [this.oneDriveFolder]);
                                break;
                            default:
                                break;
                        }
                    }
                }

                , rejectAction: function (event) {
                    var ev = $(event.target);
                    var bubble = ev.closest(".manage-action.link-delete");
                    if (bubble.length === 0)
                        return;
                    bubble.fadeToggle("slow", function () {
                        $(this).hide();
                    });
                }
                , confirmAction: function (event) {
                    var parent = $(event.currentTarget).closest(".manage-share-tile");
                    var siblings = parent.siblings(".manage-share-tile");
                    var fileId = parent.find(".manage-share-link").attr("file-id");

                    switch (this.cloudProvider) {

                        case "googledrive":
                            $("body").trigger("europass:share:google:delete", [fileId, parent]);
                            break;
                        case "dropbox":
                            $("body").trigger("europass:share:dropbox:delete", [fileId, parent]);
                            break;
                        case "onedrive":
                            $("body").trigger("europass:share:onedrive:delete", [fileId, parent]);
                            break;
                        default:
                            break;
                    }
                }

                , toggleRevokeConfirm: function (event) {
                    var revokeBtn = $(event.currentTarget);

                    var confirmBubble = revokeBtn.siblings(".manage-action.link-delete");
                    if (confirmBubble.length === 0)
                        return;

                    //find open attachment-menu-options and in-action prompts and toggle, so that only one menu item can be open
                    var otherActivePrompt = revokeBtn.closest('.manage-share-tile').siblings('.manage-share-tile').find('.manage-share-revoke.in-action');
                    if (otherActivePrompt.length > 0) {
                        var otherActiveOption = revokeBtn.closest('.manage-share-tile').siblings('.manage-share-tile').find('.manage-action.link-delete');
                        otherActiveOption.hide();
                        otherActivePrompt.toggleClass("in-action");
                    }

                    confirmBubble.toggle();

                    revokeBtn.toggleClass("in-action");
                }


            });

            return ManageSharesView;
        }
);