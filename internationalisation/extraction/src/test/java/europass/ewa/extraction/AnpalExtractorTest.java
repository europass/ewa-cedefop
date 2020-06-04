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
import europass.ewa.extraction.modules.AnpalExtractorModule;
import org.json.JSONObject;
import static org.junit.Assert.assertNotNull;
import org.junit.Test;

/**
 *
 * @author at
 */
public class AnpalExtractorTest {

    private static Injector injector = null;
    private AnpalExtractor extractor = null;

    public AnpalExtractorTest() {
        injector = Guice.createInjector(new AnpalExtractorModule());
        extractor = injector.getInstance(AnpalExtractor.class);
    }

    @Test
    public void testGetImages() {
        assertNotNull(extractor);
        extractor.getImages();
    }

    @Test
    public void testGetTexts() {
        assertNotNull(extractor);
        JSONObject anpalTextsJson = extractor.getTexts();
        if (anpalTextsJson != null) {
            assertNotNull(anpalTextsJson.get("export.wizard.anpal.textPost"));
            assertNotNull(anpalTextsJson.get("export.wizard.anpal.fiscalCodeLabel"));
            assertNotNull(anpalTextsJson.get("export.wizard.anpal.version"));
        }
    }

}
