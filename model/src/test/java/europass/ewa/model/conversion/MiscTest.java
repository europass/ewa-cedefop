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
package europass.ewa.model.conversion;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Test;

import europass.ewa.enums.ConversionFileType;
import europass.ewa.enums.EuropassDocumentType;
import europass.ewa.model.FileData;
import europass.ewa.model.PrintingPreference;

public class MiscTest {

    @Test
    public void listupdate() {

        List<FileData> list = new ArrayList<FileData>();
        list.add(new FileData("file1", "image/jpg", "http://test.gr/1111"));
        list.add(new FileData("file2", "image/jpg", "http://test.gr/2222"));
        list.add(new FileData("file3", "image/jpg", "http://test.gr/3333"));

        assertThat(list.get(0).getMimeType(), is("image/jpg"));
    }

    @Test
    public void stringreplace() {
        String key = "LearnerInfo.Skills.Linguistic.ForeignLanguage[4].Certificate[3]";

        String defaultKey = key.replaceAll("\\[\\d+\\]", "[0]");

        assertThat(defaultKey, is("LearnerInfo.Skills.Linguistic.ForeignLanguage[0].Certificate[0]"));
    }

    @Test
    public void testenumDocument() {
        assertThat(EuropassDocumentType.ECV.name(), is("ECV"));
        assertThat(EuropassDocumentType.ECV.toString(), is("ECV"));
    }

    @Test
    public void testenumFileType() {
        assertThat(ConversionFileType.HTML.name(), is("HTML"));
        assertThat(ConversionFileType.HTML.toString(), is("HTML"));
        assertThat(ConversionFileType.HTML.getDescription(), is("html"));
        assertThat(ConversionFileType.HTML.getExtension(), is(".html"));
        assertThat(ConversionFileType.HTML.getMimeType(), is("text/html"));
    }

    @Test
    public void listContainsStr() {
        List<String> c = new ArrayList<String>();
        c.add(new String("alpha"));
        c.add(new String("beta"));
        c.add(new String("alpha"));

        assertThat("Size: ", c.size(), is(3));
    }

    @Test
    public void setContainsStr() {
        Set<String> c = new HashSet<String>();
        c.add(new String("alpha"));
        c.add(new String("beta"));
        c.add(new String("alpha"));

        assertThat("Size: ", c.size(), is(2));
    }

    @Test
    public void listContainsPref() {
        List<PrintingPreference> c = new ArrayList<PrintingPreference>();
        c.add(new PrintingPreference("alpha", true));
        c.add(new PrintingPreference("alpha", true, null, "address-format", null));
        c.add(new PrintingPreference("alpha", false));
        c.add(new PrintingPreference("alpha", true));

        assertThat("Size: ", c.size(), is(4));
    }

    @Test
    public void setContainsPref() {
        Set<PrintingPreference> c = new HashSet<PrintingPreference>();
        c.add(new PrintingPreference("alpha", true));
        c.add(new PrintingPreference("alpha", true, null, "address-format", null));
        c.add(new PrintingPreference("alpha", false));
        c.add(new PrintingPreference("alpha", true));

        assertThat("Size: ", c.size(), is(3));
    }
}
