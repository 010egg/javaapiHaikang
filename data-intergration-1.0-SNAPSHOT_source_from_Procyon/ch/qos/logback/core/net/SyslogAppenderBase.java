// 
// Decompiled by Procyon v0.5.36
// 

package ch.qos.logback.core.net;

import java.io.IOException;
import java.io.OutputStream;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.nio.charset.Charset;
import ch.qos.logback.core.Layout;
import ch.qos.logback.core.AppenderBase;

public abstract class SyslogAppenderBase<E> extends AppenderBase<E>
{
    static final String SYSLOG_LAYOUT_URL = "http://logback.qos.ch/codes.html#syslog_layout";
    static final int MAX_MESSAGE_SIZE_LIMIT = 65000;
    Layout<E> layout;
    String facilityStr;
    String syslogHost;
    protected String suffixPattern;
    SyslogOutputStream sos;
    int port;
    int maxMessageSize;
    Charset charset;
    
    public SyslogAppenderBase() {
        this.port = 514;
    }
    
    @Override
    public void start() {
        int errorCount = 0;
        if (this.facilityStr == null) {
            this.addError("The Facility option is mandatory");
            ++errorCount;
        }
        if (this.charset == null) {
            this.charset = Charset.defaultCharset();
        }
        try {
            this.sos = this.createOutputStream();
            final int systemDatagramSize = this.sos.getSendBufferSize();
            if (this.maxMessageSize == 0) {
                this.maxMessageSize = Math.min(systemDatagramSize, 65000);
                this.addInfo("Defaulting maxMessageSize to [" + this.maxMessageSize + "]");
            }
            else if (this.maxMessageSize > systemDatagramSize) {
                this.addWarn("maxMessageSize of [" + this.maxMessageSize + "] is larger than the system defined datagram size of [" + systemDatagramSize + "].");
                this.addWarn("This may result in dropped logs.");
            }
        }
        catch (UnknownHostException e) {
            this.addError("Could not create SyslogWriter", e);
            ++errorCount;
        }
        catch (SocketException e2) {
            this.addWarn("Failed to bind to a random datagram socket. Will try to reconnect later.", e2);
        }
        if (this.layout == null) {
            this.layout = this.buildLayout();
        }
        if (errorCount == 0) {
            super.start();
        }
    }
    
    public abstract SyslogOutputStream createOutputStream() throws UnknownHostException, SocketException;
    
    public abstract Layout<E> buildLayout();
    
    public abstract int getSeverityForEvent(final Object p0);
    
    @Override
    protected void append(final E eventObject) {
        if (!this.isStarted()) {
            return;
        }
        try {
            String msg = this.layout.doLayout(eventObject);
            if (msg == null) {
                return;
            }
            if (msg.length() > this.maxMessageSize) {
                msg = msg.substring(0, this.maxMessageSize);
            }
            this.sos.write(msg.getBytes(this.charset));
            this.sos.flush();
            this.postProcess(eventObject, this.sos);
        }
        catch (IOException ioe) {
            this.addError("Failed to send diagram to " + this.syslogHost, ioe);
        }
    }
    
    protected void postProcess(final Object event, final OutputStream sw) {
    }
    
    public static int facilityStringToint(final String facilityStr) {
        if ("KERN".equalsIgnoreCase(facilityStr)) {
            return 0;
        }
        if ("USER".equalsIgnoreCase(facilityStr)) {
            return 8;
        }
        if ("MAIL".equalsIgnoreCase(facilityStr)) {
            return 16;
        }
        if ("DAEMON".equalsIgnoreCase(facilityStr)) {
            return 24;
        }
        if ("AUTH".equalsIgnoreCase(facilityStr)) {
            return 32;
        }
        if ("SYSLOG".equalsIgnoreCase(facilityStr)) {
            return 40;
        }
        if ("LPR".equalsIgnoreCase(facilityStr)) {
            return 48;
        }
        if ("NEWS".equalsIgnoreCase(facilityStr)) {
            return 56;
        }
        if ("UUCP".equalsIgnoreCase(facilityStr)) {
            return 64;
        }
        if ("CRON".equalsIgnoreCase(facilityStr)) {
            return 72;
        }
        if ("AUTHPRIV".equalsIgnoreCase(facilityStr)) {
            return 80;
        }
        if ("FTP".equalsIgnoreCase(facilityStr)) {
            return 88;
        }
        if ("NTP".equalsIgnoreCase(facilityStr)) {
            return 96;
        }
        if ("AUDIT".equalsIgnoreCase(facilityStr)) {
            return 104;
        }
        if ("ALERT".equalsIgnoreCase(facilityStr)) {
            return 112;
        }
        if ("CLOCK".equalsIgnoreCase(facilityStr)) {
            return 120;
        }
        if ("LOCAL0".equalsIgnoreCase(facilityStr)) {
            return 128;
        }
        if ("LOCAL1".equalsIgnoreCase(facilityStr)) {
            return 136;
        }
        if ("LOCAL2".equalsIgnoreCase(facilityStr)) {
            return 144;
        }
        if ("LOCAL3".equalsIgnoreCase(facilityStr)) {
            return 152;
        }
        if ("LOCAL4".equalsIgnoreCase(facilityStr)) {
            return 160;
        }
        if ("LOCAL5".equalsIgnoreCase(facilityStr)) {
            return 168;
        }
        if ("LOCAL6".equalsIgnoreCase(facilityStr)) {
            return 176;
        }
        if ("LOCAL7".equalsIgnoreCase(facilityStr)) {
            return 184;
        }
        throw new IllegalArgumentException(facilityStr + " is not a valid syslog facility string");
    }
    
    public String getSyslogHost() {
        return this.syslogHost;
    }
    
    public void setSyslogHost(final String syslogHost) {
        this.syslogHost = syslogHost;
    }
    
    public String getFacility() {
        return this.facilityStr;
    }
    
    public void setFacility(String facilityStr) {
        if (facilityStr != null) {
            facilityStr = facilityStr.trim();
        }
        this.facilityStr = facilityStr;
    }
    
    public int getPort() {
        return this.port;
    }
    
    public void setPort(final int port) {
        this.port = port;
    }
    
    public int getMaxMessageSize() {
        return this.maxMessageSize;
    }
    
    public void setMaxMessageSize(final int maxMessageSize) {
        this.maxMessageSize = maxMessageSize;
    }
    
    public Layout<E> getLayout() {
        return this.layout;
    }
    
    public void setLayout(final Layout<E> layout) {
        this.addWarn("The layout of a SyslogAppender cannot be set directly. See also http://logback.qos.ch/codes.html#syslog_layout");
    }
    
    @Override
    public void stop() {
        if (this.sos != null) {
            this.sos.close();
        }
        super.stop();
    }
    
    public String getSuffixPattern() {
        return this.suffixPattern;
    }
    
    public void setSuffixPattern(final String suffixPattern) {
        this.suffixPattern = suffixPattern;
    }
    
    public Charset getCharset() {
        return this.charset;
    }
    
    public void setCharset(final Charset charset) {
        this.charset = charset;
    }
}
