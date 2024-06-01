package com.org.management.repository;

import java.util.HashMap;

import org.springframework.stereotype.Repository;

import com.org.management.model.Employee;

@Repository
public class EmployeeRepository {

	public HashMap<Integer, Employee> employeeMap = new HashMap<>();
	
}
