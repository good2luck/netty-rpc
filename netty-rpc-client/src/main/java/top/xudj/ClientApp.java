package top.xudj;

import sun.misc.ProxyGenerator;
import top.xudj.api.HelloService;
import top.xudj.constant.Constants;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Scanner;

/**
 * Client 演示类
 */
public class ClientApp {

    public static void main(String[] args) {
        // 创建客户端
        ClientNetty client = new ClientNetty();
        client.connect(Constants.HOST, Constants.PORT);
        try {
            HelloService helloService = ClientProxyFactory.create(HelloService.class, client);
            // 生成代理类
            // buildProxy();
            while (true) {
                // 从控制台读取
                Scanner scanner = new Scanner(System.in);
                String nextLine = scanner.nextLine();
                if ("exit".equals(nextLine)) {
                    System.exit(0);
                }
                String hello = helloService.hello(nextLine);
                System.out.println(hello);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            client.shutdownGracefully();
        }
    }

    /**
     * 可以生成代理类class
     */
    public static void buildProxy() {
        byte[] bytes = ProxyGenerator.generateProxyClass("HelloService$proxy", new Class[]{HelloService.class});
        String fileName = System.getProperty("user.dir")+"/netty-rpc-client/target/HelloService$proxy.class";
        try {
            File file = new File(fileName);
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            fileOutputStream.write(bytes);
            fileOutputStream.flush();
            fileOutputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
