package com.xgrt.mapper;

import com.github.pagehelper.Page;
import com.xgrt.annotation.AutoFill;
import com.xgrt.dto.DishPageQueryDTO;
import com.xgrt.entity.Dish;
import com.xgrt.enumeration.OperationType;
import com.xgrt.vo.DishVO;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface DishMapper {

    /**
     * 根据分类id查询菜品数量
     * @param categoryId
     * @return
     */
    @Select("select count(id) from dish where category_id = #{categoryId}")
    Integer countByCategoryId(Long categoryId);

    /**
     * 插入菜品数据
     * 有自动填充
     * @param dish
     */
    @AutoFill(OperationType.INSERT)
    void insert(Dish dish);

    /**
     * 菜品分页查询
     * 动态SQL
     * @param dishPageQueryDTO
     * @return
     */
    Page<DishVO> pageQuery(DishPageQueryDTO dishPageQueryDTO);

    @Select("select * from dish where id = #{id}")
    Dish getById(Long id);

    @Delete("delete from dish where id = #{id}")
    void deleteById(Long id);

    /**
     * 根据 菜品id集合 批量删除菜品
     * @param ids
     */
    void deleteByIds(List<Long> ids);

    /**
     * 修改菜品信息
     * 动态SQL
     * 有修改时间和修改人，可以使用自动填充
     * @param dish
     */
    @AutoFill(value = OperationType.UPDATE)
    void update(Dish dish);

    /**
     * 按分类id查询菜品
     * @param categoryId
     * @return
     */
    @Select("select * from dish where category_id=#{categoryId} order by create_time desc")
    List<Dish> listByCategoryId(Long categoryId);

    /**
     * 根据id批量查询菜品
     * @param ids
     * @return
     */
    List<Dish> getByIdBatch(List<Long> ids);
}
