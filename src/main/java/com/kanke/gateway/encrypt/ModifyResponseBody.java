package com.kanke.gateway.encrypt;

import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.reactivestreams.Publisher;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.rewrite.ModifyResponseBodyGatewayFilterFactory;
import org.springframework.cloud.gateway.filter.factory.rewrite.RewriteFunction;
import org.springframework.stereotype.Component;

import reactor.core.publisher.Mono;

@Component
public class ModifyResponseBody {

	@Resource(name = "modifyResponseBodyGatewayFilterFactory2")
	private ModifyResponseBodyGatewayFilterFactory modifyResponseBodyGatewayFilterFactory;

	public GatewayFilter body(RewriteFunction<String, String> rewriteFunction) {
		GatewayFilter gatewayFilter = 	modifyResponseBodyGatewayFilterFactory.apply(c -> c.setRewriteFunction(String.class, String.class, (ex,r)->{
			if(StringUtils.isNotBlank(r)) {
				Publisher<String> body = rewriteFunction.apply(ex, r);
				return body;
			}
			return Mono.empty();
		}));
		return gatewayFilter;

	}

}
