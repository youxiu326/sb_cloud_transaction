package com.huarui.controller;

import com.huarui.dao.XcOrdersRepository;
import com.huarui.mq.ChooseCourseTask;
import com.huarui.service.XcOrdersService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Controller
@RequestMapping("/order")
public class OrderCtrl {

    private static final Logger LOGGER = LoggerFactory.getLogger(OrderCtrl.class);

    @Autowired
    private XcOrdersService ordersService;

    /**
     * 首页
     * @return
     */
    @RequestMapping("/index")
    public String index(){
        return "index";
    }

    /***
     * 下单
     * {"productId","quantity"}
     * @param map
     * @return
     */
    @PostMapping("/pay")
    @ResponseBody
    public String pay(@RequestBody Map<String,Integer> map){


        ordersService.pay(map);

        return "ok";
    }


}
