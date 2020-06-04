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
package europass.ewa.oo.client.module;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Named;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Strings;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import com.sun.jersey.multipart.impl.MultiPartWriter;

import europass.ewa.oo.client.OfficeClient;
import europass.ewa.oo.client.OfficeClientImplementation;

public class OfficeClientModule extends AbstractModule {

    private static final Logger LOG = LoggerFactory.getLogger(OfficeClientModule.class);

    public static final String ACTIVE_OFFICE_CLIENT_SERVERS = "active.office.client.servers";
    public static final String OFFICE_REST_CLIENT = "office.rest.client";
    public static final String OPEN_OFFICE_CLIENT_SERVERS_PARAM = "europass-ewa-services.oo.client.servers";

    private List<URI> activeOOServers = null;

    private Client client = null;

    @Override
    protected void configure() {
        //--- OO Conversion client ---
        bind(OfficeClient.class).to(OfficeClientImplementation.class);
    }

    @Provides
    @Singleton
    @Named(ACTIVE_OFFICE_CLIENT_SERVERS)
    List<URI> openOfficeClientServers(@Named(OPEN_OFFICE_CLIENT_SERVERS_PARAM) String strList) throws IOException {
        if (activeOOServers == null) {
            activeOOServers = new ArrayList<URI>();

            if (!Strings.isNullOrEmpty(strList)) {

                String[] list = strList.split(",");
                for (int i = 0; i < list.length; i++) {
                    String uri = list[i];
                    if (Strings.isNullOrEmpty(uri)) {
                        continue;
                    }
                    try {
                        activeOOServers.add(new URI(uri));
                    } catch (URISyntaxException e) {
                        LOG.error(String.format("Failed to include office server with URI %s, because URI is not valid.", uri));
                    }
                }
            }
        }
        return activeOOServers;
    }

    @Provides
    @Singleton
    @Named(OFFICE_REST_CLIENT)
    Client officeRestClient() {
        if (client == null) {
            ClientConfig cfg = new DefaultClientConfig();
            cfg.getClasses().add(MultiPartWriter.class);
            client = Client.create(cfg);
        }
        return client;
    }
}
