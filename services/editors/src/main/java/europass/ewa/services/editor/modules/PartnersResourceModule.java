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
package europass.ewa.services.editor.modules;

import com.google.inject.servlet.ServletModule;
import europass.ewa.services.editor.resources.publish.CvLibraryProviderResource;
import europass.ewa.services.editor.resources.publish.EuresProviderResource;
import europass.ewa.services.editor.resources.publish.AnpalProviderResource;
import europass.ewa.services.editor.resources.publish.IndeedProviderResource;
import europass.ewa.services.editor.resources.publish.MonsterProviderResource;
import europass.ewa.services.editor.resources.publish.ProxyDownloadXMLPartnersResource;
import europass.ewa.services.editor.resources.publish.XingProviderResource;

/**
 * Created by jos on 7/19/2017.
 */
public class PartnersResourceModule extends ServletModule {

    @Override
    protected void configureServlets() {

        bind(EuresProviderResource.class);
        bind(MonsterProviderResource.class);
        bind(XingProviderResource.class);
        bind(CvLibraryProviderResource.class);
        bind(AnpalProviderResource.class);
        bind(IndeedProviderResource.class);
        bind(ProxyDownloadXMLPartnersResource.class);
    }
}
