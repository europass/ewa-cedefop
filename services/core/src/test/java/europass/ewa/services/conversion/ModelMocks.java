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

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

import europass.ewa.enums.EuropassDocumentType;
import europass.ewa.model.Attachment;
import europass.ewa.model.ContactInfo;
import europass.ewa.model.ContactMethod;
import europass.ewa.model.DocumentInfo;
import europass.ewa.model.Identification;
import europass.ewa.model.LearnerInfo;
import europass.ewa.model.PersonName;
import europass.ewa.model.PrintingPreference;
import europass.ewa.model.ReferenceTo;
import europass.ewa.model.SkillsPassport;

public class ModelMocks {

	static SkillsPassport jpgAndpdfAttachments( Map<String, PrintingPreference> prefs ) {
		
		SkillsPassport esp = new SkillsPassport();
		
		List<Attachment> attachments = new ArrayList<Attachment>();
		
		try {
			Attachment jpg1 = new Attachment();
			jpg1.setId( "ATT_JPG_1" );
			jpg1.setName( "diploma.jpg" );
			jpg1.setDescription( "Diploma" );
			jpg1.setMimeType( "image/jpeg" );
			jpg1.setData( "Diploma".getBytes( "UTF-8" ) );
			
			Attachment jpg2 = new Attachment();
			jpg2.setId( "ATT_JPG_2" );
			jpg2.setName( "licence.jpg" );
			jpg2.setDescription( "Licence" );
			jpg2.setMimeType( "image/jpeg" );
			jpg2.setData( "Licence".getBytes( "UTF-8" ) );
			
			Attachment pdf1 = new Attachment();
			pdf1.setId( "ATT_PDF_1" );
			pdf1.setName( "certificate.pdf" );
			pdf1.setDescription( "Certificate" );
			pdf1.setMimeType( "application/pdf" );
			pdf1.setData( "Certificate".getBytes( "UTF-8" ) );
			
			Attachment pdf2 = new Attachment();
			pdf2.setId( "ATT_PDF_2" );
			pdf2.setName( "thesis.pdf" );
			pdf2.setDescription( "Thesis" );
			pdf2.setMimeType( "application/pdf" );
			pdf2.setData( "Thesis".getBytes( "UTF-8" ) );
			
			Attachment pdf3 = new Attachment();
			pdf3.setId( "ATT_PDF_3" );
			pdf3.setName( "citation.pdf" );
			pdf3.setDescription( "Citation" );
			pdf3.setMimeType( "application/pdf" );
			pdf3.setData( "Citation".getBytes( "UTF-8" ) );
			
			attachments.add( jpg1 );
			attachments.add( jpg2 );
			attachments.add( pdf1 );
			attachments.add( pdf2 );
			attachments.add( pdf3 );
		} catch( final UnsupportedEncodingException e ){}
		
		esp.setAttachmentList( attachments );
		
		LearnerInfo learner = new LearnerInfo();
		List<ReferenceTo> annexes = new ArrayList<>();
		annexes.add( new ReferenceTo( "ATT_JPG_1" ) );
		annexes.add( new ReferenceTo( "ATT_JPG_2" ) );
		annexes.add( new ReferenceTo( "ATT_PDF_1" ) );
		annexes.add( new ReferenceTo( "ATT_PDF_2" ) );
		annexes.add( new ReferenceTo( "ATT_PDF_3" ) );
		learner.setDocumentation( annexes );
		
		ContactMethod email = new ContactMethod("mail@provider.com");
		ContactInfo contactinfo = new ContactInfo();
		contactinfo.setEmail( email );
		Identification identification = new Identification();
		identification.setContactInfo( contactinfo );
		learner.setIdentification( identification );
		
		
		esp.setLearnerInfo( learner );
		
		esp.activatePreferences( "ECV", prefs == null ? Collections.<String, PrintingPreference>emptyMap() : prefs );
		esp.applyDefaultPreferences( "ECV" );
		
		return esp;
	}
	
	static SkillsPassport noDocumentInfo( Map<String, PrintingPreference> prefs ) {
		
		SkillsPassport esp = sampleSkillsPassport(prefs);
		
		return esp;
	}

	static SkillsPassport withCreationDate( Map<String, PrintingPreference> prefs ) {
		
		SkillsPassport esp = sampleSkillsPassport(prefs);

		DocumentInfo info = new DocumentInfo();
		info.setDocumentType(EuropassDocumentType.ECV);
		
		info.setCreationDate(new DateTime(2014, 2, 28, 12, 35, DateTimeZone.UTC));
		
		esp.setDocumentInfo(info);
		
		return esp;
	}

	static SkillsPassport noCreationDateWithLastUpdateDate( Map<String, PrintingPreference> prefs) {

		SkillsPassport esp = sampleSkillsPassport(prefs);
		
		DocumentInfo info = new DocumentInfo();
		info.setDocumentType(EuropassDocumentType.ECV);
		
		info.setLastUpdateDate(new DateTime(2014, 2, 28, 12, 35, DateTimeZone.UTC));
		
		esp.setDocumentInfo(info);
		
		return esp;
	}

	private static SkillsPassport sampleSkillsPassport( Map<String, PrintingPreference> prefs ){

		SkillsPassport esp = new SkillsPassport();
		
		LearnerInfo learner = new LearnerInfo();
		
		Identification identification = new Identification();
		identification.setPersonName(new PersonName("John", "Smith"));

		ContactInfo contactinfo = new ContactInfo();

		ContactMethod email = new ContactMethod("jsmith@mail.com");
		
		contactinfo.setEmail(email);
		identification.setContactInfo(contactinfo);
		learner.setIdentification(identification);
		
		esp.setLearnerInfo( learner );
		
		esp.activatePreferences( "ECV", prefs == null ? Collections.<String, PrintingPreference>emptyMap() : prefs );
		esp.applyDefaultPreferences( "ECV" );
		
		return esp;
	}

}
