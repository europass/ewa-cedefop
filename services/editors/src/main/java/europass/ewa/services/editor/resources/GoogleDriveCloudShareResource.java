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

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import joptsimple.internal.Strings;
import europass.ewa.enums.ConversionFileType;
import europass.ewa.model.SkillsPassport;
import europass.ewa.services.Paths;
import europass.ewa.services.annotation.EWAEditor;
import europass.ewa.services.conversion.model.ExportableModel;
import europass.ewa.services.conversion.model.ExportableModelFactory;
import europass.ewa.services.conversion.process.DocumentGeneration;

@Path(Paths.CLOUD_BASE + Paths.SHARE + "/" + Paths.CLOUD_STORAGE_PROVIDER_GDRIVE)
public class GoogleDriveCloudShareResource extends CloudProviderResource {

    @Context
    HttpServletRequest context;

//	private static final String RTE_HTML_ESCAPE_LT_REGEXP = "&lt;(\\/?[a-z]+>)";
    private static final String RTE_HTML_ESCAPE_LT_REGEXP = "&lt;";
    private static final String RTE_HTML_ESCAPE_LT_DELIMITER = "--RTE_HTML_ESCAPE_LT--";

//	private static final String module   = ServerModules.SERVICES_EDITORS.getModule() ,
//							    location = CloudProvider.GOOGLEDRIVE.getDescription();
    @Inject
    public GoogleDriveCloudShareResource(
            @EWAEditor ExportableModelFactory<String> modelFactory,
            @EWAEditor DocumentGeneration generation) {
        super(modelFactory, generation, CloudProvider.GOOGLEDRIVE);
    }

    @POST
    @Produces(MediaType.TEXT_HTML)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path(Paths.PATH_XML)
    public Response xml(@QueryParam("stats") String keepstats, StoreInfo storeinfo, @Context HttpServletRequest request) {

        return doStore(storeinfo, ConversionFileType.XML, Boolean.parseBoolean(keepstats), request.getHeader("user-agent"), request.getAttribute("X-Request-ID").toString());
    }

    Response store(ExportableModel modelContainer, ConversionFileType fileType, String folder, String token) {

        SkillsPassport esp = modelContainer.getModel();
        String fname = this.filenameTimeAppend(esp.getFilename(), "xml");

        String xml = modelContainer.xmlRepresentation();

        String rteEscapeDelimXml = xml.replaceAll(RTE_HTML_ESCAPE_LT_REGEXP, RTE_HTML_ESCAPE_LT_DELIMITER);

        // Will be used for the url construction
        String shareUrl = Paths.SHARE + "/" + esp.getLocale() + "/" + Paths.CLOUD_STORAGE_PROVIDER_GDRIVE;

        String sessionID = (context.getSession().getId() != null ? context.getSession().getId() : "");
        if (Strings.isNullOrEmpty(fname)) {
            return handleResponseStatus(200, "google-share", "", shareUrl, rteEscapeDelimXml, sessionID);
        }

        return handleResponseStatus(200, "google-share", fname, shareUrl, rteEscapeDelimXml, sessionID);
    }
}
