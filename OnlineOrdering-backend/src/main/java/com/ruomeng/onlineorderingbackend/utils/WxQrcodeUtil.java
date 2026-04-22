package com.ruomeng.onlineorderingbackend.utils;

import cn.binarywang.wx.miniapp.api.WxMaService;
import cn.binarywang.wx.miniapp.bean.WxMaCodeLineColor;
import cn.binarywang.wx.miniapp.constant.WxMaConstants;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.RandomUtil;
import com.ruomeng.onlineorderingbackend.properties.WxMaProperties;
import com.ruomeng.onlineorderingbackend.exception.BusinessException;
import com.ruomeng.onlineorderingbackend.exception.ErrorCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * 微信小程序码工具类
 */
@Slf4j
@Component
public class WxQrcodeUtil {

    private static WxMaService wxMaService;
    
    private static WxMaProperties wxMaProperties;
    
    private static AliOssUtil aliOssUtil;

    @Autowired
    public void setWxMaService(WxMaService wxMaService) {
        WxQrcodeUtil.wxMaService = wxMaService;
    }

    @Autowired
    public void setWxMaProperties(WxMaProperties wxMaProperties) {
        WxQrcodeUtil.wxMaProperties = wxMaProperties;
    }
    
    @Autowired
    public void setAliOssUtil(AliOssUtil aliOssUtil) {
        WxQrcodeUtil.aliOssUtil = aliOssUtil;
    }

    /**
     * 生成餐台小程序码
     */
    public static String generateTableQrcode(String tableNumber) {
        try {
            // 场景值：传递餐台号
            String scene = "tableNumber=" + tableNumber;
            
            // 小程序页面路径（可以为空，扫码后进入首页）
            String page = wxMaProperties.getPagePath();
            
            // 生成小程序码（无数量限制）
            byte[] qrcodeBytes = wxMaService.getQrcodeService().createWxaCodeUnlimitBytes(
                scene,
                page,
                false, // 不检查page是否存在（允许页面不存在）
                WxMaConstants.DEFAULT_ENV_VERSION,  // 要打开的小程序版本
                430,   // 二维码宽度
                false, // 是否需要透明底色
                new WxMaCodeLineColor("0", "0", "0"),  // 线条颜色（黑色）
                false  // 是否自动配置线条颜色
            );
            
            // 生成文件名：qrcode/日期_随机字符串.png
            String uuid = RandomUtil.randomString(16);
            String fileName = String.format("qrcode/%s_%s.png", 
                    DateUtil.formatDate(new Date()), 
                    uuid);
            
            // 上传到阿里云 OSS
            String url = aliOssUtil.upload(qrcodeBytes, fileName);
            
            log.info("生成餐台小程序码成功，餐台号：{}，URL：{}", tableNumber, url);
            return url;
            
        } catch (Exception e) {
            log.error("生成餐台小程序码失败，餐台号：{}", tableNumber, e);
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "生成小程序码失败：" + e.getMessage());
        }
    }
}
