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
package europass.ewa.services.rest.modules;

import com.google.inject.Singleton;
import com.google.inject.TypeLiteral;
import com.google.inject.servlet.ServletModule;

import europass.ewa.model.SkillsPassport;
import europass.ewa.services.annotation.BackwardCompatibility;
import europass.ewa.services.annotation.RestApi;
import europass.ewa.services.conversion.model.ExportableModelFactory;
import europass.ewa.services.conversion.process.DocumentGeneration;
import europass.ewa.services.conversion.process.RemoteDocumentGeneration;
import europass.ewa.services.rest.exception.ApiExceptionMapper;
import europass.ewa.services.rest.exception.GenericExceptionMapper;
import europass.ewa.services.rest.model.BackwardCompatibleModelFactory;
import europass.ewa.services.rest.model.RemoteExportableModelFactory;
import europass.ewa.services.rest.resources.ExtractDocumentResource;
import europass.ewa.services.rest.resources.RemoteDocumentResource;
import europass.ewa.services.rest.resources.SupportedLanguagesResource;
import europass.ewa.services.rest.resources.XMLBackwardCompatibilityResource;

public class RestServicesModule extends ServletModule {

    @Override
    protected void configureServlets() {

        //----- Exception Mapper
        bind(ApiExceptionMapper.class);
        bind(GenericExceptionMapper.class);

        //Exportable Model Factory
        bind(new TypeLiteral<ExportableModelFactory<SkillsPassport>>() {
        }).annotatedWith(RestApi.class).to(RemoteExportableModelFactory.class).in(Singleton.class);
        bind(new TypeLiteral<ExportableModelFactory<String>>() {
        }).annotatedWith(BackwardCompatibility.class).to(BackwardCompatibleModelFactory.class).in(Singleton.class);

        //Orchestration of steps
        bind(DocumentGeneration.class).annotatedWith(RestApi.class).to(RemoteDocumentGeneration.class);

        //--- Rest API Document	
        bind(ExtractDocumentResource.class);
        bind(RemoteDocumentResource.class);
        bind(SupportedLanguagesResource.class);
        bind(XMLBackwardCompatibilityResource.class);

    }

}
