package com.kanke.gateway.config;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClientsProperties;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.BooleanSpec;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder.Builder;
import org.springframework.cloud.gateway.route.builder.UriSpec;
import org.springframework.cloud.loadbalancer.annotation.LoadBalancerClientSpecification;
import org.springframework.cloud.loadbalancer.support.LoadBalancerClientFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.web.reactive.function.client.WebClient;

import com.kanke.gateway.encrypt.EncryptInstance;
import com.kanke.gateway.encrypt.GetAndPostEncryptInstance;
import com.kanke.gateway.encrypt.ModifyResponseBody;
import com.kanke.gateway.entry.Encrypt;
import com.kanke.gateway.entry.Forward;
import com.kanke.gateway.entry.RouteRule;
import com.kanke.gateway.type.RuleType;
import com.kanke.gateway.util.RouteUtil;
import com.kanke.gateway.util.ServiceInstanceListSupplierUtil;
import com.kanke.gateway.util.UrlUtil;


@ConfigurationProperties(prefix = "spring")
@Configuration
public class RouteConfig  {
	
	
	private Map<String, String> route;

	public Map<String, String> getRoute() {
		return route;
	}

	public void setRoute(Map<String, String> route) {
		this.route = route;
	}
	
	private Map<String,EncryptInstance> encryptInstanceMap = new HashMap<>();
	private Map<String,RouteRule> routeRuleMap = new HashMap<>();
	
	
	public Encrypt getEncrypt(String name) {
		if(routeRuleMap.containsKey(name)) {
			return routeRuleMap.get(name).getEncrypt();
		}else {
			return null;
		}
	}
	
	
	@Autowired
	private GetAndPostEncryptInstance getAndPostEncryptInstance;
	
	private void loadEncryptInstance() {
		encryptInstanceMap.put(getAndPostEncryptInstance.getName(), getAndPostEncryptInstance);
	}
	
	private EncryptInstance get(RouteRule routeRule) {
		if(routeRule.getEncrypt()==null||StringUtils.isBlank(routeRule.getEncrypt().getName())) {
			return null;
		}
		EncryptInstance encryptInstance = encryptInstanceMap.get(routeRule.getEncrypt().getName());
		return encryptInstance;
	}
	@Autowired
	ModifyResponseBody modifyResponseBody;
	
	@Bean
	public RouteLocator routes(RouteLocatorBuilder builder) {
		this.loadEncryptInstance();
		Builder  routesBuilder  = builder.routes();
		List<RouteRule> routeRules = RouteUtil.parse(route);
		for(RouteRule routeRule:routeRules) {
			
			routeRuleMap.put(routeRule.getName(), routeRule);
			
			EncryptInstance encryptInstance = this.get(routeRule);
			routesBuilder.route(routeRule.getName(),r->{
				UriSpec  uriSpec = null; 
				if(routeRule.getRuleType()==RuleType.HOST) {
					uriSpec = r.host(routeRule.getHosts());
				}else {
					uriSpec =  r.path(routeRule.getPaths());
				}
				if(encryptInstance!=null) {
					uriSpec = ((BooleanSpec) uriSpec).filters(f->    {
						f = f.retry(3);
						if(routeRule.getRewritePath()!=null&&StringUtils.isNotBlank(routeRule.getRewritePath().getRegex())&&StringUtils.isNotBlank(routeRule.getRewritePath().getReplacement())) {
							f=f.rewritePath(routeRule.getRewritePath().getRegex(), routeRule.getRewritePath().getReplacement());
						}
						f = f.filters(encryptInstance.responseEncryptGatewayFilter(routeRule));
						f = f.filters(encryptInstance.requestDecryptGatewayFilter(routeRule));
						return f;
					}
							);
				}
				return uriSpec.uri(routeRule.getUri());
				
			});
		}
		return routesBuilder.build();
		
	}
	@Bean
	@Primary
	public LoadBalancerClientFactory loadBalancerClientFactory(LoadBalancerClientsProperties properties) throws IOException {
		List<RouteRule> routeRules = RouteUtil.parse(route);
		LoadBalancerClientFactory clientFactory = new LoadBalancerClientFactory(properties);
		List<LoadBalancerClientSpecification> list = new ArrayList<>();
		for(RouteRule routeRule:routeRules) {
			List<Class<?>> clss = new ArrayList<>();
			String[] forwards = routeRule.getForward();
			
 			List<Forward> forwardList = new ArrayList<>();
			for(String forward:forwards) {
				Forward ward  = UrlUtil.getForwardForUrl(forward);
				forwardList.add(ward);
			}
			clss.add(ServiceInstanceListSupplierUtil.getServiceInstanceListSupplierSimple(routeRule.getName(), forwardList));
			list.add(new LoadBalancerClientSpecification(routeRule.getName(),clss.toArray(new Class[] {})));
		}
		clientFactory.setConfigurations(list);
		return clientFactory;
	}
	
	@LoadBalanced
	@Bean
	WebClient.Builder webClientBuilder() {
		return WebClient.builder();
	}


}

