package com.xgrt.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.xgrt.constant.MessageConstant;
import com.xgrt.constant.StatusConstant;
import com.xgrt.dto.SetmealDTO;
import com.xgrt.dto.SetmealPageQueryDTO;
import com.xgrt.entity.Dish;
import com.xgrt.entity.Setmeal;
import com.xgrt.entity.SetmealDish;
import com.xgrt.exception.DeletionNotAllowedException;
import com.xgrt.exception.SetmealEnableFailedException;
import com.xgrt.mapper.DishMapper;
import com.xgrt.mapper.SetMealDishMapper;
import com.xgrt.mapper.SetmealMapper;
import com.xgrt.result.PageResult;
import com.xgrt.service.SetmealService;
import com.xgrt.vo.SetmealVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class SetmealServiceImpl implements SetmealService {
    @Autowired
    private SetmealMapper setmealMapper;
    @Autowired
    private SetMealDishMapper setMealDishMapper;

    @Autowired
    private DishMapper dishMapper;
    /**
     * 分页查询套餐
     * @param setmealPageQueryDTO
     * @return
     */
    public PageResult pageQuery(SetmealPageQueryDTO setmealPageQueryDTO) {
        PageHelper.startPage(setmealPageQueryDTO.getPage(),setmealPageQueryDTO.getPageSize());
        Page<SetmealVO>  page=setmealMapper.pageQuery(setmealPageQueryDTO);
        return new PageResult(page.getTotal(),page.getResult());
    }

    /**
     * 新增 套餐 和 套餐菜品
     * 涉及setmeal表和setmeal_dish表
     * @param setmealDTO
     */
    @Transactional
    public void saveWithSstmealDish(SetmealDTO setmealDTO) {
        Setmeal setmeal = new Setmeal();
        BeanUtils.copyProperties(setmealDTO,setmeal);

        //向 setmeal表 插入数据 并获取主键值
        setmealMapper.insert(setmeal);
        Long setmealId = setmeal.getId();

        //向 setmeal_dish表 插入数据
        List<SetmealDish> setmealDishes = setmealDTO.getSetmealDishes();
        for (SetmealDish setmealDish : setmealDishes) {
            setmealDish.setSetmealId(setmealId);
        }
        setMealDishMapper.insertBatch(setmealDishes);
    }

    /**
     * 根据id查询套餐
     * @param id
     * @return
     */
    public SetmealVO getById(Long id) {
        SetmealVO setmealVO = new SetmealVO();

        //根据id查询 套餐
        Setmeal setmeal=setmealMapper.getById(id);

        //根据 套餐id 查询 套餐菜品
        List<SetmealDish> setmealDishes=setMealDishMapper.getBySetmealId(id);

        //赋值
        BeanUtils.copyProperties(setmeal,setmealVO);
        setmealVO.setSetmealDishes(setmealDishes);
        return setmealVO;
    }

    /**
     * 根据 套餐Id 修改 套餐基本信息 和 套餐菜品
     * @param setmealDTO
     */
    @Transactional
    public void updateWithSetmealDish(SetmealDTO setmealDTO) {
        Setmeal setmeal=new Setmeal();
        BeanUtils.copyProperties(setmealDTO,setmeal);

        //修改 套餐基本信息
        //TODO：name重复时发送了正确的错误信息 但 前端不显示
        setmealMapper.update(setmeal);

        //修改 套餐菜品
        //按套餐Id删除套餐菜品
        setMealDishMapper.deleteBySetmealId(setmealDTO.getId());
        //重新添加套餐菜品
        List<SetmealDish> setmealDishes = setmealDTO.getSetmealDishes();
        if (setmealDishes!=null&& !setmealDishes.isEmpty()) {
            for (SetmealDish setmealDish : setmealDishes) {
                setmealDish.setSetmealId(setmealDTO.getId());
            }
        }
        setMealDishMapper.insertBatch(setmealDishes);
    }

    /**
     * 批量删除套餐 和 关联的套餐菜品
     * @param ids
     */
    @Transactional
    public void deleteBatch(List<Long> ids) {
        //能不能删——判断套餐中有没有正在起售的套餐
        for (Long id : ids) {
            Setmeal setmeal = setmealMapper.getById(id);
            if (setmeal.getStatus()== StatusConstant.ENABLE){
                throw new DeletionNotAllowedException(MessageConstant.SETMEAL_ON_SALE);
            }
        }

        //批量删除删除套餐
        setmealMapper.deleteBatch(ids);

        //根据套餐id批量删除套餐菜品
        setMealDishMapper.deleteBySetmealIdBatch(ids);
    }

    /**
     * 起售停售套餐
     * @param status
     * @param id
     */
    public void startAndStop(Integer status, Long id) {
        if (status==StatusConstant.ENABLE){
            //能不能起售——套餐中的菜品都是起售状态
            List<SetmealDish> setmealDishes = setMealDishMapper.getBySetmealId(id);
            /*for (SetmealDish setmealDish : setmealDishes) {
                Dish dish = dishMapper.getById(setmealDish.getDishId());
                if (dish.getStatus()==StatusConstant.DISABLE){
                    throw new SetmealEnableFailedException(MessageConstant.SETMEAL_ENABLE_FAILED);
                }
            }*/

            //优化：先把 菜品全部查询出来，再去筛选
            List<Long> dishIds = new ArrayList<>();
            for (SetmealDish setmealDish : setmealDishes) {
                dishIds.add(setmealDish.getDishId());
            }
            List<Dish> dishes=dishMapper.getByIdBatch(dishIds);
            for (Dish dish : dishes) {
                if (dish.getStatus()== StatusConstant.DISABLE){
                    throw new SetmealEnableFailedException(MessageConstant.SETMEAL_ENABLE_FAILED);
                }
            }
        }
        //修改套餐状态
        Setmeal setmeal=Setmeal.builder()
                .id(id)
                .status(status)
                .build();
        setmealMapper.update(setmeal);
    }
}
