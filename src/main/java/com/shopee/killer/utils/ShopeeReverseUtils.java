package com.shopee.killer.utils;

import java.security.NoSuchAlgorithmException;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

public class ShopeeReverseUtils {
    public static String reverseIfNonMatch(String params) throws NoSuchAlgorithmException {
        String md5LowerIfNonMatch = MD5Utils.MD5Lower("55b03" + MD5Utils.MD5Lower(params) + "55b03");
        return "55b03" + "-" + md5LowerIfNonMatch;
    }

    public static String genCliRequestID(AtomicInteger atomicInteger) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(UUID.randomUUID());
        stringBuilder.append(".");
        return b(atomicInteger, stringBuilder);
    }

    public static String b(AtomicInteger atomicInteger, StringBuilder sb) {
        sb.append(atomicInteger.getAndIncrement());
        return sb.toString();
    }
}
