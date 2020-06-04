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

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.Set;

import javax.inject.Named;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.google.inject.Inject;

import europass.ewa.modules.SupportedLocaleModule;
import europass.ewa.resources.JsonResourceBundle;
import europass.ewa.services.Paths;
import europass.ewa.services.ResponseUtils;
import europass.ewa.services.rest.pojos.EuropassLanguagesJSON;
import europass.ewa.services.rest.pojos.EuropassLanguagesXML;
import europass.ewa.services.rest.pojos.Language;

@Path(Paths.SUPPORTED_LANGUAGES_BASE)
public class SupportedLanguagesResource {

    private final Set<Locale> supportedLocales;
    private final XmlMapper xmlMapper;
    private final ObjectMapper jsonMapper;
    private final ResourceBundle bundle;

    private final String[] acceptXML = {MediaType.APPLICATION_XML};
    private final String[] acceptJSON = {MediaType.APPLICATION_JSON};

    private StringBuilder sb;

    @Inject
    public SupportedLanguagesResource(@Named(SupportedLocaleModule.EWA_SUPPORTED_LANGUAGES) Set<Locale> supportedLocales, XmlMapper xmlMapper, ObjectMapper jsonMapper) {

        this.supportedLocales = supportedLocales;
        this.xmlMapper = xmlMapper;
        this.jsonMapper = jsonMapper;
        this.bundle = ResourceBundle.getBundle("bundles/ContentLocale", new JsonResourceBundle.Control(new ObjectMapper()));
        this.sb = new StringBuilder();
    }

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String getGreeting() {
        return "This is the Supported Languages Service!";
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON + Paths.UTF8_CHARSET)
    @Path("/json")
    public Response getSupportedLanguagesJson() {

//		String json = "";
        List<Language> languagesList = new EuropassLanguagesJSON();

        for (Locale locale : supportedLocales) {

            String code = locale.toString();
            String label = bundle.getString(code);

            languagesList.add(new Language(code, label));
        }

        try {
            sb.append(jsonMapper.writeValueAsString(languagesList));
//			json = jsonMapper.writerWithDefaultPrettyPrinter().writeValueAsString(sb);

        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return ResponseUtils.buildResponse(sb.toString(), MediaType.APPLICATION_JSON, 200, acceptJSON);
    }

    @GET
    @Produces(MediaType.APPLICATION_XML + Paths.UTF8_CHARSET)
    @Path("/xml")
    public Response getSupportedLanguagesXml() {

        EuropassLanguagesXML languagesWrapper = new EuropassLanguagesXML(new ArrayList<Language>());

        List<Language> languagesList = languagesWrapper.getEuropassLanguagesXML();

        for (Locale locale : supportedLocales) {

            String code = locale.toString();
            String label = bundle.getString(code);

            languagesList.add(new Language(code, label));
        }

        try {
            sb.append(xmlMapper.writeValueAsString(languagesWrapper));

        } catch (JsonProcessingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return ResponseUtils.buildResponse(sb.toString(), MediaType.APPLICATION_XML, 200, acceptXML);
    }
}
