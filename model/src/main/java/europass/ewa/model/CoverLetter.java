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
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.google.common.base.Strings;
import com.google.common.collect.Collections2;

import europass.ewa.collections.Predicates;
import europass.ewa.enums.EuropassDocumentType;

@JsonPropertyOrder({"addressee", "letter", "documentation"})
@JsonIgnoreProperties(ignoreUnknown = true)
public class CoverLetter extends PrintableObject {

    protected final static String OPENING_PUNCTUATION_KEY = "CL.Opening.Salutation.Suffix";

    protected final static String CLOSING_PUNCTUATION_KEY = "CL.Closing.Salutation.Suffix";

    protected final static String DOCUMENT_CUSTOMIZATIONS_RESOURCE = "bundles/DocumentCustomizations";

    private Addressee addressee;

    private Letter letter;

    private GenericDocumentation documentation;

    @JsonProperty("Addressee")
    @JacksonXmlProperty(localName = "Addressee", namespace = Namespace.NAMESPACE)
    public Addressee getAddressee() {
        return withPreferences(addressee, "Addressee");
    }

    public void setAddresse(Addressee addressee) {
        this.addressee = addressee;
    }

    @JsonProperty("Letter")
    @JacksonXmlProperty(localName = "Letter", namespace = Namespace.NAMESPACE)
    public Letter getLetter() {
        return withPreferences(letter, "Letter");
    }

    public void setLetter(Letter letter) {
        this.letter = letter;
    }

    @JsonProperty("Documentation")
    @JacksonXmlProperty(localName = "Documentation", namespace = Namespace.NAMESPACE)
    public GenericDocumentation getDocumentation() {
        return withDocument(documentation, getDocument());
    }

    public void setDocumentation(GenericDocumentation documentation) {
        this.documentation = documentation;
    }

    @JsonIgnore
    @Override
    public boolean checkEmpty() {
        return ((addressee == null || (addressee != null && addressee.checkEmpty()))
                && (letter == null || (letter != null && letter.checkEmpty()))
                && (documentation == null || (documentation != null && documentation.checkEmpty())));
    }

    @Override
    public void applyDefaultPreferences(List<PrintingPreference> newPrefs) {
        applyDefaultPreferences(getAddressee(), Addressee.class, "Addressee", newPrefs);

        applyDefaultPreferences(getLetter(), Letter.class, "Letter", newPrefs);

        applyDefaultPreferences("Justification", newPrefs);
        applyDefaultPreferences("SignatureName", newPrefs);

        super.applyDefaultPreferences(newPrefs);
    }

    @JsonIgnore
    private ContactInfo getContactInfo() {
        ContactInfo info = new ContactInfo();

        SkillsPassport esp = getDocument();
        if (esp == null) {
            return info;
        }

        LearnerInfo learner = esp.getLearnerInfo();
        if (learner == null) {
            return info;
        }

        Identification identification = learner.getIdentification();
        if (identification == null) {
            return info;
        }

        ContactInfo contactInfo = identification.getContactInfo();
        if (contactInfo == null) {
            return info;
        }

        return contactInfo;
    }

    @JsonIgnore
    boolean printContacts() {
        ContactInfo info = getContactInfo();

        boolean hasEmail = info.getEmail() != null;
        List<ContactMethod> tels = info.getTelephoneList();
        boolean hasTel = tels != null && tels.size() > 0;
        List<ContactMethod> ims = info.getInstantMessagingList();
        boolean hasIM = ims != null && ims.size() > 0;

        return (hasEmail || hasTel || hasIM);
    }

    @JsonIgnore
    boolean isCoverLetterJustified() {
        //boolean isECL = returnDocumentType().equals( EuropassDocumentType.ECL );

        SkillsPassport document = this.document();
        if (document == null) {
            return false;
        }

        Map<String, List<PrintingPreference>> documentPrintingPrefs = document.getDocumentPrintingPrefs();
        if (documentPrintingPrefs == null) {
            return false;
        }

        // 1. Get the List of PrintingPreferences for the current Document
        List<PrintingPreference> prefs = documentPrintingPrefs.get(EuropassDocumentType.ECL.getPreferencesAcronym());

        Collection<PrintingPreference> matchedPrefs = Collections2.filter(prefs, Predicates.containsPattern(Predicates.CL_JUSTIFICATION_PREF));

        if (matchedPrefs.isEmpty()) {
            return false;
        }
        for (PrintingPreference pref : matchedPrefs) {
            return (pref.getJustify() != null) ? pref.getJustify() : false;
        }

        return false;
    }

    @JsonIgnore
    boolean isSignatureNameEnabled() {

        final SkillsPassport document = this.document();
        if (document == null) {
            return false;
        }

        final Map<String, List<PrintingPreference>> documentPrintingPrefs = document.getDocumentPrintingPrefs();
        if (documentPrintingPrefs == null) {
            return false;
        }

        // 1. Get the List of PrintingPreferences for the current Document
        List<PrintingPreference> prefs = documentPrintingPrefs.get(EuropassDocumentType.ECL.getPreferencesAcronym());

        Collection<PrintingPreference> matchedPrefs = Collections2.filter(prefs, Predicates.containsPattern(Predicates.CL_CLOSING_SALUTATION_ENABLED_NAME_PREF));

        for (PrintingPreference pref : matchedPrefs) {
            return (pref.getEnableName() != null) ? pref.getEnableName() : false;
        }

        return false;

    }

    static final int TEL_LIMIT = 2;
    static final int IM_LIMIT = 1;

    @JsonIgnore
    public ContactMethod ContactEmail() {
        return getContactInfo().getEmail();
    }

    /**
     * In the Cover Letter only the first two telephones will be displayed.
     *
     * @return
     */
    @JsonIgnore
    public List<ContactMethod> ReducedTelephoneList() {
        //Reduce Telephones
        List<ContactMethod> tels = getContactInfo().getTelephoneList();
        return (tels != null && tels.size() > TEL_LIMIT) ? reduceList(tels, TEL_LIMIT) : tels;
    }

    /**
     * In the Cover Letter only the first IM will be displayed.
     *
     * @return
     */
    @JsonIgnore
    public List<ContactMethod> ReducedInstantMessagingList() {
        //Reduce IMs
        List<ContactMethod> ims = getContactInfo().getInstantMessagingList();
        return (ims != null && ims.size() > IM_LIMIT) ? reduceList(ims, IM_LIMIT) : ims;
    }

    private static List<ContactMethod> reduceList(List<ContactMethod> list, int limit) {
        int size = list.size();
        if (size > limit) {
            List<ContactMethod> limited = new ArrayList<>();
            int j = 0;
            int i = 0;
            while (i < size && j < limit) {
                ContactMethod item = list.get(i);
                i++;
                if (item == null) {
                    continue;
                }
                if (Strings.isNullOrEmpty(item.getContact())) {
                    continue;
                }

                limited.add(item);
                j++;
            }
            if (j > 0) {
                return limited;
            }
        }
        return list;
    }

    /**
     * Used by the ODT generator to update the list of instant messaging contact
     * info, with the addition of the "|" in the contact use label so it can be
     * displayed in the odt as : Skype ed.walshe | msn edmondwalshe
     *
     * @return an updated list of instant messaging contact info, but with pipes
     * in the type
     */
    @JsonIgnore
    public List<ContactMethod> ReducedInstantMessagingListWithPipe() {
        List<ContactMethod> instantMessagingList = ReducedInstantMessagingList();

        if (instantMessagingList == null) {
            return instantMessagingList;
        }

        boolean skipNext = false;
        for (int i = 0; i < instantMessagingList.size(); i++) {
            String newLabel = "";
            ContactMethod oldContactMethod = instantMessagingList.get(i);
            if (oldContactMethod == null) {
                skipNext = true;
                continue;
            }

            CodeLabel oldUse = oldContactMethod.getUse();

            // decide when to add the pipe
            if (i > 0 && i < instantMessagingList.size() && !skipNext) {
                // if contact use is null, the pipe should be placed anyway, so
                // we add an empty use code
                if (oldUse == null) {
                    oldUse = new CodeLabel();
                    oldUse.setCode("");
                    oldUse.setLabel("");
                    oldContactMethod.setUse(oldUse);
                }
                String oldLabel = oldUse.getLabel();
                if (!Strings.isNullOrEmpty(oldLabel) && !oldLabel.contains("| ")) {
                    newLabel = "| " + oldUse.getLabel();
                    oldUse.setLabel(newLabel);
                }
            }
            skipNext = false;
        }
        return instantMessagingList;
    }
}
