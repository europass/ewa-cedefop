/* 
 * Copyright (c) 2002-2020 Cedefop.
 * 
 * This file is part of EWA (Cedefop).
 * 
 * EWA (Cedefop) is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * EWA (Cedefop) is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with EWA (Cedefop). If not, see <http ://www.gnu.org/licenses/>.
 */
package europass.ewa.services.editor.messages;

import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.servlet.http.HttpServletRequest;

import joptsimple.internal.Strings;

import europass.ewa.resources.JsonResourceBundle;
import europass.ewa.services.editor.modules.EditorServicesModule;
import org.slf4j.Logger;

public class SessionAwareWithBodyHtmlWrapper implements HtmlWrapper {

    private final Provider<HttpServletRequest> httpRequest;

//	private String editorsURL;
    private Locale locale;

//	private static final String EUROPASS_LOGO_IMAGE_BASE64 = "data:image/png;base64,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";
//	private static final String WARNING_IMAGE_BASE64 = "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAABgAAAAYCAYAAADgdz34AAAAGXRFWHRTb2Z0d2FyZQBBZG9iZSBJbWFnZVJlYWR5ccllPAAAAyRpVFh0WE1MOmNvbS5hZG9iZS54bXAAAAAAADw/eHBhY2tldCBiZWdpbj0i77u/IiBpZD0iVzVNME1wQ2VoaUh6cmVTek5UY3prYzlkIj8+IDx4OnhtcG1ldGEgeG1sbnM6eD0iYWRvYmU6bnM6bWV0YS8iIHg6eG1wdGs9IkFkb2JlIFhNUCBDb3JlIDUuMy1jMDExIDY2LjE0NTY2MSwgMjAxMi8wMi8wNi0xNDo1NjoyNyAgICAgICAgIj4gPHJkZjpSREYgeG1sbnM6cmRmPSJodHRwOi8vd3d3LnczLm9yZy8xOTk5LzAyLzIyLXJkZi1zeW50YXgtbnMjIj4gPHJkZjpEZXNjcmlwdGlvbiByZGY6YWJvdXQ9IiIgeG1sbnM6eG1wPSJodHRwOi8vbnMuYWRvYmUuY29tL3hhcC8xLjAvIiB4bWxuczp4bXBNTT0iaHR0cDovL25zLmFkb2JlLmNvbS94YXAvMS4wL21tLyIgeG1sbnM6c3RSZWY9Imh0dHA6Ly9ucy5hZG9iZS5jb20veGFwLzEuMC9zVHlwZS9SZXNvdXJjZVJlZiMiIHhtcDpDcmVhdG9yVG9vbD0iQWRvYmUgUGhvdG9zaG9wIENTNiAoTWFjaW50b3NoKSIgeG1wTU06SW5zdGFuY2VJRD0ieG1wLmlpZDoxNkM4RDg0NkU2NUExMUUzQkIwQTlBNDA3MTIzMDdENSIgeG1wTU06RG9jdW1lbnRJRD0ieG1wLmRpZDoxNkM4RDg0N0U2NUExMUUzQkIwQTlBNDA3MTIzMDdENSI+IDx4bXBNTTpEZXJpdmVkRnJvbSBzdFJlZjppbnN0YW5jZUlEPSJ4bXAuaWlkOjE2QzhEODQ0RTY1QTExRTNCQjBBOUE0MDcxMjMwN0Q1IiBzdFJlZjpkb2N1bWVudElEPSJ4bXAuZGlkOjE2QzhEODQ1RTY1QTExRTNCQjBBOUE0MDcxMjMwN0Q1Ii8+IDwvcmRmOkRlc2NyaXB0aW9uPiA8L3JkZjpSREY+IDwveDp4bXBtZXRhPiA8P3hwYWNrZXQgZW5kPSJyIj8+w0EvfAAAAc1JREFUeNqclrEvQ1EUxl+fioVFFCFpZ30tFRJzhxrZaMUq0kkMwn+AgcaOCSmLhMSojIStVqoxYdOQ0LS+I+fKU+++ezjJL73vvXO+k3vvuec2UM1mLR8LgymQBFEQAh/gAdyDM7AP7nQCAU0CEl4BE6DJ8rcaJ1kC5caPtkfAGCiCjEBcaZDvDf/6JpgDh6DNQ6gOrsGlJlEr2AXzugSTIKeZFYmPg2EwAtL87teSgzXW+pGA1nyTHbzsChy7nmnNL3T7ylphd4JlnqLO3jzevfv4k9aqShDhKftZzONdnyGGKjBis7htcG4HPa7nbj4TpupK23yIJOa4xnFhTNLWTN/L4v9IEKMEHULnhGvcL4wJ2ZbcEpqxcSOehb5UNS1MVBjzFOQe0itwDvJ+0QluFiYoUtApGBUG5IQNUFnB5mNfEwYM/WGDv9o4JSiBA0HAOR+wLnAi8CfNkqqiRVAxBGyDF/AKtgy+Fdb8bhF0E81oWrCylKvbpnz86qxVVpWhLM/TX9f0pmkwCKpgwEd8gbWsxgRkG3yB72huNcewLLNgz3QnH3G954XVVWNfp1HcawaWa08yvFHqbwsJdPL3Rz6gBa6WW132TwEGAFYHVtuCt8WBAAAAAElFTkSuQmCC";
    private static final String DEFAULT_MESSAGE = "There was a problem processing your request, please try again later or contact Europass Feedback.";
    private static final Logger LOG = org.slf4j.LoggerFactory.getLogger(SessionAwareWithBodyHtmlWrapper.class);

    @Inject
    public SessionAwareWithBodyHtmlWrapper(Provider<HttpServletRequest> httpRequest) {
        this.httpRequest = httpRequest;
    }

    /**
     * Session - aware utility to wrap a json string inside a script tag within
     * an html document, while adding the session id to a suitable meta tag. The
     * body of the HTML contains a properly translated and formatted HTML
     * element which is to be displayed when the response is renderered in a new
     * tab/window.
     *
     * @param json, the json as ErrorMessage
     *
     * @return an HTML String
     */
    @Override
    public String htmlWrap(String json) {
        return htmlWrap(json, META_SUCCESS_STATUS);
    }

    @Override
    public String htmlWrap(europass.ewa.services.exception.ErrorMessage error, String status) {
        HttpServletRequest request = httpRequest.get();
        this.locale = (Locale) request.getAttribute(EditorServicesModule.USER_REQUEST_LOCALE);
//		String errorAsHtml = message( error, this.locale );
        String[] errorMessageParts = messageParts(error);

        return html(error.asJsonString(), errorMessageParts, status);
    }

    @Override
    public String htmlWrap(String jsonString, String status) {
        LOG.debug("inside htmlWrap");
        return html(jsonString, null, status);
    }

    private String html(String jsonString, Object errorInfo, String status) {
        HttpServletRequest request = httpRequest.get();

        this.locale = (Locale) request.getAttribute(EditorServicesModule.USER_REQUEST_LOCALE);

//		boolean messageInBody = false;
        String messageBody = "";

        StringBuilder html = new StringBuilder();

        html.append("<!doctype html>");
        html.append("<html lang='en'>");

        html.append("<head>");
        html.append("<meta http-equiv=\"content-type\" content=\"text/html; charset=utf-8\"/>");
        //html.append( "<meta name=\"jsessionid\" content=\""+sessionId+"\">" );
        html.append("<meta name=\"status\" content=\"" + status + "\"/>");

        String errorMessageHTML = "<button class=\"notification message close error\" type=\"button\"></button>"
                + "<div class=\"message-area\">"
                + "<p>" + DEFAULT_MESSAGE + "</p></div>";

        if (errorInfo != null && errorInfo instanceof String[]) {

            String[] parts = (String[]) errorInfo;

            errorMessageHTML = "<button class=\"notification message close error\" type=\"button\"></button>"
                    + "	<div class=\"message-area\">"
                    + "	<p>" + parts[1] + "</p><em class=\"trace-code\">" + parts[0] + "</em></div>";
        }

        html.append("<script>\n"
                //				+ "var errorWrapperSection = document.createElement('section');\n"
                //				+ "errorWrapperSection.setAttribute('class','feedback-area');\n"
                //				+ "errorWrapperSection.setAttribute('id','preview-error-section');\n"

                //				+ "if( opener.document.getElementById('preview-document-error') === undefined ){ \n"
                + "if ( window.opener !== null && window.opener !== undefined && window.opener.document !== null && window.opener.document !== undefined ){\n"
                + "var parentNotificationsAreaSection = window.opener.document.getElementById('app-notifications');\n"
                + "var errorSection = document.createElement('section');\n"
                + "errorSection.setAttribute('id','preview-document-error');\n"
                + "errorSection.setAttribute('class','notification error');\n"
                + "errorSection.innerHTML = '" + errorMessageHTML + "';\n"
                + "var errorMessageAlreadyExists = false;\n"
                //				+ "errorWrapperSection.appendChild(errorSection);\n"

                + "/* **Duplicate Error Message Check: Iterates over existing notifications in order to replace identical errors with\n"
                + "the latest error code */\n"
                + "\n"
                + "//get all p tags inside #application-  notifications \n"
                + "var notification_area_messages = parentNotificationsAreaSection.querySelectorAll('section.error');\n"
                + "var current_errorDescription = errorSection.getElementsByTagName('p').item(0).innerText;\n"
                + "var len = notification_area_messages.length;\n"
                + "for ( var i = 0;  i < len; i++) {\n"
                + " var hasParagraph  = notification_area_messages[i].getElementsByTagName('p'); \n"
                + " var currentMessage =  ( hasParagraph.length>0 ?  hasParagraph.item(0).innerText : notification_area_messages[i].innerText );\n"
                + "	// if you find a similar error description, replace the HTML of the element\n"
                + "	if ( currentMessage === current_errorDescription ) {\n"
                + "		//set code\n"
                + "		notification_area_messages[i].innerHTML = errorSection.innerHTML;\n"
                + "		errorMessageAlreadyExists = true;\n"
                + "		break;\n"
                + "	}\n"
                + "}\n"
                + "if (!errorMessageAlreadyExists) {\n"
                + "		var divErrMessageHTML = document.createElement('div');\n"
                + "		divErrMessageHTML.appendChild(errorSection);\n"
                + "		parentNotificationsAreaSection.innerHTML = parentNotificationsAreaSection.innerHTML + divErrMessageHTML.innerHTML;\n"
                + "}\n"
                + "}\n"
                + "window.close();"
                + "</script>");

        /*		else if(errorInfo == null || errorInfo instanceof String){
			String errorAsHtml = (String)errorInfo;
			messageInBody = !Strings.isNullOrEmpty( errorAsHtml );
			
			if (messageInBody){
				html.append("<style>"
						+ ".image-area {"
						+ "border: none"
						+ "margin: auto;"
						+ "position: relative;"
						
						+ "margin-top: 1.5rem;"
					+ "}"
					+ ".message-area {"
						+ "background: url('"+WARNING_IMAGE_BASE64+"') no-repeat scroll #737373;"
						+ "border: 0 none;"
						+ "color: #e40707;"
						+ "font-family: Open Sans,sans-serif;"
						+ "font-size: 16px;"
						+ "font-weight: 0,9rem;"
						+ "line-height: normal;"
						+ "display: block;"
						+ "vertical-align: baseline;"
						+ "text-align: left;"
						+ "padding: 1% 3% 1% 3%;"
						+ "background-position: 2% center;"
						+ "background-color: #f4f4f4;"
					    + "background-repeat: no-repeat;"
					    + "border-radius: 4px;"
						+ "margin: auto;"
						+ "position: relative;"
						+ "right: 25px;"
						+ "top: 10px;"
						+ "width: 30%;"+
						
					"}"+

						"</style>");
			
				messageBody += "<div class=\"image-area\">" + "<img width='220' height='62' src='" + EUROPASS_LOGO_IMAGE_BASE64 + "'>" + "</div>"
						+ "<div class=\"message-area\">"
						+ errorAsHtml
						+ "</div>"
						+ "";
			}
		
		}
         */
        html.append("</head>");

        // Error as JSON string
        html.append("<script type=\"application/json\">");
        html.append(jsonString);
        html.append("</script>");

        html.append("<body>" + IE_FRIENDLY_MESSAGE_BUSTER);

        html.append(messageBody);

        html.append("</body>");

        html.append("</html>");

        return html.toString();
    }

    /*	private String message( europass.ewa.services.exception.ErrorMessage error, Locale locale ){
		if ( locale == null )
			locale = Locale.ENGLISH;
		
		String errorMessage = DEFAULT_MESSAGE;
		
		ResourceBundle bundle = ResourceBundle.getBundle("bundles/Notification", locale, new JsonResourceBundle.Control() );
		if (bundle != null && !Strings.isNullOrEmpty( error.getCode() ) ){
			try {
				String bundleTxt = bundle.getString(error.getCode());
				if ( !Strings.isNullOrEmpty( bundleTxt ))
					errorMessage = bundleTxt;
			} catch ( final MissingResourceException e ){}
		}
		
		StringBuilder message = 
			new StringBuilder("<div>");
				message.append("<p>"+ errorMessage +"</p>");
//				message.append("<p>"+error.getMessage()+"</p>");
				message.append("<p>"+error.getTrace()+"</p>");
		message.append( "</div>");
		
		return message.toString();
	}*/
    private String[] messageParts(europass.ewa.services.exception.ErrorMessage error) {
        if (this.locale == null) {
            this.locale = Locale.ENGLISH;
        }

        String errorMessage = DEFAULT_MESSAGE;

        ResourceBundle bundle = ResourceBundle.getBundle("bundles/Notification", locale, new JsonResourceBundle.Control());
        if (bundle != null && !Strings.isNullOrEmpty(error.getCode())) {
            try {
                String bundleTxt = bundle.getString(error.getCode());
                if (!Strings.isNullOrEmpty(bundleTxt)) {
                    errorMessage = bundleTxt;
                }
            } catch (final MissingResourceException e) {
            }
        }

        String[] parts = {error.getTrace(), errorMessage};

        return parts;
    }

}
