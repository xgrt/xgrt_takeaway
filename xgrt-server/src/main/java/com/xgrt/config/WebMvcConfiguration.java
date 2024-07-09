package com.xgrt.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.xgrt.interceptor.JwtTokenAdminInterceptor;
import com.xgrt.json.JacksonObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;

import java.util.List;

/**
 * 配置类，注册web层相关组件
 */
@Configuration
@Slf4j
public class WebMvcConfiguration extends WebMvcConfigurationSupport {

    @Autowired
    private JwtTokenAdminInterceptor jwtTokenAdminInterceptor;

    /**
     * 注册自定义拦截器
     *
     * @param registry
     */
    protected void addInterceptors(InterceptorRegistry registry) {
        log.info("开始注册自定义拦截器...");
        registry.addInterceptor(jwtTokenAdminInterceptor)
                .addPathPatterns("/admin/**")
                .excludePathPatterns("/admin/employee/login");
    }

    /**
     * 通过knife4j生成接口文档
     * 只需复制后修改相关参数即可
     * @return
     */
    @Bean
    public Docket docket() {
        log.info("开始生成接口文档");
        ApiInfo apiInfo = new ApiInfoBuilder()
                .title("苍穹外卖项目接口文档")//标题
                .version("2.0")//版本
                .description("苍穹外卖项目接口文档")//描述信息
                .build();
        Docket docket = new Docket(DocumentationType.SWAGGER_2/* swagger版本 */)
                .apiInfo(apiInfo)
                .select()
                //指定生成接口需要扫描的包，扫描类和其中的方法
                .apis(RequestHandlerSelectors.basePackage("com.xgrt.controller"))
                .paths(PathSelectors.any())
                .build();
        return docket;
    }

    /**
     * 设置静态资源映射
     * 方法名不能修改，此方法是重写了父类WebMvcConfigurationSupport的方法
     * @param registry
     */
    protected void addResourceHandlers(ResourceHandlerRegistry registry) {
        log.info("开始设置静态资源映射……");
        registry.addResourceHandler("/doc.html").addResourceLocations("classpath:/META-INF/resources/");
        registry.addResourceHandler("/webjars/**").addResourceLocations("classpath:/META-INF/resources/webjars/");
    }

    /**
     * 重写 扩展Spring MVC消息转换器extendMessageConverters 方法
     * @param converters
     */
    @Override
    protected void extendMessageConverters(List<HttpMessageConverter<?>> converters) {
        log.info("扩展消息转换器");
        //创建一个信息转换器（Spring提供）
        MappingJackson2HttpMessageConverter converter=new MappingJackson2HttpMessageConverter();
        //需要为信息转换器设置一个对象转换器
        // 对象转换器可以将Java对象转换成JSON数据
        converter.setObjectMapper(new JacksonObjectMapper());
        //将自己的信息转换器加入到容器当中
        // 默认加入的转换器，是排在最末尾的
        // 加入一个参数 0 让自己的转换器在最前面
        converters.add(0 , converter);
    }
}
