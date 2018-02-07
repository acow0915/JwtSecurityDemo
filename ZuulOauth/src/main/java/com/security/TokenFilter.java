package com.security;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;

public class TokenFilter extends ZuulFilter {
	
//	@Autowired
//	private AuthClient authClient;
	
	@Autowired        // NO LONGER auto-created by Spring Cloud (see below)
    @LoadBalanced     // Explicitly request the load-balanced template // with Ribbon built-in
	private RestTemplate client;

	@Override public String filterType() { 
		return "pre"; // 可以在請求被路由之前呼叫
	} 
	
	@Override public int filterOrder() { 
		return 0; // filter執行順序，通過數字指定 ,優先順序為0，數字越大，優先順序越低 
	}
	
	@Override public boolean shouldFilter() { 
		return true;// 是否執行該過濾器，此處為true，說明需要過濾 
	}
	
	@Override
	public Object run() {
		
		RequestContext context = RequestContext.getCurrentContext();
		
		HttpServletRequest req = context.getRequest();
		
		System.out.println(req.getRequestURL().toString());
		System.out.println(req.getRequestURI());
		System.out.println(req.getSession().getId());
		
		
		if("/proxy/auth/login".equals(req.getRequestURI())){
			
		} else {
//			String result = authClient.check();
			
			final String requestHeader = req.getHeader("x-auth-token");
			
			MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
			headers.add("x-auth-token", requestHeader);
			HttpEntity<Object> request = new HttpEntity<>(null, headers);
			
			
			ResponseEntity<String> result = client.postForEntity("http://Auth/auth/check", request, String.class);
			
//			Object result = client.exchange("http://Auth/auth/check", HttpMethod.POST, request, Object.class).getBody();
			if("OK".equals(result.getBody())){
				
			} else {
				context.setSendZuulResponse(false);  
	            context.setResponseStatusCode(401);  
	            context.setResponseBody("{\"result\":\"token is not correct!\"}");  
	            context.set("isSuccess", false); 
			}
		}
		return null;
	}

}
