package test.ttl.local;

import lombok.Data;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author xavior.wx(微光)  xavior.wx@alibaba-inc.com
 * @version ttl.local.InheritableNoUseTtlDemo 1.0.0  (*^▽^*)
 * @date 2022/3/29 20:19       (๑•̀ㅂ•́)و✧
 * @since 1.0.0              (*￣︶￣)
 */
public class InheritableNoUseTtlDemo {
    private static ThreadLocal<Map<String, Integer>> holder = new InheritableThreadLocal<Map<String, Integer>>() {
        @Override
        protected Map<String, Integer> initialValue() {
            return new HashMap<>(10);
        }
    };

    private static final ExecutorService threadPool = Executors.newFixedThreadPool(2);


    @Data
    public static class WrapedRunnable implements Runnable {

        private Map<String,Integer> attachment;

        private Runnable runnable;

        public WrapedRunnable(Runnable runnable) {
            this.runnable = runnable;
            this.attachment = new HashMap<>(holder.get());
        }

        @Override
        public void run() {
            holder.set(this.attachment);
            runnable.run();
        }
    }

    @Test
    public void testMandatoryTTL() {

        Executor executor = Executors.newFixedThreadPool(1);
        executor.execute(() -> {
            System.out.println("init");
        });

        HashMap<String,Integer> attachment = new HashMap<>();
        attachment.put("1", 2);
        holder.set(attachment);

        //普通方式
        executor.execute(() -> {
            System.out.println(holder.get().containsKey("1"));
        });

        //处理后的方式
        executor.execute(new WrapedRunnable(() -> {
            System.out.println(holder.get().containsKey("1"));
        }));
    }


    @Test
    public void testInheritableNoUseTtlDemo() throws InterruptedException {
        for (int i = 0; i < 10; i++) {
            new TomcatThread(i).start();
        }

        Thread.sleep(3000);
        threadPool.shutdown();
    }

    static class TomcatThread extends Thread {
        //线程下标
        int index;

        public TomcatThread(int index) {
            this.index = index;

        }

        @Override
        public void run() {
            String parentThreadName = Thread.currentThread().getName();
            //父线程中将index值塞入线程上下文变量
            System.out.println(parentThreadName + ":" + index);
            holder.get().put(parentThreadName, index);

            threadPool.submit(new WrapedRunnable(new BusinessThread(parentThreadName)));
        }
    }

    static class BusinessThread implements Runnable {
        //父进程名称
        private String parentThreadName;

        public BusinessThread(String parentThreadName) {
            this.parentThreadName = parentThreadName;
        }

        @Override
        public void run() {
            System.out.println("parent:" + parentThreadName + ":" + holder.get().get(parentThreadName));
        }
    }
}
