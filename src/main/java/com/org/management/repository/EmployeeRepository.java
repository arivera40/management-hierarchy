package com.org.management.repository;

import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Repository;

import com.org.management.model.Employee;

@Repository
public class EmployeeRepository {

	public HashMap<Integer, Employee> employeeMap = new HashMap<>();
	
	public boolean initialSave(Employee employee) {
		// Save employee only if employeeId is unique.
		if (!employeeMap.containsKey(employee.getEmployeeId())) {
			employeeMap.put(employee.getEmployeeId(), employee);
			return true;
		}
		return false;
	}
	
	public int save(Employee employee) {
		// Check if employee already exists
		if (employeeMap.containsKey(employee.getEmployeeId())) {
			return -1;
		}
		// Check if manager exists
		if (!employeeMap.containsKey(employee.getManagerId())) {
			return 0;
		}
		// Save employee
		employeeMap.put(employee.getEmployeeId(), employee);
		return 1;
	}

	public List<Employee> getEmployees() {
		List<Employee> employees = employeeMap.values().stream()
				.collect(Collectors.toList());
		return employees;
	}

	public Employee getEmployee(Integer employeeId) {
		return employeeMap.get(employeeId);
	}

	public Employee getManager(Employee employee) {
		return employeeMap.get(employee.getManagerId());
	}

	public List<Employee> getSubordinates(Employee employee) {
		List<Employee> subordinates = employeeMap.values().stream()
				.filter(e -> e.getManagerId() == employee.getEmployeeId()).collect(Collectors.toList());
		return subordinates;
	}
}
