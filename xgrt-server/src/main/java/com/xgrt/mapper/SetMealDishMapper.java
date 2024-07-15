package com.xgrt.mapper;

import com.xgrt.entity.SetmealDish;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface SetMealDishMapper {
    /**
     * 根据菜品ID查询对应的套餐ID
     * 动态SQL
     * @param dishIds
     * @return
     */
    //select setmeal_id from setmeal_dish where dish_id in (1,2,3……)
    List<Long> getSetMealDishIds(List<Long> dishIds);

    /**
     * 批量插入 套餐-菜品关系
     * @param setmealDishes
     */
    void insertBetch(List<SetmealDish> setmealDishes);
}
