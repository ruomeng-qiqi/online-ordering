package com.ruomeng.onlineorderingbackend.service.impl;

import cn.hutool.core.util.IdUtil;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.ruomeng.onlineorderingbackend.common.PageResult;
import com.ruomeng.onlineorderingbackend.exception.BusinessException;
import com.ruomeng.onlineorderingbackend.exception.ErrorCode;
import com.ruomeng.onlineorderingbackend.mapper.*;
import com.ruomeng.onlineorderingbackend.model.dto.OrderCancelDTO;
import com.ruomeng.onlineorderingbackend.model.dto.OrderDTO;
import com.ruomeng.onlineorderingbackend.model.dto.OrderPageQueryDTO;
import com.ruomeng.onlineorderingbackend.model.dto.PaymentDTO;
import com.ruomeng.onlineorderingbackend.model.entity.*;
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
    public OrderVO getById(Long id) {
        // 查询订单
        Order order = orderMapper.selectById(id);
        if (order == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "订单不存在");
        }

        // 转换为VO
        OrderVO orderVO = new OrderVO();
        BeanUtils.copyProperties(order, orderVO);

        // 查询顾客信息
        Customer customer = customerMapper.selectById(order.getCustomerId());
        if (customer != null) {
            orderVO.setCustomerName(customer.getNickname());
            orderVO.setIsMember(customer.getIsMember());
        }

        // 查询餐台信息（包括已删除的餐台）
        DiningTable table = diningTableMapper.selectByIdIncludeDeleted(order.getTableId());
        if (table != null) {
            orderVO.setTableNumber(table.getTableNumber());
            orderVO.setTableName(table.getTableName());
        }

        // 查询订单明细
        List<OrderDetail> orderDetails = orderDetailMapper.selectByOrderId(id);
        List<OrderVO.OrderDetailItemVO> itemVOList = orderDetails.stream().map(detail -> {
            OrderVO.OrderDetailItemVO itemVO = new OrderVO.OrderDetailItemVO();
            BeanUtils.copyProperties(detail, itemVO);
            return itemVO;
        }).collect(Collectors.toList());
        orderVO.setDetails(itemVOList);

        return orderVO;
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

        // 4. 计算实付金额
        BigDecimal actualAmount = order.getTotalAmount()
                .subtract(order.getDiscountAmount() != null ? order.getDiscountAmount() : BigDecimal.ZERO);

        // 5. 更新订单状态
        order.setOrderStatus(2); // 已完成
        order.setPaymentMethod(2); // 线下支付
        order.setActualAmount(actualAmount);
        order.setPointsUsed(0); // 管理端完成订单不使用积分
        order.setPointsDeduction(BigDecimal.ZERO);
        order.setCheckoutTime(LocalDateTime.now());
        order.setUpdateTime(LocalDateTime.now());

        // 6. 发放积分（只有会员才能获得积分）
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

        // 7. 保存订单
        int result = orderMapper.update(order);
        if (result <= 0) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "完成订单失败");
        }

        // 8. 更新餐台状态为空闲
        DiningTable table = diningTableMapper.selectById(order.getTableId());
        if (table != null) {
            table.setStatus(0); // 0-空闲
            table.setUpdateTime(LocalDateTime.now());
            diningTableMapper.updateStatus(table);
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

        // 更新餐台状态为空闲
        DiningTable table = diningTableMapper.selectById(order.getTableId());
        if (table != null) {
            table.setStatus(0); // 0-空闲
            table.setUpdateTime(LocalDateTime.now());
            diningTableMapper.updateStatus(table);
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

    /**
     * 根据顾客ID查询订单列表
     */
    @Override
    public List<OrderVO> listByCustomerId(Long customerId) {
        // 校验参数
        if (customerId == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "顾客ID不能为空");
        }

        // 查询订单列表
        List<Order> orders = orderMapper.listByCustomerId(customerId);

        // 转换为VO列表
        return orders.stream().map(order -> {
            OrderVO orderVO = new OrderVO();
            BeanUtils.copyProperties(order, orderVO);

            // 查询餐台信息（包括已删除的餐台）
            DiningTable table = diningTableMapper.selectByIdIncludeDeleted(order.getTableId());
            if (table != null) {
                orderVO.setTableNumber(table.getTableNumber());
                orderVO.setTableName(table.getTableName());
            }

            // 查询订单明细
            List<OrderDetail> orderDetails = orderDetailMapper.selectByOrderId(order.getId());
            List<OrderVO.OrderDetailItemVO> detailVOList = orderDetails.stream().map(detail -> {
                OrderVO.OrderDetailItemVO detailVO = new OrderVO.OrderDetailItemVO();
                BeanUtils.copyProperties(detail, detailVO);
                return detailVO;
            }).collect(Collectors.toList());
            orderVO.setDetails(detailVOList);

            return orderVO;
        }).collect(Collectors.toList());
    }

    /**
     * 根据ID查询订单详情（含明细）
     */
    @Override
    public OrderVO getByIdWithDetails(Long id) {
        // 查询订单
        Order order = orderMapper.selectById(id);
        if (order == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "订单不存在");
        }

        // 转换为VO
        OrderVO orderVO = new OrderVO();
        BeanUtils.copyProperties(order, orderVO);

        // 查询顾客信息
        Customer customer = customerMapper.selectById(order.getCustomerId());
        if (customer != null) {
            orderVO.setCustomerName(customer.getNickname());
            orderVO.setIsMember(customer.getIsMember());
        }

        // 查询餐台信息（包括已删除的餐台）
        DiningTable table = diningTableMapper.selectByIdIncludeDeleted(order.getTableId());
        if (table != null) {
            orderVO.setTableNumber(table.getTableNumber());
            orderVO.setTableName(table.getTableName());
        }

        // 查询订单明细
        List<OrderDetail> orderDetails = orderDetailMapper.selectByOrderId(id);
        List<OrderVO.OrderDetailItemVO> detailVOList = orderDetails.stream().map(detail -> {
            OrderVO.OrderDetailItemVO detailVO = new OrderVO.OrderDetailItemVO();
            BeanUtils.copyProperties(detail, detailVO);
            return detailVO;
        }).collect(Collectors.toList());
        orderVO.setDetails(detailVOList);

        return orderVO;
    }

    /**
     * 创建订单（用户端）
     */
    @Override
    @Transactional
    public OrderVO createOrder(OrderDTO orderDTO) {
        // 1. 校验参数
        if (orderDTO.getCustomerId() == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "顾客ID不能为空");
        }
        if (orderDTO.getTableId() == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "餐台ID不能为空");
        }
        if (orderDTO.getDetails() == null || orderDTO.getDetails().isEmpty()) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "订单明细不能为空");
        }

        // 2. 校验顾客是否存在
        Customer customer = customerMapper.selectById(orderDTO.getCustomerId());
        if (customer == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "顾客不存在");
        }

        // 3. 校验餐台是否存在
        DiningTable table = diningTableMapper.selectById(orderDTO.getTableId());
        if (table == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "餐台不存在");
        }

        // 4. 计算订单总金额
        BigDecimal totalAmount = BigDecimal.ZERO;
        for (OrderDTO.OrderDetailDTO detailDTO : orderDTO.getDetails()) {
            if (detailDTO.getQuantity() == null || detailDTO.getQuantity() <= 0) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "商品数量必须大于0");
            }
            if (detailDTO.getPrice() == null || detailDTO.getPrice().compareTo(BigDecimal.ZERO) <= 0) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "商品价格必须大于0");
            }
            BigDecimal amount = detailDTO.getPrice().multiply(new BigDecimal(detailDTO.getQuantity()));
            totalAmount = totalAmount.add(amount);
        }

        // 5. 创建订单
        Order order = new Order();
        order.setOrderNumber(IdUtil.getSnowflakeNextIdStr()); // 使用雪花算法生成唯一订单号
        order.setCustomerId(orderDTO.getCustomerId());
        order.setTableId(orderDTO.getTableId());
        order.setTotalAmount(totalAmount);
        order.setDiscountAmount(BigDecimal.ZERO);
        order.setPointsDeduction(BigDecimal.ZERO);
        order.setPointsUsed(0);
        order.setPointsEarned(0);
        order.setOrderStatus(1); // 待支付
        order.setRemark(orderDTO.getRemark());
        order.setCreateTime(LocalDateTime.now());
        order.setUpdateTime(LocalDateTime.now());

        int result = orderMapper.insert(order);
        if (result <= 0) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "创建订单失败");
        }

        // 6. 插入订单明细
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
        }

        // 7. 更新餐台状态为占用
        table.setStatus(1); // 1-占用
        table.setUpdateTime(LocalDateTime.now());
        int updateTableResult = diningTableMapper.updateStatus(table);
        if (updateTableResult <= 0) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "更新餐台状态失败");
        }

        // 8. 返回订单详情
        return getByIdWithDetails(order.getId());
    }

    /**
     * 查询餐台的待支付订单
     */
    @Override
    public OrderVO getPendingOrderByTableId(Long tableId) {
        // 校验参数
        if (tableId == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "餐台ID不能为空");
        }

        // 查询待支付订单
        Order order = orderMapper.selectPendingOrderByTableId(tableId);
        if (order == null) {
            return null;
        }

        // 返回订单详情
        return getByIdWithDetails(order.getId());
    }

    /**
     * 删除订单（用户端软删除）
     */
    @Override
    @Transactional
    public void deleteOrderByUser(Long id) {
        // 1. 校验订单ID
        if (id == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "订单ID不能为空");
        }

        // 2. 查询订单
        Order order = orderMapper.selectById(id);
        if (order == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "订单不存在");
        }

        // 3. 只能删除已取消的订单
        if (order.getOrderStatus() != 3) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "只能删除已取消的订单");
        }

        // 4. 软删除订单
        int result = orderMapper.softDelete(id);
        if (result <= 0) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "删除订单失败");
        }
    }

    /**
     * 支付订单
     */
    @Override
    @Transactional
    public void payOrder(PaymentDTO paymentDTO) {
        // 1. 校验参数
        if (paymentDTO.getOrderId() == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "订单ID不能为空");
        }

        // 2. 查询订单
        Order order = orderMapper.selectById(paymentDTO.getOrderId());
        if (order == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "订单不存在");
        }

        // 3. 只能支付待支付订单
        if (order.getOrderStatus() != 1) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "只能支付待支付订单");
        }

        // 4. 查询顾客信息
        Customer customer = customerMapper.selectById(order.getCustomerId());
        if (customer == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "顾客不存在");
        }

        // 5. 处理积分抵扣
        if (paymentDTO.getPointsUsed() != null && paymentDTO.getPointsUsed() > 0) {
            // 检查积分是否足够
            if (customer.getPoints() < paymentDTO.getPointsUsed()) {
                throw new BusinessException(ErrorCode.OPERATION_ERROR, "积分不足");
            }

            // 扣除顾客积分
            customer.setPoints(customer.getPoints() - paymentDTO.getPointsUsed());
            customer.setUpdateTime(LocalDateTime.now());
            customerMapper.updatePoints(customer);

            // 记录积分使用
            PointsRecord useRecord = new PointsRecord();
            useRecord.setCustomerId(customer.getId());
            useRecord.setType(2); // 订单使用
            useRecord.setPoints(-paymentDTO.getPointsUsed()); // 负数表示扣除
            useRecord.setOrderId(order.getId());
            useRecord.setRemark("订单消费抵扣");
            useRecord.setCreateTime(LocalDateTime.now());
            pointsRecordMapper.insert(useRecord);
        }

        // 6. 更新订单状态
        order.setOrderStatus(2); // 已完成
        order.setPaymentMethod(paymentDTO.getPaymentMethod());
        order.setPointsUsed(paymentDTO.getPointsUsed() != null ? paymentDTO.getPointsUsed() : 0);
        order.setPointsDeduction(paymentDTO.getPointsDeduction() != null ? paymentDTO.getPointsDeduction() : BigDecimal.ZERO);
        order.setActualAmount(paymentDTO.getActualAmount());
        order.setCheckoutTime(LocalDateTime.now());
        order.setUpdateTime(LocalDateTime.now());

        // 7. 发放积分（只有会员才能获得积分）
        if (customer.getIsMember() == 1) {
            // 计算获得的积分：消费1元=1积分，向下取整（后端计算，不信任前端传值）
            Integer pointsEarned = paymentDTO.getActualAmount().setScale(0, RoundingMode.DOWN).intValue();
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
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "支付订单失败");
        }

        // 9. 更新餐台状态为空闲
        DiningTable table = diningTableMapper.selectById(order.getTableId());
        if (table != null) {
            table.setStatus(0); // 0-空闲
            table.setUpdateTime(LocalDateTime.now());
            diningTableMapper.updateStatus(table);
        }
    }
}
