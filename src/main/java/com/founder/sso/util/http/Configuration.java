package com.founder.sso.util.http;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.security.AccessControlException;
import java.util.Properties;

public class Configuration {
    private static Properties defaultProperty;

    static {
        init();
    }

    /* package */static void init() {
        defaultProperty = new Properties();
        defaultProperty.setProperty("http.useSSL", "false");
        defaultProperty.setProperty("http.useProxy", "false");
//        defaultProperty.setProperty("http.proxyHost", "http.proxyHost");
//        defaultProperty.setProperty("http.proxyPort", "http.proxyPort");
        defaultProperty.setProperty("http.connectionTimeout", "20000");
        defaultProperty.setProperty("http.readTimeout", "120000");
        defaultProperty.setProperty("http.retryCount", "3");
        defaultProperty.setProperty("http.retryIntervalSecs", "10");

        try {
            // Android platform should have dalvik.system.VMRuntime in the
            // classpath.
            // @see
            // http://developer.android.com/reference/dalvik/system/VMRuntime.html
            Class.forName("dalvik.system.VMRuntime");
            defaultProperty.setProperty("http.dalvik", "true");
        } catch (ClassNotFoundException cnfe) {
            defaultProperty.setProperty("http.dalvik", "false");
        }
        DALVIK = getBoolean("http.dalvik");
        String t4jProps = "httpclient.properties";
        boolean loaded = loadProperties(defaultProperty, "." + File.separatorChar + t4jProps)
                || loadProperties(defaultProperty, Configuration.class.getResourceAsStream("/WEB-INF/" + t4jProps))
                || loadProperties(defaultProperty, Configuration.class.getResourceAsStream("/" + t4jProps));
    }

    private static boolean loadProperties(Properties props, String path) {
        try {
            File file = new File(path);
            if (file.exists() && file.isFile()) {
                props.load(new FileInputStream(file));
                return true;
            }
        } catch (Exception ignore) {
        }
        return false;
    }

    private static boolean loadProperties(Properties props, InputStream is) {
        try {
            props.load(is);
            return true;
        } catch (Exception ignore) {
        }
        return false;
    }

    private static boolean DALVIK;

    public static boolean isDalvik() {
        return DALVIK;
    }

    public static boolean useSSL() {
        return getBoolean("http.useSSL");
    }

    public static String getScheme() {
        return useSSL() ? "https://" : "http://";
    }

    public static String getProxyHost() {
        return getProperty("http.proxyHost");
    }

    public static String getProxyHost(String proxyHost) {
        return getProperty("http.proxyHost", proxyHost);
    }

    public static String getProxyUser() {
        return getProperty("http.proxyUser");
    }

    public static String getProxyUser(String user) {
        return getProperty("http.proxyUser", user);
    }


    public static String getProxyPassword() {
        return getProperty("http.proxyPassword");
    }

    public static String getProxyPassword(String password) {
        return getProperty("http.proxyPassword", password);
    }

    public static int getProxyPort() {
        return getIntProperty("http.proxyPort");
    }

    public static int getProxyPort(int port) {
        return getIntProperty("http.proxyPort", port);
    }

    public static int getConnectionTimeout() {
        return getIntProperty("http.connectionTimeout");
    }

    public static int getConnectionTimeout(int connectionTimeout) {
        return getIntProperty("http.connectionTimeout", connectionTimeout);
    }

    public static int getReadTimeout() {
        return getIntProperty("http.readTimeout");
    }

    public static int getReadTimeout(int readTimeout) {
        return getIntProperty("http.readTimeout", readTimeout);
    }

    public static int getRetryCount() {
        return getIntProperty("http.retryCount");
    }

    public static int getRetryCount(int retryCount) {
        return getIntProperty("http.retryCount", retryCount);
    }

    public static int getRetryIntervalSecs() {
        return getIntProperty("http.retryIntervalSecs");
    }

    public static int getRetryIntervalSecs(int retryIntervalSecs) {
        return getIntProperty("http.retryIntervalSecs", retryIntervalSecs);
    }

    public static String getUserAgent() {
        return getProperty("http.userAgent");
    }

    public static String getUserAgent(String userAgent) {
        return getProperty("http.userAgent", userAgent);
    }

    public static boolean getBoolean(String name) {
        String value = getProperty(name);
        return Boolean.valueOf(value);
    }

    public static int getIntProperty(String name) {
        String value = getProperty(name);
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException nfe) {
            return -1;
        }
    }

    public static int getIntProperty(String name, int fallbackValue) {
        String value = getProperty(name, String.valueOf(fallbackValue));
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException nfe) {
            return -1;
        }
    }

    public static long getLongProperty(String name) {
        String value = getProperty(name);
        try {
            return Long.parseLong(value);
        } catch (NumberFormatException nfe) {
            return -1;
        }
    }

    public static String getProperty(String name) {
        return getProperty(name, null);
    }

    public static String getProperty(String name, String fallbackValue) {
        String value;
        try {
            value = System.getProperty(name, fallbackValue);
            if (null == value) {
                value = defaultProperty.getProperty(name);
            }
            if (null == value) {
                String fallback = defaultProperty.getProperty(name + ".fallback");
                if (null != fallback) {
                    value = System.getProperty(fallback);
                }
            }
        } catch (AccessControlException ace) {
            // Unsigned applet cannot access System properties
            value = fallbackValue;
        }
        return replace(value);
    }

    private static String replace(String value) {
        if (null == value) {
            return value;
        }
        String newValue = value;
        int openBrace = 0;
        if (-1 != (openBrace = value.indexOf("{", openBrace))) {
            int closeBrace = value.indexOf("}", openBrace);
            if (closeBrace > (openBrace + 1)) {
                String name = value.substring(openBrace + 1, closeBrace);
                if (name.length() > 0) {
                    newValue = value.substring(0, openBrace) + getProperty(name) + value.substring(closeBrace + 1);

                }
            }
        }
        if (newValue.equals(value)) {
            return value;
        } else {
            return replace(newValue);
        }
    }


    public static boolean getDebug() {
        return getBoolean("http.debug");

    }
}
