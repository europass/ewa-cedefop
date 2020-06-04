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
package europass.ewa.mail;

import org.apache.commons.lang.StringUtils;

import javax.inject.Singleton;
import java.util.Iterator;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A very simple templating engine which replaces tokens in text.
 *
 * @author jc
 */
@Singleton
public class TemplateMerger {

    /**
     * Merges template with model. Current implementation is very simple and
     * slow. It also uses model keys in regular expression.
     */
    public String merge(final Map model, final String template) {
        String result = template;

        for (final Iterator it = model.entrySet().iterator(); it.hasNext();) {
            final Map.Entry entry = (Map.Entry) it.next();
            result = result.replaceAll(Pattern.quote((String) entry.getKey()),
                    entry.getValue() == null ? StringUtils.EMPTY : Matcher.quoteReplacement(entry.getValue().toString()));
        }

        return result;
    }
}
