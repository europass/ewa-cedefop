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
package europass.ewa;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public final class Constants {

    public static final String ECV = "ECV";

    public static final String ELP = "ELP";

    public static final String ECV_DOWNLOAD_PREFIX = "Europass-CV-";

    public static final String ELP_DOWNLOAD_PREFIX = "Europass-LP-";

    public static final String LEGACY_ECV_DOWNLOAD_PREFIX = "CV";

    public static final String LEGACY_ELP_DOWNLOAD_PREFIX = "LP";

    public static final String ECV_XML_ATTACHMENT = ECV_DOWNLOAD_PREFIX + "XML-Attachment";

    public static final String ELP_XML_ATTACHMENT = ELP_DOWNLOAD_PREFIX + "XML-Attachment";

    public static final String PDF_XML_ATTACHMENT = "Europass-XML-Attachment";

    public static final String SKILLSPASSORT = "SkillsPassport";

    public static final String SESSIONID = "SessionID";

    public static final String[] PDF_XML_ATTACHMENT_NAMES_ARR = new String[]{PDF_XML_ATTACHMENT + ".xml", ECV_XML_ATTACHMENT + ".xml", ELP_XML_ATTACHMENT + ".xml", LEGACY_ECV_DOWNLOAD_PREFIX + ".xml", LEGACY_ELP_DOWNLOAD_PREFIX + ".xml"};
    /*
	 * O(1) complexity when running .contains( value ); However O(n) when creating the set.
     */
    public static final Set<String> PDF_XML_ATTACHMENT_NAMES = Collections.unmodifiableSet(new HashSet<String>(Arrays.asList(PDF_XML_ATTACHMENT_NAMES_ARR)));

    public static final String ECV_XML_ATTACHMENT_DESCRIPTION = "Europass XML Attachment";

    public static final String UTF8_ENCODING = "UTF-8";

    public static final String EIGHT_BIT = "ISO8859-1";

    //Suppress default constructor for noninstantiability
    private Constants() {
        throw new AssertionError();
    }

}
