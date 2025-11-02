package edu.uca.registration.util;

import java.util.logging.*;

 // Use for diagnostics (not user-facing output).
public final class Log {
    private static final Logger L = Logger.getLogger("registration");
    static { L.setUseParentHandlers(true); } // or configure handlers/level from Config
    public static void info(String m){ L.info(m); }
    public static void warn(String m){ L.warning(m); }
    public static void error(String m, Throwable t){ L.log(Level.SEVERE, m, t); }
}
