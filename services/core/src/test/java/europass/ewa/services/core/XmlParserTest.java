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
package europass.ewa.services.core;

import static java.lang.Thread.currentThread;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;

import org.apache.commons.io.FileUtils;
import org.hamcrest.CoreMatchers;
import org.junit.Test;

import com.google.inject.Guice;
import com.google.inject.Injector;
import europass.ewa.Constants;
import europass.ewa.services.compatibility.XMLBackwardCompatibility;
import europass.ewa.services.compatibility.XmlCompatibilityModule;

public class XmlParserTest {
	
	private static Injector injector = null;
	
	private XMLBackwardCompatibility compatibility = null;
	
	public XmlParserTest(){
		
		injector = Guice.createInjector(
				new XmlCompatibilityModule()
			);
		
		compatibility = injector.getInstance(XMLBackwardCompatibility.class);
	}

	@Test
	public void parse10() throws IOException, URISyntaxException{
		
		final File file = new File( currentThread().getContextClassLoader().getResource("xml/Europass-CV-version1.0.xml").toURI());
		
		assertNotNull( file );
		
		String xml = FileUtils.readFileToString( file, Constants.UTF8_ENCODING );
		
		assertNotNull( xml );
		
		boolean isOld = xml.indexOf("europass:learnerinfo") > 0;
		
		assertThat( isOld, CoreMatchers.is(true) );
		
		String newXml = compatibility.transform( xml );
		
		assertNotNull( newXml );
		
		boolean isV30 = newXml.indexOf("SkillsPassport") > 0;
		
		assertThat( isV30, CoreMatchers.is(true) );
		
		boolean hasSurname = newXml.indexOf("Σουγιάς") > 0;
		
		assertThat( hasSurname, CoreMatchers.is(true) );
	}
	
	@Test
	public void parse11() throws IOException, URISyntaxException{
		final File file = new File( currentThread().getContextClassLoader().getResource("xml/Europass-CV-version1.1.xml").toURI());
		
		assertNotNull( file );
		
		String xml = FileUtils.readFileToString( file, Constants.UTF8_ENCODING );
		
		assertNotNull( xml );

		boolean isOld = xml.indexOf("europass:learnerinfo") > 0;
		
		assertThat( isOld, CoreMatchers.is(true) );
		
		String newXml = compatibility.transform( xml );
		
		assertNotNull( newXml );
		
		boolean isV30 = newXml.indexOf("SkillsPassport") > 0;
		
		assertThat( isV30, CoreMatchers.is(true) );
		
		boolean hasSurname = newXml.indexOf("Σουγιάς") > 0;
		
		assertThat( hasSurname, CoreMatchers.is(true) );
	}
	
	@Test
	public void parse12() throws IOException, URISyntaxException{
		final File file = new File( currentThread().getContextClassLoader().getResource("xml/Europass-CV-version1.2.xml").toURI());
		
		assertNotNull( file );
		
		String xml = FileUtils.readFileToString( file, Constants.UTF8_ENCODING );
		
		assertNotNull( xml );
		
		boolean isOld = xml.indexOf("europass:learnerinfo") > 0;
		
		assertThat( isOld, CoreMatchers.is(true) );
			
		String newXml = compatibility.transform( xml );
		
		assertNotNull( newXml );
		
		boolean isV30 = newXml.indexOf("SkillsPassport") > 0;
		
		assertThat( isV30, CoreMatchers.is(true) );
		
		boolean hasSurname = newXml.indexOf("Σουγιάς") > 0;
		
		assertThat( hasSurname, CoreMatchers.is(true) );
	}
	
	@Test
	public void parse20() throws IOException, URISyntaxException{
		final File file = new File( currentThread().getContextClassLoader().getResource("xml/Europass-CV-version2.0.xml").toURI());
		
		assertNotNull( file );
		
		String xml = FileUtils.readFileToString( file, Constants.UTF8_ENCODING );
		
		assertNotNull( xml );
		
		boolean isOld = xml.indexOf("europass:learnerinfo") > 0;
		
		assertThat( isOld, CoreMatchers.is(true) );
		
		String newXml = compatibility.transform( xml );
		
		assertNotNull( newXml );
		
		boolean isV30 = newXml.indexOf("SkillsPassport") > 0;
		
		assertThat( isV30, CoreMatchers.is(true) );
		
		boolean hasSurname = newXml.indexOf("Σουγιάς") > 0;
		
		assertThat( hasSurname, CoreMatchers.is(true) );
	}
	
	@Test
	public void parse30() throws IOException, URISyntaxException{
		final File file = new File( currentThread().getContextClassLoader().getResource("xml/Europass-CV-version3.0.xml").toURI());
		
		assertNotNull( file );
		
		String xml = FileUtils.readFileToString( file, Constants.UTF8_ENCODING );
		
		assertNotNull( xml );
		
		boolean isOld = xml.indexOf("europass:learnerinfo") > 0;
		
		assertThat( isOld, CoreMatchers.is(false) );
		
		String newXml = compatibility.transform( xml );
		
		assertNotNull( newXml );
		
		boolean isV30 = newXml.indexOf("SkillsPassport") > 0;
		
		assertThat( isV30, CoreMatchers.is(true) );
		
		boolean hasSurname = newXml.indexOf("Σουγιάς") > 0;
		
		assertThat( hasSurname, CoreMatchers.is(true) );
		
		//String version = XmlPath.from(newXml).get("SkillsPassport.DocumentInfo.XSDVersion");
		
		//assertThat("Version", version.trim(), is(XmlVersion.LATEST.getCode()));
	}
	
	@Test
	public void parse32CL() throws IOException, URISyntaxException{
		final File file = new File( currentThread().getContextClassLoader().getResource("xml/Europass_CL_V3.2.0_Example.xml").toURI());
		
		assertNotNull( file );
		
		String xml = FileUtils.readFileToString( file, Constants.UTF8_ENCODING );
		
		assertNotNull( xml );
		
		boolean isOld = xml.indexOf("europass:learnerinfo") > 0;
		
		assertThat( isOld, CoreMatchers.is(false) );
		
		String newXml = compatibility.transform( xml );
		
		assertNotNull( newXml );
		
		boolean isV3 = newXml.indexOf("SkillsPassport") > 0;
		
		assertThat( isV3, CoreMatchers.is(true) );
		
		boolean hasSurname = newXml.indexOf("Burnett") > 0;
		
		assertThat( hasSurname, CoreMatchers.is(true) );
	}
	
	@Test
	public void parse32CV() throws IOException, URISyntaxException{
		final File file = new File( currentThread().getContextClassLoader().getResource("xml/Europass_CV_V3.2.0_Example.xml").toURI());
		
		assertNotNull( file );
		
		String xml = FileUtils.readFileToString( file, Constants.UTF8_ENCODING );
		
		assertNotNull( xml );
		
		boolean isOld = xml.indexOf("europass:learnerinfo") > 0;
		
		assertThat( isOld, CoreMatchers.is(false) );
		
		String newXml = compatibility.transform( xml );
		
		assertNotNull( newXml );
		
		boolean isV3 = newXml.indexOf("SkillsPassport") > 0;
		
		assertThat( isV3, CoreMatchers.is(true) );
		
		boolean hasSurname = newXml.indexOf("NORRIS") > 0;
		
		assertThat( hasSurname, CoreMatchers.is(true) );
	}
	
	@Test
	public void parse32CVCL() throws IOException, URISyntaxException{
		final File file = new File( currentThread().getContextClassLoader().getResource("xml/Europass_CVwithCL_V3.2.0_Example.xml").toURI());
		
		assertNotNull( file );
		
		String xml = FileUtils.readFileToString( file, Constants.UTF8_ENCODING );
		
		assertNotNull( xml );
		
		boolean isOld = xml.indexOf("europass:learnerinfo") > 0;
		
		assertThat( isOld, CoreMatchers.is(false) );
		
		String newXml = compatibility.transform( xml );
		
		assertNotNull( newXml );
		
		boolean isV3 = newXml.indexOf("SkillsPassport") > 0;
		
		assertThat( isV3, CoreMatchers.is(true) );
		
		boolean hasSurname = newXml.indexOf("NORRIS") > 0;
		
		assertThat( hasSurname, CoreMatchers.is(true) );
	}
	
	@Test
	public void parse32LP() throws IOException, URISyntaxException{
		final File file = new File( currentThread().getContextClassLoader().getResource("xml/Europass_LP_V.3.2.0_Example.xml").toURI());
		
		assertNotNull( file );
		
		String xml = FileUtils.readFileToString( file, Constants.UTF8_ENCODING );
		
		assertNotNull( xml );
		
		boolean isOld = xml.indexOf("europass:learnerinfo") > 0;
		
		assertThat( isOld, CoreMatchers.is(false) );
		
		String newXml = compatibility.transform( xml );
		
		assertNotNull( newXml );
		
		boolean isV3 = newXml.indexOf("SkillsPassport") > 0;
		
		assertThat( isV3, CoreMatchers.is(true) );
		
		boolean hasSurname = newXml.indexOf("Sougias") > 0;
		
		assertThat( hasSurname, CoreMatchers.is(true) );
	}
	
	@Test
	public void parse33CV() throws IOException, URISyntaxException{
		final File file = new File( currentThread().getContextClassLoader().getResource("xml/Europass_CV_V3.3.0_Example.xml").toURI());
		
		assertNotNull( file );
		
		String xml = FileUtils.readFileToString( file, Constants.UTF8_ENCODING );
		
		assertNotNull( xml );
		
		boolean isOld = xml.indexOf("europass:learnerinfo") > 0;
		
		assertThat( isOld, CoreMatchers.is(false) );
		
		String newXml = compatibility.transform( xml );
		
		assertNotNull( newXml );
		
		boolean isV30 = newXml.indexOf("SkillsPassport") > 0;
		
		assertThat( isV30, CoreMatchers.is(true) );
		
		boolean hasSurname = newXml.indexOf("NORRIS") > 0;
		
		assertThat( hasSurname, CoreMatchers.is(true) );
		
		boolean hasComputer = newXml.indexOf("Computer skills") > 0;
		
		boolean hasCert = newXml.indexOf("ACDL Certificate") > 0;
		
		assertThat( hasComputer, CoreMatchers.is(true) );	
		
		assertThat( hasCert, CoreMatchers.is(true) );
        }
	
	@Test
	public void parse33CL() throws IOException, URISyntaxException{
		final File file = new File( currentThread().getContextClassLoader().getResource("xml/Europass_CL_V3.3.0_Example.xml").toURI());
		
		assertNotNull( file );
		
		String xml = FileUtils.readFileToString( file, Constants.UTF8_ENCODING );
		
		assertNotNull( xml );
		
		boolean isOld = xml.indexOf("europass:learnerinfo") > 0;
		
		assertThat( isOld, CoreMatchers.is(false) );
		
		String newXml = compatibility.transform( xml );
		
		assertNotNull( newXml );
		
		boolean isV30 = newXml.indexOf("SkillsPassport") > 0;
		
		assertThat( isV30, CoreMatchers.is(true) );
		
		boolean hasSurname = newXml.indexOf("Burnett") > 0;
		
		assertThat( hasSurname, CoreMatchers.is(true) );
	}
	
	@Test
	public void parse33CVCL() throws IOException, URISyntaxException{
		final File file = new File( currentThread().getContextClassLoader().getResource("xml/Europass_CVwithCL_V3.3.0_Example.xml").toURI());
		
		assertNotNull( file );
		
		String xml = FileUtils.readFileToString( file, Constants.UTF8_ENCODING );
		
		assertNotNull( xml );
		
		boolean isOld = xml.indexOf("europass:learnerinfo") > 0;
		
		assertThat( isOld, CoreMatchers.is(false) );
		
		String newXml = compatibility.transform( xml );
		
		assertNotNull( newXml );
		
		boolean isV30 = newXml.indexOf("SkillsPassport") > 0;
		
		assertThat( isV30, CoreMatchers.is(true) );
		
		boolean hasSurname = newXml.indexOf("NORRIS") > 0;
		
		assertThat( hasSurname, CoreMatchers.is(true) );

		boolean hasComputer = newXml.indexOf("Bla bla Microsoft Word bla bla.") > 0;
	
		assertThat( hasComputer, CoreMatchers.is(true) );

	}
	
	@Test
	public void parse33LP() throws IOException, URISyntaxException{
		final File file = new File( currentThread().getContextClassLoader().getResource("xml/Europass_LP_V.3.3.0_Example.xml").toURI());
		
		assertNotNull( file );
		
		String xml = FileUtils.readFileToString( file, Constants.UTF8_ENCODING );
		
		assertNotNull( xml );
		
		boolean isOld = xml.indexOf("europass:learnerinfo") > 0;
		
		assertThat( isOld, CoreMatchers.is(false) );
		
		String newXml = compatibility.transform( xml );
		
		assertNotNull( newXml );
		
		boolean isV30 = newXml.indexOf("SkillsPassport") > 0;
		
		assertThat( isV30, CoreMatchers.is(true) );
		
		boolean hasSurname = newXml.indexOf("Sougias") > 0;
		
		assertThat( hasSurname, CoreMatchers.is(true) );
		
		boolean hasComputer = newXml.indexOf("Computer skills") > 0;
		
		boolean hasCert = newXml.indexOf("ACDL Certificate") > 0;
		
		assertThat( hasComputer, CoreMatchers.is(true) );	
		
		assertThat( hasCert, CoreMatchers.is(true) );
        }
	
	@Test
	public void parse33All() throws IOException, URISyntaxException{
		final File file = new File( currentThread().getContextClassLoader().getResource("xml/Europass_CV_ESP_CL_LP_V3.3.0_Example.xml").toURI());
		
		assertNotNull( file );
		
		String xml = FileUtils.readFileToString( file, Constants.UTF8_ENCODING );
		
		assertNotNull( xml );
		
		boolean isOld = xml.indexOf("europass:learnerinfo") > 0;
		
		assertThat( isOld, CoreMatchers.is(false) );
		
		String newXml = compatibility.transform( xml );
		
		assertNotNull( newXml );
		
		boolean isV30 = newXml.indexOf("SkillsPassport") > 0;
		
		assertThat( isV30, CoreMatchers.is(true) );
		
		boolean hasSurname = newXml.indexOf("NORRIS") > 0;
		
		assertThat( hasSurname, CoreMatchers.is(true) );
		
		boolean hasComputer = newXml.indexOf("Computer skills") > 0;
		
		boolean hasCert = newXml.indexOf("ACDL Certificate") > 0;
		
		assertThat( hasComputer, CoreMatchers.is(true) );	
		
		assertThat( hasCert, CoreMatchers.is(true) );

	}
        
	@Test
	public void parse34CV() throws IOException, URISyntaxException{
		final File file = new File( currentThread().getContextClassLoader().getResource("xml/Europass_CV_V3.4.0_Example.xml").toURI());
		
		assertNotNull( file );
		
		String xml = FileUtils.readFileToString( file, Constants.UTF8_ENCODING );
		
		assertNotNull( xml );
		
		boolean isOld = xml.indexOf("europass:learnerinfo") > 0;
		
		assertThat( isOld, CoreMatchers.is(false) );
		
		String newXml = compatibility.transform( xml );
		
		assertNotNull( newXml );
		
		boolean isV30 = newXml.indexOf("SkillsPassport") > 0;
		
		assertThat( isV30, CoreMatchers.is(true) );
		
		boolean hasSurname = newXml.indexOf("NORRIS") > 0;
		
		assertThat( hasSurname, CoreMatchers.is(true) );
		
		boolean hasComputer = newXml.indexOf("Computer skills") > 0;
		
		boolean hasCert = newXml.indexOf("ACDL Certificate") > 0;
		
		assertThat( hasComputer, CoreMatchers.is(true) );	
		
		assertThat( hasCert, CoreMatchers.is(true) );
        }
	
	@Test
	public void parse34CL() throws IOException, URISyntaxException{
		final File file = new File( currentThread().getContextClassLoader().getResource("xml/Europass_CL_V3.4.0_Example.xml").toURI());
		
		assertNotNull( file );
		
		String xml = FileUtils.readFileToString( file, Constants.UTF8_ENCODING );
		
		assertNotNull( xml );
		
		boolean isOld = xml.indexOf("europass:learnerinfo") > 0;
		
		assertThat( isOld, CoreMatchers.is(false) );
		
		String newXml = compatibility.transform( xml );
		
		assertNotNull( newXml );
		
		boolean isV30 = newXml.indexOf("SkillsPassport") > 0;
		
		assertThat( isV30, CoreMatchers.is(true) );
		
		boolean hasSurname = newXml.indexOf("Burnett") > 0;
		
		assertThat( hasSurname, CoreMatchers.is(true) );
	}
	
	@Test
	public void parse34CVCL() throws IOException, URISyntaxException{
		final File file = new File( currentThread().getContextClassLoader().getResource("xml/Europass_CVwithCL_V3.4.0_Example.xml").toURI());
		
		assertNotNull( file );
		
		String xml = FileUtils.readFileToString( file, Constants.UTF8_ENCODING );
		
		assertNotNull( xml );
		
		boolean isOld = xml.indexOf("europass:learnerinfo") > 0;
		
		assertThat( isOld, CoreMatchers.is(false) );
		
		String newXml = compatibility.transform( xml );
		
		assertNotNull( newXml );
		
		boolean isV30 = newXml.indexOf("SkillsPassport") > 0;
		
		assertThat( isV30, CoreMatchers.is(true) );
		
		boolean hasSurname = newXml.indexOf("NORRIS") > 0;
		
		assertThat( hasSurname, CoreMatchers.is(true) );

		boolean hasComputer = newXml.indexOf("Bla bla Microsoft Word bla bla.") > 0;
	
		assertThat( hasComputer, CoreMatchers.is(true) );

	}
	
	@Test
	public void parse34LP() throws IOException, URISyntaxException{
		final File file = new File( currentThread().getContextClassLoader().getResource("xml/Europass_LP_V.3.4.0_Example.xml").toURI());
		
		assertNotNull( file );
		
		String xml = FileUtils.readFileToString( file, Constants.UTF8_ENCODING );
		
		assertNotNull( xml );
		
		boolean isOld = xml.indexOf("europass:learnerinfo") > 0;
		
		assertThat( isOld, CoreMatchers.is(false) );
		
		String newXml = compatibility.transform( xml );
		
		assertNotNull( newXml );
		
		boolean isV30 = newXml.indexOf("SkillsPassport") > 0;
		
		assertThat( isV30, CoreMatchers.is(true) );
		
		boolean hasSurname = newXml.indexOf("Sougias") > 0;
		
		assertThat( hasSurname, CoreMatchers.is(true) );
		
		boolean hasComputer = newXml.indexOf("Computer skills") > 0;
		
		boolean hasCert = newXml.indexOf("ACDL Certificate") > 0;
		
		assertThat( hasComputer, CoreMatchers.is(true) );	
		
		assertThat( hasCert, CoreMatchers.is(true) );
        }
	
	@Test
	public void parse34All() throws IOException, URISyntaxException{
		final File file = new File( currentThread().getContextClassLoader().getResource("xml/Europass_CV_ESP_CL_LP_V3.4.0_Example.xml").toURI());
		
		assertNotNull( file );
		
		String xml = FileUtils.readFileToString( file, Constants.UTF8_ENCODING );
		
		assertNotNull( xml );
		
		boolean isOld = xml.indexOf("europass:learnerinfo") > 0;
		
		assertThat( isOld, CoreMatchers.is(false) );
		
		String newXml = compatibility.transform( xml );
		
		assertNotNull( newXml );
		
		boolean isV30 = newXml.indexOf("SkillsPassport") > 0;
		
		assertThat( isV30, CoreMatchers.is(true) );
		
		boolean hasSurname = newXml.indexOf("NORRIS") > 0;
		
		assertThat( hasSurname, CoreMatchers.is(true) );
		
		boolean hasComputer = newXml.indexOf("Computer skills") > 0;
		
		boolean hasCert = newXml.indexOf("ACDL Certificate") > 0;
		
		assertThat( hasComputer, CoreMatchers.is(true) );	
		
		assertThat( hasCert, CoreMatchers.is(true) );

	}
}
