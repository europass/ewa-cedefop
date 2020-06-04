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

import java.io.IOException;
import java.io.StringReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonRootName;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.dataformat.xml.XmlFactory;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import com.fasterxml.jackson.dataformat.xml.deser.FromXmlParser;
import com.fasterxml.jackson.dataformat.xml.ser.ToXmlGenerator.Feature;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.name.Names;

import europass.ewa.model.Attachment;
import europass.ewa.model.DocumentInfo;
import europass.ewa.model.DrivingSkill;
import europass.ewa.model.JDate;
import europass.ewa.model.LearnerInfo;
import europass.ewa.model.Namespace;
import europass.ewa.model.PrintingPreference;

public class XmlToPojoCustomPrefsTest extends XmlMapperTest {

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
//---------------------------------------------------------------------------------------------------------

    /*
	 * The Following tests using a custom serializer and deserializer for the Printing Preferences
	 * 
     */
    private XmlMapper getTmpMapper() {
        XmlMapper m = new XmlMapper();
        m.setSerializationInclusion(Include.NON_EMPTY);
        m.setSerializationInclusion(Include.NON_NULL);
        m.configure(SerializationFeature.WRITE_NULL_MAP_VALUES, false);
        //The following throws java.lang.UnsupportedOperationException: Not implemented
        //this.xmlMapper.configure(SerializationFeature.INDENT_OUTPUT, true);

        m.configure(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT, false);

        m.configure(Feature.WRITE_XML_DECLARATION, true);

        m.setDateFormat(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ"));

        m.addMixInAnnotations(TestSkillsPassport.class, SkillsPassportMixin.class);

        m.addMixInAnnotations(JDate.class, DateMixin.class);

        m.addMixInAnnotations(DrivingSkill.class, DrivingSkillMixin.class);

        return m;
    }

    /**
     * Serialize POJO to XML !! Mind the Prefs
     *
     */
    @Test
    public void writePrefsXML() throws JsonProcessingException {

        TestSkillsPassport esp = new TestSkillsPassport();
        Map<String, List<PrintingPreference>> prefs = new HashMap<String, List<PrintingPreference>>();
        List<PrintingPreference> list = new ArrayList<PrintingPreference>();
        list.add(new PrintingPreference("LearnerInfo.Identification.PersonName", true, "FirstName Surname", null, null));
        list.add(new PrintingPreference("LearnerInfo.Identification.ContactInfo.Address", true, null, "s p-z m (c)", null));
        list.add(new PrintingPreference("LearnerInfo.Identification.ContactInfo.Telephone", true));
        prefs.put("ECV", list);
        esp.setDocumentPrintingPrefs(prefs);

        XmlMapper xmlMapper = this.getTmpMapper();

        String xml = xmlMapper.writeValueAsString(esp);

        assertThat("Prefs XML: ",
                xml,
                is("<?xml version='1.0' encoding='UTF-8'?>"
                        + "<SkillsPassport xmlns=\"" + Namespace.NAMESPACE + "\" xmlns:xsi=\"" + Namespace.XSI_NAMESPACE + "\" xsi:schemaLocation=\"" + Namespace.NAMESPACE + " " + Namespace.SCHEMA_LOCATION_DEFAULT + "\">"
                        + "<PrintingPreferences>"
                        + "<Document type=\"ECV\">"
                        + "<Field name=\"LearnerInfo.Identification.PersonName\" show=\"true\" order=\"FirstName Surname\"/>"
                        + "<Field name=\"LearnerInfo.Identification.ContactInfo.Address\" show=\"true\" format=\"s p-z m (c)\"/>"
                        + "<Field name=\"LearnerInfo.Identification.ContactInfo.Telephone\" show=\"true\"/>"
                        + "</Document>"
                        + "</PrintingPreferences>"
                        + "</SkillsPassport>"));
    }

    /**
     * Deserialize POJO from XML !!! Mind the Prefs
     *
     * @throws IOException
     * @throws JsonMappingException
     * @throws JsonParseException
     *
     */
    @Test
    public void readSkillsPrefsXML() throws JsonParseException, JsonMappingException, IOException {
        String xml = "<?xml version='1.0' encoding='UTF-8'?>"
                + "<SkillsPassport xmlns=\"" + Namespace.NAMESPACE + "\" xmlns:xsi=\"" + Namespace.XSI_NAMESPACE + "\" xsi:schemaLocation=\"" + Namespace.NAMESPACE + " " + Namespace.SCHEMA_LOCATION_DEFAULT + "\">"
                + "<PrintingPreferences>"
                + "<Document type=\"ECV\">"
                + "<Field name=\"LearnerInfo.PersonName\" show=\"true\" order=\"Surname FirstName\"></Field>"
                + "<Field name=\"Address\" show=\"true\" format=\"s \\n p-z m (c)\"></Field>"
                + "<Field name=\"Telephone\" show=\"true\"></Field>"
                + "</Document>"
                + "<Document type=\"ELP\">"
                + "<Field name=\"Language\" show=\"false\"></Field>"
                + "</Document>"
                + "</PrintingPreferences>"
                + "<LearnerInfo>"
                + "<Identification>"
                + "<PersonName><FirstName>Eleni</FirstName><Surname>Kargioti</Surname></PersonName>"
                + "</Identification>"
                + "</LearnerInfo>"
                + "</SkillsPassport>";
        XmlMapper xmlMapper = this.getTmpMapper();

        TestSkillsPassport esp = xmlMapper.readValue(xml, TestSkillsPassport.class);

        assertNotNull(esp.getDocumentPrintingPrefs().get("ECV"));
        assertThat(esp.getDocumentPrintingPrefs().get("ECV").size(), is(3));
        assertThat(esp.getDocumentPrintingPrefs().get("ECV").get(0).getName().toString(), is("LearnerInfo.PersonName"));
        assertThat(esp.getDocumentPrintingPrefs().get("ECV").get(0).getShow(), is(true));
        assertThat(esp.getDocumentPrintingPrefs().get("ECV").get(0).getOrder(), is("Surname FirstName"));
        assertThat(esp.getDocumentPrintingPrefs().get("ECV").get(1).getName().toString(), is("Address"));
        assertThat(esp.getDocumentPrintingPrefs().get("ECV").get(1).getShow(), is(true));
        assertThat(esp.getDocumentPrintingPrefs().get("ECV").get(1).getFormat(), is("s \\n p-z m (c)"));
        assertThat(esp.getDocumentPrintingPrefs().get("ECV").get(2).getName().toString(), is("Telephone"));
        assertThat(esp.getDocumentPrintingPrefs().get("ECV").get(2).getShow(), is(true));

        assertNotNull(esp.getDocumentPrintingPrefs().get("ELP"));
        assertThat(esp.getDocumentPrintingPrefs().get("ELP").size(), is(1));
        assertThat(esp.getDocumentPrintingPrefs().get("ELP").get(0).getName().toString(), is("Language"));
        assertThat(esp.getDocumentPrintingPrefs().get("ELP").get(0).getShow(), is(false));

        assertThat("Name: ",
                esp.getLearnerInfo().getIdentification().getPersonName().getFirstName(),
                is("Eleni"));
    }

    @JsonPropertyOrder({"locale", "documentInfo", "documentPrintingPrefs", "learnerinfo", "attachmentlist"})
    @JacksonXmlRootElement(localName = "SkillsPassport", namespace = Namespace.NAMESPACE)
    @JsonRootName("SkillsPassport")
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class TestSkillsPassport {

        private Locale locale;

        private DocumentInfo documentInfo;

        private Map<String, List<PrintingPreference>> documentPrintingPrefs = null;

        private LearnerInfo learnerInfo;

        private List<Attachment> attachmentlist;

        public TestSkillsPassport() {
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
            return documentInfo;
        }

        public void setDocumentInfo(DocumentInfo documentInfo) {
            this.documentInfo = documentInfo;
        }

        @JsonProperty("PrintingPreferences")
        public Map<String, List<PrintingPreference>> getDocumentPrintingPrefs() {
            return documentPrintingPrefs;
        }

        public void setDocumentPrintingPrefs(
                Map<String, List<PrintingPreference>> documentPrintingPrefs) {
            this.documentPrintingPrefs = documentPrintingPrefs;
        }

        @JsonProperty("LearnerInfo")
        @JacksonXmlProperty(localName = "LearnerInfo", namespace = Namespace.NAMESPACE)
        public LearnerInfo getLearnerInfo() {
            return learnerInfo;
        }

        public void setLearnerInfo(LearnerInfo learnerinfo) {
            this.learnerInfo = learnerinfo;
        }

        @JsonProperty("Attachment")
        @JacksonXmlProperty(localName = "Attachment", namespace = Namespace.NAMESPACE)
        @JacksonXmlElementWrapper(localName = "AttachmentList", namespace = Namespace.NAMESPACE)
        public List<Attachment> getAttachmentList() {
            return attachmentlist;
        }

        public void setAttachmentList(List<Attachment> attachmentlist) {
            this.attachmentlist = attachmentlist;
        }
    }

    @SuppressWarnings("deprecation")
    @Test
    public void readXmlMapOfList() throws Throwable {
        String xml = "<PrintingPreferences>"
                + "<Document type=\"ECV\">"
                + "<Field name=\"LearnerInfo.PersonName\" show=\"true\" order=\"Surname FirstName\"></Field>"
                + "<Field name=\"Address\" show=\"true\" format=\"s \\n p-z m (c)\"></Field>"
                + "<Field name=\"Telephone\" show=\"true\"></Field>"
                + "</Document>"
                + "<Document type=\"ELP\">"
                + "<Field name=\"Language\" show=\"false\"></Field>"
                + "</Document>"
                + "</PrintingPreferences>";

        StringReader reader = new StringReader(xml);
        assertNotNull(reader);

        JsonFactory factory = new XmlFactory();
        JsonParser jp = factory.createJsonParser(reader);
        FromXmlParser xp = (FromXmlParser) jp;

        Map<String, List<PrintingPreference>> map = new HashMap<String, List<PrintingPreference>>();
        List<PrintingPreference> list = null;
        String type = null;
        String name = null;
        String order = null;
        String before = null;
        String format = null;
        String show = null;
        PrintingPreference pref = null;

        JsonToken jt = xp.nextToken();

        //while - OUTER
        while (jt != null) {
            jt = xp.nextToken();
            if (jt == JsonToken.FIELD_NAME) {
                String currentName = xp.getCurrentName();

                if ("Document".equals(currentName)) {
                    list = new ArrayList<PrintingPreference>();

                    //while - INNER Document
                    while (jt != null) {
                        jt = xp.nextToken();
                        if (jt == JsonToken.FIELD_NAME) {
                            currentName = xp.getCurrentName();
                            if ("type".equals(currentName)) {
                                while (jt != JsonToken.VALUE_STRING) {
                                    jt = xp.nextToken();
                                }
                                type = xp.getText();
                                map.put(type, list);
                                break; //break from while - INNER Document
                            }
                        }
                    }
                } else if ("Field".equals(currentName)) {
                    while (jt != JsonToken.END_OBJECT) {
                        jt = xp.nextToken();
                        if (jt == JsonToken.FIELD_NAME) {
                            currentName = xp.getCurrentName();

                            if ("name".equals(currentName)) {
                                while (jt != JsonToken.VALUE_STRING) {
                                    jt = xp.nextToken();
                                }
                                name = xp.getText();
                            } else if ("show".equals(currentName)) {
                                while (jt != JsonToken.VALUE_STRING) {
                                    jt = xp.nextToken();
                                }
                                show = xp.getText();
                            } else if ("order".equals(currentName)) {
                                while (jt != JsonToken.VALUE_STRING) {
                                    jt = xp.nextToken();
                                }
                                order = xp.getText();
                            } else if ("format".equals(currentName)) {
                                while (jt != JsonToken.VALUE_STRING) {
                                    jt = xp.nextToken();
                                }
                                format = xp.getText();
                            } else if ("before".equals(currentName)) {
                                while (jt != JsonToken.VALUE_STRING) {
                                    jt = xp.nextToken();
                                }
                                before = xp.getText();
                            }
                        }
                    }
                    pref = new PrintingPreference(name, Boolean.parseBoolean(show), order, format, before);
                    map.get(type).add(pref);
                }
            }
        }

        assertNotNull(map.get("ECV"));
        assertThat(map.get("ECV").size(), is(3));
        assertThat(map.get("ECV").get(0).getName().toString(), is("LearnerInfo.PersonName"));
        assertThat(map.get("ECV").get(0).getShow(), is(true));
        assertThat(map.get("ECV").get(0).getOrder(), is("Surname FirstName"));
        assertThat(map.get("ECV").get(1).getFormat(), is("s \\n p-z m (c)"));

        assertNotNull(map.get("ELP"));
        assertThat(map.get("ELP").size(), is(1));
        assertThat(map.get("ELP").get(0).getName().toString(), is("Language"));
        assertThat(map.get("ELP").get(0).getShow(), is(false));
    }
}
