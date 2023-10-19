// 
// Decompiled by Procyon v0.5.36
// 

package com.alibaba.druid.sql.visitor.functions;

import java.util.List;
import com.alibaba.druid.sql.ast.expr.SQLValuableExpr;
import com.alibaba.druid.sql.visitor.SQLASTVisitor;
import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.expr.SQLMethodInvokeExpr;
import com.alibaba.druid.sql.visitor.SQLEvalVisitor;

public class Lcase implements Function
{
    public static final Lcase instance;
    
    @Override
    public Object eval(final SQLEvalVisitor visitor, final SQLMethodInvokeExpr x) {
        if (x.getArguments().size() != 1) {
            return SQLEvalVisitor.EVAL_ERROR;
        }
        final SQLExpr param0 = x.getArguments().get(0);
        param0.accept(visitor);
        final Object param0Value = param0.getAttributes().get("eval.value");
        if (param0Value == null) {
            return SQLEvalVisitor.EVAL_ERROR;
        }
        final String strValue = param0Value.toString();
        final String result = strValue.toLowerCase();
        return result;
    }
    
    public Object eval(final SQLMethodInvokeExpr x) {
        final List<SQLExpr> arguments = x.getArguments();
        if (arguments.size() == 1) {
            final Object p0 = arguments.get(0).getValue();
            if (p0 instanceof String) {
                final String str = (String)p0;
                return str.toLowerCase();
            }
        }
        return SQLEvalVisitor.EVAL_ERROR;
    }
    
    static {
        instance = new Lcase();
    }
}
