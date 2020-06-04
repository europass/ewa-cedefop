define(
        [
            'jquery',
            'Utils',
            'underscore',
            'views/forms/FormView',
            'views/forms/attachment/LinkedAttachmentFormView',
            'hbs!templates/forms/cv/driving',
            'europass/maps/DrivingLicenseMap', //        ,'europass/TabletInteractionsView'
            'ModalFormInteractions'
        ],
        function ($, Utils, _, FormView, LinkedAttachmentFormView, HtmlTemplate, DrivingLicenseMap, ModalFormInteractions) { //, TabletInteractionsView

            var DrivingFormView = function (options) {
                LinkedAttachmentFormView.apply(this, [options]);
            };

            DrivingFormView.prototype = {

                htmlTemplate: HtmlTemplate

                , events: _.extend({
                    //"click ul.drivingLicense>li :not(input)":"handleDrivingLicence", input[id^='Driving']ul.drivingLicense>li
                    "click ul.drivingLicense>li": "handleDrivingLicence"
                }, LinkedAttachmentFormView.prototype.events)


                , handleDrivingLicence: function (event) {
                    this.isTablet = (/(iPhone|iPod|iPad)/.test(navigator.userAgent) || (/Android/.test(navigator.userAgent)) || (/BlackBerry/.test(navigator.userAgent)));
                    if (this.isTablet) {

                        /**
                         * pgia: EWA-1815
                         * Load TabletInteractionsView via require on the tablets variable ONLY if isTablet == true
                         */
                        var handleDriving = true;
                        var clickEvent = {target: event.target, currentTarget: event.currentTarget};
                        require(['europass/TabletInteractionsView'], function (TabletInteractionsView) {

                            if (TabletInteractionsView.handleDrivingLicence(clickEvent) === false)
                                handleDriving = false;
                        }
                        );

                        return handleDriving;
                    }
                }
                //Called be FormView.submit before setting the content
                , formToModel: function (frm) {
                    var m = new Backbone.NestedModel({});
                    var changes = [];

                    frm.find(":checkbox:checked").each(function (idx, el) {
                        var input = $(el);
                        var drivingName = input.attr("name");
                        var name = drivingName + "[" + idx + "]";
                        var value = input.val();

                        if (value !== undefined && value !== "") {
                            var attr = Utils.prepareModelAttr(name, value);
                            if (attr !== null) {
                                changes.push(attr);
                                m.set(attr);
                            }
                        }
                    });

                    //Add ReferenceTo
                    this.model.appendDocumentation("LearnerInfo.Skills.Driving", m);
                    return {"model": m, "changes": changes};
                }
                /**
                 * @Override
                 */
                , submitted: function (event, globalDateFormatUpdated) {
                    this.$el.trigger("europass:waiting:indicator:show");

                    LinkedAttachmentFormView.prototype.doSubmit.call(this);

                    FormView.prototype.submitted.apply(this, [event, globalDateFormatUpdated]);
                }
                , modalClosed: function (event, globalDateFormatUpdated) {
                    if (LinkedAttachmentFormView.prototype.doModalClosed.call(this)) {
                        ModalFormInteractions.confirmSaveSection(event, this.frm.attr("id"));
                    } else {
                        FormView.prototype.modalClosed.apply(this, [event, globalDateFormatUpdated]);
                    }
                }
                /**
                 * @Override
                 */
                , cancelled: function (event) {
                    FormView.prototype.cancelled.apply(this, [event]);
                }
            };

            DrivingFormView.prototype = $.extend(
                    //true, 
                            {},
                            LinkedAttachmentFormView.prototype,
                            DrivingFormView.prototype
                            );

                    return DrivingFormView;
                }
        );