define(
        [
            'require',
            'jquery',
            'underscore',
            'views/compose/ComposeView'
        ],
        function (require, $, _, ComposeView) {

            var GenericSkillsComposeView = ComposeView.extend({

                //Events of ComposeView plus those here..
//			events: _.extend({
//				"europass:skills:linked:attachment:changed": "configureRerendering"
//			}, ComposeView.prototype.events),

                initialize: function (options) {

                    this.section = options.section;
                    //Find the Handlebars template to load

                    var skill = this.section.substr(this.section.lastIndexOf(".") + 1);

                    var requirePath = 'hbs!templates/compose/cv/genericskills/' + skill.toLowerCase();

                    var requiredTemplate = null;
                    var that = this;
                    require(['assembly/ecvCompose'], function () {
                        require([requirePath], function (hbstemplate) {
                            requiredTemplate = hbstemplate;
                            that.htmlTemplate = requiredTemplate;
                            //And now call the parent initialize
                            ComposeView.prototype.initialize.apply(that, [options]);

                            if (that.model.getActiveSkillsSection() === that.section) {
                                if (that.section === "SkillsPassport.LearnerInfo.Skills.Other") {
                                    that.reRender(that.section, "click-origin-controls");
                                } else {
                                    that.reRender(that.section, "click-origin-compose");
                                }
                                that.model.resetActiveSkillsSection();
                            }
                        });
                    });
                },

                configureRerendering: function (event) {
                    /*				var prevAttrSectionValue = that.model.getModelPreviousSection(that.section);
                     var currAttrSectionValue = that.model.get(that.section);
                     
                     if(!_.isNull(prevAttrSectionValue) && !_.isUndefined(currAttrSectionValue) && !_.isNull(currAttrSectionValue))
                     if(!_.isEqual(currAttrSectionValue, prevAttrSectionValue)){
                     if (that.section == "SkillsPassport.LearnerInfo.Skills.Other") {
                     that.reRender(that.section,"click-origin-controls");
                     } else {
                     that.reRender(that.section,"click-origin-compose");
                     }
                     }
                     */

                    if (this.section === "SkillsPassport.LearnerInfo.Skills.Other") {
                        this.reRender(this.section, "click-origin-controls");
                    } else {
                        this.reRender(this.section, "click-origin-compose");
                    }
                },

                enableFunctionalities: function (model) {
                    this.contents = this.model.get(this.section);
                    ComposeView.prototype.enableFunctionalities.apply(this, [model]);
                },
                /**
                 * 	@Override method from views.compose.ComposeView
                 * 
                 * This is required only for the Other Skills section.
                 * 
                 * When this specific section requires re-rendering it will delegate to the parentView instead.
                 * This is done to accommodate the requirement that when:
                 * i) no content exists and new content is added the entire document view must re-render
                 *    in order to display the section, which is originally non-existent.
                 * ii) content exists but the last list-item is removed so it becomes empty. In this case
                 *     the entire document view must re-render in order to NOT display the section.
                 */
                reRender: function (relSection, origin) {
//				console.log("GenericSkills:reRender || relSection: "+relSection+"\nsection: "+this.section);

                    if (relSection === this.section) {
                        var doTransition = this.doTransition(origin);
//					console.log("doTransition: "+doTransition);

                        var isOtherSkills = !_.isEmpty(this.section.match("\w*\\.Other$"));
//					console.log("isOtherSkills: "+isOtherSkills);
                        if (!isOtherSkills) {
                            this.render(this.reRenderIndicator, [doTransition]);
                            return;
                        }

                        var contents = this.model.get(this.section);
                        var isFirst = !_.isEmpty(contents) && _.isEmpty(this.contents);
                        var isEmpty = _.isEmpty(contents) && !_.isEmpty(this.contents);
//					console.log("isFirst: "+isFirst+"\nisEmpty: "+isEmpty);

                        this.contents = contents;

                        if (!isFirst && !isEmpty) {
//						console.log("re-render now this");
                            this.render(this.reRenderIndicator, [doTransition]);
                            return;
                        }

                        if (_.isObject(this.parentView) && isOtherSkills) {
                            if (isFirst) {
//							console.log("re-render now parent");
                                this.parentView.setRenderIndicationTarget(this.options.el);
                            }
                            this.parentView.onReRendering(origin);
                        }
                    }
                }

                , applyTooltip: function (model) {
                    var modelInfo = _.isFunction(this.model.info) ? this.model.info() : null;
                    var sectionKey = this.section.substring(this.section.indexOf("LearnerInfo.") + "LearnerInfo.".length, this.section.length);
                    modelInfo.isSectionEmpty(sectionKey) ?
                            this.$el.addClass("empty") :
                            this.$el.removeClass("empty");
                }
            });

            return GenericSkillsComposeView;
        }
);