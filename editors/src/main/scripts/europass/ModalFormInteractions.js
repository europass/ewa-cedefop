define('ModalFormInteractions',
        [
            'jquery',
            'jqueryui',
            'scrollTo',
            'Utils',
            'hbs!templates/main/drawer',
            'models/SkillsPassportInstance',
            'mapviews/ModalFormViewMap'
        ],
        function ($, jqueryui, scrollTo, Utils, DrawerTpl, SkillsPassportInstance, MODAL_FORM_VIEW_MAP) {

            var PROTOCOLS = ["http:", "https:", "ftp:", "mailto:"];
            var times = 0;
            var ModalFormInteractions = {};

            /**
             * Gets or Creates the Dom element for the Form.
             */
            ModalFormInteractions.getModalForm = function (formId, section) {
                var formEl = $(Utils.jId(formId));
                if (formEl === undefined || formEl === null || formEl.length == 0) {

                    var context = {
                        id: formId,
                        data_rel_section: section
                    };

                    var html = DrawerTpl(context);
                    var modalEl = $(html);
                    modalEl.appendTo("body");

                    var closeEl = modalEl.find("button.close");

                    formEl = modalEl.find("form.main");
                    closeEl.click(function (event, ui) {
                        formEl.trigger("europass:modal:closed");
                    });
                }
                return formEl.closest("div.overlay");
            };

            ModalFormInteractions.getSectionInfo = function (btn) {
                var form = btn.attr("data-rel-form");
                var doc = btn.attr("data-rel-doc");
                var clazz = btn.attr("data-rel-clazz");
                var section = btn.attr("data-rel-section");
                var index = btn.attr("data-rel-index");
                var subsection = btn.attr("data-rel-subsection");
                var helpSection = btn.attr("data-rel-help-section");
                var validationField = btn.attr("data-validation-field");
                var origin = btn.attr("data-rel-origin");
                var isAchievementPersonalDataTreatment = btn.attr("data-rel-personal-data-treatment");

                var info = {
                    form: form,
                    doc: doc,
                    clazz: clazz,
                    index: index,
                    section: section,
                    subsection: subsection,
                    helpSection: helpSection,
                    validationField: validationField,
                    origin: origin,
                    isAchievementPersonalDataTreatment: isAchievementPersonalDataTreatment
                };
                return info;
            };
            ModalFormInteractions.openForm = function (event, openedModal) {

                var currentTarget = $(event.currentTarget);
                var target = $(event.target);

                var hasProtocol = null;
                var isLinkedAttachment = target.hasClass("open-linked-attachments");

                var otherLangEmpty = target.hasClass("opens-modal-form-lang-empty") || currentTarget.hasClass("opens-modal-form-lang-empty");
                var isEmptyLang = currentTarget.hasClass('opens-modal-form-lang-empty');
                var otherLangSpecialOpen = isEmptyLang && target.hasClass('opens-modal-form-lang-empty') ||
                        isEmptyLang && target.hasClass('noSkipOpen') ||
                        !(isEmptyLang && target.hasClass('opens-modal-form'))//the button ADD LANG 2orMore and the edit self assesment button
                        ; //if something is true then it should not return/exit
                var isAchievementPersonalDataTreatment = target.hasClass('personal-data-treatment-achievements');

                //the click on row self-assesment
                //!(isEmptyLang && ( target.parent("td[id*='language']").length > 0)) ||//the click on row self-assesment
                //!(isEmptyLang && ( target.parent('tr.self-evaluation').length > 0)) || //the click on row self-assesment
                if ((isEmptyLang && target.closest('.language.compose-list.list-item.opens-modal-form').length > 0) || // click self assessment row
                        (isEmptyLang && target.closest('.open-modal-skippme').length > 0)) {//click on table header when non empty
                    otherLangSpecialOpen = false;
                }

                //this is for chrome
                if (isEmptyLang && target.parent('button.subsection.compose-list.add.Skills.opens-modal-form').length > 0) { // click add new lang
                    otherLangSpecialOpen = false;
                }

                //isEmptyLang && open-modal-skippme
                /*The Modal Form will not open if:
                 * 1. the target 's protocol is one of the expected (which means that is an href or mailto)
                 * 2. the target is a button, but which does not open a modal form, rather performs an action. 
                 * 3. the target is either a nested element that opens a form or an element inside a nested element that opens a form,
                 * 					  and the target is not the actual nested element, identified by the nested-modal class name. 
                 */
                try {
                    hasProtocol = ($.inArray(event.target.protocol, PROTOCOLS) !== -1);
                } catch (e) {
                    //do not throw and stop process;
                }

                var isActionButton = target.is(":button") && !target.hasClass("opens-modal-form");
                var isNestedSection = (target.hasClass("nested-modal") || target.closest(".nested-modal").length > 0) && (!currentTarget.hasClass("nested-modal"));

                var editingDisabled = currentTarget.hasClass("disable-editing") && currentTarget.hasClass("list-item");

                if (hasProtocol || isActionButton || isNestedSection || editingDisabled || !otherLangSpecialOpen) {
                    //should not open the form...
//				alert("modal form does not open " + isNestedSection);
                    return;//do not use return false because this might cause stoping of event propagation
                } else {

                    var info = ModalFormInteractions.getSectionInfo(currentTarget);

                    /*if (currentTarget.hasClass("opens-modal-form-lang-empty") && otherLangStopPropagation){
                     return;
                     }*/

                    if (isAchievementPersonalDataTreatment) {
                        info = ModalFormInteractions.setCustomAchievementPersonalDataTreatment(info);
                    }

                    if (otherLangEmpty) {//hard coded for other lang
                        info = ModalFormInteractions.setOtherLangEmptyInfo(info);
                    }

                    if (Utils.isEmptyObject(info))
                        return;

                    //enable the waiting indicator
                    if (!openedModal) {
                        $("body").trigger("europass:waiting:indicator:show");
                    }

                    var overlay = ModalFormInteractions.getModalForm(info.form, info.section);

                    /*
                     * We use a callback here, since the loading of the form elements takes place
                     * ASYNCHRONOUSLY!
                     */
                    MODAL_FORM_VIEW_MAP.renderForm(info, SkillsPassportInstance.attributes,
                            function (modaloverlay) {
                                var overlay = $(modaloverlay);
                                var modal = (overlay !== undefined ? overlay.children(".modal") : undefined);
                                var children = (modal !== undefined ? modal.children() : undefined);
                                overlay.show(function () {
                                    $(this).animate({"background-color": "rgba(0,0,0, 0.7)"}, 600);
                                    modal.find("form.drawer").css("overflow-y", "scroll");
                                    if (children !== undefined && modal !== undefined) {
                                        children.addBack().show('slide', {direction: "right"}, 500, function () {
                                            if ($(this).is("form")) {
                                                //If the event target indicates that we need to open the attachment sections, do so by scrolling there
                                                if (isLinkedAttachment) {
                                                    //									console.log("scroll to attachments");
                                                    $(".overlay:visible > .modal > .main")
                                                            .scrollTo("fieldset.linked-attachments", {duration: 1000, easing: 'linear', axis: 'y'});
                                                }
                                            }

                                            $("body").trigger("europass:drawer:opened");

                                        });
                                    }

                                    $("body").trigger("europass:waiting:indicator:hide");
                                });
                            },
                            ModalFormInteractions, [overlay]);
                }
                //Close Button tooltip
                /*var closeEl = $(".ui-dialog-titlebar-close");
                 var closeTxt = EditorHelp["Modal.Help.Close"];
                 closeEl.addClass("tip spot");
                 closeEl.append('<span class=\"data-title\" style=\"display:none\">'+closeTxt+'</span>');*/
            };
            ModalFormInteractions.setOtherLangEmptyInfo = function (info) {
                if (Utils.isUndefined(info.forminfo))
                    info.form = "Form:LearnerInfo.Skills.Linguistic.ForeignLanguage[0]";
                if (Utils.isUndefined(info.forminfo))
                    info.doc = "cv";
                if (Utils.isUndefined(info.clazz))
                    info.clazz = "ForeignLanguageFormView";
                if (Utils.isUndefined(info.section))
                    info.section = "SkillsPassport.LearnerInfo.Skills.Linguistic.ForeignLanguage[0]";
                if (Utils.isUndefined(info.index))
                    info.index = "0";
                if (Utils.isUndefined(info.helpSection))
                    info.helpSection = "Help.SkillsPassport.LearnerInfo.Skills.Linguistic.ForeignLanguage";
                return info;
            };

            ModalFormInteractions.setCustomAchievementPersonalDataTreatment = function (info) {
                info.achievementPersonalDataTreatment = true;

                return info;
            };

            /**
             * Opens a form without checking the target, etc.
             */
            ModalFormInteractions.simpleOpenForm = function (event) {
                var btn = $(event.target);
                var info = ModalFormInteractions.getSectionInfo(btn);
                ModalFormInteractions.simpleOpenFormByInfo(info);
            };
            /**
             * Opens a form based on an info object
             */
            ModalFormInteractions.simpleOpenFormByInfo = function (info) {
                var overlay = ModalFormInteractions.getModalForm(info.form);
                /*
                 * We use a callback here, since the loading of the form elements takes place
                 * ASYNCHRONOUSLY!
                 */
                MODAL_FORM_VIEW_MAP.renderSimpleForm(info, SkillsPassportInstance.attributes, function (modaloverlay) {
                    var overlay = $(modaloverlay);
                    var modal = (overlay !== undefined ? overlay.children(".modal") : undefined);
                    var children = (modal !== undefined ? modal.children() : undefined);
                    overlay.show(function () {
                        $(this).animate({"background-color": "rgba(0,0,0, 0.7)"}, 400);
                        modal.find("form.drawer").css("overflow-y", "scroll");
                        if (children !== undefined && modal !== undefined) {
                            children.addBack().show('slide', {direction: "right", easing: "easeInSine"}, 400, function () {
                                $("body").trigger("europass:drawer:opened");
                            });
                        }
                    });
                }, ModalFormInteractions, [overlay]);
            };

            ModalFormInteractions.confirmDeleteSection = function (event) {
                var info = ModalFormInteractions.readDeleteButton(event);
                var section = info.section;
                var relatedView = info.relatedView;
                $(event.target).trigger("europass:delete:requested", [relatedView, section]);
            };

            ModalFormInteractions.confirmSaveSection = function (event, relatedView) {
                $(event.target).trigger("europass:save:requested", [relatedView]);
            };

            ModalFormInteractions.confirmReset = function (event) {
                var info = ModalFormInteractions.readDeleteButton(event);
                var relatedView = info.relatedView;
                $(event.target).trigger("europass:reset:requested", [relatedView]);
            };

            ModalFormInteractions.readDeleteButton = function (event) {
                var btn = $(event.target);
                var info = ModalFormInteractions.getSectionInfo(btn);
                var section = info.section;
                var relatedView = btn.attr("data-rel-view");
                return {
                    section: section,
                    relatedView: relatedView
                };
            };
            return ModalFormInteractions;
        }
);
