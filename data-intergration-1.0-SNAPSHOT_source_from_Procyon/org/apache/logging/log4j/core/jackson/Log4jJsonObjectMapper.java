// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.logging.log4j.core.jackson;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.ObjectMapper;

public class Log4jJsonObjectMapper extends ObjectMapper
{
    private static final long serialVersionUID = 1L;
    
    public Log4jJsonObjectMapper() {
        this(false, true);
    }
    
    public Log4jJsonObjectMapper(final boolean encodeThreadContextAsList, final boolean includeStacktrace) {
        this.registerModule(new Log4jJsonModule(encodeThreadContextAsList, includeStacktrace));
        this.setSerializationInclusion(JsonInclude.Include.NON_EMPTY);
    }
}
