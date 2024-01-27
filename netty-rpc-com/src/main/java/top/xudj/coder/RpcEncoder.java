package top.xudj.coder;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import top.xudj.serializer.ISerializer;

/**
 * rpc 编码器 使用netty的组件
 * @see io.netty.handler.codec.MessageToByteEncoder
 */
public class RpcEncoder extends MessageToByteEncoder {
    /**
     * 自定义的编码器
     */
    private ISerializer serializer;

    public RpcEncoder(ISerializer serializer) {
        this.serializer = serializer;
    }

    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, Object t, ByteBuf byteBuf) throws Exception {
        // 序列化
        byte[] bytes = serializer.serialize(t);
        // 写入长度
        byteBuf.writeInt(bytes.length);
        // 写入内容
        byteBuf.writeBytes(bytes);
    }

}
