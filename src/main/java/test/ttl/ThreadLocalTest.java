package test.ttl;
/**
 * @author xavior.wx(微光)  xavior.wx@alibaba-inc.com
 * @version ttl.ThreadLocalTest 1.0.0
 * @date 2022/3/29 19:21
 */
public class ThreadLocalTest {
    /**
     * 如果把下面的InheritableThreadLocal换成ThreadLocal的话，在子线程里的输出将为是空
     */
    public static final ThreadLocal<Integer> THREAD_LOCAL = new InheritableThreadLocal<>();

    public static void main(String[] args) {
        THREAD_LOCAL.set(456);
        Thread thread = new MyThread();
        thread.start();
        System.out.println("main = " + THREAD_LOCAL.get());
        THREAD_LOCAL.remove();
    }

    static class MyThread extends Thread {
        @Override
        public void run() {
            System.out.println("MyThread = " + THREAD_LOCAL.get());
        }
    }
}
