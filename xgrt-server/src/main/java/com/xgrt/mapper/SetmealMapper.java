package com.xgrt.mapper;

import com.github.pagehelper.Page;
import com.xgrt.annotation.AutoFill;
import com.xgrt.dto.SetmealDTO;
import com.xgrt.dto.SetmealPageQueryDTO;
import com.xgrt.entity.Setmeal;
import com.xgrt.enumeration.OperationType;
import com.xgrt.vo.SetmealVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface SetmealMapper {

    /**
     * 根据分类id查询套餐的数量
     * @param id
     * @return
     */
    @Select("select count(id) from setmeal where category_id = #{categoryId}")
    Integer countByCategoryId(Long id);

    /**
     * 分页查询套餐
     * 动态SQL
     * @param setmealPageQueryDTO
     * @return
     */
    Page<SetmealVO> pageQuery(SetmealPageQueryDTO setmealPageQueryDTO);


    /**
     * 新增套餐
     * @param setmeal
     */
    @AutoFill(value = OperationType.INSERT)
    void insert(Setmeal setmeal);
}
