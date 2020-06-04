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

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

public class ReferenceToListTest {

    SkillsPassport esp;

    @Before
    public void prepare() {
        esp = new SkillsPassport();
        LearnerInfo learner = new LearnerInfo();

        List<ReferenceTo> annex = new ArrayList<ReferenceTo>();
        annex.add(new ReferenceTo("ATT_1"));
        annex.add(null);
        annex.add(new ReferenceTo());
        annex.add(new ReferenceTo("ATT_2"));
        annex.add(null);
        learner.setDocumentation(annex);

        List<Achievement> as = new ArrayList<>();
        Achievement a = new Achievement();
        List<ReferenceTo> rel = new ArrayList<>();
        rel.add(new ReferenceTo("ATT_1"));
        rel.add(null);
        rel.add(new ReferenceTo());
        rel.add(new ReferenceTo("ATT_2"));
        a.setReferenceToList(rel);
        as.add(a);
        learner.setAchievementList(as);

        esp.setLearnerInfo(learner);
    }

    @Test
    public void size() {
        assertThat(esp.getLearnerInfo().getDocumentation().size(), is(2));

        assertThat(esp.getLearnerInfo().getAchievementList().get(0).getReferenceToList().size(), is(2));
    }

    @Test
    public void addNull() {
        esp.getLearnerInfo().getDocumentation().add(null);
        assertThat(esp.getLearnerInfo().getDocumentation().size(), is(2));

        esp.getLearnerInfo().getAchievementList().get(0).getReferenceToList().add(null);
        assertThat(esp.getLearnerInfo().getAchievementList().get(0).getReferenceToList().size(), is(2));
    }

    @Test
    public void addNoIdRef() {
        esp.getLearnerInfo().getDocumentation().add(new ReferenceTo());
        assertThat(esp.getLearnerInfo().getDocumentation().size(), is(2));

        esp.getLearnerInfo().getAchievementList().get(0).getReferenceToList().add(new ReferenceTo());
        assertThat(esp.getLearnerInfo().getAchievementList().get(0).getReferenceToList().size(), is(2));
    }

    @Test
    public void addProper() {
        esp.getLearnerInfo().getDocumentation().add(new ReferenceTo("ATT_3"));
        assertThat(esp.getLearnerInfo().getDocumentation().size(), is(3));

        esp.getLearnerInfo().getAchievementList().get(0).getReferenceToList().add(new ReferenceTo("ATT_3"));
        assertThat(esp.getLearnerInfo().getAchievementList().get(0).getReferenceToList().size(), is(3));
    }
}
