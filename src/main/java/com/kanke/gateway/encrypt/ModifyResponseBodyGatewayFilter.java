package com.kanke.gateway.encrypt;

import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.factory.rewrite.ModifyResponseBodyGatewayFilterFactory;
import org.springframework.cloud.gateway.filter.factory.rewrite.ModifyResponseBodyGatewayFilterFactory.Config;
import org.springframework.web.server.ServerWebExchange;

import reactor.core.publisher.Mono;

public abstract class ModifyResponseBodyGatewayFilter implements GatewayFilter {
	
	
	private  ModifyResponseBodyGatewayFilterFactory  modifyResponseBodyGatewayFilterFactory;
	
	public ModifyResponseBodyGatewayFilter(ModifyResponseBodyGatewayFilterFactory  modifyResponseBodyGatewayFilterFactory) {
		this.modifyResponseBodyGatewayFilterFactory = modifyResponseBodyGatewayFilterFactory;
	}
	

	@Override
	public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
		return this.modifyResponseBodyGatewayFilterFactory.apply((c)->this.apply(c)).filter(exchange, chain);
	}
	protected abstract Config apply(Config c);

}
