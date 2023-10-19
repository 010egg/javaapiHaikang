// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.logging.log4j.core.layout;

import java.io.IOException;
import java.io.Writer;
import org.apache.logging.log4j.core.LogEvent;
import java.nio.charset.StandardCharsets;
import org.apache.logging.log4j.core.config.DefaultConfiguration;
import org.apache.logging.log4j.core.config.plugins.PluginFactory;
import org.apache.logging.log4j.core.config.plugins.PluginAttribute;
import org.apache.logging.log4j.core.config.plugins.PluginConfiguration;
import java.util.HashMap;
import java.util.Map;
import org.apache.logging.log4j.core.pattern.RegexReplacement;
import java.nio.charset.Charset;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.plugins.Plugin;

@Plugin(name = "JsonLayout", category = "Core", elementType = "layout", printObject = true)
public final class JsonLayout extends AbstractJacksonLayout
{
    private static final String DEFAULT_FOOTER = "]";
    private static final String DEFAULT_HEADER = "[";
    static final String CONTENT_TYPE = "application/json";
    
    protected JsonLayout(final Configuration config, final boolean locationInfo, final boolean properties, final boolean encodeThreadContextAsList, final boolean complete, final boolean compact, final boolean eventEol, final String headerPattern, final String footerPattern, final Charset charset, final boolean includeStacktrace) {
        super(config, new JacksonFactory.JSON(encodeThreadContextAsList, includeStacktrace).newWriter(locationInfo, properties, compact), charset, compact, complete, eventEol, PatternLayout.createSerializer(config, null, headerPattern, "[", null, false, false), PatternLayout.createSerializer(config, null, footerPattern, "]", null, false, false));
    }
    
    @Override
    public byte[] getHeader() {
        if (!this.complete) {
            return null;
        }
        final StringBuilder buf = new StringBuilder();
        final String str = this.serializeToString(this.getHeaderSerializer());
        if (str != null) {
            buf.append(str);
        }
        buf.append(this.eol);
        return this.getBytes(buf.toString());
    }
    
    @Override
    public byte[] getFooter() {
        if (!this.complete) {
            return null;
        }
        final StringBuilder buf = new StringBuilder();
        buf.append(this.eol);
        final String str = this.serializeToString(this.getFooterSerializer());
        if (str != null) {
            buf.append(str);
        }
        buf.append(this.eol);
        return this.getBytes(buf.toString());
    }
    
    @Override
    public Map<String, String> getContentFormat() {
        final Map<String, String> result = new HashMap<String, String>();
        result.put("version", "2.0");
        return result;
    }
    
    @Override
    public String getContentType() {
        return "application/json; charset=" + this.getCharset();
    }
    
    @PluginFactory
    public static JsonLayout createLayout(@PluginConfiguration final Configuration config, @PluginAttribute(value = "locationInfo", defaultBoolean = false) final boolean locationInfo, @PluginAttribute(value = "properties", defaultBoolean = false) final boolean properties, @PluginAttribute(value = "propertiesAsList", defaultBoolean = false) final boolean propertiesAsList, @PluginAttribute(value = "complete", defaultBoolean = false) final boolean complete, @PluginAttribute(value = "compact", defaultBoolean = false) final boolean compact, @PluginAttribute(value = "eventEol", defaultBoolean = false) final boolean eventEol, @PluginAttribute(value = "header", defaultString = "[") final String headerPattern, @PluginAttribute(value = "footer", defaultString = "]") final String footerPattern, @PluginAttribute(value = "charset", defaultString = "UTF-8") final Charset charset, @PluginAttribute(value = "includeStacktrace", defaultBoolean = true) final boolean includeStacktrace) {
        final boolean encodeThreadContextAsList = properties && propertiesAsList;
        return new JsonLayout(config, locationInfo, properties, encodeThreadContextAsList, complete, compact, eventEol, headerPattern, footerPattern, charset, includeStacktrace);
    }
    
    public static JsonLayout createDefaultLayout() {
        return new JsonLayout(new DefaultConfiguration(), false, false, false, false, false, false, "[", "]", StandardCharsets.UTF_8, true);
    }
    
    @Override
    public void toSerializable(final LogEvent event, final Writer writer) throws IOException {
        if (this.complete && this.eventCount > 0L) {
            writer.append((CharSequence)", ");
        }
        super.toSerializable(event, writer);
    }
}
