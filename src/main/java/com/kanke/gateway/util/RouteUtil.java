package com.kanke.gateway.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.kanke.gateway.entry.Encrypt;
import com.kanke.gateway.entry.RouteRule;
import com.kanke.gateway.type.RuleType;

public class RouteUtil {
	
	private static String[] names(Map<String,String> routeMap) {
		String names = routeMap.get("names");
		if(StringUtils.isNotBlank(names)) {
			return StringUtils.split(names, ',');
			
		}else {
			return new String[] {};
		}
	}
	
	private static RuleType ruleType(Map<String,String> routeMap,String name) {
		String rule = routeMap.get("appender."+name+".rule");
		if(StringUtils.equals(rule, "host")) {
			return RuleType.HOST;
		}
		return RuleType.PATH;
	}
	private static String[] hosts(Map<String,String> routeMap,String name) {
		String hosts = routeMap.get("appender."+name+".hosts");
		if(StringUtils.isNotBlank(hosts)) {
			return StringUtils.split(hosts, ',');
		}else {
			return new String[] {};
		}
		
	}
	
	private static String[] paths(Map<String,String> routeMap,String name) {
		String hosts = routeMap.get("appender."+name+".paths");
		if(StringUtils.isNotBlank(hosts)) {
			return StringUtils.split(hosts, ',');
		}else {
			return new String[] {};
		}
		
	}
	
	
	private static Encrypt encrypt(Map<String,String> routeMap,String name) {
		Encrypt encrypt = new Encrypt();
		encrypt.setKey(routeMap.get("appender."+name+".encrypt.key"));
		encrypt.setIv(routeMap.get("appender."+name+".encrypt.iv"));
		encrypt.setName(routeMap.get("appender."+name+".encrypt.name"));
		return encrypt;
	}
	
	
	private static String[] forward(Map<String,String> routeMap,String name) {
		String forward = routeMap.get("appender."+name+".forwards");
		if(StringUtils.isNotBlank(forward)) {
			return StringUtils.split(forward, ',');
		}else {
			return new String[] {};
		}
		
	}

	public static List<RouteRule> parse(Map<String,String> routeMap){
		String[] names  = RouteUtil.names(routeMap);
		List<RouteRule> roles = new ArrayList<>();
		for (String name : names) {
			RouteRule routeRule = new RouteRule();
			routeRule.setName(name);
			routeRule.setRuleType(RouteUtil.ruleType(routeMap, name));
			if(routeRule.getRuleType()==RuleType.HOST) {
				routeRule.setHosts(RouteUtil.hosts(routeMap, name));
			}else {
				routeRule.setPaths(RouteUtil.paths(routeMap, name));
			}
			routeRule.setUri("lb://"+name);
			routeRule.setForward(forward(routeMap, name));
			routeRule.setEncrypt(encrypt(routeMap,name));
			roles.add(routeRule);
		}
		return roles;
	}

}
