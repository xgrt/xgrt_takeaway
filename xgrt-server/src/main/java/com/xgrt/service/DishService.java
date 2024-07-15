package com.xgrt.service;

import com.xgrt.dto.DishDTO;
import com.xgrt.dto.DishPageQueryDTO;
import com.xgrt.entity.Dish;
import com.xgrt.result.PageResult;
import com.xgrt.vo.DishVO;
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

    /**
     * 根据ID查询菜品和对应的口味数据
     * @param id
     * @return
     */
    DishVO getByIdWithFlavor(Long id);

    /**
     * 根据id修改菜品基本信息和对应的口味信息
     * @param dishDTO
     */
    void updateWithFlavor(DishDTO dishDTO);

    /**
     * 按分类id查询菜品
     * @param categoryId
     * @return
     */
    List<Dish> listByCategoryId(Long categoryId);


    /**
     * 起售停售菜品
     * @param id
     * @param status
     */
    //TODO：起售停售菜品（等 套餐功能 和 用户端展示菜品功能 完成）
    /*void startOrStop(Long id, Integer status);*/
}
