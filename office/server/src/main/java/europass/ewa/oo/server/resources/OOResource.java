package europass.ewa.oo.server.resources;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import com.sun.jersey.core.header.FormDataContentDisposition;
import com.sun.jersey.multipart.FormDataBodyPart;
import com.sun.jersey.multipart.FormDataParam;

import europass.ewa.oo.server.impl.OOConverterWithJod;


// The module is / , and guice sais office : so we are file /office/...
@Path("/")
public class OOResource {

	private static final Logger LOG  = LoggerFactory.getLogger(OOResource.class);
	
	private final String ooPath;
	private final OOConverterWithJod ooConverter;
	
	@Inject
	public OOResource(@Named("europass-ewa-services.oo.repository") String ooPath, OOConverterWithJod ooConverter) {
		this.ooPath = ooPath;
		this.ooConverter = ooConverter;
	}
	
	@GET
	@Path("/status")
	public Response status() {
		// simple, not 100% safe, solution:
		// since this.ooConverter is a dependency, and since OOConverterWithJod initializes the pool on startup, hitting /status should return OK
		return Response.ok("OK", "text/html").build();
	}
	
	@POST
	@Path("/convert/{type}/")
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	public Response convert(@FormDataParam("file") InputStream in,
			@FormDataParam("file") FormDataContentDisposition disposition,
			@FormDataParam("file") FormDataBodyPart bp, 
			@PathParam("type")String type,
			@Context HttpServletRequest request) {
		
		String requestId = request.getHeader("X-Request-ID");
		FileOutputStream out = null;
		File fout=null;
		try {
			LOG.debug("tmp office path is " + ooPath);
			File ooDir = new File(ooPath);
			fout = File.createTempFile("ootemp-", ".odt", ooDir);
			LOG.debug("created temp file " + fout.getName());
			out = new FileOutputStream(fout);
			IOUtils.copy(in, out);
		} catch (IOException ex) {
			log("!!!IO EXCEPTION", requestId, ex, false);
			return Response.status(Status.BAD_REQUEST).entity(ex.getMessage()).build();
		} catch (Exception all_ex) {
			log("!!!EXCEPTION", requestId, all_ex, false);
			return Response.status(Status.INTERNAL_SERVER_ERROR).entity(all_ex.getMessage()).build();
	    } finally {
			if ( out != null ) { try { out.close(); } catch (Exception in_ex) {} }
		}
		
		String mime="";
		if ( type.equalsIgnoreCase("pdf") ) {
			mime="application/pdf";
		} else if ( type.equalsIgnoreCase("doc") ) {
			mime="application/msword";
		} else {
			return Response.status(Status.NOT_ACCEPTABLE).entity("Not acceptable mimeType: " + mime).build();
		}

		File converted=null;
		FileInputStream convS=null;
		try {
			converted = ooConverter.convert(fout, "oocnvrt", type, ooPath, requestId);
			convS=new FileInputStream(converted);
			byte[] data = new byte[(int)(converted.length())];
			
			IOUtils.read(convS, data);
			return Response.ok(data, mime).build();
		} catch (Exception e) {
			log("Error converting", requestId, e, false);
			return Response.status(Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
		} finally {
			// Cleanup
			if ( fout != null ) { try { fout.delete(); } catch (Exception in_ex) {} }
			if ( convS != null ) { try { convS.close(); } catch (Exception in_ex) {} }
			if ( converted != null ) { try { converted.delete(); } catch (Exception in_ex) {} }
			
		}
	}
	
	private void log( String message, String requestId, Exception e, boolean debug){
		String msg = String.format("\"Message\":\"%s\",\"RequestId\":\"%s\"", message, requestId);
		if (debug == true) 
			LOG.debug( msg, e);
		else
			LOG.error( msg, e);
	}
}
