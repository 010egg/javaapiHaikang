// 
// Decompiled by Procyon v0.5.36
// 

package ch.qos.logback.classic.pattern;

import ch.qos.logback.classic.spi.ILoggingEvent;

public class NopThrowableInformationConverter extends ThrowableHandlingConverter
{
    @Override
    public String convert(final ILoggingEvent event) {
        return "";
    }
}
