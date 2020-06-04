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

import java.net.URISyntaxException;
import java.net.URL;
import java.util.List;
import java.util.Properties;

import org.hamcrest.CoreMatchers;
import org.joda.time.DateTime;
import org.junit.Assert;
import org.junit.Test;

import com.google.api.services.analytics.model.GaData;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Scopes;
import com.google.inject.name.Names;

import europass.ewa.database.guice.HibernateModule;
import europass.ewa.mail.MailSender;
import europass.ewa.modules.ExternalFileModule;
import europass.ewa.tools.ga.enums.HibernateTablesTypes;
import europass.ewa.tools.ga.executor.GAStepsExecutorVisitor;
import europass.ewa.tools.ga.executor.Visitor;
import europass.ewa.tools.ga.guice.GAStatisticsModule;
import europass.ewa.tools.ga.info.DateRange;
import europass.ewa.tools.ga.info.GAStatisticsInfo;
import europass.ewa.tools.ga.info.QueryInfo;
import europass.ewa.tools.ga.logger.DatabaseGAStatisticsLogger;
import europass.ewa.tools.ga.manager.DownloadsManager;
import europass.ewa.tools.ga.manager.VisitsManager;
import europass.ewa.tools.ga.manager.dao.DownloadsManagerDaoDBImpl;
import europass.ewa.tools.ga.manager.data.HibernateDownloads;
import europass.ewa.tools.ga.manager.data.HibernateVisits;
import europass.ewa.tools.utils.GAExecuteMailSenderImpl;
import europass.ewa.tools.utils.Utils;
import europass.webapps.tools.ga.GAMockObject.QueryPeriod;

import org.junit.Ignore;

@Ignore
public class GAProcessTest {

    private static GaData data;

    protected static Injector injector = null;
    protected final DatabaseGAStatisticsLogger logger;

    private static GAStatisticsInfo gaInfo;
    private static List<HibernateVisits> recordsVisitsList;
    private static List<HibernateDownloads> recordsDownloadsList;

    public GAProcessTest() throws URISyntaxException {

        Properties hp = new Properties();

        URL rc = GAProcessTest.class.getClassLoader().getResource("analytics.properties");
        String absolutePath = rc.getPath();

        injector = Guice.createInjector(
                new ExternalFileModule(absolutePath, ""),
                new HibernateModule(hp, Scopes.SINGLETON),
                new GAStatisticsModule(),
                new AbstractModule() {
            @Override
            protected void configure() {

                bindConstant().annotatedWith(
                        Names.named("europass-ewa-tools-ganalytics.mail.message.charset")).to("UTF-8");
                bindConstant().annotatedWith(
                        Names.named("europass-ewa-tools-ganalytics.mail.to.recipients")).to("");
                bindConstant().annotatedWith(
                        Names.named("europass-ewa-tools-ganalytics.mail.sender")).to("europass-team@instore.gr");
                bindConstant().annotatedWith(
                        Names.named("europass-ewa-tools-ganalytics.mail.smtp.server")).to("corfu.instore.gr");
                bindConstant().annotatedWith(
                        Names.named("europass-ewa-tools-ganalytics.mail.smtp.port")).to("25");
                bindConstant().annotatedWith(
                        Names.named("europass-ewa-tools-ganalytics.mail.smtp.user")).to("");
                bindConstant().annotatedWith(
                        Names.named("europass-ewa-tools-ganalytics.mail.smtp.password")).to("");
                bindConstant().annotatedWith(
                        Names.named("europass-ewa-tools-ganalytics.mail.smtp.ssl")).to("");
                bindConstant().annotatedWith(
                        Names.named("europass-ewa-tools-ganalytics.mail.smtp.tls")).to("");

                bindConstant().annotatedWith(
                        Names.named("europass-ewa-tools-ganalytics.ga.connection.timeout")).to("60000");
                bindConstant().annotatedWith(
                        Names.named("europass-ewa-tools-ganalytics.ga.read.timeout")).to("60000");
                bindConstant().annotatedWith(
                        Names.named("europass-ewa-tools-ganalytics.ga.retries.delay.base")).to("1");
                bindConstant().annotatedWith(
                        Names.named("europass-ewa-tools-ganalytics.ga.retries")).to("10");

                bind(MailSender.class).to(GAExecuteMailSenderImpl.class).asEagerSingleton();
                bind(Visitor.class).to(GAStepsExecutorVisitor.class);
            }
        }
        );

        logger = injector.getInstance(DatabaseGAStatisticsLogger.class);

        Assert.assertNotNull(logger);
    }

    // we use the new account for the day test
    @Test
    public void visitsConsumePeriodDay() {

        DateTime start = DateTime.now().withYear(2014).withMonthOfYear(2).withDayOfMonth(7);
        DateTime end = DateTime.now().withYear(2014).withMonthOfYear(2).withDayOfMonth(7);

        QueryInfo qInfo = GAMockObject.visitsQueryInfo(start, end);
        data = GAMockObject.visitsGData(GAMockObject.trackVisitsRequestWithDate(start, QueryPeriod.DAY));

        VisitsManager mgr = new VisitsManager(qInfo);
        mgr.consume(data);

        TestUtils.printVisitsRowsAndBeans(data, mgr, false);

        Assert.assertThat("Visits Data Object List equals to Sample GData size",
                mgr.getTableDao().getRecordsTotalVolume(), CoreMatchers.is(TestUtils.GaDataVisitsVolume(data)));

        recordsVisitsList = mgr.getTableDao().getAllRecords();

        Assert.assertThat("Visits Data Object List not Empty", recordsVisitsList.size() > 0, CoreMatchers.is(true));

        for (HibernateVisits bean : recordsVisitsList) {

            Assert.assertThat("Iso_country_code is not null", bean.getIso_country_code().length() > 0, CoreMatchers.is(true));
            Assert.assertThat("Year is 2014", bean.getYear(), CoreMatchers.is(2014));
            Assert.assertThat("Month is February", bean.getMonth(), CoreMatchers.is(2));
            Assert.assertThat("Day is 7", bean.getDay(), CoreMatchers.is(7));
            Assert.assertThat("Visits is set", bean.getVolume() > 0, CoreMatchers.is(true));

        }
    }

    @Test
    public void visitsConsumePeriodOddMonth() {

        // ODD MONTH
        DateTime start = DateTime.now().withYear(2014).withMonthOfYear(1).withDayOfMonth(1);
        DateTime end = DateTime.now().withYear(2014).withMonthOfYear(1).withDayOfMonth(31);

        QueryInfo qInfo = GAMockObject.visitsQueryInfo(start, end);
        data = GAMockObject.visitsGData(GAMockObject.trackVisitsRequestWithDate(start, QueryPeriod.DAY));

        VisitsManager mgr = new VisitsManager(qInfo);
        mgr.consume(data);

        TestUtils.printVisitsRowsAndBeans(data, mgr, false);

        Assert.assertThat("Visits Data Object List equals to Sample GData size",
                mgr.getTableDao().getRecordsTotalVolume(), CoreMatchers.is(TestUtils.GaDataVisitsVolume(data)));

        recordsVisitsList = mgr.getTableDao().getAllRecords();

        Assert.assertThat("Visits Data Object List not Empty", recordsVisitsList.size() > 0, CoreMatchers.is(true));

        for (HibernateVisits bean : recordsVisitsList) {

            Assert.assertThat("Iso_country_code is not null", bean.getIso_country_code().length() > 0, CoreMatchers.is(true));
            Assert.assertThat("Year is 2014", bean.getYear(), CoreMatchers.is(2014));
            Assert.assertThat("Month is 1", bean.getMonth(), CoreMatchers.is(1));
            Assert.assertThat("Day is greater or equal to 1", bean.getDay() >= 1, CoreMatchers.is(true));
            Assert.assertThat("Day is less or equal to 31", bean.getDay() <= 31, CoreMatchers.is(true));
            Assert.assertThat("Visits is set", bean.getVolume() > 0, CoreMatchers.is(true));
        }
    }

    @Test
    public void visitsConsumePeriodEvenMonth() {

        DateTime start = DateTime.now().withYear(2013).withMonthOfYear(12).withDayOfMonth(1);
        DateTime end = DateTime.now().withYear(2013).withMonthOfYear(12).withDayOfMonth(31);

        QueryInfo qInfo = GAMockObject.visitsQueryInfo(start, end);
        data = GAMockObject.visitsGData(GAMockObject.trackVisitsRequestWithDate(start, QueryPeriod.MONTH));

        VisitsManager mgr = new VisitsManager(qInfo);
        mgr.consume(data);

        TestUtils.printVisitsRowsAndBeans(data, mgr, false);

        Assert.assertThat("Downloads Data Object List equals to Sample GData size",
                mgr.getTableDao().getRecordsTotalVolume(), CoreMatchers.is(TestUtils.GaDataVisitsVolume(data)));

        recordsVisitsList = mgr.getTableDao().getAllRecords();

        Assert.assertThat("Visits Data Object List not Empty", recordsVisitsList.size() > 0, CoreMatchers.is(true));

        for (HibernateVisits bean : recordsVisitsList) {

            Assert.assertThat("Iso_country_code is not null", bean.getIso_country_code().length() > 0, CoreMatchers.is(true));
            Assert.assertThat("Year is 2013", bean.getYear(), CoreMatchers.is(2013));
            Assert.assertThat("Month is 12", bean.getMonth(), CoreMatchers.is(12));
            Assert.assertThat("Day is greater or equal to 1", bean.getDay() >= 1, CoreMatchers.is(true));
            Assert.assertThat("Day is less or equal to 31", bean.getDay() <= 31, CoreMatchers.is(true));
            Assert.assertThat("Visits is set", bean.getVolume() > 0, CoreMatchers.is(true));
        }
    }

    // we use the new account for the day test
    @Test
    public void visitsConsumePeriodYear() {

        DateTime start = DateTime.now().withYear(2013).withMonthOfYear(1).withDayOfMonth(1);
        DateTime end = DateTime.now().withYear(2013).withMonthOfYear(12).withDayOfMonth(31);

        QueryInfo qInfo = GAMockObject.visitsQueryInfo(start, end);
        data = GAMockObject.visitsGData(GAMockObject.trackVisitsRequestWithDate(start, QueryPeriod.YEAR));

        VisitsManager mgr = new VisitsManager(qInfo);
        mgr.consume(data);

        TestUtils.printVisitsRowsAndBeans(data, mgr, false);

        Assert.assertThat("Downloads Data Object List equals to Sample GData size",
                mgr.getTableDao().getRecordsTotalVolume(), CoreMatchers.is(TestUtils.GaDataVisitsVolume(data)));

        recordsVisitsList = mgr.getTableDao().getAllRecords();

        Assert.assertThat("Visits Data Object List not Empty", recordsVisitsList.size() > 0, CoreMatchers.is(true));

        for (HibernateVisits bean : recordsVisitsList) {

            Assert.assertThat("Iso_country_code is not null", bean.getIso_country_code().length() > 0, CoreMatchers.is(true));
            Assert.assertThat("Year is 2013", bean.getYear(), CoreMatchers.is(2013));
            Assert.assertThat("Visits is set", bean.getVolume() > 0, CoreMatchers.is(true));
        }
    }

    @Test
    public void downloadsConsumePeriodDay() {

        DateTime start = DateTime.now().withYear(2013).withMonthOfYear(11).withDayOfMonth(15);
        DateTime end = DateTime.now().withYear(2013).withMonthOfYear(11).withDayOfMonth(15);

        QueryInfo qInfo = GAMockObject.donwloadsQueryInfo(start, end);
        data = GAMockObject.donwloadsGData(GAMockObject.trackEventRequestSampleWithDate(start, QueryPeriod.DAY));

        DownloadsManager mgr = new DownloadsManager(qInfo);
        mgr.consume(data);

        TestUtils.printEventRowsAndBeans(data, mgr, false);

        Assert.assertThat("Downloads Data Object List equals to Sample GData size",
                mgr.getTableDao().getRecordsTotalVolume(), CoreMatchers.is(TestUtils.GaDataDownloadsVolume(data)));

        recordsDownloadsList = mgr.getTableDao().getAllRecords();

        Assert.assertThat("Downloads Data Object List not Empty", recordsDownloadsList.size() > 0, CoreMatchers.is(true));

    }

    @Test
    public void downloadsConsumePeriodOddMonth() {

        // ODD MONTH
        DateTime start = DateTime.now().withYear(2014).withMonthOfYear(1).withDayOfMonth(1);
        DateTime end = DateTime.now().withYear(2014).withMonthOfYear(1).withDayOfMonth(31);

        QueryInfo qInfo = GAMockObject.donwloadsQueryInfo(start, end);
        data = GAMockObject.donwloadsGData(GAMockObject.trackEventRequestSampleWithDate(start, QueryPeriod.MONTH));

        DownloadsManager mgr = new DownloadsManager(qInfo);
        mgr.consume(data);

        TestUtils.printEventRowsAndBeans(data, mgr, false);

        Assert.assertThat("Downloads Data Object List equals to Sample GData size",
                mgr.getTableDao().getRecordsTotalVolume(), CoreMatchers.is(TestUtils.GaDataDownloadsVolume(data)));

        recordsDownloadsList = mgr.getTableDao().getAllRecords();

        Assert.assertThat("Downloads Data Object List not Empty", recordsDownloadsList.size() > 0, CoreMatchers.is(true));

    }

    @Test
    public void downloadsConsumePeriodEvenMonth() {

        // EVEN MONTH
        DateTime start = DateTime.now().withYear(2013).withMonthOfYear(12).withDayOfMonth(1);
        DateTime end = DateTime.now().withYear(2013).withMonthOfYear(12).withDayOfMonth(31);

        QueryInfo qInfo = GAMockObject.donwloadsQueryInfo(start, end);
        data = GAMockObject.donwloadsGData(GAMockObject.trackEventRequestSampleWithDate(start, QueryPeriod.MONTH));

        DownloadsManager mgr = new DownloadsManager(qInfo);
        mgr.consume(data);

        TestUtils.printEventRowsAndBeans(data, mgr, false);

        Assert.assertThat("Downloads Data Object List equals to Sample GData size",
                mgr.getTableDao().getRecordsTotalVolume(), CoreMatchers.is(TestUtils.GaDataDownloadsVolume(data)));

        recordsDownloadsList = mgr.getTableDao().getAllRecords();

        Assert.assertThat("Downloads Data Object List not Empty", recordsDownloadsList.size() > 0, CoreMatchers.is(true));

    }

    // we use the new account for the day test
    @Test
    public void downloadsConsumePeriodYear() {

        DateTime start = DateTime.now().withYear(2013).withMonthOfYear(12).withDayOfMonth(1);
        DateTime end = DateTime.now().withYear(2013).withMonthOfYear(12).withDayOfMonth(31);

        QueryInfo qInfo = GAMockObject.donwloadsQueryInfo(start, end);
        data = GAMockObject.donwloadsGData(GAMockObject.trackEventRequestSampleWithDate(start, QueryPeriod.MONTH));

        DownloadsManager mgr = new DownloadsManager(qInfo);
        mgr.consume(data);

        TestUtils.printEventRowsAndBeans(data, mgr, false);

        Assert.assertThat("Downloads Data Object List equals to Sample GData size",
                mgr.getTableDao().getRecordsTotalVolume(), CoreMatchers.is(TestUtils.GaDataDownloadsVolume(data)));

        recordsDownloadsList = mgr.getTableDao().getAllRecords();

        Assert.assertThat("Downloads Data Object List not Empty", recordsDownloadsList.size() > 0, CoreMatchers.is(true));

    }

    @Test
    public void listCountriesIsoCodesTest() {

        Assert.assertThat("ISO table for 'Greece' is 'EL'", Utils.getCountryIsoCode("Greece"), CoreMatchers.is("EL"));
        Assert.assertThat("ISO table for 'United Kingdom' is 'UK'", Utils.getCountryIsoCode("United Kingdom"), CoreMatchers.is("UK"));
        Assert.assertThat("ISO table for 'Bolivia, Plurinational State of' is 'BO'", Utils.getCountryIsoCode("Bolivia, Plurinational State of"), CoreMatchers.is("BO"));
        Assert.assertThat("There no ISO table for 'Uknown Origin'", Utils.getCountryIsoCode("Uknown Origin"), CoreMatchers.is("Uknown Origin"));
    }

    public void downloadsInvalidPersistPeriodDay() throws RuntimeException {

        String[] configs = {"/"};
        String[] dates = {"2013", "11", "1"};
        String[] tables = {"downloads"};

        DateTime yesterday = new DateTime().minusDays(1);

        gaInfo = TestUtils.processPersistTest(configs, dates, tables, "");

        DownloadsManagerDaoDBImpl downloadsMgrDao = (DownloadsManagerDaoDBImpl) gaInfo.getTableManagers().get(0).getTableDao();
        recordsDownloadsList = downloadsMgrDao.getAllRecords();

        Assert.assertThat("Downloads Data Object List is Empty", recordsDownloadsList.size() > 0, CoreMatchers.is(true));

        Assert.assertThat("Downloads Data Object List equals to records stored",
                recordsDownloadsList.size(), CoreMatchers.is(logger.countTablePeriodDate("HibernateDownloads", yesterday.getYear(), yesterday.getMonthOfYear(), yesterday.getDayOfMonth())));

        // remove entries
        logger.removeFromTablePeriodDate("HibernateDownloads", yesterday.getYear(), yesterday.getMonthOfYear(), yesterday.getDayOfMonth());
    }

    @Test
    public void consumePeriodDownloadsEvent() {

        DateTime start = DateTime.now().withYear(2013).withMonthOfYear(5).withDayOfMonth(1);
        DateTime end = DateTime.now().withYear(2013).withMonthOfYear(5).withDayOfMonth(31);

        QueryInfo qInfo = GAMockObject.donwloadsQueryInfo(start, end);
        data = GAMockObject.donwloadsGData(GAMockObject.trackEventRequestSample(25));

        DownloadsManager mgr = new DownloadsManager(qInfo);
        mgr.consume(data);

        TestUtils.printEventRowsAndBeans(data, mgr, false);

        Assert.assertThat("Downloads Data Object List equals to Sample GData size",
                mgr.getTableDao().getRecordsTotalVolume(), CoreMatchers.is(TestUtils.GaDataDownloadsVolume(data)));
    }

    @Test
    public void consumeAndStorePeriodDownloadsEventWithFailedInserts() {

        DateTime start = DateTime.now().withYear(2013).withMonthOfYear(5).withDayOfMonth(1);
        DateTime end = DateTime.now().withYear(2013).withMonthOfYear(5).withDayOfMonth(31);

        QueryInfo qInfo = GAMockObject.donwloadsQueryInfo(start, end);

        DownloadsManager mgr = new DownloadsManager(qInfo);
        mgr.consume(GAMockObject.donwloadsGData(GAMockObject.trackEventRequestWithInvalidData(start)));

        logger.log(HibernateTablesTypes.HibernateDownloads, mgr.getTableDao().getAllRecords(), new DateRange(qInfo));

        Assert.assertThat("Downloads Data Object List equlas to records stored",
                (mgr.getTableDao().getAllRecords().size() - 2), CoreMatchers.is(logger.countTablePeriodDate("HibernateDownloads", 2013, 5)));

        // remove entries as they are random and cannot be used again for testing
        logger.removeFromTablePeriodDate("HibernateDownloads", 2013, 5);
    }
}
