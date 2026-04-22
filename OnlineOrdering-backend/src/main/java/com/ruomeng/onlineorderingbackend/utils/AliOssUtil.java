package com.ruomeng.onlineorderingbackend.utils;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.RandomUtil;
import com.aliyun.oss.OSS;
import com.ruomeng.onlineorderingbackend.exception.BusinessException;
import com.ruomeng.onlineorderingbackend.exception.ErrorCode;
import com.ruomeng.onlineorderingbackend.properties.AliOssProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Date;

/**
 * 阿里云 OSS 文件上传工具类
 */
@Slf4j
@Component
public class AliOssUtil {

    @Autowired
    private OSS ossClient;

    @Autowired
    private AliOssProperties aliOssProperties;

    /**
     * 上传文件
     * @param file 文件
     * @param filePath 文件路径（包含目录和文件名，如：dish/2026-04-22_xxx.jpg）
     */
    public String upload(MultipartFile file, String filePath) {
        // 校验文件
        if (file == null || file.isEmpty()) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "文件不能为空");
        }

        try {
            // 上传文件到 OSS
            ossClient.putObject(
                    aliOssProperties.getBucketName(),
                    filePath,
                    file.getInputStream()
            );

            // 返回文件访问 URL
            String url = "https://" + aliOssProperties.getBucketName() + "." 
                    + aliOssProperties.getEndpoint() + "/" + filePath;
            
            log.info("文件上传成功，文件路径：{}，URL：{}", filePath, url);
            return url;

        } catch (IOException e) {
            log.error("文件上传失败", e);
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "文件上传失败：" + e.getMessage());
        }
    }

    /**
     * 上传字节数组
     * @param bytes 字节数组
     * @param filePath 文件路径（包含目录和文件名，如：qrcode/2026-04-22_xxx.png）
     */
    public String upload(byte[] bytes, String filePath) {
        try {
            // 上传文件到 OSS
            ossClient.putObject(
                    aliOssProperties.getBucketName(),
                    filePath,
                    new java.io.ByteArrayInputStream(bytes)
            );

            // 返回文件访问 URL
            String url = "https://" + aliOssProperties.getBucketName() + "." 
                    + aliOssProperties.getEndpoint() + "/" + filePath;
            
            log.info("文件上传成功，文件路径：{}，URL：{}", filePath, url);
            return url;

        } catch (Exception e) {
            log.error("文件上传失败", e);
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "文件上传失败：" + e.getMessage());
        }
    }
}
