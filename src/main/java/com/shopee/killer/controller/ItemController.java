package com.shopee.killer.controller;

import com.shopee.killer.service.ItemService;
import com.shopee.killer.utils.Result;
import lombok.extern.slf4j.Slf4j;
import net.sf.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/item")
@Slf4j
public class ItemController {
    @Autowired
    private ItemService itemService;

    @PostMapping("/getItemDetail")
    public Result getItemDetail(@RequestBody JSONObject params) {
        log.info("接收到获取商品详情请求");
        log.info("itemId: " + params.getString("itemId") + ", shopId: " + params.getString("shopId"));
        try {
            return itemService.getOneItemDetail(params.getString("itemId"), params.getString("shopId"));
        } catch (Exception e) {
            log.error("获取商品详情失败", e);
            return null;
        }
    }
}
