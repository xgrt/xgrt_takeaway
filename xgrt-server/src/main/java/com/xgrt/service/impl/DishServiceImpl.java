package com.xgrt.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.xgrt.constant.MessageConstant;
import com.xgrt.constant.StatusConstant;
import com.xgrt.dto.DishDTO;
import com.xgrt.dto.DishPageQueryDTO;
import com.xgrt.entity.Dish;
import com.xgrt.entity.DishFlavor;
import com.xgrt.exception.DeletionNotAllowedException;
import com.xgrt.mapper.DishFlavorMapper;
import com.xgrt.mapper.DishMapper;
import com.xgrt.mapper.SetMealDishMapper;
import com.xgrt.result.PageResult;
import com.xgrt.service.DishService;
import com.xgrt.vo.DishVO;
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

    @Autowired
    private SetMealDishMapper setMealDishMapper;
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

    /**
     * 菜品分页查询
     * @param dishPageQueryDTO
     * @return
     */
    public PageResult pageQuery(DishPageQueryDTO dishPageQueryDTO) {
        PageHelper.startPage(dishPageQueryDTO.getPage(),dishPageQueryDTO.getPageSize());
        Page<DishVO> page=dishMapper.pageQuery(dishPageQueryDTO);
        return new PageResult(page.getTotal() ,page.getResult());
    }

    /**
     * 菜品批量删除
     * @param ids
     */
    @Transactional
    public void deleteBatch(List<Long> ids) {
        //1、判断当前菜品是否能够删除——是否存在起售中的菜品
        for (Long id : ids) {
            Dish dish=dishMapper.getById(id);
            if (dish.getStatus() == StatusConstant.ENABLE){
                throw new DeletionNotAllowedException(MessageConstant.DISH_ON_SALE);
            }
        }

        //2、判断当前菜品是否能够删除——是否被套餐关联
        List<Long> setMealDishIds = setMealDishMapper.getSetMealDishIds(ids);
        if (setMealDishIds != null && !setMealDishIds.isEmpty()) {
            throw new DeletionNotAllowedException(MessageConstant.DISH_BE_RELATED_BY_SETMEAL);
        }

        //3、删除菜品表中的菜品数据
        /*for (Long id : ids) {
            dishMapper.deleteById(id);

            //4、删除菜品关联的口味数据
            dishFlavorMapper.deleteByDishId(id);
        }*/

        //优化：发送的SQL太多 性能不稳定

        //根据 菜品id集合 批量删除 菜品数据
        //sql:delete from dish where id in (?????)
        dishMapper.deleteByIds(ids);

        //根据 菜品id集合 批量删除 关联的口味数据
        //sql:delete from dish_flavor where dish_id in (?????)
        dishFlavorMapper.deleteByDishIds(ids);
    }
}
