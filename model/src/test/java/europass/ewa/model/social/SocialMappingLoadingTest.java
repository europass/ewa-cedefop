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
package europass.ewa.model.social;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

import java.io.IOException;

import org.hamcrest.CoreMatchers;
import org.junit.BeforeClass;
import org.junit.Test;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.name.Names;

public class SocialMappingLoadingTest {

    private static Injector injector = null;

    private static MappingListRoot linkedInMapping;

    @BeforeClass
    public static void prepare() {
        injector = Guice.createInjector(
                new SocialMappingsModule()
        );

        linkedInMapping = injector.getInstance(Key.get(MappingListRoot.class, Names.named(SocialMappingsModule.SOCIAL_MAPPING_LINKEDIN)));

    }

    @Test
    public void loadLinkedIn() throws IOException {
        assertNotNull("There is a root", linkedInMapping);

        assertThat("Provider", linkedInMapping.getProvider(), CoreMatchers.is("LinkedIn"));

        assertNotNull("Nested Mappings", linkedInMapping.getMappingList());

        assertThat("Firstname", linkedInMapping.getMappingList().get(0).getFrom(), CoreMatchers.startsWith("/firstName"));
    }
}
