package com.kanke.gateway.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;

import reactor.core.publisher.Mono;

@RestController
public class HelloController {

	@Autowired
	WebClient.Builder loadBalancedWebClientBuilder;
	
	 @RequestMapping("/hello")
	  public Mono<String> hello() {
	    return loadBalancedWebClientBuilder
	        .build().get().uri("http://say-hello/index")
	        .retrieve().bodyToMono(String.class);
	  }
}
