package test.ttl;

import java.util.Arrays;

/**
 * @author xavior.wx(微光)  xavior.wx@alibaba-inc.com
 * @version ttl.ThreadStackInfoDemo 1.0.0  (*^▽^*)
 * @date 2022/3/30 15:56       (๑•̀ㅂ•́)و✧
 * @since 1.0.0              (*￣︶￣)
 */
public class ThreadStackInfoDemo {
    public static void main(String[] args) {
        aone();
    }

    public static void aone() {
        Arrays.stream(Thread.currentThread().getStackTrace()).forEach(stackTraceElement -> System.out.println(stackTraceElement));

    }
}
