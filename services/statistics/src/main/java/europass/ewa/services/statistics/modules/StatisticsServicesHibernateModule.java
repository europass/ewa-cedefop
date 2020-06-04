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

import org.hibernate.cfg.Configuration;

import com.google.inject.Singleton;

import europass.ewa.database.guice.AbstractModelModule;
import europass.ewa.database.guice.HibernateConfigurator;
import europass.ewa.services.statistics.hibernate.HibernateStatisticsServicesFetcher;
import europass.ewa.services.statistics.hibernate.StatisticsServicesFetcher;
import europass.ewa.services.statistics.hibernate.data.*;

public class StatisticsServicesHibernateModule extends AbstractModelModule implements HibernateConfigurator {

	@Override
	public void configure() {

		super.configure(); //MUST call parent class

		// CAUTION SINGLETON: a Singleton makes sure that only one database connection is made or
		// that only one thread can access the connection at a time
		bind(StatisticsServicesFetcher.class).to(HibernateStatisticsServicesFetcher.class).in(Singleton.class);
	}

	@Override
	public void configure(Configuration config) {

		config.addPackage("europass.ewa.services.statistics.hibernate.data");
		//Annotated Classes
		config.addAnnotatedClass(CubeEntryDocs.class);
		config.addAnnotatedClass(CubeEntryDocsLangs.class);
		config.addAnnotatedClass(CubeEntryWorkExp.class);
		config.addAnnotatedClass(CubeEntryAge.class);
		config.addAnnotatedClass(CubeEntryGender.class);
		config.addAnnotatedClass(CubeEntryFLangCounter.class);
		config.addAnnotatedClass(CubeEntryFLangPivot.class);
		config.addAnnotatedClass(CubeEntryShort.class);
		config.addAnnotatedClass(CubeEntryNat.class);
		config.addAnnotatedClass(CubeEntryNatRank.class);
		config.addAnnotatedClass(CubeEntryNatFLang.class);
		config.addAnnotatedClass(CubeEntryNatLang.class);
		config.addAnnotatedClass(CubeEntryNatMLang.class);
		config.addAnnotatedClass(CubeEntryMlang.class);
		config.addAnnotatedClass(CubeEntryFlang.class);
		config.addAnnotatedClass(CubeEntryLangs.class);
		config.addAnnotatedClass(CubeEntryFlangShort.class);
		config.addAnnotatedClass(CubeEntry.class);
		config.addAnnotatedClass(CubeEntryEmailHash.class);
		config.addAnnotatedClass(StatDownloads.class);
		config.addAnnotatedClass(StatVisits.class);
		config.addAnnotatedClass(IsoCountry.class);
		config.addAnnotatedClass(IsoNationality.class);
	}

}
