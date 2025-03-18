package Obsidian.demo.config;

import java.time.Duration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.jsontype.impl.LaissezFaireSubTypeValidator;

@Configuration
@EnableCaching
public class RedisConfig {

	@Value(value = "${spring.redis.host}")
	private String redisHost; // localhost

	@Value(value = "${spring.redis.port}")
	private int redisPort; // 6379

	@Bean
	public RedisConnectionFactory redisConnectionFactory() {
		LettuceConnectionFactory factory = new LettuceConnectionFactory(redisHost, redisPort);
		factory.setValidateConnection(true); // Redis 연결 복구 설정 추가
		return factory;
		}

	/**
	 * RedisTemplate 을 통해서 JSON 데이터 직렬화를 진행함
	 */
	@Bean
	public RedisTemplate<String, Object> redisTemplate() {
		RedisTemplate<String, Object> template = new RedisTemplate<>();
		template.setConnectionFactory(redisConnectionFactory());

		// JSON 직렬화 설정 추가
		ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.configure(SerializationFeature.ORDER_MAP_ENTRIES_BY_KEYS, false);
		objectMapper.configure(MapperFeature.SORT_PROPERTIES_ALPHABETICALLY, false);
		objectMapper.activateDefaultTyping(
			LaissezFaireSubTypeValidator.instance, ObjectMapper.DefaultTyping.NON_FINAL
		);

		GenericJackson2JsonRedisSerializer serializer = new GenericJackson2JsonRedisSerializer(objectMapper);

		template.setKeySerializer(new StringRedisSerializer());
		template.setValueSerializer(serializer);
		template.setHashKeySerializer(new StringRedisSerializer());
		template.setHashValueSerializer(serializer);

		template.afterPropertiesSet();
		return template;
	}

	// TTL(캐시 만료 시간) 설정 추가
	@Bean
	public RedisCacheManager cacheManager(RedisConnectionFactory connectionFactory) {

		// 5분(300초) 로 TTL 초기화
		RedisCacheConfiguration cacheConfig = RedisCacheConfiguration.defaultCacheConfig()
			.entryTtl(Duration.ofMinutes(5))
			.disableCachingNullValues();

		return RedisCacheManager.builder(connectionFactory)
			.cacheDefaults(cacheConfig)
			.build();
	}

}
