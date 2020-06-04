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
import java.util.Collection;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.xml.transform.Transformer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import com.google.common.base.Strings;
import com.google.common.collect.Collections2;

import europass.ewa.collections.Predicates;
import europass.ewa.enums.EuropassDocumentType;
import europass.ewa.model.decorator.WithPreferences;
import europass.ewa.model.wrapper.ModelContainer;

/**
 * This is the main object representing a SkillsPassport.
 *
 * A SkillsPassport is characterised by a specific Locale and consists of the
 * following parts:
 *
 * <ol>
 * <li><strong>DocumentInfo: </strong> Including information about the specific
 * Document, such as dates, xsd version, generation services, etc</li>
 * <li><strong>PrintingPreferences: A set of configurations per document,
 * concerning about how to print each one.</strong></li>
 * <li><strong>LearnerInfo: The actual information about the learner's
 * identification, experiences, skills and achievements.</strong></li>
 * <li><strong>Attachments: A list of documents attached to this SkillsPassport.
 * This will actually translate to bytes, to be included in the produced XML
 * file. </strong></li>
 * </ol>
 *
 * @author ekar
 *
 */
// For JsonPropertyOrder, check Mixins for JSON and XML @JsonPropertyOrder
@JacksonXmlRootElement(localName = "SkillsPassport", namespace = Namespace.NAMESPACE)
@JsonRootName("SkillsPassport")
@JsonIgnoreProperties(ignoreUnknown = true)
public class SkillsPassport implements ModelContainer {

    private static final Logger LOG = LoggerFactory.getLogger(SkillsPassport.class);

    private Locale locale;

    private DocumentInfo documentInfo;

    private Map<String, List<PrintingPreference>> documentPrintingPrefs;

    private LearnerInfo learnerInfo;

    private List<Attachment> attachmentList;

    private CoverLetter coverLetter;

    @JsonIgnore
    private Map<String, List<ReferenceTo>> sectionsOfReferenceTo = null;

    @JsonIgnore
    private List<ReferenceTo> referenceToSections = null;

    @JsonIgnore
    private AttachmentsInfo attachmentsInfo;

    @JsonIgnore
    private String filename;

    @JsonIgnore
    private String simpleFilename;

    public SkillsPassport() {
    }

    @JacksonXmlProperty(isAttribute = true, localName = "xmlns:xsi")
    public String getXsiNamespace() {
        return Namespace.XSI_NAMESPACE;
    }

    @JacksonXmlProperty(isAttribute = true, localName = "xsi:schemaLocation")
    public String getSchemaLocation() {
        return Namespace.getSchemaLocation();
    }

    @JsonProperty("Locale")
    @JacksonXmlProperty(isAttribute = true, localName = "locale")
    public Locale getLocale() {
        return locale;
    }

    public void setLocale(Locale locale) {
        this.locale = locale;
    }

    @JsonProperty("DocumentInfo")
    @JacksonXmlProperty(localName = "DocumentInfo", namespace = Namespace.NAMESPACE)
    public DocumentInfo getDocumentInfo() {
        if (documentInfo != null) {
            documentInfo.withLocale(locale);
        }
        return documentInfo;
    }

    public void setDocumentInfo(DocumentInfo documentInfo) {
        this.documentInfo = documentInfo;
    }

    @JsonProperty("PrintingPreferences")
    @JacksonXmlProperty(localName = "PrintingPreferences", namespace = Namespace.NAMESPACE)
    public Map<String, List<PrintingPreference>> getDocumentPrintingPrefs() {
        return documentPrintingPrefs;
    }

    public void setDocumentPrintingPrefs(Map<String, List<PrintingPreference>> documentPrintingPrefs) {
        this.documentPrintingPrefs = documentPrintingPrefs;
    }

    @JsonProperty("LearnerInfo")
    @JacksonXmlProperty(localName = "LearnerInfo", namespace = Namespace.NAMESPACE)
    public LearnerInfo getLearnerInfo() {
        return withPreferences(learnerInfo, "LearnerInfo");
    }

    public void setLearnerInfo(LearnerInfo learnerinfo) {
        this.learnerInfo = learnerinfo;
    }

    @JsonProperty("Attachment")
    @JacksonXmlProperty(localName = "Attachment", namespace = Namespace.NAMESPACE)
    @JacksonXmlElementWrapper(localName = "AttachmentList", namespace = Namespace.NAMESPACE)
    public List<Attachment> getAttachmentList() {
        return attachmentList;
    }

    public void setAttachmentList(List<Attachment> attachmentList) {
        this.attachmentList = attachmentList;
    }

    @JsonProperty("CoverLetter")
    @JacksonXmlProperty(localName = "CoverLetter", namespace = Namespace.NAMESPACE)
    public CoverLetter getCoverLetter() {
        return withPreferences(coverLetter, "CoverLetter");
    }

    public void setCoverLetter(CoverLetter coverLetter) {
        this.coverLetter = coverLetter;
    }

    /**
     * List of attachments ordered by the order that the respective idrefs
     * appear in the List of ReferenceTo of the LearnerInfo.Documentation
     *
     * When no LearnerInfo or LearnerInfo.Documentation we return the
     * attachmentList as is
     *
     * @return
     */
    @JsonIgnore
    public List<Attachment> getOrderedAttachmentList() {

        if (attachmentList == null || (attachmentList != null && attachmentList.isEmpty())) {
            return Collections.emptyList();
        }

        if (learnerInfo == null) {
            return attachmentList;
        }

        List<ReferenceTo> documentation = learnerInfo.getDocumentation();

        if (documentation == null || (documentation != null && documentation.isEmpty())) {
            return attachmentList;
        }

        List<Attachment> unmodifiableList = Collections.unmodifiableList(attachmentList);

        List<Attachment> orderedAttachmentList = new ArrayList<Attachment>(unmodifiableList);

        Collections.sort(orderedAttachmentList, new ReferenceToComparator(learnerInfo));

        return orderedAttachmentList;
    }

    /**
     * ******* PRINTING PREFERENCES************************************
     */
    @JsonIgnore
    private Map<String, DefaultActivePreferences> activePrefs = new HashMap<String, DefaultActivePreferences>();/*new DefaultActivePreferences();*/

    /**
     * Returns the name that is valid for the preference of this document. If it
     * is ESP or ECV_ESP, then use ECV.
     *
     * @return
     */
    @JsonIgnore
    public String getPrefDocumentName() {
        EuropassDocumentType docType = this.returnDocumentType();
        if (EuropassDocumentType.UNKNOWN.equals(docType)) {
            docType = EuropassDocumentType.ECV;
        }
        return docType.getPreferencesAcronym();
    }

    /**
     * Initiates the active preferences with:
     * <ol>
     * <li><b>The current document preferences</b>
     * <em>in case there are no document preferences, an empty list is used</em>
     * </li>
     * <li><b>The default document preferences</b>
     * <em>which are provided by the ConversionModule.class</em></li>
     * </ol>
     *
     * @param name
     * @param defaults
     */
    @SuppressWarnings("unchecked")
    public void activatePreferences(String name, Map<String, PrintingPreference> defaults) {

        DefaultActivePreferences thisActivePrefs = activePrefs.get(name);

        if (thisActivePrefs == null) {
            thisActivePrefs = new DefaultActivePreferences();
            thisActivePrefs.setName(name);
            activePrefs.put(name, thisActivePrefs);
        }

        List<PrintingPreference> providedPrefs = (documentPrintingPrefs != null && documentPrintingPrefs.get(name) != null ? documentPrintingPrefs.get(name) : Collections.EMPTY_LIST);

        thisActivePrefs.load(name, providedPrefs, defaults);
    }

    /**
     * ONLY Applies a list of default preferences. The model objects that are
     * null, will remain so. This exists for the cases where it is not necessary
     * to have a full non-null - yet possibly empty model.
     *
     * @param document the string name of the document, e.g. ECV or ELP
     */
    public void applyDefaultPreferences() {
        this.applyDefaultPreferences(null);
    }

    public void applyDefaultPreferences(String document) {
        String name = document;

        //When no specific document is supplied, decide based on the current model.
        if (Strings.isNullOrEmpty(name)) {
            //use the set type, when none specific is given
            name = this.getPrefDocumentName();
        }

        DefaultActivePreferences thisActivePrefs = activePrefs.get(name);

        if (thisActivePrefs == null) {
            throw new IllegalStateException(
                    "SkillsPassport:applyDefaultPreferences - Cannot apply default preferences before calling activatePreferences ");
        }
        /*
		 * Initiate a new List of Preferences. By calling
		 * .applyDefaultPreferences to the LearnerInfo a series of getters
		 * execute with an empty list. The empty list is populated from the
		 * DefaultActivePreferences implementation. According to this it will
		 * add a preference to the empty list if found in the Document
		 * Preferences or if found in the Default Preferences.
         */
        List<PrintingPreference> prefs = new ArrayList<PrintingPreference>();

        // ===== LearnerInfo =====
        LearnerInfo info = withPreferences(name, learnerInfo, "LearnerInfo");
        if (info == null) {
            try {
                info = withPreferences(name, LearnerInfo.class.newInstance(), "LearnerInfo");
            } catch (Exception e) {
                // throws InstantiationException, IllegalAccessException
                LOG.error("Uunable to instantiate object from class '" + LearnerInfo.class.getName() + "'", e);
            }
        }
        if (info != null) {
            //call with empty list, which will be populated as the model getters are called
            info.applyDefaultPreferences(prefs);
        }
        // ===== Cover Letter =====
        CoverLetter coverLetter = withPreferences(name, this.coverLetter, "CoverLetter");

        if (coverLetter == null) {
            try {
                coverLetter = withPreferences(name, CoverLetter.class.newInstance(), "CoverLetter");
            } catch (Exception e) {
                // throws InstantiationException, IllegalAccessException
                LOG.error("Unable to instantiate object from class '" + LearnerInfo.class.getName() + "'", e);
            }
        }
        if (coverLetter != null) {
            //call with empty list, which will be populated as the model getters are called
            coverLetter.applyDefaultPreferences(prefs);
            this.coverLetter = coverLetter;
        }

        // case no printing preferences are present
        if (documentPrintingPrefs == null) {
            documentPrintingPrefs = new HashMap<String, List<PrintingPreference>>();
        }
        //Finally replace with the filled-in list and re-activate
        documentPrintingPrefs.put(name, prefs);
        thisActivePrefs.load(name, prefs, thisActivePrefs.defaultPrefs);
    }

    /**
     * Initiates the path of filling-in the Printing Preference-related
     * information
     *
     * @param object
     * @param prefKey
     * @return
     */
    <T extends WithPreferences> T withPreferences(String document, T object, String prefKey) {
        if (object != null) {
            object.withPreferences(this, activePrefs.get(document), prefKey, locale);
        }
        return object;
    }

    <T extends WithPreferences> T withPreferences(T object, String prefKey) {
        return this.withPreferences(this.getPrefDocumentName(), object, prefKey);
    }

    /**
     * Retrieve preference related to the Grid. If no preferences and no ELP,
     * then false Otherwise true.
     *
     * Then search for the specific preferences. If it does not exists, then
     * true since the document is ELP. If it does, then respect its show value.
     *
     * @return
     */
    @JsonIgnore
    protected boolean showCEFRGrid() {
        boolean isELP = returnDocumentType().equals(EuropassDocumentType.ELP);
        if (documentPrintingPrefs == null && !isELP) {
            return false;
        }
        if (documentPrintingPrefs == null && isELP) {
            return true;
        }

        // 1. Get the List of PrintingPreferences for the current Document
        List<PrintingPreference> prefs = documentPrintingPrefs.get(EuropassDocumentType.ELP.getPreferencesAcronym());

        Collection<PrintingPreference> matchedPrefs = Collections2.filter(prefs, Predicates.containsPattern(Predicates.CEFR_GRID_REF));

        if (matchedPrefs.size() == 0) {
            return true;
        }
        for (PrintingPreference pref : matchedPrefs) {
            return pref.getShow();
        }
        return true;
    }

    @JsonIgnore
    public Map<String, List<ReferenceTo>> getSectionOfReferenceTo() {
        if (sectionsOfReferenceTo == null) {
            this.sectionOfReferenceTo();
        }
        return sectionsOfReferenceTo;
    }

    /**
     * Prepares the a map of sections that accept ReferenceTo
     */
    @JsonIgnore
    public void sectionOfReferenceTo() {

        LearnerInfo learnerInfo = this.getLearnerInfo();

        if (sectionsOfReferenceTo != null) {
            return;
        }
        if (learnerInfo == null) {
            sectionsOfReferenceTo = Collections.emptyMap();
            return;
        }

        sectionsOfReferenceTo = new HashMap< String, List<ReferenceTo>>();

        List<ReferenceTo> annex = learnerInfo.getDocumentation();
        if (annex != null && annex.size() > 0) {
            sectionsOfReferenceTo.put(learnerInfo.prefKey(), learnerInfo.getDocumentation());
        }
        List<WorkExperience> works = learnerInfo.getWorkExperienceList();
        if (works != null && works.size() > 0) {
            this.sectionOfReferenceTo(sectionsOfReferenceTo, works);
        }
        List<Education> education = learnerInfo.getEducationList();
        if (education != null && education.size() > 0) {
            this.sectionOfReferenceTo(sectionsOfReferenceTo, education);
        }
        List<Achievement> achievements = learnerInfo.getAchievementList();
        if (achievements != null && achievements.size() > 0) {
            this.sectionOfReferenceTo(sectionsOfReferenceTo, achievements);
        }
        Skills skills = learnerInfo.getSkills();
        if (skills != null) {
            LinguisticSkills linguistic = skills.getLinguistic();
            if (linguistic != null) {
                List<LinguisticSkill> foreign = linguistic.getForeignLanguage();
                if (foreign != null && foreign.size() > 0) {
                    this.sectionOfReferenceTo(sectionsOfReferenceTo, foreign);
                }
            }
            GenericSkill communication = skills.getCommunication();
            if (communication != null) {
                sectionsOfReferenceTo.put(communication.prefKey(), communication.getReferenceToList());
            }
            GenericSkill organisational = skills.getOrganisational();
            if (organisational != null) {
                sectionsOfReferenceTo.put(organisational.prefKey(), organisational.getReferenceToList());
            }
            GenericSkill jobRelated = skills.getJobRelated();
            if (jobRelated != null) {
                sectionsOfReferenceTo.put(jobRelated.prefKey(), jobRelated.getReferenceToList());
            }
            ComputerSkill computer = skills.getComputer();
            if (computer != null) {
                sectionsOfReferenceTo.put(computer.prefKey(), computer.getReferenceToList());
            }
            GenericSkill other = skills.getOther();
            if (other != null) {
                sectionsOfReferenceTo.put(other.prefKey(), other.getReferenceToList());
            }
            DrivingSkill driving = skills.getDriving();
            if (driving != null) {
                sectionsOfReferenceTo.put(driving.prefKey(), driving.getReferenceToList());
            }
        }
        return;
    }

    private <E extends ReferenceToReceptor & WithPreferences> void sectionOfReferenceTo(
            Map<String, List<ReferenceTo>> sectionsOfReferenceTo,
            List<E> list) {
        for (E item : list) {
            if (item == null) {
                continue;
            }
            sectionsOfReferenceTo.put(item.prefKey(), item.listOfReferenceTo());
        }
    }

    /**
     * If The model has attachments
     *
     * @return
     */
    @JsonIgnore
    public boolean hasAttachments() {
        return attachmentList != null && attachmentList.size() > 0;
    }

    /**
     * Resolves an Attachment based on the idRef of the ReferenceTo
     *
     * @param reference
     * @return
     */
    protected Attachment resolve(ReferenceTo reference) {
        if (attachmentList == null) {
            return null;
        }

        for (Attachment a : attachmentList) {
            if (a.getId().equals(reference.getIdref())) {
                a.setRelatedSection(reference.refKey());
                return a;
            }
        }
        return null;
    }

    @JsonIgnore
    public void setAttachmentsInfo(AttachmentsInfo attachmentsInfo) {
        this.attachmentsInfo = attachmentsInfo;
    }

    @JsonIgnore
    public AttachmentsInfo getAttachmentsInfo() {
        if (attachmentsInfo == null) {
            attachmentsInfo = new AttachmentsInfo();
        }
        return attachmentsInfo;
    }

    /**
     * Returns a boolean whether the current ReferenceTo is the last one in the
     * List. It will return true if indeed it is the last in the list, or if it
     * may not be the last in the list, but ALL those that follow should not be
     * displayed.
     *
     * @param referenceTo
     * @return
     */
    @JsonIgnore
    public boolean resolveIfLast(ReferenceTo referenceTo) {

        if (learnerInfo == null) {
            return true;
        }

        return learnerInfo.resolveIfLast(referenceTo);
    }

    /**
     * *****************************************************
     */
    /**
     * *****************************************************
     */
    /**
     * ***** HTML to ODT Transformer specifics *************
     */
    @JsonIgnore
    private Transformer htmlTransformer = null;

    @JsonIgnore
    public void setTranformer(Transformer htmlTransformer) {
        this.htmlTransformer = htmlTransformer;
    }

    @JsonIgnore
    public Transformer getTransformer() {
        return this.htmlTransformer;
    }

    /**
     * *****************************************************
     */
    /**
     * Retrieves the document type based on the information within the model If
     * no DocumentType is set in the model, then we try to infer the document
     * type based on the existence of attachments. If no DocumentType is set and
     * there are no attachments, the document type will be ECV.
     *
     * @return enum
     */
    @JsonIgnore
    public EuropassDocumentType returnDocumentType() {
        EuropassDocumentType finalType = EuropassDocumentType.UNKNOWN;

        if (documentInfo != null) {
            finalType = documentInfo.getDocumentType() == null ? EuropassDocumentType.UNKNOWN : documentInfo.getDocumentType();
        }
        //When we cannot conclude on the document type, we try to decide based on the attachments
        if (EuropassDocumentType.UNKNOWN.equals(finalType)) {
            boolean hasAttachments = (this.hasAttachments());
            if (!hasAttachments) {
                finalType = EuropassDocumentType.ECV;
            }
            if (hasAttachments) {
                finalType = EuropassDocumentType.ECV_ESP;
            }
        }
        return finalType;
    }

    /**
     * Update the Document type
     */
    @JsonIgnore
    public void updateDocumentType(EuropassDocumentType type) {
        if (documentInfo == null) {
            documentInfo = new DocumentInfo();
        }
        documentInfo.setDocumentType(type);
    }

    /**
     * Update the Document Generator
     */
    @JsonIgnore
    public void updateDocumentGenerator(String generator) {
        if (Strings.isNullOrEmpty(generator)) {
            return;
        }

        if (documentInfo == null) {
            documentInfo = new DocumentInfo();
        }
        documentInfo.setGenerator(generator);
    }

    /**
     * Update the Document XSD Version
     */
    @JsonIgnore
    public void updateDocumentXSDVersion(String xsdVersion) {
        if (Strings.isNullOrEmpty(xsdVersion)) {
            return;
        }

        if (documentInfo == null) {
            documentInfo = new DocumentInfo();
        }
        documentInfo.setXsdversion(xsdVersion);
    }

    /**
     * ****** UTILS for getting info from model *************
     */
    @JsonIgnore
    public String personSurname() {
        if (learnerInfo == null) {
            return "";
        }
        return learnerInfo.personSurname();
    }

    @JsonIgnore
    public CodeLabel personGender() {
        if (learnerInfo == null) {
            return null;
        }
        return learnerInfo.personGender();
    }

    @JsonIgnore
    public FileData personalPhoto() {
        if (learnerInfo == null) {
            return null;
        }
        return learnerInfo.personalPhoto();
    }

    @JsonIgnore
    public FileData personalSignature() {
        if (learnerInfo == null) {
            return null;
        }
        return learnerInfo.personalSignature();
    }

    /**
     * returns the last's download filename
     *
     * @return
     */
    @JsonIgnore
    public String getFilename() {
        return filename;
    }

    /**
     * sets the filename of the last download
     *
     * @param filename
     */
    @JsonIgnore
    public void setFilename(String filename) {
        this.filename = filename;
    }

    @JsonIgnore
    public void setSimpleFilename(String simpleFilename) {
        this.simpleFilename = simpleFilename;
    }

    @JsonIgnore
    public String getSimpleFilename() {
        return simpleFilename;
    }

    // **** CEFR URL ****/
    @JsonIgnore
    private static final String CEFR_URL = "http://europass.cedefop.europa.eu/_LOCALE_/resources/european-language-levels-cefr";

    @JsonIgnore
    public String cefrURL() {
        Locale docLocale = locale;
        if (docLocale == null) {
            docLocale = Locale.ENGLISH;
        }

        return CEFR_URL.replace("_LOCALE_", docLocale.getLanguage().toString());
    }

    // **** ICT URL ****/
    //TODO SHOULD CHANGE IT TO A VALID ICT LINK A.S.A.P. !!!
    @JsonIgnore
    private static final String ICT_URL = "http://europass.cedefop.europa.eu/_LOCALE_/resources/digital-competences";

    @JsonIgnore
    public String ictURL() {
        Locale docLocale = locale;
        if (docLocale == null) {
            docLocale = Locale.ENGLISH;
        }

        return ICT_URL.replace("_LOCALE_", docLocale.getLanguage().toString());
    }

    // --- AS MODEL CONTAINER
    @JsonIgnore
    @Override
    public SkillsPassport getModel() {
        return this;
    }

    /**
     * EWA-1668 : Handle gracefully breaking changes that took place in frames
     * of XML v3.3.0 release
     *
     * Eliminate faulty codes of deprecated occupation values
     */
    protected static final String[] ELIMINATED_OCCUPATION_CODES = {"21490", "82230"};

    @JsonIgnore
    public void eliminateOccupationCodes() {
        LearnerInfo info = this.getLearnerInfo();
        if (info != null) {

            //Eliminate headline occupation description code
            Headline headline = info.getHeadline();
            if (headline != null) {
                CodeLabel codeLabel = headline.getDescription();
                if (codeLabel != null) {
                    String code = codeLabel.getCode();
                    if (!Strings.isNullOrEmpty(code)) {

                        for (String excluded : ELIMINATED_OCCUPATION_CODES) {

                            if (code.equals(excluded)) {
                                info.getHeadline().getDescription().setCode(null);
                                this.setLearnerInfo(info);
                                break;
                            }
                        }
                    }
                }
            }

            //Eliminate work experience occupation description code
            List<WorkExperience> workExpList = info.getWorkExperienceList();
            if (workExpList != null) {

                for (WorkExperience exp : workExpList) {

                    CodeLabel position = exp.getPosition();
                    if (position != null) {

                        String code = position.getCode();

                        if (!Strings.isNullOrEmpty(code)) {

                            for (String excluded : ELIMINATED_OCCUPATION_CODES) {

                                if (code.equals(excluded)) {
                                    position.setCode(null);
                                    info.getWorkExperienceList().get(exp.index()).setPosition(position);
                                    this.setLearnerInfo(info);
                                    break;
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * *********************************************************************************************
     */
    /**
     * *********************************************************************************************
     */
    private static final EnumSet<EuropassDocumentType> DOC_WITH_PREFS
            = EnumSet.of(EuropassDocumentType.ECV, EuropassDocumentType.ELP, EuropassDocumentType.ECL);

    public void activatePreferences(Map<EuropassDocumentType, Map<String, PrintingPreference>> defaultPrefs) {
        for (EuropassDocumentType doc : DOC_WITH_PREFS) {
            String acronym = doc.getPreferencesAcronym();
            this.activatePreferences(acronym, defaultPrefs.get(doc));
            this.applyDefaultPreferences(acronym);
        }
    }
}
