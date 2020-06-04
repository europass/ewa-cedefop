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
package europass.ewa.model.conversion.xml;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hamcrest.CoreMatchers;
import org.junit.Before;
import org.junit.Test;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.name.Names;

import europass.ewa.model.Attachment;
import europass.ewa.model.DrivingSkill;
import europass.ewa.model.FileData;
import europass.ewa.model.JDate;
import europass.ewa.model.LearnerInfo;
import europass.ewa.model.MockObjects;
import europass.ewa.model.Namespace;
import europass.ewa.model.PrintingPreference;
import europass.ewa.model.ReferenceTo;
import europass.ewa.model.SkillsPassport;

public class PojoToXmlTest extends XmlMapperTest {

    @Before
    public void prepareInjections() {
        Guice.createInjector(
                new AbstractModule() {

            @Override
            protected void configure() {
                bindConstant().annotatedWith(Names.named(Namespace.SCHEMA_LOCATION_DEFAULT_PARAM))
                        .to(Namespace.NAMESPACE + " http://europass.cedefop.europa.eu/xml/EuropassSchema_V3.0.xsd");

                requestStaticInjection(Namespace.class);
            }
        }
        );

    }

    @Test
    public void writeHeadline() throws JsonProcessingException {
        SkillsPassport esp = MockObjects.headline();

        XmlMapper m = this.getMapper();

        String xml = m.writeValueAsString(esp);

        assertNotNull("XML produced - ", xml);

        boolean isContained
                = xml.indexOf("<Headline><Type><Code>personal_statement</Code><Label>Personal Statement</Label></Type><Description><Label>This is my personal statement</Label></Description></Headline>") > -1;

        assertThat(
                isContained,
                CoreMatchers.is(true));
    }

    @Test
    public void writeXML() throws JsonProcessingException {
        String xml = this.getMapper().writeValueAsString(MockObjects.complete());
        assertNotNull("XML produced - ", xml);

//		System.out.println( xml );
        /*String filename = "C:\\tmp\\europass\\ewa-conversion\\out_from_pojo" + System.currentTimeMillis()+".xml" ;
		FileUtils.writeStringToFile(new File(filename), xml, "UTF-8");*/
    }

    @Test
    public void writeCreationDateXML() throws JsonProcessingException {
        String xml = this.getMapper().writeValueAsString(MockObjects.complete().getDocumentInfo().getCreationDate());

        assertNotNull("XML produced - ", xml);

        assertThat("Date: ",
                xml,
                is("<?xml version='1.0' encoding='UTF-8'?><DateTime>2012-06-06T00:00:00.000Z</DateTime>"));

    }

    @JacksonXmlRootElement(namespace = Namespace.NAMESPACE)
    private abstract class FileDataMixin {
    }

    @Test
    public void writePhoto() throws JsonProcessingException {
        FileData photo = MockObjects.complete().getLearnerInfo().getIdentification().getPhoto();
        //remove temp uri
        photo.setTmpuri(null);

        XmlMapper m = this.getMapper();

        m.addMixInAnnotations(FileData.class, FileDataMixin.class);

        String xml = m.writeValueAsString(photo);

        assertNotNull("XML produced - ", xml);

        assertThat(
                xml,
                is("<?xml version='1.0' encoding='UTF-8'?><FileData xmlns=\"" + Namespace.NAMESPACE + "\"><MimeType>image/png</MimeType><Data>TXlQaG90bw==</Data></FileData>"));
    }

    @Test
    public void writeAnnex() throws JsonProcessingException {
        ObjectMapper mapper = this.getMapper();

        mapper.configure(SerializationFeature.INDENT_OUTPUT, false);

        SkillsPassport esp = new SkillsPassport();
        LearnerInfo learner = new LearnerInfo();

        List<ReferenceTo> annex = new ArrayList<ReferenceTo>();
        annex.add(new ReferenceTo("ATT_1"));
        annex.add(null);
        annex.add(new ReferenceTo());
        annex.add(new ReferenceTo("ATT_2"));
        annex.add(null);
        learner.setDocumentation(annex);
        esp.setLearnerInfo(learner);

        String xml = this.getMapper().writeValueAsString(esp);

        assertNotNull("XML produced - ", xml);

        String actualXml = "<?xml version='1.0' encoding='UTF-8'?>"
                + "<SkillsPassport xmlns=\"" + Namespace.NAMESPACE + "\" xmlns:xsi=\"" + Namespace.XSI_NAMESPACE + "\" xsi:schemaLocation=\"" + Namespace.NAMESPACE + " " + Namespace.SCHEMA_LOCATION_DEFAULT + "\">"
                + "<LearnerInfo>"
                + "<Documentation>"
                + "<ReferenceTo idref=\"ATT_1\"/>"
                + "<ReferenceTo idref=\"ATT_2\"/>"
                + "</Documentation>"
                + "</LearnerInfo>"
                + "</SkillsPassport>";

        assertThat("Annex XML:", xml, is(actualXml));
    }

    @Test
    public void writeFileData() throws JsonProcessingException {
        String xml = this.getMapper().writeValueAsString(MockObjects.espFileDataObj());

        assertNotNull("XML produced - ", xml);

        String actualXml = "<?xml version='1.0' encoding='UTF-8'?>"
                + "<SkillsPassport xmlns=\"" + Namespace.NAMESPACE + "\" xmlns:xsi=\"" + Namespace.XSI_NAMESPACE + "\" xsi:schemaLocation=\"" + Namespace.NAMESPACE + " " + Namespace.SCHEMA_LOCATION_DEFAULT + "\">"
                + "<AttachmentList>"
                + "<Attachment id=\"ATT_1\">"
                + "<Name>Certificate.pdf</Name>"
                + "<MimeType>application/pdf</MimeType>"
                + "<Data>RklMRUJZVEVTMQ==</Data>"
                + "<TempURI>http://europass.instore.gr/files/file/WR3452ERUTT7534</TempURI>"
                + "<Description>Certificate of Attendance</Description>"
                + "</Attachment>"
                + "<Attachment id=\"ATT_2\">"
                + "<Name>Diploma.pdf</Name>"
                + "<MimeType>application/pdf</MimeType>"
                + "<Data>RklMRUJZVEVTMg==</Data>"
                + "<TempURI>http://europass.instore.gr/files/file/WR3452EPOS3244</TempURI>"
                + "<Description>Engineering Diploma</Description>"
                + "</Attachment>"
                + "</AttachmentList>"
                + "</SkillsPassport>";

        assertThat("FileData XML:", xml, is(actualXml));

    }

    @Test
    public void writeFileDataNoUri() throws JsonProcessingException {

        List<Attachment> atts = new ArrayList<Attachment>();
        atts.add(new Attachment("ATT_1", "My Certificate", "Certificate.pdf", "application/pdf", "http://europass.instore.gr/files/file/WR3452ERUTT7534", "My Certificate".getBytes()));
        SkillsPassport esp = new SkillsPassport();
        esp.setAttachmentList(atts);

        for (Attachment at : esp.getAttachmentList()) {
            at.setData("MYBYTES".getBytes());
            at.setTmpuri(null);
        }

        String xml = this.getMapper().writeValueAsString(esp);

        assertNotNull("XML produced - ", xml);

        String actualXml = "<?xml version='1.0' encoding='UTF-8'?>"
                + "<SkillsPassport xmlns=\"" + Namespace.NAMESPACE + "\" xmlns:xsi=\"" + Namespace.XSI_NAMESPACE + "\" xsi:schemaLocation=\"" + Namespace.NAMESPACE + " " + Namespace.SCHEMA_LOCATION_DEFAULT + "\">"
                + "<AttachmentList>"
                + "<Attachment id=\"ATT_1\">"
                + "<Name>Certificate.pdf</Name>"
                + "<MimeType>application/pdf</MimeType>"
                + "<Data>TVlCWVRFUw==</Data>"
                + "<Description>My Certificate</Description>"
                + "</Attachment>"
                + "</AttachmentList>"
                + "</SkillsPassport>";

        assertThat("FileData XML:", xml, is(actualXml));

    }

    @JacksonXmlRootElement(namespace = Namespace.NAMESPACE)
    private abstract class DrivingSkillMixin {

        @JacksonXmlElementWrapper(localName = "Description", namespace = Namespace.NAMESPACE)
        @JacksonXmlProperty(localName = "Licence", namespace = Namespace.NAMESPACE)
        abstract public List<String> getDescription();

        abstract public void setDescription(List<String> description);
    }

    @Test
    public void writeDrivingXML() throws JsonProcessingException {
        XmlMapper m = this.getMapper();

        m.addMixInAnnotations(DrivingSkill.class, DrivingSkillMixin.class);

        String xml = m.writeValueAsString(MockObjects.complete().getLearnerInfo().getSkills().getDriving());

        assertThat("Driving",
                xml,
                is("<?xml version='1.0' encoding='UTF-8'?><DrivingSkill xmlns=\"" + Namespace.NAMESPACE + "\"><Description><Licence>A</Licence><Licence>B1</Licence></Description></DrivingSkill>"));
    }

    @JacksonXmlRootElement(namespace = Namespace.NAMESPACE)
    private abstract class BirthdateMixin {

        @JsonProperty("Year")
        @JacksonXmlProperty(isAttribute = true, localName = "year")
        abstract public Integer getYear();

        abstract public void setYear(Integer year);

        @JsonProperty("Month")
        @JacksonXmlProperty(isAttribute = true, localName = "month")
        @JsonSerialize(using = GMonthSerialiser.class, as = Integer.class)
        abstract Integer getMonth();

        @JsonDeserialize(using = GMonthDeserialiser.class, as = Integer.class)
        abstract void setMonth(Integer month);

        @JsonProperty("Day")
        @JacksonXmlProperty(isAttribute = true, localName = "day")
        @JsonSerialize(using = GDaySerialiser.class, as = Integer.class)
        abstract Integer getDay();

        @JsonDeserialize(using = GDayDeserialiser.class, as = Integer.class)
        abstract void setDay(Integer day);
    }

    @Test
    public void writeBirthdateXML() throws JsonProcessingException {
        XmlMapper m = this.getMapper();

        m.addMixInAnnotations(JDate.class, BirthdateMixin.class);

        String xml = m.writeValueAsString(MockObjects.complete().getLearnerInfo().getIdentification().getDemographics().getBirthdate());
        assertThat("Birthdate XML: ",
                xml,
                is("<?xml version='1.0' encoding='UTF-8'?><JDate xmlns=\"" + Namespace.NAMESPACE + "\" year=\"1984\" month=\"--02\" day=\"---10\"/>"));
    }

    @Test
    public void writePrefsXML() throws JsonProcessingException {

        String xml = this.getMapper().writeValueAsString(MockObjects.bothPrefs());

        assertThat("Prefs XML: ",
                xml,
                is("<?xml version='1.0' encoding='UTF-8'?>"
                        + "<SkillsPassport xmlns=\"" + Namespace.NAMESPACE + "\" xmlns:xsi=\"" + Namespace.XSI_NAMESPACE + "\" xsi:schemaLocation=\"" + Namespace.NAMESPACE + " " + Namespace.SCHEMA_LOCATION_DEFAULT + "\">"
                        + "<PrintingPreferences>"
                        + "<Document type=\"ECV\">"
                        + "<Field name=\"LearnerInfo\" show=\"true\" order=\"Identification Headline WorkExperience Education Skills Achievement ReferenceTo\"/>"
                        + "<Field name=\"LearnerInfo.Identification.PersonName\" show=\"true\" order=\"FirstName Surname\"/>"
                        + "<Field name=\"LearnerInfo.Identification.ContactInfo.Address\" show=\"true\" format=\"s \\n p-z m (c)\"/>"
                        + "<Field name=\"LearnerInfo.Identification.ContactInfo.Telephone\" show=\"true\"/>"
                        + "</Document>"
                        + "<Document type=\"ELP\">"
                        + "<Field name=\"LearnerInfo\" show=\"true\" order=\"Identification LinguisticSkills\"/>"
                        + "</Document>"
                        + "</PrintingPreferences>"
                        + "</SkillsPassport>"));
    }

    @Test
    public void writeEmptyPrefsXML() throws JsonProcessingException {

        SkillsPassport esp = new SkillsPassport();
        Map<String, List<PrintingPreference>> ecvprefs = new HashMap<String, List<PrintingPreference>>();
        List<PrintingPreference> prefs = new ArrayList<PrintingPreference>();
        prefs.add(new PrintingPreference("alpha", true));
        prefs.add(null);
        prefs.add(new PrintingPreference(null, true, "order", null, null));
        prefs.add(new PrintingPreference("", true, "", "format", null));
        prefs.add(new PrintingPreference("beta", false));
        prefs.add(new PrintingPreference(null, false, null, null, null));
        ecvprefs.put("ECV", prefs);
        esp.setDocumentPrintingPrefs(ecvprefs);

        String xml = this.getMapper().writeValueAsString(esp);

        assertThat("Prefs XML: ",
                xml,
                is("<?xml version='1.0' encoding='UTF-8'?>"
                        + "<SkillsPassport xmlns=\"" + Namespace.NAMESPACE + "\" xmlns:xsi=\"" + Namespace.XSI_NAMESPACE + "\" xsi:schemaLocation=\"" + Namespace.NAMESPACE + " " + Namespace.SCHEMA_LOCATION_DEFAULT + "\">"
                        + "<PrintingPreferences>"
                        + "<Document type=\"ECV\">"
                        + "<Field name=\"alpha\" show=\"true\"/>"
                        + "<Field/>"
                        + "<Field show=\"true\" order=\"order\"/>"
                        + "<Field name=\"\" show=\"true\" format=\"format\"/>"
                        + "<Field name=\"beta\" show=\"false\"/>"
                        + "<Field show=\"false\"/>"
                        + "</Document>"
                        + "</PrintingPreferences>"
                        + "</SkillsPassport>"));
    }

    @Test
    public void writeEducationXML() throws JsonProcessingException {
        String xml = this.getMapper().writeValueAsString(MockObjects.espEduObj());

        assertThat("Education XML: ",
                xml,
                is("<?xml version='1.0' encoding='UTF-8'?>"
                        + "<SkillsPassport xmlns=\"" + Namespace.NAMESPACE + "\" xmlns:xsi=\"" + Namespace.XSI_NAMESPACE + "\" xsi:schemaLocation=\"" + Namespace.NAMESPACE + " " + Namespace.SCHEMA_LOCATION_DEFAULT + "\">"
                        + "<LearnerInfo>"
                        + "<EducationList>"
                        + "<Education>"
                        + "<Period>"
                        + "<From year=\"2008\" month=\"--05\"/>"
                        + "<To year=\"2010\" month=\"--05\"/>"
                        + "<Current>false</Current>"
                        + "</Period>"
                        + "<Documentation>"
                        + "<ReferenceTo idref=\"ATT_1\"/>"
                        + "<ReferenceTo idref=\"ATT_2\"/>"
                        + "<ReferenceTo idref=\"ATT_3\"/>"
                        + "</Documentation>"
                        + "<Title>Computer Science</Title>"
                        + "<Activities>&lt;ul>&lt;li>Programming&lt;/li>&lt;li>Data structures&lt;/li>&lt;/ul></Activities>"
                        + "<Organisation>"
                        + "<Name>University of Aegean</Name>"
                        + "<ContactInfo>"
                        + "<Address>"
                        + "<Contact>"
                        + "<Country>"
                        + "<Code>EL</Code>"
                        + "<Label>Hellas</Label>"
                        + "</Country>"
                        + "</Contact>"
                        + "</Address>"
                        + "</ContactInfo>"
                        + "</Organisation>"
                        + "<Level>"
                        + "<Code>5</Code>"
                        + "<Label>ISCED 5</Label>"
                        + "</Level>"
                        + "<Field>"
                        + "<Code>5</Code>"
                        + "<Label>Engineering</Label>"
                        + "</Field>"
                        + "</Education>"
                        + "</EducationList>"
                        + "</LearnerInfo>"
                        + "</SkillsPassport>"));
    }

}
