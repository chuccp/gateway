package com.kanke.gateway.entry;

import com.kanke.gateway.type.RuleType;

public class RouteRule {
	
	private String name;
	
	private RuleType ruleType;
	
	
	private String uri;
	
	private String[] hosts;
	
	private String[] paths;
	
	private String[] forward;
	
	private Encrypt encrypt;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public RuleType getRuleType() {
		return ruleType;
	}

	public void setRuleType(RuleType ruleType) {
		this.ruleType = ruleType;
	}

	public String[] getHosts() {
		return hosts;
	}

	public void setHosts(String[] hosts) {
		this.hosts = hosts;
	}

	public String[] getForward() {
		return forward;
	}

	public void setForward(String[] forward) {
		this.forward = forward;
	}

	public String[] getPaths() {
		return paths;
	}

	public void setPaths(String[] paths) {
		this.paths = paths;
	}

	public Encrypt getEncrypt() {
		return encrypt;
	}

	public void setEncrypt(Encrypt encrypt) {
		this.encrypt = encrypt;
	}

	public String getUri() {
		return uri;
	}

	public void setUri(String uri) {
		this.uri = uri;
	}
	
	
	
}
