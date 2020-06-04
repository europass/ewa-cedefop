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

import java.io.IOException;
import java.io.InputStream;

import org.apache.pdfbox.io.IOUtils;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;

import europass.ewa.enums.PDFLibrary;
import europass.ewa.services.exception.FileManagePermissionException;
import europass.ewa.services.files.PDFUtils;

public class DecidePdfLibraryTest {
	
	@Test
	public void sample() throws IOException{
		byte[] data = prepare( "pdf/sample.pdf" );
		Assert.assertNotNull( data );
		
		PDFLibrary library = PDFUtils.decideLibrary( data , "true");
		
		Assert.assertThat("Sample uses iText: ", library, CoreMatchers.is( PDFLibrary.iText ));
	}
	
	@Test(expected=FileManagePermissionException.class)
	public void locked() throws IOException{
		byte[] data = prepare( "pdf/locked.pdf" );
		Assert.assertNotNull( data );
		
		PDFUtils.decideLibrary( data , "true");
	}
	
	@Test(expected=FileManagePermissionException.class)
	public void password() throws IOException{
		byte[] data = prepare( "pdf/password.pdf" );
		Assert.assertNotNull( data );
		
		PDFUtils.decideLibrary( data , "true" );
	}
	
	@Test
	public void form() throws IOException{
		byte[] data = prepare( "pdf/form.pdf" );
		Assert.assertNotNull( data );
		
		PDFLibrary library = PDFUtils.decideLibrary( data, "true" );
		
		Assert.assertThat("form uses PDFBox: ", library, CoreMatchers.is( PDFLibrary.PDFBox ));
	}
	
	@Test
	public void extractionNotAllowed() throws IOException{
		byte[] data = prepare( "pdf/extraction-not-allowed.pdf" );
		Assert.assertNotNull( data );
		
		PDFLibrary library = PDFUtils.decideLibrary( data , "true");
		
		Assert.assertThat("Extraction Not Allowed uses JPedal: ", library, CoreMatchers.is( PDFLibrary.JPedal ));
	}

    @Test
    public void extractionNotAllowedWhenJPedalDisabled() throws IOException{
        byte[] data = prepare( "pdf/extraction-not-allowed.pdf" );
        Assert.assertNotNull( data );

        PDFLibrary library = PDFUtils.decideLibrary( data , "false");

        Assert.assertThat("Extraction Not Allowed uses JPedal: ", library, CoreMatchers.is( PDFLibrary.PDFBox ));
    }
	
	@Test
	public void extractionNotAllowedPages0Bg() throws IOException{
		byte[] data = prepare( "pdf/extraction-not-allowed-bg.pdf" );
		Assert.assertNotNull( data );
		
		PDFLibrary library = PDFUtils.decideLibrary( data , "true");
		
		Assert.assertThat("Extraction Not Allowed uses JPedal: ", library, CoreMatchers.is( PDFLibrary.JPedal ));
	}

    @Test
    public void extractionNotAllowedPages0BgJPedalDisabled() throws IOException{
        byte[] data = prepare( "pdf/extraction-not-allowed-bg.pdf" );
        Assert.assertNotNull( data );

        PDFLibrary library = PDFUtils.decideLibrary( data , "false");

        Assert.assertThat("Extraction Not Allowed uses JPedal: ", library, CoreMatchers.is( PDFLibrary.PDFBox ));
    }
	
	@Test
	public void extractionNotAllowedPages0Font() throws IOException{
		byte[] data = prepare( "pdf/extraction-not-allowed-font.pdf" );
		Assert.assertNotNull( data );
		
		PDFLibrary library = PDFUtils.decideLibrary( data , "true");
		
		Assert.assertThat("Extraction Not Allowed uses JPedal: ", library, CoreMatchers.is( PDFLibrary.JPedal ));
	}

    @Test
    public void extractionNotAllowedPages0FontJPedalDisabled() throws IOException{
        byte[] data = prepare( "pdf/extraction-not-allowed-font.pdf" );
        Assert.assertNotNull( data );

        PDFLibrary library = PDFUtils.decideLibrary( data , "false");

        Assert.assertThat("Extraction Not Allowed uses JPedal: ", library, CoreMatchers.is( PDFLibrary.PDFBox ));
    }

	private byte[] prepare( String name ) throws IOException{
		ClassLoader cl = currentThread().getContextClassLoader();
		InputStream in = cl.getResourceAsStream( name );
		
		return IOUtils.toByteArray( in );
	}
}

