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

    public static void main(String[] args) {
        String vwvwuvvwu = ShopeeSoNameDeal.vwuvwwuvu("47", "uvwvuwwww");
        System.out.println(vwvwuvvwu);

        String wvwvwuvuw = ShopeeSoNameDeal.vwuvwwuvu("13170317", "wvwvwuvuw");
        System.out.println(wvwvwuvuw);

        String wuwvuuwwu = ShopeeSoNameDeal.vwuvwwuvu("1413103F1B1318", "wuwvuuwwu");
        System.out.println(wuwvuuwwu);

        String wuvvuuvwu = ShopeeSoNameDeal.vwuvwwuvu("424D4B121305253F343207044C551C18040116191A221C1813571C04551803191956180757101B06010C", "wuvvuuvwu");
        System.out.println(wuvvuuvwu);

        String wuuwwvuuv = ShopeeSoNameDeal.vwuvwwuvu("424D48131106263D373207074D57151A1B02120D01571E05551B031B19", "wuuwwvuuv");
        System.out.println(wuuwwvuuv);

        String uvvwuuuuv = ShopeeSoNameDeal.vwuvwwuvu("404E4B", "uvvwuuuuv");
        System.out.println(uvvwuuuuv);

        String vwvwuvvwu1 = ShopeeSoNameDeal.vwuvwwuvu("041E051C21191D121B", "vwvwuvvwu");
        System.out.println(vwvwuvvwu1);

        String wuuwwuuuw = ShopeeSoNameDeal.vwuvwwuvu("2221335A4F", "wuuwwuuuw");
        System.out.println(wuuwwuuuw);

        String wvwwwuvvw = ShopeeSoNameDeal.vwuvwwuvu("12181418131056151619561918035514135719031B1B", "wvwwwuvvw");
        System.out.println(wvwwwuvvw);

        String wuuwwuuuw1 = ShopeeSoNameDeal.vwuvwwuvu("2221335A4F", "wuuwwuuuw");
        System.out.println(wuuwwuuuw1);
    }
}
