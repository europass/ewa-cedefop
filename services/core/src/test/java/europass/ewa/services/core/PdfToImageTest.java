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

import java.awt.Image;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.*;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.List;
import javax.imageio.ImageIO;
import europass.ewa.model.ByteMetadata;
import org.apache.commons.io.FileUtils;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.ghost4j.document.DocumentException;
import org.ghost4j.document.PDFDocument;
import org.ghost4j.renderer.RendererException;
import org.ghost4j.renderer.SimpleRenderer;
import org.hamcrest.CoreMatchers;
import org.icepdf.core.exceptions.PDFSecurityException;
import org.icepdf.core.pobjects.Document;
import org.icepdf.core.pobjects.Page;
import org.icepdf.core.util.GraphicsRenderingHints;
import org.jpedal.PdfDecoder;
import org.jpedal.exception.PdfException;
import org.jpedal.fonts.FontMappings;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.parser.ImageRenderInfo;
import com.itextpdf.text.pdf.parser.PdfImageObject;
import com.itextpdf.text.pdf.parser.PdfReaderContentParser;
import com.itextpdf.text.pdf.parser.RenderListener;
import com.itextpdf.text.pdf.parser.TextRenderInfo;
import com.qoppa.pdf.PDFException;
import com.qoppa.pdfImages.PDFImages;
import com.sun.pdfview.PDFFile;
import com.sun.pdfview.PDFPage;
import com.sun.pdfview.PDFParseException;

import europass.ewa.Utils;

public class PdfToImageTest {
	File pdfSample;
	File pdfLocked;
	File pdfForm;
	File pdfNoExtr;
	File pdfNoExtrBg;
	File pdfNoExtrFont;
	File pdfPassword;
	File pdfJBIG2;
	File pdfJVMCrash;

	URL pdfSampleUrl;
	URL pdfLockedUrl;
	URL pdfFormUrl;
	URL pdfNoExtrUrl;
	URL pdfNoExtrBgUrl;
	URL pdfNoExtrFontUrl;
	URL pdfPasswordUrl;
	URL pdfJBIG2Url;
	URL pdfJVMCrashUrl;

	static final String SEPARATOR = System.getProperty( "file.separator" );

	@Before
	public void readPdf() {
		ClassLoader cl = currentThread().getContextClassLoader();

		pdfSampleUrl = cl.getResource( "pdf/sample.pdf" );
		pdfSample = new File( pdfSampleUrl.getFile() );
		Assert.assertNotNull( pdfSample );

		pdfLockedUrl = cl.getResource( "pdf/locked.pdf" );
		pdfLocked = new File( pdfLockedUrl.getFile() );
		Assert.assertNotNull( pdfLocked );

		pdfFormUrl = cl.getResource( "pdf/form.pdf" );
		pdfForm = new File( pdfFormUrl.getFile() );
		Assert.assertNotNull( pdfForm );

		pdfNoExtrUrl = cl.getResource( "pdf/extraction-not-allowed.pdf" );
		pdfNoExtr = new File( pdfNoExtrUrl.getFile() );
		Assert.assertNotNull( pdfNoExtr );

		pdfNoExtrBgUrl = cl.getResource( "pdf/extraction-not-allowed-bg.pdf" );
		pdfNoExtrBg = new File( pdfNoExtrBgUrl.getFile() );
		Assert.assertNotNull( pdfNoExtrBg );

		pdfNoExtrFontUrl = cl.getResource( "pdf/extraction-not-allowed-font.pdf" );
		pdfNoExtrFont = new File( pdfNoExtrFontUrl.getFile() );
		Assert.assertNotNull( pdfNoExtrFont );

		pdfPasswordUrl = cl.getResource( "pdf/password.pdf" );
		pdfPassword = new File( pdfPasswordUrl.getFile() );
		Assert.assertNotNull( pdfPassword );

		pdfJBIG2Url = cl.getResource( "pdf/weird.pdf" );
		pdfJBIG2 = new File( pdfJBIG2Url.getFile() );
		Assert.assertNotNull( pdfJBIG2 );
		
		pdfJVMCrashUrl = cl.getResource( "pdf/jvmcrash.pdf" );
		pdfJVMCrash = new File( pdfJVMCrashUrl.getFile() );
		Assert.assertNotNull( pdfJVMCrash );
	}

	@Ignore
	@Test ( expected = NoClassDefFoundError.class)
	public void ghost4j() throws FileNotFoundException, IOException, RendererException, DocumentException {
		PDFDocument document = new PDFDocument();
		document.load( pdfNoExtr );

		SimpleRenderer renderer = new SimpleRenderer();
		// set resolution (in DPI)
		renderer.setResolution( 300 );

		List<Image> images = renderer.render( document );
		for ( int i = 0; i < images.size(); i++ ) {
			Image page = images.get( i );

			Assert.assertNotNull( page );

			// ImageIO.write((RenderedImage) images.get(i), "png", new File((i +
			// 1) + ".png"));
		}
	}

	// =========================================================================================
	@Ignore
	@Test
	public void jPDFImages() throws PDFException, IOException, URISyntaxException {
		testjPDFImages( "pdf/extraction-not-allowed.pdf", "no-page-extraction" );
	}

	@Ignore
	@Test ( expected = PDFException.class)
	public void jPDFImagesLocked() throws PDFException, IOException, URISyntaxException {
		testjPDFImages( "pdf/locked.pdf", "locked-" );
	}

	@Ignore
	@Test
	public void jPDFImagesForm() throws PDFException, IOException, URISyntaxException {
		testjPDFImages( "pdf/form.pdf", "form-" );
	}

	private void testjPDFImages( String pdfFilePath, String prefix ) throws PDFException, IOException, URISyntaxException {
		ClassLoader cl = currentThread().getContextClassLoader();
		PDFImages pdfInfo = new PDFImages( cl.getResourceAsStream( pdfFilePath ), null );
		URL pdfUrl = getClass().getResource( pdfFilePath );

		Assert.assertNotNull( pdfInfo );

		int pages = pdfInfo.getPageCount();
		Assert.assertThat( "Number of Pages:", pages > 0, CoreMatchers.is( true ) );

		for ( int i = 0; i < pages; i++ ) {
			// int pageIndex, String fileName, int dpi
			File imageFile = new File( newFilePath( pdfUrl, prefix + "_page" + i + "_jPDFImage.png" ) );
			FileOutputStream fout = new FileOutputStream( imageFile );

			pdfInfo.savePageAsPNG( 0, fout, 300 );

			assertImageFile( imageFile );
		}

	}

	// =========================================================================================
	@Ignore
	@Test
	public void pdfRenderer() throws IOException, URISyntaxException {
		testPdfRenderer( pdfNoExtr, pdfNoExtrUrl, "no-page-extraction" );
	}

	@Ignore
	@Test ( expected = PDFParseException.class)
	public void pdfRendererLocked() throws IOException, URISyntaxException {
		testPdfRenderer( pdfLocked, pdfLockedUrl, "locked-" );
	}

	@Ignore
	@Test
	public void pdfRendererForm() throws IOException, URISyntaxException {
		testPdfRenderer( pdfForm, pdfFormUrl, "form-" );
	}

	private static void testPdfRenderer( File pdf, URL pdfUrl, String prefix ) throws IOException, URISyntaxException {

		RandomAccessFile raf = new RandomAccessFile( pdf, "r" );
		FileChannel fc = raf.getChannel();
		ByteBuffer buf = fc.map( FileChannel.MapMode.READ_ONLY, 0, fc.size() );
		PDFFile pdfFile = new PDFFile( buf );

		raf.close();

		Assert.assertThat( "Allows Printing: ", pdfFile.isPrintable(), CoreMatchers.is( true ) );

		int pages = pdfFile.getNumPages();
		Assert.assertThat( "Number of Pages: ", pages > 0, CoreMatchers.is( true ) );

		for ( int i = 0; i < pages; i++ ) {

			PDFPage page = pdfFile.getPage( i );
			// get the width and height for the doc at the default zoom
			Rectangle rect = new Rectangle( 0, 0, (int) page.getBBox().getWidth(), (int) page.getBBox().getHeight() );
			// generate the image
			Image image = page.getImage( rect.width, rect.height, // width &
																	// height
					rect, // clip rect
					null, // null for the ImageObserver
					true, // fill background with white
					true // block until drawing is done
					);

			BufferedImage bi = (BufferedImage) image;
			File imageFile = new File( newFilePath( pdfUrl, prefix + "_page" + i + "_pdfRenderer.png" ) );
			ImageIO.write( bi, "png", imageFile );

			assertImageFile( imageFile );
		}

	}

	// =========================================================================================
	@Ignore
	@Test
	public void icepdf() throws org.icepdf.core.exceptions.PDFException, PDFSecurityException, IOException, URISyntaxException {
		testIcepdf( pdfNoExtr, pdfNoExtrUrl, "no-page-extraction" );
	}

	@Ignore
	@Test ( expected = PDFSecurityException.class)
	public void icepdfLocked() throws org.icepdf.core.exceptions.PDFException, PDFSecurityException, IOException, URISyntaxException {
		testIcepdf( pdfLocked, pdfLockedUrl, "locked-" );
	}

	@Ignore
	@Test
	public void icepdfForm() throws org.icepdf.core.exceptions.PDFException, PDFSecurityException, IOException, URISyntaxException {
		testIcepdf( pdfForm, pdfFormUrl, "form-" );
	}

	private static void testIcepdf( File pdf, URL pdfUrl, String prefix ) throws org.icepdf.core.exceptions.PDFException,
			PDFSecurityException, IOException, URISyntaxException {
		Document document = new Document();

		document.setUrl( pdfUrl );

		// Paint each pages content to an image and
		// save page captures to file.
		float scale = 1.0f;
		float rotation = 0f;

		// For permissions play with the
		// bits...http://res.icesoft.org/docs/icepdf/latest/core/org/icepdf/core/pobjects/security/Permissions.html
		// document.getSecurityManager().getPermissions()
		int pages = document.getNumberOfPages();

		Assert.assertThat( "Number of Pages: ", pages > 0, CoreMatchers.is( true ) );

		for ( int i = 0; i < pages; i++ ) {
			// write the image to file
			BufferedImage image = (BufferedImage) document.getPageImage( i, GraphicsRenderingHints.PRINT, Page.BOUNDARY_CROPBOX, rotation,
					scale );

			RenderedImage rendImage = image;

			File imageFile = new File( newFilePath( pdfUrl, prefix + "_page" + i + "_ICEpdf.png" ) );
			ImageIO.write( rendImage, "png", imageFile );

			image.flush();

			assertImageFile( imageFile );
		}
		// clean up resources
		document.dispose();
	}

	// =========================================================================================
	@Ignore
	@Test ( expected = NullPointerException.class)
	public void iText() throws URISyntaxException, IOException {
		String imageFilePath = newFilePath( pdfNoExtrUrl, "iText.png" );

		PdfReader reader = new PdfReader( pdfNoExtrUrl );
		PdfReaderContentParser parser = new PdfReaderContentParser( reader );
		MyImageRenderListener listener = new MyImageRenderListener( imageFilePath );

		int pages = reader.getNumberOfPages();

		Assert.assertThat( "Number of Pages: ", pages, CoreMatchers.is( 1 ) );
		// fails with NullPointerException when trying to get page 0
		parser.processContent( 0, listener );

		reader.close();

		File imageFile = new File( imageFilePath );

		assertImageFile( imageFile );
	}

	// =========================================================================================
	// PDF BOX
	// =========================================================================================
	@Ignore
	@Test
	public void pdfBoxSample() throws IOException, URISyntaxException {
		// works ok, permissions=0
		this.testPdfBox( pdfSample, pdfSampleUrl, "sample" );
	}

	@Ignore
	public void pdfBox() throws IOException, URISyntaxException {
		// permissions=-1852 (4:false, 5:false, 10:false, 11:false) (3,12 are
		// true)
		// pages:1
		this.testPdfBox( pdfNoExtr, pdfNoExtrUrl, "no-page-extraction" );
		// Caused by: java.util.zip.DataFormatException: incorrect header check
		// PDFToImage utility requires password to be given in order to properly
		// load the pdf
		// It will work with the sample pdf
	}

	@Ignore
	@Test
	public void pdfBoxForm() throws IOException, URISyntaxException {
		// works ok, permissions=0
		this.testPdfBox( pdfForm, pdfFormUrl, "form" );
	}

	@Ignore
	@Test ( expected = java.lang.NoClassDefFoundError.class)
	public void pdfBoxLocked() throws IOException, URISyntaxException {
		// Fails on constructing the Reader
		// java.lang.ClassNotFoundException:
		// org.bouncycastle.cms.CMSEnvelopedData
		// no info for permissions or pages
		this.testPdfBox( pdfLocked, pdfLockedUrl, "locked" );
	}

	@Test
	public void pdfBoxNoExtr() throws IOException, URISyntaxException {
		// permissions=-1852 (4:false, 5:false, 10:false, 11:false) (3,12 are
		// true)
		this.testPdfBox( pdfNoExtr, pdfNoExtrUrl, "extraction-not-allowed" );
		// Caused by: java.util.zip.DataFormatException: incorrect header check
	}

	@Ignore
	@Test
	public void pdfBoxNoExtrBg() throws IOException, URISyntaxException {
		// permissions=-1292 (4:false, 5:true, 10:true, 11:false) (3,12 are
		// true)
		// pages=0
		this.testPdfBox( pdfNoExtrBg, pdfNoExtrBgUrl, "extraction-not-allowed-BG-problem" );
	}

	@Ignore
	@Test
	public void pdfBoxNoExtrFont() throws IOException, URISyntaxException {
		// permissions=-1084 (4:false, 5:false, 10:true, 11:false) (3,12 are
		// true)
		// pages=0
		this.testPdfBox( pdfNoExtrFont, pdfNoExtrFontUrl, "extraction-not-allowed-Font-problem" );
	}

	@Ignore
	@Test ( expected = com.itextpdf.text.exceptions.BadPasswordException.class)
	public void pdfBoxPassword() throws IOException, URISyntaxException {
		// com.itextpdf.text.exceptions.BadPasswordException: Bad user password
		this.testPdfBox( pdfPassword, pdfPasswordUrl, "password-protected" );
	}
	
	@Ignore
	@Test
	public void pdfJVMCrash() throws IOException, URISyntaxException {
		this.testPdfBox( pdfJVMCrash, pdfJVMCrashUrl, "jvm-crash" );
	}

	@Ignore
	@Test
	public void pdfBoxJBIG2() throws IOException, URISyntaxException {
		// permissions=0
		// pages=4
		this.testPdfBox( pdfJBIG2, pdfJBIG2Url, "jbig2" );
	}

	public void testPdfBox( File pdf, URL pdfUrl, String prefix ) throws IOException, URISyntaxException {
		FileInputStream fin = new FileInputStream( pdf );
		PdfReader reader = new PdfReader( fin );
		int permissions = (int) reader.getPermissions();

		System.out.println( "File: " + pdfUrl );
		System.out.println( "Permissions: " + permissions );
		System.out.println( "== Bit 3 == " + "\n(Revision 2) Print the document. "
				+ "\n(Revision 3) Print the document (possibly not at the highest qualitylevel, "
				+ "depending on whether bit 12 is also set).\n:" + Utils.isBitOn( permissions, 2 ) );
		System.out.println( "== Bit 4 == " + "\nModify the contents of the document by operations "
				+ "other than those controlled by bits 6, 9, and 11.\n:" + Utils.isBitOn( permissions, 3 ) );
		System.out.println( "== Bit 5 == " + "\n(Revision 2) Copy or otherwise extract text and graphics from the document, "
				+ "including extracting text and graphics " + "(in support of accessibility to disabled users or for other purposes). "
				+ "\n(Revision 3) Copy or otherwise extract text and graphics "
				+ "from the document by operations other than that controlled by bit 10.\n:" + Utils.isBitOn( permissions, 4 ) );
		System.out.println( "== Bit 6 == " + "\nAdd or modify text annotations, fill in interactive form fields, "
				+ "and, if bit 4 is also set, " + "create or modify interactive form fields (includingsignature fields).\n:"
				+ Utils.isBitOn( permissions, 5 ) );
		System.out.println( "== Bit 9 == " + "\n(Revision 3 only) Fill in existing interactive form fields (including signature fields), "
				+ "even if bit 6 is clear.\n:" + Utils.isBitOn( permissions, 8 ) );
		System.out.println( "== Bit 10 == " + "\n(Revision 3 only) Extract text and graphics "
				+ "(in support of accessibility to disabled users or for other purposes).\n:" + Utils.isBitOn( permissions, 9 ) );
		System.out.println( "== Bit 11 == " + "\n(Revision 3 only) Assemble the document "
				+ "(insert, rotate, or delete pages and create bookmarks or thumbnail images), " + "even if bit 4 is clear.\n:"
				+ Utils.isBitOn( permissions, 10 ) );
		System.out
				.println( "== Bit 12 == "
						+ "\n(Revision 3 only) Print the document to a representation from which a faithful digital copy of the PDF content could be generated. "
						+ "\nWhen this bit is clear (and bit 3 is set), printing is limited to a lowlevel representation of the appearance, "
						+ "\npossibly of degraded quality. (See implementation note 16 in Appendix H.).\n:"
						+ Utils.isBitOn( permissions, 11 ) );

		final PDDocument document = PDDocument.load(pdf);
		final PDFRenderer pdfRenderer = new PDFRenderer(document);
		
		int pages = document.getNumberOfPages();
		for (int i = 1; i <= pages ; i++) {
			final String filePath = prefix+"_pdfBox"+"_page";

			final BufferedImage bi = pdfRenderer.renderImage(i-1);
			final ByteArrayOutputStream baos = new ByteArrayOutputStream();
			ImageIO.write(bi, "png", baos);
			baos.flush();

			final ByteMetadata byteMeta = new ByteMetadata();
			byteMeta.setData(baos.toByteArray());
			byteMeta.setWidth(bi.getWidth());
			byteMeta.setHeight(bi.getHeight());

			final File imageFile = new File (filePath+(i)+".png");
			FileUtils.writeByteArrayToFile(imageFile, byteMeta.getData());

			assertImageFile( imageFile, true );
		}

		document.close();
		fin.close();
		// Caused by: java.util.zip.DataFormatException: incorrect header check
		// PDFToImage utility requires password to be given in order to properly
		// load the pdf
		// It will work with the sample pdf
	}

	// =========================================================================================
	// JPEDAL
	// =========================================================================================
	@Test
	public void jPedalSample() throws PdfException, URISyntaxException, IOException {
		testJPedal( pdfSample, pdfSampleUrl, "sample" );
	}

	/*
	 * This would correctly fail because the number of pages returned is 0, when
	 * the document is locked
	 */
	@Test ( expected = AssertionError.class)
	public void jPedalLocked() throws PdfException, URISyntaxException, IOException {
		testJPedal( pdfLocked, pdfLockedUrl, "locked-" );
	}

	@Test
	public void jPedalForm() throws PdfException, URISyntaxException, IOException {
		testJPedal( pdfForm, pdfFormUrl, "form-" );
	}

	@Test
	public void jPedalNoExtraction() throws PdfException, URISyntaxException, IOException {
		testJPedal( pdfNoExtr, pdfNoExtrUrl, "extraction-not-allowed" );
	}

	@Test
	public void jPedalNoExtractionBg() throws PdfException, URISyntaxException, IOException {
		testJPedal( pdfNoExtrBg, pdfNoExtrBgUrl, "extraction-not-allowed-BG-problem-" );
	}

	@Test
	public void jPedalNoExtractionFont() throws PdfException, URISyntaxException, IOException {
		testJPedal( pdfNoExtrFont, pdfNoExtrFontUrl, "extraction-not-allowed-Font-problem-" );
	}

	@Test ( expected = AssertionError.class)
	public void jPedalPassword() throws PdfException, URISyntaxException, IOException {
		// IsEncrypted and Is NOT Viewable
		testJPedal( pdfPassword, pdfPasswordUrl, "password-protected" );
	}

	@Test ( expected = java.lang.RuntimeException.class)
	public void jPedalJBIG2() throws PdfException, URISyntaxException, IOException {
		// IsEncrypted and Is NOT Viewable
		testJPedal( pdfJBIG2, pdfJBIG2Url, "jbig2" );
	}

	private static void testJPedal( File pdf, URL pdfUrl, String prefix ) throws PdfException, URISyntaxException, IOException {
		/** instance of PdfDecoder to convert PDF into image */
		PdfDecoder decode_pdf = new PdfDecoder( true );

		/** set mappings for non-embedded fonts to use */
		FontMappings.setFontReplacements();

		/** open the PDF file - can also be a URL or a byte array */
		decode_pdf.openPdfFileFromURL( pdfUrl.toURI().toString(), false );

		decode_pdf.setExtractionMode( 0, 1f ); // do not save images

		System.out.println( "File: " + pdfUrl );
		System.out.println( "isDisplayable: " + decode_pdf.isDisplayable() );
		System.out.println( "isEncrypted: " + decode_pdf.isEncrypted() );
		System.out.println( "isExtractionAllowed: " + decode_pdf.isExtractionAllowed() );
		System.out.println( "isFileViewable: " + decode_pdf.isFileViewable() );
		System.out.println( "isForm: " + decode_pdf.isForm() );

		int pages = decode_pdf.getPageCount();

		Assert.assertThat( "Number of Pages: ", pages > 0, CoreMatchers.is( true ) );

		/** get page 1 as an image */
		// page range if you want to extract all pages with a loop
		// int start = 1, end = decode_pdf.getPageCount();
		for ( int i = 1; i <= pages; i++ ) {
			BufferedImage rendImage = decode_pdf.getPageAsImage( i );

			Assert.assertNotNull( "Rendered image of " + i + " is not null:", rendImage );

			File imageFile = new File( newFilePath( pdfUrl, prefix + "_page" + (i - 1) + "_jPedal.png" ) );
			ImageIO.write( rendImage, "png", imageFile );

			assertImageFile( imageFile );
		}
		/** close the pdf file */
		decode_pdf.closePdfFile();
	}

	// ---------------------------------------------------------------------------------------------------
	private static void assertImageFile( File imageFile ) {
		assertImageFile( imageFile, true );
	}

	private static void assertImageFile( File imageFile, boolean delete ) {
		Assert.assertThat( "Image file exists: ", imageFile.exists(), CoreMatchers.is( true ) );
		Assert.assertThat( "Image file and is non empty: ", imageFile.length() > 0, CoreMatchers.is( true ) );

		if ( delete ) {
			FileUtils.deleteQuietly( imageFile );
			Assert.assertThat( "Image file is deleted: ", imageFile.exists(), CoreMatchers.is( false ) );
		}

	}

	private static String newFilePath( URL url, String name ) throws URISyntaxException {
		return name;
	}

	@SuppressWarnings ( "unused")
	private static String relativeFilePath( URL url, String name ) throws URISyntaxException {
		String fullPath = url.getPath();
		String parent = fullPath.indexOf( SEPARATOR ) >= 0 ? fullPath.substring( 0, fullPath.lastIndexOf( SEPARATOR ) ) : fullPath;
		return parent + SEPARATOR + name;
	}

	static class MyImageRenderListener implements RenderListener {
		/** The new document to which we've added a border rectangle. */
		protected String path = "";

		/**
		 * Creates a RenderListener that will look for images.
		 */
		public MyImageRenderListener(String path) {
			this.path = path;
		}

		/**
		 * @see com.itextpdf.text.pdf.parser.RenderListener#beginTextBlock()
		 */
		public void beginTextBlock() {
		}

		/**
		 * @see com.itextpdf.text.pdf.parser.RenderListener#endTextBlock()
		 */
		public void endTextBlock() {
		}

		/**
		 * @see com.itextpdf.text.pdf.parser.RenderListener#renderImage(com.itextpdf.text.pdf.parser.ImageRenderInfo)
		 */
		public void renderImage( ImageRenderInfo renderInfo ) {
			try {
				String filename;
				FileOutputStream os;
				PdfImageObject image = renderInfo.getImage();
				if ( image == null ) return;
				filename = String.format( path, renderInfo.getRef().getNumber(), image.getFileType() );
				os = new FileOutputStream( filename );
				os.write( image.getImageAsBytes() );
				os.flush();
				os.close();
			} catch ( IOException e ) {
				System.out.println( e.getMessage() );
			}
		}

		/**
		 * @see com.itextpdf.text.pdf.parser.RenderListener#renderText(com.itextpdf.text.pdf.parser.TextRenderInfo)
		 */
		public void renderText( TextRenderInfo renderInfo ) {
		}
	}
}
