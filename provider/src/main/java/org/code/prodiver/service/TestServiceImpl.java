package org.code.prodiver.service;


import org.code.TestService;
import org.code.annitation.RpcService;

/**
 * The type Test service.
 */
@RpcService
public class TestServiceImpl implements TestService {
    @Override
    public void test(String key) {
        System.out.println("服务提供2 test 测试成功  :" + key);
    }

    @Override
    public void test2(String key) {
        System.out.println("服务提供2 tes2 测试成功  :" + key);
    }
}
