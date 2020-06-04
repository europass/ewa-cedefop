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
package europass.ewa.services.enums;

public enum HttpErrorCodeLabels {
    HTTP400(400, "bad.request"),
    HTTP401(401, "unauthorized"),
    HTTP404(404, "not.found"),
    HTTP405(405, "method.not.allowed"),
    HTTP406(406, "not.acceptable"),
    HTTP415(415, "unsupported.content.type"),
    HTTP500(500, "internal.server.error");

    private int code;
    private String label;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    private HttpErrorCodeLabels(int code, String label) {
        this.code = code;
        this.label = label;
    }

    public static HttpErrorCodeLabels match(int code) {
        if (code == 0) {
            return HTTP500;
        }
        for (HttpErrorCodeLabels codeLabel : values()) {
            if (codeLabel.code == code) {
                return codeLabel;
            }
        }
        return HTTP500;
    }

    public static String getMatchingLabel(int code) {
        HttpErrorCodeLabels match = match(code);
        return match.getLabel();
    }
}
