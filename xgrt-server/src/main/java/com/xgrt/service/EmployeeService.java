package com.xgrt.service;

import com.xgrt.dto.EmployeeDTO;
import com.xgrt.dto.EmployeeLoginDTO;
import com.xgrt.dto.EmployeePageQueryDTO;
import com.xgrt.entity.Employee;
import com.xgrt.result.PageResult;

public interface EmployeeService {

    /**
     * 员工登录
     * @param employeeLoginDTO
     * @return
     */
    Employee login(EmployeeLoginDTO employeeLoginDTO);

    /**
     * 新增员工
     * @param employeeDTO
     */
    void save(EmployeeDTO employeeDTO);


    /**
     * 分页查询
     * @param employeePageQueryDTO
     * @return
     */
    PageResult pageQuery(EmployeePageQueryDTO employeePageQueryDTO);
}
