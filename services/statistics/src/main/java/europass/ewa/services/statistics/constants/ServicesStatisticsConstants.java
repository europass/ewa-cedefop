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
package europass.ewa.services.statistics.constants;

import europass.ewa.services.statistics.enums.request.QueryPrefixes;
import europass.ewa.services.statistics.hibernate.data.*;
import java.util.Arrays;
import java.util.List;

/**
 * ServicesStatisticsConstants
 * 
 * Defines name strings and regular expressions that are utilized in service endpoint construction, types of constants, hibernate entities etc
 * 
 * @author pgia
 *
 */
public final class ServicesStatisticsConstants {

	
	public static final String RESPONSE_TYPE_BASE = "/to";
	
	public static final List EDITORS_LANGUAGES = Arrays.asList(new String[]{"bg_BG", "cs_CZ", "da_DK", "de_DE",
		"et_EE", "el_GR", "es_ES", "hr_HR", "is_IS", "it_IT", "lv_LV", "lt_LT", "hu_HU", "mt_MT", "nl_NL", "nb_NO",
		"pl_PL", "pt_PT", "ro_RO", "sk_SK", "sl_SI", "sr_RS", "fi_FI", "sv_SE", "tr_TR", "en_GB", "fr_FR", "mk_MK"});

	// Query Prefixes
	public static final String REQUEST_PREFIX_GENERATED = "/"+QueryPrefixes.GENERATED+";";
	public static final String REQUEST_PREFIX_VISITS = "/"+QueryPrefixes.VISITS+";";
	public static final String REQUEST_PREFIX_DOWNLOADS = "/"+QueryPrefixes.DOWNLOADS+";";

	// Values Regular Expressions
	public static final String DATE_YEAR_REGEXP =             "(2(\\d){3})";
	public static final String DATE_YEAR_MONTH_REGEXP =       "(2(\\d){3}\\.((0[1-9])|(1[0-2])))";
	public static final String DATE_YEAR_MONTH_DAY_REGEXP =   "(2(\\d){3}\\.((0[1-9])|(1[0-2]))\\.((0[1-9])|((1|2)[0-9])|30|31))";

	public static final String DATE_REGEXP =                  "("+DATE_YEAR_MONTH_DAY_REGEXP+"|"+DATE_YEAR_MONTH_REGEXP+"|"+DATE_YEAR_REGEXP+")";
	
	public static String DATE_YEAR_RANGE_REGEXP =            "("+DATE_YEAR_REGEXP+"-"+DATE_YEAR_REGEXP+")";
	public static String DATE_YEAR_MONTH_RANGE_REGEXP =      "("+DATE_YEAR_MONTH_REGEXP+"-"+DATE_YEAR_MONTH_REGEXP+")";
	public static String DATE_YEAR_MONTH_DAY_RANGE_REGEXP =  "("+DATE_YEAR_MONTH_DAY_REGEXP+"-"+DATE_YEAR_MONTH_DAY_REGEXP+")";

	public static final String DATE_RANGE_REGEXP =            "("+DATE_YEAR_MONTH_DAY_RANGE_REGEXP+"|"+DATE_YEAR_MONTH_RANGE_REGEXP+"|"+DATE_YEAR_RANGE_REGEXP+")";
	
	private static String DATE_YEAR_OR_REGEXP =               DATE_YEAR_REGEXP+"(\\+"+DATE_YEAR_REGEXP+")+";
	private static String DATE_YEAR_MONTH_OR_REGEXP =         DATE_YEAR_MONTH_REGEXP+"(\\+"+DATE_YEAR_MONTH_REGEXP+")+";
	private static String DATE_YEAR_MONTH_DAY_OR_REGEXP =     DATE_YEAR_MONTH_DAY_REGEXP+"(\\+"+DATE_YEAR_MONTH_DAY_REGEXP+")+";

	public static String DATE_OR_REGEXP =                    "("+DATE_YEAR_OR_REGEXP+"|"+DATE_YEAR_MONTH_OR_REGEXP+"|"+DATE_YEAR_MONTH_DAY_OR_REGEXP+")";

	private static String DATE_YEAR_AND_REGEXP =               DATE_YEAR_REGEXP+"(,"+DATE_YEAR_REGEXP+")+";
	private static String DATE_YEAR_MONTH_AND_REGEXP =         DATE_YEAR_MONTH_REGEXP+"(,"+DATE_YEAR_MONTH_REGEXP+")+";
	private static String DATE_YEAR_MONTH_DAY_AND_REGEXP =     DATE_YEAR_MONTH_DAY_REGEXP+"(,"+DATE_YEAR_MONTH_DAY_REGEXP+")+";
	
	public static String DATE_AND_REGEXP =                    "("+DATE_YEAR_AND_REGEXP+"|"+DATE_YEAR_MONTH_AND_REGEXP+"|"+DATE_YEAR_MONTH_DAY_AND_REGEXP+")";
	
	private static String DATE_YEAR_NOT_REGEXP =               "!" + DATE_YEAR_REGEXP;
	private static String DATE_YEAR_MONTH_NOT_REGEXP =         "!" + DATE_YEAR_MONTH_REGEXP;
	private static String DATE_YEAR_MONTH_DAY_NOT_REGEXP =     "!" + DATE_YEAR_MONTH_DAY_REGEXP;
	
	public static String DATE_NOT_REGEXP =                    "("+DATE_YEAR_NOT_REGEXP+"|"+DATE_YEAR_MONTH_NOT_REGEXP+"|"+DATE_YEAR_MONTH_DAY_NOT_REGEXP+")";
	
	public static final String NUMBER_REGEXP =                "(\\d)+";
	
	public static String NUMBER_RANGE_NO_MIN_MAX_ONCE =       "("+NUMBER_REGEXP+"-"+NUMBER_REGEXP+")";
	public static String NUMBER_RANGE_NUMBERS_ONLY =          "("+NUMBER_RANGE_NO_MIN_MAX_ONCE+"+(,"+NUMBER_RANGE_NO_MIN_MAX_ONCE+")*)";
	
	public static String NUMBER_RANGE_WITH_MIN =              "((min)-"+NUMBER_REGEXP+"){1}"+"(,"+NUMBER_RANGE_NO_MIN_MAX_ONCE+")*";
	public static String NUMBER_RANGE_WITH_MAX =              "("+NUMBER_RANGE_NUMBERS_ONLY+"(,"+NUMBER_REGEXP+"-(max)){1})";
	public static String NUMBER_RANGE_WITH_MAX_ONLY =         "("+NUMBER_REGEXP+"-(max)){1}";
	public static String NUMBER_RANGE_WITH_MIN_MAX =          "(("+NUMBER_RANGE_WITH_MIN+"){1}"+"(,"+NUMBER_RANGE_WITH_MAX_ONLY+"){1})";
	
	public static String NUMBER_RANGE_REGEXP =                "("+NUMBER_RANGE_NUMBERS_ONLY+"|"+NUMBER_RANGE_WITH_MIN+"|"+NUMBER_RANGE_WITH_MAX+"|"+NUMBER_RANGE_WITH_MAX_ONLY+"|"+NUMBER_RANGE_WITH_MIN_MAX+")";
	
	// Entity Table Names
	
	
	public static final String CUBE_ENTRY_AGE = "cube_entry_age";
	public static final String CUBE_ENTRY_DOCS = "cube_entry_docs";
	public static final String CUBE_ENTRY_DOCS_LANGS = "cube_entry_docs_langs";
	public static final String CUBE_ENTRY_FLANG = "cube_entry_flang";
	public static final String CUBE_ENTRY_FLANG_COUNTER = "cube_entry_flang_counter";
	public static final String CUBE_ENTRY_FLANG_PIVOT = "cube_entry_flang_pivot";
	public static final String CUBE_ENTRY_FLANG_SHORT = "cube_entry_flang_short";
	public static final String CUBE_ENTRY_GENDER = "cube_entry_gender";
	public static final String CUBE_ENTRY_LANGS = "cube_entry_langs";
	public static final String CUBE_ENTRY_MLANG = "cube_entry_mlang";
	public static final String CUBE_ENTRY_NAT = "cube_entry_nat";
	public static final String CUBE_ENTRY_NAT_RANK = "cube_nationality_rank";
	public static final String CUBE_ENTRY_NAT_FLANG = "cube_entry_nat_flang";
	public static final String CUBE_ENTRY_NAT_LANGS = "cube_entry_nat_langs";
	public static final String CUBE_ENTRY_NAT_MLANG = "cube_entry_nat_mlang";
	public static final String CUBE_ENTRY_SHORT = "cube_entry_short";
	public static final String CUBE_ENTRY_WORKEXP = "cube_entry_workexp";
	public static final String CUBE_TOP20NAT = "cube_top20nat";

	public static final String CUBE_ENTRY = "cube_entry";
	public static final String CUBE_ENTRY_EMAIL_HASH = "cube_entry_email_hash";
	
	public static final String STAT_VISITS = "stat_visits";
	public static final String STAT_DOWNLOADS = "stat_downloads";
	
	public static final String ISO_COUNTRY = "iso_country";
	public static final String ISO_NATIONALITY = "iso_nationality";
	
	public static final String CUBE_ENTRY_AGE_ENTITY = CubeEntryAge.class.getSimpleName();
	public static final String CUBE_ENTRY_DOCS_ENTITY = CubeEntryDocs.class.getSimpleName();
	public static final String CUBE_ENTRY_DOCS_LANGS_ENTITY = CubeEntryDocsLangs.class.getSimpleName();
	public static final String CUBE_ENTRY_FLANG_ENTITY = CubeEntryFlang.class.getSimpleName();
	public static final String CUBE_ENTRY_FLANG_COUNTER_ENTITY = CubeEntryFLangCounter.class.getSimpleName();
	public static final String CUBE_ENTRY_FLANG_PIVOT_ENTITY = CubeEntryFLangPivot.class.getSimpleName();
	public static final String CUBE_ENTRY_FLANG_SHORT_ENTITY = CubeEntryFlangShort.class.getSimpleName();
	public static final String CUBE_ENTRY_GENDER_ENTITY = CubeEntryGender.class.getSimpleName();
	public static final String CUBE_ENTRY_LANGS_ENTITY = CubeEntryLangs.class.getSimpleName();
	public static final String CUBE_ENTRY_MLANG_ENTITY = CubeEntryMlang.class.getSimpleName();
	public static final String CUBE_ENTRY_NAT_ENTITY = CubeEntryNat.class.getSimpleName();
	public static final String CUBE_ENTRY_NAT_RANK_ENTITY = CubeEntryNatRank.class.getSimpleName();
	public static final String CUBE_ENTRY_NAT_FLANG_ENTITY = CubeEntryNatFLang.class.getSimpleName();
	public static final String CUBE_ENTRY_NAT_LANGS_ENTITY = CubeEntryNatLang.class.getSimpleName();
	public static final String CUBE_ENTRY_NAT_MLANG_ENTITY = CubeEntryNatMLang.class.getSimpleName();
	public static final String CUBE_ENTRY_SHORT_ENTITY = CubeEntryShort.class.getSimpleName();
	public static final String CUBE_ENTRY_WORKEXP_ENTITY = CubeEntryWorkExp.class.getSimpleName();
	public static final String CUBE_TOP20NAT_ENTITY = "cube_top20nat";
	
	public static final String CUBE_ENTRY_ENTITY = CubeEntry.class.getSimpleName();
	public static final String CUBE_ENTRY_EMAIL_HASH_ENTITY = CubeEntryEmailHash.class.getSimpleName();
	
	public static final String STAT_VISITS_ENTITY = StatVisits.class.getSimpleName();
	public static final String STAT_DOWNLOADS_ENTITY = StatDownloads.class.getSimpleName();
	
	public static final String ISO_COUNTRY_ENTITY = IsoCountry.class.getSimpleName();
	public static final String ISO_NATIONALITY_ENTITY = IsoNationality.class.getSimpleName();

	public static final String ORDER_BY_RESULTS = "results";
}
