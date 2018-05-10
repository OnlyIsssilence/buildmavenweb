package com.spring.demo.redis;

import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.JedisCluster;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

/**
 * Author: MuYa
 * Date  : 2017/6/15
 * Description:带密码的redis集群
 */
public class RedisClusterPassword {

    private static RedisClusterPassword redisClusterComponent = new RedisClusterPassword();

    private static final String HOST_128 = "192.168.28.128";
    private static final String HOST_129 = "192.168.28.129";
    private static final String HOST_131 = "192.168.28.131";
    /**
     * redisCluster客户端
     */
    private JedisCluster redisCluster;
    /**
     * 客户端连接超时时间
     */
    private final static int TIME_OUT = 3000;

    /**
     * soket超时时间
     */
    private final static int SO_TIME_OUT = 3000;

    /**
     * 最大尝试次数
     */
    private final static int MAX_ATTEMP = 5;

    private RedisClusterPassword() {
        try {
            // i am
            // redis节点信息
            Set<HostAndPort> nodeList = new HashSet<HostAndPort>();
            nodeList.add(new HostAndPort(HOST_128, 7000));
            nodeList.add(new HostAndPort(HOST_128, 7001));
            nodeList.add(new HostAndPort(HOST_129, 7000));
            nodeList.add(new HostAndPort(HOST_129, 7001));
            nodeList.add(new HostAndPort(HOST_131, 7000));
            nodeList.add(new HostAndPort(HOST_131, 7001));
            //redisCluster = new JedisCluster(nodeList, getCommonPoolConfig()); 不需要密码
            redisCluster = new JedisCluster(nodeList, TIME_OUT,
                    SO_TIME_OUT, MAX_ATTEMP, "123456", getCommonPoolConfig());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    /**
     * pool配置
     * @return
     */
    public static GenericObjectPoolConfig getCommonPoolConfig() {
        GenericObjectPoolConfig poolConfig = new GenericObjectPoolConfig();
        poolConfig.setMaxTotal(GenericObjectPoolConfig.DEFAULT_MAX_TOTAL * 10);
        poolConfig.setMaxIdle(GenericObjectPoolConfig.DEFAULT_MAX_IDLE * 5);
        poolConfig.setMinIdle(GenericObjectPoolConfig.DEFAULT_MAX_IDLE * 2);
        // JedisPool.borrowObject最大等待时间
        poolConfig.setMaxWaitMillis(1000L);
        // 开启jmx
        poolConfig.setJmxEnabled(true);
        return poolConfig;
    }

    public static RedisClusterPassword getInstance() {
        return redisClusterComponent;
    }

    public void destroy() {
        if (redisCluster != null) {
            try {
                redisCluster.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public JedisCluster getRedisCluster() {
        return redisCluster;
    }

    public static void main(String[] args) {
        JedisCluster cluster = RedisClusterPassword.getInstance().getRedisCluster();
        cluster.set("testkey","testvalue");
        System.out.println(cluster.get("testkey"));
    }
}
