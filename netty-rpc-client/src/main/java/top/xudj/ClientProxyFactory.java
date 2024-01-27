package top.xudj;

import java.lang.reflect.Proxy;

/**
 * 客户端的代理工厂
 */
public class ClientProxyFactory {

    /**
     * 创建代理对象
     *
     * @param interfaceClass: 接口类型
     * @param client: 客户端
     * @param <T>
     * @return
     */
    public static <T> T create(Class<T> interfaceClass, ClientNetty client) {
        return (T) Proxy.newProxyInstance(interfaceClass.getClassLoader(),
                new Class[]{interfaceClass},
                new RpcClientDynamicProxy(client));
    }

}

