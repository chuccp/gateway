package com.kanke.gateway.entry;

public class StoreToken {

	private String share;

	private String key;

	private Long localTime;

	private Long remoteTime;

	private Long cha;

	public String getShare() {
		return share;
	}

	public void setShare(String share) {
		this.share = share;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public Long getLocalTime() {
		return localTime;
	}

	public void setLocalTime(Long localTime) {
		this.localTime = localTime;
	}

	public Long getRemoteTime() {
		return remoteTime;
	}

	public void setRemoteTime(Long remoteTime) {
		this.remoteTime = remoteTime;
	}

	public Long getCha() {
		return cha;
	}

	public void setCha(Long cha) {
		this.cha = cha;
	}

}
