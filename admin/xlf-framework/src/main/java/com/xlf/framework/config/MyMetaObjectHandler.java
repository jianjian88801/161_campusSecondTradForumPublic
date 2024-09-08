package com.xlf.framework.config;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.xlf.common.security.util.SecurityUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Date;

@Slf4j
@Component
public class MyMetaObjectHandler implements MetaObjectHandler {

    /**
     * 插入填充字段
     * @param metaObject
     */
    @Override
    public void insertFill(MetaObject metaObject) {
        try {
//            log.info("start insert fill ....");
            //填充创建时间
            this.strictInsertFill(metaObject, "createTime", Date.class, new Date()); // 起始版本 3.3.0(推荐使用)
            //填充创建人
            this.strictInsertFill(metaObject, "createBy", String.class, SecurityUtils.getUsername());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 更新填充字段
     * @param metaObject
     */
    @Override
    public void updateFill(MetaObject metaObject) {
        try {
//            log.info("start update fill ....");
            this.strictUpdateFill(metaObject, "updateTime", Date.class, new Date()); // 起始版本 3.3.0(推荐)
            this.strictUpdateFill(metaObject, "updateBy", String.class, SecurityUtils.getUsername());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
