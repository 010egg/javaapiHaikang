// 
// Decompiled by Procyon v0.5.36
// 

package com.alibaba.fastjson2.util;

import java.security.AccessController;
import java.util.Objects;
import com.alibaba.fastjson2.reader.ByteArrayValueConsumer;
import com.alibaba.fastjson2.reader.CharArrayValueConsumer;
import java.time.ZonedDateTime;
import java.time.LocalDateTime;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.Instant;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.Calendar;
import java.util.Date;
import java.util.OptionalLong;
import java.util.OptionalInt;
import java.util.Optional;
import java.util.EnumSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.Collection;
import com.alibaba.fastjson2.PropertyNamingStrategy;
import com.alibaba.fastjson2.writer.ObjectWriterAdapter;
import com.alibaba.fastjson2.writer.ObjectWriter12;
import com.alibaba.fastjson2.writer.ObjectWriter11;
import com.alibaba.fastjson2.writer.ObjectWriter10;
import com.alibaba.fastjson2.writer.ObjectWriter9;
import com.alibaba.fastjson2.writer.ObjectWriter8;
import com.alibaba.fastjson2.writer.ObjectWriter7;
import com.alibaba.fastjson2.writer.ObjectWriter6;
import com.alibaba.fastjson2.writer.ObjectWriter5;
import com.alibaba.fastjson2.writer.ObjectWriter4;
import com.alibaba.fastjson2.writer.ObjectWriter3;
import com.alibaba.fastjson2.writer.ObjectWriter2;
import com.alibaba.fastjson2.writer.ObjectWriter1;
import com.alibaba.fastjson2.writer.ObjectWriter;
import com.alibaba.fastjson2.filter.ValueFilter;
import com.alibaba.fastjson2.filter.NameFilter;
import com.alibaba.fastjson2.filter.PropertyFilter;
import com.alibaba.fastjson2.filter.PropertyPreFilter;
import com.alibaba.fastjson2.writer.FieldWriter;
import com.alibaba.fastjson2.JSONWriter;
import com.alibaba.fastjson2.reader.ObjectReaderAdapter;
import com.alibaba.fastjson2.reader.ObjectReader12;
import com.alibaba.fastjson2.reader.ObjectReader11;
import com.alibaba.fastjson2.reader.ObjectReader10;
import com.alibaba.fastjson2.reader.ObjectReader9;
import com.alibaba.fastjson2.reader.ObjectReader8;
import com.alibaba.fastjson2.reader.ObjectReader7;
import com.alibaba.fastjson2.reader.ObjectReader6;
import com.alibaba.fastjson2.reader.ObjectReader5;
import com.alibaba.fastjson2.reader.ObjectReader4;
import com.alibaba.fastjson2.reader.ObjectReader3;
import com.alibaba.fastjson2.reader.ObjectReader2;
import com.alibaba.fastjson2.reader.ObjectReader1;
import com.alibaba.fastjson2.reader.ObjectReader;
import com.alibaba.fastjson2.reader.FieldReader;
import com.alibaba.fastjson2.JSONReader;
import java.lang.reflect.Method;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.net.URL;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;
import java.security.ProtectionDomain;

public class DynamicClassLoader extends ClassLoader
{
    private static final ProtectionDomain DOMAIN;
    private static final Map<String, Class<?>> classMapping;
    private static final DynamicClassLoader instance;
    private final Map<String, Class> classes;
    
    public DynamicClassLoader() {
        this(getParentClassLoader());
    }
    
    public DynamicClassLoader(final ClassLoader parent) {
        super(parent);
        this.classes = new ConcurrentHashMap<String, Class>();
    }
    
    static ClassLoader getParentClassLoader() {
        final ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();
        if (contextClassLoader != null) {
            try {
                contextClassLoader.loadClass(DynamicClassLoader.class.getName());
                return contextClassLoader;
            }
            catch (ClassNotFoundException ex) {}
        }
        return DynamicClassLoader.class.getClassLoader();
    }
    
    @Override
    protected Class<?> loadClass(final String name, final boolean resolve) throws ClassNotFoundException {
        final Class<?> mappingClass = DynamicClassLoader.classMapping.get(name);
        if (mappingClass != null) {
            return mappingClass;
        }
        final Class clazz = this.classes.get(name);
        if (clazz != null) {
            return (Class<?>)clazz;
        }
        ClassNotFoundException error = null;
        try {
            return super.loadClass(name, resolve);
        }
        catch (ClassNotFoundException e) {
            error = e;
            final ClassLoader tcl = Thread.currentThread().getContextClassLoader();
            if (tcl != null && tcl != this) {
                try {
                    return tcl.loadClass(name);
                }
                catch (ClassNotFoundException ex) {}
            }
            throw error;
        }
    }
    
    public void definePackage(final String name) throws ClassFormatError {
        if (this.getPackage(name) != null) {
            return;
        }
        super.definePackage(name, "", "", "", "", "", "", null);
    }
    
    public Class<?> loadClass(final String name, final byte[] b, final int off, final int len) throws ClassFormatError {
        final Class<?> clazz = this.defineClass(name, b, off, len, DynamicClassLoader.DOMAIN);
        this.classes.put(name, clazz);
        return clazz;
    }
    
    public Class<?> defineClassPublic(final String name, final byte[] b, final int off, final int len) throws ClassFormatError {
        return this.defineClass(name, b, off, len, DynamicClassLoader.DOMAIN);
    }
    
    public boolean isExternalClass(final Class<?> clazz) {
        final ClassLoader classLoader = clazz.getClassLoader();
        if (classLoader == null) {
            return false;
        }
        for (ClassLoader current = this; current != null; current = current.getParent()) {
            if (current == classLoader) {
                return false;
            }
        }
        return true;
    }
    
    public static DynamicClassLoader getInstance() {
        return DynamicClassLoader.instance;
    }
    
    static {
        classMapping = new HashMap<String, Class<?>>();
        instance = new DynamicClassLoader();
        final Class[] array;
        final Class[] classes = array = new Class[] { Object.class, Type.class, Field.class, Method.class, Fnv.class, JSONReader.class, FieldReader.class, ObjectReader.class, ObjectReader1.class, ObjectReader2.class, ObjectReader3.class, ObjectReader4.class, ObjectReader5.class, ObjectReader6.class, ObjectReader6.class, ObjectReader7.class, ObjectReader8.class, ObjectReader9.class, ObjectReader10.class, ObjectReader11.class, ObjectReader12.class, ObjectReaderAdapter.class, JSONWriter.class, JSONWriter.Context.class, FieldWriter.class, PropertyPreFilter.class, PropertyFilter.class, NameFilter.class, ValueFilter.class, ObjectWriter.class, ObjectWriter1.class, ObjectWriter2.class, ObjectWriter3.class, ObjectWriter4.class, ObjectWriter5.class, ObjectWriter6.class, ObjectWriter7.class, ObjectWriter8.class, ObjectWriter9.class, ObjectWriter10.class, ObjectWriter11.class, ObjectWriter12.class, ObjectWriterAdapter.class, JDKUtils.class, TypeUtils.class, DateUtils.class, PropertyNamingStrategy.class, Collection.class, Set.class, List.class, ArrayList.class, LinkedList.class, Map.class, HashMap.class, LinkedHashMap.class, EnumSet.class, Optional.class, OptionalInt.class, OptionalLong.class, Date.class, Calendar.class, ConcurrentHashMap.class, Supplier.class, Consumer.class, Exception.class, Enum.class, Class.class, Boolean.class, Byte.class, Short.class, Integer.class, Long.class, Float.class, Double.class, String.class, BigInteger.class, BigDecimal.class, Instant.class, LocalTime.class, LocalDate.class, LocalDateTime.class, ZonedDateTime.class, CharArrayValueConsumer.class, ByteArrayValueConsumer.class };
        for (final Class clazz : array) {
            DynamicClassLoader.classMapping.put(clazz.getName(), clazz);
        }
        final String[] array2;
        final String[] strings = array2 = new String[] { "sun.misc.Unsafe", "java.sql.Timestamp", "java.sql.Date" };
        for (final String string : array2) {
            try {
                final Class<?> c = Class.forName(string);
                DynamicClassLoader.classMapping.put(string, c);
            }
            catch (ClassNotFoundException ex) {}
        }
        final Class<DynamicClassLoader> obj = DynamicClassLoader.class;
        Objects.requireNonNull(obj);
        DOMAIN = AccessController.doPrivileged(obj::getProtectionDomain);
    }
}
