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
            'i18n!localization/nls/GuiLabelExtra',
            'hbs!templates/main/jobPortals',
            'hbs!templates/main/share/anpal',
            'hbs!templates/main/share/cvlibrary',
            'hbs!templates/main/share/eures',
            'hbs!templates/main/share/monster',
            'hbs!templates/main/share/xing',
            'hbs!templates/main/share/indeed',
            'europass/http/Resource',
            'europass/http/ServicesUri',
            'europass/http/WindowConfigInstance',
            'models/ShareCloudInfoModel',
            'europass/http/MediaType',
            'analytics/EventsController'
        ],
        function ($, _, jqueryui, Backbone, Utils, cookie, HttpUtils,
                Notification, GuiLabel, GuiLabelExtra, Template, AnpalTpl, CvLibraryTpl, EuresTpl, MonsterTpl, XingTpl, IndeedTpl,
                Resource, ServicesUri, WindowConfig, ShareCloudInfoModel, MediaType, Events) {

            var JobPortalsView = Backbone.View.extend({

                sectionEl: $("#share-document-btn")
                , infoSelector: "#PostToJobPortalsForm .job-portals-info"
                , optionsSelector: "#PostToJobPortalsForm .job-share-options"
                , overlaySelector: "#PostToJobPortalsForm"
                , defaultSelector: "#job-portals-default"
                , header: "#job-portals-default h2"
                , successSelector: "#job-portal-success"
                , errorSelector: "#job-portal-error"
                , errorMsgSelector: "#job-portal-warning-msg"

                , alreadyRendered: false

                , googleDriveShareLogged: false
                , dropboxShareLogged: false
                , onedriveShareLogged: false
                , event: new Events
                , events: {
                    "click #share-document-btn:not(.disabled)": "showJobPortalsForm",

                    "click #PostToJobPortalsForm button.close": "hideJobPortalsForm",
                    "click #PostToJobPortalsForm button.cancel.to-eures": "hideJobPortalsForm",
                    "click #btn-ok-to-job-portal": "hideJobPortalsForm",
                    "click #btn-cancel-share": "hideJobPortalsForm",

                    "click .ui-job-portal-logo-area .icon": "toggleJobPortalShare",

                    "click #btn-submit-to-eures:not(.disabled)": "doFinishEures",
                    "click #btn-submit-to-xing:not(.disabled)": "doFinishXing",
                    "click #btn-submit-to-monster:not(.disabled)": "doFinishMonster",
                    "click #btn-submit-to-cvLibrary:not(.disabled)": "doFinishCvLibrary",
                    "click #btn-submit-to-anpal:not(.disabled)": "doFinishAnpal",
                    "click #btn-submit-to-indeed:not(.disabled)": "doFinishIndeed",

                    "click #eures-terms-check": "toggleSubmitToJobPortalBtn",
                    "click #xing-terms-check": "toggleSubmitToJobPortalBtn",
                    "click #monster-terms-check": "toggleSubmitToMonsterBtn",
                    "click #cvLibrary-terms-check": "toggleSubmitToJobPortalBtn",
                    "click #indeed-terms-check": "toggleSubmitToJobPortalBtn",
                    "change #monster-country": "updateMonsterPolicies",
                    "keyup #fiscalCode": "toggleSubmitToAnpalControls",
                    "click #anpal-privacy-consent": "toggleSubmitToAnpalControls",

                    "click #btn-return-to-eures": "goToOptions",
                    "click .button.cancel.share-form": "goToOptions",

                    "europass:share:response:success": "triggerResponseSuccess",
                    "europass:share:response:error": "triggerResponseError",

                    "europass:share:popups:blocked": "triggerBlockedPopupsNotification",
                    "click .ui-job-portal-area .message-area a": "triggerResponseSuccess",

                    "europass:share:user:logged": "cloudMarkLogged"

                }
                /**
                 * Initialize
                 * @param options
                 */
                , initialize: function (options) {

                    this.messageContainer = options.messageContainer;
                    this.contextRoot = WindowConfig.getDefaultEwaEditorContext();
                    this.render();

                }
                , onClose: function () {
                    this.alreadyRendered = false;
                    this.close();
                }
                , render: function () {
                    var ctx = {};
                    ctx.shareXing = WindowConfig.showShareXingButton;
                    ctx.shareMonster = WindowConfig.showShareMonsterButton;
                    ctx.shareEures = WindowConfig.showShareEuresButton;
                    if (ewaLocale == 'en') {
                        ctx.shareCvLibrary = WindowConfig.showShareCvLibraryButton;
                    }
                    if (ewaLocale == 'it') {
                        ctx.shareAnpal = WindowConfig.showShareAnpalButton;
                    }
                    const indeedLocales = ['en', 'es', 'cs', 'da', 'de', 'el', 'fi', 'fr', 'it', 'hu', 'nl', 'nb', 'pl', 'pt', 'ro', 'sv', 'tr'];
                    if (_.contains(indeedLocales, ewaLocale)) {
                        ctx.shareIndeed = WindowConfig.showShareIndeedButton;
                    }

                    ctx.noOfSharePortalsVisible = 1;
                    if (ctx.shareXing) {
                        ctx.noOfSharePortalsVisible++;
                    }
                    if (ctx.shareMonster) {
                        ctx.noOfSharePortalsVisible++;
                    }
                    if (ctx.shareEures) {
                        ctx.noOfSharePortalsVisible++;
                    }
                    if (ctx.shareCvLibrary) {
                        ctx.noOfSharePortalsVisible++;
                    }
                    if (ctx.shareAnpal) {
                        ctx.noOfSharePortalsVisible++;
                    }
                    if (ctx.shareIndeed) {
                        ctx.noOfSharePortalsVisible++;
                    }
                    ctx.locale = ewaLocale;

                    this.$el.append(Template(ctx));

                    this.infoArea = this.$el.find(this.infoSelector);
                    this.optionsArea = this.$el.find(this.optionsSelector);
                    this.overlay = this.$el.find(this.overlaySelector);
                    this.errorDrawer = this.$el.find(this.errorSelector);
                    this.successDrawer = this.$el.find(this.successSelector);
                    this.defaultDrawer = this.$el.find(this.defaultSelector);
                    this.header = this.$el.find(this.header);
                    this.errorMsg = this.$el.find(this.errorMsgSelector);
                }
                , renderDefault: function () {
                    this.errorDrawer.children().addBack().hide();
                    this.successDrawer.children().addBack().hide();
                    this.defaultDrawer.children().addBack().show();

                }
                , renderState: function (success, isMonsterShare) {

                    //this.defaultDrawer.children().addBack().show();
                    this.header.hide();
                    $("#job-portals-default p").first().hide();
                    this.deselectJobPortals();
                    this.optionsArea.hide();
                    if (success === true) {
                        this.errorDrawer.children().addBack().hide();
                        this.successDrawer.children().addBack().show();
                        if (!isMonsterShare) {
                            this.successDrawer.find('.monster-success-info').hide();
                        } else {
                            this.successDrawer.find('.monster-success-info').show();
                        }
                    } else {
                        this.successDrawer.children().addBack().hide();
                        this.errorDrawer.children().addBack().show();
                    }
                }
                //Can this be removed?
                , cloudMarkLogged: function (event, cloudService) {

                    switch (cloudService) {

                        case "googledrive" :
                            this.googleDriveShareLogged = true;
                            break;
                        case "dropbox" :
                            this.dropboxShareLogged = true;
                            break;
                        case "onedrive" :
                            this.onedriveShareLogged = true;
                            break;
                        default:
                            break;
                    }

                    var elem = $(this.el).find("button#btn-share-" + cloudService);
                    elem.addClass("orange");
                }
                , showJobPortalsForm: function (event) {
                    //this.arrangeLocalStorage();
                    $('body').addClass('modal_overlay_open');
                    var _that = this;
                    var _overlay = this.overlay;
                    var _area = this.infoArea;
                    var children = (_area !== undefined ? _area.children() : undefined);
                    _that.selectFirstJobPortal();

                    //$("body").trigger("europass:waiting:indicator:show");
                    _overlay.toggleClass("visible", function () {
                        $(this).animate({"background-color": "rgba(0,0,0,0.7)"}, 400);
                        if (children !== undefined) {

                            if (!Utils.checkCookie()) {
                                $("body").trigger("europass:share:response:error", [-1, Notification["store.data.locally.not.supported"]]);
                            } else {
                                _area.find("#job-portals-default").children().addBack().css("display", "block");
                                _area.find(".ui-job-portals-area .on-error").children().addBack().css("display", "none");
                                _area.find(".ui-job-portals-area .on-success").children().addBack().css("display", "none");
                            }

                            children.addBack().show('slide', {direction: "right", easing: "easeInSine"}, 400, function () {
                                //Making the vertical overflow auto for overflow-y scrolling if needed, while the modal is open
                                //_area.css("overflow-y", "scroll");
                                _that.sectionEl.trigger("europass:drawer:opened");
                            });
                        }
                        $("body").trigger("europass:waiting:indicator:hide");

                    });

                }

                , hideJobPortalsForm: function (event) {
                    $('body').removeClass('modal_overlay_open');
                    var _that = this;
                    var _overlay = this.overlay;
                    var _area = this.infoArea;
                    var children = (_area !== undefined ? _area.children() : undefined);
                    if (children !== undefined) {
                        children.hide('slide', {direction: "right", easing: "easeInSine"}, 400);
                    }
                    _area.hide('slide', {direction: "right", easing: "easeInSine"}, 400);
                    _overlay.animate({"background-color": "rgba(0,0,0,0)"}, 400, function () {
                        $(this).toggleClass("visible");
                    });

                    // Event to be triggered once the modal closes so the share views are cleaned up by current data
                    $("body").trigger("europass:share:views:cleanup");
                }
                , goToOptions: function (event) {
                    /* EWA-1748 function for share TODO enhance it */
                    if ($(event.target).attr("id") === "btn-return-to-eures") {
                        this.errorDrawer.children().addBack().hide();
                        //this.$el.find("#share-for-review").children().addBack().css("display", "block");
                    }
                    this.$el.find("#btn-cancel-share").removeClass("non-visible");
                    this.header.show();
                    $("#job-portals-default p").first().show();
                    this.optionsArea.show();
                    this.selectFirstJobPortal();
                }
                , toggleJobPortalShare: function (event) {

                    var clickedTarget = $(event.target);
                    var selected = clickedTarget.attr('id');
                    this.selectJobPortal(selected);

                }
                /**
                 * 
                 * Highlight Job Portal and display appropriate content
                 */
                , selectJobPortal: function (selected) {
                    //Dehighlight any already selected/active portals and remove portal info from modal
                    this.deselectJobPortals();

                    //Highlight newly selected portal
                    var selectedPortalLogoArea = $(".ui-job-portal-logo-area .icon#" + selected);
                    selectedPortalLogoArea.parent().addClass("active");

                    var selectedPortalArea = $(".ui-job-portal-area#" + selected + "-default .job-portal");
                    $(selectedPortalArea).parent().addClass("active");

                    var portalTpl;
                    switch (selected) {
                        case 'eures':
                            portalTpl = EuresTpl;
                            break;
                        case 'monster':
                            portalTpl = MonsterTpl;
                            break;
                        case 'cvlibrary':
                            portalTpl = CvLibraryTpl;
                            break;
                        case 'xing':
                            portalTpl = XingTpl;
                            break;
                        case 'anpal':
                            portalTpl = AnpalTpl;
                            break;
                        case 'indeed':
                            portalTpl = IndeedTpl;
                            break;
                        default:
                            break;
                    }

                    //Append portal info to modal
                    var shareContent = this.$el.find('div.job-share-content');
                    shareContent.append(portalTpl);

                    //Check posting conditions
                    var emptyLearnerInfo = _.isEmpty(this.model.attributes.SkillsPassport.LearnerInfo);
                    var emptyCoverletter = _.isEmpty(this.model.attributes.SkillsPassport.CoverLetter);
                    var emptyAttachment = _.isEmpty(this.model.attributes.SkillsPassport.Attachment);
                    var emptyEmail = this.model.attributes.SkillsPassport.LearnerInfo === undefined
                            || this.model.attributes.SkillsPassport.LearnerInfo.Identification === undefined
                            || this.model.attributes.SkillsPassport.LearnerInfo.Identification.ContactInfo === undefined
                            || _.isEmpty(this.model.attributes.SkillsPassport.LearnerInfo.Identification.ContactInfo.Email);
                    var emptyPersonName = this.model.attributes.SkillsPassport.LearnerInfo === undefined
                            || this.model.attributes.SkillsPassport.LearnerInfo.Identification === undefined
                            || this.model.attributes.SkillsPassport.LearnerInfo.Identification.PersonName === undefined
                            || _.isEmpty(this.model.attributes.SkillsPassport.LearnerInfo.Identification.PersonName.FirstName)
                            || _.isEmpty(this.model.attributes.SkillsPassport.LearnerInfo.Identification.PersonName.Surname);
                    var emptyCountryCode = this.model.attributes.SkillsPassport.LearnerInfo === undefined
                            || this.model.attributes.SkillsPassport.LearnerInfo.Identification === undefined
                            || this.model.attributes.SkillsPassport.LearnerInfo.Identification.ContactInfo === undefined
                            || this.model.attributes.SkillsPassport.LearnerInfo.Identification.ContactInfo.Address === undefined
                            || this.model.attributes.SkillsPassport.LearnerInfo.Identification.ContactInfo.Address.Contact === undefined
                            || this.model.attributes.SkillsPassport.LearnerInfo.Identification.ContactInfo.Address.Contact.Country === undefined
                            || _.isEmpty(this.model.attributes.SkillsPassport.LearnerInfo.Identification.ContactInfo.Address.Contact.Country.Code);

                    //this.$el.find("#job-portals-default .post").show();
                    shareContent.find(".ui-job-portal-area .feedback-area .message-area p").html("");
                    shareContent.find(".ui-job-portal-area .feedback-area").css("display", "none");

                    this.$el.find("#job-portals-default .warning").hide();

                    var emptyCV = emptyLearnerInfo && emptyCoverletter && emptyAttachment;

                    switch (selected) {
                        case 'eures':
                            if (emptyCV) {
                                this.$el.find('div.ui-terms').hide();
                                this.$el.find("#job-portals-default .button.submit").addClass("disabled");
                                this.$el.find("#job-portals-default .button.submit").show();
                            } else {
                                this.$el.find('div.ui-terms').show();
                                this.$el.find('div.ui-terms input[type="checkbox"]').prop('checked', false);
                                this.$el.find("#job-portals-default .button.submit").hide();
                            }
                            break;
                        case 'monster':

                            if (emptyCV || emptyEmail || emptyPersonName || emptyCountryCode) {

                                this.$el.find("#job-portals-default .emailNote").show();
                                this.$el.find("#ui-monster-terms").hide();
                                this.$el.find("#job-portals-default .post").hide();
                                this.$el.find("#job-portals-default .button.submit").addClass("disabled");
                                this.$el.find("#job-portals-default .button.submit").show();

                            } else {

                                this.$el.find("#job-portals-default .emailNote").hide();
                                this.$el.find("#ui-monster-terms").hide();
                                this.$el.find("div.monster-submit-controls").hide();
                                this.$el.find("#job-portals-default .button.submit").addClass("disabled");
                                this.$el.find("#job-portals-default .button.submit").show();

                                if (!emptyEmail && !emptyPersonName && !emptyCountryCode) {

                                    $.ajax({
                                        url: ServicesUri.document_conversion_to.check_cv_size,
                                        type: "POST",
                                        data: {
                                            json: this.model.conversion().toTransferable()
                                        },
                                        success: function (result) {
                                            var fullCvSize = parseInt(result.fullCvSize);
                                            var plainCvSize = parseInt(result.plainCvSize); //no attachments

                                            //Monster is currently raising the limit of CV files from 500KB to 5MB 
                                            //(with still a 500KB limit for the text part of the file)
                                            if (plainCvSize <= 512000) {
                                                $("body").find("#ui-monster-terms").show();
                                                $("body").find(".monster-submit-controls").hide();
                                                if ($("body").find("#monster-country option:selected").val() === "-1") {
                                                    $("body").find("#monster-country").trigger("change");
                                                }
                                                if (fullCvSize > 5242880) {
                                                    $("body").find("#monster-default .sizeNotePostNoAttachments").show();
                                                }
                                            } else {
                                                $("body").find("#monster-default .post").hide();
                                                $("body").find("#monster-default .sizeNoteNoPost").show();
                                            }
                                        },
                                        error: function (xhr, ajaxOptions, thrownError) {
                                        },
                                        complete: function () {
                                            $("body").find("#monster-cv-searchable").prop('checked', false);
                                            $("body").find("#monster-terms-check").prop('checked', false);
                                        }
                                    });
                                }
                            }
                            break;
                        case 'cvlibrary':
                            if (emptyCV || emptyEmail || emptyPersonName) {
                                this.$el.find('div.ui-terms').hide();
                                this.$el.find("#job-portals-default .emailNote").show();
                                this.$el.find("#job-portals-default .post").hide();
                                this.$el.find("#job-portals-default .button.submit").addClass("disabled");
                                this.$el.find("#job-portals-default .button.submit").show();

                            } else {
                                this.$el.find('div.ui-terms').show();
                                this.$el.find('div.ui-terms input[type="checkbox"]').prop('checked', false);
                                this.$el.find("#job-portals-default .button.submit").hide();
                            }
                            break;
                        case 'xing':
                            if (emptyCV || emptyEmail || emptyPersonName) {
                                this.$el.find('div.ui-terms').hide();
                                this.$el.find("#job-portals-default .emailNote").show();
                                this.$el.find("#job-portals-default .post").hide();
                                this.$el.find("#job-portals-default .button.submit").addClass("disabled");
                                this.$el.find("#job-portals-default .button.submit").show();

                            } else {
                                this.$el.find('div.ui-terms').show();
                                this.$el.find('div.ui-terms input[type="checkbox"]').prop('checked', false);
                                this.$el.find("#job-portals-default .button.submit").hide();
                            }
                            break;
                        case 'anpal':

                            if (emptyCV || emptyEmail || emptyPersonName) {

                                this.$el.find("#job-portals-default .emailNote").show();
                                this.$el.find("#job-portals-default .post").hide();
                                this.$el.find("#ui-anpal-terms").hide();
                                this.$el.find("#job-portals-default .button.submit").addClass("disabled");
                                this.$el.find("#job-portals-default .button.submit").show();

                            } else {
                                this.$el.find("#ui-anpal-terms").show();
                                this.$el.find("#anpal-default .anpal-submit-controls").hide();
                                this.$el.find("#job-portals-default .button.submit").addClass("disabled");
                                this.$el.find("#job-portals-default .button.submit").show();
                            }
                            break;
                        case 'indeed':
                            if (emptyCV || emptyEmail || emptyPersonName) {
                                this.$el.find('div.ui-terms').hide();
                                this.$el.find("#job-portals-default .emailNote").show();
                                this.$el.find("#job-portals-default .post").hide();
                                this.$el.find("#job-portals-default .button.submit").addClass("disabled");
                                this.$el.find("#job-portals-default .button.submit").show();
                            } else {
                                this.$el.find('div.ui-terms').show();
                                this.$el.find('div.ui-terms input[type="checkbox"]').prop('checked', false);
                                this.$el.find("#job-portals-default .button.submit").hide();
                            }
                            break
                        default:
                            break;
                    }
                }
                , selectFirstJobPortal: function () {
                    var firstJobPortalIcon = $(".ui-job-portal-logo-area .icon")[0];
                    var selectedJobPortal = $(firstJobPortalIcon).attr('id');
                    this.selectJobPortal(selectedJobPortal);
                }
                , deselectJobPortals: function () {
                    //Dehighlight any already selected/active portals and remove portal info from modal
                    $('.ui-job-portal-logo-area').each(function (id, item) {
                        $(item).removeClass('active');
                    });
                    $('.ui-job-portal-area').each(function (id, item) {
                        $(item).removeClass('active');
                    });
                    var shareContent = this.$el.find('div.job-share-content');
                    shareContent.html('');
                }
                , restoreJobPortalsShares: function () {
                    this.$el.find(".ui-job-portals-area .job-share-content").html();
                    this.$el.find(".ui-job-portal-logo-area").removeClass("active");
                    this.$el.find(".ui-job-portal-area").removeClass("active");
                }
                /**
                 * When Post to Eures is clicked
                 */
                , doFinishEures: function () {
                    //this.parentView.cleanupFeedback();
                    this.$el.trigger("europass:waiting:indicator:show", true);
                    this.postToEures();
                }
                /**
                 * When Post to XING is clicked
                 */
                , doFinishXing: function () {
                    //this.parentView.cleanupFeedback();
                    this.$el.trigger("europass:waiting:indicator:show", true);
                    this.postToXing();
                }
                , toggleSubmitToJobPortalBtn: function (event) {
                    var checkboxClicked = $(event.target);
                    var postToButton = checkboxClicked.parents('div.ui-terms').next('button');
                    if (checkboxClicked.prop('checked')) {
                        postToButton.removeClass("disabled");
                        postToButton.show();
                    } else {
                        postToButton.hide();
                        postToButton.addClass("disabled");
                    }
                }
                , toggleSubmitToMonsterBtn: function () {
                    if (this.$el.find("#monster-terms-check").prop('checked')) {
                        if (!($("#PostToJobPortalsForm #monster-cv-searchable").is(':disabled'))) {
                            this.$el.find("div.monster-submit-controls").show();
                        }
                        this.$el.find("button.monster-submit-controls").show();
                        this.$el.find("#btn-submit-to-monster").removeClass("disabled");
                    } else {
                        this.$el.find(".monster-submit-controls").hide();
                        this.$el.find("#btn-submit-to-monster").addClass("disabled");
                    }
                }
                , toggleSubmitToAnpalControls: function (event) {
                    this.$el.find("#fiscalCode").val(this.$el.find("#fiscalCode").val().toUpperCase());
                    var fiscalCode = this.$el.find("#fiscalCode").val();
                    var fiscalCodeValid = (fiscalCode !== '') ? this.isFiscalCodeValid(fiscalCode) : false;

                    if (!fiscalCodeValid) {
                        this.$el.find("#anpal-default .ficalCodeNote").show();
                        this.$el.find(".anpal-submit-controls").hide();
                        this.$el.find("#btn-submit-to-anpal").addClass("disabled");
                    } else {
                        this.$el.find("#anpal-default .ficalCodeNote").hide();

                        if (this.$el.find("#anpal-privacy-consent").prop('checked')) {
                            this.$el.find(".anpal-submit-controls").show();
                            this.$el.find("#btn-submit-to-anpal").removeClass("disabled");
                        } else {
                            this.$el.find(".anpal-submit-controls").hide();
                            this.$el.find("#btn-submit-to-anpal").addClass("disabled");
                        }
                    }
                }
                , isFiscalCodeValid: function (fiscalCode) {

                    if (!/^[A-Z]{6}\d{2}[A-Z]\d{2}[A-Z]\d{3}[A-Z]$/.test($.trim(fiscalCode))) {
                        return false;
                    }
                    var set1 = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ";
                      	var set2 = "ABCDEFGHIJABCDEFGHIJKLMNOPQRSTUVWXYZ";
                      	var seteven = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
                      	var setodd = "BAKPLCQDREVOSFTGUHMINJWZYX";
                      	var s = 0;
                    for (i = 1; i <= 13; i += 2) {
                        s += seteven.indexOf(set2.charAt(set1.indexOf(fiscalCode.charAt(i))));
                    }
                    for (i = 0; i <= 14; i += 2) {
                        s += setodd.indexOf(set2.charAt(set1.indexOf(fiscalCode.charAt(i))));
                    }
                    if (s % 26 != fiscalCode.charCodeAt(15) - 'A'.charCodeAt(0)) {
                        return false;
                    }
                    //+Check if in the first 6 characters of the fiscal code there are one or more letters of the name and lastname.
                    return true;
                }
                , updateMonsterPolicies: function () {
                    //policies checkbox links
                    var monsterPolicies = {UK: {tou: "http://inside.monster.co.uk/terms-of-use", pp: "http://inside.monster.co.uk/privacy/home.aspx", c: "http://inside.monster.co.uk/cookie-info/inside2.aspx"}, FR: {tou: "http://donnees.monster.fr/conditions-utilisation", pp: "http://donnees.monster.fr/charte/inside2.aspx", c: "http://donnees.monster.fr/cookie-info/inside2.aspx"}, NL: {tou: "http://inside.monsterboard.nl/voorwaarden", pp: "http://inside.monsterboard.nl/verklaring/inside2.aspx", c: "http://inside.monsterboard.nl/cookie-info/inside2.aspx"}, DE: {tou: "http://datenschutz.monster.de/nutzungsbedingungen", pp: "http://datenschutz.monster.de/datenschutz/home.aspx", c: "http://datenschutz.monster.de/cookie-info/inside2.aspx"}, SE: {tou: "http://integritet.monster.se/villkor", pp: "http://integritet.monster.se/integritet/home.aspx", c: "http://integritet.monster.se/cookie-info/inside2.aspx"}, ES: {tou: "http://datos.monster.es/condiciones", pp: "http://datos.monster.es/politica/inside2.aspx", c: "http://datos.monster.es/cookie-info/inside2.aspx"}, CZ: {tou: "http://inside.monster.cz/terms-of-use", pp: "http://inside.monster.cz/policy/inside2.aspx", c: "http://inside.monster.cz/cookie-info/inside2.aspx"}, FI: {tou: "http://yksityisyys.monster.fi/terms-of-use", pp: "http://yksityisyys.monster.fi/policy/inside2.aspx", c: "http://yksityisyys.monster.fi/cookie-info/inside2.aspx"}, AT: {tou: "http://datenschutz.monster.at/nutzungsbedingungen", pp: "http://datenschutz.monster.at/datenschutz/home.aspx", c: "http://datenschutz.monster.at/cookie-info/inside2.aspx"}, IE: {tou: "http://inside.monster.ie/terms-of-use", pp: "http://inside.monster.ie/privacy/home.aspx", c: "http://inside.monster.ie/cookie-info/inside2.aspx"}, IT: {tou: "http://archivio.monster.it/termini", pp: "http://archivio.monster.it/politica/inside2.aspx", pd: "http://strumenti.monster.it/informativa-privacy-candidato/home.aspx", c: "http://archivio.monster.it/cookie-info/inside2.aspx"}, BE: {tou: "http://inside.monster.be/terms-of-use", pp: "http://inside.monster.be/policy/inside2.aspx", c: "http://inside.monster.be/cookie-info/inside2.aspx"}, LU: {tou: "http://inside.monster.lu/terms-of-use", pp: "http://inside.monster.lu/policy/inside2.aspx", c: "http://inside.monster.lu/cookie-info/inside2.aspx"}, CH: {tou: "http://datenschutz.monster.ch/nutzungsbedingungen", pp: "http://datenschutz.monster.ch/datenschutz/home.aspx", c: "http://datenschutz.monster.ch/cookie-info/inside2.aspx"}};

                    if (this.$el.find("#monster-country option:selected").val() === "-1") {
                        this.$el.find('#monster-country option[value="-1"]').attr("disabled", "disabled");

                        var countryCode = this.model.attributes.SkillsPassport.LearnerInfo.Identification.ContactInfo.Address.Contact.Country.Code;
                        if (this.$el.find('#monster-country option[value="' + countryCode + '"]').length > 0) {
                            this.$el.find('#monster-country option[value="' + countryCode + '"]').prop('selected', true);
                        } else {
                            this.$el.find('#monster-country option:last').prop('selected', true);
                        }
                    }
                    var selectedCountryOption = this.$el.find("#monster-country option:selected");
                    var selectedCountry = selectedCountryOption.val();

                    var touUrlKey = monsterPolicies[selectedCountry]["tou"];
                    this.$el.find("#ui-monster-terms a.tou").attr("href", touUrlKey);

                    var privacyPolicyUrlKey = monsterPolicies[selectedCountry]["pp"];
                    this.$el.find("#ui-monster-terms a.privacy-policy").attr("href", privacyPolicyUrlKey);

                    var privacyDisclaimerUrlKey = monsterPolicies[selectedCountry]["pd"];
                    this.$el.find("#ui-monster-terms a.privacy-disclaimer").attr("href", privacyDisclaimerUrlKey);
                    if (selectedCountry === "IT") {
                        this.$el.find("span#privacy-disclaimer").show();
                    } else {
                        this.$el.find("span#privacy-disclaimer").hide();
                    }

                    var useOfCookiesUrlKey = monsterPolicies[selectedCountry]["c"];
                    this.$el.find("#ui-monster-terms a.use-of-cookies").attr("href", useOfCookiesUrlKey);

                    //searchable checkbox
                    if (selectedCountry === "DE" || selectedCountry === "AT") {
                        $("#PostToJobPortalsForm #monster-cv-searchable").attr("disabled", "disabled");
                        $("#PostToJobPortalsForm #monster-cv-searchable").parent("div").hide();
                    } else {
                        $("#PostToJobPortalsForm #monster-cv-searchable").removeAttr("disabled");
                        if (this.$el.find("#monster-terms-check").prop('checked')) {
                            $("#PostToJobPortalsForm #monster-cv-searchable").parent("div").show();
                        }
                    }
                }
                /**
                 * When Post to MONSTER button is clicked
                 */
                , doFinishMonster: function () {
                    if (this.$el.find("#monster-terms-check").prop('checked')) {
                        //this.parentView.cleanupFeedback();
                        this.$el.trigger("europass:waiting:indicator:show", true);
                        this.postToMonster();

                    }
                }
                /**
                 * When Post to CV-Library is clicked
                 */
                , doFinishCvLibrary: function () {
                    this.$el.trigger("europass:waiting:indicator:show", true);
                    this.postToCvLibrary();
                }
                /**
                 * When Post to Ιnfocamere is clicked
                 */
                , doFinishAnpal: function () {
                    this.$el.trigger("europass:waiting:indicator:show", true);
                    this.postToAnpal();
                }
                /**
                 * When Post to Indeed is clicked
                 */
                , doFinishIndeed: function () {
                    this.$el.trigger("europass:waiting:indicator:show", true);
                    this.postToIndeed();
                }
                /**
                 * Do the actual Eures POST
                 */
                , postToEures: function () {

                    var text = "DOWNLOAD-" + new Date().getTime(); //Download token
                    var activeDownloadToken = Utils.hashCode(text) + "-" + Utils.randomInK();
                    this.event.postTo('Post to Eures');

                    $.ajax({
                        url: ServicesUri.document_conversion_to.post_to_eures,
                        type: "POST",
                        data: {
                            json: this.model.conversion().toTransferable(),
                            downloadToken: activeDownloadToken
                        },
                        success: function (result) {

                            var parsed = null;

                            try {
                                parsed = JSON.parse(result);
                            } catch (e) {
                                $("body").trigger("europass:waiting:indicator:hide");
                                throw new Error("Result is not parsable.");
                            }

                            if (parsed && parsed.token && parsed.url) { //trigger redirect, TODO tidy up + checks
                                //setTimeout( function(){},timeOutMillis );
                                var euresRedirect = parsed.url + parsed.token + "?lang=" + ewaLocale;
                                var euresTab = window.open(euresRedirect, '_blank');

                                //if popup was blocked, show a message
                                if (euresTab === undefined || (euresTab === null && Utils.tryOpeningPopUp() === false)/*$.isEmptyObject( euresTab )*/) {

                                    var msg = Notification["eures.post.redirection"] || "<a href='[[redirectURL]]'>Click here</a> to proceed to Eures.";
                                    msg = msg.replace("[[redirectURL]]", euresRedirect);

                                    $("body").trigger("europass:share:popups:blocked", ["eures-warning-msg", msg]);
                                } else {
                                    $("body").trigger("europass:share:response:success");
                                }
                            }
                        },
                        error: function (xhr, ajaxOptions, thrownError) {

                            //Extract the JSON response from the response text
                            var parsed = {};

                            try {
                                if (!_.isUndefined(xhr.responseText)) {
                                    var json = xhr.responseText.split('<script type="application/json">')[1].split("</script>")[0];
                                    parsed = JSON.parse(json);
                                }

                            } catch (e) {
                                $("body").trigger("europass:waiting:indicator:hide");
                            }

                            var errCode = "";

                            if (!_.isUndefined(parsed.Error)) {
                                if (!_.isUndefined(parsed.Error.trace))
                                    errCode = parsed.Error.trace;
                            }

                            $("body").trigger("europass:share:response:error", [xhr.status, errCode]);
                        },
                        complete: function () {
                            $("body").trigger("europass:waiting:indicator:hide");
                        }
                    });

                }
                /**
                 * Do the actual XING POST
                 */
                , postToXing: function () {
                    this.event.postTo('Post to Xing');
                    var text = "DOWNLOAD-" + new Date().getTime(); //Download token
                    var activeDownloadToken = Utils.hashCode(text) + "-" + Utils.randomInK();

                    var _that = this;

                    $.ajax({
                        url: ServicesUri.document_conversion_to.post_to_xing,
                        type: "POST",
                        data: {
                            json: this.model.conversion().toTransferable(),
                            downloadToken: activeDownloadToken
                        },
                        success: function (result) {

                            var parsed = null;

                            try {
                                parsed = JSON.parse(result);
                            } catch (e) {
                                $("body").trigger("europass:waiting:indicator:hide");
                                throw new Error("Result is not parsable.");
                            }

                            if (parsed && parsed.token && parsed.url) {	 //trigger redirect , TODO tidy up + checks
                                //setTimeout( function(){},timeOutMillis );
                                var xingRedirect = parsed.url; // + parsed.token;
                                var xingTab = window.open(xingRedirect, '_blank');

                                //if popup was blocked, show a message
                                if (xingTab === undefined || (xingTab === null && Utils.tryOpeningPopUp() === false)) {

                                    //var msg = Notification["xing.post.redirection"] || "<a href='[[redirectURL]]'>Click here</a> to proceed to XING.";
                                    var msg = "Access to the XING portal was denied by your browser's pop-up blocker. <a href=\"[[redirectURL]]\" target=\"_blank\">Click here</a> to try to load the page." || "<a href='[[redirectURL]]'>Click here</a> to proceed to XING.";
                                    msg = msg.replace("[[redirectURL]]", xingRedirect);

                                    $("body").trigger("europass:share:popups:blocked", ["xing-warning-msg", msg]);
                                } else {
                                    $("body").trigger("europass:share:response:success");
                                }
                            }
                        },
                        error: function (xhr, ajaxOptions, thrownError) {

                            //Extract the JSON response from the response text
                            var parsed = {};

                            try {
                                if (!_.isUndefined(xhr.responseText)) {
                                    var json = xhr.responseText.split('<script type="application/json">')[1].split("</script>")[0];
                                    parsed = JSON.parse(json);
                                }

                            } catch (e) {
                                $("body").trigger("europass:waiting:indicator:hide");
                            }

                            var errCode = "";
                            var errMessage = "";

                            if (!_.isUndefined(parsed.Error)) {
                                if (!_.isUndefined(parsed.Error.trace)) {
                                    errCode = parsed.Error.trace;
                                }
                                if (!_.isUndefined(parsed.Error.message)) {
                                    errMessage = parsed.Error.message;
                                }
                            }

                            $("body").trigger("europass:share:response:error", [xhr.status, errCode, errMessage]);
                        },
                        complete: function () {
                            $("body").trigger("europass:waiting:indicator:hide");
                        }
                    });

                }
                /**
                 * Do the actual MONSTER POST
                 */
                , postToMonster: function () {
                    var text = "DOWNLOAD-" + new Date().getTime(); //Download token
                    var activeDownloadToken = Utils.hashCode(text) + "-" + Utils.randomInK();
                    var makeCvSearchable = this.$el.find("#monster-cv-searchable").prop('checked');
                    var monsterCountry = this.$el.find("#monster-country option:selected").val();
                    this.event.postTo('Post to Monster');
                    var _that = this;

                    $.ajax({
                        url: ServicesUri.document_conversion_to.post_to_monster,
                        type: "POST",
                        data: {
                            json: this.model.conversion().toTransferable(),
                            downloadToken: activeDownloadToken,
                            isCvPublic: makeCvSearchable,
                            monsterCountry: monsterCountry
                        },
                        success: function (result) {

                            var parsed = null;

                            try {
                                parsed = JSON.parse(result);
                            } catch (e) {
                                $("body").trigger("europass:waiting:indicator:hide");
                                throw new Error("Result is not parsable.");
                            }

                            $("body").trigger("europass:share:response:success", [true]);
                        },
                        error: function (xhr, ajaxOptions, thrownError) {

                            //Extract the JSON response from the response text
                            var parsed = {};

                            try {
                                if (!_.isUndefined(xhr.responseText)) {
                                    var json = xhr.responseText.split('<script type="application/json">')[1].split("</script>")[0];
                                    parsed = JSON.parse(json);
                                }

                            } catch (e) {
                                $("body").trigger("europass:waiting:indicator:hide");
                            }

                            var errCode = "";

                            if (!_.isUndefined(parsed.Error)) {
                                if (!_.isUndefined(parsed.Error.trace))
                                    errCode = parsed.Error.trace;
                            }

                            $("body").trigger("europass:share:response:error", [xhr.status, errCode]);
                        },
                        complete: function () {
                            $("body").trigger("europass:waiting:indicator:hide");
                        }
                    });

                }
                /**
                 * Do the actual CV-Library POST
                 */
                , postToCvLibrary: function () {
                    this.event.postTo('Post to CV-Library');
                    var text = "DOWNLOAD-" + new Date().getTime(); //Download token
                    var activeDownloadToken = Utils.hashCode(text) + "-" + Utils.randomInK();

                    $.ajax({
                        url: ServicesUri.document_conversion_to.post_to_cvLibrary,
                        type: "POST",
                        data: {
                            json: this.model.conversion().toTransferable(),
                            downloadToken: activeDownloadToken
                        },
                        success: function (result) {
                            var parsed = null;
                            try {
                                parsed = JSON.parse(result);
                                if (parsed.error) {
                                    $("body").trigger("europass:share:response:error", [400, Notification["cvLibrary.already.registered"]]);
                                } else {
                                    $("body").trigger("europass:share:response:success");
                                }
                            } catch (e) {
                                $("body").trigger("europass:waiting:indicator:hide");
                                throw new Error("Result is not parsable.");
                            }
                        },
                        error: function (xhr, ajaxOptions, thrownError) {
                            //Extract the JSON response from the response text
                            var parsed = {};

                            try {
                                if (!_.isUndefined(xhr.responseText)) {
                                    var json = xhr.responseText.split('<script type="application/json">')[1].split("</script>")[0];
                                    parsed = JSON.parse(json);
                                }

                            } catch (e) {
                                $("body").trigger("europass:waiting:indicator:hide");
                            }

                            var errCode = "";
                            var errMessage = "";

                            if (!_.isUndefined(parsed.Error)) {
                                if (!_.isUndefined(parsed.Error.trace)) {
                                    errCode = parsed.Error.trace;
                                }
                                if (!_.isUndefined(parsed.Error.message)) {
                                    errMessage = parsed.Error.message;
                                }
                            }

                            $("body").trigger("europass:share:response:error", [xhr.status, errCode, errMessage]);
                        },
                        complete: function () {
                            $("body").trigger("europass:waiting:indicator:hide");
                        }
                    });

                }
                /**
                 * Do the actual Ιnfocamere POST
                 */
                , postToAnpal: function () {
                    this.event.postTo('Post to ANPAL-UNIONCAMERE');
                    var fiscalCode = this.$el.find("#fiscalCode").val();
                    var extraUEConsent = this.$el.find("#anpal-extra-ue-consent").prop('checked');

                    $.ajax({
                        url: ServicesUri.document_conversion_to.post_to_anpal,
                        type: "POST",
                        data: {
                            json: this.model.conversion().toTransferable(),
                            fiscalCode: fiscalCode,
                            extraUEConsent: extraUEConsent
                        },
                        success: function (result) {
                            var parsed = null;
                            try {
                                parsed = JSON.parse(result);
                                if (parsed.status == 'EXCEPTION') {
                                    var msg = parsed.data.message;
                                    $("body").trigger("europass:share:response:error", [-1, msg]);
                                } else {
                                    $("body").trigger("europass:share:response:success");
                                }
                            } catch (e) {
                                $("body").trigger("europass:waiting:indicator:hide");
                                throw new Error("Result is not parsable.");
                            }
                        },
                        error: function (xhr, ajaxOptions, thrownError) {
                            //Extract the JSON response from the response text
                            var parsed = {};

                            try {
                                if (!_.isUndefined(xhr.responseText)) {
                                    var json = xhr.responseText.split('<script type="application/json">')[1].split("</script>")[0];
                                    parsed = JSON.parse(json);
                                }

                            } catch (e) {
                                $("body").trigger("europass:waiting:indicator:hide");
                            }

                            var errCode = "";
                            var errMessage = "";

                            if (!_.isUndefined(parsed.Error)) {
                                if (!_.isUndefined(parsed.Error.trace)) {
                                    errCode = parsed.Error.trace;
                                }
                                if (!_.isUndefined(parsed.Error.message)) {
                                    errMessage = parsed.Error.message;
                                }
                            }

                            $("body").trigger("europass:share:response:error", [xhr.status, errCode, errMessage]);
                        },
                        complete: function () {
                            $("body").trigger("europass:waiting:indicator:hide");
                        }
                    });
                }
                /**
                 * Do the actual Indeed POST
                 */
                , postToIndeed: function () {
                    var text = "DOWNLOAD-" + new Date().getTime(); // Download token
                    var activeDownloadToken = Utils.hashCode(text) + "-" + Utils.randomInK();
                    this.event.postTo('Post to Indeed');

                    $.ajax({
                        url: ServicesUri.document_conversion_to.post_to_indeed,
                        type: "POST",
                        data: {
                            json: this.model.conversion().toTransferable(),
                            downloadToken: activeDownloadToken
                        },
                        success: function (result) {
                            var parsed = null;
                            try {
                                parsed = result;
                                $("body").trigger("europass:share:response:success");
                            } catch (e) {
                                $("body").trigger("europass:waiting:indicator:hide");
                                throw new Error("Result is not parsable.");
                            }
                        },
                        error: function (xhr, ajaxOptions, thrownError) {
                            //Extract the JSON response from the response text
                            var parsed = {};
                            try {
                                if (!_.isUndefined(xhr.responseText)) {
                                    var json = xhr.responseText.split('<script type="application/json">')[1].split("</script>")[0];
                                    parsed = JSON.parse(json);
                                }

                            } catch (e) {
                                $("body").trigger("europass:waiting:indicator:hide");
                            }

                            var errCode = "";

                            if (!_.isUndefined(parsed.Error)) {
                                if (!_.isUndefined(parsed.Error.trace))
                                    errCode = parsed.Error.trace;
                            }

                            $("body").trigger("europass:share:response:error", [xhr.status, errCode]);
                        },
                        complete: function () {
                            $("body").trigger("europass:waiting:indicator:hide");
                        }
                    });

                }
                , triggerResponseSuccess: function (event, isMonsterShare) {
                    this.renderState(true, isMonsterShare);
                }

                //TODO create a new parameter for the #button-return-to-eures so it will renderDefault or return to the share fr
                , triggerResponseError: function (event, error, errCode, errMessage) {
                    this.renderState(false);

                    var errorKey = "error.code.status500";

                    if (error === 401 || error === 403 || error === 404 || error === 500) {
                        errorKey = "error.code.status" + error;
                    } else if (error >= 400 && error < 500 && error !== 401) {
                        errorKey = "error.code.status400";
                    } else if (error === -1) {

                        var hdrTxtElem = this.$el.find("#job-portal-error .note.eures .text");
                        hdrTxtElem.html(GuiLabel["europass.shared.modal.error"]);

                        var btn = this.$el.find("button#btn-return-to-eures");
                        btn.html(GuiLabel["Buttons.OK"]);
                        btn.attr("id", "btn-ok-to-job-portal");
                    }

                    var msg = Notification["eures.post.error"] + (errMessage !== undefined ? " (" + errMessage + ")" : "");
                    /*.replace("<posturl>", WindowConfig.remoteUploadCallbackUrl)*/;

                    //this.messageContainer.trigger("europass:message:show", ["error", msg + Notification[errorKey]+ " " + errCode]);

                    this.errorMsg.find("p").empty();

                    if (error === undefined || error === -1)
                        this.errorMsg.find("p").append(errCode);
                    else
                        this.errorMsg.find("p").append(msg + ": " + Notification[errorKey] + " " + errCode);

                    //					this.parentView.setModalFeedbackClass( "error" );
                    $("body").trigger("europass:waiting:indicator:hide");
                },
                triggerBlockedPopupsNotification: function (event, warningDivId, errMessage) {
                    var warningDiv = this.$el.find("#" + warningDivId);
                    warningDiv.find(".message-area p").html(errMessage);
                    warningDiv.show();
                    warningDiv.parents(".job-portal").find(".button.submit").hide();
                }
            });

            return JobPortalsView;
        }
);