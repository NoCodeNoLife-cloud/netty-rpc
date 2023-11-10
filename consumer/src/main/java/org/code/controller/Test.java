package org.code.controller;

import org.code.Test2Service;
import org.code.TestService;
import org.code.annitation.RpcReference;
import org.code.common.constants.FaultTolerantRules;
import org.code.common.constants.LoadBalancerRules;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * The type Test.
 */
@RestController
public class Test {
	/**
	 * The Test service.
	 */
	@RpcReference(timeout = 10000L, faultTolerant = FaultTolerantRules.Failover, loadBalancer = LoadBalancerRules.RoundRobin)
	TestService testService;
	/**
	 * The Test 2 service.
	 */
	@RpcReference(loadBalancer = LoadBalancerRules.ConsistentHash)
	Test2Service test2Service;

	/**
	 * 轮询
	 * 会触发故障转移,提供方模拟异常
	 * @param key the key
	 * @return string
	 */
	@RequestMapping("test/{key}")
	public String test(@PathVariable String key) {
		testService.test(key);
		return "test1 ok";
	}

	/**
	 * 一致性哈希
	 * @param key the key
	 * @return string
	 */
	@RequestMapping("test2/{key}")
	public String test2(@PathVariable String key) {

		return test2Service.test(key);
	}

	/**
	 * 轮询,无如何异常
	 * @param key the key
	 * @return string
	 */
	@RequestMapping("test3/{key}")
	public String test3(@PathVariable String key) {
		testService.test2(key);
		return "test2 ok";
	}
}
