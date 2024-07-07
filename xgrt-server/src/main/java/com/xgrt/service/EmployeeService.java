package com.xgrt.service;

import com.xgrt.dto.EmployeeLoginDTO;
import com.xgrt.entity.Employee;

public interface EmployeeService {

    /**
     * 员工登录
     * @param employeeLoginDTO
     * @return
     */
    Employee login(EmployeeLoginDTO employeeLoginDTO);

}
