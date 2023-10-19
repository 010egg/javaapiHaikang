// 
// Decompiled by Procyon v0.5.36
// 

package com.alibaba.druid.sql.ast.statement;

import com.alibaba.druid.sql.ast.SQLObject;
import com.alibaba.druid.sql.visitor.SQLASTVisitor;
import java.util.ArrayList;
import com.alibaba.druid.sql.ast.SQLName;
import java.util.List;
import com.alibaba.druid.sql.ast.SQLObjectImpl;

public class SQLAlterTableReplaceColumn extends SQLObjectImpl implements SQLAlterTableItem
{
    private final List<SQLColumnDefinition> columns;
    private SQLName firstColumn;
    private SQLName afterColumn;
    private boolean first;
    
    public SQLAlterTableReplaceColumn() {
        this.columns = new ArrayList<SQLColumnDefinition>();
    }
    
    @Override
    protected void accept0(final SQLASTVisitor visitor) {
        if (visitor.visit(this)) {
            this.acceptChild(visitor, this.columns);
        }
        visitor.endVisit(this);
    }
    
    public List<SQLColumnDefinition> getColumns() {
        return this.columns;
    }
    
    public void addColumn(final SQLColumnDefinition column) {
        if (column != null) {
            column.setParent(this);
        }
        this.columns.add(column);
    }
    
    public SQLName getFirstColumn() {
        return this.firstColumn;
    }
    
    public void setFirstColumn(final SQLName first) {
        this.firstColumn = first;
    }
    
    public boolean isFirst() {
        return this.first;
    }
    
    public void setFirst(final boolean first) {
        this.first = first;
    }
    
    public SQLName getAfterColumn() {
        return this.afterColumn;
    }
    
    public void setAfterColumn(final SQLName after) {
        this.afterColumn = after;
    }
}
