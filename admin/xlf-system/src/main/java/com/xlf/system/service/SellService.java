package com.xlf.system.service;

import com.xlf.system.domain.Sell;
import com.baomidou.mybatisplus.extension.service.IService;
import com.xlf.system.domain.Topic;

import java.util.List;

/**
* @author 小新
* @description 针对表【sell】的数据库操作Service
* @createDate 2023-03-15 14:47:06
*/
public interface SellService extends IService<Sell> {

    List<Sell> getList(Integer orderType, String name);

    Sell getSellById(Long sellId,String name);

    void addSell(Sell sell);
}
