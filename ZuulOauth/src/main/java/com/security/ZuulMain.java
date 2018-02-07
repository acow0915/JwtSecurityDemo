package com.security;

import java.util.ArrayList;

import org.apache.catalina.Context;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.embedded.ConfigurableEmbeddedServletContainer;
import org.springframework.boot.context.embedded.EmbeddedServletContainerCustomizer;
import org.springframework.boot.context.embedded.tomcat.TomcatContextCustomizer;
import org.springframework.boot.context.embedded.tomcat.TomcatEmbeddedServletContainerFactory;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.netflix.feign.EnableFeignClients;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

import com.google.common.collect.Lists;
import com.winterframework.modules.catalina.session.RedisSessionHandlerValve;
import com.winterframework.modules.catalina.session.RedisSessionManager;

/**
 * Oauth!
 *
 */
@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients
@EnableZuulProxy
public class ZuulMain {
    public static void main( String[] args ) {
    	SpringApplication.run(ZuulMain.class, args);
    }
    /*
    @Bean
    public EmbeddedServletContainerCustomizer containerCustomizer() {

        return new EmbeddedServletContainerCustomizer() {
            @Override
            public void customize(ConfigurableEmbeddedServletContainer container) {
                TomcatEmbeddedServletContainerFactory containerFactory = (TomcatEmbeddedServletContainerFactory) container;
                containerFactory.addContextValves(new RedisSessionHandlerValve());
                ArrayList<MyTomcatContextCustomizer> customizers = Lists.newArrayList(new MyTomcatContextCustomizer());
                containerFactory.setTomcatContextCustomizers(customizers);
            }
        };
    }

    public class MyTomcatContextCustomizer implements TomcatContextCustomizer {
        @Override
        public void customize(Context context) {
            context.setSessionTimeout(30);
            context.setManager(new RedisSessionManager() {{
                setHost("10.13.22.131");
            }});
        }
    }
	*/
    
    @Bean
    public TokenFilter TokenFilter(){
    	return new TokenFilter();
    }
    
    @LoadBalanced    // Make sure to create the load-balanced template
    @Bean
    RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
