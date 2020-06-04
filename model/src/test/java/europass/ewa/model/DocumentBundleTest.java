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
package europass.ewa.model;

import java.util.ArrayList;
import java.util.List;

import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;

import europass.ewa.enums.EuropassDocumentType;

public class DocumentBundleTest {

    @Test
    public void clIncludedInCV() {
        List<EuropassDocumentType> bundle = new ArrayList<>();
        bundle.add(EuropassDocumentType.ECL);
        bundle.add(EuropassDocumentType.ELP);

        DocumentInfo docInfo = new DocumentInfo();
        docInfo.setDocumentType(EuropassDocumentType.ECV);

        docInfo.setBundle(bundle);

        Assert.assertThat("[main is CV] CL in included ", docInfo.withCoverLetter(), CoreMatchers.is(true));
    }

    @Test
    public void cl_cv_mainCV() {
        List<EuropassDocumentType> bundle = new ArrayList<>();
        bundle.add(EuropassDocumentType.ECL);
        bundle.add(EuropassDocumentType.ELP);

        DocumentInfo docInfo = new DocumentInfo();
        docInfo.setDocumentType(EuropassDocumentType.ECV);

        docInfo.setBundle(bundle);

        Assert.assertThat("[main is CV] CL in main ", docInfo.withCoverLetter(), CoreMatchers.is(true));

        Assert.assertThat("[main is CV] ELP in main ", docInfo.withLanguagePassport(), CoreMatchers.is(true));
    }

    @Test
    public void cl_cv_mainEsp() {
        List<EuropassDocumentType> bundle = new ArrayList<>();
        bundle.add(EuropassDocumentType.ECL);
        bundle.add(EuropassDocumentType.ELP);

        DocumentInfo docInfo = new DocumentInfo();
        docInfo.setDocumentType(EuropassDocumentType.ECV_ESP);

        docInfo.setBundle(bundle);

        Assert.assertThat("[main is CV_ESP] CL in main ", docInfo.withCoverLetter(), CoreMatchers.is(true));

        Assert.assertThat("[main is CV_ESP] ELP in main ", docInfo.withLanguagePassport(), CoreMatchers.is(true));
    }

    @Test
    public void cl_cv_mainUnknown() {
        List<EuropassDocumentType> bundle = new ArrayList<>();
        bundle.add(EuropassDocumentType.ECL);
        bundle.add(EuropassDocumentType.ELP);

        DocumentInfo docInfo = new DocumentInfo();
        docInfo.setDocumentType(EuropassDocumentType.UNKNOWN);

        docInfo.setBundle(bundle);

        Assert.assertThat("[main is UNKNOWN] CL in main ", docInfo.withCoverLetter(), CoreMatchers.is(true));

        Assert.assertThat("[main is UNKNOWN] ELP in main ", docInfo.withLanguagePassport(), CoreMatchers.is(true));

    }

    @Test
    public void unkown_cv_mainCV() {
        List<EuropassDocumentType> bundle = new ArrayList<>();
        bundle.add(EuropassDocumentType.UNKNOWN);
        bundle.add(EuropassDocumentType.ELP);

        DocumentInfo docInfo = new DocumentInfo();
        docInfo.setDocumentType(EuropassDocumentType.ECV);

        docInfo.setBundle(bundle);

        Assert.assertThat("[main is CV] UNKNOWN in main ", docInfo.withCoverLetter(), CoreMatchers.is(false));

        Assert.assertThat("[main is CV] ELP in main ", docInfo.withLanguagePassport(), CoreMatchers.is(true));
    }

    @Test
    public void unkown_cv_mainUnknown() {
        List<EuropassDocumentType> bundle = new ArrayList<>();
        bundle.add(EuropassDocumentType.ECV);
        bundle.add(EuropassDocumentType.ELP);

        DocumentInfo docInfo = new DocumentInfo();
        docInfo.setDocumentType(EuropassDocumentType.UNKNOWN);

        docInfo.setBundle(bundle);

        Assert.assertThat("[main is UNKNOWN] UNKNOWN in main ", docInfo.withCoverLetter(), CoreMatchers.is(false));

        Assert.assertThat("[main is UNKNOWN] ELP in main ", docInfo.withLanguagePassport(), CoreMatchers.is(true));
    }
}
