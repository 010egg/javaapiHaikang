// 
// Decompiled by Procyon v0.5.36
// 

package org.postgresql.jdbc3;

import java.sql.Array;
import java.sql.Clob;
import java.sql.Blob;
import java.sql.Ref;
import java.util.Map;
import java.util.Calendar;
import java.io.Reader;
import java.io.InputStream;
import java.sql.Timestamp;
import java.sql.Time;
import java.sql.Date;
import java.math.BigDecimal;
import org.postgresql.core.ResultHandler;
import java.sql.ParameterMetaData;
import org.postgresql.Driver;
import java.net.URL;
import org.postgresql.core.Utils;
import org.postgresql.util.PSQLException;
import org.postgresql.util.PSQLState;
import org.postgresql.util.GT;
import org.postgresql.core.BaseConnection;
import java.util.List;
import java.util.ArrayList;
import org.postgresql.core.Field;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.postgresql.jdbc2.AbstractJdbc2Connection;
import org.postgresql.jdbc2.AbstractJdbc2Statement;

public abstract class AbstractJdbc3Statement extends AbstractJdbc2Statement
{
    private final int rsHoldability;
    
    public AbstractJdbc3Statement(final AbstractJdbc3Connection c, final int rsType, final int rsConcurrency, final int rsHoldability) throws SQLException {
        super(c, rsType, rsConcurrency);
        this.rsHoldability = rsHoldability;
    }
    
    public AbstractJdbc3Statement(final AbstractJdbc3Connection connection, final String sql, final boolean isCallable, final int rsType, final int rsConcurrency, final int rsHoldability) throws SQLException {
        super(connection, sql, isCallable, rsType, rsConcurrency);
        this.rsHoldability = rsHoldability;
    }
    
    @Override
    public boolean getMoreResults(final int current) throws SQLException {
        if (current == 1 && this.result != null && this.result.getResultSet() != null) {
            this.result.getResultSet().close();
        }
        if (this.result != null) {
            this.result = this.result.getNext();
        }
        if (current == 3) {
            while (this.firstUnclosedResult != this.result) {
                if (this.firstUnclosedResult.getResultSet() != null) {
                    this.firstUnclosedResult.getResultSet().close();
                }
                this.firstUnclosedResult = this.firstUnclosedResult.getNext();
            }
        }
        return this.result != null && this.result.getResultSet() != null;
    }
    
    @Override
    public ResultSet getGeneratedKeys() throws SQLException {
        this.checkClosed();
        if (this.generatedKeys == null || this.generatedKeys.getResultSet() == null) {
            return this.createDriverResultSet(new Field[0], new ArrayList());
        }
        return this.generatedKeys.getResultSet();
    }
    
    @Override
    public int executeUpdate(String sql, final int autoGeneratedKeys) throws SQLException {
        if (autoGeneratedKeys == 2) {
            return this.executeUpdate(sql);
        }
        sql = addReturning(this.connection, sql, new String[] { "*" }, false);
        this.wantsGeneratedKeysOnce = true;
        return this.executeUpdate(sql);
    }
    
    static String addReturning(final BaseConnection connection, String sql, final String[] columns, final boolean escape) throws SQLException {
        if (!connection.haveMinimumServerVersion("8.2")) {
            throw new PSQLException(GT.tr("Returning autogenerated keys is only supported for 8.2 and later servers."), PSQLState.NOT_IMPLEMENTED);
        }
        sql = sql.trim();
        if (sql.endsWith(";")) {
            sql = sql.substring(0, sql.length() - 1);
        }
        final StringBuilder sb = new StringBuilder(sql);
        sb.append(" RETURNING ");
        for (int i = 0; i < columns.length; ++i) {
            if (i != 0) {
                sb.append(", ");
            }
            if (escape) {
                Utils.escapeIdentifier(sb, columns[i]);
            }
            else {
                sb.append(columns[i]);
            }
        }
        return sb.toString();
    }
    
    @Override
    public int executeUpdate(final String sql, final int[] columnIndexes) throws SQLException {
        if (columnIndexes == null || columnIndexes.length == 0) {
            return this.executeUpdate(sql);
        }
        throw new PSQLException(GT.tr("Returning autogenerated keys by column index is not supported."), PSQLState.NOT_IMPLEMENTED);
    }
    
    @Override
    public int executeUpdate(String sql, final String[] columnNames) throws SQLException {
        if (columnNames == null || columnNames.length == 0) {
            return this.executeUpdate(sql);
        }
        sql = addReturning(this.connection, sql, columnNames, true);
        this.wantsGeneratedKeysOnce = true;
        return this.executeUpdate(sql);
    }
    
    @Override
    public boolean execute(String sql, final int autoGeneratedKeys) throws SQLException {
        if (autoGeneratedKeys == 2) {
            return this.execute(sql);
        }
        sql = addReturning(this.connection, sql, new String[] { "*" }, false);
        this.wantsGeneratedKeysOnce = true;
        return this.execute(sql);
    }
    
    @Override
    public boolean execute(final String sql, final int[] columnIndexes) throws SQLException {
        if (columnIndexes == null || columnIndexes.length == 0) {
            return this.execute(sql);
        }
        throw new PSQLException(GT.tr("Returning autogenerated keys by column index is not supported."), PSQLState.NOT_IMPLEMENTED);
    }
    
    @Override
    public boolean execute(String sql, final String[] columnNames) throws SQLException {
        if (columnNames == null || columnNames.length == 0) {
            return this.execute(sql);
        }
        sql = addReturning(this.connection, sql, columnNames, true);
        this.wantsGeneratedKeysOnce = true;
        return this.execute(sql);
    }
    
    @Override
    public int getResultSetHoldability() throws SQLException {
        return this.rsHoldability;
    }
    
    public void setURL(final int parameterIndex, final URL x) throws SQLException {
        throw Driver.notImplemented(this.getClass(), "setURL(int,URL)");
    }
    
    public ParameterMetaData getParameterMetaData() throws SQLException {
        final int flags = 49;
        final StatementResultHandler handler = new StatementResultHandler();
        this.connection.getQueryExecutor().execute(this.preparedQuery, this.preparedParameters, handler, 0, 0, flags);
        final int[] oids = this.preparedParameters.getTypeOIDs();
        if (oids != null) {
            return this.createParameterMetaData(this.connection, oids);
        }
        return null;
    }
    
    public abstract ParameterMetaData createParameterMetaData(final BaseConnection p0, final int[] p1) throws SQLException;
    
    public void registerOutParameter(final String parameterName, final int sqlType) throws SQLException {
        throw Driver.notImplemented(this.getClass(), "registerOutParameter(String,int)");
    }
    
    public void registerOutParameter(final String parameterName, final int sqlType, final int scale) throws SQLException {
        throw Driver.notImplemented(this.getClass(), "registerOutParameter(String,int,int)");
    }
    
    public void registerOutParameter(final String parameterName, final int sqlType, final String typeName) throws SQLException {
        throw Driver.notImplemented(this.getClass(), "registerOutParameter(String,int,String)");
    }
    
    public URL getURL(final int parameterIndex) throws SQLException {
        throw Driver.notImplemented(this.getClass(), "getURL(String)");
    }
    
    public void setURL(final String parameterName, final URL val) throws SQLException {
        throw Driver.notImplemented(this.getClass(), "setURL(String,URL)");
    }
    
    public void setNull(final String parameterName, final int sqlType) throws SQLException {
        throw Driver.notImplemented(this.getClass(), "setNull(String,int)");
    }
    
    public void setBoolean(final String parameterName, final boolean x) throws SQLException {
        throw Driver.notImplemented(this.getClass(), "setBoolean(String,boolean)");
    }
    
    public void setByte(final String parameterName, final byte x) throws SQLException {
        throw Driver.notImplemented(this.getClass(), "setByte(String,byte)");
    }
    
    public void setShort(final String parameterName, final short x) throws SQLException {
        throw Driver.notImplemented(this.getClass(), "setShort(String,short)");
    }
    
    public void setInt(final String parameterName, final int x) throws SQLException {
        throw Driver.notImplemented(this.getClass(), "setInt(String,int)");
    }
    
    public void setLong(final String parameterName, final long x) throws SQLException {
        throw Driver.notImplemented(this.getClass(), "setLong(String,long)");
    }
    
    public void setFloat(final String parameterName, final float x) throws SQLException {
        throw Driver.notImplemented(this.getClass(), "setFloat(String,float)");
    }
    
    public void setDouble(final String parameterName, final double x) throws SQLException {
        throw Driver.notImplemented(this.getClass(), "setDouble(String,double)");
    }
    
    public void setBigDecimal(final String parameterName, final BigDecimal x) throws SQLException {
        throw Driver.notImplemented(this.getClass(), "setBigDecimal(String,BigDecimal)");
    }
    
    public void setString(final String parameterName, final String x) throws SQLException {
        throw Driver.notImplemented(this.getClass(), "setString(String,String)");
    }
    
    public void setBytes(final String parameterName, final byte[] x) throws SQLException {
        throw Driver.notImplemented(this.getClass(), "setBytes(String,byte)");
    }
    
    public void setDate(final String parameterName, final Date x) throws SQLException {
        throw Driver.notImplemented(this.getClass(), "setDate(String,Date)");
    }
    
    public void setTime(final String parameterName, final Time x) throws SQLException {
        throw Driver.notImplemented(this.getClass(), "setTime(String,Time)");
    }
    
    public void setTimestamp(final String parameterName, final Timestamp x) throws SQLException {
        throw Driver.notImplemented(this.getClass(), "setTimestamp(String,Timestamp)");
    }
    
    public void setAsciiStream(final String parameterName, final InputStream x, final int length) throws SQLException {
        throw Driver.notImplemented(this.getClass(), "setAsciiStream(String,InputStream,int)");
    }
    
    public void setBinaryStream(final String parameterName, final InputStream x, final int length) throws SQLException {
        throw Driver.notImplemented(this.getClass(), "setBinaryStream(String,InputStream,int)");
    }
    
    public void setObject(final String parameterName, final Object x, final int targetSqlType, final int scale) throws SQLException {
        throw Driver.notImplemented(this.getClass(), "setObject(String,Object,int,int)");
    }
    
    public void setObject(final String parameterName, final Object x, final int targetSqlType) throws SQLException {
        throw Driver.notImplemented(this.getClass(), "setObject(String,Object,int)");
    }
    
    public void setObject(final String parameterName, final Object x) throws SQLException {
        throw Driver.notImplemented(this.getClass(), "setObject(String,Object)");
    }
    
    public void setCharacterStream(final String parameterName, final Reader reader, final int length) throws SQLException {
        throw Driver.notImplemented(this.getClass(), "setCharacterStream(String,Reader,int)");
    }
    
    public void setDate(final String parameterName, final Date x, final Calendar cal) throws SQLException {
        throw Driver.notImplemented(this.getClass(), "setDate(String,Date,Calendar)");
    }
    
    public void setTime(final String parameterName, final Time x, final Calendar cal) throws SQLException {
        throw Driver.notImplemented(this.getClass(), "setTime(String,Time,Calendar)");
    }
    
    public void setTimestamp(final String parameterName, final Timestamp x, final Calendar cal) throws SQLException {
        throw Driver.notImplemented(this.getClass(), "setTimestamp(String,Timestamp,Calendar)");
    }
    
    public void setNull(final String parameterName, final int sqlType, final String typeName) throws SQLException {
        throw Driver.notImplemented(this.getClass(), "setNull(String,int,String)");
    }
    
    public String getString(final String parameterName) throws SQLException {
        throw Driver.notImplemented(this.getClass(), "getString(String)");
    }
    
    public boolean getBoolean(final String parameterName) throws SQLException {
        throw Driver.notImplemented(this.getClass(), "getBoolean(String)");
    }
    
    public byte getByte(final String parameterName) throws SQLException {
        throw Driver.notImplemented(this.getClass(), "getByte(String)");
    }
    
    public short getShort(final String parameterName) throws SQLException {
        throw Driver.notImplemented(this.getClass(), "getShort(String)");
    }
    
    public int getInt(final String parameterName) throws SQLException {
        throw Driver.notImplemented(this.getClass(), "getInt(String)");
    }
    
    public long getLong(final String parameterName) throws SQLException {
        throw Driver.notImplemented(this.getClass(), "getLong(String)");
    }
    
    public float getFloat(final String parameterName) throws SQLException {
        throw Driver.notImplemented(this.getClass(), "getFloat(String)");
    }
    
    public double getDouble(final String parameterName) throws SQLException {
        throw Driver.notImplemented(this.getClass(), "getDouble(String)");
    }
    
    public byte[] getBytes(final String parameterName) throws SQLException {
        throw Driver.notImplemented(this.getClass(), "getBytes(String)");
    }
    
    public Date getDate(final String parameterName) throws SQLException {
        throw Driver.notImplemented(this.getClass(), "getDate(String)");
    }
    
    public Time getTime(final String parameterName) throws SQLException {
        throw Driver.notImplemented(this.getClass(), "getTime(String)");
    }
    
    public Timestamp getTimestamp(final String parameterName) throws SQLException {
        throw Driver.notImplemented(this.getClass(), "getTimestamp(String)");
    }
    
    public Object getObject(final String parameterName) throws SQLException {
        throw Driver.notImplemented(this.getClass(), "getObject(String)");
    }
    
    public BigDecimal getBigDecimal(final String parameterName) throws SQLException {
        throw Driver.notImplemented(this.getClass(), "getBigDecimal(String)");
    }
    
    public Object getObjectImpl(final String parameterName, final Map map) throws SQLException {
        throw Driver.notImplemented(this.getClass(), "getObject(String,Map)");
    }
    
    public Ref getRef(final String parameterName) throws SQLException {
        throw Driver.notImplemented(this.getClass(), "getRef(String)");
    }
    
    public Blob getBlob(final String parameterName) throws SQLException {
        throw Driver.notImplemented(this.getClass(), "getBlob(String)");
    }
    
    public Clob getClob(final String parameterName) throws SQLException {
        throw Driver.notImplemented(this.getClass(), "getClob(String)");
    }
    
    public Array getArray(final String parameterName) throws SQLException {
        throw Driver.notImplemented(this.getClass(), "getArray(String)");
    }
    
    public Date getDate(final String parameterName, final Calendar cal) throws SQLException {
        throw Driver.notImplemented(this.getClass(), "getDate(String,Calendar)");
    }
    
    public Time getTime(final String parameterName, final Calendar cal) throws SQLException {
        throw Driver.notImplemented(this.getClass(), "getTime(String,Calendar)");
    }
    
    public Timestamp getTimestamp(final String parameterName, final Calendar cal) throws SQLException {
        throw Driver.notImplemented(this.getClass(), "getTimestamp(String,Calendar)");
    }
    
    public URL getURL(final String parameterName) throws SQLException {
        throw Driver.notImplemented(this.getClass(), "getURL(String)");
    }
    
    @Override
    public void setObject(final int parameterIndex, final Object x, int targetSqlType, final int scale) throws SQLException {
        if (targetSqlType == 16) {
            targetSqlType = -7;
        }
        super.setObject(parameterIndex, x, targetSqlType, scale);
    }
    
    @Override
    public void setNull(final int parameterIndex, int sqlType) throws SQLException {
        if (sqlType == 16) {
            sqlType = -7;
        }
        super.setNull(parameterIndex, sqlType);
    }
    
    @Override
    protected boolean wantsHoldableResultSet() {
        return this.rsHoldability == 1;
    }
    
    public void registerOutParameter(final int parameterIndex, int sqlType) throws SQLException {
        switch (sqlType) {
            case 16: {
                sqlType = -7;
                break;
            }
        }
        super.registerOutParameter(parameterIndex, sqlType, !this.adjustIndex);
    }
    
    public void registerOutParameter(final int parameterIndex, final int sqlType, final int scale) throws SQLException {
        this.registerOutParameter(parameterIndex, sqlType);
    }
}