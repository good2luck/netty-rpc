package top.xudj.coder;


import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import top.xudj.serializer.ISerializer;

import java.util.List;

/**
 * rpc 解码器 使用netty的组件
 * @see io.netty.handler.codec.ByteToMessageDecoder
 */
public class RpcDecoder extends ByteToMessageDecoder {

    // 注意，需要定义class，完成数据的解析成指定类型，不然ServerHandler会因为无法匹配类型无法执行
    private Class clazz;
    private ISerializer serializer;

    public RpcDecoder(Class clazz, ISerializer serializer) {
        this.clazz = clazz;
        this.serializer = serializer;
    }

    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> list) throws Exception {
        // 读取长度，首先要大于写入的int长度（4字节）
        if (byteBuf.readableBytes() < 4) {
            System.out.println("RpcDecoder.decode: byteBuf.readableBytes() < 4");
            return;
        }
        // 标记一下当前的readIndex的位置
        byteBuf.markReaderIndex();
        // 读取长度
        int dataLength = byteBuf.readInt();
        // 长度如果小于0，关闭连接
        if (dataLength <= 0) {
            System.out.println("RpcDecoder.decode: dataLength < 0");
            channelHandlerContext.close();
            return;
        }

        // 如果可读长度小于内容长度，resetReaderIndex
        if (byteBuf.readableBytes() < dataLength) {
            System.out.println("RpcDecoder.decode: byteBuf.readableBytes() < dataLength");
            byteBuf.resetReaderIndex();
            return;
        }

        // 读取内容
        byte[] data = new byte[dataLength];
        byteBuf.readBytes(data);
        // 反序列化
        Object obj = serializer.deserialize(data, clazz);
        list.add(obj);
    }
}
