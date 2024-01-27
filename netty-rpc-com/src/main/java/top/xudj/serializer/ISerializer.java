package top.xudj.serializer;

/**
 * 序列化接口
 */
public interface ISerializer {

    /**
     * 序列化
     * @param obj
     * @return
     */
    <T> byte[] serialize(T obj);

    /**
     * 反序列化
     * @param data
     * @param clazz
     * @return
     */
    <T> T deserialize(byte[] data, Class<T> clazz);

}
