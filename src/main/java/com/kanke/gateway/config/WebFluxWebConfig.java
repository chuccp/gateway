package com.kanke.gateway.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.http.codec.ServerCodecConfigurer;
import org.springframework.web.reactive.config.WebFluxConfigurer;
@Configuration
public class WebFluxWebConfig implements WebFluxConfigurer{
	 @Override
	    public void configureHttpMessageCodecs(ServerCodecConfigurer configurer) {
	        configurer.defaultCodecs().maxInMemorySize(2 * 1024 * 1024);
	    }
}
