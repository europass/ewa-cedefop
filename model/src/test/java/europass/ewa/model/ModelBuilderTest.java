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

import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

public class ModelBuilderTest {

    private static SkillsPassport esp;

    @BeforeClass
    public static void prepare() {

        esp = new SkillsPassportBuilder.Builder()
                .withLearnerInfo(
                        new LearnerInfoBuilder.Builder()
                                .withEducation(
                                        new EducationBuilder.Builder()
                                                .organisation(
                                                        new OrganisationBuilder.Builder()
                                                                .name("University")
                                                                .address(
                                                                        new AddressBuilder.Builder()
                                                                                .municipality("Athens")
                                                                                .build()
                                                                                .get()
                                                                )
                                                                .build()
                                                                .get()
                                                )
                                                .build()
                                                .get()
                                )
                                .build()
                                .get()
                )
                .build().get();

    }

    @Test
    public void test() {
        Assert.assertThat(esp.getLearnerInfo().getEducationList().get(0).getOrganisation().getName(), CoreMatchers.is("University"));
    }
}
