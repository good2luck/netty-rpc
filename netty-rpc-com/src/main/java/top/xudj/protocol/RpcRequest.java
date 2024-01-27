package top.xudj.protocol;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

/**
 * 协议（请求内容）
 * prc request
 */
@Getter
@Setter
@ToString
public class RpcRequest implements Serializable {
    private static final long serialVersionUID = 1L;
    // 为下面变量添加注释

    /**
     * 请求id
     */
    private String requestId;

    /**
     * 类名
     */
    private String className;

    /**
     * 方法名
     */
    private String methodName;

    /**
     * 参数类型
     */
    private Class<?>[] parameterTypes;

    /**
     * 参数
     */
    private Object[] parameters;


}
