package com.nowcoder.community.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * @author yanhao
 * @note
 * @create 2022-08-13 下午 4:00
 */
@Configuration
@EnableScheduling //启动定时任务
@EnableAsync
public class ThreadPoolConfig {
}
