define(
        [
            'jquery', //'underscore',
            'backbone',
            'Utils',
            'cookie',
            'HttpUtils',
            'i18n!localization/nls/Notification',
//		'i18n!localization/nls/GuiLabel',
            'hbs!templates/download/local',
            'europass/http/WindowConfigInstance',
            'views/download/DownloadController',
            'analytics/EventsController'
        ],
        function ($, Backbone, Utils, Cookies, HttpUtils, Notification, Template, WindowConfig, DownloadController, Events) {

            var DownloadView = Backbone.View.extend({

                event: new Events,

//			events: {
//				"wizard:process:completed" : "onCompleted"
//				"europass:wizard:export:complete" : "onCompleted",
//			},

                alreadyRendered: false,

                onClose: function () {
                    $('iframe.hidden-download').unbind("load.ewa.download.iframe", $.proxy(this.iframeOnload, this));
                    this.alreadyRendered = false;

                    this.finishBtn.hide();
//				this.parentView.cleanup();

                    this.downloadController.cleanup();
                    delete this.downloadController;
                },
                initialize: function (options) {

                    this.parentView = options.parentView;

                    this.finishBtn = options.parentView.finishBtn;

                    this.messageContainer = options.messageContainer;

                    this.downloadController = new DownloadController({
                        relatedController: this,
                        messageContainer: options.messageContainer,
                        info: options.info
                    });

                    this.contextRoot = WindowConfig.getDefaultEwaEditorContext();

                    //live works on event bubbling mechanism where as iframe load event is not user action triggered. 
                    //So we cannot use live to handle the iframe load event.
                    this.iframeBound = false;

                }
                , render: function () {
                    if (this.alreadyRendered === false) {
                        //this.$el.html(html);

                        //Adds the hidden iframe
                        var html = "<iframe class=\"hidden-download\" name=\"downloadiframe\" style=\"display: none;\"></iframe>";
                        $(html).appendTo(this.$el);

                        this.alreadyRendered = true;
                    }
                    //this.parentView.cleanupFeedback();

                    //this.parentView.nextBtn.hide();
                    //this.finishBtn.show();
                }
                /**
                 * Function that runs when the remote method of downloading returns
                 */
                , iframeOnload: function (event) {

//console.log("iframe on load...");
                    var iframe = $(event.target);
                    var success = true;

                    var isSuccessfulDownload = ($.trim(iframe.contents().find("head").html()) === "");
                    if (!isSuccessfulDownload) {
                        success = false;
                        this.handleFailedDownload(iframe);
                    }
//console.log("stop waiting indicator with success status '"+success+"'");
                    //stop the waiting indicator...
                    this.disableWaitingIndicator(success);
                }
                /**
                 * The download failed
                 */
                , handleFailedDownload: function (iframe) {
                    console.log('error')
                    var response = HttpUtils.readHtmlResponse(iframe);
                    var msg = HttpUtils.downloadErrorMessage(response.msg);
                    this.messageContainer.trigger("europass:message:show", ["error", msg], false);
                }
                /**
                 * When Download is clicked
                 */
                , doFinish: function () {
                    //this.parentView.cleanupFeedback();
                    if (navigator.cookieEnabled) {
                        //start the waiting indicator...
                        this.enableWaitingIndicator();
                    }

                    var iframe = $("iframe.hidden-download");
                    if (this.iframeBound === false) {
                        iframe.bind("load.ewa.download.iframe", $.proxy(this.iframeOnload, this));
                        this.iframeBound = true;
                    }
                    this.callRemoteMethod();
                }
                /**
                 * Do the actual FORM POST
                 */
                , callRemoteMethod: function (url) {
                    var url = this.downloadController.decideUrl();
                    console.log(url)
                    this.event.exportTo(url);
                    var newModel = JSON.parse(this.model.conversion().toTransferable());

                    // All set to true by default
                    var hasCV = true;
                    var hasCL = true;
                    var hasESP = true;
                    var hasLP = true;

                    // Check for existing documents
                    if (newModel.SkillsPassport.DocumentInfo.DocumentType) {
                        hasCV = newModel.SkillsPassport.DocumentInfo.DocumentType === "ECV"
                                || newModel.SkillsPassport.DocumentInfo.DocumentType === "ECV_ESP";
                        hasESP = newModel.SkillsPassport.DocumentInfo.DocumentType === "ESP"
                                || newModel.SkillsPassport.DocumentInfo.DocumentType === "ECV_ESP";
                        hasCL = newModel.SkillsPassport.DocumentInfo.DocumentType === "ECL";
                        hasLP = newModel.SkillsPassport.DocumentInfo.DocumentType === "ELP";

                    }
                    if (newModel.SkillsPassport.DocumentInfo.Document) {
                        hasCL = hasCL || newModel.SkillsPassport.DocumentInfo.Document.indexOf('ECL') !== -1;
                        hasLP = hasLP || newModel.SkillsPassport.DocumentInfo.Document.indexOf('ELP') !== -1;
                    }
//				console.log({'cv': hasCV, 'esp': hasESP,'lp': hasLP, 'cl': hasCL});

                    // Manipulate Document Preferences (ESP has no Preferences)
                    if (newModel.Preferences) {
                        if (!hasCL) {
//						console.log('Deleted ECL Pref')
                            delete newModel.Preferences.ECL;
                        }
                        if (!hasLP) {
//						console.log('Deleted LP Pref')
                            delete newModel.Preferences.ELP;
                        }
                        if (!hasCV) {
//						console.log('Deleted ECV Pref')
                            delete newModel.Preferences.ECV;
                        }
                    }

                    // Manipulate Printing Preferences Only for ECV and ELP
                    if (newModel.SkillsPassport.PrintingPreferences) {
                        if (!hasCL && newModel.SkillsPassport.PrintingPreferences.ECL) {
//						console.log('Deleted ECL Print Pref');
                            delete newModel.SkillsPassport.PrintingPreferences.ECL;
                        }
                        if (!hasCV && newModel.SkillsPassport.PrintingPreferences.ECV) {
//						console.log('Deleted ECV Print Pref');
                            delete newModel.SkillsPassport.PrintingPreferences.ECV;
                        }
                    }

                    // Manipulate the real data for each document

                    if (!hasCL && newModel.SkillsPassport.CoverLetter) {
                        delete newModel.SkillsPassport.CoverLetter;
                        if (newModel.SkillsPassport.LearnerInfo && newModel.SkillsPassport.LearnerInfo.Identification) {
//						console.log('Deleted ECL Signature');
                            delete newModel.SkillsPassport.LearnerInfo.Identification.Signature;
                        }
                    }
                    if (newModel.SkillsPassport.LearnerInfo && newModel.SkillsPassport.LearnerInfo.Skills && newModel.SkillsPassport.LearnerInfo.Skills.Linguistic) {
                        var linguisticSection = newModel.SkillsPassport.LearnerInfo.Skills.Linguistic;
                    }
                    if (!hasLP && linguisticSection) {
                        if (linguisticSection.ForeignLanguage) {
                            linguisticSection.ForeignLanguage.map(function (item) {
                                if (item.Experience) {
                                    delete item.Experience;
                                }
                                if (item.Certificate) {
                                    item.Certificate.map(function (keys) {
                                        Object.keys(keys).forEach(function (title) {
                                            if (title !== 'Title') {
                                                delete keys[title];
                                            }
                                        });
                                    });
                                }
                            });
                        }
                    }

                    if (!hasESP && newModel.SkillsPassport.Attachment) {
                        delete newModel.SkillsPassport.Attachment;
                        delete newModel.SkillsPassport.LearnerInfo.ReferenceTo;
                        this.removeRef(newModel.SkillsPassport.LearnerInfo);
                    }

                    if (!hasCV && newModel.SkillsPassport.LearnerInfo) {
                        Object.keys(newModel.SkillsPassport.LearnerInfo).forEach(function (item) {
                            if (item === "Identification" && hasCL) {
                                Object.keys(newModel.SkillsPassport.LearnerInfo.Identification).forEach(function (subItem) {
                                    if (subItem === 'Demographics') {
                                        delete newModel.SkillsPassport.LearnerInfo.Identification[subItem];
                                    }
                                });
                            } else if (item === "Identification" && hasLP) {
                                Object.keys(newModel.SkillsPassport.LearnerInfo.Identification).forEach(function (subItem) {
                                    if (subItem !== 'PersonName') {
                                        delete newModel.SkillsPassport.LearnerInfo.Identification[subItem];
                                    }
                                });
                            } else if (item === "Skills" && hasLP) {
                                Object.keys(newModel.SkillsPassport.LearnerInfo.Skills).forEach(function (subItem) {
                                    if (subItem !== 'Linguistic') {
                                        delete newModel.SkillsPassport.LearnerInfo.Skills[subItem];
                                    }
                                });
                            } else if (item === 'ReferenceTo') {
                                if (!hasESP && newModel.SkillsPassport.LearnerInfo.ReferenceTo) {
                                    delete newModel.SkillsPassport.LearnerInfo[item];
                                }
                            } else {
                                delete newModel.SkillsPassport.LearnerInfo[item];
                            }
                        });
                    }

                    var permissionToKeepCv = this.$el.find(":input[type=\"checkbox\"]#keep-cv-permission:checked").length > 0;

                    var data = {
                        json: JSON.stringify(newModel),
                        keepCv: permissionToKeepCv
                    };

                    //Download token
                    var text = "DOWNLOAD-" + new Date().getTime();
                    this.activeDownloadToken = Utils.hashCode(text) + "-" + Utils.randomInK();
                    data["downloadToken"] = this.activeDownloadToken;
                    data["cookieUserID"] = Utils.readCookie();

                    var isIOS = ("IOS" === WindowConfig.operatingSystem);
                    if (isIOS) {
                        HttpUtils.download(url, data, null, "_blank");
                    } else {
                        HttpUtils.download(url, data, null, "downloadiframe");
                    }
                }

//			,onCompleted: function(){
//				
//				this.parentView.completeBtn.hide();
//				this.finishBtn.hide();
//			}
                , removeRef: function (obj) {
                    for (var prop in obj) {
                        if (prop === 'ReferenceTo')
                            delete obj[prop];
                        else if (typeof obj[prop] === 'object')
                            this.removeRef(obj[prop]);
                    }
                }

                /**
                 * Enable a waiting indication.
                 * 
                 * For the case of file download we neeed to start a timer
                 * which will check for whether there exists a cookie 
                 * with value same as the cookie. When so we need to 
                 * to stop the timer and clear everything.
                 */
                , enableWaitingIndicator: function (isEmail) {
                    //console.log("enable-wait-indicator");
                    //start a timer...
                    console.log('waiting...')
                    if (this.timer === undefined || this.timer === null) {
                        this.$el.trigger("europass:waiting:indicator:show", [true]);
                        //console.log("enable-timer");
                        var that = this;
                        this.counter = 0;
                        this.timer = window.setInterval(function () {
                            var cookieValue =
                                    $.isFunction(Cookies) === true ?
                                    Cookies.get("europass-ewa-temp-document-filename") :
                                    "no-jquery-cookie-js";
                            that.counter++;
                            //console.log( "cookie: " +cookieValue);
                            //console.log( "counter: " +that.counter);
                            //wait for 60*500/1000 secs
                            if ((that.counter === 120) || (cookieValue !== undefined)) {
                                if (that.counter === 120) {
                                    console.log('error');
                                    that.messageContainer.trigger("europass:message:show", ["error", Notification["Download.Document.Failure"]]);
                                }

                                setTimeout(function () {
                                    that.disableWaitingIndicator(that.counter < 120);
                                }, 2500);
                            }
                        }, 500);
                    } else {
                        //show the indicator true is the value for the parameter withOwnTimer
                        this.$el.trigger("europass:waiting:indicator:show", [true]);
                    }
                }
                /**
                 * Disable the wait indication.
                 * 
                 * For the case of file download we need to 
                 * cleanup the timer and the download token.
                 */
                , disableWaitingIndicator: function (success) {
//console.log("disable-wait-indicator");
                    //stop timer 
                    if (this.timer !== undefined) {
                        //console.log("clear timer");
                        window.clearInterval(this.timer);
                        delete this.counter;
                        delete this.timer;
                    }
                    //clear cookie
                    if (this.activeDownloadToken !== undefined && this.activeDownloadToken !== null) {
                        var filename = "Europass CV";
                        filename = Cookies.get("europass-ewa-temp-document-filename");
                        Cookies.remove("europass-ewa-temp-document-filename", {path: this.contextRoot, domain: ".europass.cedefop.europa.eu"});

                        delete this.activeDownloadToken;
                        if (success === true) {
                            console.log('success')
                            this.messageContainer.addClass("success");
                            this.$el.trigger("europass:wizard:export:complete");

                            this.downloadController.triggerMessageWithPathFile("Download.Document.Ready", filename, "success", false);

                        }
                    }
                    //hide the indicator
                    this.$el.trigger("europass:waiting:indicator:hide");
                }
            });
            return DownloadView;
        }
);
