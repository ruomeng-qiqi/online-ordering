package com.ruomeng.onlineorderingbackend.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.ruomeng.onlineorderingbackend.common.PageResult;
import com.ruomeng.onlineorderingbackend.exception.BusinessException;
import com.ruomeng.onlineorderingbackend.exception.ErrorCode;
import com.ruomeng.onlineorderingbackend.mapper.CustomerMapper;
import com.ruomeng.onlineorderingbackend.mapper.PointsRecordMapper;
import com.ruomeng.onlineorderingbackend.model.dto.CustomerPageQueryDTO;
import com.ruomeng.onlineorderingbackend.model.dto.PointsAdjustDTO;
import com.ruomeng.onlineorderingbackend.model.entity.Customer;
import com.ruomeng.onlineorderingbackend.model.entity.PointsRecord;
import com.ruomeng.onlineorderingbackend.model.vo.CustomerVO;
import com.ruomeng.onlineorderingbackend.model.vo.PointsRecordVO;
import com.ruomeng.onlineorderingbackend.service.CustomerService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 顾客服务实现
 */
@Slf4j
@Service
public class CustomerServiceImpl implements CustomerService {

    @Autowired
    private CustomerMapper customerMapper;

    @Autowired
    private PointsRecordMapper pointsRecordMapper;

    /**
     * 分页查询顾客
     */
    @Override
    public PageResult pageQuery(CustomerPageQueryDTO customerPageQueryDTO) {
        // 开始分页
        PageHelper.startPage(customerPageQueryDTO.getPage(), customerPageQueryDTO.getPageSize());
        
        // 执行查询
        Page<Customer> page = (Page<Customer>) customerMapper.pageQuery(customerPageQueryDTO);
        
        // 获取总记录数和当前页数据
        long total = page.getTotal();
        List<Customer> customers = page.getResult();
        
        // 转换为VO列表
        List<CustomerVO> records = customers.stream()
                .map(customer -> {
                    CustomerVO vo = new CustomerVO();
                    BeanUtils.copyProperties(customer, vo);
                    return vo;
                })
                .collect(Collectors.toList());
        
        // 返回分页结果
        return new PageResult(total, records);
    }

    /**
     * 根据ID查询顾客详情
     */
    @Override
    public CustomerVO getById(Long id) {
        // 查询顾客
        Customer customer = customerMapper.selectById(id);
        if (customer == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "顾客不存在");
        }

        // 转换为VO
        CustomerVO customerVO = new CustomerVO();
        BeanUtils.copyProperties(customer, customerVO);
        return customerVO;
    }

    /**
     * 更新顾客状态
     */
    @Override
    public void updateStatus(Long id, Integer status) {
        // 查询顾客是否存在
        Customer customer = customerMapper.selectById(id);
        if (customer == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "顾客不存在");
        }

        // 设置新状态和更新时间
        customer.setId(id);
        customer.setStatus(status);
        customer.setUpdateTime(LocalDateTime.now());
        
        // 更新状态
        int result = customerMapper.updateStatus(customer);
        if (result <= 0) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "更新状态失败");
        }
    }

    /**
     * 删除顾客
     */
    @Override
    public void deleteById(Long id) {
        // 查询顾客是否存在
        Customer customer = customerMapper.selectById(id);
        if (customer == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "顾客不存在");
        }

        // 删除顾客（积分记录会通过外键级联删除）
        int result = customerMapper.deleteById(id);
        if (result <= 0) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "删除顾客失败");
        }
    }

    /**
     * 调整顾客积分
     */
    @Override
    @Transactional
    public void adjustPoints(PointsAdjustDTO pointsAdjustDTO) {
        // 校验参数
        if (pointsAdjustDTO.getCustomerId() == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "顾客ID不能为空");
        }
        if (pointsAdjustDTO.getPoints() == null || pointsAdjustDTO.getPoints() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "积分数量必须大于0");
        }

        // 查询顾客是否存在
        Customer customer = customerMapper.selectById(pointsAdjustDTO.getCustomerId());
        if (customer == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "顾客不存在");
        }

        // 计算积分变动
        int pointsChange = pointsAdjustDTO.getType() == 1 
            ? pointsAdjustDTO.getPoints() 
            : -pointsAdjustDTO.getPoints();

        // 检查减少积分时是否超过当前积分
        if (pointsChange < 0 && Math.abs(pointsChange) > customer.getPoints()) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "减少的积分不能超过当前积分");
        }

        // 计算新积分
        int newPoints = customer.getPoints() + pointsChange;
        int newTotalPoints = customer.getTotalPoints();
        if (pointsChange > 0) {
            newTotalPoints += pointsChange;
        }

        // 设置新积分和更新时间
        customer.setPoints(newPoints);
        customer.setTotalPoints(newTotalPoints);
        customer.setUpdateTime(LocalDateTime.now());
        
        // 更新数据库
        int result = customerMapper.updatePoints(customer);
        if (result <= 0) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "更新积分失败");
        }

        // 插入积分记录
        PointsRecord pointsRecord = new PointsRecord();
        pointsRecord.setCustomerId(customer.getId());
        pointsRecord.setType(3); // 手动调整
        pointsRecord.setPoints(pointsChange);
        // 只有当remark不为空时才设置，否则使用数据库默认值
        if (pointsAdjustDTO.getRemark() != null && !pointsAdjustDTO.getRemark().trim().isEmpty()) {
            pointsRecord.setRemark(pointsAdjustDTO.getRemark());
        }
        pointsRecord.setCreateTime(LocalDateTime.now());
        pointsRecordMapper.insert(pointsRecord);
    }

    /**
     * 查询顾客积分记录
     */
    @Override
    public List<PointsRecordVO> getPointsRecords(Long customerId) {
        // 查询积分记录
        List<PointsRecord> pointsRecords = pointsRecordMapper.selectByCustomerId(customerId);

        // 转换为VO列表
        return pointsRecords.stream()
                .map(pointsRecord -> {
                    PointsRecordVO vo = new PointsRecordVO();
                    BeanUtils.copyProperties(pointsRecord, vo);
                    return vo;
                })
                .collect(Collectors.toList());
    }
}
