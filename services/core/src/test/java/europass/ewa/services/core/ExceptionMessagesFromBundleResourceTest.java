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

import static org.junit.Assert.assertThat;

import java.io.IOException;

import org.hamcrest.CoreMatchers;
import org.junit.Test;

import europass.ewa.services.exception.ApiException;
import europass.ewa.services.exception.FileAccessForbiddenException;
import europass.ewa.services.exception.FileExceedsCummulativeLimitException;
import europass.ewa.services.exception.FileExceedsLimitException;
import europass.ewa.services.exception.FileManagePermissionException;
import europass.ewa.services.exception.FileNotFoundException;
import europass.ewa.services.exception.InputUndefinedException;
import europass.ewa.services.exception.PhotoCroppingException;
import europass.ewa.services.exception.PhotoReadingException;
import europass.ewa.services.exception.PhotoResizingException;
import europass.ewa.services.exception.ThumbSavingException;
import europass.ewa.services.exception.UndefinedMediaTypeException;
import europass.ewa.services.exception.XMLCompatibilityException;
import europass.ewa.services.exception.XMLUndefinedException;
import europass.ewa.services.exception.XMLVersionException;

public class ExceptionMessagesFromBundleResourceTest {

	
	@Test
	public void apiException() throws IOException {
		ApiException apiExc = new ApiException();
		assertThat(apiExc.getMessage(), CoreMatchers.is("EWA API Runtime Exception") );	
	}
	
	@Test
	public void apiExceptionThrow() throws IOException {
		ApiException apiExc = new ApiException(new Throwable("ss"));
		assertThat(apiExc.getMessage(), CoreMatchers.is("EWA API Runtime Exception") );	
	}
	
	@Test
	public void fileForbiddenException() throws IOException {
		FileAccessForbiddenException exc = new FileAccessForbiddenException();
		assertThat(exc.getMessage(), CoreMatchers.is("Access to the file is forbidden.") );			
	}
	
	@Test
	public void fileForbiddenExceptionThrow() throws IOException {
		FileAccessForbiddenException exc = new FileAccessForbiddenException(new Throwable("ss"));
		assertThat(exc.getMessage(), CoreMatchers.is("Access to the file is forbidden.") );			
	}
	
	@Test
	public void fileExceedsCummulativeLimitException() throws IOException {
		FileExceedsCummulativeLimitException exc = new FileExceedsCummulativeLimitException();
		assertThat(exc.getMessage(), CoreMatchers.is( String.format("By uploading this file, the maximum cummulative size limit of %d is exceeded", 0) ));	
		
	}
	
	@Test
	public void fileExceedsLimitException() throws IOException {
		int limit = 200;
		FileExceedsLimitException exc = new FileExceedsLimitException(limit);
		assertThat(exc.getMessage(), CoreMatchers.is( String.format("The file exceeds the allowed limit of %d.", limit) ));	
		
	}
	
	@Test
	public void fileManagePermissionException() throws IOException {
		FileManagePermissionException exc = new FileManagePermissionException();
		assertThat(exc.getMessage(), CoreMatchers.is("The file does not have the required permission to allow its integration" ));	
		
	}
	
	@Test
	public void fileNotFoundException() throws IOException {
		FileNotFoundException exc = new FileNotFoundException();
		assertThat(exc.getMessage(), CoreMatchers.is("The file cannot be found in the repository." ));	
	}
	
	@Test
	public void fileNotFoundExceptionThrow() throws IOException {
		FileNotFoundException exc = new FileNotFoundException(new Throwable("ss"));
		assertThat(exc.getMessage(), CoreMatchers.is("The file cannot be found in the repository." ));	
	}
	
	@Test
	public void inputUndefinedException() throws IOException {
		InputUndefinedException exc = new InputUndefinedException();
		assertThat(exc.getMessage(), CoreMatchers.is("The input is undefined or empty." ));	
		
	}
	
	@Test
	public void photoCroppingException() throws IOException {
		PhotoCroppingException exc = new PhotoCroppingException();
		assertThat(exc.getMessage(), CoreMatchers.is("The photo could not be cropped." ));	
		
	}
	
	@Test
	public void photoReadingException() throws IOException {
		PhotoReadingException exc = new PhotoReadingException();
		assertThat(exc.getMessage(), CoreMatchers.is("The photo bytes could not be read." ));		
	}
	
	@Test
	public void photoReadingExceptionThrow() throws IOException {
		PhotoReadingException exc = new PhotoReadingException(new Throwable("ss"));
		assertThat(exc.getMessage(), CoreMatchers.is("The photo bytes could not be read." ));		
	}
	
	@Test
	public void photoResizingException() throws IOException {
		PhotoResizingException exc = new PhotoResizingException();
		assertThat(exc.getMessage(), CoreMatchers.is("The photo could not be resized to comply with Europass ratio." ));	
	}
	
	@Test
	public void photoResizingExceptionThrow() throws IOException {
		PhotoResizingException exc = new PhotoResizingException(new Throwable("ss"));
		assertThat(exc.getMessage(), CoreMatchers.is("The photo could not be resized to comply with Europass ratio." ));	
	}
	
	@Test
	public void thumbSavingException() throws IOException {
		ThumbSavingException exc = new ThumbSavingException();
		assertThat(exc.getMessage(), CoreMatchers.is("Error creating thumbnail for PDF attachment" ));	
	}
	
	@Test
	public void thumbSavingExceptionThrow() throws IOException {
		ThumbSavingException exc = new ThumbSavingException(new Throwable("ss"));
		assertThat(exc.getMessage(), CoreMatchers.is("Error creating thumbnail for PDF attachment" ));	
	}
	
	@Test
	public void undefinedMediaTypeException() throws IOException {
		UndefinedMediaTypeException exc = new UndefinedMediaTypeException();
		assertThat(exc.getMessage(), CoreMatchers.is("The media type is not defined for this file." ));	
	}
	
	@Test
	public void xmlCompatibilityException() throws IOException {
		XMLCompatibilityException exc = new XMLCompatibilityException();
		assertThat(exc.getMessage(), CoreMatchers.is("Failed to apply transformation to XML." ));	
	}
	
	@Test
	public void xmlCompatibilityExceptionThrow() throws IOException {
		XMLCompatibilityException exc = new XMLCompatibilityException(new Throwable("ss"));
		assertThat(exc.getMessage(), CoreMatchers.is("Failed to apply transformation to XML." ));	
	}
	
	@Test
	public void xmlUndefinedException() throws IOException {
		XMLUndefinedException exc = new XMLUndefinedException();
		assertThat(exc.getMessage(), CoreMatchers.is("The provided XML is null or the empty string." ));	
	}
	
	@Test
	public void xmlVersionException() throws IOException {
		XMLVersionException exc = new XMLVersionException();
		assertThat(exc.getMessage(), CoreMatchers.is("Failed to retrieve the XSD version with which the XML is compatible." ));	
	}
	
	
	@Test
	public void xmlVersionExceptionThrow() throws IOException {
		XMLVersionException exc = new XMLVersionException(new Throwable("ss"));
		assertThat(exc.getMessage(), CoreMatchers.is("Failed to retrieve the XSD version with which the XML is compatible." ));	
	}
	
	
	
	
}
