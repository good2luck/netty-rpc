package top.xudj;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LoggingHandler;
import top.xudj.coder.RpcDecoder;
import top.xudj.coder.RpcEncoder;
import top.xudj.constant.Constants;
import top.xudj.protocol.RpcRequest;
import top.xudj.protocol.RpcResponse;
import top.xudj.serializer.JsonSerializer;

/**
 * RPC服务端
 */
public class ServerNetty {

    /**
     * 绑定端口
     * @param port
     */
    public void bind(int port) {
        EventLoopGroup boss = new NioEventLoopGroup(1);
        EventLoopGroup worker = new NioEventLoopGroup();

        // 服务器netty启动器
        ServerBootstrap serverBootstrap = new ServerBootstrap();
        serverBootstrap.group(boss, worker)
                .option(ChannelOption.SO_BACKLOG, 1024)
                .channel(NioServerSocketChannel.class)
                .handler(new LoggingHandler())
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel socketChannel) throws Exception {
                        // in顺序: RpcDecoder -> ServerHandler
                        // out顺序: ServerHandler -> RpcEncoder
                        ChannelPipeline pipeline = socketChannel.pipeline();
                        // 添加编码器
                        pipeline.addLast(new RpcDecoder(RpcRequest.class, new JsonSerializer()));
                        pipeline.addLast(new RpcEncoder(new JsonSerializer()));
                        // 添加自定义处理器
                        pipeline.addLast(new ServerHandler());
                    }
                });
        try {
            // 异步等待启动
            ChannelFuture channelFuture = serverBootstrap.bind(port).sync();
            System.out.println("server start on port: " + port);
            // 等待服务器关闭
            channelFuture.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            System.out.println("server shutdown");
            // 优雅关闭
            boss.shutdownGracefully();
            worker.shutdownGracefully();
        }
    }

}
