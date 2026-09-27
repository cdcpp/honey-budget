package com.honeybudget.common;

import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.*;

@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Repeatable(SwaggerApiResponses.class)
@ApiResponse // Swagger의 원본 ApiResponse를 메타 어노테이션으로 사용
public @interface SwaggerApiResponse {

    @AliasFor(annotation = ApiResponse.class, attribute = "responseCode")
    String responseCode() default "200";

    @AliasFor(annotation = ApiResponse.class, attribute = "description")
    String description() default "";
}