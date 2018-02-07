package com.security;

import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@FeignClient(name="Auth")
public interface AuthClient {

	@RequestMapping(value = "/auth/check", method = RequestMethod.POST)
	String check(String token);
}
