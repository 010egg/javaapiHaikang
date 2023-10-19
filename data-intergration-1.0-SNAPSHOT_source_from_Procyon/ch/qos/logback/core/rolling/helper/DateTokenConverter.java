// 
// Decompiled by Procyon v0.5.36
// 

package ch.qos.logback.core.rolling.helper;

import ch.qos.logback.core.util.DatePatternToRegexUtil;
import java.util.Date;
import java.util.List;
import ch.qos.logback.core.util.CachingDateFormatter;
import java.util.TimeZone;
import ch.qos.logback.core.pattern.DynamicConverter;

public class DateTokenConverter<E> extends DynamicConverter<E> implements MonoTypedConverter
{
    public static final String CONVERTER_KEY = "d";
    public static final String AUXILIARY_TOKEN = "AUX";
    public static final String DEFAULT_DATE_PATTERN = "yyyy-MM-dd";
    private String datePattern;
    private TimeZone timeZone;
    private CachingDateFormatter cdf;
    private boolean primary;
    
    public DateTokenConverter() {
        this.primary = true;
    }
    
    @Override
    public void start() {
        this.datePattern = this.getFirstOption();
        if (this.datePattern == null) {
            this.datePattern = "yyyy-MM-dd";
        }
        final List<String> optionList = this.getOptionList();
        if (optionList != null) {
            for (int optionIndex = 1; optionIndex < optionList.size(); ++optionIndex) {
                final String option = optionList.get(optionIndex);
                if ("AUX".equalsIgnoreCase(option)) {
                    this.primary = false;
                }
                else {
                    this.timeZone = TimeZone.getTimeZone(option);
                }
            }
        }
        this.cdf = new CachingDateFormatter(this.datePattern);
        if (this.timeZone != null) {
            this.cdf.setTimeZone(this.timeZone);
        }
    }
    
    public String convert(final Date date) {
        return this.cdf.format(date.getTime());
    }
    
    @Override
    public String convert(final Object o) {
        if (o == null) {
            throw new IllegalArgumentException("Null argument forbidden");
        }
        if (o instanceof Date) {
            return this.convert((Date)o);
        }
        throw new IllegalArgumentException("Cannot convert " + o + " of type" + o.getClass().getName());
    }
    
    public String getDatePattern() {
        return this.datePattern;
    }
    
    public TimeZone getTimeZone() {
        return this.timeZone;
    }
    
    @Override
    public boolean isApplicable(final Object o) {
        return o instanceof Date;
    }
    
    public String toRegex() {
        final DatePatternToRegexUtil datePatternToRegexUtil = new DatePatternToRegexUtil(this.datePattern);
        return datePatternToRegexUtil.toRegex();
    }
    
    public boolean isPrimary() {
        return this.primary;
    }
}
