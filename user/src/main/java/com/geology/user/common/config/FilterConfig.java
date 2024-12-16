package com.geology.user.common.config;

import com.geology.user.controller.CustomAuthFilter;
import com.geology.user.controller.CustomAuthFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FilterConfig {

    @Autowired
    private CustomAuthFilter customAuthFilter;

    @Bean
    public FilterRegistrationBean<CustomAuthFilter> registrationBean() {
        FilterRegistrationBean<CustomAuthFilter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(customAuthFilter);
        registrationBean.addUrlPatterns("/user/user/test/*"); // 指定过滤器的URL模式
        return registrationBean;
    }
}
