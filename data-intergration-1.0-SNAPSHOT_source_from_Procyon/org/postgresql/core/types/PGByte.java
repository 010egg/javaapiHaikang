// 
// Decompiled by Procyon v0.5.36
// 

package org.postgresql.core.types;

import org.postgresql.util.PSQLException;
import org.postgresql.util.PSQLState;
import org.postgresql.util.GT;
import java.math.BigDecimal;

public class PGByte implements PGType
{
    private Byte val;
    
    public PGByte(final Byte x) {
        this.val = x;
    }
    
    public static PGType castToServerType(final Byte val, final int targetType) throws PSQLException {
        try {
            switch (targetType) {
                case -7: {
                    return new PGBoolean((val == 0) ? Boolean.FALSE : Boolean.TRUE);
                }
                case -6:
                case 5: {
                    return new PGByte(val);
                }
                case 7: {
                    return new PGFloat(new Float(val));
                }
                case 6:
                case 8: {
                    return new PGDouble(new Double(val));
                }
                case 2:
                case 3: {
                    return new PGBigDecimal(new BigDecimal(val.toString()));
                }
                case -1:
                case 12: {
                    return new PGString(val.toString());
                }
                default: {
                    return new PGUnknown(val);
                }
            }
        }
        catch (Exception ex) {
            throw new PSQLException(GT.tr("Cannot convert an instance of {0} to type {1}", new Object[] { val.getClass().getName(), "Types.OTHER" }), PSQLState.INVALID_PARAMETER_TYPE, ex);
        }
    }
    
    @Override
    public String toString() {
        return this.val.toString();
    }
}
