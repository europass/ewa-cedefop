/**
 * Create html with user option to disable analytics tracking
 */

(function () {
	var cookiesModal = document.createElement("div");
	cookiesModal.classList.add("cookies-modal");
	cookiesModal.setAttribute("id", "do-not-track-options");
	cookiesModal.innerHTML = "\n\t<div  style=\"max-width:1000px;margin:0 auto;padding: 0 20px\">\n\t\t<div><h3 style=\"font-size:20px; margin-bottom:10px;\">ABOUT COOKIE POLICY</h3></div>\n\t\t<div>\n\t\t\t<div style=\"line-height:20px;\" id=\"cookie-body\">\n\t\t\t\t<p class=\"text\" style=\"margin:0;\">Europass wesbites use cookies, which help us deliver our services.For more details, see our <a href=\"https://europass.cedefop.europa.eu/legal-notice#cookie-policy\" target=\"_blank\" style=\"color: #4cb0ff;font-weight:bold; \">Cookie policy</a></p>\n\t\t\t\t<p style=\"margin-top:0; margin-bottom: 15px;\">You can enable and disable cookies by selecting the options below.</p>\n\t\t\t</div>\t\t\t\n\t\t\t<ul style=\"list-style-type:none;padding:0;display:flex;margin:0; \">\n\t\t\t\t<li><input id=\"mandatory\" style=\"margin:0 2px 0 0;border: 0; vertical-align:middle;\" type=\"checkbox\" name=\"mandatory\" value=\"mandatory\" checked disabled> <span id=\"mandatory_text\">Mandatory cookies<span><span style=\"margin-left:10px;\">|</span></li>\n\t\t\t\t<li style=\"padding-left:10px;\"><input id=\"analytics\" style=\"margin:0 2px; vertical-align:middle;\" id=\"analytics\" type=\"checkbox\" name=\"analytics\" value=\"analytics\" " + (navigator.doNotTrack === "1" ? "" : "checked") + "> <span id=\"analytics_text\" Analytics</li>\n\t\t\t</ul>\n\t\t</div>\n         <div style=\"margin-top:10px\">\n\t\t\t<button style=\"border-radius: 7px;\n\t\t\t\tbackground-color: #2979b8;\n\t\t\t\tcolor: white;\n\t\t\t\ttransition-property: none;\n\t\t\t\ttext-decoration: none;\n\t\t\t\tdisplay: inline-block;\n\t\t\t\tposition: relative;\n\t\t\t\tmargin: 5px 0 0;\n\t\t\t\tfont-size: 15px;\n\t\t\t\tline-height: 1em;\n\t\t\t\tfont-weight: 700;\n\t\t\t\tpadding: 13px 13px;\n\t\t\t\twidth: 120px;\n\t\t\t\ttext-align: center;\n\t\t\t\tborder: none;\n\t\t\t\tcursor: pointer;\" id=\"cookies-agreed\">OK, I Agree\n\t\t\t</button>\n\t\t</div>\n\t</div>\n\t";
	cookiesModal.style.fontFamily = "arial";
	cookiesModal.style.fontSize = "15px";
	cookiesModal.style.padding = "20px 0";
	cookiesModal.style.margin = "0 auto";
	cookiesModal.style.borderRadius = "3px";
	cookiesModal.style.background = "#464444";
	cookiesModal.style.color = "white";
	cookiesModal.style.position = "fixed";
	cookiesModal.style.bottom = 0;
	cookiesModal.style.left = 0;
	cookiesModal.style.right = 0;
	cookiesModal.style.zIndex = 10000;

	if (getCookie("do-not-track") === "true") {
		var gaTimer = setInterval(function () {
			if (typeof _paq !== "undefined") {
				_paq.push(['optUserOut']);
				clearInterval(gaTimer);
			}
		}, 100);
	} else if (getCookie("do-not-track").length === 0) {

		document.body.addEventListener("load-do-not-track", function (event) {
			fetchTexts(event.detail.locale);

			// select the target node
			var target = document.body;
			// create an observer instance
			var observer = new MutationObserver(function (mutations) {
				lastMutation = 0;
				mutations.forEach(function (mutation) {
					lastMutation = new Date();
				});
			});
			var mutationTime = setInterval(function () {
				if (Math.floor(new Date() - lastMutation) > 1000) {
					_paq.push(['forgetUserOptOut']);
					clearInterval(mutationTime);
					document.body.appendChild(cookiesModal);
				}
			}, 1000);
			// configuration of the observer:
			var config = {childList: true, attributes:true};
			// pass in the target node, as well as the observer options
			observer.observe(target, config);

			var agreedCookies = cookiesModal.querySelector("button");
			agreedCookies.addEventListener("click", function (event) {
				var analytics = cookiesModal.querySelector("#analytics").checked;
				if (!analytics) {
					_paq.push(['optUserOut']);
					setCookie("do-not-track", true, 60);
					cookiesModal.style.display = "none";
				} else {
					_paq.push(['forgetUserOptOut']);
					setCookie("do-not-track", false, 60);
					_paq.push(['trackEvent', 'Analytics', 'enabled']);
					cookiesModal.style.display = "none";
				}
			});
		});
	}

	function getCookie(cname) {
		var name = cname + "=";
		var decodedCookie = decodeURIComponent(document.cookie);
		var ca = decodedCookie.split(";");
		for (var i = 0; i < ca.length; i++) {
			var c = ca[i];
			while (c.charAt(0) === " ") {
				c = c.substring(1);
			}
			if (c.indexOf(name) === 0) {
				return c.substring(name.length, c.length);
			}
		}
		return "";
	}

	function setCookie(cname, cvalue, exdays) {
		var d = new Date();
		d.setTime(d.getTime() + exdays * 24 * 60 * 60 * 1000);
		var expires = "expires=" + d.toUTCString();
		document.cookie = cname + "=" + cvalue + ";" + expires + ";path=/";
	}

	function fetchTexts(locale) {
		require(
			['i18n!localization/nls/Notification/'+locale],
			function (Notification) {
				if (Notification["cookie.consent.notification.title"]) {
					cookiesModal.querySelector("h3").innerHTML = Notification["cookie.consent.notification.title"];
				}
				if (Notification["cookie.consent.notification.message.body"]) {
					cookiesModal.querySelector("#cookie-body").innerHTML = Notification["cookie.consent.notification.message.body"];
				}
				if (Notification["cookie.consent.notification.message.options.mandatory"]) {
					cookiesModal.querySelector("#mandatory_text").innerHTML = Notification["cookie.consent.notification.message.options.mandatory"];
				}
				if (Notification["cookie.consent.notification.message.options.analytics"]) {
					cookiesModal.querySelector("#analytics_text").innerHTML = Notification["cookie.consent.notification.message.options.analytics"];
				}
				if (Notification["cookie.consent.notification.message.agree"]) {
					cookiesModal.querySelector("button").innerHTML = Notification["cookie.consent.notification.message.agree"];
				}
			});
	}
})(window);

(function () {

  if ( typeof window.CustomEvent === "function" ) return false;

  function CustomEvent ( event, params ) {
    params = params || { bubbles: false, cancelable: false, detail: undefined };
    var evt = document.createEvent( 'CustomEvent' );
    evt.initCustomEvent( event, params.bubbles, params.cancelable, params.detail );
    return evt;
   }

  CustomEvent.prototype = window.Event.prototype;

  window.CustomEvent = CustomEvent;
})();
