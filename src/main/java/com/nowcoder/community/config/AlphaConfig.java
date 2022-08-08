package com.nowcoder.community.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.text.SimpleDateFormat;

/**
 * @author yanhao
 * @note
 * @create 2022-08-06 上午 11:43
 */
@Configuration //配置类注解
public class AlphaConfig {

    @Bean //这个方法返回的对象SimpleDateFormat将会装配到容器中
    public SimpleDateFormat simpleDateFormat(){
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    }

}
