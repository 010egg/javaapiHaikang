// 
// Decompiled by Procyon v0.5.36
// 

package com.alibaba.druid.util;

import java.util.HashSet;
import java.util.Set;

public class OdpsUtils
{
    private static Set<String> builtinDataTypes;
    
    public static boolean isBuiltinDataType(final String dataType) {
        if (dataType == null) {
            return false;
        }
        final String table_lower = dataType.toLowerCase();
        Set<String> dataTypes = OdpsUtils.builtinDataTypes;
        if (dataTypes == null) {
            dataTypes = new HashSet<String>();
            loadDataTypes(dataTypes);
            OdpsUtils.builtinDataTypes = dataTypes;
        }
        return dataTypes.contains(table_lower);
    }
    
    public static void loadDataTypes(final Set<String> dataTypes) {
        Utils.loadFromFile("META-INF/druid/parser/odps/builtin_datatypes", dataTypes);
    }
}
