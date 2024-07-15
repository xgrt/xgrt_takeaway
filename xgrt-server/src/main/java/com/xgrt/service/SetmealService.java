package com.xgrt.service;

import com.xgrt.dto.SetmealDTO;
import com.xgrt.dto.SetmealPageQueryDTO;
import com.xgrt.result.PageResult;

public interface SetmealService {
    /**
     * 分页查询套餐
     * @param setmealPageQueryDTO
     * @return
     */
    PageResult pageQuery(SetmealPageQueryDTO setmealPageQueryDTO);

    /**
     * 新增套餐 和 套餐-菜品关联项
     * @param setmealDTO
     */
    void saveWithSstmealDish(SetmealDTO setmealDTO);
}
