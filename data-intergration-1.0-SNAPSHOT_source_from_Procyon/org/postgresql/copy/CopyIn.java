// 
// Decompiled by Procyon v0.5.36
// 

package org.postgresql.copy;

import java.sql.SQLException;

public interface CopyIn extends CopyOperation
{
    void writeToCopy(final byte[] p0, final int p1, final int p2) throws SQLException;
    
    void flushCopy() throws SQLException;
    
    long endCopy() throws SQLException;
}
