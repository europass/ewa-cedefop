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

import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;

import europass.ewa.enums.EuropassDocumentType;

public class HtmlToOdtTest {

    static final String INPUT_WITH_WRAP = "<div class=\"dummy-root\"><p>Hello Europass!</p></div>";

    static final String INPUT_NO_WRAP = "<p>Hello Europass!</p>";

    @Test
    public void rootWrapForECL() {
        String expected = "<div class=\"dummy-root ECL\"><p>Hello Europass!</p></div>";

        String actual1 = OdtDisplayableUtils.prepareHtmlWrap(INPUT_WITH_WRAP, EuropassDocumentType.ECL);

        Assert.assertThat("ECL html", actual1, CoreMatchers.is(expected));

        String actual2 = OdtDisplayableUtils.prepareHtmlWrap(INPUT_NO_WRAP, EuropassDocumentType.ECL);

        Assert.assertThat("ECL html", actual2, CoreMatchers.is(expected));
    }

    @Test
    public void rootWrapForUnknown() {
        String expected = "<div class=\"dummy-root\"><p>Hello Europass!</p></div>";

        String actual1 = OdtDisplayableUtils.prepareHtmlWrap(INPUT_WITH_WRAP, EuropassDocumentType.UNKNOWN);

        Assert.assertThat("Unknown html", actual1, CoreMatchers.is(expected));

        String actual2 = OdtDisplayableUtils.prepareHtmlWrap(INPUT_NO_WRAP, EuropassDocumentType.UNKNOWN);

        Assert.assertThat("Unknown html", actual2, CoreMatchers.is(expected));
    }

    @Test
    public void rootWrapForNull() {
        String expected = "<div class=\"dummy-root\"><p>Hello Europass!</p></div>";

        String actual1 = OdtDisplayableUtils.prepareHtmlWrap(INPUT_WITH_WRAP, null);

        Assert.assertThat("Null html", actual1, CoreMatchers.is(expected));

        String actual2 = OdtDisplayableUtils.prepareHtmlWrap(INPUT_NO_WRAP, null);

        Assert.assertThat("Null html", actual2, CoreMatchers.is(expected));

    }
}
