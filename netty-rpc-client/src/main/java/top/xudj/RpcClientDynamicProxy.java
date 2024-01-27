package top.xudj;


import top.xudj.protocol.RpcRequest;
import top.xudj.protocol.RpcResponse;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.Optional;
import java.util.UUID;

/**
 * RPC客户端动态代理
 */
public class RpcClientDynamicProxy implements InvocationHandler {

    private ClientNetty client;
    public RpcClientDynamicProxy(ClientNetty client) {
        this.client = client;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        RpcRequest request = new RpcRequest();
        String requestId = UUID.randomUUID().toString();

        String className = method.getDeclaringClass().getName();
        String methodName = method.getName();
        // 检查是否是Object类的方法
        // 为了防止在调试情况下，频繁调用toString方法，导致非预期rpc调用
        if (method.getDeclaringClass() == Object.class) {
            return handleObjectMethodInvoke(proxy, methodName, args);
        }
        Class<?>[] parameterTypes = method.getParameterTypes();

        request.setRequestId(requestId);
        request.setClassName(className);
        request.setMethodName(methodName);
        request.setParameterTypes(parameterTypes);
        request.setParameters(args);
        System.out.println("request: " + request);

        // 发送请求
        RpcResponse rpcResponse = client.send(request);
        System.out.println("rpcResponse: " + rpcResponse);

        return Optional.ofNullable(rpcResponse).map(r -> r.getResult()).orElse(null);
    }


    /**
     * 处理Object类的方法
     *
     * @param proxy: 代理对象
     * @param methodName: 方法名
     * @param args: 参数
     * @return
     */
    private Object handleObjectMethodInvoke(Object proxy, String methodName, Object[] args) {
        switch (methodName) {
            case "toString":
                return proxy.getClass().getName() + "@" + Integer.toHexString(System.identityHashCode(proxy));
            case "hashCode":
                return System.identityHashCode(proxy);
            case "equals":
                return proxy == args[0];
        }
        throw new UnsupportedOperationException(methodName);
    }

}
