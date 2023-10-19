// 
// Decompiled by Procyon v0.5.36
// 

package com.alibaba.fastjson2.reader;

import com.alibaba.fastjson2.util.Fnv;
import java.util.Arrays;
import com.alibaba.fastjson2.JSONReader;
import java.util.function.Function;
import java.util.Iterator;
import com.alibaba.fastjson2.JSONException;
import java.lang.reflect.Type;
import com.alibaba.fastjson2.JSONFactory;
import java.util.Collection;

class ObjectReaderImplInt16Array extends ObjectReaderPrimitive
{
    static final ObjectReaderImplInt16Array INSTANCE;
    static final long HASH_TYPE;
    
    ObjectReaderImplInt16Array() {
        super(Short[].class);
    }
    
    @Override
    public Object createInstance(final Collection collection) {
        final Short[] array = new Short[collection.size()];
        int i = 0;
        for (final Object item : collection) {
            Short value;
            if (item == null) {
                value = null;
            }
            else if (item instanceof Number) {
                value = ((Number)item).shortValue();
            }
            else {
                final Function typeConvert = JSONFactory.getDefaultObjectReaderProvider().getTypeConvert(item.getClass(), Short.class);
                if (typeConvert == null) {
                    throw new JSONException("can not cast to Short " + item.getClass());
                }
                value = typeConvert.apply(item);
            }
            array[i++] = value;
        }
        return array;
    }
    
    @Override
    public Object readObject(final JSONReader jsonReader, final Type fieldType, final Object fieldName, final long features) {
        if (jsonReader.readIfNull()) {
            return null;
        }
        if (jsonReader.nextIfArrayStart()) {
            Short[] values = new Short[16];
            int size = 0;
            while (!jsonReader.nextIfArrayEnd()) {
                if (jsonReader.isEnd()) {
                    throw new JSONException(jsonReader.info("input end"));
                }
                final int minCapacity = size + 1;
                if (minCapacity - values.length > 0) {
                    final int oldCapacity = values.length;
                    int newCapacity = oldCapacity + (oldCapacity >> 1);
                    if (newCapacity - minCapacity < 0) {
                        newCapacity = minCapacity;
                    }
                    values = Arrays.copyOf(values, newCapacity);
                }
                final Integer i = jsonReader.readInt32();
                values[size++] = (short)((i == null) ? 0 : i.shortValue());
            }
            jsonReader.nextIfComma();
            return Arrays.copyOf(values, size);
        }
        if (!jsonReader.isString()) {
            throw new JSONException(jsonReader.info("TODO"));
        }
        final String str = jsonReader.readString();
        if (str.isEmpty()) {
            return null;
        }
        throw new JSONException(jsonReader.info("not support input " + str));
    }
    
    @Override
    public Object readJSONBObject(final JSONReader jsonReader, final Type fieldType, final Object fieldName, final long features) {
        if (jsonReader.nextIfMatch((byte)(-110))) {
            final long typeHash = jsonReader.readTypeHashCode();
            if (typeHash != ObjectReaderImplInt16Array.HASH_TYPE && typeHash != ObjectReaderImplInt16ValueArray.HASH_TYPE) {
                throw new JSONException(jsonReader.info("not support type " + jsonReader.getString()));
            }
        }
        final int entryCnt = jsonReader.startArray();
        if (entryCnt == -1) {
            return null;
        }
        final Short[] array = new Short[entryCnt];
        for (int i = 0; i < entryCnt; ++i) {
            final Integer integer = jsonReader.readInt32();
            array[i] = ((integer == null) ? null : Short.valueOf(integer.shortValue()));
        }
        return array;
    }
    
    static {
        INSTANCE = new ObjectReaderImplInt16Array();
        HASH_TYPE = Fnv.hashCode64("[Short");
    }
}
