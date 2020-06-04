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

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.google.common.base.Predicate;
import com.google.common.base.Strings;
import com.google.common.collect.Iterables;

import europass.ewa.model.decorator.WithDocumentList;
import europass.ewa.model.wrapper.IdRefSafeList;
import java.util.Arrays;

@JsonPropertyOrder({"identification", "headline", "workExperienceList",
    "educationList", "skills", "achievementList", "documentation"})
@JsonIgnoreProperties(ignoreUnknown = true)
public class LearnerInfo extends PrintableObject {

    private static final int MIN_NUMBER_OF_CV_SECTIONS = 4;

    private Identification identification;

    private Headline headline;

    private List<WorkExperience> workExperienceList;

    private List<Education> educationList;

    private Skills skills;

    private List<Achievement> achievementList;

    private List<ReferenceTo> documentation;

    @JsonProperty("Identification")
    @JacksonXmlProperty(localName = "Identification", namespace = Namespace.NAMESPACE)
    public Identification getIdentification() {
        return withPreferences(identification, "Identification");
    }

    public void setIdentification(Identification identification) {
        this.identification = identification;
    }

    @JsonProperty("Headline")
    @JacksonXmlProperty(localName = "Headline", namespace = Namespace.NAMESPACE)
    public Headline getHeadline() {
        return withDocument(headline, getDocument());
    }

    public void setHeadline(Headline headline) {
        this.headline = headline;
    }

    @JsonProperty("WorkExperience")
    @JacksonXmlProperty(localName = "WorkExperience", namespace = Namespace.NAMESPACE)
    @JacksonXmlElementWrapper(localName = "WorkExperienceList", namespace = Namespace.NAMESPACE)
    public List<WorkExperience> getWorkExperienceList() {
        return withPreferences(workExperienceList, "WorkExperience");
    }

    public void setWorkExperienceList(List<WorkExperience> workexperiencelist) {
        this.workExperienceList = workexperiencelist;
    }

    @JsonProperty("Education")
    @JacksonXmlProperty(localName = "Education", namespace = Namespace.NAMESPACE)
    @JacksonXmlElementWrapper(localName = "EducationList", namespace = Namespace.NAMESPACE)
    public List<Education> getEducationList() {
        return withPreferences(educationList, "Education");
    }

    public void setEducationList(List<Education> educationlist) {
        this.educationList = educationlist;
    }

    @JsonProperty("Skills")
    @JacksonXmlProperty(localName = "Skills", namespace = Namespace.NAMESPACE)
    public Skills getSkills() {
        return withPreferences(skills, "Skills");
    }

    public void setSkills(Skills skills) {
        this.skills = skills;
    }

    @JsonProperty("Achievement")
    @JacksonXmlProperty(localName = "Achievement", namespace = Namespace.NAMESPACE)
    @JacksonXmlElementWrapper(localName = "AchievementList", namespace = Namespace.NAMESPACE)
    public List<Achievement> getAchievementList() {
        return withDocument(achievementList, getDocument());
    }

    public void setAchievementList(List<Achievement> achievementlist) {
        this.achievementList = achievementlist;
    }

    @JsonProperty("ReferenceTo")
    @JacksonXmlProperty(localName = "ReferenceTo", namespace = Namespace.NAMESPACE)
    @JacksonXmlElementWrapper(localName = "Documentation", namespace = Namespace.NAMESPACE)
    public List<ReferenceTo> getDocumentation() {
        return withDocument(documentation, getDocument());
    }

    public void setDocumentation(List<ReferenceTo> documentation) {
        this.documentation = IdRefSafeList.getInstance(documentation);
    }

    /**
     * ************************************************************************
     */
    @Override
    public void applyDefaultPreferences(List<PrintingPreference> newPrefs) {

        applyDefaultPreferences(getIdentification(), Identification.class,
                "Identification", newPrefs);

        applyDefaultPreferences(
                (PrintableList<WorkExperience>) getWorkExperienceList(),
                WorkExperience.class, "WorkExperience", newPrefs);

        applyDefaultPreferences((PrintableList<Education>) getEducationList(),
                Education.class, "Education", newPrefs);

        applyDefaultPreferences(getSkills(), Skills.class, "Skills", newPrefs);

        applyDefaultPreferences("CEFLanguageLevelsGrid", newPrefs);

        super.applyDefaultPreferences(newPrefs);

    }

    /**
     * Returns the ReferenceTo from the Documentation that corresponds to this
     * Attachment
     *
     * @param attachment
     * @return
     */
    @JsonIgnore
    protected ReferenceTo resolveReferenceTo(Attachment attachment) {

        // Get id
        String id = attachment.getId();
        if (Strings.isNullOrEmpty(id)) {
            return null;
        }

        // Find matching ReferenceTo
        for (ReferenceTo ref : documentation) {
            if (ref == null) {
                continue;
            }
            if (id.equals(ref.getIdref())) {
                // Return the show status
                return ref;
            }
        }
        return null;
    }

    public boolean resolveIfLast(final ReferenceTo referenceTo) {

        if (documentation == null || documentation.isEmpty()) {
            return true;
        }

        if (referenceTo == null) {
            return true;
        }

        String id = referenceTo.getIdref();

        if (Strings.isNullOrEmpty(id)) {
            return true;
        }

        //is last if it is the last in the list
//		int idx = documentation.indexOf(referenceTo);
        ReferenceTo matched = Iterables.find(documentation, new Predicate<ReferenceTo>() {
            public boolean apply(ReferenceTo r) {
                return referenceTo.matches(r);
            }
        });
        int idx = matched.index();
        int noRefs = documentation.size();

        boolean isLastInList = (idx == (noRefs - 1));

        return isLastInList;

    }

    public boolean hasAchievementList() {

        return (achievementList != null && ((WithDocumentList<Achievement>) getAchievementList()).nonEmpty());
    }

    public boolean hasDocumentation() {

        return (documentation != null && ((WithDocumentList<ReferenceTo>) getDocumentation()).nonEmpty());
    }

    /**
     * Decides whether the LearnerInfo contains only Documentation
     *
     * @return
     */
    public boolean hasOnlyDocumentation() {
        return (identification == null
                && headline == null
                && workExperienceList == null
                && educationList == null
                && skills == null
                && achievementList == null
                && (documentation != null && !documentation.isEmpty()));
    }

    /**
     * ****** UTILS for getting info from model *************
     */
    @JsonIgnore
    protected String personSurname() {
        if (identification == null) {
            return "";
        }
        return identification.personSurname();
    }

    @JsonIgnore
    protected CodeLabel personGender() {
        if (identification == null) {
            return null;
        }
        return identification.personGender();
    }

    @JsonIgnore
    protected FileData personalPhoto() {
        if (identification == null) {
            return null;
        }
        return identification.getPhoto();
    }

    @JsonIgnore
    protected FileData personalSignature() {
        if (identification == null) {
            return null;
        }
        return identification.getSignature();
    }

    /**
     * ** Order of Sections ***
     */
    /**
     * Decide the order of work and education sections
     *
     * @return
     */
    @JsonIgnore
    public boolean workExperienceFirst() {
        PrintingPreference pref = this.pref();

        if (pref == null) {
            return true;
        }

        String order = pref.getOrder();

        if (Strings.isNullOrEmpty(order)) {
            return true;
        }

        String[] sections = order.split(" ");

        if (sections.length < MIN_NUMBER_OF_CV_SECTIONS) {
            return true;
        }

        String third = sections[2];

        boolean workFirst = (third.indexOf("WorkExperience") == 0);
        boolean eduFirst = (third.indexOf("Education") == 0);

        if (!workFirst && !eduFirst) {
            return true;
        }

        return (workFirst && !eduFirst);

    }

    @JsonIgnore
    public String getPageBreaksPreference() {
        PrintingPreference pref = this.pref();
        if (pref == null) {
            return null;
        }
        String pageBreaks = pref.getPageBreaks();
        if (Strings.isNullOrEmpty(pageBreaks)) {
            return null;
        }
        return pageBreaks;
    }

    int workExperienceListIndex = 0;

    @JsonIgnore
    public List<WorkExperience> WorkExperienceListWithIndex() {
        return withPreferences(this.indexedList(workExperienceList), "WorkExperience");
    }

    @JsonIgnore
    public boolean breakPageBeforeWorkExperience() {
        boolean breakBeforeSection = false;
        if (workExperienceList.get(workExperienceListIndex) != null) {
            return breakBeforeSection("WorkExperience" + String.valueOf(workExperienceListIndex++));
        } else {
            return breakBeforeSection;
        }
    }

    int educationListIndex = 0;

    @JsonIgnore
    public List<Education> EducationListWithIndex() {
        return withPreferences(this.indexedList(educationList), "Education");
    }

    @JsonIgnore
    public boolean breakPageBeforeEducation() {
        boolean breakBeforeSection = false;
        if (educationList.get(educationListIndex) != null) {
            return breakBeforeSection("Education" + String.valueOf(educationListIndex++));
        } else {
            return breakBeforeSection;
        }
    }

    int achievementListIndex = 0;

    @JsonIgnore
    public List<Achievement> AchievementListWithIndex() {
        return withPreferences(this.indexedList(achievementList), "Achievement");
    }

    @JsonIgnore
    public boolean breakPageBeforeAchievement() {
        boolean breakBeforeSection = false;
        if (achievementList.get(achievementListIndex) != null) {
            return breakBeforeSection("Achievement" + String.valueOf(achievementListIndex++));
        } else {
            return breakBeforeSection;
        }
    }

    @JsonIgnore
    public boolean breakPageBeforeWorkEducation() {
        return breakBeforeSection("WorkEducation");
    }

    @JsonIgnore
    public boolean breakPageBeforeSkills() {
        return breakBeforeSection("Skills");
    }

    @JsonIgnore
    public boolean breakPageBeforeSkillsCommunication() {
        return breakBeforeSection("SkillsCommunication");
    }

    @JsonIgnore
    public boolean breakPageBeforeSkillsOrganisational() {
        return breakBeforeSection("SkillsOrganisational");
    }

    @JsonIgnore
    public boolean breakPageBeforeSkillsJobRelated() {
        return breakBeforeSection("SkillsJobRelated");
    }

    @JsonIgnore
    public boolean breakPageBeforeSkillsComputer() {
        return breakBeforeSection("SkillsComputer");
    }

    @JsonIgnore
    public boolean breakPageBeforeSkillsOther() {
        return breakBeforeSection("SkillsOther");
    }

    @JsonIgnore
    public boolean breakPageBeforeSkillsDriving() {
        return breakBeforeSection("SkillsDriving");
    }

    @JsonIgnore
    public boolean breakPageBeforeAchievements() {
        return breakBeforeSection("Achievement");
    }

    @JsonIgnore
    public boolean breakPageBeforeAttachments() {
        return breakBeforeSection("ReferenceTo");
    }

    @JsonIgnore
    public boolean breakBeforeSection(String section) {
        if (getPageBreaksPreference() != null) {
            List pageBreakSections = Arrays.asList(getPageBreaksPreference().trim().split(" "));
            return pageBreakSections.contains(section);
        }
        return false;
    }

    @JsonIgnore
    public boolean showCEFRGrid() {
        SkillsPassport model = document();
        if (model == null) {
            return false;
        }
        return model.showCEFRGrid();
    }

    @JsonIgnore
    @Override
    public boolean checkEmpty() {
        return ((identification == null || (identification != null && identification.checkEmpty()))
                && (headline == null || (headline != null && headline.checkEmpty()))
                && (workExperienceList == null
                || (workExperienceList != null && ((PrintableList<WorkExperience>) getWorkExperienceList()).checkEmpty()))
                && (educationList == null
                || (educationList != null && ((PrintableList<Education>) getEducationList()).checkEmpty()))
                && (skills == null || (skills != null && skills.checkEmpty()))
                && (achievementList == null
                || (achievementList != null && ((WithDocumentList<Achievement>) getAchievementList()).checkEmpty()))
                && (documentation == null
                || (documentation != null && ((WithDocumentList<ReferenceTo>) getDocumentation()).checkEmpty())));
    }
}
