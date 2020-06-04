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
package europass.ewa.services.filters;

import java.io.IOException;
import java.util.UUID;

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
public class RefererFilter implements Filter {

    private final String allowRef;

    @Inject
    public RefererFilter(@Named("europass-ewa-services.referers") String allowRef) {
        this.allowRef = allowRef;
    }

    private String[] referers = null;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        if (allowRef != null && !"".equals(allowRef.trim())) {
            referers = allowRef.split(",");
        }
    }

    @Override
    public void destroy() {
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {

        boolean found = false;
        HttpServletRequest req = ((HttpServletRequest) request);
        String referer = req.getHeader("referer");

        if (referer == null) {
            // With no referer it is not allowed
            referer = "";
        }

        /**
         * EWA 1520: Log Traceability : Implementation of a Unique ID scheme for
         * cross module error traceability Ref:
         * https://devcenter.heroku.com/articles/http-request-id
		 *
         */
        req.setAttribute("X-Request-ID", UUID.randomUUID());

        if (referers != null && referers.length > 0) {
            for (int i = 0; i < referers.length; i++) {
                if (referer.startsWith(referers[i])) {
                    found = true;
                }
            }
        } else {
            found = true;
        }
        if (found) {
            chain.doFilter(request, response);
        } else {

            HttpServletResponse resp = (HttpServletResponse) response;
            resp.reset();
            resp.setStatus(HttpServletResponse.SC_FORBIDDEN);
            return;
        }

    }
}
