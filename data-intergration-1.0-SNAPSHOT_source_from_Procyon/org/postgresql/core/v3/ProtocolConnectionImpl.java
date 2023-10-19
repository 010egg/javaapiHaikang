// 
// Decompiled by Procyon v0.5.36
// 

package org.postgresql.core.v3;

import java.util.Collection;
import org.postgresql.core.Encoding;
import java.io.IOException;
import org.postgresql.core.QueryExecutor;
import java.sql.SQLException;
import org.postgresql.PGNotification;
import org.postgresql.core.Utils;
import org.postgresql.util.HostSpec;
import java.util.HashSet;
import java.util.Properties;
import org.postgresql.core.Logger;
import org.postgresql.core.PGStream;
import java.util.ArrayList;
import java.sql.SQLWarning;
import java.util.Set;
import org.postgresql.core.ProtocolConnection;

class ProtocolConnectionImpl implements ProtocolConnection
{
    private boolean integerDateTimes;
    private final Set<Integer> useBinaryForOids;
    private String serverVersion;
    private int serverVersionNum;
    private int cancelPid;
    private int cancelKey;
    private boolean standardConformingStrings;
    private int transactionState;
    private SQLWarning warnings;
    private boolean closed;
    private final ArrayList notifications;
    private final PGStream pgStream;
    private final String user;
    private final String database;
    private final QueryExecutorImpl executor;
    private final Logger logger;
    private final int connectTimeout;
    
    ProtocolConnectionImpl(final PGStream pgStream, final String user, final String database, final Properties info, final Logger logger, final int connectTimeout) {
        this.useBinaryForOids = new HashSet<Integer>();
        this.serverVersionNum = 0;
        this.closed = false;
        this.notifications = new ArrayList();
        this.pgStream = pgStream;
        this.user = user;
        this.database = database;
        this.logger = logger;
        this.executor = new QueryExecutorImpl(this, pgStream, info, logger);
        this.standardConformingStrings = false;
        this.connectTimeout = connectTimeout;
    }
    
    @Override
    public HostSpec getHostSpec() {
        return this.pgStream.getHostSpec();
    }
    
    @Override
    public String getUser() {
        return this.user;
    }
    
    @Override
    public String getDatabase() {
        return this.database;
    }
    
    @Override
    public String getServerVersion() {
        return this.serverVersion;
    }
    
    @Override
    public int getServerVersionNum() {
        if (this.serverVersionNum != 0) {
            return this.serverVersionNum;
        }
        return Utils.parseServerVersionStr(this.serverVersion);
    }
    
    @Override
    public synchronized boolean getStandardConformingStrings() {
        return this.standardConformingStrings;
    }
    
    @Override
    public synchronized int getTransactionState() {
        return this.transactionState;
    }
    
    @Override
    public synchronized PGNotification[] getNotifications() throws SQLException {
        final PGNotification[] array = this.notifications.toArray(new PGNotification[this.notifications.size()]);
        this.notifications.clear();
        return array;
    }
    
    @Override
    public synchronized SQLWarning getWarnings() {
        final SQLWarning chain = this.warnings;
        this.warnings = null;
        return chain;
    }
    
    @Override
    public QueryExecutor getQueryExecutor() {
        return this.executor;
    }
    
    @Override
    public void sendQueryCancel() throws SQLException {
        PGStream cancelStream = null;
        try {
            if (this.logger.logDebug()) {
                this.logger.debug(" FE=> CancelRequest(pid=" + this.cancelPid + ",ckey=" + this.cancelKey + ")");
            }
            cancelStream = new PGStream(this.pgStream.getHostSpec(), this.connectTimeout);
            cancelStream.SendInteger4(16);
            cancelStream.SendInteger2(1234);
            cancelStream.SendInteger2(5678);
            cancelStream.SendInteger4(this.cancelPid);
            cancelStream.SendInteger4(this.cancelKey);
            cancelStream.flush();
            cancelStream.ReceiveEOF();
            cancelStream.close();
            cancelStream = null;
        }
        catch (IOException e) {
            if (this.logger.logDebug()) {
                this.logger.debug("Ignoring exception on cancel request:", e);
            }
        }
        finally {
            if (cancelStream != null) {
                try {
                    cancelStream.close();
                }
                catch (IOException ex) {}
            }
        }
    }
    
    @Override
    public void close() {
        if (this.closed) {
            return;
        }
        try {
            if (this.logger.logDebug()) {
                this.logger.debug(" FE=> Terminate");
            }
            this.pgStream.SendChar(88);
            this.pgStream.SendInteger4(4);
            this.pgStream.flush();
            this.pgStream.close();
        }
        catch (IOException ioe) {
            if (this.logger.logDebug()) {
                this.logger.debug("Discarding IOException on close:", ioe);
            }
        }
        this.closed = true;
    }
    
    @Override
    public Encoding getEncoding() {
        return this.pgStream.getEncoding();
    }
    
    @Override
    public boolean isClosed() {
        return this.closed;
    }
    
    void setServerVersion(final String serverVersion) {
        this.serverVersion = serverVersion;
    }
    
    void setServerVersionNum(final int serverVersionNum) {
        this.serverVersionNum = serverVersionNum;
    }
    
    void setBackendKeyData(final int cancelPid, final int cancelKey) {
        this.cancelPid = cancelPid;
        this.cancelKey = cancelKey;
    }
    
    synchronized void addWarning(final SQLWarning newWarning) {
        if (this.warnings == null) {
            this.warnings = newWarning;
        }
        else {
            this.warnings.setNextWarning(newWarning);
        }
    }
    
    synchronized void addNotification(final PGNotification notification) {
        this.notifications.add(notification);
    }
    
    synchronized void setTransactionState(final int state) {
        this.transactionState = state;
    }
    
    synchronized void setStandardConformingStrings(final boolean value) {
        this.standardConformingStrings = value;
    }
    
    @Override
    public int getProtocolVersion() {
        return 3;
    }
    
    @Override
    public int getBackendPID() {
        return this.cancelPid;
    }
    
    public boolean useBinaryForReceive(final int oid) {
        return this.useBinaryForOids.contains(oid);
    }
    
    @Override
    public void setBinaryReceiveOids(final Set oids) {
        this.useBinaryForOids.clear();
        this.useBinaryForOids.addAll(oids);
    }
    
    public void setIntegerDateTimes(final boolean state) {
        this.integerDateTimes = state;
    }
    
    @Override
    public boolean getIntegerDateTimes() {
        return this.integerDateTimes;
    }
    
    @Override
    public void abort() {
        try {
            this.pgStream.getSocket().close();
        }
        catch (IOException ex) {}
        this.closed = true;
    }
}
