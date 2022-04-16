package com.rxl.controller;

import com.rxl.springbootoperation.MqOperationProductor2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author ren.xiaolong
 * @date 2022/4/16
 * @Description
 */
@RestController
public class springbootOperationController {

    @Autowired
    MqOperationProductor2 mqOperationProductor2;

    @GetMapping("ceshi1")
    public void test1(){
        mqOperationProductor2.sendMessage();
    }
}
