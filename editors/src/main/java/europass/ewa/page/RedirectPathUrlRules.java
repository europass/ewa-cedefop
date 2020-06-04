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

import javax.inject.Singleton;
import javax.servlet.http.HttpServletRequest;
import java.util.*;

@Singleton
public class RedirectPathUrlRules {

    private final PageKeyFormat keyFormat;
    private final PageKey defaultPage;
    private String paramNoLocalePath;
    private String paramCustomPath;

    private final String CV_ESP_UPLOAD_PATH = "/cv-esp/upload";

    public RedirectPathUrlRules(final PageKeyFormat keyFormat, final PageKey defaultPage,
            final String paramNoLocalePath, final String paramCustomPath) {
        this.keyFormat = keyFormat;
        this.defaultPage = defaultPage;
        this.paramNoLocalePath = paramNoLocalePath;
        this.paramCustomPath = paramCustomPath;
    }

    protected Set getRulesForMissingLocales(final String propertyDiffUrls) {
        final Set missingLocaleUrls = new HashSet();

        if (checkEmptyOrNullParams(propertyDiffUrls)) {
            return missingLocaleUrls;
        }

        final String[] urls = propertyDiffUrls.split(",");
        for (String url : urls) {
            url = url.trim();
            missingLocaleUrls.add(url);
        }
        return missingLocaleUrls;
    }

    protected Map getRulesForReplacingWithCustomPaths(final String propertySameUrls) {
        final Map replacedpaths = new HashMap();
        if (checkEmptyOrNullParams(propertySameUrls)) {
            return replacedpaths;
        }
        final String[] urlCouples = propertySameUrls.split(",");
        for (String couple : urlCouples) {
            couple = couple.trim();
            final String[] urlParts = couple.split(" ");
            if (urlParts.length != 2) {
                return replacedpaths;
            }
            for (int i = 0; i < urlParts.length; i++) {
                replacedpaths.put(urlParts[0], urlParts[1]);
            }
        }
        return replacedpaths;
    }

    private boolean checkEmptyOrNullParams(final String property) {
        if (property == null || property.isEmpty()) {
            return true;
        }
        return false;
    }

    public String getRedirectUrlOnDefaultLocale(final HttpServletRequest request, final String originalUrl, final Locale locale) {

        final PageKey pageKey = keyFormat.parseSpecificRequest(request);
        final Set missingLocaleUrls = getRulesForMissingLocales(paramNoLocalePath);

        if (missingLocaleUrls.contains(originalUrl)) {
            if (CV_ESP_UPLOAD_PATH.equals(pageKey.getPath())) {
                pageKey.setPath(CV_ESP_UPLOAD_PATH.replace("cv-esp", "cv"));
            }
            return keyFormat.format(pageKey);
        }
        return keyFormat.format(new PageKey(locale, defaultPage.getPath()));
    }

    public boolean shouldRedirectPath(final PageKey pageKey) {

        final Map replacedPathUrls = getRulesForReplacingWithCustomPaths(paramCustomPath);

        return replacedPathUrls.containsKey(pageKey.getPath());
    }

    public String getRedirectUrlWhenCustomRuleApplied(final PageKey pageKey) {

        final Map replacedPathUrls = getRulesForReplacingWithCustomPaths(paramCustomPath);
        final String path = pageKey.getPath();
        final String redirectPath = (String) replacedPathUrls.get(path);
        pageKey.setPath(redirectPath);

        return keyFormat.format(pageKey);
    }
}
