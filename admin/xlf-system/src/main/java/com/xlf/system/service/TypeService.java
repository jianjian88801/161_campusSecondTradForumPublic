package com.xlf.system.service;

import com.xlf.system.domain.Type;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
* @author 小新
* @description 针对表【type】的数据库操作Service
* @createDate 2023-04-11 19:25:53
*/
public interface TypeService extends IService<Type> {

    List<Type> getPage(String name,Integer status);

    String checkMenuNameUnique(Type type);

    Boolean deleteTypeByIds(Long[] typeIds);
}
