package com.honeybudget.common;

import java.lang.annotation.*;

@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface SwaggerApiResponses {
    SwaggerApiResponse[] value();
}
