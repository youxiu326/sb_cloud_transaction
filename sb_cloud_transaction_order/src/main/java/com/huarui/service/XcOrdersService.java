package com.huarui.service;

import com.alibaba.druid.support.json.JSONUtils;
import com.huarui.config.RabbitMQConfig;
import com.huarui.controller.OrderCtrl;
import com.huarui.dao.XcOrdersDetailRepository;
import com.huarui.dao.XcOrdersPayRepository;
import com.huarui.dao.XcOrdersRepository;
import com.huarui.dao.XcTaskRepository;
import com.huarui.model.XcOrders;
import com.huarui.model.XcOrdersDetail;
import com.huarui.model.XcOrdersPay;
import com.huarui.model.XcTask;
import com.huarui.util.JsonUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.annotation.Transient;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
public class XcOrdersService {

    private static final Logger LOGGER = LoggerFactory.getLogger(XcOrdersService.class);

    @Autowired
    private XcOrdersRepository ordersRepository;

    @Autowired
    private XcOrdersDetailRepository ordersDetailRepository;

    @Autowired
    private XcOrdersPayRepository payRepository;

    @Autowired
    private XcTaskRepository xcTaskRepository;

    /**
     *  创建订单
     *   注意: taskId 需和选课服务中taskid一致
     * @param items
     */
    @Transactional(rollbackFor = Exception.class)
    public void pay(Map<String,Integer> items){

        List<XcOrdersDetail> details = new ArrayList<>();

        Date date = new Date();
        Float orderPrice = 0F;
        String orderNumber = UUID.randomUUID().toString().substring(0,10);
        String status = "pay";

        for (Map.Entry<String, Integer> entry : items.entrySet()) {

            String itemId =  entry.getKey();//商品id
            Integer itemNum = entry.getValue();//购买数量
            Float itemPrice = itemId.equals("001")?10F:20F;

            orderPrice += (itemPrice*itemNum);

            XcOrdersDetail detail = new XcOrdersDetail();
            detail.setItemId(itemId);
            detail.setItemNum(itemNum);
            detail.setItemPrice(itemPrice);
            detail.setStartTime(date);
            detail.setEndTime(date);
            detail.setOrderNumber(orderNumber);
            details.add(detail);
        }



        XcOrders orders = new XcOrders();
        orders.setOrderNumber(orderNumber);
        orders.setStartTime(date);
        orders.setEndTime(date);
        orders.setStatus(status);//创建状态
        orders.setPrice(orderPrice);
        orders.setDetails(JsonUtils.parseString(details));

        XcOrdersPay pay = new XcOrdersPay();
        pay.setOrderNumber(orderNumber);
        pay.setStatus(status);

        //保存订单 订单详情 支付记录
        ordersRepository.save(orders);
        ordersDetailRepository.save(details);
        payRepository.save(pay);

        //添加选课消息
        XcTask task = new XcTask();
        task.setCreateTime(date);
        task.setUpdateTime(date);
        task.setVersion(1);//乐观锁版本号
        task.setStatus(status);
        task.setRequestBody(JsonUtils.parseString(orders));
        task.setMqExchange(RabbitMQConfig.LEARNING_EXCHANGE);
        task.setMqRoutingkey(RabbitMQConfig.LEARNING_ADD__KEY);
        xcTaskRepository.save(task);
    }

}
