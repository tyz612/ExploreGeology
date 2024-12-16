package com.geology.user.common.config;

import com.geology.user.controller.CustomAuthFilter;
import com.geology.user.controller.CustomAuthFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                // 禁用CSRF保护，因为这是一个REST API示例
                .csrf().disable()
                // 指定哪些路径是公开的，不需要认证
                .authorizeRequests()
                .antMatchers("/user/login").permitAll() // 所有/public/**路径下的资源都允许访问
                .antMatchers("/user/sendCaptcha").permitAll() // 所有/api/open/**路径下的资源都允许访问
                // 其他所有请求都需要认证
                .anyRequest().authenticated()
                .and()
                // 添加自定义过滤器
                .addFilterBefore(new CustomAuthFilter(), BasicAuthenticationFilter.class);
                // 配置HTTP Basic认证
//                .httpBasic();
    }
    @Override
    public void configure(WebSecurity web) throws Exception {

        web.ignoring().antMatchers("/user/login");
        web.ignoring().antMatchers("/user/sendCaptcha");

    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }


}
