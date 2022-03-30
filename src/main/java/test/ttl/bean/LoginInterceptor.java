package test.ttl.bean;

public class LoginInterceptor {
    /**
     * 模拟拦截方法
     */
    public void userInterceptor() {
        UserInfo userInfo = getUserFromRedis();
        ThreadLocalHolder.setUser(userInfo);
    }

    /**
     * 模拟从redis中获取信息，这里写死直接返回
     *
     * @return
     */
    public UserInfo getUserFromRedis() {
        UserInfo userInfo = new UserInfo();
        userInfo.setId(1L);
        userInfo.setUserName("chenyin");
        return userInfo;
    }
}
