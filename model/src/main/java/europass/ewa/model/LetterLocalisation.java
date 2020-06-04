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
import java.util.ResourceBundle;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.google.common.base.Strings;

import europass.ewa.resources.JsonResourceBundle;

@JsonPropertyOrder({"date", "place"})
@JsonIgnoreProperties(ignoreUnknown = true)
public class LetterLocalisation extends PrintableObject {

    private static final String LOCALISATION_DEFAULT_ORDER = "Place Date";

    private JDate date;

    private Place place;

    @JsonProperty("Date")
    @JacksonXmlProperty(localName = "Date", namespace = Namespace.NAMESPACE)
    public JDate getDate() {
        return date;
    }

    @JsonIgnore
    PrintableValue<JDate> date() {
        return withPreferences(date, "Date");
    }

    public void setDate(JDate date) {
        this.date = date;
    }

    @JsonIgnore
    public String dateTxt() {
        if (date != null) {

            PrintingPreference pref = date().pref() == null ? this.preferencesFromDocument(this.document(), date().prefKey()) : date().pref();

            String formatName = (pref == null) ? JDate.DEFAULT_FORMAT : pref.getFormat();
            return date.format(formatName, locale());
        }
        return "";
    }

    @JsonProperty("Place")
    @JacksonXmlProperty(localName = "Place", namespace = Namespace.NAMESPACE)
    public Place getPlace() {
        return place;
    }

    public void setPlace(Place place) {
        this.place = place;
    }

    @JsonIgnore
    String placeTxt() {
        if (place == null) {
            return "";
        }
        return place.getMunicipality();
    }

    @JsonIgnore
    public String localisationTxt() {
        String order = LOCALISATION_DEFAULT_ORDER;

        PrintingPreference pref = pref();
        if (pref != null) {
            String prefOrder = pref.getOrder();
            if (!Strings.isNullOrEmpty(prefOrder)) {
                order = prefOrder;
            }
        }
        String dateTxt = dateTxt();
        boolean hasDate = !Strings.isNullOrEmpty(dateTxt);
        String placeTxt = placeTxt();
        boolean hasPlace = !Strings.isNullOrEmpty(placeTxt);
        String localisation_delimiter = ",";

        boolean placeFirst = order.equals(LOCALISATION_DEFAULT_ORDER);

        StringBuilder bld = new StringBuilder("");
        String localisationDelimiterKey = "CoverLetter.Letter.Localisation.Delimiter";

        try {		//get the localisation Delimiter from the Document Customizations bundle
            Locale locale = locale();

            ResourceBundle bundle = ResourceBundle.getBundle("preferences/CLExtraPreferences", locale, new JsonResourceBundle.Control(new ObjectMapper()));
            localisation_delimiter = bundle.getString((localisationDelimiterKey));
        } catch (final Exception e) {
            throw e;
        } finally {
            localisation_delimiter += " ";
        }

        if (placeFirst) {
            bld.append(hasPlace ? placeTxt : "");
            bld.append(hasPlace && hasDate ? localisation_delimiter : "");
            bld.append(hasDate ? dateTxt : "");
        } else {
            bld.append(hasDate ? dateTxt : "");
            bld.append(hasDate && hasPlace ? localisation_delimiter : "");
            bld.append(hasPlace ? placeTxt : "");
        }
        return bld.toString();
    }

    @JsonIgnore
    @Override
    public boolean checkEmpty() {
        return ((date == null || (date != null && date.checkEmpty()))
                && (place == null || (place != null && place.checkEmpty())));
    }

    @Override
    public void applyDefaultPreferences(List<PrintingPreference> newPrefs) {
        date().applyDefaultPreferences(newPrefs);

        applyDefaultPreferences(getPlace(), Place.class, "Place", newPrefs);

        super.applyDefaultPreferences(newPrefs);
    }
}
