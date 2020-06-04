define(
        [
            'jquery',
            'underscore',
            'jcrop',
            'views/forms/ImageFormView',
            'hbs!templates/forms/signature/signaturemain',
            'hbs!templates/forms/signature/signatureedit',
            'hbs!templates/forms/signature/signaturedisplay'
        ],
        function ($, _, jcrop, ImageFormView, HtmlTemplate, EditTpl, DisplayTpl) {

            var SignaturePhotoFormView = function (options) {
                ImageFormView.apply(this, [options]);
            };

            SignaturePhotoFormView.prototype = {
                CROP_AREA_WIDTH: 450
                , CROP_AREA_HEIGHT: 150

                , FORM_TYPE: "SkillsPassport.LearnerInfo.Identification.Signature"
                , IMAGE_WIDTH: 300
                , IMAGE_HEIGHT: 100
                , IMAGE_ASPECT_RATIO: (300 / 100)

                , source_image_id: "Signature_Crop_Source"

                , preview_image_id: "Signature_Crop_Preview"

                , htmlTemplate: HtmlTemplate

                , editTemplate: EditTpl

                , displayTemplate: DisplayTpl

                , updateableContainerSelector: "div.existing-signature-details"

                , DIMENSION_METAKEY: "dimension"

                , CROPPING_METAKEY: "cropping"

                , SCALING_METAKEY: "scaling"

                        //Events of ImageFormView plus those here..
                , events: _.extend({
                    "europass:delete:confirmed": "reRender"
                }, ImageFormView.prototype.events)

                , enableFunctionalities: function () {
                    ImageFormView.prototype.enableFunctionalities.call(this);
                }

                , reRender: function () {
                    ImageFormView.prototype.reRender.call(this);
                }
            };

            SignaturePhotoFormView.prototype = $.extend(
                    //true,
                            {},
                            ImageFormView.prototype,
                            SignaturePhotoFormView.prototype
                            );

                    return SignaturePhotoFormView;
                }
        );