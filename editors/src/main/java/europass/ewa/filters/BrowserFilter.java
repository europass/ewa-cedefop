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
import java.util.EnumSet;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.google.inject.name.Named;

import europass.ewa.enums.UserAgent;
import europass.ewa.module.EditorsModule;

/**
 * Servlet Filter implementation class BrowserFilter
 */
@Singleton
public class BrowserFilter implements Filter {

    private static final int OPERA_VERSION_NUMBER = 8;
    private final EnumSet<UserAgent> unsupportedAgents;

    /**
     * Default constructor.
     */
    @Inject
    public BrowserFilter(@Named(EditorsModule.EWA_UNSUPPORTED_UAS) EnumSet<UserAgent> unsupportedAgents) {
        this.unsupportedAgents = unsupportedAgents;
    }

    /**
     * @see Filter#destroy()
     */
    public void destroy() {
    }

    /**
     * @see Filter#doFilter(ServletRequest, ServletResponse, FilterChain)
     */
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        final HttpServletRequest req = (HttpServletRequest) request;
        final HttpServletResponse res = (HttpServletResponse) response;

        HttpSession session = req.getSession();

        String operaVersion = "null";
        String thisAgent = req.getHeader("User-Agent");

        //getVersion for Opera
        if (thisAgent != null && !"".equals(thisAgent) && thisAgent.indexOf("Opera/") > -1) {
            operaVersion = thisAgent.substring(thisAgent.indexOf("Version/") + OPERA_VERSION_NUMBER, thisAgent.length());
            thisAgent = "Opera/" + operaVersion;
        }

        UserAgent agent = UserAgent.match(thisAgent);

        if (agent != null && unsupportedAgents.size() > 0 && unsupportedAgents.contains(agent)) {
            res.setCharacterEncoding("UTF-8");
            res.setContentType("text/html");
            req.getRequestDispatcher("/WEB-INF/pages/unsupportedBrowser.jsp").forward(req, res);
            return;
        }

        // Set browser family if available
        session.setAttribute(EditorsModule.EWA_LOCALE_BROWSER_VARIABLE, agent.getDescription());

        // pass the request along the filter chain
        chain.doFilter(request, response);
    }

    /**
     * @see Filter#init(FilterConfig)
     */
    public void init(FilterConfig fConfig) throws ServletException {
    }

}
