package com.xgrt.service;

import com.xgrt.dto.SetmealDTO;
import com.xgrt.dto.SetmealPageQueryDTO;
import com.xgrt.result.PageResult;
import com.xgrt.vo.SetmealVO;

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

    /**
     * 根据id查询套餐
     * @param id
     * @return
     */
    SetmealVO getById(Long id);

    /**
     * 修改 套餐 和 套餐菜品
     * @param setmealDTO
     */
    void updateWithSetmealDish(SetmealDTO setmealDTO);
}
