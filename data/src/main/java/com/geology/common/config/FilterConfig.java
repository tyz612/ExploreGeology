package com.geology.common.config;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FilterConfig {
    @Bean
    public FilterRegistrationBean<ForceCorsFilter> forceCorsFilter() {
        FilterRegistrationBean<ForceCorsFilter> bean = new FilterRegistrationBean<>();
        bean.setFilter(new ForceCorsFilter());
        bean.addUrlPatterns("/*"); // 覆盖所有路径
        return bean;
    }
}
