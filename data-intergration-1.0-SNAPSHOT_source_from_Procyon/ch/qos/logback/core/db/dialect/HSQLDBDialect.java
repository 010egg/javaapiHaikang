// 
// Decompiled by Procyon v0.5.36
// 

package ch.qos.logback.core.db.dialect;

public class HSQLDBDialect implements SQLDialect
{
    public static final String SELECT_CURRVAL = "CALL IDENTITY()";
    
    @Override
    public String getSelectInsertId() {
        return "CALL IDENTITY()";
    }
}
