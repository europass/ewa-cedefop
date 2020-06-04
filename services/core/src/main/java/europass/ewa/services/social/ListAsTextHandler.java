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
package europass.ewa.services.social;

import java.util.List;

import javax.inject.Singleton;

import com.github.mustachejava.Mustache;

@Singleton
public class ListAsTextHandler<A extends Object, B extends Object> implements Transformer {

    protected Mustache template;
    private static final ListAsText<Mustache> asText = new MustacheListAsText();

    @SuppressWarnings("unchecked")
    @Override
    public Object transform(Object from, Object to, Object... params)
            throws InstanceClassMismatchException {

        if (!(from instanceof List<?>)) {
            throw new InstanceClassMismatchException();
        }
        if (!(to instanceof Mustache)) {
            throw new InstanceClassMismatchException();
        }

        List<?> itemsList = (List<?>) from;

        return (String) asText.transform(itemsList, template, params);
    }
}
