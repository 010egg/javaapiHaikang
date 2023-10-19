// 
// Decompiled by Procyon v0.5.36
// 

package ch.qos.logback.classic.pattern;

import ch.qos.logback.classic.spi.ILoggingEvent;

public class MethodOfCallerConverter extends ClassicConverter
{
    @Override
    public String convert(final ILoggingEvent le) {
        final StackTraceElement[] cda = le.getCallerData();
        if (cda != null && cda.length > 0) {
            return cda[0].getMethodName();
        }
        return "?";
    }
}
