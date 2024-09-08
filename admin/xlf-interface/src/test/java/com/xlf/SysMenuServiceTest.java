package com.xlf;

import com.alibaba.fastjson.JSON;
import com.xlf.system.service.SysMenuService;
import org.junit.jupiter.api.Test;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class SysMenuServiceTest {

    @Resource
    SysMenuService menuService;

    @Test
    void selectMenuTreeByUserId() {
        System.out.println(JSON.toJSONString(menuService.buildMenus(menuService.selectMenuTreeByUserId(1L))));
    }
}
