define(
        [
            'jquery',
            'underscore',
            'views/compose/ComposeView',
            'hbs!templates/compose/cv/skills',
            'views/compose/cv/MotherTongueComposeView',
            'views/compose/cv/ForeignLanguageListComposeView',
            'views/compose/cv/DrivingComposeView',
            'views/compose/cv/GenericSkillsComposeView',
            'views/compose/cv/ComputerSkillsComposeView'
        ],
        function ($, _, ComposeView, HtmlTemplate, MotherTongueComposeView, ForeignLanguageListComposeView,
                DrivingComposeView, GenericSkillsComposeView, ComputerSkillsComposeView) {

            var SkillsComposeView = ComposeView.extend({

                htmlTemplate: HtmlTemplate

                , onClose: function () {
                    ComposeView.prototype.onClose.apply(this);

                    $(this.subsections).each(function (idx, subsection) {
                        if (_.isObject(subsection) && _.isFunction(subsection.close))
                            subsection.close();
                    });
                    this.subsections = [];
                }
                //@Override
                , onInit: function (options) {
                    this.subsections = [];

                    ComposeView.prototype.onInit.apply(this, [options]);
                }
                //@Override
                , render: function (callback, args) {
                    ComposeView.prototype.render.apply(this, [callback, args]);

                    if ($.isArray(this.subsections) && this.subsections.length === 0) {
                        this.onRender();
                    }
                }
                /**
                 * Will close all related sections and then re-initialized them
                 */
                , onReRendering: function (origin) {
//				console.log("SkillsComposeView on re-rendering...");
                    $(this.subsections).each(function (idx, subsection) {
                        if (_.isObject(subsection) && _.isFunction(subsection.close))
                            subsection.close();
                    });
                    this.subsections = [];

                    this.reRender(this.section, origin);
                }
                /**
                 * IMPORTANT!!!
                 * Unfortunatelly re-delegation of views is necessary if not initiated on each re-render of the main SkillsPassport view...
                 *
                 * See http://stackoverflow.com/questions/9271507/how-to-render-and-append-sub-views-in-backbone-js
                 */
                , onRender: function () {

                    this.subsections = [];

                    //PERSONAL SKILLS - Mother Tongue
                    this.motherTongueComposeView = new MotherTongueComposeView({
                        el: '#Compose\\:LearnerInfo\\.Skills\\.Linguistic\\.MotherTongue',
                        model: this.model,
                        section: "SkillsPassport.LearnerInfo.Skills.Linguistic.MotherTongue",
                        initialRendering: true,
                        parentView: this
                    });
                    this.subsections.push(this.motherTongueComposeView);

                    //PERSONAL SKILLS - Foreign Language
                    this.foreignLanguageListComposeView = new ForeignLanguageListComposeView({
                        el: '#Compose\\:LearnerInfo\\.Skills\\.Linguistic\\.ForeignLanguage',
                        model: this.model,
                        section: "SkillsPassport.LearnerInfo.Skills.Linguistic.ForeignLanguage",
                        initialRendering: true,
                        parentView: this
                    });
                    this.subsections.push(this.foreignLanguageListComposeView);

                    //PERSONAL SKILLS - Driving License
                    this.drivingComposeView = new DrivingComposeView({
                        el: '#Compose\\:LearnerInfo\\.Skills\\.Driving',
                        model: this.model,
                        section: "SkillsPassport.LearnerInfo.Skills.Driving",
                        initialRendering: true,
                        parentView: this
                    });
                    this.subsections.push(this.drivingComposeView);

                    //PERSONAL SKILLS - Generic Skills 
                    this.otherSkills = ["Communication", "Organisational", "JobRelated", "Other"];
                    var that = this;
                    $.each(this.otherSkills, function (idx, other) {
                        var name = other.toLowerCase() + "ComposeView";
                        that[ name ] = new GenericSkillsComposeView({
                            el: '#Compose\\:LearnerInfo\\.Skills\\.' + other,
                            model: that.model,
                            section: "SkillsPassport.LearnerInfo.Skills." + other,
                            initialRendering: true,
                            parentView: that
                        });
                        that.subsections.push(that[ name ]);
                    });

                    //PERSONAL SKILLS - Computer Skills
                    this.computerSkillsComposeView = new ComputerSkillsComposeView({
                        el: '#Compose\\:LearnerInfo\\.Skills\\.Computer',
                        model: this.model,
                        section: "SkillsPassport.LearnerInfo.Skills.Computer",
                        initialRendering: true,
                        parentView: this
                    });
                    this.subsections.push(this.computerSkillsComposeView);
                }
            });

            return SkillsComposeView;
        }
);