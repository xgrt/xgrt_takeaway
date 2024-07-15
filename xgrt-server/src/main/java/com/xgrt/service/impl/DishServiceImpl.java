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

    /**
     * 根据ID查询菜品和对应的口味数据
     * 要查询 菜品表 和 口味表
     * @param id
     * @return
     */
    public DishVO getByIdWithFlavor(Long id) {
        //1、根据id查询菜品数据
        Dish dish = dishMapper.getById(id);

        //2、根据菜品id查询对应的口味数据
        List<DishFlavor> dishFlavors=dishFlavorMapper.getByDishId(id);

        //3、将查询的数据封装到VO
        DishVO dishVO = new DishVO();
        BeanUtils.copyProperties(dish,dishVO);//将 dish 的 属性 复制到 dishVO
        dishVO.setFlavors(dishFlavors);

        return dishVO;
    }

    /**
     * 根据id修改菜品基本信息和对应的口味信息
     * 操作菜品表和口味表
     * @param dishDTO
     */
    @Transactional
    public void updateWithFlavor(DishDTO dishDTO) {
        Dish dish = new Dish();
        BeanUtils.copyProperties(dishDTO,dish);
        //修改口味表基础信息
        dishMapper.update(dish);

        //口味表更新可能性太多，直接全部删除再重新添加
        //删除原有的口味信息
        dishFlavorMapper.deleteByDishId(dishDTO.getId());

        //重新插入口味数据
        List<DishFlavor> flavors = dishDTO.getFlavors();
        if (flavors != null && !flavors.isEmpty()) {
            flavors.forEach(dishFlavor -> {
                dishFlavor.setDishId(dishDTO.getId());
            });
            dishFlavorMapper.insertBetch(flavors);
        }
    }

    /**
     * 按分类id查询菜品
     * @param categoryId
     * @return
     */
    public List<Dish> listByCategoryId(Long categoryId) {
        return dishMapper.listByCategoryId(categoryId);
    }

    /**
     * 起售停售菜品
     * @param id
     * @param status
     */
    //TODO：起售停售菜品（等 套餐功能 和 用户端展示菜品功能 完成）
   /* public void startOrStop(Long id, Integer status) {
        Dish dish = new Dish();
        dish.setId(id);
        dish.setStatus(status);
        dishMapper.update(dish);
    }*/
}
