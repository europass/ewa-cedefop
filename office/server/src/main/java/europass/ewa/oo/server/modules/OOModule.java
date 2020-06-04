package europass.ewa.oo.server.modules;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider;
import com.fasterxml.jackson.jaxrs.xml.JacksonXMLProvider;
import com.google.inject.Singleton;
import com.sun.jersey.guice.JerseyServletModule;
import com.sun.jersey.guice.spi.container.servlet.GuiceContainer;

import europass.ewa.oo.server.resources.OOResource;

public class OOModule extends JerseyServletModule{

	@SuppressWarnings("unused")
	private static final Logger LOG = LoggerFactory.getLogger(OOModule.class);
	
	@Override
	protected void configureServlets() {
		
		bind(GuiceContainer.class).in(Singleton.class);
		
		//--- Message Body Reader and Writer Providers ---
		bind(JacksonJsonProvider.class).in(Singleton.class);
		bind(JacksonXMLProvider.class).in(Singleton.class);
		
		bind(OOResource.class);
		
		serve("/office/*").with(GuiceContainer.class);
	}
}
