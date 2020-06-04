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
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import europass.ewa.enums.ConversionFileType;
import europass.ewa.model.SkillsPassport;
import europass.ewa.services.Paths;
import europass.ewa.services.annotation.EWAEditor;
import europass.ewa.services.conversion.model.ExportableModel;
import europass.ewa.services.conversion.model.ExportableModelFactory;
import europass.ewa.services.conversion.process.DocumentGeneration;

@Path(Paths.CLOUD_BASE + "/dropbox")
public class DropboxCloudProviderResource extends CloudProviderResource {

    private static final String DROPBOX_ROOT = "dropbox";
    private static final String DROPBOX_PATH_FOLDER = "Europass";
    private static final String DROPBOX_DEFAULT_FOLDER = DROPBOX_ROOT + "/" + DROPBOX_PATH_FOLDER;

    @Inject
    public DropboxCloudProviderResource(@EWAEditor final ExportableModelFactory<String> modelFactory,
            @EWAEditor final DocumentGeneration generation) {

        super(modelFactory, generation, CloudProvider.DROPBOX);
    }

    @POST
    @Produces(MediaType.TEXT_HTML)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path(Paths.PATH_XML)
    public Response xml(@QueryParam("stats") final String keepstats,
            final StoreInfo storeinfo, @Context final HttpServletRequest request) {

        return doStore(storeinfo, ConversionFileType.XML, Boolean.parseBoolean(keepstats),
                request.getHeader("user-agent"), request.getAttribute("X-Request-ID").toString());
    }

    @POST
    @Produces(MediaType.TEXT_HTML)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path(Paths.PATH_OPEN_DOCUMENT)
    public Response opendoc(@QueryParam("stats") final String keepstats, final StoreInfo storeinfo,
            @Context final HttpServletRequest request) {

        return doStore(storeinfo, ConversionFileType.OPEN_DOC, Boolean.parseBoolean(keepstats),
                request.getHeader("user-agent"), request.getAttribute("X-Request-ID").toString());
    }

    @POST
    @Produces(MediaType.TEXT_HTML)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path(Paths.PATH_WORD)
    public Response word(@QueryParam("stats") final String keepstats, final StoreInfo storeinfo,
            @Context final HttpServletRequest request) {

        return doStore(storeinfo, ConversionFileType.WORD_DOC, Boolean.parseBoolean(keepstats),
                request.getHeader("user-agent"), request.getAttribute("X-Request-ID").toString());
    }

    @POST
    @Produces(MediaType.TEXT_HTML)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path(Paths.PATH_PDF)
    public Response pdf(@QueryParam("stats") final String keepstats, final StoreInfo storeinfo,
            @Context HttpServletRequest request) {

        return doStore(storeinfo, ConversionFileType.PDF, Boolean.parseBoolean(keepstats),
                request.getHeader("user-agent"), request.getAttribute("X-Request-ID").toString());
    }

    @Override
    Response store(final ExportableModel modelContainer, final ConversionFileType fileType,
            String folder, final String authToken) {
        //We need a path here, which we cannot fetch with the Dropbox Chooser on the client-side.
        //Therefore we set to "dropbox/Europass"
        if (!DROPBOX_DEFAULT_FOLDER.equals(folder)) {
            folder = DROPBOX_PATH_FOLDER;
        }

        final SkillsPassport esp = modelContainer.getModel();
        final String filename = esp.getFilename();
        final CloseableHttpClient httpclient = HttpClients.createDefault();

        int statusCode = uploadToDropboxAction(modelContainer, authToken, folder, filename, httpclient);

        if (filename != null && !filename.isEmpty()) {
            return handleResponseStatus(statusCode, filename);
        } else {
            return handleResponseStatus(statusCode);
        }
    }
}
