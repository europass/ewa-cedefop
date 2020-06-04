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

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.joda.time.DateTime;

import com.google.api.services.analytics.model.GaData;
import com.google.api.services.analytics.model.GaData.Query;

import europass.ewa.tools.ga.info.QueryInfo;

public class GAMockObject {

    // Used for event tracking
    final static String[] ecvDocumentType = {"CV_Examples", "CV_Templates", "CV_Instructions", "ECV_Examples", "ECV_Templates", "ECV_Instructions"};

    final static String[] elpDocumentType = {"LP_Examples", "LP_Templates", "LP_Instructions", "ELP_Examples", "ELP_Templates", "ELP_Instructions"};

    final static String[] emDocumentType = {"EM_Examples"};

    final static String[] csDocumentType = {"CS_Examples", "ECS_Examples"};

    final static String[] edsDocumentType = {"DS_Examples", "EDS_Examples"};

    final static String[] isoLanguageCountry = {"en_CY", "de_FR", "en_UK", "nl", "it", "el_UK", "ga", "pl_AT", "fr", "es_PO"};

    final static String[] ipCountry = {"CY", "FR", "UK", "ES", "CH", "US", "ME", "KO", "EL", "SW"};

    final static String truncationOnIsoLanguageCode = "invalidLanguageinvalidLanguageinvalidLanguageinvalidLanguage_XX";

    public static enum QueryPeriod {
        YEAR,
        MONTH,
        DAY
    }

    // Used for visits	
    // Event downloadsMetrics
    private static final String DOWNLOADS_METRICS = "ga:totalEvents";
    private static final String DOWNLOADS_DIMENSIONS = "ga:eventCategory,ga:eventAction,ga:eventLabel,ga:year,ga:month,ga:day";

    // Event downloadsMetrics
    private static final String VISITS_METRICS = "ga:visits";
    private static final String VISITS_DIMENSIONS = "ga:country,ga:year,ga:month,ga:day";
    private static final String VISITS_SORT = "ga:year,ga:month,ga:day,ga:country";

    public static QueryInfo donwloadsQueryInfo(DateTime start, DateTime end) {

        QueryInfo qInfo = new QueryInfo();

        qInfo.setStartDate(start);
        qInfo.setEndDate(end);

        qInfo.setMetrics(DOWNLOADS_METRICS);
        qInfo.setDimensions(DOWNLOADS_DIMENSIONS);

        return qInfo;
    }

    public static QueryInfo visitsQueryInfo(DateTime start, DateTime end) {

        QueryInfo qInfo = new QueryInfo();

        qInfo.setStartDate(start);
        qInfo.setEndDate(end);

        qInfo.setMetrics(VISITS_METRICS);
        qInfo.setDimensions(VISITS_DIMENSIONS);
        qInfo.setSort(VISITS_SORT);

        return qInfo;

    }

    public static GaData donwloadsGData(List<List<String>> rows) {

        GaData data = new GaData();
        data.setRows(rows);
        data.setQuery(new Query());

        List<String> Metrics = new ArrayList<String>();
        Metrics.add(DOWNLOADS_METRICS);

        data.getQuery().setMetrics(Metrics);
        data.getQuery().setDimensions(DOWNLOADS_DIMENSIONS);

        return data;
    }

    public static GaData visitsGData(List<List<String>> rows) {

        GaData data = new GaData();
        data.setRows(rows);
        data.setQuery(new Query());

        List<String> metrics = new ArrayList<String>();
        metrics.add(VISITS_METRICS);

        data.getQuery().setMetrics(metrics);
        data.getQuery().setDimensions(VISITS_DIMENSIONS);

        List<String> sort = new ArrayList<String>();
        sort.add(VISITS_SORT);

        data.getQuery().setSort(sort);

        return data;
    }

    public static List<List<String>> trackEventRequestWithInvalidData(DateTime start) {

        List<List<String>> list = new ArrayList<List<String>>();

        // Construct six rows of ga data of which two consecutive rows are cause data truncation
        for (int i = 0; i < 6; i++) {

            List<String> row = new ArrayList<String>();

            row.add(ecvDocumentType[i]);

            if (i == 2 || i == 3) {
                row.add(truncationOnIsoLanguageCode);
            } else {
                row.add(isoLanguageCountry[i]);
            }

            row.add(ipCountry[i]);

            // Date
            row.add("" + start.getYear());
            row.add("" + start.getMonthOfYear());
            row.add("" + start.getDayOfMonth());
            start.plusDays(1);

            // Volume
            row.add("" + new Random().nextInt(150) + 1);

            list.add(row);

        }

        return list;

    }

    public static List<List<String>> trackEventRequestWithDate(DateTime startDate, QueryPeriod period) {

        String[] document_type
                = {"CV_Examples", "LP_Instructions", "DS_Examples", "CS_Examples", "CV_Templates", "EDS_Examples", "DS_Examples", "ECV_Instructions", "ELP_Instructions", "LP_Instructions"};

        List<List<String>> list = new ArrayList<List<String>>();

        for (int i = 0; i < 10; i++) {

            List<String> items = new ArrayList<String>();

            items.add(document_type[i]);

            items.add(isoLanguageCountry[i]);
            items.add(ipCountry[i]);

            items.add("" + startDate.getYear());

            if (period.equals(QueryPeriod.YEAR)) {

                items.add("" + new Random().nextInt(11) + 1);
                // for testing purposes we avoid complex checks for dates
                items.add("" + new Random().nextInt(27) + 1);

            } else if (period.equals(QueryPeriod.MONTH)) {

                items.add("" + startDate.getMonthOfYear());
                items.add("" + new Random().nextInt(27) + 1);
            } else {
                items.add("" + startDate.getMonthOfYear());
                items.add("" + startDate.getDayOfMonth());
            }

            items.add("" + new Random().nextInt(150) + 1);

            if (list.indexOf(items) == -1) {
                list.add(items);
            }
        }

        return list;
    }

    public static List<List<String>> trackVisitsRequestWithDate(DateTime date, QueryPeriod period) {

        List<List<String>> list = new ArrayList<List<String>>();

        String[] month = {"1", "2", "3", "4", "5", "7", "8", "9", "11", "12"};
        String[] day = {"5", "12", "13", "18", "9", "20", "31", "22", "6", "17"};

        for (int i = 0; i < 10; i++) {

            List<String> items = new ArrayList<String>();

            items.add(ipCountry[i]);

            items.add("" + date.getYear());

            if (period.equals(QueryPeriod.YEAR)) {

                items.add("" + month[i]);
                items.add("" + day[1]);

            } else if (period.equals(QueryPeriod.MONTH)) {

                items.add("" + date.getMonthOfYear());
                items.add("" + day[1]);
            } else {
                items.add("" + date.getMonthOfYear());
                items.add("" + date.getDayOfMonth());
            }

            items.add("" + new Random().nextInt(150) + 1);

            if (list.indexOf(items) == -1) {
                list.add(items);
            }
        }

        return list;
    }

    public static List<List<String>> trackEventRequestSampleWithDate(DateTime date, QueryPeriod period) {

        String[] document_type
                = {"CV_Examples", "LP_Instructions", "DS_Examples", "CS_Examples", "CV_Templates", "EDS_Examples", "DS_Examples", "ECV_Instructions", "ELP_Instructions", "LP_Instructions"};

        List<List<String>> list = new ArrayList<List<String>>();

        String[] month = {"1", "2", "3", "4", "5", "7", "8", "9", "11", "12"};
        String[] day = {"5", "12", "13", "18", "9", "20", "31", "22", "6", "17"};

        for (int i = 0; i < 10; i++) {

            List<String> items = new ArrayList<String>();

            items.add(document_type[i]);

            items.add(isoLanguageCountry[i]);
            items.add(ipCountry[i]);

            items.add("" + date.getYear());

            if (period.equals(QueryPeriod.YEAR)) {

                items.add("" + month[i]);
                items.add("" + day[1]);

            } else if (period.equals(QueryPeriod.MONTH)) {

                items.add("" + date.getMonthOfYear());
                items.add("" + day[1]);
            } else {
                items.add("" + date.getMonthOfYear());
                items.add("" + date.getDayOfMonth());
            }

            items.add("" + new Random().nextInt(150) + 1);

            if (list.indexOf(items) == -1) {
                list.add(items);
            }
        }

        return list;
    }

    public static List<List<String>> trackEventRequestSample(int size) {

        String[] document_type
                = {"CV_Examples", "LP_Instructions", "DS_Examples", "CS_Examples", "CV_Templates", "EDS_Examples", "DS_Examples", "ECV_Instructions", "ELP_Instructions", "LP_Instructions"};

        List<List<String>> list = new ArrayList<List<String>>();

        int previous1 = 0;
        int previous2 = 0;
        int idx = 0;
        int maxIdx = document_type.length;

        for (int i = 0; i < size; i++) {

            List<String> items = new ArrayList<String>();

            previous1 = new Random().nextInt();
            while ((idx = new Random().nextInt(maxIdx)) == previous1);

            items.add(document_type[idx]);

            previous2 = new Random().nextInt(maxIdx);
            while ((idx = new Random().nextInt(maxIdx)) == previous2);

            idx = new Random().nextInt(maxIdx);
            items.add(isoLanguageCountry[idx]);
            items.add(ipCountry[idx]);

            items.add("2013");
            items.add("5");
            items.add("" + (new Random().nextInt(30) + 1));

            items.add("" + new Random().nextInt(150) + 1);

            if (list.indexOf(items) == -1) {
                list.add(items);
            }
        }

        return list;
    }

}
