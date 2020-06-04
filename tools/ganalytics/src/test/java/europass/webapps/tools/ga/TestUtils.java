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
package europass.webapps.tools.ga;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.joda.time.DateTime;
import org.junit.Assert;

import com.google.api.services.analytics.model.GaData;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Scopes;

import europass.ewa.database.guice.HibernateModule;
import europass.ewa.tools.ga.guice.GAStatisticsModule;
import europass.ewa.tools.ga.info.GAStatisticsInfo;
import europass.ewa.tools.ga.logger.DatabaseGAStatisticsLogger;
import europass.ewa.tools.ga.manager.DownloadsManager;
import europass.ewa.tools.ga.manager.VisitsManager;
import europass.ewa.tools.ga.manager.data.HibernateDownloads;
import europass.ewa.tools.ga.manager.data.HibernateVisits;
import europass.ewa.tools.ga.process.GAStatisticsProcess;
import europass.ewa.tools.utils.Utils;

public class TestUtils {

    public static GAStatisticsInfo processBeanTest(String[] configs, String[] dates, String[] tables, String propsPrefix) throws RuntimeException {

        List<List<String>> arguments = TestUtils.constructArguments(configs, dates, tables);

        Properties props = Utils.getProperties(propsPrefix + "analytics.properties", false, TestUtils.class);

        GAStatisticsInfo gaInfo = new GAStatisticsInfo(
                props.getProperty("application.name"),
                props.getProperty("application.auth.key.path"),
                props.getProperty("application.auth.user"),
                props.getProperty("application.auth.client.secret"));

        final Injector injector = Guice.createInjector(
                new GAStatisticsModule()
        );

        GAStatisticsProcess process = injector.getInstance(GAStatisticsProcess.class);
        process.process(gaInfo, arguments);

        return gaInfo;
    }

    public static GAStatisticsInfo processPersistTest(String[] configs, String[] dates, String[] tables, String propsPrefix) throws RuntimeException {

        Properties hp = new Properties();
        final Injector injector = Guice.createInjector(
                new HibernateModule(hp, Scopes.SINGLETON),
                new GAStatisticsModule()
        );

        DatabaseGAStatisticsLogger logger = injector.getInstance(DatabaseGAStatisticsLogger.class);

        Assert.assertNotNull(logger);

        List<List<String>> arguments = TestUtils.constructArguments(configs, dates, tables);

        Properties props = Utils.getProperties(propsPrefix + "analytics.properties", false, TestUtils.class);

        GAStatisticsInfo gaInfo = new GAStatisticsInfo(props.getProperty("application.name"),
                props.getProperty("application.auth.key.path"),
                props.getProperty("application.auth.user"),
                props.getProperty("application.auth.client.secret"));

        GAStatisticsProcess process = injector.getInstance(GAStatisticsProcess.class);
        process.process(gaInfo, arguments);

        return gaInfo;
    }

    public static void printEventRowsAndBeans(GaData data, DownloadsManager mgr, boolean printRows) {

        System.out.println("Downloads List (Elements " + data.getRows().size() + ", Total Volume " + GaDataDownloadsVolume(data) + "):");

        if (printRows) {
            for (List<String> row : data.getRows()) {

                System.out.print("_trackEvent > ");
                System.out.print("category: " + row.get(0) + ", ");
                System.out.print("action: " + row.get(1) + ", ");
                System.out.print("label: " + row.get(2) + ", ");
                System.out.print("year: " + row.get(3) + ", ");
                System.out.print("month: " + row.get(4) + ", ");
                System.out.print("day: " + row.get(5) + ", ");
                System.out.println("value: " + row.get(6));
            }
        }

        System.out.println("Beans List (Elements " + mgr.getTableDao().getAllRecords().size() + ", Total Volume " + mgr.getTableDao().getRecordsTotalVolume() + "):");

        if (printRows) {
            for (HibernateDownloads bean : mgr.getTableDao().getAllRecords()) {

                System.out.print("Bean > ");

                System.out.print("Document: " + bean.getDocument() + ", ");
                System.out.print("Type: " + bean.getType() + ", ");
                System.out.print("ISO language: " + bean.getIso_language_code() + ", ");
                System.out.print("ISO Country: " + bean.getIso_country_code() + ", ");
                System.out.print("Date: " + bean.getDay() + "/" + bean.getMonth() + "/" + bean.getYear() + ", ");
                System.out.println("Volume: " + bean.getVolume());
            }
        }
    }

    public static void printVisitsRowsAndBeans(GaData data, VisitsManager mgr, boolean printRows) {

        System.out.println("Visits List (Elements " + data.getRows().size() + ", Total Volume " + GaDataVisitsVolume(data) + "):");

        if (printRows) {
            for (List<String> row : data.getRows()) {

                System.out.print("visits > ");
                System.out.print("country: " + row.get(0) + ", ");
                System.out.print("year: " + row.get(1) + ", ");
                System.out.print("month: " + row.get(2) + ", ");
                System.out.print("day: " + row.get(3) + ", ");
                System.out.println("value: " + row.get(4));
            }
        }

        System.out.println("Beans List (Elements " + mgr.getTableDao().getAllRecords().size() + ", Total Volume " + mgr.getTableDao().getRecordsTotalVolume() + "):");

        if (printRows) {
            for (HibernateVisits bean : mgr.getTableDao().getAllRecords()) {

                System.out.print("Bean > ");
                System.out.print("country: " + bean.getIso_country_code() + ", ");
                System.out.print("Date: " + bean.getDay() + "/" + bean.getMonth() + "/" + bean.getYear() + ", ");
                System.out.println("Volume: " + bean.getVolume());
            }
        }
    }

    public static int GaDataVisitsVolume(GaData gaData) {

        List<List<String>> results = gaData.getRows();

        int volume = 0;

        for (List<String> row : results) {

            volume += Integer.valueOf(row.get(4));

        }

        return volume;
    }

    public static int GaDataDownloadsVolume(GaData gaData) {

        List<List<String>> results = gaData.getRows();

        int volume = 0;

        for (List<String> row : results) {

            volume += Integer.valueOf(row.get(6));

        }

        return volume;
    }

    private static List<List<String>> constructArguments(String[] config, String[] date, String[] tables) {

        List<List<String>> userArgsList = new ArrayList<List<String>>();

        List<String> configs = new ArrayList<String>();

        // get absolute path from resources folder
        URL resourceURL = TestUtils.class.getResource(config[0]);
        String path = (new File(resourceURL.getFile())).getAbsolutePath();
        configs.add(path);

        userArgsList.add(configs);

        DateTime now = new DateTime();

        List<String> dates = new ArrayList<String>();

        if (date.length > 0) {
            dates.add(date[0]);

            if (date.length > 1) {
                dates.add(date[1]);

                if (date.length > 2) {
                    dates.add(date[2]);
                }
            }
        } else {
            now.minusDays(1);
            dates.add("" + now.getYear());
            dates.add("" + now.getMonthOfYear());
            dates.add("" + now.getDayOfMonth());
        }

        userArgsList.add(dates);

        List<String> tableNames = new ArrayList<String>();
        if (tables.length > 0) {
            for (String table : tables) {
                tableNames.add(table);
            }
        }

        userArgsList.add(tableNames);

        return userArgsList;
    }
}
