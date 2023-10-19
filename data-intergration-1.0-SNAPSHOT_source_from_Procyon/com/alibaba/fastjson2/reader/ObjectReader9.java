// 
// Decompiled by Procyon v0.5.36
// 

package com.alibaba.fastjson2.reader;

import com.alibaba.fastjson2.schema.JSONSchema;
import java.util.function.Function;
import java.util.function.Supplier;

public class ObjectReader9<T> extends ObjectReaderAdapter<T>
{
    protected final FieldReader fieldReader0;
    protected final FieldReader fieldReader1;
    protected final FieldReader fieldReader2;
    protected final FieldReader fieldReader3;
    protected final FieldReader fieldReader4;
    protected final FieldReader fieldReader5;
    protected final FieldReader fieldReader6;
    protected final FieldReader fieldReader7;
    protected final FieldReader fieldReader8;
    final long hashCode0;
    final long hashCode1;
    final long hashCode2;
    final long hashCode3;
    final long hashCode4;
    final long hashCode5;
    final long hashCode6;
    final long hashCode7;
    final long hashCode8;
    final long hashCode0LCase;
    final long hashCode1LCase;
    final long hashCode2LCase;
    final long hashCode3LCase;
    final long hashCode4LCase;
    final long hashCode5LCase;
    final long hashCode6LCase;
    final long hashCode7LCase;
    final long hashCode8LCase;
    protected ObjectReader objectReader0;
    protected ObjectReader objectReader1;
    protected ObjectReader objectReader2;
    protected ObjectReader objectReader3;
    protected ObjectReader objectReader4;
    protected ObjectReader objectReader5;
    protected ObjectReader objectReader6;
    protected ObjectReader objectReader7;
    protected ObjectReader objectReader8;
    
    public ObjectReader9(final Class objectClass, final String typeKey, final String typeName, final long features, final Supplier<T> creator, final Function buildFunction, final FieldReader... fieldReaders) {
        this(objectClass, typeKey, typeName, features, null, creator, buildFunction, (FieldReader[])fieldReaders);
    }
    
    public ObjectReader9(final Class objectClass, final String typeKey, final String typeName, final long features, final JSONSchema schema, final Supplier<T> creator, final Function buildFunction, final FieldReader... fieldReaders) {
        super(objectClass, typeKey, typeName, features, schema, creator, buildFunction, fieldReaders);
        this.fieldReader0 = fieldReaders[0];
        this.fieldReader1 = fieldReaders[1];
        this.fieldReader2 = fieldReaders[2];
        this.fieldReader3 = fieldReaders[3];
        this.fieldReader4 = fieldReaders[4];
        this.fieldReader5 = fieldReaders[5];
        this.fieldReader6 = fieldReaders[6];
        this.fieldReader7 = fieldReaders[7];
        this.fieldReader8 = fieldReaders[8];
        this.hashCode0 = this.fieldReader0.fieldNameHash;
        this.hashCode1 = this.fieldReader1.fieldNameHash;
        this.hashCode2 = this.fieldReader2.fieldNameHash;
        this.hashCode3 = this.fieldReader3.fieldNameHash;
        this.hashCode4 = this.fieldReader4.fieldNameHash;
        this.hashCode5 = this.fieldReader5.fieldNameHash;
        this.hashCode6 = this.fieldReader6.fieldNameHash;
        this.hashCode7 = this.fieldReader7.fieldNameHash;
        this.hashCode8 = this.fieldReader8.fieldNameHash;
        this.hashCode0LCase = this.fieldReader0.fieldNameHashLCase;
        this.hashCode1LCase = this.fieldReader1.fieldNameHashLCase;
        this.hashCode2LCase = this.fieldReader2.fieldNameHashLCase;
        this.hashCode3LCase = this.fieldReader3.fieldNameHashLCase;
        this.hashCode4LCase = this.fieldReader4.fieldNameHashLCase;
        this.hashCode5LCase = this.fieldReader5.fieldNameHashLCase;
        this.hashCode6LCase = this.fieldReader6.fieldNameHashLCase;
        this.hashCode7LCase = this.fieldReader7.fieldNameHashLCase;
        this.hashCode8LCase = this.fieldReader8.fieldNameHashLCase;
    }
    
    @Override
    public FieldReader getFieldReader(final long hashCode) {
        if (hashCode == this.hashCode0) {
            return this.fieldReader0;
        }
        if (hashCode == this.hashCode1) {
            return this.fieldReader1;
        }
        if (hashCode == this.hashCode2) {
            return this.fieldReader2;
        }
        if (hashCode == this.hashCode3) {
            return this.fieldReader3;
        }
        if (hashCode == this.hashCode4) {
            return this.fieldReader4;
        }
        if (hashCode == this.hashCode5) {
            return this.fieldReader5;
        }
        if (hashCode == this.hashCode6) {
            return this.fieldReader6;
        }
        if (hashCode == this.hashCode7) {
            return this.fieldReader7;
        }
        if (hashCode == this.hashCode8) {
            return this.fieldReader8;
        }
        return null;
    }
    
    @Override
    public FieldReader getFieldReaderLCase(final long hashCode) {
        if (hashCode == this.hashCode0LCase) {
            return this.fieldReader0;
        }
        if (hashCode == this.hashCode1LCase) {
            return this.fieldReader1;
        }
        if (hashCode == this.hashCode2LCase) {
            return this.fieldReader2;
        }
        if (hashCode == this.hashCode3LCase) {
            return this.fieldReader3;
        }
        if (hashCode == this.hashCode4LCase) {
            return this.fieldReader4;
        }
        if (hashCode == this.hashCode5LCase) {
            return this.fieldReader5;
        }
        if (hashCode == this.hashCode6LCase) {
            return this.fieldReader6;
        }
        if (hashCode == this.hashCode7LCase) {
            return this.fieldReader7;
        }
        if (hashCode == this.hashCode8LCase) {
            return this.fieldReader8;
        }
        return null;
    }
}
