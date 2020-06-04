define(
        [
            'jquery',
            'underscore',
            'views/compose/ComposeView',
            'europass/http/Resource',
            'europass/http/SessionManagerInstance'//'europass/TabletInteractionsView',
        ],
        function ($, _, ComposeView, Resource, Session) {//TabletInteractionsView, 

            var ClosingSalutationComposeView = ComposeView.extend({

                htmlTemplate: "compose/cl/closingSalutation"

                , events: _.extend({
                    "click :button.signature.delete": "requestDeleteSignature"
                            // "touchstart :button.signature.delete": "requestDeleteSignature"
                }, ComposeView.prototype.events)

                , onInit: function (options) {
                    this.model.bind("content:store:skipped", this.reRender, this);
                    ComposeView.prototype.onInit.apply(this, [options]);

                    this.applyWhenMissingDocuments();
                }

                , onClose: function (options) {
                    ComposeView.prototype.onClose.apply(this, [options]);
                    this.model.unbind("content:store:skipped", this.reRender);
                }

                , PERSON_NAME_REGEXP: new RegExp(/^SkillsPassport\.LearnerInfo\.Identification(\.PersonName(\.FirstName|\.Surname)?)?$/)
                , SIGNATURE_REGEXP: new RegExp(/^SkillsPassport\.LearnerInfo\.Identification\.Signature?$/)

                        /**
                         * @Override 
                         * 
                         * Re-render when:
                         * 1. the same section
                         * 2. Personal Info section
                         */
                , reRender: function (relSection, origin) {
                    if (this.PERSON_NAME_REGEXP.test(relSection) || this.SIGNATURE_REGEXP.test(relSection)) {
                        this.reRender(this.section, origin);
                    } else {
                        ComposeView.prototype.reRender.apply(this, [relSection, origin]);
                    }
                }
                /**
                 * @Override 
                 * 
                 * Re-render when:
                 * 1. the same section
                 * 2. Personal Info section
                 */
                , reRenderPreferenceOrder: function (relSection) {
                    if (this.PERSON_NAME_REGEXP.test(relSection)) {
                        this.reRender(this.section);
                    }
                }
                , requestDeleteSignature: function (event) {

                    // TODO CLEAN UP COMMENT On mobile not using 2 step tooltip anymore !!
                    // if ((this.isTablet && _.isFunction(this.tablets.handleSignatureDeletion)) && this.tablets.handleSignatureDeletion(event) === false) {
                    // 	return false;
                    // }

                    this.doDeleteSignature(event);
                }
                , doDeleteSignature: function (event) {
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
                    //var btn = $(event.target).find("button.delete");
                    //var relatedView = $( Utils.jId( btn.attr("data-rel-view")));

                    /* In case multiple sections are passed (like the ClosingSalutation view delete button, split the section and keep the one that contains the signature
                     trigger deletion for the rest of the sections **/
                    var sectionArray = section.split(" ");

                    //start the waiting indicator...
                    this.$el.trigger("europass:waiting:indicator:show");

                    for (var idx = 0; idx < sectionArray.length; idx++) {
                        if (sectionArray[idx].toLowerCase().indexOf("signature") !== -1) {
                            this.removeSignature(sectionArray[idx]);
                        } else {//relatedView section
                            this.removeClosingSalutation(sectionArray[idx]);
                        }
                    }
                    this.$el.trigger("europass:waiting:indicator:hide");
                }
                , updateRemovedSignature: function (path) {
                    //Delete Signature section - will trigger a re-render event
                    this.model.reset(path);

                    //Stop the wait indicator
                    this.$el.trigger("europass:waiting:indicator:hide");
                }
                , removeSignature: function (path) {
                    var matched = this.model.get(path);
                    var status = false;
                    if (matched !== null && matched !== undefined && matched !== '') {
                        //Delete service
                        var uri = matched.TempURI;
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
                                    this.updateRemovedSignature(path);
                                }
                            },
                            error: {
                                scope: this,
                                callback: function (statusObj) {
                                    status = false;
                                    this.updateRemovedSignature(path);
                                }
                            }
                        });
                    } else {
                        status = false;
                    }
                    return status;
                }

                , removeClosingSalutation: function (path) {
                    var matched = this.model.get(path);
                    if (matched !== null) {
                        this.model.reset(path);
                    }
                }
                /**
                 * @Override
                 * In order to handle the tooltip of the signature area as well.
                 */
                , applyTooltip: function () {
                    var modelInfo = _.isFunction(this.model.info) ? this.model.info() : null;
                    /*				if ( modelInfo!==null && _.isFunction(modelInfo.isSignatureEmpty) && modelInfo.isSignatureEmpty() === false ){
                     //add photo tooltip
                     this.$el.find(".Signature").addClass("tip mouse");
                     }else{
                     this.$el.find(".Signature").removeClass("tip mouse");
                     }*/
                    modelInfo.isCLSectionEmpty(this.section) ?
                            this.$el.addClass("empty") :
                            this.$el.removeClass("empty");
                }

                , applyWhenMissingDocuments: function () {
                    var _that = this;
                    this.$el.find('#signature-cl-photo').on("error", function (evt) {
                        _that.displayDefaultViewWhenMissingDocuments(this);
                    });
                }
            });

            return ClosingSalutationComposeView;
        }
);