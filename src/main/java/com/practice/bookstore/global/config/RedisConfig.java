package com.practice.bookstore.global.config;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.security.jackson2.SecurityJackson2Modules;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;

@Configuration
@EnableRedisHttpSession     //spring session을 Redis에 저장하겠다고 선언
public class RedisConfig {

    /*
    * [핵심] Spring Session이 사용할 직렬화 도구를 교체하는 Bean
    * 이 Bean 이름이 'springSessionDefaultRedisSerializer'여야 Spring Session이 자동으로 인식함.
    * */
    @Bean
    public RedisSerializer<Object> springSessionDefaultRedisSerializer(){
        // 1. Jackson이 날짜를 알 수 있게 ObjectMapper를 커스텀
        ObjectMapper objectMapper = new ObjectMapper();

        // [핵심] Java 8 날짜 모듈 등록 (LocalDateTime 해결)
        objectMapper.registerModule(new JavaTimeModule());
        // (선택) 날짜를 [2025,12,4,...] 배열이 아니라 "2025-12-04..." 문자열로 저장
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        // Spring Security 전용 모듈 등록
        // UsernamePasswordAuthenticationToken 같은 시큐리티 내부 객체를 Json으로 변환하는 방법을 알려줌
        objectMapper.registerModules(SecurityJackson2Modules.getModules(getClass().getClassLoader()));

        // [중요] Redis에 저장될 때 클래스 타입 정보(@class)를 포함하도록 설정
        // (이게 없으면 나중에 꺼낼 때 무슨 객체인지 몰라서 에러 남)
        objectMapper.activateDefaultTyping(
          objectMapper.getPolymorphicTypeValidator(),
          ObjectMapper.DefaultTyping.NON_FINAL,
          JsonTypeInfo.As.PROPERTY
        );

        // 2. 커스텀한 Mapper를 사용하는 Serializer 반환
        return new GenericJackson2JsonRedisSerializer(objectMapper);
    }

    /*
    * [선택] 수동으로 RedisTemplate을 쓸 때(캐시 등)를 위한 설정
    * Key는 Stirng, Value는 Json으로 저장
     */
    @Bean
    public RedisTemplate<String,Object> redisTemplate(RedisConnectionFactory connectionFactory){
        RedisTemplate<String,Object> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(connectionFactory);

        // Key 직렬화 : String
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setHashKeySerializer(new StringRedisSerializer());

        // Value 직렬화 : JSON
        redisTemplate.setValueSerializer(new GenericJackson2JsonRedisSerializer());
        redisTemplate.setHashValueSerializer(new GenericJackson2JsonRedisSerializer());

        return redisTemplate;
    }
}
