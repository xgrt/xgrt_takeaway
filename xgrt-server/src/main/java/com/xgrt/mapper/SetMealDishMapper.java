package com.xgrt.mapper;

import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface SetMealDishMapper {
    /**
     * 根据菜品ID查询对应的菜单ID
     * 动态SQL
     * @param dishIds
     * @return
     */
    //select setmeal_id from setmeal_dish where dish_id in (1,2,3……)
    List<Long> getSetMealDishIds(List<Long> dishIds);
}
