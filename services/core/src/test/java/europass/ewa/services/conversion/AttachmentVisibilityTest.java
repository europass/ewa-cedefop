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
import java.io.InputStream;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.apache.pdfbox.io.IOUtils;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import com.google.common.collect.Collections2;
import com.google.common.collect.Iterables;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.TypeLiteral;
import com.google.inject.name.Names;

import europass.ewa.collections.Predicates;
import europass.ewa.enums.ConversionFileType;
import europass.ewa.enums.EuropassDocumentType;
import europass.ewa.enums.PDFLibrary;
import europass.ewa.model.Attachment;
import europass.ewa.model.AttachmentVisitor;
import europass.ewa.model.AttachmentsInfo;
import europass.ewa.model.Namespace;
import europass.ewa.model.PrintingPreference;
import europass.ewa.model.SkillsPassport;
import europass.ewa.model.conversion.ModelModule;
import europass.ewa.services.ODT;
import europass.ewa.services.PDF;
import europass.ewa.services.WORD;
import europass.ewa.services.conversion.model.ExportableModel;
import europass.ewa.services.conversion.model.ExportableModelWrapper;
import europass.ewa.services.conversion.steps.AttachmentsVisibilityStep;
import europass.ewa.services.conversion.steps.ODTAttachmentVisitor;
import europass.ewa.services.conversion.steps.PDFAttachmentVisitor;
import europass.ewa.services.conversion.steps.WORDAttachmentVisitor;
import europass.ewa.services.files.ConcreteImageProcessing;
import europass.ewa.services.files.ImageProcessing;

@Ignore
@Deprecated
//EWA-651
public class AttachmentVisibilityTest {

	static Map<String, PrintingPreference> ecvDefaultPrefs;
	
	static AttachmentsVisibilityStep step;
	
	@BeforeClass
	public static void prepare(){
		
		Injector injector = Guice.createInjector( 
				new AbstractModule() {
					@Override
					protected void configure() {
						bindConstant().annotatedWith(Names.named(Namespace.SCHEMA_LOCATION_DEFAULT_PARAM))
						.to(Namespace.NAMESPACE + " http://europass.cedefop.europa.eu/xml/EuropassSchema_V3.0.xsd");
				
						bind(ImageProcessing.class).to(ConcreteImageProcessing.class);
						
						bind(AttachmentVisitor.class).annotatedWith(ODT.class).to(ODTAttachmentVisitor.class);
						bind(AttachmentVisitor.class).annotatedWith(WORD.class).to(WORDAttachmentVisitor.class);
						bind(AttachmentVisitor.class).annotatedWith(PDF.class).to(PDFAttachmentVisitor.class);
						
						binder().requestStaticInjection( AttachmentsVisibilityStep.class);
					}
				},
				new ModelModule()
				);
		ecvDefaultPrefs = 
				injector.getProvider( Key.get( new TypeLiteral<Map<String, PrintingPreference>>(){},
						Names.named( ModelModule.DEFAULT_CV_PREFS ) )).get();
		
		step = injector.getInstance( AttachmentsVisibilityStep.class );
	}
	
	@Test
	public void toODTCV() throws IOException{
		this.doTestEsp( EuropassDocumentType.ECV, ConversionFileType.OPEN_DOC, 600.0F, 770.0F );
	}
	@Test
	public void toWordCV() throws IOException{
		this.doTestEsp( EuropassDocumentType.ECV, ConversionFileType.WORD_DOC, 550.0F, 550.0F );
	}
	@Test
	public void toPdfCV() throws IOException{
		this.doTestEsp( EuropassDocumentType.ECV, ConversionFileType.PDF, 550.0F, 550.0F );
	}
	
	@Test
	public void toODTCVEsp() throws IOException{
		this.doTestEsp( EuropassDocumentType.ECV_ESP, ConversionFileType.OPEN_DOC, 600.0F, 770.0F );
	}
	@Test
	public void toWordCVEsp() throws IOException{
		this.doTestEsp( EuropassDocumentType.ECV_ESP, ConversionFileType.WORD_DOC, 550.0F, 650.0F );
	}
	@Test
	public void toPdfCVEsp() throws IOException{
		this.doTestEsp( EuropassDocumentType.ECV_ESP, ConversionFileType.PDF, 550.0F, 550.0F );
	}
	
	@Test
	public void toODTEsp() throws IOException{
		this.doTestEsp( EuropassDocumentType.ESP, ConversionFileType.OPEN_DOC, 600.0F, 770.0F );
	}
	@Test
	public void toWordEsp() throws IOException{
		this.doTestEsp( EuropassDocumentType.ESP, ConversionFileType.WORD_DOC, 550.0F, 650.0F );
	}
	@Test
	public void toPdfEsp() throws IOException{
		this.doTestEsp( EuropassDocumentType.ESP, ConversionFileType.PDF, 550.0F, 550.0F );
	}
	
	public void doTestEsp( EuropassDocumentType docType, ConversionFileType fileType, float firstPageDefaultHeight, float nextPageDefaultHeight ) throws IOException{
		
		ExportableModel model = new ExportableModelWrapper();
		
		model.setModel(  ModelMocks.jpgAndpdfAttachments( ecvDefaultPrefs ) );
		
		model.setDocumentType( docType );
		model.setFileType( fileType );
		
		//Set proper attachments
		ClassLoader cl = getClass().getClassLoader();
		InputStream jpg1IS = cl.getResourceAsStream( "attachments/small.jpg" );
		model.getModel().getAttachmentList().get( 0 ).setData( IOUtils.toByteArray( jpg1IS ) );
		InputStream jpg2IS = cl.getResourceAsStream( "attachments/medium.jpg" );
		model.getModel().getAttachmentList().get( 1 ).setData( IOUtils.toByteArray( jpg2IS ) );
		InputStream pdf1IS = cl.getResourceAsStream( "attachments/withIText.pdf" );
		model.getModel().getAttachmentList().get( 2 ).setData( IOUtils.toByteArray( pdf1IS ) );
		InputStream pdf2IS = cl.getResourceAsStream( "attachments/withPDFBox.pdf" );
		model.getModel().getAttachmentList().get( 3 ).setData( IOUtils.toByteArray( pdf2IS ) );
		InputStream pdf3IS = cl.getResourceAsStream( "attachments/withJPedal.pdf" );
		model.getModel().getAttachmentList().get( 4 ).setData( IOUtils.toByteArray( pdf3IS ) );
		
		step.doStep( model );
		
		SkillsPassport esp = model.getModel();
		
		boolean isESP = EuropassDocumentType.ESP.equals( docType );
		boolean isCV = EuropassDocumentType.ECV.equals( docType );
		boolean isPDF = ConversionFileType.PDF.equals( fileType );
		
		Collection<PrintingPreference> email =
				Collections2.filter(esp.getDocumentPrintingPrefs().get( "ECV" ), 
						Predicates.containsPattern("LearnerInfo\\.Identification\\.ContactInfo\\.Email"));
		Assert.assertThat("Printing Preference of Personal Email is hidden", 
				Iterables.get( email, 0 ).getShow(), 
				CoreMatchers.is( !isESP ));
		
		Collection<PrintingPreference> annexes =
				Collections2.filter(esp.getDocumentPrintingPrefs().get( "ECV" ), 
						Predicates.containsPattern("LearnerInfo\\.ReferenceTo"));
		Assert.assertThat("Printing Preference of Annexes is visible", 
				Iterables.get( annexes, 0 ).getShow(), 
				CoreMatchers.is( !isCV ));
		
		AttachmentsInfo info = esp.getAttachmentsInfo();
		
		Assert.assertThat( "Visible Attachments in Document", 
				info.isShowable(), 
				CoreMatchers.is( !isCV ) );
		Assert.assertThat( "Include Attachments inline", 
				info.isIncludeInline(), 
				CoreMatchers.is( !isPDF && !isCV ) );
		
		if ( !isCV ){
			List<Attachment> included = info.getVisibleAttachments();
			
			Assert.assertThat( "JPG and PDF attachments are included", 
					included.size(), 
					CoreMatchers.is( 5 ) );
			
			if ( !isPDF ){
				Attachment jpg1 = included.get( 0 );
				Assert.assertThat( "JPG 1 width is not resized", 
						jpg1.getByteMetadaList().get( 0 ).getWidth(), 
						CoreMatchers.is( Float.valueOf( "450.0F" ) ) );
				Assert.assertThat( "JPG 1 height is not resized", 
						jpg1.getByteMetadaList().get( 0 ).getHeight(), 
						CoreMatchers.is( Float.valueOf( "450.0F" ) ) );
				Assert.assertNotNull("JPG 1 has data",
						jpg1.getByteMetadaList().get( 0 ).getData());
				
				Attachment jpg2 = included.get( 1 );
				Assert.assertThat( "JPG 2 width is resized to default width 522", 
						jpg2.getByteMetadaList().get( 0 ).getWidth(), 
						CoreMatchers.is( Float.valueOf( "522.0F" ) ) );
				Assert.assertThat( "JPG 2 height is resized accordingly", 
						jpg2.getByteMetadaList().get( 0 ).getHeight() < 950.0F, 
						CoreMatchers.is( true ) );
				Assert.assertNotNull("JPG 2 has data",
						jpg2.getByteMetadaList().get( 0 ).getData());
				
				Attachment pdf1 = included.get( 2 );
				Assert.assertThat( "PDF-1 manageable with iText", 
						pdf1.getPdfLibrary(), 
						CoreMatchers.is( PDFLibrary.iText ) );
				Assert.assertNotNull("PDF-1 has data",
						pdf1.getByteMetadaList().get( 0 ).getData());
				Assert.assertThat( "PDF-1 height is resized to default height " + firstPageDefaultHeight, 
						pdf1.getByteMetadaList().get( 0 ).getHeight() , 
						CoreMatchers.is( firstPageDefaultHeight) );
				Assert.assertThat( "PDF-1 width is resized accordingly", 
						pdf1.getByteMetadaList().get( 0 ).getWidth() < 794.0F, 
						CoreMatchers.is( true ) );
				
				Attachment pdf2 = included.get( 3 );
				Assert.assertThat( "PDF-2 manageable with PDFBox", 
						pdf2.getPdfLibrary(), 
						CoreMatchers.is( PDFLibrary.PDFBox ) );
				Assert.assertThat("PDF-2 has 2 pages",
						pdf2.getByteMetadaList().size(),
						CoreMatchers.is( 2 ) );
				Assert.assertNotNull("PDF-2 1st Page: has data",
						pdf2.getByteMetadaList().get( 0 ).getData());
				Assert.assertThat( "PDF-2 1st Page is resized to firstPage default height " + firstPageDefaultHeight, 
						pdf2.getByteMetadaList().get( 0 ).getHeight() , 
						CoreMatchers.is( firstPageDefaultHeight) );
				Assert.assertThat( "PDF-2 1st Page width is resized accordingly", 
						pdf2.getByteMetadaList().get( 0 ).getWidth() < 794.0F, 
						CoreMatchers.is( true ) );
				Assert.assertNotNull("PDF-2 2nd Page: has data",
						pdf2.getByteMetadaList().get( 1 ).getData());
				Assert.assertThat( "PDF-2 2nd Page width is resized to fit the width ", 
						pdf2.getByteMetadaList().get( 1 ).getWidth() <= Attachment.ATT_WIDTH_PIXELS , 
						CoreMatchers.is( true ) );
				Assert.assertThat( "PDF-2 2nd Page height is resized to fit the height ", 
						pdf2.getByteMetadaList().get( 1 ).getHeight() <= nextPageDefaultHeight, 
						CoreMatchers.is( true ) );
				
				Attachment pdf3 = included.get( 4 );
				Assert.assertThat( "PDF-3 manageable with JPedal", 
						pdf3.getPdfLibrary(), 
						CoreMatchers.is( PDFLibrary.JPedal ) );
				Assert.assertThat("PDF-3 has 1 page",
						pdf3.getByteMetadaList().size(),
						CoreMatchers.is( 1 ) );
				Assert.assertNotNull("PDF-3 Page: has data",
						pdf3.getByteMetadaList().get( 0 ).getData());
				Assert.assertThat( "PDF-3 Page is resized to firstPage default height " + firstPageDefaultHeight, 
						pdf2.getByteMetadaList().get( 0 ).getHeight() , 
						CoreMatchers.is( firstPageDefaultHeight) );
				Assert.assertThat( "PDF-3 1st Page width is resized accordingly", 
						pdf3.getByteMetadaList().get( 0 ).getWidth() < 794.0F, 
						CoreMatchers.is( true ) );
			} else {
				Attachment jpg1 = included.get( 0 );
				Assert.assertNull( "JPG 1 has no byte metadata", 
						jpg1.getByteMetadaList());
				
				Attachment jpg2 = included.get( 1 );
				Assert.assertNull( "JPG 2 has no byte metadata", 
						jpg2.getByteMetadaList());
				
				Attachment pdf1 = included.get( 2 );
				Assert.assertThat( "PDF-1 manageable with iText", 
						pdf1.getPdfLibrary(), 
						CoreMatchers.is( PDFLibrary.iText ) );
				
				Attachment pdf2 = included.get( 3 );
				Assert.assertThat( "PDF-2 manageable with PDFBox", 
						pdf2.getPdfLibrary(), 
						CoreMatchers.is( PDFLibrary.PDFBox ) );
				
				Attachment pdf3 = included.get( 4 );
				Assert.assertThat( "PDF-3 manageable with JPedal", 
						pdf3.getPdfLibrary(), 
						CoreMatchers.is( PDFLibrary.JPedal ) );
			}
		}
	}
}
