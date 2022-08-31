package com.kanke.gateway.config;

import java.util.concurrent.TimeUnit;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.kanke.gateway.entry.StoreToken;

@Configuration
public class CacheConfig {

	public static long matchDuration = 10;
	public static TimeUnit matchUnit = TimeUnit.SECONDS;

	@Bean("storeTokenCache")
	public Cache<String, StoreToken> videoCache() {
		Cache<String, StoreToken> cache = Caffeine.newBuilder().expireAfterWrite(matchDuration, matchUnit)
				.expireAfterAccess(matchDuration, matchUnit).maximumSize(10_000).build((k) -> new StoreToken());
		return cache;
	}

}
