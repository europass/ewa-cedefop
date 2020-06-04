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

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import com.google.inject.Inject;

import europass.ewa.enums.ConversionFileType;
import europass.ewa.enums.LogFields;
import europass.ewa.enums.ServerModules;
import europass.ewa.logging.ExtraLogInfo;
import europass.ewa.model.SkillsPassport;
import europass.ewa.services.MediaTypeUtils;
import europass.ewa.services.Paths;
import europass.ewa.services.annotation.BackwardCompatibility;
import europass.ewa.services.annotation.RestApi;
import europass.ewa.services.conversion.model.ExportableModel;
import europass.ewa.services.conversion.model.ExportableModelFactory;
import europass.ewa.services.conversion.process.DocumentGeneration;
import europass.ewa.services.exception.ApiException;
import europass.ewa.services.exception.DisallowedMediaTypeException;
import europass.ewa.services.exception.UndefinedMediaTypeException;
import europass.ewa.statistics.DocumentGenerator;

@Path(Paths.UPGRADE_BASE)
public class XMLBackwardCompatibilityResource {

    private final ExportableModelFactory<String> modelFactory;

    private final DocumentGeneration generation;

    private final ArrayList<MediaType> listAllowedTypes;

    @Inject
    public XMLBackwardCompatibilityResource(@BackwardCompatibility ExportableModelFactory<String> modelFactory, @RestApi DocumentGeneration generation, ArrayList<MediaType> listAllowedTypes) {
        this.modelFactory = modelFactory;
        this.generation = generation;
        this.listAllowedTypes = listAllowedTypes;
    }

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String getGreeting() {
        return "This is an XML Backward Compatibility Service!";
    }

    @POST
    @Consumes(MediaType.APPLICATION_XML + Paths.UTF8_CHARSET)
    @Produces(MediaType.APPLICATION_XML + Paths.UTF8_CHARSET)
    public SkillsPassport upgradeXml(String source, @QueryParam("stats") String keepstats, @Context HttpServletRequest request) {

        String requestId = UUID.randomUUID().toString();
        request.setAttribute("X-Request-ID", requestId);

        //if source not xml ...
        // Check if media type is allowed.
        InputStream is = new ByteArrayInputStream(source.getBytes());
        BufferedInputStream bis = new BufferedInputStream(is);
        MediaType mediaType = MediaType.WILDCARD_TYPE;
        listAllowedTypes.add(MediaType.TEXT_XML_TYPE);
        listAllowedTypes.add(MediaType.APPLICATION_XML_TYPE);
        try {
            mediaType = MediaTypeUtils.readMediaType(bis, listAllowedTypes);
        } // handle UndefinedMediaTypeException
        // this exception is thrown when JSON is given as input
        // readMediaType cannot resolve the media type of JSON input
        catch (final UndefinedMediaTypeException e) {
            throw ApiException.addInfo(new DisallowedMediaTypeException(),
                    new ExtraLogInfo().add(e.getExtraLogInfo()).add(LogFields.MODULE, ServerModules.SERVICES_REST.getModule()));
        }
        if (!mediaType.equals(MediaType.TEXT_XML_TYPE)) {
            throw ApiException.addInfo(new DisallowedMediaTypeException(),
                    new ExtraLogInfo().add(LogFields.MODULE, ServerModules.SERVICES_REST.getModule()));
        }
        ExportableModel modelContainer = modelFactory.getInstance(source, ConversionFileType.XML, DocumentGenerator.WEB_SERVICES_REST_UPGRADE);

        modelContainer.augmentLogInfo(LogFields.REQ_ID, requestId);

        if (keepstats != null) {
            modelContainer.setKeepStats(Boolean.valueOf(keepstats));
        }

        generation.process(modelContainer);

        return modelContainer.getModel();
    }
}
