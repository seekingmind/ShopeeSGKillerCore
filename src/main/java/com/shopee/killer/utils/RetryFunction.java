package com.shopee.killer.utils;

import lombok.extern.slf4j.Slf4j;

import java.util.function.Supplier;

@Slf4j
public class RetryFunction<R> {
    public static <R> R retry(long retryCount, long interval, Supplier<R> supplier) {
        Long retryCountBack = retryCount;
        Boolean success = Boolean.FALSE;
        R result = null;

        // 还有重试次数并且尚未请求成功的时候
        while (retryCount > 0 && !success) {
            try {
                result = supplier.get();
                if (result != null) {
                    success = Boolean.TRUE;
                }
            } catch (Exception e) {
                log.error("接口请求失败，本次错误的信息为：{}, 剩余尝试次数为：{}", e.getMessage(), retryCount);
            }

            retryCount--;

            try {
                Thread.sleep(interval);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

        if (result == null) {
            log.error("接口请求失败，重试次数已用完，重试次数为：{}", retryCountBack);
        }

        return result;
    }
}
