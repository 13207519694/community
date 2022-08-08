package com.nowcoder.community.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD) //声明注解范围
@Retention(RetentionPolicy.RUNTIME) //声明注解生效时间
//@Document 生成文档的时候带不带
//@Inherited 注解能不能继承
//获取注解 Method.getDeclaredAnnotations() or Method.getAnnotation(Class<T> annotationClass)
public @interface LoginRequired {



}
