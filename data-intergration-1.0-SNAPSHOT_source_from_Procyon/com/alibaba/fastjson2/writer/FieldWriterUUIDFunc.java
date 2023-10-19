// 
// Decompiled by Procyon v0.5.36
// 

package com.alibaba.fastjson2.writer;

import java.util.UUID;
import com.alibaba.fastjson2.JSONWriter;
import java.lang.reflect.Method;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.function.Function;

final class FieldWriterUUIDFunc<T> extends FieldWriterObjectFinal<T>
{
    final Function function;
    
    FieldWriterUUIDFunc(final String name, final int ordinal, final long features, final String format, final String label, final Type fieldType, final Class fieldClass, final Field field, final Method method, final Function function) {
        super(name, ordinal, features, format, label, fieldType, fieldClass, field, method);
        this.function = function;
    }
    
    @Override
    public Object getFieldValue(final Object object) {
        return this.function.apply(object);
    }
    
    @Override
    public boolean write(final JSONWriter jsonWriter, final T object) {
        final UUID uuid = this.function.apply(object);
        if (uuid != null) {
            this.writeFieldName(jsonWriter);
            if (this.objectWriter == null) {
                this.objectWriter = this.getObjectWriter(jsonWriter, UUID.class);
            }
            if (this.objectWriter != ObjectWriterImplUUID.INSTANCE) {
                this.objectWriter.write(jsonWriter, uuid, this.fieldName, this.fieldClass, this.features);
            }
            else {
                jsonWriter.writeUUID(uuid);
            }
            return true;
        }
        final long features = this.features | jsonWriter.getFeatures();
        if ((features & JSONWriter.Feature.WriteNulls.mask) != 0x0L) {
            this.writeFieldName(jsonWriter);
            jsonWriter.writeNull();
            return true;
        }
        return false;
    }
    
    @Override
    public Function getFunction() {
        return this.function;
    }
}
