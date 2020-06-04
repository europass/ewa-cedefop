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

import static org.junit.Assert.assertThat;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hamcrest.CoreMatchers;
import org.junit.Before;
import org.junit.Test;

import europass.ewa.enums.EuropassDocumentType;

public class RemoveAttachmentTest {

    SkillsPassport esp;

    @Before
    public void prepareBeforeTest() {
        esp = new SkillsPassport();

        DocumentInfo docInfo = new DocumentInfo();
        docInfo.setDocumentType(EuropassDocumentType.ECV);
        esp.setDocumentInfo(docInfo);

        Map<String, List<PrintingPreference>> docPrefs = new HashMap<String, List<PrintingPreference>>();
        List<PrintingPreference> prefs = new ArrayList<PrintingPreference>();
        prefs.add(new PrintingPreference("LearnerInfo", true));
        prefs.add(new PrintingPreference("LearnerInfo.ReferenceTo", true));
        prefs.add(new PrintingPreference("LearnerInfo.ReferenceTo[0]", true));
        prefs.add(new PrintingPreference("LearnerInfo.ReferenceTo[1]", true));
        prefs.add(new PrintingPreference("LearnerInfo.ReferenceTo[2]", true));
        prefs.add(new PrintingPreference("LearnerInfo.WorkExperience", true));
        prefs.add(new PrintingPreference("LearnerInfo.WorkExperience[0]", true));
        prefs.add(new PrintingPreference("LearnerInfo.WorkExperience[1]", true));
        prefs.add(new PrintingPreference("LearnerInfo.WorkExperience[2]", true));
        prefs.add(new PrintingPreference("LearnerInfo.WorkExperience[0].ReferenceTo", true));
        prefs.add(new PrintingPreference("LearnerInfo.WorkExperience[1].ReferenceTo", true));
        prefs.add(new PrintingPreference("LearnerInfo.WorkExperience[2].ReferenceTo", true));
        prefs.add(new PrintingPreference("LearnerInfo.WorkExperience[0].ReferenceTo[0]", true));
        prefs.add(new PrintingPreference("LearnerInfo.WorkExperience[1].ReferenceTo[0]", true));
        prefs.add(new PrintingPreference("LearnerInfo.WorkExperience[2].ReferenceTo[0]", true));
        docPrefs.put("ECV", prefs);
        esp.setDocumentPrintingPrefs(docPrefs);

        List<Attachment> atts = new ArrayList<Attachment>();
        atts.add(new Attachment("ATT1", "ATT1", new FileData()));
        atts.add(new Attachment("ATT2", "ATT2", new FileData()));
        atts.add(new Attachment("ATT3", "ATT3", new FileData()));
        esp.setAttachmentList(atts);

        LearnerInfo learner = new LearnerInfo();
        learner.setPrefKey("LearnerInfo");

        List<ReferenceTo> annex = new ArrayList<ReferenceTo>();
        annex.add(new ReferenceTo("ATT1"));
        annex.add(new ReferenceTo("ATT2"));
        annex.add(new ReferenceTo("ATT3"));

        List<WorkExperience> works = new ArrayList<WorkExperience>();
        for (int i = 0; i < 3; i++) {
            WorkExperience work = new WorkExperience();
            work.setPosition(new CodeLabel(null, "Position " + (i + 1)));
            List<ReferenceTo> refs = new ArrayList<ReferenceTo>();
            refs.add(new ReferenceTo("ATT" + (i + 1)));
            work.setReferenceToList(refs);
            work.setPrefKey("LearnerInfo.WorkExperience[" + i + "]");
            works.add(work);
        }
        learner.setWorkExperienceList(works);

        learner.setDocumentation(annex);
        esp.setLearnerInfo(learner);
    }

    @Test
    public void removeAtt() {
        Attachment toRemove = new Attachment();
        toRemove.setId("ATT2");

        CleanupUtils.disableAttachment(esp, toRemove);

        assertThat("Size of Att: ", esp.getAttachmentList().size(), CoreMatchers.is(3));

        assertThat("Size of Annex: ", esp.getLearnerInfo().getDocumentation().size(), CoreMatchers.is(2));

        assertThat("Size of Preference: ", esp.getDocumentPrintingPrefs().get("ECV").size(), CoreMatchers.is(13));

        assertThat("Size of Work 1: ", esp.getLearnerInfo().getWorkExperienceList().get(0).getReferenceToList().size(), CoreMatchers.is(1));

        assertThat("Size of Work 2: ", esp.getLearnerInfo().getWorkExperienceList().get(1).getReferenceToList().size(), CoreMatchers.is(0));

    }
}
