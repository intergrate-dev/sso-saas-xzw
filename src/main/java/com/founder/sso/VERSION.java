package com.founder.sso;

public final class VERSION {
    public static final int MAJOR_VERSION = 2;
    public static final int MINOR_VERSION = 0;
    public static final int REVISION_VERSION = 0;

    public static String getVersionNumber() {
        return VERSION.MAJOR_VERSION + "." + VERSION.MINOR_VERSION + "." + VERSION.REVISION_VERSION;
    }
}
