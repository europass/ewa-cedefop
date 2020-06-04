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
package europass.ewa.filters;

import java.io.IOException;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

@Singleton
public class SkipFilesFilter implements Filter {

    private final String skipPagesParam;

    @Inject
    public SkipFilesFilter(@Named("ewa.editors.skip.filters") String skipPagesParam) {
        this.skipPagesParam = skipPagesParam;
    }

    private String[] skipPages = null;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        if (skipPagesParam != null && !"".equals(skipPagesParam.trim())) {
            skipPages = skipPagesParam.split(",");
        }
    }

    @Override
    public void destroy() {
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {

        boolean found = false;
        HttpServletRequest req = ((HttpServletRequest) request);
        String page = req.getServletPath();
        if (page == null) {
            page = "";
        }
        if (skipPages != null && skipPages.length > 0) {
            for (int i = 0; i < skipPages.length; i++) {
                if (page.startsWith(skipPages[i].trim())) {
                    found = true;
                }
            }
        }
        if (!found) {
            chain.doFilter(request, response);
        } else {
            request.getRequestDispatcher(req.getServletPath()).forward(request, response);
        }

    }
}
