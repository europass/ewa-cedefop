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
package europass.ewa.services.rest.resources;

import europass.ewa.enums.*;
import europass.ewa.locales.LocaleDetector;
import europass.ewa.logging.ExtraLogInfo;
import europass.ewa.model.DocumentInfo;
import europass.ewa.model.SkillsPassport;
import europass.ewa.services.Paths;
import europass.ewa.services.annotation.RestApi;
import europass.ewa.services.conversion.model.ExportableModel;
import europass.ewa.services.conversion.model.ExportableModelFactory;
import europass.ewa.services.conversion.model.TranslationInfo;
import europass.ewa.services.conversion.process.DocumentGeneration;
import europass.ewa.services.exception.ApiException;
import europass.ewa.services.exception.NotSupportedLocaleException;
import europass.ewa.statistics.DocumentGenerator;
import org.apache.commons.lang.StringUtils;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import java.util.*;

/**
 * XML/JSON based conversion services.
 *
 * Note that the default Jackson Xml/Object Mapper is used, therefore the
 * XML/JSON is expected to be compatible with V3.0.
 *
 * @author ekar
 *
 */
@Path(Paths.CONVERSION_BASE)
public class RemoteDocumentResource {

    private final ExportableModelFactory<SkillsPassport> modelFactory;

    private final DocumentGeneration generation;

    private final LocaleDetector localeDetector;

    @Inject
    public RemoteDocumentResource(@RestApi ExportableModelFactory<SkillsPassport> modelFactory, @RestApi DocumentGeneration generation, LocaleDetector localeDetector) {
        this.modelFactory = modelFactory;
        this.generation = generation;
        this.localeDetector = localeDetector;
    }

    @GET
    @Produces("text/plain")
    public String getGreeting() {
        return "Europass: This is an XML/JSON v3.0 based Conversion Service!";
    }

    // ------------ JSON TO XML --------------------------------------
    @POST
    @Consumes(MediaType.APPLICATION_JSON + Paths.UTF8_CHARSET)
    @Produces({MediaType.APPLICATION_XML + Paths.UTF8_CHARSET, MediaType.APPLICATION_JSON + Paths.UTF8_CHARSET})
    @Path(Paths.PATH_XML)
    public SkillsPassport toXML(@QueryParam("stats") String stats, @Context HttpServletRequest request, SkillsPassport esp) {

        return pojoProcess(esp, ConversionFileType.XML, getAcceptableLanguages(request), stats);

    }

    // ------------ JSON TO XML CV ONLY ----------------------------
    @POST
    @Consumes({MediaType.APPLICATION_JSON + Paths.UTF8_CHARSET})
    @Produces({MediaType.APPLICATION_XML + Paths.UTF8_CHARSET, MediaType.APPLICATION_JSON + Paths.UTF8_CHARSET})
    @Path(Paths.PATH_XML_CV_ONLY)
    public SkillsPassport toXmlCvOnly(@QueryParam("stats") String stats, @Context HttpServletRequest request, SkillsPassport esp) {

        return pojoProcess(esp, EuropassDocumentType.ECV, ConversionFileType.XML, getAcceptableLanguages(request), stats);

    }

    // ------------ JSON TO XML ESP ONLY-----------------------------
    @POST
    @Consumes({MediaType.APPLICATION_JSON + Paths.UTF8_CHARSET})
    @Produces({MediaType.APPLICATION_XML + Paths.UTF8_CHARSET, MediaType.APPLICATION_JSON + Paths.UTF8_CHARSET})
    @Path(Paths.PATH_XML_ESP_ONLY)
    public SkillsPassport toXmlEspOnly(@QueryParam("stats") String stats, @Context HttpServletRequest request, SkillsPassport esp) {

        return pojoProcess(esp, EuropassDocumentType.ESP, ConversionFileType.XML, getAcceptableLanguages(request), stats);

    }

    // ------------ XML TO JSON -----------------------------------------------
    @POST
    @Consumes(MediaType.APPLICATION_XML + Paths.UTF8_CHARSET)
    @Produces({MediaType.APPLICATION_JSON + Paths.UTF8_CHARSET, MediaType.APPLICATION_XML})
    @Path(Paths.PATH_JSON)
    public SkillsPassport toJson(@QueryParam("stats") String stats, @Context HttpServletRequest request, SkillsPassport esp) {

        return pojoProcess(esp, ConversionFileType.JSON, getAcceptableLanguages(request), stats);

    }

    @POST
    @Consumes(MediaType.APPLICATION_XML + Paths.UTF8_CHARSET)
    @Produces({MediaType.APPLICATION_JSON + Paths.UTF8_CHARSET, MediaType.APPLICATION_XML})
    @Path(Paths.PATH_JSON_CV_ONLY)
    public SkillsPassport toJsonCvOnly(@QueryParam("stats") String stats, @Context HttpServletRequest request, SkillsPassport esp) {

        return pojoProcess(esp, EuropassDocumentType.ECV, ConversionFileType.JSON, getAcceptableLanguages(request), stats);

    }

    @POST
    @Consumes(MediaType.APPLICATION_XML + Paths.UTF8_CHARSET)
    @Produces({MediaType.APPLICATION_JSON + Paths.UTF8_CHARSET, MediaType.APPLICATION_XML})
    @Path(Paths.PATH_JSON_ESP_ONLY)
    public SkillsPassport toJsonEspOnly(@QueryParam("stats") String stats, @Context HttpServletRequest request, SkillsPassport esp) {

        return pojoProcess(esp, EuropassDocumentType.ESP, ConversionFileType.JSON, getAcceptableLanguages(request), stats);

    }

    // ------------ XML/JSON TO OPEN DOCUMENT --------------------------------
    @POST
    @Consumes({MediaType.APPLICATION_XML + Paths.UTF8_CHARSET, MediaType.APPLICATION_JSON + Paths.UTF8_CHARSET})
    @Produces({ContentTypes.OPEN_DOC_CT, MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    @Path(Paths.PATH_OPEN_DOCUMENT)
    public Response toOdt(@QueryParam("stats") String stats, @Context HttpServletRequest request, SkillsPassport esp) {

        return process(esp, ConversionFileType.OPEN_DOC, getAcceptableLanguages(request), stats);

    }

    // ------------ XML/JSON TO OPEN DOCUMENT (CV ONLY) --------------------------------
    @POST
    @Consumes({MediaType.APPLICATION_XML + Paths.UTF8_CHARSET, MediaType.APPLICATION_JSON + Paths.UTF8_CHARSET})
    @Produces({ContentTypes.OPEN_DOC_CT, MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    @Path(Paths.PATH_OPEN_DOCUMENT_CV_ONLY)
    public Response toOdtCvOnly(@QueryParam("stats") String stats, @Context HttpServletRequest request, SkillsPassport esp) {

        return process(esp, EuropassDocumentType.ECV, ConversionFileType.OPEN_DOC, getAcceptableLanguages(request), stats);

    }

    // ------------ XML/JSON TO OPEN DOCUMENT (ESP ONLY) --------------------------------
    @POST
    @Consumes({MediaType.APPLICATION_XML + Paths.UTF8_CHARSET, MediaType.APPLICATION_JSON + Paths.UTF8_CHARSET})
    @Produces({ContentTypes.OPEN_DOC_CT, MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    @Path(Paths.PATH_OPEN_DOCUMENT_ESP_ONLY)
    public Response toOdtEspOnly(@QueryParam("stats") String stats, @Context HttpServletRequest request, SkillsPassport esp) {

        return process(esp, EuropassDocumentType.ESP, ConversionFileType.OPEN_DOC, getAcceptableLanguages(request), stats);

    }

    // ------------ XML/JSON TO WORD ------------------------------------------
    @POST
    @Consumes({MediaType.APPLICATION_XML + Paths.UTF8_CHARSET, MediaType.APPLICATION_JSON + Paths.UTF8_CHARSET})
    @Produces({ContentTypes.WORD_DOC_CT, MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    @Path(Paths.PATH_WORD)
    public Response toDoc(@QueryParam("stats") String stats, @Context HttpServletRequest request, SkillsPassport esp) {

        return process(esp, ConversionFileType.WORD_DOC, getAcceptableLanguages(request), stats);

    }

    // ------------ XML/JSON TO WORD (CV ONLY) ------------------------------------------
    @POST
    @Consumes({MediaType.APPLICATION_XML + Paths.UTF8_CHARSET, MediaType.APPLICATION_JSON + Paths.UTF8_CHARSET})
    @Produces({ContentTypes.WORD_DOC_CT, MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    @Path(Paths.PATH_WORD_CV_ONLY)
    public Response toDocCvOnly(@QueryParam("stats") String stats, @Context HttpServletRequest request, SkillsPassport esp) {

        return process(esp, EuropassDocumentType.ECV, ConversionFileType.WORD_DOC, getAcceptableLanguages(request), stats);

    }

    // ------------ XML/JSON TO WORD (ESP ONLY) ------------------------------------------
    @POST
    @Consumes({MediaType.APPLICATION_XML + Paths.UTF8_CHARSET, MediaType.APPLICATION_JSON + Paths.UTF8_CHARSET})
    @Produces({ContentTypes.WORD_DOC_CT, MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    @Path(Paths.PATH_WORD_ESP_ONLY)
    public Response toDocEspOnly(@QueryParam("stats") String stats, @Context HttpServletRequest request, SkillsPassport esp) {

        return process(esp, EuropassDocumentType.ESP, ConversionFileType.WORD_DOC, getAcceptableLanguages(request), stats);

    }

    // ------------ XML/JSON TO PDF (ECV+ESP) --------------------------------
    @POST
    @Consumes({MediaType.APPLICATION_XML + Paths.UTF8_CHARSET, MediaType.APPLICATION_JSON + Paths.UTF8_CHARSET})
    @Produces({ContentTypes.PDF_CT, MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    @Path(Paths.PATH_PDF)
    public Response toPdf(@QueryParam("stats") String stats, @Context HttpServletRequest request, SkillsPassport esp) {

        return process(esp, ConversionFileType.PDF, getAcceptableLanguages(request), stats);

    }

    // ------------ XML/JSON TO PDF (ESP ONLY) --------------------------------
    @POST
    @Consumes({MediaType.APPLICATION_XML + Paths.UTF8_CHARSET, MediaType.APPLICATION_JSON + Paths.UTF8_CHARSET})
    @Produces({ContentTypes.PDF_CT, MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    @Path(Paths.PATH_PDF_ESP_ONLY)
    public Response toPDFEspOnly(@QueryParam("stats") String stats, @Context HttpServletRequest request, SkillsPassport esp) {

        return process(esp, EuropassDocumentType.ESP, ConversionFileType.PDF, getAcceptableLanguages(request), stats);
    }

    // ------------ XML/JSON TO PDF ( CV ONLY) --------------------------------
    @POST
    @Consumes({MediaType.APPLICATION_XML + Paths.UTF8_CHARSET, MediaType.APPLICATION_JSON + Paths.UTF8_CHARSET})
    @Produces({ContentTypes.PDF_CT, MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    @Path(Paths.PATH_PDF_CV_ONLY)
    public Response toPDFCVOnly(@QueryParam("stats") String stats, @Context HttpServletRequest request, SkillsPassport esp) {

        return process(esp, EuropassDocumentType.ECV, ConversionFileType.PDF, getAcceptableLanguages(request), stats);
    }

    //--------------------------------------------------------------------------
    //--------------------------------------------------------------------------
    /**
     * Used by the XML endpoints
     *
     * @param esp
     * @param file
     * @param requestedLocales
     * @param keepstats
     * @return
     */
    private Response process(SkillsPassport esp, ConversionFileType file, List<Locale> requestedLocales, String keepstats) {

        ExportableModel modelContainer = modelFactory.getInstance(esp, file, DocumentGenerator.WEB_SERVICES_REST);

        //EWA 1520 add Request Id to extra log info
        modelContainer.augmentLogInfo(LogFields.REQ_ID, UUID.randomUUID().toString());

        configureProcess(modelContainer, esp, requestedLocales, keepstats);

        return prepareSuccessResponse(modelContainer);

    }

    /**
     * Used by the XML -cv and -esp endpoints Important: [EWA-983] Removes any
     * "DocumentInfo.Bundle" information from input/ output
     *
     * @param esp
     * @param document
     * @param file
     * @param requestedLocales
     * @param keepstats
     * @return
     */
    private Response process(SkillsPassport esp, EuropassDocumentType document, ConversionFileType file, List<Locale> requestedLocales, String keepstats) {

        ExportableModel modelContainer = modelFactory.getInstance(esp, document, file, DocumentGenerator.WEB_SERVICES_REST);

        modelContainer.augmentLogInfo(LogFields.REQ_ID, UUID.randomUUID().toString());

        removeDocumentBundleInfo(modelContainer);

        configureProcess(modelContainer, esp, requestedLocales, keepstats);

        return prepareSuccessResponse(modelContainer);
    }

    private void configureProcess(ExportableModel modelContainer, SkillsPassport esp, List<Locale> requestedLocales, String keepstats) {

        if (keepstats != null) {
            modelContainer.setKeepStats(Boolean.valueOf(keepstats));
        }

        modelContainer.setTranslationInfo(manageTranslationRequest(requestedLocales, esp.getLocale()));

        generation.process(modelContainer);

    }
    //--------------------------------------------------------------------------
    //--------------------------------------------------------------------------

    /**
     * Used by the PDF, DOC and ODT endpoints
     *
     * @param esp
     * @param file
     * @param requestedLocales
     * @param keepstats
     * @return
     */
    private SkillsPassport pojoProcess(SkillsPassport esp, ConversionFileType file, List<Locale> requestedLocales, String keepstats) {

        ExportableModel modelContainer = modelFactory.getInstance(esp, file, DocumentGenerator.WEB_SERVICES_REST);

        //EWA 1520 add Request Id to extra log info
        modelContainer.augmentLogInfo(LogFields.REQ_ID, UUID.randomUUID().toString());

        return this.modelToPojo(modelContainer, esp, file, requestedLocales, keepstats);
    }

    /**
     * Used by the PDF, DOC and ODT -cv and -esp endpoints Important: [EWA-983]
     * Removes any "DocumentInfo.Bundle" information from input/ output
     *
     * @param esp
     * @param document
     * @param file
     * @param requestedLocales
     * @param keepstats
     * @return
     */
    private SkillsPassport pojoProcess(SkillsPassport esp, EuropassDocumentType document, ConversionFileType file, List<Locale> requestedLocales, String keepstats) {

        ExportableModel modelContainer = modelFactory.getInstance(esp, document, file, DocumentGenerator.WEB_SERVICES_REST);

        modelContainer.setDocumentType(document);

        modelContainer.augmentLogInfo(LogFields.REQ_ID, UUID.randomUUID().toString());

        removeDocumentBundleInfo(modelContainer);

        return this.modelToPojo(modelContainer, esp, file, requestedLocales, keepstats);
    }

    private SkillsPassport modelToPojo(ExportableModel modelContainer, SkillsPassport esp, ConversionFileType file, List<Locale> requestedLocales, String keepstats) {

        if (keepstats != null) {
            modelContainer.setKeepStats(Boolean.valueOf(keepstats));
        }

        modelContainer.setTranslationInfo(manageTranslationRequest(requestedLocales, esp.getLocale()));

        generation.process(modelContainer);

        return modelContainer.getModel();
    }

    //--------------------------------------------------------------------------
    //--------------------------------------------------------------------------
    private void removeDocumentBundleInfo(ExportableModel modelContainer) {
        SkillsPassport esp = modelContainer.getModel();
        if (esp == null) {
            return;
        }
        DocumentInfo docInfo = esp.getDocumentInfo();
        if (docInfo == null) {
            return;
        }
        List<EuropassDocumentType> bundle = docInfo.getBundle();
        if (bundle == null || (bundle != null && bundle.isEmpty())) {
            return;
        }
        //Set to null
        docInfo.setBundle(null);
    }

    //--------------------------------------------------------------------------
    //--------------------------------------------------------------------------
    /**
     * Set properly the translation info depending on the Accept-Language Header
     * and the ESP locale.
     *
     * @param requestedLocales
     * @param modelLocale
     * @return
     */
    private TranslationInfo manageTranslationRequest(List<Locale> requestedLocales, Locale modelLocale) {

        Locale requestedLocale = this.localeDetector.getSupported(requestedLocales);

        // 1. Check if requested Locale language is not supported
        if (requestedLocale == null) {
            throw ApiException.addInfo(new NotSupportedLocaleException(requestedLocales),
                    new ExtraLogInfo().add(LogFields.MODULE, ServerModules.SERVICES_REST.getModule()));
        }

        //2. Check if the requested locale is not set - use model's locale for translation + document
        if (this.localeDetector.isAnyLocale(requestedLocale)) {
            return new TranslationInfo(modelLocale);
        }

        // 3. Request locale is a supported locale, so use this for translation + document 
        return new TranslationInfo(requestedLocale);

//		Check if ESP locale is null - translate to the requested locale (for sure it is not null)
//		if ( modelLocale == null || (modelLocale != null && Strings.isNullOrEmpty( modelLocale.getLanguage() )) ) {
//			return new TranslationInfo( requestedLocale, true );
//		}
//		// 4. Check if requested Locale (not null) is different than the ESP locale (not null)
//		if ( !requestedLocale.getLanguage().equalsIgnoreCase( modelLocale.getLanguage() ) ) {
//			return new TranslationInfo( requestedLocale, true );
//		}
//		return new TranslationInfo( modelLocale, false );
    }

    //--------------------------------------------------------------------------
    //--------------------------------------------------------------------------
    private Response prepareSuccessResponse(ExportableModel model) {

        ConversionFileType fileType = model.getFileType();

        // Create a new ResponseBuilder with an OK status.
        ResponseBuilder r = Response.ok();

        // Content Type same as file type
        r.header("Content-Type", fileType.getMimeType() + Paths.UTF8_CHARSET);

        return r.entity(model.asBytes()).build();
    }

    private List<Locale> getAcceptableLanguages(final HttpServletRequest request) {

        final String acceptedLanguage = request.getHeader("accept-language");

        if (StringUtils.isNotBlank(acceptedLanguage)) {

            return Arrays.asList(new Locale(acceptedLanguage));
        }
        return Arrays.asList(new Locale("*"));
    }
}
