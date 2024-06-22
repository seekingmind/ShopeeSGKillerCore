/**
 * 该文件用于根据传入参数，生成对应 so 文件的名称
 */

package com.shopee.killer.utils;

import java.io.UnsupportedEncodingException;

public class ShopeeSoNameDeal {
    public static final char[] vvwuwwwww = "0123456789ABCDEF".toCharArray();
    private static final String wuvvuwvuw = "UTF-8";

    private static byte[] uvwwvuuwu(byte[] bArr, String str) {
        int length = bArr.length;
        int length2 = str.length();
        int i = 0;
        int i2 = 0;
        while (i < length) {
            if (i2 >= length2) {
                i2 = 0;
            }
            bArr[i] = (byte) (bArr[i] ^ str.charAt(i2));
            i++;
            i2++;
        }
        return bArr;
    }

    public static byte[] uwvuvvwwv(String str) {
        int length = str.length();
        byte[] bArr = new byte[length / 2];
        for (int i = 0; i < length; i += 2) {
            bArr[i / 2] = (byte) (Character.digit(str.charAt(i + 1), 16) + (Character.digit(str.charAt(i), 16) << 4));
        }
        return bArr;
    }

    public static boolean uwvuwuuwu(String str, String str2) {
        return str != null && str.length() * 2 >= 65535;
    }

    public static String vwuvwwuvu(String str, String str2) {
        try {
            return new String(uvwwvuuwu(uwvuvvwwv(str), str2), "UTF-8");
        } catch (UnsupportedEncodingException unused) {
            return new String(uvwwvuuwu(uwvuvvwwv(str), str2));
        }
    }
}
