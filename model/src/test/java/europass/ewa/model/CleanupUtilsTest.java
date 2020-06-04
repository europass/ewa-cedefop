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
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.TypeLiteral;
import com.google.inject.name.Names;

import europass.ewa.model.conversion.ModelModule;

public class CleanupUtilsTest {

    static Map<String, PrintingPreference> cvBundleMap;

    static Map<String, PrintingPreference> lpBundleMap;

    private static Injector injector = null;

    @BeforeClass
    public static void setDefaultPrefs() {
        injector = Guice.createInjector(
                new AbstractModule() {
            @Override
            protected void configure() {
                bindConstant().annotatedWith(Names.named(Namespace.SCHEMA_LOCATION_DEFAULT_PARAM))
                        .to(Namespace.NAMESPACE + " http://europass.cedefop.europa.eu/xml/EuropassSchema_V3.0.xsd");
            }
        },
                new ModelModule()
        );

        cvBundleMap
                = injector.getProvider(Key.get(new TypeLiteral<Map<String, PrintingPreference>>() {
                },
                        Names.named(ModelModule.DEFAULT_CV_PREFS))).get();
        lpBundleMap
                = injector.getProvider(Key.get(new TypeLiteral<Map<String, PrintingPreference>>() {
                },
                        Names.named(ModelModule.DEFAULT_LP_PREFS))).get();

    }

    private static SkillsPassport model() {
        SkillsPassport esp = new SkillsPassport();

        esp.setLocale(Locale.ENGLISH);

        //Work Experience
        WorkExperience work1 = new WorkExperience();
        List<ReferenceTo> work1Doc = new ArrayList<ReferenceTo>();
        work1Doc.add(new ReferenceTo("ATT_1"));
        work1Doc.add(new ReferenceTo("ATT_3"));
        work1.setReferenceToList(work1Doc);

        List<WorkExperience> worklist = new ArrayList<WorkExperience>();
        worklist.add(work1);

        //Education
        Education edu1 = new Education();
        List<ReferenceTo> edu1Doc = new ArrayList<ReferenceTo>();
        edu1Doc.add(new ReferenceTo("ATT_1"));
        //should be removed
        edu1Doc.add(new ReferenceTo("ATT_2"));
        edu1Doc.add(new ReferenceTo("ATT_3"));
        edu1.setReferenceToList(edu1Doc);
        List<Education> educationlist = new ArrayList<Education>();
        educationlist.add(edu1);

        //Skills
        Skills skills = new Skills();
        List<String> driving = new ArrayList<String>();
        driving.add("A");
        driving.add("B1");
        skills.setDriving(new DrivingSkill(driving));

        List<LinguisticSkill> mother = new ArrayList<LinguisticSkill>();
        mother.add(new LinguisticSkill(new CodeLabel("el", "Greek")));

        List<LinguisticSkill> foreign = new ArrayList<LinguisticSkill>();
        LinguisticSkill foreign1 = new LinguisticSkill(new CodeLabel("en", "English"));
        List<ReferenceTo> foreignDoc = new ArrayList<ReferenceTo>();
        //should be removed
        foreignDoc.add(new ReferenceTo("ATT_5"));
        foreign1.setReferenceToList(foreignDoc);

        foreign.add(foreign1);

        skills.setLinguistic(new LinguisticSkills(mother, foreign));

        //Achievements
        List<Achievement> achievements = new ArrayList<Achievement>();
        Achievement participations = new Achievement();
        List<ReferenceTo> partDoc = new ArrayList<ReferenceTo>();
        partDoc.add(new ReferenceTo("ATT_4"));
        //should be removed
        partDoc.add(new ReferenceTo("ATT_5"));
        participations.setReferenceToList(partDoc);
        achievements.add(participations);

        //Documentation
        List<ReferenceTo> annexes = new ArrayList<ReferenceTo>();
        annexes.add(new ReferenceTo("ATT_1"));
        //should be removed
        annexes.add(new ReferenceTo("ATT_2"));
        annexes.add(new ReferenceTo("ATT_3"));
        annexes.add(new ReferenceTo("ATT_4"));
        //should be removed
        annexes.add(new ReferenceTo("ATT_5"));

        LearnerInfo learnerinfo = new LearnerInfo();
        learnerinfo.setEducationList(educationlist);
        learnerinfo.setWorkExperienceList(worklist);
        learnerinfo.setSkills(skills);
        learnerinfo.setAchievementList(achievements);
        learnerinfo.setDocumentation(annexes);

        //Attachments
        List<Attachment> attachments = new ArrayList<Attachment>();
        attachments.add(new Attachment("ATT_1", "My Scanned Diploma", "Diploma.pdf", "application/pdf", "http://europass.instore.gr/ewars/photo/WR3452ER/UTT7534", "My Scanned Diploma".getBytes()));
        attachments.add(new Attachment("ATT_3", "Volunteeting Experience", "Experience.pdf", "application/pdf", "http://europass.instore.gr/ewars/photo/WT4322OO/UCD2434", "Volunteeting Experience".getBytes()));
        attachments.add(new Attachment("ATT_4", "Participation to WorldWide Conference", "Conference.pdf", "application/pdf", "http://europass.instore.gr/ewars/photo/WT4322OO/OPD2569", "Participation to WorldWide Conference".getBytes()));

        esp.setLearnerInfo(learnerinfo);
        esp.setAttachmentList(attachments);

        //Default Printing Preferences of Annexes
        List<PrintingPreference> prefs = new ArrayList<PrintingPreference>();
        prefs.add(new PrintingPreference("LearnerInfo.ReferenceTo[0]", true));
        prefs.add(new PrintingPreference("LearnerInfo.ReferenceTo[1]", true));
        prefs.add(new PrintingPreference("LearnerInfo.ReferenceTo[2]", true));
        prefs.add(new PrintingPreference("LearnerInfo.ReferenceTo[3]", true));
        prefs.add(new PrintingPreference("LearnerInfo.ReferenceTo[4]", true));
        Map<String, List<PrintingPreference>> map = new HashMap<String, List<PrintingPreference>>();
        map.put("ECV", prefs);

        esp.setDocumentPrintingPrefs(map);

        //Activate existing preferences And add missing preferences according to defaults
        esp.activatePreferences("ECV", cvBundleMap);
        esp.applyDefaultPreferences("ECV");

        return esp;

    }

    static class RefPreficate implements Predicate<ReferenceTo> {

        private final String idRef;

        public RefPreficate(String idRef) {
            this.idRef = idRef;
        }

        @Override
        public boolean apply(ReferenceTo input) {
            return idRef.equals(input.getIdref());
        }

    }

    @Test
    public void unresolvedAttachmentRefs() {
        SkillsPassport esp = model();

        //calls unresolvedAttachmentRefs
        CleanupUtils.unresolvedAttachmentRefs(esp);

        Assert.assertThat("The attachments are left unchanged (3)",
                esp.getAttachmentList().size(),
                CoreMatchers.is(3));

        Assert.assertThat("2 refs are removed from Annexes, resulting in 3 refs",
                esp.getLearnerInfo().getDocumentation().size(),
                CoreMatchers.is(3));
        Assert.assertThat("ATT_2 is removed from Annexes",
                Collections2.filter(esp.getLearnerInfo().getDocumentation(), new RefPreficate("ATT_2")).size(),
                CoreMatchers.is(0)
        );
        Assert.assertThat("ATT_5 is removed from Annexes",
                Collections2.filter(esp.getLearnerInfo().getDocumentation(), new RefPreficate("ATT_2")).size(),
                CoreMatchers.is(0)
        );
    }

}
