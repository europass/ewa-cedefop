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
import java.util.Locale;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.google.common.base.Strings;

import europass.ewa.model.decorator.WithDocumentList;
import static europass.ewa.model.format.OdtDisplayableUtils.formatWebsiteLinks;
import java.util.regex.Pattern;

@JsonPropertyOrder({"address", "email", "telephoneList", "websiteList", "instantMessagingList"})
@JsonIgnoreProperties(ignoreUnknown = true)
public class ContactInfo extends PrintableObject {

    private ContactAddress address;

    private ContactMethod email;

    private List<ContactMethod> telephoneList;

    private List<ContactMethod> websiteList;

    private List<ContactMethod> instantMessagingList;

    public ContactInfo() {
    }

    @JsonProperty("Address")
    @JacksonXmlProperty(localName = "Address", namespace = Namespace.NAMESPACE)
    public ContactAddress getAddress() {
        return withPreferences(address, "Address");
    }

    public void setAddress(ContactAddress address) {
        this.address = address;
    }

    @JsonProperty("Email")
    @JacksonXmlProperty(localName = "Email", namespace = Namespace.NAMESPACE)
    public ContactMethod getEmail() {
        return withDocument(email, getDocument());
    }

    public void setEmail(ContactMethod email) {
        this.email = email;
    }

    @JsonProperty("Telephone")
    @JacksonXmlProperty(localName = "Telephone", namespace = Namespace.NAMESPACE)
    @JacksonXmlElementWrapper(localName = "TelephoneList", namespace = Namespace.NAMESPACE)
    public List<ContactMethod> getTelephoneList() {
        return withDocument(telephoneList, getDocument());
    }

    public void setTelephoneList(List<ContactMethod> telephonelist) {
        this.telephoneList = telephonelist;
    }

    @JsonProperty("Website")
    @JacksonXmlProperty(localName = "Website", namespace = Namespace.NAMESPACE)
    @JacksonXmlElementWrapper(localName = "WebsiteList", namespace = Namespace.NAMESPACE)
    public List<ContactMethod> getWebsiteList() {
        return withDocument(websiteList, getDocument());
    }

    public void setWebsiteList(List<ContactMethod> websitelist) {
        this.websiteList = websitelist;
    }

    int websiteListIndex = 0;

    @JsonIgnore
    /**
     * formatWebsiteList is used for returning properly formatted URLs as
     * xlinks. EWA 1474 websiteListIndex is used as an instance variable because
     * formatWebsiteList is called as many times as the websites in the list,
     * thus the need for a broader scope
     *
     * @returns formatted website list
     */
    public String formatWebsiteList() {

        String website = "";

        if (websiteList.get(websiteListIndex) != null && websiteList.get(websiteListIndex).getContact() != null) {
            website = websiteList.get(websiteListIndex++).getContact().toString().trim();
            return formatWebsiteLinks((website));
        } else {
            return "";
        }
    }

    @JsonIgnore
    /**
     * isLinkedIn is used for returning whether the URLs are linkedin or not.
     * using the same websiteListIndex with the method above
     */
    public boolean isLinkedIn() {

        String website = "";
        //String regex = "(((https?|ftp|smtp)://)?([www])*\\.)[linkedin/~\\-]+\\.[a-zA-Z0-9/~\\-_,&=\\?\\.;]+[^\\.,\\s<]";
        //var jsRegex = /^((https?|ftp|smtp):\/\/)?(www.)?linkedin.com(\w+:{0,1}\w*@)?(\S+)(:([0-9])+)?(\/|\/([\w#!:.?+=&%@!\-\/]))?/;
        String regex = "^((https?|ftp|smtp)://)?(www.)?linkedin.com(\\w+:{0,1}\\w*@)?(\\S+)(:([0-9])+)?(/|/([\\w#!:.?+=&%@!\\-/]))?";
        Pattern pattern = Pattern.compile(regex);

        if (websiteList.get(websiteListIndex) != null && websiteList.get(websiteListIndex).getContact() != null) {
            website = websiteList.get(websiteListIndex).getContact().toString().trim();
            boolean matches = pattern.matcher(website).matches();
            return matches;
        } else {
            return false;
        }
    }

    @JsonProperty("InstantMessaging")
    @JacksonXmlProperty(localName = "InstantMessaging", namespace = Namespace.NAMESPACE)
    @JacksonXmlElementWrapper(localName = "InstantMessagingList", namespace = Namespace.NAMESPACE)
    public List<ContactMethod> getInstantMessagingList() {
        return withDocument(instantMessagingList, getDocument());
    }

    public void setInstantMessagingList(List<ContactMethod> instantmessaginglist) {
        this.instantMessagingList = instantmessaginglist;
    }

    /**
     * *********************************************************************
     */
    @JsonIgnore
    public void translateTo(SkillsPassport esp, Locale locale) {
        translateTo(esp, locale, "TelephoneType", telephoneList);
        translateTo(esp, locale, "InstantMessagingType", instantMessagingList);
    }

    /**
     * Adds extra properties to the ContactMethod, namely the list index and
     * boolean values if the item is first or last in the list.
     *
     * Leverages {@link IndexedList}, {@link IndexedListItem} and
     * {@link ListItem}
     *
     * @return
     */
    @JsonIgnore
    public List<ContactMethod> TelephoneListWithIndex() {
        return withDocument(this.indexedList(telephoneList), getDocument());
    }

    @JsonIgnore
    public List<ContactMethod> WebsiteListWithIndex() {
        return withDocument(this.indexedList(websiteList), getDocument());
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
    public List<ContactMethod> InstantMessagingListWithPipe() {

        if (instantMessagingList == null) {
            return instantMessagingList;
        }

        // the instant messaging list
        List<ContactMethod> oldList = this.getInstantMessagingList();
        boolean skipNext = false;
        for (int i = 0; i < oldList.size(); i++) {
            String newLabel = "";
            ContactMethod oldContactMethod = oldList.get(i);
            if (oldContactMethod == null) {
                skipNext = true;
                continue;
            }

            CodeLabel oldUse = oldContactMethod.getUse();

            // decide when to add the pipe
            if (i > 0 && i < oldList.size() && !skipNext) {
                // if contact use is null, the pipe should be placed anyway, so
                // we add an empty use code
                if (oldUse == null) {
                    oldUse = new CodeLabel();
                    oldUse.setCode("");
                    oldUse.setLabel("");
                    oldContactMethod.setUse(oldUse);
                }
                String oldLabel = oldUse.getLabel();
                if (!Strings.isNullOrEmpty(oldLabel)
                        && oldLabel.indexOf("| ") < 0) {
                    newLabel = "| " + oldUse.getLabel();
                    oldUse.setLabel(newLabel);
                }
            }
            skipNext = false;
        }
        return oldList;
    }

    @JsonIgnore
    public Address getAddressContact() {
        if (address == null) {
            return null;
        }
        return address.getContact();
    }

    @Override
    public void applyDefaultPreferences(List<PrintingPreference> newPrefs) {
        applyDefaultPreferences(getAddress(), ContactAddress.class, "Address", newPrefs);

        super.applyDefaultPreferences(newPrefs);

    }

    @JsonIgnore
    @Override
    public boolean checkEmpty() {
        return ((address == null || (address != null && address.checkEmpty()))
                && (email == null || (email != null && email.checkEmpty()))
                && (telephoneList == null || (telephoneList != null && ((WithDocumentList<ContactMethod>) getTelephoneList()).checkEmpty()))
                && (websiteList == null || (websiteList != null && ((WithDocumentList<ContactMethod>) getWebsiteList()).checkEmpty()))
                && (instantMessagingList == null || (instantMessagingList != null && ((WithDocumentList<ContactMethod>) getInstantMessagingList()).checkEmpty())));
    }
}
