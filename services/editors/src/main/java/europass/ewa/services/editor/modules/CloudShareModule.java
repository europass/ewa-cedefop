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

import europass.ewa.services.editor.resources.DropboxCloudShareResource;
import europass.ewa.services.editor.resources.GoogleDriveCloudShareResource;
import europass.ewa.services.editor.resources.OneDriveCloudShareResource;

public class CloudShareModule extends ServletModule {

    @Override
    protected void configureServlets() {

        //--- Cloud Storage Document
        bind(GoogleDriveCloudShareResource.class);
        bind(DropboxCloudShareResource.class);
        bind(OneDriveCloudShareResource.class);
    }
}
