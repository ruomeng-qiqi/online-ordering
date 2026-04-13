package com.ruomeng.onlineorderingbackend.utils;

import cn.binarywang.wx.miniapp.api.WxMaService;
import cn.binarywang.wx.miniapp.bean.WxMaCodeLineColor;
import cn.binarywang.wx.miniapp.constant.WxMaConstants;
import com.ruomeng.onlineorderingbackend.config.WxMaProperties;
import com.ruomeng.onlineorderingbackend.exception.BusinessException;
import com.ruomeng.onlineorderingbackend.exception.ErrorCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * 微信小程序码工具类
 */
@Slf4j
@Component
public class WxQrcodeUtil {

    private static WxMaService wxMaService;
    
    private static WxMaProperties wxMaProperties;

    @Autowired
    public void setWxMaService(WxMaService wxMaService) {
        WxQrcodeUtil.wxMaService = wxMaService;
    }

    @Autowired
    public void setWxMaProperties(WxMaProperties wxMaProperties) {
        WxQrcodeUtil.wxMaProperties = wxMaProperties;
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
            
            // 保存小程序码到本地
            String fileName = "table_" + tableNumber + "_" + System.currentTimeMillis() + ".png";
            String savePath = saveQrcodeToLocal(qrcodeBytes, fileName);
            
            log.info("生成餐台小程序码成功，餐台号：{}，保存路径：{}", tableNumber, savePath);
            return savePath;
            
        } catch (Exception e) {
            log.error("生成餐台小程序码失败，餐台号：{}", tableNumber, e);
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "生成小程序码失败：" + e.getMessage());
        }
    }

    /**
     * 保存小程序码到本地
     */
    private static String saveQrcodeToLocal(byte[] qrcodeBytes, String fileName) throws IOException {
        // 获取保存路径
        String qrcodePath = wxMaProperties.getQrcodePath();
        
        // 创建目录
        File directory = new File(qrcodePath);
        if (!directory.exists()) {
            directory.mkdirs();
        }
        
        // 保存文件
        String filePath = qrcodePath + fileName;
        File file = new File(filePath);
        try (FileOutputStream fos = new FileOutputStream(file)) {
            fos.write(qrcodeBytes);
        }
        
        // 返回访问URL（这里返回相对路径，实际使用时需要配置静态资源访问）
        return "/qrcode/" + fileName;
    }

    /**
     * 删除小程序码文件
     */
    public static void deleteQrcodeFile(String qrcodeUrl) {
        if (qrcodeUrl == null || qrcodeUrl.isEmpty()) {
            return;
        }
        
        // 如果是占位图URL，不需要删除
        if (qrcodeUrl.startsWith("https://via.placeholder.com") || qrcodeUrl.startsWith("http://")) {
            return;
        }
        
        try {
            // 从URL中提取文件名
            String fileName = qrcodeUrl.substring(qrcodeUrl.lastIndexOf("/") + 1);
            String qrcodePath = wxMaProperties.getQrcodePath();
            String filePath = qrcodePath + fileName;
            
            File file = new File(filePath);
            if (file.exists()) {
                boolean deleted = file.delete();
                if (deleted) {
                    log.info("删除小程序码文件成功：{}", filePath);
                } else {
                    log.warn("删除小程序码文件失败：{}", filePath);
                }
            }
        } catch (Exception e) {
            log.error("删除小程序码文件异常：{}", qrcodeUrl, e);
        }
    }
}
