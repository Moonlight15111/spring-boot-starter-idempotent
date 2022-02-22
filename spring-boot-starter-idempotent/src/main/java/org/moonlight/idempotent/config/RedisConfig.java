package org.moonlight.idempotent.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import redis.clients.jedis.JedisPoolConfig;

/**
 * 功能描述:
 * redis的配置
 * 主要逻辑:
 * <p>
 * 注意事项:
 *
 * @author Moonlight
 * @date 2021-09-17 14:45
 */
@Configuration
public class RedisConfig {

    /**
     * redis 服务器地址
     */
    @Value("${spring.redis.host}")
    private String host;

    /**
     * redis 数据库索引(默认0)
     */
    @Value("${spring.redis.database}")
    private int database;

    /**
     * redis 端口号
     */
    @Value("${spring.redis.port}")
    private int port;

    /**
     * redis 服务器密码
     */
    @Value("${spring.redis.password}")
    private String password;

    /**
     * redis 超时时间
     */
    @Value("${spring.redis.timeout}")
    private int timeout;

    /**
     * redis 连接池最大连接数(使用负值无限制)
     */
    @Value("${spring.redis.jedis.pool.max-active}")
    private int maxActive;

    /**
     * redis 连接池最大阻塞等待时间(负值无限制)
     */
    @Value("${spring.redis.jedis.pool.max-wait}")
    private int maxWait;

    /**
     * redis 连接池最大空闲数
     */
    @Value("${spring.redis.jedis.pool.max-idle}")
    private int maxIdle;

    /**
     * redis 连接池小空闲数
     */
    @Value("${spring.redis.jedis.pool.min-idle}")
    private int minIdle;

    /**
     * 功能描述: jedis连接池配置
     * @return JedisPoolConfig
     * @author moonlight
     * @date 2021/9/17 15:03
     **/
    @Bean
    public JedisPoolConfig jedisPool() {
        JedisPoolConfig config = new JedisPoolConfig();
        config.setMaxTotal(maxActive);
        config.setMaxIdle(maxIdle);
        config.setMinIdle(minIdle);
        config.setMaxWaitMillis(maxWait);
        return config;
    }

    /**
     * 功能描述: redis连接信息配置
     * @return RedisStandaloneConfiguration
     * @author moonlight
     * @date 2021/9/17 15:07
     **/
    @Bean
    public RedisStandaloneConfiguration jedisConfig() {
        RedisStandaloneConfiguration configuration = new RedisStandaloneConfiguration();
        configuration.setHostName(host);
        configuration.setPort(port);
        configuration.setPassword(password);
        configuration.setDatabase(database);
        return configuration;
    }

    /**
     * 功能描述: 创建redis连接工厂
     * @param jedisPool jedis连接池配置
     * @param jedisConfig redis的连接配置
     * @return RedisConnectionFactory
     * @author moonligh
     * @date 2021/9/17 15:14
     **/
    @Bean
    public RedisConnectionFactory redisConnectionFactory(JedisPoolConfig jedisPool, RedisStandaloneConfiguration jedisConfig) {
        JedisConnectionFactory connectionFactory = new JedisConnectionFactory(jedisConfig);
        connectionFactory.setPoolConfig(jedisPool);
        return connectionFactory;
    }

    /**
     * 功能描述: 构建一个redisTemplate
     * @return RedisTemplate
     * @author moonlight
     **/
    @Bean("redisTemplate")
    public RedisTemplate<String, Object> redisTemplate() {
        /*
         * Redis 序列化器.
         *
         * RedisTemplate 默认的系列化类是 JdkSerializationRedisSerializer,用JdkSerializationRedisSerializer序列化的话,
         * 被序列化的对象必须实现Serializable接口。在存储内容时，除了属性的内容外还存了其它内容在里面，总长度长，且不容易阅读。
         *
         * Jackson2JsonRedisSerializer 和 GenericJackson2JsonRedisSerializer，两者都能系列化成 json，
         * 但是后者会在 json 中加入 @class 属性，类的全路径包名，方便反系列化。前者如果存放了 List 则在反系列化的时候如果没指定
         * TypeReference 则会报错 java.util.LinkedHashMap cannot be cast to
         */
        RedisSerializer<String> redisKeySerializer = new StringRedisSerializer();
        RedisSerializer<Object> redisValueSerializer = new GenericJackson2JsonRedisSerializer();

        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();

        // key 的序列化采用 StringRedisSerializer
        redisTemplate.setKeySerializer(redisKeySerializer);
        redisTemplate.setHashKeySerializer(redisKeySerializer);
        // value 值的序列化采用 GenericJackson2JsonRedisSerializer
        redisTemplate.setValueSerializer(redisValueSerializer);
        redisTemplate.setHashValueSerializer(redisValueSerializer);

        redisTemplate.setConnectionFactory(redisConnectionFactory(jedisPool(), jedisConfig()));

        return redisTemplate;
    }
}