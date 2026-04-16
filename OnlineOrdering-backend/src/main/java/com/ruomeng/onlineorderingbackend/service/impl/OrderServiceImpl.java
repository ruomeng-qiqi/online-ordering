package com.ruomeng.onlineorderingbackend.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.ruomeng.onlineorderingbackend.common.PageResult;
import com.ruomeng.onlineorderingbackend.exception.BusinessException;
import com.ruomeng.onlineorderingbackend.exception.ErrorCode;
import com.ruomeng.onlineorderingbackend.mapper.*;
import com.ruomeng.onlineorderingbackend.model.dto.OrderCancelDTO;
import com.ruomeng.onlineorderingbackend.model.dto.OrderDTO;
import com.ruomeng.onlineorderingbackend.model.dto.OrderPageQueryDTO;
import com.ruomeng.onlineorderingbackend.model.entity.*;
import com.ruomeng.onlineorderingbackend.model.vo.OrderDetailVO;
import com.ruomeng.onlineorderingbackend.model.vo.OrderVO;
import com.ruomeng.onlineorderingbackend.service.OrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 订单服务实现
 */
@Slf4j
@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private OrderDetailMapper orderDetailMapper;

    @Autowired
    private CustomerMapper customerMapper;

    @Autowired
    private DiningTableMapper diningTableMapper;

    @Autowired
    private PointsRecordMapper pointsRecordMapper;

    /**
     * 分页查询订单列表
     */
    @Override
    public PageResult pageQuery(OrderPageQueryDTO orderPageQueryDTO) {
        // 开始分页
        PageHelper.startPage(orderPageQueryDTO.getPage(), orderPageQueryDTO.getPageSize());
        
        // 执行查询
        Page<OrderVO> page = orderMapper.pageQuery(orderPageQueryDTO);
        
        // 获取总记录数和当前页数据
        long total = page.getTotal();
        List<OrderVO> records = page.getResult();
        
        // 返回分页结果
        return new PageResult(total, records);
    }

    /**
     * 查询订单详情
     */
    @Override
    public OrderDetailVO getById(Long id) {
        // 查询订单
        Order order = orderMapper.selectById(id);
        if (order == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "订单不存在");
        }

        // 转换为VO
        OrderDetailVO orderDetailVO = new OrderDetailVO();
        BeanUtils.copyProperties(order, orderDetailVO);

        // 查询顾客信息
        Customer customer = customerMapper.selectById(order.getCustomerId());
        if (customer != null) {
            orderDetailVO.setCustomerName(customer.getNickname());
            orderDetailVO.setIsMember(customer.getIsMember());
        }

        // 查询餐台信息
        DiningTable table = diningTableMapper.selectById(order.getTableId());
        if (table != null) {
            orderDetailVO.setTableNumber(table.getTableNumber());
            orderDetailVO.setTableName(table.getTableName());
        }

        // 查询订单明细
        List<OrderDetail> orderDetails = orderDetailMapper.selectByOrderId(id);
        List<OrderDetailVO.OrderItemVO> itemVOList = orderDetails.stream().map(detail -> {
            OrderDetailVO.OrderItemVO itemVO = new OrderDetailVO.OrderItemVO();
            BeanUtils.copyProperties(detail, itemVO);
            return itemVO;
        }).collect(Collectors.toList());
        orderDetailVO.setOrderDetails(itemVOList);

        return orderDetailVO;
    }

    /**
     * 更新订单
     */
    @Override
    @Transactional
    public void update(OrderDTO orderDTO) {
        // 1. 校验订单ID
        if (orderDTO.getId() == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "订单ID不能为空");
        }

        // 2. 查询订单
        Order order = orderMapper.selectById(orderDTO.getId());
        if (order == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "订单不存在");
        }

        // 3. 只能修改待支付订单
        if (order.getOrderStatus() != 1) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "只能修改待支付订单");
        }

        // 4. 校验订单明细
        if (orderDTO.getDetails() == null || orderDTO.getDetails().isEmpty()) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "订单明细不能为空");
        }

        // 5. 删除原有订单明细
        orderDetailMapper.deleteByOrderId(order.getId());

        // 6. 插入新的订单明细并计算总金额
        BigDecimal totalAmount = BigDecimal.ZERO;
        for (OrderDTO.OrderDetailDTO detailDTO : orderDTO.getDetails()) {
            OrderDetail orderDetail = new OrderDetail();
            orderDetail.setOrderId(order.getId());
            orderDetail.setDishId(detailDTO.getDishId());
            orderDetail.setSetmealId(detailDTO.getSetmealId());
            orderDetail.setName(detailDTO.getName());
            orderDetail.setImage(detailDTO.getImage());
            orderDetail.setQuantity(detailDTO.getQuantity());
            orderDetail.setPrice(detailDTO.getPrice());
            orderDetail.setAmount(detailDTO.getPrice().multiply(new BigDecimal(detailDTO.getQuantity())));
            orderDetail.setFlavor(detailDTO.getFlavor());
            orderDetail.setCreateTime(LocalDateTime.now());
            
            orderDetailMapper.insert(orderDetail);
            totalAmount = totalAmount.add(orderDetail.getAmount());
        }

        // 7. 更新订单信息
        order.setTotalAmount(totalAmount);
        order.setDiscountAmount(orderDTO.getDiscountAmount() != null ? orderDTO.getDiscountAmount() : BigDecimal.ZERO);
        order.setRemark(orderDTO.getRemark());
        order.setUpdateTime(LocalDateTime.now());
        
        int result = orderMapper.update(order);
        if (result <= 0) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "更新订单失败");
        }
    }

    /**
     * 完成订单
     */
    @Override
    @Transactional
    public void complete(Long id) {
        // 1. 查询订单
        Order order = orderMapper.selectById(id);
        if (order == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "订单不存在");
        }

        // 2. 只能完成待支付订单
        if (order.getOrderStatus() != 1) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "只能完成待支付订单");
        }

        // 3. 查询顾客信息
        Customer customer = customerMapper.selectById(order.getCustomerId());
        if (customer == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "顾客不存在");
        }

        // 4. 处理积分抵扣
        if (order.getPointsUsed() != null && order.getPointsUsed() > 0) {
            // 扣除顾客积分
            customer.setPoints(customer.getPoints() - order.getPointsUsed());
            customer.setUpdateTime(LocalDateTime.now());
            customerMapper.updatePoints(customer);

            // 记录积分使用
            PointsRecord useRecord = new PointsRecord();
            useRecord.setCustomerId(customer.getId());
            useRecord.setType(2); // 订单使用
            useRecord.setPoints(-order.getPointsUsed()); // 负数表示扣除
            useRecord.setOrderId(order.getId());
            useRecord.setRemark("订单消费抵扣");
            useRecord.setCreateTime(LocalDateTime.now());
            pointsRecordMapper.insert(useRecord);
        }

        // 5. 计算实付金额
        BigDecimal actualAmount = order.getTotalAmount()
                .subtract(order.getDiscountAmount() != null ? order.getDiscountAmount() : BigDecimal.ZERO)
                .subtract(order.getPointsDeduction() != null ? order.getPointsDeduction() : BigDecimal.ZERO);

        // 6. 更新订单状态
        order.setOrderStatus(2); // 已完成
        order.setPaymentMethod(2); // 线下支付
        order.setActualAmount(actualAmount);
        order.setCheckoutTime(LocalDateTime.now());
        order.setUpdateTime(LocalDateTime.now());

        // 7. 发放积分（只有会员才能获得积分）
        if (customer.getIsMember() == 1) {
            // 计算获得的积分：消费1元=1积分，向下取整
            Integer pointsEarned = actualAmount.setScale(0, RoundingMode.DOWN).intValue();
            order.setPointsEarned(pointsEarned);

            // 增加顾客积分
            customer.setPoints(customer.getPoints() + pointsEarned);
            customer.setTotalPoints(customer.getTotalPoints() + pointsEarned);
            customer.setUpdateTime(LocalDateTime.now());
            customerMapper.updatePoints(customer);

            // 记录积分获得
            PointsRecord earnRecord = new PointsRecord();
            earnRecord.setCustomerId(customer.getId());
            earnRecord.setType(1); // 订单获得
            earnRecord.setPoints(pointsEarned);
            earnRecord.setOrderId(order.getId());
            earnRecord.setRemark("订单消费获得");
            earnRecord.setCreateTime(LocalDateTime.now());
            pointsRecordMapper.insert(earnRecord);
        }

        // 8. 保存订单
        int result = orderMapper.update(order);
        if (result <= 0) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "完成订单失败");
        }
    }

    /**
     * 取消订单
     */
    @Override
    public void cancel(OrderCancelDTO orderCancelDTO) {
        // 校验订单ID
        if (orderCancelDTO.getId() == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "订单ID不能为空");
        }

        // 查询订单是否存在
        Order order = orderMapper.selectById(orderCancelDTO.getId());
        if (order == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "订单不存在");
        }

        // 只能取消待支付订单
        if (order.getOrderStatus() != 1) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "只能取消待支付订单");
        }

        // 设置取消信息
        order.setOrderStatus(3);
        order.setCancelReason(orderCancelDTO.getCancelReason());
        order.setUpdateTime(LocalDateTime.now());
        
        int result = orderMapper.update(order);
        if (result <= 0) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "取消订单失败");
        }
    }

    /**
     * 删除订单
     */
    @Override
    public void delete(Long id) {
        // 查询订单是否存在
        Order order = orderMapper.selectById(id);
        if (order == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "订单不存在");
        }

        // 待支付订单不能删除
        if (order.getOrderStatus() == 1) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "待支付订单不能删除");
        }

        // 删除订单明细
        orderDetailMapper.deleteByOrderId(id);
        
        // 删除订单
        int result = orderMapper.deleteById(id);
        if (result <= 0) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "删除订单失败");
        }
    }
}
