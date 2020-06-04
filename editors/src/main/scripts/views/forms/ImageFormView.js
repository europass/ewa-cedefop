define(
        [
            'jquery',
            'jqueryui',
            'underscore',
            'jcrop',
            'Utils',
            'views/forms/attachment/AttachmentFormView',
            'hbs!templates/forms/photo/photo',
            'hbs!templates/forms/photo/photoedit',
            'hbs!templates/forms/photo/photodisplay',
            'europass/http/FileManager',
            'views/attachment/AttachmentManagerInstance',
            'ModalFormInteractions'
        ],
        function ($,
                jqueryui,
                _,
                jcrop,
                Utils,
                AttachmentFormView,
                photo,
                photoedit,
                photodisplay,
                FileManager,
                AttachmentManager,
                ModalFormInteractions) {

            var ImageFormView = function (options) {
                AttachmentFormView.apply(this, [options]);
            };

            ImageFormView.prototype = {

                FORM_TYPE: null

                , PHOTO_PATH_DEFAULT: "SkillsPassport.LearnerInfo.Identification.Photo"
                , SIGNATURE_PATH_DEFAULT: "SkillsPassport.LearnerInfo.Identification.Signature"

                , CROP_AREA_WIDTH: null
                , CROP_AREA_HEIGHT: null

                , IMAGE_PATH: null
                , IMAGE_WIDTH: null
                , IMAGE_HEIGHT: null
                , IMAGE_ASPECT_RATIO: null

                , source_image_id: null

                , preview_image_id: null

                , htmlTemplate: photo

                , editTemplate: photoedit

                , displayTemplate: photodisplay

                , updateableContainerSelector: null

                , CURRENT_DIMENSIONS: null

                , DIMENSION_METAKEY: "dimension"

                , CURRENT_CROPPING: null

                , CROPPING_METAKEY: "cropping"

                , CURRENT_SCALING: null

                , SCALING_METAKEY: "scaling"

                        /**
                         * @Override
                         */
                , renderReset: function (event) {

                    this.unsetCurrentScaling();
                    this.unsetCurrentCropping();

                    var html = this.displayTemplate(this.relevantModelSection());

                    var el = this.$el.find(this.updateableContainerSelector);

                    el.slideUp('slow', function () {

                        el.html(html);

                        el.slideDown('slow');
                    });
                }
                /**
                 * @Override
                 */
                , enableFunctionalities: function () {

                    AttachmentManager.prepare();

                    this.messageContainer = this.$el.find(this.messageContainerSelector);
                    this.uploadedContainer = this.$el.find(this.uploadedContainerSelector);

                    /* ---- DESIDE IF IT IS A PHOTO OR A SIGNATURE AND ACT ACORDINGLY ---- */

                    switch (this.FORM_TYPE) {

                        case this.SIGNATURE_PATH_DEFAULT:
                            this.IMAGE_PATH = this.SIGNATURE_PATH_DEFAULT;
                            break;
                        case this.PHOTO_PATH_DEFAULT:
                        default:
                            this.IMAGE_PATH = this.PHOTO_PATH_DEFAULT;
                            break;
                    }

                    FileManager.enableFileUpload(this.$el, {

                        selector: this.uploaderSelector,

                        isPhoto: this.FORM_TYPE == this.PHOTO_PATH_DEFAULT ? true : false,

                        isSignature: this.FORM_TYPE == this.SIGNATURE_PATH_DEFAULT ? true : false,

                        indexUploadBtn: 0,

                        messageContainer: this.messageContainer,

                        dropZone: this.$el.find(this.dropZoneSelector),

                        onAdd: {
                            f: this.onAddCallback,
                            scope: this
                        },
                        onDone: {
                            f: this.onUploadSuccess,
                            scope: this
                        }
                    });


                    //If there are already Photo data, that exceed the Europass dimensions, rerender!

                    this.onRenderHandlePhoto();

                }
                /**
                 * Runs when the form is rendered (the modal opens up)
                 */
                , onRenderHandlePhoto: function () {

                    var model = AttachmentManager.capture();
                    if (model === undefined || model === null) {
                        return false;
                    }

                    var photodata = model.get(this.getPath());

                    if (photodata === undefined || photodata === null) {
                        return false;
                    }

                    //set current dimensions
                    var dimensions = this.getDimensions(photodata);

                    if (dimensions === undefined || dimensions === null) {
                        return false;
                    }

                    this.setCurrentDimension(dimensions);

                    var cropping = Utils.getMetadata(photodata, "cropping");
                    if (cropping !== undefined && cropping !== null) {
                        this.setCurrentCropping(cropping);
                    }

                    /*
                     * The photo.hbs includes the photodisplay.hbs to show the photo
                     * However if there is an already uploaded photo, 
                     * which is not within the Europass dimensions,
                     * we will show the cropping functionality.
                     * If the user has already cropped the photo, we will need
                     * to show the croppingg functionality to the state where
                     * it was when the user hit save.
                     */
                    if (this.dimensionsWithinEuropass(photodata) === false) {
                        this.updatePhotoHtml(false); //not new upload
                    }

                    this.applyWhenMissingDocuments();
                }

                , keepImage: function (photodata) {

                    switch (this.IMAGE_PATH) {

                        case this.PHOTO_PATH_DEFAULT:
                            AttachmentManager.keepPhoto(photodata);
                            break;

                        case this.SIGNATURE_PATH_DEFAULT:
                            AttachmentManager.keepSignature(photodata);
                            break;

                        default:
                            return false;

                    }
                }

                /**
                 * This runs whenever a new photo is uploaded 
                 */
                , onUploadSuccess: function (json) {

                    var photodata = json.FileData;
                    if (photodata === undefined || photodata === null) {
                        var messageContainer = this.$el.find(this.messageContainerSelector);
                        FileManager.displayError(messageContainer);
                        return false;
                    }

                    //Set the uploaded image to the internal model depending if it is photo or signature
                    this.keepImage(photodata);

                    //set current dimensions
                    var dimensions = this.getDimensions(photodata);
                    if (dimensions === undefined || dimensions === null) {
                        var messageContainer = this.$el.find(this.messageContainerSelector);
                        FileManager.displayError(messageContainer, 500);
                        return false;
                    }

                    this.setCurrentDimension(dimensions);
                    //Set the scaling info to the internal model as well
                    this.setScaling(photodata);

                    this.$el.trigger("europass:attachment:temp:model:changed");

                    //Save should be available now...
                    this.updateMenuAvailability(true);

                }
                /**
                 * Call to re-render the specific updatable part based on Attachment Manager's Temporary Model.
                 * Finally trigger the suitable event.
                 */
                , reRender: function (event) {
                    //NEW UPLOAD!
                    /*
                     * This will update the html area according to the newly uploaded photo.
                     */
                    this.updatePhotoHtml(true);

                    this.$el.trigger("europass:attachment:form:rerender");

                }
                /**
                 * Re-render the specific updatable part based on Attachment Manager's Temporary Model.
                 * @param animation whether to use animation or not.
                 */
                , updatePhotoHtml: function (newUpload) {

                    var html = "";

                    //Will read the scale config either from the current scope, 
                    //the metadata of the photo, 
                    //or re-calculate it based on the uploaded photo.
                    var scaleConfig = this.getScaling();

                    if (scaleConfig.withinDimensions === true) {

                        //FITS Europass Dimensions !
                        this.setScaling(null);
                        this.deleteCurrentScaling();
                        this.deleteCurrentCropping();

                        html = this.displayTemplate(this.relevantModelSection());

                    } else {
                        //DOES NOT FIT ! Display the cropping functionality
                        html = this.editTemplate(this.relevantModelSection());
                    }




                    var el = this.$el.find(this.updateableContainerSelector);

                    if (el.length > 0) {

                        //NEW UPLOAD!!!! - We are here because a new file is uploaded
                        if (newUpload === true) {
                            this.newUpload = true;

                            var that = this;

                            //Show with animation...
                            el.slideUp('slow', function () {

                                el.html(html);

                                if (scaleConfig.withinDimensions === false) {
                                    that.enableCropping(newUpload);
                                }

                                el.slideDown('slow');
                            });
                        } else {
                            this.newUpload = false;
                            //NOT NEW UPLOAD!!! - We are here because the form is opened with an already existing big file.

                            el.html(html);

                            if (scaleConfig.withinDimensions === false) {
                                this.enableCropping(newUpload);
                            }
                        }
                    }
                }
                /**
                 * Enable JCrop
                 */
                , enableCropping: function (newUpload) {

                    var dimensions = this.CURRENT_DIMENSIONS;

                    if (dimensions === undefined || dimensions === null) {
                        dimensions = {
                            w: this.CROP_AREA_WIDTH,
                            h: this.CROP_AREA_HEIGHT
                        };
                    }

                    var config = {
                        //To set the true image size, pass an array of [w,h] to this option:
                        trueSize: [dimensions.w, dimensions.h],

                        allowSelect: false,
                        allowMove: true,
                        allowResize: true/*false*/,

                        // decide whether it is a photo or a signature
                        baseClass: 'photo-crop',
                        onChange: $.proxy(this.showPreview, this),

                        aspectRatio: this.IMAGE_WIDTH / this.IMAGE_HEIGHT,

                        boxWidth: this.CROP_AREA_WIDTH,
                        boxHeight: this.CROP_AREA_HEIGHT,

//					setSelect: [ selectPointX , selectPointY , selectPointX2, selectPointY2 ],

                        createHandles: ['nw', 'ne', 'se', 'sw'],
                        createDragbars: [],
                        borderOpacity: 0.9,
                        handleOpacity: 0.9,
                        handleSize: 4,
                        bgColor: ''
                    };

                    this.calculateCropping(dimensions, config);

                    if (newUpload === false && this.CURRENT_CROPPING !== null) {


                        var crop = this.CURRENT_CROPPING;

                        // console.log("Current Cropping: " + JSON.stringify(crop));

                        var prevX = crop.ox;
                        var prevY = crop.oy;

                        var prevX2 = prevX + (this.IMAGE_WIDTH * (crop.ox2 / crop.width));
                        var prevY2 = prevY + (this.IMAGE_HEIGHT * (crop.oy2 / crop.height));
//					
//					var prevX2 = this.CURRENT_CROPPING.ox2 - this.CURRENT_CROPPING.x2 + (this.CURRENT_CROPPING.width / xRatio);
//					var prevY2 = this.CURRENT_CROPPING.oy2 - this.CURRENT_CROPPING.y2 + (this.CURRENT_CROPPING.height / yRatio);

                        //Cases:

//					var xRatio = ( crop.ox2 - crop.width ) / ( crop.ox2 - crop.width + this.IMAGE_WIDTH );
//					var yRatio = ( crop.oy2 - crop.height ) / ( crop.oy2 - crop.height + this.IMAGE_HEIGHT );
                        /*					var xRatio =  Math.round(this.IMAGE_WIDTH / crop.ox2 );
                         var yRatio =  Math.round(this.IMAGE_HEIGHT / crop.oy2 );
                         
                         if(crop.x == crop.ox)
                         prevX2 = (( crop.ox2 - crop.width ) * xRatio ) + this.IMAGE_WIDTH;
                         if(crop.y == crop.oy)
                         prevY2 = (( crop.oy2 - crop.height ) * yRatio ) + this.IMAGE_HEIGHT;
                         */

                        config.setSelect = [prevX, prevY, prevX2, prevY2];
//					this.setCurrentCropping( this.CURRENT_CROPPING );

                        //console.log(JSON.stringify({"StartX": prevX, "StartY": prevY, "EndX": prevX2, "EndY": prevY2}));
                    }

                    if (this.CURRENT_SCALING !== null && this.CURRENT_SCALING.scale !== null) {
                        var scale = this.CURRENT_SCALING.scale;
                        config.boxWidth = (scale.boxWidth == 0) ? dimensions.w : scale.boxWidth;
                        config.boxHeight = (scale.boxHeight == 0) ? dimensions.h : scale.boxHeight;
                    }

                    //API invocation method
                    var jcrop_api = null;
                    var sourcePhotoEl = $("#" + this.source_image_id + "");
                    if (sourcePhotoEl.length > 0 && $.isFunction(sourcePhotoEl.Jcrop) === true) {

                        var ua = navigator.userAgent.toLowerCase();
                        // TODO ndim tidy this up
                        var isWindowsSafari = (ua.indexOf("safari/") !== -1 &&
                                ua.indexOf("windows") !== -1 &&
                                ua.indexOf("chrom") === -1) ? true : false;

                        //EWA-1567 Personal photo image displays improperly after upload
                        //for photo upload  in Windows safari wait some 300ms before rendering, 
                        if (isWindowsSafari && this.IMAGE_PATH.indexOf("Photo") > 0) {

                            sourcePhotoEl.parent().hide();

                            setTimeout(function () {

                                sourcePhotoEl.Jcrop(config, function () {
                                    jcrop_api = this;
                                });
                                sourcePhotoEl.parent().show();

                            }, 300);
                        } else {
                            sourcePhotoEl.Jcrop(config, function () {
                                jcrop_api = this;
                            });
                        }
                    }
                    this.JCROP_API = jcrop_api;
                }

                , calculateCropping: function (dimensions, config) {

                    //EWA-971: Decide on the cropping frame dimensions
                    var diff = 0;
                    if (dimensions.w >= dimensions.h) {
                        diff = Math.round(dimensions.w / this.IMAGE_WIDTH);
                    } else {
                        diff = Math.round(dimensions.h / this.IMAGE_HEIGHT);
                    }
                    var magnifier = 100;
                    if (diff >= 16) {
                        magnifier = 14;
                    } else if (diff < 16 && diff >= 10) {
                        magnifier = 10;
                    } else if (diff < 10 && diff >= 6) {
                        magnifier = 8;
                    } else if (diff < 6 && diff >= 4) {
                        magnifier = 4;
                    } else if (diff < 4 && diff >= 2) {
                        magnifier = 2;
                    }

                    var optimalWidth = ((dimensions.w < this.IMAGE_WIDTH) ? dimensions.w : this.IMAGE_WIDTH);
                    var optimalHeight = ((dimensions.h < this.IMAGE_HEIGHT) ? dimensions.h : this.IMAGE_HEIGHT);

                    var minOptimalWidth = this.IMAGE_WIDTH;
                    var minOptimalHeight = this.IMAGE_HEIGHT;

                    if (optimalWidth < this.IMAGE_WIDTH) {

                        minOptimalWidth = optimalWidth;
                        minOptimalHeight = optimalWidth / config.aspectRatio;
                    }

                    if (optimalHeight < this.IMAGE_HEIGHT) {

                        minOptimalWidth = optimalHeight * config.aspectRatio;
                        minOptimalHeight = optimalHeight;
                    }

                    config.minSize = [minOptimalWidth, minOptimalHeight];

                    var selectPointX = 0;
                    var selectPointY = 0;

                    var selectPointX2 = optimalWidth * magnifier;
                    var selectPointY2 = optimalHeight * magnifier;

                    config.setSelect = [selectPointX, selectPointY, selectPointX2, selectPointY2];
                }


                /**
                 * Method that runs as the cropper box moves in order to capture the cropping point.
                 * NOTE!!! Jcrop returns coordinates mapped to the "true" image size. 
                 */
                , showPreview: function (coords) {

                    var rx = this.IMAGE_WIDTH / coords.w;
                    var ry = this.IMAGE_HEIGHT / coords.h;

                    var dimensions = this.CURRENT_DIMENSIONS;
                    if (dimensions === undefined || dimensions === null) {
                        dimensions = {
                            w: this.CROP_AREA_WIDTH,
                            h: this.CROP_AREA_HEIGHT
                        };
                    }

                    var cropping = {
                        width: Math.round(rx * dimensions.w),
                        height: Math.round(ry * dimensions.h),
                        x: Math.round(rx * coords.x),
                        y: Math.round(ry * coords.y),
                        x2: Math.round(rx * coords.x2),
                        y2: Math.round(ry * coords.y2),
                        ox: coords.x,
                        oy: coords.y,
                        ox2: dimensions.w,
                        oy2: dimensions.h
                    };

                    //TODO: check to find cropping formula
                    //console.log(coords.w+","+coords.h+": "+JSON.stringify(cropping));

                    //width and height to the ORIGINAL image!!!
                    var previewPhoto = $("#" + this.preview_image_id + "");
                    previewPhoto.css({
                        width: cropping.width + 'px',
                        height: cropping.height + 'px',
                        marginLeft: '-' + cropping.x + 'px',
                        marginTop: '-' + cropping.y + 'px'
                    });

                    this.setCurrentCropping(cropping);
                }
                /**
                 * @Override
                 * 
                 * Relevant model here is the entire model
                 */
                , relevantModelSection: function () {
                    var capturedModel = AttachmentManager.capture();
                    if (capturedModel === undefined || capturedModel === null) {
                        return {};
                    } else {
                        return capturedModel.get("SkillsPassport");
                    }
                }

                /**
                 * Re-rendering the attachment details...
                 * Perform any cleanup, such as clearing up the uploaded section
                 */
                , onReRender: function (event) {
                    FileManager.cleanupUploaded(this.$el);
                }
                , saveImage: function () {
                    switch (this.IMAGE_PATH) {

                        case this.PHOTO_PATH_DEFAULT:
                            AttachmentManager.savePhoto(this.getCropping(), this.getScaling());
                            this.model.trigger("model:binaries:reset");
                            break;

                        case this.SIGNATURE_PATH_DEFAULT:
                            AttachmentManager.saveSignature(this.getCropping(), this.getScaling());
                            this.model.trigger("model:binaries:reset");
                            break;

                        default:
                            return false;

                    }
                }
                /**
                 * @Override
                 */
                , submitted: function (event) {
                    //start the waiting indicator...
                    this.$el.trigger("europass:waiting:indicator:show");

                    //Will save the photo along with the crop config if not null. If null it will remove the crop config from metadata

                    //Set the uploaded image to the internal model depending if it is photo or signature
                    this.saveImage();

                    this.closeModal();

                    this.model.trigger("content:changed", this.section, this.origin);

                }
                , formDataChanged: function() {
                    if (AttachmentManager.internalModel === undefined || AttachmentManager.internalModel === null) {
                        return false;
                    }
                    var hasChanged = false;
                    if (!_.isEqual(AttachmentManager.model.get(this.IMAGE_PATH), AttachmentManager.internalModel.get(this.IMAGE_PATH))) {
                        hasChanged = true;
                    } else if (!_.isUndefined(AttachmentManager.internalModel.get(this.IMAGE_PATH)) && !_.isNull(AttachmentManager.internalModel.get(this.IMAGE_PATH))) {
                        if (!_.isEqual(Utils.getMetadata(AttachmentManager.model.get(this.IMAGE_PATH), "cropping"), this.CURRENT_CROPPING) || 
                                !_.isEqual(Utils.getMetadata(AttachmentManager.model.get(this.IMAGE_PATH), "scaling"), this.CURRENT_SCALING)) {
                            hasChanged = true;
                        }
                    }
                    return hasChanged;
                }
                , modalClosed: function(event) {
                    if (this.formDataChanged()) {
                        ModalFormInteractions.confirmSaveSection(event, this.frm.attr("id"));
                    } else {
                        this.closeModal();
                    }
                }
                , disableCrop: function () {
                    if (this.JCROP_API !== undefined && this.JCROP_API !== null) {
                        this.JCROP_API.destroy();
                    }
                    this.JCROP_API = null;
                }
                /** When the modal is closed **/
                , onModalClose: function () {
                    FileManager.disableFileUpload(this.$el);
                    this.disableCrop();
                }
                /** when Modal is cancelled **/
                , cancelled: function () {
                    AttachmentFormView.prototype.cancelled.apply(this, []);
                }
                /************************************************************************/
                /************************************************************************/
                /*************** SCALING / CROPPING / DIMENSIONS ************************/
                /************************************************************************/
                /**
                 * Read the metadata and in case there is a dimension metadata,
                 * prepare the configuration of the crop object
                 */
                , calculateScaling: function (photodata) {
                    //Check the dimensions
                    var scaling = {
                        withinDimensions: this.dimensionsWithinEuropass(photodata)
                    };

                    var scale = this.calculateBoxSizing(photodata);
                    if (scale !== null) {
                        scaling.scale = scale;
                    }
                    return scaling;
                }
                /**
                 * Decide how to resize the image
                 */
                , calculateBoxSizing: function (photodata) {
                    //instantiate to provide for the case where dimensions is null...
                    var w = this.CROP_AREA_WIDTH;
                    var h = this.CROP_AREA_HEIGHT;

                    var dimensions = this.CURRENT_DIMENSIONS;
                    if (dimensions === undefined || dimensions === null) {
                        dimensions = {
                            w: this.CROP_AREA_WIDTH,
                            h: this.CROP_AREA_HEIGHT
                        };
                    } else if (dimensions !== undefined && dimensions !== null) {
                        w = dimensions.w;
                        h = dimensions.h;
                    }

                    var wFitPhoto = (w <= this.IMAGE_WIDTH);
                    var hFitPhoto = (h <= this.IMAGE_HEIGHT);
                    var wFitCrop = (w > this.IMAGE_WIDTH && w <= this.CROP_AREA_WIDTH);
                    var hFitCrop = (h > this.IMAGE_HEIGHT && h <= this.CROP_AREA_HEIGHT);
                    var wGtCrop = (w > this.CROP_AREA_WIDTH);
                    var hGtCrop = (h > this.CROP_AREA_HEIGHT);

                    var scale = null;
                    //1. NO CROPPING: fits the photo
                    //if ( wFitPhoto && hFitPhoto ){ 
                    /*   NO SCALING
                     *  width AND height greater than photo, but both fit the crop area
                     */
                    if (wFitCrop && hFitCrop) {
                        scale = {
                            boxWidth: w,
                            boxHeight: h
                        };
                        //console.log("2. NO SCALING - yes adjustment");
                    }
                    /* 2. SCALE BY HEIGHT
                     * Width is greater than photo BUT fits crop, AND height fits photo.
                     * Thus scale height to be at least equal to the photo height
                     */
                    else if (wFitCrop && hFitPhoto) {
                        scale = {
//						boxWidth  : 0,
//						boxHeight : this.IMAGE_HEIGHT
                            boxWidth: 0,
                            boxHeight: dimensions.h
                        };
                        //console.log("3. SCALE BY HEIGHT - yes adjustment");
                    }
                    /* 3. SCALE BY HEIGHT 2
                     * Width fits photo, BUT height is greater than photo and crop.
                     * Thus scale height to be at least equal to the photo height
                     */
                    /*				else if ( wFitCrop && hGtCrop ){ 
                     scale = {
                     //						boxWidth  : 0,
                     //						boxHeight : this.IMAGE_HEIGHT
                     boxWidth  : 0,
                     boxHeight : this.CROP_AREA_HEIGHT
                     };
                     //console.log("3. SCALE BY HEIGHT - yes adjustment");
                     } 
                     /*
                     * 4. SCALE BY WIDTH
                     * Width fits photo BUT height is greater than photo, yet fits crop
                     * Thus scale width to be at least equal to the photo width
                     */
                    else if (wFitPhoto && hFitCrop) {
                        scale = {
//						boxWidth  : this.IMAGE_WIDTH,
//						boxHeight : 0
                            boxWidth: dimensions.w,
                            boxHeight: 0
                        };
                        //console.log("4. SCALE BY WIDTH - yes adjustment");
                    }
                    /*
                     * 5. SCALE BOTH
                     * either of them greater than the crop
                     */
                    else if (wGtCrop || hGtCrop) {
                        /* However decide on which dimension to fit
                         * Fit to the one that requires the more scaling.
                         */
                        var ratioW = (w / this.CROP_AREA_WIDTH);
                        var ratioH = (h / this.CROP_AREA_HEIGHT);
                        var scaleW = (ratioW >= ratioH);
                        scale = {
                            boxWidth: (scaleW === true) ? this.CROP_AREA_WIDTH : 0,
                            boxHeight: (scaleW === false) ? this.CROP_AREA_HEIGHT : 0
                        };
                        //console.log("5. SCALE BOTH - yes adjustment");
                    }

                    return scale;
                }
                /**
                 * 1. calculates the scalling configuration based on the photodata
                 * 2. stores it in the current object (ImageFormView)
                 * 3. stores it in the internal model as metadata of the photodata
                 * 
                 * If photodata is null, then remove the scaling info
                 */
                , setScaling: function (photodata) {
                    //remove if null
                    if (photodata === null) {
                        this.deleteCurrentScaling();
                    } else {
                        //Set the scaling info to the internal model as well
                        var scaleConfig = this.calculateScaling(photodata);
                        this.setCurrentScaling(scaleConfig);
                    }
                }
                /**
                 * reads the scaling configuration
                 * 1. from the current object
                 * 2. from the metadata of the photodata
                 * 3. from recalculation based onthe photodata
                 */
                , getScaling: function () {

                    var scaleConfig = this.CURRENT_SCALING;
                    //console.log("1. scale config from this...");
                    //if this.scaleConfig is not defined... Get it from the photo data
                    if (scaleConfig === undefined || scaleConfig === null) {
                        //console.log("2. scale config from photo data...");
                        scaleConfig = this.recallScaling();
                    }
                    //if still not defined, recalculate it based on the photo dimensions...
                    if (scaleConfig === undefined || scaleConfig === null) {
                        //console.log("3. scale config from recalculation ");
                        var model = AttachmentManager.capture();
                        if (model === undefined || model === null) {
                            return scaleConfig;
                        }
                        scaleConfig = this.calculateScaling(model.get(this.getPath()));
                        this.setCurrentScaling(scaleConfig);
                        this.storeCurrentScaling();
                    }
                    //if still not defined, then there are no photo dimensions...
                    return scaleConfig;

                }
                /**
                 * reads the cropping configuration
                 * 1. from the current object
                 * 2. from the metadata of the photodata
                 */
                , getCropping: function () {

                    var cropConfig = this.CURRENT_CROPPING;
                    console.log("getcropping1");
                    console.log(cropConfig);
                    //console.log("1. crop config from this...");
                    //if this.scaleConfig is not defined... Get it from the photo data
                    if (cropConfig === undefined || cropConfig === null) {
                        //console.log("2. crop config from photo data...");
                        cropConfig = this.recallCropping();
                        console.log("getcropping2");
                        console.log(cropConfig);
                    }
                    //if still not defined, then there are no photo dimensions...
                    return cropConfig;

                }
                /**
                 * Checks if the specific photo's dimensions
                 * are within the Europass dimensions
                 */
                , dimensionsWithinEuropass: function (photodata) {
                    var dimensions = this.getDimensions(photodata);

                    if (dimensions !== null) {

                        var w = dimensions.w;
                        var h = dimensions.h;

                        var wFitPhoto = (w <= this.IMAGE_WIDTH);
                        var hFitPhoto = (h <= this.IMAGE_HEIGHT);

                        return (wFitPhoto && hFitPhoto);
                    }

                    return null;
                }
                /**
                 * Checks if the specific photo's dimensions
                 * are within the Europass dimensions
                 */
                , getDimensions: function (photodata) {

                    var dimension = Utils.getMetadata(photodata, "dimension");
                    if (dimension !== null && dimension !== "") {
                        var wh = dimension.split("x");
                        var w = wh[0];
                        var h = wh[1];
                        return {
                            w: w,
                            h: h
                        };
                    }
                    return null;
                }

                /********* DIMENSIONS **********/
                , setCurrentDimension: function (dimensions) {
                    this.CURRENT_DIMENSIONS = dimensions;
                }
                , unsetCurrentDimension: function () {
                    this.CURRENT_DIMENSIONS = null;
                }
                , storeCurrentDimension: function () {
                    Utils.setMetadata(AttachmentManager.capture(), this.getPath(), this.DIMENSION_METAKEY, this.CURRENT_DIMENSIONS);
                }
                , recallDimension: function () {
                    Utils.getModelMetadata(AttachmentManager.capture(), this.getPath(), this.DIMENSION_METAKEY);
                }
                /********* CROPPING **********/
                , setCurrentCropping: function (cropping) {
                    this.CURRENT_CROPPING = cropping;
                }
                , unsetCurrentCropping: function () {
                    this.CURRENT_CROPPING = null;
                }
                , storeCurrentCropping: function () {
                    Utils.setMetadata(AttachmentManager.capture(), this.getPath(), this.CROPPING_METAKEY, this.CURRENT_CROPPING);
                }
                , deleteCurrentCropping: function () {
                    Utils.removeMetadata(AttachmentManager.capture(), this.getPath(), this.CROPPING_METAKEY);
                    this.unsetCurrentCropping();
                }
                , recallCropping: function () {
                    Utils.getModelMetadata(AttachmentManager.capture(), this.getPath(), this.CROPPING_METAKEY);
                }
                /********* SCALING **********/
                , setCurrentScaling: function (scaling) {
                    this.CURRENT_SCALING = scaling;
                }
                , unsetCurrentScaling: function () {
                    this.CURRENT_SCALING = null;
                }
                , storeCurrentScaling: function () {
                    Utils.setMetadata(AttachmentManager.capture(), this.getPath(), this.SCALING_METAKEY, this.CURRENT_SCALING);
                }
                , deleteCurrentScaling: function () {
                    Utils.removeMetadata(AttachmentManager.capture(), this.getPath(), this.SCALING_METAKEY);
                    this.unsetCurrentScaling();
                }
                , recallScaling: function () {
                    Utils.getModelMetadata(AttachmentManager.capture(), this.getPath(), this.SCALING_METAKEY);
                }

                , getPath: function () {
                    return this.IMAGE_PATH;
                }

                , applyWhenMissingDocuments: function () {
                    this.$el.find('#Signature_Crop_Source').on('error', function (evt) {
                        $(".existing-signature-details").html("<img src='/editors/static/ewa/images/missingDocumentIcon.png' style='width:100px'/>");
                    });

                    this.$el.find('#Photo_Crop_Source').on('error', function (evt) {
                        $(".existing-photo-details").html("<img src='/editors/static/ewa/images/missingDocumentIcon.png' style='width:100px'/>");
                    });
                }
            };

            ImageFormView.prototype = $.extend(
                    //true, 
                            {},
                            AttachmentFormView.prototype,
                            ImageFormView.prototype
                            );

                    return ImageFormView;
                }
        );