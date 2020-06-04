define(
        [
            'jquery',
            'underscore',
            'Utils',
            'views/compose/ComposeView',
            'hbs!templates/compose/cv/personalinfo',
            'europass/http/Resource',
            'europass/http/MediaType',
            'europass/http/SessionManagerInstance',
            'views/prefs/PrintingPreferencesView'//'i18n!localization/nls/Notification','europass/TabletInteractionsView'
        ],
        function ($, _, Utils, ComposeView, HtmlTemplate, Resource, MediaType, Session, PrintingPreferencesView) {

            var PersonalInfoComposeView = ComposeView.extend({
                htmlTemplate: HtmlTemplate

                , events: _.extend({
                    "click :button.photo.delete": "requestDeletePhoto",
                    // "touchstart :button.photo.delete": "requestDeletePhoto",
                    "click :button.names.switch": "handlePersonNameOrder"
                            // "touchstart :button.names.switch": "handlePersonNameOrder"
                }, ComposeView.prototype.events)

                , photoSection: "Photo"

                , prefsView: null

                , onInit: function (options) {
                    this.prefsView = new PrintingPreferencesView({
                        model: this.model
                    });
                    ComposeView.prototype.onInit.apply(this, [options]);

                    this.applyWhenMissingDocuments();
                }

                , onClose: function () {
                    ComposeView.prototype.onClose.apply(this);
                    if (_.isObject(this.prefsView) && _.isFunction(this.prefsView.close))
                        this.prefsView.close();
                    delete this.prefsView;
                }
                /**
                 * @Override
                 * Rerender if relSection equals this.section or Photo
                 */
                , reRender: function (relSection, origin) {
                    //First check if the related section updated is an indexed section
                    var listSection = Utils.getListSection(relSection);
                    if (listSection !== null) {
                        relSection = listSection;
                    }

                    if ((relSection === this.section) || (relSection === (this.section + "." + this.photoSection))) {
                        this.render(this.reRenderIndicator, [this.doTransition(origin)]);
                    }
                    this.applyWhenMissingDocuments();
                }
                /*requestDeletePhoto: check if tablet for hover effect then call do delete photo
                 * */
                , requestDeletePhoto: function (event) {

                    // TODO CLEAN UP COMMENT On mobile not using 2 step tooltip anymore !!
                    // if ((this.isTablet && _.isFunction(this.tablets.handleTipSpot)) && this.tablets.handleTipSpot(event, "currentTarget") === false) {
                    // 	return false;
                    // } else {
                    // 	this.doDeletePhoto(event);
                    // }
                    this.doDeletePhoto(event);

                }
                , doDeletePhoto: function (event) {
                    var btn = $(event.target);
                    var section = btn.attr("data-rel-section");
                    var relatedView = btn.attr("data-rel-view");
                    btn.trigger("europass:delete:requested", [relatedView, section]);
                }
                /**
                 * On confirmation of delete we need to run 
                 * proceedDeletion: function( event, section )
                 * as per the ComposeView
                 * 
                 *  @Override
                 */
                , proceedDeletion: function (event, section) {
                    event.stopPropagation();

                    //start the waiting indicator...
                    this.$el.trigger("europass:waiting:indicator:show");

                    this.removePhoto(section);
                }
                , updateRemovedPhoto: function (photoPath) {
                    //Delete Photo section - will trigger a re-render event
                    this.model.reset(photoPath);

                    $("#missing-documents-info").hide();

                    //Stop the wait indicator
                    this.$el.trigger("europass:waiting:indicator:hide");
                }
                , removePhoto: function (photoPath) {
                    var matchedPhoto = this.model.get(photoPath);
                    var status = false;
                    if (matchedPhoto !== null) {
                        //Delete service
                        var uri = matchedPhoto.TempURI;
                        if (uri === undefined || uri === null || uri === "") {
                            status = false;
                            return;
                        }

                        var resource = new Resource(uri);
                        resource._delete({
                            success: {
                                scope: this,
                                callback: function () {
                                    status = true;
                                    this.updateRemovedPhoto(photoPath);
                                }
                            },
                            error: {
                                scope: this,
                                callback: function (statusObj) {
                                    status = false;
                                    this.updateRemovedPhoto(photoPath);
                                }
                            }
                        });
                    } else {
                        status = false;
                    }
                    return status;
                }
                //update the Preferences
                , handlePersonNameOrder: function (event) {
                    if (this.prefsView !== undefined && this.prefsView !== null) {

                        // TODO CLEAN UP COMMENT On mobile not using 2 step tooltip anymore !!
                        // if ((this.isTablet && _.isFunction(this.tablets.handleTipSpot)) && this.tablets.handleTipSpot(event, "target") === false) {
                        // 	return false;
                        // } else {
                        // 	this.switchPersonNameOrder();
                        // }

                        this.switchPersonNameOrder();

                        return false;
                    }
                }
                , switchPersonNameOrder: function (origin) {
                    //start the waiting indicator...
                    this.$el.trigger("europass:waiting:indicator:show");

                    this.triggerPrefsOrderChange("ECV");

                    // Switch person name ALSO for other document types !!
                    this.triggerPrefsOrderChange("ECL");
                    this.triggerPrefsOrderChange("ELP");
                }
                , triggerPrefsOrderChange: function (documentType, origin) {

                    this.prefsView.prefsDocument = documentType;

                    var switched = this.prefsView.switchPersonNames(documentType);
                    if (switched === true) {
                        this.model.trigger("prefs:order:changed", this.section);
                        this.render(this.reRenderIndicator, [this.doTransition(origin)]);
                    }
                }
                , applyTooltip: function (model) {
                    var modelInfo = _.isFunction(this.model.info) ? this.model.info() : null;

                    /*var sectionKey = this.section.substring(this.section.indexOf("LearnerInfo.")+"LearnerInfo.".length,this.section.length); */
                    //EWA 1477: remove section manage if created by of errorneous model image during empty form saves
                    modelInfo.isIdentificationEmpty() ? this.$el.addClass("empty").find(".edit").remove() : this.$el.removeClass("empty");
                }

                , applyWhenMissingDocuments: function () {
                    var _this = this;
                    this.$el.find('#personal-info-cv-photo').on('error', function (evt) {
                        _this.errorMissingDocument = true;
                        _this.displayDefaultViewWhenMissingDocuments(this);
                    });
                }
            });

            return PersonalInfoComposeView;
        }
);