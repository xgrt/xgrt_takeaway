package com.xgrt.mapper;

import com.github.pagehelper.Page;
import com.xgrt.dto.EmployeePageQueryDTO;
import com.xgrt.entity.Employee;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface EmployeeMapper {

    /**
     * 简单SQL用注解配置
     * 复杂SQL如涉及到动态标签，通过Mapper映射文件进行配置
     * 根据用户名查询员工
     * @param username
     * @return
     */
    @Select("select * from employee where username = #{username}")
    Employee getByUsername(String username);

    @Insert("insert into employee (name, username, password, phone, sex, id_number, create_time, update_time, create_user, update_user) " +
            "values " +
            "(#{name},#{username},#{password},#{phone},#{sex},#{idNumber},#{createTime},#{updateTime},#{createUser},#{updateUser})")
    void insert(Employee employee);

    /**
     * 分页查询
     * 需要动态查询
     * 通过映射文件配置SQL
     * @param employeePageQueryDTO
     * @return
     */
    Page<Employee> pageQuery(EmployeePageQueryDTO employeePageQueryDTO);

    /**
     * 根据主键 动态 修改语句
     * 通过映射文件配置SQL
     * @param employee
     */
    void update(Employee employee);
}
