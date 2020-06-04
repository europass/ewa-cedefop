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
package europass.ewa.page;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import europass.ewa.enums.UserAgent;

public class PageKey {

    private String channel;

    private Locale locale;

    private String path;

    private Map<String, String[]> parameters;

    private UserAgent agent = UserAgent.UNKNOWN;
    private String browserName = UserAgent.UNKNOWN.getDescription();

    //* Constructors **/
    public PageKey() {
    }

    public PageKey(String locale, String path) {
        this.locale = new Locale(locale);
        this.path = path;
    }

    public PageKey(Locale locale, String path) {
        this.locale = locale;
        this.path = path;
    }

    //* Getters, Setters **/
    public Locale getLocale() {
        return locale;
    }

    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }

    public void setLocale(Locale locale) {
        this.locale = locale;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    /**
     * The <b>parameters</b> component may hold request parameters (a.k.a.
     * request query) that are part of the URI of the page corresponding to this
     * key.
     *
     * @return A map of request parameters keyed by parameter names.
     */
    public Map<String, String[]> getParameters() {
        if (parameters == null) {
            parameters = new HashMap<String, String[]>();
        }
        return parameters;
    }

    public void setParameters(Map<String, String[]> parameters) {
        this.parameters = parameters;
    }

    public UserAgent getUserAgent() {
        return agent;
    }

    public void setUserAgent(UserAgent agent) {
        this.agent = agent != null ? agent : UserAgent.UNKNOWN;
    }

    public void setBrowser(String browser) {
        this.browserName = browser != null ? browser : UserAgent.UNKNOWN.getDescription();
    }

    public String getBrowser() {
        return browserName;
    }
}
