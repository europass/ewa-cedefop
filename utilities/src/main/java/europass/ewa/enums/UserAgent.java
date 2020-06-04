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
package europass.ewa.enums;

import java.util.EnumSet;

public enum UserAgent {
    MSIE11("MSIE 11.0"),
    MSIE10("MSIE 10.0"),
    MSIE9("MSIE 9.0"),
    MSIE8("MSIE 8.0"),
    MSIE7("MSIE 7.0"),
    MSIE6("MSIE 6."),
    MSIE5("MSIE 5."),
    MSIE4("MSIE 4."),
    TRIDENT60("Trident/6.0"),
    TRIDENT50("Trident/5.0"),
    TRIDENT40("Trident/4.0"),
    CHROME10("Chrome/10."),
    CHROME9("Chrome/9."),
    CHROME8("Chrome/8."),
    CHROME7("Chrome/7."),
    CHROME6("Chrome/6."),
    CHROME5("Chrome/5."),
    CHROME4("Chrome/4."),
    CHROME3("Chrome/3."),
    CHROME2("Chrome/2."),
    CHROME1("Chrome/1."),
    CHROME0("Chrome/0."),
    CHROME("Chrome/"),
    FIREFOX10("Firefox/10."),
    FIREFOXX9("Firefox/9."),
    FIREFOX8("Firefox/8."),
    FIREFOX7("Firefox/7."),
    FIREFOX6("Firefox/6."),
    FIREFOX5("Firefox/5."),
    FIREFOX4("Firefox/4."),
    FIREFOX3_6("Firefox/3.6"),
    FIREFOX3_5("Firefox/3.5"),
    FIREFOX3("Firefox/3.0"),
    FIREFOX2("Firefox/2."),
    FIREFOX1("Firefox/1."),
    FIREFOX0("Firefox/0."),
    OPERA10("Opera/10."),
    OPERA9("Opera/9."),
    OPERA8("Opera/8."),
    OPERA7("Opera/7."),
    OPERA6("Opera/6."),
    OPERA5("Opera/5."),
    OPERA4("Opera/4."),
    OPERA3("Opera/3."),
    OPERA2("Opera/2."),
    OPERA1("Opera/1."),
    OPERA("OPR/"),
    SAFARI4("Safari/4"),
    SAFARI3("Safari/3"),
    SAFARI2("Safari/2"),
    SAFARI1("Safari/1"),
    SAFARI("Safari"),
    ANDROID_BROWSER("Android Browser"),
    IOS_CHROME("CriOS/"),
    IOS_OPERA("OPiOS/"),
    // used for detection of iOS Operating System type on smartphones & tablets devices
    IOS("iOS"),
    IPAD("iPad"),
    ANDROID("Android"),
    MODERN("MODERN"),
    UNKNOWN("UNKNOWN");

    private String description;

    UserAgent(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return description;
    }

    public String getDescription() {
        return description;
    }

    public static EnumSet<UserAgent> getSet() {
        return EnumSet.allOf(UserAgent.class);
    }

    public static UserAgent match(String str) {
        if (str == null) {
            return UNKNOWN;
        }
        for (UserAgent agent : values()) {

            // Android Browser
            if (str.indexOf("Mozilla/5.0") > -1 && str.indexOf("Android ") > -1 && str.matches("(.*)[A|a]pple[W|w]eb[K|k]it(.*)")) {
                if (str.indexOf("Chrome") == -1) {
                    return UserAgent.ANDROID_BROWSER;
                }
            }
            // IE 10
            if (str.matches("(.*)MSIE 10.0(.*)")) {
                return UserAgent.MSIE10;
            }
            // IE 11
            if (str.matches("(.*)Trident/7.0(.*)rv:11.0(.*)")) {
                return UserAgent.MSIE11;
            }

            if (str.indexOf("Safari") > -1) {

                // Opera on Android
                if (str.indexOf("OPiOS/") > -1) {
                    return UserAgent.IOS_OPERA;
                } else if (str.indexOf("CriOS/") > -1) {
                    return UserAgent.IOS_CHROME;
                } else if (str.indexOf("Chrome/") > -1) {
                    return UserAgent.CHROME;
                } else {
                    return UserAgent.SAFARI;
                }
            }
            if (str.indexOf(agent.description) > -1) {
                return agent;
            }
        }
        return MODERN;
    }

    public static UserAgent fromValue(String str) {
        if (str == null) {
            return UNKNOWN;
        }
        try {
            UserAgent ua = UserAgent.valueOf(str);
            if (ua != null) {
                return ua;
            }
        } catch (IllegalArgumentException iae) {
            return MODERN;
        }
        return MODERN;
    }

}
