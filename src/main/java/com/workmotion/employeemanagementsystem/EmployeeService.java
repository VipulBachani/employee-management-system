package com.workmotion.employeemanagementsystem;

import java.util.Optional;

public interface EmployeeService {
	Employee save(Employee employee);
	Optional<Employee> findById(Long id);
}
