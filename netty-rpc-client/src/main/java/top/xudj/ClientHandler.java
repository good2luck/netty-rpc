package top.xudj;

import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelPromise;
import top.xudj.protocol.RpcRequest;
import top.xudj.protocol.RpcResponse;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * 处理器，支持出入站数据处理
 * @see ChannelInboundHandlerAdapter
 * @see io.netty.channel.ChannelOutboundHandler
 */
public class ClientHandler extends ChannelDuplexHandler {

    /**
     * 使用Map维护请求对象ID与响应结果Future的映射关系
     * CompletableFuture: JDK8新增的异步编程工具类
     */
    private Map<String, CompletableFuture> futureMap = new ConcurrentHashMap<>();

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (msg instanceof RpcResponse) {
            RpcResponse rpcResponse = (RpcResponse) msg;
            // 解析结果
            CompletableFuture completableFuture = futureMap.get(rpcResponse.getRequestId());
            // 给到结果
            completableFuture.complete(rpcResponse);
        }
        // 触发下一个ChannelInboundHandler
        super.channelRead(ctx, msg);
    }

    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
        if (msg instanceof RpcRequest) {
            RpcRequest rpcRequest = (RpcRequest) msg;
            // 发送前，先将请求对象ID与响应结果Future的映射关系存入Map
            futureMap.put(rpcRequest.getRequestId(), new CompletableFuture());
        }
        super.write(ctx, msg, promise);
    }

    /**
     * 获取响应结果
     *
     * @param requestId: 请求对象ID
     * @return
     */
    public RpcResponse getRpcResponse(String requestId) {
        try {
            // 从Map中获取响应结果Future
            CompletableFuture completableFuture = futureMap.get(requestId);
            // 获取结果，如果没有结果会阻塞
            return (RpcResponse) completableFuture.get(30, TimeUnit.SECONDS);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

}
