package cn.woodwhales.gray.common.config.gateway;

/**
 * @author woodwhales on 2023-01-04 1:00
 */
public class GlobalContext {

    private static ThreadLocal<String> threadLocal = new ThreadLocal<String>();

    public static void setCurrentEnvironment(String currentEnvironmentVsersion) {
        threadLocal.set(currentEnvironmentVsersion);
    }

    public static String getCurrentEnvironment() {
        return threadLocal.get();
    }

}
