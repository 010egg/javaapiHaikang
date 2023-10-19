// 
// Decompiled by Procyon v0.5.36
// 

package com.alibaba.druid.sql.ast.expr;

import com.alibaba.druid.sql.ast.SQLExpr;
import java.util.Collections;
import java.util.List;
import com.alibaba.druid.sql.visitor.SQLASTVisitor;
import com.alibaba.druid.sql.ast.SQLObject;
import com.alibaba.druid.sql.ast.statement.SQLSelect;
import java.io.Serializable;
import com.alibaba.druid.sql.ast.SQLExprImpl;

public final class SQLExistsExpr extends SQLExprImpl implements Serializable
{
    private static final long serialVersionUID = 1L;
    public boolean not;
    public SQLSelect subQuery;
    
    public SQLExistsExpr() {
        this.not = false;
    }
    
    public SQLExistsExpr(final SQLSelect subQuery) {
        this.not = false;
        this.setSubQuery(subQuery);
    }
    
    public SQLExistsExpr(final SQLSelect subQuery, final boolean not) {
        this.not = false;
        this.setSubQuery(subQuery);
        this.not = not;
    }
    
    public boolean isNot() {
        return this.not;
    }
    
    public void setNot(final boolean not) {
        this.not = not;
    }
    
    public SQLSelect getSubQuery() {
        return this.subQuery;
    }
    
    public void setSubQuery(final SQLSelect subQuery) {
        if (subQuery != null) {
            subQuery.setParent(this);
        }
        this.subQuery = subQuery;
    }
    
    @Override
    protected void accept0(final SQLASTVisitor visitor) {
        if (visitor.visit(this) && this.subQuery != null) {
            this.subQuery.accept(visitor);
        }
        visitor.endVisit(this);
    }
    
    @Override
    public List getChildren() {
        return Collections.singletonList(this.subQuery);
    }
    
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = 31 * result + (this.not ? 1231 : 1237);
        result = 31 * result + ((this.subQuery == null) ? 0 : this.subQuery.hashCode());
        return result;
    }
    
    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (this.getClass() != obj.getClass()) {
            return false;
        }
        final SQLExistsExpr other = (SQLExistsExpr)obj;
        if (this.not != other.not) {
            return false;
        }
        if (this.subQuery == null) {
            if (other.subQuery != null) {
                return false;
            }
        }
        else if (!this.subQuery.equals(other.subQuery)) {
            return false;
        }
        return true;
    }
    
    @Override
    public SQLExistsExpr clone() {
        final SQLExistsExpr x = new SQLExistsExpr();
        x.not = this.not;
        if (this.subQuery != null) {
            x.setSubQuery(this.subQuery.clone());
        }
        return x;
    }
}
