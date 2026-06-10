package com.ruomeng.onlineorderingbackend.controller;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.RandomUtil;
import com.ruomeng.onlineorderingbackend.common.Result;
import com.ruomeng.onlineorderingbackend.utils.AliOssUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.Date;

/**
 * 通用接口
 */
@Api(tags = "通用接口")
@Slf4j
@RestController
public class CommonController {

    @Autowired
    private AliOssUtil aliOssUtil;

    /**
     * 管理端文件上传
     */
    @PostMapping("/admin/common/upload")
    @ApiOperation("管理端文件上传")
    public Result<String> adminUpload(MultipartFile file, String type) {
        log.info("管理端文件上传：{}，类型：{}", file.getOriginalFilename(), type);
        return uploadFile(file, type);
    }

    /**
     * 用户端文件上传
     */
    @PostMapping("/common/upload")
    @ApiOperation("用户端文件上传")
    public Result<String> userUpload(MultipartFile file, String type) {
        log.info("用户端文件上传：{}，类型：{}", file.getOriginalFilename(), type);
        return uploadFile(file, type);
    }

    /**
     * 文件上传通用方法
     */
    private Result<String> uploadFile(MultipartFile file, String type) {
        // 获取原始文件名
        String originalFilename = file.getOriginalFilename();
        
        // 生成文件名：日期_随机字符串.扩展名
        String uuid = RandomUtil.randomString(16);
        String fileName = String.format("%s_%s.%s", 
                DateUtil.formatDate(new Date()), 
                uuid,
                FileUtil.getSuffix(originalFilename));
        
        // 根据类型确定目录
        String directory = getDirectory(type);
        String filePath = directory + fileName;
        
        // 上传文件
        String url = aliOssUtil.upload(file, filePath);
        
        return Result.success(url);
    }
    
    /**
     * 根据类型获取目录
     */
    private String getDirectory(String type) {
        if (type == null || type.isEmpty()) {
            return "";
        }
        
        switch (type) {
            case "dish":
                return "dish/";
            case "setmeal":
                return "setmeal/";
            case "avatar":
                return "avatar/";
            default:
                return "";
        }
    }
}
