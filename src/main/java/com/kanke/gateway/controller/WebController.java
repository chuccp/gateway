package com.kanke.gateway.controller;

import java.security.NoSuchAlgorithmException;
import java.util.Date;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.ReactiveStringRedisTemplate;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;

import com.google.crypto.tink.subtle.Hex;
import com.google.crypto.tink.subtle.X25519;
import com.kanke.gateway.config.RouteConfig;
import com.kanke.gateway.encrypt.GetAndPostEncryptInstance;
import com.kanke.gateway.entry.Encrypt;
import com.kanke.gateway.entry.StoreToken;
import com.kanke.gateway.entry.SwitchKey;
import com.kanke.gateway.util.JsonUtil;
import com.kanke.gateway.util.SecurityUtil;

import reactor.core.publisher.Mono;

@RestController
@CrossOrigin("*")
public class WebController {
	
	
	@Autowired
	RouteConfig routeConfig;
	
	@Autowired
	private ReactiveStringRedisTemplate reactiveStringRedisTemplate;
	
	
	
	@RequestMapping(path = "/{name}/getSwitchKey")
	public Mono<String> getSwitchKey(@PathVariable("name") String name,@RequestParam("secret")String secret,ServerWebExchange serverWebExchange,@RequestParam("time")Long time,@RequestParam(value = "SToken",required = false)String SToken) throws Exception {
		Encrypt  encrypt  = routeConfig.getEncrypt(name);
		if(encrypt==null||StringUtils.isBlank(encrypt.getKey())) {
			return Mono.just("未配置密钥");
		}
		String token = serverWebExchange.getRequest().getHeaders().getFirst(GetAndPostEncryptInstance.x_token_id);
		if(StringUtils.isBlank(token)) {
			token = SToken;
			if(StringUtils.isBlank(token)) {
			return Mono.just("token is null");}
		}
		Date current = new Date();
		byte[] privatekey = X25519.generatePrivateKey();
		byte[] publickey = X25519.publicFromPrivate(privatekey);
		byte[] secretbytes = Hex.decode(secret);
		byte[] key = X25519.computeSharedSecret(privatekey, secretbytes);
		String keys0 = Hex.encode(key);
		String keys = Hex.encode(this.getRealKey(keys0, token,routeConfig.getEncrypt(name).getKey()));
		StoreToken storeToken = new StoreToken();
		storeToken.setKey(keys);
		storeToken.setShare(keys0);
		storeToken.setRemoteTime(time);
		storeToken.setLocalTime(current.getTime());
		storeToken.setCha(Math.abs(storeToken.getLocalTime()-storeToken.getRemoteTime() ));
		String SwitchKey = "SwitchKey_"+name;
		String vkey = Hex.encode(publickey);
//		System.out.println(JsonUtil.ObjectToString(storeToken));
		return reactiveStringRedisTemplate.opsForHash().put(SwitchKey, token, JsonUtil.ObjectToString(storeToken)).thenReturn(vkey);
		
	}
	
	private byte[] getRealKey(String key, String openId,String appendkey) throws NoSuchAlgorithmException {
		StringBuffer sb = new StringBuffer();
		sb.append(key).append(openId).append(appendkey);
		return SecurityUtil.SHA256(sb.toString().getBytes());
	}
	
	

	public static void main(String[] args) {
		System.out.println(Hex.encode(X25519.generatePrivateKey()));
	}
}
