package top.xudj.service;

import top.xudj.api.HelloService;
import top.xudj.api.IService;

/**
 * HelloService 实现类
 */
public class HelloServiceImpl implements HelloService, IService {

    @Override
    public String hello(String name) {
        return "Hello, " + name;
    }

}
