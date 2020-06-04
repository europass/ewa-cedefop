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
package europass.ewa.extraction;

import com.google.inject.Guice;
import com.google.inject.Injector;
import europass.ewa.extraction.modules.EwaTextExtractionModule;
import org.junit.Test;

import java.io.IOException;
import java.util.Locale;

import static junit.framework.TestCase.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Test calls EwaTextExtractor.extractLocalisableResources
 *
 * EwaTextExtractor: 1. fetches localised properties from zanata server for each
 * localisable resource and locale 2. converts properties files to json files
 * and copies to appropriate modules 3. produces js files to be used by editors
 * front-end
 *
 * @author at
 */
public class EwaTextExtractorTest {

    private static Injector injector = null;
    private EwaTextExtractor extractor = null;

    public EwaTextExtractorTest() {
        injector = Guice.createInjector(new EwaTextExtractionModule());
        extractor = injector.getInstance(EwaTextExtractor.class);
    }

    @Test
    public void test() throws IOException {
        assertNotNull(extractor);

        extractor.extractLocalisableResources();
    }

    @Test
    public void testGetLanguageCodeInZanataScriptFormat() {

        final Locale locale_en = new Locale("en");
        assertEquals("en", extractor.replaceLanguageCodeWithCustom(locale_en));

        final Locale locale_srLatn = new Locale("sr-lat");
        assertEquals("sr-Latn", extractor.replaceLanguageCodeWithCustom(locale_srLatn));

        final Locale locale_srCyr = new Locale("sr-cyr");
        assertEquals("sr", extractor.replaceLanguageCodeWithCustom(locale_srCyr));
    }

}
