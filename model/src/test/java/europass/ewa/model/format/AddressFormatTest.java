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
package europass.ewa.model.format;

import static org.junit.Assert.assertThat;

import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;

import com.google.common.base.Strings;

import europass.ewa.model.Address;
import europass.ewa.model.Addressee;
import europass.ewa.model.CLMockObject;
import europass.ewa.model.CodeLabel;
import europass.ewa.model.ContactAddress;
import europass.ewa.model.Organisation;
import europass.ewa.model.OrganisationalContactInfo;
import europass.ewa.model.SkillsPassport;

public class AddressFormatTest {

    static String pattern = "s\np-z m (c)";

    static String errorPattern = "text/short";

    @Test
    public void onlyCountry() {
        Address address = constructAddress(null, null, null, null, new CodeLabel("EL", "Greece"));

        AddressFormat format = AddressFormat.compile(errorPattern, address, "EL");
        String got = format.format(address);

        String expected = "(Greece)";

        assertThat(got, CoreMatchers.is(expected));
    }

    @Test
    public void onlyMunicipality() {
        Address address = constructAddress(null, null, "Marousi", null, null);

        AddressFormat format = AddressFormat.compile(pattern, address, "EL");
        String got = format.format(address);

        String expected = "Marousi";

        assertThat(got, CoreMatchers.is(expected));
    }

    @Test
    public void streetAndMunicipality() {
        Address address = constructAddress("Konitsis 11B", null, "Marousi", null, null);

        AddressFormat format = AddressFormat.compile(pattern, address, "EL");
        String got = format.format(address);

        String expected = "Konitsis 11B<text:line-break /> Marousi";

        assertThat(got, CoreMatchers.is(expected));
    }

    @Test
    public void addressFull() {
        Address address = constructAddress("Konitsis 11B", null, "Marousi", "15124", new CodeLabel("EL", "Greece"));

        AddressFormat format = AddressFormat.compile(pattern, address, "EL");
        String got = format.format(address);

        String expected = "Konitsis 11B<text:line-break />EL-15124 Marousi (Greece)";

        assertThat(got, CoreMatchers.is(expected));
    }

    @Test
    public void country() {
        Address address = constructAddress(null, null, null, null, new CodeLabel("EL", "Greece"));

        AddressFormat format = AddressFormat.compile(pattern, address, "EL");
        String got = format.format(address);

        String expected = "(Greece)";

        assertThat(got, CoreMatchers.is(expected));
    }

    @Test
    public void country2() {
        Address address = constructAddress(null, null, null, null, new CodeLabel("EL", "Greece"));

        AddressFormat format = AddressFormat.compile(pattern, address, null);
        String got = format.format(address);

        String expected = "(Greece)";

        assertThat(got, CoreMatchers.is(expected));
    }

    @Test
    public void cityAndCountry() {
        Address address = constructAddress(null, null, "Marousi", null, new CodeLabel("EL", "Greece"));

        AddressFormat format = AddressFormat.compile(pattern, address, "EL");
        String got = format.format(address);

        String expected = "Marousi (Greece)";

        assertThat(got, CoreMatchers.is(expected));
    }

    @Test
    public void zipAndCityAndCountry() {
        Address address = constructAddress(null, null, "Marousi", "151 24", new CodeLabel("EL", "Greece"));

        AddressFormat format = AddressFormat.compile(pattern, address, "EL");
        String got = format.format(address);

        String expected = "EL-151 24 Marousi (Greece)";

        assertThat(got, CoreMatchers.is(expected));
    }

    @Test
    public void zipAndCityAndCountry2() {
        Address address = constructAddress(null, null, "Marousi", "151 24", new CodeLabel("EL", "Greece"));

        AddressFormat format = AddressFormat.compile(pattern, address, null);
        String got = format.format(address);

        String expected = "151 24 Marousi (Greece)";

        assertThat(got, CoreMatchers.is(expected));
    }

    /**
     * TESTS RELATED TO EWA-900
     */
    @Test
    public void addressee() {
        Address address = constructAddress("Konitsis 11B", null, "Marousi", "151 24", new CodeLabel("EL", "Greece"));

        String got = address.cityPostalCodeOnly();

        String expected = "151 24 Marousi";

        assertThat(got, CoreMatchers.is(expected));
    }

    @Test
    public void addresseeLatvia() {
        Address address = constructAddress("Satekles iela 25", null, "Riga", "1050", new CodeLabel("LV", "Latvia"));

        String got = address.cityPostalCodeOnly();

        String expected = "Riga LV-1050";

        assertThat(got, CoreMatchers.is(expected));
    }

    @Test
    public void addresseeLatviaNoPostal() {
        Address address = constructAddress("Satekles iela 25", null, "Riga", null, new CodeLabel("LV", "Latvia"));

        String got = address.cityPostalCodeOnly();

        String expected = "Riga";

        assertThat(got, CoreMatchers.is(expected));
    }

    @Test
    public void addresseeLatviaNoMunicipality() {
        Address address = constructAddress("Satekles iela 25", null, null, "1050", new CodeLabel("LV", "Latvia"));

        String got = address.cityPostalCodeOnly();

        String expected = "LV-1050";

        assertThat(got, CoreMatchers.is(expected));
    }

    @Test
    public void addresseeAddressLVFull() {

        SkillsPassport esp = CLMockObject.letter();
        Addressee addressee = new Addressee();
        Organisation addresseeOrganisation = new Organisation();
        OrganisationalContactInfo contactInfo = new OrganisationalContactInfo();
        ContactAddress contactAddress = new ContactAddress();

        Address address = this.constructAddress("Satekles iela 25", null, "Riga", "1050", new CodeLabel("LV", "Latvia"));

        contactAddress.setContact(address);
        contactInfo.setAddress(contactAddress);
        addresseeOrganisation.setContactInfo(contactInfo);
        esp.getCoverLetter().setAddresse(addressee);
        esp.getCoverLetter().getAddressee().setOrganisation(addresseeOrganisation);

        Assert.assertThat("ODT text equals CLMockObject.ADDRESSEE_ADDRESS_LV_FULL ", esp.getCoverLetter().getAddressee().organisationTxt().replaceAll("(\\r|\\n)", ""), CoreMatchers.is(CLMockObject.ODT_ADDRESSEE_ADDRESS_LV_FULL));
    }

    @Test
    public void addresseeAddressLVfullWOrganisationName() {

        SkillsPassport esp = CLMockObject.letter();
        Addressee addressee = new Addressee();
        Organisation addresseeOrganisation = new Organisation();
        addresseeOrganisation.setName("Clipper emergency center");

        OrganisationalContactInfo contactInfo = new OrganisationalContactInfo();
        ContactAddress contactAddress = new ContactAddress();
        Address address = this.constructAddress("Satekles iela 25", null, "Riga", "1050", new CodeLabel("LV", "Latvia"));

        contactAddress.setContact(address);
        contactInfo.setAddress(contactAddress);
        addresseeOrganisation.setContactInfo(contactInfo);
        esp.getCoverLetter().setAddresse(addressee);
        esp.getCoverLetter().getAddressee().setOrganisation(addresseeOrganisation);

        Assert.assertThat("ODT text equals CLMockObject.ADDRESSEE_ADDRESS_LV_FULL_W_ORGNAME ", esp.getCoverLetter().getAddressee().organisationTxt().replaceAll("(\\r|\\n)", ""), CoreMatchers.is(CLMockObject.ODT_ADDRESSEE_ADDRESS_LV_FULL_W_ORGNAME));
    }

    @Test
    public void addresseeAddressLVnoStreetLineNoMunicipalityWOrganisationName() {

        SkillsPassport esp = CLMockObject.letter();
        Addressee addressee = new Addressee();
        Organisation addresseeOrganisation = new Organisation();
        addresseeOrganisation.setName("Clipper emergency center");

        OrganisationalContactInfo contactInfo = new OrganisationalContactInfo();
        ContactAddress contactAddress = new ContactAddress();
        Address address = this.constructAddress(null, null, null, "1050", new CodeLabel("LV", "Latvia"));

        contactAddress.setContact(address);
        contactInfo.setAddress(contactAddress);
        addresseeOrganisation.setContactInfo(contactInfo);
        esp.getCoverLetter().setAddresse(addressee);
        esp.getCoverLetter().getAddressee().setOrganisation(addresseeOrganisation);

        Assert.assertThat("ODT text equals CLMockObject.ADDRESSEE_ADDRESS_LV_NO_STREETLINE_NO_MUNICIPALITY_W_ORGNAME ", esp.getCoverLetter().getAddressee().organisationTxt().replaceAll("(\\r|\\n)", ""), CoreMatchers.is(CLMockObject.ODT_ADDRESSEE_ADDRESS_LV_NO_STREETLINE_NO_MUNICIPALITY_W_ORGNAME));
    }

    @Test
    public void addresseeAddressLVnoPostalNoMunicipalityWOrganisationName() {

        SkillsPassport esp = CLMockObject.letter();
        Addressee addressee = new Addressee();
        Organisation addresseeOrganisation = new Organisation();
        addresseeOrganisation.setName("Clipper emergency center");

        OrganisationalContactInfo contactInfo = new OrganisationalContactInfo();
        ContactAddress contactAddress = new ContactAddress();
        Address address = this.constructAddress("Satekles iela 25", null, null, null, new CodeLabel("LV", "Latvia"));

        contactAddress.setContact(address);
        contactInfo.setAddress(contactAddress);
        addresseeOrganisation.setContactInfo(contactInfo);
        esp.getCoverLetter().setAddresse(addressee);
        esp.getCoverLetter().getAddressee().setOrganisation(addresseeOrganisation);

        Assert.assertThat("ODT text equals CLMockObject.ADDRESSEE_ADDRESS_LV_NO_POSTAL_NO_MUNICIPALITY_W_ORGNAME ", esp.getCoverLetter().getAddressee().organisationTxt().replaceAll("(\\r|\\n)", ""), CoreMatchers.is(CLMockObject.ODT_ADDRESSEE_ADDRESS_LV_NO_POSTAL_NO_MUNICIPALITY_W_ORGNAME));
    }

    @Test
    public void addresseeAddressLVnoStreetLineNoPostalNoMunicipalityWOrganisationName() {

        SkillsPassport esp = CLMockObject.letter();
        Addressee addressee = new Addressee();
        Organisation addresseeOrganisation = new Organisation();
        addresseeOrganisation.setName("Clipper emergency center");

        OrganisationalContactInfo contactInfo = new OrganisationalContactInfo();
        ContactAddress contactAddress = new ContactAddress();
        Address address = this.constructAddress(null, null, null, null, new CodeLabel("LV", "Latvia"));

        contactAddress.setContact(address);
        contactInfo.setAddress(contactAddress);
        addresseeOrganisation.setContactInfo(contactInfo);
        esp.getCoverLetter().setAddresse(addressee);
        esp.getCoverLetter().getAddressee().setOrganisation(addresseeOrganisation);

        Assert.assertThat("ODT text equals CLMockObject.ADDRESSEE_ADDRESS_LV_NO_STREETLINE_NO_MUNICIPALITY_NO_POSTAL_W_ORGNAME ", esp.getCoverLetter().getAddressee().organisationTxt().replaceAll("(\\r|\\n)", ""), CoreMatchers.is(CLMockObject.ODT_ADDRESSEE_ADDRESS_LV_NO_STREETLINE_NO_MUNICIPALITY_NO_POSTAL_W_ORGNAME));
    }

    @Test
    public void addresseeAddressLVnoPostal() {

        SkillsPassport esp = CLMockObject.letter();
        Addressee addressee = new Addressee();
        Organisation addresseeOrganisation = new Organisation();

        OrganisationalContactInfo contactInfo = new OrganisationalContactInfo();
        ContactAddress contactAddress = new ContactAddress();
        Address address = this.constructAddress("Satekles iela 25", null, "Riga", null, new CodeLabel("LV", "Latvia"));

        contactAddress.setContact(address);
        contactInfo.setAddress(contactAddress);
        addresseeOrganisation.setContactInfo(contactInfo);
        esp.getCoverLetter().setAddresse(addressee);
        esp.getCoverLetter().getAddressee().setOrganisation(addresseeOrganisation);

        Assert.assertThat("ODT text equals CLMockObject.ODT_ADDRESSEE_ADDRESS_LV_NO_POSTAL ", esp.getCoverLetter().getAddressee().organisationTxt().replaceAll("(\\r|\\n)", ""), CoreMatchers.is(CLMockObject.ODT_ADDRESSEE_ADDRESS_LV_NO_POSTAL));
    }

    @Test
    public void addresseeAddressLVnoMunicipality() {

        SkillsPassport esp = CLMockObject.letter();
        Addressee addressee = new Addressee();
        Organisation addresseeOrganisation = new Organisation();

        OrganisationalContactInfo contactInfo = new OrganisationalContactInfo();
        ContactAddress contactAddress = new ContactAddress();
        Address address = this.constructAddress("Satekles iela 25", null, null, "1050", new CodeLabel("LV", "Latvia"));

        contactAddress.setContact(address);
        contactInfo.setAddress(contactAddress);
        addresseeOrganisation.setContactInfo(contactInfo);
        esp.getCoverLetter().setAddresse(addressee);
        esp.getCoverLetter().getAddressee().setOrganisation(addresseeOrganisation);

        Assert.assertThat("ODT text equals CLMockObject.ODT_ADDRESSEE_ADDRESS_LV_NO_MUNICIPALITY ", esp.getCoverLetter().getAddressee().organisationTxt().replaceAll("(\\r|\\n)", ""), CoreMatchers.is(CLMockObject.ODT_ADDRESSEE_ADDRESS_LV_NO_MUNICIPALITY));
    }

    @Test
    public void addresseeAddressLVnoStreetLine() {

        SkillsPassport esp = CLMockObject.letter();
        Addressee addressee = new Addressee();
        Organisation addresseeOrganisation = new Organisation();

        OrganisationalContactInfo contactInfo = new OrganisationalContactInfo();
        ContactAddress contactAddress = new ContactAddress();
        Address address = this.constructAddress(null, null, "Riga", "1050", new CodeLabel("LV", "Latvia"));

        contactAddress.setContact(address);
        contactInfo.setAddress(contactAddress);
        addresseeOrganisation.setContactInfo(contactInfo);
        esp.getCoverLetter().setAddresse(addressee);
        esp.getCoverLetter().getAddressee().setOrganisation(addresseeOrganisation);

        Assert.assertThat("ODT text equals CLMockObject.ODT_ADDRESSEE_ADDRESS_LV_NO_STREETLINE ", esp.getCoverLetter().getAddressee().organisationTxt().replaceAll("(\\r|\\n)", ""), CoreMatchers.is(CLMockObject.ODT_ADDRESSEE_ADDRESS_LV_NO_STREETLINE));
    }

    @Test
    public void addresseeAddressLVnoStreetLineNoPostal() {

        SkillsPassport esp = CLMockObject.letter();
        Addressee addressee = new Addressee();
        Organisation addresseeOrganisation = new Organisation();

        OrganisationalContactInfo contactInfo = new OrganisationalContactInfo();
        ContactAddress contactAddress = new ContactAddress();
        Address address = this.constructAddress(null, null, "Riga", null, new CodeLabel("LV", "Latvia"));

        contactAddress.setContact(address);
        contactInfo.setAddress(contactAddress);
        addresseeOrganisation.setContactInfo(contactInfo);
        esp.getCoverLetter().setAddresse(addressee);
        esp.getCoverLetter().getAddressee().setOrganisation(addresseeOrganisation);

        Assert.assertThat("ODT text equals CLMockObject.ODT_ADDRESSEE_ADDRESS_LV_NO_STREETLINE_NO_POSTAL ", esp.getCoverLetter().getAddressee().organisationTxt().replaceAll("(\\r|\\n)", ""), CoreMatchers.is(CLMockObject.ODT_ADDRESSEE_ADDRESS_LV_NO_STREETLINE_NO_POSTAL));
    }

    @Test
    public void addresseeAddressLVnoStreetLineNoMunicipality() {

        SkillsPassport esp = CLMockObject.letter();
        Addressee addressee = new Addressee();
        Organisation addresseeOrganisation = new Organisation();

        OrganisationalContactInfo contactInfo = new OrganisationalContactInfo();
        ContactAddress contactAddress = new ContactAddress();
        Address address = this.constructAddress(null, null, null, "1050", new CodeLabel("LV", "Latvia"));

        contactAddress.setContact(address);
        contactInfo.setAddress(contactAddress);
        addresseeOrganisation.setContactInfo(contactInfo);
        esp.getCoverLetter().setAddresse(addressee);
        esp.getCoverLetter().getAddressee().setOrganisation(addresseeOrganisation);

        Assert.assertThat("ODT text equals CLMockObject.ODT_ADDRESSEE_ADDRESS_LV_NO_STREETLINE_NO_MUNICIPALITY ", esp.getCoverLetter().getAddressee().organisationTxt().replaceAll("(\\r|\\n)", ""), CoreMatchers.is(CLMockObject.ODT_ADDRESSEE_ADDRESS_LV_NO_STREETLINE_NO_MUNICIPALITY));
    }

    /**
     * RELATED TO EWA-901
     */
    @Test
    public void addresseeAddressLVFullWithAddressLine2() {

        SkillsPassport esp = CLMockObject.letter();
        Addressee addressee = new Addressee();
        Organisation addresseeOrganisation = new Organisation();
        OrganisationalContactInfo contactInfo = new OrganisationalContactInfo();
        ContactAddress contactAddress = new ContactAddress();

        Address address = this.constructAddress("Satekles iela 25", "Visvalža iela 5", "Riga", "1050", new CodeLabel("LV", "Latvia"));

        contactAddress.setContact(address);
        contactInfo.setAddress(contactAddress);
        addresseeOrganisation.setContactInfo(contactInfo);
        esp.getCoverLetter().setAddresse(addressee);
        esp.getCoverLetter().getAddressee().setOrganisation(addresseeOrganisation);

        Assert.assertThat("ODT text equals CLMockObject.ODT_ADDRESSEE_ADDRESS_LV_FULL_W_ADDRESSLINE2 ", esp.getCoverLetter().getAddressee().organisationTxt().replaceAll("(\\r|\\n)", ""), CoreMatchers.is(CLMockObject.ODT_ADDRESSEE_ADDRESS_LV_FULL_W_ADDRESSLINE2));
    }

    @Test
    public void addresseeAddressLVnoStreetLineWithAddressLine2NoPostal() {

        SkillsPassport esp = CLMockObject.letter();
        Addressee addressee = new Addressee();
        Organisation addresseeOrganisation = new Organisation();

        OrganisationalContactInfo contactInfo = new OrganisationalContactInfo();
        ContactAddress contactAddress = new ContactAddress();
        Address address = this.constructAddress(null, "Visvalža iela 5", "Riga", null, new CodeLabel("LV", "Latvia"));

        contactAddress.setContact(address);
        contactInfo.setAddress(contactAddress);
        addresseeOrganisation.setContactInfo(contactInfo);
        esp.getCoverLetter().setAddresse(addressee);
        esp.getCoverLetter().getAddressee().setOrganisation(addresseeOrganisation);

        Assert.assertThat("ODT text equals CLMockObject.ODT_ADDRESSEE_ADDRESS_LV_NO_STREETLINE_W_ADDRESSLINE2_NO_POSTAL ", esp.getCoverLetter().getAddressee().organisationTxt().replaceAll("(\\r|\\n)", ""), CoreMatchers.is(CLMockObject.ODT_ADDRESSEE_ADDRESS_LV_NO_STREETLINE_W_ADDRESSLINE2_NO_POSTAL));
    }

    @Test
    public void addresseeAddressLVWithAddressLine2NoPostalNoMunicipalityWOrganisationName() {

        SkillsPassport esp = CLMockObject.letter();
        Addressee addressee = new Addressee();
        Organisation addresseeOrganisation = new Organisation();
        addresseeOrganisation.setName("Clipper emergency center");

        OrganisationalContactInfo contactInfo = new OrganisationalContactInfo();
        ContactAddress contactAddress = new ContactAddress();
        Address address = this.constructAddress("Satekles iela 25", "Visvalža iela 5", null, null, new CodeLabel("LV", "Latvia"));

        contactAddress.setContact(address);
        contactInfo.setAddress(contactAddress);
        addresseeOrganisation.setContactInfo(contactInfo);
        esp.getCoverLetter().setAddresse(addressee);
        esp.getCoverLetter().getAddressee().setOrganisation(addresseeOrganisation);

        Assert.assertThat("ODT text equals CLMockObject.ODT_ADDRESSEE_ADDRESS_LV_W_ADDRESSLINE2_NO_POSTAL_NO_MUNICIPALITY_W_ORGNAME ", esp.getCoverLetter().getAddressee().organisationTxt().replaceAll("(\\r|\\n)", ""), CoreMatchers.is(CLMockObject.ODT_ADDRESSEE_ADDRESS_LV_W_ADDRESSLINE2_NO_POSTAL_NO_MUNICIPALITY_W_ORGNAME));
    }

    private Address constructAddress(String addressLine, String addressLine2, String municipality, String postalCode, CodeLabel country) {

        Address address = new Address();

        if (!Strings.isNullOrEmpty(addressLine)) {
            address.setAddressLine(addressLine);
        }

        if (!Strings.isNullOrEmpty(addressLine2)) {
            address.setAddressLine2(addressLine2);
        }

        if (!Strings.isNullOrEmpty(municipality)) {
            address.setMunicipality(municipality);
        }

        if (!Strings.isNullOrEmpty(postalCode)) {
            address.setPostalCode(postalCode);
        }

        if (country != null) {
            if (!country.checkEmpty()) {
                address.setCountry(country);
            }
        }

        return address;

    }

}
