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
package europass.ewa.services.modules;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.imageio.ImageIO;
import javax.inject.Named;
import javax.ws.rs.core.MediaType;

import com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider;
import com.fasterxml.jackson.jaxrs.xml.JacksonXMLProvider;
import com.google.common.collect.ImmutableMap;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.TypeLiteral;
import com.sun.jersey.guice.JerseyServletModule;
import com.sun.jersey.guice.spi.container.servlet.GuiceContainer;

import europass.ewa.model.AttachmentVisitor;
import europass.ewa.model.SkillsPassport;
import europass.ewa.model.conversion.Converter;
import europass.ewa.model.conversion.json.JSON;
import europass.ewa.model.conversion.json.JsonConverter;
import europass.ewa.model.conversion.json.ModelContainerConverter;
import europass.ewa.model.conversion.xml.XML;
import europass.ewa.model.conversion.xml.XmlConverter;
import europass.ewa.model.wrapper.ModelContainer;
import europass.ewa.services.ODT;
import europass.ewa.services.PDF;
import europass.ewa.services.WORD;
import europass.ewa.services.annotation.Default;
import europass.ewa.services.conversion.mapper.ObjectMapperResolver;
import europass.ewa.services.conversion.mapper.XmlMapperResolver;
import europass.ewa.services.conversion.steps.AttachmentsVisibilityStep;
import europass.ewa.services.conversion.steps.BytePreparationStep;
import europass.ewa.services.conversion.steps.DefaultPreferencesStep;
import europass.ewa.services.conversion.steps.ODTAttachmentVisitor;
import europass.ewa.services.conversion.steps.PDFAttachmentVisitor;
import europass.ewa.services.conversion.steps.WORDAttachmentVisitor;
import europass.ewa.services.files.ConcreteImageProcessing;
import europass.ewa.services.files.ImageProcessing;

public class CoreServicesModule extends JerseyServletModule {

    public static final String UPLOAD_CV_ALLOWED_TYPES = "ewa.upload.cv.types";

    private List<MediaType> uploadEspTypes = null;

    @Override
    protected void configureServlets() {
        bind(GuiceContainer.class).in(Singleton.class);

        //--- Message Body Reader and Writer Providers ---
        bind(JacksonJsonProvider.class).in(Singleton.class);
        bind(JacksonXMLProvider.class).in(Singleton.class);
        //--- Mapper Resolvers ---
        bind(ObjectMapperResolver.class);
        bind(XmlMapperResolver.class);

        //--- Converters ---
        bind(new TypeLiteral<Converter<SkillsPassport>>() {
        }).annotatedWith(XML.class).to(XmlConverter.class);
        bind(new TypeLiteral<Converter<SkillsPassport>>() {
        }).annotatedWith(JSON.class).to(JsonConverter.class);
        bind(new TypeLiteral<Converter<ModelContainer>>() {
        }).to(ModelContainerConverter.class);

        //Attachment Visitors
        bind(AttachmentVisitor.class).annotatedWith(ODT.class).to(ODTAttachmentVisitor.class);
        bind(AttachmentVisitor.class).annotatedWith(WORD.class).to(WORDAttachmentVisitor.class);
        bind(AttachmentVisitor.class).annotatedWith(PDF.class).to(PDFAttachmentVisitor.class);

        //Image Processing
        bind(ImageProcessing.class).to(ConcreteImageProcessing.class);

        //Allow static injections for fields of generation steps
        binder().requestStaticInjection(DefaultPreferencesStep.class);
        binder().requestStaticInjection(BytePreparationStep.class);
        binder().requestStaticInjection(AttachmentsVisibilityStep.class);

        // --- Guice Container and Filters --- 
        //WADL generation is enabled in Jersey by default. This means that OPTIONS methods are added by default to each resource and an auto-generated /application.wadl resource is deployed too. 
        //To override this default behavior and disable WADL generation in Jersey, we setup the configuration property "com.sun.jersey.config.feature.DisableWADL" to "true". 
        serve("/*").with(GuiceContainer.class, ImmutableMap.of("com.sun.jersey.config.feature.DisableWADL", "true"));

        //This is necessary when running in Tomcat, so that the Image IO plugins are resolved.
        ImageIO.scanForPlugins();
    }

    @Provides
    @Default
    public Locale defaultLocale() {
        return Locale.ENGLISH;
    }

    @Provides
    @Singleton
    @Named(UPLOAD_CV_ALLOWED_TYPES)
    List<MediaType> getUploadTypes() {
        if (uploadEspTypes == null) {
            uploadEspTypes = new ArrayList<MediaType>();
            uploadEspTypes.add(MediaType.APPLICATION_XML_TYPE);
            uploadEspTypes.add(MediaType.TEXT_XML_TYPE);
            uploadEspTypes.add(new MediaType("application", "pdf"));
            uploadEspTypes.add(new MediaType("application", "x-pdf"));
        }
        return uploadEspTypes;
    }

}
