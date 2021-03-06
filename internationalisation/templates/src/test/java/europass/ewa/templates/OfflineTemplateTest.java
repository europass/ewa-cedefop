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
package europass.ewa.templates;

import com.google.inject.Guice;
import com.google.inject.Injector;
import europass.ewa.oo.client.module.OfficeClientModule;
import europass.ewa.templates.modules.ConfigModule;
import europass.ewa.templates.modules.OfflineTemplateModule;
import java.io.IOException;
import java.net.URISyntaxException;
import javax.xml.bind.JAXBException;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author at
 */
public class OfflineTemplateTest {

    private static Injector injector = null;

    private TemplateTranslator translator = null;

    public OfflineTemplateTest() {
        injector = Guice.createInjector(
                new ConfigModule(),
                new OfficeClientModule(),
                new OfflineTemplateModule()
        );
        translator = injector.getInstance(OfflineTemplateTranslator.class);
    }

//	@Test
//	public void test() throws IOException, URISyntaxException, JAXBException {
//		assertNotNull(translator);
//
//		translator.convert();
//	}
}
