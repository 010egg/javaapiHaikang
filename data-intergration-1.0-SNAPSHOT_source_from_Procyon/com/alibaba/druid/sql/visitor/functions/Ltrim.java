// 
// Decompiled by Procyon v0.5.36
// 

package com.alibaba.druid.sql.visitor.functions;

import com.alibaba.druid.sql.visitor.SQLASTVisitor;
import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.expr.SQLMethodInvokeExpr;
import com.alibaba.druid.sql.visitor.SQLEvalVisitor;

public class Ltrim implements Function
{
    public static final Ltrim instance;
    
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
        int index = -1;
        for (int i = 0; i < strValue.length(); ++i) {
            if (!Character.isWhitespace(strValue.charAt(i))) {
                index = i;
                break;
            }
        }
        if (index <= 0) {
            return strValue;
        }
        return strValue.substring(index);
    }
    
    static {
        instance = new Ltrim();
    }
}
