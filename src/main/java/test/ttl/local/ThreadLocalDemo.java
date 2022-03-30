package test.ttl.local;

import test.ttl.bean.BusinessService;
import test.ttl.bean.LoginInterceptor;
import test.ttl.bean.ThreadLocalHolder;

/**
 * @author: chenyin
 * @date: 2019-10-22 13:13
 */
public class ThreadLocalDemo {
    public static void main(String[] args) {
        BusinessService businessService = new BusinessService();
        LoginInterceptor loginInterceptor = new LoginInterceptor();
        //模拟一个普通的同步web请求
        new Thread(() -> {
            // 模拟用户身份拦截器
            loginInterceptor.userInterceptor();
            System.out.println(Thread.currentThread().getName() + ":" + ThreadLocalHolder.getUser());
            // 拦截器通过后 同步处理业务
            businessService.doBusiness();
        }).start();

        //模拟一个普通的异步web请求
        new Thread(() -> {
            // 模拟用户身份拦截器
            loginInterceptor.userInterceptor();
            System.out.println(Thread.currentThread().getName() + ":" + ThreadLocalHolder.getUser());
            // 拦截器通过后 异步处理业务
            businessService.doBusinessAsync();
        }).start();
    }

}
