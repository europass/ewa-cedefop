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
package europass.ewa.services.conversion;

import java.io.IOException;
import java.util.Map;

import org.hamcrest.CoreMatchers;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.TypeLiteral;
import com.google.inject.name.Names;

import europass.ewa.model.DocumentInfo;
import europass.ewa.model.Namespace;
import europass.ewa.model.PrintingPreference;
import europass.ewa.model.SkillsPassport;
import europass.ewa.model.conversion.ModelModule;
import europass.ewa.services.conversion.model.ExportableModel;
import europass.ewa.services.conversion.model.ExportableModelWrapper;
import europass.ewa.services.conversion.steps.DocumentUpdateDateStep;

public class DocumentDatesTest {

	static Map<String, PrintingPreference> ecvDefaultPrefs;
	
	static DocumentUpdateDateStep step;
	
	static ExportableModel model;
	
	@BeforeClass
	public static void prepare(){
		
		Injector injector = Guice.createInjector( 
				new AbstractModule() {
					@Override
					protected void configure() {
						bindConstant().annotatedWith(Names.named(Namespace.SCHEMA_LOCATION_DEFAULT_PARAM))
						.to(Namespace.NAMESPACE + " http://europass.cedefop.europa.eu/xml/EuropassSchema_V3.0.xsd");
				
						binder().requestStaticInjection( DocumentUpdateDateStep.class);
					}
				},
				new ModelModule()
				);
		ecvDefaultPrefs = 
				injector.getProvider( Key.get( new TypeLiteral<Map<String, PrintingPreference>>(){},
						Names.named( ModelModule.DEFAULT_CV_PREFS ) )).get();
		
		step = injector.getInstance( DocumentUpdateDateStep.class );
		model = new ExportableModelWrapper();
	}

	@Test
	public void withNoDocumentInfo() throws IOException{

		model.setModel(  ModelMocks.noDocumentInfo( ecvDefaultPrefs ) );
		step.doStep( model );
		
		SkillsPassport esp = model.getModel();
		
		DocumentInfo info = esp.getDocumentInfo();
		Assert.assertThat("Document Info is not null", 
				info, CoreMatchers.notNullValue());

		DateTime creationDate = info.getCreationDate();
		Assert.assertThat("Document Creation Date is not null", 
				creationDate, CoreMatchers.notNullValue());

		DateTime updateDate = info.getCreationDate();
		Assert.assertThat("Document Update Date is not null", 
				updateDate, CoreMatchers.notNullValue());
		
	}

	@Test
	public void withDocumentInfoCreationDate() throws IOException{
		model.setModel(  ModelMocks.withCreationDate( ecvDefaultPrefs ) );
		step.doStep( model );
		
		SkillsPassport esp = model.getModel();
		
		DocumentInfo info = esp.getDocumentInfo();
		Assert.assertThat("Document Info is not null", 
				info, CoreMatchers.notNullValue());

		DateTime expected = new DateTime(2014, 2, 28, 12, 35, DateTimeZone.UTC);
		
		DateTime creationDate = info.getCreationDate();
		Assert.assertThat("Document Creation Date is not null", 
				creationDate, CoreMatchers.equalTo(expected));

		DateTime updateDate = info.getCreationDate();
		Assert.assertThat("Document Update Date is not null", 
				updateDate, CoreMatchers.notNullValue());
	}

	@Test
	public void withDocumentInfoNoCreationDateWithLastUpdateDate() throws IOException{
		model.setModel(  ModelMocks.noCreationDateWithLastUpdateDate( ecvDefaultPrefs ) );
		step.doStep( model );
		
		SkillsPassport esp = model.getModel();
		
		DocumentInfo info = esp.getDocumentInfo();
		Assert.assertThat("Document Info is not null", 
				info, CoreMatchers.notNullValue());

		
		DateTime expected = new DateTime(2014, 2, 28, 12, 35, DateTimeZone.UTC);
		
		DateTime creationDate = info.getCreationDate();
		Assert.assertThat("Document Creation Date is not 28/2/2014", 
				creationDate, CoreMatchers.not(expected));

		DateTime updateDate = info.getCreationDate();
		Assert.assertThat("Document Update Date is not 28/2/2014", 
				updateDate, CoreMatchers.not(expected));
	}
	
//	@Test
//	public void toODTCV() throws IOException{
//		this.doTestEsp( EuropassDocumentType.ECV, ConversionFileType.OPEN_DOC, 600.0F, 770.0F );
//	}
//	@Test
//	public void toWordCV() throws IOException{
//		this.doTestEsp( EuropassDocumentType.ECV, ConversionFileType.WORD_DOC, 550.0F, 550.0F );
//	}
//	@Test
//	public void toPdfCV() throws IOException{
//		this.doTestEsp( EuropassDocumentType.ECV, ConversionFileType.PDF, 550.0F, 550.0F );
//	}
//	
//	@Test
//	public void toODTCVEsp() throws IOException{
//		this.doTestEsp( EuropassDocumentType.ECV_ESP, ConversionFileType.OPEN_DOC, 600.0F, 770.0F );
//	}
//	@Test
//	public void toWordCVEsp() throws IOException{
//		this.doTestEsp( EuropassDocumentType.ECV_ESP, ConversionFileType.WORD_DOC, 550.0F, 650.0F );
//	}
//	@Test
//	public void toPdfCVEsp() throws IOException{
//		this.doTestEsp( EuropassDocumentType.ECV_ESP, ConversionFileType.PDF, 550.0F, 550.0F );
//	}
//	
//	@Test
//	public void toODTEsp() throws IOException{
//		this.doTestEsp( EuropassDocumentType.ESP, ConversionFileType.OPEN_DOC, 600.0F, 770.0F );
//	}
//	@Test
//	public void toWordEsp() throws IOException{
//		this.doTestEsp( EuropassDocumentType.ESP, ConversionFileType.WORD_DOC, 550.0F, 650.0F );
//	}
//	@Test
//	public void toPdfEsp() throws IOException{
//		this.doTestEsp( EuropassDocumentType.ESP, ConversionFileType.PDF, 550.0F, 550.0F );
//	}
	
}
