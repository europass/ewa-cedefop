define(
        [
            'jquery',
            'jcrop',
//		EWA-1811
//		'Utils',
            'views/forms/attachment/AttachmentFormView',
            'views/forms/ImageFormView',
            'hbs!templates/forms/photo/photo',
            'hbs!templates/forms/photo/photoedit',
            'hbs!templates/forms/photo/photodisplay',
            'europass/http/FileManager',
            'views/attachment/AttachmentManagerInstance'
        ],
        function ($,
                jcrop,
//		EWA-1811
//		Utils,
                AttachmentFormView,
                ImageFormView,
                HtmlTemplate,
                PhotoEditTpl,
                PhotoDisplayTpl,
                FileManager,
                AttachmentManager) {

            var PhotoFormView = function (options) {
                ImageFormView.apply(this, [options]);
            };

            PhotoFormView.prototype = {

                CROP_AREA_WIDTH: 462
                , CROP_AREA_HEIGHT: 364

                , FORM_TYPE: "SkillsPassport.LearnerInfo.Identification.Photo"
                , IMAGE_WIDTH: 95
                , IMAGE_HEIGHT: 110
                , IMAGE_ASPECT_RATIO: (95 / 110)

                , source_image_id: "Photo_Crop_Source"

                , preview_image_id: "Photo_Crop_Preview"

                , htmlTemplate: HtmlTemplate

                , editTemplate: PhotoEditTpl

                , displayTemplate: PhotoDisplayTpl

                , updateableContainerSelector: "div.existing-photo-details"

                , DIMENSION_METAKEY: "dimension"

                , CROPPING_METAKEY: "cropping"

                , SCALING_METAKEY: "scaling"

            };
            PhotoFormView.prototype = $.extend(
                    //true, 
                            {},
                            ImageFormView.prototype,
                            PhotoFormView.prototype
                            );

                    return PhotoFormView;
                }
        );