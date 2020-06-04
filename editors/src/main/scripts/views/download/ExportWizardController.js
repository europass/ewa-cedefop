define(
        [
            'jquery', //'require', 'underscore',
            'views/WizardController',
            'hbs!templates/download/exportwizard',
            'HttpUtils',
            'europass/http/WindowConfigInstance',
            'europass/http/ServicesUri',
            'europass/http/SessionManagerInstance',
            'europass/GlobalDocumentInstance',
            'views/download/wizard/OneStepExportWizard',
            'Utils',
            'analytics/EventsController'
        ],
        function ($, WizardController, htmlTemplate, HttpUtils, WindowConfig, ServicesUri, Session, GlobalDocument,
                OneStepExportWizard, Utils, Events) {//require,

            var ExportWizardController = WizardController.extend({
                event: new Events,
                isPartnerAvailable: false,
                events: {
                    "click #quick-download-btn": "quickDownload",
                    "click #export-wizard-init-btn": "exportWizard",

                    // Using Share button to do a post-back whenever interop partner is using remote-upload functionality.
                    "click #share-document-btn": "initiatePostBackForPartners"
                },
                WIZARD_REQUIRE_PATH: "views/download/wizard/ExportWizardProcess",
                WIZARD_FORM_ID: "ExportWizardForm",
                WIZARD_TYPE: "export",
                render: function () {
                    var ctx = {};
                    ctx.share = WindowConfig.showShareButton;
                    ctx.isPartner = this.isPartnerAvailable;
                    this.$el.html(htmlTemplate(ctx));
                },
                exportWizard: function () {
                    $('body').addClass('modal_overlay_open');
                    var formEl = this.findWizardForm();
                    var overlay = formEl.closest("div.overlay");
                    var exportWizard = new OneStepExportWizard({
                        el: overlay,
                        model: this.model
                    });
                    exportWizard.render();
                },
                /**
                 * Perform a quick download of the current document
                 */
                quickDownload: function (event) {
                    //check if iframe (hidden-preview) exists and if not place it on the main page
                    //otherwise remove it first						
                    var isIE = false || !!document.documentMode;
                    // Edge 20+
                    var isEdge = !isIE && !!window.StyleMedia;

                    var downloadurl = ServicesUri.document_conversion_to.preview_pdf + "?t=" + $.now();
                    this.model.set("SkillsPassport.DocumentInfo.DocumentType", GlobalDocument.getDocument());
                    this.model.set("SkillsPassport.DocumentInfo.Document", []); //clear so the preview doc will only show the current document.

                    var data = {
                        json: this.model.conversion().toTransferable(),
                        keepCv: false
                    };

                    if (/Android|webOS|iPhone|iPad|iPod|BlackBerry|IEMobile|Opera Mini/i.test(navigator.userAgent) | isEdge) {
                        //keep the old behavior in mobile devices and MS Edge
                        HttpUtils.download(downloadurl, data, null, "_blank");
                    } else {
                        if ($("#previewModalWrapper").length > 0) {
                            $("#previewModalWrapper").remove();
                        }

                        var embeddedModalIframe = "<div id=\"previewModalWrapper\" class=\"previewModal\">\n\
												<div class=\"previewModalContent\">\n\
													<span class=\"previewClose\">x</span>\n\
													<iframe style=\"width:100%; height:90%;\" class=\"hidden-preview\" name=\"previewiframe\">\n\
													</iframe>\n\
													</div>\n\
											  </div>";

                        //place div with iframe on page						
                        $(embeddedModalIframe).appendTo("body");

                        var iframe = $("iframe.hidden-preview");
                        iframe.bind("load.ewa.preview.iframe", $.proxy(this.previewIframeOnLoad, this));
                        if (/Chrome/i.test(navigator.userAgent)) {
                            this.pdfChromeBox(data, downloadurl);
                        } else {
                            HttpUtils.download(downloadurl, data, null, "previewiframe");

                            if (isIE) {
                                this.showPreviewModal();
                            }
                        }

                    }
                },

                pdfChromeBox: function (data, url) {
                    data = typeof data === 'string' ? data : $.param(data);
                    var xhr = new XMLHttpRequest();

                    xhr.open('POST', url, true);
                    xhr.setRequestHeader("Content-Type", "application/x-www-form-urlencoded");
                    xhr.responseType = "arraybuffer";

                    xhr.addEventListener("load", function (evt) {
                        var modal = $("#previewModalWrapper");
                        modal.show();

                        //close modal if close btn is clicked or user clicks anywhere outside of the modal
                        var spanBtn = $(".previewClose");
                        spanBtn.click(function () {
                            modal.remove();
                        });
                        var responseData = evt.target.response;
                        var blob = new Blob([responseData], {
                            type: "application/pdf"
                        });
                        var pdf = URL.createObjectURL(blob);
                        if (this.status === 200) {
                            $("iframe.hidden-preview").attr("src", pdf);
                        }
                    }, false);
                    xhr.send(encodeURI(data));
                },

                /**
                 * Function that runs when the remote method of previewing cv returns
                 */
                previewIframeOnLoad: function (event) {
                    this.event.preview();
                    var iframe = $(event.target);
                    iframe.unbind("load.ewa.preview.iframe", $.proxy(this.previewIframeOnLoad, this));
                    var isPreviewIframeEmpty = (iframe.contents().find("body").is(':empty') === "");
                    if (isPreviewIframeEmpty) {
                        //console.log("iframe was not loaded with data");
                        this.handleFailedPreview(iframe);
                    } else {
                        //console.log("iframe loaded with data");
                        //check if the iframe contains an error								
                        if (iframe.contents().find("head").find("title").text().indexOf("Error") !== -1) { //error with the service
                            //console.log("iframe contains incorrect data");
                            this.handleFailedPreview(iframe);
                        } else {//success
                            //console.log("iframe contains correct data");
                            //check if api returned some error 
                            var meta = iframe.contents().find("head").find("meta")[1];
                            if (iframe.contents().find("head").find(meta).attr("name") === "status") {
                                this.handleFailedPreview(iframe);
                            } else {
                                //show modal with iframe
                                this.showPreviewModal();
                            }
                        }
                    }
                },
                /**
                 * The preview failed
                 */
                handleFailedPreview: function (iframe) {
                    if ($("#previewModalWrapper").length > 0) {
                        $("#previewModalWrapper").remove();
                    }

                    var responseMsg = HttpUtils.readHtmlResponse(iframe);
                    var msg = HttpUtils.downloadErrorMessage(responseMsg);


                    //display error message
                    var messageContainer = $("body");
                    messageContainer.trigger("europass:message:show", ["error", msg]);

                },

                /**
                 * 
                 * Show custom preview modal
                 */
                showPreviewModal: function () {
                    var modal = $("#previewModalWrapper");
                    modal.show();

                    //close modal if close btn is clicked or user clicks anywhere outside of the modal
                    var spanBtn = $(".previewClose");
                    spanBtn.click(function () {
                        modal.remove();
                    });
                }

                , initiatePostBackForPartners: function (event) {
                    this.isPartnerAvailable = Utils.isPartnerAvailable();
                    if (this.isPartnerAvailable) {
                        event.stopPropagation();
                        $("body").trigger("europass:post:back:main:partners:finish");
                    }
                }

            });
            return ExportWizardController;
        }
);