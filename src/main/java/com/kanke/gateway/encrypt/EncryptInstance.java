package com.kanke.gateway.encrypt;

import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.web.server.ServerWebExchange;

import com.kanke.gateway.entry.RouteRule;

import reactor.core.publisher.Mono;

public interface EncryptInstance  {
	
	
	public default String getName() {
		return "default";
	}
	
	
	public default Mono<String> modifyResponseBody(ServerWebExchange exchange,String text){
		return Mono.just(text);
	}
	
	
	public default GatewayFilter responseEncryptGatewayFilter(RouteRule routeRule) {
		return new GatewayFilter() {
			
			@Override
			public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
				return chain.filter(exchange);
			}
		};
	}
	
	
	
	public default GatewayFilter requestDecryptGatewayFilter(RouteRule routeRule) {
		return new GatewayFilter() {
			@Override
			public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
				try {
					return requestDecrypt(exchange,chain,routeRule);
				} catch (Exception e) {
					e.printStackTrace();
					return chain.filter(exchange);
				}
				
			}};
	}


	Mono<Void> requestDecrypt(ServerWebExchange exchange, GatewayFilterChain chain, RouteRule routeRule)throws Exception;


	
	


}
