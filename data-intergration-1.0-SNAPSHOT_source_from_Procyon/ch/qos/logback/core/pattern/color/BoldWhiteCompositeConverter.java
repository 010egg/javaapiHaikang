// 
// Decompiled by Procyon v0.5.36
// 

package ch.qos.logback.core.pattern.color;

public class BoldWhiteCompositeConverter<E> extends ForegroundCompositeConverterBase<E>
{
    @Override
    protected String getForegroundColorCode(final E event) {
        return "1;37";
    }
}
