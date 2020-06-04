/**
 * Accepts:
 * context, name="Contact" type="website" i.e. (#format_link . name="Contact" type="website")
 * or
 * context, name="Contact" type="email"
 */
define(
	['handlebars', 'Utils', 'underscore'],
	function (Handlebars, Utils, _) {
		function format_link(context, options) {

			if (!context || Handlebars.Utils.isEmpty(context)) {
				return options.inverse(this);
			}
			var buffer = "";
			var result = "";
			var safeStr = "";
			var value = context;

			if (_.isEmpty(value))
				return "";

			if (options.hash.type === "website") {

				var regex = new RegExp("^(?:(?:https?://|ftp://|www.\\w))(?:\\S+(?::\\S*)?@)?(?:|(?:(?:[a-z\\u00a1-\\uffff0-9]-*)*[a-z\\u00a1-\\uffff0-9]+)(?:\\.(?:[a-z\\u00a1-\\uffff0-9]-*)*[a-z\\u00a1-\\uffff0-9]+)*(?:\\.(?:[a-z\\u00a1-\\uffff]{2,})))(?::\\d{2,5})?(?:/\\S*)?$", "i");

				if (!regex.test(value)) {
					safeStr = Handlebars.Utils.escapeExpression(value);
				} else {
					safeStr = Handlebars.Utils.escapeExpression(value);
				}
				//href needs to be properly formed, starting with http://
				var url = safeStr.indexOf("http") === 0 ? safeStr : "http://" + safeStr;

				result = '<a title="' + safeStr + '" href="' + url +
					'" target="_blank">' + safeStr + '</a>';
			} else if (options.hash.type === "email") {
				var regexEmail = /^([a-zA-Z0-9_\.\-\+])+\@(([a-zA-Z0-9\-])+\.)+([a-zA-Z0-9]{2,4})+$/;

				if (!regexEmail.test(value)) {
					result = Handlebars.Utils.escapeExpression(value);
				} else {
					safeStr = Handlebars.Utils.escapeExpression(value);
					result = '<a href="mailto:' + safeStr + '" target="_blank">' + safeStr + '</a>';
				}
			}
			buffer += options.fn(result);
			return new Handlebars.SafeString(buffer);
		}
		Handlebars.registerHelper('format_link', format_link);
		return format_link;
	}
);
