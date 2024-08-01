
package com.monitor.contract.config;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import org.apache.ibatis.reflection.MetaObject;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import java.time.Instant;
import java.util.Objects;

@Configuration
public class MybatisPlusConfig implements MetaObjectHandler {

    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor(){
        MybatisPlusInterceptor mi = new MybatisPlusInterceptor();
        mi.addInnerInterceptor(new PaginationInnerInterceptor());
        return mi;
    }

    @Override
    public void insertFill(MetaObject metaObject) {
        if (Objects.isNull(getFieldValByName("createTime", metaObject))) {
            this.setFieldValByName("createTime", Instant.now(), metaObject);
        }

        if (Objects.isNull(getFieldValByName("modifyTime", metaObject))) {
            this.setFieldValByName("modifyTime", Instant.now(), metaObject);
        }
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        if (Objects.isNull(getFieldValByName("modifyTime", metaObject))) {
            this.setFieldValByName("modifyTime", Instant.now(), metaObject);
        }
    }
}
