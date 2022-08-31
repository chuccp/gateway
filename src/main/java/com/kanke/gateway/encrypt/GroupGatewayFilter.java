package com.kanke.gateway.encrypt;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.web.server.ServerWebExchange;

import reactor.core.publisher.Mono;

public class GroupGatewayFilter  implements GatewayFilter {
	
	
	public List<GatewayFilter> gatewayFilterList;
	
	private GroupGatewayFilterChain groupGatewayFilterChain;
	
	public static GroupGatewayFilter createGroupGatewayFilter(GatewayFilter ...gatewayFilter) {
		return new GroupGatewayFilter(Arrays.asList(gatewayFilter));
	}

	public GroupGatewayFilter(List<GatewayFilter> gatewayFilterList) {
		this.gatewayFilterList = gatewayFilterList;
		this.groupGatewayFilterChain = new GroupGatewayFilterChain(new ArrayList<>(gatewayFilterList));
	}

	@Override
	public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
		groupGatewayFilterChain.preChain = chain;
		return   groupGatewayFilterChain.filter(exchange);
	}
	
	private static class GroupGatewayFilterChain implements GatewayFilterChain {

		private final int index;
		
		private GatewayFilterChain preChain;

		private final List<GatewayFilter> filters;

		GroupGatewayFilterChain(List<GatewayFilter> filters) {
			this.filters = filters;
			this.index = 0;
		}

		private GroupGatewayFilterChain(GroupGatewayFilterChain parent, int index) {
			this.filters = parent.getFilters();
			this.index = index;
		}

		public List<GatewayFilter> getFilters() {
			return filters;
		}

		@Override
		public Mono<Void> filter(ServerWebExchange exchange) {
			return Mono.defer(() -> {
				if (this.index < filters.size()) {
					GatewayFilter filter = filters.get(this.index);
					GroupGatewayFilterChain chain = new GroupGatewayFilterChain(this, this.index + 1);
					chain.preChain = this.preChain;
					return filter.filter(exchange, chain);
				}
				else {
					return preChain.filter(exchange); // complete
				}
			});
		}

	}
}
