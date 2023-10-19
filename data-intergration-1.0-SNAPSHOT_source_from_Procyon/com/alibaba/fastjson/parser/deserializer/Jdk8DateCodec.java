// 
// Decompiled by Procyon v0.5.36
// 

package com.alibaba.fastjson.parser.deserializer;

import java.time.chrono.ChronoZonedDateTime;
import com.alibaba.fastjson.serializer.BeanContext;
import java.io.IOException;
import com.alibaba.fastjson.serializer.SerializeWriter;
import java.time.temporal.TemporalAccessor;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.alibaba.fastjson.serializer.JSONSerializer;
import java.util.Locale;
import java.util.Date;
import java.util.TimeZone;
import com.alibaba.fastjson.parser.JSONLexer;
import java.time.Duration;
import java.time.Period;
import java.time.ZoneId;
import java.time.OffsetTime;
import java.time.OffsetDateTime;
import com.alibaba.fastjson.parser.JSONScanner;
import java.time.ZonedDateTime;
import com.alibaba.fastjson.JSON;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.LocalDateTime;
import java.lang.reflect.Type;
import com.alibaba.fastjson.parser.DefaultJSONParser;
import java.time.format.DateTimeFormatter;
import com.alibaba.fastjson.serializer.ContextObjectSerializer;
import com.alibaba.fastjson.serializer.ObjectSerializer;

public class Jdk8DateCodec extends ContextObjectDeserializer implements ObjectSerializer, ContextObjectSerializer, ObjectDeserializer
{
    public static final Jdk8DateCodec instance;
    private static final String defaultPatttern = "yyyy-MM-dd HH:mm:ss";
    private static final DateTimeFormatter defaultFormatter;
    private static final DateTimeFormatter defaultFormatter_23;
    private static final DateTimeFormatter formatter_dt19_tw;
    private static final DateTimeFormatter formatter_dt19_cn;
    private static final DateTimeFormatter formatter_dt19_cn_1;
    private static final DateTimeFormatter formatter_dt19_kr;
    private static final DateTimeFormatter formatter_dt19_us;
    private static final DateTimeFormatter formatter_dt19_eur;
    private static final DateTimeFormatter formatter_dt19_de;
    private static final DateTimeFormatter formatter_dt19_in;
    private static final DateTimeFormatter formatter_d8;
    private static final DateTimeFormatter formatter_d10_tw;
    private static final DateTimeFormatter formatter_d10_cn;
    private static final DateTimeFormatter formatter_d10_kr;
    private static final DateTimeFormatter formatter_d10_us;
    private static final DateTimeFormatter formatter_d10_eur;
    private static final DateTimeFormatter formatter_d10_de;
    private static final DateTimeFormatter formatter_d10_in;
    private static final DateTimeFormatter ISO_FIXED_FORMAT;
    private static final String formatter_iso8601_pattern = "yyyy-MM-dd'T'HH:mm:ss";
    private static final String formatter_iso8601_pattern_23 = "yyyy-MM-dd'T'HH:mm:ss.SSS";
    private static final String formatter_iso8601_pattern_29 = "yyyy-MM-dd'T'HH:mm:ss.SSSSSSSSS";
    private static final DateTimeFormatter formatter_iso8601;
    
    @Override
    public <T> T deserialze(final DefaultJSONParser parser, final Type type, final Object fieldName, final String format, final int feature) {
        final JSONLexer lexer = parser.lexer;
        if (lexer.token() == 8) {
            lexer.nextToken();
            return null;
        }
        if (lexer.token() == 4) {
            final String text = lexer.stringVal();
            lexer.nextToken();
            DateTimeFormatter formatter = null;
            if (format != null) {
                if ("yyyy-MM-dd HH:mm:ss".equals(format)) {
                    formatter = Jdk8DateCodec.defaultFormatter;
                }
                else {
                    formatter = DateTimeFormatter.ofPattern(format);
                }
            }
            if ("".equals(text)) {
                return null;
            }
            if (type == LocalDateTime.class) {
                LocalDateTime localDateTime;
                if (text.length() == 10 || text.length() == 8) {
                    final LocalDate localDate = this.parseLocalDate(text, format, formatter);
                    localDateTime = LocalDateTime.of(localDate, LocalTime.MIN);
                }
                else {
                    localDateTime = this.parseDateTime(text, formatter);
                }
                return (T)localDateTime;
            }
            if (type == LocalDate.class) {
                LocalDate localDate2;
                if (text.length() == 23) {
                    final LocalDateTime localDateTime2 = LocalDateTime.parse(text);
                    localDate2 = LocalDate.of(localDateTime2.getYear(), localDateTime2.getMonthValue(), localDateTime2.getDayOfMonth());
                }
                else {
                    localDate2 = this.parseLocalDate(text, format, formatter);
                }
                return (T)localDate2;
            }
            if (type == LocalTime.class) {
                LocalTime localTime;
                if (text.length() == 23) {
                    final LocalDateTime localDateTime2 = LocalDateTime.parse(text);
                    localTime = LocalTime.of(localDateTime2.getHour(), localDateTime2.getMinute(), localDateTime2.getSecond(), localDateTime2.getNano());
                }
                else {
                    boolean digit = true;
                    for (int i = 0; i < text.length(); ++i) {
                        final char ch = text.charAt(i);
                        if (ch < '0' || ch > '9') {
                            digit = false;
                            break;
                        }
                    }
                    if (digit && text.length() > 8 && text.length() < 19) {
                        final long epochMillis = Long.parseLong(text);
                        localTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(epochMillis), JSON.defaultTimeZone.toZoneId()).toLocalTime();
                    }
                    else {
                        localTime = LocalTime.parse(text);
                    }
                }
                return (T)localTime;
            }
            if (type == ZonedDateTime.class) {
                if (formatter == Jdk8DateCodec.defaultFormatter) {
                    formatter = Jdk8DateCodec.ISO_FIXED_FORMAT;
                }
                if (formatter == null && text.length() <= 19) {
                    final JSONScanner s = new JSONScanner(text);
                    final TimeZone timeZone = parser.lexer.getTimeZone();
                    s.setTimeZone(timeZone);
                    final boolean match = s.scanISO8601DateIfMatch(false);
                    if (match) {
                        final Date date = s.getCalendar().getTime();
                        return (T)ZonedDateTime.ofInstant(date.toInstant(), timeZone.toZoneId());
                    }
                }
                final ZonedDateTime zonedDateTime = this.parseZonedDateTime(text, formatter);
                return (T)zonedDateTime;
            }
            if (type == OffsetDateTime.class) {
                final OffsetDateTime offsetDateTime = OffsetDateTime.parse(text);
                return (T)offsetDateTime;
            }
            if (type == OffsetTime.class) {
                final OffsetTime offsetTime = OffsetTime.parse(text);
                return (T)offsetTime;
            }
            if (type == ZoneId.class) {
                final ZoneId offsetTime2 = ZoneId.of(text);
                return (T)offsetTime2;
            }
            if (type == Period.class) {
                final Period period = Period.parse(text);
                return (T)period;
            }
            if (type == Duration.class) {
                final Duration duration = Duration.parse(text);
                return (T)duration;
            }
            if (type != Instant.class) {
                return null;
            }
            boolean digit2 = true;
            for (int j = 0; j < text.length(); ++j) {
                final char ch2 = text.charAt(j);
                if (ch2 < '0' || ch2 > '9') {
                    digit2 = false;
                    break;
                }
            }
            if (digit2 && text.length() > 8 && text.length() < 19) {
                final long epochMillis2 = Long.parseLong(text);
                return (T)Instant.ofEpochMilli(epochMillis2);
            }
            final Instant instant = Instant.parse(text);
            return (T)instant;
        }
        else {
            if (lexer.token() != 2) {
                throw new UnsupportedOperationException();
            }
            long millis = lexer.longValue();
            lexer.nextToken();
            if ("unixtime".equals(format)) {
                millis *= 1000L;
            }
            else if ("yyyyMMddHHmmss".equals(format)) {
                final int yyyy = (int)(millis / 10000000000L);
                final int MM = (int)(millis / 100000000L % 100L);
                final int dd = (int)(millis / 1000000L % 100L);
                final int HH = (int)(millis / 10000L % 100L);
                final int mm = (int)(millis / 100L % 100L);
                final int ss = (int)(millis % 100L);
                if (type == LocalDateTime.class) {
                    return (T)LocalDateTime.of(yyyy, MM, dd, HH, mm, ss);
                }
            }
            if (type == LocalDateTime.class) {
                return (T)LocalDateTime.ofInstant(Instant.ofEpochMilli(millis), JSON.defaultTimeZone.toZoneId());
            }
            if (type == LocalDate.class) {
                return (T)LocalDateTime.ofInstant(Instant.ofEpochMilli(millis), JSON.defaultTimeZone.toZoneId()).toLocalDate();
            }
            if (type == LocalTime.class) {
                return (T)LocalDateTime.ofInstant(Instant.ofEpochMilli(millis), JSON.defaultTimeZone.toZoneId()).toLocalTime();
            }
            if (type == ZonedDateTime.class) {
                return (T)ZonedDateTime.ofInstant(Instant.ofEpochMilli(millis), JSON.defaultTimeZone.toZoneId());
            }
            if (type == Instant.class) {
                return (T)Instant.ofEpochMilli(millis);
            }
            throw new UnsupportedOperationException();
        }
    }
    
    protected LocalDateTime parseDateTime(final String text, DateTimeFormatter formatter) {
        if (formatter == null) {
            if (text.length() == 19) {
                final char c4 = text.charAt(4);
                final char c5 = text.charAt(7);
                final char c6 = text.charAt(10);
                final char c7 = text.charAt(13);
                final char c8 = text.charAt(16);
                if (c7 == ':' && c8 == ':') {
                    if (c4 == '-' && c5 == '-') {
                        if (c6 == 'T') {
                            formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
                        }
                        else if (c6 == ' ') {
                            formatter = Jdk8DateCodec.defaultFormatter;
                        }
                    }
                    else if (c4 == '/' && c5 == '/') {
                        formatter = Jdk8DateCodec.formatter_dt19_tw;
                    }
                    else {
                        final char c9 = text.charAt(0);
                        final char c10 = text.charAt(1);
                        final char c11 = text.charAt(2);
                        final char c12 = text.charAt(3);
                        final char c13 = text.charAt(5);
                        if (c11 == '/' && c13 == '/') {
                            final int v0 = (c9 - '0') * 10 + (c10 - '0');
                            final int v2 = (c12 - '0') * 10 + (c4 - '0');
                            if (v0 > 12) {
                                formatter = Jdk8DateCodec.formatter_dt19_eur;
                            }
                            else if (v2 > 12) {
                                formatter = Jdk8DateCodec.formatter_dt19_us;
                            }
                            else {
                                final String country = Locale.getDefault().getCountry();
                                if (country.equals("US")) {
                                    formatter = Jdk8DateCodec.formatter_dt19_us;
                                }
                                else if (country.equals("BR") || country.equals("AU")) {
                                    formatter = Jdk8DateCodec.formatter_dt19_eur;
                                }
                            }
                        }
                        else if (c11 == '.' && c13 == '.') {
                            formatter = Jdk8DateCodec.formatter_dt19_de;
                        }
                        else if (c11 == '-' && c13 == '-') {
                            formatter = Jdk8DateCodec.formatter_dt19_in;
                        }
                    }
                }
            }
            else if (text.length() == 23) {
                final char c4 = text.charAt(4);
                final char c5 = text.charAt(7);
                final char c6 = text.charAt(10);
                final char c7 = text.charAt(13);
                final char c8 = text.charAt(16);
                final char c14 = text.charAt(19);
                if (c7 == ':' && c8 == ':' && c4 == '-' && c5 == '-' && c6 == ' ' && c14 == '.') {
                    formatter = Jdk8DateCodec.defaultFormatter_23;
                }
            }
            if (text.length() >= 17) {
                final char c4 = text.charAt(4);
                if (c4 == '\u5e74') {
                    if (text.charAt(text.length() - 1) == '\u79d2') {
                        formatter = Jdk8DateCodec.formatter_dt19_cn_1;
                    }
                    else {
                        formatter = Jdk8DateCodec.formatter_dt19_cn;
                    }
                }
                else if (c4 == '\ub144') {
                    formatter = Jdk8DateCodec.formatter_dt19_kr;
                }
            }
        }
        if (formatter == null) {
            final JSONScanner dateScanner = new JSONScanner(text);
            if (dateScanner.scanISO8601DateIfMatch(false)) {
                final Instant instant = dateScanner.getCalendar().toInstant();
                return LocalDateTime.ofInstant(instant, ZoneId.systemDefault());
            }
            boolean digit = true;
            for (int i = 0; i < text.length(); ++i) {
                final char ch = text.charAt(i);
                if (ch < '0' || ch > '9') {
                    digit = false;
                    break;
                }
            }
            if (digit && text.length() > 8 && text.length() < 19) {
                final long epochMillis = Long.parseLong(text);
                return LocalDateTime.ofInstant(Instant.ofEpochMilli(epochMillis), JSON.defaultTimeZone.toZoneId());
            }
        }
        return (formatter == null) ? LocalDateTime.parse(text) : LocalDateTime.parse(text, formatter);
    }
    
    protected LocalDate parseLocalDate(final String text, final String format, DateTimeFormatter formatter) {
        if (formatter == null) {
            if (text.length() == 8) {
                formatter = Jdk8DateCodec.formatter_d8;
            }
            if (text.length() == 10) {
                final char c4 = text.charAt(4);
                final char c5 = text.charAt(7);
                if (c4 == '/' && c5 == '/') {
                    formatter = Jdk8DateCodec.formatter_d10_tw;
                }
                final char c6 = text.charAt(0);
                final char c7 = text.charAt(1);
                final char c8 = text.charAt(2);
                final char c9 = text.charAt(3);
                final char c10 = text.charAt(5);
                if (c8 == '/' && c10 == '/') {
                    final int v0 = (c6 - '0') * 10 + (c7 - '0');
                    final int v2 = (c9 - '0') * 10 + (c4 - '0');
                    if (v0 > 12) {
                        formatter = Jdk8DateCodec.formatter_d10_eur;
                    }
                    else if (v2 > 12) {
                        formatter = Jdk8DateCodec.formatter_d10_us;
                    }
                    else {
                        final String country = Locale.getDefault().getCountry();
                        if (country.equals("US")) {
                            formatter = Jdk8DateCodec.formatter_d10_us;
                        }
                        else if (country.equals("BR") || country.equals("AU")) {
                            formatter = Jdk8DateCodec.formatter_d10_eur;
                        }
                    }
                }
                else if (c8 == '.' && c10 == '.') {
                    formatter = Jdk8DateCodec.formatter_d10_de;
                }
                else if (c8 == '-' && c10 == '-') {
                    formatter = Jdk8DateCodec.formatter_d10_in;
                }
            }
            if (text.length() >= 9) {
                final char c4 = text.charAt(4);
                if (c4 == '\u5e74') {
                    formatter = Jdk8DateCodec.formatter_d10_cn;
                }
                else if (c4 == '\ub144') {
                    formatter = Jdk8DateCodec.formatter_d10_kr;
                }
            }
            boolean digit = true;
            for (int i = 0; i < text.length(); ++i) {
                final char ch = text.charAt(i);
                if (ch < '0' || ch > '9') {
                    digit = false;
                    break;
                }
            }
            if (digit && text.length() > 8 && text.length() < 19) {
                final long epochMillis = Long.parseLong(text);
                return LocalDateTime.ofInstant(Instant.ofEpochMilli(epochMillis), JSON.defaultTimeZone.toZoneId()).toLocalDate();
            }
        }
        return (formatter == null) ? LocalDate.parse(text) : LocalDate.parse(text, formatter);
    }
    
    protected ZonedDateTime parseZonedDateTime(final String text, DateTimeFormatter formatter) {
        if (formatter == null) {
            if (text.length() == 19) {
                final char c4 = text.charAt(4);
                final char c5 = text.charAt(7);
                final char c6 = text.charAt(10);
                final char c7 = text.charAt(13);
                final char c8 = text.charAt(16);
                if (c7 == ':' && c8 == ':') {
                    if (c4 == '-' && c5 == '-') {
                        if (c6 == 'T') {
                            formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
                        }
                        else if (c6 == ' ') {
                            formatter = Jdk8DateCodec.defaultFormatter;
                        }
                    }
                    else if (c4 == '/' && c5 == '/') {
                        formatter = Jdk8DateCodec.formatter_dt19_tw;
                    }
                    else {
                        final char c9 = text.charAt(0);
                        final char c10 = text.charAt(1);
                        final char c11 = text.charAt(2);
                        final char c12 = text.charAt(3);
                        final char c13 = text.charAt(5);
                        if (c11 == '/' && c13 == '/') {
                            final int v0 = (c9 - '0') * 10 + (c10 - '0');
                            final int v2 = (c12 - '0') * 10 + (c4 - '0');
                            if (v0 > 12) {
                                formatter = Jdk8DateCodec.formatter_dt19_eur;
                            }
                            else if (v2 > 12) {
                                formatter = Jdk8DateCodec.formatter_dt19_us;
                            }
                            else {
                                final String country = Locale.getDefault().getCountry();
                                if (country.equals("US")) {
                                    formatter = Jdk8DateCodec.formatter_dt19_us;
                                }
                                else if (country.equals("BR") || country.equals("AU")) {
                                    formatter = Jdk8DateCodec.formatter_dt19_eur;
                                }
                            }
                        }
                        else if (c11 == '.' && c13 == '.') {
                            formatter = Jdk8DateCodec.formatter_dt19_de;
                        }
                        else if (c11 == '-' && c13 == '-') {
                            formatter = Jdk8DateCodec.formatter_dt19_in;
                        }
                    }
                }
            }
            if (text.length() >= 17) {
                final char c4 = text.charAt(4);
                if (c4 == '\u5e74') {
                    if (text.charAt(text.length() - 1) == '\u79d2') {
                        formatter = Jdk8DateCodec.formatter_dt19_cn_1;
                    }
                    else {
                        formatter = Jdk8DateCodec.formatter_dt19_cn;
                    }
                }
                else if (c4 == '\ub144') {
                    formatter = Jdk8DateCodec.formatter_dt19_kr;
                }
            }
            boolean digit = true;
            for (int i = 0; i < text.length(); ++i) {
                final char ch = text.charAt(i);
                if (ch < '0' || ch > '9') {
                    digit = false;
                    break;
                }
            }
            if (digit && text.length() > 8 && text.length() < 19) {
                final long epochMillis = Long.parseLong(text);
                return ZonedDateTime.ofInstant(Instant.ofEpochMilli(epochMillis), JSON.defaultTimeZone.toZoneId());
            }
        }
        return (formatter == null) ? ZonedDateTime.parse(text) : ZonedDateTime.parse(text, formatter);
    }
    
    public int getFastMatchToken() {
        return 4;
    }
    
    public void write(final JSONSerializer serializer, final Object object, final Object fieldName, Type fieldType, final int features) throws IOException {
        final SerializeWriter out = serializer.out;
        if (object == null) {
            out.writeNull();
        }
        else {
            if (fieldType == null) {
                fieldType = object.getClass();
            }
            if (fieldType == LocalDateTime.class) {
                final int mask = SerializerFeature.UseISO8601DateFormat.getMask();
                final LocalDateTime dateTime = (LocalDateTime)object;
                String format = serializer.getDateFormatPattern();
                if (format == null) {
                    if ((features & mask) != 0x0 || serializer.isEnabled(SerializerFeature.UseISO8601DateFormat)) {
                        format = "yyyy-MM-dd'T'HH:mm:ss";
                    }
                    else if (serializer.isEnabled(SerializerFeature.WriteDateUseDateFormat)) {
                        format = JSON.DEFFAULT_DATE_FORMAT;
                    }
                    else {
                        final int nano = dateTime.getNano();
                        if (nano == 0) {
                            format = "yyyy-MM-dd'T'HH:mm:ss";
                        }
                        else if (nano % 1000000 == 0) {
                            format = "yyyy-MM-dd'T'HH:mm:ss.SSS";
                        }
                        else {
                            format = "yyyy-MM-dd'T'HH:mm:ss.SSSSSSSSS";
                        }
                    }
                }
                if (format != null) {
                    this.write(out, dateTime, format);
                }
                else {
                    out.writeLong(dateTime.atZone(JSON.defaultTimeZone.toZoneId()).toInstant().toEpochMilli());
                }
            }
            else {
                out.writeString(object.toString());
            }
        }
    }
    
    public void write(final JSONSerializer serializer, final Object object, final BeanContext context) throws IOException {
        final SerializeWriter out = serializer.out;
        final String format = context.getFormat();
        this.write(out, (TemporalAccessor)object, format);
    }
    
    private void write(final SerializeWriter out, final TemporalAccessor object, final String format) {
        if ("unixtime".equals(format)) {
            final Instant instant = null;
            if (object instanceof ChronoZonedDateTime) {
                final long seconds = ((ChronoZonedDateTime)object).toEpochSecond();
                out.writeInt((int)seconds);
                return;
            }
            if (object instanceof LocalDateTime) {
                final long seconds = ((LocalDateTime)object).atZone(JSON.defaultTimeZone.toZoneId()).toEpochSecond();
                out.writeInt((int)seconds);
                return;
            }
        }
        if ("millis".equals(format)) {
            Instant instant = null;
            if (object instanceof ChronoZonedDateTime) {
                instant = ((ChronoZonedDateTime)object).toInstant();
            }
            else if (object instanceof LocalDateTime) {
                instant = ((LocalDateTime)object).atZone(JSON.defaultTimeZone.toZoneId()).toInstant();
            }
            if (instant != null) {
                final long millis = instant.toEpochMilli();
                out.writeLong(millis);
                return;
            }
        }
        DateTimeFormatter formatter;
        if (format == "yyyy-MM-dd'T'HH:mm:ss") {
            formatter = Jdk8DateCodec.formatter_iso8601;
        }
        else {
            formatter = DateTimeFormatter.ofPattern(format);
        }
        final String text = formatter.format(object);
        out.writeString(text);
    }
    
    public static Object castToLocalDateTime(final Object value, String format) {
        if (value == null) {
            return null;
        }
        if (format == null) {
            format = "yyyy-MM-dd HH:mm:ss";
        }
        final DateTimeFormatter df = DateTimeFormatter.ofPattern(format);
        return LocalDateTime.parse(value.toString(), df);
    }
    
    static {
        instance = new Jdk8DateCodec();
        defaultFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        defaultFormatter_23 = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");
        formatter_dt19_tw = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
        formatter_dt19_cn = DateTimeFormatter.ofPattern("yyyy\u5e74M\u6708d\u65e5 HH:mm:ss");
        formatter_dt19_cn_1 = DateTimeFormatter.ofPattern("yyyy\u5e74M\u6708d\u65e5 H\u65f6m\u5206s\u79d2");
        formatter_dt19_kr = DateTimeFormatter.ofPattern("yyyy\ub144M\uc6d4d\uc77c HH:mm:ss");
        formatter_dt19_us = DateTimeFormatter.ofPattern("MM/dd/yyyy HH:mm:ss");
        formatter_dt19_eur = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
        formatter_dt19_de = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss");
        formatter_dt19_in = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
        formatter_d8 = DateTimeFormatter.ofPattern("yyyyMMdd");
        formatter_d10_tw = DateTimeFormatter.ofPattern("yyyy/MM/dd");
        formatter_d10_cn = DateTimeFormatter.ofPattern("yyyy\u5e74M\u6708d\u65e5");
        formatter_d10_kr = DateTimeFormatter.ofPattern("yyyy\ub144M\uc6d4d\uc77c");
        formatter_d10_us = DateTimeFormatter.ofPattern("MM/dd/yyyy");
        formatter_d10_eur = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        formatter_d10_de = DateTimeFormatter.ofPattern("dd.MM.yyyy");
        formatter_d10_in = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        ISO_FIXED_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").withZone(ZoneId.systemDefault());
        formatter_iso8601 = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
    }
}
