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
package europass.ewa.services.social;

import java.io.StringWriter;
import java.io.Writer;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.mustachejava.Mustache;

import europass.ewa.model.JDate;
import europass.ewa.services.social.linkedin.DateConfig;

public class MustacheListAsText implements ListAsText<Mustache> {

    private static final Logger LOG = LoggerFactory.getLogger(MustacheListAsText.class);

    @SuppressWarnings("unchecked")
    @Override
    public <A extends Object> String transform(List<A> list, Mustache template, Object... params) {
        StringBuilder bld = new StringBuilder("");

        List<DateConfig> dateConfigList = null;
        for (Object param : params) {
            if (param instanceof List<?>) {
                List<?> paramList = (List<?>) param;
                if (paramList.get(0) instanceof DateConfig) {
                    dateConfigList = (List<DateConfig>) param;
                }
            }
        }

        Writer writer = new StringWriter();

        // index needed to iterate in case we have a parameter object that is iterable
        for (int i = 0; i < list.size(); i++) {
            EnrichedItem<A> enriched = new EnrichedItem<A>(list.get(i));

            if (dateConfigList != null) {

                DateConfig dateConfig = dateConfigList.get(i);

                JDate startDate = dateConfig.getStartDate();
                if (startDate != null) {
                    enriched.setStartDate(startDate);
                }
                JDate endDate = dateConfig.getEndDate();
                if (endDate != null) {
                    enriched.setEndDate(endDate);
                }
            }

            template.execute(writer, enriched);
        }

        bld.append(writer.toString());
        bld.append("");

        LOG.info("Mustache Template Compilation & Execution result:\n" + bld.toString());

        return bld.toString();
    }
}
