package com.workmotion.employeemanagementsystem;

public interface EmployeeService {
	Employee save(Employee employee);
	Employee findById(Long id);
}
