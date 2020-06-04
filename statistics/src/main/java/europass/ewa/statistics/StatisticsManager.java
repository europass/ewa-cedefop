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
package europass.ewa.statistics;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.IllegalFieldValueException;
import org.joda.time.Years;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Joiner;
import com.google.common.base.Strings;

import europass.ewa.enums.ConversionFileType;
import europass.ewa.enums.EuropassDocumentType;
import europass.ewa.enums.ExportDestination;
import europass.ewa.model.Achievement;
import europass.ewa.model.Address;
import europass.ewa.model.Attachment;
import europass.ewa.model.CEFRLevel;
import europass.ewa.model.Certificate;
import europass.ewa.model.CodeLabel;
import europass.ewa.model.ContactAddress;
import europass.ewa.model.ContactInfo;
import europass.ewa.model.ContactMethod;
import europass.ewa.model.Demographics;
import europass.ewa.model.DocumentInfo;
import europass.ewa.model.DrivingSkill;
import europass.ewa.model.Education;
import europass.ewa.model.EmployerOrganisation;
import europass.ewa.model.Headline;
import europass.ewa.model.Identification;
import europass.ewa.model.JDate;
import europass.ewa.model.LearnerInfo;
import europass.ewa.model.LinguisticSkill;
import europass.ewa.model.LinguisticSkills;
import europass.ewa.model.Organisation;
import europass.ewa.model.OrganisationalContactInfo;
import europass.ewa.model.Period;
import europass.ewa.model.Skills;
import europass.ewa.model.SkillsPassport;
import europass.ewa.model.WorkExperience;
import europass.ewa.statistics.data.StatsAchievement;
import europass.ewa.statistics.data.StatsDetails;
import europass.ewa.statistics.data.StatsEducation;
import europass.ewa.statistics.data.StatsEntry;
import europass.ewa.statistics.data.StatsExperience;
import europass.ewa.statistics.data.StatsForeignLanguages;
import europass.ewa.statistics.data.StatsLinguisticCertificate;
import europass.ewa.statistics.data.StatsMotherLanguage;
import europass.ewa.statistics.data.StatsNationality;
import europass.ewa.statistics.data.StatsWorkExperience;

public class StatisticsManager {

    private final Joiner PIPE_STRING_JOINER = Joiner.on("|").skipNulls();

    private final SkillsPassport esp;

    private StatsEntry statsEntry;

    private EuropassDocumentType document;
    private ConversionFileType fileType;
    private DocumentGenerator generator;
    private ExportDestination exportTo;

    private Years totalEduYears;
    private Years totalWorkYears;

    private static final Logger LOG = LoggerFactory.getLogger(DatabaseStatisticsLogger.class);

    StatisticsManager(SkillsPassport esp) {
        this.esp = esp;
    }

    StatsEntry prepare() {
        return this.prepare(null, null, null, null);
    }

    StatsEntry prepare(EuropassDocumentType document, ConversionFileType fileType, DocumentGenerator generator, ExportDestination exportTo) {
        this.statsEntry = new StatsEntry();

        this.totalEduYears = Years.years(0);
        this.totalWorkYears = Years.years(0);

        //Fail quickly if esp is null
        if (this.esp == null) {
            return this.statsEntry;
        }

        this.fileType = fileType == null ? ConversionFileType.UNKNOWN : fileType;
        this.document = document == null ? EuropassDocumentType.UNKNOWN : document;
        this.generator = generator == null ? DocumentGenerator.UNKNOWN : generator;
        this.exportTo = exportTo == null ? ExportDestination.UNKNOWN : exportTo;

        this.setMetadata();
        this.setDocumentInfo();

        this.setLearnerInfo();

        this.setAttachmentInfo();

        return this.statsEntry;
    }

    // ==== METADATA ====
    private void setMetadata() {
        // Document Language
        Locale docLocale = esp.getLocale();
        if (docLocale != null) {
            statsEntry.setLanguage(docLocale.toString());
        }

        // file format
        if (fileType != null) {
            statsEntry.setFileFormat(fileType.getDescription());
        }

        // export destination
        if (exportTo != null) {
            statsEntry.setExportTo(exportTo.getDescription());;
        }
    }
    // ==== DOCUMENT INFO ====

    private void setDocumentInfo() {
        DocumentInfo docInfo = esp.getDocumentInfo();

        if (docInfo == null) {
            return;
        }

        //Document Type if set to Unknown
        statsEntry.setDocumentType(
                EuropassDocumentType.UNKNOWN.equals(document)
                ? docInfo.getDocumentType() == null ? EuropassDocumentType.UNKNOWN.getAcronym() : docInfo.getDocumentType().getAcronym()
                : document.getAcronym());

        //Generator if set to Unknown
        statsEntry.setGeneratedBy(
                DocumentGenerator.UNKNOWN.equals(generator)
                ? (Strings.isNullOrEmpty(docInfo.getGenerator()) ? DocumentGenerator.UNKNOWN.getDescription() : docInfo.getGenerator())
                : generator.getDescription());

        //Dates
        statsEntry.setCreationDate(DateTime.now(DateTimeZone.UTC));

        DateTime creationDate = docInfo.getCreationDate();
        if (creationDate == null) {
            return;
        }

        DateTime updateDate = docInfo.getLastUpdateDate();
        if (updateDate == null) {
            return;
        }

        statsEntry.setIsNew((creationDate.compareTo(updateDate) == 0));

    }

    /**
     * Set the learner info data
     *
     * @param esp
     * @param statsEntry
     * @param fileType
     */
    private void setLearnerInfo() {
        LearnerInfo learnerinfo = esp.getLearnerInfo();

        if (learnerinfo == null) {
            return;
        }
        // ============ IDENTIFICATION ================
        this.setIdentification(learnerinfo);

        //============= HEADLINE======================
        this.setHeadline(learnerinfo);

        // ============ WORK EXPERIENCE ================
        this.setWorkExperience(learnerinfo);

        // ============ EDUCATION ================
        this.setEducationExperience(learnerinfo);

        // ============= EMAIL ===================
        this.setEmailHash(learnerinfo);

        // ============ PERSONAL SKILLS ================
        this.setSkills(learnerinfo);

        // ============ ACHIEVEMENT LIST ================
        this.setAchievements(learnerinfo);

    }

    private void setEmailHash(LearnerInfo learnerinfo) {
        if (learnerinfo.getIdentification() != null && learnerinfo.getIdentification().getContactInfo() != null
                && learnerinfo.getIdentification().getContactInfo().getEmail() != null) {
            String email = learnerinfo.getIdentification().getContactInfo().getEmail().getContact();
            if (email != null) {
                int hashEmail = email.hashCode(); //zero hash is for empty string				
                statsEntry.setEmailHashCode(hashEmail);
            } else {
                return;
            }
        } else {
            return;
        }
    }

    private void setSkills(LearnerInfo learnerinfo) {
        Skills skills = learnerinfo.getSkills();

        if (skills == null) {
            return;
        }

        // sets mother tongue, foreign languages (level and certificates)
        this.setLinguisticSkills(skills.getLinguistic());

        this.setDrivingSkills(skills.getDriving());
    }

    private void setLinguisticSkills(LinguisticSkills linguisticSkills) {
        if (linguisticSkills == null) {
            return;
        }
        // ============ MOTHER TONGUE ================
        setMotherLanguages(linguisticSkills.getMotherTongue());

        // ============ FOREIGN LANGUAGES ================
        setForeignLanguages(linguisticSkills.getForeignLanguage());
    }

    private void setDrivingSkills(DrivingSkill driving) {
        if (driving == null) {
            return;
        }

        List<String> licences = driving.getDescription();

        if (licences == null) {
            return;
        }

        statsEntry.setSkillsDriving(PIPE_STRING_JOINER.join(licences));
    }

    private void setIdentification(LearnerInfo learnerinfo) {
        Identification identification = learnerinfo.getIdentification();

        if (identification == null) {
            return;
        }

        // === CONTACT INFO ===
        ContactInfo contactInfo = identification.getContactInfo();

        if (contactInfo != null) {
            // --- Country of Postal Address
            statsEntry.setPostalAddressCountry(this.setPostalCountry(contactInfo));
            // --- Telephones
            this.getDetails().setTelephoneTypes(this.setContactMethod(contactInfo.getTelephoneList()));
            // --- IM
            this.getDetails().setImTypes(this.setContactMethod(contactInfo.getInstantMessagingList()));
        }

        // === DEMOGRAPHICS ===
        Demographics demographics = identification.getDemographics();

        if (demographics != null) {
            // Age and Date of Birth
            this.setBirthdate(demographics);
            //Gender
            this.setGender(demographics);
            //Nationalities
            this.setNationalities(demographics);
        }

    }

    /**
     * Instantiate and set bi-directionally the StatsEntry and StatsDetails
     *
     * @return
     */
    private StatsDetails getDetails() {
        if (statsEntry == null) {
            return new StatsDetails();
        }

        if (statsEntry.getDetails() == null) {
            StatsDetails details = new StatsDetails();
            statsEntry.setDetails(details);
            details.setStatsEntry(statsEntry);
        }

        return statsEntry.getDetails();
    }

    private void setHeadline(LearnerInfo learnerinfo) {
        Headline headline = learnerinfo.getHeadline();

        if (headline == null) {
            return;
        }

        //Headline Type
        CodeLabel type = headline.getType();
        statsEntry.setHeadlineType(getKeyOrValue(type));

        //Headline Description
        boolean hasType = type != null;
        boolean hasTypeCode = type != null && !Strings.isNullOrEmpty(type.getCode());
        //Store headline description if 
        //1) type is null, 
        //2) or type is not null, but type.code is null or empty
        //3) or type is not null, type.code is also not null or empty AND type.code is not "personal_statement"
        if (type == null
                || (hasType && Strings.isNullOrEmpty(type.getCode()))
                || (hasTypeCode && !Headline.HEADLINE_CODE_STATS_EXCLUDED.equals(type.getCode()))) {

            statsEntry.setHeadlineDescription(getKeyOrValue(headline.getDescription()));
        }

    }

    private void setBirthdate(Demographics demographics) {
        JDate birthdate = demographics.getBirthdate();
        if (birthdate == null) {
            return;
        }

        DateTime start = convertJDateToDateTimeAtStartOfDay(birthdate);

        //Again, No need to proceed with Age, if there is no birthday
        if (start == null) {
            return;
        }

        //add birthday
        statsEntry.setDateOfBirth(start.toDateTime());
        DateTime end = DateTime.now(DateTimeZone.UTC);
        int age = Years.yearsBetween(start, end).getYears();
        statsEntry.setAge(age);

    }

    private void setGender(Demographics demographics) {
        CodeLabel gender = demographics.getGender();

        if (gender == null) {
            return;
        }

        String genderCode = gender.getCode();
        if (Strings.isNullOrEmpty(genderCode)) {
            genderCode = "U";
        }
        statsEntry.setGender(genderCode.charAt(0));
    }

    /**
     * Utility to calculate the duration of a single Experience
     *
     * @param experience
     * @return
     */
    private void setExperiencePeriod(Period period, StatsExperience statsExp) {

        if (period == null) {
            return;
        }

        JDate from = period.getFrom();

        //No need to proceed with To and Duration, if there is no From
        if (from == null) {
            return;
        }

        DateTime start = convertJDateToDateTimeAtStartOfDay(from);

        //Again, No need to proceed with To and Duration, if there is no From.Year
        if (start == null) {
            return;
        }

        statsExp.setPeriodFrom(start.toDateTime());

        JDate to = period.getTo();
        boolean isCurrent = period.getCurrent();

        if (to == null && !isCurrent) {
            return;
        }

        // Calculate End:
        DateTime end = isCurrent ? DateTime.now(DateTimeZone.UTC).withTimeAtStartOfDay() : convertJDateToDateTimeAtStartOfDay(to);

        statsExp.setPeriodTo(end.toDateTime());

        Integer duration = Years.yearsBetween(start, end).getYears();

        statsExp.setDuration(duration);
    }

    /**
     * Set data related to mother language
     *
     * @param esp
     */
    private void setMotherLanguages(List<LinguisticSkill> motherTongues) {

        if (listNullOrEmpty(motherTongues)) {
            return;
        }

        List<StatsMotherLanguage> statsMotherLangs = new ArrayList<StatsMotherLanguage>();

        for (LinguisticSkill motherTongue : motherTongues) {

            if (motherTongue == null) {
                continue;
            }

            CodeLabel desc = motherTongue.getDescription();

            String motherLangCodeLabel = getKeyOrValue(desc);

            if (motherLangCodeLabel != null) {
                StatsMotherLanguage statsMotherLang = new StatsMotherLanguage();
                statsMotherLang.setLanguage(motherLangCodeLabel);

                statsMotherLangs.add(statsMotherLang);
            }
        }

        if (statsMotherLangs.size() > 0) {
            statsEntry.setMotherLangs(statsMotherLangs);
        }
    }

    private void setForeignLanguages(List<LinguisticSkill> foreignLangs) {
        if (listNullOrEmpty(foreignLangs)) {
            return;
        }

        List<StatsForeignLanguages> statsForeignLanguages = new ArrayList<StatsForeignLanguages>();
        for (LinguisticSkill foreignLang : foreignLangs) {
            if (foreignLang == null) {
                continue;
            }

            CodeLabel desc = foreignLang.getDescription();

            String languageType = getKeyOrValue(desc);

            if (languageType != null) {

                StatsForeignLanguages statsForeignLanguage = new StatsForeignLanguages();

                //Set language name
                statsForeignLanguage.setLanguageType(languageType);

                //CEFR Level
                CEFRLevel level = foreignLang.getProficiencyLevel();
                if (level != null) {
                    statsForeignLanguage.setListeningLevel(level.getListening());
                    statsForeignLanguage.setReadingLevel(level.getReading());
                    statsForeignLanguage.setSpokenInteractionLevel(level.getSpokenInteraction());
                    statsForeignLanguage.setSpokenProductionLevel(level.getSpokenProduction());
                    statsForeignLanguage.setWritingLevel(level.getWriting());
                }

                //Linguistic Certificate
                setLinguisticCertificates(foreignLang.getVerifiedBy(), statsForeignLanguage);

                if (!statsForeignLanguage.checkEmpty()) {
                    statsForeignLanguages.add(statsForeignLanguage);
                }
            }

        }

        if (statsForeignLanguages.size() > 0) {
            statsEntry.setForeignLanguages(statsForeignLanguages);
        }
    }

    private void setLinguisticCertificates(List<Certificate<String>> certificates, StatsForeignLanguages statsLanguage) {

        if (listNullOrEmpty(certificates)) {
            return;
        }

        List<StatsLinguisticCertificate> statsCertificates = new ArrayList<StatsLinguisticCertificate>();

        for (Certificate<String> certificate : certificates) {
            if (certificate == null) {
                continue;
            }

            StatsLinguisticCertificate statCertificate = new StatsLinguisticCertificate();
            //Title
            statCertificate.setTitle(certificate.getTitle());

            //Date
            JDate date = certificate.getDate();
            if (date != null) {
                DateTime dateMd = convertJDateToDateTimeAtStartOfDay(date);
                if (dateMd != null) {
                    statCertificate.setIssueDate(dateMd.toDateTime());
                }
            }

            //Level
            statCertificate.setCefrLevel(certificate.getLevel());

            if (!statCertificate.checkEmpty()) {
                statsCertificates.add(statCertificate);
            }
        }

        if (statsCertificates.size() > 0) {
            statsLanguage.setCertificates(statsCertificates);
        }

    }

    private void setNationalities(Demographics demographics) {

        List<CodeLabel> nationalities = demographics.getNationalityList();

        if (listNullOrEmpty(nationalities)) {
            return;
        }

        List<StatsNationality> statsNationalities = new ArrayList<StatsNationality>();

        //HOT: Bidirectional setting!
        for (CodeLabel nationality : nationalities) {
            if (nationality == null) {
                continue;
            }
            String codeOrLabel = getKeyOrValue(nationality);

            if (codeOrLabel != null) {
                StatsNationality statNat = new StatsNationality();
                statNat.setNationality(codeOrLabel);
//				statNat.setStatsEntry(statsEntry);
                statsNationalities.add(statNat);
            }
        }
        statsEntry.setNationalities(statsNationalities);
    }

    private void setWorkExperience(LearnerInfo learnerinfo) {
        List<WorkExperience> workExperiences = learnerinfo.getWorkExperienceList();

        if (listNullOrEmpty(workExperiences)) {
            return;
        }

        List<StatsWorkExperience> statWorkList = new ArrayList<StatsWorkExperience>();

        for (WorkExperience work : workExperiences) {
            if (work == null) {
                continue;
            }

            StatsWorkExperience statWork = new StatsWorkExperience();

            //From, To, Duration, Total duration
            Period period = work.getPeriod();
            if (period != null) {
                this.setExperiencePeriod(period, statWork);
                Integer duration = statWork.getDuration();
                if (duration != null) {
                    totalWorkYears = totalWorkYears.plus(duration);
                }

            }
            //Work Position
            statWork.setPosition(getKeyOrValue(work.getPosition()));

            //Employer Country and Sector
            EmployerOrganisation org = work.getEmployer();
            if (org != null) {
                statWork.setEmployerSector(getKeyOrValue(org.getSector()));
                statWork.setEmployerCountry(this.setPostalCountry(org.getContactInfo()));
            }

            //HOT! Add Bidirectional mapping
//			statWork.setStatsEntry( statsEntry );
            if (!statWork.checkEmpty()) {
                statWorkList.add(statWork);
            }

        }
        statsEntry.setWorkExperiences(statWorkList);
        statsEntry.setWorkExperienceYears(Integer.valueOf(totalWorkYears.getYears()));

    }

    private void setEducationExperience(LearnerInfo learnerinfo) {
        List<Education> educations = learnerinfo.getEducationList();

        if (listNullOrEmpty(educations)) {
            return;
        }

        List<StatsEducation> statsEduList = new ArrayList<StatsEducation>();

        for (Education edu : educations) {
            if (edu == null) {
                continue;
            }

            StatsEducation statEdu = new StatsEducation();

            //From, To, Duration, Total duration
            Period period = edu.getPeriod();
            if (period != null) {
                this.setExperiencePeriod(period, statEdu);
                Integer duration = statEdu.getDuration();
                if (duration != null) {
                    totalEduYears = totalEduYears.plus(duration);
                }
            }

            //Qualification
            statEdu.setQualification(edu.getTitle());

            //Qualification Level 
            statEdu.setQualificationLevel(getKeyOrValue(edu.getLevel()));

            //Field of Study
            statEdu.setEducationalField(getKeyOrValue(edu.getField()));

            //Educational Country
            Organisation org = edu.getOrganisation();
            if (org != null) {
                statEdu.setOrganisationCountry(this.setPostalCountry(org.getContactInfo()));
            }

            //HOT! Add Bidirectional mapping
//			statEdu.setStatsEntry( statsEntry );
            if (!statEdu.checkEmpty()) {
                statsEduList.add(statEdu);
            }

        }
        statsEntry.setEducationExperiences(statsEduList);
        statsEntry.setEducationYears(Integer.valueOf(totalEduYears.getYears()));

    }

    private void setAchievements(LearnerInfo learnerinfo) {
        List<Achievement> achievements = learnerinfo.getAchievementList();

        if (listNullOrEmpty(achievements)) {
            return;
        }

        List<StatsAchievement> statAchievements = new ArrayList<StatsAchievement>();

        for (Achievement achievement : achievements) {

            if (achievement == null) {
                continue;
            }

            String category = getKeyOrValue(achievement.getTitle());

            if (category != null) {
                StatsAchievement statAchievement = new StatsAchievement();
                statAchievement.setCategory(category);
                statAchievements.add(statAchievement);
            }
        }

        if (statAchievements.size() > 0) {
            statsEntry.setAchievements(statAchievements);
        }
    }

    private void setAttachmentInfo() {
        List<Attachment> list = esp.getAttachmentList();

        if (listNullOrEmpty(list)) {
            return;
        }

        StatsDetails details = this.getDetails();
        details.setNumberOfFiles(list.size());

        List<String> types = new ArrayList<>();
        Long cumulative_size = (long) 0;

        for (Attachment attachment : list) {
            if (attachment == null) {
                continue;
            }
            if (attachment.getMimeType() != null) {
                types.add(attachment.getMimeType());
            }

            if (attachment.getData() != null) {
                cumulative_size += attachment.getData().length;
            }
        }

        if (cumulative_size > 0.0) {
            details.setCumulativeSize(cumulative_size);
        }
        if (types.size() > 0) {
            details.setTypeOfFiles(PIPE_STRING_JOINER.join(types));
        }
    }

    //--- Util re-usable methods --
    private String setContactMethod(List<ContactMethod> contacts) {
        if (contacts == null) {
            return null;
        }

        List<String> uses = new ArrayList<>();

        for (ContactMethod contact : contacts) {
            if (contact == null) {
                continue;
            }
            String use = getKeyOrValue(contact.getUse());
            if (use == null) {
                continue;
            }
            uses.add(use);
        }

        if (uses.size() == 0) {
            return null;
        }

        return PIPE_STRING_JOINER.join(uses);
    }

    /**
     * Personal Address/ Employer Address/ Educational Provider Address
     *
     * @param address
     */
    private String setPostalCountry(ContactInfo contactInfo) {

        if (contactInfo == null) {
            return null;
        }

        Address address = contactInfo.getAddressContact();

        return this.setPostalCountry(address);
    }

    /**
     * Employer Address/ Educational Provider Address
     *
     * @param address
     */
    private String setPostalCountry(OrganisationalContactInfo contactInfo) {

        if (contactInfo == null) {
            return null;
        }

        ContactAddress contactAddress = contactInfo.getAddress();
        if (contactAddress == null) {
            return null;
        }

        Address address = contactAddress.getContact();

        return this.setPostalCountry(address);
    }

    /**
     * Reused for Personal Address/ Employer Address/ Educational Provider
     * Address
     *
     * @param address
     */
    private String setPostalCountry(Address address) {

        if (address == null) {
            return null;
        }

        return getKeyOrValue(address.getCountry());
    }

    /**
     * Get key of option selected, if user had custom input, will return the
     * input string
     *
     * @param codeLabel
     * @return
     */
    private static String getKeyOrValue(CodeLabel codeLabel) {
        if (codeLabel == null) {
            return null;
        }
        String code = codeLabel.getCode();
        String label = codeLabel.getLabel();

        return (Strings.isNullOrEmpty(code))
                ? (Strings.isNullOrEmpty(label) ? null : label.trim())
                : code.trim();
    }

    private static <E extends Object> boolean listNullOrEmpty(List<E> list) {

        if (list == null) {
            return true;
        } else if (list.size() == 0) {
            return true;
        }

        return false;
    }

    private static DateTime convertJDateToDateTimeAtStartOfDay(JDate date) {
        if (date == null) {
            return null;
        }
        Integer year = date.getYear();
        if (year == null || (year <= 1753 && year >= 9999)) {
            return null;
        }
        Integer month = date.getMonth();
        Integer day = date.getDay();
        DateTime dateTime = new DateTime(DateTimeZone.UTC);
        try {
            dateTime = new DateTime(year, month == null ? 1 : month, day == null ? 1 : day, 0, 0, DateTimeZone.UTC);
        } catch (IllegalFieldValueException e) {
            LOG.info("DatabaseStatisticsLogger:log - Invalid DateTime Value", e);
            dateTime = new DateTime(year, 1, 1, 0, 0, DateTimeZone.UTC);
        }

        return dateTime.withTimeAtStartOfDay();
    }
}
