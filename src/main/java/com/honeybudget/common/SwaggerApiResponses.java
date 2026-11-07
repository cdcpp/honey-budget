package com.honeybudget.common;

import java.lang.annotation.*;

/**
 * SwaggerApiResponse 다중 지정을 위한 컨테이너 어노테이션.
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface SwaggerApiResponses {
    SwaggerApiResponse[] value();
}
