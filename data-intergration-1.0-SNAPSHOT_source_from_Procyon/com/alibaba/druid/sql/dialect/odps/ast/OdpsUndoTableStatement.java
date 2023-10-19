// 
// Decompiled by Procyon v0.5.36
// 

package com.alibaba.druid.sql.dialect.odps.ast;

import com.alibaba.druid.sql.ast.SQLName;
import com.alibaba.druid.sql.ast.SQLObject;
import com.alibaba.druid.sql.visitor.SQLASTVisitor;
import com.alibaba.druid.sql.dialect.odps.visitor.OdpsASTVisitor;
import com.alibaba.druid.DbType;
import java.util.ArrayList;
import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.statement.SQLAssignItem;
import java.util.List;
import com.alibaba.druid.sql.ast.statement.SQLExprTableSource;
import com.alibaba.druid.sql.ast.statement.SQLAlterStatement;

public class OdpsUndoTableStatement extends OdpsStatementImpl implements SQLAlterStatement
{
    private SQLExprTableSource table;
    private List<SQLAssignItem> partitions;
    private SQLExpr to;
    
    public OdpsUndoTableStatement() {
        this.partitions = new ArrayList<SQLAssignItem>();
        super.dbType = DbType.odps;
    }
    
    @Override
    protected void accept0(final OdpsASTVisitor visitor) {
        if (visitor.visit(this)) {
            this.acceptChild(visitor, this.table);
            this.acceptChild(visitor, this.partitions);
            this.acceptChild(visitor, this.to);
        }
        visitor.endVisit(this);
    }
    
    public SQLExprTableSource getTable() {
        return this.table;
    }
    
    public void setTable(final SQLExprTableSource table) {
        if (table != null) {
            table.setParent(table);
        }
        this.table = table;
    }
    
    public void setTable(final SQLName table) {
        this.setTable(new SQLExprTableSource(table));
    }
    
    public SQLExpr getTo() {
        return this.to;
    }
    
    public void setTo(final SQLExpr to) {
        this.to = to;
    }
    
    public List<SQLAssignItem> getPartitions() {
        return this.partitions;
    }
}
