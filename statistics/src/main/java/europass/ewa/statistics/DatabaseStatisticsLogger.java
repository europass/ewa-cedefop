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

import java.util.List;

import javax.inject.Inject;
import javax.inject.Provider;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import europass.ewa.enums.ConversionFileType;
import europass.ewa.enums.EuropassDocumentType;
import europass.ewa.enums.ExportDestination;
import europass.ewa.model.SkillsPassport;
import europass.ewa.statistics.data.StatsDetails;
import europass.ewa.statistics.data.StatsEntry;

public class DatabaseStatisticsLogger implements StatisticsLogger {

    private static final Logger LOG = LoggerFactory.getLogger(DatabaseStatisticsLogger.class);

    private final Provider<Session> sessionProvider;

    @Inject
    public DatabaseStatisticsLogger(Provider<Session> sessionProvider) {
        this.sessionProvider = sessionProvider;
    }

    @Override
    public void log(SkillsPassport esp) {
        this.log(esp, EuropassDocumentType.UNKNOWN, ConversionFileType.UNKNOWN);
    }

    @Override
    public void log(SkillsPassport esp, EuropassDocumentType document, ConversionFileType fileType) {
        this.log(esp, document, fileType, DocumentGenerator.UNKNOWN, ExportDestination.UNKNOWN);
    }

    @Override
    public void log(SkillsPassport esp, EuropassDocumentType document, ConversionFileType fileType, DocumentGenerator generator, ExportDestination exportTo) {
        try {
            LOG.info("DatabaseStatisticsLogger:log - About to keep statistics for SkillsPassport...");

            // Prepare statistical entry of the main document
            StatisticsManager manager = new StatisticsManager(esp);
            StatsEntry statsEntry = manager.prepare(document, fileType, generator, exportTo);

            // Persist it
            if (statsEntry != null) {
                this.store(statsEntry);
            }

            // Get the entry's id to be used in the related entry id fields of the document and the bundles
            Long entryId = statsEntry.getId();

            if (esp.getDocumentInfo() != null && entryId != null) {

                statsEntry.setRelatedEntryId(entryId);

                List<EuropassDocumentType> bundleDocumentsList = esp.getDocumentInfo().getBundleDocuments();
                for (EuropassDocumentType docType : bundleDocumentsList) {

                    StatsEntry bundleStatsEntry = manager.prepare(docType, fileType, generator, exportTo);
                    bundleStatsEntry.setRelatedEntryId(entryId);

                    // Persist it
                    if (bundleStatsEntry != null) {
                        this.store(bundleStatsEntry);
                    }
                }
            }

        } catch (Exception e) {
            LOG.error("DatabaseStatisticsLogger:log - Failed to store statistics", e);
        }
    }

    protected void store(StatsEntry statsEntry) {
        Session session = sessionProvider.get();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
            session.save(statsEntry);

            tx.commit();

        } catch (Exception e) {
            try {
                if (tx != null) {
                    tx.rollback();
                }
            } catch (Exception ex) {
                LOG.error("DatabaseStatisticsLogger:log - Failed to rollback", ex);
            }
            LOG.error("DatabaseStatisticsLogger:log ", e);
        }
    }

    @SuppressWarnings("unchecked")
    protected StatsEntry fetchById(Long id) {
        Session session = sessionProvider.get();

        String hql = "FROM StatsEntry e WHERE e.id = ? ";
        Query query = session.createQuery(hql);
        query.setMaxResults(1);
        query.setFloat(0, id);

        List<StatsEntry> resultList = query.list();
        if (resultList.size() > 0) {
            return (StatsEntry) resultList.get(0);
        }

        return null;
    }

    protected boolean deleteFromByKey(String entity, String keyName, Long value) {
        Session session = sessionProvider.get();

        String hql = "delete " + entity + " where " + keyName + " = ? ";
        Query query = session.createQuery(hql);
        query.setParameter(0, value);

        int recordsRemoved = query.executeUpdate();

        if (recordsRemoved >= 1) {
            return true;
        } else {
            return false;
        }
    }

    @SuppressWarnings("unchecked")
    protected List<StatsEntry> fetchLatest() {
        Session session = sessionProvider.get();

        String hql = "FROM StatsEntry e ORDER BY e.id DESC";
        Query query = session.createQuery(hql);
        query.setMaxResults(1);

        return (List<StatsEntry>) query.list();
    }

    @SuppressWarnings("unchecked")
    protected List<StatsDetails> fetchLatestDetails() {
        Session session = sessionProvider.get();

        String hql = "FROM StatsDetails d ORDER BY d.id DESC";
        Query query = session.createQuery(hql);
        query.setMaxResults(1);

        return (List<StatsDetails>) query.list();
    }

}
