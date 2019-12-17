package com.hx.anno;

import java.lang.annotation.*;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface SourcePropertyRemark {
    String title();
    String detail();
    String value();
}
