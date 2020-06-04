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
package europass.ewa.services.editor;

import java.util.Enumeration;

import javax.inject.Inject;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import europass.ewa.services.editor.files.FileBean;
import europass.ewa.services.files.FileRepository;

public class ServicesSessionListener implements HttpSessionListener {

    private static final Logger LOG = LoggerFactory.getLogger(ServicesSessionListener.class);

    private final FileRepository uploadedFilesRepo;

    @Inject
    public ServicesSessionListener(FileRepository uploadedFilesRepo) {
        super();
        this.uploadedFilesRepo = uploadedFilesRepo;
    }

    @Override
    public void sessionCreated(HttpSessionEvent se) {
    }

    @Override
    public void sessionDestroyed(HttpSessionEvent se) {
        HttpSession session = se.getSession();

        Enumeration<String> e = session.getAttributeNames();
        if (e != null) {
            while (e.hasMoreElements()) {
                String name = e.nextElement();
                Object obj = session.getAttribute(name);
                if (obj instanceof FileBean) {
                    FileBean fb = (FileBean) obj;
                    String fileId = fb.getId();

                    try {
                        this.uploadedFilesRepo.delete(fileId);
                    } catch (Exception ex) {
                        LOG.error("ServicesSessionListener: Failed to delete file with id '" + fileId + "'", ex);
                    }
                    session.removeAttribute(fileId);
                }

            }
        }
    }

}
