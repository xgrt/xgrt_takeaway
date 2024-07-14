package com.xgrt.service;

import com.xgrt.dto.DishDTO;
import com.xgrt.dto.DishPageQueryDTO;
import com.xgrt.result.PageResult;
import org.springframework.stereotype.Service;

import java.util.List;


public interface DishService {

    /**
     * 新建菜品和对应的口味
     * @param dishDTO
     */
    public void saveWithFlavor(DishDTO dishDTO);

    /**
     * 菜品分页查询
     * @param dishPageQueryDTO
     * @return
     */
    PageResult pageQuery(DishPageQueryDTO dishPageQueryDTO);

    /**
     * 菜品批量删除
     * @param ids
     */
    void deleteBatch(List<Long> ids);
}
