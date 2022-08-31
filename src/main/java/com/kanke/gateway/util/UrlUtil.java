package com.kanke.gateway.util;

import java.net.MalformedURLException;
import java.net.URL;

import org.apache.commons.lang3.StringUtils;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import com.kanke.gateway.entry.Forward;

public class UrlUtil {
	public  static Forward getForwardForUrl(String url) throws MalformedURLException {
		URL uri = new URL(url);
		if(uri.getPort()<=-1) {
			if(StringUtils.equalsIgnoreCase(uri.getProtocol(), "https")) {
				return new Forward(uri.getHost(), 443, true);
			}
			if(StringUtils.equalsIgnoreCase(uri.getProtocol(), "http")) {
				return new Forward(uri.getHost(), 80, false);
			}
		}
		 return new Forward(uri.getHost(), uri.getPort(), StringUtils.equalsIgnoreCase(uri.getProtocol(), "https"));
	}
	
	
	public static MultiValueMap<String, String> parseQuery(String queryUrl){
		MultiValueMap<String, String> pamaterMap = new LinkedMultiValueMap<String, String>();
		if (StringUtils.isNotBlank(queryUrl)) {
			String[] params = StringUtils.split(queryUrl, "&");
			for (String param : params) {
				String[] values = StringUtils.split(param, '=');
				if (values.length == 2) {
					pamaterMap.add(values[0], values[1]);
				}
			}
		}
		return pamaterMap;
	}
	
}
