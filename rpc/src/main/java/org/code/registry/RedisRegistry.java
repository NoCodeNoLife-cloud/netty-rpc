package org.code.registry;

import org.code.common.RpcServiceNameBuilder;
import org.code.common.ServiceMeta;
import org.code.config.RpcProperties;
import org.springframework.util.ObjectUtils;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * The type Redis registry.
 */
public class RedisRegistry implements RegistryService {

    private static final int ttl = 10 * 1000;
    private JedisPool jedisPool;
    private String UUID;
    private Set<String> serviceMap = new HashSet<>();

    private ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();

    /**
     * Register the current service, register the current service ip, port, and time into redis, and start the scheduled task
     * Use collections to store service node information
     */
    public RedisRegistry() {
        RpcProperties properties = RpcProperties.getInstance();
        String[] split = properties.getRegisterAddr().split(":");
        JedisPoolConfig poolConfig = new JedisPoolConfig();
        poolConfig.setMaxTotal(10);
        poolConfig.setMaxIdle(5);
        jedisPool = new JedisPool(poolConfig, split[0], Integer.valueOf(split[1]));
        this.UUID = java.util.UUID.randomUUID().toString();
        // health monitoring
        heartbeat();
    }

    private void heartbeat() {
        int sch = 5;
        scheduledExecutorService.scheduleWithFixedDelay(() -> {
            for (String key : serviceMap) {
                // 1. Obtain all service nodes, and check whether the expiration time of the service node is < the current time. If it is less than, you have the right to delete all service information under the node
                List<ServiceMeta> serviceNodes = listServices(key);
                Iterator<ServiceMeta> iterator = serviceNodes.iterator();
                while (iterator.hasNext()) {
                    ServiceMeta node = iterator.next();
                    // 1. Delete expired service
                    if (node.getEndTime() < new Date().getTime()) {
                        iterator.remove();
                    }
                    // 2. Self-renewal
                    if (node.getUUID().equals(this.UUID)) {
                        node.setEndTime(node.getEndTime() + ttl / 2);
                    }
                }
                // reload service
                if (!ObjectUtils.isEmpty(serviceNodes)) {
                    loadService(key, serviceNodes);
                }
            }

        }, sch, sch, TimeUnit.SECONDS);
    }

    private List<ServiceMeta> listServices(String key) {
        Jedis jedis = getJedis();
        List<String> list = jedis.lrange(key, 0, -1);
        jedis.close();
        List<ServiceMeta> serviceMetas = list.stream().map(o -> JSON.parseObject(o, ServiceMeta.class)).collect(Collectors.toList());
        return serviceMetas;
    }

    private void loadService(String key, List<ServiceMeta> serviceMetas) {
        String script = "redis.call('DEL', KEYS[1])\n" +
                "for i = 1, #ARGV do\n" +
                "   redis.call('RPUSH', KEYS[1], ARGV[i])\n" +
                "end \n" +
                "redis.call('EXPIRE', KEYS[1],KEYS[2])";
        List<String> keys = new ArrayList<>();
        keys.add(key);
        keys.add(String.valueOf(10));
        List<String> values = serviceMetas.stream().map(o -> JSON.toJSONString(o)).collect(Collectors.toList());
        Jedis jedis = getJedis();
        jedis.eval(script, keys, values);
        jedis.close();
    }

    private Jedis getJedis() {
        Jedis jedis = jedisPool.getResource();
        RpcProperties properties = RpcProperties.getInstance();
        jedis.auth(properties.getRegisterPsw());
        return jedis;
    }

    @Override
    public void register(ServiceMeta serviceMeta) throws Exception {
        String key = RpcServiceNameBuilder.buildServiceKey(serviceMeta.getServiceName(), serviceMeta.getServiceVersion());
        if (!serviceMap.contains(key)) {
            serviceMap.add(key);
        }
        serviceMeta.setUUID(this.UUID);
        serviceMeta.setEndTime(new Date().getTime() + ttl);
        Jedis jedis = getJedis();
        String script = "redis.call('RPUSH', KEYS[1], ARGV[1])\n" +
                "redis.call('EXPIRE', KEYS[1], ARGV[2])";
        List<String> value = new ArrayList<>();
        value.add(JSON.toJSONString(serviceMeta));
        value.add(String.valueOf(10));
        jedis.eval(script, Collections.singletonList(key), value);
        jedis.close();
    }

    @Override
    public void unRegister(ServiceMeta serviceMeta) throws Exception {

    }


    @Override
    public List<ServiceMeta> discoveries(String serviceName) {
        return listServices(serviceName);
    }

    @Override
    public void destroy() throws IOException {

    }


}
