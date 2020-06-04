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
package europass.ewa.model.format;

import org.owasp.html.HtmlPolicyBuilder;
import org.owasp.html.PolicyFactory;

public class HtmlSanitizer {

    private HtmlSanitizer() {
        throw new AssertionError();
    }

    public static String sanitize(String html) {
        // http://javadox.com/com.googlecode.owasp-java-html-sanitizer/owasp-java-html-sanitizer/r223/javadoc/org/owasp/html/HtmlPolicyBuilder.html
        // this is non thread safe so we create whenever we need to sanitize
        PolicyFactory builder = new HtmlPolicyBuilder()
                .allowElements("a", "br", "p", "blockquote", "b", "i", "u", "ul", "ol", "li", "strong", "em", "span", "sup", "sub")
                .allowUrlProtocols("http", "https", "mailto")
                .allowAttributes("href", "target").onElements("a")
                .allowAttributes("class").onElements("p", "ul", "span")
                .requireRelNofollowOnLinks()
                .toFactory();
        return builder.sanitize(html);
    }
}
