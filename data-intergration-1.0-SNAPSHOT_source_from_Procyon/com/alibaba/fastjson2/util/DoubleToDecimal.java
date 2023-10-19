// 
// Decompiled by Procyon v0.5.36
// 

package com.alibaba.fastjson2.util;

import java.lang.invoke.CallSite;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.LambdaMetafactory;
import java.lang.invoke.MethodType;

public final class DoubleToDecimal
{
    static final long INFINITY;
    static final long INFI;
    static final long NITY;
    private static final long[] G;
    static final long[] pow10;
    static final LongBiFunction MULTIPLY_HIGH;
    
    public static int toString(final double v, final byte[] bytes, final int off, final boolean json) {
        final long C_TINY = 3L;
        final long C_MIN = 4503599627370496L;
        final int BQ_MASK = 2047;
        final long T_MASK = 4503599627370495L;
        final long bits = Double.doubleToRawLongBits(v);
        final long t = bits & 0xFFFFFFFFFFFFFL;
        final int bq = (int)(bits >>> 52) & 0x7FF;
        if (bq >= 2047) {
            int index = off;
            if (json) {
                JDKUtils.UNSAFE.putInt(bytes, JDKUtils.ARRAY_BYTE_BASE_OFFSET + index, IOUtils.NULL_32);
                index += 4;
            }
            else if (t != 0L) {
                bytes[index] = 78;
                bytes[index + 1] = 97;
                bytes[index + 2] = 78;
                index += 3;
            }
            else {
                if (bits <= 0L) {
                    bytes[index++] = 45;
                }
                JDKUtils.UNSAFE.putLong(bytes, JDKUtils.ARRAY_BYTE_BASE_OFFSET + index, DoubleToDecimal.INFINITY);
                index += 8;
            }
            return index - off;
        }
        int index = off - 1;
        if (bits < 0L) {
            bytes[++index] = 45;
        }
        if (bq == 0 && t == 0L) {
            index = off - 1;
            if (bits != 0L) {
                bytes[++index] = 45;
            }
            bytes[index + 1] = 48;
            bytes[index + 2] = 46;
            bytes[index + 3] = 48;
            return index + 4 - off;
        }
        boolean _stat = false;
        long f = 0L;
        int e = 0;
        int q = 0;
        int dk = 0;
        long c;
        if (bq != 0) {
            final int mq = 1075 - bq;
            c = (0x10000000000000L | t);
            if (0 < mq & mq < 53) {
                f = c >> mq;
                if (f << mq == c) {
                    _stat = true;
                }
            }
            if (!_stat) {
                q = -mq;
                dk = 0;
            }
        }
        else {
            q = -1074;
            if (t < 3L) {
                c = 10L * t;
                dk = -1;
            }
            else {
                c = t;
                dk = 0;
            }
        }
        if (!_stat) {
            final int out = (int)c & 0x1;
            final long cb = c << 2;
            final long cbr = cb + 2L;
            long cbl;
            int k;
            if (c != 4503599627370496L | q == -1074) {
                cbl = cb - 2L;
                k = (int)(q * 661971961083L >> 41);
            }
            else {
                cbl = cb - 1L;
                k = (int)(q * 661971961083L - 274743187321L >> 41);
            }
            final int h = q + (int)(-k * 913124641741L >> 38) + 2;
            final long g1 = DoubleToDecimal.G[k + 324 << 1];
            final long g2 = DoubleToDecimal.G[k + 324 << 1 | 0x1];
            final long cp = cb << h;
            final long z = (g1 * cp >>> 1) + DoubleToDecimal.MULTIPLY_HIGH.multiplyHigh(g2, cp);
            final long vbp = DoubleToDecimal.MULTIPLY_HIGH.multiplyHigh(g1, cp) + (z >>> 63);
            final long vb = vbp | (z & Long.MAX_VALUE) + Long.MAX_VALUE >>> 63;
            final long cp2 = cbl << h;
            final long z2 = (g1 * cp2 >>> 1) + DoubleToDecimal.MULTIPLY_HIGH.multiplyHigh(g2, cp2);
            final long vbp2 = DoubleToDecimal.MULTIPLY_HIGH.multiplyHigh(g1, cp2) + (z2 >>> 63);
            final long vbl = vbp2 | (z2 & Long.MAX_VALUE) + Long.MAX_VALUE >>> 63;
            final long cp3 = cbr << h;
            final long z3 = (g1 * cp3 >>> 1) + DoubleToDecimal.MULTIPLY_HIGH.multiplyHigh(g2, cp3);
            final long vbp3 = DoubleToDecimal.MULTIPLY_HIGH.multiplyHigh(g1, cp3) + (z3 >>> 63);
            final long vbr = vbp3 | (z3 & Long.MAX_VALUE) + Long.MAX_VALUE >>> 63;
            final long s = vb >> 2;
            if (s >= 100L) {
                final long sp10 = 10L * DoubleToDecimal.MULTIPLY_HIGH.multiplyHigh(s, 1844674407370955168L);
                final long tp10 = sp10 + 10L;
                final boolean upin = vbl + out <= sp10 << 2;
                final boolean wpin = (tp10 << 2) + out <= vbr;
                if (upin != wpin) {
                    f = (upin ? sp10 : tp10);
                    e = k;
                    _stat = true;
                }
            }
            if (!_stat) {
                final long t2 = s + 1L;
                final boolean uin = vbl + out <= s << 2;
                final boolean win = (t2 << 2) + out <= vbr;
                if (uin != win) {
                    f = (uin ? s : t2);
                }
                else {
                    final long cmp = vb - (s + t2 << 1);
                    f = ((cmp < 0L || (cmp == 0L && (s & 0x1L) == 0x0L)) ? s : t2);
                }
                e = k + dk;
            }
        }
        int len = (int)((64 - Long.numberOfLeadingZeros(f)) * 661971961083L >> 41);
        if (f >= DoubleToDecimal.pow10[len]) {
            ++len;
        }
        final int H = 17;
        f *= DoubleToDecimal.pow10[17 - len];
        e += len;
        final long hm = DoubleToDecimal.MULTIPLY_HIGH.multiplyHigh(f, 193428131138340668L) >>> 20;
        final int l = (int)(f - 100000000L * hm);
        final int h2 = (int)(hm * 1441151881L >>> 57);
        final int m = (int)(hm - 100000000 * h2);
        if (0 < e && e <= 7) {
            final int MASK_28 = 268435455;
            bytes[++index] = (byte)(48 + h2);
            int y = (int)(DoubleToDecimal.MULTIPLY_HIGH.multiplyHigh((long)(m + 1) << 28, 193428131138340668L) >>> 20) - 1;
            int i;
            for (i = 1; i < e; ++i) {
                final int t3 = 10 * y;
                bytes[++index] = (byte)(48 + (t3 >>> 28));
                y = (t3 & 0xFFFFFFF);
            }
            bytes[++index] = 46;
            while (i <= 8) {
                final int t3 = 10 * y;
                bytes[++index] = (byte)(48 + (t3 >>> 28));
                y = (t3 & 0xFFFFFFF);
                ++i;
            }
            if (l != 0) {
                y = (int)(DoubleToDecimal.MULTIPLY_HIGH.multiplyHigh((long)(l + 1) << 28, 193428131138340668L) >>> 20) - 1;
                for (int j = 0; j < 8; ++j) {
                    final int t4 = 10 * y;
                    bytes[++index] = (byte)(48 + (t4 >>> 28));
                    y = (t4 & 0xFFFFFFF);
                }
            }
            while (bytes[index] == 48) {
                --index;
            }
            if (bytes[index] == 46) {
                ++index;
            }
            return index + 1 - off;
        }
        if (-3 < e && e <= 0) {
            bytes[1 + index] = 48;
            bytes[2 + index] = 46;
            index += 2;
            while (e < 0) {
                bytes[++index] = 48;
                ++e;
            }
            bytes[++index] = (byte)(48 + h2);
            final int MASK_28 = 268435455;
            int y = (int)(DoubleToDecimal.MULTIPLY_HIGH.multiplyHigh((long)(m + 1) << 28, 193428131138340668L) >>> 20) - 1;
            for (int j = 0; j < 8; ++j) {
                final int t4 = 10 * y;
                bytes[++index] = (byte)(48 + (t4 >>> 28));
                y = (t4 & 0xFFFFFFF);
            }
            if (l != 0) {
                y = (int)(DoubleToDecimal.MULTIPLY_HIGH.multiplyHigh((long)(l + 1) << 28, 193428131138340668L) >>> 20) - 1;
                for (int j = 0; j < 8; ++j) {
                    final int t4 = 10 * y;
                    bytes[++index] = (byte)(48 + (t4 >>> 28));
                    y = (t4 & 0xFFFFFFF);
                }
            }
            while (bytes[index] == 48) {
                --index;
            }
            if (bytes[index] == 46) {
                ++index;
            }
            return index + 1 - off;
        }
        bytes[1 + index] = (byte)(48 + h2);
        bytes[2 + index] = 46;
        index += 2;
        final int MASK_28 = 268435455;
        int y = (int)(DoubleToDecimal.MULTIPLY_HIGH.multiplyHigh((long)(m + 1) << 28, 193428131138340668L) >>> 20) - 1;
        for (int j = 0; j < 8; ++j) {
            final int t4 = 10 * y;
            bytes[++index] = (byte)(48 + (t4 >>> 28));
            y = (t4 & 0xFFFFFFF);
        }
        if (l != 0) {
            y = (int)(DoubleToDecimal.MULTIPLY_HIGH.multiplyHigh((long)(l + 1) << 28, 193428131138340668L) >>> 20) - 1;
            for (int j = 0; j < 8; ++j) {
                final int t4 = 10 * y;
                bytes[++index] = (byte)(48 + (t4 >>> 28));
                y = (t4 & 0xFFFFFFF);
            }
        }
        while (bytes[index] == 48) {
            --index;
        }
        if (bytes[index] == 46) {
            ++index;
        }
        int e2 = e - 1;
        bytes[++index] = 69;
        if (e2 < 0) {
            bytes[++index] = 45;
            e2 = -e2;
        }
        if (e2 < 10) {
            bytes[++index] = (byte)(48 + e2);
        }
        else {
            if (e2 >= 100) {
                final int d = e2 * 1311 >>> 17;
                bytes[++index] = (byte)(48 + d);
                e2 -= 100 * d;
            }
            final int d = e2 * 103 >>> 10;
            bytes[1 + index] = (byte)(48 + d);
            bytes[2 + index] = (byte)(48 + (e2 - 10 * d));
            index += 2;
        }
        return index + 1 - off;
    }
    
    public static int toString(final double v, final char[] chars, final int off, final boolean json) {
        final int P = 53;
        final int Q_MIN = -1074;
        final long C_TINY = 3L;
        final long C_MIN = 4503599627370496L;
        final int BQ_MASK = 2047;
        final long T_MASK = 4503599627370495L;
        final long bits = Double.doubleToRawLongBits(v);
        final long t = bits & 0xFFFFFFFFFFFFFL;
        final int bq = (int)(bits >>> 52) & 0x7FF;
        if (bq >= 2047) {
            int index = off;
            if (json) {
                JDKUtils.UNSAFE.putLong(chars, JDKUtils.ARRAY_BYTE_BASE_OFFSET + (index << 1), IOUtils.NULL_64);
                index += 4;
            }
            else if (t != 0L) {
                chars[index] = 'N';
                chars[index + 1] = 'a';
                chars[index + 2] = 'N';
                index += 3;
            }
            else {
                if (bits <= 0L) {
                    chars[index++] = '-';
                }
                JDKUtils.UNSAFE.putLong(chars, JDKUtils.ARRAY_CHAR_BASE_OFFSET + (index << 1), DoubleToDecimal.INFI);
                JDKUtils.UNSAFE.putLong(chars, JDKUtils.ARRAY_CHAR_BASE_OFFSET + (index + 4 << 1), DoubleToDecimal.NITY);
                index += 8;
            }
            return index - off;
        }
        int index = off - 1;
        if (bits < 0L) {
            chars[++index] = '-';
        }
        if (bq == 0 && t == 0L) {
            index = off - 1;
            if (bits != 0L) {
                chars[++index] = '-';
            }
            chars[1 + index] = '0';
            chars[2 + index] = '.';
            chars[3 + index] = '0';
            return index + 4 - off;
        }
        boolean _stat = false;
        long f = 0L;
        int e = 0;
        int q = 0;
        int dk = 0;
        long c;
        if (bq != 0) {
            final int mq = 1075 - bq;
            c = (0x10000000000000L | t);
            if (0 < mq & mq < 53) {
                f = c >> mq;
                if (f << mq == c) {
                    _stat = true;
                }
            }
            if (!_stat) {
                q = -mq;
                dk = 0;
            }
        }
        else {
            q = -1074;
            if (t < 3L) {
                c = 10L * t;
                dk = -1;
            }
            else {
                c = t;
                dk = 0;
            }
        }
        if (!_stat) {
            final int out = (int)c & 0x1;
            final long cb = c << 2;
            final long cbr = cb + 2L;
            long cbl;
            int k;
            if (c != 4503599627370496L | q == -1074) {
                cbl = cb - 2L;
                k = (int)(q * 661971961083L >> 41);
            }
            else {
                cbl = cb - 1L;
                k = (int)(q * 661971961083L - 274743187321L >> 41);
            }
            final int h = q + (int)(-k * 913124641741L >> 38) + 2;
            final long g1 = DoubleToDecimal.G[k + 324 << 1];
            final long g2 = DoubleToDecimal.G[k + 324 << 1 | 0x1];
            final long cp = cb << h;
            final long z = (g1 * cp >>> 1) + DoubleToDecimal.MULTIPLY_HIGH.multiplyHigh(g2, cp);
            final long vbp = DoubleToDecimal.MULTIPLY_HIGH.multiplyHigh(g1, cp) + (z >>> 63);
            final long vb = vbp | (z & Long.MAX_VALUE) + Long.MAX_VALUE >>> 63;
            final long cp2 = cbl << h;
            final long z2 = (g1 * cp2 >>> 1) + DoubleToDecimal.MULTIPLY_HIGH.multiplyHigh(g2, cp2);
            final long vbp2 = DoubleToDecimal.MULTIPLY_HIGH.multiplyHigh(g1, cp2) + (z2 >>> 63);
            final long vbl = vbp2 | (z2 & Long.MAX_VALUE) + Long.MAX_VALUE >>> 63;
            final long cp3 = cbr << h;
            final long z3 = (g1 * cp3 >>> 1) + DoubleToDecimal.MULTIPLY_HIGH.multiplyHigh(g2, cp3);
            final long vbp3 = DoubleToDecimal.MULTIPLY_HIGH.multiplyHigh(g1, cp3) + (z3 >>> 63);
            final long vbr = vbp3 | (z3 & Long.MAX_VALUE) + Long.MAX_VALUE >>> 63;
            final long s = vb >> 2;
            if (s >= 100L) {
                final long sp10 = 10L * DoubleToDecimal.MULTIPLY_HIGH.multiplyHigh(s, 1844674407370955168L);
                final long tp10 = sp10 + 10L;
                final boolean upin = vbl + out <= sp10 << 2;
                final boolean wpin = (tp10 << 2) + out <= vbr;
                if (upin != wpin) {
                    f = (upin ? sp10 : tp10);
                    e = k;
                    _stat = true;
                }
            }
            if (!_stat) {
                final long t2 = s + 1L;
                final boolean uin = vbl + out <= s << 2;
                final boolean win = (t2 << 2) + out <= vbr;
                if (uin != win) {
                    f = (uin ? s : t2);
                }
                else {
                    final long cmp = vb - (s + t2 << 1);
                    f = ((cmp < 0L || (cmp == 0L && (s & 0x1L) == 0x0L)) ? s : t2);
                }
                e = k + dk;
            }
        }
        int len = (int)((64 - Long.numberOfLeadingZeros(f)) * 661971961083L >> 41);
        if (f >= DoubleToDecimal.pow10[len]) {
            ++len;
        }
        final int H = 17;
        f *= DoubleToDecimal.pow10[17 - len];
        e += len;
        final long hm = DoubleToDecimal.MULTIPLY_HIGH.multiplyHigh(f, 193428131138340668L) >>> 20;
        final int l = (int)(f - 100000000L * hm);
        final int h2 = (int)(hm * 1441151881L >>> 57);
        final int m = (int)(hm - 100000000 * h2);
        if (0 < e && e <= 7) {
            final int MASK_28 = 268435455;
            chars[++index] = (char)(48 + h2);
            int y = (int)(DoubleToDecimal.MULTIPLY_HIGH.multiplyHigh((long)(m + 1) << 28, 193428131138340668L) >>> 20) - 1;
            int i;
            for (i = 1; i < e; ++i) {
                final int t3 = 10 * y;
                chars[++index] = (char)(48 + (t3 >>> 28));
                y = (t3 & 0xFFFFFFF);
            }
            chars[++index] = '.';
            while (i <= 8) {
                final int t3 = 10 * y;
                chars[++index] = (char)(48 + (t3 >>> 28));
                y = (t3 & 0xFFFFFFF);
                ++i;
            }
            if (l != 0) {
                y = (int)(DoubleToDecimal.MULTIPLY_HIGH.multiplyHigh((long)(l + 1) << 28, 193428131138340668L) >>> 20) - 1;
                for (int j = 0; j < 8; ++j) {
                    final int t4 = 10 * y;
                    chars[++index] = (char)(48 + (t4 >>> 28));
                    y = (t4 & 0xFFFFFFF);
                }
            }
            while (chars[index] == '0') {
                --index;
            }
            if (chars[index] == '.') {
                ++index;
            }
            return index + 1 - off;
        }
        if (-3 < e && e <= 0) {
            chars[1 + index] = '0';
            chars[2 + index] = '.';
            index += 2;
            while (e < 0) {
                chars[++index] = '0';
                ++e;
            }
            chars[++index] = (char)(48 + h2);
            final int MASK_28 = 268435455;
            int y = (int)(DoubleToDecimal.MULTIPLY_HIGH.multiplyHigh((long)(m + 1) << 28, 193428131138340668L) >>> 20) - 1;
            for (int j = 0; j < 8; ++j) {
                final int t4 = 10 * y;
                chars[++index] = (char)(48 + (t4 >>> 28));
                y = (t4 & 0xFFFFFFF);
            }
            if (l != 0) {
                y = (int)(DoubleToDecimal.MULTIPLY_HIGH.multiplyHigh((long)(l + 1) << 28, 193428131138340668L) >>> 20) - 1;
                for (int j = 0; j < 8; ++j) {
                    final int t4 = 10 * y;
                    chars[++index] = (char)(48 + (t4 >>> 28));
                    y = (t4 & 0xFFFFFFF);
                }
            }
            while (chars[index] == '0') {
                --index;
            }
            if (chars[index] == '.') {
                ++index;
            }
            return index + 1 - off;
        }
        chars[1 + index] = (char)(48 + h2);
        chars[2 + index] = '.';
        index += 2;
        final int MASK_28 = 268435455;
        int y = (int)(DoubleToDecimal.MULTIPLY_HIGH.multiplyHigh((long)(m + 1) << 28, 193428131138340668L) >>> 20) - 1;
        for (int j = 0; j < 8; ++j) {
            final int t4 = 10 * y;
            chars[++index] = (char)(48 + (t4 >>> 28));
            y = (t4 & 0xFFFFFFF);
        }
        if (l != 0) {
            y = (int)(DoubleToDecimal.MULTIPLY_HIGH.multiplyHigh((long)(l + 1) << 28, 193428131138340668L) >>> 20) - 1;
            for (int j = 0; j < 8; ++j) {
                final int t4 = 10 * y;
                chars[++index] = (char)(48 + (t4 >>> 28));
                y = (t4 & 0xFFFFFFF);
            }
        }
        while (chars[index] == '0') {
            --index;
        }
        if (chars[index] == '.') {
            ++index;
        }
        int e2 = e - 1;
        chars[++index] = 'E';
        if (e2 < 0) {
            chars[++index] = '-';
            e2 = -e2;
        }
        if (e2 < 10) {
            chars[++index] = (char)(48 + e2);
        }
        else {
            if (e2 >= 100) {
                final int d = e2 * 1311 >>> 17;
                chars[++index] = (char)(48 + d);
                e2 -= 100 * d;
            }
            final int d = e2 * 103 >>> 10;
            chars[1 + index] = (char)(48 + d);
            chars[2 + index] = (char)(48 + (e2 - 10 * d));
            index += 2;
        }
        return index + 1 - off;
    }
    
    public static int toString(final float v, final byte[] bytes, final int off, final boolean json) {
        final int Q_MIN = -149;
        final int C_TINY = 8;
        final int C_MIN = 8388608;
        final int BQ_MASK = 255;
        final int T_MASK = 8388607;
        final long MASK_32 = 4294967295L;
        final int MASK_33 = 268435455;
        final int bits = Float.floatToRawIntBits(v);
        final int t = bits & 0x7FFFFF;
        final int bq = bits >>> 23 & 0xFF;
        if (bq >= 255) {
            int index = off;
            if (json) {
                JDKUtils.UNSAFE.putInt(bytes, JDKUtils.ARRAY_BYTE_BASE_OFFSET + index, IOUtils.NULL_32);
                index += 4;
            }
            else if (t != 0) {
                bytes[index] = 78;
                bytes[index + 1] = 97;
                bytes[index + 2] = 78;
                index += 3;
            }
            else {
                if (bits <= 0) {
                    bytes[index++] = 45;
                }
                JDKUtils.UNSAFE.putLong(bytes, JDKUtils.ARRAY_BYTE_BASE_OFFSET + index, DoubleToDecimal.INFINITY);
                index += 8;
            }
            return index - off;
        }
        int index = off - 1;
        if (bits < 0) {
            bytes[++index] = 45;
        }
        int q = 0;
        int dk = 0;
        if (bq == 0 && t == 0) {
            index = off - 1;
            if (bits != 0) {
                bytes[++index] = 45;
            }
            bytes[1 + index] = 48;
            bytes[2 + index] = 46;
            bytes[3 + index] = 48;
            return index + 4 - off;
        }
        boolean _stat = false;
        int f = 0;
        int e = 0;
        int c;
        if (bq != 0) {
            final int mq = 150 - bq;
            c = (0x800000 | t);
            if (0 < mq & mq < 24) {
                f = c >> mq;
                if (f << mq == c) {
                    _stat = true;
                }
            }
            if (!_stat) {
                q = -mq;
                dk = 0;
            }
        }
        else {
            q = -149;
            if (t < 8) {
                c = 10 * t;
                dk = -1;
            }
            else {
                c = t;
                dk = 0;
            }
        }
        if (!_stat) {
            final int out = c & 0x1;
            final long cb = c << 2;
            final long cbr = cb + 2L;
            long cbl;
            int k;
            if (c != 8388608 | q == -149) {
                cbl = cb - 2L;
                k = (int)(q * 661971961083L >> 41);
            }
            else {
                cbl = cb - 1L;
                k = (int)(q * 661971961083L - 274743187321L >> 41);
            }
            final int h = q + (int)(-k * 913124641741L >> 38) + 33;
            final long g = DoubleToDecimal.G[k + 324 << 1] + 1L;
            final long x1 = DoubleToDecimal.MULTIPLY_HIGH.multiplyHigh(g, cb << h);
            final long vbp = x1 >>> 31;
            final int vb = (int)(vbp | (x1 & 0xFFFFFFFFL) + 4294967295L >>> 32);
            final long x2 = DoubleToDecimal.MULTIPLY_HIGH.multiplyHigh(g, cbl << h);
            final long vbp2 = x2 >>> 31;
            final int vbl = (int)(vbp2 | (x2 & 0xFFFFFFFFL) + 4294967295L >>> 32);
            final long x3 = DoubleToDecimal.MULTIPLY_HIGH.multiplyHigh(g, cbr << h);
            final long vbp3 = x3 >>> 31;
            final int vbr = (int)(vbp3 | (x3 & 0xFFFFFFFFL) + 4294967295L >>> 32);
            final int s = vb >> 2;
            if (s >= 100) {
                final int sp10 = 10 * (int)(s * 1717986919L >>> 34);
                final int tp10 = sp10 + 10;
                final boolean upin = vbl + out <= sp10 << 2;
                final boolean wpin = (tp10 << 2) + out <= vbr;
                if (upin != wpin) {
                    _stat = true;
                    f = (upin ? sp10 : tp10);
                    e = k;
                }
            }
            if (!_stat) {
                final int t2 = s + 1;
                final boolean uin = vbl + out <= s << 2;
                final boolean win = (t2 << 2) + out <= vbr;
                if (uin != win) {
                    f = (uin ? s : t2);
                }
                else {
                    final int cmp = vb - (s + t2 << 1);
                    f = ((cmp < 0 || (cmp == 0 && (s & 0x1) == 0x0)) ? s : t2);
                }
                e = k + dk;
            }
        }
        int len = (int)((32 - Integer.numberOfLeadingZeros(f)) * 661971961083L >> 41);
        if (f >= DoubleToDecimal.pow10[len]) {
            ++len;
        }
        f *= (int)DoubleToDecimal.pow10[9 - len];
        e += len;
        final int h2 = (int)(f * 1441151881L >>> 57);
        final int l = f - 100000000 * h2;
        if (0 < e && e <= 7) {
            bytes[++index] = (byte)(48 + h2);
            int y = (int)(DoubleToDecimal.MULTIPLY_HIGH.multiplyHigh((long)(l + 1) << 28, 193428131138340668L) >>> 20) - 1;
            int i;
            for (i = 1; i < e; ++i) {
                final int t3 = 10 * y;
                bytes[++index] = (byte)(48 + (t3 >>> 28));
                y = (t3 & 0xFFFFFFF);
            }
            bytes[++index] = 46;
            while (i <= 8) {
                final int t3 = 10 * y;
                bytes[++index] = (byte)(48 + (t3 >>> 28));
                y = (t3 & 0xFFFFFFF);
                ++i;
            }
            while (bytes[index] == 48) {
                --index;
            }
            if (bytes[index] == 46) {
                ++index;
            }
        }
        else if (-3 < e && e <= 0) {
            bytes[1 + index] = 48;
            bytes[2 + index] = 46;
            index += 2;
            while (e < 0) {
                bytes[++index] = 48;
                ++e;
            }
            bytes[++index] = (byte)(48 + h2);
            int y = (int)(DoubleToDecimal.MULTIPLY_HIGH.multiplyHigh((long)(l + 1) << 28, 193428131138340668L) >>> 20) - 1;
            for (int j = 0; j < 8; ++j) {
                final int t4 = 10 * y;
                bytes[++index] = (byte)(48 + (t4 >>> 28));
                y = (t4 & 0xFFFFFFF);
            }
            while (bytes[index] == 48) {
                --index;
            }
            if (bytes[index] == 46) {
                ++index;
            }
        }
        else {
            bytes[1 + index] = (byte)(48 + h2);
            bytes[2 + index] = 46;
            index += 2;
            int y = (int)(DoubleToDecimal.MULTIPLY_HIGH.multiplyHigh((long)(l + 1) << 28, 193428131138340668L) >>> 20) - 1;
            for (int j = 0; j < 8; ++j) {
                final int t4 = 10 * y;
                bytes[++index] = (byte)(48 + (t4 >>> 28));
                y = (t4 & 0xFFFFFFF);
            }
            while (bytes[index] == 48) {
                --index;
            }
            if (bytes[index] == 46) {
                ++index;
            }
            int e2 = e - 1;
            bytes[++index] = 69;
            if (e2 < 0) {
                bytes[++index] = 45;
                e2 = -e2;
            }
            if (e2 < 10) {
                bytes[++index] = (byte)(48 + e2);
            }
            else {
                final int d = e2 * 103 >>> 10;
                bytes[1 + index] = (byte)(48 + d);
                bytes[2 + index] = (byte)(48 + (e2 - 10 * d));
                index += 2;
            }
        }
        return index + 1 - off;
    }
    
    public static int toString(final float v, final char[] chars, final int off, final boolean json) {
        final int Q_MIN = -149;
        final int C_TINY = 8;
        final int C_MIN = 8388608;
        final int BQ_MASK = 255;
        final int T_MASK = 8388607;
        final long MASK_32 = 4294967295L;
        final int MASK_33 = 268435455;
        final int bits = Float.floatToRawIntBits(v);
        final int t = bits & 0x7FFFFF;
        final int bq = bits >>> 23 & 0xFF;
        if (bq >= 255) {
            int index = off;
            if (json) {
                JDKUtils.UNSAFE.putLong(chars, JDKUtils.ARRAY_CHAR_BASE_OFFSET + (index << 1), IOUtils.NULL_64);
                index += 4;
            }
            else if (t != 0) {
                chars[index] = 'N';
                chars[index + 1] = 'a';
                chars[index + 2] = 'N';
                index += 3;
            }
            else {
                if (bits <= 0) {
                    chars[index++] = '-';
                }
                JDKUtils.UNSAFE.putLong(chars, JDKUtils.ARRAY_CHAR_BASE_OFFSET + (index << 1), DoubleToDecimal.INFI);
                JDKUtils.UNSAFE.putLong(chars, JDKUtils.ARRAY_CHAR_BASE_OFFSET + (index + 4 << 1), DoubleToDecimal.NITY);
                index += 8;
            }
            return index - off;
        }
        int index = off - 1;
        if (bits < 0) {
            chars[++index] = '-';
        }
        int q = 0;
        int dk = 0;
        if (bq == 0 && t == 0) {
            index = off - 1;
            if (bits != 0) {
                chars[++index] = '-';
            }
            chars[1 + index] = '0';
            chars[2 + index] = '.';
            chars[3 + index] = '0';
            index += 3;
            return index + 1 - off;
        }
        boolean _stat = false;
        int f = 0;
        int e = 0;
        int c;
        if (bq != 0) {
            final int mq = 150 - bq;
            c = (0x800000 | t);
            if (0 < mq & mq < 24) {
                f = c >> mq;
                if (f << mq == c) {
                    _stat = true;
                }
            }
            if (!_stat) {
                q = -mq;
                dk = 0;
            }
        }
        else {
            q = -149;
            if (t < 8) {
                c = 10 * t;
                dk = -1;
            }
            else {
                c = t;
                dk = 0;
            }
        }
        if (!_stat) {
            final int out = c & 0x1;
            final long cb = c << 2;
            final long cbr = cb + 2L;
            long cbl;
            int k;
            if (c != 8388608 | q == -149) {
                cbl = cb - 2L;
                k = (int)(q * 661971961083L >> 41);
            }
            else {
                cbl = cb - 1L;
                k = (int)(q * 661971961083L - 274743187321L >> 41);
            }
            final int h = q + (int)(-k * 913124641741L >> 38) + 33;
            final long g = DoubleToDecimal.G[k + 324 << 1] + 1L;
            final long x1 = DoubleToDecimal.MULTIPLY_HIGH.multiplyHigh(g, cb << h);
            final long vbp = x1 >>> 31;
            final int vb = (int)(vbp | (x1 & 0xFFFFFFFFL) + 4294967295L >>> 32);
            final long x2 = DoubleToDecimal.MULTIPLY_HIGH.multiplyHigh(g, cbl << h);
            final long vbp2 = x2 >>> 31;
            final int vbl = (int)(vbp2 | (x2 & 0xFFFFFFFFL) + 4294967295L >>> 32);
            final long x3 = DoubleToDecimal.MULTIPLY_HIGH.multiplyHigh(g, cbr << h);
            final long vbp3 = x3 >>> 31;
            final int vbr = (int)(vbp3 | (x3 & 0xFFFFFFFFL) + 4294967295L >>> 32);
            final int s = vb >> 2;
            if (s >= 100) {
                final int sp10 = 10 * (int)(s * 1717986919L >>> 34);
                final int tp10 = sp10 + 10;
                final boolean upin = vbl + out <= sp10 << 2;
                final boolean wpin = (tp10 << 2) + out <= vbr;
                if (upin != wpin) {
                    _stat = true;
                    f = (upin ? sp10 : tp10);
                    e = k;
                }
            }
            if (!_stat) {
                final int t2 = s + 1;
                final boolean uin = vbl + out <= s << 2;
                final boolean win = (t2 << 2) + out <= vbr;
                if (uin != win) {
                    f = (uin ? s : t2);
                }
                else {
                    final int cmp = vb - (s + t2 << 1);
                    f = ((cmp < 0 || (cmp == 0 && (s & 0x1) == 0x0)) ? s : t2);
                }
                e = k + dk;
            }
        }
        int len = (int)((32 - Integer.numberOfLeadingZeros(f)) * 661971961083L >> 41);
        if (f >= DoubleToDecimal.pow10[len]) {
            ++len;
        }
        f *= (int)DoubleToDecimal.pow10[9 - len];
        e += len;
        final int h2 = (int)(f * 1441151881L >>> 57);
        final int l = f - 100000000 * h2;
        if (0 < e && e <= 7) {
            chars[++index] = (char)(48 + h2);
            int y = (int)(DoubleToDecimal.MULTIPLY_HIGH.multiplyHigh((long)(l + 1) << 28, 193428131138340668L) >>> 20) - 1;
            int i;
            for (i = 1; i < e; ++i) {
                final int t3 = 10 * y;
                chars[++index] = (char)(48 + (t3 >>> 28));
                y = (t3 & 0xFFFFFFF);
            }
            chars[++index] = '.';
            while (i <= 8) {
                final int t3 = 10 * y;
                chars[++index] = (char)(48 + (t3 >>> 28));
                y = (t3 & 0xFFFFFFF);
                ++i;
            }
            while (chars[index] == '0') {
                --index;
            }
            if (chars[index] == '.') {
                ++index;
            }
        }
        else if (-3 < e && e <= 0) {
            chars[1 + index] = '0';
            chars[2 + index] = '.';
            index += 2;
            while (e < 0) {
                chars[++index] = '0';
                ++e;
            }
            chars[++index] = (char)(48 + h2);
            int y = (int)(DoubleToDecimal.MULTIPLY_HIGH.multiplyHigh((long)(l + 1) << 28, 193428131138340668L) >>> 20) - 1;
            for (int j = 0; j < 8; ++j) {
                final int t4 = 10 * y;
                chars[++index] = (char)(48 + (t4 >>> 28));
                y = (t4 & 0xFFFFFFF);
            }
            while (chars[index] == '0') {
                --index;
            }
            if (chars[index] == '.') {
                ++index;
            }
        }
        else {
            chars[1 + index] = (char)(48 + h2);
            chars[2 + index] = '.';
            index += 2;
            int y = (int)(DoubleToDecimal.MULTIPLY_HIGH.multiplyHigh((long)(l + 1) << 28, 193428131138340668L) >>> 20) - 1;
            for (int j = 0; j < 8; ++j) {
                final int t4 = 10 * y;
                chars[++index] = (char)(48 + (t4 >>> 28));
                y = (t4 & 0xFFFFFFF);
            }
            while (chars[index] == '0') {
                --index;
            }
            if (chars[index] == '.') {
                ++index;
            }
            int e2 = e - 1;
            chars[++index] = 'E';
            if (e2 < 0) {
                chars[++index] = '-';
                e2 = -e2;
            }
            if (e2 < 10) {
                chars[++index] = (char)(48 + e2);
            }
            else {
                final int d = e2 * 103 >>> 10;
                chars[1 + index] = (char)(48 + d);
                chars[2 + index] = (char)(48 + (e2 - 10 * d));
                index += 2;
            }
        }
        return index + 1 - off;
    }
    
    static long multiplyHigh(final long x, final long y) {
        final long x2 = x >> 32;
        final long x3 = x & 0xFFFFFFFFL;
        final long y2 = y >> 32;
        final long y3 = y & 0xFFFFFFFFL;
        final long z2 = x3 * y3;
        final long t = x2 * y3 + (z2 >>> 32);
        long z3 = t & 0xFFFFFFFFL;
        final long z4 = t >> 32;
        z3 += x3 * y2;
        return x2 * y2 + z4 + (z3 >> 32);
    }
    
    static {
        final long infinity = 5291279215216915577L;
        INFINITY = (JDKUtils.BIG_ENDIAN ? infinity : Long.reverseBytes(infinity));
        long infi = 20548145752965225L;
        if (!JDKUtils.BIG_ENDIAN) {
            infi <<= 8;
            infi = Long.reverseBytes(infi);
        }
        INFI = infi;
        long nity = 30962698417340537L;
        if (!JDKUtils.BIG_ENDIAN) {
            nity <<= 8;
            nity = Long.reverseBytes(nity);
        }
        NITY = nity;
        G = new long[] { 5696189077778435540L, 6557778377634271669L, 9113902524445496865L, 1269073367360058862L, 7291122019556397492L, 1015258693888047090L, 5832897615645117993L, 6346230177223303157L, 4666318092516094394L, 8766332956520552849L, 7466108948025751031L, 8492109508320019073L, 5972887158420600825L, 4949013199285060097L, 4778309726736480660L, 3959210559428048077L, 7645295562778369056L, 6334736895084876923L, 6116236450222695245L, 3223115108696946377L, 4892989160178156196L, 2578492086957557102L, 7828782656285049914L, 436238524390181040L, 6263026125028039931L, 2193665226883099993L, 5010420900022431944L, 9133629810990300641L, 8016673440035891111L, 9079784475471615541L, 6413338752028712889L, 5419153173006337271L, 5130671001622970311L, 6179996945776024979L, 8209073602596752498L, 6198646298499729642L, 6567258882077401998L, 8648265853541694037L, 5253807105661921599L, 1384589460720489745L, 8406091369059074558L, 5904691951894693915L, 6724873095247259646L, 8413102376257665455L, 5379898476197807717L, 4885807493635177203L, 8607837561916492348L, 438594360332462878L, 6886270049533193878L, 4040224303007880625L, 5509016039626555102L, 6921528257148214824L, 8814425663402488164L, 3695747581953323071L, 7051540530721990531L, 4801272472933613619L, 5641232424577592425L, 1996343570975935733L, 9025971879324147880L, 3194149713561497173L, 7220777503459318304L, 2555319770849197738L, 5776622002767454643L, 3888930224050313352L, 4621297602213963714L, 6800492993982161005L, 7394076163542341943L, 5346765568258592123L, 5915260930833873554L, 7966761269348784022L, 4732208744667098843L, 8218083422849982379L, 7571533991467358150L, 2080887032334240837L, 6057227193173886520L, 1664709625867392670L, 4845781754539109216L, 1331767700693914136L, 7753250807262574745L, 7664851543223128102L, 6202600645810059796L, 6131881234578502482L, 4962080516648047837L, 3060830580291846824L, 7939328826636876539L, 6742003335837910079L, 6351463061309501231L, 7238277076041283225L, 5081170449047600985L, 3945947253462071419L, 8129872718476161576L, 6313515605539314269L, 6503898174780929261L, 3206138077060496254L, 5203118539824743409L, 720236054277441842L, 8324989663719589454L, 4841726501585817270L, 6659991730975671563L, 5718055608639608977L, 5327993384780537250L, 8263793301653597505L, 8524789415648859601L, 3998697245790980200L, 6819831532519087681L, 1354283389261828999L, 5455865226015270144L, 8462124340893283845L, 8729384361624432231L, 8005375723316388668L, 6983507489299545785L, 4559626171282155773L, 5586805991439636628L, 3647700937025724618L, 8938889586303418605L, 3991647091870204227L, 7151111669042734884L, 3193317673496163382L, 5720889335234187907L, 4399328546167885867L, 9153422936374700651L, 8883600081239572549L, 7322738349099760521L, 5262205657620702877L, 5858190679279808417L, 2365090118725607140L, 4686552543423846733L, 7426095317093351197L, 7498484069478154774L, 813706063123630946L, 5998787255582523819L, 2495639257869859918L, 4799029804466019055L, 3841185813666843096L, 7678447687145630488L, 6145897301866948954L, 6142758149716504390L, 8606066656235469486L, 4914206519773203512L, 6884853324988375589L, 7862730431637125620L, 3637067690497580296L, 6290184345309700496L, 2909654152398064237L, 5032147476247760397L, 483048914547496228L, 8051435961996416635L, 2617552670646949126L, 6441148769597133308L, 2094042136517559301L, 5152919015677706646L, 5364582523955957764L, 8244670425084330634L, 4893983223587622099L, 6595736340067464507L, 5759860986241052841L, 5276589072053971606L, 918539974250931950L, 8442542515286354569L, 7003687180914356604L, 6754034012229083655L, 7447624152102440445L, 5403227209783266924L, 5958099321681952356L, 8645163535653227079L, 3998935692578258285L, 6916130828522581663L, 5043822961433561789L, 5532904662818065330L, 7724407183888759755L, 8852647460508904529L, 3135679457367239799L, 7082117968407123623L, 4353217973264747001L, 5665694374725698898L, 7171923193353707924L, 9065110999561118238L, 407030665140201709L, 7252088799648894590L, 4014973346854071690L, 5801671039719115672L, 3211978677483257352L, 4641336831775292537L, 8103606164099471367L, 7426138930840468060L, 5587072233075333540L, 5940911144672374448L, 4469657786460266832L, 4752728915737899558L, 7265075043910123789L, 7604366265180639294L, 556073626030467093L, 6083493012144511435L, 2289533308195328836L, 4866794409715609148L, 1831626646556263069L, 7786871055544974637L, 1085928227119065748L, 6229496844435979709L, 6402765803808118083L, 4983597475548783767L, 6966887050417449628L, 7973755960878054028L, 3768321651184098759L, 6379004768702443222L, 6704006135689189330L, 5103203814961954578L, 1673856093809441141L, 8165126103939127325L, 833495342724150664L, 6532100883151301860L, 666796274179320531L, 5225680706521041488L, 533437019343456425L, 8361089130433666380L, 8232196860433350926L, 6688871304346933104L, 6585757488346680741L, 5351097043477546483L, 7113280398048299755L, 8561755269564074374L, 313202192651548637L, 6849404215651259499L, 2095236161492194072L, 5479523372521007599L, 3520863336564710419L, 8767237396033612159L, 99358116390671185L, 7013789916826889727L, 1924160900483492110L, 5611031933461511781L, 7073351942499659173L, 8977651093538418850L, 7628014293257544353L, 7182120874830735080L, 6102411434606035483L, 5745696699864588064L, 4881929147684828386L, 9193114719783340903L, 2277063414182859933L, 7354491775826672722L, 5510999546088198270L, 5883593420661338178L, 719450822128648293L, 4706874736529070542L, 4264909472444828957L, 7530999578446512867L, 8668529563282681493L, 6024799662757210294L, 3245474835884234871L, 4819839730205768235L, 4441054276078343059L, 7711743568329229176L, 7105686841725348894L, 6169394854663383341L, 3839875066009323953L, 4935515883730706673L, 1227225645436504001L, 7896825413969130677L, 118886625327451240L, 6317460331175304541L, 5629132522374826477L, 5053968264940243633L, 2658631610528906020L, 8086349223904389813L, 2409136169475294470L, 6469079379123511850L, 5616657750322145900L, 5175263503298809480L, 4493326200257716720L, 8280421605278095168L, 7189321920412346751L, 6624337284222476135L, 217434314217011916L, 5299469827377980908L, 173947451373609533L, 8479151723804769452L, 7657013551681595899L, 6783321379043815562L, 2436262026603366396L, 5426657103235052449L, 7483032843395558602L, 8682651365176083919L, 6438829327320028278L, 6946121092140867135L, 6995737869226977784L, 5556896873712693708L, 5596590295381582227L, 8891034997940309933L, 7109870065239576402L, 7112827998352247947L, 153872830078795637L, 5690262398681798357L, 5657121486175901994L, 9104419837890877372L, 1672696748397622544L, 7283535870312701897L, 6872180620830963520L, 5826828696250161518L, 1808395681922860493L, 4661462957000129214L, 5136065360280198718L, 7458340731200206743L, 2683681354335452463L, 5966672584960165394L, 5836293898210272294L, 4773338067968132315L, 6513709525939172997L, 7637340908749011705L, 1198563204647900987L, 6109872726999209364L, 958850563718320789L, 4887898181599367491L, 2611754858345611793L, 7820637090558987986L, 489458958611068546L, 6256509672447190388L, 7770264796372675483L, 5005207737957752311L, 682188614985274902L, 8008332380732403697L, 6625525006089305327L, 6406665904585922958L, 1611071190129533939L, 5125332723668738366L, 4978205766845537474L, 8200532357869981386L, 4275780412210949635L, 6560425886295985109L, 1575949922397804547L, 5248340709036788087L, 3105434345289198799L, 8397345134458860939L, 6813369359833673240L, 6717876107567088751L, 7295369895237893754L, 5374300886053671001L, 3991621508819359841L, 8598881417685873602L, 2697245599369065423L, 6879105134148698881L, 7691819701608117823L, 5503284107318959105L, 4308781353915539097L, 8805254571710334568L, 6894050166264862555L, 7044203657368267654L, 9204588947753800367L, 5635362925894614123L, 9208345565573995455L, 9016580681431382598L, 3665306460692661759L, 7213264545145106078L, 6621593983296039730L, 5770611636116084862L, 8986624001378742108L, 4616489308892867890L, 3499950386361083363L, 7386382894228588624L, 5599920618177733380L, 5909106315382870899L, 6324610901913141866L, 4727285052306296719L, 6904363128901468655L, 7563656083690074751L, 5512957784129484362L, 6050924866952059801L, 2565691819932632328L, 4840739893561647841L, 207879048575150701L, 7745183829698636545L, 5866629699833106606L, 6196147063758909236L, 4693303759866485285L, 4956917651007127389L, 1909968600522233067L, 7931068241611403822L, 6745298575577483229L, 6344854593289123058L, 1706890045720076260L, 5075883674631298446L, 5054860851317971332L, 8121413879410077514L, 4398428547366843807L, 6497131103528062011L, 5363417245264430207L, 5197704882822449609L, 2446059388840589004L, 8316327812515919374L, 7603043836886852730L, 6653062250012735499L, 7927109476880437346L, 5322449800010188399L, 8186361988875305038L, 8515919680016301439L, 7564155960087622576L, 6812735744013041151L, 7895999175441053223L, 5450188595210432921L, 4472124932981887417L, 8720301752336692674L, 3466051078029109543L, 6976241401869354139L, 4617515269794242796L, 5580993121495483311L, 5538686623206349399L, 8929588994392773298L, 5172549782388248714L, 7143671195514218638L, 7827388640652509295L, 5714936956411374911L, 727887690409141951L, 9143899130258199857L, 6698643526767492606L, 7315119304206559886L, 1669566006672083762L, 5852095443365247908L, 8714350434821487656L, 4681676354692198327L, 1437457125744324640L, 7490682167507517323L, 4144605808561874585L, 5992545734006013858L, 7005033461591409992L, 4794036587204811087L, 70003547160262509L, 7670458539527697739L, 1956680082827375175L, 6136366831622158191L, 3410018473632855302L, 4909093465297726553L, 883340371535329080L, 7854549544476362484L, 8792042223940347174L, 6283639635581089987L, 8878308186523232901L, 5026911708464871990L, 3413297734476675998L, 8043058733543795184L, 5461276375162681596L, 6434446986835036147L, 6213695507501100438L, 5147557589468028918L, 1281607591258970028L, 8236092143148846269L, 205897738643396882L, 6588873714519077015L, 2009392598285672668L, 5271098971615261612L, 1607514078628538134L, 8433758354584418579L, 4416696933176616176L, 6747006683667534863L, 5378031953912248102L, 5397605346934027890L, 7991774377871708805L, 8636168555094444625L, 3563466967739958280L, 6908934844075555700L, 2850773574191966624L, 5527147875260444560L, 2280618859353573299L, 8843436600416711296L, 3648990174965717279L, 7074749280333369037L, 1074517732601618662L, 5659799424266695229L, 6393637408194160414L, 9055679078826712367L, 4695796630997791177L, 7244543263061369894L, 67288490056322619L, 5795634610449095915L, 1898505199416013257L, 4636507688359276732L, 1518804159532810606L, 7418412301374842771L, 4274761062623452130L, 5934729841099874217L, 1575134442727806543L, 4747783872879899373L, 6794130776295110719L, 7596454196607838997L, 9025934834701221989L, 6077163357286271198L, 3531399053019067268L, 4861730685829016958L, 6514468057157164137L, 7778769097326427133L, 8578474484080507458L, 6223015277861141707L, 1328756365151540482L, 4978412222288913365L, 6597028314234097870L, 7965459555662261385L, 1331873265919780784L, 6372367644529809108L, 1065498612735824627L, 5097894115623847286L, 4541747704930570025L, 8156630584998155658L, 3577447513147001717L, 6525304467998524526L, 6551306825259511697L, 5220243574398819621L, 3396371052836654196L, 8352389719038111394L, 1744844869796736390L, 6681911775230489115L, 3240550303208344274L, 5345529420184391292L, 2592440242566675419L, 8552847072295026067L, 5992578795477635832L, 6842277657836020854L, 1104714221640198342L, 5473822126268816683L, 2728445784683113836L, 8758115402030106693L, 2520838848122026975L, 7006492321624085354L, 5706019893239531903L, 5605193857299268283L, 6409490321962580684L, 8968310171678829253L, 8410510107769173933L, 7174648137343063403L, 1194384864102473662L, 5739718509874450722L, 4644856706023889253L, 9183549615799121156L, 53073100154402158L, 7346839692639296924L, 7421156109607342373L, 5877471754111437539L, 7781599295056829060L, 4701977403289150031L, 8069953843416418410L, 7523163845262640050L, 9222577334724359132L, 6018531076210112040L, 7378061867779487306L, 4814824860968089632L, 5902449494223589845L, 7703719777548943412L, 2065221561273923105L, 6162975822039154729L, 7186200471132003969L, 4930380657631323783L, 7593634784276558337L, 7888609052210118054L, 1081769210616762369L, 6310887241768094443L, 2710089775864365057L, 5048709793414475554L, 5857420635433402369L, 8077935669463160887L, 3837849794580578305L, 6462348535570528709L, 8604303057777328129L, 5169878828456422967L, 8728116853592817665L, 8271806125530276748L, 6586289336264687617L, 6617444900424221398L, 8958380283753660417L, 5293955920339377119L, 1632681004890062849L, 8470329472543003390L, 6301638422566010881L, 6776263578034402712L, 5041310738052808705L, 5421010862427522170L, 343699775700336641L, 8673617379884035472L, 549919641120538625L, 6938893903907228377L, 5973958935009296385L, 5551115123125782702L, 1089818333265526785L, 8881784197001252323L, 3588383740595798017L, 7105427357601001858L, 6560055807218548737L, 5684341886080801486L, 8937393460516749313L, 9094947017729282379L, 1387108685230112769L, 7275957614183425903L, 2954361355555045377L, 5820766091346740722L, 6052837899185946625L, 4656612873077392578L, 1152921504606846977L, 7450580596923828125L, 1L, 5960464477539062500L, 1L, 4768371582031250000L, 1L, 7629394531250000000L, 1L, 6103515625000000000L, 1L, 4882812500000000000L, 1L, 7812500000000000000L, 1L, 6250000000000000000L, 1L, 5000000000000000000L, 1L, 8000000000000000000L, 1L, 6400000000000000000L, 1L, 5120000000000000000L, 1L, 8192000000000000000L, 1L, 6553600000000000000L, 1L, 5242880000000000000L, 1L, 8388608000000000000L, 1L, 6710886400000000000L, 1L, 5368709120000000000L, 1L, 8589934592000000000L, 1L, 6871947673600000000L, 1L, 5497558138880000000L, 1L, 8796093022208000000L, 1L, 7036874417766400000L, 1L, 5629499534213120000L, 1L, 9007199254740992000L, 1L, 7205759403792793600L, 1L, 5764607523034234880L, 1L, 4611686018427387904L, 1L, 7378697629483820646L, 3689348814741910324L, 5902958103587056517L, 1106804644422573097L, 4722366482869645213L, 6419466937650923963L, 7555786372591432341L, 8426472692870523179L, 6044629098073145873L, 4896503746925463381L, 4835703278458516698L, 7606551812282281028L, 7737125245533626718L, 1102436455425918676L, 6189700196426901374L, 4571297979082645264L, 4951760157141521099L, 5501712790637071373L, 7922816251426433759L, 3268717242906448711L, 6338253001141147007L, 4459648201696114131L, 5070602400912917605L, 9101741783469756789L, 8112963841460668169L, 5339414816696835055L, 6490371073168534535L, 6116206260728423206L, 5192296858534827628L, 4892965008582738565L, 8307674973655724205L, 5984069606361426541L, 6646139978924579364L, 4787255685089141233L, 5316911983139663491L, 5674478955442268148L, 8507059173023461586L, 5389817513965718714L, 6805647338418769269L, 2467179603801619810L, 5444517870735015415L, 3818418090412251009L, 8711228593176024664L, 6109468944659601615L, 6968982874540819731L, 6732249563098636453L, 5575186299632655785L, 3541125243107954001L, 8920298079412249256L, 5665800388972726402L, 7136238463529799405L, 2687965903807225960L, 5708990770823839524L, 2150372723045780768L, 9134385233318143238L, 7129945171615159552L, 7307508186654514591L, 169932915179262157L, 5846006549323611672L, 7514643961627230372L, 4676805239458889338L, 2322366354559873974L, 7482888383134222941L, 1871111759924843197L, 5986310706507378352L, 8875587037423695204L, 4789048565205902682L, 3411120815197045840L, 7662477704329444291L, 7302467711686228506L, 6129982163463555433L, 3997299761978027643L, 4903985730770844346L, 6887188624324332438L, 7846377169233350954L, 7330152984177021577L, 6277101735386680763L, 7708796794712572423L, 5021681388309344611L, 633014213657192454L, 8034690221294951377L, 6546845963964373411L, 6427752177035961102L, 1548127956429588405L, 5142201741628768881L, 6772525587256536209L, 8227522786606030210L, 7146692124868547611L, 6582018229284824168L, 5717353699894838089L, 5265614583427859334L, 8263231774657780795L, 8424983333484574935L, 7687147617339583786L, 6739986666787659948L, 6149718093871667029L, 5391989333430127958L, 8609123289839243947L, 8627182933488204734L, 2706550819517059345L, 6901746346790563787L, 4009915062984602637L, 5521397077432451029L, 8741955272500547595L, 8834235323891921647L, 8453105213888010667L, 7067388259113537318L, 3073135356368498210L, 5653910607290829854L, 6147857099836708891L, 9046256971665327767L, 4302548137625868741L, 7237005577332262213L, 8976061732213560478L, 5789604461865809771L, 1646826163657982898L, 4631683569492647816L, 8696158560410206965L, 7410693711188236507L, 1001132845059645012L, 5928554968950589205L, 6334929498160581494L, 4742843975160471364L, 5067943598528465196L, 7588550360256754183L, 2574686535532678828L, 6070840288205403346L, 5749098043168053386L, 4856672230564322677L, 2754604027163487547L, 7770675568902916283L, 6252040850832535236L, 6216540455122333026L, 8690981495407938512L, 4973232364097866421L, 5108110788955395648L, 7957171782556586274L, 4483628447586722714L, 6365737426045269019L, 5431577165440333333L, 5092589940836215215L, 6189936139723221828L, 8148143905337944345L, 680525786702379117L, 6518515124270355476L, 544420629361903293L, 5214812099416284380L, 7814234132973343281L, 8343699359066055009L, 3279402575902573442L, 6674959487252844007L, 4468196468093013915L, 5339967589802275205L, 9108580396587276617L, 8543948143683640329L, 5350356597684866779L, 6835158514946912263L, 6124959685518848585L, 5468126811957529810L, 8589316563156989191L, 8749002899132047697L, 4519534464196406897L, 6999202319305638157L, 9149650793469991003L, 5599361855444510526L, 3630371820034082479L, 8958978968711216842L, 2119246097312621643L, 7167183174968973473L, 7229420099962962799L, 5733746539975178779L, 249512857857504755L, 9173994463960286046L, 4088569387313917931L, 7339195571168228837L, 1426181102480179183L, 5871356456934583069L, 6674968104097008831L, 4697085165547666455L, 7184648890648562227L, 7515336264876266329L, 2272066188182923754L, 6012269011901013063L, 3662327357917294165L, 4809815209520810450L, 6619210701075745655L, 7695704335233296721L, 1367365084866417240L, 6156563468186637376L, 8472589697376954439L, 4925250774549309901L, 4933397350530608390L, 7880401239278895842L, 4204086946107063100L, 6304320991423116673L, 8897292778998515965L, 5043456793138493339L, 1583811001085947287L, 8069530869021589342L, 6223446416479425982L, 6455624695217271474L, 1289408318441630463L, 5164499756173817179L, 2876201062124259532L, 8263199609878107486L, 8291270514140725574L, 6610559687902485989L, 4788342003941625298L, 5288447750321988791L, 5675348010524255400L, 8461516400515182066L, 5391208002096898316L, 6769213120412145653L, 2468291994306563491L, 5415370496329716522L, 5663982410187161116L, 8664592794127546436L, 1683674226815637140L, 6931674235302037148L, 8725637010936330358L, 5545339388241629719L, 1446486386636198802L, 8872543021186607550L, 6003727033359828406L, 7098034416949286040L, 4802981626687862725L, 5678427533559428832L, 3842385301350290180L, 9085484053695086131L, 7992490889531419449L, 7268387242956068905L, 4549318304254180398L, 5814709794364855124L, 3639454643403344318L, 4651767835491884099L, 4756238122093630616L, 7442828536787014559L, 2075957773236943501L, 5954262829429611647L, 3505440625960509963L, 4763410263543689317L, 8338375722881273455L, 7621456421669902908L, 5962703527126216881L, 6097165137335922326L, 8459511636442883828L, 4877732109868737861L, 4922934901783351901L, 7804371375789980578L, 4187347028111452718L, 6243497100631984462L, 7039226437231072498L, 4994797680505587570L, 1942032335042947675L, 7991676288808940112L, 3107251736068716280L, 6393341031047152089L, 8019824610967838509L, 5114672824837721671L, 8260534096145225969L, 8183476519740354675L, 304133702235675419L, 6546781215792283740L, 243306961788540335L, 5237424972633826992L, 194645569430832268L, 8379879956214123187L, 2156107318460286790L, 6703903964971298549L, 7258909076881094917L, 5363123171977038839L, 7651801668875831096L, 8580997075163262143L, 6708859448088464268L, 6864797660130609714L, 9056436373212681737L, 5491838128104487771L, 9089823505941100552L, 8786941004967180435L, 1630996757909074751L, 7029552803973744348L, 1304797406327259801L, 5623642243178995478L, 4733186739803718164L, 8997827589086392765L, 5728424376314993901L, 7198262071269114212L, 4582739501051995121L, 5758609657015291369L, 9200214822954461581L, 9213775451224466191L, 9186320494614273045L, 7371020360979572953L, 5504381988320463275L, 5896816288783658362L, 8092854405398280943L, 4717453031026926690L, 2784934709576714431L, 7547924849643082704L, 4455895535322743090L, 6038339879714466163L, 5409390835629149634L, 4830671903771572930L, 8016861483245230030L, 7729075046034516689L, 3603606336337592240L, 6183260036827613351L, 4727559476441028954L, 4946608029462090681L, 1937373173781868001L, 7914572847139345089L, 8633820300163854287L, 6331658277711476071L, 8751730647502038591L, 5065326622169180857L, 5156710110630675711L, 8104522595470689372L, 872038547525260492L, 6483618076376551497L, 6231654060133073878L, 5186894461101241198L, 1295974433364548779L, 8299031137761985917L, 228884686012322885L, 6639224910209588733L, 5717130970922723793L, 5311379928167670986L, 8263053591480089358L, 8498207885068273579L, 308164894771456841L, 6798566308054618863L, 2091206323188120634L, 5438853046443695090L, 5362313873292406831L, 8702164874309912144L, 8579702197267850929L, 6961731899447929715L, 8708436165185235905L, 5569385519558343772L, 6966748932148188724L, 8911016831293350036L, 3768100661953281312L, 7128813465034680029L, 1169806122191669888L, 5703050772027744023L, 2780519305124291072L, 9124881235244390437L, 2604156480827910553L, 7299904988195512349L, 7617348406775193928L, 5839923990556409879L, 7938553132791110304L, 4671939192445127903L, 8195516913603843405L, 7475102707912204646L, 2044780617540418478L, 5980082166329763716L, 9014522123516155429L, 4784065733063810973L, 5366943291441969181L, 7654505172902097557L, 6742434858936195528L, 6123604138321678046L, 1704599072407046100L, 4898883310657342436L, 8742376887409457526L, 7838213297051747899L, 1075082168258445910L, 6270570637641398319L, 2704740141977711890L, 5016456510113118655L, 4008466520953124674L, 8026330416180989848L, 6413546433524999478L, 6421064332944791878L, 8820185961561909905L, 5136851466355833503L, 1522125547136662440L, 8218962346169333605L, 590726468047704741L, 6575169876935466884L, 472581174438163793L, 5260135901548373507L, 2222739346921486196L, 8416217442477397611L, 5401057362445333075L, 6732973953981918089L, 2476171482585311299L, 5386379163185534471L, 3825611593439204201L, 8618206661096855154L, 2431629734760816398L, 6894565328877484123L, 3789978195179608280L, 5515652263101987298L, 6721331370885596947L, 8825043620963179677L, 8909455786045999954L, 7060034896770543742L, 3438215814094889640L, 5648027917416434993L, 8284595873388777197L, 9036844667866295990L, 2187306953196312545L, 7229475734293036792L, 1749845562557050036L, 5783580587434429433L, 6933899672158505514L, 4626864469947543547L, 13096515613938926L, 7402983151916069675L, 1865628832353257443L, 5922386521532855740L, 1492503065882605955L, 4737909217226284592L, 1194002452706084764L, 7580654747562055347L, 3755078331700690783L, 6064523798049644277L, 8538085887473418112L, 4851619038439715422L, 3141119895236824166L, 7762590461503544675L, 6870466239749873827L, 6210072369202835740L, 5496372991799899062L, 4968057895362268592L, 4397098393439919250L, 7948892632579629747L, 8880031836874825961L, 6359114106063703798L, 3414676654757950445L, 5087291284850963038L, 6421090138548270680L, 8139666055761540861L, 8429069814306277926L, 6511732844609232689L, 4898581444074067179L, 5209386275687386151L, 5763539562630208905L, 8335018041099817842L, 5532314485466423924L, 6668014432879854274L, 736502773631228816L, 5334411546303883419L, 2433876626275938215L, 8535058474086213470L, 7583551416783411467L, 6828046779268970776L, 6066841133426729173L, 5462437423415176621L, 3008798499370428177L, 8739899877464282594L, 1124728784250774760L, 6991919901971426075L, 2744457434771574970L, 5593535921577140860L, 2195565947817259976L, 8949657474523425376L, 3512905516507615961L, 7159725979618740301L, 965650005835137607L, 5727780783694992240L, 8151217634151930732L, 9164449253911987585L, 3818576177788313364L, 7331559403129590068L, 3054860942230650691L, 5865247522503672054L, 6133237568526430876L, 4692198018002937643L, 6751264462192099863L, 7507516828804700229L, 8957348732136404618L, 6006013463043760183L, 9010553393080078856L, 4804810770435008147L, 1674419492351197600L, 7687697232696013035L, 4523745595132871322L, 6150157786156810428L, 3618996476106297057L, 4920126228925448342L, 6584545995626947969L, 7872201966280717348L, 3156575963519296104L, 6297761573024573878L, 6214609585557347207L, 5038209258419659102L, 8661036483187788089L, 8061134813471454564L, 6478960743616640295L, 6448907850777163651L, 7027843002264267398L, 5159126280621730921L, 3777599994440458757L, 8254602048994769474L, 2354811176362823687L, 6603681639195815579L, 3728523348461214111L, 5282945311356652463L, 4827493086139926451L, 8452712498170643941L, 5879314530452927160L, 6762169998536515153L, 2858777216991386566L, 5409735998829212122L, 5976370588335019576L, 8655577598126739396L, 2183495311852210675L, 6924462078501391516L, 9125493878965589187L, 5539569662801113213L, 5455720695801516188L, 8863311460481781141L, 6884478705911470739L, 7090649168385424913L, 3662908557358221429L, 5672519334708339930L, 6619675660628487467L, 9076030935533343889L, 1368109020150804139L, 7260824748426675111L, 2939161623491598473L, 5808659798741340089L, 506654891422323617L, 4646927838993072071L, 2249998320508814055L, 7435084542388915313L, 9134020534926967972L, 5948067633911132251L, 1773193205828708893L, 4758454107128905800L, 8797252194146787761L, 7613526571406249281L, 4852231473780084609L, 6090821257124999425L, 2037110771653112526L, 4872657005699999540L, 1629688617322490021L, 7796251209119999264L, 2607501787715984033L, 6237000967295999411L, 3930675837543742388L, 4989600773836799529L, 1299866262664038749L, 7983361238138879246L, 5769134835004372321L, 6386688990511103397L, 2770633460632542696L, 5109351192408882717L, 7750529990618899641L, 8174961907854212348L, 5022150355506418780L, 6539969526283369878L, 7707069099147045347L, 5231975621026695903L, 631632057204770793L, 8371160993642713444L, 8389308921011453915L, 6696928794914170755L, 8556121544180118293L, 5357543035931336604L, 6844897235344094635L, 8572068857490138567L, 5417812354437685931L, 6857655085992110854L, 644901068808238421L, 5486124068793688683L, 2360595262417545899L, 8777798510069901893L, 1932278012497118276L, 7022238808055921514L, 5235171224739604944L, 5617791046444737211L, 6032811387162639117L, 8988465674311579538L, 5963149404718312264L, 7190772539449263630L, 8459868338516560134L, 5752618031559410904L, 6767894670813248108L, 9204188850495057447L, 5294608251188331487L };
        pow10 = new long[] { 1L, 10L, 100L, 1000L, 10000L, 100000L, 1000000L, 10000000L, 100000000L, 1000000000L, 10000000000L, 100000000000L, 1000000000000L, 10000000000000L, 100000000000000L, 1000000000000000L, 10000000000000000L, 100000000000000000L };
        LongBiFunction function = null;
        if (JDKUtils.JVM_VERSION > 8 && !JDKUtils.ANDROID) {
            try {
                final MethodHandles.Lookup lookup = JDKUtils.trustedLookup(DoubleToDecimal.class);
                final MethodType methodType = MethodType.methodType(Long.TYPE, Long.TYPE, Long.TYPE);
                final MethodHandle methodHandle = lookup.findStatic(Math.class, "multiplyHigh", methodType);
                final CallSite callSite = LambdaMetafactory.metafactory(lookup, "multiplyHigh", MethodType.methodType(LongBiFunction.class), methodType, methodHandle, methodType);
                function = callSite.getTarget().invokeExact();
            }
            catch (Throwable t) {}
        }
        if (function == null) {
            function = DoubleToDecimal::multiplyHigh;
        }
        MULTIPLY_HIGH = function;
    }
    
    @FunctionalInterface
    interface LongBiFunction
    {
        long multiplyHigh(final long p0, final long p1);
    }
}
