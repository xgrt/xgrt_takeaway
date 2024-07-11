package com.xgrt.mapper;

import com.xgrt.entity.DishFlavor;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface DishFlavorMapper {

    /**
     * 批量插入口味数据
     * 要用到动态SQL
     * @param flavors
     */
    void insertBetch(List<DishFlavor> flavors);
}
