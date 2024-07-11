package com.xgrt.service;

import com.xgrt.dto.DishDTO;
import org.springframework.stereotype.Service;


public interface DishService {

    /**
     * 新建菜品和对应的口味
     * @param dishDTO
     */
    public void saveWithFlavor(DishDTO dishDTO);
}
