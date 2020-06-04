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
package europass.ewa.services.statistics.api;

import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.PathSegment;

import com.google.inject.Inject;
import com.sun.jersey.core.util.MultivaluedMapImpl;

import europass.ewa.services.statistics.api.info.QueryInfo;
import europass.ewa.services.statistics.api.process.StatisticsApiRequestProcess;
import europass.ewa.services.statistics.enums.request.QueryPrefixes;
import europass.ewa.services.statistics.structures.QueryProperties;
import europass.ewa.services.statistics.validators.factory.ValidatorFactory;

public class StatsAPIMockObjects {

    private static QueryInfo statisticsApiInfo;
    private static QueryProperties qprops;
    private static ValidatorFactory validatorFactory;
    private static StringBuilder sb;
    private static StatisticsApiRequestProcess process;

    @Inject
    public StatsAPIMockObjects(StatisticsApiRequestProcess proc, QueryInfo info, QueryProperties props, ValidatorFactory factory) {

        process = proc;
        statisticsApiInfo = info;
        qprops = props;
        validatorFactory = factory;
        sb = new StringBuilder();
    }

    public static QueryInfo getStatisticsApiInfo() {
        return statisticsApiInfo;
    }

    public static QueryProperties getQprops() {
        return qprops;
    }

    public static ValidatorFactory getValidatorFactory() {
        return validatorFactory;
    }

    public static StringBuilder getSb() {
        return sb;
    }

    public static StatisticsApiRequestProcess getProcess() {
        return process;
    }

//	public static final String DOCUMENT_TYPE_DATE_HSQL = "select e.CV, e.year_no, e.month_no from CubeEntryDocs e where ((e.year_no >= 2013 AND e.month_no >= 1)  AND (e.year_no <= 2013 AND e.month_no <= 12) ) group by e.CV, e.year_no, e.month_no";
    public static final String DOCUMENT_TYPE_DATE_HSQL = "select SUM(e.CV), e.year_no from CubeEntryDocs e where (e.year_no = 2013 )  group by e.year_no";

    public static PathSegment documentTypeDate() {

        MultivaluedMap<String, String> map = new MultivaluedMapImpl();
        map.add("document-type", "CV");
        map.add("date", "2013");

        return createPathSegment(QueryPrefixes.GENERATED.getDescription(), map);
    }

//	public static final String DOCUMENT_TYPE_DATE_LANGUAGE_HSQL = "select e.CV, e.doc_lang, e.year_no, e.month_no from CubeEntryDocsLangs e where ((e.year_no >= 2013 AND e.month_no >= 1)  AND (e.year_no <= 2013 AND e.month_no <= 12) ) group by e.CV, e.doc_lang, e.year_no, e.month_no";
    public static final String DOCUMENT_TYPE_DATE_LANGUAGE_HSQL = "select SUM(e.CV), e.doc_lang, e.year_no from CubeEntryDocsLangs e where (e.year_no = 2013 )  group by e.doc_lang, e.year_no";

    public static PathSegment documentTypeDateLanguage() {

        MultivaluedMap<String, String> map = new MultivaluedMapImpl();
        map.add("document-type", "CV");
        map.add("date", "2013");
        map.add("language", "");

        return createPathSegment(QueryPrefixes.GENERATED.getDescription(), map);
    }

//	public static final String AGE_RANGE_DATE_HSQL = "select e.upto20, e.from21to25, e.from26to30, e.year_no, e.month_no from CubeEntryAge e where ((e.year_no >= 2013 AND e.month_no >= 1 AND e.day_no >= 1)  AND (e.year_no <= 2013 AND e.month_no <= 12 AND e.day_no <= 31) ) group by e.upto20, e.from21to25, e.from26to30, e.year_no, e.month_no";
    public static final String AGE_RANGE_DATE_HSQL = "select SUM(e.upto20), SUM(e.from21to25), SUM(e.from26to30), e.year_no from CubeEntryAge e where (e.year_no = 2013 )  group by e.year_no order by SUM(e.upto20) DESC, SUM(e.from21to25) DESC, SUM(e.from26to30) DESC";

    public static PathSegment ageRangeDate() {

        MultivaluedMap<String, String> map = new MultivaluedMapImpl();
        map.add("age", "min-20,21-25,26-30");
        map.add("date", "2013");

        return createPathSegment(QueryPrefixes.GENERATED.getDescription(), map);
    }

    public static final String AGE_RANGE_DOCUMENT_TYPE_DATE_COUNTRY_TOP15_HSQL = "select e.doc_type, e.upto20, e.from21to25, e.from26to30, e.from31to35, e.from35plus, e.year_no, e.month_no, e.address_country from CubeEntryAge e where e.doc_type = 'CV' AND ((e.year_no >= 2013 AND e.month_no >= 1 AND e.day_no >= 1)  AND (e.year_no <= 2013 AND e.month_no <= 12 AND e.day_no <= 31) ) group by e.doc_type, e.upto20, e.from21to25, e.from26to30, e.from31to35, e.from35plus, e.year_no, e.month_no, e.address_country order by e.doc_type DESC, e.upto20 DESC, e.from21to25 DESC, e.from26to30 DESC, e.from31to35 DESC, e.from35plus DESC, e.year_no DESC, e.month_no DESC, e.address_country DESC";

    public static PathSegment ageRangeDocumentTypeDateByCountryTop15() {

        MultivaluedMap<String, String> map = new MultivaluedMapImpl();
        map.add("document-type", "CV");
        map.add("age", "min-20,21-25,26-30,31-35,35-max");
        map.add("country", "");
        map.add("date", "2013");
        map.add("top", "15");

        return createPathSegment(QueryPrefixes.GENERATED.getDescription(), map);
    }

//	public static final String DOCUMENT_TYPE_DATE_GENDER_NATIONALITY = "select e.nationality, e.doc_type, e.gender_group, e.year_no, e.month_no from CubeEntryNatLang e where e.doc_type = 'CV' AND ((e.year_no >= 2013 AND e.month_no >= 1 AND e.day_no >= 1)  AND (e.year_no <= 2013 AND e.month_no <= 12 AND e.day_no <= 31) ) group by e.nationality, e.doc_type, e.gender_group, e.year_no, e.month_no";
    public static final String DOCUMENT_TYPE_DATE_GENDER_NATIONALITY = "select SUM(e.rec_count), e.nationality, e.doc_type, e.gender_group, e.year_no from CubeEntryNatLang e where e.doc_type = 'CV' AND (e.year_no = 2013 )  group by e.nationality, e.doc_type, e.gender_group, e.year_no order by SUM(e.rec_count) DESC";

    public static PathSegment documentTypeDateGenderNationality() {

        MultivaluedMap<String, String> map = new MultivaluedMapImpl();
        map.add("document-type", "CV");
        map.add("date", "2013");
        map.add("gender", "");
        map.add("nationality", "");

        return createPathSegment(QueryPrefixes.GENERATED.getDescription(), map);
    }

//	public static final String DOCUMENT_TYPE_COUNTRY_WORK_EXPERIENCE_GENDER_DATE = "select SUM(e.rec_count), e.doc_type, e.gender_group, e.work_years, e.year_no, e.month_no, e.address_country from CubeEntry e where e.doc_type = 'CV' AND e.work_years >= 1 AND e.work_years <= 5  AND ((e.year_no >= 2013 AND e.month_no >= 1 AND e.day_no >= 1)  AND (e.year_no <= 2013 AND e.month_no <= 12 AND e.day_no <= 31) ) group by e.doc_type, e.gender_group, e.work_years, e.year_no, e.month_no, e.address_country, e.rec_count";
    public static final String DOCUMENT_TYPE_COUNTRY_WORK_EXPERIENCE_GENDER_DATE = "select SUM(e.rec_count), e.doc_type, e.gender_group, e.work_years, e.year_no, e.address_country from CubeEntryLangs e where e.doc_type = 'CV' AND e.work_years >= 1 AND e.work_years <= 5  AND (e.year_no = 2013 )  group by e.doc_type, e.gender_group, e.work_years, e.year_no, e.address_country order by SUM(e.rec_count) DESC";

    public static PathSegment documentTypeCountryGenderWorkExperienceDate() {

        MultivaluedMap<String, String> map = new MultivaluedMapImpl();
        map.add("document-type", "CV");
        map.add("country", "");
        map.add("work-experience", "1-5");
        map.add("gender", "");
        map.add("date", "2013");

        return createPathSegment(QueryPrefixes.GENERATED.getDescription(), map);
    }

    public static PathSegment invalidQueryPrefixName() {

        MultivaluedMap<String, String> map = new MultivaluedMapImpl();
        map.add("document-type", "CV");	// wrong param name

        return createPathSegment("othersources", map);
    }

    public static PathSegment invalidParameterForPrefix() {

        MultivaluedMap<String, String> map = new MultivaluedMapImpl();
        map.add("examples-format", "");	// wrong param name
        map.add("date", "2013");

        return createPathSegment(QueryPrefixes.GENERATED.getDescription(), map);
    }

    public static PathSegment invalidParameterName() {

        MultivaluedMap<String, String> map = new MultivaluedMapImpl();
        map.add("doc-type", "CV");	// wrong param name
        map.add("date", "2013");

        return createPathSegment(QueryPrefixes.GENERATED.getDescription(), map);
    }

    public static PathSegment invalidParameterValue() {

        MultivaluedMap<String, String> map = new MultivaluedMapImpl();
        map.add("document-type", "C-V");	// wrong param value
        map.add("date", "2013");

        return createPathSegment(QueryPrefixes.GENERATED.getDescription(), map);
    }

    public static PathSegment invalidDateValue() {

        MultivaluedMap<String, String> map = new MultivaluedMapImpl();
        map.add("document-type", "CV");
        map.add("date", "2013.13"); // wrong date value

        return createPathSegment(QueryPrefixes.GENERATED.getDescription(), map);
    }

    public static PathSegment emptyResults() {

        MultivaluedMap<String, String> map = new MultivaluedMapImpl();
        map.add("date", "2001");
        map.add("gender", "male");

        return createPathSegment(QueryPrefixes.GENERATED.getDescription(), map);
    }

    private static PathSegment createPathSegment(final String path, final MultivaluedMap<String, String> multiMap) {

        return new PathSegment() {

            @Override
            public String getPath() {
                return path;
            }

            @Override
            public MultivaluedMap<String, String> getMatrixParameters() {
                return multiMap;
            }
        };

    }
}
