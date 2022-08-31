package com.kanke.gateway.encrypt;

import static org.springframework.util.CollectionUtils.unmodifiableMultiValueMap;

import java.io.IOException;
import java.net.URI;

import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.data.redis.core.ReactiveStringRedisTemplate;
import org.springframework.http.HttpMethod;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.util.UriComponentsBuilder;

import com.github.benmanes.caffeine.cache.Cache;
import com.google.crypto.tink.subtle.Hex;
import com.kanke.gateway.entry.RouteRule;
import com.kanke.gateway.entry.StoreToken;
import com.kanke.gateway.util.JsonUtil;
import com.kanke.gateway.util.SecurityUtil;
import com.kanke.gateway.util.UrlUtil;

import reactor.core.publisher.Mono;

@Component
public class GetAndPostEncryptInstance implements EncryptInstance {
	
	@Autowired
	ReactiveStringRedisTemplate reactiveStringRedisTemplate;
	@Autowired
	ModifyRequestBody modifyRequestBody;
	
	@Autowired
	ModifyResponseBody modifyResponseBody;
	
	public static String x_token_id ="x-token-id";

	@Override
	public String getName() {
		return "GetAndPost";
	}

	@Resource(name = "storeTokenCache")
	Cache<String, StoreToken> videoCache;

	
	@Override
	public GatewayFilter responseEncryptGatewayFilter(RouteRule routeRule) {
		return modifyResponseBody.body((ex,v)->{
			try {
				StoreToken  storeToken  = videoCache.getIfPresent(ex.getRequest().getId());
				if(storeToken==null) {
					return Mono.just(v);
				}
				videoCache.invalidate(ex.getRequest().getId());
				String text = this.writeEncryptAES(v, storeToken, routeRule, "UTF-8");
				return Mono.just(text);
			} catch (Exception e) {
				e.printStackTrace();
			}
			return Mono.just(v);
			
		});
	}

	private String writeEncryptAES(String text,StoreToken storeToken,RouteRule routeRule,String encoding)throws IOException {
		byte[] datas = text.getBytes(encoding);
		byte[] data = SecurityUtil.encodeAES(datas, Hex.decode(storeToken.getKey()), routeRule.getEncrypt().getIv().getBytes());
		return Hex.encode(data);
	}
	
	private String readDecodeAES(String text,StoreToken storeToken,RouteRule routeRule,String encoding)throws IOException {
		byte[] datas = Hex.decode(text);
		byte[] data = SecurityUtil.decodeAES(datas, Hex.decode(storeToken.getKey()),  routeRule.getEncrypt().getIv().getBytes());
		if (data != null && data.length > 0) {
			return  new String(data, "UTF-8");
		}
		return "";
	}
	
	@Override
	public Mono<Void> requestDecrypt(ServerWebExchange exchange, GatewayFilterChain chain,RouteRule routeRule) throws Exception {
		ServerHttpRequest  serverHttpRequest  = exchange.getRequest();
		HttpMethod  httpMethod  = serverHttpRequest.getMethod();
		String token = serverHttpRequest.getHeaders().getFirst(x_token_id);
		if(StringUtils.isNotBlank(token)) {
			String SwitchKey = "SwitchKey_"+routeRule.getName();
			//先处理GET请求
			if(httpMethod==HttpMethod.GET) {
				MultiValueMap<String, String> 	queryParams = serverHttpRequest.getQueryParams();
				String get = queryParams.getFirst("get");
				if(StringUtils.isNotBlank(get)) {
					return reactiveStringRedisTemplate.opsForHash().get(SwitchKey, token).flatMap((redisv)->{
						try {
							StoreToken storeToken = JsonUtil.StringToObject((String)redisv, StoreToken.class);
							videoCache.put(serverHttpRequest.getId(), storeToken);
							String queryUrl = this.readDecodeAES(get, storeToken,routeRule,"UTF-8");
							MultiValueMap<String, String> multiValueMap  = UrlUtil.parseQuery(queryUrl);
							URI newUri = UriComponentsBuilder.fromUri(serverHttpRequest.getURI()).replaceQueryParams(unmodifiableMultiValueMap(multiValueMap)).build().toUri();
							ServerHttpRequest updatedRequest = exchange.getRequest().mutate().uri(newUri).build();
							return chain.filter(exchange.mutate().request(updatedRequest).build());
						} catch (Exception e) {
							return chain.filter(exchange);
						}
					});
				}
			}else if(httpMethod==HttpMethod.POST) {
				return reactiveStringRedisTemplate.opsForHash().get(SwitchKey, token).flatMap((redisv)->{
					StoreToken storeToken = JsonUtil.StringToObject((String)redisv, StoreToken.class);
					videoCache.put(serverHttpRequest.getId(), storeToken);
					MultiValueMap<String, String> 	queryParams = serverHttpRequest.getQueryParams();
					String get = queryParams.getFirst("get");
					if(StringUtils.isNotBlank(get)){
						try {
							String queryUrl = this.readDecodeAES(get, storeToken,routeRule,"UTF-8");
							MultiValueMap<String, String> multiValueMap  = UrlUtil.parseQuery(queryUrl);
							URI newUri = UriComponentsBuilder.fromUri(serverHttpRequest.getURI()).replaceQueryParams(unmodifiableMultiValueMap(multiValueMap)).build().toUri();
							ServerHttpRequest updatedRequest = exchange.getRequest().mutate().uri(newUri).build();
							ServerWebExchange  serverWebExchange  = exchange.mutate().request(updatedRequest).build();
							return GroupGatewayFilter.createGroupGatewayFilter(this.requestBodyDecrypt(storeToken, routeRule)).filter(serverWebExchange, chain);
						} catch (Exception e) {
							e.printStackTrace();
							return chain.filter(exchange);
						}
					}
					return GroupGatewayFilter.createGroupGatewayFilter(this.requestBodyDecrypt(storeToken, routeRule)).filter(exchange, chain);
				});
			}
		}
		return chain.filter(exchange);
	}

	public GatewayFilter requestBodyDecrypt(final StoreToken storeToken,final RouteRule routeRule) {
		
		GatewayFilter  gatewayFilter = modifyRequestBody.formData((v)->{
			try {
				String post =  this.readDecodeAES(v, storeToken, routeRule, "UTF-8");
				return post;
			} catch (IOException e) {
				e.printStackTrace();
			}
			return "";
			
		});
		
		return gatewayFilter;
	}


}
