package org.dubbotest.server.utils;

public class CommonUtils {
    public static void randomSleep() {
        try {
            int min = 1; // 最小休眠时间：1ms
            int max = 100; // 最大休眠时间：100ms
            int sleepTime = (int) (Math.random() * (max - min + 1) + min); // 生成 [min, max] 范围内的随机数
            Thread.sleep(sleepTime); // 随机休眠
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt(); // 恢复中断状态
        }
    }
}
