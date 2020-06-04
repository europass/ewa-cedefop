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
package europass.ewa.model.conversion.json;

import java.io.IOException;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;

import europass.ewa.model.conversion.Converter;
import europass.ewa.model.conversion.exception.PojoToJsonException;
import europass.ewa.model.wrapper.ModelContainer;

public class ModelContainerConverter implements Converter<ModelContainer> {

    private static final Logger LOG = LoggerFactory.getLogger(ModelContainerConverter.class);

    private final ObjectMapper mapper;

    @Inject
    public ModelContainerConverter(ObjectMapper mapper) {
        this.mapper = mapper;
    }

    @Override
    public ModelContainer load(String source) {
        throw new UnsupportedOperationException("Method not implemented.");
    }

    /**
     * Converts a ModelContainer POJO to JSON string
     *
     * @param ModelContainer
     * @return String JSON
     */
    @Override
    public String write(ModelContainer object) {
        try {
            return mapper.writeValueAsString(object);
        } catch (final IOException e) {
            LOG.error(" JsonConverter:write - Failed to write POJO as JSON string.", e);
            throw new PojoToJsonException(e);
        }
    }

    @Override
    public String convert(String source) {
        throw new UnsupportedOperationException("Method not implemented.");
    }

    @Override
    public boolean validate(String source) {
        throw new UnsupportedOperationException("Json Validation not yet supported");
    }

}
