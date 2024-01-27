package top.xudj;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import top.xudj.coder.RpcDecoder;
import top.xudj.coder.RpcEncoder;
import top.xudj.protocol.RpcRequest;
import top.xudj.protocol.RpcResponse;
import top.xudj.serializer.JsonSerializer;

import java.net.InetSocketAddress;

/**
 * 客户端
 */
public class ClientNetty {

    // 事件循环组
    private EventLoopGroup eventLoopGroup;
    // 处理器
    private ClientHandler clientHandler;
    // 通道
    private Channel channel;

    /**
     * 连接服务端
     * @param host: 服务端地址
     * @param port: 服务端端口
     */
    public void connect(String host, int port) {
        eventLoopGroup = new NioEventLoopGroup();
        clientHandler = new ClientHandler();
        // 启动类
        Bootstrap bootstrap = new Bootstrap();
        bootstrap.group(eventLoopGroup);
        bootstrap.option(ChannelOption.SO_KEEPALIVE, true);
        bootstrap.option(ChannelOption.TCP_NODELAY, true);
        bootstrap.option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 5000);
        bootstrap.channel(NioSocketChannel.class);
        bootstrap.handler(new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel channel) throws Exception {
                // in顺序: RpcDecoder -> clientHandler
                // out顺序: clientHandler -> RpcEncoder
                ChannelPipeline pipeline = channel.pipeline();
                // 添加编解码器 RpcEncoder未指定范型
                pipeline.addLast(new RpcEncoder(new JsonSerializer()));
                pipeline.addLast(new RpcDecoder(RpcResponse.class, new JsonSerializer()));

                // 添加自定义的处理器
                pipeline.addLast(clientHandler);
            }
        });


        try {
            // 等待连接成功
            ChannelFuture connectFuture = bootstrap.connect(new InetSocketAddress(host, port))
                    .addListener((ChannelFutureListener) channelFuture ->
                            System.out.println("客户端连接状态:" + channelFuture.isSuccess())).sync();
            channel = connectFuture.channel();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * 发送请求
     *
     * @param rpcRequest: 请求对象
     * @return
     */
    public RpcResponse send (final RpcRequest rpcRequest) {
        // 发送请求并等待
        try {
            channel.writeAndFlush(rpcRequest).sync();
        } catch (Exception e) {
            e.printStackTrace();
        }
        // 获取响应结果
        return clientHandler.getRpcResponse(rpcRequest.getRequestId());
    }

    /**
     * 关闭通道
     */
    public void shutdownGracefully() {
        System.out.println("客户端优雅关闭");
        eventLoopGroup.shutdownGracefully();
    }
}
