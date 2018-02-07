package com.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.security.CustomAuthenticationProvider;
import com.security.filter.JwtAuthenticationTokenFilter;

@Configuration
@EnableWebSecurity//创建一个Spring Bean，Bean的类型是Filter，名字为springSecurityFilterChain
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter{
	
	@Autowired
	private AuthenticationEntryPoint jwtAuthenticationEntryPoint;
	
	@Autowired
    private UserDetailsService customUserDetailsService;

    @Autowired
    public void configureAuthentication(AuthenticationManagerBuilder authenticationManagerBuilder) throws Exception {
        authenticationManagerBuilder
                .userDetailsService(customUserDetailsService)
                .passwordEncoder(passwordEncoder());
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
	
	@Bean
    public JwtAuthenticationTokenFilter authenticationTokenFilterBean() throws Exception {
        return new JwtAuthenticationTokenFilter();
    }
	
	/**
	 * 認證器
	 * @return
	 */
	@Bean
	public AuthenticationProvider customAuthenticationProvider() {
		return new CustomAuthenticationProvider();
	}
	
	@Override
    protected void configure(HttpSecurity http) throws Exception {
		http.addFilterBefore(authenticationTokenFilterBean(), UsernamePasswordAuthenticationFilter.class);
		
		http.csrf().disable()
		.httpBasic().authenticationEntryPoint(jwtAuthenticationEntryPoint).and()
		.exceptionHandling().authenticationEntryPoint(jwtAuthenticationEntryPoint).and()
        // don't create session
        .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).and()
        .authorizeRequests()
        .antMatchers("/", "/auth/login").permitAll()
        .anyRequest().authenticated();
		
//        http
//            .authorizeRequests()
//            	.antMatchers("/", "/resources/**", "/h2-console/**").permitAll()
//            	.anyRequest().authenticated()
//                .and()
//                .httpBasic().authenticationEntryPoint(jwtAuthenticationEntryPoint)
//                .and()
//            .formLogin()
//                .loginPage("/loginPage").permitAll()
//                .successForwardUrl("/home")
//                .failureUrl("/loginPage")
//                .usernameParameter("username")
//                .passwordParameter("password")
//                .and()
//            .logout()
//                .permitAll()
//                .and()
//                .headers().frameOptions().disable();
        
//        http.sessionManagement().maximumSessions(10)//允许同时多少个用户同时登陆
//        .and().sessionCreationPolicy(SessionCreationPolicy.NEVER);
        
        /*
        HttpSessionEventPublisher             监听session创建和销毁 
        ConcurrentSessionFilter                  每次有http请求时校验，看你当前的session是否是过期的 
        SessionRegistryImpl                       存放session中的信息，并做处理 
        ConcurrentSessionControllerImpl     用户登入登出的控制 
        SessionInformation       存储session中信息的model 
        */
    }
	
	@Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
//        auth.inMemoryAuthentication().withUser("a").password("a").roles("USER");
        auth.authenticationProvider(customAuthenticationProvider());
    }
	
//	@Bean//redis
//    public HttpSessionStrategy httpSessionStrategy() {
//        return new HeaderHttpSessionStrategy();
//    }
}
