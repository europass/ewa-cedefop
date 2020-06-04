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

import com.ctc.wstx.stax.WstxInputFactory;

public class CustomWstxInputFactory extends WstxInputFactory {

    public CustomWstxInputFactory() {
        super();

        /**
         * EWA-1437: Fix XML External Entity (XXE) security issue - Disallowing
         * doctype, external entites & parameters elements in xml body
         *
         * - see
         * http://woodstox.codehaus.org/2.0.6/javadoc/com/ctc/wstx/stax/WstxInputFactory.html
         */
        this.setProperty(IS_REPLACING_ENTITY_REFERENCES, false);
        this.setProperty(IS_SUPPORTING_EXTERNAL_ENTITIES, false);
    }
}
