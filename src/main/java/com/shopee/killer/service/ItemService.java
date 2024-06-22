package com.shopee.killer.service;

import com.shopee.killer.utils.Result;

public interface ItemService {
    Result getOneItemDetail(String itemId, String shopId) throws Exception;
}
