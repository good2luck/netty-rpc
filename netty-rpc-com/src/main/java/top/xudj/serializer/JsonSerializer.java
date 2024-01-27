package top.xudj.serializer;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONReader;

/**
 * 使用Json序列化
 */
public class JsonSerializer implements ISerializer {

    private JSONReader.Feature[] features = {JSONReader.Feature.SupportClassForName};

    @Override
    public <T> byte[] serialize(T obj) {
        return JSON.toJSONBytes(obj);
    }

    @Override
    public <T> T deserialize(byte[] data, Class<T> clazz) {
        return JSON.parseObject(data, clazz, features);
    }

}
