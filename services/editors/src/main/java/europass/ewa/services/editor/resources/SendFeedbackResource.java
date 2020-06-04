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
package europass.ewa.services.editor.resources;

import java.io.UnsupportedEncodingException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.inject.Inject;
import javax.mail.Message;
import javax.mail.Multipart;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.internet.MimeUtility;
import javax.mail.util.ByteArrayDataSource;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import net.sf.uadetector.OperatingSystem;
import net.sf.uadetector.OperatingSystemFamily;
import net.sf.uadetector.ReadableUserAgent;
import net.sf.uadetector.UserAgentFamily;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Strings;
import com.google.inject.name.Named;

import europass.ewa.CachedUserAgentStringParser;
import europass.ewa.Constants;
import europass.ewa.enums.LogFields;
import europass.ewa.enums.ServerModules;
import europass.ewa.logging.ExtraLogInfo;
import europass.ewa.mail.MailSender;
import europass.ewa.services.Paths;
import europass.ewa.services.editor.modules.EditorServicesModule;
import europass.ewa.services.enums.DownloadStatus;
import europass.ewa.services.enums.EmailStatus;
import europass.ewa.services.exception.ApiException;

@Path(Paths.PATH_CONTACT)
public class SendFeedbackResource {

    private static final Logger LOG = LoggerFactory.getLogger(SendFeedbackResource.class);

    private static final String module = ServerModules.SERVICES_EDITORS.getModule(),
            location = "Send Feedback Email";

    public static final String FORM_PARAM_USER_AGENT = "user-agent";

    public static final String PROP_EMAIL_RECIPIENT = "europass-ewa-services.feedback.mail.smtp.recipient";

    public static final String PROP_EMAIL_SPAM = "europass-ewa-services.feedback.mail.smtp.spam.recipient";

    public static final String PROP_EMAIL_SPAM_SUBJECT = "europass-ewa-services.feedback.mail.smtp.spam.subject";

    public static final String PROP_EMAIL_NO_REPLY = "europass-ewa-services.feedback.mail.smtp.noreply";

    public static final String PROP_EMAIL_SUBJECT = "europass-ewa-services.feedback.mail.smtp.subject";
    public static final String PROP_EMAIL_ACCESSIBILITY_SUBJECT = "europass-ewa-services.feedback.mail.smtp.accessibility.subject";

    public static final String PROP_ERROR_BASE = "europass-ewa-services.feedback.mail.smtp.error.url";

    public static final String ISO_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSS zzz";

    public static final String LEGACY_FORMAT = "EEE MMM dd hh:mm:ss zzz yyyy";

    private final CachedUserAgentStringParser userAgentCache;

    private final MailSender mailer;

    private final String recipient;

    private final String defaultSender;

    private final String emailSubject;
    private final String emailAccessibilitySubject;

    private final String spamAddress;

    private final String spamSubject;

    @Inject
    @Named(PROP_ERROR_BASE)
    private static String errorBaseUrl;

    @Inject
    public SendFeedbackResource(@Named(EditorServicesModule.USER_AGENT_CACHE) CachedUserAgentStringParser userAgentCache,
            @Named(PROP_EMAIL_RECIPIENT) String recipient,
            @Named(PROP_EMAIL_SPAM) String spamAddress,
            @Named(PROP_EMAIL_SPAM_SUBJECT) String spamSubject,
            @Named(PROP_EMAIL_NO_REPLY) String defaultSender,
            @Named(PROP_EMAIL_SUBJECT) String emailSubject,
            @Named(PROP_EMAIL_ACCESSIBILITY_SUBJECT) String emailAccessibilitySubject,
            MailSender mailer) {

        this.userAgentCache = userAgentCache;

        this.recipient = Strings.isNullOrEmpty(recipient) ? "europass-feedback@cedefop.europa.eu" : recipient;
        this.emailSubject = Strings.isNullOrEmpty(emailSubject) ? "EWA-EUROPASS-FEEDBACK" : emailSubject;
        this.emailAccessibilitySubject = Strings.isNullOrEmpty(emailAccessibilitySubject) ? "ACCESSIBILITY-EUROPASS-FEEDBACK" : emailAccessibilitySubject;
        this.spamAddress = Strings.isNullOrEmpty(spamAddress) ? "europass-feedback-spam@cedefop.europa.eu" : spamAddress;
        this.spamSubject = Strings.isNullOrEmpty(spamSubject) ? "EWA-EUROPASS-FEEDBACK-SPAM" : spamSubject;

        this.defaultSender = defaultSender;

        this.mailer = mailer;
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_HTML)
    public Response jsonFeedBack(final ContactInfo contactInfo,
            @HeaderParam(FORM_PARAM_USER_AGENT) String userAgent,
            @Context HttpServletRequest request) {

        final ReadableUserAgent agent = userAgentCache.parse(userAgent);

        //Enrich the EnvironmentInfo Object with data from the Request User-Agent Header 
        if (contactInfo.isInfoAvailable() && agent != null) {

            final Date cuptured = new Date();

            final UserAgentFamily agentFamily = agent.getFamily();
            final String browser = agentFamily != null ? agentFamily.getName() : "N/A";
            final String browser_version = agent.getVersionNumber().toVersionString();

            final OperatingSystem os = agent.getOperatingSystem();
            final OperatingSystemFamily agentOSFamily = os != null ? os.getFamily() : null;
            final String os_name = os != null ? os.getName() : null;
            final String os_type = agentOSFamily != null ? agentOSFamily.getName() : "N/A";

            final EnvironmentInfo info = contactInfo.getEnvironmentInfo();

            info.setUserAgentString(userAgent);
            info.setBrowser(browser);
            info.setBrowserVersion(browser_version);
            info.setOsType(os_type);
            info.setOsName(os_name);
            info.setCapturedTime(cuptured);
        }

        return HtmlResponseReporting.report(this.sendByEmail(contactInfo));
    }

    /**
     * Sent by email the Feedback Message and the JSON system info as attachment
     *
     * @param contactInfo
     * @return
     */
    private EmailStatus sendByEmail(final ContactInfo contactInfo) {

        final ExtraLogInfo logInfo = new ExtraLogInfo().add(LogFields.LOCATION, location).add(LogFields.MODULE, module);

        if (contactInfo == null) {
            final String msg = "Could not send document via email, because the provided input is null";

            throw ApiException.addInfo(new ApiException(msg, DownloadStatus.INPUT_EMPTY.getDescription(), Status.BAD_REQUEST), logInfo);
        }

        final String ua = contactInfo.getEnvironmentInfo() != null ? contactInfo.getEnvironmentInfo().getUserAgentString() : StringUtils.EMPTY;
        logInfo.add(LogFields.UA, ua);

        // Validate recipient's and spam address
        if (!mailer.isValidAddress(recipient) || !mailer.isValidAddress(spamAddress)) {
            final String msg = "The provided email is not a valid email address";

            throw ApiException.addInfo(new ApiException(msg, EmailStatus.INVALID_EMAIL.getDescription(), Status.BAD_REQUEST),
                    logInfo);
        }

        final MimeMessage message = mailer.newMessage();

        final DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd:hh:mm");
        final Calendar cal = Calendar.getInstance();

        final String timestm = dateFormat.format(cal.getTime());

        try {

            //Check if the message is a spamAddress
            final boolean isSpam = contactInfo.getSpam();
            message.addRecipient(Message.RecipientType.TO, new InternetAddress(recipient));
            if (isSpam) {
                message.addRecipient(Message.RecipientType.TO, new InternetAddress(spamAddress));
            }

            // 1. From
            final String sender = Strings.isNullOrEmpty(contactInfo.getSender()) ? this.defaultSender : contactInfo.getSender();

            // Validate sender's address
            if (!mailer.isValidAddress(sender)) {
                final String msg = "The provided email is not a valid email address";

                throw ApiException.addInfo(new ApiException(msg, EmailStatus.INVALID_EMAIL.getDescription(), Status.BAD_REQUEST),
                        logInfo);
            }
            message.setFrom(new InternetAddress(sender));

            // 1. Subject
            String subject = emailSubject;
            if (isSpam) {
                subject = spamSubject;
            } else if (contactInfo.isAccessible()) {
                subject = emailAccessibilitySubject;
            }
            message.setSubject(subject, "UTF-8");

            // 2. Content
            final MimeBodyPart textPart = new MimeBodyPart();
            textPart.setText(contactInfo.toHtml(), "UTF-8", "html");

            // 3. Attachment
            MimeBodyPart attachFilePart = null;
            if (contactInfo.isInfoAvailable()) {
                try {
                    attachFilePart = new MimeBodyPart();
                    final DataSource ds = new ByteArrayDataSource(contactInfo.getEnvironmentInfo().getAsJSON().getBytes(), "application/json");
                    attachFilePart.setDataHandler(new DataHandler(ds));
                    attachFilePart.setHeader("Content-Type", ds.getContentType());
                    attachFilePart.setHeader("Content-Transfer-Encoding", "BASE64");
                    String filename = "europass_feedback_systeminfo_" + timestm + ".json";
                    // encode the attachment file name
                    filename = MimeUtility.encodeText(filename, Constants.UTF8_ENCODING, "Q");
                    attachFilePart.setFileName(filename);
                } catch (UnsupportedEncodingException ex) {
                    LOG.error("UnsupportedEncodingException MimeUtility, Q");
                } catch (Exception generalEx) {
                    LOG.error("Exception During Attachment Handling, Q");
                }
            }

            final Multipart mp = new MimeMultipart();
            mp.addBodyPart(textPart);
            if (attachFilePart != null) {
                mp.addBodyPart(attachFilePart);
            }
            message.setContent(mp);

            if (!mailer.sendMail(message)) {
                throw ApiException.addInfo(new ApiException("Failed to send the email", EmailStatus.NOT_SENT.getDescription(), Status.BAD_REQUEST), logInfo);
            }
        } catch (Exception e) {
            throw ApiException.addInfo(new ApiException(e, EmailStatus.NOT_SENT.getDescription(), Status.INTERNAL_SERVER_ERROR), logInfo);
        }

        return EmailStatus.SENT;
    }

    @JsonRootName(value = "data")
    static class ContactInfo {

        @JsonProperty("Message")
        private String message;
        @JsonProperty("Email")
        private String sender;
        @JsonProperty("Spam")
        private boolean spam;
        @JsonProperty("IncludeInfo")
        private boolean infoAvailable;
        @JsonProperty("EnvironmentInfo")
        private EnvironmentInfo info;
        @JsonProperty("IsAccessible")
        private boolean isAccessible;

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public String getSender() {
            return this.sender;
        }

        public void setSender(String sender) {
            this.sender = sender;
        }

        public boolean getSpam() {
            return this.spam;
        }

        public void setSpam(boolean spam) {
            this.spam = spam;
        }

        public boolean isInfoAvailable() {
            return infoAvailable;
        }

        public void setInfoAvailable(boolean infoAvailable) {
            this.infoAvailable = infoAvailable;
        }

        public EnvironmentInfo getEnvironmentInfo() {
            return info;
        }

        public void setEnvironmentInfo(EnvironmentInfo info) {
            this.info = info;
        }

        public boolean isAccessible() {
            return isAccessible;
        }

        public void setAccessible(boolean accessible) {
            isAccessible = accessible;
        }

        @JsonIgnore
        public String toHtml() {

            EnvironmentInfo info = this.getEnvironmentInfo();
            String result = this.getMessage() != null ? this.parseForErrorCode(this.getMessage()) + "<br/><br/><br/>" : " ";

            if (this.isInfoAvailable()) {
                result = result.concat("<table>");
                result = result.concat("<caption style=\"text-align:left\">--------------------------- System Details ------------------------</caption>");
                result = result.concat("<tbody>");
                result = result.concat("<tr><td>Capture Time:</td><td>" + this.prettyPrintCurrentDate(info.getCapturedTime(), LEGACY_FORMAT) + "</td></tr>");
                result = result.concat("<tr><td>Javascript:</td><td>" + info.getJavascript() + "</td></tr>");
                result = result.concat("<tr><td>User Agent String:</td><td>" + info.getUserAgentString() + "</td></tr>");
                result = result.concat("<tr><td>OS Type:</td><td>" + info.getOsType() + "</td></tr>");
                result = result.concat("<tr><td>Screen Depth:</td><td>" + info.getScreenDepth() + "</td></tr>");
                result = result.concat("<tr><td>Browser Screen:</td><td>" + info.getBrowserScreen() + "</td></tr>");
                result = result.concat("<tr><td>OS Name:</td><td>" + info.getOsName() + "</td></tr>");
                result = result.concat("<tr><td>Color Depth:</td><td>" + info.getColorDepth() + "</td></tr>");
                result = result.concat("<tr><td>Browser:</td><td>" + info.getBrowser() + "</td></tr>");
                result = result.concat("<tr><td>Browser Version:</td><td>" + info.getBrowserVersion() + "</td></tr>");
                result = result.concat("<tr><td>Java Enabled:</td><td>" + info.isJavaEnabled() + "</td></tr>");
                result = result.concat("<tr><td>Language:</td><td>" + info.getLanguage() + "</td></tr>");
                result = result.concat("<tr><td>Cookies:</td><td>" + info.isCookies() + "</td></tr>");
                result = result.concat("</tbody>");
                result = result.concat("</table>");
            }

            return result;

        }

        @JsonIgnore
        private String prettyPrintCurrentDate(Date date, String dateFormat) {
            final SimpleDateFormat sdf = new SimpleDateFormat(dateFormat, Locale.ENGLISH);
            final TimeZone utc = TimeZone.getTimeZone("UTC");
            sdf.setTimeZone(utc);
            return sdf.format(date);
        }

        @JsonIgnore
        private String parseForErrorCode(String msg) {

            if (msg.indexOf("ErrCode:") > 0) {

                String errCode = msg.substring(msg.indexOf("ErrCode:") + "ErrCode:".length());

                String errorUrl = SendFeedbackResource.errorBaseUrl + errCode;

                msg = msg.replaceAll("ErrCode:" + msg.substring(msg.indexOf("ErrCode:") + "ErrCode:".length()), "<a href=\"" + errorUrl + "\">ErrCode: " + errCode + "</a>");

            }

            msg = msg.replaceAll("(\r\n|\n)", "<br/>");

            return msg;
        }

    }

    static class EnvironmentInfo {

        @JsonProperty("Javascript")
        private String javascript;
        @JsonProperty("Cookies")
        private boolean cookies;
        @JsonProperty("Browser_Screen")
        private String browserScreen;
        @JsonProperty("Language")
        private String language;
        @JsonProperty("Screen_Depth")
        private String screenDepth;
        @JsonProperty("Color_Depth")
        private String colorDepth;
        @JsonProperty("Java_Enabled")
        private boolean javaEnabled;

        @JsonProperty("User Agent String")
        private String userAgentString;
        @JsonProperty("Browser")
        private String browser;
        @JsonProperty("Browser Version")
        private String browserVersion;
        @JsonProperty("OS Type")
        private String osType;
        @JsonProperty("OS Name")
        private String osName;
        @JsonProperty("Capture Time")
        private Date capturedTime;

        public String getUserAgentString() {
            return userAgentString;
        }

        public void setUserAgentString(String userAgentString) {
            this.userAgentString = userAgentString;
        }

        public String getBrowser() {
            return browser;
        }

        public void setBrowser(String browser) {
            this.browser = browser;
        }

        public String getBrowserVersion() {
            return browserVersion;
        }

        public void setBrowserVersion(String browserVersion) {
            this.browserVersion = browserVersion;
        }

        public String getOsType() {
            return osType;
        }

        public void setOsType(String osType) {
            this.osType = osType;
        }

        public String getOsName() {
            return osName;
        }

        public void setOsName(String osName) {
            this.osName = osName;
        }

        public Date getCapturedTime() {
            return capturedTime;
        }

        public void setCapturedTime(Date capturedTime) {
            this.capturedTime = capturedTime;
        }

        public String getJavascript() {
            return javascript;
        }

        public void setJavascript(String javascript) {
            this.javascript = javascript;
        }

        public boolean isCookies() {
            return cookies;
        }

        public void setCookies(boolean cookies) {
            this.cookies = cookies;
        }

        public String getBrowserScreen() {
            return browserScreen;
        }

        public void setBrowserScreen(String browserScreen) {
            this.browserScreen = browserScreen;
        }

        public String getLanguage() {
            return language;
        }

        public void setLanguage(String language) {
            this.language = language;
        }

        public String getScreenDepth() {
            return screenDepth;
        }

        public void setScreenDepth(String screenDepth) {
            this.screenDepth = screenDepth;
        }

        public String getColorDepth() {
            return colorDepth;
        }

        public void setColorDepth(String colorDepth) {
            this.colorDepth = colorDepth;
        }

        public boolean isJavaEnabled() {
            return javaEnabled;
        }

        public void setJavaEnabled(boolean javaEnabled) {
            this.javaEnabled = javaEnabled;
        }

        @JsonIgnore
        public String getAsJSON() throws JsonProcessingException {
            ObjectMapper mapper = new ObjectMapper();
            return mapper.writeValueAsString(this);
        }
    }
}
