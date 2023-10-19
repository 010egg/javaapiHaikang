// 
// Decompiled by Procyon v0.5.36
// 

package com.alibaba.druid.sql.ast.statement;

import com.alibaba.druid.sql.ast.SQLObject;
import com.alibaba.druid.sql.visitor.SQLASTVisitor;
import com.alibaba.druid.sql.ast.SQLName;
import com.alibaba.druid.sql.ast.SQLObjectImpl;

public class SQLAlterTableDropClusteringKey extends SQLObjectImpl implements SQLAlterTableItem
{
    private SQLName keyName;
    
    public SQLName getKeyName() {
        return this.keyName;
    }
    
    public void setKeyName(final SQLName keyName) {
        this.keyName = keyName;
    }
    
    @Override
    protected void accept0(final SQLASTVisitor visitor) {
        if (visitor.visit(this)) {
            this.acceptChild(visitor, this.keyName);
        }
        visitor.endVisit(this);
    }
}
