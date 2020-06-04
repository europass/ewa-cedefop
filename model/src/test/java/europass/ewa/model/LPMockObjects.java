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

public class LPMockObjects {

    public static final SkillsPassport elpSkillsObj;

    public static final SkillsPassport elpSkillsObjArea;

    public static final SkillsPassport elpCerts;

    public static final SkillsPassport elpExps;

    static {
        elpExps = new SkillsPassport();

        LearnerInfo l = new LearnerInfo();
        Skills s = new Skills();
        LinguisticSkills ls = new LinguisticSkills();
        List<LinguisticSkill> fs = new ArrayList<>();
        LinguisticSkill f = new LinguisticSkill();
        List<LinguisticExperience> acquiredDuring = new ArrayList<>();
        LinguisticExperience ex1 = new LinguisticExperience();
        ex1.setDescription("exp1");
        JDate ex1from = new JDate();
        ex1from.setDay(10);
        ex1from.setMonth(3);
        ex1from.setYear(2000);
        JDate ex1to = new JDate();
        ex1to.setDay(8);
        ex1to.setMonth(4);
        ex1to.setYear(2002);
        ex1.setPeriod(new Period(ex1from, ex1to));
        acquiredDuring.add(ex1);
        f.setAcquiredDuring(acquiredDuring);
        fs.add(f);
        ls.setForeignLanguage(fs);
        s.setLinguistic(ls);
        l.setSkills(s);
        elpExps.setLearnerInfo(l);

    }

    static {
        elpCerts = new SkillsPassport();

        LearnerInfo l = new LearnerInfo();
        Skills s = new Skills();
        LinguisticSkills ls = new LinguisticSkills();
        List<LinguisticSkill> fs = new ArrayList<>();
        LinguisticSkill f = new LinguisticSkill();
        List<Certificate<String>> cs = new ArrayList<>();
        Certificate<String> crt1 = new Certificate<String>();
        crt1.setLevel("A1");
        cs.add(crt1);
        Certificate<String> crt2 = new Certificate<String>();
        crt2.setLevel("A2");
        cs.add(crt2);
        f.setVerifiedBy(cs);
        fs.add(f);
        ls.setForeignLanguage(fs);
        s.setLinguistic(ls);
        l.setSkills(s);
        elpCerts.setLearnerInfo(l);

    }

    static {
        elpSkillsObj = new SkillsPassport();

        LearnerInfo skillsLearner = new LearnerInfo();

        Identification identification = new Identification();
        identification.setPersonName(new PersonName("Σάκης", "Πεταλούδας"));

        Skills skills = new Skills();

        List<LinguisticSkill> mother = new ArrayList<LinguisticSkill>();
        mother.add(new LinguisticSkill(new CodeLabel("el", "Greek")));
        mother.add(new LinguisticSkill(new CodeLabel("es", "Spanish")));

        List<LinguisticSkill> foreign = new ArrayList<LinguisticSkill>();
        LinguisticSkill foreign1 = new LinguisticSkill(new CodeLabel("en", "English"));
        foreign1.setProficiencyLevel(new CEFRLevel("C1", "C2", "B2", "B2", "B1"));
        List<LinguisticExperience> acquiredDuring = new ArrayList<LinguisticExperience>();
        LinguisticExperience linguisticExp = new LinguisticExperience();
        linguisticExp.setDescription("<p><em>Summer</em> English courses that help me to improve my spoken interaction level</p>");
        Period period = new Period();
        JDate from, to;
        from = new JDate();
        to = new JDate();
        from.setDay(10);
        from.setMonth(6);
        from.setYear(2000);
        to.setDay(15);
        to.setMonth(8);
        to.setYear(2001);
        period.setFrom(from);
        period.setTo(to);
        linguisticExp.setPeriod(period);
        acquiredDuring.add(linguisticExp);
        foreign1.setAcquiredDuring(acquiredDuring);
        List<Certificate<String>> certificates1 = new ArrayList<Certificate<String>>();
        Certificate<String> cert1 = new Certificate<String>("Cambridge Certificate of Proficiency in English");
        cert1.setTitle("CPE (short title)");
        cert1.setAwardingBody("British Council");
        JDate cert1Date = new JDate();
        cert1Date.setDay(15);
        cert1Date.setMonth(10);
        cert1Date.setYear(2013);
        cert1.setDate(cert1Date);
        cert1.setLevel("C2");
        certificates1.add(cert1);
        certificates1.add(new Certificate<String>("Michigan Certificate of Proficiency in English"));
        foreign1.setVerifiedBy(certificates1);

        LinguisticSkill foreign2 = new LinguisticSkill(new CodeLabel("it", "Italian"));
        foreign2.setProficiencyLevel(new CEFRLevel("B1", "B2", "B1", "A2", "A1"));
        List<Certificate<String>> certificates2 = new ArrayList<Certificate<String>>();
        certificates2.add(new Certificate<String>("Certificate of Adequacy in Italian"));
        foreign2.setVerifiedBy(certificates2);
        foreign.add(foreign1);
        foreign.add(foreign2);

        skills.setLinguistic(new LinguisticSkills(mother, foreign));

        skillsLearner.setSkills(skills);
        skillsLearner.setIdentification(identification);

        elpSkillsObj.setLearnerInfo(skillsLearner);
    }

    static {
        elpSkillsObjArea = new SkillsPassport();

        LearnerInfo skillsLearner = new LearnerInfo();

        Skills skills = new Skills();

        List<LinguisticSkill> foreign = new ArrayList<LinguisticSkill>();
        LinguisticSkill foreign1 = new LinguisticSkill(new CodeLabel("en", "English"));
        foreign1.setProficiencyLevel(new CEFRLevel("C1", "C2", "B2", "B2", "B1"));
        List<LinguisticExperience> acquiredDuring = new ArrayList<LinguisticExperience>();
        LinguisticExperience linguisticExp = new LinguisticExperience();
        linguisticExp.setDescription("Summer English courses that help me to improve my spoken interaction level");
        Period period = new Period();
        JDate from, to;
        from = new JDate();
        to = new JDate();
        from.setDay(10);
        from.setMonth(6);
        from.setYear(2000);
        to.setDay(15);
        to.setMonth(8);
        to.setYear(2001);
        period.setFrom(from);
        period.setTo(to);
        linguisticExp.setPeriod(period);
        CodeLabel area = new CodeLabel();
        area.setCode("option1");
        area.setLabel("area option1");
        linguisticExp.setArea(area);

        acquiredDuring.add(linguisticExp);
        foreign1.setAcquiredDuring(acquiredDuring);
        List<Certificate<String>> certificates1 = new ArrayList<Certificate<String>>();
        Certificate<String> cert1 = new Certificate<String>("Cambridge Certificate of Proficiency in English");
        cert1.setTitle("CPE (short title)");
        cert1.setAwardingBody("British Council");
        JDate cert1Date = new JDate();
        cert1Date.setDay(15);
        cert1Date.setMonth(10);
        cert1Date.setYear(2013);
        cert1.setDate(cert1Date);
        cert1.setLevel("C2");
        certificates1.add(cert1);
        certificates1.add(new Certificate<String>("Michigan Certificate of Proficiency in English"));
        foreign1.setVerifiedBy(certificates1);

        foreign.add(foreign1);
        LinguisticSkills lingskills = new LinguisticSkills();
        lingskills.setForeignLanguage(foreign);
        skills.setLinguistic(lingskills);

        skillsLearner.setSkills(skills);

        elpSkillsObjArea.setLearnerInfo(skillsLearner);
    }
}
