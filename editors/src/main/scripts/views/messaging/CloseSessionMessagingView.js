define(
        [
            'jquery',
//	EWA-1811
//	'underscore',
            'backbone',
            'models/NavigationRoutesInstance',
            'i18n!localization/nls/EditorHelp'
        ],
        function ($,
//		EWA-1811
//		_,
                Backbone,
                NavigationRoutes,
                EditorHelp) {

            var CloseSessionMessagingView = Backbone.View.extend({

                currentRoute: "n/a",

                ignoreRoutes: ["social.linkedin"],

                initialize: function () {

                    this.$el = $("body");

                    // If the browser supports 'onbeforeunload'
                    if (isEventSupported('onbeforeunload')) {
                        //binds event caught when browser window closed or refreshed. Confirm Message Box send to user.
                        $(window).bind("beforeunload", $.proxy(this.validateExit, this));
                    }

                    function isEventSupported(eventName) {
                        var el = this.window;
                        var isSupported = (eventName in el);
                        /*DOESNT WORK FOR OPERA
                         if (!isSupported) {
                         el.setAttribute(eventName, 'return;');
                         isSupported = typeof tempEl[eventName] == 'function';
                         }*/
                        return isSupported;
                    }
                }
                , onClose: function () {
                    $(window).unbind("beforeunload", $.proxy(this.validateExit));
                }

                /**
                 * When a string is assigned to the returnValue property of window.event, 
                 * a dialog box appears that gives users the option to stay on the current document 
                 * and retain the string that was assigned to it. 
                 * The default statement that appears in the dialog box, "Are you sure you want to navigate away from this page? ... Press OK to continue, or Cancel to stay on the current page.", cannot be removed or altered.
                 * http://msdn.microsoft.com/en-us/library/ms536907
                 * 
                 * Included stopPropagation/cancelBubbling so the event will not travel on the ancestors (this will kill the bubbling process).
                 * stopPropagation is used for Firefox, Chrome, and Safari
                 * cancelBubble is used for Safari and Internet Explorer
                 * 
                 * The custom message is not shown in firefox : see  https://bugzilla.mozilla.org/show_bug.cgi?id=588292
                 * In Firefox we can display a confirm dialog, but 2 dialogs for leaving a page might be too annoying
                 * 
                 */
                , findMatchingActive: function (currentActiveElement, cls) {
                    return (
                            (currentActiveElement !== undefined && currentActiveElement !== null)
                            &&
                            (
                                    currentActiveElement.hasClass(cls)
                                    || currentActiveElement.parent().hasClass(cls)
                                    || (
                                            (currentActiveElement.context !== undefined && currentActiveElement.context !== null)
                                            && (
                                                    $(currentActiveElement.context.activeElement).hasClass(cls)
                                                    || $(currentActiveElement.context.activeElement).parent().hasClass(cls)
                                                    )
                                            )
                                    )
                            );
                }
                , validateExit: function (event) {
                    //http://eureka.ykyuen.info/2011/02/22/jquery-javascript-capture-the-browser-or-tab-closed-event/#update20120219
                    //console.log("focus: " + $(event.originalEvent.target).find("*:focus").attr("class") );
                    var currentActiveElement = $(event.originalEvent.target).find(":focus");

                    var cls1 = "ui-languages";

                    var cls2 = "ui-dialog";

                    var cls3 = "manage";

                    var isUILangDropDown = this.findMatchingActive(currentActiveElement, cls1);
                    var isModalForm = this.findMatchingActive(currentActiveElement, cls2);

                    var isUILangDropDownWithActive = this.findMatchingActive($(document.activeElement), cls1);
                    var isModalFormWithActive = this.findMatchingActive($(document.activeElement), cls3);

                    var isUILangDropDownIE = this.findMatchingActive($(event.target.activeElement), cls1);
                    var isModalFormIE = this.findMatchingActive($(event.target.activeElement), cls3);

                    var isCurrentRouteIgnored = ($.inArray(NavigationRoutes.findActiveRoute(), this.ignoredRoutes) === -1);

                    if (isCurrentRouteIgnored || isUILangDropDown || isModalForm
                            || isUILangDropDownWithActive || isModalFormWithActive
                            || isUILangDropDownIE || isModalFormIE) {
                        //exit
                        return;
                    } else {
                        var exitMessage = EditorHelp ["Confirmation.window.unload"];
                        if (exitMessage === undefined || exitMessage === "" || exitMessage === null) {
                            exitMessage = "This page is asking you to confirm that you want to leave - data you have entered may not be saved.";
                        }

                        //set dont_confirm_leave to 1 when you want the user to be able to leave without confirmation
                        var dont_confirm_leave = 0;
                        if (dont_confirm_leave !== 1) {
                            if (!event)
                                event = window.event;

                            if (event.cancelBubble !== null) {
                                event.cancelBubble = true;
                            }
                            event.returnValue = exitMessage;
                            if (event.stopPropagation) {
                                event.stopPropagation();//Prevents the event from bubbling up the DOM tree, preventing any parent handlers from being notified of the event. 
                                event.preventDefault();//the default action of the event will not be triggered.
                            }
                            return exitMessage;
                        }
                    }
                }

            });

            return CloseSessionMessagingView;
        }
);