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
package europass.ewa.servlet;

import java.util.Locale;

import javax.servlet.FilterConfig;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class ServletUtils {

    private ServletUtils() {
    }

    private static final Logger LOG = LoggerFactory.getLogger(ServletUtils.class);

    public static final String INCLUDE_REQUEST_URI_ATTR = "javax.servlet.include.request_uri";

    public static final String INCLUDE_PATH_INFO_ATTR = "javax.servlet.include.path_info";

    public static final String INCLUDE_SERVLET_PATH_ATTR = "javax.servlet.include.servlet_path";

    public static final String FORWARD_REQUEST_URI_ATTR = "javax.servlet.forward.request_uri";

    public static final String FORWARD_PATH_INFO_ATTR = "javax.servlet.forward.path_info";

    public static final String FORWARD_SERVLET_PATH_ATTR = "javax.servlet.forward.servlet_path";

    private static final String CTX_MSG_NOT_SET = "Context parameter {} not set. Using default {}.";

    private static final String CTX_MSG_SET = "Context parameter {} set to {}";

    private static final String CTX_MSG_PARSE_ERROR = "Failed to parse context parameter {}. Using default {}";

    private static final String CFG_MSG_NOT_SET = "ServletConfig parameter {} not set. Using default {}.";

    private static final String CFG_MSG_SET = "ServletConfig parameter {} set to {}";

    private static final String CFG_MSG_PARSE_ERROR = "Failed to parse ServletConfig parameter {}. Using default {}";

    private static final String FLT_MSG_NOT_SET = "FilterConfig parameter {} not set. Using default {}.";

    private static final String FLT_MSG_SET = "FilterConfig parameter {} set to {}";

    private static final String FLT_MSG_PARSE_ERROR = "Failed to parse FilterConfig parameter {}. Using default {}";

    private static final String REQ_MSG_NOT_SET = "Request parameter {} not set. Using default {}.";

    private static final String REQ_MSG_SET = "Request parameter {} set to {}";

    private static final String REQ_MSG_PARSE_ERROR = "Failed to parse Request parameter {}. Using default {}";

    public abstract static class ParametersUtil<T> {

        private final String MSG_NOT_SET;

        private final String MSG_SET;

        private final String MSG_PARSE_ERROR;

        public ParametersUtil(String MSG_NOT_SET, String MSG_SET, String MSG_PARSE_ERROR) {
            this.MSG_NOT_SET = MSG_NOT_SET;
            this.MSG_SET = MSG_SET;
            this.MSG_PARSE_ERROR = MSG_PARSE_ERROR;
        }

        public boolean getParameter(T t, String name, boolean defaultValue) {
            String str = getParameter(t, name);
            if (str == null || str.trim().equals("")) {
                LOG.debug(MSG_NOT_SET, name, defaultValue);
                return defaultValue;
            }
            str = str.trim();
            return str.equalsIgnoreCase("true") || str.equalsIgnoreCase("on") || str.equals("1");

        }

        public int getParameter(T parameters, String name, int defaultValue) {
            String str = getParameter(parameters, name);
            if (str == null || str.trim().equals("")) {
                LOG.debug(MSG_NOT_SET, name, defaultValue);
                return defaultValue;
            }

            try {
                int value = Integer.parseInt(str);
                LOG.debug(MSG_SET, name, value);
                return value;
            } catch (NumberFormatException nfe) {
                LOG.warn(MSG_PARSE_ERROR, new Object[]{name, defaultValue});
                LOG.warn("exception", nfe);
                return defaultValue;
            }
        }

        public long getParameter(T parameters, String name, long defaultValue) {
            String str = getParameter(parameters, name);
            if (str == null || str.trim().equals("")) {
                LOG.debug(MSG_NOT_SET, name, defaultValue);
                return defaultValue;
            }

            try {
                long value = Long.parseLong(str);
                LOG.debug(MSG_SET, name, value);
                return value;
            } catch (NumberFormatException nfe) {
                LOG.warn(MSG_PARSE_ERROR, new Object[]{name, defaultValue});
                LOG.warn("exception", nfe);
                return defaultValue;
            }
        }

        public float getParameter(T parameters, String name, float defaultValue) {
            String str = getParameter(parameters, name);
            if (str == null || str.trim().equals("")) {
                LOG.debug(MSG_NOT_SET, name, defaultValue);
                return defaultValue;
            }

            try {
                float value = Float.parseFloat(str);
                LOG.debug(MSG_SET, name, value);
                return value;
            } catch (NumberFormatException nfe) {
                LOG.warn(MSG_PARSE_ERROR, new Object[]{name, defaultValue});
                LOG.warn("exception", nfe);
                return defaultValue;
            }
        }

        public double getParameter(T parameters, String name, double defaultValue) {
            String str = getParameter(parameters, name);
            if (str == null || str.trim().equals("")) {
                LOG.debug(MSG_NOT_SET, name, defaultValue);
                return defaultValue;
            }

            try {
                double value = Double.parseDouble(str);
                LOG.debug(MSG_SET, name, value);
                return value;
            } catch (NumberFormatException nfe) {
                LOG.warn(MSG_PARSE_ERROR, new Object[]{name, defaultValue});
                LOG.warn("exception", nfe);
                return defaultValue;
            }
        }

        public String getParameter(T parameters, String name, String defaultValue) {
            String str = getParameter(parameters, name);
            if (str == null || str.equals("")) {
                LOG.debug(MSG_NOT_SET, name, defaultValue);
                return defaultValue;
            }

            return str;
        }

        public Locale getParameter(T parameters, String name, Locale defaultValue) {
            String str = getParameter(parameters, name);
            if (str == null) {
                LOG.debug(MSG_NOT_SET, name, defaultValue);
                return defaultValue;
            }

            String[] parts = str.split("_");
            switch (parts.length) {
                case 0:
                    return defaultValue;
                case 1:
                    return new Locale(parts[0]);
                case 2:
                    return new Locale(parts[0], parts[1]);
                default:
                    return new Locale(parts[0], parts[1], parts[2]);
            }
        }

        @SuppressWarnings("unchecked")
        public <E extends Enum<E>> E getParameter(T parameters, String name, E defaultValue) {
            String str = getParameter(parameters, name);
            if (str == null) {
                LOG.debug(MSG_NOT_SET, name, defaultValue);
                return defaultValue;
            }
            try {
                return (E) Enum.valueOf(defaultValue.getClass(), str);

            } catch (IllegalArgumentException iae) {
                LOG.warn(MSG_PARSE_ERROR, name, defaultValue);
                return defaultValue;
            }
        }

        public String[] getParameter(T parameters, String name, String[] defaultArray) {
            String str = getParameter(parameters, name);
            if (str == null || str.isEmpty()) {
                LOG.info(MSG_NOT_SET, name, defaultArray);
                return defaultArray;
            }
            String[] values = str.split(",");
            for (int i = 0; i < values.length; i++) {
                values[i] = values[i].trim();
            }
            return values;
        }

        abstract String getParameter(T parameters, String name);
    }

    public static final ParametersUtil<ServletContext> SERVLET_CONTEXT = new ParametersUtil<ServletContext>(CTX_MSG_NOT_SET, CTX_MSG_SET, CTX_MSG_PARSE_ERROR) {
        @Override
        String getParameter(ServletContext parameters, String name) {
            return parameters.getInitParameter(name);
        }
    };

    public static final ParametersUtil<ServletConfig> SERVLET_CONFIG = new ParametersUtil<ServletConfig>(CFG_MSG_NOT_SET, CFG_MSG_SET, CFG_MSG_PARSE_ERROR) {
        @Override
        String getParameter(ServletConfig parameters, String name) {
            return parameters.getInitParameter(name);
        }
    };

    public static final ParametersUtil<ServletRequest> SERVLET_REQUEST = new ParametersUtil<ServletRequest>(REQ_MSG_NOT_SET, REQ_MSG_SET, REQ_MSG_PARSE_ERROR) {
        @Override
        String getParameter(ServletRequest parameters, String name) {
            return parameters.getParameter(name);
        }
    };

    public static final ParametersUtil<FilterConfig> FILTER_CONFIG = new ParametersUtil<FilterConfig>(FLT_MSG_NOT_SET, FLT_MSG_SET, FLT_MSG_PARSE_ERROR) {
        @Override
        String getParameter(FilterConfig parameters, String name) {
            return parameters.getInitParameter(name);
        }
    };

    public static boolean getParameter(ServletContext ctx, String name, boolean defaultValue) {
        return SERVLET_CONTEXT.getParameter(ctx, name, defaultValue);
    }

    public static boolean getParameter(ServletConfig config, String name, boolean defaultValue) {
        return SERVLET_CONFIG.getParameter(config, name, defaultValue);
    }

    public static boolean getParameter(FilterConfig config, String name, boolean defaultValue) {
        return FILTER_CONFIG.getParameter(config, name, defaultValue);
    }

    public static boolean getParameter(ServletRequest request, String name, boolean defaultValue) {
        return SERVLET_REQUEST.getParameter(request, name, defaultValue);
    }

    public static int getParameter(ServletContext ctx, String name, int defaultValue) {
        return SERVLET_CONTEXT.getParameter(ctx, name, defaultValue);
    }

    public static int getParameter(ServletConfig config, String name, int defaultValue) {
        return SERVLET_CONFIG.getParameter(config, name, defaultValue);
    }

    public static int getParameter(FilterConfig config, String name, int defaultValue) {
        return FILTER_CONFIG.getParameter(config, name, defaultValue);
    }

    public static int getParameter(ServletRequest request, String name, int defaultValue) {
        return SERVLET_REQUEST.getParameter(request, name, defaultValue);
    }

    public static long getParameter(ServletContext ctx, String name, long defaultValue) {
        return SERVLET_CONTEXT.getParameter(ctx, name, defaultValue);
    }

    public static long getParameter(ServletConfig config, String name, long defaultValue) {
        return SERVLET_CONFIG.getParameter(config, name, defaultValue);
    }

    public static long getParameter(FilterConfig config, String name, long defaultValue) {
        return FILTER_CONFIG.getParameter(config, name, defaultValue);
    }

    public static long getParameter(ServletRequest request, String name, long defaultValue) {
        return SERVLET_REQUEST.getParameter(request, name, defaultValue);
    }

    public static float getParameter(ServletContext ctx, String name, float defaultValue) {
        return SERVLET_CONTEXT.getParameter(ctx, name, defaultValue);
    }

    public static float getParameter(ServletConfig config, String name, float defaultValue) {
        return SERVLET_CONFIG.getParameter(config, name, defaultValue);
    }

    public static float getParameter(FilterConfig config, String name, float defaultValue) {
        return FILTER_CONFIG.getParameter(config, name, defaultValue);
    }

    public static float getParameter(ServletRequest request, String name, float defaultValue) {
        return SERVLET_REQUEST.getParameter(request, name, defaultValue);
    }

    public static double getParameter(ServletContext ctx, String name, double defaultValue) {
        return SERVLET_CONTEXT.getParameter(ctx, name, defaultValue);
    }

    public static double getParameter(ServletConfig config, String name, double defaultValue) {
        return SERVLET_CONFIG.getParameter(config, name, defaultValue);
    }

    public static double getParameter(FilterConfig config, String name, double defaultValue) {
        return FILTER_CONFIG.getParameter(config, name, defaultValue);
    }

    public static double getParameter(ServletRequest request, String name, double defaultValue) {
        return SERVLET_REQUEST.getParameter(request, name, defaultValue);
    }

    public static String getParameter(ServletContext ctx, String name, String defaultValue) {
        return SERVLET_CONTEXT.getParameter(ctx, name, defaultValue);
    }

    public static String getParameter(ServletConfig config, String name, String defaultValue) {
        return SERVLET_CONFIG.getParameter(config, name, defaultValue);
    }

    public static String getParameter(FilterConfig config, String name, String defaultValue) {
        return FILTER_CONFIG.getParameter(config, name, defaultValue);
    }

    public static String getParameter(ServletRequest request, String name, String defaultValue) {
        return SERVLET_REQUEST.getParameter(request, name, defaultValue);
    }

    public static Locale getParameter(ServletContext ctx, String name, Locale defaultValue) {
        return SERVLET_CONTEXT.getParameter(ctx, name, defaultValue);
    }

    public static Locale getParameter(ServletConfig config, String name, Locale defaultValue) {
        return SERVLET_CONFIG.getParameter(config, name, defaultValue);
    }

    public static Locale getParameter(FilterConfig config, String name, Locale defaultValue) {
        return FILTER_CONFIG.getParameter(config, name, defaultValue);
    }

    public static Locale getParameter(ServletRequest request, String name, Locale defaultValue) {
        return SERVLET_REQUEST.getParameter(request, name, defaultValue);
    }

    public static <E extends Enum<E>> E getParameter(ServletContext ctx, String name, E defaultValue) {
        return SERVLET_CONTEXT.getParameter(ctx, name, defaultValue);
    }

    public static <E extends Enum<E>> E getParameter(FilterConfig config, String name, E defaultValue) {
        return FILTER_CONFIG.getParameter(config, name, defaultValue);
    }

    public static <E extends Enum<E>> E getParameter(ServletRequest ctx, String name, E defaultValue) {
        return SERVLET_REQUEST.getParameter(ctx, name, defaultValue);
    }

    public static String[] getParameter(ServletContext ctx, String name, String[] defaultArray) {
        return SERVLET_CONTEXT.getParameter(ctx, name, defaultArray);
    }

    public static String[] getParameter(FilterConfig ctx, String name, String[] defaultArray) {
        return FILTER_CONFIG.getParameter(ctx, name, defaultArray);
    }

    public static String[] getParameter(ServletRequest ctx, String name, String[] defaultArray) {
        return SERVLET_REQUEST.getParameter(ctx, name, defaultArray);
    }

}
