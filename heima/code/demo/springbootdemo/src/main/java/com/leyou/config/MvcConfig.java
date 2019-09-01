package com.leyou.config;

import com.leyou.interceptor.LoginInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
@Configuration
public class MvcConfig implements WebMvcConfigurer {
    /**
      * 描述: 通过@Bean注解，将我们定义的拦截器注册到Spring容器
      * @return com.leyou.interceptor.LoginInterceptor
     **/
    @Bean
    public LoginInterceptor loginInterceptor(){
        return new LoginInterceptor();
    }

    /**
      * 描述: 重写接口中的拦截器方法,添加拦截路径
        * @param registry :
      * @return void
     **/
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(this.loginInterceptor()).addPathPatterns("/**");
    }
}
