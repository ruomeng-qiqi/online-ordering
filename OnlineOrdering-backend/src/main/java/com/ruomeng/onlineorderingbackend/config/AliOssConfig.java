package com.ruomeng.onlineorderingbackend.config;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.ruomeng.onlineorderingbackend.properties.AliOssProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 阿里云 OSS 配置
 */
@Slf4j
@Configuration
public class AliOssConfig {

    @Autowired
    private AliOssProperties aliOssProperties;

    @Bean
    public OSS ossClient() {
        log.info("初始化阿里云 OSS 客户端，Endpoint: {}, Bucket: {}", 
                aliOssProperties.getEndpoint(), 
                aliOssProperties.getBucketName());
        
        return new OSSClientBuilder().build(
                aliOssProperties.getEndpoint(),
                aliOssProperties.getAccessKeyId(),
                aliOssProperties.getAccessKeySecret()
        );
    }
}
