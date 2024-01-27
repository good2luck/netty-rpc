package top.xudj;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import top.xudj.api.IService;
import top.xudj.protocol.RpcRequest;
import top.xudj.protocol.RpcResponse;
import top.xudj.service.ServiceHelper;

import java.lang.reflect.Method;

/**
 * RPC服务处理器
 */
public class ServerHandler extends SimpleChannelInboundHandler<RpcRequest> {

    /**
     * 读取客户端发来的请求数据
     *
     * @param ctx: 通道处理器上下文
     * @param rpcRequest: 客户端发来的请求数据
     * @throws Exception
     */
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RpcRequest rpcRequest) throws Exception {
        RpcResponse rpcResponse = new RpcResponse();
        rpcResponse.setRequestId(rpcRequest.getRequestId());

        // 通过反射调用服务方法
        try {
            Object result = reflect(rpcRequest);
            System.out.println("ServerHandler.channelRead0: result = " + result);
            rpcResponse.setResult(result);
        } catch (Exception e) {
            rpcResponse.setError(e.getMessage());
            e.printStackTrace();
        }
        ctx.writeAndFlush(rpcResponse);
    }

    /**
     * 通过反射调用服务方法
     *
     * @param rpcRequest: 客户端发来的请求数据
     * @return
     */
    private Object reflect(RpcRequest rpcRequest) throws Exception {
        // 使用 Class forName
        Class<?> aClass = Class.forName(rpcRequest.getClassName());
        // 查找实现类
        IService serviceImpl = ServiceHelper.getServiceImpl(aClass);

        // 得到 Class getMethod
        Method method = serviceImpl.getClass().getMethod(rpcRequest.getMethodName(), rpcRequest.getParameterTypes());
        method.setAccessible(true);

        Object result = method.invoke(serviceImpl, rpcRequest.getParameters());
        return result;
    }
}
