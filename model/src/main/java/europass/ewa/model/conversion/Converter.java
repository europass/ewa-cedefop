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
package europass.ewa.model.conversion;

public interface Converter<T> {

    /**
     * Loads a POJO from the string representation of the specific structure.
     * E.g. from a JSON string if this is a JSON-Converter and from an XML
     * string if this is an XML-Converter.
     *
     * @param source , a string representation of the structure
     * @return the POJO
     */
    T load(String source);

    /**
     * Converts a POJO to the specific structure. E.g. For a JSON-Converter this
     * will convert the POJO to JSON and for an XML-Converter from POJO to XML.
     *
     * @param object , the POJO object
     * @return the string representation of the structure in the specific
     * format.
     */
    String write(T object);

    /**
     * Converts the specific structure to the supplementary structure. E.g. For
     * a JSON-Converter this will convert JSON to XML and for an XML-Converter
     * XML to JSON.
     *
     * @param source , a string representation of the structure
     * @return the string representation of the structure in the supplementary
     * format.
     */
    String convert(String source);

    /**
     * Validates the given structure JSON, XML against the respective schema.
     *
     * @param source , the structure to validate
     * @return the result of validation
     */
    boolean validate(String source);
}
