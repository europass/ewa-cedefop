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
package europass.ewa.services.social.linkedin;

import java.util.ResourceBundle;

import javax.inject.Singleton;

import europass.ewa.model.CodeLabel;
import europass.ewa.resources.JsonResourceBundle;
import europass.ewa.services.social.InstanceClassMismatchException;
import europass.ewa.services.social.Transformer;

@Singleton
public class CountryCodeHandler implements Transformer {

    @SuppressWarnings("unchecked")
    @Override
    public Object transform(Object from, Object to, Object... params)
            throws InstanceClassMismatchException {

        if (!(from instanceof String)) {
            throw new InstanceClassMismatchException();
        }
        if (!(to instanceof CodeLabel)) {
            throw new InstanceClassMismatchException();
        }

        CodeLabel cLabel = (CodeLabel) to;

        try {

            String countryCode = (String) from;
            switch (countryCode) {

                case "gr":
                    countryCode = "EL";
                    break;
                case "gb":
                    countryCode = "UK";
                    break;
                default:
                    countryCode = countryCode.toUpperCase();
                    break;
            }

            //set country code to cLabel
            cLabel.setCode(countryCode);

            ResourceBundle bundle = ResourceBundle.getBundle("bundles/Country", new JsonResourceBundle.Control());
            cLabel.setLabel(bundle.getString(countryCode));

            return cLabel;

        } catch (final Exception e) {
            cLabel.setCode((String) from);
            return cLabel;
        }

    }

}
