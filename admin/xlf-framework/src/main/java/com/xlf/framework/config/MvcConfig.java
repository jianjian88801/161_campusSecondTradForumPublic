package com.xlf.framework.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class MvcConfig {
    @Bean
    public WebMvcConfigurer webMvcConfigurer(){
        return new WebMvcConfigurer() {
            //文件预览，静态资源映射。前端访问：localhost:8088/preview/file/md5/fileName
            @Override
            public void addResourceHandlers(ResourceHandlerRegistry registry) {
                registry.addResourceHandler("/preview/file/**").addResourceLocations("file:F:/educationProject/商城/小程序/campusSecondTrad/admin/");

//                registry.addResourceHandler("/preview/file/**").addResourceLocations("file:/home/images/");

            }
        };
    }
}
