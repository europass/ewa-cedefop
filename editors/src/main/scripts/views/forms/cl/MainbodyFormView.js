define(
        [
            'jquery',
            'underscore',
            'views/forms/FormView',
            'i18n!localization/nls/CLClosingRecommendations',
            'i18n!localization/nls/CLMainBodyRecommendations',
            'i18n!localization/nls/CLOpeningRecommendations',
            'hbs!templates/forms/cl/mainbody',
            'hbs!templates/forms/cl/contextmenu'
        ],
        function ($, _, FormView, CLClosingRecommendations, CLMainBodyRecommendations, CLOpeningRecommendations, HtmlTemplate, CtxMenuTemplate) {

            var MainbodyFormView = FormView.extend({

                htmlTemplate: HtmlTemplate

                , enableFunctionalities: function () {

                    //call parent FINALLY enable functionalities
                    var options = {
                        rteConfig: {
                            applyTo: [
                                {
                                    namePattern: "^.+?(\\bOpening|\\bMainBody|\\bClosing)$",
                                    config: {
//								  buttons : ['|','alignleft','alignjustify']
                                    }
                                }
                            ]
                        }
                    };

                    //call parent enable functionalities
                    FormView.prototype.enableFunctionalities.call(this, [options]);

                    var frm = this.frm;

                    // Rich Text Editor
                    frm.find('textarea.context-sensitive-recommendations').each(function (idx, target) { //'div.redactor_context-sensitive-recommendations'
                        var sectionName = $(target).attr('id');
                        var html = null;

                        if (sectionName.indexOf('Opening') !== -1) {
                            html = CLOpeningRecommendations["html"];
                        } else if (sectionName.indexOf('Closing') !== -1) {
                            html = CLClosingRecommendations["html"];
                        } else if (sectionName.indexOf('MainBody' !== -1)) {
                            html = CLMainBodyRecommendations["html"];
                        } else {
                            return;
                        }

                        var htmlMenu = CtxMenuTemplate({"section": sectionName, "html": html});
                        $(target).after(htmlMenu);
                        $(target).trigger("europass:contextmenu:added", htmlMenu);
                        /*					
                         var el = $(target).siblings('.cke_1_contents');
                         var sectionName = el.siblings('textarea').attr('name');
                         
                         
                         
                         if (!_.isNull(sectionName.match(/Opening$/)))
                         html = CLOpeningRecommendations["html"];
                         else if (!_.isNull(sectionName.match(/MainBody$/)))
                         html = CLMainBodyRecommendations["html"];
                         else if (!_.isNull(sectionName.match(/Closing$/)))
                         html = CLClosingRecommendations["html"];
                         else
                         return;
                         
                         var htmlMenu = CtxMenuTemplate({"section": sectionName, "html": html});
                         
                         el.after(htmlMenu);
                         
                         el.trigger("europass:contextmenu:added");
                         */
                    });

                    //call parent FINALLY enable functionalities
                    FormView.prototype.finallyEnableFunctionalities.call(this);
                }

            });
            return MainbodyFormView;
        }
);