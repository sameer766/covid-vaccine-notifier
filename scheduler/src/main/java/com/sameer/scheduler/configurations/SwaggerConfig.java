package com.sameer.scheduler.configurations;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;


@Configuration
@EnableSwagger2
public class SwaggerConfig {

    @Bean
    public Docket apiDocket() {


        ApiInfo apiInfo = new ApiInfo("Documentation for scheduler api",
                "",
                "1.0",
                "APACHE 2.0",
                new springfox.documentation.service.Contact(
                        "Sameer Pande",
                        "https://github.com/sameer766",
                        "pandesameer76@gmail.com"),
                "Apache 2.0",
                "",
                Collections.emptyList());


        return new Docket(DocumentationType.SWAGGER_2)
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.sameer.scheduler"))
                .paths(PathSelectors.any())
                .build()
                .protocols(new HashSet<>(Arrays.asList("HTTP", "HTTPs")))
                .apiInfo(apiInfo);

    }

//
//  @Override
//  public void addResourceHandlers(ResourceHandlerRegistry registry) {
//
//    registry
//        .addResourceHandler("swagger-ui.html")
//        .addResourceLocations("classpath:/META-INF/resources/");
//
//    registry
//        .addResourceHandler("/webjars/**")
//        .addResourceLocations("classpath:/META-INF/resources/webjars/");
//  }
}
