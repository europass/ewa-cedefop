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
package europass.ewa.tools.ga.logger;

import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import europass.ewa.tools.ga.enums.ErrorTypesRecommendations;
import europass.ewa.tools.ga.enums.HibernateTablesTypes;
import europass.ewa.tools.ga.enums.TableArgumentsTypes;
import europass.ewa.tools.ga.errors.GAJsonResponseError;
import europass.ewa.tools.ga.exceptions.InterruptExecutionException;
import europass.ewa.tools.ga.info.DateRange;
import europass.ewa.tools.ga.manager.data.HibernateDownloads;
import europass.ewa.tools.ga.manager.data.HibernateInfo;
import europass.ewa.tools.ga.manager.data.HibernateVisits;
import europass.ewa.tools.utils.GAExecuteMailSenderImpl;

public class DatabaseGAStatisticsLogger implements GAStatisticsLogger {

	private static final Logger LOG = LoggerFactory.getLogger(DatabaseGAStatisticsLogger.class);
	private static final String CONFIGURATION_PROMPT = "Check your hibernate .properties file";
	private static final String TRACKED_DATA_MISMATCH = "Do not try to insert data from GA. There is a mismatch between tracked data format and the database data format";

	private static final String SUBJECT_PREFIX = "GA Database Errror: [ANALYTICS] Attention: database error(s) while trying to insert GA data for ";
	
	private GAExecuteMailSenderImpl mailSender;
	
	private final SessionFactory factory;
	@Inject
	public DatabaseGAStatisticsLogger(SessionFactory factory, GAExecuteMailSenderImpl mailSender) {
		
		this.factory = factory;
		this.mailSender = mailSender;
	}

	public GAExecuteMailSenderImpl getMailSender(){
	
		return mailSender;
	}
	
	@Override
	public <E extends HibernateInfo> void log(HibernateTablesTypes type, List<E> records, DateRange dateRange){
		Session session = null;
		
		int totalFailedRows = 0;
		int totalFailedVolume = 0;
		
		try {
			session = factory.openSession();
			Transaction tx = null;
			
			for (E row : records){
				
				try{
					
					tx = session.beginTransaction();
					session.save(row);
					tx.commit();
					LOG.info("SUCCESSFUL INSERT for "+type.getDescription() +": "+row.printDetails());
				
				} catch (Exception e) {

					try{
						
						if( tx != null && tx.isActive() ){
							tx.rollback();
							session.clear();
						}
							
						if(e.getClass().getName().equals("org.hibernate.exception.ConstraintViolationException")){
							fetchUpdateRowVolume(session, row, type, row.getVolume());
						}
						
					} catch (Exception ex) {
						LOG.debug("Hibernate Transaction rollback failed for table "+type.getDescription(), ex);
					}

					if(!e.getClass().getName().equals("org.hibernate.exception.ConstraintViolationException")){
						totalFailedRows += 1;
						totalFailedVolume += row.getVolume();
						
						String message = "FAILED INSERT for "+type.getDescription()+": "+row.printDetails() + "<br/><u>Cause</u>: " + e.getMessage();
						
						LOG.debug("Hibernate insert failed with ", e);
						LOG.error(message);
						
						this.appendError(e, message);
						
					}
				}
			}
		}
		catch ( final Exception e ){
			this.appendError(e, e.getMessage());
			LOG.debug( " " ,  e );
		}
		finally {
			if(session.isOpen())
				session.close();
			
			if(mailSender.getErrorsMap().size() > 0){
				if(dateRange.isPeriod())
					this.constructSendFailureMessage(dateRange.getFrom().getYear(), dateRange.getFrom().getMonthOfYear(), 0);
				else
					this.constructSendFailureMessage(dateRange.getFrom().getYear(), dateRange.getFrom().getMonthOfYear(), dateRange.getFrom().getDayOfMonth());
			}
			
			LOG.warn("NUMBER OF FAILED INSERTIONS: "+totalFailedRows);
			LOG.warn("NUMBER OF VOLUMES LOST: "+totalFailedVolume);
		}
	}

	public void deleteMassive(HibernateTablesTypes hTable, DateRange dateRange) throws InterruptExecutionException{
		Session session = null;
		Transaction tx = null;
		
		try {
			
			session = factory.openSession();
			tx = session.beginTransaction();
			deleteFromTable(session, hTable, dateRange);
			tx.commit();
			LOG.info("Massive delete successfully executed for "+hTable);
			
		} catch (Exception e) {
			try {
				if( tx != null && tx.isActive() ){
					tx.rollback();
					session.clear();
				}
			} catch (Exception ex) {
				LOG.debug("Hibernate Transaction rollback failed for table "+hTable, ex);
			}
			LOG.debug("Hibernate insert failed with ", e);
			
			String message = "Massive delete failed for "+hTable+"<br/><u>Cause</u>: " + e.getMessage();
			LOG.error(message);
			this.appendError(e, message);
			
			if(mailSender.getErrorsMap().size() > 0){
				if(dateRange.isPeriod())
					this.constructSendFailureMessage(dateRange.getFrom().getYear(), dateRange.getFrom().getMonthOfYear(), 0);
				else
					this.constructSendFailureMessage(dateRange.getFrom().getYear(), dateRange.getFrom().getMonthOfYear(), dateRange.getFrom().getDayOfMonth());
			}
			
			throw new InterruptExecutionException(e);
		}
	}
	
	protected void deleteFromTable(Session session, HibernateTablesTypes hTable, DateRange dateRange){
		
		String hql = "DELETE FROM "+hTable+" e WHERE e.date";
		Query query = null;
		
		if(!dateRange.getFrom().equals(dateRange.getTo())){
			
			hql += " >= :dateFrom AND e.date <= :dateTo";
			query = session.createQuery( hql ).setDate("dateFrom", dateRange.getFrom().toDate()).setDate("dateTo", dateRange.getTo().toDate());
		}
		else{
			hql += " = :dateFrom";
			query = session.createQuery( hql ).setDate("dateFrom", dateRange.getFrom().toDate());
		}
		query.executeUpdate();

	}

	public void fetchUpdateRowVolume(Session session, HibernateInfo row, HibernateTablesTypes type, int volume) {

//		Session session = factory.openSession();
		
		
		String hql = "FROM "+type+" e ";
		
		Query query = null;
		
		try{
			
			switch(type){
				
				case HibernateVisits:
	
					HibernateVisits currentVisitsRow = (HibernateVisits) row;
	
					hql += "WHERE e.year = :year " +
							" AND e.month = :month " +
							" AND e.day = :day " +
							" AND e.iso_country_code = :iso_country_code";
					
					query = session.createQuery( hql )
									.setInteger("year", currentVisitsRow.getYear())
									.setInteger("month", currentVisitsRow.getMonth())
									.setInteger("day", currentVisitsRow.getDay())
									.setString("iso_country_code", currentVisitsRow.getIso_country_code());
					
					query.setMaxResults( 1 );
					HibernateVisits visitsRowToUpdate = (HibernateVisits)query.list().get(0);
					visitsRowToUpdate.setVolume(row.getVolume() + volume);
	
					session.update(visitsRowToUpdate);
					session.flush();
					LOG.warn("SUCCESSFUL UPDATE volume for "+type.getDescription() +": "+visitsRowToUpdate.printDetails());
					break;
		
				case HibernateDownloads:
					
					HibernateDownloads currentDownloadsRow = (HibernateDownloads) row;
	
					hql += "WHERE e.year = :year " +
							" AND e.month = :month " +
							" AND e.day = :day " +
							" AND e.document = :document" +
							" AND e.type = :type" +
							" AND e.iso_country_code = :iso_country_code" +
							" AND e.iso_language_code = :iso_language_code" +
							" AND e.ip_country = :ip_country";
					
					query = session.createQuery( hql )
									.setInteger("year", currentDownloadsRow.getYear())
									.setInteger("month", currentDownloadsRow.getMonth())
									.setInteger("day", currentDownloadsRow.getDay())
									.setString("document", currentDownloadsRow.getDocument())
									.setString("type", currentDownloadsRow.getType())
									.setString("iso_country_code", currentDownloadsRow.getIso_country_code())
									.setString("iso_language_code", currentDownloadsRow.getIso_language_code())
									.setString("ip_country", currentDownloadsRow.getIp_country());
					
					query.setMaxResults( 1 );
					HibernateDownloads downloadsRowToUpdate = (HibernateDownloads)query.list().get(0);
					downloadsRowToUpdate.setVolume(downloadsRowToUpdate.getVolume() + volume);
	
					session.update(downloadsRowToUpdate);
//					session.flush();
					LOG.warn("SUCCESSFUL UPDATE volume for "+type.getDescription() +": "+downloadsRowToUpdate.printDetails());
					break;
			}
		
		}catch (Exception e) {
			
//			try{
//				if( tx != null && tx.isActive() ){
//					tx.rollback();
//					session.clear();
//				}
//			} catch (Exception ex) {
//				LOG.debug("Hibernate Transaction rollback failed for table "+type.getDescription(), ex);
//			}
//			
//			LOG.debug("Hibernate update failed with ", e);
			LOG.error("FAILED UPDATE for "+type.getDescription()+": "+row.printDetails() + " due to " + e.getMessage());
		}
	}
	
	@Override
	public HibernateVisits fetchLatestVisit() {

		Session session = factory.openSession();
		session.clear();
		
		String hql = " FROM HibernateVisits e ORDER BY e.id ASC";
	
		Query query = session.createQuery( hql );
		query.setMaxResults( 1 );
		
		return (HibernateVisits)query.list().get(0);
	}
	
	@Override
	public HibernateDownloads fetchLatestDowload() {

		Session session = factory.openSession();
		session.clear();
		
		String hql = " FROM HibernateDownloads e ORDER BY e.id ASC";
	
		Query query = session.createQuery( hql );
		query.setMaxResults( 1 );
		
		return (HibernateDownloads)query.list().get(0);
	}

	protected void delete( Object row ){
		Session session = factory.openSession();
		session.clear();
		Transaction tx = null;
		try {
			tx = session.beginTransaction();
			session.delete(row);
			tx.commit();
			
		} catch (Exception e) {
			try {
				if ( tx != null ) {
					tx.rollback();
				}
			} catch (Exception ex) {
				LOG.error("DatabaseStatisticsLogger:log - Failed to rollback", ex);
			}
			LOG.error("DatabaseStatisticsLogger:log ", e);
		}
	}	
	
	// Used for testing
	public boolean existsVisit(HibernateVisits row) {

		Session session = factory.openSession();
		session.clear();
		
		String hql = "FROM " + TableArgumentsTypes.visits + " e WHERE e.id = :id";
		
		Query query = session.createQuery( hql )
				.setInteger("id", row.getId());
		
		return (query.uniqueResult() != null);
	}
	
	// Used for testing
	public int countTablePeriodDate(String tableName, int year, int month, int ... day) {

		Session session = factory.openSession();
		session.clear();
		
		String hql = "FROM "+tableName+" e "
						+ "WHERE e.year="+year+" "
						+ "AND e.month="+month;
		
		if(day != null && day.length > 0)
			hql += " AND e.day="+day[0];
		
		Query query = session.createQuery( hql );
		
		return query.list().size();
	}
	
	// Used for testing
	public void removeFromTablePeriodDate(String tableName, int year, int month, int ... day) {

		Session session = factory.openSession();
		session.clear();
		
		String hql = "FROM "+tableName+" e "
						+ "WHERE e.year="+year+" "
						+ "AND e.month="+month;
		
		if(day != null && day.length > 0)
			hql += " AND e.day="+day[0];
		
		Query query = session.createQuery( hql );
		
		for(Object row : query.list())
			this.delete(row);
		
	}

	private void appendError(Exception e, String message){
		
		String causeClassName = e.getClass().getCanonicalName();
		
		GAJsonResponseError error = ErrorTypesRecommendations.getError("800", ErrorTypesRecommendations.databaseError, message);
		error.setDbException(causeClassName);

		// Get exception exact class name
		causeClassName = causeClassName.substring(causeClassName.lastIndexOf(".") + 1, causeClassName.length());
		
		// Dispatch by class name and configure GAJsonResponseError object
		switch(causeClassName){
		
			case "GenericJDBCException":
			case "JDBCConnectionException":
				error.setRecommendation(CONFIGURATION_PROMPT);
				break;

			case "DataException":
				error.setRecommendation(TRACKED_DATA_MISMATCH);
				break;
				
				
				
				
			default:
				break;
		}
		
		Map<String, GAJsonResponseError> errorsMap = this.getMailSender().getErrorsMap();
		errorsMap.put(""+errorsMap.size(), error);
	}

	private void constructSendFailureMessage(int year, int month, int day){
		
		String emailBody = mailSender.constructDBFailureMessage();
		mailSender.constructAndSendStandardMailWithPeriod(year, month, day, SUBJECT_PREFIX, emailBody);
	}
}