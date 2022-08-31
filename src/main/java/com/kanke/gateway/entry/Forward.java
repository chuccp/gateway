package com.kanke.gateway.entry;

public class Forward {
	
	
	
	public Forward(String host, int port, boolean secure) {
		this.host = host;
		this.port = port;
		this.secure = secure;
	}

	private String host;
	
	private int port;
	
	private boolean secure;

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public boolean isSecure() {
		return secure;
	}

	public void setSecure(boolean secure) {
		this.secure = secure;
	}

	@Override
	public String toString() {
		return "Forward [host=" + host + ", port=" + port + ", secure=" + secure + "]";
	}
	
	

}
