package com.xgrt.service.impl;

import com.xgrt.dto.DishDTO;
import com.xgrt.entity.Dish;
import com.xgrt.entity.DishFlavor;
import com.xgrt.mapper.DishFlavorMapper;
import com.xgrt.mapper.DishMapper;
import com.xgrt.service.DishService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.beans.Transient;
import java.util.List;

@Service
@Slf4j
public class DishServiceImpl implements DishService {

    @Autowired
    private DishMapper dishMapper;

    @Autowired
    private DishFlavorMapper dishFlavorMapper;

    /**
     * 新建菜品和对应的口味
     * 涉及 多表操作，要保证 数据一致性 和 方法原子性
     * 开启 事务控制
     * 要在 SpringBoot启动器 加上 注解方式 的事务管理
     * @param dishDTO
     */
    @Transactional
    public void saveWithFlavor(DishDTO dishDTO) {
        Dish dish = new Dish();

        BeanUtils.copyProperties(dishDTO,dish);
        //向 菜品表 插入1条数据
        dishMapper.insert(dish);

        //获取 insert 语句获取的 主键值
        Long dishId = dish.getId();

        List<DishFlavor> flavors = dishDTO.getFlavors();
        if (flavors != null && !flavors.isEmpty()) {
            //向 口味实体 插入 dishId
            flavors.forEach(dishFlavor -> {
                dishFlavor.setDishId(dishId);
            });
            //向 口味表 插入n条数据
            dishFlavorMapper.insertBetch(flavors);
        }
    }
}
