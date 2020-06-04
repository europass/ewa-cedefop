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
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.social.connect.Connection;
import org.springframework.social.linkedin.api.LinkedIn;
import org.springframework.social.linkedin.api.LinkedInProfileFull;
import org.springframework.social.linkedin.connect.LinkedInConnectionFactory;
import org.springframework.social.oauth2.AccessGrant;
import org.springframework.social.oauth2.GrantType;
import org.springframework.social.oauth2.OAuth2Operations;
import org.springframework.social.oauth2.OAuth2Parameters;
import org.springframework.web.client.RestOperations;

import com.google.inject.name.Named;

import europass.ewa.enums.EuropassDocumentType;
import europass.ewa.enums.LogFields;
import europass.ewa.enums.ServerModules;
import europass.ewa.enums.UserAgent;
import europass.ewa.logging.ExtraLogInfo;
import europass.ewa.model.PrintingPreference;
import europass.ewa.model.SkillsPassport;
import europass.ewa.model.conversion.ModelModule;
import europass.ewa.model.conversion.json.ModelContainerConverter;
import europass.ewa.model.social.MappingListRoot;
import europass.ewa.model.social.SocialMappingsModule;
import europass.ewa.model.wrapper.Feedback;
import europass.ewa.model.wrapper.UploadedModelWrapper;
import europass.ewa.services.Paths;
import europass.ewa.services.annotation.EWAEditor;
import europass.ewa.services.editor.messages.HtmlWrapper;
import static europass.ewa.services.editor.resources.FileResource.checkAndFetchCookie;
import europass.ewa.services.enums.LinkedInProfileFields;
import europass.ewa.services.enums.SocialErrorStatus;
import europass.ewa.services.enums.UploadStatus;
import europass.ewa.services.exception.ApiException;
import europass.ewa.services.exception.SocialServiceAuthenticationException;
import europass.ewa.services.exception.SocialServiceDataRetrievalException;
import europass.ewa.services.files.FileRepository;
import europass.ewa.services.files.ModelFileManager;
import europass.ewa.services.social.MappingParser;
import europass.ewa.services.social.Transformer;
import europass.ewa.services.social.linkedin.LinkedInModule;
import javax.ws.rs.core.NewCookie;
import org.json.JSONObject;
import org.springframework.http.ResponseEntity;

/**
 * The chain of work is the following : Step a: Call the initial API service.
 * This clears the session ( invalidate ) , generates a new one and calls OAuth
 * with state as the new session id Step b. The callback takes the state, and
 * goes to final callback adding jsessionid to keep the new session. Step c.
 * Generation takes place, and on success , json data is placed in the session.
 * Step d. In case of success the return is a redirect that contains the
 * jsession as a path variable. The UI editor will understand this variable and
 * will call /social/import/helper/{provider};jsessionid= , to retrieve the
 * actual json data and put them in place.
 */
@Path(Paths.SOCIAL_IMPORT_BASE)
public class SocialImportResource {

    @Context
    HttpServletRequest context;

    private static final Logger LOG = LoggerFactory.getLogger(SocialImportResource.class);

    private static final String module = ServerModules.SERVICES_EDITORS.getModule();
    private static final String location = "LinkedIn Import";

    public static final String SOCIAL_SESSION_PARAM_JSON = "social_sess_json_stored";
    public static final String SOCIAL_SESSION_REFERRER = "social_sess_ref";

    /**
     * EWA-1651 / pgia :
     *
     * The SOCIAL_LINKEDIN_SCOPE variable is set to utilize the scopes
     * r_basicprofile, r_emailaddress, w_share due to the limitation on the
     * r_fullprofile access applied by LinkedIn on May 12th - May 19th, 2015
     *
     * https://developer.linkedin.com/support/developer-program-transition (More
     * for these on Jira Issue EWA-1540)
     *
     * The previous SOCIAL_LINKEDIN_SCOPE value will be commented in case there
     * are changes by the LinkedIn API, or an appliance for acces from cedefop
     */
    public static final String SOCIAL_LINKEDIN_SCOPE = "r_basicprofile r_emailaddress w_share";
//	public static final String SOCIAL_LINKEDIN_SCOPE = "r_fullprofile r_emailaddress r_contactinfo r_network";

    public static final String SOCIAL_LINKEDIN_REST_REQUEST_URI = "https://api.linkedin.com/v1/people/~";

    private final ModelContainerConverter jsonconverter;
    private final FileRepository fileRepository;
    private final ModelFileManager fileManager;
    private final HtmlWrapper htmlWrapper;

    private final Map<EuropassDocumentType, Map<String, PrintingPreference>> defaultPrefs;

    private final String apiURL;
    private final String editorsURL;

    private final String linkedinClientId;
    private final String linkedinClientSecret;
    private final String linkedinClientCallback;
    private static LinkedInConnectionFactory linkedInConnectionFactory;

    //---
    private final MappingListRoot linkedInMapping;
    private final Set<Transformer> linkedInHandlers;

    @Inject
    public SocialImportResource(
            ModelContainerConverter jsonconverter,
            FileRepository fileRepository,
            @EWAEditor ModelFileManager fileManager,
            @EWAEditor HtmlWrapper htmlWrapper,
            @Named("europass-ewa-services.api.url") String apiURL,
            @Named("editors.webapp.url") String editorsURL,
            @Named("europass-ewa-services.linkedin.client.id") String linkedinClientId,
            @Named("europass-ewa-services.linkedin.client.secret") String linkedinClientSecret,
            @Named("europass-ewa-services.linkedin.client.callback") String linkedinClientCallback,
            @Named(ModelModule.DEFAULT_PREFS) Map<EuropassDocumentType, Map<String, PrintingPreference>> defaultPrefs,
            @Named(SocialMappingsModule.SOCIAL_MAPPING_LINKEDIN) MappingListRoot linkedInMapping,
            @Named(LinkedInModule.HANDLERS_SET) Set<Transformer> linkedInHandlers
    ) {

        this.apiURL = apiURL;
        this.editorsURL = editorsURL;

        this.linkedinClientId = linkedinClientId;
        this.linkedinClientSecret = linkedinClientSecret;
        this.linkedinClientCallback = linkedinClientCallback;

        this.jsonconverter = jsonconverter;
        this.fileRepository = fileRepository;
        this.fileManager = fileManager;
        this.htmlWrapper = htmlWrapper;

        this.defaultPrefs = defaultPrefs;

        this.linkedInMapping = linkedInMapping;
        this.linkedInHandlers = linkedInHandlers;
    }

    @GET
    @Produces("text/plain")
    public String getGreeting() {
        return "Europass: Social Import Services";
    }

    /**
     * Step a: Call the initial API service. This clears the session (
     * invalidate ) , generates a new one and calls OAuth with state as the new
     * session id in order to get the access token
     *
     * @param referer the url from which the request to linked authentication
     * occurred
     * @param cookieId
     * @return
     */
    @GET
    @Path(Paths.SOCIAL_IMPORT_PROVIDER_LINKEDIN)
    public Response linkedInImportCall(@HeaderParam("Referer") String referer,
            @QueryParam("id") String cookieId) {

        String jsessionid = context.getSession().getId();
        LOG.debug("referer in linkedin: " + referer);

        try {
            if (linkedInConnectionFactory == null) {
                linkedInConnectionFactory = new LinkedInConnectionFactory(linkedinClientId, linkedinClientSecret);
            }
            OAuth2Operations oauthOperations = linkedInConnectionFactory.getOAuthOperations();
            OAuth2Parameters params = new OAuth2Parameters();
            JSONObject obj = new JSONObject();
            obj.put("State", jsessionid);
            obj.put("Cookie", cookieId);
            obj.put("Referer", referer);

            params.setState(obj.toString());
            params.setRedirectUri(linkedinClientCallback);
            params.setScope(SOCIAL_LINKEDIN_SCOPE);

            String redirect = oauthOperations.buildAuthorizeUrl(GrantType.AUTHORIZATION_CODE, params);
            return Response.ok(redirect).build();
        } catch (Exception ex) {
            throw ApiException.addInfo(new SocialServiceAuthenticationException(ex),
                    new ExtraLogInfo().add(LogFields.LOCATION, "SocialImport:LinkedIn").add(LogFields.MODULE, module));
        }
    }

    /**
     * Step b. The callback takes the state, and goes to final callback adding
     * jsessionid to keep the new session. Return from linked in ( callback
     * first stage)
     *
     * @param error
     * @param authorizationCode
     * @param state
     * @return
     */
    @GET
    @Path(Paths.SOCIAL_IMPORT_CALLBACK + "/" + Paths.SOCIAL_IMPORT_PROVIDER_LINKEDIN)
    @Produces(MediaType.TEXT_HTML)
    public Response linkedinSocialCallback(@QueryParam("error") String error,
            @QueryParam("code") String authorizationCode,
            @QueryParam("state") String state) {
        // Just a simple redirect to the final callback stage with adding the ;jsessionid to bind the session. 

        try {
            String errStr = "";
            if (error != null) {
                errStr = "&error=" + URLEncoder.encode(error, "UTF-8");
                authorizationCode = "-";
            }

            JSONObject obj = new JSONObject(state);

            String sessionState = obj.getString("State");
            String cookie = obj.getString("Cookie");
            String referer = obj.getString("Referer");
            LOG.debug("referer in /callback/linkedin is " + referer);

            String urlStr = apiURL + Paths.SOCIAL_IMPORT_BASE
                    + Paths.SOCIAL_IMPORT_CALLBACK_FINAL + "/"
                    + Paths.SOCIAL_IMPORT_PROVIDER_LINKEDIN
                    + ";jsessionid=" + sessionState
                    + "?code=" + URLEncoder.encode(authorizationCode, "UTF-8")
                    + errStr
                    + "&state=" + sessionState
                    + "&cookieId=" + cookie
                    + "&referer=" + referer;

            URI retURI = new URI(urlStr);
            return Response.seeOther(retURI).build();

        } catch (Exception ex) {
            LOG.error("Error constructing URL ", ex);
            // Normally Should NEVER happen
            return Response.serverError().build();
        }
    }

    /**
     * Step c. Generation takes place, and on success , json data is placed in
     * the session. Return from linked in final ( callback final stage)
     *
     * Step d. In case of success the return is a redirect that contains the
     * jsession as a path variable. The UI editor will understand this variable
     * and will call /social/import/helper/{provider};jsessionid= , to retrieve
     * the actual json data and put them in place.
     *
     * @param error
     * @param authorizationCode
     * @param state
     * @param cookieId
     * @param referer
     * @return
     */
    @GET
    @Path(Paths.SOCIAL_IMPORT_CALLBACK_FINAL + "/" + Paths.SOCIAL_IMPORT_PROVIDER_LINKEDIN)
    public Response linkedinSocialCallbackFinal(@QueryParam("error") String error,
            @QueryParam("code") String authorizationCode,
            @QueryParam("state") String state,
            @QueryParam("cookieId") String cookieId,
            @QueryParam("referer") String referer) {

        /* ### STEP C ### */
        cookieId = cookieId == null ? "" : cookieId;
        //check if no cookie id has passed and create a cookie
        LOG.debug("/callback/final/linkedin - cookie id: " + cookieId);
        NewCookie editorsUserCookie = checkAndFetchCookie(cookieId);
        cookieId = editorsUserCookie != null ? editorsUserCookie.getValue() : cookieId;

        LOG.debug("referer in /callback/final/linkedin " + referer);

        String returnURL = referer;

        URI retURI = null;

        if (error != null && !"".equals(error)) {
            // Error in logging in or credentials
            return constructErrorUriResponse(returnURL, SocialErrorStatus.valueOf(error), editorsUserCookie);
        }

        SocialErrorStatus errorStatus = null;
        String json = "";
        try {
            //Retrieve LinkedIn Profile
            LinkedInProfileFull linkedInProfile = null;
            LinkedIn linkedin = null;
            try {
                AccessGrant accessGrant = linkedInConnectionFactory.getOAuthOperations().exchangeForAccess(authorizationCode, linkedinClientCallback, null);
                Connection<LinkedIn> connection = linkedInConnectionFactory.createConnection(accessGrant);

                //Load an ESP from LinkedIn through Mapping xml and Reflection
                linkedin = connection.getApi();
//				ProfileOperations profileOp = linkedin.profileOperations();
//				linkedInProfile = profileOp.getUserProfileFull();

                RestOperations restOperations = linkedin.restOperations();

                String uri = SOCIAL_LINKEDIN_REST_REQUEST_URI + LinkedInProfileFields.fieldsRequest();
                LOG.debug("linkedin rest uri: " + uri);

                //EWA 1614, LinkedIn import: add support for secondary languages
                HttpHeaders headers = new HttpHeaders();
                headers.add("Accept-Language", this.getLILocaleString(returnURL));

                HttpEntity<?> requestEntity = new HttpEntity<Object>(headers);

                LOG.debug("before creating responseEntity");
                ResponseEntity<LinkedInProfileFull> responseEntity = restOperations.exchange(URI.create(uri), HttpMethod.GET, requestEntity, LinkedInProfileFull.class);
                LOG.debug("responseEntity status: " + responseEntity.getStatusCode().name());

                //responseEntity.getBody().getExtraData().get("pictureUrls")
                linkedInProfile = responseEntity.getBody();
                LOG.debug("linkedin profile picture url: " + linkedInProfile.getProfilePictureUrl());
                //linkedInProfile = restOperations.getForObject( uri, LinkedInProfileFull.class );
                if (linkedInProfile.getExtraData() != null) {

                    if (linkedInProfile.getExtraData().containsKey("pictureUrls")) {
                        if (linkedInProfile.getExtraData().get("pictureUrls") != null) {
                            LOG.debug("linkedin profile pictureUrls value is: " + linkedInProfile.getExtraData().get("pictureUrls"));
                        } else {
                            LOG.debug("linkedin profile pictureUrls value is null");
                        }
                    } else {
                        LOG.debug("Extra Data map does not contain pictureUrls key");
                    }

                } else {
                    LOG.debug("Extra Data from linkedinProfile is null");
                }

            } catch (final Exception e) {
                LOG.error("real exception", e);
                LOG.debug("real exception message: " + e.getMessage() + "--- class: " + e.getClass());
                throw ApiException.addInfo(new SocialServiceDataRetrievalException("Europass failed to retrieve profile data from LinkedIn"),
                        new ExtraLogInfo().add(LogFields.LOCATION, location).add(LogFields.MODULE, module));
            }
            try {
                //Translate to Europass Profile - might through 
                SkillsPassport esp = null;
                MappingParser<LinkedInProfileFull> parser
                        = new MappingParser<LinkedInProfileFull>(linkedInMapping, linkedInHandlers);
                LOG.debug("cookie id: " + cookieId);

                esp = parser.parse(linkedInProfile, retrieveLocale(returnURL), cookieId);
                LOG.debug("parsed linkedInProfile into esp object");
                if (esp.getLearnerInfo().getIdentification().getPhoto() == null) {
                    LOG.debug("esp object has no photo");
                } else {
                    LOG.debug("esp object has a photo object attached");
                }

                // Apply default printing preferences and Activate Preferences for All documents
                //ORDER IS IMPORTANT HERE, AS CHECKEMPTY TO APPLY PRINTING PREFERNCE CHECKS THAT PHOTO/ATTACHMENT DATA IS NOT NULL
                esp.activatePreferences(this.defaultPrefs);

                UploadedModelWrapper espWrapper = new UploadedModelWrapper(esp);
                LOG.debug("before augment POJO with TempURIs");
                // Read bytes from XML and write files to disk, augment POJO with TempURIs and delete data				
                List<Feedback> feedback = fileManager.augmentWithURI(esp, cookieId);
                LOG.debug("after augment POJO with TempURIs");
                espWrapper.setInfo(feedback);

                //Store Result
                json = jsonconverter.write(espWrapper);
                context.getSession().setAttribute(SOCIAL_SESSION_PARAM_JSON, json);

            } catch (final Exception e) {
                LOG.error("real exception", e);
                throw ApiException.addInfo(new ApiException("Failed to prepare a Europass profile", e, "social.service.import.esp.prepare.error", Status.INTERNAL_SERVER_ERROR),
                        new ExtraLogInfo().add(LogFields.LOCATION, location).add(LogFields.MODULE, module));
            }
        } catch (final Exception e) {
            if (errorStatus == null) {
                errorStatus = SocialErrorStatus.match(e);
            }
            return constructErrorUriResponse(returnURL, errorStatus, editorsUserCookie, e);
        }

        /* ### STEP D ### */
        // Return to the caller, with jsession id information inside, AND on path.
        Response response = null;
        try {

            String thisAgent = context.getHeader("User-Agent");
            if (thisAgent != null && !"".equals(thisAgent)) {
                UserAgent agent = UserAgent.match(thisAgent);

                String browserDescription = agent.getDescription();
                if (browserDescription != null) {
                    if (browserDescription.equals(UserAgent.MSIE9.getDescription())) {
                        returnURL = returnURL.replaceAll("#social", "#/social");
                    }
                }
            }

            LOG.debug("return url: " + returnURL);

            String uri = URLEncoder.encode(returnURL + "/ok/" + state, "ISO-8859-1");

            LOG.debug("url encoding uri: " + uri);

            retURI = new URI(editorsURL + "/social/linkedin/callback?uri=" + uri);

            if (editorsUserCookie == null) {
                response = Response.seeOther(retURI).build();
            } else {
                response = Response.seeOther(retURI).cookie(editorsUserCookie).build();
            }
        } catch (URISyntaxException | UnsupportedEncodingException uri_ex) { //Should not happen 
            LOG.error("Error making URI", uri_ex);
            response = editorsUserCookie == null ? Response.serverError().build()
                    : Response.serverError().cookie(editorsUserCookie).build();
        }

        return response;
    }

    /*
	 * Returns (GET) the json stored in the session as an HTML wrapped object. Should have session in call
     */
    @GET
    @Path(Paths.SOCIAL_IMPORT_HELPER + "/" + Paths.SOCIAL_IMPORT_PROVIDER_LINKEDIN)
    @Produces(MediaType.TEXT_HTML)
    public Response linkedinGETHelper() {
        return linkedinHelper();
    }

    /*
	 * Returns (POST) the json stored in the session as an HTML wrapped object. Should have session in call
     */
    @POST
    @Path(Paths.SOCIAL_IMPORT_HELPER + "/" + Paths.SOCIAL_IMPORT_PROVIDER_LINKEDIN)
    @Produces(MediaType.TEXT_HTML)
    public Response linkedinPOSTHelper() {
        return linkedinHelper();
    }

    /*
	 * Main for GET and POST previous methods
     */
    public Response linkedinHelper() {
        try {
            HttpSession session = context.getSession();

            String json = (String) session.getAttribute(SOCIAL_SESSION_PARAM_JSON);
            if (json == null || json.trim().equals("")) {
                return Response.status(Status.NO_CONTENT).build();
            }
            session.removeAttribute(SOCIAL_SESSION_PARAM_JSON); // cleanup data
            String htmlResponse = htmlWrapper.htmlWrap(json, Status.OK.toString());
            return Response.ok().type(MediaType.TEXT_HTML).entity(htmlResponse).build();

        } catch (final Exception e) {
            throw ApiException.addInfo(new ApiException(e, UploadStatus.OTHER.getDescription(), Status.INTERNAL_SERVER_ERROR),
                    new ExtraLogInfo().add(LogFields.LOCATION, location).add(LogFields.MODULE, module));
        }
    }

    /**
     * Constructs a URI containing the http code and the error text key that
     * will be used for the message in /editors and will redirect to that URI
     *
     * @param returnURL
     * @param error
     * @return
     */
    private Response constructErrorUriResponse(String returnURL, SocialErrorStatus errorStatus, NewCookie editorsUserCookie) {
        return this.constructErrorUriResponse(returnURL, errorStatus, editorsUserCookie, null);
    }

    private Response constructErrorUriResponse(String returnURL, SocialErrorStatus errorStatus, NewCookie editorsUserCookie, Throwable exception) {
        String baseReturnURI = editorsURL + "/social/linkedin/callback";
        String serverErrorReturnURI = null;

        if (errorStatus == null) {
            errorStatus = SocialErrorStatus.server_error;
        }

        int httpCode = errorStatus.getHttpCode();
        String errorKey = errorStatus.getErrorKey();

        //LOG
        ApiException apiException;
        if (exception == null) {
            apiException = ApiException.addInfo(new ApiException("Social:LinkedIn - SkillsPassport from LinkedIn profile failed", errorKey, httpCode),
                    new ExtraLogInfo().add(LogFields.LOCATION, location).add(LogFields.MODULE, module));
        } else {
            if (exception instanceof ApiException) {
                apiException = (ApiException) exception;
            } else {
                apiException = ApiException.addInfo(new ApiException(exception, errorKey, httpCode),
                        new ExtraLogInfo().add(LogFields.LOCATION, location).add(LogFields.MODULE, module));
            }
        }
        apiException.log();

        serverErrorReturnURI = returnURL + "/code/" + httpCode + "/error/" + errorKey + "/trace/" + apiException.getTrace();

        URI retURI = null;
        try {
            retURI = new URI(baseReturnURI + "?uri=" + URLEncoder.encode(serverErrorReturnURI, "ISO-8859-1"));
        } catch (URISyntaxException | UnsupportedEncodingException uri_ex) {
            LOG.error("SocialImport: problem constructing error feedback");
        }
        if (editorsUserCookie == null) {
            return Response.seeOther(retURI).build();
        } else {
            return Response.seeOther(retURI).cookie(editorsUserCookie).build();
        }
    }

    /* Utility method that returns specific Locale formats needed by 
	 * linked In api (in the accept-language HTTP header)
	 * in order to get other versions of profiles
	 * more info in https://jira.cedefop.europa.eu/browse/EWA-1614
	 * @param the url
	 * @return the linked in locale
	 * */
    private String getLILocaleString(String returnURL) {

        String locale = retrieveLocale(returnURL).toString().toLowerCase();

        String result = locale + "-";
        //int idx =  returnURL.indexOf( this.editorsURL)== 0? this.editorsURL.length() : -1;
        //String loc = returnURL.substring( idx+1, idx+3);
        //locale = loc.toLowerCase()+ "-" + loc.toUpperCase();

        switch (locale) {

            case "en": {
                result += "US";
                break;
            }
            case "da": {
                result += "DK";
                break;
            }
            case "pt": {
                result += "BR";
                break;
            }
            case "cs": {
                result += "CZ";
                break;
            }
            case "sv": {
                result += "SE";
                break;
            }
            default: {
                result += locale.toUpperCase();
            }
        }
        return result;
    }

    String urlLocalePattern = ".*/([a-z]{2})/social/linkedin";

    private Locale retrieveLocale(String url) {
        Pattern r = Pattern.compile(urlLocalePattern);
        Matcher m = r.matcher(url);
        if (m.find()) {
            return new Locale(m.group(1));
        }
        return Locale.ENGLISH;
    }
}
