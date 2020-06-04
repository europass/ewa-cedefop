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
package europass.ewa.statistics;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import europass.ewa.enums.ConversionFileType;
import europass.ewa.enums.EuropassDocumentType;
import europass.ewa.enums.ExportDestination;
import europass.ewa.model.SkillsPassport;

public class DummyStatisticsLogger implements StatisticsLogger {

    private static final Logger LOG = LoggerFactory.getLogger(DummyStatisticsLogger.class);

    @Override
    public void log(SkillsPassport esp) {
        LOG.info("DatabaseStatisticsLogger:log - About to keep statistics for SkillsPassport...");
    }

    @Override
    public void log(SkillsPassport esp, EuropassDocumentType document,
            ConversionFileType fileType) {
        this.log(esp, document, fileType, DocumentGenerator.UNKNOWN, ExportDestination.UNKNOWN);
    }

    @Override
    public void log(SkillsPassport esp, EuropassDocumentType document, ConversionFileType fileType, DocumentGenerator generator, ExportDestination exportTo) {

        LOG.info("DatabaseStatisticsLogger:log - About to keep statistics for SkillsPassport (" + document + ")");
    }

}
