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
package europass.ewa.services.statistics.modules;

import com.google.inject.Singleton;
import com.google.inject.servlet.ServletModule;
import com.sun.jersey.guice.spi.container.servlet.GuiceContainer;

import europass.ewa.services.statistics.api.info.QueryInfo;
import europass.ewa.services.statistics.api.process.StatisticsApiRequestProcess;
import europass.ewa.services.statistics.mappings.structures.ParameterValueTypesMappings;
import europass.ewa.services.statistics.mappings.structures.QueryPrefixParameterMappings;
import europass.ewa.services.statistics.mappings.structures.caching.CachedQueries;
import europass.ewa.services.statistics.parser.GenericParser;
import europass.ewa.services.statistics.parser.ParametersParser;
import europass.ewa.services.statistics.resources.StatisticsAPIResource;
import europass.ewa.services.statistics.validators.factory.ValidatorFactory;
import europass.ewa.services.statistics.validators.factory.ValidatorFactoryImpl;

public class StatisticsServicesModule extends ServletModule{

	@Override
	protected void configureServlets() {
	
		bind(GuiceContainer.class).in(Singleton.class);

		// Singletons
		
		bind(QueryInfo.class);
		
		bind(ParameterValueTypesMappings.class).in(Singleton.class);
		bind(QueryPrefixParameterMappings.class).in(Singleton.class);
//		bind(QueryEntityMatchings.class).in(Singleton.class);
		
		bind(ValidatorFactory.class).to(ValidatorFactoryImpl.class).asEagerSingleton();
		bind(GenericParser.class).to(ParametersParser.class).asEagerSingleton();
		
		bind(StatisticsApiRequestProcess.class);
		
		bind(CachedQueries.class).in(Singleton.class);
		
		//Html Feedback
//		binder().requestStaticInjection(HtmlResponseReporting.class);
		
		//--- EXCEPTION AND FEEDBACK
		//----- Html Wrapper
//		bind( HtmlWrapper.class ).annotatedWith(EWAEditor.class).to(SessionAwareWithBodyHtmlWrapper.class);
		
		//----- Exception Mapper
//		bind(ApiExceptionMapper.class);
//		bind(GenericExceptionMapper.class);
		
		
		
		//--- Rest API Document	
		bind( StatisticsAPIResource.class );
		
		serve("/*").with(GuiceContainer.class);
		
	}
}
