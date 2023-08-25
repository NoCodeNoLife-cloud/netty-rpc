package org.code.prodiver.service;

import org.code.Test2Service;
import org.code.annitation.RpcService;

/**
 * The type Test 2 service.
 */
@RpcService
public class Test2ServiceImpl implements Test2Service {
    @Override
    public String test(String key) {
        System.out.println("服务提供2 test2 测试成功 :" + key);
        return key;
    }
}
