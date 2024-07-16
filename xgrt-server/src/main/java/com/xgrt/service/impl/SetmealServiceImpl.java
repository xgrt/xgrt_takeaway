package com.xgrt.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.xgrt.dto.SetmealDTO;
import com.xgrt.dto.SetmealPageQueryDTO;
import com.xgrt.entity.Setmeal;
import com.xgrt.entity.SetmealDish;
import com.xgrt.mapper.SetMealDishMapper;
import com.xgrt.mapper.SetmealMapper;
import com.xgrt.result.PageResult;
import com.xgrt.service.SetmealService;
import com.xgrt.vo.SetmealVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class SetmealServiceImpl implements SetmealService {
    @Autowired
    private SetmealMapper setmealMapper;
    @Autowired
    private SetMealDishMapper setMealDishMapper;

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
        setmealMapper.update(setmeal);

        //修改 套餐菜品
        Long setmealId = setmealDTO.getId();
        //按套餐Id删除套餐菜品
        setMealDishMapper.deleteBySetmealId(setmealId);

        //重新添加套餐菜品
        List<SetmealDish> setmealDishes = setmealDTO.getSetmealDishes();
        for (SetmealDish setmealDish : setmealDishes) {
            setmealDish.setSetmealId(setmealId);
        }
        setMealDishMapper.insertBatch(setmealDishes);
    }
}
