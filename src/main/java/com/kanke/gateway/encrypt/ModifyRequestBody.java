package com.kanke.gateway.encrypt;

import java.util.function.Function;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.rewrite.ModifyRequestBodyGatewayFilterFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;

import com.kanke.gateway.util.UrlUtil;

import reactor.core.publisher.Mono;

@Component
public class ModifyRequestBody {

	@Autowired
	private  ModifyRequestBodyGatewayFilterFactory  modifyRequestBodyGatewayFilterFactory;
	
	public  GatewayFilter  formData(Function<String, String> function) {
		GatewayFilter gatewayFilter = 	modifyRequestBodyGatewayFilterFactory.apply(c -> c.setRewriteFunction(MultiValueMap.class, MultiValueMap.class, (ex,r)->{
			@SuppressWarnings("unchecked")
			String post = (String) r.getFirst("post");
			if(StringUtils.isNotBlank(post)) {
				String v = function.apply(post);
				return Mono.just(UrlUtil.parseQuery(v));
			}
			return Mono.just(r);
			
			
		}));
		return gatewayFilter;
	}
}
