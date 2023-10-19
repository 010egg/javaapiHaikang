// 
// Decompiled by Procyon v0.5.36
// 

package com.alibaba.druid.filter.logging;

import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

public class Slf4jLogFilter extends LogFilter
{
    private Logger dataSourceLogger;
    private Logger connectionLogger;
    private Logger statementLogger;
    private Logger resultSetLogger;
    
    public Slf4jLogFilter() {
        this.dataSourceLogger = LoggerFactory.getLogger(this.dataSourceLoggerName);
        this.connectionLogger = LoggerFactory.getLogger(this.connectionLoggerName);
        this.statementLogger = LoggerFactory.getLogger(this.statementLoggerName);
        this.resultSetLogger = LoggerFactory.getLogger(this.resultSetLoggerName);
    }
    
    @Override
    public String getDataSourceLoggerName() {
        return this.dataSourceLoggerName;
    }
    
    @Override
    public void setDataSourceLoggerName(final String dataSourceLoggerName) {
        this.dataSourceLoggerName = dataSourceLoggerName;
        this.dataSourceLogger = LoggerFactory.getLogger(dataSourceLoggerName);
    }
    
    public void setDataSourceLogger(final Logger dataSourceLogger) {
        this.dataSourceLogger = dataSourceLogger;
        this.dataSourceLoggerName = dataSourceLogger.getName();
    }
    
    @Override
    public String getConnectionLoggerName() {
        return this.connectionLoggerName;
    }
    
    @Override
    public void setConnectionLoggerName(final String connectionLoggerName) {
        this.connectionLoggerName = connectionLoggerName;
        this.connectionLogger = LoggerFactory.getLogger(connectionLoggerName);
    }
    
    public void setConnectionLogger(final Logger connectionLogger) {
        this.connectionLogger = connectionLogger;
        this.connectionLoggerName = connectionLogger.getName();
    }
    
    @Override
    public String getStatementLoggerName() {
        return this.statementLoggerName;
    }
    
    @Override
    public void setStatementLoggerName(final String statementLoggerName) {
        this.statementLoggerName = statementLoggerName;
        this.statementLogger = LoggerFactory.getLogger(statementLoggerName);
    }
    
    public void setStatementLogger(final Logger statementLogger) {
        this.statementLogger = statementLogger;
        this.statementLoggerName = statementLogger.getName();
    }
    
    @Override
    public String getResultSetLoggerName() {
        return this.resultSetLoggerName;
    }
    
    @Override
    public void setResultSetLoggerName(final String resultSetLoggerName) {
        this.resultSetLoggerName = resultSetLoggerName;
        this.resultSetLogger = LoggerFactory.getLogger(resultSetLoggerName);
    }
    
    public void setResultSetLogger(final Logger resultSetLogger) {
        this.resultSetLogger = resultSetLogger;
        this.resultSetLoggerName = resultSetLogger.getName();
    }
    
    @Override
    public boolean isConnectionLogErrorEnabled() {
        return this.connectionLogger.isErrorEnabled() && super.isConnectionLogErrorEnabled();
    }
    
    @Override
    public boolean isDataSourceLogEnabled() {
        return this.dataSourceLogger.isDebugEnabled() && super.isDataSourceLogEnabled();
    }
    
    @Override
    public boolean isConnectionLogEnabled() {
        return this.connectionLogger.isDebugEnabled() && super.isConnectionLogEnabled();
    }
    
    @Override
    public boolean isStatementLogEnabled() {
        return this.statementLogger.isDebugEnabled() && super.isStatementLogEnabled();
    }
    
    @Override
    public boolean isResultSetLogEnabled() {
        return this.resultSetLogger.isDebugEnabled() && super.isResultSetLogEnabled();
    }
    
    @Override
    public boolean isResultSetLogErrorEnabled() {
        return this.resultSetLogger.isErrorEnabled() && super.isResultSetLogErrorEnabled();
    }
    
    @Override
    public boolean isStatementLogErrorEnabled() {
        return this.statementLogger.isErrorEnabled() && super.isStatementLogErrorEnabled();
    }
    
    @Override
    protected void connectionLog(final String message) {
        this.connectionLogger.debug(message);
    }
    
    @Override
    protected void statementLog(final String message) {
        this.statementLogger.debug(message);
    }
    
    @Override
    protected void resultSetLog(final String message) {
        this.resultSetLogger.debug(message);
    }
    
    @Override
    protected void resultSetLogError(final String message, final Throwable error) {
        this.resultSetLogger.error(message, error);
    }
    
    @Override
    protected void statementLogError(final String message, final Throwable error) {
        this.statementLogger.error(message, error);
    }
}
