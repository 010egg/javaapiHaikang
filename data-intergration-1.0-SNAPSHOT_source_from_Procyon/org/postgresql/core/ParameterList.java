// 
// Decompiled by Procyon v0.5.36
// 

package org.postgresql.core;

import java.io.InputStream;
import java.sql.SQLException;

public interface ParameterList
{
    void registerOutParameter(final int p0, final int p1) throws SQLException;
    
    int getParameterCount();
    
    int getInParameterCount();
    
    int getOutParameterCount();
    
    int[] getTypeOIDs();
    
    void setIntParameter(final int p0, final int p1) throws SQLException;
    
    void setLiteralParameter(final int p0, final String p1, final int p2) throws SQLException;
    
    void setStringParameter(final int p0, final String p1, final int p2) throws SQLException;
    
    void setBytea(final int p0, final byte[] p1, final int p2, final int p3) throws SQLException;
    
    void setBytea(final int p0, final InputStream p1, final int p2) throws SQLException;
    
    void setBytea(final int p0, final InputStream p1) throws SQLException;
    
    void setBinaryParameter(final int p0, final byte[] p1, final int p2) throws SQLException;
    
    void setNull(final int p0, final int p1) throws SQLException;
    
    ParameterList copy();
    
    void clear();
    
    String toString(final int p0);
}