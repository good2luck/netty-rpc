package top.xudj.service;

import top.xudj.api.HelloService;
import top.xudj.api.IService;

import java.util.HashMap;
import java.util.Map;

/**
 * 获取service的实现类
 */
public class ServiceHelper {

    public static Map<Class, IService> serviceMap = new HashMap<>();
    static {
        serviceMap.put(HelloService.class, new HelloServiceImpl());
    }

    /**
     * 获取service的实现类
     *
     * @param clazz: 类类型
     * @return
     */
    public static IService getServiceImpl(Class clazz) {
        return serviceMap.get(clazz);
    }

}
