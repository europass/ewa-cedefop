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
package europass.ewa.statistics.guice;

import org.hibernate.cfg.Configuration;

import com.google.inject.Singleton;

import europass.ewa.database.guice.AbstractModelModule;
import europass.ewa.database.guice.HibernateConfigurator;
import europass.ewa.statistics.DatabaseStatisticsLogger;
import europass.ewa.statistics.StatisticsLogger;
import europass.ewa.statistics.data.StatsAchievement;
import europass.ewa.statistics.data.StatsDetails;
import europass.ewa.statistics.data.StatsEducation;
import europass.ewa.statistics.data.StatsLinguisticCertificate;
import europass.ewa.statistics.data.StatsWorkExperience;
import europass.ewa.statistics.data.StatsEntry;
import europass.ewa.statistics.data.StatsForeignLanguages;
import europass.ewa.statistics.data.StatsMotherLanguage;
import europass.ewa.statistics.data.StatsNationality;

public class StatisticsModule extends AbstractModelModule implements HibernateConfigurator {

    @Override
    public void configure() {

        super.configure(); //MUST call parent class

        // CAUTION SINGLETON: a Singleton makes sure that only one database connection is made or
        // that only one thread can access the connection at a time
        bind(StatisticsLogger.class).to(DatabaseStatisticsLogger.class).in(
                Singleton.class);
    }

    @Override
    public void configure(Configuration config) {

        config.addPackage("europass.ewa.statistics.data");

        //Annotated Classes
        config.addAnnotatedClass(StatsEntry.class);
        config.addAnnotatedClass(StatsMotherLanguage.class);
        config.addAnnotatedClass(StatsNationality.class);
        config.addAnnotatedClass(StatsForeignLanguages.class);
        config.addAnnotatedClass(StatsWorkExperience.class);
        config.addAnnotatedClass(StatsEducation.class);
        config.addAnnotatedClass(StatsAchievement.class);
        config.addAnnotatedClass(StatsLinguisticCertificate.class);
        config.addAnnotatedClass(StatsDetails.class);
    }

}
