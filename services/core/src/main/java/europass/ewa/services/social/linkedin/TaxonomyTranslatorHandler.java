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
package europass.ewa.services.social.linkedin;

import java.util.Locale;
import java.util.ResourceBundle;

import javax.inject.Singleton;

import europass.ewa.resources.JsonResourceBundle;
import europass.ewa.services.social.InstanceClassMismatchException;
import europass.ewa.services.social.Transformer;

@Singleton
public class TaxonomyTranslatorHandler implements Transformer {

    /**
     * @param params: <taxonomy> <key> <locale>
     */
    @SuppressWarnings("unchecked")
    @Override
    public Object transform(Object from, Object to, Object... params)
            throws InstanceClassMismatchException {

        if (!(to instanceof String)) {
            throw new InstanceClassMismatchException();
        }

        Locale locale = Locale.ENGLISH;
        String taxonomy = null;
        String key = null;
        if (params != null) {
            switch (params.length) {
                case 4:
                case 3: {
                    Object param2 = params[2];
                    if (param2 instanceof Locale) {
                        locale = (Locale) param2;
                    } else if (param2 instanceof String) {
                        locale = new Locale((String) params[2]);
                    }
                }
                case 2:
                    key = (String) params[1];
                case 1: {
                    taxonomy = (String) params[0];
                    break;
                }
                case 0:
                    break;
            }
        }
        if (taxonomy == null || key == null) {
            return to;
        }
        try {

            ResourceBundle bundle = ResourceBundle.getBundle("bundles/" + taxonomy, locale, new JsonResourceBundle.Control());

            to = bundle.getString(key);

            return to;
        } catch (final Exception e) {
            return to;
        }

    }
}
