package top.xudj;

import top.xudj.constant.Constants;

/**
 * 服务端演示类
 */
public class Server {

    public static void main(String[] args) {
        ServerNetty serverNetty = new ServerNetty();
        serverNetty.bind(Constants.PORT);
    }

}
