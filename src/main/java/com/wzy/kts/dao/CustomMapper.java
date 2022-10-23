package com.wzy.kts.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

/**
 * @author yu.wu
 * @description 自定义mapper
 * @date 2022/10/22 23:00
 */
public interface CustomMapper<T> extends BaseMapper<T> {

    default void insertBatch(List<T> list){
        for (T t : list) {
            insert(t);
        }
    }
}
