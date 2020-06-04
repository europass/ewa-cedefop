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
import javax.servlet.http.HttpServletResponse;

@Singleton
public class LinkedInCallbackFilter implements Filter {

    private final String socialCallbackParam;
    private String[] socialCallbacks = null;

    @Inject
    public LinkedInCallbackFilter(@Named("ewa.editors.social.callback.linkedin") String socialCallbackParam) {
        this.socialCallbackParam = socialCallbackParam;
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        if (socialCallbackParam != null && !"".equals(socialCallbackParam.trim())) {
            socialCallbacks = socialCallbackParam.split(",");
        }
    }

    @Override
    public void destroy() {
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse,
            FilterChain chain) throws IOException, ServletException {

        final HttpServletRequest request = (HttpServletRequest) servletRequest;
        final HttpServletResponse response = (HttpServletResponse) servletResponse;

        boolean found = false;
        HttpServletRequest req = ((HttpServletRequest) request);
        String page = req.getServletPath();
        if (page == null) {
            page = "";
        }
        if (socialCallbacks != null && socialCallbacks.length > 0) {
            for (int i = 0; i < socialCallbacks.length; i++) {
                if (page.equals(socialCallbacks[i].trim())) {
                    found = true;
                }
            }
        }
        if (!found) {
            chain.doFilter(request, response);
        } else {

            response.setCharacterEncoding("UTF-8");
            response.setContentType("text/html");
            request.getRequestDispatcher("/WEB-INF/pages/socialRedirect.jsp").forward(request, response);
            return;
        }
    }
}
