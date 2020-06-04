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

public class LearnerInfoBuilder {

    private final Builder b;

    private LearnerInfoBuilder(Builder b) {
        this.b = b;
    }

    public LearnerInfo get() {
        LearnerInfo o = new LearnerInfo();

        if (b.identification != null) {
            o.setIdentification(b.identification);
        }
        if (b.headline != null) {
            o.setHeadline(b.headline);
        }
        if (b.workExperienceList != null) {
            o.setWorkExperienceList(b.workExperienceList);
        }
        if (b.educationList != null) {
            o.setEducationList(b.educationList);
        }
        if (b.skills != null) {
            o.setSkills(b.skills);
        }
        if (b.achievementList != null) {
            o.setAchievementList(b.achievementList);
        }
        if (b.documentation != null) {
            o.setDocumentation(b.documentation);
        }

        return o;
    }

    public static class Builder {

        private Identification identification;

        private Headline headline;

        private List<WorkExperience> workExperienceList;

        private List<Education> educationList;

        private Skills skills;

        private List<Achievement> achievementList;

        private List<ReferenceTo> documentation;

        public Builder withIdentification(Identification identification) {
            this.identification = identification;
            return this;
        }

        public Builder withHeadline(Headline headline) {
            this.headline = headline;
            return this;
        }

        public Builder withWork(WorkExperience item) {
            boolean isNew = false;
            if (this.workExperienceList == null) {
                this.workExperienceList = new ArrayList<WorkExperience>();
                isNew = true;
            }
            if (!isNew && this.workExperienceList.contains(item)) {
                return this;
            }
            this.workExperienceList.add(item);
            return this;
        }

        public Builder withEducation(Education item) {
            boolean isNew = false;
            if (this.educationList == null) {
                this.educationList = new ArrayList<Education>();
                isNew = true;
            }
            if (!isNew && this.educationList.contains(item)) {
                return this;
            }
            this.educationList.add(item);
            return this;
        }

        public Builder withAchievement(Achievement item) {
            boolean isNew = false;
            if (this.achievementList == null) {
                this.achievementList = new ArrayList<Achievement>();
                isNew = true;
            }
            if (!isNew && this.achievementList.contains(item)) {
                return this;
            }
            this.achievementList.add(item);
            return this;
        }

        public Builder withAnnes(ReferenceTo item) {
            boolean isNew = false;
            if (this.documentation == null) {
                this.documentation = new ArrayList<ReferenceTo>();
                isNew = true;
            }
            if (!isNew && this.documentation.contains(item)) {
                return this;
            }
            this.documentation.add(item);
            return this;
        }

        public Builder withCommunication(GenericSkill skill) {
            if (this.skills == null) {
                this.skills = new Skills();
            }
            this.skills.setCommunication(skill);
            return this;
        }

        public Builder withComputer(ComputerSkill skill) {
            if (this.skills == null) {
                this.skills = new Skills();
            }
            this.skills.setComputer(skill);
            return this;
        }

        public Builder withJobRelated(GenericSkill skill) {
            if (this.skills == null) {
                this.skills = new Skills();
            }
            this.skills.setJobRelated(skill);
            return this;
        }

        public Builder withOrganisational(GenericSkill skill) {
            if (this.skills == null) {
                this.skills = new Skills();
            }
            this.skills.setOrganisational(skill);
            return this;
        }

        public Builder withOther(GenericSkill skill) {
            if (this.skills == null) {
                this.skills = new Skills();
            }
            this.skills.setOther(skill);
            return this;
        }

        public Builder withDriving(DrivingSkill driving) {
            if (this.skills == null) {
                this.skills = new Skills();
            }
            this.skills.setDriving(driving);
            return this;
        }

        public Builder withMotherTongue(LinguisticSkill item) {
            LinguisticSkills linguistic = linguistic();

            List<LinguisticSkill> list = linguistic.getMotherTongue();
            this.addLinguistic(list, item);
            linguistic.setMotherTongue(list);
            return this;
        }

        public Builder withForeignLanguage(LinguisticSkill item) {
            LinguisticSkills linguistic = linguistic();

            List<LinguisticSkill> list = linguistic.getForeignLanguage();
            this.addLinguistic(list, item);
            linguistic.setForeignLanguage(list);
            return this;
        }

        private void addLinguistic(List<LinguisticSkill> list, LinguisticSkill item) {
            boolean isNew = false;
            if (list == null) {
                list = new ArrayList<LinguisticSkill>();
                isNew = true;
            }
            if (!isNew && list.contains(item)) {
                return;
            }
            list.add(item);
        }

        private LinguisticSkills linguistic() {
            if (this.skills == null) {
                this.skills = new Skills();
            }
            LinguisticSkills linguistic = this.skills.getLinguistic();
            if (linguistic == null) {
                linguistic = new LinguisticSkills();
                this.skills.setLinguistic(linguistic);
            }
            return this.skills.getLinguistic();
        }

        public LearnerInfoBuilder build() {
            return new LearnerInfoBuilder(this);
        }
    }
}
