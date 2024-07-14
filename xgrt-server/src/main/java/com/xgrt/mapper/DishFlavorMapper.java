package com.xgrt.mapper;

import com.xgrt.entity.DishFlavor;
import org.apache.ibatis.annotations.Delete;
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

    /**
     * 根据菜品ID删除对应的口味属性
     * @param dishId
     */
    @Delete("delete from dish_flavor where dish_id=#{dishId}")
    void deleteByDishId(Long dishId);

    /**
     * 根据 菜品id集合 批量删除关联的口味
     * @param dishIds
     */
    void deleteByDishIds(List<Long> dishIds);
}
