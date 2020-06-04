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
import java.util.Locale;

import europass.ewa.enums.EuropassDocumentType;

public class CLMockObject {

    public static String PREFIX_XML = "<?xml version='1.0' encoding='UTF-8'?><SkillsPassport xmlns=\"" + Namespace.NAMESPACE + "\" xmlns:xsi=\"" + Namespace.XSI_NAMESPACE + "\" xsi:schemaLocation=\"" + Namespace.NAMESPACE + " " + Namespace.SCHEMA_LOCATION_DEFAULT + "\">";
    public static String SUFFIX_XML = "</SkillsPassport>";

    public static String PREFIX_COVERLETTER_XML = "<CoverLetter>";
    public static String SUFFIX_COVERLETTER_XML = "</CoverLetter>";

    public static String PREFIX_JSON = "{\"SkillsPassport\":{";
    public static String SUFFIX_JSON = "}}";

    public static String PREFIX_COVERLETTER_JSON = "\"CoverLetter\":{";
    public static String SUFFIX_COVERLETTER_JSON = "}";

    public static String PERSONNAME_WITH_TITLE_XML = "<PersonName><FirstName>Jim</FirstName><Surname>Burnett</Surname></PersonName>";
    public static String PERSONNAME_WITH_TITLE_JSON = "\"PersonName\":{\"FirstName\":\"Jim\",\"Surname\":\"Burnett\"}";

    public static SkillsPassport learner() {
        return new SkillsPassportBuilder.Builder()
                .withLearnerInfo(
                        new LearnerInfoBuilder.Builder()
                                .withIdentification(
                                        new IdentificationBuilder.Builder()
                                                .personName(new PersonName("Jim", "Burnett"))
                                                .build()
                                                .get()
                                )
                                .build()
                                .get()
                )
                .build().get();
    }

    public static SkillsPassport learnerWithNameOnly() {
        return learner();
    }

    public static SkillsPassport learnerWithAddressOnly() {
        return learnerWithContact(0, 0, false, true);
    }

    public static SkillsPassport learnerWithContactOnly() {
        return learnerWithContact(4, 3, true, false);
    }

    public static SkillsPassport learnerWithAddressContact() {
        return learnerWithContact(4, 3, true, true);

    }

    public static SkillsPassport learnerWithContact(int telLimit, int imLimit, boolean wEmail, boolean wAddress) {
        SkillsPassport esp = learner();
        ContactInfo personalContact = new ContactInfo();

        if (wAddress) {
            ContactAddress personalAddress = new ContactAddress(new Address("12 Strawberry Hills", "B26 3QJ", "Birmingham", new CodeLabel("UK", "United Kingdom")));
            personalContact.setAddress(personalAddress);
        }

        if (wEmail) {
            ContactMethod email = new ContactMethod("jim.bernett@provider.com");
            personalContact.setEmail(email);
        }

        if (telLimit > 0) {
            List<ContactMethod> tels = new ArrayList<ContactMethod>();
            if (telLimit > 4) {
                telLimit = 4;
            }
            switch (telLimit) {
                case 4:
                    tels.add(new ContactMethod(new CodeLabel("mobile", "Mobile"), "+44 123456789"));
                case 3:
                    tels.add(new ContactMethod(new CodeLabel("home", "Home"), "+44 987654321"));
                case 2:
                    tels.add(new ContactMethod(new CodeLabel("mobile", "Mobile"), "+44 987654321"));
                default:
                    tels.add(new ContactMethod(new CodeLabel("work", "Work"), "+44 556998951"));
            }
            personalContact.setTelephoneList(tels);
        }
        if (imLimit > 0) {
            List<ContactMethod> ims = new ArrayList<ContactMethod>();
            if (imLimit > 3) {
                imLimit = 3;
            }
            switch (imLimit) {
                case 3:
                    ims.add(new ContactMethod(new CodeLabel("msn", "MSN"), "jim.bernett"));
                case 2:
                    ims.add(new ContactMethod(new CodeLabel("twitter"), "jim.bernett"));
                default:
                    ims.add(new ContactMethod(new CodeLabel("gtalk"), "jim.bernett"));
            }
            personalContact.setInstantMessagingList(ims);
        }

        esp.getLearnerInfo().getIdentification().setContactInfo(personalContact);

        CoverLetter coverLetter = new CoverLetter();
        esp.setCoverLetter(coverLetter);

        return esp;
    }

    public static String ENCLOSED_JSON = "\"Heading\":{\"Code\":\"enclosed\",\"Label\":\"Enclosed:\"}";
    public static String INTERDOCUMENT_JSON = "\"InterDocument\":[{\"ref\":\"ECV\"},{\"ref\":\"ESP\"},{\"ref\":\"ELP\"}]";
    public static String INTRADOCUMENT_JSON = "\"IntraDocument\":[{\"idref\":\"ATT_1\"},{\"idref\":\"ATT_2\"}]";
    public static String EXTRADOCUMENT_JSON = "\"ExtraDocument\":[{\"Description\":\"List of Citations\"},{\"Description\":\"Video CV\",\"href\":\"http://myvideocv.com/jim.burnett\"}]";
    public static String DOCUMENTATION_JSON = "\"Documentation\":{" + ENCLOSED_JSON + "," + INTERDOCUMENT_JSON + "," + INTRADOCUMENT_JSON + "," + EXTRADOCUMENT_JSON + "}";

    public static String ENCLOSED_XML = "<Heading><Code>enclosed</Code><Label>Enclosed:</Label></Heading>";
    public static String INTERDOCUMENT_XML = "<InterDocument><ReferencedDocument ref=\"ECV\"/><ReferencedDocument ref=\"ESP\"/><ReferencedDocument ref=\"ELP\"/></InterDocument>";
    public static String INTRADOCUMENT_XML = "<IntraDocument><ReferenceTo idref=\"ATT_1\"/><ReferenceTo idref=\"ATT_2\"/></IntraDocument>";
    public static String EXTRADOCUMENT_XML = "<ExtraDocument><ReferencedResource><Description>List of Citations</Description></ReferencedResource><ReferencedResource href=\"http://myvideocv.com/jim.burnett\"><Description>Video CV</Description></ReferencedResource></ExtraDocument>";
    public static String DOCUMENTATION_XML = "<Documentation>" + ENCLOSED_XML + INTERDOCUMENT_XML + INTRADOCUMENT_XML + EXTRADOCUMENT_XML + "</Documentation>";

    public static SkillsPassport documentation(String locale) {

        //Enclosed Label
        CodeLabel headingCodeLabel = new CodeLabel("enclosed", "Enclosed:");

        if (locale == "de") {
            headingCodeLabel.setCode("attached");
            headingCodeLabel.setLabel("Angebracht:");
        }

        //Europass Documents
        List<ReferencedDocument> interDocumentList = new ArrayList<>();
        interDocumentList.add(new ReferencedDocument(EuropassDocumentType.ECV));
        interDocumentList.add(new ReferencedDocument(EuropassDocumentType.ESP));
        interDocumentList.add(new ReferencedDocument(EuropassDocumentType.ELP));

        //External Resources
        List<ReferencedResource> extraDocumentList = new ArrayList<>();
        extraDocumentList.add(new ReferencedResource("List of Citations", null));
        extraDocumentList.add(new ReferencedResource("Video CV", "http://myvideocv.com/jim.burnett"));

        //Attachments
        List<ReferenceTo> intraDocumentList = new ArrayList<>();
        intraDocumentList.add(new ReferenceTo("ATT_1"));
        intraDocumentList.add(new ReferenceTo("ATT_2"));

        GenericDocumentation documentation = new GenericDocumentation();
        documentation.setHeading(headingCodeLabel);
        documentation.setInterDocumentList(interDocumentList);
        documentation.setIntraDocumentList(intraDocumentList);
        documentation.setExtraDocumentList(extraDocumentList);

        CoverLetter coverLetter = new CoverLetter();
        coverLetter.setDocumentation(documentation);

        SkillsPassport esp = new SkillsPassport();
        esp.setCoverLetter(coverLetter);

        return esp;
    }

    public static String POSITION_JSON = "\"Position\":{\"Code\":\"12332\",\"Label\":\"Human resource manager\"}";
    public static String POSITION_XML = "<Position><Code>12332</Code><Label>Human resource manager</Label></Position>";

    public static String ORGANISATION_JSON = "\"Organisation\":{\"Name\":\"Clipper emergency center\",\"ContactInfo\":{\"Address\":{\"Contact\":{\"AddressLine\":\"Wall street 42\",\"PostalCode\":\"SW1P 3AT\",\"Municipality\":\"London\",\"Country\":{\"Code\":\"UK\",\"Label\":\"United Kingdom\"}}}}}";
    public static String ORGANISATION_WITH_ADDRESSLINE2_JSON = "\"Organisation\":{\"Name\":\"Clipper emergency center\",\"ContactInfo\":{\"Address\":{\"Contact\":{\"AddressLine\":\"Wall street 42\",\"AddressLine2\":\"151 Culford Rd\",\"PostalCode\":\"SW1P 3AT\",\"Municipality\":\"London\",\"Country\":{\"Code\":\"UK\",\"Label\":\"United Kingdom\"}}}}}";
    public static String ORGANISATION_XML = "<Organisation><Name>Clipper emergency center</Name><ContactInfo><Address><Contact><AddressLine>Wall street 42</AddressLine><PostalCode>SW1P 3AT</PostalCode><Municipality>London</Municipality><Country><Code>UK</Code><Label>United Kingdom</Label></Country></Contact></Address></ContactInfo></Organisation>";
    public static String ORGANISATION_WITH_ADDRESSLINE2_XML = "<Organisation><Name>Clipper emergency center</Name><ContactInfo><Address><Contact><AddressLine>Wall street 42</AddressLine><AddressLine2>151 Culford Rd</AddressLine2><PostalCode>SW1P 3AT</PostalCode><Municipality>London</Municipality><Country><Code>UK</Code><Label>United Kingdom</Label></Country></Contact></Address></ContactInfo></Organisation>";

    public static SkillsPassport organisation() {
        Address address = new Address();
        address.setAddressLine("Wall street 42");
        address.setPostalCode("SW1P 3AT");
        address.setMunicipality("London");
        address.setCountry(new CodeLabel("UK", "United Kingdom"));

        ContactAddress contactAddress = new ContactAddress();
        contactAddress.setContact(address);

        OrganisationalContactInfo contactinfo = new OrganisationalContactInfo();
        contactinfo.setAddress(contactAddress);

        Organisation organisation = new Organisation();
        organisation.setName("Clipper emergency center");
        organisation.setContactInfo(contactinfo);

        Addressee addressee = new Addressee();
        addressee.setOrganisation(organisation);

        CoverLetter coverLetter = new CoverLetter();
        coverLetter.setAddresse(addressee);

        SkillsPassport esp = new SkillsPassport();
        esp.setCoverLetter(coverLetter);

        return esp;
    }

    public static String ADDRESSEE_NAME_WITH_TITLE_XML = "<PersonName><Title><Code>dr</Code><Label>Dr.</Label></Title><FirstName>John</FirstName><Surname>Stuart</Surname></PersonName>";
    public static String ADDRESSEE_NAME_WITH_TITLE_JSON = "\"PersonName\":{\"Title\":{\"Code\":\"dr\",\"Label\":\"Dr.\"},\"FirstName\":\"John\",\"Surname\":\"Stuart\"}";
    public static String ADDRESSEE_JSON = "\"Addressee\":{" + ADDRESSEE_NAME_WITH_TITLE_JSON + "," + POSITION_JSON + "," + ORGANISATION_JSON + "}";
    public static String ADDRESSEE_WITH_ADDRESSLINE2_JSON = "\"Addressee\":{" + ADDRESSEE_NAME_WITH_TITLE_JSON + "," + POSITION_JSON + "," + ORGANISATION_WITH_ADDRESSLINE2_JSON + "}";
    public static String ADDRESSEE_XML = "<Addressee>" + ADDRESSEE_NAME_WITH_TITLE_XML + POSITION_XML + ORGANISATION_XML + "</Addressee>";
    public static String ADDRESSEE_WITH_ADDRESSLINE2_XML = "<Addressee>" + ADDRESSEE_NAME_WITH_TITLE_XML + POSITION_XML + ORGANISATION_WITH_ADDRESSLINE2_XML + "</Addressee>";

    public static SkillsPassport addressee() {

        Addressee addressee = new Addressee();
        addressee.setPersonName(new PersonName(new CodeLabel("dr", "Dr."), "John", "Stuart"));
        addressee.setPosition(new CodeLabel("12332", "Human resource manager"));
        addressee.setOrganisation(organisation().getCoverLetter().getAddressee().getOrganisation());

        CoverLetter coverLetter = new CoverLetter();
        coverLetter.setAddresse(addressee);

        SkillsPassport esp = new SkillsPassport();
        esp.setCoverLetter(coverLetter);

        return esp;
    }

    public static SkillsPassport addresseeWithAddressLine2() {

        Addressee addressee = new Addressee();
        addressee.setPersonName(new PersonName(new CodeLabel("dr", "Dr."), "John", "Stuart"));
        addressee.setPosition(new CodeLabel("12332", "Human resource manager"));

        Organisation org = organisation().getCoverLetter().getAddressee().getOrganisation();
        Address addr = org.getContactInfo().getAddress().getContact();
        addr.setAddressLine2("151 Culford Rd");

        org.getContactInfo().getAddress().setContact(addr);

        addressee.setOrganisation(org);

        CoverLetter coverLetter = new CoverLetter();
        coverLetter.setAddresse(addressee);

        SkillsPassport esp = new SkillsPassport();
        esp.setCoverLetter(coverLetter);

        return esp;
    }

    public static String LOCALISATION_JSON = "\"Localisation\":{\"Date\":{\"Year\":2013,\"Month\":10,\"Day\":15},\"Place\":{\"Municipality\":\"Birmingham\"}}";
    public static String SUBJECTLINE_JSON = "\"SubjectLine\":\"Ref. IT support officer/2013/01/AD\"";
    public static String OPENING_SALUTATION_JSON = "\"OpeningSalutation\":{\"Salutation\":{\"Label\":\"Dear Mr.\"},\"PersonName\":{\"Surname\":\"Stuart\"}}";
    public static String BODY_JSON = "\"Body\":{\"Opening\":\"<p>I would like to express my interest on the position of IT Support Officer which was advertised in today’s Journal. For the past four years I have worked in IT with Brown’s. As the company is moving to another part of the country, I will be made redundant in two weeks’ time.</p>\",\"MainBody\":\"<p>I am confident that the experience acquired in my present job, which involves various IT duties in person and by phone, will prove valuable. I also:</p><ul><li>deal with IT queries</li><li>handle incoming calls</li><li>handle incoming and outgoing post</li><li>order printer consumables.</li></ul><p>Before this job I was a trainee with Brightson&#39;s (Solicitors) in North Street, Invertown and completed RSA I and II in Business Administration and have various Microsoft package experience. I have always enjoyed working with people and think my previous experience will allow me to work as part of the team and to be an effective representative of your company.</p>\",\"Closing\":\"<p>I am available for interview at any time and could start work immediately. You can ask for references from my present and previous employers.</p><p>Please find enclosed a copy of my CV for more information.</p>\"}";
    public static String CLOSING_SALUTATION_JSON = "\"ClosingSalutation\":{\"Label\":\"Yours sincerelly\"}";
    public static String LETTER_JSON = "\"Letter\":{" + LOCALISATION_JSON + "," + SUBJECTLINE_JSON + "," + OPENING_SALUTATION_JSON + "," + BODY_JSON + "," + CLOSING_SALUTATION_JSON + "}";

    public static String LOCALISATION_XML = "<Localisation><Date year=\"2013\" month=\"--10\" day=\"---15\"/><Place><Municipality>Birmingham</Municipality></Place></Localisation>";
    public static String SUBJECTLINE_XML = "<SubjectLine>Ref. IT support officer/2013/01/AD</SubjectLine>";
    public static String OPENING_SALUTATION_XML = "<OpeningSalutation><Salutation><Label>Dear Mr.</Label></Salutation><PersonName><Surname>Stuart</Surname></PersonName></OpeningSalutation>";
    public static String BODY_XML = "<Body><Opening>&lt;p>I would like to express my interest on the position of IT Support Officer which was advertised in today’s Journal. For the past four years I have worked in IT with Brown’s. As the company is moving to another part of the country, I will be made redundant in two weeks’ time.&lt;/p></Opening><MainBody>&lt;p>I am confident that the experience acquired in my present job, which involves various IT duties in person and by phone, will prove valuable. I also:&lt;/p>&lt;ul>&lt;li>deal with IT queries&lt;/li>&lt;li>handle incoming calls&lt;/li>&lt;li>handle incoming and outgoing post&lt;/li>&lt;li>order printer consumables.&lt;/li>&lt;/ul>&lt;p>Before this job I was a trainee with Brightson&amp;#39;s (Solicitors) in North Street, Invertown and completed RSA I and II in Business Administration and have various Microsoft package experience. I have always enjoyed working with people and think my previous experience will allow me to work as part of the team and to be an effective representative of your company.&lt;/p></MainBody><Closing>&lt;p>I am available for interview at any time and could start work immediately. You can ask for references from my present and previous employers.&lt;/p>&lt;p>Please find enclosed a copy of my CV for more information.&lt;/p></Closing></Body>";
    public static String CLOSING_SALUTATION_XML = "<ClosingSalutation><Label>Yours sincerelly</Label></ClosingSalutation>";
    public static String LETTER_XML = "<Letter>" + LOCALISATION_XML + SUBJECTLINE_XML + OPENING_SALUTATION_XML + BODY_XML + CLOSING_SALUTATION_XML + "</Letter>";

    // RELATED TO EWA-900 - NOTE: the address municipality-postal code format will be the default (p m) 
    public static String ODT_ADDRESSEE_ADDRESS_LV_NO_COUNTRY = "<text:span>Satekles iela 25</text:span><text:span><text:line-break/>1050 Riga</text:span>";
    public static String ODT_ADDRESSEE_ADDRESS_LV_FULL = "<text:span>Satekles iela 25</text:span><text:span><text:line-break/>Riga LV-1050</text:span><text:span><text:line-break/>Latvia</text:span>";

    public static String ODT_ADDRESSEE_ADDRESS_LV_FULL_W_ORGNAME = "<text:span>Clipper emergency center</text:span><text:span><text:line-break/>Satekles iela 25</text:span><text:span><text:line-break/>Riga LV-1050</text:span><text:span><text:line-break/>Latvia</text:span>";
    public static String ODT_ADDRESSEE_ADDRESS_LV_NO_STREETLINE_NO_MUNICIPALITY_W_ORGNAME = "<text:span>Clipper emergency center</text:span><text:span><text:line-break/>LV-1050</text:span><text:span><text:line-break/>Latvia</text:span>";
    public static String ODT_ADDRESSEE_ADDRESS_LV_NO_POSTAL_NO_MUNICIPALITY_W_ORGNAME = "<text:span>Clipper emergency center</text:span><text:span><text:line-break/>Satekles iela 25</text:span><text:span><text:line-break/>Latvia</text:span>";
    public static String ODT_ADDRESSEE_ADDRESS_LV_NO_STREETLINE_NO_MUNICIPALITY_NO_POSTAL_W_ORGNAME = "<text:span>Clipper emergency center</text:span><text:span><text:line-break/>Latvia</text:span>";

    public static String ODT_ADDRESSEE_ADDRESS_LV_NO_POSTAL = "<text:span>Satekles iela 25</text:span><text:span><text:line-break/>Riga</text:span><text:span><text:line-break/>Latvia</text:span>";
    public static String ODT_ADDRESSEE_ADDRESS_LV_NO_MUNICIPALITY = "<text:span>Satekles iela 25</text:span><text:span><text:line-break/>LV-1050</text:span><text:span><text:line-break/>Latvia</text:span>";

    public static String ODT_ADDRESSEE_ADDRESS_LV_NO_STREETLINE = "<text:span>Riga LV-1050</text:span><text:span><text:line-break/>Latvia</text:span>";
    public static String ODT_ADDRESSEE_ADDRESS_LV_NO_STREETLINE_NO_POSTAL = "<text:span>Riga</text:span><text:span><text:line-break/>Latvia</text:span>";
    public static String ODT_ADDRESSEE_ADDRESS_LV_NO_STREETLINE_NO_MUNICIPALITY = "<text:span>LV-1050</text:span><text:span><text:line-break/>Latvia</text:span>";

    // RELATED TO EWA-901
    public static String ODT_ADDRESSEE_ADDRESS_LV_FULL_W_ADDRESSLINE2 = "<text:span>Satekles iela 25</text:span><text:span><text:line-break/>Visvalža iela 5</text:span><text:span><text:line-break/>Riga LV-1050</text:span><text:span><text:line-break/>Latvia</text:span>";
    public static String ODT_ADDRESSEE_ADDRESS_LV_NO_STREETLINE_W_ADDRESSLINE2_NO_POSTAL = "<text:span>Visvalža iela 5</text:span><text:span><text:line-break/>Riga</text:span><text:span><text:line-break/>Latvia</text:span>";
    public static String ODT_ADDRESSEE_ADDRESS_LV_W_ADDRESSLINE2_NO_POSTAL_NO_MUNICIPALITY_W_ORGNAME = "<text:span>Clipper emergency center</text:span><text:span><text:line-break/>Satekles iela 25</text:span><text:span><text:line-break/>Visvalža iela 5</text:span><text:span><text:line-break/>Latvia</text:span>";

    public static SkillsPassport letter() {

        Letter letter = new Letter();
        letter.setLocalisation(letterLocalisation().getCoverLetter().getLetter().getLocalisation());
        letter.setSubjectLine("Ref. IT support officer/2013/01/AD");
        letter.setOpeningSalutation(openingSalutation().getCoverLetter().getLetter().getOpeningSalutation());
        letter.setBody(body().getCoverLetter().getLetter().getBody());
        letter.setClosingSalutation(new CodeLabel(null, "Yours sincerelly"));

        CoverLetter coverLetter = new CoverLetter();
        coverLetter.setLetter(letter);

        SkillsPassport esp = new SkillsPassport();
        esp.setCoverLetter(coverLetter);

        return esp;
    }

    public static SkillsPassport body() {
        LetterBody body = new LetterBody();
        body.setOpening("<p>I would like to express my interest on the position of IT Support Officer which was advertised in today’s Journal. For the past four years I have worked in IT with Brown’s. As the company is moving to another part of the country, I will be made redundant in two weeks’ time.</p>");
        body.setMainBody("<p>I am confident that the experience acquired in my present job, which involves various IT duties in person and by phone, will prove valuable. I also:</p><ul><li>deal with IT queries</li><li>handle incoming calls</li><li>handle incoming and outgoing post</li><li>order printer consumables.</li></ul><p>Before this job I was a trainee with Brightson's (Solicitors) in North Street, Invertown and completed RSA I and II in Business Administration and have various Microsoft package experience. I have always enjoyed working with people and think my previous experience will allow me to work as part of the team and to be an effective representative of your company.</p>");
        body.setClosing("<p>I am available for interview at any time and could start work immediately. You can ask for references from my present and previous employers.</p><p>Please find enclosed a copy of my CV for more information.</p>");

        Letter letter = new Letter();
        letter.setBody(body);

        CoverLetter coverLetter = new CoverLetter();
        coverLetter.setLetter(letter);

        SkillsPassport esp = new SkillsPassport();
        esp.setCoverLetter(coverLetter);

        return esp;
    }

    public static SkillsPassport letterLocalisation() {
        JDate date = new JDate();
        date.setYear(2013);
        date.setMonth(10);
        date.setDay(15);
        LetterLocalisation localisation = new LetterLocalisation();
        localisation.setDate(date);
        localisation.setPlace(new Place("Birmingham", null));

        Letter letter = new Letter();
        letter.setLocalisation(localisation);

        CoverLetter coverLetter = new CoverLetter();
        coverLetter.setLetter(letter);

        SkillsPassport esp = new SkillsPassport();
        esp.setCoverLetter(coverLetter);

        return esp;
    }

    public static SkillsPassport openingSalutation() {
        OpeningSalutation openingSalutation = new OpeningSalutation();

        openingSalutation.setSalutation(new CodeLabel(null, "Dear Mr."));
        openingSalutation.setPersonName(new PersonName(null, "Stuart"));

        Letter letter = new Letter();
        letter.setOpeningSalutation(openingSalutation);

        CoverLetter coverLetter = new CoverLetter();
        coverLetter.setLetter(letter);

        SkillsPassport esp = new SkillsPassport();
        esp.setCoverLetter(coverLetter);

        return esp;
    }

    public static SkillsPassport openingSalutation(String Label, String Name) {
        OpeningSalutation openingSalutation = new OpeningSalutation();
        openingSalutation.setSalutation(new CodeLabel(null, Label));
        openingSalutation.setPersonName(new PersonName(null, Name));

        Letter letter = new Letter();
        letter.setOpeningSalutation(openingSalutation);

        CoverLetter coverLetter = new CoverLetter();
        coverLetter.setLetter(letter);

        SkillsPassport esp = new SkillsPassport();
        esp.setCoverLetter(coverLetter);

        return esp;
    }

    public static SkillsPassport nullOpeningSalutation() {
        OpeningSalutation openingSalutation = new OpeningSalutation();

        Letter letter = new Letter();
        letter.setOpeningSalutation(openingSalutation);

        CoverLetter coverLetter = new CoverLetter();
        coverLetter.setLetter(letter);

        SkillsPassport esp = new SkillsPassport();
        esp.setCoverLetter(coverLetter);

        return esp;
    }

    public static SkillsPassport fullCL() {
        SkillsPassport esp = new SkillsPassport();

        CoverLetter coverLetter = new CoverLetter();
        coverLetter.setAddresse(addressee().getCoverLetter().getAddressee());
        coverLetter.setLetter(letter().getCoverLetter().getLetter());
        coverLetter.setDocumentation(documentation("en").getCoverLetter().getDocumentation());

        esp.setCoverLetter(coverLetter);

        return esp;
    }

    public static SkillsPassport fullCLWithLearner() {
        SkillsPassport esp = new SkillsPassport();

        CoverLetter coverLetter = new CoverLetter();
        coverLetter.setAddresse(addressee().getCoverLetter().getAddressee());
        coverLetter.setLetter(letter().getCoverLetter().getLetter());
        coverLetter.setDocumentation(documentation("en").getCoverLetter().getDocumentation());

        esp.setCoverLetter(coverLetter);

        esp.setLearnerInfo(learnerWithAddressContact().getLearnerInfo());

        return esp;
    }

    public static SkillsPassport fullCLWithLearnerDE() {
        SkillsPassport esp = new SkillsPassport();
        esp.setLocale(new Locale("de"));

        CoverLetter coverLetter = new CoverLetter();
        coverLetter.setAddresse(addressee().getCoverLetter().getAddressee());
        coverLetter.setLetter(letter().getCoverLetter().getLetter());

        coverLetter.setDocumentation(documentation("de").getCoverLetter().getDocumentation());

        esp.setCoverLetter(coverLetter);

        esp.setLearnerInfo(learnerWithAddressContact().getLearnerInfo());

        return esp;
    }

    public static SkillsPassport eclObj(ECL_OBJECTS type) {

        SkillsPassport esp = CLMockObject.fullCLWithLearner();

        switch (type) {

            case NAME_ONLY:
                esp = CLMockObject.learnerWithNameOnly();
                break;

            case NAME_ADDRESS:
                esp = CLMockObject.learnerWithAddressOnly();
                break;

            case NAME_CONTACT:
                esp = CLMockObject.learnerWithContactOnly();
                break;

            case NAME_ADDRESS_CONTACT:
                esp = CLMockObject.learnerWithAddressContact();
                break;

            case FULL:
            default:
                esp = CLMockObject.fullCLWithLearner();
                break;
        }

        return esp;
    }

    public enum ECL_OBJECTS {
        NAME_ONLY,
        NAME_ADDRESS,
        NAME_CONTACT,
        NAME_ADDRESS_CONTACT,
        FULL
    };

}
