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
package europass.ewa.tools.ga;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;
import org.joda.time.DateTime;

import com.google.inject.AbstractModule;
import com.google.inject.CreationException;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Scopes;

import europass.ewa.database.guice.HibernateModule;
import europass.ewa.mail.MailSender;
import europass.ewa.modules.ExternalFileModule;
import europass.ewa.modules.LogbackConfigurationModule;
import europass.ewa.tools.ga.executor.GAStepsExecutorVisitor;
import europass.ewa.tools.ga.executor.Visitor;
import europass.ewa.tools.ga.guice.GAStatisticsModule;
import europass.ewa.tools.ga.info.GAStatisticsInfo;
import europass.ewa.tools.ga.process.GAStatisticsProcess;
import europass.ewa.tools.utils.GAExecuteMailSenderImpl;
import europass.ewa.tools.utils.Utils;

public class GAStatisticsRunner {

	private static Options options;
	
	private static final String PROPER_DATE_INPUT_PATTERN = "Proper exact_date input pattern: YYYY or ( YYYY MM ) or ( YYYY MM DD )";
	private static final String PROPER_DATE = "Date value must not be greater or equal to today's value";
	private static final String NO_NUMERIC = "Year, Month, Day values must be numbers";
	
	private static final String GANALYTICS_PROPERTIES_FILENAME = "europass-webapps-tools-ga.properties";
	private static final String HIBERNATE_PROPERTIES_FILENAME = "europass-webapps-tools-ga-hibernate.properties";
	
	private static final String LOGBACK_CONFIG_PROPERTY_NAME = "europass-webapps-tools-ganalytics";
	
	private static CommandLine cmd = null;
	private static CommandLineParser parser = null;
	
	public static void main(String[] args) {
		
		try {
			parser = new PosixParser();
			List<List<String>> userArgsList = getUserArgs(args);

			Injector injector = getInjector(userArgsList.get(0).get(0));	// Get config Path from arguments list
			GAStatisticsProcess process = injector.getInstance(GAStatisticsProcess.class); 
			process.process(injector.getInstance(GAStatisticsInfo.class), userArgsList);
		} catch (RuntimeException e) {
			System.exit(-1);
		}
	}
	
	protected static Injector getInjector(String path) throws RuntimeException {

		String hibernatePath = path + HIBERNATE_PROPERTIES_FILENAME;
		
		Properties hibernateProperties = Utils.getProperties(hibernatePath,true, GAStatisticsRunner.class);
		
		if(hibernateProperties == null){
			
			System.err.println("Hibernate properties file not present or not accessible. Check the configuration path.");
			throw new RuntimeException();

		}

		hibernateProperties.setProperty("hibernate.connection.driver_class", hibernateProperties.getProperty("hibernate.connection.driver_class"));
		hibernateProperties.setProperty("hibernate.connection.url", hibernateProperties.getProperty("hibernate.connection.url"));
		hibernateProperties.setProperty("hibernate.connection.username", hibernateProperties.getProperty("hibernate.connection.username"));
		hibernateProperties.setProperty("hibernate.connection.password", hibernateProperties.getProperty("hibernate.connection.password"));
		hibernateProperties.setProperty("hibernate.show_sql", hibernateProperties.getProperty("hibernate.show_sql"));
		hibernateProperties.setProperty("hibernate.dialect", hibernateProperties.getProperty("hibernate.dialect"));
		hibernateProperties.setProperty("hibernate.connection.release_mode", hibernateProperties.getProperty("hibernate.connection.release_mode"));

		try{
		
			Injector injector = Guice.createInjector(
									new ExternalFileModule(path,GANALYTICS_PROPERTIES_FILENAME),
									new LogbackConfigurationModule(LOGBACK_CONFIG_PROPERTY_NAME),
									new HibernateModule( hibernateProperties , Scopes.SINGLETON ),
									new GAStatisticsModule(),
									new AbstractModule() {
										@Override
										protected void configure() {
											bind(MailSender.class).to(GAExecuteMailSenderImpl.class).asEagerSingleton();
											bind(Visitor.class).to(GAStepsExecutorVisitor.class);
										}
									}
								);
			return injector;
		
		}catch(CreationException e){
			
			if(e.getCause() instanceof  org.hibernate.HibernateException)
				System.err.println("Check your hibernate .properties file configuration");
			else
				System.err.println("Internal Error");
			
			throw new RuntimeException();
		}
	}
	
	/**
	 * Construct the arguments as a List that consists of the below List<String> elements:
	 * 
	 * - first List<String> element:	configuration files path String
	 * - second List<String> element:	Date values as Strings (YYYY MM or YYY MM DD)
	 * - third List<String> element:	tableNames String(s) - OPTIONAL
	 * 
	 * @param args
	 * @return
	 * @throws RuntimeException 
	 */
	
	private static List<List<String>> getUserArgs(String[] args) throws RuntimeException{
		
		buildOptions();
		
		try {
			cmd = parser.parse(options, args);
		} catch (ParseException pe) {
			argumentsFailureAndExit(pe.getMessage());
		}		

		List<List<String>> userArgsList = new ArrayList<List<String>>();
		
		List<String> config = new ArrayList<String>();
		
		String path = cmd.getOptionValue("config");
		if(path.charAt(path.length()-1) != File.separatorChar){
		    path += File.separator;
		}		
		config.add(path);
		
		userArgsList.add(config);
		
		List<String> dates = new ArrayList<String>();
		handleDateArguments(dates);
		userArgsList.add(dates);
		
		List<String> table = new ArrayList<String>();
		if(cmd.getOptionValues("t") != null){
			for(int i = 0; i < cmd.getOptionValues("t").length; i++)
				table.add(cmd.getOptionValues("t")[i]);
		}
		
		userArgsList.add(table);
		
		return userArgsList;
	}
	
	private static void buildOptions() {
		options = new Options();
		
		OptionBuilder.withLongOpt("config");
		OptionBuilder.hasArg();
		OptionBuilder.withArgName("path");
		OptionBuilder.isRequired(true);
		OptionBuilder.withDescription("The absolute - local - path to where project and hibernate properties as well as private key files are located.");
		options.addOption(OptionBuilder.create('c'));

		OptionBuilder.withLongOpt("year");
		OptionBuilder.hasArg();
		OptionBuilder.withArgName("YYYY");
		OptionBuilder.withDescription("Year value of the exact_date to view statistics for."
				+ "If current year is given, it will present data for the previous year.");
		options.addOption(OptionBuilder.create('y'));

		OptionBuilder.withLongOpt("month");
		OptionBuilder.hasArg();
		OptionBuilder.withArgName("MM");
		OptionBuilder.withDescription("Month value of the exact_date to view statistics for. When month value is given, at least a Year value must also be given. "
				+ "If current year and month is given, it will present statistics data for previous month's period.");
		options.addOption(OptionBuilder.create('m'));
		
		OptionBuilder.withLongOpt("exact_date");
		OptionBuilder.hasArg();
		OptionBuilder.withArgName("DD");
		OptionBuilder.isRequired(false);
		OptionBuilder.withDescription("Day of exact_date to view statistics for. When day value is given, a Year and a Month value must also be given. "
				+ "If current exact_date is given, it will present data for the previous day.");
		options.addOption(OptionBuilder.create('d'));
		
		OptionBuilder.withLongOpt("tables");
		OptionBuilder.hasArgs(2);
		OptionBuilder.withArgName("table1 table2|table1|table2");
		OptionBuilder.isRequired(false);
		OptionBuilder.withValueSeparator(' ');
		OptionBuilder.withDescription("The Europass Statistcs database table names for which we want to retrieve GA data and store to. "
				+ "If none or invalid table names provided, all the tables of the Europass Statistcs database will be populated.");
		options.addOption(OptionBuilder.create('t'));
	}
	
	private static void argumentsFailureAndExit(String errMsg) throws RuntimeException{
		
		System.err.println(errMsg);
		HelpFormatter formatter = new HelpFormatter();
		formatter.printHelp("java GAStatisticsRunner", options);
		throw new RuntimeException();
	}
	
	private static void handleDateArguments(List<String> dates) throws RuntimeException{
		
		if(cmd == null)
			return;

		DateTime current = new DateTime();
		
		// In case of no year arguments
		if(cmd.getOptionValue("y") == null){
				
			// Handle Date input - progressive logic [ year [ month [ day ] ] ]
			if( cmd.getOptionValue("m") != null || cmd.getOptionValue("d") != null )
				argumentsFailureAndExit(PROPER_DATE_INPUT_PATTERN);
			
			if( cmd.getOptionValue("m") == null && cmd.getOptionValue("d") == null ){

				current = new DateTime().minusDays(1);
				
				dates.add(String.valueOf(current.getYear()));
				dates.add(String.valueOf(current.getMonthOfYear()));
				dates.add(String.valueOf(current.getDayOfMonth()));
			}
		}
		else{

			if( cmd.getOptionValue("m") == null && cmd.getOptionValue("d") != null )
				argumentsFailureAndExit(PROPER_DATE_INPUT_PATTERN);

			try{
			
				int yearGiven = Integer.valueOf(cmd.getOptionValue("y"));
				if(yearGiven <= current.getYear()){
				
					dates.add(cmd.getOptionValue("y"));
					
					if(cmd.getOptionValue("m") != null){
						int monthGiven = Integer.valueOf(cmd.getOptionValue("m"));
						if(monthGiven <= current.getMonthOfYear()){
							
							dates.add(cmd.getOptionValue("m"));
							
							if(monthGiven == current.getMonthOfYear()){
	
								if(cmd.getOptionValue("d") != null){
									int dayGiven = Integer.valueOf(cmd.getOptionValue("d"));
									if(dayGiven < current.getDayOfMonth()){
									
										dates.add(cmd.getOptionValue("d"));
									}
									else
										argumentsFailureAndExit(PROPER_DATE);
								}
								
							}else{
	
								if(cmd.getOptionValue("d") != null){
									dates.add(cmd.getOptionValue("d"));
								}
							}
						}
						else{
							if(yearGiven == current.getYear())
								argumentsFailureAndExit(PROPER_DATE);
						
							dates.add(cmd.getOptionValue("m"));
						}
					}
				}
				else
					argumentsFailureAndExit(PROPER_DATE);
			
			}catch(NumberFormatException ex){
				
				argumentsFailureAndExit(NO_NUMERIC);
			}
		}
	}
}
