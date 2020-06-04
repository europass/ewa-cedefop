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
import europass.ewa.extraction.modules.EscoExtractorModule;
import org.junit.Test;

import static org.junit.Assert.assertNotNull;

public class EscoExtractorTest {

    private static Injector injector = null;
    private EscoOccupationsExtractor extractor = null;

    public EscoExtractorTest() {
        injector = Guice.createInjector(new EscoExtractorModule());
        extractor = injector.getInstance(EscoOccupationsExtractor.class);
    }

    @Test
    public void getEscoOccupations() throws Exception {
        assertNotNull(extractor);
        extractor.execute();
    }
}
