package com.nowcoder.community.config;

import org.springframework.boot.web.servlet.MultipartConfigFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.servlet.MultipartConfigElement;
import java.io.File;

/**
 * @author yanhao
 * @note
 * @create 2022-08-15 下午 1:53
 */
@Configuration
public class UploadFileConfig {

    private static final String TMP_UPLOAD_LOCATION = "/tmp/uploads/";

    @Bean
    public MultipartConfigElement multipartConfigElement() {
        // 创建存储的文件夹 避免使用的时候找不到
        // 这里用的是 Hutool 的工具类,可以自己写
        File dir = new File(TMP_UPLOAD_LOCATION);
        if(!dir.exists()){
            dir.mkdirs();
        }
        MultipartConfigFactory factory = new MultipartConfigFactory();
        factory.setLocation(TMP_UPLOAD_LOCATION);
//        // 单个文件最大
//        factory.setMaxFileSize(20);
//        // 设置总上传数据总大小
//        factory.setMaxRequestSize("20MB");
        return factory.createMultipartConfig();
    }

}
