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
package europass.ewa.tools.ga.guice;

import org.hibernate.cfg.Configuration;

import com.google.inject.Singleton;

import europass.ewa.database.guice.AbstractModelModule;
import europass.ewa.database.guice.HibernateConfigurator;
import europass.ewa.tools.ga.logger.DatabaseGAStatisticsLogger;
import europass.ewa.tools.ga.logger.GAStatisticsLogger;
import europass.ewa.tools.ga.manager.data.HibernateDownloads;
import europass.ewa.tools.ga.manager.data.HibernateVisits;

public class GAStatisticsModule extends AbstractModelModule implements HibernateConfigurator{

	@Override
	public void configure(){
		
		super.configure(); //MUST call parent class
		
		// CAUTION SINGLETON: a Singleton makes sure that only one database connection is made or
		// that only one thread can access the connection at a time
		bind(GAStatisticsLogger.class).to(DatabaseGAStatisticsLogger.class).in(Singleton.class);
	}
	
	@Override
	public void configure(Configuration config) {

		config.addPackage("europass.ewa.tools.model");
		//Annotated Classes
		config.addAnnotatedClass(HibernateVisits.class);
		config.addAnnotatedClass(HibernateDownloads.class);
	}
	
}
