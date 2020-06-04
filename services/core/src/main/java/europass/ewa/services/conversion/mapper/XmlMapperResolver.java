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
package europass.ewa.services.conversion.mapper;

import javax.ws.rs.Produces;
import javax.ws.rs.ext.ContextResolver;
import javax.ws.rs.ext.Provider;

import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.google.inject.Inject;
import com.google.inject.Singleton;

/**
 * Xml Mapper used for XML-based Conversions
 *
 * @author ekar
 *
 */
@Provider
@Singleton
@Produces("application/xml")
public class XmlMapperResolver implements ContextResolver<XmlMapper> {

    private final XmlMapper xmlMapper;

    @Inject
    public XmlMapperResolver(XmlMapper xmlMapper) {
        this.xmlMapper = xmlMapper;
    }

    @Override
    public XmlMapper getContext(Class<?> type) {
        return xmlMapper;
    }
}
