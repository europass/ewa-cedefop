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

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

import java.util.ResourceBundle;

import org.hamcrest.CoreMatchers;
import org.junit.Test;

import europass.ewa.page.SimpleJsonResourceBundle;

public class SimpleJsonResourceBundleTest {

    @Test
    public void load() {
        ResourceBundle notification
                = ResourceBundle.getBundle("bundles/Notification", new SimpleJsonResourceBundle.Control());

        assertNotNull(notification);

        String message = notification.getString("initial.loading.error.msg");

        assertThat("Message", message, CoreMatchers.containsString("Loading the page"));
    }

}
