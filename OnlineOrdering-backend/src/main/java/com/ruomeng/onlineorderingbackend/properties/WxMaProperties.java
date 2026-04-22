package com.ruomeng.onlineorderingbackend.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 微信小程序配置属性
 */
@Data
@Component
@ConfigurationProperties(prefix = "wechat.miniapp")
public class WxMaProperties {
    
    /** 小程序 AppID */
    private String appid;
    
    /** 小程序 AppSecret */
    private String secret;
    
    /** 小程序页面路径 */
    private String pagePath;
}
