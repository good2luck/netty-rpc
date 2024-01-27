package top.xudj.protocol;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 协议（响应内容）
 * prc response
 */
@Getter
@Setter
@ToString
public class RpcResponse {

    /**
     * 响应id
     */
    private String requestId;

    /**
     * 异常
     */
    private String error;

    /**
     * 返回的结果
     */
    private Object result;

}
