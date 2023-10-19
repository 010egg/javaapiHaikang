// 
// Decompiled by Procyon v0.5.36
// 

package com.fasterxml.jackson.core.json;

import com.fasterxml.jackson.core.util.VersionUtil;
import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.core.Versioned;

public final class PackageVersion implements Versioned
{
    public static final Version VERSION;
    
    @Override
    public Version version() {
        return PackageVersion.VERSION;
    }
    
    static {
        VERSION = VersionUtil.parseVersion("2.9.0", "com.fasterxml.jackson.core", "jackson-core");
    }
}
