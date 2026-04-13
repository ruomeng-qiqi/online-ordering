package com.ruomeng.onlineorderingbackend.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.ruomeng.onlineorderingbackend.common.PageResult;
import com.ruomeng.onlineorderingbackend.exception.BusinessException;
import com.ruomeng.onlineorderingbackend.exception.ErrorCode;
import com.ruomeng.onlineorderingbackend.mapper.DiningTableMapper;
import com.ruomeng.onlineorderingbackend.model.dto.DiningTableDTO;
import com.ruomeng.onlineorderingbackend.model.dto.DiningTablePageQueryDTO;
import com.ruomeng.onlineorderingbackend.model.entity.DiningTable;
import com.ruomeng.onlineorderingbackend.model.vo.DiningTableVO;
import com.ruomeng.onlineorderingbackend.service.DiningTableService;
import com.ruomeng.onlineorderingbackend.utils.WxQrcodeUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 餐台服务实现
 */
@Slf4j
@Service
public class DiningTableServiceImpl implements DiningTableService {

    @Autowired
    private DiningTableMapper diningTableMapper;

    /**
     * 添加餐台
     */
    @Override
    public void add(DiningTableDTO diningTableDTO) {
        // 检查餐台号是否已存在
        DiningTable existingTable = diningTableMapper.selectByTableNumber(diningTableDTO.getTableNumber());
        if (existingTable != null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "餐台号已存在");
        }

        // 创建餐台实体并复制属性
        DiningTable diningTable = new DiningTable();
        BeanUtils.copyProperties(diningTableDTO, diningTable);
        diningTable.setStatus(0); // 默认空闲
        diningTable.setCreateTime(LocalDateTime.now());
        diningTable.setUpdateTime(LocalDateTime.now());
        
        // 生成小程序码（使用餐台号）
        try {
            String qrcodeUrl = WxQrcodeUtil.generateTableQrcode(diningTable.getTableNumber());
            diningTable.setQrCode(qrcodeUrl);
        } catch (Exception e) {
            log.error("生成小程序码失败，使用默认二维码", e);
            diningTable.setQrCode("https://via.placeholder.com/200?text=" + diningTableDTO.getTableNumber());
        }
        
        // 插入数据库
        int result = diningTableMapper.insert(diningTable);
        if (result <= 0) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "添加餐台失败");
        }
    }

    /**
     * 更新餐台
     */
    @Override
    public void update(DiningTableDTO diningTableDTO) {
        // 校验餐台ID
        if (diningTableDTO.getId() == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "餐台ID不能为空");
        }

        // 查询餐台是否存在
        DiningTable diningTable = diningTableMapper.selectById(diningTableDTO.getId());
        if (diningTable == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "餐台不存在");
        }

        // 如果餐台号变更，检查新餐台号是否已存在
        boolean tableNumberChanged = !diningTable.getTableNumber().equals(diningTableDTO.getTableNumber());
        if (tableNumberChanged) {
            DiningTable existingTable = diningTableMapper.selectByTableNumber(diningTableDTO.getTableNumber());
            if (existingTable != null) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "餐台号已存在");
            }
        }

        // 更新餐台信息
        diningTable.setTableNumber(diningTableDTO.getTableNumber());
        diningTable.setTableName(diningTableDTO.getTableName());
        diningTable.setSeats(diningTableDTO.getSeats());
        diningTable.setSort(diningTableDTO.getSort());
        diningTable.setUpdateTime(LocalDateTime.now());
        
        // 如果餐台号变更，重新生成小程序码
        if (tableNumberChanged) {
            // 先删除旧的小程序码文件
            String oldQrCode = diningTable.getQrCode();
            
            try {
                String qrcodeUrl = WxQrcodeUtil.generateTableQrcode(diningTable.getTableNumber());
                diningTable.setQrCode(qrcodeUrl);
                
                // 生成新二维码成功后，删除旧文件
                WxQrcodeUtil.deleteQrcodeFile(oldQrCode);
            } catch (Exception e) {
                log.error("重新生成小程序码失败，保留原二维码", e);
            }
        }
        
        int result = diningTableMapper.update(diningTable);
        if (result <= 0) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "更新餐台失败");
        }
    }

    /**
     * 删除餐台
     */
    @Override
    public void delete(Long id) {
        // 查询餐台是否存在
        DiningTable diningTable = diningTableMapper.selectById(id);
        if (diningTable == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "餐台不存在");
        }

        // 检查餐台状态，只能删除空闲状态的餐台
        if (diningTable.getStatus() != 0) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "只能删除空闲状态的餐台");
        }

        // 删除餐台
        int result = diningTableMapper.deleteById(id);
        if (result <= 0) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "删除餐台失败");
        }
        
        // 删除小程序码文件
        WxQrcodeUtil.deleteQrcodeFile(diningTable.getQrCode());
    }

    /**
     * 批量删除餐台
     */
    @Override
    public void deleteBatch(List<Long> ids) {
        // 校验参数
        if (ids == null || ids.isEmpty()) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "餐台ID列表不能为空");
        }

        // 检查所有餐台状态，只能删除空闲状态的餐台
        for (Long id : ids) {
            DiningTable diningTable = diningTableMapper.selectById(id);
            if (diningTable != null && diningTable.getStatus() != 0) {
                throw new BusinessException(ErrorCode.OPERATION_ERROR, "只能删除空闲状态的餐台");
            }
        }

        // 先获取所有餐台的二维码URL，用于删除文件
        List<String> qrcodeUrls = new java.util.ArrayList<>();
        for (Long id : ids) {
            DiningTable diningTable = diningTableMapper.selectById(id);
            if (diningTable != null && diningTable.getQrCode() != null) {
                qrcodeUrls.add(diningTable.getQrCode());
            }
        }

        // 批量删除餐台
        int result = diningTableMapper.deleteBatchByIds(ids);
        if (result <= 0) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "批量删除餐台失败");
        }
        
        // 删除所有小程序码文件
        for (String qrcodeUrl : qrcodeUrls) {
            WxQrcodeUtil.deleteQrcodeFile(qrcodeUrl);
        }
    }

    /**
     * 根据ID查询餐台
     */
    @Override
    public DiningTableVO getById(Long id) {
        // 查询餐台
        DiningTable diningTable = diningTableMapper.selectById(id);
        if (diningTable == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "餐台不存在");
        }

        // 转换为VO
        DiningTableVO diningTableVO = new DiningTableVO();
        BeanUtils.copyProperties(diningTable, diningTableVO);
        return diningTableVO;
    }

    /**
     * 分页查询餐台
     */
    @Override
    public PageResult pageQuery(DiningTablePageQueryDTO diningTablePageQueryDTO) {
        // 开始分页
        PageHelper.startPage(diningTablePageQueryDTO.getPage(), diningTablePageQueryDTO.getPageSize());
        
        // 执行查询
        Page<DiningTable> page = diningTableMapper.pageQuery(diningTablePageQueryDTO);
        
        // 获取总记录数和当前页数据
        long total = page.getTotal();
        List<DiningTable> diningTables = page.getResult();
        
        // 转换为VO列表
        List<DiningTableVO> records = diningTables.stream()
                .map(diningTable -> {
                    DiningTableVO vo = new DiningTableVO();
                    BeanUtils.copyProperties(diningTable, vo);
                    return vo;
                })
                .collect(java.util.stream.Collectors.toList());
        
        // 返回分页结果
        return new PageResult(total, records);
    }
}
