package com.org.management.service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.org.management.model.Employee;
import com.org.management.repository.EmployeeRepository;

@Service
public class EmployeeService {

	@Autowired
	private EmployeeRepository employeeRepository;

	public boolean initialSave(Employee employee) {
		// Save employee only if employeeId is unique.
		if (!employeeRepository.employeeMap.containsKey(employee.getEmployeeId())) {
			employeeRepository.employeeMap.put(employee.getEmployeeId(), employee);
			return true;
		}
		return false;
	}
	
	public int save(Employee employee) {
		// Check if employee already exists
		if (employeeRepository.employeeMap.containsKey(employee.getEmployeeId())) {
			return -1;
		}
		// Check if manager exists
		if (!employeeRepository.employeeMap.containsKey(employee.getManagerId())) {
			return 0;
		}
		// Save employee
		employeeRepository.employeeMap.put(employee.getEmployeeId(), employee);
		return 1;
	}

	public List<Employee> getEmployees() {
		List<Employee> employees = employeeRepository.employeeMap.values().stream()
				.collect(Collectors.toList());
		return employees;
	}

	public Employee getEmployee(Integer employeeId) {
		return employeeRepository.employeeMap.get(employeeId);
	}

	public Employee getManager(Employee employee) {
		return employeeRepository.employeeMap.get(employee.getManagerId());
	}

	public List<Employee> getSubordinates(Employee employee) {
		List<Employee> subordinates = employeeRepository.employeeMap.values().stream()
				.filter(e -> e.getManagerId() == employee.getEmployeeId()).collect(Collectors.toList());
		return subordinates;
	}
}
