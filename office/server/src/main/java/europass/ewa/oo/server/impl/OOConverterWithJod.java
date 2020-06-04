package europass.ewa.oo.server.impl;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import europass.ewa.oo.server.OOStartup;
import java.io.File;
import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import org.artofsolving.jodconverter.OfficeDocumentConverter;
import org.artofsolving.jodconverter.document.DocumentFamily;
import org.artofsolving.jodconverter.document.DocumentFormat;
import org.artofsolving.jodconverter.office.DefaultOfficeManagerConfiguration;
import org.artofsolving.jodconverter.office.OfficeManager;

@Singleton
public class OOConverterWithJod {

	private static final Logger LOG = LoggerFactory.getLogger(OOConverterWithJod.class);

	private final OfficeManager officeManager;

	@Inject
	public OOConverterWithJod(@Named("europass-ewa-services.oo.server.binary") String ooBinaryPath, @Named("europass-ewa-services.oo.server.ports") String ooPorts) {
		LOG.info("initializing OOConverterWithJod");
		final String[] stringPorts = ooPorts.split(",");
		final int[] ports = new int[stringPorts.length];
		for (int i = 0; i < stringPorts.length; i++) {
			ports[i] = Integer.parseInt(stringPorts[i]);
		}
		LOG.info("killing possibly existing soffice instances");
		killAllExistingOfficeInstances();
		LOG.info("done killing possibly existing soffice instances");
		officeManager = new DefaultOfficeManagerConfiguration().
				setPortNumbers(ports).
				setOfficeHome(ooBinaryPath).
				setTaskExecutionTimeout(30000L). // java.util.concurrent.TimeoutException
				buildOfficeManager();
		officeManager.start();
	}
	
	/**
	 * We need to kill the possibly existing soffice instances before attaching to new ones.
	 * Because JOD can't attach to existing soffice instances.
	 * They may exist because tomcat was not shutdown, but killed, thus did not have a chance to cleanup at {@link OOStartup#contextDestroyed(javax.servlet.ServletContextEvent) }
	 * This means that this application should only be deployed once per server (physical or VM) and that on that server soffice is not used in parallel for something else.
	 */
	private void killAllExistingOfficeInstances() {
		Runtime runtime = Runtime.getRuntime();
		// http://stackoverflow.com/questions/6356340/killing-a-process-using-java
		try {
			if (System.getProperty("os.name").toLowerCase().contains("windows")) {
				Process process = runtime.exec("taskkill /F /IM soffice.bin");
				process.waitFor(); // http://stackoverflow.com/a/12668943/72478
			} else {
				Process process = runtime.exec("pkill soffice");
				process.waitFor(); // http://stackoverflow.com/a/12668943/72478
			}
		} catch (IOException e) {
			LOG.error("office kill error", e);
		} catch (InterruptedException e) {
			LOG.error("office kill error", e);
		}
	}
	
	public void shutdown() {
		LOG.info("shutdown OOConverterWithJod, stopping office pool");
		officeManager.stop();
	}

	public File convert(File srcFile, String prefix, String extension, String outDir, String requestId) throws Exception {
		LOG.info("converting (new) " + srcFile + " " + prefix + " " + extension + " " + outDir + " " + requestId);
		final long time = System.currentTimeMillis();

		final OfficeDocumentConverter converter = new OfficeDocumentConverter(officeManager);
		DocumentFormat format = converter.getFormatRegistry().getFormatByExtension(extension);
		Map storeProperties = new HashMap(format.getStoreProperties(DocumentFamily.TEXT)); // this returns an immutable {FilterName: writer_pdf_Export} so we clone
		Map exportProperties = new HashMap();
		storeProperties.put("FilterData", exportProperties);
		exportProperties.put("ExportFormFields", Boolean.FALSE); // FRAWEB-166
		format.setStoreProperties(DocumentFamily.TEXT, storeProperties);

		converter.convert(srcFile, new File(outDir, requestId + "." + extension));

		LOG.info("converted " + requestId + " in " + (System.currentTimeMillis() - time) + "ms");

		return new File(outDir, requestId + "." + extension);
	}
}
