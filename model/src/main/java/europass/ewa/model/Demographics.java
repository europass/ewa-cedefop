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
import europass.ewa.model.reflection.ReflectionUtils;

@JsonPropertyOrder({"birthdate", "gender", "nationalityList"})
@JsonIgnoreProperties(ignoreUnknown = true)
public class Demographics extends PrintableObject {

    private JDate birthdate;

    private CodeLabel gender;

    private List<CodeLabel> nationalityList;

    public Demographics() {
    }

    @JsonProperty("Birthdate")
    @JacksonXmlProperty(localName = "Birthdate", namespace = Namespace.NAMESPACE)
    public JDate getBirthdate() {
        return birthdate;
    }

    public void setBirthdate(JDate birthdate) {
        this.birthdate = birthdate;
    }

    @JsonProperty("Gender")
    @JacksonXmlProperty(localName = "Gender", namespace = Namespace.NAMESPACE)
    public CodeLabel getGender() {
        return gender;
    }

    public void setGender(CodeLabel gender) {
        this.gender = gender;
    }

    @JsonProperty("Nationality")
    @JacksonXmlProperty(localName = "Nationality", namespace = Namespace.NAMESPACE)
    @JacksonXmlElementWrapper(localName = "NationalityList", namespace = Namespace.NAMESPACE)
    public List<CodeLabel> getNationalityList() {
        return withDocument(nationalityList, getDocument());
    }

    public void setNationalityList(List<CodeLabel> nationalitylist) {
        this.nationalityList = nationalitylist;
    }

    /**
     * ********************************************************************
     */
    @JsonIgnore
    public void translateTo(SkillsPassport esp, Locale locale) {
        gender = translate(esp, locale, "GenderOption", gender, null, ReflectionUtils.ADJUST_GENDER_CODE);
        nationalityList = translate(esp, locale, "Nationality", nationalityList);
    }

    public PrintableValue<JDate> birthdate() {
        return withPreferences(birthdate, "Birthdate");
    }

    /**
     * Used by ODT Generator to format the birtdate according to the selected
     * format
     *
     * @return
     */
    @JsonIgnore
    public String formatBirthdate() {
        if (birthdate != null) {
            PrintingPreference pref = birthdate().pref();
            String formatName = pref.getFormat();
            return birthdate.format(formatName, locale());
        }
        return "";
    }

    /**
     * Used by the ODT generator to prepare a string with the list of
     * nationalities (comma-separated)
     *
     * @return
     */
    @JsonIgnore
    public String nationalityToString() {
        if (nationalityList == null) {
            return "";
        }

        String commaList = "";
        boolean skipNext = true;

        int natSize = nationalityList.size();
        for (int i = 0; i < natSize; i++) {

            CodeLabel nationality = nationalityList.get(i);

            if (nationality == null) {
                skipNext = skipNext && true;//if the item is null then skip the comma
                continue;
            }
            String label = nationality.getLabel();
            if (Strings.isNullOrEmpty(label)) {
                skipNext = skipNext && true;//if the item is null then skip the comma
                continue;
            }
            // decide when to add the comma
            if (i > 0 && i < natSize && !skipNext) {
                commaList += ", ";
            }
            commaList += label;
            skipNext = false;
        }
        return commaList;
    }

    @Override
    @JsonIgnore
    public void applyDefaultPreferences(List<PrintingPreference> newPrefs) {

        birthdate().applyDefaultPreferences(newPrefs);

        super.applyDefaultPreferences(newPrefs);

    }

    /**
     * * Print the Pipe **
     */
    @JsonIgnore
    public boolean pipeDoB() {

        return (gender != null && gender.nonEmpty()) && (birthdate != null && !birthdate.checkEmpty());
    }

    @JsonIgnore
    public boolean pipeNationality() {

//		WithDocumentList<CodeLabel> withDocumentList = new WithDocumentList<CodeLabel>(getNationalityList());
        boolean[] shows
                = {((WithDocumentList<CodeLabel>) getNationalityList()).nonEmpty(),
                    (gender != null && gender.nonEmpty()),
                    (birthdate != null && !birthdate.checkEmpty())};
        return this.printPipe(shows);
    }

    @JsonIgnore
    @Override
    public boolean checkEmpty() {

        return ((birthdate == null || (birthdate != null && birthdate.checkEmpty()))
                && (gender == null || (gender != null && gender.checkEmpty()))
                && (nationalityList == null || (((WithDocumentList<CodeLabel>) getNationalityList()).checkEmpty())));
    }
}
