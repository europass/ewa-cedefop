define(
        [
            'jquery',
            'underscore',
            'europass/backbone/EWABackboneView',
            'Utils',
            'ModalFormInteractions',
            'europass/GlobalDocumentInstance',
            'hbs!templates/messaging/message',
            'europass/http/WindowConfigInstance'
        ],
        function ($, _, EWABackboneView, Utils, ModalFormInteractions, GlobalDocument, MessageTpl, WindowConfig) {

            var ComposeView = Backbone.EWAView.extend({
                section: null
                , scrollPos: 0
                , isAtComposeView: false
                , isTablet: false
                , tablets: {}
                
                , newEuropassPortalContainerSelector: "#new-europass-portal-info"
                , documentStoringContainerSelector: "#document-storing-warning"
                , missingDocumentsContainerSelector: "#missing-documents-info"

                , initialRendering: true

                , events: {
                    "europass:delete:confirmed ": "proceedDeletion",
                    "click .item.skills.opens-modal-form": "getScrollPos",
                    "click .opens-modal-form-lang-empty": "getScrollPos",
                    "click [id^=\"ListItem:LearnerInfo.Achievement\"]": "getScrollPos",
                    "click .additional.empty": "getScrollPos"
                            //"click  .opens-modal-form"    : "getScrollPos"
//				"europass:autosort:order:change" : "toggleAutoSortOrder"
                }
                /**
                 * Performs the deletion of the respective section.
                 * 
                 * This function runs when the 'europass:delete:confirmed' event
                 * is triggered to the element in which the current view is bound to. 
                 * 
                 * Note: This event will be caught by the parent ComposeView objects too,
                 * as is the SkillsPassportComposeView.
                 * To prevent the event from being caught up the hierarchy we need to
                 * stop its propagation, or alternatively introduce a control which will
                 * only execute delete if the target element of the event is actually the el of the current View.
                 */
                , proceedDeletion: function (event, section) {
                    //execute if current el matches the element of the event
                    //start the waiting indicator...
                    this.$el.trigger("europass:waiting:indicator:show");

                    this.model.reset(section);
                    //Seee http://api.jquery.com/event.stopPropagation/
                    event.stopPropagation();
                }
                , onClose: function () {
                    this.model.unbind("model:linked:attachment:changed", this.reRenderAttachments);
                    this.model.unbind("model:prefs:order:changed", this.reRenderPreferenceOrder);
                    this.model.unbind("model:content:changed", this.reRender);
                    this.model.unbind("model:list:sort:change", this.reRender);

                    this.emptyViewsIndex();

                    this.cleanupNewEuropassPortalNotification();
                    this.cleanupDocumentNotification();
                    this.cleanupMissingDocumentNotification();

                    delete this.renderIndicationTarget;
                }
                //ekar: Important Note: Instead of overriding "initialize", use the callback function "onInit"
                , onInit: function (options) {
//console.log("Main:initialize :: "+options.section + " - render? " + options.initialRendering );
                    // In 1.1, Backbone Views no longer have the options argument attached as this.options automatically. Feel free to continue attaching it if you like. 
                    this.options = options;

                    this.parentView = options.parentView;
                    this.navigation = (options.navigation !== null && options.navigation !== undefined) ? options.navigation : "";
                    this.section = (options.section !== null && options.section !== undefined) ? options.section : "";
                    this.index = (options.itemIndex !== null && options.itemIndex !== undefined) ? options.itemIndex : "";
                    //console.log("ComposeView:initialize ("+this.section+") with index ["+ this.index+"]." );

                    this.model.bind("model:linked:attachment:changed", this.reRenderAttachments, this);
                    this.model.bind("model:prefs:order:changed", this.reRenderPreferenceOrder, this);
                    this.model.bind("model:content:changed", this.reRender, this);
                    this.model.bind("model:list:sort:change", this.reRender, this);

                    //Allow some instances to NOT be rendered upon instantiation.
                    //E.g. This is the case for the partials used in the Skills template.
                    if (options.initialRendering === true) {
                        this.render();
                    }

                    this.prepareViewsIndex();

                    this.isTablet = (/(iPhone|iPod|iPad)/.test(navigator.userAgent) || (/Android/.test(navigator.userAgent)) || (/BlackBerry/.test(navigator.userAgent)));
                    if (this.isTablet) {

                        /**
                         * pgia: EWA-1815
                         * Load TabletInteractionsView via require on the tablets variable ONLY if isTablet == true
                         */
                        var _that = this;
                        require(['europass/TabletInteractionsView'], function (TabletInteractionsView) {
                            //alert('initializing tablet interactions in ComposeView');	
                            _that.tablets = TabletInteractionsView;
                        }
                        );
                    }

                    //Call any relevant callbacks from the options using the parent object
                    Backbone.EWAView.prototype.onInit.apply(this, [options]);
                }
                , prepareViewsIndex: function () {
                    this.enabledViews = [];
                }
                , emptyViewsIndex: function () {
                    $(this.enabledViews).each(function (idx, v) {
                        if (_.isObject(v) && _.isFunction(v.close))
                            v.close();
                    });
                    this.enabledViews = [];
                }
                , addToViewsIndex: function (view) {
                    if (this.enabledViews === undefined) {
                        this.prepareViewsIndex();
                    }
                    this.enabledViews.push(view);
                }
                /**
                 * Render the view
                 * @param callback (optional)
                 */
                , render: function (callback, args) {
//console.log("Main:render :: " + this.section + " -ready? " + ( this.$el.length > 0 ) );
//console.log("Callback: " + _.isFunction(callback) );
                    if (this.$el.length === 0) {
                        return false;
                    }

                    var context = {};
                    $.extend(true, context, this.model.attributes);
                    context.Preferences = this.model.preferences.get(GlobalDocument.getPrefDocument());

                    //Enrich the context with the List Item index and sub-section, if relevant
                    if (this.index !== null) {
                        context.Idx = this.index;
                        context.subsection = this.section.substr(this.section.lastIndexOf(".") + 1);
                        context.locale = globalConfig.locale;
                    }
                    var html = this.template(context);

                    if (_.isFunction(callback)) {
                        var that = this;
                        if (args === undefined)
                            args = [];
                        this.$el.html(html).promise().done(function () {
                            callback.apply(that, args);
                        });
                    } else {
                        this.$el.html(html);
                    }

                    this.applyTooltip();

                    if (this.enabledViews !== undefined && this.enabledViews !== null && this.enabledViews.length > 0) {
                        //in case render is called without previously calling close
                        //the views area already enabled. Therefore we need to close them
                        //before proceeding.
                        this.emptyViewsIndex();//before enabling.. remove the views if they already exist
                    }

                    this.enableFunctionalities(this.model);
                    //console.log("COMPOSE -RENDER: " + this.section);
                }

                /**
                 * Adds the tooltip-related classes on the related elements
                 * Abstract class needs to be overwriten in special cases
                 */
                , applyTooltip: function () {}
                /**
                 * To be overriden by objects extending this View in order to add
                 * interaction and other functionalities
                 */
                , enableFunctionalities: function (model) {}

                /**
                 * Re-render whenever the preference order is changed.
                 * This should run only when the relSection is exactly the same as this.section
                 * OR when the relSection is the parent LearnerInfo
                 */
                , reRenderPreferenceOrder: function (relSection) {
                    if (relSection === this.section) {
//					console.log("re-render order");
                        this.reRender(this.section, "click-origin-controls");
                    }
                }
                /**
                 * Render only if the relSection is exactly the same as this.section.
                 * This allows us to have all views as child instances of the ComposeView and have each share
                 * this specific function to re-render as a response to the model content change event.
                 */
                , reRender: function (relSection, origin) {
//console.log("Main Compose View ::: reRender for <"+relSection+"> '"+this.section+"'");
                    //First check if the related section updated is an indexed section
                    var targetListItem = null;
                    var listSection = Utils.getListSection(relSection);
                    if (listSection !== null) {
                        relSection = listSection;
                    }
                    //The first condition is 'true' for annexes (SkillsPassport.LearnerInfo.ReferenceTo) so the annexes section is skipped here (second condition)
                    //Its according re-rendering is implemented at ../views/compose/cv/AnnexesComposeView
                    if (relSection === this.section) {
                        //console.log("Main Compose View ::: do render view with origin: " + origin );
                        if (relSection === 'SkillsPassport.LearnerInfo.WorkExperience' ||
                                relSection === 'SkillsPassport.LearnerInfo.Education') {
                            var sectionForHighlighting;

                            if (arguments !== null && arguments !== undefined) {
                                if (arguments.length > 0) {
                                    //get argument for sort target position
                                    targetListItem = arguments[2];
                                    $(".drawer.main.modalform").each(function (i) {
                                        if ($(this).is(":visible")) {
                                            sectionForHighlighting = $($(this)[0]).data('rel-section');
                                        }
                                    })
                                }
                            }
                            if (sectionForHighlighting !== undefined) {
                                this.render(this.reRenderIndicator, [this.doTransition(origin), targetListItem, sectionForHighlighting]);
                            } else {
                                this.render(this.reRenderIndicator, [this.doTransition(origin), targetListItem]);
                            }
                        } else {
                            this.render(this.reRenderIndicator, [this.doTransition(origin), targetListItem]);
                        }
                    } else if (relSection == 'SkillsPassport.CoverLetter.Letter.ClosingSalutation') {
                        this.reRenderIndicator(this.doTransition(origin), targetListItem, "Compose:CL:ClosingSalutation");
                    }
                }
                , getScrollPos: function (event) {
                    var main = $("body").find("#main-content-area");
                    if (main !== undefined && main !== null && main.length > 0) {
                        this.scrollPos = main.scrollTop();
                        this.isAtComposeView = true;
                    }
                }
                /**
                 * Respond to the event of changing the Attachments.
                 * By default do NOT act.
                 * To be overriden if necessary.
                 */
                , reRenderAttachments: function () {
                    var main = $("body").find("#main-content-area");
                    if (main !== undefined && main !== null && main.length > 0 && this.scrollPos !== undefined && this.scrollPos > 0 && this.isAtComposeView === true) {
                        $(main).animate({scrollTop: this.scrollPos}, 400, 'linear');
                    }

                }

                /**
                 * Callback to be used for re-rendering of the section
                 */
                , reRenderIndicator: function (doTransition, targetListItem, argID) {
                    var targetEl = this.$el;
                    if (!_.isEmpty(this.renderIndicationTarget))
                        targetEl = $(this.renderIndicationTarget);

                    var attrID = targetEl.attr("id");
                    if (argID !== undefined) {
                        attrID = argID;
                    }
                    if (attrID === undefined) {
                    } else {
                        $("body").trigger("europass:section:updated", [targetEl, doTransition, targetListItem, attrID]);
                    }
                    delete this.renderIndicationTarget;
                }
                , doTransition: function (origin) {
                    return "click-origin-controls" === origin;
                }
                /**
                 * Allows ComposeView instances to set the re-rendering indication target to something other than this.$el
                 */
                , setRenderIndicationTarget: function (elSelector) {
                    if (!_.isEmpty(elSelector))
                        this.renderIndicationTarget = elSelector;
                }

                /**
                 * Open extra elements
                 */
                , openModals: function (configuration) {
                    if (_.isUndefined(configuration))
                        return;

                    var extraClazzes = configuration.clazzes;

                    if (extraClazzes !== undefined && extraClazzes !== null && $.isArray(extraClazzes) && extraClazzes.length > 0) {
                        var that = this;

                        //Instantiate extra classes as Forms!
                        $(extraClazzes).each(function (i, extraClazz) {
                            if (extraClazz.showFirstTimeOnly === true) {
                                //delete this attribute to prevent the extra views to be rendered upon re-render of this form.
                                //NOTE! the options are not reset unless 'initialize' or 'close' is executed.
                                configuration.clazzes.splice(i, 1);

                                if (extraClazz.form !== null && extraClazz.form !== undefined) {
                                    ModalFormInteractions.simpleOpenFormByInfo(extraClazz);
                                } else if (extraClazz.requirePath !== null && extraClazz.clazz !== null) {
                                    if (extraClazz.driveName !== null && extraClazz.driveName !== undefined) {
                                        Utils.newFormInstance({
                                            _className: extraClazz.clazz,
                                            _requireName: extraClazz.requirePath,
                                            _driveName: extraClazz.driveName,
                                            model: that.model
                                        }, function (view) {
                                            view.render();
                                        }, this);
                                    } else {
                                        Utils.newFormInstance({
                                            _className: extraClazz.clazz,
                                            _requireName: extraClazz.requirePath,
                                            model: that.model
                                        }, function (view) {
                                            view.render();
                                        }, this);
                                    }
                                }
                            }
                        });

                    }
                    //Trigger events!
                    var events = configuration.events;
                    if (!_.isEmpty(events)) {
                        for (var i = 0; i < events.length; i++) {
                            var event = events[i];

                            var el = $(event.el);
                            var eventName = event.event;
                            var eventArgs = event.args;
                            if (el.length > 0 && !_.isEmpty(eventName)) {
                                if (!_.isEmpty(eventArgs) && _.isArray(eventArgs))
                                    el.trigger(eventName, eventArgs);
                                else
                                    el.trigger(eventName);
                            }
                        }
                    }
                }
                /**
                 * Called when this view needs to be cleared.
                 * Used by cv/SkillsPassportComposeView, elp/ComposeView, ecl/ComposeView, esp/EspComposeView
                 * 
                 * Will clean up the section denoted by this.documentStoringContainerSelector
                 */
                , cleanupDocumentNotification: function () {
                    var container = $(this.documentStoringContainerSelector);
                    container.hide();
                    container.find("section.notification").remove();
                }
                /**
                 * Called when this view needs to show the document specific notification.
                 * Used by cv/SkillsPassportComposeView, elp/ComposeView, ecl/ComposeView, esp/EspComposeView
                 * 
                 * Will clean up the section denoted by this.documentStoringContainerSelector
                 */
                , displayDocumentNotification: function (message) {
                    var container = $(this.documentStoringContainerSelector);

                    if (container.hasClass("dismissed"))
                        return;

                    //state, message, section, ignoreTip, closable 
                    var context = {};
                    context.state = "warning";
                    context.message = message;
                    context.closable = "yes";

                    var html = MessageTpl(context);

                    container.html(html);
                    container.show();
                }


                /**
                 * Called when this view needs to be cleared.
                 *
                 * Will clean up the section denoted by this.missingDocumentsContainerSelector
                 */
                , cleanupMissingDocumentNotification: function () {
                    var container = $(this.missingDocumentsContainerSelector);
                    container.hide();
                    container.find("section.notification").remove();
                }
                /**
                 * Called when this view needs to show the document specific notification.
                 * Will clean up the section denoted by this.missingDocumentsContainerSelector
                 */
                , displayMissingDocumentNotification: function (message) {
                    var container = $(this.missingDocumentsContainerSelector);
                    if (container.hasClass("dismissed"))
                        return;

                    //state, message, section, ignoreTip, closable
                    var context = {};
                    context.state = "info";
                    context.message = "<p>" + message + "</p";
                    context.closable = "yes";

                    var html = MessageTpl(context);
                    container.html(html);
                }

                 /**
                 * Called when this view needs to be cleared.
                 * Used by cv/SkillsPassportComposeView, elp/ComposeView, ecl/ComposeView, esp/EspComposeView
                 * 
                 * Will clean up the section denoted by this.newEuropassPortalContainerSelector
                 */
                , cleanupNewEuropassPortalNotification: function () {
                    var container = $(this.newEuropassPortalContainerSelector);
                    container.hide();
                    container.find("section.notification").remove();
                }
                /**
                 * Called when this view needs to show the document specific notification.
                 * Used by cv/SkillsPassportComposeView, elp/ComposeView, ecl/ComposeView, esp/EspComposeView
                 * 
                 * Will clean up the section denoted by this.newEuropassPortalContainerSelector
                 */
                , displayNewEuropassPortalNotification: function (message) {
                    if (WindowConfig.showNewEuropassPortalNotification === true) {
                        var container = $(this.newEuropassPortalContainerSelector);

                        if (container.hasClass("dismissed"))
                            return;

                        //state, message, section, ignoreTip, closable 
                        var context = {};
                        context.state = "info";
                        context.message = message;
                        context.closable = "yes";

                        var html = MessageTpl(context);

                        container.html(html);
                        container.show();
                    }
                }

                , displayDefaultViewWhenMissingDocuments: function (_that) {
                    $("#missing-documents-info").show();
                    _that.src = "/editors/static/ewa/images/missingDocumentIcon.png";
                    _that.style = "width:100px";

                    $(_that).show();
                    $(_that).closest('.thumb').removeClass('pdf-default-attachment-icon');
                }

                /**
                 * Called when the autosort button is pressed on a list.
                 * Used by cv/WorkExperienceListComposeView, cv/WorkExperienceListComposeView
                 * 
                 * Will automaticaly sort the elements of the list by date descending 
                 */
                /*			,autoSortByDate: function(section, data){
                 
                 for(var i=0; i<_.size(data); i++){
                 
                 var dateFrom = data[i].from;
                 var dateTo = data[i].to;
                 
                 if(_.isUndefined(dateFrom) || _.isNull(dateFrom)){
                 data[i].from = undefined;
                 }
                 if(_.isUndefined(dateTo) || _.isNull(dateTo)){
                 
                 if(!_.isUndefined(data[i].item.Period) && data[i].item.Period.Current){
                 
                 var currentDate = new XDate();
                 data[i].to = { 'Year': currentDate.getFullYear(), 'Month': (currentDate.getMonth() + 1), 'Day': currentDate.getDate()};
                 }else{
                 data[i].to = undefined;
                 }
                 }
                 }
                 
                 $(this.el).trigger("europass:autosort:date", [data, section, this.el]);
                 }*/

                /*			,toggleAutoSortOrder: function( event, previous ){
                 
                 var autoSortBtn = this.$el.find("button.autoSort.byDate");
                 if(previous == "descending"){
                 autoSortBtn.removeClass("descending");
                 autoSortBtn.addClass("ascending");
                 }else if(previous == "ascending"){
                 autoSortBtn.removeClass("ascending");
                 autoSortBtn.addClass("descending");
                 }
                 }			*/
            });

            return ComposeView;
        }
);