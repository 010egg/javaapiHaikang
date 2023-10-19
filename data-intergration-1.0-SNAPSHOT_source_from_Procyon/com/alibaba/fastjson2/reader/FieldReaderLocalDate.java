// 
// Decompiled by Procyon v0.5.36
// 

package com.alibaba.fastjson2.reader;

import java.time.LocalDate;
import com.alibaba.fastjson2.JSONReader;
import java.util.function.BiConsumer;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import com.alibaba.fastjson2.schema.JSONSchema;
import java.util.Locale;
import java.lang.reflect.Type;

final class FieldReaderLocalDate extends FieldReaderObject
{
    public FieldReaderLocalDate(final String fieldName, final Type fieldType, final Class fieldClass, final int ordinal, final long features, final String format, final Locale locale, final Object defaultValue, final JSONSchema schema, final Method method, final Field field, final BiConsumer function) {
        super(fieldName, fieldType, fieldClass, ordinal, features, format, locale, defaultValue, schema, method, field, function);
        this.initReader = ObjectReaderImplLocalDate.of(format, locale);
    }
    
    @Override
    public ObjectReader getObjectReader(final JSONReader jsonReader) {
        return this.initReader;
    }
    
    @Override
    public ObjectReader getObjectReader(final JSONReader.Context context) {
        return this.initReader;
    }
    
    @Override
    public void readFieldValue(final JSONReader jsonReader, final Object object) {
        LocalDate localDate;
        if (this.format != null) {
            localDate = this.initReader.readObject(jsonReader);
        }
        else {
            localDate = jsonReader.readLocalDate();
        }
        this.accept(object, localDate);
    }
}
